package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.MyProgressDialog;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.videoUtil.VideoUtil;
import com.echoesnet.eatandmeet.utils.videoUtil.VideoUtilListener;
import com.echoesnet.eatandmeet.views.ThumbnailView;
import com.echoesnet.eatandmeet.views.adapters.ThumbnailAdapter;
import com.echoesnet.eatandmeet.views.widgets.video.EmptyControlVideo;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.orhanobut.logger.Logger;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 视频时长剪切
 */
public class CutTimeActivity extends BaseActivity
{

    //    @BindView(R.id.textureView)
//    public TextureView textureView;
    @BindView(R.id.video_view)
    public EmptyControlVideo mVideoView;
    @BindView(R.id.ll_thumbnail)
    public LinearLayout llThumbnail;
    @BindView(R.id.rv_thumbnail)
    public RecyclerView rvThumbnail;
    @BindView(R.id.thumbnailView)
    public ThumbnailView thumbnailView;
    @BindView(R.id.tv_cancel)
    public TextView tvCancel;
    @BindView(R.id.tv_finish_video)
    public TextView tvFinishVideo;
    @BindView(R.id.rl_video)
    public RelativeLayout rlVideo;
    @BindView(R.id.rl_thumb)
    public RelativeLayout rlThumb;
    @BindView(R.id.rl_thumbnail_view)
    public RelativeLayout rlThumbnailView;
    @BindView(R.id.rl_finish)
    public RelativeLayout rlFinish;
    @BindView(R.id.img_thumb)
    public ImageView imgThumb;


    private final String TAG = CutTimeActivity.class.getSimpleName();
    /**
     * 视频宽度
     */
    public static int VIDEO_WIDTH = 1280;
    /**
     * 视频高度
     */
    public static int VIDEO_HEIGHT = 720;
    private String mVideoPath;

    private int videoWidth;
    private int videoHeight;

    private int videoDuration;
    private Activity mAct;
    private int startTime;
    private int endTime;
    private int windowWidth;
    private int windowHeight;
    private int itemWidth;
    private ThumbnailAdapter thumbnailAdapter;
    private MyProgressDialog pDialog;
    private float rotation = 0;
    private List<String> imgPathList;
    private int frameNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cut_time);

        ButterKnife.bind(this);
        mAct = this;
        Intent intent = getIntent();
        mVideoPath = intent.getStringExtra("path");
        Logger.t(TAG).d("mVideoPath>>>>>>>>>>" + mVideoPath);
        windowWidth = getWindowManager().getDefaultDisplay().getWidth();
        windowHeight = getWindowManager().getDefaultDisplay().getHeight();
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(mVideoPath);
        try
        {
            rotation = Float.valueOf(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
            videoDuration = Integer.valueOf(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            videoHeight = Integer.valueOf(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            videoWidth = Integer.valueOf(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            if (rotation == 90 || rotation == 270)
            {
                int temp = videoWidth;
                videoWidth = videoHeight;
                videoHeight = temp;
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        Logger.t(TAG).d("rotation>>" + rotation + "|videoDuration>>" + videoDuration);
        imgPathList = new ArrayList<>();
        itemWidth = windowWidth / 14;
        initVideoSize();
        thumbnailAdapter = new ThumbnailAdapter(mAct, mVideoPath, itemWidth, rotation, imgPathList);
        try
        {
            FFmpeg.getInstance(mAct).loadBinary(new LoadBinaryResponseHandler());
        } catch (FFmpegNotSupportedException e)
        {
            e.printStackTrace();
        }
        initUI();
    }

    private void initUI()
    {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mAct, LinearLayoutManager.HORIZONTAL, false);
        rvThumbnail.setLayoutManager(linearLayoutManager);
        rvThumbnail.setAdapter(thumbnailAdapter);
        VideoOptionModel videoOptionModel = new VideoOptionModel(tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        List<VideoOptionModel> list = new ArrayList<>();
        list.add(videoOptionModel);
        GSYVideoManager.instance().setOptionModelList(list);
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
        mVideoView.setVideoAllCallBack(new GSYSampleCallBack()
        {
            @Override
            public void onPrepared(String url, Object... objects)
            {
                super.onPrepared(url, objects);
                Logger.t(TAG).d("prepared>>>>>>>>>>>>>>>");
            }

            @Override
            public void onStartPrepared(String url, Object... objects)
            {
                super.onStartPrepared(url, objects);
                Logger.t(TAG).d("onStartPrepared>>>>>>>>>>>>>>>");
            }

            @Override
            public void onAutoComplete(String url, Object... objects)
            {
                super.onAutoComplete(url, objects);
                mVideoView.setSeekOnStart(startTime);
                mVideoView.startPlayLogic();
            }
        });
        mVideoView.setUp(mVideoPath, false, null);
        mVideoView.startPlayLogic();
        initThumbs();
        thumbnailView.setOnScrollBorderListener(new ThumbnailView.OnScrollBorderListener()
        {
            @Override
            public void OnScrollBorder(float start, float end)
            {
//                changeTime();
            }

            @Override
            public void onScrollStateChange()
            {
                changeVideoPlay();
            }

            @Override
            public void complete()
            {
                if (mVideoView != null)
                {
                    Logger.t(TAG).d("seekTo>>>" + startTime);
                    if (startTime == 0)
                        mVideoView.startPlayLogic();
                    else
                        mVideoView.seekTo(startTime);
                }
            }
        });
        rvThumbnail.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState)
                {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        changeVideoPlay();
                        break;
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void changeVideoPlay()
    {
        View item = rvThumbnail.findChildViewUnder(thumbnailView.getLeftInterval(), thumbnailView.getTopInterval());
        int position = rvThumbnail.getChildLayoutPosition(item) - 1;
        int msec = position * 1000;
        startTime = msec;
        View endItem = rvThumbnail.findChildViewUnder(thumbnailView.getEndLeftInterval(), thumbnailView.getTopInterval());
        int endPosition = rvThumbnail.getChildLayoutPosition(endItem) - 1;
        int endMsec = endPosition * 1000;
        endTime = endMsec;
        Logger.t(TAG).d("endTime>" + endTime);
        Logger.t(TAG).d("startTime>" + startTime);
        if (mVideoView != null)
        {
            mVideoView.seekTo(msec);
            thumbnailView.start();
        }
    }


    /**
     * 剪切视频
     */
    private void cutVideo()
    {
        if (mVideoView != null)
            mVideoView.onVideoPause();
        if (pDialog == null)
            pDialog = new MyProgressDialog()
                    .setOutTime(false)
                    .buildDialog(mAct).setDescription("视频剪切中");
        pDialog.setCancelable(false);
        pDialog.show();
        View item = rvThumbnail.findChildViewUnder(thumbnailView.getLeftInterval(), thumbnailView.getTopInterval());
        int position = rvThumbnail.getChildLayoutPosition(item) - 1;
        int msec = position * 1000;
        startTime = msec;
        View endItem = rvThumbnail.findChildViewUnder(thumbnailView.getEndLeftInterval(), thumbnailView.getTopInterval());
        int endPosition = rvThumbnail.getChildLayoutPosition(endItem) - 1;
        int endMsec = endPosition * 1000;
        endTime = endMsec;
        long time = System.currentTimeMillis();
        String outPath = NetHelper.getRootDirPath(mAct) + NetHelper.CUT_VIDEO + time + ".mp4";
        String thumbnailPath = NetHelper.getRootDirPath(mAct) + NetHelper.CUT_VIDEO + time;
        int startM = startTime / 1000;
        int endM = (endTime - startTime) / 1000;
        Log.d("cutVideo", "startM = " + startM + "|endM = " + endM + "|");
        String startStr;
        String endStr;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        startStr = simpleDateFormat.format(new Date(startTime));

        if (endM < 10)
        {
            endStr = "00:00:0" + endM;
        } else
        {
            endStr = "00:00:" + endM;
        }
        VideoUtil.cutVideo(mAct, startStr, endStr, mVideoPath, outPath, new VideoUtilListener()
        {
            @Override
            public void start()
            {

            }

            @Override
            public void complete(String videoPath, String err)
            {
                if (!TextUtils.isEmpty(err))
                    Logger.t(TAG).d("剪切视频出错" + err);
                VideoUtil.getVideoMediaThumbnail(mAct, 0, outPath, thumbnailPath, rotation, true,
                        new VideoUtilListener()
                        {
                            @Override
                            public void start()
                            {

                            }

                            @Override
                            public void complete(String outPath, String err)
                            {
                                mAct.runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        if (pDialog != null && pDialog.isShowing())
                                            pDialog.dismiss();
                                        ToastUtils.showShort("剪切成功");
                                        String showType = videoWidth > videoHeight ? "0" : "1";
                                        Logger.t(TAG).d("剪切成功 mVideoPath>" + videoPath + "|thumbnailPath>" + thumbnailPath + "|videoWidth>" + videoWidth + "|videoHeight>" + videoHeight + "|showType>" + showType);
                                        Intent intent = new Intent();
                                        intent.putExtra("path", videoPath);
                                        intent.putExtra("thumbnailPath", thumbnailPath);
                                        intent.putExtra("showType", showType);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                });

                            }
                        });
            }
        });
//        Observable.create(new ObservableOnSubscribe<String>()
//        {
//            @Override
//            public void subscribe(ObservableEmitter<String> e) throws Exception
//            {
//                String output = NetHelper.getRootDirPath(mAct)+"/video/"+System.currentTimeMillis()+".mp4";
//
//                int startM = startTime/1000;
//                int endM = (endTime-startTime)/1000;
//
//                String startStr;
//                String endStr;
//
//                if(startM < 10){
//                    startStr = "00:00:0"+startM;
//                }else{
//                    startStr = "00:00:"+startM;
//                }
//
//                if(endM < 10){
//                    endStr = "00:00:0"+endM;
//                }else{
//                    endStr = "00:00:"+endM;
//                }
//
//                StringBuilder sb = new StringBuilder("ffmpeg");
//                sb.append(" -i");
//                sb.append(" "+mVideoPath);
//                sb.append(" -vcodec");
//                sb.append(" copy");
//                sb.append(" -acodec");
//                sb.append(" copy");
//                sb.append(" -ss");
//                sb.append(" "+startStr);
//                sb.append(" -t");
//                sb.append(" "+endStr);
//                sb.append(" "+output);
////                int i = UtilityAdapter.FFmpegRun("", sb.toString());
//                e.onNext(output);
//            }
//        }).subscribeOn(Schedulers.computation())
//                .unsubscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<String>()
//                {
//                    @Override
//                    public void accept(String s) throws Exception
//                    {
//                        if(!TextUtils.isEmpty(s)){
//                            Toast.makeText(mAct, "剪切成功", Toast.LENGTH_SHORT).show();
//                            setResult(RESULT_OK);
//                            finish();
//                        }
//                    }
//                });
    }


    /**
     * 初始化视频播放器
     */
    private void initVideoSize()
    {
        thumbnailView.setMinInterval(itemWidth);
    }

    /**
     * 初始化缩略图
     */
    private void initThumbs()
    {

        frameNum = videoDuration / 1000;
        final int frameTime = 1000;
        Logger.t(TAG).d("frame>>>>>>>>>" + frameNum);
        thumbnailAdapter.setFrameCount(frameNum + 4);
        thumbnailAdapter.setFrameTime(frameTime);
//        Message message = new Message();
//        message.arg1 = 0;
//        message.what = 0;
//        thumbHandler.sendMessage(message);
        thumbnailAdapter.notifyDataSetChanged();
        thumbnailView.start();

    }

    private Handler thumbHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 0:
                    int postion = msg.arg1;
                    Logger.t(TAG).d("time>>" + postion);
                    File videoFile = new File(mVideoPath);
                    String outPath = NetHelper.getRootDirPath(mAct) + NetHelper.CUT_VIDEO_THUMBNAIL + videoFile.getName();
                    File file = new File(outPath);
                    if (!file.exists())
                        file.mkdirs();
                    final String output = outPath + "/" + postion;
                    File file1 = new File(output);
                    if (!file1.exists())
                        VideoUtil.getVideoThumbnail(mAct, String.valueOf(postion), mVideoPath, output, true, new VideoUtilListener()
                        {
                            @Override
                            public void start()
                            {

                            }

                            @Override
                            public void complete(String outPath, String err)
                            {
                                if (postion % 2 == 0 || (postion - 1) == frameNum)
                                    thumbnailAdapter.notifyDataSetChanged();
                                imgPathList.add(outPath);
                                if (postion - 1 < frameNum)
                                {
                                    Message message = new Message();
                                    message.arg1 = postion + 1;
                                    message.what = 0;
                                    sendMessage(message);
                                }
                            }
                        });
                    break;
            }
        }
    };

    @OnClick({R.id.img_play, R.id.tv_cancel, R.id.tv_finish_video})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_play:

                break;
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_finish_video:
                cutVideo();
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        if (mVideoView != null)
        {
            mVideoView.release();
        }
        if (thumbHandler != null)
            thumbHandler.removeMessages(0);
        FFmpeg.getInstance(mAct).killRunningProcesses();
        super.onDestroy();
    }
}

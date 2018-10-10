package com.echoesnet.eatandmeet.activities.live;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.activities.MVPBaseActivity;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.models.bean.LiveEnterRoomBean;
import com.echoesnet.eatandmeet.models.bean.LookAnchorBean;
import com.echoesnet.eatandmeet.presenters.ImpLHotAnchorVideoActView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ILHotAnchorVideoActView;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LiveHostInfoPop;
import com.echoesnet.eatandmeet.views.widgets.LiveMsgDialog.LiveChatDialog;
import com.echoesnet.eatandmeet.views.widgets.SwipeMoveRelativeLayout;
import com.echoesnet.eatandmeet.views.widgets.video.EmptyControlVideo;
import com.google.gson.Gson;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;


import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * Created by an on 2016/11/7 0007.
 */

public class LHotAnchorVideoAct extends MVPBaseActivity<ILHotAnchorVideoActView, ImpLHotAnchorVideoActView> implements ILHotAnchorVideoActView
{
    private static final String TAG = LHotAnchorVideoAct.class.getSimpleName();
    private static final int MSG_INIT_SEEKBAR = 1;
    private static final int MSG_UPDATE_SEEKBAR = 2;
    @BindView(R.id.video_view)
    EmptyControlVideo mVideoView;
    @BindView(R.id.btn_play_complete)
    IconTextView btnPlayComplete;
    @BindView(R.id.iv_loading_cover)
    ImageView ivLoadingCover;
    @BindView(R.id.fl_booty_call)
    FrameLayout flBootyCall;
    @BindView(R.id.seekbar)
    SeekBar seekbar;
    @BindView(R.id.start_time)
    TextView startTimeTv;
    @BindView(R.id.end_time)
    TextView endTimeTv;
    @BindView(R.id.ll_bottom_controller)
    LinearLayout botoomController;
    @BindView(R.id.btn_pause_or_play)
    ImageView pauseOrPlayBtn;
    @BindView(R.id.tvMealTicketCount)
    TextView tvMealTicketCount;
    @BindView(R.id.tvId)
    TextView tvId;
    @BindView(R.id.tvRoomName)
    TextView tvRoomName;
    @BindView(R.id.tvAudiencesCount)
    TextView tvAudiencesCount;
    @BindView(R.id.tvFollow)
    TextView tvFollow;
    @BindView(R.id.rivHostAvatar)
    LevelHeaderView rivHostAvatar;
    @BindView(R.id.llMealTicketGroup)
    LinearLayout llMealTicketGroup;
    @BindView(R.id.swipeLayout)
    SwipeMoveRelativeLayout swipeLayout;

    private Activity mAct;
    private String videoUrl;
    private String roomId;
    private String luid;
    private int cunrrentPosition;
    private AlertDialog dialog;
    private SimpleDateFormat simpleDateFormat;
    private boolean isShowRanking = false,isShowThisTime = false;
    private LiveHostInfoPop mLiveHostInfoPop;
    private LiveEnterRoomBean liveEnterRoomBean;
    @Override
    protected void onCreate(Bundle arg0)
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(arg0);
        setContentView(R.layout.act_hotanchorvideo);
        ButterKnife.bind(this);
        afterViews();
    }

    private void afterViews()
    {
        mAct = this;
        videoUrl = getIntent().getStringExtra("video");
        roomId = getIntent().getStringExtra("roomId");
        luid = getIntent().getStringExtra("luid");
        String num = getIntent().getStringExtra("watchNum");
        String watchNum = TextUtils.isEmpty(num)?String.valueOf(new Random().nextInt(100)):num;
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
        Logger.t(TAG).d("vedio==" + videoUrl);
        if (!TextUtils.isEmpty(videoUrl))
        {
            ImageUtils.newShowLoadingCover( getIntent().getStringExtra("coverImgUrl"), ivLoadingCover);
            initVideoView();
        }
        if (mPresenter != null)
        {
            mPresenter.showRankingAndBootyCall();
            mPresenter.getRoomInfor(roomId);
        }
        if (!TextUtils.isEmpty(watchNum))
        {
            tvAudiencesCount.setText(String.format("%s人气",watchNum));
        }

    }

    private void initVideoView()
    {
        mVideoView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (botoomController.getVisibility() == View.VISIBLE)
                {
                    botoomController.setVisibility(View.GONE);
                }else {
                    botoomController.setVisibility(View.VISIBLE);
                }
            }
        });

        mVideoView.setVideoAllCallBack(new GSYSampleCallBack(){
            @Override
            public void onPrepared(String url, Object... objects)
            {
                super.onPrepared(url, objects);
                //准备完成
                ImageUtils.fadeOut(ivLoadingCover, 500);
                mHandler.sendEmptyMessage(MSG_INIT_SEEKBAR);
                mHandler.sendEmptyMessage(MSG_UPDATE_SEEKBAR);
            }

            @Override
            public void onAutoComplete(String url, Object... objects)
            {
                super.onAutoComplete(url, objects);
                pauseOrPlayBtn.setImageResource(R.drawable.player_icon_bottomview_play_button_normal);
            }

            @Override
            public void onPlayError(String url, Object... objects)
            {
                super.onPlayError(url, objects);
                if (dialog == null)
                    dialog = new AlertDialog.Builder(mAct, R.style.AppTheme_Dialog_Alert)
                            .setTitle("提示")
                            .setMessage("播放出错请重试")
                            .setCancelable(false)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                    finish();
                                }
                            }).create();
                if (dialog != null && !dialog.isShowing() && !mAct.isFinishing())
                    dialog.show();
            }
        });
        if (!TextUtils.isEmpty(videoUrl))
        {
            mVideoView.setUp(videoUrl,true,"");
            mVideoView.startPlayLogic();
        }
//       mVideoView.setOnInfoListener(new UMediaPlayer.OnInfoListener() {
//           @Override
//           public boolean onInfo(UMediaPlayer uMediaPlayer, int vStreamStatus, int arg2) {
////               Logger.i("LLLLLxxxx,vStreamStatus:%d,iDon'tKnow-arg2:%d",vStreamStatus,arg2);
//
////               Logger.i("LLLLLxxxx,videoW:%d,videoH:%d",
////                       uMediaPlayer.getVideoWidth(),
////                       uMediaPlayer.getVideoHeight());
//               // LLLLLxxxx,videoW:368,videoH:640
//
//
//               if(vStreamStatus==merge.tv.danmaku.ijk.media.player.IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
//                   float videoW = uMediaPlayer.getVideoWidth();
//                   float videoH = uMediaPlayer.getVideoHeight();
//                   int subVCount = mVideoView.getChildCount();
//                   for (int i = 0; i < subVCount; i++) {
//                       View subV = mVideoView.getChildAt(i);
////                       Logger.i("LLLLLxxxx,w:%d,h:%d,i:%d",subV.getWidth(),subV.getHeight(),i);
//
//                       float vw = subV.getWidth();
//                       float vh = subV.getHeight();
//
//                       float scale =  vw / videoW;
//
//                       float stretchingW = videoW*scale;
//                       float stretchingH = videoH*scale;
//
//                       if(stretchingH>vh){
//
//                       }else {
//                           scale = vh / videoH;
//                           stretchingW = videoW*scale;
//                           stretchingH = videoH*scale;
//                       }
//
//
//                       subV.setLayoutParams(new FrameLayout.LayoutParams((int)stretchingW,(int)stretchingH));
//
//                   }
//               }
//
//               return false;
//           }
//       });

        swipeLayout.setListener(new SwipeMoveRelativeLayout.SwipeMoveListener()
        {
            @Override
            public void onClick()
            {
                if (botoomController.getVisibility() == View.VISIBLE)
                {
                    botoomController.setVisibility(View.GONE);
                }else {
                    botoomController.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void dispatchTouch(MotionEvent event)
            {

            }

            @Override
            public void onSwipeToHidden()
            {

            }

            @Override
            public void onSwipeToShow()
            {

            }
        });


    }

    @OnClick({R.id.btn_play_complete, R.id.fl_booty_call, R.id.btn_pause_or_play, R.id.llMealTicketGroup,R.id.rivHostAvatar, R.id.tvFollow})
    void clickView(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_play_complete:
                mVideoView.onVideoPause();
                finish();
                overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
                break;
            case R.id.fl_booty_call:
                Intent bootyCallIntent = new Intent(mAct,CNewUserInfoAct.class);
                bootyCallIntent.putExtra("toUId", luid);
                bootyCallIntent.putExtra("fromAct", "LHotAnchorVideoAct");
                bootyCallIntent.putExtra("currentPage", 2);
                cunrrentPosition = mVideoView.getPlayPosition();
                startActivity(bootyCallIntent);
                break;
            case R.id.btn_pause_or_play:
                if (mVideoView.isInPlayingState()){
                    mVideoView.onVideoPause();
                    pauseOrPlayBtn.setImageResource(R.drawable.player_icon_bottomview_play_button_normal);
                }else {
                    mVideoView.onVideoResume();
                    pauseOrPlayBtn.setImageResource(R.drawable.player_icon_bottomview_pause_button_normal);
                }
                break;
            case R.id.llMealTicketGroup://查看榜单
                if (isShowRanking)
                {
                    Intent intent = new Intent(mAct, LRankingAct.class);
                    intent.putExtra("roomId", roomId);
                    intent.putExtra("isShowThisTime", isShowThisTime);
                    mAct.startActivity(intent);
                }
                break;
            case R.id.rivHostAvatar:
                if (liveEnterRoomBean != null)
                    mPresenter.getAnchorInformation(liveEnterRoomBean.getuId());
                break;
            case R.id.tvFollow:
                mPresenter.focus(liveEnterRoomBean.getuId());
                break;
            default:
                break;
        }
    }
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_INIT_SEEKBAR:
                    initSeekBar();
                    break;
                case MSG_UPDATE_SEEKBAR:
                    int position =  mVideoView.getCurrentPlayer().getCurrentPositionWhenPlaying();
                    Logger.t(TAG).d("position>>>" + position);
                    String hms = simpleDateFormat.format(position);
                    seekbar.setProgress(position);
                    startTimeTv.setText(hms);
                    if (position < mVideoView.getDuration())
                        sendEmptyMessageDelayed(MSG_UPDATE_SEEKBAR, 800);
                    break;
            }
        }
    };

    private void initSeekBar()
    {
        Logger.t(TAG).d("mVideoView.getDuration() ->" + mVideoView.getDuration());
        seekbar.setEnabled(true);
        seekbar.setMax(mVideoView.getDuration());
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                mVideoView.seekTo(seekBar.getProgress());
            }
        });
        endTimeTv.setText(simpleDateFormat.format(mVideoView.getDuration()));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (cunrrentPosition != 0)
            mVideoView.seekTo(cunrrentPosition);
    }

    @Override
    protected void onDestroy()
    {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        if (null != mVideoView)
        {
            mVideoView.onVideoPause();
            mVideoView.release();
        }
        if (mHandler != null)
        {
            mHandler.removeMessages(MSG_UPDATE_SEEKBAR);
            mHandler = null;
        }
        super.onDestroy();
    }

    @Override
    protected ImpLHotAnchorVideoActView createPresenter()
    {
        return new ImpLHotAnchorVideoActView();
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        Logger.t(TAG).d(e.getMessage());
    }

    @Override
    public void showRankingCallBack(boolean isShowRanking, boolean isShowThisTime, String receive)
    {
        this.isShowRanking = isShowRanking;
        this.isShowThisTime = isShowThisTime;
        llMealTicketGroup.setVisibility(isShowRanking?View.VISIBLE:View.INVISIBLE);
        if ("0".equals(receive))
        {
            flBootyCall.setVisibility(View.VISIBLE);
        } else
        {
            flBootyCall.setVisibility(View.GONE);
        }
    }

    @Override
    public void getRoomInformationCallBack(LiveEnterRoomBean liveEnterRoomBean)
    {
        if (liveEnterRoomBean != null){
            this.liveEnterRoomBean = liveEnterRoomBean;
            tvMealTicketCount.setText(String.format("%s >",liveEnterRoomBean.getMeal()));
            tvId.setText(String.format(getResources().getString(R.string.live_id), liveEnterRoomBean.getAnchorId()));
            tvRoomName.setText(liveEnterRoomBean.getNicName());
//            tvAudiencesCount.setText(liveEnterRoomBean.get);
            tvFollow.setVisibility(View.GONE);
            if (!SharePreUtils.getId(mAct).equals(liveEnterRoomBean.getAnchorId()))
            {
                if ("0".equals(liveEnterRoomBean.getFlag()))
                    tvFollow.setVisibility(View.VISIBLE);
            }
            rivHostAvatar.setHeadImageByUrl(liveEnterRoomBean.getPhUrl());
            rivHostAvatar.setLevel(liveEnterRoomBean.getAnchorLevel());
            rivHostAvatar.showRightIcon(liveEnterRoomBean.getIsVuser());
        }
    }

    @Override
    public void getAnchorInformationCallBack(String response)
    {

        final LookAnchorBean bean = new Gson().fromJson(response, LookAnchorBean.class);
        mLiveHostInfoPop = new LiveHostInfoPop(mAct, bean, bean.getuId(),liveEnterRoomBean.getHxRoomId());

        mLiveHostInfoPop.showPopupWindow((ViewGroup)findViewById(android.R.id.content), null);

        if (SharePreUtils.getUId(mAct).equals(bean.getuId()))
        {
            //主播自己点击自己,隐藏按钮
            mLiveHostInfoPop.hideSelfState();
        }
        else
        {
            mLiveHostInfoPop.setOnFocusItemClickListener(new LiveHostInfoPop.OnFocusItemClickListener()
            {
                @Override
                public void onFocusClick()
                {
                    mPresenter.focus(liveEnterRoomBean.getuId());
                }
            });
            mLiveHostInfoPop.setOnHelloItemClickListener(new LiveHostInfoPop.OnHelloItemClickListener()
            {
                @Override
                public void onHelloClick()
                {
                    if (mLiveHostInfoPop != null)
                        mLiveHostInfoPop.dismiss();
                    //弹出聊天界面
                    //保存到本地，聊天人信息
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            EaseUser toEaseUser = new EaseUser(bean.getImuId());
                            toEaseUser.setuId(bean.getuId());
                            toEaseUser.setId(bean.getId());
                            toEaseUser.setNickName(bean.getNicName());
                            toEaseUser.setAvatar(bean.getUphUrl());
                            toEaseUser.setLevel(bean.getLevel());
                            toEaseUser.setSex(bean.getSex());
                            toEaseUser.setAge(bean.getAge());
                            toEaseUser.setRemark(bean.getRemark());
                            toEaseUser.setIsVuser(bean.getIsVuser());
                            HuanXinIMHelper.getInstance().saveContact(toEaseUser);
                        }
                    }).start();
                    EaseUser eUser = new EaseUser(bean.getImuId());
                    eUser.setNickName(bean.getNicName());
                    eUser.setuId(bean.getuId());
                    eUser.setAvatar(bean.getUphUrl());
                    LiveChatDialog liveChatDialog = LiveChatDialog.newInstance(eUser);
                    liveChatDialog.show(getSupportFragmentManager(), TAG);
                }
            });
            mLiveHostInfoPop.setOnYuePaoItemClickListener(new LiveHostInfoPop.OnYuePaoItemClickListener()
            {
                @Override
                public void onYuePaoClick()
                {
                    mPresenter.addWish(liveEnterRoomBean.getuId());
                }
            });


        }
    }

    @Override
    public void focusSuccess()
    {
        if (liveEnterRoomBean != null)
            liveEnterRoomBean.setFlag("1");
        tvFollow.setVisibility(View.GONE);
        if (mLiveHostInfoPop != null && mLiveHostInfoPop.isShowing())
        {
            mLiveHostInfoPop.focusAuthorSuccess();
        }
    }

    @Override
    public void addWishSuccess()
    {
        if (mLiveHostInfoPop != null)
        {
            mLiveHostInfoPop.yueAuthorSuccess();
        }
        ToastUtils.showShort("加约会管家成功");
    }

    @Override
    public void onBackPressed()
    {
        finish();
        overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
    }
}

package com.echoesnet.eatandmeet.views.widgets.chat.chatrow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.TrendsPlayVideoAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.ChatMessageAdapter2;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.util.DateUtils;
import com.hyphenate.util.EMLog;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ChatRowVideo extends ChatRowFile
{

    private RoundedImageView imageView;
    private TextView timeLengthView;
    private ImageView playView;
    private IconTextView itvPlayVideo;
    private LinearLayout llLoading;
    private String localUrl;
    EMVideoMessageBody videoBody;

    public ChatRowVideo(Context context, EMMessage message, int position, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter)
    {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView()
    {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_video : R.layout.ease_row_sent_video, this);
    }

    @Override
    protected void onFindViewById()
    {
        imageView = ((RoundedImageView) findViewById(R.id.chatting_content_iv));
        timeLengthView = (TextView) findViewById(R.id.chatting_length_iv);
        percentageView = (TextView) findViewById(R.id.percentage);
        itvPlayVideo = (IconTextView) findViewById(R.id.itv_play_video);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);
    }

    @Override
    protected void onSetUpView()
    {
        videoBody = (EMVideoMessageBody) message.getBody();
        String localThumb = videoBody.getLocalThumb();
        Logger.t(TAG).d("localThumb:" + localThumb + " | " + videoBody.getThumbnailUrl());
        if (!TextUtils.isEmpty(localThumb))
        {
            showVideoThumbView(localThumb, imageView, videoBody.getThumbnailUrl(), message);
        }
        if (videoBody.getDuration() > 0)
        {
            //Duration 以秒传递 然后 toTime 要毫秒 所以乘 1000
            String time = DateUtils.toTime(videoBody.getDuration() * 1000);
            Logger.t(TAG).d("videoBody.getDuration()：" + videoBody.getDuration() + " | time:" + time);
            timeLengthView.setText(time);
        }
        EMLog.d(TAG, "video thumbnailStatus:" + videoBody.thumbnailDownloadStatus());
        if (message.direct() == EMMessage.Direct.RECEIVE)
        {
            if (videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING)
            {
                setMessageReceiveCallback();
            }
            else
            {
                // System.err.println("!!!! not back receive, show image directly");
                if (localThumb != null)
                {
                    showVideoThumbView(localThumb, imageView, videoBody.getThumbnailUrl(), message);
                }
            }
            return;
        }
        //handle sending message
        handleSendMessage();
    }

    @Override
    protected void onUpdateView()
    {
        super.onUpdateView();
        adapter.notifyItemChanged(position);
    }

    @Override
    protected void onBubbleClick()
    {
        EMVideoMessageBody videoBody = (EMVideoMessageBody) message.getBody();
        EMLog.d(TAG, "video view is on click");

        localUrl = videoBody.getLocalUrl();
        File file = new File(videoBody.getLocalUrl());
        if (file.exists())
        {
            Intent intent = new Intent(context, TrendsPlayVideoAct.class);
            intent.putExtra("url", localUrl);
            String showType = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_VIDEO_TYPE, "0");
            intent.putExtra("showType", showType);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
        }
        else
        {
            itvPlayVideo.setVisibility(GONE);
            llLoading.setVisibility(VISIBLE);
            downloadVideo(message);
        }
        if (message != null && message.direct() == EMMessage.Direct.RECEIVE && !message.isAcked()
                && message.getChatType() == ChatType.Chat)
        {
            try
            {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void downloadVideo(final EMMessage message)
    {
        message.setMessageStatusCallback(new EMCallBack()
        {
            @Override
            public void onSuccess()
            {
                activity.runOnUiThread(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        progressBar.setProgress(0);
                        llLoading.setVisibility(GONE);
                        itvPlayVideo.setVisibility(VISIBLE);
                        Intent intent = new Intent(context, TrendsPlayVideoAct.class);
                        intent.putExtra("url", localUrl);
                        String showType = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_VIDEO_TYPE, "0");
                        intent.putExtra("showType", showType);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(0, 0);
                    }
                });
            }

            @Override
            public void onProgress(final int progress, String status)
            {
                Log.d("ease", "video progress:" + progress);
                activity.runOnUiThread(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        progressBar.setProgress(progress);
                        percentageView.setText(progress + "%");
                    }
                });

            }

            @Override
            public void onError(int error, String msg)
            {
                Log.e("###", "offline file transfer error:" + msg);
                File file = new File(localUrl);
                if (file.exists())
                {
                    file.delete();
                }
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ToastUtils.showShort("视频播放失败，请重试。");
                        llLoading.setVisibility(GONE);
                        itvPlayVideo.setVisibility(VISIBLE);
                    }
                });
            }
        });
        EMClient.getInstance().chatManager().downloadAttachment(message);
    }

    /**
     * show video thumbnails
     *
     * @param localThumb   local path for thumbnail
     * @param iv
     * @param thumbnailUrl Url on server for thumbnails
     * @param message
     */
    private void showVideoThumbView(final String localThumb, final ImageView iv, String thumbnailUrl, final EMMessage message)
    {
        if (message.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_APPEND_MESSAGE, false))
        {
            File file = new File(localThumb);
            if (file.exists())
            {
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(localThumb)
                        .placeholder(R.drawable.qs_photo)
                        .error(R.drawable.qs_photo)
                        .into(new SimpleTarget<Bitmap>()
                        {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation)
                            {
                                reSizeTheImage(resource);
                            }
                        });
            }
        }
        else
        {
            final String path = TextUtils.isEmpty(videoBody.getThumbnailUrl()) ? videoBody.getRemoteUrl() : videoBody.getThumbnailUrl();
            /*GlideApp.with(activity)
                    .asBitmap()
                    .load(path)
                    .placeholder(R.drawable.qs_photo)
                    .error(R.drawable.qs_photo)
                    .into(new SimpleTarget<Bitmap>()
                    {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation)
                        {
                            reSizeTheImage(resource);
                        }
                    });*/
            Observable.create(new ObservableOnSubscribe<Map<String, Object>>()
            {
                @Override
                public void subscribe(@NonNull ObservableEmitter<Map<String, Object>> e) throws Exception
                {
                    int resWidth = message.getIntAttribute(EamConstant.EAM_CHAT_ATTR_VIDEO_THUMBNAIL_WIDTH, 0);
                    int resHeight = message.getIntAttribute(EamConstant.EAM_CHAT_ATTR_VIDEO_THUMBNAIL_HEIGHT, 0);
                    float width = resWidth;
                    float height = resHeight;
                    float rate = width / height;
                    int targetLength = CommonUtils.dp2px(activity, 170);
                    int targetWidth = CommonUtils.dp2px(activity, 120);
                    int imgRate = 0;
                    Logger.t(TAG).d("------------>width前:" + width + " | height:" + height + " | targetHeight:" + targetLength + " | targetWidth:" + targetWidth + " | " + Float.compare(rate, 1.0f));
                    if (Float.compare(rate, 1.0f) == 1)//横图
                    {
                        imgRate = 1;
                        if (width != targetLength)
                        {
                            width = targetLength;
                            height = targetLength / rate;
                        }
                    }
                    else if (Float.compare(rate, 1.0f) == -1)//竖图
                    {
                        imgRate = -1;
                        if (height != targetLength)
                        {
                            height = targetLength;
                            width = targetLength * rate + CommonUtils.dp2px(context, 3);
                        }
                    }
                    else if (Float.compare(rate, 1.0f) == 0)//方图
                    {
                        imgRate = 0;
                        height = targetWidth;
                        width = targetWidth;
                    }
                    bubbleLayout.getLayoutParams().width = (int) width;
                    bubbleLayout.getLayoutParams().height = (int) height;
//                resBitmap = Bitmap.createScaledBitmap(finalResource, (int) width, (int) height, true);
                    Map<String, Object> map = new ArrayMap<>();
                    map.put("path", path);
                    map.put("width", (int) width);
                    map.put("height", (int) height);
                    map.put("imgRate", imgRate);
                    e.onNext(map);
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Map<String, Object>>()
                    {
                        @Override
                        public void accept(@NonNull Map<String, Object> obj) throws Exception
                        {
                            Map<String, Object> map = obj;
                            String path = map.get("path").toString();
                            int width = (int) map.get("width");
                            int height = (int) map.get("height");
                            int imgRate = (int) map.get("imgRate");
                            int placeholder = switchPlaceImg(imgRate);
                            GlideApp.with(EamApplication.getInstance())
                                    .asBitmap()
                                    .load(path)
                                    .thumbnail(0.1f)
                                    .override(width, height)
                                    .placeholder(placeholder)
                                    .error(placeholder)
                                    .centerCrop()
                                    .into(imageView);
                            imageView.setVisibility(VISIBLE);
                            ((ChatMessageAdapter2) adapter).selectLast();
                        }
                    });
        }

        /*// first check if the thumbnail image already loaded into cache
        Bitmap bitmap = ImageCache.getInstance().get(localThumb);
        if (bitmap != null)
        {
            // thumbnail image is already loaded, reuse the drawable
            iv.setImageBitmap(bitmap);
        }
        else
        {
            new AsyncTask<Void, Void, Bitmap>()
            {

                @Override
                protected Bitmap doInBackground(Void... params)
                {
                    if (new File(localThumb).exists())
                    {
                        return ImageUtils.decodeScaleImage(localThumb, 160, 160);
                    }
                    else
                    {
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Bitmap result)
                {
                    super.onPostExecute(result);
                    if (result != null)
                    {
                        ImageCache.getInstance().put(localThumb, result);
                        iv.setImageBitmap(result);
                    }
                    else
                    {
                        if (message.status() == EMMessage.Status.FAIL)
                        {
                            if (EaseCommonUtils.isNetWorkConnected(activity))
                            {
                                message.setMessageStatusCallback(callBack);
                                EMClient.getInstance().chatManager().downloadThumbnail(message);
                            }
                        }

                    }
                }
            }.execute();
        }*/

    }

    private int switchPlaceImg(int imgRate)
    {
        int placeholder = 0;
        switch (imgRate)
        {
            case 1:
                placeholder = R.drawable.qs_talk_heng;
                break;
            case -1:
                placeholder = R.drawable.qs_photo_shu;
                break;
            case 0:
                placeholder = R.drawable.qs_talk_fang;
                break;
        }
        return placeholder;
    }

    private void reSizeTheImage(Bitmap resource)
    {
        float width = resource.getWidth();
        float height = resource.getHeight();
        float rate = width / height;
        int targetLength = CommonUtils.dp2px(activity, 170);
        int targetWidth = CommonUtils.dp2px(activity, 120);
        Logger.t(TAG).d("-------->width前:" + width + " | height:" + height + " | targetHeight:" + targetLength + " | targetWidth:" + targetWidth + " | " + Float.compare(rate, 1.0f));
        if (Float.compare(rate, 1.0f) == 1)//横图
        {
            if (width != targetLength)
            {
                width = targetLength;
                height = targetLength / rate;
            }
        }
        else if (Float.compare(rate, 1.0f) == -1)//竖图
        {
            if (height != targetLength)
            {
                height = targetLength;
                width = targetLength * rate;
            }
        }
        else if (Float.compare(rate, 1.0f) == 0)//方图
        {
            height = targetWidth;
            width = targetWidth;
        }
        //if (width>imgTargetWidth)
        Logger.t(TAG).d("-------->width后:" + width + " | height:" + height);
        bubbleLayout.getLayoutParams().width = (int) width;
        bubbleLayout.getLayoutParams().height = (int) height;
//        imageView.requestLayout();
        imageView.setImageBitmap(resource);
//        imageView.setVisibility(VISIBLE);
        ((ChatMessageAdapter2) adapter).selectLast();
    }

    EMCallBack callBack = new EMCallBack()
    {
        @Override
        public void onSuccess()
        {
            final EMVideoMessageBody videoBody = (EMVideoMessageBody) message.getBody();
            final String localThumb = videoBody.getLocalThumb();
            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    showVideoThumbView(localThumb, imageView, videoBody.getThumbnailUrl(), message);
                }
            });

        }

        @Override
        public void onError(int i, String s)
        {

        }

        @Override
        public void onProgress(int i, String s)
        {

        }
    };
}

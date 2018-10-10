package com.echoesnet.eatandmeet.views.widgets.chat.chatrow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.IMUtils.ImageCache;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.views.adapters.ChatMessageAdapter2;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ChatRowImage extends ChatRowFile
{

    protected ImageView imageView;
    private RelativeLayout imageBubble;
    private EMImageMessageBody imgBody;

    private Disposable disposable;

    public ChatRowImage(Context context, EMMessage message, int position, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter)
    {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView()
    {
        inflater.inflate(message.direct() ==
                EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_picture : R.layout.ease_row_sent_picture, this);
    }

    @Override
    protected void onFindViewById()
    {
        percentageView = (TextView) findViewById(R.id.percentage);
        imageView = (ImageView) findViewById(R.id.bubble);
        imageBubble = (RelativeLayout) findViewById(R.id.image_bubble);
    }


    @Override
    protected void onSetUpView()
    {
//        if (((ChatMessageAdapter2) adapter).getItemCount() - 1 != position)
//            return;
        imgBody = (EMImageMessageBody) message.getBody();
        Logger.t(TAG).d("------------getThumbnailUrl:" + imgBody.getThumbnailUrl() + " |  getThumbnailUrl:" + imgBody.getRemoteUrl());
        if (message.direct() == EMMessage.Direct.RECEIVE)
        {
            if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING)
            {
//                imageView.setImageResource(R.drawable.qs_photo);
                //set the receive message callback
                setMessageReceiveCallback();
            }
            else
            {
                percentageView.setVisibility(View.GONE);
//                imageView.setImageResource(R.drawable.qs_photo);
                String thumbPath = imgBody.thumbnailLocalPath();
                if (!new File(thumbPath).exists())
                {
                    // to make it compatible with thumbnail received in previous version
                    thumbPath = ImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
                }
                showImageView(thumbPath, imgBody.getLocalUrl(), message);
            }
            return;
        }
        String filePath = imgBody.getLocalUrl();
        String thumbPath = ImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
//        String thumbPath = filePath;
        showImageView(thumbPath, filePath, message);
//        handleSendMessage();
    }

//    public void onSetUpView(int width, int height, Bitmap resource)
//    {
//        imageView.getLayoutParams().width = width;
//        imageView.getLayoutParams().height = height;
////        imageView.requestLayout();
//        imageView.setImageBitmap(resource);
//    }

    @Override
    protected void onUpdateView()
    {
        super.onUpdateView();
        adapter.notifyItemChanged(position);
    }

    @Override
    protected void onBubbleClick()
    {
        if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING)
        {
            //thumbnail image downloading
            return;
        }
        else if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED)
        {
//            progressBar.setVisibility(View.VISIBLE);
            percentageView.setVisibility(View.VISIBLE);
            // retry download with click event of user
            EMClient.getInstance().chatManager().downloadThumbnail(message);
        }

        List<String> finalImgLst = new ArrayList<>();
//        Intent intent = new Intent(context, EaseShowBigImageActivity.class);
        File file = new File(imgBody.getLocalUrl());
        String path;
        if (file.exists())
        {
            Uri uri = Uri.fromFile(file);
//            intent.putExtra("uri", uri);
            path = uri.toString();
        }
        else
        {
            // The local full size pic does not exist yet.
            // ShowBigImage needs to download it from the server
            // first
//            String msgId = message.getMsgId();
//            intent.putExtra("messageId", msgId);
//            intent.putExtra("localUrl", imgBody.getLocalUrl());
            path = TextUtils.isEmpty(imgBody.getThumbnailUrl()) ? imgBody.getRemoteUrl() : imgBody.getThumbnailUrl();
        }
        finalImgLst.clear();
        Map<String, String> map = ((ChatMessageAdapter2) adapter).getAllImageUrl();

//        for (int i = 0; i < map.entrySet().size(); i++)
//        {
//            Map.Entry<String, String> entry = map.
//        }
        int position = 0;
        int temp = 0;
        for (Map.Entry<String, String> stringStringEntry : map.entrySet())
        {
            if (stringStringEntry.getKey().equals(message.getMsgId()))
            {
                position = temp;
                finalImgLst.add(stringStringEntry.getValue());
            }
            else
            {
                finalImgLst.add(stringStringEntry.getValue());
            }
            temp++;
        }
        if (message != null && message.direct() == EMMessage.Direct.RECEIVE && !message.isAcked()
                && message.getChatType() == EMMessage.ChatType.Chat)
        {
            try
            {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        CommonUtils.showImageBrowser(context, finalImgLst, position, imageView);

    }

    /**
     * load image into image view
     */
    private void showImageView(final String thumbernailPath, final String localFullSizePath, final EMMessage message)
    {
        Logger.t(TAG).d("------------------>>>thumbernailPath:" + thumbernailPath);
        Logger.t(TAG).d("------------------>>>thumbnailLocalPath:" + imgBody.thumbnailLocalPath());
        Logger.t(TAG).d("------------------>>>getLocalUrl:" + imgBody.getLocalUrl());
        Logger.t(TAG).d("------------------>>>getThumbnailUrl:" + imgBody.getThumbnailUrl());
        Logger.t(TAG).d("------------------>>>getRemoteUrl:" + imgBody.getRemoteUrl());
        if (message.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_APPEND_MESSAGE, false))
        {
            File file = new File(localFullSizePath);
            if (file.exists())
            {
                reSizeImage(localFullSizePath);
            }
        }
        else
        {
            String path;
//            File file = new File(thumbernailPath);
//            if(file.exists())
//                path = thumbernailPath;
//            else if(new File(imgBody.thumbnailLocalPath()).exists())
//                path = imgBody.thumbnailLocalPath();
//            else
//                path = TextUtils.isEmpty(imgBody.getThumbnailUrl()) ? imgBody.getRemoteUrl() : imgBody.getThumbnailUrl();

//            imageView.setImageResource(R.drawable.mainBackColor);
            //imageView.setImageBitmap(ImageUtils.getBitmapFromFile(activity, imgBody.thumbnailLocalPath()));

            File file = new File(localFullSizePath);
            if (file.exists())
            {
                path = localFullSizePath;
            }
            else
            {
                path = TextUtils.isEmpty(imgBody.getThumbnailUrl()) ? imgBody.getRemoteUrl() : imgBody.getThumbnailUrl();
            }
            Logger.t(TAG).d("-------->width:path" + path);
            if (disposable != null && !disposable.isDisposed())
                disposable.dispose();
            reSizeImage(path);
        }
    }

    private void reSizeImage(final String path)
    {
        disposable = Observable.create(new ObservableOnSubscribe<Map<String, Object>>()
        {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Map<String, Object>> e) throws Exception
            {
                int resWidth = imgBody.getWidth();
                int resHeight = imgBody.getHeight();
                if (resWidth == 0 || resHeight == 0)
                {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path, options);
                    resHeight = options.outHeight;
                    resWidth = options.outWidth;
                }
                float width = resWidth;
                float height = resHeight;
                float rate = width / height;
                int targetLength = CommonUtils.dp2px(activity, 170);
                int targetWidth = CommonUtils.dp2px(activity, 120);
                int imgRate = 0;
                Logger.t(TAG).d("------------>width前:" + width + " | height:" + height + " | targetHeight:" + targetLength + " | targetWidth:" + targetWidth + " | " + "rate:" + rate + " | " + Float.compare(rate, 1.0f));
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
                        if (height / width > 3)//长图
                        {
                            width = targetLength * rate * 3;
                        }
                        else
                            width = targetLength * rate;
                    }
                }
                else if (Float.compare(rate, 1.0f) == 0)//方图
                {
                    imgRate = 0;
                    height = targetWidth;
                    width = targetWidth;
                }
                imageBubble.getLayoutParams().width = (int) width;
                imageBubble.getLayoutParams().height = (int) height;
//                resBitmap = Bitmap.createScaledBitmap(finalResource, (int) width, (int) height, true);
                Map<String, Object> map = new ArrayMap<>();
                map.put("path", path);
                map.put("width", (int) width);
                map.put("height", (int) height);
                map.put("imgRate", imgRate);

//                Bitmap bitmap = null;
//                if (!TextUtils.isEmpty(imgBody.thumbnailLocalPath()))
//                    bitmap = ImageUtils.getBitmapFromFile(activity, imgBody.thumbnailLocalPath());
//                map.put("bitmap", bitmap);
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
                        String path = obj.get("path").toString();
                        int width = (int) obj.get("width");
                        int height = (int) obj.get("height");
                        int imgRate = (int) obj.get("imgRate");
                        int placeholder = switchPlaceImg(imgRate);
//                Bitmap bitmap = (Bitmap) obj.get("bitmap");
//                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        Logger.t(TAG).d("-------------------------------------->>>>>" + imgBody.thumbnailLocalPath());
                        GlideApp.with(EamApplication.getInstance())
                                .load(path)
                                .thumbnail(0.1f)
                                .override(width, height)
                                .placeholder(placeholder)//== null ? placeholder : bitmap
                                .error(placeholder)
                                .centerCrop()
                                .into(imageView);
//                        imageView.setVisibility(VISIBLE);
                        ((ChatMessageAdapter2) adapter).selectLast();
                    }
                });
    }

    public void release()
    {
        if (imageView != null)
            imageView.setImageDrawable(null);
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


    private void downloadBitmap(String thumbernailPath, final String localFullSizePath, final EMMessage message)
    {
        final File file = new File(localFullSizePath);
        if (!file.exists())
        {
            final String tempPath = file.getParent() + "/temp_" + file.getName();

            EMCallBack callBack = new EMCallBack()
            {
                @Override
                public void onSuccess()
                {
                    ((Activity) context).runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            new File(tempPath).renameTo(file);
                            Bitmap bitmap = ImageUtils.getBitmapFromFile((Activity) context, localFullSizePath);
                            if (bitmap == null)
                            {
                                imageView.setImageResource(R.drawable.qs_photo);
                            }
                            else
                            {
                                ImageUtils.compressBitmap(bitmap, localFullSizePath, 60, 0);

                                int width = bitmap.getWidth();
                                int height = bitmap.getHeight();
                                imageView.getLayoutParams().width = width / 2;
                                imageView.getLayoutParams().height = height / 2;
                                imageView.setImageBitmap(bitmap);
                                ImageCache.getInstance().put(localFullSizePath, bitmap);
                            }
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
            message.setMessageStatusCallback(callBack);
            EMClient.getInstance().chatManager().downloadAttachment(message);
        }
        else
        {
            Bitmap bitmap = ImageUtils.getBitmapFromFile((Activity) context, localFullSizePath);
            ImageUtils.compressBitmap(bitmap, localFullSizePath, 60, 0);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            imageView.getLayoutParams().width = width / 2;
            imageView.getLayoutParams().height = height / 2;
            imageView.setImageBitmap(bitmap);
            ImageCache.getInstance().put(localFullSizePath, bitmap);
        }
        Logger.t(TAG).d("--------------------thumbernailPath:" + thumbernailPath + " | localFullSizePath:" + localFullSizePath);
        /*File file = new File(thumbernailPath);
        if (file.exists())
        {
            Logger.t(TAG).d("--------------------thumbernailPath 压缩 :");
            Bitmap bitmap = ImageUtils.getBitmapFromFile((Activity) context, thumbernailPath);
//            ImageUtils.compressBitmap(bitmap, thumbernailPath, 60, 0);
            return bitmap;
        }
        else if (new File(imgBody.thumbnailLocalPath()).exists())
        {
            Logger.t(TAG).d("--------------------imgBody.thumbnailLocalPath() 压缩 :" + imgBody.thumbnailLocalPath());
            Bitmap bitmap = ImageUtils.getBitmapFromFile((Activity) context, imgBody.thumbnailLocalPath());
            ImageUtils.compressBitmap(bitmap, imgBody.thumbnailLocalPath(), 60, 0);
            return bitmap;
        }
        else
        {
            if (message.direct() == EMMessage.Direct.SEND)
            {
                if (localFullSizePath != null && new File(localFullSizePath).exists())
                {
                    Logger.t(TAG).d("--------------------localFullSizePath 压缩 :");
                    Bitmap bitmap = ImageUtils.getBitmapFromFile((Activity) context, localFullSizePath);
                    ImageUtils.compressBitmap(bitmap, localFullSizePath, 60, 0);
                    return bitmap;
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }*/
    }


    private BitmapFactory.Options getBitmapOption(int inSampleSize)
    {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }
}

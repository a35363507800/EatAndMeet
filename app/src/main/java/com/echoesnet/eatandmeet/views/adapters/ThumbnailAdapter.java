package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.videoUtil.VideoUtil;
import com.echoesnet.eatandmeet.utils.videoUtil.VideoUtilListener;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/12/19 0019
 * @description 缩略图adapter
 */
public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ThumbnailViewHolder>
{
    private final String TAG = ThumbnailAdapter.class.getSimpleName();
    private Activity mAct;
    private int frameCount;
    private int frameTime;
    private int itemWidth;
    private String path;//视频路径
    private String fileName;
    private FFmpeg fFmpeg;
    private float rotation = 0;
    private List<String> imgPathList;

    public ThumbnailAdapter(Activity mAct, String path, int itemWidth,float rotation,List<String> imgPathList)
    {
        this.mAct = mAct;
        this.path = path;
        this.itemWidth = itemWidth;
        this.rotation = rotation;
        this.imgPathList = imgPathList;
        fFmpeg = FFmpeg.getInstance(mAct);
        if (!TextUtils.isEmpty(path))
        {
            File file = new File(path);
            fileName = file.getName();
            Logger.t(TAG).d("fileName" + file.getName() + "rotation > " + rotation);
        }

    }

    public void setFrameCount(int frameCount)
    {
        this.frameCount = frameCount;
    }

    public void setFrameTime(int frameTime)
    {
        this.frameTime = frameTime;
    }

    @Override
    public ThumbnailAdapter.ThumbnailViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        ImageView imageView = new ImageView(mAct);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(itemWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);
        ThumbnailViewHolder viewHolder = new ThumbnailViewHolder(imageView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ThumbnailAdapter.ThumbnailViewHolder holder, final int position)
    {
        final ImageView imageView =  (ImageView)holder.itemView;
        imageView.setImageDrawable(new ColorDrawable(ContextCompat.getColor(mAct, R.color.black)));
        imageView.setTag(position - 2);
        if (!(position == 0 || position == 1 || position == frameCount - 1 || position == frameCount - 2))
        {
             Logger.t(TAG).d("position>>" + position + frameCount);
//             if (position - 2 < imgPathList.size())
//             {
//                 String outPath = imgPathList.get(position - 2);
//                 GlideApp.with(EamApplication.getInstance())
//                         .asBitmap()
//                         .centerCrop()
//                         .load(outPath)
//                         .into(imageView);
//             }

//            new AsyncTask<Void, Void, Bitmap>() {
//                @Override
//                protected Bitmap doInBackground(Void... params) {
//                    long start = System.currentTimeMillis();
//                    Bitmap bitmap = mediaMetadata.getScaledFrameAtTime(1000 * (position - 2), MediaMetadataRetriever.OPTION_CLOSEST,40,80);
//                    Log.d("adapter>","" + (start - System.currentTimeMillis()));
//                    return bitmap;
//                }
//                @Override
//                protected void onPostExecute(Bitmap result) {
//                    imageView.setImageBitmap(result);
//                }
//            }.execute();
           cutVideoThumb(path,position - 2,imageView);
//            if (position >= 2 && position-2 < imgPathList.size())
//            {
//                String imgPath = imgPathList.get(position - 2);
//                GlideApp.with(mAct)
//                        .asBitmap()
//                        .load(imgPath)
//                        .into(imageView);
//            }

        }
    }
    private void cutVideoThumb(final String path, final int position, final ImageView imageView) {
        String outPath = NetHelper.getRootDirPath(mAct) + NetHelper.CUT_VIDEO_THUMBNAIL + fileName;
        File file = new File(outPath);
        if (!file.exists())
            file.mkdirs();
        final String output = outPath+"/"+position;
        File file1 = new File(output);
        if (!file1.exists()) // 如果存在不重新截图
        {
            VideoUtil.getVideoMediaThumbnail(mAct, position, path, output, rotation,false, new VideoUtilListener()
            {
                @Override
                public void start()
                {

                }

                @Override
                public void complete(String outPath, String err)
                {
                    File thumFile = new File(outPath);
                    int tag = (int) imageView.getTag();
                    Logger.t(TAG).d("截取完成>>>tag=" + tag + "| position=" + position);
                    try
                    {
                        if (position == tag)
                            GlideApp.with(EamApplication.getInstance())
                                    .asBitmap()
                                    .centerCrop()
                                    .load(outPath)
                                    .into(imageView);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        Logger.t(TAG).d("截取完成>>>e=" + e.getMessage());
                    }

                    if (!thumFile.exists())
                    {

                    }else {

                    }

                }
            });
        }else {
            imageView.setTag(null);
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(output)
                    .centerCrop()
                    .into(imageView);

            imageView.setTag(position);
        }

    }
    @Override
    public int getItemCount()
    {
        return frameCount;
    }

    static class ThumbnailViewHolder extends RecyclerView.ViewHolder
    {
        public ThumbnailViewHolder(View itemView)
        {
            super(itemView);
        }
    }
}

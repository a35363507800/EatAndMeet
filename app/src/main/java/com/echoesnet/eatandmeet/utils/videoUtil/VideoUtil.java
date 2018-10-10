package com.echoesnet.eatandmeet.utils.videoUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2018/1/8 0008
 * @description 视频处理工具
 */
public class VideoUtil
{
    public static final String TAG = VideoUtil.class.getSimpleName();

    /**
     * 剪切视频
     * @param mAct
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param path 视频路径
     * @param output 视频输出路径
     * @param videoUtilListener 完成监听
     */
    public static void cutVideo(Activity mAct,String startTime, String endTime, String path, String output,
                                VideoUtilListener videoUtilListener){
     // -ss 00:00:01 -t 00:00:09 -i /storage/emulated/0/joke_essay/1517299117596.mp4 -c:v libx264 -c:a aac -strict experimental -b:a 98k /storage/emulated/0/EatAndMeet/CutVideo/1517383378160.mp4
        FFmpeg fFmpeg = FFmpeg.getInstance(mAct);
        Logger.t(TAG).d("cut video start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " );
        StringBuilder cutVideoSb = new StringBuilder();
        cutVideoSb.append("-ss");
        cutVideoSb.append(" " + startTime);
        cutVideoSb.append(" -t");
        cutVideoSb.append(" " + endTime);
        cutVideoSb.append(" -i");
        cutVideoSb.append(" " + path);
        cutVideoSb.append(" -c:v libx264 -c:a aac -strict experimental -b:a 98k");
        cutVideoSb.append(" " + output);
        Logger.t(TAG).d("sb>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + cutVideoSb);
        long start = System.currentTimeMillis();
        try
        {
            String finalOutput = output;
            fFmpeg.execute(cutVideoSb.toString().split(" "), new FFmpegExecuteResponseHandler()
            {
                @Override
                public void onSuccess(String message)
                {
                    Logger.t(TAG).d("剪切视频onSuccess>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                }

                @Override
                public void onProgress(String message)
                {
                    Logger.t(TAG).d("剪切视频onProgress>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + message);
                }

                @Override
                public void onFailure(String message)
                {
                    Logger.t(TAG).d("剪切视频onFailure>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + message);
                }

                @Override
                public void onStart()
                {
                    Logger.t(TAG).d("剪切视频onStart>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    if (videoUtilListener != null)
                        videoUtilListener.start();
                }

                @Override
                public void onFinish()
                {
                    Logger.t(TAG).d("剪切视频onFinish>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 耗时" +(System.currentTimeMillis() - start) + finalOutput );
                    if (videoUtilListener != null)
                        videoUtilListener.complete(finalOutput,"");
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e)
        {
            if (videoUtilListener != null)
                videoUtilListener.complete("",e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取视频缩略图并缓存到本地
     * @param mAct
     * @param time 需要获取时间点
     * @param path 视频路径
     * @param output 缩略图输出路径
     * @param isOriginal 是否是原图
     * @param videoUtilListener
     */
    public static void getVideoThumbnail(Activity mAct,String time,String path, String output,boolean isOriginal,
                                VideoUtilListener videoUtilListener){
//        FFmpeg fFmpeg = FFmpeg.getInstance(mAct);
//        Logger.t(TAG).d("getVideoThumbnail start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " );
//        StringBuilder sb = new StringBuilder();
//        sb.append("-ss");
//        sb.append(" "+ time);
//        sb.append(" -i");
//        sb.append(" "+path);
//        sb.append(" -f");
//        sb.append(" image2");
//        if (!isOriginal){
//            sb.append(" -s");
//            sb.append(" 30x60");
//        }
//        sb.append(" "+output);
//            String finalOutput = output;
//        try
//        {
//            fFmpeg.execute(sb.toString().split(" "), new FFmpegExecuteResponseHandler()
//            {
//                @Override
//                public void onSuccess(String message)
//                {
//                    Logger.t(TAG).d("缩略图获取onSuccess>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//                }
//
//                @Override
//                public void onProgress(String message)
//                {
//                    Logger.t(TAG).d("缩略图获取onProgress>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//                }
//
//                @Override
//                public void onFailure(String message)
//                {
//                    Logger.t(TAG).d("缩略图获取onFailure>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + message);
////                    if (videoUtilListener != null)
////                        videoUtilListener.complete(finalOutput,message);
//                }
//
//                @Override
//                public void onStart()
//                {
//                    Logger.t(TAG).d("缩略图获取onStart>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//                    if (videoUtilListener != null)
//                        videoUtilListener.start();
//                }
//
//                @Override
//                public void onFinish()
//                {
//                    Logger.t(TAG).d("缩略图获取onFinish>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + finalOutput );
//                    if (videoUtilListener != null)
//                        videoUtilListener.complete(finalOutput,"");
//                }
//            });
//        } catch (FFmpegCommandAlreadyRunningException e)
//        {
//            e.printStackTrace();
//        }
        Observable.create(new ObservableOnSubscribe<String>()
        {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception
            {
                FFmpeg fFmpeg = FFmpeg.getInstance(mAct);
                Logger.t(TAG).d("getVideoThumbnail start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " );
                StringBuilder sb = new StringBuilder();
                sb.append("-ss");
                sb.append(" "+ time);
                sb.append(" -i");
                sb.append(" "+path);
                sb.append(" -f");
                sb.append(" image2");
                if (!isOriginal){
                    sb.append(" -s");
                    sb.append(" 30x60");
                }
                sb.append(" "+output);
                String finalOutput = output;
                String[] ffmpegBinary = new String[] { getFFmpeg(mAct, null) };
                String[] command = concatenate(ffmpegBinary, sb.toString().split(" "));
                Logger.t(TAG).d("sb>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + Arrays.toString(command));
                Process process = null;
                try
                {
                    File outFile = new File(output).getParentFile();
                    if (!outFile.exists())
                        outFile.mkdirs();
                    process = Runtime.getRuntime().exec(command);
                    Logger.t(TAG).d("process>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + process);
                } catch (Exception er)
                {
                    Logger.t(TAG).d("缩略图获取err" + er.getMessage());
                    if (videoUtilListener != null)
                        videoUtilListener.complete("",er.getMessage());
                    er.printStackTrace();
                }
                if (process != null)
                    process.destroy();
                e.onNext(output);
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>()
                {
                    @Override
                    public void accept(String s) throws Exception
                    {
                        Logger.t(TAG).d("缩略图获取 suc" + s);
                        if (videoUtilListener != null)
                            videoUtilListener.complete(s,"");
                    }
                });

    }

    /**
     * 获取视频缩略图
     * @param mAct
     * @param path 视频路径
     * @param output 缩略图输出路径
     * @param videoUtilListener
     */
    public static void compressVideo(Activity mAct,String path, String output,
                                VideoUtilListener videoUtilListener){
        File outFile = new File(output).getParentFile();
        if (!outFile.exists())
            outFile.mkdirs();
        FFmpeg fFmpeg = FFmpeg.getInstance(mAct);
        Logger.t(TAG).d("getVideoThumbnail start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " );
        StringBuilder sb = new StringBuilder();
        sb.append("-i");
        sb.append(" "+path);
        sb.append(" -vcodec libx264 -preset fast -crf 20 -y -vf scale=1920:-1 -acodec libmp3lame -ab 128k");
        sb.append(" "+ output);
        Logger.t(TAG).d("sb>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + sb);
        try
        {
            String finalOutput = output;
            fFmpeg.execute(sb.toString().split(" "), new FFmpegExecuteResponseHandler()
            {
                @Override
                public void onSuccess(String message)
                {
                    Logger.t(TAG).d("视频压缩onSuccess>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                }

                @Override
                public void onProgress(String message)
                {
                    Logger.t(TAG).d("视频压缩onProgress>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                }

                @Override
                public void onFailure(String message)
                {
                    Logger.t(TAG).d("视频压缩onFailure>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + message);
//                    if (videoUtilListener != null)
//                        videoUtilListener.complete(finalOutput,message);
                }

                @Override
                public void onStart()
                {
                    Logger.t(TAG).d("视频压缩onStart>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    if (videoUtilListener != null)
                        videoUtilListener.start();
                }

                @Override
                public void onFinish()
                {
                    Logger.t(TAG).d("视频压缩onFinish>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + finalOutput );
                    if (videoUtilListener != null)
                        videoUtilListener.complete(finalOutput,"");
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e)
        {
            Logger.t(TAG).d("视频压缩err" + e.getMessage());
            if (videoUtilListener != null)
                videoUtilListener.complete("",e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取视频缩略图
     * @param mAct
     * @param time 需要获取时间点
     * @param path 视频路径
     * @param output 缩略图输出路径
     * @param videoUtilListener
     */
    public static void getVideoMediaThumbnail(Activity mAct,int time,String path, String output,float rotation,boolean isOriginal,
                                VideoUtilListener videoUtilListener){
        Observable.create(new ObservableOnSubscribe<String>()
        {
            @Override
            public void subscribe(ObservableEmitter<String> o) throws Exception
            {
                Bitmap bitmap = null;
                FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
                try {
                    retriever.setDataSource(path);
                    int with = 60;
                    int height = 120;
                    if (rotation == 90 || rotation == 270)
                    {
                        int temp = with;
                        with = height;
                        height = temp;
                    }
                    if (isOriginal)
                    {
                        bitmap = retriever.getFrameAtTime(1000000 * time, MediaMetadataRetriever.OPTION_CLOSEST);
                    }else {
                        bitmap = retriever.getScaledFrameAtTime(1000000 * time, MediaMetadataRetriever.OPTION_CLOSEST,with,height);
                    }
                    bitmap = ImageUtils.rotateImageView(rotation,bitmap);
                }
                catch(IllegalArgumentException e) {
                    e.printStackTrace();
                }
                catch (RuntimeException e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        retriever.release();
                    }
                    catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(output);
                    if (fos != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                o.onNext(output);
            }
        }).subscribeOn(Schedulers.computation())
                .unsubscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>()
                {
                    @Override
                    public void accept(String s) throws Exception
                    {
                        if (videoUtilListener != null)
                            videoUtilListener.complete(output,"");
                    }
                });


    }


    /**
     * @param videoPath
     * @return 0 横屏 1 竖屏
     */
    public static String getVideoOrientation(String videoPath){
        try
        {
            MediaMetadataRetriever retr = new MediaMetadataRetriever();
            retr.setDataSource(videoPath);
            float rotation = 0;
            String height = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT); // 视频高度
            String width = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH); // 视
            int videoWidth = Integer.valueOf(width);
            int videoHeight = Integer.valueOf(height);
            try
            {
                rotation = Float.valueOf(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
            } catch (NumberFormatException e)
            {
                e.printStackTrace();
            }
            if (rotation == 90 || rotation == 270)
            {
                int temp = videoWidth;
                videoWidth = videoHeight;
                videoHeight = temp;
            }
            Logger.t(TAG).d("videoWidth>>" + videoWidth + "|videoHeight>>" + videoHeight + "|rotation>>" + rotation);
            if (videoWidth > videoHeight)
                return "0";
            else
                return "1";
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return "0";
    }
    private static String getFFmpeg(Context context, Map<String, String> environmentVars) {
        String ffmpegCommand = "";
        if (environmentVars != null) {
            for (Map.Entry<String, String> var : environmentVars.entrySet()) {
                ffmpegCommand += var.getKey()+"="+var.getValue()+" ";
            }
        }
        ffmpegCommand += context.getFilesDir().getAbsolutePath() + File.separator +"ffmpeg";
        return ffmpegCommand;
    }

    private static <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }
}

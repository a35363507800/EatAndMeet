package com.echoesnet.eatandmeet.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.http4retrofit2.down.DownloadHttpMethods;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.echoesnet.eatandmeet.utils.GlideOptions.bitmapTransform;

/**
 * @author kevin
 */
public class LruCacheBitmapLoader
{
    private static String TAG = LruCacheBitmapLoader.class.getSimpleName();
    private static LruCacheBitmapLoader lruCacheBitmapLoader;

    /**
     * 缓存Image的类，当存储Image的大小大于LruCache设定的值，系统自动释放内存
     */
    private LruCache<String, Bitmap> mMemoryCache;
//    private LruCache<String, BitmapDrawable> mDrawableCache;

    public static LruCacheBitmapLoader getInstance()
    {
        if (lruCacheBitmapLoader == null)
        {
            lruCacheBitmapLoader = new LruCacheBitmapLoader();
        }
        return lruCacheBitmapLoader;
    }

    public LruCacheBitmapLoader()
    {
        //获取系统分配给每个应用程序的最大内存，每个应用系统分配32M
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 4;
        //给LruCache分配1/8 4M
        mMemoryCache = new LruCache<String, Bitmap>(mCacheSize)
        {
            //必须重写此方法，来测量Bitmap的大小
            @Override
            protected int sizeOf(String key, Bitmap value)
            {
                return value.getRowBytes() * value.getHeight();
            }
        };

//        mDrawableCache = new LruCache<String, BitmapDrawable>(mCacheSize){
//            //必须重写此方法，来测量Bitmap的大小
//            @Override
//            protected int sizeOf(String key, BitmapDrawable value) {
//                return value.getBitmap().getRowBytes() * value.getBitmap().getHeight();
//            }
//        };

    }

    public void recycle()
    {
        mMemoryCache.evictAll();
    }


    /**
     * <b color="#00f543">先检查map 中 bitmap<br> 然后本地文件查找<br> 最后缺省图+loading<br></b>
     *
     * @param act
     * @param key       URL
     * @param target
     * @param reqWidth  压缩尺寸
     * @param reqHeight 压缩尺寸
     */
    public void putBitmapInto(final Activity act, final String key, final ImageView target, final int reqWidth, final int reqHeight)
    {
        if (getBitmapFromMemCache(key) != null)
        {
            target.setImageBitmap(getBitmapFromMemCache(key));
        }
        else
        {
            String filePath = null;
            try
            {
                filePath = NetHelper.getRootDirPath(act) + "gift" + File.separator + MD5Util.MD5(key);
            } catch (Exception e)
            {
                e.printStackTrace();
                Logger.t(TAG).d(e.getMessage());
            }
            if (filePath==null)
                return;
            File file = new File(filePath);
            if (file.exists())
            {
                Bitmap bitmap = decodeBitmapFromFile8888(filePath, reqWidth, reqHeight);
                addBitmapToMemoryCache(key, bitmap);
                target.setImageBitmap(bitmap);
            }
            else
            {
                target.setImageDrawable(ContextCompat.getDrawable(EamApplication.getInstance(),R.drawable.userhead));
                String baseUrl = CommonUtils.getHostName(key);
                final String finalFilePath = filePath;
                new DownloadHttpMethods(baseUrl, null).downloadFile(key, file, new Observer()
                {
                    @Override
                    public void onSubscribe(Disposable d)
                    {

                    }

                    @Override
                    public void onNext(Object value)
                    {

                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        e.printStackTrace();
                        Logger.t(TAG).d("LruCacheBitmapLoader", "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete()
                    {
                        if (null != act && null != target)
                        {
                            Bitmap bitmap = decodeBitmapFromFile8888(finalFilePath, reqWidth, reqHeight);
                            addBitmapToMemoryCache(key, bitmap);
                            target.setImageBitmap(bitmap);
                        }
                    }
                });
            }
        }
    }
    /**
     * <b color="#00f543">先检查map 中 bitmap<br> 然后本地文件查找<br> 最后缺省图+loading<br></b>
     *
     * @param act
     * @param key       URL
     * @param target
     * @param reqWidth  压缩尺寸
     * @param reqHeight 压缩尺寸
     */
    public void putBitmapInto(final Activity act, final String key, final Map target, final int reqWidth, final int reqHeight)
    {
        if (getBitmapFromMemCache(key) != null)
        {
            target.put(key,getBitmapFromMemCache(key));
        }
        else
        {
            String filePath = null;
            try
            {
                filePath = NetHelper.getRootDirPath(act) + "gift" + File.separator + MD5Util.MD5(key);
            } catch (Exception e)
            {
                e.printStackTrace();
                Logger.t(TAG).d(e.getMessage());
            }
            File file = new File(filePath);
            if (file.exists())
            {
                Bitmap bitmap = decodeBitmapFromFile8888(filePath, reqWidth, reqHeight);
                addBitmapToMemoryCache(key, bitmap);
                target.put(key,bitmap);
            }
            else
            {

                String baseUrl = CommonUtils.getHostName(key);
                final String finalFilePath = filePath;
                new DownloadHttpMethods(baseUrl, null).downloadFile(key, file, new Observer()
                {
                    @Override
                    public void onSubscribe(Disposable d)
                    {

                    }

                    @Override
                    public void onNext(Object value)
                    {

                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        e.printStackTrace();
                        Logger.t(TAG).d("LruCacheBitmapLoader", "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete()
                    {
                        if (null != act && null != target)
                        {
                            Bitmap bitmap = decodeBitmapFromFile8888(finalFilePath, reqWidth, reqHeight);
                            addBitmapToMemoryCache(key, bitmap);
                            target.put(key,bitmap);
                        }
                    }
                });
            }
        }
    }

    public void putBitmapFromDrawable(Context context, final int key, final ImageView target){
        String keyStr = String.valueOf(key);
        if (getBitmapFromMemCache(keyStr) != null)
        {
            target.setImageBitmap(getBitmapFromMemCache(keyStr));
        }else {
           Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),key);
            if (bitmap != null){
                addBitmapToMemoryCache(keyStr,bitmap);
                target.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * 文件中读取图片
     *
     * @param filePath
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public Bitmap decodeBitmapFromFile8888(String filePath, int reqWidth, int reqHeight)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
//		options.inPreferredConfig = Bitmap.Config.ARGB_4444;		//降低图片位数,真色彩失真很严重,效果很差....
//        options.inPreferredConfig = Bitmap.Config.RGB_565;		//使用不带透明度的方法解析,比8888方法,减少一半内存占用
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;        //
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false; // 设置了此属性一定要记得将值设置为false
        return BitmapFactory.decodeFile(filePath, options);
    }


    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {
//			if (width > height) {
//				inSampleSize = Math.round((float) height / (float) reqHeight);
//			} else {
//				inSampleSize = Math.round((float) width / (float) reqWidth);
//			}
            if (reqWidth > reqHeight)
            {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
            else
            {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            }
        }
        return inSampleSize;
    }

    /**
     * 添加Bitmap到内存缓存
     *
     * @param key
     * @param bitmap
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap)
    {
        if (getBitmapFromMemCache(key) == null && bitmap != null)
        {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从内存缓存中获取一个Bitmap
     *
     * @param key
     * @return
     */
    public Bitmap getBitmapFromMemCache(String key)
    {
        return mMemoryCache.get(key);
    }


    public Bitmap getResBitmap(Context ctx, int id, int reqWidth, int reqHeight)
    {
        String key = id + "";
        Bitmap bm = getBitmapFromMemCache(key);
        if (bm == null)
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
//		options.inPreferredConfig = Bitmap.Config.ARGB_4444;		//降低图片位数,真色彩失真很严重,效果很差....
//        options.inPreferredConfig = Bitmap.Config.RGB_565;		//使用不带透明度的方法解析,比8888方法,减少一半内存占用
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;        //
            BitmapFactory.decodeResource(ctx.getResources(), id, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false; // 设置了此属性一定要记得将值设置为false
            InputStream is = ctx.getResources().openRawResource(id);
            bm = BitmapFactory.decodeStream(is, null, options);
//            bm = BitmapFactory.decodeResource(ctx.getResources(), id, options);
            addBitmapToMemoryCache(key, bm);
        }
        return bm;
    }

//
//    public Bitmap getResBitmap(Context ctx , int id, int reqWidth, int reqHeight, String size ) {
//        // size = s m b;
//        String key = id+size;
//        Bitmap bm = getBitmapFromMemCache(key);
//        if(bm == null ){
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
////		options.inPreferredConfig = Bitmap.Config.ARGB_4444;		//降低图片位数,真色彩失真很严重,效果很差....
////        options.inPreferredConfig = Bitmap.Config.RGB_565;		//使用不带透明度的方法解析,比8888方法,减少一半内存占用
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;		//
//            BitmapFactory.decodeResource(ctx.getResources(),id,options);
//            switch (size){
//                case "s":
//                    options.inSampleSize = 1;
//                    break;
//                case "m":
//                    options.inSampleSize = 2;
//                    break;
//                case "b":
//                    options.inSampleSize = 3;
//                    break;
//            }
////            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//            options.inJustDecodeBounds = false; // 设置了此属性一定要记得将值设置为false
//            bm= BitmapFactory.decodeResource(ctx.getResources(),id,options);
//
//
//            addBitmapToMemoryCache(key,bm);
//        }
//        return bm;
//    }


    private int frameDuration = 200;

    /**
     * 帧动画大图Bitmap
     * <b color="#00f543">先检查map 中 bitmap<br> 然后本地文件查找<br>
     * <p>
     * int pIndex=0 不一定是从0开始的。。。图片会断帧。<br>
     * lostTimes 断帧次数，结束文件加载循环用<br>
     * <p>
     * <p>
     * <p>
     * </b>
     *
     * @param act
     * @param giftResPath
     * @param reqWidth    压缩尺寸
     * @param reqHeight   压缩尺寸
     * @param maxLostTime 断帧次数，结束文件加载循环用。
     */
    public AnimationDrawable createBigGiftAnimationFromDisk(final Activity act, final String giftResPath, final int reqWidth, final int reqHeight, int maxLostTime)
    {
        AnimationDrawable ad4Back = new AnimationDrawable();
        //文件夹 -> g_0016，g_0015   ->> come from filePath;
        //pic file -> "0.png","130.png".... ->> make it by this method;

        int lostTimes = 0;
        int pIndex = 0;

        String filePath = NetHelper.getRootDirPath(act) + File.separator + "bigGift" + File.separator + giftResPath + File.separator + pIndex;
        File file = new File(filePath);

        while (lostTimes < maxLostTime)
        {
            if (file.exists())
            {
                lostTimes = 0; //本帧存在，清零最大失帧数

                String key4Cache = giftResPath + "p" + pIndex; //just a Key
                if (getBitmapFromMemCache(key4Cache) != null)
                {
                    ad4Back.addFrame(new BitmapDrawable(act.getResources(), getBitmapFromMemCache(key4Cache)), frameDuration);
                }
                else
                {
                    Bitmap bitmap = decodeBitmapFromFile8888(filePath, reqWidth, reqHeight);
                    addBitmapToMemoryCache(key4Cache, bitmap);
                    ad4Back.addFrame(new BitmapDrawable(act.getResources(), bitmap), frameDuration);
                }

            }
            else
            {
                lostTimes++;
            }

            //work done;
            pIndex++; // new next file,
            filePath = NetHelper.getRootDirPath(act) + File.separator + "bigGift" + File.separator + giftResPath + File.separator + pIndex;
            file = new File(filePath);
        }

        return ad4Back;
    }

    /**
     * 帧动画大图Bitmap
     * @param act
     * @param giftResPath
     * @param reqWidth
     * @param reqHeight
     * @param maxLostTime
     * @param targetV
     * @param duration 帧间隔时间（由于存在线程切换的问题，此时间没有包含将图片设置到控件上的时间，设置时候注意），
     * @param cb
     */
    public void runBigGiftAnimation(final Activity act, final String giftResPath, final int reqWidth,
                                    final int reqHeight, final int maxLostTime, final ImageView targetV, final int duration, FrameAnimCallBack cb)
    {
        this.callBack = cb;
        //文件夹 -> g_0016，g_0015   ->> come from filePath;
        //pic file -> "0.png","130.png".... ->> make it by this method;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                int lostTimes = 0;
                int pIndex = 0;

                String filePath = NetHelper.getRootDirPath(act) + File.separator + "bigGift" + File.separator + giftResPath + File.separator + pIndex;
                File file = new File(filePath);

                while (lostTimes < maxLostTime)
                {
                    if (file.exists())
                    {
                        lostTimes = 0; //本帧存在，清零最大失帧数
                        final String file4Path = filePath;

                        long start=System.currentTimeMillis();
                        final Bitmap bitMap=BitmapFactory.decodeFile(file4Path);
                        long end=System.currentTimeMillis();
                        int interval=0,loadPigTime= (int) (end-start);
                        if (loadPigTime>duration)
                            interval=0;
                        else
                            interval=duration-loadPigTime;

                        act.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                targetV.setImageBitmap(bitMap);
                            }
                        });
                        try
                        {
                            Thread.sleep(interval);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        lostTimes++;
                    }

                    //work done;
                    pIndex++; // new next file,
                    filePath = NetHelper.getRootDirPath(act) + File.separator + "gift" + File.separator +
                            "bigGift" + File.separator + giftResPath + File.separator + pIndex;
                    file = new File(filePath);
                }

                act.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (null != callBack)
                        {
                            callBack.onAnimFinished();
                        }
                    }
                });
            }
        }).start();
    }

    private FrameAnimCallBack callBack;

    public interface FrameAnimCallBack
    {
        void onAnimFinished();
    }

}

package com.echoesnet.eatandmeet.utils.ImageUtils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;
import com.orhanobut.logger.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.echoesnet.eatandmeet.utils.GlideOptions.bitmapTransform;

//import com.zfdang.multiple_images_selector.ImagesSelectorActivity;
//import com.zfdang.multiple_images_selector.SelectorSettings;

/**
 * Created by wangben on 2016/8/22.
 */
public class ImageUtils
{
    private static final String TAG = ImageUtils.class.getSimpleName();

    public static File saveBitmapToFile(File imgFile, int compressQuality)
    {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;

        File file = new File(extStorageDirectory, "temp.jpg");
        try
        {
            // make a new bitmap from your file
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getPath());
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, outStream);
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                outStream.flush();
                outStream.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        return file;
    }

    public static String getImagePath(String remoteUrl)
    {
        String imageName = remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1, remoteUrl.length());
        String path = PathUtil.getInstance().getImagePath() + "/" + imageName;
        EMLog.d("msg", "image path:" + path);
        return path;

    }

    public static String getThumbnailImagePath(String thumbRemoteUrl)
    {
        String thumbImageName = thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
        String path = PathUtil.getInstance().getImagePath() + "/" + "th" + thumbImageName;
        EMLog.d("msg", "thum image path:" + path);
        return path;
    }

    public static File convertImageToFile(Bitmap bitmap, String filePath, int quality)
    {
        //create a file to write bitmap data
        File f = new File(filePath);
        FileOutputStream fos = null;
        try
        {
            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            //write the bytes in file
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);

            return f;
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {

            try
            {
                fos.flush();
                fos.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 读取照片exif信息中的旋转角度
     *
     * @param path 照片路径
     * @return角度
     */
    public static int readPictureDegree(String path)
    {
        int degree = 0;
        try
        {
            ExifInterface exifInterface = new ExifInterface(path);
            Logger.t(TAG).d("exifInterface》" + exifInterface + " , ");
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Logger.t(TAG).d("orientation为》" + orientation + " , ");
            switch (orientation)
            {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotateImageView(float angle, Bitmap bitmap)
    {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        //Logger.t(TAG).d("旋转的角度为=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return resizedBitmap;
    }


    public static Bitmap rotaingImageView2(int angle, Uri uri, Context context)
    {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        // 创建新的图片
        try
        {
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), hostUri);
            Bitmap bitmap = getThumbnail(uri, 500, context);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return resizedBitmap;
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        }
        return null;
    }

    // 测试获取Bitmap 参数size宽和高的最大值。
    public static Bitmap getThumbnail(Uri uri, int size, Context context) throws FileNotFoundException, IOException
    {
        InputStream input = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;
        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;
        double ratio = (originalSize > size) ? (originalSize / size) : 1.0;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio)
    {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }

    public static void saveImageToGallery(Context context, Bitmap bmp, String bitmapName)
    {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "EatAndMeet");
        FileOutputStream fos = null;
        if (!appDir.exists())
        {
            appDir.mkdir();
        }
        String fileName = bitmapName + ".jpg";
        File file = new File(appDir, fileName);
        try
        {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);

        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                fos.flush();
                fos.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

        }

        // 其次把文件插入到系统图库
        try
        {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "EatAndMeet")));
    }

    public static void saveImageToGallery(Context context, File file)
    {
        // 其次把文件插入到系统图库
        try
        {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "EatAndMeet")));
    }

    /**
     * 压缩图片
     *
     * @param filePath
     * @return
     */
    private static Bitmap getSmallBitmap(String filePath)
    {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bm = BitmapFactory.decodeFile(filePath, options);
        if (bm == null)
        {
            return null;
        }
        int degree = readPictureDegree(filePath);
        bm = rotateBitmap(bm, degree);
        ByteArrayOutputStream baos = null;
        try
        {
            baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, baos);

        } finally
        {
            try
            {
                if (baos != null)
                    baos.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return bm;
    }

    /**
     * 从文件中获得压缩图片，压缩质量
     *
     * @param filePath
     * @param quality
     * @return
     */
    private static Bitmap compressBitmapFromFile(String filePath, int quality)
    {
        Bitmap bm = BitmapFactory.decodeFile(filePath);
        ByteArrayOutputStream baos = null;
        try
        {
            baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        } finally
        {
            try
            {
                if (baos != null)
                    baos.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return bm;
    }

    public static File convertFileFromImg(Context context, String newFileName, String sourceFilePath, int quality)
    {
        File file = new File(NetHelper.getRootDirPath(context), newFileName);
        FileOutputStream out = null;
        ByteArrayOutputStream stream = null;
        Bitmap smallBitmap = compressBitmapFromFile(sourceFilePath, quality);
        if (file.exists())
        {
            file.delete();
        }
        try
        {
            stream = new ByteArrayOutputStream();
            smallBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            out = new FileOutputStream(file);
            out.write(stream.toByteArray());

        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                stream.close();
                out.flush();
                out.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        Logger.t(TAG).d(file.length() / 1024 + "  源文件大小");
        return file;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }

        return inSampleSize;
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int rotate)
    {
        if (bitmap == null)
            return null;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        // Setting post rotate to 90
        Matrix mtx = new Matrix();
        mtx.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    /**
     * 保存图片到指定位置
     *
     * @param context
     * @param newFileName 自定义图片名称
     * @return
     */
    public static File getFileFromImage(Context context, Bitmap bitmap, String newFileName)
    {
        File file = new File(NetHelper.getRootDirPath(context), newFileName);
        FileOutputStream out = null;
        ByteArrayOutputStream stream = null;
        if (file.exists())
        {
            file.delete();
        }
        try
        {
            stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            out = new FileOutputStream(file);
            out.write(stream.toByteArray());
            Logger.t("公共方法").d("图片已保存--文件名称-- " + file.getAbsolutePath());
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {


            try
            {
                stream.close();
                out.flush();
                out.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return file;
    }


    /**
     * 以屏幕尺寸压缩图片
     *
     * @param context
     * @param absolutePath
     * @return
     */
    public static Bitmap getBitmapFromFile(Activity context, String absolutePath)
    {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        // 这个inJustDecodeBounds很重要
        opt.inJustDecodeBounds = true;
        // 获取到这个图片的原始宽度和高度
        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;

        if (context == null)
        {
            return BitmapFactory.decodeFile(absolutePath, opt);
        }
        // 获取屏的宽度和高度
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();
        Logger.t(TAG).d("屏幕宽：" + display.getWidth() + "屏幕高：" + display.getHeight());

        // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
        opt.inSampleSize = 1;
        // 根据屏的大小和图片大小计算出缩放比例
        if (picWidth > picHeight)
        {
            if (picWidth > screenWidth)
                opt.inSampleSize = picWidth / screenWidth;
        }
        else
        {
            if (picHeight > screenHeight)

                opt.inSampleSize = picHeight / screenHeight;
        }
        // 这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(absolutePath, opt);
    }

    /**
     * Uri转Bitmap
     *
     * @param uri
     * @param mContext
     * @return
     */
    public static Bitmap getBitmapFromUri(Uri uri, Activity mContext)
    {
        Logger.t(TAG).d("hostUri：" + uri.getPath());
        BitmapFactory.Options opt = new BitmapFactory.Options();
        // 这个isjustdecodebounds很重要
        opt.inJustDecodeBounds = true;
        Bitmap bm = null;
        try
        {
            Logger.t(TAG).d("流：" + mContext.getContentResolver().openInputStream(uri));
            bm = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri), null, opt);
        } catch (FileNotFoundException e)
        {
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
        }

        // 获取到这个图片的原始宽度和高度
        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;

        // 获取屏的宽度和高度
        WindowManager windowManager = mContext.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
 /*        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();*/

        Logger.t(TAG).d("屏幕宽：" + display.getWidth() + "屏幕高：" + display.getHeight());

        int screenWidth = 480;
        int screenHeight = 800;

        // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
        opt.inSampleSize = 1;
        // 根据屏的大小和图片大小计算出缩放比例
        if (picWidth > picHeight)
        {
            if (picWidth > screenWidth)
                opt.inSampleSize = picWidth / screenWidth;
        }
        else
        {
            if (picHeight > screenHeight)

                opt.inSampleSize = picHeight / screenHeight;
        }
        // 这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false;
        try
        {
            bm = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri), null, opt);
        } catch (FileNotFoundException e)
        {
            Logger.t(TAG).d("exception:" + e.getMessage());
            e.printStackTrace();
        }
        return bm;
    }

    /**
     * @param image    bitmap对象
     * @param filePath 要保存的指定目录
     * @Description: 通过JNI图片压缩把Bitmap保存到指定目录
     * @author XiaoSai
     * @date 2016年3月23日 下午6:28:15
     * @version V1.0.0
     */
    /**
     * 压缩图片
     *
     * @param image    原图
     * @param filePath 图片保存目标目录
     * @param maxSize  图片最大大小
     * @param angle    旋转角度
     */
    public static void compressBitmap(Bitmap image, String filePath, int maxSize, int angle)
    {
        if (maxSize < 150)
            maxSize = 150;
        // 获取尺寸压缩倍数
        int ratio = ImageUtils.getRatioSize(image.getWidth(), image.getHeight());
        // 压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap(image.getWidth() / ratio, image.getHeight() / ratio, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, image.getWidth() / ratio, image.getHeight() / ratio);
        canvas.drawBitmap(image, null, rect, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        result.compress(Bitmap.CompressFormat.JPEG, options, baos);
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > maxSize)
        {
            // 重置baos即清空baos
            baos.reset();
            // 每次都减少10
            options -= 10;
            // 这里压缩options%，把压缩后的数据存放到baos中
            result.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        // JNI保存图片到SD卡 这个关键
        ImageUtils.saveBitmap(result, options, filePath, angle);
        if (result != null && !result.isRecycled())        // 释放Bitmap
        {
            result.recycle();
            result = null;
        }
    }

    private static File saveBitmap(Bitmap bitmap, int quality, String filePath, int angle)
    {
        Bitmap rotatedBitmap;
        FileOutputStream out = null;
        ByteArrayOutputStream stream = null;
        if (angle != 0)
        {
            //旋转图片 动作
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        else
        {
            rotatedBitmap = bitmap;
        }

        File file = new File(filePath);
        if (file.exists())
        {
            file.delete();
        }
        try
        {
            stream = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
            out = new FileOutputStream(filePath);
            out.write(stream.toByteArray());

        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                stream.close();
                out.flush();
                out.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        return file;
    }

    /**
     * 计算缩放比
     *
     * @param bitWidth  当前图片宽度
     * @param bitHeight 当前图片高度
     * @return
     * @Description:函数描述
     * @author XiaoSai
     * @date 2016年3月21日 下午3:03:38
     * @version V1.0.0
     */
    public static int getRatioSize(int bitWidth, int bitHeight)
    {
        // 图片最大分辨率
        int imageHeight = 1280;
        int imageWidth = 960;

        // 缩放比
        int ratio = 1;
        // 缩放比,由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        if (bitWidth > bitHeight && bitWidth > imageWidth)
        {
            // 如果图片宽度比高度大,以宽度为基准
            ratio = bitWidth / imageWidth;
        }
        else if (bitWidth < bitHeight && bitHeight > imageHeight)
        {
            // 如果图片高度比宽度大，以高度为基准
            ratio = bitHeight / imageHeight;
        }
        // 最小比率为1
        if (ratio <= 0)
            ratio = 1;
        return ratio;
    }

    /**
     * 毛玻璃化图片
     *
     * @param context
     * @param imgUrl      要处理的图片链接
     * @param blurDensity 毛玻璃效率的力度 取值一般为 1到10
     * @param imageView
     * @param isNetImg    是否是存放在Ucloud的网络图片，不是得话，为false
     * @throws IllegalArgumentException blurDensity为正整数
     */
    public static void showLoadingCover(Context context, String imgUrl, final int blurDensity, final ImageView imageView, boolean isNetImg)
    {
        if (isNetImg)
        {
            //使用uclound获得缩略图
            imgUrl = imgUrl + "?iopcmd=thumbnail&type=1&scale=20";
        }
        if (blurDensity == 0)
            throw new IllegalArgumentException("值为正整数");
        GlideApp.with(context)
                .asBitmap()
                .load(imgUrl)
                .error(R.drawable.userhead)
                .skipMemoryCache(false)
                .into(new SimpleTarget<Bitmap>()
                {
                    @Override
                    public void onResourceReady(Bitmap originBitmap, Transition<? super Bitmap> glideAnimation)
                    {
                        int scaleRatio = blurDensity;
                        int blurRadius = 8;
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originBitmap,
                                originBitmap.getWidth() / scaleRatio,
                                originBitmap.getHeight() / scaleRatio,
                                false);
                        Bitmap blurBitmap = FastBlurUtil.doBlur(scaledBitmap, blurRadius, true);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setImageBitmap(blurBitmap);
                    }
                });
    }

    @Deprecated
    public static void showLoadingCover(Context context, String imgUrl, final ImageView imageView)
    {
        //使用uclound获得缩略图
        imgUrl = imgUrl + "?iopcmd=thumbnail&type=1&scale=20";
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(imgUrl)
                .placeholder(R.drawable.userhead)
                .error(R.drawable.userhead)
                .skipMemoryCache(false)
                .into(new SimpleTarget<Bitmap>()
                {
                    @Override
                    public void onResourceReady(Bitmap originBitmap, Transition<? super Bitmap> glideAnimation)
                    {
                        int scaleRatio = 1;
                        int blurRadius = 8;
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originBitmap,
                                originBitmap.getWidth() / scaleRatio,
                                originBitmap.getHeight() / scaleRatio,
                                false);
                        Bitmap blurBitmap = FastBlurUtil.doBlur(scaledBitmap, blurRadius, true);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setImageBitmap(blurBitmap);

                    }
                });
    }

    /**
     * 直播间毛玻璃化图片
     *
     * @param imgUrl    要处理的图片链接
     * @param imageView
     */
    public static void newShowLoadingCover(String imgUrl, final ImageView imageView)
    {
        //使用uclound获得缩略图
        if (!TextUtils.isEmpty(imgUrl) && imgUrl.contains("http://huisheng.ufile.ucloud"))
        {
            imgUrl = imgUrl + "?iopcmd=thumbnail&type=1&scale=20";
        }
        GlideApp.with(EamApplication.getInstance())
                .load(imgUrl)
                .apply(bitmapTransform(new BlurTransformation(8)))
                .placeholder(R.drawable.black)
                .error(R.drawable.black)
                .skipMemoryCache(false)
                .into(imageView);
        imageView.setVisibility(View.VISIBLE);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    public static void getBlurImgFromUrl(String imgUrl, final IImageHandleListener listener)
    {
        //使用uclound获得缩略图
        imgUrl = imgUrl + "?iopcmd=thumbnail&type=1&scale=20";
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(imgUrl)
                .placeholder(R.drawable.userhead)
                .error(R.drawable.userhead)
                .skipMemoryCache(false)
                .into(new SimpleTarget<Bitmap>()
                {
                    @Override
                    public void onResourceReady(Bitmap originBitmap, Transition<? super Bitmap> glideAnimation)
                    {
                        int scaleRatio = 1;
                        int blurRadius = 8;
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originBitmap,
                                originBitmap.getWidth() / scaleRatio,
                                originBitmap.getHeight() / scaleRatio,
                                false);
                        Bitmap blurBitmap = FastBlurUtil.doBlur(scaledBitmap, blurRadius, true);
                        if (listener != null)
                            listener.onSuccess(new BitmapDrawable(EamApplication.getInstance().getResources(), blurBitmap));
                    }
                });
    }

    public static void fadeOut(final View view, long duration)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.5f, 0.0f).setDuration(duration);
        animator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                view.setVisibility(View.GONE);
                EamLogger.t(TAG).writeToDefaultFile("蒙版淡出动画结束");
            }
        });
        animator.start();
    }

    public interface IImageHandleListener
    {
        void onSuccess(Drawable drawable);

        void onError(String code, String msg);
    }
}

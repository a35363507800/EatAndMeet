package com.echoesnet.eatandmeet.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CommonImageViewAct;
import com.echoesnet.eatandmeet.activities.RelationAct;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.activities.liveplay.View.LivePlayAct1;
import com.echoesnet.eatandmeet.activities.liveplay.View.StartLiveProxyAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.DishRightMenuGroupBean;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.models.bean.LAnchorsListBean;
import com.echoesnet.eatandmeet.models.bean.LiveEnterRoomBean;
import com.echoesnet.eatandmeet.models.bean.ShareToFaceBean;
import com.echoesnet.eatandmeet.models.datamodel.ImageDisposalType;
import com.echoesnet.eatandmeet.utils.MemoryUtils.MemoryHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hyphenate.util.HanziToPinyin;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import okhttp3.Call;


/**
 * Created by wangben on 2016/4/29.
 */
public final class CommonUtils
{
    private static final String TAG = CommonUtils.class.getSimpleName();

    public static final String SEPARATOR = "!=end=!";
    /**
     * 这个是百度lbs存放餐厅数据第tableId
     */
    public static int BAIDU_GEOTABLE_ID = 142302;//测试环境
    //    public static int BAIDU_GEOTABLE_ID = 149994;//正式环境（提审也是这个）
    //搜索周边餐厅的半径为50公里
    public static final int BAIDU_GEO_RADIUS = 50000;
    public static final int BAIDU_RETURN_NUMBER = 1000;

    private CommonUtils()
    {
    }


    //弹出居中黑色半透明圆角Dialog  延迟两秒消失
    public static void showTixianDialog(Activity mAct, String title)
    {
        final Dialog dialog = new Dialog(mAct, R.style.dialogBg);
        View view = LayoutInflater.from(mAct).inflate(R.layout.dialog_tixian_alert_ok, null);
        dialog.setContentView(view);
        dialog.show();
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        }, 2000);
    }

    /**
     * Returns the available screensize, including status bar and navigation bar
     */
    public static Size getScreenSize(Activity context)
    {
        Display display = context.getWindowManager().getDefaultDisplay();
        int realWidth;
        int realHeight;

        if (Build.VERSION.SDK_INT >= 17)
        {
            DisplayMetrics realMetrics = new DisplayMetrics();
            display.getRealMetrics(realMetrics);
            realWidth = realMetrics.widthPixels;
            realHeight = realMetrics.heightPixels;
        }
        else if (Build.VERSION.SDK_INT >= 14)
        {
            try
            {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                realWidth = (Integer) mGetRawW.invoke(display);
                realHeight = (Integer) mGetRawH.invoke(display);
            } catch (RuntimeException e)
            {
                throw e;
            } catch (Exception e)
            {
                //this may not be 100% accurate, but it's all we've got
                realWidth = display.getWidth();
                realHeight = display.getHeight();
            }
        }
        else
        {
            //This should be close, as lower API devices should not have window navigation bars
            realWidth = display.getWidth();
            realHeight = display.getHeight();
        }
        return new Size(realWidth, realHeight);
    }

    public static class Size
    {
        public final int width;
        public final int height;

        public Size(int width, int height)
        {
            this.width = width;
            this.height = height;
        }
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位转成为 dp
     */
    public static int px2dp(Context context, float pxValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * dp转px
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context context, float dp)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param context
     * @param sp
     * @return
     */
    public static int sp2px(Context context, int sp)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sp,
                context.getResources().getDisplayMetrics());
    }


    /**
     * 二维码弹出层
     *
     * @param bitmap
     */
    public static void getQRCode(Activity mActivity, Bitmap bitmap, boolean showHint)
    {
        final Dialog dialog = new Dialog(mActivity, R.style.AlertDialogStyle);
        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.qr_code_pop, null);
        contentView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });
        dialog.setContentView(contentView);
        TextView tv_qr_content = (TextView) contentView.findViewById(R.id.tv_qr_content);
        SpannableStringBuilder builder = new SpannableStringBuilder(tv_qr_content.getText().toString());
        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        ForegroundColorSpan redSpan = new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.C0412));
        builder.setSpan(redSpan, 3, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_qr_content.setText(builder);
        if (!showHint)
            tv_qr_content.setVisibility(View.GONE);

        ImageView iv_qr = (ImageView) contentView.findViewById(R.id.iv_qr);
        iv_qr.setImageBitmap(bitmap);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (CommonUtils.getScreenSize(mActivity).width * 0.85f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    /**
     * 判断app是否处于前台
     *
     * @return
     */
    public static boolean isAppOnForeground(Context mActivity)
    {
        ActivityManager activityManager = (ActivityManager) mActivity.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = mActivity.getApplicationContext().getPackageName();
        /**
         * 获取Android设备中所有正在运行的App
         */
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses)
        {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 保留两位小数
     *
     * @return
     */
    public static String keep2Decimal(double inputNumber)
    {
        DecimalFormat df = new DecimalFormat("######0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(inputNumber);
    }

    /**
     * 获取设备号
     *
     * @param context
     * @return
     */
    public static String getDeviceId(@Nullable Context context)
    {
        String id = "";
        if (context == null)
            context = EamApplication.getInstance();
//        TelephonyManager mTeleManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        if (!TextUtils.isEmpty(mTeleManager.getDeviceId()))//需要特定权限
//        {
//            id = mTeleManager.getDeviceId();
//        }
//        else
        try
        {
            id = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return id;
    }


    public static String getJsonFromFile(File file)
    {
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String readline = "";
            StringBuffer sb = new StringBuffer();
            while ((readline = br.readLine()) != null)
            {
                sb.append(readline);
            }
            br.close();
            Logger.t(TAG).d("读取成功：" + sb.toString());
            return sb.toString();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "";
    }


    /**
     * Lst转数组
     *
     * @param list
     * @return
     */
    public static String[] listToStrArr(List<String> list)
    {
        String[] strArr = new String[list.size()];
        for (int i = 0; i < list.size(); i++)
        {
            strArr[i] = list.get(i);
        }
        return strArr;
    }


    /**
     * 将字符串list拼接成含有分割付的串
     *
     * @param list
     * @param separator
     * @return
     */
    public static String listToStrWishSeparator(List<String> list, String separator)
    {
        if (list == null || list.size() == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++)
        {
            sb.append(list.get(i)).append(separator);
        }
        return sb.toString().substring(0, sb.toString().length() - separator.length());
    }

    /**
     * 将使用分隔符分割的字符串转化为List
     *
     * @param sourceStr
     * @param separator
     * @return
     */
    public static List<String> strWithSeparatorToList(String sourceStr, String separator)
    {
        if (TextUtils.isEmpty(sourceStr))
            return new ArrayList<>();
        return Arrays.asList(sourceStr.split(separator));
    }

    /**
     * 将以!=end=!分割字符串转换为集合
     *
     * @param temp
     * @return
     */
    public static List<String> strToList(String temp)
    {
        if (!TextUtils.isEmpty(temp))
        {
            List<String> list = new ArrayList<>();
            String[] str = temp.split(CommonUtils.SEPARATOR);
            for (int i = 0; i < str.length; i++)
            {
                list.add(str[i]);
            }
            return list;
        }
        return new ArrayList<>();
    }

    /**
     * 打开拨号界面
     *
     * @param mContext
     * @param phoneNum
     */
    public static void makeCall(Activity mContext, String phoneNum)
    {
        Intent intentP = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNum));
        intentP.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intentP);
    }

    /**
     * 将list转换成string
     */
    public static String sceneListToString(List sceneList) throws IOException
    {
        // 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 然后将得到的字符数据装载到ObjectOutputStream
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        // writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
        objectOutputStream.writeObject(sceneList);
        // 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
        String sceneListString = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT), "UTF-8");
        // 关闭objectOutputStream
        objectOutputStream.close();
        return sceneListString;
    }

    /**
     * 将String 转换成list
     */
    public static List stringToSceneList(String sceneListString)
            throws StreamCorruptedException, IOException, ClassNotFoundException
    {
        byte[] mobileBytes = Base64.decode(sceneListString.getBytes("UTF-8"), Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        List sceneList = (List) objectInputStream.readObject();
        objectInputStream.close();
        return sceneList;
    }

    /**
     * 将View转化为bitmap
     *
     * @param addViewContent
     * @return
     */
    public static Bitmap getViewBitmap(View addViewContent)
    {
        addViewContent.setDrawingCacheEnabled(true);
        addViewContent.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        addViewContent.layout(0, 0, addViewContent.getMeasuredWidth(), addViewContent.getMeasuredHeight());
        addViewContent.buildDrawingCache();
        Bitmap cacheBitmap = addViewContent.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        return bitmap;
    }

    /**
     * 通过经纬度计算两点直接的距离
     *
     * @param start
     * @param end
     * @return
     */
    public static double getDistance(LatLng start, LatLng end)
    {
        double lat1 = (Math.PI / 180) * start.latitude;
        double lat2 = (Math.PI / 180) * end.latitude;

        double lon1 = (Math.PI / 180) * start.longitude;
        double lon2 = (Math.PI / 180) * end.longitude;

        //地球半径
        double R = 6371.393;

        //地球半径
        //double R = 6371;

        //两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;
        return d * 1000;
    }

    private static final double EARTH_RADIUS = 6378137.0;

    /**
     * 通过经纬度计算两点直接的距离
     *
     * @param lon1
     * @param lat1
     * @param lon2
     * @param lat2
     * @return 返回单位是米
     */
    public static double getDistance(double lon1, double lat1, double lon2, double lat2)
    {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);

        double radLon1 = rad(lon1);
        double radLon2 = rad(lon2);

        if (radLat1 < 0)
            radLat1 = Math.PI / 2 + Math.abs(radLat1);// south
        if (radLat1 > 0)
            radLat1 = Math.PI / 2 - Math.abs(radLat1);// north
        if (radLon1 < 0)
            radLon1 = Math.PI * 2 - Math.abs(radLon1);// west
        if (radLat2 < 0)
            radLat2 = Math.PI / 2 + Math.abs(radLat2);// south
        if (radLat2 > 0)
            radLat2 = Math.PI / 2 - Math.abs(radLat2);// north
        if (radLon2 < 0)
            radLon2 = Math.PI * 2 - Math.abs(radLon2);// west
        double x1 = EARTH_RADIUS * Math.cos(radLon1) * Math.sin(radLat1);
        double y1 = EARTH_RADIUS * Math.sin(radLon1) * Math.sin(radLat1);
        double z1 = EARTH_RADIUS * Math.cos(radLat1);

        double x2 = EARTH_RADIUS * Math.cos(radLon2) * Math.sin(radLat2);
        double y2 = EARTH_RADIUS * Math.sin(radLon2) * Math.sin(radLat2);
        double z2 = EARTH_RADIUS * Math.cos(radLat2);

        double d = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2));
        //余弦定理求夹角
        double theta = Math.acos((EARTH_RADIUS * EARTH_RADIUS + EARTH_RADIUS * EARTH_RADIUS - d * d) / (2 * EARTH_RADIUS * EARTH_RADIUS));
        double dist = theta * EARTH_RADIUS;
        return dist;
    }

    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }


    /**
     * 从图库中获得照片
     *
     * @param activity
     */
    public static void getImageFromGallery(Activity activity)
    {
        Intent getImageIntent = new Intent();
        getImageIntent.setType("image/*");
        getImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        if (getImageIntent.resolveActivity(activity.getPackageManager()) != null)
        {
            //return the call activity after get the picture.
            activity.startActivityForResult(getImageIntent, EamConstant.EAM_TAKE_PHOTO_FROM_GALLERY);
        }
    }

    /**
     * 从相机获得照片
     *
     * @param activity
     */
    public static void getImageFromCamera(Activity activity)
    {
        File storageDir = activity.getExternalCacheDir();
        File image = new File(storageDir, "TempImage.jpg");
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (openCameraIntent.resolveActivity(activity.getPackageManager()) != null)
        {
            openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
            activity.startActivityForResult(openCameraIntent, EamConstant.EAM_TAKE_PHOTO_FROM_CAMERA);
        }
    }

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    public static int getVerCode(@Nullable Context context)
    {
        if (context == null)
            context = EamApplication.getInstance();
        int verCode = -1;
        try
        {
            verCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e)
        {
            Log.e("获取版本号失败", e.getMessage());
        }
        return verCode;
    }

    /**
     * 获取版本名称
     *
     * @param context
     * @return
     */
    public static String getVerName(Context context)
    {
        String verCode = "";
        try
        {
            verCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e)
        {
            Log.e("获取版本名称失败", e.getMessage());
        }
        return verCode;
    }

    /**
     * 获取程序包名
     *
     * @param context
     * @return
     */
    public static String getPackageName(Context context)
    {
        String packageName = "";
        try
        {
            packageName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).
                    packageName;
        } catch (PackageManager.NameNotFoundException e)
        {
            Log.e("获取程序名称失败", e.getMessage());
        }
        return packageName;
    }

    // 获取AndroidManifest.xml中<meta>标签中的数据
    public static String getMetaValue(Context context, String metaKey)
    {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null)
        {
            return null;
        }
        try
        {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai)
            {
                metaData = ai.metaData;
            }
            if (null != metaData)
            {
                apiKey = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e)
        {

        }
        return apiKey;
    }

    /**
     * 获得进程名称
     */
    public static String getProcessName(Context cxt, int pid)
    {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null)
        {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps)
        {
            if (procInfo.pid == pid)
            {
                return procInfo.processName;
            }
        }
        return null;
    }

    /**
     * 创建二维码图片
     *
     * @param mContext
     * @param toConverContent 待转换的内容
     * @param qrWidthDp       生成图片的宽
     * @param qrHeightDp      生成图片的宽
     * @return
     */
    public static Bitmap createQRImage(Context mContext, String toConverContent, int qrWidthDp, int qrHeightDp)
    {
        try
        {
            qrWidthDp = CommonUtils.dp2px(mContext, qrWidthDp);
            qrHeightDp = CommonUtils.dp2px(mContext, qrHeightDp);
            //判断URL合法性
            if (TextUtils.isEmpty(toConverContent))
            {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(toConverContent, BarcodeFormat.QR_CODE,
                    qrWidthDp, qrHeightDp, hints);
            int[] pixels = new int[qrWidthDp * qrHeightDp];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < qrHeightDp; y++)
            {
                for (int x = 0; x < qrWidthDp; x++)
                {
                    if (bitMatrix.get(x, y))
                    {
                        pixels[y * qrWidthDp + x] = 0xff000000;
                    }
                    else
                    {
                        pixels[y * qrWidthDp + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(qrWidthDp, qrHeightDp, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, qrWidthDp, 0, 0, qrWidthDp, qrHeightDp);
            return bitmap;
        } catch (WriterException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 输入验证
     *
     * @param verifyType 1=密码，2=邮箱，3=电话号码，4=qq号 5:汉字姓名 6:15位身份证号 7:18位身份证号
     * @param inputStr   要验证的输入值
     * @return
     */
    public static boolean verifyInput(int verifyType, String inputStr)
    {
        String rexStr = "";
        switch (verifyType)
        {
            case 1:
                //以字母开头，长度在8~16之间，只能包含字母、数字
                rexStr = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";
                break;
            case 2:
                //邮箱
                rexStr = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
                break;
            case 3:
                //rexStr="^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";
                rexStr = "^1\\d{10}$";
                break;
            case 4:
                rexStr = "^\\d{5,15}$";
                break;
            case 5:
                rexStr = "[\\u4e00-\\u9fa5]+";
                break;
            case 6:
                rexStr = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
                break;
            case 7:
                rexStr = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|[Xx])$";
                break;
            default:
                break;
        }
        Pattern password = Pattern.compile(rexStr);
        Matcher m1 = password.matcher(inputStr);
        return m1.matches();
    }

    //弹出权限设置弹窗
    public static void openPermissionSettings(final Activity activity, String description)
    {
        new CustomAlertDialog(activity)
                .builder()
                .setTitle("设置权限")
                .setMsg(description)
                .setPositiveButton("去设置", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent setIntent = new Intent();
                        setIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        setIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        setIntent.setData(Uri.fromParts("package", getPackageName(activity), null));
                        activity.startActivityForResult(setIntent, 0); //此为设置完成后返回到获取界面
                    }
                }).setNegativeButton("不用了", null)
                .show();
    }

    /**
     * 6.0以下的系统检测权限
     *
     * @return
     */
    public static boolean cameraIsCanUse()
    {
        boolean isCanUse = true;
        Camera mCamera = null;
        try
        {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters(); //针对魅族手机
            mCamera.setParameters(mParameters);
        } catch (RuntimeException e)
        {
            Logger.t(TAG).d("相机catch" + e.getMessage());
            e.printStackTrace();
            isCanUse = false;
        }

        if (mCamera != null)
        {
            try
            {
                mCamera.release();
            } catch (Exception e)
            {
                Logger.t(TAG).d("相机catch" + e.getMessage());
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }

    public static boolean cameraIsCanUse(int i)
    {
        boolean isCanUse = true;
        Camera mCamera = null;
        try
        {
            if (Camera.getNumberOfCameras() == 1)
                i = 0;
            mCamera = Camera.open(i);
            Camera.Parameters mParameters = mCamera.getParameters(); //针对魅族手机
            mCamera.setParameters(mParameters);
        } catch (RuntimeException e)
        {
            isCanUse = false;
        }
        return isCanUse;
    }

    /**
     * 获取菜品总数量
     *
     * @param dishList
     * @return
     */
    public static int getDishCount(ArrayList<DishRightMenuGroupBean> dishList)
    {
        int dishCount = 0;
        for (int i = 0; i < dishList.size(); i++)
        {
            for (int j = 0; j < dishList.get(i).getList().size(); j++)
            {
                dishCount += dishList.get(i).getList().get(j).getDishNum();
            }
        }
        /*for (int i = 0; i < dishList.size(); i++) {
            dishCount += dishList.get(i).getDishNum();
        }*/
        return dishCount;
    }

    /**
     * 获取菜品总价格
     *
     * @param dishList
     * @return
     */
    public static double getDishPrice(ArrayList<DishRightMenuGroupBean> dishList)
    {
        double dishPrice = 0.00;
        for (int i = 0; i < dishList.size(); i++)
        {
            for (int j = 0; j < dishList.get(i).getList().size(); j++)
            {
                if (dishList.get(i).getList().get(j).getDishNum() != 0)
                {
                    dishPrice += (dishList.get(i).getList().get(j).getDishNum() * Double.parseDouble(dishList.get(i).getList().get(j).getDishPrice()));
                }
            }
        }
        BigDecimal b = new BigDecimal(dishPrice);
        BigDecimal one = new BigDecimal(1);
        Logger.t(TAG).d("dishPrice--> " + dishPrice + " , " + b.divide(one, 3, RoundingMode.HALF_UP).doubleValue()
                + " , " + b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
        return b.divide(one, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /**
     * 分享到微信,微信朋友圈,QQ,QQ空间，看脸好友，新浪微博
     *
     * @param mContext
     * @param shareKey  对应渠道的参数类：微信为 Wechat.ShareParams，微信朋友圈为：WechatMoments.ShareParams等
     * @param shareInfo shareInfo中封装着分享需要的参数
     */
    public static void shareWithApp(final Context mContext, String shareKey, ShareToFaceBean shareInfo)
    {
        //初始化ShareSDK
//        MobSDK.init(mContext);
        PlatformActionListener listener;
        Platform weChat = ShareSDK.getPlatform(Wechat.NAME);//用于微信朋友圈分享检查微信客户端
        Platform qqChat = ShareSDK.getPlatform(QQ.NAME);
        if (shareInfo.getShareListener() == null)
        {
            listener = new PlatformActionListener()
            {
                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap)
                {
                    Logger.t(TAG).d("分享成功");
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable)
                {
                    Logger.t(TAG).d("分享失败");

                }

                @Override
                public void onCancel(Platform platform, int i)
                {
                    Logger.t(TAG).d("分享取消");
                }
            };
            shareInfo.setShareListener(listener);
        }
        switch (shareKey)
        {
            case "我的动态":

                break;
            case "微信好友":
                if (weChat.isClientValid())
                {
                    Wechat.ShareParams sp = new Wechat.ShareParams();
                    sp.setShareType(shareInfo.getShareType());
                    sp.setTitle(shareInfo.getShareTitle());
                    sp.setUrl(shareInfo.getShareUrl());
                    sp.setText(shareInfo.getShareContent());
                    if (!TextUtils.isEmpty(shareInfo.getShareImgUrl()))
                        sp.setImageUrl(shareInfo.getShareImgUrl());
                    else
                        sp.setImageData(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher));
                    weChat.setPlatformActionListener(shareInfo.getShareListener());
                    weChat.share(sp);
                }
                else
                    ToastUtils.showShort("您未安装微信客户端，请先安装");
                break;
            case "QQ好友":
                Logger.t(TAG).d("ShareBean:" + shareInfo.toString());
                if (qqChat.isClientValid())
                {
                    QQ.ShareParams qq = new QQ.ShareParams();
                    qq.setTitle(shareInfo.getShareTitle());
                    qq.setTitleUrl(shareInfo.getShareUrl());
                    qq.setImageUrl(shareInfo.getShareAppImageUrl());
                    qq.setText(shareInfo.getShareContent());
                    qqChat.setPlatformActionListener(shareInfo.getShareListener());
                    qqChat.share(qq);
                }
                else
                    ToastUtils.showShort("您未安装QQ客户端，请先安装");
                break;
            case "看脸好友":
                int i = NetHelper.getNetworkStatus(mContext);
                if (i == -1)
                {
                    ToastUtils.showShort("当前无网络连接，请稍后再试！");
                    return;
                }
                String shareTitle, shareContent;
                Intent intent = new Intent(mContext, RelationAct.class);
                intent.putExtra("openSource", "share");
                intent.putExtra("shareType", shareInfo.getOpenSouse());
                intent.putExtra("sendType", shareInfo.getSendType());
                if ("liveShare".equals(shareInfo.getOpenSouse()))
                {
                    shareTitle = String.format("<html><body><b>#%s#</b>%s</body></html>", shareInfo.getRoomName(), "的直播间");
                    shareContent = String.format("<html><body>越帅越优惠, 越靓越实惠, 尽在看脸吃饭。点击进入<b>#%s#</b>%s</body></html>", shareInfo.getRoomName(), "的直播间");
                }
                else
                {
                    shareTitle = shareInfo.getShareTitle();
                    shareContent = shareInfo.getShareContent();
                }
                intent.putExtra("shareTitle", shareTitle);
                intent.putExtra("shareContent", shareContent);
                intent.putExtra("shareImageUrl", shareInfo.getShareImgUrl());
                intent.putExtra("shareUrl", shareInfo.getShareUrl());
                intent.putExtra("gameId", shareInfo.getGameId());
                intent.putExtra("roomId", shareInfo.getRoomId());
                intent.putExtra("columnId", shareInfo.getColumnId());
                intent.putExtra("activityId", shareInfo.getActivityId());
                intent.putExtra("nickName", shareInfo.getRoomName());
                intent.putExtra("messageDes", shareInfo.getMessageDes());
                intent.putExtra("roomName", shareInfo.getRoomName() + "的直播间");
                intent.putExtra("clubId", shareInfo.getClubId());
                ((Activity) mContext).startActivityForResult(intent, EamConstant.EAM_OPEN_RELATION);
                // mContext.startActivityForResult(intent, EamConstant.EAM_LIVE_SHARED_OPEN_SOURCE);
                break;
            case "微信朋友圈":
                Platform weChatMoments = ShareSDK.getPlatform(WechatMoments.NAME);
                if (weChat.isClientValid())
                {
                    WechatMoments.ShareParams shareParams = new WechatMoments.ShareParams();
                    shareParams.setShareType(shareInfo.getShareType());
                    shareParams.setTitle(shareInfo.getShareWeChatMomentsTitle());
                    shareParams.setUrl(shareInfo.getShareUrl());
                    shareParams.setText(shareInfo.getShareContent());
                    if (!TextUtils.isEmpty(shareInfo.getShareImgUrl()))
                        shareParams.setImageUrl(shareInfo.getShareImgUrl());
                    else
                        shareParams.setImageData(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher));
                    weChatMoments.setPlatformActionListener(shareInfo.getShareListener());
                    weChatMoments.share(shareParams);
                }
                else
                {
                    ToastUtils.showShort("您未安装微信客户端，请先安装");
                }
                break;
            case "QQ空间":
                if (qqChat.isClientValid())
                {
                    Platform qqzone = ShareSDK.getPlatform(QZone.NAME);
                    QZone.ShareParams qz = new QZone.ShareParams();
//                qz.setShareType(shareInfo.getShareType());
//                qz.setUrl(shareInfo.getShareUrl());
                    qz.setSite(shareInfo.getShareSite());
                    qz.setSiteUrl(shareInfo.getShareSiteUrl());
                    qz.setTitle(shareInfo.getShareTitle());
                    qz.setTitleUrl(shareInfo.getShareTitleUrl());
                    qz.setImageUrl(shareInfo.getShareAppImageUrl());
                    qz.setText(shareInfo.getShareContent());

                    qqzone.setPlatformActionListener(shareInfo.getShareListener());
                    // 执行图文分享
                    qqzone.share(qz);
                }
                else
                    ToastUtils.showShort("您未安装QQ客户端，请先安装");

                break;
            case "新浪微博":
                Platform sinaWeibo = ShareSDK.getPlatform(SinaWeibo.NAME);
                if (sinaWeibo.isClientValid())
                {
                    SinaWeibo.ShareParams sinaShareParams = new SinaWeibo.ShareParams();
                    sinaShareParams.setText(shareInfo.getShareSinaContent());
                    if (TextUtils.isEmpty(shareInfo.getShareAppImageUrl()))
                        sinaShareParams.setImageData(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher));
                    else
                        sinaShareParams.setImageUrl(shareInfo.getShareAppImageUrl());
                    if (sinaWeibo.isAuthValid())
                    {
                        sinaWeibo.removeAccount(true);
                        ShareSDK.removeCookieOnAuthorize(true);
                    }
                    // 执行图文分享
                    sinaWeibo.setPlatformActionListener(shareInfo.getShareListener());
                    sinaWeibo.share(sinaShareParams);
                }
                else
                    ToastUtils.showShort("您未安装新浪微博客户端，请先安装");
                break;
            default:
                break;
        }
    }

    /**
     * 获取是否存在NavigationBar(虚拟按键)
     *
     * @param context
     * @return
     */

    public static boolean checkDeviceHasNavigationBar(Context context)
    {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0)
        {
            hasNavigationBar = rs.getBoolean(id);
        }
        try
        {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride))
            {
                hasNavigationBar = false;
            }
            else if ("0".equals(navBarOverride))
            {
                hasNavigationBar = true;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("检查是否存在虚拟键catch：" + e.getMessage());
            return false;
        }
        return hasNavigationBar;
    }


    /**
     * 获取 虚拟按键的高度
     *
     * @param context
     * @return
     */
    public static int getBottomStatusHeight(Context context)
    {
        int totalHeight = getDpi(context);
        int contentHeight = getScreenHeight(context);
        return totalHeight - contentHeight;
    }

    //获取屏幕原始尺寸高度，包括虚拟功能键高度
    public static int getDpi(Context context)
    {
        int dpi = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try
        {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi = displayMetrics.heightPixels;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return dpi;
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context)
    {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 字符串MD5加密
     *
     * @param s
     * @return
     */
    public static String toMD5(String s)
    {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try
        {
            byte[] btInput = s.getBytes("utf-8");
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++)
            {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private static long lastClickTime;

    /**
     * 是否重复点击
     *
     * @return true 是，false 否
     */
    public static boolean isFastDoubleClick()
    {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800)
        {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    private static Map<String, Boolean> map = new HashMap<>();

    /**
     * TAG 不能重复 在这定一下命名规则 tag=类名+编号 (编号自己定，在同一个类里不重复就好)
     *
     * @param tag
     * @return
     */
    public static boolean getLock(String tag)
    {
        boolean lastClickBoolean = false;
        if (map.containsKey(tag))
            lastClickBoolean = map.get(tag);
        else
            map.put(tag, lastClickBoolean);

        return lastClickBoolean;
    }

    /**
     * TAG 不能重复 在这定一下命名规则 tag=类名+编号 (编号自己定，在同一个类里不重复就好)
     *
     * @param tag
     */
    public static void clickLock(String tag)
    {
        boolean clickLock = true;
        map.put(tag, clickLock);
    }

    /**
     * TAG 不能重复 在这定一下命名规则 tag=类名+编号 (编号自己定，在同一个类里不重复就好)
     *
     * @param tag
     */
    public static void removeClickLock(String tag)
    {
        boolean clickLock = false;
        map.put(tag, clickLock);
    }

    /**
     * 作用：用户是否同意录音权限
     *
     * @return true 同意 false 拒绝
     */
    // TODO: 2018/1/5 起航暂时采用这个方法，等出现问题后改为正规的权限处理方式 --wb
    public synchronized static boolean isVoicePermission()
    {
        try
        {
            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, 22050, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, AudioRecord.getMinBufferSize(22050, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT));
            record.startRecording();
            int recordingState = record.getRecordingState();
            if (recordingState == AudioRecord.RECORDSTATE_STOPPED)
            {
                record.release();
                return false;
            }
            record.release();
            return true;
        } catch (Exception e)
        {
            return false;
        }
    }

    /**
     * 去除字符串中的空格、回车、换行符、制表符
     *
     * @param str
     * @return
     */
    public static String removeBlank(String str)
    {
        String dest = "";
        if (str != null)
        {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 通过对比得到与宽高比最接近的尺寸（如果有相同尺寸，优先选择）
     *
     * @param surfaceWidth  需要被进行对比的原宽
     * @param surfaceHeight 需要被进行对比的原高
     * @param preSizeList   需要对比的预览尺寸列表
     * @return 得到与原宽高比例最接近的尺寸
     */
    public static Camera.Size getCloselyPreSize(int surfaceWidth, int surfaceHeight,
                                                List<Camera.Size> preSizeList)
    {

        int ReqTmpWidth;
        int ReqTmpHeight;
        // 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
        if (true)
        {
            ReqTmpWidth = surfaceHeight;
            ReqTmpHeight = surfaceWidth;
        }
        else
        {
            ReqTmpWidth = surfaceWidth;
            ReqTmpHeight = surfaceHeight;
        }
        //先查找preview中是否存在与surfaceview相同宽高的尺寸
        for (Camera.Size size : preSizeList)
        {
            if ((size.width == ReqTmpWidth) && (size.height == ReqTmpHeight))
            {
                return size;
            }
        }

        // 得到与传入的宽高比最接近的size
        float reqRatio = ((float) ReqTmpWidth) / ReqTmpHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : preSizeList)
        {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin)
            {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }
        return retSize;
    }

    /**
     * set initial letter of according user's nickname( username if no nickname)
     *
     * @param user
     */
    public static void setUserInitialLetter(EaseUser user)
    {
        final String DefaultLetter = "#";
        String letter = DefaultLetter;

        final class GetInitialLetter
        {
            String getLetter(String name)
            {
                if (TextUtils.isEmpty(name))
                {
                    return DefaultLetter;
                }
                char char0 = name.toLowerCase().charAt(0);
                if (Character.isDigit(char0))
                {
                    return DefaultLetter;
                }
                ArrayList<HanziToPinyin.Token> l = HanziToPinyin.getInstance().get(name.substring(0, 1));
                if (l != null && l.size() > 0 && l.get(0).target.length() > 0)
                {
                    HanziToPinyin.Token token = l.get(0);
                    String letter = token.target.substring(0, 1).toUpperCase();
                    char c = letter.charAt(0);
                    if (c < 'A' || c > 'Z')
                    {
                        return DefaultLetter;
                    }
/*                    else if (String.valueOf(c).equals("↑"))
                    {
                        return "↑";
                    }*/
                    return letter;
                }
                return DefaultLetter;
            }
        }
        if (user.getUserType().equals("1"))
        {
            user.setInitialLetter("");
            return;
        }
        if (!TextUtils.isEmpty(user.getNickName()))
        {
            letter = new GetInitialLetter().getLetter(user.getNickName());
            user.setInitialLetter(letter);
            return;
        }
        if (letter == DefaultLetter && !TextUtils.isEmpty(user.getuId()))
        {
            letter = new GetInitialLetter().getLetter(user.getuId());
        }
        user.setInitialLetter(letter);
    }


    //region 设置沉浸式状态栏的代码

    /**
     * 设置状态栏颜色 * * @param activity 需要设置的activity * @param color 状态栏颜色值
     */

    public static void setColor(Activity activity, int color)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 生成一个状态栏大小的矩形
            View statusView = createStatusView(activity, color);
            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(statusView);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }

    //** * 生成一个和状态栏大小相同的矩形条 * * @param activity 需要设置的activity * @param color 状态栏颜色值 * @return 状态栏矩形条 *//*
    private static View createStatusView(Activity activity, int color)
    {
        // 获得状态栏高度
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);

        // 绘制一个和状态栏一样高的矩形
        View statusView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight);
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(color);
        return statusView;
    }


    /**
     * 使状态栏透明 * <p> * 适用于图片作为背景的界面,此时需要图片填充到状态栏 * * @param activity 需要设置的activity
     *//*
    public static void setTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }*/
    //endregion


    /**
     * 得到设备屏幕的宽度
     */
    public static int getScreenWidth(Context context)
    {
        if (context == null)
            context = EamApplication.getInstance();
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 得到设备屏幕的高度
     */
    public static int getScreenHeight1(Context context)
    {
        if (context == null)
            context = EamApplication.getInstance();
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static void setTransparentTopBar(Activity act)
    {
        act.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
/*        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = act.getWindow();
*//*            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);*//*


            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Window window = act.getWindow();
            //透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    //设置成白色的背景，字体颜色为黑色。
    public static boolean setMiuiStatusBarDarkMode(Activity activity, boolean darkmode)
    {
        try
        {
            Class<? extends Window> clazz = activity.getWindow().getClass();
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置成白色的背景，字体颜色为黑色。
     *
     * @param activity
     * @param isSystemUi isSystemUi=true:字黑   isSystemUi=false:字白
     */
    public static void setStatusBarDarkMode(Activity activity, boolean isSystemUi)
    {
//        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi"))
        setMiuiStatusBarDarkMode(activity, isSystemUi);
        //设置成白色的背景，字体颜色为黑色。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (isSystemUi)
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            else
            {
                activity.getWindow().getDecorView().setSystemUiVisibility(0);
            }
        }
    }

    public static int getStatusBarHeight(Activity act)
    {
        Rect rectangle = new Rect();
        Window window = act.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop =
                window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentViewTop - statusBarHeight;
        Logger.t(TAG).d(String.format("statusBarHeight :%s  contentViewTop :%s titleBarHeight: %s", statusBarHeight, contentViewTop, titleBarHeight));
        return titleBarHeight;
    }

    /**
     * 缓存图片到指定文件夹 并设置image
     *
     * @param imgView
     * @param imgUrl
     * @param path
     */
    public static void setImageFromFile(final ImageView imgView, final String imgUrl, final String path)
    {
        final String titleMd5Name = CommonUtils.toMD5(imgUrl);
        File giftFile = new File(path, titleMd5Name);
        if (!giftFile.exists())
        {
            Logger.t(TAG).d("不存在" + titleMd5Name);
            OkHttpUtils.get()
                    .url(imgUrl)
                    .build()
                    .execute(new FileCallBack(path, titleMd5Name + ".temp")
                    {
                        @Override
                        public void inProgress(float progress, long total)
                        {

                        }

                        @Override
                        public void onError(Call call, Exception e)
                        {
                            Logger.t(TAG).d("title" + imgUrl + "down fail" + e.getMessage());
                        }

                        @Override
                        public void onResponse(File response)
                        {
                            Logger.t(TAG).d(imgUrl + "下载成功");
                            response.renameTo(new File(path, titleMd5Name));
                            setImageFromFile(imgView, imgUrl, path);
                        }
                    });
        }
        else
        {
            if (imgView != null)
            {
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(giftFile)
                        .centerCrop()
                        .into(imgView);
            }
        }
    }

    public static String getHostName(String urlString)
    {
        String head = "";
        int index = urlString.indexOf("://");
        if (index != -1)
        {
            head = urlString.substring(0, index + 3);
            urlString = urlString.substring(index + 3);
        }
        index = urlString.indexOf("/");
        if (index != -1)
        {
            urlString = urlString.substring(0, index + 1);
        }
        return head + urlString;
    }

    /**
     * 跳转 查看大图
     *
     * @param context
     * @param urls            图片url集合
     * @param currentPosition 跳转目标position
     * @param view            控件点击的view
     */
    public static void showImageBrowser(Context context, List<String> urls, int currentPosition, View view)
    {

        Intent intent = new Intent(context, CommonImageViewAct.class);
        intent.putStringArrayListExtra(EamConstant.EAM_SHOW_IMG_URLS, (ArrayList<String>) urls);
        intent.putExtra(EamConstant.EAM_SHOW_IMG_CURRENT_ITEM, currentPosition);
        int[] location = new int[2];
        int width = 50;
        int height = 50;
        if (view == null)
        {
            location[0] = CommonUtils.getScreenWidth(context) / 2;
            location[1] = CommonUtils.getScreenHeight1(context) / 2;
        }
        else
        {
            view.getLocationOnScreen(location);
            width = view.getWidth();
            height = view.getHeight();
        }
        intent.putExtra(EamConstant.EAM_SHOW_IMG_LOCATION_X, location[0]);
        intent.putExtra(EamConstant.EAM_SHOW_IMG_LOCATION_Y, location[1]);
        intent.putExtra(EamConstant.EAM_SHOW_IMG_WIDTH, width);
        intent.putExtra(EamConstant.EAM_SHOW_IMG_HEIGHT, height);
        context.startActivity(intent);
    }


    /**
     * 跳转 查看大图 （查看的是用户头像）
     *
     * @param context
     * @param urls            图片url集合
     * @param currentPosition 跳转目标position
     * @param view            控件点击的view
     * @param isHeadPic       查看的是用户头像
     */
    public static void showImageBrowser(Context context, List<String> urls, int currentPosition, View view, boolean isHeadPic)
    {

        Intent intent = new Intent(context, CommonImageViewAct.class);
        intent.putStringArrayListExtra(EamConstant.EAM_SHOW_IMG_URLS, (ArrayList<String>) urls);
        intent.putExtra(EamConstant.EAM_SHOW_IMG_CURRENT_ITEM, currentPosition);
        int[] location = new int[2];
        int width = 50;
        int height = 50;
        if (view == null)
        {
            location[0] = CommonUtils.getScreenWidth(context) / 2;
            location[1] = CommonUtils.getScreenHeight1(context) / 2;
        }
        else
        {
            view.getLocationOnScreen(location);
            width = view.getWidth();
            height = view.getHeight();
        }
        intent.putExtra(EamConstant.EAM_SHOW_IMG_LOCATION_X, location[0]);
        intent.putExtra(EamConstant.EAM_SHOW_IMG_LOCATION_Y, location[1]);
        intent.putExtra(EamConstant.EAM_SHOW_IMG_WIDTH, width);
        intent.putExtra(EamConstant.EAM_SHOW_IMG_HEIGHT, height);

        intent.putExtra(EamConstant.EAM_SHOW_IMG_HEAD_PIC, isHeadPic);
        context.startActivity(intent);
    }

    /**
     * 播放视频
     *
     * @param activity
     * @param url      缩略图
     * @param view     控件点击的view
     */
    public static void playVideo(Activity activity, Intent intent, String url, View view)
    {
        intent.putExtra(EamConstant.EAM_SHOW_IMG_URLS, url);
        int[] location = new int[2];
        int width = 50;
        int height = 50;
        if (view == null)
        {
            location[0] = CommonUtils.getScreenWidth(activity) / 2;
            location[1] = CommonUtils.getScreenHeight1(activity) / 2;
        }
        else
        {
            view.getLocationOnScreen(location);
            width = view.getWidth();
            height = view.getHeight();
        }
        intent.putExtra(EamConstant.EAM_SHOW_IMG_LOCATION_X, location[0]);
        intent.putExtra(EamConstant.EAM_SHOW_IMG_LOCATION_Y, location[1]);
        intent.putExtra(EamConstant.EAM_SHOW_IMG_WIDTH, width);
        intent.putExtra(EamConstant.EAM_SHOW_IMG_HEIGHT, height);
        activity.startActivityForResult(intent, EamConstant.EAM_OPEN_TRENDS_PLAY_VIDEO);
        activity.overridePendingTransition(0, 0);
    }

    private static final int inSampleSize = 2;

    /**
     * url缩略图 拼接
     * https://docs.ucloud.cn/storage_cdn/ufile/pic  : UCloud 图片处理文档中心   不完善，有待优化  ----yqh
     *
     * @param imageUrl 图片地址
     * @param iopcmd   处理类型 {@link ImageDisposalType} 当前仅支持 THUMBNAIL
     * @param type     具体处理类型 {@link ImageDisposalType:}
     *                 THUMBNAIL: 当前仅支持 8||13(类似centerCrop) & 7(限定短边，长边自适应缩放);
     *                 CROP:
     *                 ROTATE:
     *                 WATERMARK:
     * @param width    设计图中宽度
     * @param height   设计图中高度
     * @return 拼接好的url
     */
    public static String getThumbnailImageUrlByUCloud(String imageUrl, ImageDisposalType iopcmd, int type, int width, int height)
    {
        if (!imageUrl.contains("http://huisheng.ufile.ucloud"))
            return imageUrl;
        Logger.t(TAG).d("--------imageUrl：" + imageUrl + " | width：" + width + " | height：" + height);

        switch (iopcmd)
        {
            case THUMBNAIL:// 缩放
                imageUrl = imageUrl + "?iopcmd=thumbnail&type=" + type;
                switch (type)
                {
                    case 1:
                        imageUrl = imageUrl + "&scale=" + width;
                        break;
                    case 7:
                        imageUrl = imageUrl + "&width=" + width * inSampleSize + "&height=" + height * inSampleSize;
                        break;
                    case 8:
                    case 13:
                        imageUrl = imageUrl + "&width=" + width * inSampleSize;
                        break;
                }
                break;
            case CROP:     // 裁剪
                imageUrl = imageUrl + "?iopcmd=crop&";
                break;
            case ROTATE:   // 旋转
                imageUrl = imageUrl + "?iopcmd=rotate&";
                break;
            case WATERMARK:// 水印
                imageUrl = imageUrl + "?iopcmd=watermark&";
                break;
        }
        Logger.t(TAG).d("width----->>>>>" + imageUrl);
        return imageUrl;
    }

    /**
     * @param imageUrl
     * @param iopcmd
     * @param type     1 等比缩放，最通用的处理方式
     * @param scale
     * @return
     */
    public static String getThumbnailImageUrlByUCloud(String imageUrl, ImageDisposalType iopcmd, int type, int scale)
    {
        return getThumbnailImageUrlByUCloud(imageUrl, iopcmd, type, scale, 0);
    }

    /**
     * 启动开启直播代理act
     *
     * @param mActivity
     * @param roomMode
     * @param roomName
     * @param vedioName ucloud 录播文件名
     * @param flyPage
     * @param strRoomid
     * @param flags
     * @param reqCode
     */
    public static void startLiveProxyAct(final Activity mActivity, int roomMode, String roomName, String vedioName, String flyPage, final String strRoomid, Integer flags, int reqCode)
    {
        Intent intent = new Intent(mActivity, StartLiveProxyAct.class);
        intent.putExtra("roomMode", roomMode);
        intent.putExtra("roomid", strRoomid);
        intent.putExtra("roomName", roomName);
        intent.putExtra("vedioName", vedioName);
        intent.putExtra("flyPage", flyPage);
        intent.putExtra("flags", flags);
        intent.putExtra("reqCode", reqCode);
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
    }

    /**
     * 兼容老代码
     *
     * @param startingActivity
     * @param roomMode
     * @param roomName
     * @param vedioName
     * @param flyPage
     * @param strRoomId
     * @param flags
     */
    public static void startLivePlay(final Activity startingActivity, final int roomMode, final String roomName,
                                     final String vedioName, final String flyPage, final String strRoomId, final Integer flags)
    {
        CommonUtils.speakSwitch = "unable";
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(startingActivity);
        reqParamMap.put(ConstCodeTable.roomId, strRoomId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult result)
            {
                super.onNext(result);
                Logger.t(TAG).d("startlive>>>>>" + result.getBody());
                LiveEnterRoomBean eh = new Gson().fromJson(result.getBody(), LiveEnterRoomBean.class);
                Logger.t(TAG).d("ranking >>" + eh.getRanking() + ",star >>" + eh.getStar());
                Intent intent = new Intent(startingActivity, LivePlayAct1.class);
//                if ("2".equals(eh.getLiveSource()))
//                {
//                    intent = new Intent(startingActivity, LiveRoomAct2.class);
//                }
                if (null != flags)
                {
                    intent.setFlags(flags);
                }
                //else
                // intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("roomMode", roomMode);
                intent.putExtra("roomid", strRoomId);
                intent.putExtra("roomName", roomName);
                intent.putExtra("vedioName", vedioName);
                intent.putExtra("flyPage", flyPage);
                final LAnchorsListBean bean = new LAnchorsListBean();
                bean.setRoomId(strRoomId);
                intent.putExtra("lAnchorsList", new ArrayList<LAnchorsListBean>()
                {
                    {
                        add(bean);
                    }
                });
                intent.putExtra(LiveRecord.KEY_ENTERROOM_EH, eh);
                startingActivity.finish();
                startingActivity.startActivity(intent);
                startingActivity.overridePendingTransition(com.echoesnet.eatandmeet.R.anim.fade_in_short, com.echoesnet.eatandmeet.R.anim.fade_out_short);
            }
        }, NetInterfaceConstant.LiveC_enterRoom, "1", reqParamMap);
    }

    public static void startLive(final Activity startingActivity, int roomMode, String roomName, String sign, String flyPage, final String strRoomid, Integer flags, int reqCode)
    {
        CommonUtils.isInLiveRoom = true;
        CommonUtils.speakSwitch = "unable";
        final Intent intent = new Intent(startingActivity, LivePlayAct1.class);
        if (null != flags)
        {
            intent.setFlags(flags);
        }
        //else
        // intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra("roomMode", roomMode);
        intent.putExtra("roomid", strRoomid);
        intent.putExtra("roomName", roomName);
        intent.putExtra("sign", sign);
        intent.putExtra("flyPage", flyPage);
        final LAnchorsListBean bean = new LAnchorsListBean();
        bean.setRoomId(strRoomid);
        intent.putExtra("lAnchorsList", new ArrayList<LAnchorsListBean>()
        {
            {
                add(bean);
            }
        });

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(startingActivity);
        reqParamMap.put(ConstCodeTable.roomId, strRoomid);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult result)
            {
                super.onNext(result);
                Logger.t(TAG).d("startlive>>>>>>>>>>>>>>>>>>>>>>>>" + result.getBody());
                LiveEnterRoomBean eh = new Gson().fromJson(result.getBody(), LiveEnterRoomBean.class);
                intent.putExtra(LiveRecord.KEY_ENTERROOM_EH, eh);
                startingActivity.finish();
                startingActivity.startActivity(intent);
                startingActivity.overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
            }
        }, NetInterfaceConstant.LiveC_enterRoom, "1", reqParamMap);
    }

    /**
     * 协助直播跳转
     */
    public static String jumpHelperId = "-1";
    /**
     * 是否在直播间内，  控制开直播进入聊天发送 语音等功能
     */
    public static boolean isInLiveRoom = false;
    /**
     * 是否在聊天室(CChatActivity)内
     */
    public static boolean isInChatRoom = false;

    public static String speakSwitch = "able";

    public static boolean isSwitched2Back = false;

    public static boolean isAppKilled = false;

    /**
     * 记录阅读量
     *
     * @param commonOperateListener 返回监听  readNum 阅读数
     */
    public static void serverIncreaseRead(Context context, final String tId, final ICommonOperateListener commonOperateListener)
    {
        Map<String, String> param = NetHelper.getCommonPartOfParam(context);
        param.put(ConstCodeTable.tId, tId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                try
                {
                    Logger.t(TAG).d("增加阅读数的UID:" + tId + ";返回:" + response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (commonOperateListener != null)
                        commonOperateListener.onSuccess(jsonObject.getString("readNum"));
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }

        }, NetInterfaceConstant.TrendC_addReadNum, param);
    }

    /**
     * 获取栈顶Activity
     *
     * @return 栈顶Activity
     */
    public static Activity getTopActivity()
    {
        if (MemoryHelper.sTopActivityWeakRef != null)
        {
            Activity activity = MemoryHelper.sTopActivityWeakRef.get();
            if (activity != null)
            {
                return activity;
            }
        }
        List<Activity> activities = MemoryHelper.sActivityList;
        int size = activities.size();
        return size > 0 ? activities.get(size - 1) : null;
    }

    public static boolean getDayFirst(Activity mActivity, String type)
    {
        Date d = new Date();
        System.out.println(d);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);

        //产品需每天第一次进入APP弹出每日签到 七日不需要弹出
        String dayCount;
        switch (type)
        {
            case "sign":
                dayCount = SharePreUtils.getDayCount(mActivity);
                break;
            case "task":
                dayCount = SharePreUtils.getTaskShowCount(mActivity);
                break;
            case "achieve":
                dayCount = SharePreUtils.getAchieveShowCount(mActivity);
                break;
            default:
                dayCount = SharePreUtils.getDayCount(mActivity);
                break;
        }
        try
        {
            if (TextUtils.isEmpty(dayCount))
            {
                JSONObject jb = new JSONObject();
                jb.put("day", dateNowStr);
                jb.put("count", 1);
                switch (type)
                {
                    case "sign":
                        SharePreUtils.setDayCount(mActivity, jb.toString());
                        break;
                    case "task":
                        SharePreUtils.setTaskShowCount(mActivity, jb.toString());
                        break;
                    case "achieve":
                        SharePreUtils.setAchieveShowCount(mActivity, jb.toString());
                        break;
                    default:

                        break;
                }
                return true;
            }
            else
            {

                JSONObject jb = new JSONObject(dayCount);
                String day = jb.getString("day");
                int count = jb.getInt("count");

                if (!dateNowStr.equals(day))
                    count = 0;

                jb.put("day", dateNowStr);
                jb.put("count", count + 1);
                switch (type)
                {
                    case "sign":
                        SharePreUtils.setDayCount(mActivity, jb.toString());
                        break;
                    case "task":
                        SharePreUtils.setTaskShowCount(mActivity, jb.toString());
                        break;
                    case "achieve":
                        SharePreUtils.setAchieveShowCount(mActivity, jb.toString());
                        break;
                    default:
                        break;
                }

                if (count == 0)
                {
                    return true;
                }
            }

        } catch (JSONException e)
        {

            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取图片MimeType
     *
     * @param filePath
     * @return
     */
    public static String getImageMimeType(String filePath)
    {
        String mime = "jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        String type = options.outMimeType;
        mime = type.substring(6, type.length());
        return mime;
    }


    /**
     * 获取安装 App(支持 8.0)的意图
     * <p>8.0 需添加权限
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     *
     * @param file      文件
     * @param authority 7.0 及以上安装需要传入清单文件中的{@code <provider>}的 authorities 属性
     *                  <br>参看 https://developer.android.com/reference/android/support/v4/content/FileProvider.html
     * @param isNewTask 是否开启新的任务栈
     * @return 安装 App(支持 8.0)的意图
     */
    public static Intent getInstallAppIntent(final File file,
                                             final String authority,
                                             final boolean isNewTask)
    {
        if (file == null) return null;
        Intent intent = new Intent(Intent.ACTION_VIEW);//I3CKBE  LHUW8P
        Uri data;
        String type = "application/vnd.android.package-archive";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
        {
            data = Uri.fromFile(file);
        }
        else
        {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            data = FileProvider.getUriForFile(EamApplication.getInstance(), authority, file);
        }
        intent.setDataAndType(data, type);
        return getIntent(intent, isNewTask);
    }

    private static Intent getIntent(final Intent intent, final boolean isNewTask)
    {
        return isNewTask ? intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) : intent;
    }

    /**
     * 检查敏感字
     *
     * @param matchWords
     * @return
     */
    public static Map<String, Object> checkSensitiveWord(String matchWords, boolean needReplace)
    {
        Map<String, Object> map = new HashMap<>();
        boolean isContain = false;
        String newWords = "";
        for (String s : EamApplication.getInstance().sensitiveWordsList)
        {
            if (matchWords.contains(s))
            {
                isContain = true;
                if (needReplace)
                {
                    StringBuilder replaceWords = new StringBuilder();
                    for (int i = 0; i < s.length(); i++)
                    {
                        replaceWords.append("*");
                    }
                    newWords = matchWords.replace(s, replaceWords.toString());
                }
                break;
            }
        }
        map.put("isContains", isContain);
        if (needReplace)
            map.put("newWords", newWords);
        return map;
    }

}
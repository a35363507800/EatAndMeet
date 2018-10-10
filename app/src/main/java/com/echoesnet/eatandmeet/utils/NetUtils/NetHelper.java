package com.echoesnet.eatandmeet.utils.NetUtils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;

import com.echoesnet.eatandmeet.activities.LoginAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IGetSecurityCodeListener;
import com.echoesnet.eatandmeet.presenters.viewinterface.IGetVoiceCodeListener;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.Constants;
import com.echoesnet.eatandmeet.utils.FileUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.ZipUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import okhttp3.Call;
import okhttp3.MediaType;


public class NetHelper
{
    public final static String TAG = NetHelper.class.getSimpleName();

    /**
     * 公网
     */
    public static String SERVER_SITE = "http://106.75.50.97:8844";
    /**
     * 心跳服务器
     */
    public static String SERVER_SITE_HEART = "http://106.75.61.141:8800";

    /**
     * 检查服务器是否可用
     */
    public static String SERVER_SITE_CHECK_SERVER = "http://app.echoesnet.com:8388";

    //正式
    public static String TX_USER_SIGN =
            "eJxlj01vgkAURff8iglbm3aAoWB3iB8gRao2TemGTGGwT8uIMFi06X-Xokkn6duek3vv*1YQQurz4" +
                    "-KWpum24SIRh5Kp6AGpWL35g2UJWUJFYlTZP8jaEiqW0FywqoOaad3b*PckCzLGBeRwdZysAB7xT" +
                    "*BMkupsk3RdlxxyjtBMYmiyAqsOhqPY9efubn9YbY5eGkQvxnEkioYF83zhrflXNH7V6bAHMW93eEknjv-hhLbbToKaxv3BOsDe9O2uqfMB9ML3yNZr0Z*W0fCJjP1Z4UuVAgp2HWRZmGCT6BLds6qGLe8E-TxX043L78qPcgK-CF-v";
    //测试
    //public final static String TX_USER_SIGN="eJxlj8FOg0AURfd8BWGrscwAMpq4oO3EENtSWzHEDSHMQF
    // *BKR2GBmr8d5U2kcS3PSf33vep6bpuvC22d0maHlqhYtXX3NAfdcM0bv9gXQOLExVbkv2DvKtB8jjJFJcDRI57T8zfG1nAuFCQwdXxWAUiECUIPpIaVsRD1yXH-olArkXGOQ3kA1zScOa-ztZRzY4PxWbrbVC0Z9Fknc1pttvfqOKUt2VIdiv7vEzxS*X5OeXtmdUfAZF08W6XfSWS6FkG1J*H2IfJqsv6Ywd0mk69p1GlgopfB7mEOBhhd0RPXDZwEIOATeQgbF1*1760b*joYMU_";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String MD5_KYE = "comecho519";
    public static final String EAM_APP_KEY = "100148SHY3FH4PPA";


    public static String LIVE_SHARE_ADDRESS = "http://shangjia.echoesnet.com/activity/liveshare/index.html?roomId=";
    public static final String LIVE_SHARE_PIC = "http://huisheng.ufile.ucloud.com.cn/share_pic_v3.png";

    /**
     * H5 address
     */
    public static String H5_ADDRESS = "http://106.75.50.97";
    /**
     * 代付分享
     */
    public static String SHARE_ORDERDS_ADDRESS = "http://shangjia.echoesnet.com";

    /**
     * 邀请好友地址
     */
    public static String SHARE_INVITE_FIRENDS_ADDRESS = "http://yqhy.echoesnet.com";

    /**
     * 轰趴餐馆分享地址
     */
    public static String SHARE_HP_ADDRESS = "http://106.75.50.97/activity/o2o/index.html?hpId=";

    /**
     * 上传身份证照片地址
     */
    public static String UPLOAD_IDCARD_PIC = "http://106.75.50.97:8866/upload/realName";


    public static final String DATA_FOLDER = "data/";
    public static final String CAMERA_FOLDER = "camera/";
    public static final String EMOJI_FOLDER = "Emojs/";
    public static final String GIFT_FOLDER = "gift/";
    public static final String LOG_FOLDER = "log/";
    public static final String CRASH_FOLDER = "crash/";
    public static final String TITLE_FOLDER = "title/";
    public static final String ANCHOR_TYPE_FOLDER = "AnchorType/";
    //    public static final String DOWNLOAD_IMAGE_FOLDER = "DownLoadImages/";
    public static final String DOWNLOAD_IMAGE_FOLDER = "EAMImages/";
    public static final String CUT_VIDEO = "CutVideo/";//剪切视频输出目录
    public static final String CUT_VIDEO_THUMBNAIL = "CutVideo/Thumbnail/";//剪切视频缩略图缓存

    //获得根目录
    public static String getRootDirPath(@Nullable Context context)
    {
        if (context == null)
            context = EamApplication.getInstance();
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
            return Environment.getExternalStorageDirectory().getPath() + File.separator +
                    "EatAndMeet/";
        else
            return context.getFilesDir().getPath() + File.separator + "EatAndMeet/";
    }

    /**
     * 产生向后台的请求参数包
     *
     * @param reqInterface 接口
     * @param reqJsonBody  参数
     * @return
     */
    public static String getRequestJsonStr(String reqInterface, String reqJsonBody)
    {
        String reqStr = String.format("{\"head\":{\"reqName\":\"%s\"},\"body\":%s}",
                reqInterface, reqJsonBody);
        return reqStr;
    }

    public static String getRequestStrToTx(String reqInterface, String param)
    {
        if (TextUtils.isEmpty(param))
            param = "usersig=" + TX_USER_SIGN + "&identifier=AdminOnline&sdkappid=" + Constants
                    .SDK_APPID + "&contenttype=json";
        return "https://console.tim.qq.com/" + reqInterface + "?" + param;
    }

    /**
     * 调用后台，发送短信验证码
     *
     * @param mobile
     */
    public static void getSecurityCodeMsg(final Context mContext, final String mobile, String
            type, String tokenId, final IGetSecurityCodeListener listener)
    {
        Map<String, String> reqParamMap = new HashMap<>();
        reqParamMap.put(ConstCodeTable.mobile, mobile);
        if (TextUtils.isEmpty(tokenId))
        {
            tokenId = SharePreUtils.getToken(mContext);
        }
        reqParamMap.put(ConstCodeTable.token, tokenId);
        reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(mContext));
        reqParamMap.put(ConstCodeTable.type, type);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (listener != null)
                    listener.onSuccess();
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                if ("USR_EXITS".equals(apiE.getErrorCode()))//如果手机号被注册过了，就弹窗提示
                {
                    new CustomAlertDialog(mContext)
                            .builder()
                            .setTitle("提示")
                            .setMsg("您的手机号已注册，您是否要登录？")
                            .setPositiveButton("去登录", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Intent lIntent = new Intent(mContext, LoginAct.class);
                                    lIntent.putExtra("inputMobile", mobile);
                                    mContext.startActivity(lIntent);
                                    ((Activity) mContext).finish();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
                else
                {
                    if (!"5".equals(type))
                    {
                        super.onHandledError(apiE);
                    }
                    listener.onFailed(apiE.getErrorCode());
                }


                Logger.t(TAG).d("错误码为：%s", apiE.getErrorCode());
            }
        }, NetInterfaceConstant.UserC_sendCodes, reqParamMap);

    }

    /**
     * 获取语音验证码
     *
     * @param mobile
     * @param type    1注册 2 订餐更换手机号  3忘记登录密码 4忘记支付密码 5 微信验证
     * @param tokenId
     */
    public static void getVoiceCodeMsg(final Context mContext, final String mobile, String type,
                                       String tokenId, final IGetVoiceCodeListener listener)
    {
        Map<String, String> reqParamMap = new ArrayMap<>();
        reqParamMap.put(ConstCodeTable.mobile, mobile);
        reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(mContext));
        if (TextUtils.isEmpty(tokenId))
        {
            tokenId = SharePreUtils.getToken(mContext);
        }
        reqParamMap.put(ConstCodeTable.token, tokenId);
        reqParamMap.put(ConstCodeTable.type, type);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (listener != null)
                    listener.onSuccess();
                ToastUtils.showLong("验证码已发出，请注意接听电话哟~");
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
//                super.onHandledError(apiE);
                String code = apiE.getErrorCode();
                if (listener != null)
                    listener.onFailed(code);
                //如果手机号被注册过了，就弹窗提示
                if (!"USR_EXITS".equals(code))
                    super.onHandledError(apiE);
                Logger.t(TAG).d("错误码为：%s", code);
            }
        }, NetInterfaceConstant.UserC_voiceCode, reqParamMap);

    }


    //region 保留方法
    public static void verifyIdCard(String realname, String idcard, final Context context)
    {
        String mall_id = "110555";
        String appkey = "e82e099d906d92dc2081dd0ee4797fef";
        String url = "http://121.41.42.121:8080/v2/id-server?";
        idcard = idcard.toLowerCase();
        long tm = System.currentTimeMillis();

        String md5_param = mall_id + realname + idcard + tm + appkey;
        String sign = md5(md5_param);
        String param = new StringBuffer()
                .append("mall_id=" + mall_id)
                .append("&realname=" + realname)
                .append("&idcard=" + idcard)
                .append("&tm=" + tm)
                .append("&sign=" + sign)
                .toString();
        OkHttpUtils
                .get()
                .url(url + param)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        NetHelper.handleNetError(context, null, TAG, e);
                    }

                    @Override
                    public void onResponse(String response)
                    {
                        Logger.t("身份证").d(response);
                        try
                        {
                            JSONObject result = new JSONObject(response);
                            int status = Integer.parseInt(result.getString("status"));
                            int code = Integer.parseInt(result.getJSONObject("data").getString
                                    ("code"));
                            String message = result.getJSONObject("data").getString("message");

                            /*客户可以根据自己的业务需求进行处理*/
                            if (status == 2001)
                            {
                                //2001=正常服务
                                if (code == 1000)
                                {
                                    //一致
                                }
                                else if (code == 1001)
                                {
                                    //不一致
                                }
                                else if (code == 1002)
                                {
                                    //库中无此号
                                }
                                //如果命令相应正常，一下情况不需要处理
                                else if (code == 1101)
                                {
                                    //商家ID不合法
                                }
                                else if (code == 1102)
                                {
                                    //身份证姓名不合法
                                }
                                else if (code == 1103)
                                {
                                    //身份证号码不合法
                                }
                                else if (code == 1104)
                                {
                                    //签名不合法

                                }
                                else if (code == 1107)
                                {
                                    //tm不合法
                                }
                            }
                            //正常情况下不需要处理，商家也可以根据自己的业务进行处理
                            else if (status == 2002)
                            {
                                //2002=第三方服务器异常

                            }
                            else if (status == 2003)
                            {
                                //2003=服务器维护

                            }
                            else if (status == 2004)
                            {
                                //2004=账号余额不足

                            }
                            else if (status == 2005)
                            {
                                //2005=参数异常

                            }
                            //1000=一致
                            //1001=不一致
                            //1002=库中无此号
                            //1101=商家ID不合法
                            //1102=身份证姓名不合法
                            //1103=身份证号码不合法
                            //1104=签名不合法
                            //1105=第三方服务器异常
                            //1106=账户余额不足
                            //1107=tm不合法
                            //1108=其他异常
                            //1109=账号被暂停
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                });
/*        String url_v = url + param;
        String jsonString = url2string(url_v);
        JSONObject result = JSONObject.parseObject(jsonString);*/
    }

    private static String md5(String s)
    {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
                'E', 'F'};
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

    private static String url2string(String url)
    {
        StringBuffer sb = new StringBuffer();
        try
        {
            InputStream is = new URL(url).openStream();
            byte[] buf = new byte[1024 * 10];
            int len = 0;
            while ((len = is.read(buf, 0, 1024 * 10)) > 0)
            {
                sb.append(new String(buf, 0, len));
            }
            is.close();
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }
    //endregion


    // 获取LBS餐厅geotableid
    public final static String GETLBS_GEOTABLEID = "http://api.map.baidu" +
            ".com/geodata/v3/geotable/list?ak=ejfaxdXUK1H43KUMLp2IX4ZDAI4cgGoT";
    // 获取LBS餐厅geotableidlist
    public final static String GETLBS_GEOTABLELIST = "http://api.map.baidu" +
            ".com/geodata/v3/poi/list?ak=ejfaxdXUK1H43KUMLp2IX4ZDAI4cgGoT&geotable_id=";
    // 获取LBS餐厅区域
    public final static String GETLBS_AREALIST = "http://api.map.baidu" +
            ".com/geosearch/v3/local?ak=ejfaxdXUK1H43KUMLp2IX4ZDAI4cgGoT&geotable_id=";
    // 根据给定位置餐厅获取周边餐厅
    public final static String GET_AROUND_RESID = "http://api.map.baidu" +
            ".com/geosearch/v3/nearby?ak=ejfaxdXUK1H43KUMLp2IX4ZDAI4cgGoT&radius=";

    /**
     * 根据给定的poi点的id来获得poi点的详情
     */
    public final static String getPoiDetail(String poiId)
    {
        return "http://api.map.baidu.com/geosearch/v3/detail/" + poiId +
                "?ak=ejfaxdXUK1H43KUMLp2IX4ZDAI4cgGoT";
    }

    /**
     * 获取网络状态
     *
     * @param context
     * @return 返回值 -1：没有网络  1：WIFI网络2：wap网络3：net网络
     */
    public static int getNetworkStatus(Context context)
    {
        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null)
        {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE)
        {
            if (!TextUtils.isEmpty(networkInfo.getExtraInfo()))
            {
                if ("cmnet".equals(networkInfo.getExtraInfo().toLowerCase()))
                {
                    netType = 3;
                }
                else
                {
                    netType = 2;
                }
            }
        }
        else if (nType == ConnectivityManager.TYPE_WIFI)
        {
            netType = 1;
        }
        return netType;
    }

    /**
     * 直播分享计次
     */
    public static void addLiveShareCount(Context context)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(context);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("直播分享计次返回结果：" + response);
            }
        }, NetInterfaceConstant.LiveC_share, reqParamMap);
    }

    /**
     * 请求失败显示Toast
     *
     * @param context
     */
    public static void handleNetError(@Nullable Context context, String description, String
            exceptSource, Throwable e)
    {
        Logger.t(exceptSource).d(String.format("异常来源：%s；错误信息为%s", exceptSource, "netError:" + e
                == null ? "" : e.getMessage()));
        if (TextUtils.isEmpty(description))
        {
            ToastUtils.showShort("你的网络好像不给力，重新试一下！");
        }
    }

    public static Map<String, String> getCommonPartOfParam(@Nullable Context context)
    {
        if (context == null)
            context = EamApplication.getInstance();
        Map<String, String> reqParamMap = new HashMap<>();
        reqParamMap.put(ConstCodeTable.token, SharePreUtils.getToken(context));
        reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(context));
        reqParamMap.put(ConstCodeTable.uId, SharePreUtils.getUId(context));
        return reqParamMap;
    }

    public static Map<String, Object> getH5CommonMap(@Nullable Context context)
    {
        if (context == null)
            context = EamApplication.getInstance();
        Map<String, Object> reqParamMap = new ArrayMap<>();
        reqParamMap.put("token", SharePreUtils.getToken(context));
        reqParamMap.put("uid", SharePreUtils.getUId(context));
        reqParamMap.put("deviceId", CommonUtils.getDeviceId(context));
        reqParamMap.put("abc", NetHelper.MD5_KYE);
        return reqParamMap;
    }

    public static void checkPrivilegeToLevel(final Context context, String id,
                                             final ICommonOperateListener callback)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(context);
        reqParamMap.put(ConstCodeTable.pId, id);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                try
                {
                    JSONObject object = new JSONObject(response);
                    String level = object.getString("level");
                    if (callback != null)
                        callback.onSuccess(level);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d("error:" + e.getMessage());
                }
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                try
                {
                    JSONObject object = new JSONObject(apiE.getErrBody());
                    String level = object.getString("level");
                    if (callback != null)
                        callback.onError(level, ErrorCodeTable.parseErrorCode(apiE.getErrorCode()));
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d("error:" + e.getMessage());
                }
            }
        }, NetInterfaceConstant.LiveC_privilegeToLevel, reqParamMap);

    }

    /**
     * 检查 大礼物
     *
     * @param mContext
     */
    public static void checkBigGiftExist(final Context mContext)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                try
                {
                    JSONObject body = new JSONObject(response.getBody());
                    int giftVersion = Integer.parseInt(body.getString("version"));
                    Logger.t(TAG).d(giftVersion + "," + SharePreUtils.getGiftVersion(mContext));
                    String bigGiftPath = NetHelper.getRootDirPath(mContext) + NetHelper
                            .GIFT_FOLDER + "bigGift";
                    File bigGift = new File(bigGiftPath);
                    //返回值 -1：没有网络  1：WIFI网络2：wap网络3：net网络
                    final int netState = NetHelper.getNetworkStatus(mContext);
                    if (netState != 1)//不是WiFi网络不下载
                        return;
                    if (!bigGift.exists() || giftVersion > SharePreUtils.getGiftVersion(mContext))
                    {
                        downLoadBigGift(mContext, NetInterfaceConstant.FILE_BIG_GIFT_RESOURCE,
                                giftVersion);
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, NetInterfaceConstant.LiveC_giftVersion, null, reqParamMap);
    }

    private static void downLoadBigGift(final Context mContext, String url, final int giftVersion)
    {
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new FileCallBack(NetHelper.getRootDirPath(mContext) + NetHelper
                        .GIFT_FOLDER, "bigGift.zip")
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        Logger.t(TAG).d("下载失败");
                    }

                    @Override
                    public void onResponse(final File responseFile)
                    {
                        new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    ZipUtils.unZipWithoutSuffix(responseFile, new File(NetHelper
                                            .getRootDirPath(mContext) + GIFT_FOLDER), new
                                            ICommonOperateListener()
                                            {
                                                @Override
                                                public void onSuccess(String response)
                                                {
                                                    SharePreUtils.setGiftVersion(mContext, giftVersion);
                                                    FileUtils.deleteFile(responseFile.getPath());
                                                }

                                                @Override
                                                public void onError(String code, String msg)
                                                {
                                                    //解压失败再解压失败
                                                    try
                                                    {
                                                        ZipUtils.unZipWithoutSuffix(responseFile, new
                                                                File(NetHelper.getRootDirPath(mContext) +
                                                                "gift"), new ICommonOperateListener()
                                                        {
                                                            @Override
                                                            public void onSuccess(String response)
                                                            {
                                                                SharePreUtils.setGiftVersion(mContext,
                                                                        giftVersion);
                                                                FileUtils.deleteFile(responseFile.getPath
                                                                        ());
                                                            }

                                                            @Override
                                                            public void onError(String code, String msg)
                                                            {
                                                                //二次解压失败不再解压！
                                                            }
                                                        });
                                                    } catch (IOException e)
                                                    {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void inProgress(float progress, long total)
                    {

                    }
                });
    }


    /**
     * 向后台发起post请求,此函数废弃
     * 请使用{@link com.echoesnet.eatandmeet.http4retrofit2.HttpMethods#startServerRequest(Observer, String, Map)}
     *
     * @param businessName 中间件接口名称
     * @param syncFlag     请求方式 1：同步 0 异步
     * @param messageJson  请求参数
     * @param callback     结果回调
     * @throws IllegalArgumentException "syncFlag 参数必须为1 或者0 或者 null"
     */
    @Deprecated
    public static void postRequest(String businessName, @Nullable String syncFlag, String
            messageJson, Callback callback)
    {
        try
        {
          /*   OkHttpUtils.post()
                    .url(NetHelper.SERVER_SITE_UPDATE)
                    .addParams("businessName", businessName)
                    .addParams("syncFlag", syncFlag)
                    .addParams("appKey", NetHelper.EAM_APP_KEY)
                    .addParams("md5", MD5Util.MD5(messageJson + NetHelper.MD5_KYE))
                    .addParams("messageJson", messageJson)
                    .build()
                    .execute(callback);*/
            OkHttpUtils.postString()
                    .url(NetHelper.SERVER_SITE)
                    .content(messageJson)
                    .build()
                    .execute(callback);

        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(String.format("异常来至于接口》%s 异常信息为》%s", businessName, e.getMessage()));
        }
    }


//    /**
//     * 以中间件的形式向后台发起post请求
//     *
//     * @param businessName 中间件接口名称
//     * @param syncFlag     请求方式 1：同步 0 异步
//     * @param messageJson  请求参数
//     * @param extraParams  额外的表单参数
//     * @param callback     结果回调
//     */
//   /* @Deprecated
//    public static void postRequest(String businessName, String syncFlag, String messageJson,
//                                   Map<String, String> extraParams, Callback callback)
//    {
//        try
//        {
//            if (TextUtils.isEmpty(syncFlag))
//                syncFlag = "1";
//            PostFormBuilder pFormBuilder = OkHttpUtils.post()
//                    .url(NetHelper.SERVER_SITE_UPDATE)
//                    .addParams("businessName", businessName)
//                    .addParams("syncFlag", syncFlag)
//                    .addParams("appKey", NetHelper.EAM_APP_KEY)
//                    .addParams("md5", MD5Util.MD5(messageJson + NetHelper.MD5_KYE))
//                    .addParams("messageJson", messageJson);
//            if (extraParams != null)
//            {
//                for (Map.Entry<String, String> entry : extraParams.entrySet())
//                {
//                    pFormBuilder.addParams(entry.getKey(), entry.getValue());
//                }
//            }
//            pFormBuilder
//                    .build()
//                    .execute(callback);
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//            Logger.t(TAG).d(String.format("异常来至于接口》%s 异常信息为》%s", businessName, e.getMessage()));
//        }
//    }*/

    public static void obtainWifiInfo(Context context, ICommonOperateListener listener)
    {
        try
        {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null)
            {
                WifiInfo info = wifiManager.getConnectionInfo();
                // 链接信号强度，5为获取的信号强度值在5以内
                int strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);
                // 链接速度
                int speed = info.getLinkSpeed();
                // 链接速度单位
                String units = WifiInfo.LINK_SPEED_UNITS;
                // Wifi源名称
                String ssid = info.getSSID();
                Logger.t(TAG).d("WiFi：" + ssid + "信号强度：" + strength + "速度：" + speed + " " + units);
            }
        } catch (Exception e)
        {
            Logger.t(TAG).d("获取网络状况失败》" + e.getMessage());
        }

    }

    /**
     * 查询是否显示过新手引导
     *
     * @param context
     * @param type                  新手引导查询类型 "1":"签到，动态", "2":"发布动态，点赞评论", "3":"选桌",
     *                              "4":"表情", "5":"视频，图片", "6":"打招呼", "7":"关注动态", "8":"订位",
     *                              "9":"收藏", "10":"直播", "11":"约会管家", "12":"打招呼关注"
     * @param commonOperateListener 返回监听  0显示新手引导 1不显示
     */
    public static void checkIsShowNewbie(Context context, final String type, final ICommonOperateListener commonOperateListener)
    {
        Map<String, String> param = getCommonPartOfParam(context);
        param.put(ConstCodeTable.type, type);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                try
                {
                    Logger.t(TAG).d("新手引导查询:" + type + ";返回:" + response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (commonOperateListener != null)
                        commonOperateListener.onSuccess(jsonObject.getString("newbie"));
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                if (commonOperateListener != null)
                    commonOperateListener.onError(apiE.getErrorCode(), apiE.getErrBody());
            }
        }, NetInterfaceConstant.NewbieC_guide, param);
    }


    /**
     * 保存新手引导状态标记为显示过
     *
     * @param context
     * @param type
     */
    public static void saveShowNewbieStatus(Context context, String type)
    {
        Map<String, String> param = getCommonPartOfParam(context);
        param.put(ConstCodeTable.type, type);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
            }
        }, NetInterfaceConstant.NewbieC_iknow, param);
    }


    /**
     * 活动分享要给的奖励
     *
     * @param context
     * @param type    0. 圣诞活动直播分享 1. 圣诞活动大V文章分享
     */
    public static void activityShare(Context context, String type)
    {
        Map<String, String> param = getCommonPartOfParam(context);
        param.put(ConstCodeTable.type, type);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
            }
        }, NetInterfaceConstant.ActivityC_share, param);
    }

}

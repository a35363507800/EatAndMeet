package com.echoesnet.eatandmeet.controllers;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.util.ArrayMap;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.request.target.ViewTarget;
import com.echoesnet.eam.icontextmodule.IconUtil;
import com.echoesnet.eatandmeet.BuildConfig;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.Constants;
import com.echoesnet.eatandmeet.utils.CrashHandler;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.echoesnet.eatandmeet.utils.IMUtils.TXInitBusinessHelper;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.echoesnet.eatandmeet.utils.MemoryUtils.MemoryHelper;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.mob.MobSDK;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;
import com.zhy.http.okhttp.https.HttpsUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


//import com.squareup.leakcanary.RefWatcher;

/**
 * Created by wangben on 2016/4/12.
 * 由于使用了分包，所以在5.0系统以下会出问题，需要继承 MultiDexApplication
 */
public class EamApplication extends MultiDexApplication
{
    private static final String TAG = EamApplication.class.getSimpleName();

    //note: 此处的全局变量只在当前进程下有效，如若跨进程将不再有效，请在开发过程中注意
    //      如果涉及到在不同线程中设置全局变量,请使用volatile关键字，以便于设置的值可以及时被其他线程的使用者获知-wb

    // FIXME: 2017/3/17 刘洋；faceEgg 作为全局变量；目前直播返回后，刷新脸蛋数使用；
    public String uInfoFaceEgg = "0";
    public String wxLoginFlag = "0";
    // FIXME: 2017/8/3 凌子元；badger各项数量作为全局变量；目前在对应页面直接修改即可；
    //聊天的未读消息数量
    public String msgCount = "0";
    //打招呼的未读消息数量
    public String helloCount = "0";
    //未读互动通知数量
    public String interactionCount = "0";
    //关注动态更新数量
    public String dynamicCount = "0";
    //平台通知数量
    public String notificationCount = "0";
    //标记hxCmd消息是否检查有人领取红包
    public boolean isCheckRed = false;


    // 约会订单提交需要的streamId,由于传值问题，需要全局变量协助  以后需要改 这样不行
    public String dateStreamId = "";

    private static EamApplication instance;
    /**
     * 用来协助流程跳转的List
     */
    public List<Activity> actStake = new ArrayList<>();

    /**
     * 控制聊天页
     */
    public Map<String, Activity> controlChat = new ArrayMap<>();
    /**
     * 控制个人详情页
     */
    public Map<String, Activity> controlUInfo = new ArrayMap<>();

    public List<String> sensitiveWordsList = new ArrayList<>();

    /**
     * 用来传递餐厅的最低起订价
     */
    public String lessPrice = "0";
    /**
     * 存放餐厅的经纬度,由于历史原因，此处第一个存储的好像是纬度，第二是经度。待确定
     * 中国天津的纬度大约在：40左右，经度在：117左右，可根据具体的值来确定顺序
     */
    public String[] geoPosition = new String[2];
    public JSONObject materialObj = null;

    /**
     * 0 用户  1主播
     */
    public int liveIdentity = Constants.MEMBER;
    /**
     * 是否显示我的约会
     */
    public String isShowReceive = "0";

    /**
     * 开直播是否调用 startLive
     */
    public boolean hasCallServerStartLived = false;

    /**
     * 用来存放直播间列表的直播间封面
     */
    public HashMap<String, String> livePage = new HashMap<>();
    private OkHttpClient okHttpClient;
    private Gson gson;
    //private ExecutorService executor;

    public volatile int getChannelResult=0;// 是否已经从后台获得需要特殊处理渠道的信息，0:初始状态；1：需要特殊处理 2：不需要特殊处理
    @Override
    public void onCreate()
    {
        super.onCreate();
        //内存泄漏检测
        if (LeakCanary.isInAnalyzerProcess(this))
        {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process
            return;
        }
//        LeakCanary.install(this);
        instance = this;
        switchAppEnvironment(BuildConfig.evnIndex);


        String processName = CommonUtils.getProcessName(this, android.os.Process.myPid());
        if (processName != null)
        {
            boolean defaultProcess = processName.equals(BuildConfig.APPLICATION_ID);
            if (defaultProcess)
            {
                com.orhanobut.logger.LogLevel logLevel = com.orhanobut.logger.LogLevel.NONE;
                com.echoesnet.eatandmeet.utils.Log.LogLevel eamLogLevel = com.echoesnet.eatandmeet.utils.Log.LogLevel.FULL;
                if (BuildConfig.enableDebugLog)
                {
                    logLevel = LogLevel.FULL;
                    eamLogLevel = com.echoesnet.eatandmeet.utils.Log.LogLevel.FULL;
                }

                Logger.init("EatAndMeet")
                        .logLevel(logLevel);
                EamLogger.init("EatAndMeet")
                        .logLevel(eamLogLevel);

                MobSDK.init(getApplicationContext());

                IconUtil.getInstance().init();

                //Glide设置
                ViewTarget.setTagId(R.id.glide_tag);
                configOkHttp(BuildConfig.enableDebugLog);

                //检查是否有虚拟键
/*                if (CommonUtils.checkDeviceHasNavigationBar(getApplicationContext()))
                {
                    Logger.t("EamApp").d("有虚拟栏");
                    //默认使用的高度是设备的可用高度，也就是不包括状态栏和底部的操作栏的，如果你希望拿设备的物理高度进行百分比化
                    AutoLayoutConifg.getInstance().useDeviceSize();
                }*/
                TXInitBusinessHelper.initApp(getApplicationContext());//腾讯直播初始化
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                {
                    initHuanXin();
                    configBaiduMap();
                }
                MemoryHelper mMemoryBoss = MemoryHelper.getInstance();
                registerActivityLifecycleCallbacks(mMemoryBoss);
                registerComponentCallbacks(mMemoryBoss);

                //崩溃捕捉
                CrashHandler.getInstance().init(getApplicationContext());
                //VmPolicy
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                }
                configBaiduTongJi();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static EamApplication getInstance()
    {
        return instance;
    }

    /**
     * OkHttp初始化及配置
     */
    private void configOkHttp(boolean isDebugModel)
    {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        long readTimeout = 10L;
        long conTimeout = 10L;
        if (isDebugModel)
        {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            readTimeout = 10L;
        }
        else
        {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        //ClearableCookieJar cookieJar1 = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getApplicationContext()));
        CookieJarImpl cookieJar1 = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(conTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .addInterceptor(logging)
                // .cookieJar(cookieJar1)
                .sslSocketFactory(HttpsUtils.getSslSocketFactory(null, null, null))//设置可访问的网站
                .build();
        OkHttpUtils.initClient(okHttpClient);
        //LoggerInterceptor
    }

    public OkHttpClient getOkHttpClient()
    {
        return okHttpClient;
    }

/*    public ExecutorService getExecutor()
    {
        if (executor == null)
            executor = Executors.newCachedThreadPool();
        return executor;
    }*/

    public Gson getGsonInstance()
    {
        if (gson == null)
            gson = new Gson();
        return gson;
    }

    private void initHuanXin()
    {
        //初始化环信
        HuanXinIMHelper.getInstance().init(getApplicationContext());
    }




    public int getBadgerCount()
    {
        int count = 0;
        try
        {
            count += Integer.parseInt(this.msgCount);
            count += Integer.parseInt(this.helloCount);
            count += Integer.parseInt(this.interactionCount);
            count += Integer.parseInt(this.dynamicCount);
            count += Integer.parseInt(this.notificationCount);
        } catch (NumberFormatException e)
        {
            Logger.t("application").d("角标数量转换有错误");
        }
        if (count > 99)
            count = 99;
        Logger.t("==============").d("红点监听" + count + "/" + msgCount + "/" + helloCount + "/" + interactionCount + "/" + dynamicCount + "/" + notificationCount + "/");
        return count;
    }

    public void notifiBadgerCount()
    {
        ShortcutBadger.applyCount(this, getBadgerCount());
    }

    private void configBaiduMap()
    {
        SDKInitializer.initialize(getApplicationContext());
    }

    private void configBaiduTongJi()
    {
        // 调试百度统计SDK的Log开关，可以在IDE中看到sdk打印的日志，发布时去除调用，或者设置为false
        StatService.setDebugOn(BuildConfig.enableDebugLog);
    }

    /**
     * 切换app的执行环境
     *
     * @param envType 1：公网正式环境 2：公网测试环境 3：内侧环境 4.昕琰ip、测试直播接口
     */
    private void switchAppEnvironment(int envType)
    {
        switch (envType)
        {
            case 1:
                CommonUtils.BAIDU_GEOTABLE_ID = 149994;
                NetHelper.SERVER_SITE = "http://app.echoesnet.com:8844";
                NetHelper.SERVER_SITE_HEART = "http://app.echoesnet.com:8800";
                NetHelper.SERVER_SITE_CHECK_SERVER = "http://app.echoesnet.com:8388";
                NetHelper.TX_USER_SIGN = "eJxlj01vgkAURff8iglbm3aAoWB3iB8gRao2TemGTGGwT8uIMFi06X-Xokkn6duek3vv*1YQQurz4-KWpum24SIRh5Kp6AGpWL35g2UJWUJFYlTZP8jaEiqW0FywqoOaad3b*PckCzLGBeRwdZysAB7xT*BMkupsk3RdlxxyjtBMYmiyAqsOhqPY9efubn9YbY5eGkQvxnEkioYF83zhrflXNH7V6bAHMW93eEknjv-hhLbbToKaxv3BOsDe9O2uqfMB9ML3yNZr0Z*W0fCJjP1Z4UuVAgp2HWRZmGCT6BLds6qGLe8E-TxX043L78qPcgK-CF-v";
                NetHelper.LIVE_SHARE_ADDRESS = "http://shangjia.echoesnet.com/activity/liveshare/index.html?roomId=";
                NetHelper.UPLOAD_IDCARD_PIC = "http://app.echoesnet.com:8866/upload/realName";
                CdnHelper.fileFolder = "online/";
                NetInterfaceConstant.FILE_GIFT_VERSION = CdnHelper.CDN_ORIGINAL_SITE + "giftInfo.json";
                Constants.SDK_APPID = 1400015431;
                Constants.ACCOUNT_TYPE = 7753;
                NetHelper.H5_ADDRESS = "http://shangjia.echoesnet.com";
                NetHelper.SHARE_ORDERDS_ADDRESS = "http://shangjia.echoesnet.com";
                NetHelper.SHARE_HP_ADDRESS = "http://shangjia.echoesnet.com/activity/o2o/index.html?hpId=";
                break;
            case 2:
                CommonUtils.BAIDU_GEOTABLE_ID = 142302;
                NetHelper.SERVER_SITE = "http://106.75.50.97:8844";
                NetHelper.SERVER_SITE_HEART = "http://106.75.61.141:8800";
                NetHelper.SERVER_SITE_CHECK_SERVER = "http://106.75.77.3:8388";
                NetHelper.UPLOAD_IDCARD_PIC = "http://106.75.50.97:8866/upload/realName";
                NetHelper.TX_USER_SIGN = "eJxlj8FOg0AURfd8BWGrscwAMpq4oO3EENtSWzHEDSHMQF*BKR2GBmr8d5U2kcS3PSf33vep6bpuvC22d0maHlqhYtXX3NAfdcM0bv9gXQOLExVbkv2DvKtB8jjJFJcDRI57T8zfG1nAuFCQwdXxWAUiECUIPpIaVsRD1yXH-olArkXGOQ3kA1zScOa-ztZRzY4PxWbrbVC0Z9Fknc1pttvfqOKUt2VIdiv7vEzxS*X5OeXtmdUfAZF08W6XfSWS6FkG1J*H2IfJqsv6Ywd0mk69p1GlgopfB7mEOBhhd0RPXDZwEIOATeQgbF1*1760b*joYMU_";
                NetHelper.LIVE_SHARE_ADDRESS = "http://active.echoesnet.com/activity/liveshare/index.html?roomId=";
                CdnHelper.fileFolder = "test/";
                NetInterfaceConstant.FILE_GIFT_VERSION = CdnHelper.CDN_ORIGINAL_SITE + CdnHelper.fileFolder + "giftInfo.json";
                Constants.ACCOUNT_TYPE = 8208;
                Constants.SDK_APPID = 1400017380;
                NetHelper.H5_ADDRESS = "http://106.75.50.97";//测试地址/
                NetHelper.SHARE_HP_ADDRESS = "http://106.75.50.97/activity/o2o/index.html?hpId=";
//              NetHelper.H5_ADDRESS = "http://192.168.10.247";//测试地址/ song ge
//              NetHelper.H5_ADDRESS = "http://192.168.10.249:8080";//liu min
//              NetHelper.H5_ADDRESS = "http://192.168.10.248:8080";//zhang jun zhuo
//              NetHelper.H5_ADDRESS = "http://192.168.10.246:8080";// xiao peng
//              NetHelper.H5_ADDRESS = "http://192.168.10.???:8080";// xiao li

                NetHelper.SHARE_ORDERDS_ADDRESS = "http://active.echoesnet.com";
                break;
            case 3:
                CommonUtils.BAIDU_GEOTABLE_ID = 142302;
                NetHelper.SERVER_SITE = "http://106.75.47.115:8833";
                CdnHelper.fileFolder = "neice/";
                break;
            case 4:
                CommonUtils.BAIDU_GEOTABLE_ID = 142302;
                NetHelper.SERVER_SITE = "http://192.168.10.15:8844";
                CdnHelper.fileFolder = "test/";
                break;
            case 5:
                CommonUtils.BAIDU_GEOTABLE_ID = 142302;
                NetHelper.SERVER_SITE = "http://106.75.65.32:8080/queue";
                CdnHelper.fileFolder = "test/";
                break;
            default:
                break;
        }
    }
}

package com.echoesnet.eatandmeet.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.baidu.mapapi.SDKInitializer;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.FileUtils;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.echoesnet.eatandmeet.utils.IMUtils.IMHelper;
import com.echoesnet.eatandmeet.utils.IMUtils.TencentHelper;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;


public class WelcomeAct extends BaseActivity
{
    private final static String TAG = WelcomeAct.class.getSimpleName();

    private final int SPLASH_DISPLAY_MOST_LENGTH = 0;//实际时视情况缩小
    private Activity mAct;
    private Runnable runnabler;
    private Handler handler = new Handler();

    private volatile boolean conServerFinish = false;//表示后台服务器反馈情况，true表示后台服务响应了

    private String tokenLoginResult;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAct = this;
        hideNavigationBar();
        setContentView(R.layout.act_launch);
        startApp();
    }

    private void showBackground()
    {
        try
        {
            final String launchImg = NetHelper.getRootDirPath(getApplicationContext()) + NetHelper.DOWNLOAD_IMAGE_FOLDER + "app_lanch_bg.jpg";
            File file = new File(launchImg);
            if (file.exists())
                getWindow().setBackgroundDrawable(Drawable.createFromPath(launchImg));
            else
            {
                // getWindow().setFormat(PixelFormat.RGBA_8888);
                getWindow().setBackgroundDrawableResource(R.drawable.default_start);
            }
        } catch (Exception e)
        {
            getWindow().setBackgroundDrawableResource(R.drawable.default_start);
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    public void onBackPressed()
    {
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (runnabler != null && handler != null)
        {
            handler.removeCallbacks(runnabler);
        }
        conServerFinish = true;
        IMHelper.getInstance().removeLoginFinishListener();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
        {
            hideNavigationBar();
        }
    }

    private void hideNavigationBar()
    {
        final View decorView = getWindow().getDecorView();
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION   //上下bar还在，但显示的内容拓展到被bar盖住的区域了
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN            //上下bar还在，显示的内容拓展到上面bar的位置
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION          //navigation_bar消失
                | View.SYSTEM_UI_FLAG_FULLSCREEN               //bar存在，内容消失 显示一个点
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;        //向内滑动以操作状态栏
        decorView.setSystemUiVisibility(flags);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
        {
            @Override
            public void onSystemUiVisibilityChange(int visibility)
            {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                {
                    decorView.setSystemUiVisibility(flags);
                }
            }
        });
    }


    /**
     * 带有版本判断的启动App
     */
    private void startApp()
    {
        getChannelInfo(mAct);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(mAct, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(mAct, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)
            {
                getWindow().setBackgroundDrawableResource(R.drawable.default_start);
                ActivityCompat.requestPermissions(mAct,
                        new String[]{Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
            }
            else
            {
                //初始化环信
                HuanXinIMHelper.getInstance().init(getApplicationContext());
                SDKInitializer.initialize(getApplicationContext());
                checkServerIsCanUse();
            }
        }
        else
        {
            checkServerIsCanUse();
        }
    }

    /**
     * 具体启动app
     */
    private void start()
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Logger.t(TAG).d("当前版本：" + CommonUtils.getVerCode(mAct)
                        + "上一个版本" + SharePreUtils.getVersionCode(mAct));
                if (SharePreUtils.getVersionCode(mAct) == 0)//第一次，或者卸载后安装(其实就是Share被删除了)
                {
                    try
                    {
                        new Thread(() ->
                        {
                            deleteEmoji();//表情图片更改为 无后缀名 防止现有用户崩溃，所以删除emoji
                            deleteEamApk();
                        }).start();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    SharePreUtils.setVersionCode(mAct, CommonUtils.getVerCode(mAct));
                    StringBuilder resultBuilder = FileUtils.readFile(NetHelper.getRootDirPath(mAct) + NetHelper.DATA_FOLDER + "appInnerFile.json", "UTF-8");
                    if (resultBuilder != null)
                    {
                        Map<String, String> map = new Gson().fromJson(resultBuilder.toString(), new TypeToken<HashMap<String, String>>()
                        {
                        }.getType());
                        SharePreUtils.setToken(mAct, map.get(ConstCodeTable.token));
                        SharePreUtils.setUId(mAct, map.get(ConstCodeTable.uId));
                        Logger.t(TAG).d("文件内容为》 " + resultBuilder.toString());
                        tokenLogin();
                    }
                    else
                    {
                        Logger.t(TAG).d("读取文件返回null");
                        startActivity(new Intent(mAct, LoginModeAct.class));
                        WelcomeAct.this.finish();
                    }
                }
                else
                {
                    tokenLogin();
                }
            }
        }, SPLASH_DISPLAY_MOST_LENGTH);
    }

    /**
     * 删除表情
     */
    private void deleteEmoji()
    {
        try
        {
            String rPath = NetHelper.getRootDirPath(mAct) + NetHelper.EMOJI_FOLDER;
            String path1 = rPath + "xiaomei_1/cover.png";
            String path2 = rPath + "xiaomei_2/cover.png";
            File file1 = new File(path1);
            File file2 = new File(path2);
            if (file1.exists() || file2.exists())
                FileUtils.deleteFile(rPath);
        } catch (Exception e)
        {
            Logger.t(TAG).d("删除表情出错");
            e.printStackTrace();
        }
    }

    private void deleteEamApk()
    {
        File file = new File(NetHelper.getRootDirPath(mAct) + "EatAndMeet.apk");
        if (file.exists())
            file.delete();
    }

    //权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        Logger.t(TAG).d("权限回调 " + requestCode);
        switch (requestCode)
        {
            case 3:
                boolean isGranted = true;
                for (int grantResult : grantResults)
                {
                    if (grantResult != PackageManager.PERMISSION_GRANTED)
                    {
                        isGranted = false;
                        break;
                    }
                }
                if (isGranted)
                {
                    //初始化环信
                    HuanXinIMHelper.getInstance().init(getApplicationContext());
                    SDKInitializer.initialize(getApplicationContext());
                    checkServerIsCanUse();
                }
                else
                {
                    new CustomAlertDialog(WelcomeAct.this)
                            .builder()
                            .setMsg("应用没有获取必须的权限无法使用,请到设置页面打开相应的权限")
                            .setCancelable(false)
                            .setPositiveButton("确认", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    finish();
                                }
                            }).show();
//                    ActivityCompat.requestPermissions(mAct,
//                            new String[]{Manifest.permission.READ_PHONE_STATE,
//                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                }
                break;
        }
    }

    /**
     * 检查后台服务器是否可用--wb
     */
    private void checkServerIsCanUse()
    {
        showBackground();
        if (NetHelper.getNetworkStatus(mAct) == -1)
        {
            if (!TextUtils.isEmpty(SharePreUtils.getToken(mAct)))
            {
                startActivity(new Intent(mAct, HomeAct.class));
            }
            else
            {
                ToastUtils.showLong("当前无网络连接，请检测您的网络环境后重启进入");
                startActivity(new Intent(mAct, LoginModeAct.class));
            }
            if (EamApplication.getInstance().getChannelResult != 0)//防止内存泄漏
                finish();
            return;
        }
        Observable.interval(3500, 2000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Long>()
                {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception
                    {
                        Logger.t(TAG).d("计时器1 >" + aLong);
                        if (conServerFinish == false)
                            ToastUtils.showLong(String.format("系统检测到您网络环境很差，第 %s 次尝试重连!", aLong + 1));
                    }
                }).takeUntil(new Predicate<Long>()
        {
            @Override
            public boolean test(@NonNull Long aLong) throws Exception
            {
                Logger.t(TAG).d("计时器2 >" + aLong);
                return aLong == 2;
            }
        }).doOnComplete(new Action()
        {
            @Override
            public void run() throws Exception
            {
                Logger.t(TAG).d("计时器完结");
            }
        }).compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();

        Map<String, String> reqParamMap = new HashMap<>();
        reqParamMap.put(ConstCodeTable.deviceType, "android");
        reqParamMap.put(ConstCodeTable.versionCode, CommonUtils.getVerCode(mAct) + "");
        reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(mAct));
        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.TransferC_RequestTransfer, new Gson().toJson(reqParamMap));
        Logger.t(TAG).d("请求：" + paramJson);

        OkHttpUtils.postString()
                .url(NetHelper.SERVER_SITE_CHECK_SERVER)
                .content(paramJson)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        Logger.t(TAG).d("检查服务器返回：" + e.getMessage());
                        new CustomAlertDialog(WelcomeAct.this)
                                .builder()
                                .setMsg("服务器繁忙，确认重试！")
                                .setCancelable(false)
                                .setPositiveButton("确认", new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        start();
                                    }
                                }).show();
                    }

                    @Override
                    public void onResponse(String response)
                    {
                        Logger.t(TAG).d("检查服务器返回--> " + response.toString());
                        try
                        {
                            JSONObject obj = new JSONObject(response);
                            if ("0".equals(obj.getString("status")))
                            {
                                start();
                            }
                            else
                            {
                                String body = obj.getString("body");
                                new CustomAlertDialog(WelcomeAct.this)
                                        .builder()
                                        .setMsg(body)
                                        .setCancelable(false)
                                        .setPositiveButton("确认", new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                finish();
                                            }
                                        }).show();
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * token登录
     */
    private void tokenLogin()
    {
        if (runnabler == null)
        {
            this.runnabler = () -> {
                startActivity(new Intent(mAct, LoginModeAct.class));
                finish();
            };
        }
        if (TextUtils.isEmpty(SharePreUtils.getUId(mAct)) || TextUtils.isEmpty(SharePreUtils.getToken(mAct)))
        {
            conServerFinish = true;
            handler.postDelayed(runnabler, 1000);
        }
        else
        {
            Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
            HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
            {
                @Override
                public void onNext(String response)
                {
                    super.onNext(response);
                    conServerFinish = true;
                    new Thread(() -> {
                        int timeOut = 0;
                        while (EamApplication.getInstance().getChannelResult == 0 && timeOut < 1600)//等待结果返回或者超时
                        {
                            SystemClock.sleep(200);
                            timeOut += 200;
                        }
                        mAct.runOnUiThread(() -> {
                            handleTokenLogin(response);
                        });
                    }).start();
                }

                @Override
                public void onHandledNetError(Throwable throwable)
                {
                    super.onHandledNetError(throwable);
                    if (!"".equals(SharePreUtils.getToken(mAct)))
                    {
                        startActivity(new Intent(mAct, HomeAct.class));
                    }
                }

                @Override
                public void onError(Throwable e)
                {
                    super.onError(e);
                    conServerFinish = true;
                    Logger.t(TAG).d("错误码为：%s", e == null ? "null" : e.getMessage());
                    finish();
                }

                @Override
                public void onHandledError(ApiException apiE)
                {
                    String code = apiE.getErrorCode();
                    Logger.t(TAG).d("错误码为：%s", code);
                    if ("USR_INFO_INCOM".equals(code))//如果信息不完善则要求去完善资料
                    {
                        startActivity(new Intent(mAct, MakeUserInfo.class));
                    }
                    else if ("USR_NOT_EXITS".equals(code) || "USR_NOT_REGISTER".equals(code))
                    {
                        startActivity(new Intent(mAct, LoginModeAct.class));
                    }
                    mAct.finish();
                }
            }, NetInterfaceConstant.UserC_tokenSignIn, reqParamMap);
        }
    }

    /**
     * 环信登录
     */
    private void huanXinLogin()
    {
        if (IMHelper.getInstance().isLoggedIn())
        {
            new Thread(() -> {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
            }).start();
        }
        else
        {
            Logger.t(TAG).d("环信重新登录");
            IMHelper.getInstance().setOnILoginFinishListener(new IMHelper.IHxLoginFinishedListener()
            {
                @Override
                public void onSuccess()
                {
                    WelcomeAct.this.finish();
                }

                @Override
                public void onFailed(int i, final String s)
                {
                    mAct.runOnUiThread(() -> ToastUtils.showShort("H登录失败,请重试！"));
                    Logger.t(TAG).d("登录失败，错误信息：i:" + i + ",s:" + s);
                    WelcomeAct.this.finish();
                }
            });
            IMHelper.getInstance().huanXinLogin(mAct);
        }
    }

    private void getChannelInfo(Context context)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                try
                {
                    JSONObject obj = new JSONObject(response);
                    String market = CommonUtils.getMetaValue(context, "BaiduMobAd_CHANNEL");
                    List<String> indicators = Arrays.asList(obj.optString("market", "huawei").split(CommonUtils.SEPARATOR));
                    EamApplication.getInstance().getChannelResult = indicators.contains(market) ? 1 : 2;
                } catch (Exception e)
                {
                    e.printStackTrace();
                    EamApplication.getInstance().getChannelResult = 2;
                }
            }

            @Override
            public void onError(Throwable e)
            {
                EamApplication.getInstance().getChannelResult = 2;
            }
        }, NetInterfaceConstant.USERC_GET_CHANNEL_INFO, reqParamMap);
    }

    private void handleTokenLogin(String response)
    {
        try
        {
            JSONObject object = new JSONObject(response);
            final String tlsName = object.getString("name");
            final String tlsSign = object.getString("sign");
            SharePreUtils.setTlsName(mAct, tlsName);//腾讯用户名和签名
            SharePreUtils.setTlsSign(mAct, tlsSign);
            SharePreUtils.setId(mAct, object.getString("id"));
            SharePreUtils.setNicName(mAct, object.getString("nicName"));
            SharePreUtils.setHeadImg(mAct, object.getString("phUrl"));
            SharePreUtils.setPhoneContact(mAct, object.getString("phoneContact"));
            TencentHelper.txLogin(null);
            Logger.t(TAG).d("开始启动Home 页");
            SharePreUtils.setFirst(mAct, object.getString("first"));//首次签到
            Intent intent = new Intent(WelcomeAct.this, HomeAct.class);
            startActivity(intent);
            huanXinLogin();
        } catch (JSONException e)
        {
            Logger.t(TAG).d("TOKEN_LOGIN>" + e.getMessage());
            e.printStackTrace();
            if (!mAct.isFinishing())
            {
                new CustomAlertDialog(WelcomeAct.this)
                        .builder()
                        .setMsg("服务器繁忙，请重试！")
                        .setCancelable(false)
                        .setPositiveButton("确认", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                mAct.finish();
                            }
                        }).show();
            }
        }
    }
}

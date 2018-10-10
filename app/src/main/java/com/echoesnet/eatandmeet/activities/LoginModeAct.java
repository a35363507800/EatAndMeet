package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.listeners.IOnAppStateChangeListener;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.IMUtils.IMHelper;
import com.echoesnet.eatandmeet.utils.IMUtils.TXInitBusinessHelper;
import com.echoesnet.eatandmeet.utils.IMUtils.TencentHelper;
import com.echoesnet.eatandmeet.utils.LoadingProgressUtil;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.echoesnet.eatandmeet.utils.MyProgressDialog;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.mob.MobSDK;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by Administrator on 2017/11/23.
 *
 * @author ling
 */

public class LoginModeAct extends BaseActivity
{
    public final static String TAG = LoginModeAct.class.getSimpleName();
    @BindView(R.id.btn_start)
    TextView btStart;
    @BindView(R.id.iv_background)
    ImageView ivBackground;
    @BindView(R.id.im_weixin_login)
    ImageView ivWeixin;
    @BindView(R.id.loading_view)
    RelativeLayout loadingView;
    @BindView(R.id.root)
    RelativeLayout root;
    private Activity mAct;

    private boolean isShowLoadingView = true;

    private MyProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_loginmode);
        mAct = this;
        ButterKnife.bind(this);
        MobSDK.init(this, EamConstant.SHARESDK_APPKEY, EamConstant.SHARESDK_APPSECRET);
        initView();

        if (("1").equals(EamApplication.getInstance().wxLoginFlag))
        {
            goWeChatLogin();
//            LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 0, null);
        }

        if (("2").equals(EamApplication.getInstance().wxLoginFlag))
        {
            startActivity(new Intent(mAct, LoginAct.class));
            finish();
        }


        EamApplication.getInstance().wxLoginFlag = "0";
    }

    @OnClick({R.id.im_weixin_login, R.id.btn_start})
    void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.im_weixin_login:
                goWeChatLogin();
                break;
            case R.id.btn_start:
                startActivity(new Intent(mAct, LoginAct.class));
                finish();
                break;
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (!isShowLoadingView)
        {
            loadingView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (!CommonUtils.isAppOnForeground(this))
        {
            //已经切后台
            isShowLoadingView = false;
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
//        if (loadingView != null && loadingView.getVisibility() == View.VISIBLE)
//            LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
    }

    private void initView()
    {
        btStart.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        pDialog = new MyProgressDialog()
                .buildDialog(this)
                .setDescription("正在登录...");
        pDialog.setCancelable(false);
    }


    private void goWeChatLogin()
    {
        //三方登录微信
        Platform weChat = ShareSDK.getPlatform(Wechat.NAME);
        weChat.setPlatformActionListener(paListener);
        if (!weChat.isClientValid())
        {
            ToastUtils.showShort("您未安装微信，请先安装");
            return;
        }
        if (weChat.isAuthValid())
        {
            //微信是否授权登录了
            weChat.removeAccount(true);
        }
//        weChat.SSOSetting(false);
//        weChat.authorize();
        weChat.showUser(null);
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 0, null);
    }

    PlatformActionListener paListener = new PlatformActionListener()
    {
        @Override
        public void onComplete(final Platform platform, int i, final HashMap<String, Object> hashMap)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    //      LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 0, null);
                    try
                    {
                        String deviceInfo = Build.MANUFACTURER + " " + Build.MODEL;//生产厂商加型号
                        Map<String, String> reqParamMap = new ArrayMap<>();
                        reqParamMap.put(ConstCodeTable.access_token, platform.getDb().getToken());
                        reqParamMap.put(ConstCodeTable.refresh_token, platform.getDb().get("refresh_token"));
                        reqParamMap.put(ConstCodeTable.unionid, platform.getDb().get("unionid"));
                        reqParamMap.put(ConstCodeTable.openId, platform.getDb().getUserId());
                        reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(null));
                        reqParamMap.put(ConstCodeTable.channelId, "");
                        reqParamMap.put(ConstCodeTable.device, deviceInfo);
                        reqParamMap.put(ConstCodeTable.deviceType, "Android");
                        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
                        {
                            @Override
                            public void onNext(String response)
                            {
                                super.onNext(response);
                                Logger.t(TAG).d("微信登录获得的结果：" + response);
                                try
                                {
                                    JSONObject body = new JSONObject(response);
                                    SharePreUtils.setToken(mAct, body.getString("token"));
                                    SharePreUtils.setId(mAct, body.getString("id"));
                                    SharePreUtils.setUId(mAct, body.getString("uId"));
                                    SharePreUtils.setHeadImg(mAct, body.getString("phUrl"));
                                    SharePreUtils.setNicName(mAct, body.getString("nicName"));
                                    SharePreUtils.setUserMobile(mAct, body.getString("mobile"));
                                    SharePreUtils.setPhoneContact(mAct, body.getString("phoneContact"));
                                    final String tlsName = body.getString("name");
                                    final String tlsSign = body.getString("sign");
                                    SharePreUtils.setTlsName(mAct, tlsName);
                                    SharePreUtils.setTlsSign(mAct, tlsSign);
                                    //登录环信
                                    IMHelper.getInstance().setOnILoginFinishListener(new HxLoginFinished((LoginModeAct) mAct));
                                    IMHelper.getInstance().huanXinLogin(mAct);

                                    TXInitBusinessHelper.initApp(getApplicationContext());// FIXME: 2017/3/21 应该没有用，暂留位置，便于查找
                                    //腾讯IM登录
                                    TencentHelper.txLogin(null);
//
                                    Intent intent = new Intent(mAct, HomeAct.class);
                                    startActivity(intent);
                                    finish();
                                } catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onHandledError(ApiException apiE)
                            {
                                LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
                                //如果信息不完善则要求去完善资料
                                if ("USR_INFO_INCOM".equals(apiE.getErrorCode()))
                                {
                                    try
                                    {
                                        JSONObject eBody = new JSONObject(apiE.getErrBody());

                                        Intent intent = new Intent(mAct, WeChatMakeUserInfo.class);

                                        intent.putExtra("sex", eBody.getString("sex"));
                                        intent.putExtra("phUrl", eBody.getString("phUrl"));
                                        intent.putExtra("nicName", eBody.getString("nicName"));
                                        intent.putExtra("unionid", eBody.getString("unionid"));
                                        startActivity(intent);
                                        finish();
                                    } catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                                else if ("USR_BEEN_LOCKING".equals(apiE.getErrorCode()))
                                {
                                    new CustomAlertDialog(mAct)
                                            .builder()
                                            .setCancelable(false)
                                            .setTitle("提示")
                                            .setMsg("您的账号存在违规行为，系统已将您的账号锁定，建议您致电客服电话022-24468693进行咨询。")
                                            .setPositiveButton("确定", new View.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(View v)
                                                {

                                                }
                                            }).show();
                                }
                                else
                                {
                                    super.onHandledError(apiE);
                                }
                                if (pDialog != null && pDialog.isShowing())
                                    pDialog.dismiss();
                            }
                        }, NetInterfaceConstant.WeChatC_weChatLogin, reqParamMap);

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        Logger.t(TAG).d(e.getMessage());
                    }
                }
            });
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable)
        {
            Logger.t(TAG).d("error:" + throwable.getMessage());
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 1, new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            goWeChatLogin();
                        }
                    });
                }
            });
        }

        @Override
        public void onCancel(Platform platform, int i)
        {
            Logger.t(TAG).d("cancle" + i);
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
                }
            });

        }
    };


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();

        }
        pDialog = null;
    }

    /**
     * 环信登录完成回调
     * 使用内部静态类，防止内存泄漏--wb
     */
    private static class HxLoginFinished implements IMHelper.IHxLoginFinishedListener
    {
        private final WeakReference<LoginModeAct> mActivity;

        private HxLoginFinished(LoginModeAct context)
        {
            this.mActivity = new WeakReference<>(context);
        }

        @Override
        public void onSuccess()
        {
            final LoginModeAct cAct = mActivity.get();
            if (cAct != null)
            {
                cAct.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (cAct.pDialog != null && cAct.pDialog.isShowing())
                            cAct.pDialog.dismiss();
                        ToastUtils.showShort("登录成功");
                        //环信登录成功后，刷新一下本地的通讯录
                        //HuanXinIMHelper.getInstance().asyncFetchContactsFromServer(cAct, null);

                        EamLogger.t(TAG).writeToDefaultFile("环信登录成功》");
                        cAct.finish();//此刻finish很重要，不然此回调可能不执行

                    }
                });
            }
        }

        @Override
        public void onFailed(int i, final String s)
        {
            final LoginModeAct cAct = mActivity.get();
            if (cAct != null)
            {
                if (cAct.pDialog != null && cAct.pDialog.isShowing())
                    cAct.pDialog.dismiss();
                EamLogger.t(TAG).writeToDefaultFile("环信登录失败》" + "错误码》" + i + "错误信息》" + s);
                //暂时让登录失败也过去
                cAct.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (cAct.pDialog != null && cAct.pDialog.isShowing())
                            cAct.pDialog.dismiss();
                        ToastUtils.showShort("H登录失败：" + s);
                        cAct.finish();
                    }
                });


            }
        }
    }

    private static class AppStateChange implements IOnAppStateChangeListener
    {
        private final WeakReference<LoginModeAct> mActRef;

        private AppStateChange(LoginModeAct lAct)
        {
            mActRef = new WeakReference<>(lAct);
        }

        @Override
        public void switchToBack()
        {
            LoginModeAct lAct = mActRef.get();
            if (lAct != null)
            {
                lAct.isShowLoadingView = false;
            }
        }

        @Override
        public void killedFromTask()
        {

        }
    }


    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if ((System.currentTimeMillis() - exitTime) > 2000)
            {
                ToastUtils.showShort("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            }
            else
            {
                finish();
              //  System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}

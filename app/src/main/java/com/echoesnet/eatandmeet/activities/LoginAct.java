package com.echoesnet.eatandmeet.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.listeners.IOnAppStateChangeListener;
import com.echoesnet.eatandmeet.presenters.ImpILoginView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ILoginView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.echoesnet.eatandmeet.utils.IMUtils.IMHelper;
import com.echoesnet.eatandmeet.utils.IMUtils.TXInitBusinessHelper;
import com.echoesnet.eatandmeet.utils.IMUtils.TencentHelper;
import com.echoesnet.eatandmeet.utils.LoadingProgressUtil;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.echoesnet.eatandmeet.utils.MemoryUtils.MemoryHelper;
import com.echoesnet.eatandmeet.utils.MyProgressDialog;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.echoesnet.eatandmeet.views.widgets.securityCodeView.CheckUtil;
import com.echoesnet.eatandmeet.views.widgets.securityCodeView.CheckView;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import okhttp3.Call;


public class LoginAct extends MVPBaseActivity<LoginAct, ImpILoginView> implements ILoginView
{
    public final static String TAG = LoginAct.class.getSimpleName();

    //region 变量
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBar;
    @BindView(R.id.iv_login_pw_show)
    IconTextView ivPwShow;
    @BindView(R.id.tv_login_vcode)
    CheckView cvSecurityCode;
    @BindView(R.id.ew_login_verify_code)
    EditText etInputSecCode;
    @BindView(R.id.ev_login_phone_num)
    EditText etUserId;
    @BindView(R.id.et_login_pw)
    EditText etPassword;
    @BindView(R.id.ll_user_login)
    LinearLayout ll_user_login;
    @BindView(R.id.root)
    RelativeLayout root;
    @BindView(R.id.weixin_login)
    RelativeLayout weiXinLogin;
    @BindView(R.id.loading_view)
    RelativeLayout loadingView;
    @BindView(R.id.password_gone)
    IconTextView tvPasswordGone;
    @BindView(R.id.user_clear)
    IconTextView tvClear;
    private Context mContext;
    private MyProgressDialog pDialog;
    private boolean isShow = false;
    // 验证码：
    private int[] checkNum = null;
    private long exitTime = 0;
    private boolean isShowLoadingView = true;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        ButterKnife.bind(this);
        initAfterView();

        if (("1").equals(EamApplication.getInstance().wxLoginFlag))
            goWeChatLogin();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        String phoneNum = getIntent().getStringExtra("inputMobile");
        if (!TextUtils.isEmpty(phoneNum))
        {
            etUserId.setText(phoneNum);
        }
        Logger.t(TAG).d("onStart>>" + phoneNum);
        if (!isShowLoadingView)
        {
            loadingView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Logger.t(TAG).d("onResume");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Logger.t(TAG).d("onPause");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Logger.t(TAG).d("onStop");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();

        }
        pDialog = null;
        IMHelper.getInstance().removeLoginFinishListener();
    }

    @Override
    protected ImpILoginView createPresenter()
    {
        return new ImpILoginView();
    }


    void initAfterView()
    {
        this.mContext = this;

        topBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                Intent intent = new Intent(mContext, LoginModeAct.class);
                mContext.startActivity(intent);
                finish();
            }

            @Override
            public void right2Click(View view)
            {
            }
        }).setText("用户登录");

        topBar.getNavBtns2(new int[]{1, 0, 0, 0});
        ivPwShow.setTag("hide");
        initCheckNum();
        pDialog = new MyProgressDialog()
                .buildDialog(this)
                .setDescription("正在登录...");
        pDialog.setCancelable(false);

        //将除了标示是否安装的字段全部删除
        ArrayList<String> aLst = new ArrayList<String>();
        aLst.add("versionCode");
        aLst.add("token");
        SharePreUtils.removeValueExcludeSome(mContext, aLst);
        //登录前停止AVContext // TODO: 2017/3/14 0014  
//        QavsdkControl.getInstance().stopContext();
        MemoryHelper.getInstance().setAppStateChangeListener(new AppStateChange(LoginAct.this));


        etUserId.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (TextUtils.isEmpty(etUserId.getText().toString().trim()))
                    tvClear.setVisibility(View.GONE);
                else
                    tvClear.setVisibility(View.VISIBLE);
            }
        });
    }

    // 初始化验证码并且刷新界面
    private void initCheckNum()
    {
        checkNum = CheckUtil.getCheckNum();
        cvSecurityCode.setCheckNum(checkNum);
        cvSecurityCode.invaliChenkNum();
    }

    @OnClick({R.id.btn_login_ok, R.id.tw_login_fpw, R.id.btn_register_ok, R.id.tv_login_change_vcode,
            R.id.iv_login_pw_show, R.id.weixin_login, R.id.password_gone, R.id.user_clear})
    void onViewClick(View view)
    {
        String tokenId = SharePreUtils.getToken(mContext);
        switch (view.getId())
        {
            case R.id.btn_login_ok:
                //mPresenter.login(etUserId.getText().toString(), etPassword.getText().toString());
                if (verifyInput())
                {
                    if (mPresenter != null)
                    {
                        if (pDialog != null && !pDialog.isShowing())
                            pDialog.show();
                        mPresenter.login(etUserId.getText().toString(), etPassword.getText().toString());
                    }
                }
//                Intent intents = new Intent(mContext, MakeUserInfo.class);
//                intents.putExtra("sex", "男");
//                intents.putExtra("phUrl", "https://t11.baidu.com/it/u=2819217133,1808522652&fm=173&app=25&f=JPEG?w=600&h=300&s=2D6DF804DE211007596429A803003092");
//                intents.putExtra("nicName", "as搭讪");
//                intents.putExtra("unionid", "a4sd68a4dwd6as6daa4dw6da");
//                startActivity(intents);
                break;
            case R.id.tw_login_fpw:
                if (!TextUtils.isEmpty(tokenId))
                {
                    Intent intent = new Intent(mContext, ForgetPasswordAct.class);
                    startActivity(intent);
                }
                else
                {
                    if (mPresenter != null)
                    {
                        if (pDialog != null && !pDialog.isShowing())
                            pDialog.show();
                        mPresenter.getTokenId(2);
                    }
                }
                break;
            case R.id.btn_register_ok:
//                StatService.onEvent(mContext, "loginAct_register", getString(R.string.baidu_other), 1);
                if (!TextUtils.isEmpty(tokenId))
                {
                    Intent intent = new Intent(mContext, RegisterAct.class);
                    startActivity(intent);
                }
                else
                {
                    if (mPresenter != null)
                    {
                        if (pDialog != null && !pDialog.isShowing())
                            pDialog.show();
                        mPresenter.getTokenId(1);
                    }
                }
                break;
            case R.id.tv_login_change_vcode:
                initCheckNum();
                break;
            case R.id.iv_login_pw_show:
                if (String.valueOf(ivPwShow.getTag()).equals("hide"))
                {
                    ivPwShow.setTag("show");
                    ivPwShow.setTextColor(ContextCompat.getColor(mContext, R.color.MC7));
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    etPassword.setSelection(etPassword.getText().length());
                }
                else
                {
                    ivPwShow.setTag("hide");
                    ivPwShow.setTextColor(ContextCompat.getColor(mContext, R.color.FC3));
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etPassword.setSelection(etPassword.getText().length());
                }
                break;
            case R.id.weixin_login:
                goWeChatLogin();
                break;
            case R.id.password_gone:
                if (isShow)
                {
                    isShow = false;
                    tvPasswordGone.setText("{eam-e620 @color/FC7}");
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                else
                {
                    isShow = true;
                    tvPasswordGone.setText("{eam-e95c @color/C0412}");
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }

                break;
            case R.id.user_clear:
                etUserId.setText("");
                break;
            default:
                break;
        }
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
        weChat.SSOSetting(false);
        //   weChat.authorize();
        weChat.showUser(null);
        loadingView.setVisibility(View.VISIBLE);
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 0, null);
    }

    /**
     * 输入验证
     *
     * @return
     */
    private boolean verifyInput()
    {
        boolean allValid = true;
        //验证手机号
        if (!CommonUtils.verifyInput(3, etUserId.getText().toString()))
        {
            ToastUtils.showShort("请输入正确的手机号");
            allValid = false;
        }

        if (allValid == true && TextUtils.isEmpty(etPassword.getText().toString()))
        {
            ToastUtils.showShort("密码不能为空");
            allValid = false;
        }
        if (allValid)
        {
            String userInput = etInputSecCode.getText().toString();
            if (TextUtils.isEmpty(userInput))
            {
                ToastUtils.showShort("请输入验证码");
                return false;
            }
            if (!CheckUtil.checkNum(userInput, checkNum))
            {
                ToastUtils.showShort("验证码错误，请重新输入");
                return false;
            }
            else
                return true;
        }
        else
        {
            return false;
        }
    }

    PlatformActionListener paListener = new PlatformActionListener()
    {
        @Override
        public void onComplete(final Platform platform, int i, final HashMap<String, Object> hashMap)
        {
            weChatLogin(platform);
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

    private void weChatLogin(final Platform platform)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 0, null);
                if (mPresenter != null)
                    mPresenter.weChatLogin(platform);
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(mContext, LoginModeAct.class);
        mContext.startActivity(intent);
        super.onBackPressed();
    }

    //    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
//        {
//            if ((System.currentTimeMillis() - exitTime) > 2000)
//            {
//                ToastUtils.showShort("再按一次退出程序");
//                exitTime = System.currentTimeMillis();
//            }
//            else
//            {
//                finish();
//                //System.exit(0);
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }


    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mContext, null, exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void callServerFailCallback(String interfaceName, String code, String body)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.UserC_signIn:
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
                //如果信息不完善则要求去完善资料
                if ("USR_INFO_INCOM".equals(code))
                {
                    SharePreUtils.setUserMobile(mContext, etUserId.getText().toString());
                    Intent intent = new Intent(mContext, MakeUserInfo.class);
                    startActivity(intent);
                }
                else if ("USR_BEEN_LOCKING".equals(code))
                {
                    new CustomAlertDialog(mContext)
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
                else if (ErrorCodeTable.USR_NOT_REGISTER.equals(code))
                {
                    ToastUtils.showShort("该用户未注册");
                }

                break;
            case NetInterfaceConstant.WeChatC_weChatLogin:
                LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
                //如果信息不完善则要求去完善资料
                if ("USR_INFO_INCOM".equals(code))
                {
                    try
                    {
                        JSONObject eBody = new JSONObject(body);

                        Intent intent = new Intent(mContext, WeChatMakeUserInfo.class);

                        intent.putExtra("sex", eBody.getString("sex"));
                        intent.putExtra("phUrl", eBody.getString("phUrl"));
                        intent.putExtra("nicName", eBody.getString("nicName"));
                        intent.putExtra("unionid", eBody.getString("unionid"));
                        startActivity(intent);
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if ("USR_BEEN_LOCKING".equals(code))
                {
                    new CustomAlertDialog(mContext)
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
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
                break;
            case NetInterfaceConstant.UserC_getTokenId:
                if (code.equals("GET_TOKEN_ERROR"))
                {
                    ToastUtils.showShort("发生错误，请重试一下");
                    Logger.t(TAG).d("token 获取失败");
                }
                break;
            default:
                break;
        }
//        ToastUtils.showShort(ErrorCodeTable.parseErrorCode(code));
    }

    @Override
    public void getTokenIdCallback(String response, int type)
    {
        try
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
            JSONObject body = new JSONObject(response);
            String tokenId = body.getString("tokenId");
            if (!TextUtils.isEmpty(tokenId))
            {
                SharePreUtils.setToken(mContext, tokenId);
                switch (type)
                {
                    case 1:
                        Intent rIntent = new Intent(mContext, RegisterAct.class);
                        rIntent.putExtra("token", tokenId);
                        mContext.startActivity(rIntent);
                        break;
                    case 2:
                        Intent fIntent = new Intent(mContext, ForgetPasswordAct.class);
                        fIntent.putExtra("token", tokenId);
                        mContext.startActivity(fIntent);
                        break;
                }
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        }
    }

    @Override
    public void loginCallback(String response)
    {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        try
        {
            JSONObject body = new JSONObject(response);
            SharePreUtils.setToken(mContext, body.getString("token"));
            SharePreUtils.setUId(mContext, body.getString("uId"));
            SharePreUtils.setId(mContext, body.getString("id"));
            SharePreUtils.setHeadImg(mContext, body.getString("phUrl"));
            SharePreUtils.setNicName(mContext, body.getString("nicName"));
            SharePreUtils.setPhoneContact(mContext, body.getString("phoneContact"));
            final String tlsName = body.getString("name");
            final String tlsSign = body.getString("sign");
            SharePreUtils.setTlsName(mContext, tlsName);
            SharePreUtils.setTlsSign(mContext, tlsSign);
            //手机号
            SharePreUtils.setUserMobile(mContext, etUserId.getText().toString());

            //登录环信
            IMHelper.getInstance().setOnILoginFinishListener(new HxLoginFinished((LoginAct) mContext));
            IMHelper.getInstance().huanXinLogin(mContext);
            //腾讯IM登录
            TXInitBusinessHelper.initApp(getApplicationContext());// FIXME: 2017/3/21 应该没有用，暂留位置，便于查找
            TencentHelper.txLogin(null);
            HuanXinIMHelper.getInstance().init(getApplicationContext());//防止崩溃后不走welcome页面直接走登录页面环信出问题

            //首次签到
            SharePreUtils.setFirst(mContext, body.getString("first"));

            Intent intent = new Intent(mContext, HomeAct.class);
            startActivity(intent);

        } catch (Exception e)
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
            e.printStackTrace();
            Logger.t(TAG).d("登录异常：" + e.getMessage());
        }
    }

    @Override
    public void weChatLoginCallback(String response)
    {
        Logger.t(TAG).d("微信登录获得的结果：" + response);
        try
        {
            JSONObject body = new JSONObject(response);
            SharePreUtils.setToken(mContext, body.getString("token"));
            SharePreUtils.setId(mContext, body.getString("id"));
            SharePreUtils.setUId(mContext, body.getString("uId"));
            SharePreUtils.setHeadImg(mContext, body.getString("phUrl"));
            SharePreUtils.setNicName(mContext, body.getString("nicName"));
            SharePreUtils.setUserMobile(mContext, body.getString("mobile"));
            SharePreUtils.setPhoneContact(mContext, body.getString("phoneContact"));
            final String tlsName = body.getString("name");
            final String tlsSign = body.getString("sign");
            SharePreUtils.setTlsName(mContext, tlsName);
            SharePreUtils.setTlsSign(mContext, tlsSign);
            //登录环信
            IMHelper.getInstance().setOnILoginFinishListener(new HxLoginFinished((LoginAct) mContext));
            IMHelper.getInstance().huanXinLogin(mContext);

            TXInitBusinessHelper.initApp(getApplicationContext());// FIXME: 2017/3/21 应该没有用，暂留位置，便于查找
            //腾讯IM登录
            TencentHelper.txLogin(null);
            HuanXinIMHelper.getInstance().init(getApplicationContext());//防止崩溃后不走welcome页面直接走登录页面环信出问题

            Intent intent = new Intent(mContext, HomeAct.class);

            startActivity(intent);
            finish();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * 环信登录完成回调
     * 使用内部静态类，防止内存泄漏--wb
     */
    private static class HxLoginFinished implements IMHelper.IHxLoginFinishedListener
    {
        private final WeakReference<LoginAct> mActivity;

        private HxLoginFinished(LoginAct context)
        {
            this.mActivity = new WeakReference<>(context);
        }

        @Override
        public void onSuccess()
        {
            final LoginAct cAct = mActivity.get();
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
            final LoginAct cAct = mActivity.get();
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
        private final WeakReference<LoginAct> mActRef;

        private AppStateChange(LoginAct lAct)
        {
            mActRef = new WeakReference<>(lAct);
        }

        @Override
        public void switchToBack()
        {
            LoginAct lAct = mActRef.get();
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

}

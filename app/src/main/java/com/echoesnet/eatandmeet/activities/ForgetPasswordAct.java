package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.presenters.ImpIForgetPassWordView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IForgetPasswordView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IGetSecurityCodeListener;
import com.echoesnet.eatandmeet.presenters.viewinterface.IGetVoiceCodeListener;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.IMUtils.IMHelper;
import com.echoesnet.eatandmeet.utils.IMUtils.TencentHelper;
import com.echoesnet.eatandmeet.utils.MyProgressDialog;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ForgetPasswordAct extends MVPBaseActivity<IForgetPasswordView, ImpIForgetPassWordView> implements IForgetPasswordView
{
    public final static String TAG = ForgetPasswordAct.class.getSimpleName();
    //region 变量
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBar;
    @BindView(R.id.ev_forget_phone_num)
    EditText etPhoneNum;
    @BindView(R.id.ew_forget_pw)
    EditText etPassword;
    @BindView(R.id.ew_forget_verify_code)
    EditText etSecurityCode;
    @BindView(R.id.btn_forget_ok)
    Button btnGetPassword;
    @BindView(R.id.btn_forget_get_vcode)
    Button btnGetVcode;
    @BindView(R.id.btn_forget_get_vcode_line)
    View vGetVcode;
    //    @ViewById(R.id.iv_reg_pw_show)
//    IconTextView ivPwShow;
    @BindView(R.id.tv_tip)
    TextView tvSoundTip;
    @BindView(R.id.tv_sound_code)
    TextView tvSoundCode;  //语音验证码
    @BindView(R.id.password_gone)
    IconTextView tvPasswordGone;

    private Activity mContext;
    private MyProgressDialog pDialog;
    private String tokenId;
    private int MsgCountdown = 60;
    private int AudioCountdown = 60;
    private Timer MsgTimer;
    private Timer AudioTimer;
    private TimerTask ttMsgCountDown;
    private TimerTask ttAudioMsgCountDown;

    private boolean isShow = false;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_forget_password);
        ButterKnife.bind(this);
        initAfterView();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
        }
        IMHelper.getInstance().removeLoginFinishListener();
    }


    void initAfterView()
    {
        mContext = this;
        //  topBar.setTitle(getResources().getString(R.string.forget_password));
        //ivPwShow.setTag("hide");
        topBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                finish();
            }

            @Override
            public void right2Click(View view)
            {

            }
        }).setText(getResources().getString(R.string.forget_password));
        topBar.getNavBtns(new int[]{1, 0, 0, 0});
        tokenId = getIntent().getStringExtra("token");
        MsgTimer = new Timer();
        AudioTimer = new Timer();
        pDialog = new MyProgressDialog()
                .buildDialog(this)
                .setDescription("正在处理...");
        pDialog.setCancelable(false);

        etPassword.setFilters(new InputFilter[]{filter});

    }

    @OnClick({R.id.btn_forget_ok, R.id.btn_forget_get_vcode, R.id.tv_sound_code, R.id.password_gone})
    void onViewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_forget_ok:
                String phone = etPhoneNum.getText().toString();
                String verifyCode = etSecurityCode.getText().toString();
                String securirty = etPassword.getText().toString();
                if (TextUtils.isEmpty(phone))
                {
                    ToastUtils.showShort("请输入正确的手机号");
                    break;
                }
                if (TextUtils.isEmpty(verifyCode))
                {
                    ToastUtils.showShort("验证码不能为空");
                    break;
                }
                if (TextUtils.isEmpty(securirty))
                {
                    ToastUtils.showShort("请输入登录密码");
                    break;
                }
                if (!CommonUtils.verifyInput(1, securirty))
                {
                    ToastUtils.showShort("请输入8到16位字母和数字的组合");
                    break;
                }
                //验证验证码
                if (mPresenter != null)
                    mPresenter.validSecurityCode(phone, verifyCode, "3", tokenId);
                break;
            case R.id.btn_forget_get_vcode:
                if (!CommonUtils.verifyInput(3, etPhoneNum.getText().toString()))
                {
                    new CustomAlertDialog(mContext).builder().setMsg("请输入正确的手机号").setNegativeButton("确定", null).show();
                    break;
                }
                if (btnGetVcode.isEnabled())
                {
                    Logger.t(TAG).d("执行了");
                    //发送验证码
                    NetHelper.getSecurityCodeMsg(mContext, etPhoneNum.getText().toString(), "3", tokenId, new IGetSecurityCodeListener()
                    {
                        @Override
                        public void onSuccess()
                        {
                            btnGetVcode.setEnabled(false);
                            btnGetVcode.setTextColor(ContextCompat.getColor(mContext, R.color.FC7));
                            vGetVcode.setBackgroundColor(ContextCompat.getColor(mContext, R.color.FC7));
                            ttMsgCountDown = new SecurityCountDown();
                            MsgTimer.scheduleAtFixedRate(ttMsgCountDown, 0, 1000);
                            new CustomAlertDialog(mContext).builder().setMsg("验证码已发出，有效期15分钟呦~").setNegativeButton("确定", null).show();
                        }

                        @Override
                        public void onFailed(String errorCode)
                        {
                            failedButton();
                        }
                    });
                }
                break;
//            case R.id.iv_reg_pw_show:
//                if (String.valueOf(ivPwShow.getTag()).equals("hide"))
//                {
//                    ivPwShow.setTag("show");
//                    ivPwShow.setTextColor(ContextCompat.getColor(mContext, R.color.c10));
//                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//                    etPassword.setSelection(etPassword.getText().length());
//                }
//                else
//                {
//                    ivPwShow.setTag("hide");
//                    ivPwShow.setTextColor(ContextCompat.getColor(mContext, R.color.FC3));
//                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT |
//                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                    etPassword.setSelection(etPassword.getText().length());
//                }
//                break;
            case R.id.tv_sound_code:
                //语音验证码
                if (!CommonUtils.verifyInput(3, etPhoneNum.getText().toString()))
                {
                    ToastUtils.showShort("请输入正确的手机号");
                    break;
                }
                if (tvSoundCode.isEnabled())
                {
                    tvSoundTip.setText(getString(R.string.voiceCode));
                    tvSoundCode.setEnabled(false);
                    tvSoundCode.setTextColor(ContextCompat.getColor(mContext, R.color.C0323));
                    ttAudioMsgCountDown = new SoundCodeCountDown();
                    AudioTimer.scheduleAtFixedRate(ttAudioMsgCountDown, 0, 1000);
                    NetHelper.getVoiceCodeMsg(mContext, etPhoneNum.getText().toString(), "3", tokenId, new IGetVoiceCodeListener()
                    {
                        @Override
                        public void onSuccess()
                        {

                        }

                        @Override
                        public void onFailed(String errorCode)
                        {
                            resetGetVoiceCodeButton();
                        }
                    });
                }
                break;
            case R.id.password_gone:
                String editPassWord = etPassword.getText().toString();
                if (isShow)
                {
                    isShow = false;
                    tvPasswordGone.setText("{eam-e620 @color/FC7}");
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                } else
                {
                    isShow = true;
                    tvPasswordGone.setText("{eam-e95c @color/C0412}");
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                if (TextUtils.isEmpty(editPassWord))
                {
                    etPassword.setSelection(0);
                } else
                {
                    etPassword.setSelection(editPassWord.length());
                }

                break;
            default:
                break;
        }
    }

    /**
     * 保存腾讯账号到本地，并登录
     *
     * @param tUserName
     * @param tUserSign
     */
    private void addTXAccountToLocalAndLogin(final String tUserName, String tUserSign)
    {
        //腾讯用户名和签名
        SharePreUtils.setTlsName(mContext, tUserName);
        SharePreUtils.setTlsSign(mContext, tUserSign);
        //腾讯IM登录
        TencentHelper.txLogin( null);
    }


    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.UserC_forgetPwd:
                if ("USR_INFO_INCOM".equals(code))
                {
                    SharePreUtils.setUserMobile(mContext, etPhoneNum.getText().toString());
                    String token = errBody;
                    if (!TextUtils.isEmpty(errBody))
                        SharePreUtils.setToken(mContext, token);
                    Intent intent = new Intent(mContext, MakeUserInfo.class);
                    startActivity(intent);
                    mContext.finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getPasswordCallback(String response)
    {
        Logger.t(TAG).d("重置密码返回--> " + response);
        try
        {
            JSONObject body = new JSONObject(response);
            SharePreUtils.setToken(mContext, body.getString("token"));
            SharePreUtils.setUId(mContext, body.getString("uId"));
            SharePreUtils.setHeadImg(mContext, body.optString("phUrl", ""));
            SharePreUtils.setNicName(mContext, body.getString("nicName"));
            SharePreUtils.setTlsName(mContext, body.getString("name"));
            SharePreUtils.setTlsSign(mContext, body.getString("sign"));
            //手机号
            SharePreUtils.setUserMobile(mContext, etPhoneNum.getText().toString());
            IMHelper.getInstance().setOnILoginFinishListener(new IMHelper.IHxLoginFinishedListener()
            {
                @Override
                public void onSuccess()
                {
                    (mContext).runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ToastUtils.showShort("重置密码成功");
                        }
                    });

                    Intent intent = new Intent(mContext, HomeAct.class);
                    mContext.startActivity(intent);
                    mContext.finish();
                }

                @Override
                public void onFailed(int i, final String s)
                {
                    //暂时让登录失败也过去
                    Intent intent = new Intent(mContext, HomeAct.class);
                    mContext.startActivity(intent);
                    (mContext).runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ToastUtils.showShort("H登录失败：" + s);
                        }
                    });
                    //LoginAct.this.finish();
                }
            });
            //登录环信
            IMHelper.getInstance().huanXinLogin(mContext);
            //登录腾讯
            addTXAccountToLocalAndLogin(body.getString("name"), body.getString("sign"));
            ToastUtils.showShort("登录成功");
        } catch (JSONException e)
        {
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
        } finally
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    @Override
    public void validSecurityCodeCallback(ArrayMap<String, Object> map)
    {
        String response = (String) map.get("response");
        String mobile = (String) map.get("mobile");
        Logger.t(TAG).d("获得的结果：" + response);
                                /*switch (verifyInput())
                                {
                                    case 0:
                                        Logger.t(TAG).d("000");
                                        break;
                                    case 3:
                                        Logger.t(TAG).d("111");*/
        //*******发布时放开*********
        if (mPresenter != null)
            mPresenter.getPassword(mobile, etPassword.getText().toString().trim());
//                                        break;
//                                }
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }


    private class SecurityCountDown extends TimerTask
    {
        @Override
        public void run()
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    btnGetVcode.setText(String.format("剩余%s秒", String.valueOf(MsgCountdown--)));
                    if (MsgCountdown < 1)
                    {
                        resetGetSecurityButton();
                    }
                }
            });
        }
    }

    private void resetGetSecurityButton()
    {
        btnGetVcode.setText("获取验证码");
        btnGetVcode.setEnabled(true);
        btnGetVcode.setTextColor(ContextCompat.getColor(mContext, R.color.C0412));
        vGetVcode.setBackgroundColor(ContextCompat.getColor(mContext, R.color.C0412));
        MsgCountdown = 60;
        if (ttMsgCountDown != null)
            ttMsgCountDown.cancel();

        //MsgTimer.cancel();
        //MsgTimer.purge();
    }

    private void failedButton()
    {
        btnGetVcode.setText("获取验证码");
        btnGetVcode.setEnabled(true);
        btnGetVcode.setTextColor(ContextCompat.getColor(mContext, R.color.C0412));
        vGetVcode.setBackgroundColor(ContextCompat.getColor(mContext, R.color.C0412));
        MsgCountdown = 60;
        if (ttMsgCountDown != null)
            ttMsgCountDown.cancel();

   //     ToastUtils.showShort("您今日已到达短信获取最大次数");
        //MsgTimer.cancel();
        //MsgTimer.purge();
    }

    /**
     * 语音验证码倒计时
     */
    private class SoundCodeCountDown extends TimerTask
    {
        @Override
        public void run()
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    tvSoundCode.setText(String.format("(%s)s", String.valueOf(AudioCountdown--)));
                    if (AudioCountdown < 1)
                    {
                        resetGetVoiceCodeButton();
                    }
                }
            });
        }
    }

    /**
     * 重置语音验证码获取按钮
     */
    private void resetGetVoiceCodeButton()
    {
        tvSoundTip.setText(getString(R.string.tryOtherStyle));
        tvSoundCode.setText(getString(R.string.voiceCode));
        tvSoundCode.setEnabled(true);
        tvSoundCode.setTextColor(ContextCompat.getColor(mContext, R.color.C0311));
        AudioCountdown = 60;
        if (ttAudioMsgCountDown != null)
            ttAudioMsgCountDown.cancel();
        //MsgTimer.cancel();
        //MsgTimer.purge();
    }

    InputFilter filter = new InputFilter()
    {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
        {
            for (int i = start; i < end; i++)
            {
                int type = Character.getType(source.charAt(i));
                //System.out.println("Type : " + type);
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL)
                {
                    ToastUtils.showShort( "不支持输入Emoji表情符号");
                    return "";
                }
            }
            return null;
        }
    };

    @Override
    protected ImpIForgetPassWordView createPresenter()
    {
        return new ImpIForgetPassWordView();
    }
}

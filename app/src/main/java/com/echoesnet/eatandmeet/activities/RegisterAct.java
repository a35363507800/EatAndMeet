package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.presenters.ImpIRegisterView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IGetSecurityCodeListener;
import com.echoesnet.eatandmeet.presenters.viewinterface.IGetVoiceCodeListener;
import com.echoesnet.eatandmeet.presenters.viewinterface.IRegisterView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
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
import okhttp3.Call;


public class RegisterAct extends MVPBaseActivity<RegisterAct, ImpIRegisterView> implements  IRegisterView
{
    //region 变量
    public final static String TAG = RegisterAct.class.getSimpleName();

    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBar;
    @BindView(R.id.ev_reg_phone_num)
    EditText etPhoneNum;
    @BindView(R.id.et_reg_pw)
    EditText etPassword;
    @BindView(R.id.ew_register_verify_code)
    EditText etSecurityCode;
    @BindView(R.id.btn_register_get_vcode_line)
    View vGetVcode;
    @BindView(R.id.tv_disclaimer)
    TextView tvDisclaimer;
    @BindView(R.id.tv_tip)
    TextView tvSoundTip;
    @BindView(R.id.tv_sound_code)
    TextView tvSoundCode;
    @BindView(R.id.btn_register_get_vcode)
    Button btnGetVcode;
    @BindView(R.id.tw_reg_fpw)
    TextView tvTranLogin;
    @BindView(R.id.tv_register_commit_ok)//注册按钮
            TextView tvRegisterCommitOk;
    @BindView(R.id.iv_agree_button)
    ImageView ivAgreeButton;
    @BindView(R.id.password_gone)
    IconTextView tvPasswordGone;
    private int MsgCountdown = 60;//短信返回的安全码，用于与测试开发手机号用
    private int AudioCountdown = 60;
    private Timer MsgTimer;
    private Timer AudioTimer;
    private TimerTask ttMsgCountDown;
    private TimerTask ttAudioMsgCountDown;
    private String tokenId;
    private Activity mAct;
    private Dialog pDialog;
    private boolean isShow;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_register);
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
    }

    @Override
    protected ImpIRegisterView createPresenter()
    {
        return new ImpIRegisterView();
    }


    private void initAfterView()
    {
        mAct = this;
        TextView title = topBar.inflateTextCenter(new TopbarSwitchSkeleton()
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
        });
        title.setText("注册");
        title.setTextColor(ContextCompat.getColor(mAct, R.color.black));
        MsgTimer = new Timer();
        AudioTimer = new Timer();
        tokenId = getIntent().getStringExtra("token");
        etPassword.setFilters(new InputFilter[]{filter});

        pDialog = DialogUtil.getCommonDialog(this, "正在注册...");
        pDialog.setCancelable(false);
        ivAgreeButton.setTag(true);
        ivAgreeButton.setImageResource(R.drawable.ico_press);
    }

    @OnClick({R.id.tv_disclaimer, R.id.tw_reg_fpw, R.id.iv_agree_button, R.id.tv_register_commit_ok,
            R.id.btn_register_get_vcode, R.id.tv_sound_code, R.id.password_gone})
    void onViewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tv_disclaimer://免责声明
                Intent intent = new Intent(mAct, MRechargePolicy.class);
                startActivity(intent);
                break;
            case R.id.iv_agree_button:
                changeCheckBoxState();
                break;
            case R.id.tv_register_commit_ok://注册按钮
                /**********测试***********************/
                //startActivity(new Intent(mAct, MakeUserInfo.class));//转入完善资料界面
                //发布时后放开
                switch (verifyInput())
                {
                    case 0:
                        ToastUtils.showShort("请输入正确的手机号码");
                        break;
                    case 1:
                        String passWord = etPassword.getText().toString().trim();
                        if (TextUtils.isEmpty(passWord))
                            ToastUtils.showShort("密码不能为空");
                        else
                            ToastUtils.showShort("密码必须为8到16位字母和数字的组合");
                        break;
                    case 2:
                        ToastUtils.showShort("请阅读使用须知");
                        break;
                    case 3:
                        String phoneNum = etPhoneNum.getText().toString().trim();
                        String securityCode = etSecurityCode.getText().toString().trim();
                        //**************测试时放开，发布时一定记得打开验证，注销下面的注册******************
                        //impIRegisterView.validSecurityCode(phoneNum, securityCode, "1", tokenId);
                        if (securityCode.equals(""))
                        {
                            ToastUtils.showShort("验证码不能为空");
                            break;
                        }
                        if (mPresenter != null)
                        {
                            mPresenter.validSecurityCode(phoneNum, securityCode, "1", tokenId);
                        }
                        break;
                }
                break;
            case R.id.tw_reg_fpw:
                Intent intentL = new Intent(mAct, LoginAct.class);
                startActivity(intentL);
                break;
            //密码是否显示
//            case R.id.iv_reg_pw_show:
//                if (String.valueOf(ivPwShow.getTag()).equals("hide"))
//                {
//                    ivPwShow.setTag("show");
//                    ivPwShow.setTextColor(ContextCompat.getColor(mAct, R.color.c10));
//                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//                    etPassword.setSelection(etPassword.getText().length());
//                }
//                else
//                {
//                    ivPwShow.setTag("hide");
//                    ivPwShow.setTextColor(ContextCompat.getColor(mAct, R.color.FC3));
//                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT |
//                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                    etPassword.setSelection(etPassword.getText().length());
//                }
//                break;
            //获取验证码
            case R.id.btn_register_get_vcode:
                if (!CommonUtils.verifyInput(3, etPhoneNum.getText().toString()))
                {
                    ToastUtils.showShort( "请输入正确的手机号");
                    break;
                }
                if (btnGetVcode.isEnabled())
                {
                    //发送验证码
                    NetHelper.getSecurityCodeMsg(mAct, etPhoneNum.getText().toString(), "1",
                            tokenId, new IGetSecurityCodeListener()
                    {
                        @Override
                        public void onSuccess()
                        {
                            Logger.t(TAG).d("执行了");
                            btnGetVcode.setEnabled(false);
                            btnGetVcode.setTextColor(ContextCompat.getColor(mAct, R.color.FC7));
                            vGetVcode.setBackgroundColor(ContextCompat.getColor(mAct, R.color.FC7));
                            ttMsgCountDown = new SecurityCountDown();
                            MsgTimer.scheduleAtFixedRate(ttMsgCountDown, 0, 1000);
                            ToastUtils.showShort("发送成功，验证码有效时间15分钟！");
                        }

                        @Override
                        public void onFailed(String errorCode)
                        {
                            resetGetSecurityButton();
                        }
                    });
                }
                break;
            case R.id.tv_sound_code:
                if (!CommonUtils.verifyInput(3, etPhoneNum.getText().toString()))
                {
                    ToastUtils.showShort("请输入正确的手机号");
                    break;
                }
                if (tvSoundCode.isEnabled())
                {
                    tvSoundTip.setText("电话正在接通中，请等候...");
                    tvSoundCode.setEnabled(false);
                    tvSoundCode.setTextColor(ContextCompat.getColor(mAct, R.color.C0323));
                    ttAudioMsgCountDown = new SoundCodeCountDown();
                    AudioTimer.scheduleAtFixedRate(ttAudioMsgCountDown, 0, 1000);
                    NetHelper.getVoiceCodeMsg(mAct, etPhoneNum.getText().toString(), "1",
                            tokenId, new IGetVoiceCodeListener()
                    {
                        @Override
                        public void onSuccess()
                        {

                        }

                        @Override
                        public void onFailed(String errorCode)
                        {
                            resetGetSoundCodeButton();
                            if ("USR_EXITS".equals(errorCode))
                            {
                                new CustomAlertDialog(mAct)
                                        .builder()
                                        .setTitle("提示")
                                        .setMsg("您的手机号已注册，您是否要登录？")
                                        .setPositiveButton("去登录", v ->
                                        {
                                            Intent lIntent = new Intent(mAct, LoginAct.class);
                                            lIntent.putExtra("inputMobile", etPhoneNum.getText().toString());
                                            mAct.startActivity(lIntent);
                                            mAct.finish();
                                        })
                                        .setNegativeButton("取消", (v) ->
                                        {
                                        })
                                        .show();
                            }
                            tvPasswordGone.performClick();
                        }
                    });
                }
            case R.id.password_gone:
                if (isShow)
                {
                    isShow = false;
                    tvPasswordGone.setText("{eam-e620 @color/FC7}");
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    Editable etable = etPassword.getText();
                    Selection.setSelection(etable, etable.length());
                }
                else
                {
                    isShow = true;
                    tvPasswordGone.setText("{eam-e95c @color/C0412}");
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    Editable etable = etPassword.getText();
                    Selection.setSelection(etable, etable.length());
                }

                break;
            default:
                break;
        }
    }


//    @CheckedChange({R.id.cb_register_statement})
//    void onCheckAbleChange(CheckBox view)
//    {
//        switch (view.getId())
//        {
//            case R.id.cb_register_statement:
//                changeCheckBoxStyle();
//                break;
//            default:
//                break;
//        }
//    }
//
//    private void changeCheckBoxStyle()
//    {
//        Logger.t("注册").d(cbIsReadDisclaimer.toString() + "");
//        if (cbIsReadDisclaimer.isChecked())
//        {
//            cbIsReadDisclaimer.setButtonDrawable(ContextCompat.getDrawable(mAct, R.drawable
// .check_box_p));
//        }
//        else
//        {
//            cbIsReadDisclaimer.setButtonDrawable(ContextCompat.getDrawable(mAct, R.drawable
// .check_box_n));
//        }
//    }

    /**
     * 改变同意协议 选中状态
     */
    private void changeCheckBoxState()
    {
        boolean isAgree = (boolean) ivAgreeButton.getTag();
        ivAgreeButton.setImageResource(isAgree ? R.drawable.check_box_p : R.drawable.ico_press);
        ivAgreeButton.setTag(!isAgree);
    }

    /**
     * 验证输入
     *
     * @return
     */
    private int verifyInput()
    {
        String phoneNum = etPhoneNum.getText().toString().trim();
        if (!TextUtils.isEmpty(phoneNum) && !CommonUtils.verifyInput(3, phoneNum))
            return 0;

        String passWord = etPassword.getText().toString().trim();
        boolean isYesPWD = CommonUtils.verifyInput(1, passWord);
        //密码校验
        if (isYesPWD == false)
            return 1;
        //选择框验证未通过
        if ((Boolean) ivAgreeButton.getTag() == false)
            return 2;
        //成功
        return 3;
    }


    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mAct, null, exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void registerCallback(String response)
    {
        try
        {
            JSONObject body = new JSONObject(response);
            String token = body.getString("token");
            //bug id: 7855
//                SharePreUtils.setToken(mAct, token);
//                // 注册的时候返回token 也就是uId
//                SharePreUtils.setUId(mAct, token);
//                SharePreUtils.setId(mAct, body.getString("id"));
            Logger.t(TAG).d("token:" + token);
            SharePreUtils.setUserMobile(mAct, etPhoneNum.getText().toString());
            SharePreUtils.setTlsName(mAct, body.getString("name"));
            SharePreUtils.setTlsSign(mAct, body.getString("sign"));
            resetGetSecurityButton();
            ToastUtils.showShort("注册成功，请完善资料");
            startActivity(new Intent(mAct, MakeUserInfo.class));//转入完善资料界面
            mAct.finish();
        } catch (JSONException e)
        {
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
        } finally
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
            tvRegisterCommitOk.setEnabled(true);
        }
    }

    @Override
    public void validSecurityCodeCallback(String response)
    {
        if (mPresenter != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            tvRegisterCommitOk.setEnabled(false);
            mPresenter.register(etPhoneNum.getText().toString(), etPassword.getText().toString(),tokenId);
        }
    }

    /**
     * 验证码倒计时
     */
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

    /**
     * 重置验证码获取按钮
     */
    private void resetGetSecurityButton()
    {
        btnGetVcode.setText("获取验证码");
        btnGetVcode.setEnabled(true);
        btnGetVcode.setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
        vGetVcode.setBackgroundColor(ContextCompat.getColor(mAct, R.color.C0412));
        MsgCountdown = 60;
        if (ttMsgCountDown != null)
            ttMsgCountDown.cancel();
        //MsgTimer.cancel();
        //MsgTimer.purge();   whosyourdaddy  nuttoutrr
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
                    tvSoundCode.setText(String.format("剩余%s秒", String.valueOf(AudioCountdown--)));
                    if (AudioCountdown < 1)
                    {
                        resetGetSoundCodeButton();
                    }
                }
            });
        }
    }

    /**
     * 重置验证码获取按钮
     */
    private void resetGetSoundCodeButton()
    {
        tvSoundTip.setText(getString(R.string.tryOtherStyle));
        tvSoundCode.setText(getString(R.string.voiceCode));
        tvSoundCode.setEnabled(true);
        tvSoundCode.setTextColor(ContextCompat.getColor(mAct, R.color.C0311));
        AudioCountdown = 60;
        if (ttAudioMsgCountDown != null)
            ttAudioMsgCountDown.cancel();
        //MsgTimer.cancel();
        //MsgTimer.purge();
    }

    InputFilter filter = new InputFilter()
    {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int
                dstart, int dend)
        {
            for (int i = start; i < end; i++)
            {
                int type = Character.getType(source.charAt(i));
                //System.out.println("Type : " + type);
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL)
                {
                    ToastUtils.showShort("不支持输入Emoji表情符号");
                    return "";
                }
            }
            return null;
        }
    };

}

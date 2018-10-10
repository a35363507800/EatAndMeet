package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.presenters.ImpIMySetPayPwManagerView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IGetSecurityCodeListener;
import com.echoesnet.eatandmeet.presenters.viewinterface.IGetVoiceCodeListener;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMySetPayPwManagerView;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlterDialogs.DialogWith2BtnAtBottom;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 支付密码管理
 */
public class MySetPayPwManagerAct extends BaseActivity implements IMySetPayPwManagerView
{
    private static final String TAG = MySetPayPwManagerAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBarSwitch topBar;
    @BindView(R.id.all_my_change_pw)
    AutoLinearLayout allMyChangePw;
    @BindView(R.id.all_my_forget_pw)
    AutoLinearLayout allMyForgetPw;

    private int MsgCountdown = 60;
    private int AudioCountdown = 60;
    private Timer MsgTimer;
    private Timer AudioTimer;
    private TimerTask ttMsgCountDown;
    private TimerTask ttAudioMsgCountDown;
    private TextView tvGetVcode;
    private TextView tvSoundTip;
    private TextView tvSoundCode;

    private Dialog pDialog;
    private Activity mContext;
    boolean flags = false;

    private ImpIMySetPayPwManagerView setPayPwManagerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_set_pay_pw_manager);
        ButterKnife.bind(this);
        afterViews();
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

    private void afterViews()
    {
        mContext = this;

        topBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                mContext.finish();
            }

            @Override
            public void right2Click(View view)
            {

            }
        }).setText("管理支付密码");


        MsgTimer = new Timer();
        AudioTimer = new Timer();
        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理");
        pDialog.setCancelable(false);
        setPayPwManagerView = new ImpIMySetPayPwManagerView(mContext, this);
        EamApplication.getInstance().actStake.add(this);
    }

    @OnClick({R.id.all_my_change_pw, R.id.all_my_forget_pw})
    void viewClick(View v)
    {
        switch (v.getId())
        {
            //修改支付密码
            case R.id.all_my_change_pw:
                Intent intent = new Intent(mContext,MySetVerifyOldPayPwAct.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                break;
            //忘记支付密码
            case R.id.all_my_forget_pw:
                verifyMobileNumber();
                break;
            default:
                break;
        }
    }

    /**
     * 验证手机号
     */
    private void verifyMobileNumber()
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_content_verify_mobile, null);
        TextView tvShowTxt = (TextView) view.findViewById(R.id.tv_show_text);
        tvSoundTip = (TextView) view.findViewById(R.id.tv_tip);
        tvSoundCode = (TextView) view.findViewById(R.id.tv_sound_code);
        final EditText tvSecurityCode = (EditText) view.findViewById(R.id.ew_modify_verify_code);
        tvGetVcode = (TextView) view.findViewById(R.id.tv_get_security_code);
        tvGetVcode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (tvGetVcode.isEnabled())
                {
                    tvGetVcode.setEnabled(false);
                    ttMsgCountDown = new SecurityCountDown();
                    tvGetVcode.setTextColor(ContextCompat.getColor(mContext, R.color.FC7));
                    MsgTimer.scheduleAtFixedRate(ttMsgCountDown, 0, 1000);
                    //发送验证码
                    NetHelper.getSecurityCodeMsg(mContext, SharePreUtils.getUserMobile(mContext), "4", null, new IGetSecurityCodeListener()
                    {
                        @Override
                        public void onSuccess()
                        {
                            ToastUtils.showShort("发送成功，验证码有效时间15分钟！");
                        }

                        @Override
                        public void onFailed(String errorCode)
                        {
                            resetGetSecurityButton();
                        }
                    });
                }
            }
        });
        tvSoundCode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (tvSoundCode.isEnabled())
                {
                    ttAudioMsgCountDown = new SoundCodeCountDown();
                    AudioTimer.scheduleAtFixedRate(ttAudioMsgCountDown, 0, 1000);
                    tvSoundTip.setText("电话正在接通中，请等候...");
                    tvSoundCode.setEnabled(false);
                    tvSoundCode.setTextColor(ContextCompat.getColor(mContext, R.color.C0323));
                    NetHelper.getVoiceCodeMsg(mContext, SharePreUtils.getUserMobile(mContext), "4", null, new IGetVoiceCodeListener()
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

            }
        });
        tvShowTxt.setText(String.format("请输入尾号为%s的手机接收到的短信验证码", SharePreUtils.getUserMobile(mContext).substring(7, 11)));
        new DialogWith2BtnAtBottom(mContext)
                .buildDialog(mContext)
                .setDialogTitle("验证手机号码",false)
                .setContent(view)
                .setCancelable(false)
                .setCancelBtnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        resetGetSecurityButton();
                        resetGetVoiceCodeButton();
                        if (ttMsgCountDown != null)
                            ttMsgCountDown.cancel();
                        if (ttAudioMsgCountDown != null)
                            ttAudioMsgCountDown.cancel();
                    }
                }).setCommitBtnClickListener(new DialogWith2BtnAtBottom.OnDialogWithPositiveBtnListener()
        {
            @Override
            public void onPositiveBtnClick(View view, Dialog dialog)
            {
                if (setPayPwManagerView != null)
                    if (TextUtils.isEmpty(tvSecurityCode.getText()))
                    {
                        ToastUtils.showShort("验证码不能为空");
                        return;
                    }
                    setPayPwManagerView.validSecurityCode(SharePreUtils.getUserMobile(mContext), tvSecurityCode.getText().toString(), dialog, "4");
            }
        }).show();
    }


    CountDownTimer countDownTimer = new CountDownTimer(60000, 1000)
    {
        @Override
        public void onTick(final long l)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    tvGetVcode.setEnabled(false);
                    tvGetVcode.setText("剩余" + l / 1000 + "秒");
                }
            });
        }

        @Override
        public void onFinish()
        {
            tvGetVcode.setText("获取验证码");
            tvGetVcode.setEnabled(true);
        }
    };

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {

    }

    @Override
    public void validSecurityCodeCallback(ArrayMap<String, Object> result)
    {
        String response = (String) result.get("response");
        Dialog dialog = (Dialog) result.get("dialog");
        Logger.t(TAG).d("获得的结果：" + response);
        //*******发布时放开*********
        //验证通过，设置支付密码  wb
        dialog.dismiss();
        Intent intent = new Intent(mContext,MyVerifyIdAct.class);
        intent.putExtra(EamConstant.EAM_VERIFY_ID_OPEN_SOURCE, "MySetPayPwManagerAct");
        startActivity(intent);

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
                    tvGetVcode.setText(String.format("剩余%s秒", String.valueOf(MsgCountdown--)));
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
        tvGetVcode.setText("获取验证码");
        tvGetVcode.setEnabled(true);
        tvGetVcode.setTextColor(ContextCompat.getColor(mContext, R.color.MC7));
        MsgCountdown = 60;
        if (ttMsgCountDown != null)
            ttMsgCountDown.cancel();
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
        tvSoundTip.setText(R.string.tryOtherStyle);
        tvSoundCode.setText(getString(R.string.voiceCode));
        tvSoundCode.setEnabled(true);
        tvSoundCode.setTextColor(ContextCompat.getColor(mContext, R.color.C0311));
        AudioCountdown = 60;
        if (ttAudioMsgCountDown != null)
            ttAudioMsgCountDown.cancel();
    }

    /**
     * 修改语音验证码倒计时
     */
    CountDownTimer countDownSoundTimer = new CountDownTimer(60000, 1000)
    {
        @Override
        public void onTick(final long l)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    tvSoundCode.setEnabled(false);
                    tvSoundCode.setText("剩余" + l / 1000 + "秒");
                }
            });
        }

        @Override
        public void onFinish()
        {
            tvSoundTip.setText(getString(R.string.tryOtherStyle));
            tvSoundCode.setText(getString(R.string.voiceCode));
            tvSoundCode.setEnabled(true);
            tvSoundCode.setTextColor(ContextCompat.getColor(mContext, R.color.C0311));
        }
    };

}

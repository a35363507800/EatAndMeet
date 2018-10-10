package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.presenters.ImpIWeChatMakeInfoView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IGetSecurityCodeListener;
import com.echoesnet.eatandmeet.presenters.viewinterface.IGetVoiceCodeListener;
import com.echoesnet.eatandmeet.presenters.viewinterface.IWeChatMakeInfoView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.IMUtils.IMHelper;
import com.echoesnet.eatandmeet.utils.IMUtils.TencentHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.hyphenate.EMError;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.DateTimePicker;
import cn.qqtheme.framework.widget.WheelView;
import okhttp3.Call;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author yqh
 * @version 1.0
 * @modifier ben
 * @createDate 2017/2/18
 * @description
 */
public class WeChatMakeUserInfo extends BaseActivity implements IWeChatMakeInfoView
{
    public static final String TAG = WeChatMakeUserInfo.class.getSimpleName();
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.riv_editu_head)
    RoundedImageView rivHead;
    @BindView(R.id.rl_male)
    RelativeLayout rlMale;
    @BindView(R.id.rl_female)
    RelativeLayout rlFeMale;
    @BindView(R.id.tv_profile_birthday)
    TextView tvBirthday;
    @BindView(R.id.ev_reg_phone_num)
    EditText evRegPhoneNum;
    @BindView(R.id.ew_register_verify_code)
    EditText ewRegisterVerifyCode;
    @BindView(R.id.btn_register_get_vcode)
    Button btnRegisterGetVcode;
    @BindView(R.id.tv_sound_code)
    TextView tvSoundCode;
    @BindView(R.id.tv_tip)
    TextView tvSoundTip;
    @BindView(R.id.btn_profile_ok)
    Button btnProfileOk;
    @BindView(R.id.itv_male)
    IconTextView itvMale;
    @BindView(R.id.itv_female)
    IconTextView itvFeMale;


    private Activity mAct;
    private int countdown = 60;
    private int Audiocountdown = 60;
    private Timer timer;
    private Timer AudioTimer;
    private TimerTask ttMsgCountDown;
    private TimerTask ttAudioMsgCountDown;
    //标示尝试注册环信的次数，如果大于3次则放弃注册（几率不大，不然环信可以扔了）
    private int commitCount = 0;
    private String genderStr;
    private String sex;
    private String phUrl;
    private String nicName;
    private String unionid;
    private ImpIWeChatMakeInfoView impIWeChatMakeInfoView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_wechat_make_user_info);
        ButterKnife.bind(this);

        initAfterView();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        IMHelper.getInstance().removeLoginFinishListener();
    }

    private void initAfterView()
    {
        mAct = this;
        topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
            }

            @Override
            public void right2Click(View view)
            {
            }
        }).setText("完善资料");
//        topBarSwitch.setBackground(ContextCompat.getDrawable(mAct, R.drawable.C0321));
        List<TextView> navBtns = topBarSwitch.getNavBtns(new int[]{1, 0, 0, 0});
        for (int i = 0; i < navBtns.size(); i++)
        {
            TextView tv = navBtns.get(i);
            if (i == 0)
            {
                tv.setVisibility(View.GONE);
            }
        }
        topBarSwitch.setBottomLineVisibility(View.VISIBLE);
        timer = new Timer();
        AudioTimer = new Timer();
        evRegPhoneNum.clearFocus();
        ewRegisterVerifyCode.clearFocus();
        sex = getIntent().getStringExtra("sex");
        phUrl = getIntent().getStringExtra("phUrl");
        nicName = getIntent().getStringExtra("nicName");

        if (nicName.length() > 7)
        {
            nicName = nicName.substring(0, 6) + "..";
        }
        unionid = getIntent().getStringExtra("unionid");

        genderStr = sex;
        impIWeChatMakeInfoView = new ImpIWeChatMakeInfoView(mAct, this);
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(phUrl)
                .centerCrop()
                .placeholder(R.drawable.userhead)
                .error(R.drawable.userhead)
                .into(rivHead);

        if ("男".equals(genderStr))
        {
            rlMale.setBackground(ContextCompat.getDrawable(mAct, R.drawable.male_pressed));
            itvMale.setTextColor(ContextCompat.getColor(mAct, R.color.white));
        }
        else
        {
            rlFeMale.setBackground(ContextCompat.getDrawable(mAct, R.drawable.female_pressed));
            itvFeMale.setTextColor(ContextCompat.getColor(mAct, R.color.white));
        }
    }

    @OnClick({R.id.btn_profile_ok, R.id.tv_profile_birthday, R.id.btn_register_get_vcode, R.id.tv_sound_code})
    void onViewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_profile_ok:
                String code = ewRegisterVerifyCode.getText().toString();
                if (TextUtils.isEmpty(tvBirthday.getText()))
                {
                    ToastUtils.showShort("请选择生日");
                    return;
                }
                if (!CommonUtils.verifyInput(3, evRegPhoneNum.getText().toString()))
                {
                    ToastUtils.showShort("请输入正确的手机号");
                    return;
                }
                if (TextUtils.isEmpty(ewRegisterVerifyCode.getText()))
                {
                    ToastUtils.showShort("请输入验证码");
                    return;
                }
                if (impIWeChatMakeInfoView != null)
                    impIWeChatMakeInfoView.validSecurityCode(evRegPhoneNum.getText().toString(), code, "5", "");

//                //测试使用，跳过验证码完善资料
//                if (impIWeChatMakeInfoView != null)
//                    impIWeChatMakeInfoView.weChatDetail(unionid, nicName, phUrl, genderStr, evRegPhoneNum.getText().toString(), tvBirthday.getText().toString());
                break;
            case R.id.tv_profile_birthday:
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(evRegPhoneNum.getWindowToken(), 0);
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ewRegisterVerifyCode.getWindowToken(), 0);
                showDatePicker(tvBirthday);
                break;
            case R.id.btn_register_get_vcode:
                if (!CommonUtils.verifyInput(3, evRegPhoneNum.getText().toString()))
                {
                    ToastUtils.showShort("请输入正确的手机号");
                    break;
                }
                if (btnRegisterGetVcode.isEnabled())
                {
                    Logger.t(TAG).d("执行了");
                    //发送验证码
                    NetHelper.getSecurityCodeMsg(mAct, evRegPhoneNum.getText().toString(), "5", "", new IGetSecurityCodeListener()
                    {
                        @Override
                        public void onSuccess()
                        {
                            ToastUtils.showShort("发送成功，验证码有效时间15分钟！");
                            btnRegisterGetVcode.setEnabled(false);
                            btnRegisterGetVcode.setTextColor(ContextCompat.getColor(mAct, R.color.FC7));
                            ttMsgCountDown = new SecurityCountDown();
                            timer.scheduleAtFixedRate(ttMsgCountDown, 0, 1000);
                        }

                        @Override
                        public void onFailed(String errorCode)
                        {
                            if (ErrorCodeTable.RES_OVER_TIMES.equals(errorCode))
                                ToastUtils.showShort("注册验证码失效超过十次，无法再次获得验证码");
                            else if (ErrorCodeTable.BINDED_WECHAT_NO_THIS.equals(errorCode))
                                ToastUtils.showShort(ErrorCodeTable.parseErrorCode(errorCode));
                            else
                                ToastUtils.showShort("验证码发送失败，请重试。");
                            resetGetSecurityButton();
                        }
                    });
                }
                break;
            case R.id.tv_sound_code:
                //语音验证码
                if (!CommonUtils.verifyInput(3, evRegPhoneNum.getText().toString()))
                {
                    ToastUtils.showShort("请输入正确的手机号");
                    break;
                }
                if (tvSoundCode.isEnabled())
                {
                    NetHelper.getVoiceCodeMsg(mAct, evRegPhoneNum.getText().toString(), "5", "", new IGetVoiceCodeListener()
                    {
                        @Override
                        public void onSuccess()
                        {
                            tvSoundTip.setText(getString(R.string.voiceCode));
                            tvSoundCode.setEnabled(false);
                            tvSoundCode.setTextColor(ContextCompat.getColor(mAct, R.color.C0323));
                            ttAudioMsgCountDown = new SoundCodeCountDown();
                            AudioTimer.scheduleAtFixedRate(ttAudioMsgCountDown, 0, 1000);
                        }

                        @Override
                        public void onFailed(String errorCode)
                        {
                            resetGetSoundCodeButton();
                        }
                    });
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed()
    {

    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {

    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mAct, null, exceptSource, e);
    }

    @Override
    public void validSecurityCodeCallback(String response)
    {
        SharePreUtils.setUserMobile(mAct, evRegPhoneNum.getText().toString());
        registerHuanXin();
    }

    @Override
    public void weChatDetailCallback(ArrayMap<String, Object> map)
    {
        String response = (String) map.get("response");
        String username = (String) map.get("username");
        String psw = (String) map.get("psw");

        Logger.t(TAG).d("完善资料返回结果" + response);
        try
        {
            JSONObject body = new JSONObject(response);
            String token = body.getString("token");
            String uId = body.getString("uId");
            String nicName = body.getString("nicName");
            String phUrl = body.getString("phUrl");
            String name = body.getString("name");
            String sign = body.getString("sign");
            //首次签到
            SharePreUtils.setFirst(mAct, body.getString("first"));
            SharePreUtils.setToken(mAct, token);
            SharePreUtils.setUId(mAct, uId);
            SharePreUtils.setId(mAct, body.getString("id"));
            SharePreUtils.setHeadImg(mAct, phUrl);
            SharePreUtils.setNicName(mAct, nicName);
            SharePreUtils.setUserMobile(mAct, evRegPhoneNum.getText().toString());
            //将环信账号保存到后台
            if (impIWeChatMakeInfoView != null)
            {
                Logger.t(TAG).d("调用addHXAccountToServerAndLogin》》》》》》》》1");
                impIWeChatMakeInfoView.addHXAccountToServerAndLogin(username, psw);
            }
            Logger.t(TAG).d("调用addHXAccountToServerAndLogin》》》》》》》》2");
            addTXAccountToLocalAndLogin(name, sign);

        } catch (JSONException e)
        {
            Logger.t(TAG).d("catch了:" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void addHXAccountToServerAndLoginCallback(ArrayMap<String, Object> map)
    {
        String userName = (String) map.get("userName");
        IMHelper.getInstance().setOnILoginFinishListener(new HxLoginFinished(this));
        //登录环信
        IMHelper.getInstance().huanXinLogin(mAct);
        SharePreUtils.setHxId(mAct, userName);

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
        SharePreUtils.setTlsName(mAct, tUserName);
        SharePreUtils.setTlsSign(mAct, tUserSign);
        //腾讯IM登录
        TencentHelper.txLogin(null);
    }

    /**
     * 先注册环信，然后再完善资料
     */
    private void registerHuanXin()
    {
        //region 注册环信
        final String userName = SharePreUtils.getUserMobile(mAct) + UUID.randomUUID().toString().toLowerCase().substring(0, 8);
        final String psw = UUID.randomUUID().toString().toLowerCase().substring(0, 6);
        IMHelper.getInstance().setOnIRegisterFinishedListener(new IMHelper.IHxRegisterFinishedListener()
        {
            @Override
            public void onSuccess(String userName, String psw)
            {
                Logger.t(TAG).d("注册环信成功，开始完善资料");
                if (impIWeChatMakeInfoView != null)
                    impIWeChatMakeInfoView.weChatDetail(unionid, nicName, phUrl, genderStr,
                            evRegPhoneNum.getText().toString(), tvBirthday.getText().toString(), userName, psw);
            }

            @Override
            public void onFailed(int errorCode)
            {
                if (errorCode == EMError.NETWORK_ERROR)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ToastUtils.showShort("注册失败，请检查网络后重试");
                        }
                    });
                }
                else if (errorCode == EMError.USER_ALREADY_EXIST)
                {
                    String userName = SharePreUtils.getUserMobile(mAct) + UUID.randomUUID().toString().toLowerCase().substring(0, 8);
                    String psw = UUID.randomUUID().toString().toLowerCase().substring(0, 6);
                    IMHelper.getInstance().register(userName, psw);
                }
                else if (errorCode == EMError.USER_AUTHENTICATION_FAILED)
                {
                    //Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                }
                else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT)
                {
                    // Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (commitCount <= 3)
                    {
                        commitCount++;
                        IMHelper.getInstance().register(userName, psw);
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                ToastUtils.showShort("注册失败，请重试");
                            }
                        });
                    }
                }
                if (commitCount > 3)
                {
                    if (impIWeChatMakeInfoView != null)
                        impIWeChatMakeInfoView.weChatDetail(unionid, nicName, phUrl, genderStr,
                                evRegPhoneNum.getText().toString(), tvBirthday.getText().toString(), userName, psw);
                }
                Logger.t(TAG).d("errorCode:" + errorCode);
            }
        });
        IMHelper.getInstance().register(userName, psw);
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
                    btnRegisterGetVcode.setText(String.format("(%s)s", String.valueOf(countdown--)));
                    if (countdown < 1)
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
        btnRegisterGetVcode.setText("发送验证码");
        btnRegisterGetVcode.setEnabled(true);
        btnRegisterGetVcode.setTextColor(ContextCompat.getColor(mAct, R.color.MC7));
        countdown = 60;
        if (ttMsgCountDown != null)
            ttMsgCountDown.cancel();
    }

    private static class HxLoginFinished implements IMHelper.IHxLoginFinishedListener
    {
        private final WeakReference<WeChatMakeUserInfo> mActRef;

        private HxLoginFinished(WeChatMakeUserInfo mAct)
        {
            this.mActRef = new WeakReference<>(mAct);
        }

        @Override
        public void onSuccess()
        {
            WeChatMakeUserInfo mAct = mActRef.get();
            if (mAct != null)
            {
                Logger.t(TAG).d("登录成功");
                Intent intent = new Intent(mAct, HomeAct.class);
                Logger.t(TAG).d("开始跳转");
                mAct.startActivity(intent);
                mAct.finish();
            }
        }

        @Override
        public void onFailed(int i, final String s)
        {
            final WeChatMakeUserInfo cAct = mActRef.get();
            if (cAct != null)
            {
                cAct.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ToastUtils.showShort("H登录失败：" + s);
                    }
                });

            }
        }
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
                    tvSoundCode.setText(String.format("剩余%s秒", String.valueOf(Audiocountdown--)));
                    if (Audiocountdown < 1)
                    {
                        resetGetSoundCodeButton();
                    }
                }
            });
        }
    }

    /**
     * 重置语音验证码获取按钮
     */
    private void resetGetSoundCodeButton()
    {
        tvSoundTip.setText(getString(R.string.tryOtherStyle));
        tvSoundCode.setText(getString(R.string.voiceCode));
        tvSoundCode.setEnabled(true);
        tvSoundCode.setTextColor(ContextCompat.getColor(mAct, R.color.C0311));
        Audiocountdown = 60;
        if (ttAudioMsgCountDown != null)
            ttAudioMsgCountDown.cancel();
        //timer.cancel();
        //timer.purge();
    }


    //可能会使用
    /*private void changeCheckBoxStyle()
    {
        Logger.t("注册").d(cbIsReadDisclaimer.toString()+"");
        if (cbIsReadDisclaimer.isChecked())
        {
            cbIsReadDisclaimer.setButtonDrawable(ContextCompat.getDrawable(mAct,R.drawable.check_box_p));
        }
        else
        {
            cbIsReadDisclaimer.setButtonDrawable(ContextCompat.getDrawable(mAct,R.drawable.check_box_n));
        }
    }*/

    private void showDatePicker(final TextView showDateView)
    {
        DatePicker picker = new DatePicker(mAct, DateTimePicker.YEAR_MONTH_DAY);
        picker.setCycleDisable(false);
        picker.setLineVisible(true);
        picker.setTopLineVisible(false);
        picker.setShadowVisible(false);
        picker.setTitleText("选择日期");
        picker.setTitleTextSize(14);
        picker.setTitleTextColor(ContextCompat.getColor(mAct, R.color.C0321));
        picker.setCancelTextColor(ContextCompat.getColor(mAct, R.color.C0311));
        picker.setSubmitTextColor(ContextCompat.getColor(mAct, R.color.C0311));
        picker.setTopLineVisible(false);
        picker.setRangeStart(Calendar.getInstance().get(Calendar.YEAR) - 99, 1, 1);
        picker.setRangeEnd(Calendar.getInstance().get(Calendar.YEAR) - 10, 12, 31);
        picker.setSelectedItem(Calendar.getInstance().get(Calendar.YEAR) - 10, 1, 1);
        WheelView.LineConfig config1 = new WheelView.LineConfig();
        config1.setColor(0xFF33B5E5);//线颜色
        config1.setAlpha(140);//线透明度
        config1.setRatio((float) (1.0));//线比率
        picker.setLineConfig(config1);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener()
        {
            @Override
            public void onDatePicked(String year, String month, String day)
            {
                showDateView.setText(year + "-" + month + "-" + day);
                showDateView.setTextColor(ContextCompat.getColor(mAct, R.color.c3));
            }
        });
        picker.show();
    }
}

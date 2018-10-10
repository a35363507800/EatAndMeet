package com.echoesnet.eatandmeet.activities.live;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.MVPBaseActivity;
import com.echoesnet.eatandmeet.presenters.ImpIWithDrawView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IWithDrawView;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mob.MobSDK;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import okhttp3.Call;

public class LiveWithDrawActivity extends MVPBaseActivity<IWithDrawView, ImpIWithDrawView> implements IWithDrawView
{
    private static final String TAG = LiveWithDrawActivity.class.getSimpleName();
    @BindView(R.id.ll_no_bind)
    LinearLayout llNoBind;
    @BindView(R.id.ll_withDraw)
    LinearLayout llWithDraw;
    @BindView(R.id.riv_head)
    RoundedImageView rivHead;
    @BindView(R.id.tv_nickname)
    TextView tvNicName;
    @BindView(R.id.et_draw_money)
    EditText etDrawMoney;

    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;

    @BindView(R.id.btn_bind_ok)
    Button btn_bind_ok;
    @BindView(R.id.btn_withDraw_ok)
    Button btnWithDrawOk;
    private Platform weChat;
    private Activity mContext;
    private Dialog pDialog;
    private String canWithDrawFanPage = "0";//可提现金额
    private String etWithDrawMoney;//提现金额

    private double MIN_MARK = 0;
    private long MAX_MARK = 0;//可输入最大提现金额  单位 分
    private String prompt = "";
    private String userRate = "";
    private String platformRate = "";
    private int status;
    private String nicName = "";
    private String headImgUrl = "";
    private TextView titlev;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_with_draw_act);
        ButterKnife.bind(this);
        initAfterViews();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    private void initAfterViews()
    {
        mContext = this;
        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理...");
        pDialog.setCancelable(false);
        this.titlev = topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
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
        this.titlev.setText("提现");


        MobSDK.init(this, EamConstant.SHARESDK_APPKEY, EamConstant.SHARESDK_APPSECRET);
        canWithDrawFanPage = getIntent().getStringExtra("canWithDrawFanPage");
        userRate = getIntent().getStringExtra("userRate");
        platformRate = getIntent().getStringExtra("platformRate");
        status = getIntent().getIntExtra("status", 1);
        nicName = getIntent().getStringExtra("nicName");
        headImgUrl = getIntent().getStringExtra("headImgUrl");
        etDrawMoney.setHint("" + canWithDrawFanPage);

        double a = 0;
        try
        {
            a = Double.parseDouble(canWithDrawFanPage) * 100;
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        DecimalFormat format = new DecimalFormat();
        String money = format.format(a).replace(",", "");
        Logger.t(TAG).d("format.format(a):" + money);
        MAX_MARK = Long.parseLong(money); //变为分


        etDrawMoney.addTextChangedListener(watcher);
        setUIWithStats(status + "");

        if (status == 0)
        {
            GlideApp.with(mContext)
                    .asBitmap()
                    .load(headImgUrl)
                    .placeholder(R.drawable.userhead)
                    .into(rivHead);
            tvNicName.setText(nicName);
        }
    }


    private void showTixianDialog()
    {
        if (!mContext.isFinishing())
        {
            ToastUtils.setGravity(Gravity.CENTER, 0, 0);
            ToastUtils.showCustomShortSafe(LayoutInflater.from(mContext).inflate(R.layout.dialog_tixian_alert, null));
            ToastUtils.cancel();
        }

        /*final Dialog dialog = new Dialog(mContext, R.style.dialogBg);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_tixian_alert, null);
        dialog.setContentView(view);
        dialog.show();
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (!isFinishing() && dialog.isShowing())
                    dialog.dismiss();
            }
        }, 2000);*/
    }


    /**
     * 通过获得的状态来显示UI
     *
     * @param stats
     */
    private void setUIWithStats(String stats)
    {
        //已绑定
        if (stats.equals("0"))
        {
            titlev.setText("提 现");
            llWithDraw.setVisibility(View.VISIBLE);
            llNoBind.setVisibility(View.GONE);
        }
        else
        {
            titlev.setText("绑定微信");
            llWithDraw.setVisibility(View.GONE);
            llNoBind.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.btn_bind_ok, R.id.btn_withDraw_ok})
    void clickListener(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_bind_ok:
                //三方登录微信
                weChat = ShareSDK.getPlatform(Wechat.NAME);
                if (weChat.isClientValid())
                {
                    weChat.setPlatformActionListener(paListener);
//                if (weChat.isAuthValid())
//                {
//                    //微信是否授权登录了
//                    weChat.removeAccount(true);
//                }
                    weChat.SSOSetting(false);
                    weChat.isAuthValid();
                    weChat.authorize();
//                weChat.showUser(null);
                }
                else
                    ToastUtils.showShort("请先安装微信客户端~");
                break;
            case R.id.btn_withDraw_ok:
                etWithDrawMoney = etDrawMoney.getText().toString();
                if (!TextUtils.isEmpty(etWithDrawMoney))
                {
//
                    double a = Double.parseDouble(etWithDrawMoney) * 100;
                    DecimalFormat format = new DecimalFormat();
                    String money = format.format(a).replace(",", "");//format.format(a)：1,1000,000 附带 ","  去除
                    long inputMoney = Long.parseLong(money);

                    if (inputMoney >= 1 * 100 && inputMoney <= 200 * 100)
                    {
                        if (inputMoney <= MAX_MARK * 100)
                        {
                            new CustomAlertDialog(mContext)
                                    .builder()
                                    .setMsg(String.format("收益余额提现时比列为：\r\n主播%s平台%s，每日最多提现1次", userRate + "%", platformRate + "%"))
                                    .setPositiveButton("果断提现", new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            if (!mContext.isFinishing() && pDialog != null && !pDialog.isShowing())
                                                pDialog.show();
                                            if (mPresenter != null)
                                                mPresenter.withDrawToWeChat(etWithDrawMoney);
                                        }
                                    })
                                    .setNegativeButton("容我三思", new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View v)
                                        {

                                        }
                                    }).show();
                        }
                        else
                        {
                            ToastUtils.showShort("输入金额超出可提现金额");
                        }

                    }
                    else
                    {
                        ToastUtils.showShort("请输入1-200金额");
                    }
                }
                else
                {
                    ToastUtils.showShort("请输入提现金额");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 微信授权成功后与后台绑定
     *
     * @param platform
     */
    private void LoginWeChatSuccess(Platform platform)
    {
        GlideApp.with(mContext)
                .asBitmap()
                .load(platform.getDb().getUserIcon())
                .placeholder(R.drawable.userhead)
                .into(rivHead);
        tvNicName.setText(platform.getDb().getUserName());
        if (!mContext.isFinishing() && pDialog != null && !pDialog.isShowing())
            pDialog.show();
        if (mPresenter != null)
            mPresenter.loginWeChatSuccess(platform);
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
                    Logger.t(TAG).d("exportData" + platform.getDb().exportData());
                    LoginWeChatSuccess(platform);
                }
            });
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable)
        {
            Logger.t(TAG).d("error:" + throwable.getMessage());
        }

        @Override
        public void onCancel(Platform platform, int i)
        {
            Logger.t(TAG).d("cancle" + i);
        }
    };

    @Override
    protected ImpIWithDrawView createPresenter()
    {
        return new ImpIWithDrawView();
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mContext, "", exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
//        switch (interfaceName)
//        {
//
//        }
    }


    @Override
    public void withDrawToWeChatCallBack(String response)
    {
        Logger.t(TAG).d("返回结果:" + response);
        showTixianDialog();
        Intent intent = new Intent(EamConstant.ACTION_UPDATE_USER_BALANCE);
        intent.putExtra("needRefreshAccountInfo", true);
        sendBroadcast(intent);
        if (mPresenter != null)
            mPresenter.getAccountBalance();
        if (!mContext.isFinishing() && pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void loginWeChatSuccessCallBack(String response)
    {
        setUIWithStats("0");
        if (!mContext.isFinishing() && pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getAccountBalanceCallBack(String response)
    {
        Logger.t(TAG).d("账户余额返回信息--> " + response);
        try
        {
            JSONObject jsonBody = new JSONObject(response);
            String balance = jsonBody.getString("balance");
            //发送广播，通知直播账户相关余额已经改变
            Intent intent1 = new Intent(EamConstant.ACTION_UPDATE_BALANCE);
            intent1.putExtra("balance", balance);
            sendBroadcast(intent1);
            if (!mContext.isFinishing() && pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
            finish();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    TextWatcher watcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            if (!s.toString().contains("."))
            {
                if (s.toString().length() > 6)
                {
                    s = s.toString().substring(0, s.toString().length() - 1);
                    etDrawMoney.setText(s);
                    etDrawMoney.setSelection(s.length());
                }
            }

            if (s.toString().contains("."))
            {
                if (s.length() - 1 - s.toString().indexOf(".") > 2)
                {
                    s = s.toString().subSequence(0,
                            s.toString().indexOf(".") + 3);
                    etDrawMoney.setText(s);
                    etDrawMoney.setSelection(s.length());
                }
            }
            if (s.toString().trim().equals("."))
            {
                s = "0" + s;
                etDrawMoney.setText(s);
                etDrawMoney.setSelection(2);
            }

            if (s.toString().startsWith("0")
                    && s.toString().trim().length() > 1)
            {
                if (!s.toString().substring(1, 2).equals("."))
                {
                    etDrawMoney.setText(s.subSequence(0, 1));
                    etDrawMoney.setSelection(1);
                    return;
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s)
        {
        }
    };

}

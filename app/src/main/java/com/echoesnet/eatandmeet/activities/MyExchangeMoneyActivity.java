package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.live.LiveWithDrawActivity;
import com.echoesnet.eatandmeet.presenters.ImpIExchangeMoneyView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IExchangeMoneyView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

//饭票页面
public class MyExchangeMoneyActivity extends BaseActivity implements IExchangeMoneyView
{
    private static final String TAG = LiveWithDrawActivity.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBarSwitch topBar;
    @BindView(R.id.tv_canUse_fanPage)
    TextView tvCanUseFanPage;
    @BindView(R.id.tv_canWithDraw_fan_page)
    TextView tvCanWithDrawMoney;
    @BindView(R.id.btn_withDraw_ok)
    Button btnWithDrawOk;
    @BindView(R.id.et_draw_money)
    EditText etDrawMoney;


    private Activity mAct;
    private Dialog pDialog;
    private ImpIExchangeMoneyView exchangeMoneyView;
    private String isSign = "";
    private String rate;
    private String money;
    private String meal;
    private String mealTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_info_exchange_money);
        ButterKnife.bind(this);
        initAfterViews();
    }

    private void initAfterViews()
    {
        mAct = this;
        topBar = (TopBarSwitch) findViewById(R.id.top_bar);
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
                Intent intent = new Intent(mAct, ExchangeRecordDetailActivity.class);
                mAct.startActivity(intent);
            }
        }).setText("收 益");
        List<TextView> navBtns = topBar.getNavBtns(new int[]{1, 0, 0, 1});
        for (int i = 0; i < navBtns.size(); i++)
        {
            TextView tv = navBtns.get(i);
            if (i == 1)
            {
                tv.setText("明细");
                tv.setTextColor(ContextCompat.getColor(mAct, R.color.C0322));
                tv.setTextSize(16);
            }
        }
        pDialog = DialogUtil.getCommonDialog(mAct, "正在获取");
        pDialog.setCancelable(false);
        exchangeMoneyView = new ImpIExchangeMoneyView(mAct, this);
        exchangeMoneyView.getMyMeal();
        pDialog.show();
        etDrawMoney.addTextChangedListener(textWatcher);
    }

    @OnClick({R.id.btn_withDraw_ok, R.id.yuan})
    void clickListener(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_withDraw_ok:
            {
                final String money = etDrawMoney.getText().toString();
                if (TextUtils.isEmpty(money))
                {
                    ToastUtils.showShort("请输入金额");
                    return;
                }
                if (Double.parseDouble(money) < 1)
                {
                    ToastUtils.showShort("兑换金额最低为1元");
                    return;
                }

                if (!TextUtils.isEmpty(money))
                {
                    if (Double.parseDouble(money) > Double.parseDouble(this.money))
                    {
                        ToastUtils.showShort("输入金额超过可兑换金额");
                        return;
                    }
                }


                String rateText = "";
                if (!TextUtils.isEmpty(isSign))
                {
                    if (isSign.equals("1"))
                        rateText = "根据平台规定收益的60%归属用户，40%归属平台，请问您确定兑换吗？";
                    else
                        rateText = "根据平台规定主播与平台各获得可用收益的50%，请问您确定兑换吗？";
                }
                else
                {
                    int status = NetHelper.getNetworkStatus(mAct);
                    if (status == -1)
                    {
                        ToastUtils.showShort("当前无网络连接，请检测您的网络环境");
                    }
                    return;
                }
                Logger.t(TAG).d("测试rateText--> " + rateText + " , isSign--> " + isSign);
                new CustomAlertDialog(mAct)
                        .builder()
                        .setTitle("温馨提示")
                        .setMsg(rateText)
//                            .setPositiveButtonBackgroundColor(Color.WHITE)
                        .setPositiveButton("果断兑换", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                if (exchangeMoneyView != null)
                                    exchangeMoneyView.exchangeToBalance(CommonUtils.keep2Decimal(Double.parseDouble(money)));
                            }
                        })
                        .setNegativeButton("容我三思", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {

                            }
                        })
                        .show();
            }
            break;
            case R.id.yuan:
            {
                final String money = this.money;
                etDrawMoney.setText(CommonUtils.keep2Decimal(Double.parseDouble(money)));
            }
            break;
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (!mAct.isFinishing() && pDialog != null && !pDialog.isShowing())
            pDialog.show();
        if (exchangeMoneyView != null)
        {
            exchangeMoneyView.getMyMeal();
        }
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mAct, "", exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getMyMealCallBack(String response)
    {
        Logger.t(TAG).d("可兑换饭票返回结果:" + response);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        try
        {
            JSONObject object = new JSONObject(response);
            this.mealTotal = object.getString("mealTotal");
            this.meal = object.getString("meal");
            this.money = object.getString("money");
            this.isSign = object.getString("isSignedAnchor");

            if (isSign.equals("0"))
                this.rate = object.getString("userRate");
            else
                rate = object.getString("platformRate");

            tvCanUseFanPage.setText(this.mealTotal);
            tvCanWithDrawMoney.setText(this.meal);
            etDrawMoney.setHint(CommonUtils.keep2Decimal(Double.parseDouble(money)));
            //  etDrawMoney.setHint("当前可兑换￥" + CommonUtils.keep2Decimal(Double.parseDouble(money)));

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void exchangeToBalanceCallBack(String response)
    {
        Logger.t(TAG).d("饭票兑换到余额返回结果：" + response);
        //弹出居中黑色半透明圆角Dialog  延迟两秒消失
        CommonUtils.showTixianDialog(mAct, "提现成功");
        etDrawMoney.setText("");
        if (exchangeMoneyView != null)
            exchangeMoneyView.getMyMeal();
    }

    private TextWatcher textWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
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
            if (s.toString().trim().substring(0).equals("."))
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

            if (TextUtils.isEmpty(etDrawMoney.getText().toString().trim()))
            {
                btnWithDrawOk.setBackgroundResource(R.drawable.egg_bt_bg0331);
                btnWithDrawOk.setEnabled(false);
            }
            else
            {
                btnWithDrawOk.setBackgroundResource(R.drawable.shouyi_bt_bg);
                btnWithDrawOk.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s)
        {

        }
    };

}

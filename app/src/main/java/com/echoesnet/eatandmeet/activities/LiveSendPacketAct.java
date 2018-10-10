package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.LiveRedPacketBean;
import com.echoesnet.eatandmeet.models.bean.PayBean;
import com.echoesnet.eatandmeet.models.bean.PayMetadataBean;
import com.echoesnet.eatandmeet.presenters.ImpILiveSendRedPacketView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ILiveSendPacketView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.LoadingProgressUtil;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.payUtil.IHsPayPendingListener;
import com.echoesnet.eatandmeet.utils.payUtil.IPayFinishedListener;
import com.echoesnet.eatandmeet.utils.payUtil.PayHelper;
import com.echoesnet.eatandmeet.views.widgets.ChooseMoneyLayout;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joanzapata.iconify.widget.IconTextView;
import com.jungly.gridpasswordview.GridPasswordView;
import com.orhanobut.logger.Logger;
import com.pingplusplus.android.Pingpp;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @version 1.0
 * @modifier zdw
 * @createDate 2017/6/15
 * @description 直播中群发红包相关业务
 */
public class LiveSendPacketAct extends MVPBaseActivity<LiveSendPacketAct, ImpILiveSendRedPacketView> implements ILiveSendPacketView
{
    private static final String TAG = LiveSendPacketAct.class.getSimpleName();
    private Activity mActivity;
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    private List<LiveRedPacketBean> list;
    @BindView(R.id.loading_view)
    RelativeLayout rlLoadingView;
    @BindView(R.id.cml_layout)
    ChooseMoneyLayout chooseMoneyLayout;
    private double money; //当前选择的金额
    @BindView(R.id.btn_send_red_packet)
    Button btnSendRedPacket;
    @BindView(R.id.et_gift_money_amount)
    EditText etSendRedPacketCount;
    @BindView(R.id.tv_money_amount)
    TextView tvMoneyAmount;
    @BindView(R.id.tv_explain)
    TextView tvExplain;
    @BindView(R.id.tv_amount_hint)
    TextView tvAmountHint;
    private String roomId;
    private int redPacketCount = 1; // 红包个数
    private String payAmount;
    private AlphaAnimation appearAnimation, disAppAnimation;
    private static final int max = 100; // 红包最大个数

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_live_red_packet);
        ButterKnife.bind(this);
        initView();
    }

    private void initView()
    {
        this.mActivity = this;
        roomId = getIntent().getStringExtra("roomId");
        topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                mActivity.finish();
            }

            @Override
            public void right2Click(View view)
            {

            }
        }).setText("发红包");
        topBarSwitch.setBottomLineVisibility(View.VISIBLE);
        List<TextView> navBtn = topBarSwitch.getNavBtns(new int[]{1, 0, 0, 0});
        for (int i = 0; i < navBtn.size(); i++)
        {
            TextView tv = navBtn.get(i);
            if (i == 0)
                tv.setVisibility(View.VISIBLE);
        }

        etSendRedPacketCount.addTextChangedListener(textWatcher);
        if (mPresenter != null)
        {
            LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, true, 0, null);
            mPresenter.requestRedPacket();
        }
        // 属性动画
        appearAnimation = new AlphaAnimation(0, 1);//动画逐渐显现
        appearAnimation.setDuration(3000);//持续时间
        appearAnimation.setAnimationListener(new Animation.AnimationListener()
        {

            @Override
            public void onAnimationStart(Animation arg0)
            {
            }

            @Override
            public void onAnimationRepeat(Animation arg0)
            {
            }

            @Override
            public void onAnimationEnd(Animation arg0)
            {
                tvAmountHint.setVisibility(View.VISIBLE);
            }
        });

        disAppAnimation = new AlphaAnimation(1, 0);//动画逐渐消失
        disAppAnimation.setDuration(3000);
        disAppAnimation.setAnimationListener(new Animation.AnimationListener()
        {

            @Override
            public void onAnimationStart(Animation arg0)
            {
            }

            @Override
            public void onAnimationRepeat(Animation arg0)
            {
            }

            @Override
            public void onAnimationEnd(Animation arg0)
            {
                tvAmountHint.setVisibility(View.GONE);
            }
        });
    }

    // EditText 文本改变
    private TextWatcher textWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {
            redPacketCount = 1;
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {
            if (!charSequence.toString().isEmpty())
            {
                int value = Integer.parseInt(charSequence.toString());
                // 先选定金额
                if (!tvMoneyAmount.getText().equals("0.00"))
                {
                    // 当输入的值小于1时
                    if (value < 1)
                    {
                        tvAmountHint.setText("请输入红包个数");
                        tvAmountHint.setVisibility(View.VISIBLE);
                        tvAmountHint.setAnimation(appearAnimation);
                        tvAmountHint.startAnimation(appearAnimation);
                        tvAmountHint.setAnimation(disAppAnimation);
                        tvAmountHint.startAnimation(disAppAnimation);
                    }
                    else if (value > max)// 当输入的值大于100
                    {
                        etSendRedPacketCount.setText(String.valueOf(max));
                        redPacketCount = max;
                        tvAmountHint.setText("红包个数最多100个");
                        tvAmountHint.setVisibility(View.VISIBLE);
                        tvAmountHint.setAnimation(appearAnimation);
                        tvAmountHint.startAnimation(appearAnimation);
                        tvAmountHint.setAnimation(disAppAnimation);
                        tvAmountHint.startAnimation(disAppAnimation);
                    }
                    else// 当输入的值在1和100之间
                    {
                        redPacketCount = value;
                    }
                }
                else
                {
                    if (value < 1)
                    {
                        redPacketCount=1;
                    }
                    else if (value > max)
                    {
                        redPacketCount = max;
                        tvAmountHint.setText("红包个数最多100个");
                        tvAmountHint.setVisibility(View.VISIBLE);
                        tvAmountHint.setAnimation(appearAnimation);
                        tvAmountHint.startAnimation(appearAnimation);
                        tvAmountHint.setAnimation(disAppAnimation);
                        tvAmountHint.startAnimation(disAppAnimation);
                    }
                    else
                    {
                        redPacketCount = value;
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable)
        {
        }
    };

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.LiveC_groupRed:
                if (ErrorCodeTable.PAYPWD_ERR.equals(code))
                {
                    try
                    {
                        JSONObject jsonObject = new JSONObject(errBody);
                        String most = jsonObject.getString("most");
                        String surplus = jsonObject.getString("surplus");
                        PayHelper.clearPayPassword(mActivity);
                        ToastUtils.showShort("支付密码错误，您还可以输入" + surplus + "次");
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }else
                {
                    PayHelper.clearPopupWindows();
                }
                break;
        }
    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, true, 1, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mPresenter != null)
                    mPresenter.requestRedPacket();
            }
        });
    }

    @Override
    public void requestLiveRedPacket(String response)
    {
        if (response != null)
        {
            list = new Gson().fromJson(response, new TypeToken<List<LiveRedPacketBean>>()
            {
            }.getType());
            if (list != null && list.size() > 0)
            {
                btnSendRedPacket.setClickable(false);
                chooseMoneyLayout.setRedMoney(list);
                //设置默认选中项
                chooseMoneyLayout.setDefaultPosition(-1);
                //金额选择监听
                chooseMoneyLayout.setOnChoseMoneyListener(new ChooseMoneyLayout.onChoseMoneyListener()
                {
                    @Override
                    public void chooseMoney(int position, boolean isCheck, double moneyNum)
                    {
                        if (isCheck)
                        {
                            money = moneyNum;
                            tvMoneyAmount.setText(CommonUtils.keep2Decimal(money));
                            btnSendRedPacket.setClickable(true);
                            btnSendRedPacket.setBackground(getResources().getDrawable(R.drawable.round_corner_36_c0313_selector));
                        }
                        else
                        {
                            money = 0.00;
//                            tvMoneyAmount.setText(CommonUtils.keep2Decimal(money));
                        }
                    }
                });
            }
        }
        LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, false, 0, null);
    }

    @Override
    protected ImpILiveSendRedPacketView createPresenter()
    {
        return new ImpILiveSendRedPacketView();
    }

    @OnClick({R.id.btn_send_red_packet, R.id.tv_explain})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_send_red_packet:
                if (TextUtils.isEmpty(etSendRedPacketCount.getText().toString().trim()))
                {
                    ToastUtils.showShort("请输入红包个数");
                    return;
                }
                else if ("0".equals(etSendRedPacketCount.getText().toString().trim()))
                {
                    ToastUtils.showShort("请输入红包个数");
                    return;
                }
                else
                {
                    if (Double.parseDouble(tvMoneyAmount.getText().toString()) / redPacketCount < 0.01)
                    {
                        tvAmountHint.setText(getResources().getText(R.string.red_packet_hint));
                        tvAmountHint.setVisibility(View.VISIBLE);
                        tvAmountHint.setAnimation(appearAnimation);
                        tvAmountHint.startAnimation(appearAnimation);
                        tvAmountHint.setAnimation(disAppAnimation);
                        tvAmountHint.startAnimation(disAppAnimation);
                        return;
                    }
                    if (Integer.parseInt(etSendRedPacketCount.getText().toString().trim()) > 100)
                    {
                        tvAmountHint.setText("红包个数最多100个");
                        tvAmountHint.setVisibility(View.VISIBLE);
                        tvAmountHint.setAnimation(appearAnimation);
                        tvAmountHint.startAnimation(appearAnimation);
                        tvAmountHint.setAnimation(disAppAnimation);
                        tvAmountHint.startAnimation(disAppAnimation);
                        return;
                    }
                }
                payAmount = tvMoneyAmount.getText().toString();
                Logger.t(TAG).d("红包金额--> " + payAmount + " , 红包个数--> " + redPacketCount);
                PayHelper.clearPayHelperListeners();
                //用汇昇币支付触发
                PayHelper.setIHsPayPendingListener(new PendingPayFinish(this));
                PayBean payBean = new PayBean();
                payBean.setOrderId("");
                payBean.setMyPayType(EamConstant.EAM_PAY_PENDING);
                payBean.setAmount(payAmount);
                payBean.setSubject("看脸吃饭群发红包");               // 商品的标题
                payBean.setBody("看脸吃饭群发红包");                  // 商品的描述信息
                PayHelper.payOrder(btnSendRedPacket, payBean, mActivity,
                        new PayMetadataBean(payAmount, "", "", "6", String.valueOf(redPacketCount), "", roomId));
                break;
            case R.id.tv_explain:
                showRedExplain();
                break;
        }
    }

    @Override
    public void sendRedPackageByServer(String response, GridPasswordView gridPasswordView)
    {
        if (response != null)
        {
            Logger.t(TAG).d("发群红包返回结果--> " + response + " , " + response);
            String surplus = "";
            try
            {
                Intent intent = new Intent();
                JSONObject jsonObject = new JSONObject(response);
                String streamId = jsonObject.getString("streamId");
                Map<String, String> map = new HashMap<>();
                map.put("streamId", streamId);
                intent.putExtra("streamId", new Gson().toJson(map));
                setResult(RESULT_OK, intent);
                finish();
            } catch (JSONException e)
            {
                Logger.t(TAG).d(">>" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Logger.t(TAG).d("pay requestCode==" + requestCode + "pay resultCode==" + resultCode);
        //支付页面返回处理
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                String result = data.getExtras().getString("pay_result");
                Logger.t(TAG).d("pay result》" + result);
                if (result.equals("success"))
                {
                    PayHelper.setIPayFinishedListener(new PayFinish(this));
                    PayHelper.thirdPartyPayStateCheck2(mActivity, new PayMetadataBean(payAmount, "", "", "6", String.valueOf(redPacketCount), "", roomId));
                }
                else if (result.equals("cancel"))
                {
                    ToastUtils.showShort("支付取消");
                }
                else
                {
                    ToastUtils.showShort("支付失败, 请重试");
                }
            }
        }
    }

    //余额支付
    private static class PendingPayFinish implements IHsPayPendingListener
    {
        private final WeakReference<LiveSendPacketAct> mActRef;

        private PendingPayFinish(LiveSendPacketAct mAct)
        {
            this.mActRef = new WeakReference<LiveSendPacketAct>(mAct);
        }

        @Override
        public void payPending(String passWord, GridPasswordView gridPasswordView)
        {
            final LiveSendPacketAct cAct = mActRef.get();
            Logger.t(TAG).d("余额支付触发");
            if (cAct != null)
            {
                cAct.mPresenter.sendRedPackageByServer("0", String.valueOf(Double.parseDouble(cAct.payAmount)), "",
                        passWord, String.valueOf(cAct.redPacketCount), cAct.roomId, gridPasswordView);
            }
        }
    }

    //第三方支付
    private static class PayFinish implements IPayFinishedListener
    {
        private final WeakReference<LiveSendPacketAct> mFrgRef;

        private PayFinish(LiveSendPacketAct mAct)
        {
            this.mFrgRef = new WeakReference<LiveSendPacketAct>(mAct);
        }

        @Override
        public void PayFinished(String orderId, String streamId, String payType)
        {
            String payTypeR = "none";
            if (payType.equals("alipay") || payType.equals("alipay_wap"))
            {
                payTypeR = "1";
            }
            else if (payType.equals("wx"))
                payTypeR = "2";
            else
                payTypeR = "3";
            final LiveSendPacketAct cAct = mFrgRef.get();
            if (cAct != null)
            {
                Logger.t(TAG).d("pay finished");
                PayHelper.clearPopupWindows();
                Intent intent = new Intent();
                Map<String, String> map = new HashMap<>();
                map.put("streamId", streamId);
                intent.putExtra("streamId", new Gson().toJson(map));
//                intent.putExtra("streamId", streamId);
                cAct.setResult(RESULT_OK, intent);
                cAct.finish();
            }
        }

        @Override
        public void PayFailed(String orderId, String streamId, String payType)
        {
            final LiveSendPacketAct mAct = mFrgRef.get();
            if (mAct != null)
            {
                Logger.t(TAG).d("pay failed");
                ToastUtils.showShort("支付失败，请稍后重试！");
            }
        }
    }

    /**
     * 红包规则说明弹出层
     */
    private void showRedExplain()
    {
        final Dialog dialog = new Dialog(mActivity, R.style.AlertDialogStyle);
        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_live_red_packet_explain, null);
        dialog.setContentView(contentView);

        TextView redExplain = (TextView) contentView.findViewById(R.id.tv_red_explain);
        redExplain.setText(getString(R.string.redpacket_explain));
        TextView redRule = (TextView) contentView.findViewById(R.id.tv_red_rule);
        redRule.setText(getString(R.string.redpacket_rule));

        IconTextView itvClose = (IconTextView) contentView.findViewById(R.id.itv_close);
        itvClose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setCancelable(false);
    }


}

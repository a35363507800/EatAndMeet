package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.PayBean;
import com.echoesnet.eatandmeet.models.bean.PayMetadataBean;
import com.echoesnet.eatandmeet.presenters.ImpCSendRedPacketView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICSendRedPacketView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.payUtil.IHsPayPendingListener;
import com.echoesnet.eatandmeet.utils.payUtil.IPayFinishedListener;
import com.echoesnet.eatandmeet.utils.payUtil.PayHelper;
import com.echoesnet.eatandmeet.utils.redPacket.RedPacketConstant;
import com.echoesnet.eatandmeet.utils.redPacket.RedPacketInfo;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.jungly.gridpasswordview.GridPasswordView;
import com.orhanobut.logger.Logger;
import com.pingplusplus.android.Pingpp;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate
 * @description 发送红包界面
 */
public class CRPsendRedPacketAct extends MVPBaseActivity<ICSendRedPacketView, ImpCSendRedPacketView> implements ICSendRedPacketView
{
    private static final String TAG = CRPsendRedPacketAct.class.getSimpleName();
    //region 变量
    @BindView(R.id.top_bar)
    TopBarSwitch topBar;
    @BindView(R.id.et_gift_money_amount)
    EditText etGiftMoney;
    @BindView(R.id.btn_send_gift)
    Button btnSendGift;
    @BindView(R.id.all_gift_view)
    LinearLayout allGiftView;
    @BindView(R.id.tv_to_user)
    TextView tvToUser;
    @BindView(R.id.tv_money_amount)
    TextView tvMoneyAmount;
    RedPacketInfo redPacketInfo;

    private Dialog pDialog;
    private Activity mAct;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_crpsend_red_packet);
        ButterKnife.bind(this);
        afterViews();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //用汇昇币支付触发
        PayHelper.setIHsPayPendingListener(new PendingPayFinish(this));
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
        PayHelper.clearPopupWindows();
    }

    @Override
    public void onBackPressed()
    {
        mAct.setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private void afterViews()
    {
        mAct = this;
        TextView title = topBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                onBackPressed();
            }

            @Override
            public void right2Click(View view)
            {

            }
        });
        title.setText("发红包");
        title.setTypeface(null, Typeface.BOLD);


        redPacketInfo = getIntent().getParcelableExtra(RedPacketConstant.EXTRA_MONEY_INFO);
        tvToUser.setText(TextUtils.isEmpty(redPacketInfo.toRemark) ? redPacketInfo.toNickName : redPacketInfo.toRemark);

        etGiftMoney.addTextChangedListener(watcher);

        pDialog = DialogUtil.getCommonDialog(mAct, "正在处理...");
        pDialog.setCancelable(false);

        etGiftMoney.addTextChangedListener(new TextWatcher()
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
                if (TextUtils.isEmpty(etGiftMoney.getText().toString().trim()))
                {
                    btnSendGift.setEnabled(false);
                    btnSendGift.setBackgroundResource(R.drawable.round_btn_c0412t50_bg);
                } else
                {
                    btnSendGift.setBackgroundResource(R.drawable.round_btn_c0412_bg);
                    btnSendGift.setEnabled(true);
                }
            }
        });
    }

    @OnClick({R.id.btn_send_gift})
    void viewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_send_gift:
                try
                {
                    if (Double.parseDouble(etGiftMoney.getText().toString().trim()) < 0.1)
                    {
                        ToastUtils.showShort("发送红包金额最低为0.1元");
                        break;
                    }
                } catch (Exception e)
                {
                    ToastUtils.showShort("请输入正确的金额");
                    break;
                }
                PayHelper.clearPayHelperListeners();
                //用汇昇币支付触发
                PayHelper.setIHsPayPendingListener(new PendingPayFinish(this));
                PayBean payBean = new PayBean();
                payBean.setOrderId("");
                //很重要
                payBean.setMyPayType(EamConstant.EAM_PAY_PENDING);
                payBean.setAmount(CommonUtils.keep2Decimal(Double.parseDouble(etGiftMoney.getText().toString().trim())));
                payBean.setSubject("看脸吃饭App红包");       // 商品的标题
                payBean.setBody("聊天发红包");               // 商品的描述信息
                PayHelper.payOrder(allGiftView, payBean, mAct, new PayMetadataBean("", redPacketInfo.toUserUid, "", "2"));
                break;
            default:
                break;
        }
    }

    /**
     * Ping++回调,onActivityResult()发生在onResume()之前
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //支付页面返回处理
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                String result = data.getExtras().getString("pay_result");
                if (result.equals("success"))
                {
                    PayHelper.setIPayFinishedListener(new PayFinish(this));
                    PayHelper.thirdPartyPayStateCheck2(mAct, new PayMetadataBean("", redPacketInfo.toUserUid, "", "2"));
                } else if (result.equals("cancel"))
                {
                    ToastUtils.showShort("支付取消");
                } else
                {
                    ToastUtils.showShort("支付失败, 请重试");
                }
            }
        }
    }

    @Override
    protected ImpCSendRedPacketView createPresenter()
    {
        return new ImpCSendRedPacketView();
    }


    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody, GridPasswordView gridPasswordView)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.UserC_payRed:
                try
                {
                    if (ErrorCodeTable.PAYPWD_ERR.equals(code))
                    {
                        gridPasswordView.clearPassword();
                        JSONObject body = new JSONObject(errBody);
                        if (body.getString("surplus").equals("0"))
                        {
                            PayHelper.clearPopupWindows();
                            ToastUtils.showShort("您输入密码错误次数太多账号将会被锁定3小时");
                            mAct.setResult(RESULT_CANCELED);
                            mAct.finish();
                        } else
                        {



                            ToastUtils.showShort(String.format("支付密码错误，您还可以输入%s次", body.getString("surplus")));
                        }
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }finally
                {
                    if (pDialog != null && pDialog.isShowing())
                        pDialog.dismiss();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void requestNetError(Throwable call, String interfaceName)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.UserC_payRed:
                ToastUtils.showShort(getString(R.string.pay_fault_due_to_net));
                break;
        }
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void sendReadPacket2Callback(String response, GridPasswordView gridPasswordView)
    {
        try
        {
            Double result = Double.parseDouble(etGiftMoney.getText().toString().trim());
            //修改余额成功后，将金额回传
            Intent intent = new Intent();
            intent.putExtra(RedPacketConstant.EXTRA_RED_PACKET_ID, response);
            intent.putExtra(RedPacketConstant.EXTRA_RED_PACKET_SENDER_UID, SharePreUtils.getUId(mAct));
            intent.putExtra(RedPacketConstant.EXTRA_RED_PACKET_MONEY_AMOUNT, String.format("%s", CommonUtils.keep2Decimal(result)));
            intent.putExtra(RedPacketConstant.EXTRA_RED_PACKET_FROM_NICNAME, redPacketInfo.fromNickName);
            intent.putExtra(RedPacketConstant.EXTRA_RED_PACKET_GREETING, "恭喜发财");
            intent.putExtra(RedPacketConstant.EXTRA_RED_PACKET_FROM_URL, SharePreUtils.getHeadImg(mAct));
            mAct.setResult(RESULT_OK, intent);
            mAct.finish();
        } catch (Exception e)
        {
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
        } finally
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
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
                    etGiftMoney.setText(s);
                    etGiftMoney.setSelection(s.length());
                }
            }

            if (s.toString().contains("."))
            {
                if (s.length() - 1 - s.toString().indexOf(".") > 2)
                {
                    s = s.toString().subSequence(0,
                            s.toString().indexOf(".") + 3);
                    etGiftMoney.setText(s);
                    etGiftMoney.setSelection(s.length());
                }
            }
            if (s.toString().trim().substring(0).equals("."))
            {
                s = "0" + s;
                etGiftMoney.setText(s);
                etGiftMoney.setSelection(2);
            }

            if (s.toString().startsWith("0")
                    && s.toString().trim().length() > 1)
            {
                if (!s.toString().substring(1, 2).equals("."))
                {
                    etGiftMoney.setText(s.subSequence(0, 1));
                    etGiftMoney.setSelection(1);
                    return;
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s)
        {
            try
            {
                if (TextUtils.isEmpty(etGiftMoney.getText().toString().trim()))
                {
                    tvMoneyAmount.setText("0.00");
                    return;
                }
                Double result = Double.parseDouble(etGiftMoney.getText().toString().trim());
                if (result < 1)
                {
                    Logger.t(TAG).d("result:" + result);
                    tvMoneyAmount.setText(String.format("%s", new java.text.DecimalFormat("#0.00")
                            .format(result)));
                } else
                {
                    tvMoneyAmount.setText(String.format("%s", new java.text.DecimalFormat("#.00")
                            .format(result)));
                }
            } catch (Exception e)
            {
                Logger.t(TAG).d(e.getMessage());
            }
        }
    };


   /* @Override
    public Observable<ResponseBody> sendRedPacket(@QueryMap Map<String, String> params)
    {
        return null;
    }*/

    private static class PayFinish implements IPayFinishedListener
    {
        private final WeakReference<CRPsendRedPacketAct> mActRef;

        private PayFinish(CRPsendRedPacketAct mAct)
        {
            this.mActRef = new WeakReference<CRPsendRedPacketAct>(mAct);
        }

        @Override
        public void PayFinished(String orderId, String streamId, String payType)
        {
            final CRPsendRedPacketAct cAct = mActRef.get();
            if (cAct != null)
            {
                //支付成功后调用发红包接口
                Double amount = Double.parseDouble(cAct.etGiftMoney.getText().toString().trim());
                //修改余额成功后，将金额回传
                Intent intent = new Intent();
                intent.putExtra(RedPacketConstant.EXTRA_RED_PACKET_ID, streamId);
                intent.putExtra(RedPacketConstant.EXTRA_RED_PACKET_SENDER_UID, SharePreUtils.getUId(cAct.mAct));
                intent.putExtra(RedPacketConstant.EXTRA_RED_PACKET_MONEY_AMOUNT, String.format("%s", CommonUtils.keep2Decimal(amount)));
                intent.putExtra(RedPacketConstant.EXTRA_RED_PACKET_FROM_NICNAME, cAct.redPacketInfo.fromNickName);
                intent.putExtra(RedPacketConstant.EXTRA_RED_PACKET_GREETING, "恭喜发财");
                intent.putExtra(RedPacketConstant.EXTRA_RED_PACKET_FROM_URL, SharePreUtils.getHeadImg(cAct.mAct));
                cAct.mAct.setResult(RESULT_OK, intent);
                cAct.mAct.finish();

                //cAct.sendRedPacket(cAct,String.format("%s",CommonUtils.keep2Decimal(amount)),payTypeR,cAct.redPacketInfo.toUserUid,streamId,"");
            }
        }

        @Override
        public void PayFailed(String orderId, String streamId, String payType)
        {
            final CRPsendRedPacketAct cAct = mActRef.get();
            if (cAct != null)
            {
                cAct.setResult(RESULT_CANCELED);
                cAct.finish();
            }
        }
    }

    private static class PendingPayFinish implements IHsPayPendingListener
    {
        private final WeakReference<CRPsendRedPacketAct> mActRef;

        private PendingPayFinish(CRPsendRedPacketAct mAct)
        {
            this.mActRef = new WeakReference<CRPsendRedPacketAct>(mAct);
        }

        @Override
        public void payPending(String password, GridPasswordView gridPasswordView)
        {
            final CRPsendRedPacketAct cAct = mActRef.get();
            if (cAct != null)
            {
                if (cAct.mPresenter != null)
                {
                    if (cAct.pDialog != null && !cAct.pDialog.isShowing())
                        cAct.pDialog.show();
                    Double result = Double.parseDouble(cAct.etGiftMoney.getText().toString().trim());
                    cAct.mPresenter.sendRedPacket2(String.format("%s", CommonUtils.keep2Decimal(result)), "0"
                            , cAct.redPacketInfo.toUserUid, "", password, gridPasswordView);
                }
            }
        }
    }
}

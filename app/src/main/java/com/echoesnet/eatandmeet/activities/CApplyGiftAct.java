package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
import com.echoesnet.eatandmeet.presenters.ImpICApplyGiftView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICApplyGiftView;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.payUtil.IHsPayPendingListener;
import com.echoesnet.eatandmeet.utils.payUtil.IPayFinishedListener;
import com.echoesnet.eatandmeet.utils.payUtil.PayHelper;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.jungly.gridpasswordview.GridPasswordView;
import com.orhanobut.logger.Logger;
import com.pingplusplus.android.Pingpp;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CApplyGiftAct extends MVPBaseActivity<ICApplyGiftView, ImpICApplyGiftView> implements ICApplyGiftView
{
    private static final String TAG = CApplyGiftAct.class.getSimpleName();
    //region 变量
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.et_gift_money_amount)
    EditText etGiftMoney;
    @BindView(R.id.btn_send_gift)
    Button btnSendGift;
    @BindView(R.id.all_gift_view)
    LinearLayout allGiftView;
    @BindView(R.id.tv_from_user)
    TextView tvFromUser;
    @BindView(R.id.tv_to_user)
    TextView tvToUser;
    @BindView(R.id.tv_money_amount)
    TextView tvMoneyAmount;
    /**
     * 要与之聊天的uId;
     */
    private String toAddUid;
    /**
     * 要与之聊天的环信Id;
     */
    private String toAddUsernameH;
    private Dialog pDialog;
    private Activity mAct;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_capply_gift);
        ButterKnife.bind(this);
        afterViews();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //用汇昇币支付触发
        PayHelper.setIHsPayPendingListener(new PendingPayFinish(this));
    }


    private void afterViews()
    {
        mAct = this;
        topBar.setTitle("见面礼");
        topBar.getLeftButton().setVisibility(View.VISIBLE);
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                Intent intent = new Intent();
                intent.putExtra("result", "back");
                mAct.setResult(0, intent);
                mAct.finish();
            }

            @Override
            public void left2Click(View view)
            {

            }

            @Override
            public void rightClick(View view)
            {

            }
        });
        toAddUid = getIntent().getStringExtra("toCheckUserUid");
        toAddUsernameH = getIntent().getStringExtra("toCheckUserIdH");

        tvFromUser.setText(SharePreUtils.getNicName(mAct));
        tvToUser.setText(getIntent().getStringExtra("toCheckUserNicName"));
        etGiftMoney.addTextChangedListener(watcher);

        pDialog = DialogUtil.getCommonDialog(mAct, "正在处理...");
        pDialog.setCancelable(false);
    }

    @OnClick({R.id.btn_send_gift})
    void viewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_send_gift:
                try
                {
                    if (Double.parseDouble(etGiftMoney.getText().toString().trim()) < 1.0)
                    {
                        ToastUtils.showShort("发送红包金额必须大于1元");
                        return;
                    }
                } catch (Exception e)
                {
                    ToastUtils.showShort("请输入正确的金额");
                    return;
                }

                PayHelper.clearPayHelperListeners();
                //用汇昇币支付触发
                PayHelper.setIHsPayPendingListener(new PendingPayFinish(this));
                PayBean payBean = new PayBean();
                payBean.setOrderId("");
                payBean.setMyPayType(EamConstant.EAM_PAY_PENDING);
                payBean.setAmount(etGiftMoney.getText().toString());
                payBean.setSubject("看脸吃饭App红包");               // 商品的标题
                payBean.setBody("见面礼申请好友");        // 商品的描述信息
                PayHelper.payOrder(allGiftView, payBean, mAct, new PayMetadataBean("", toAddUid, "", "3"));
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
                    PayHelper.thirdPartyPayStateCheck2((Activity) mAct, new PayMetadataBean("", toAddUid, "", "3"));
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
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("result", "back");
        mAct.setResult(0, intent);
        mAct.finish();
    }

    /**
     * 申请好友
     *
     * @param hToAddUsername
     * @param reason
     */
    private void applyAsFriend(final String toAddUserUid, final String hToAddUsername, String reason, final String payType, String amount,
                               String streamId, String password, GridPasswordView gridPasswordView)
    {
        //参数为要添加的好友的username和添加理由
        try
        {
            if (mPresenter != null)
            {
                if (pDialog != null && !pDialog.isShowing())
                    pDialog.show();
                mPresenter.applyFriendByMoney2(toAddUserUid, reason, payType, amount, streamId, password, gridPasswordView);
            }
        } catch (Exception e)
        {
            ToastUtils.showShort("好友申请失败");
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
        }
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
    protected ImpICApplyGiftView createPresenter()
    {
        return new ImpICApplyGiftView();
    }


    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody, GridPasswordView gridPasswordView)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.NeighborC_giftFriend:
                if (ErrorCodeTable.PAYPWD_ERR.equals(code))
                {
                    try
                    {
                        JSONObject body = new JSONObject(errBody);
                        if (body.getString("surplus").equals("0"))
                        {
                            Intent intent = new Intent();
                            intent.putExtra("result", "no");
                            mAct.setResult(2, intent);
                            mAct.finish();
                            ToastUtils.showShort(String.format("由于您输入错误次数太多，密码被锁定3小时"));
                        } else
                        {
                            ToastUtils.showShort(String.format("支付密码错误，您还可以输入%s次", body.getString("surplus")));
                            if (gridPasswordView != null)
                                gridPasswordView.clearPassword();
                        }
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.NeighborC_giftFriend:
                ToastUtils.showShort(getString(R.string.pay_fault_due_to_net));
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
                break;
            default:
                break;
        }
    }


    @Override
    public void applyFriendByMoneyCallback(String response, GridPasswordView gridPasswordView, String reason)
    {


        try
        {
            EMClient.getInstance().contactManager().addContact(toAddUsernameH, reason);

            Intent intent = new Intent();
            intent.putExtra("result", "yes");
            mAct.setResult(1, intent);
            mAct.finish();
        } catch (HyphenateException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        }finally
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
            Logger.t(TAG).d("tvMoneyAmount:" + etGiftMoney.getText().toString().trim());

            try
            {
//                    if(etGiftMoney.getText().toString().length()==6)
//                        ToastUtils.showShort(mAct,"最多输入6位数");
                if (TextUtils.isEmpty(etGiftMoney.getText().toString().trim()))
                {
                    tvMoneyAmount.setText("￥ 0.00");
                    return;
                }
                Double result = Double.parseDouble(etGiftMoney.getText().toString().trim());
                if (result < 1)
                {
                    Logger.t(TAG).d("result:" + result);
                    tvMoneyAmount.setText(String.format("￥ %s", new java.text.DecimalFormat("#0.00")
                            .format(result)));
                } else
                {
                    tvMoneyAmount.setText(String.format("￥ %s", new java.text.DecimalFormat("#.00")
                            .format(result)));
                }
            } catch (Exception e)
            {

            }
        }
    };

    //第三方支付
    private static class PayFinish implements IPayFinishedListener
    {
        private final WeakReference<CApplyGiftAct> mActRef;

        private PayFinish(CApplyGiftAct mAct)
        {
            this.mActRef = new WeakReference<CApplyGiftAct>(mAct);
        }

        @Override
        public void PayFinished(String orderId, String streamId, String payType)
        {
            String payTypeR = "none";
            if (payType.equals("alipay") || payType.equals("alipay_wap"))
            {
                payTypeR = "1";
            } else if (payType.equals("wx"))
                payTypeR = "2";
            else
                payTypeR = "3";
            final CApplyGiftAct cAct = mActRef.get();
            if (cAct != null)
            {
                try
                {
                    EMClient.getInstance().contactManager().addContact(cAct.toAddUsernameH, "汇昇见面礼");
                    Intent intent = new Intent();
                    intent.putExtra("result", "yes");
                    cAct.mAct.setResult(1, intent);
                    cAct.mAct.finish();
                } catch (HyphenateException e)
                {
                    e.printStackTrace();
                }

/*                cAct.applyAsFriend(cAct.toAddUid, cAct.toAddUsernameH, "汇昇见面礼", payTypeR, cAct.etGiftMoney.getText().toString()
                        , streamId, "", null);*/
            }
        }

        @Override
        public void PayFailed(String orderId, String streamId, String payType)
        {
            final CApplyGiftAct cAct = mActRef.get();
            if (cAct != null)
            {
                Intent intent = new Intent();
                intent.putExtra("result", "no");
                cAct.setResult(2, intent);
                cAct.finish();
            }
        }
    }

    //余额支付
    private static class PendingPayFinish implements IHsPayPendingListener
    {
        private final WeakReference<CApplyGiftAct> mActRef;

        private PendingPayFinish(CApplyGiftAct mAct)
        {
            this.mActRef = new WeakReference<CApplyGiftAct>(mAct);
        }

        @Override
        public void payPending(String passWord, GridPasswordView gridPasswordView)
        {
            final CApplyGiftAct cAct = mActRef.get();
            Logger.t(TAG).d("余额支付触发");
            if (cAct != null)
            {
                cAct.applyAsFriend(cAct.toAddUid, cAct.toAddUsernameH, "汇昇见面礼", "0", cAct.etGiftMoney.getText().toString(), "", passWord, gridPasswordView);
            }
        }
    }
}

package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.presenters.ImpDApplyRefundView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDApplyRefundView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.EditViewWithCharIndicate;
import com.echoesnet.eatandmeet.views.widgets.SpinnerPopup.IOnSpinnerItemClickedListener;
import com.echoesnet.eatandmeet.views.widgets.SpinnerPopup.SpinnerPopup;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


public class DApplyRefundAct extends MVPBaseActivity<IDApplyRefundView, ImpDApplyRefundView> implements IDApplyRefundView
{
    private static final String TAG = DApplyRefundAct.class.getSimpleName();
    //region 退款详情
    @BindView(R.id.top_bar)
    TopBarSwitch topBar;
    @BindView(R.id.tv_refund_money)
    TextView tvRefundMoney;
    @BindView(R.id.tv_refund_cost)
    TextView tvRefundCost;
    @BindView(R.id.tv_percentage)
    TextView tvPercentage;
    @BindView(R.id.tv_refund_factorage)
    TextView tvRefundFactorage;
    @BindView(R.id.rb_refund_hs_pay)
    RadioButton rbHsPay;
    @BindView(R.id.rb_refund_3part_pay)
    RadioButton rb3PartPay;
    //    @ViewById(R.id.tv_bind_alipay)
//    TextView tvBindAliPay;
    @BindView(R.id.tv_refund_reason)
    TextView tvRefundReason;
    @BindView(R.id.ll_refund_alipy)
    LinearLayout ll_refund_alipy;
    @BindView(R.id.btnApplyRefund)
    Button btnApplyRefund;

    @BindView(R.id.evw_input_feedback)
    EditViewWithCharIndicate ewciFeedBack;

    //TranslateAnimation animation;// 出现的动画效果

    private Activity mContext;
    private Dialog pDialog;
    private String orderId, refundFee, refundAmount;
    private SpinnerPopup myPopWindow;
    //是否退回到汇昇账号
    private String payType = "";
    private HashMap<String, String> applyRefundInfoMap;
    private String orderType;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_apply_refund);
        ButterKnife.bind(this);
        afterViews();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        setUiContent(applyRefundInfoMap);
        //申请退款
        //applyRefund(orderId);
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
    protected ImpDApplyRefundView createPresenter()
    {
        return new ImpDApplyRefundView();
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
        }).setText("申请退款");
        applyRefundInfoMap = (HashMap<String, String>) getIntent().getSerializableExtra("applyResult");
        orderId = applyRefundInfoMap.get("orderId");
        orderType = applyRefundInfoMap.get("orderType");
/*        int[]location=new int[2];
        tvRefundReason.getLocationOnScreen(location);
        animation = new TranslateAnimation(0, 0, -700, location[1]);
        animation.setDuration(500);*/

/*        rbHsPay.setButtonDrawable(new IconDrawable(this, EchoesEamIcon.eam_p_radio_btn
        ).colorRes(R.color.MC1)
                .sizeDp(14));*/
        rbHsPay.setButtonDrawable(R.drawable.radio_btn_p);
/*        rb3PartPay.setButtonDrawable(new IconDrawable(this,EchoesEamIcon.eam_n_radio_btn
        ).colorRes(R.color.FC7)
                .sizeDp(14));*/
        rb3PartPay.setButtonDrawable(R.drawable.radio_btn_n);
        pDialog = DialogUtil.getCommonDialog(this, "正在处理...");
        pDialog.setCancelable(false);


        tvRefundReason.setTag("collapse");
//        String htmlStr=String.format("<font>没有绑定？<font color=%s>去绑定</font></font>","#36ace7");
//        tvBindAliPay.setText(Html.fromHtml(htmlStr));

        rb3PartPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                onRadioButtonClicked(buttonView);
            }
        });
        rbHsPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                onRadioButtonClicked(buttonView);
            }
        });

    }

    @OnClick({R.id.tv_refund_reason, R.id.btnApplyRefund})
    void viewClicked(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_refund_reason:
                if (tvRefundReason.getTag().equals("collapse"))
                {
                    tvRefundReason.setTag("expend");
                    tvRefundReason.setBackgroundResource(R.drawable.my_refund_drop_down_up);
                    myPopWindow = new SpinnerPopup(mContext,
                            mContext.getResources().getStringArray(R.array.refund_reason)
                            , tvRefundReason.getWidth());
                    myPopWindow.setIOnSpinnerItemClicked(new onSpinnerItemClick());
                    myPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
                    {
                        @Override
                        public void onDismiss()
                        {
                            tvRefundReason.setTag("collapse");
                            tvRefundReason.setBackgroundResource(R.drawable.my_refund_drop_down_down);
                        }
                    });
                    //myPopWindow.setOnDismissListener(new popupDismissListener(myPopWindow));
                    myPopWindow.showPopupWindow(v);
                } else
                {
                    tvRefundReason.setTag("collapse");
                    tvRefundReason.setBackgroundResource(R.drawable.my_refund_drop_down_down);
                    if (myPopWindow != null && myPopWindow.isShowing())
                        myPopWindow.hidePopupWindow();
                }
                break;
            case R.id.btnApplyRefund:
                refundCommit();
                break;
//            case R.id.tv_bind_alipay:
//                MySetAccountSecAlipayAct_.intent(mContext).start();
//                break;
            default:
                break;
        }
    }

    private void onRadioButtonClicked(View view)
    {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId())
        {
            case R.id.rb_refund_hs_pay:
                if (checked)
                {
                    Logger.t(TAG).d("HUISHENG");
                    rbHsPay.setButtonDrawable(R.drawable.radio_btn_p);
/*                    rbHsPay.setButtonDrawable(new IconDrawable(mContext,EchoesEamIcon.eam_p_radio_btn
                    ).colorRes(R.color.MC1)
                            .sizeDp(14));*/
/*                    rb3PartPay.setButtonDrawable(new IconDrawable(mContext,EchoesEamIcon.eam_n_radio_btn
                    ).colorRes(R.color.FC7)
                            .sizeDp(14));*/
                    rb3PartPay.setButtonDrawable(R.drawable.radio_btn_n);
                    rbHsPay.setChecked(true);
                    rb3PartPay.setChecked(false);
                }
                break;
            case R.id.rb_refund_3part_pay:
                if (checked)
                {
                    Logger.t(TAG).d("thirdpart");
                    rbHsPay.setButtonDrawable(R.drawable.radio_btn_n);
/*                    rbHsPay.setButtonDrawable(new IconDrawable(mContext,EchoesEamIcon.eam_n_radio_btn
                    ).colorRes(R.color.FC7)
                            .sizeDp(14));*/
                    rb3PartPay.setButtonDrawable(R.drawable.radio_btn_p);
/*                    rb3PartPay.setButtonDrawable(new IconDrawable(mContext,EchoesEamIcon.eam_p_radio_btn
                    ).colorRes(R.color.MC1)
                            .sizeDp(14));*/
                    rbHsPay.setChecked(false);
                    rb3PartPay.setChecked(true);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 提交退款
     */
    private void refundCommit()
    {
        String reason = "";
        if (TextUtils.isEmpty(ewciFeedBack.getInputText()) && TextUtils.isEmpty(tvRefundReason.getText().toString()))
        {
            ToastUtils.showShort("请选择或者填写退款原因！");
            return;
        } else
        {
            reason = String.format("选择的理由：%s； 填写的理由:%s", tvRefundReason.getText().toString(), ewciFeedBack.getInputText());
        }

        //region 注释代码
       /* //如果选择的是支付宝方式，则先检查是否绑定了账号
        if (rb3PartPay.isChecked())
        {
            if (tvBindAliPay.getVisibility()==View.VISIBLE)
            {
                //提示是否绑定支付宝
                new CustomAlertDialog(mContext)
                        .builder()
                        .setTitle("提示")
                        .setMsg("您还未绑定支付宝账号，是否现在绑定？")
                        .setPositiveButton("同意", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                MySetAccountSecAlipayAct_.intent(mContext).start();
                            }
                        })
                        .setNegativeButton("拒绝", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Logger.t(TAG).d("拒绝");
                                rbHsPay.setChecked(true);
                            }
                        }).show();
            }else
            {
                refund(reason,orderId,rbHsPay.isChecked()?"0":"1",refundAmount,refundFee);
            }
        }
        else
        {

        }*/
        //endregion

//        refund(reason,orderId,rbHsPay.isChecked()?"0":"1",refundAmount,refundFee);
        if (mPresenter != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            mPresenter.refund(reason, orderId, rbHsPay.isChecked() ? "0" : "1", refundAmount, refundFee,orderType);
        }
    }
    //region 暂时废弃的函数

    /**
     * 校验支付宝是否绑定
     */
    /*private void checkAlipayState()
    {
        pDialog.show();
        Map<String,String> reqParamMap=new NetHelper.getCommonPartOfParam(mContext);

        Logger.t(TAG).d("提交的的josn"+ new Gson().toJson(reqParamMap));
        OkHttpUtils.postString()
                .url(NetHelper.SERVER_SITE)
                .content(NetHelper.getRequestJsonStr("UserC/checkAlipay",new Gson().toJson(reqParamMap)))
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        NetHelper.handleNetError(mContext,null,TAG,e);
                        if(pDialog!=null&&pDialog.isShowing())
                            pDialog.dismiss();
                    }
                    @Override
                    public void onResponse(String response)
                    {
                        Logger.t(TAG).json(response);
                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);
                            int status=jsonResponse.getInt("status");
                            if (status==0)
                            {
                                JSONObject body=new JSONObject(jsonResponse.getString("body"));
//                                setUiContentAboutAlipay(body.getString("flag"));
                            }
                            else if (status==1)
                            {
                                String code=jsonResponse.getString("code");
                                if (!ErrorCodeTable.handleErrorCode(code,mContext))
                                ToastUtils.showShort(mContext, ErrorCodeTable.parseErrorCode(code));
                                Logger.t(TAG).d("错误码为：%s",code);
                            }
                        } catch (JSONException e)
                        {
                            Logger.t(TAG).d(e.getMessage());
                            e.printStackTrace();
                        }
                        finally
                        {
                            if(pDialog!=null&&pDialog.isShowing())
                                pDialog.dismiss();
                        }
                    }
                });
    }*/
/*
    private void setUiContentAboutAlipay(String result)
    {
        if (this.payType.equals("0"))
        {
            tvBindAliPay.setVisibility(View.GONE);
            return;
        }
        //未绑定支付宝
        if (result.equals("0"))
        {
            tvBindAliPay.setVisibility(View.VISIBLE);
        }
        else
        {
            tvBindAliPay.setVisibility(View.GONE);
        }
    }
*/
    //endregion
    private void setUiContent(Map<String, String> resultMap)
    {
        //付款金额
        tvRefundCost.setText(String.format("￥ %s", resultMap.get("payAmount")));
        tvRefundMoney.setText(resultMap.get("refundAmount"));
        Logger.t(TAG).d("refundAmount...>>>."+resultMap.get("refundAmount"));
        refundFee = resultMap.get("fee");
        refundAmount = resultMap.get("refundAmount");
        tvRefundFactorage.setText(String.format("￥ %s", refundFee));
        tvPercentage.setText("(订单金额的"+ (int)(Double.parseDouble(CommonUtils.keep2Decimal(Double.valueOf(refundFee)/Double.valueOf(resultMap.get("payAmount"))))*100)+"%)");
        String payType = resultMap.get("payMethod");
        this.payType = payType;
        Logger.t(TAG).d(payType);
        if (payType.equals("0"))//汇昇
        {
            rbHsPay.setChecked(true);
            rbHsPay.setVisibility(View.VISIBLE);

            ll_refund_alipy.setVisibility(View.GONE);
//            tvBindAliPay.setVisibility(View.GONE);
        } else
        {
            rb3PartPay.setChecked(true);
            rbHsPay.setChecked(false);
            rbHsPay.setVisibility(View.VISIBLE);
            ll_refund_alipy.setVisibility(View.VISIBLE);
        }
        //移除支付宝
//        checkAlipayState();
    }

    private void startRefundDetail(String orderId,String orderType)
    {
        Intent intentRefund = new Intent(mContext, DRefundDetailAct.class);
        intentRefund.putExtra("orderId", orderId);
        intentRefund.putExtra("orderType", orderType);
        startActivity(intentRefund);
    }

    private void startClubRefundDetail(String orderId,String orderType)
    {
        Intent intentRefund = new Intent(mContext, DRefundDetailAct.class);
        intentRefund.putExtra("orderId", orderId);
        intentRefund.putExtra("orderType", orderType);
        startActivity(intentRefund);
        finish();
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.OrderC_refund:
                if ("LIGHT_ORDER_CANNOT_REFUND".equals(code) || "SWEEP_ORDER_CANNOT_REFUND".equals(code))
                {
                    new CustomAlertDialog(mContext)
                            .builder()
                            .setTitle("提示")
                            .setMsg("请再等一下，此时不可取消订餐，如果主播最终未能到达，订单将自动取消")
                            .setPositiveButton("确定", null).show();
                }
                Logger.t(TAG).d("错误码为：%s", code);
                break;
            default:
                break;
        }
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mContext, null, exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void refundCallback(String response,String orderType)
    {
        Logger.t(TAG).json(response);
        ToastUtils.showShort("退款成功");
        if (TextUtils.equals("clubOrder",orderType))
        {
            startClubRefundDetail(orderId,orderType);
        }
        else
        {
            startRefundDetail(orderId,orderType);
        }

        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }


    class onSpinnerItemClick implements IOnSpinnerItemClickedListener
    {
        @Override
        public void itemClicked(int index, String itemStr)
        {
            tvRefundReason.setText(itemStr);
        }
    }

}

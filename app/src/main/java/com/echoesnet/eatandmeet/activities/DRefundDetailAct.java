package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.presenters.ImpDRefundDetailView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDRefundDetailView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 退款详情页
 */

public class DRefundDetailAct extends MVPBaseActivity<IDRefundDetailView, ImpDRefundDetailView> implements IDRefundDetailView
{
    private static final String TAG = DRefundDetailAct.class.getSimpleName();
    //region 变量
    @BindView(R.id.top_bar)
    TopBar topBar;
    //退款金额
    @BindView(R.id.tv_refund_money)
    TextView tvRefundMoney;
    //退款账号
    @BindView(R.id.tv_refund_cost)
    TextView tvRefundAccount;
    @BindView(R.id.tv_refund_time)
    TextView tvRefundTime;
    //流程字段
    @BindView(R.id.tv_refund_review)
    TextView tvRefundStep1;
    @BindView(R.id.tv_refund_accept)
    TextView tvRefundStep1Description;
    @BindView(R.id.tv_refund_show_time)
    TextView tvRefundStep1Time;
    @BindView(R.id.tv_refund_result)
    TextView tvRefundStep2;
    @BindView(R.id.tv_refund_finish)
    TextView tvRefundStep3;


    @BindView(R.id.tv_refund_check_result)
    TextView tvRefundCheckResult;
    @BindView(R.id.tv_refund_check_time)
    TextView tvRefundCheckTime;
    @BindView(R.id.tv_refund_get_money_time)
    TextView tvRefundGetMoneyTime;


    @BindView(R.id.iv_dot1)
    ImageView ivDot1;
    @BindView(R.id.iv_dot2)
    ImageView ivDot2;
    @BindView(R.id.iv_dot3)
    ImageView ivDot3;
    @BindView(R.id.v_line1)
    View vLine1;
    @BindView(R.id.v_line2)
    View vLine2;

    @BindView(R.id.all_apply_refund_detail)
    AutoLinearLayout allApplyRefundDetail;
    @BindView(R.id.all_past_due_detail)
    AutoLinearLayout allPassDueRefundDetail;
    @BindView(R.id.all_apply_refund_progress)
    AutoLinearLayout allApplyRefundProgress;

    //过期退款金额
    @BindView(R.id.tv_pass_due_refund_amount)
    TextView tvDueRefundMoney;
    //过期手续费
    @BindView(R.id.tv_pass_due_refund_fee)
    TextView tvDueRefundFee;
    //过期退款类型
    @BindView(R.id.tv_pass_due_refund_type)
    TextView tvDueRefundType;

    //用来表示是否是主动申请的退款（null）还是过期的退款(PassDueRefund)
    private String openType, orderId;
    private Activity mContext;
    private Dialog pDialog;
    private String orderType;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_refund_detail);
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

    void afterViews()
    {
        mContext = this;
        pDialog = DialogUtil.getCommonDialog(this, "正在处理...");
        pDialog.setCancelable(false);
        topBar.setTitle("退款详情");
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
//                if (TextUtils.equals("clubOrder",orderType))
//                {
//                     intent1 = new Intent(mContext, ClubOrderRecordDetailAct.class);
//                }
//                else
//                {
                if (!TextUtils.equals("clubOrder",orderType))
                {
                    Intent  intent1 = new Intent(mContext, DOrderRecordDetail.class);
                    //返回到订单详情
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent1.putExtra("orderId", orderId);
                    mContext.startActivity(intent1);
                }
                mContext.finish();
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
        openType = getIntent().getStringExtra(EamConstant.EAM_REFUND_DETAIL_OPEN_SOURCE);
        orderId = getIntent().getStringExtra("orderId");
        orderType = getIntent().getStringExtra("orderType");

        if (mPresenter != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            mPresenter.getRefundDetail(orderId,orderType);
        }

//        getRefundDetail(orderId);

/*        if (openType.equals("DApplyRefundAct"))
        {
            final HashMap<String,String>map= (HashMap<String, String>) getIntent().getSerializableExtra("resultMap");
            orderId=map.get("orderId");
            tvRefundAccount.setText(map.get("payMethod").equals("0")?"看脸吃饭余额":"支付宝");
            tvRefundMoney.setText(map.get("refundAmount"));
            tvRefundTime.setText(String.format("预计%s之前到账",map.get("finishTime")));
            //进度

        }
        else if (openType.equals("DOrderRecordDetail"))
        {
            orderId=getIntent().getStringExtra("orderId");
            getRefundDetail(orderId);
        }*/
    }

    private void setUiContent(Map<String, String> resultMap)
    {
        Logger.t(TAG).d("openType:" + openType);
        /*如果是过期退*/
        if (openType != null && openType.equals("PassDueRefund"))
        {
            Logger.t(TAG).d("过期退");
            allPassDueRefundDetail.setVisibility(View.VISIBLE);
            allApplyRefundDetail.setVisibility(View.GONE);
            String refundMoney = CommonUtils.keep2Decimal(Double.parseDouble(resultMap.get("refundAmount")));
            tvDueRefundMoney.setText(refundMoney);
            String refundFee = CommonUtils.keep2Decimal(Double.parseDouble(resultMap.get("refundFee")));
            tvDueRefundFee.setText(refundFee);
            tvDueRefundType.setText(resultMap.get("refundMethod").equals("0") ? "看脸吃饭余额" : "支付宝");
        } else
        {
            allApplyRefundDetail.setVisibility(View.VISIBLE);
            allApplyRefundProgress.setVisibility(View.VISIBLE);

            tvRefundAccount.setText(resultMap.get("refundMethod").equals("0") ? "看脸吃饭余额" : resultMap.get("refundMethod").equals("1") ? "支付宝" : "微信");
            tvRefundMoney.setText(resultMap.get("refundAmount"));
            tvRefundTime.setText(String.format("预计%s之前到账", resultMap.get("expectTime")));
            tvRefundStep1Description.setText(resultMap.get("checkContext"));
            tvRefundStep1Time.setText(resultMap.get("applyRefundTime"));
            switch (resultMap.get("refundStatus"))
            {
                //审核中
                case "0":

                    break;
                //审核通过
                case "1":
                    tvRefundStep2.setText("审核通过");
                    tvRefundStep2.setTextColor(ContextCompat.getColor(mContext, R.color.MC5));
                    vLine1.setBackgroundResource(R.color.MC5);
                    ivDot2.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.dot_y));
                    tvRefundCheckTime.setText(resultMap.get("checkRefundTime"));

                    break;
                //驳回
                case "2":
                    tvRefundStep2.setText("审核被驳回");
                    tvRefundStep2.setTextColor(ContextCompat.getColor(mContext, R.color.MC5));
                    tvRefundCheckResult.setText(resultMap.get("rejectReason"));
                    tvRefundCheckTime.setText(resultMap.get("checkRefundTime"));
                    vLine1.setBackgroundResource(R.color.MC5);
                    ivDot2.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.dot_y));
                    break;
                //已退款
                case "3":
                    tvRefundStep3.setText("已退款");
                    tvRefundStep3.setTextColor(ContextCompat.getColor(mContext, R.color.MC5));
                    vLine1.setBackgroundResource(R.color.MC5);
                    ivDot2.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.dot_y));
                    vLine2.setBackgroundResource(R.color.MC5);
                    ivDot3.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.dot_y));
                    tvRefundGetMoneyTime.setText(resultMap.get("refundTime"));
                    //到账后将预计到账设置为到账时间
                    tvRefundTime.setText(resultMap.get("refundTime"));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected ImpDRefundDetailView createPresenter()
    {
        return new ImpDRefundDetailView();
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mContext, null, exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getRefundDetailCallback(String response)
    {
        Logger.t(TAG).d("返回结果：" + response);
        try
        {
            JSONObject body = new JSONObject(response);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("checkContext", body.getString("checkContext"));
            map.put("refundAmount", body.getString("refundAmount"));
            map.put("refundFee", body.getString("refundFee"));
            map.put("rejectReason", body.getString("rejectReason"));
            map.put("applyRefundTime", body.getString("applyRefundTime"));
            map.put("refundStatus", body.getString("refundStatus"));
            map.put("refundResult", body.getString("refundResult"));
            map.put("expectTime", body.getString("expectTime"));
            map.put("refundMethod", body.getString("refundMethod"));
            map.put("checkResult", body.getString("checkResult"));
            map.put("refundTime", body.getString("refundTime"));
            map.put("checkRefundTime", body.getString("checkRefundTime"));
            setUiContent(map);
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();

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
}

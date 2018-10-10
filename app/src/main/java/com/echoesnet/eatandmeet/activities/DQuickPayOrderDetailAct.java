package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.listeners.IOnDOrderDeletedListener;
import com.echoesnet.eatandmeet.listeners.IOnDUnpayOrderDeletedListener;
import com.echoesnet.eatandmeet.models.bean.DishBean;
import com.echoesnet.eatandmeet.models.bean.MyResCommentBean;
import com.echoesnet.eatandmeet.models.bean.OrderRecordBean;
import com.echoesnet.eatandmeet.presenters.ImpDQuickPayOrderDetailView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDQuickPayOrderDetailView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.zhy.autolayout.AutoLinearLayout;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DQuickPayOrderDetailAct extends MVPBaseActivity<IDQuickPayOrderDetailView, ImpDQuickPayOrderDetailView> implements IDQuickPayOrderDetailView
{
    private static final String TAG = DQuickPayOrderDetailAct.class.getSimpleName();
    //region 变量
    @BindView(R.id.top_bar)
    TopBar topBar;
    //消费金额
    @BindView(R.id.tv_custom_amount_show)
    TextView tvCustomAmount;
    //订单状态
    @BindView(R.id.tv_custom_status)
    TextView tvQuickPayOrderStatus;
    @BindView(R.id.tv_order_id)
    TextView tvQuickPayOrderId;
    @BindView(R.id.tv_order_mobile)
    TextView tvQuickPayMobile;
    @BindView(R.id.tv_order_time)
    TextView tvQuickPayTime;
    @BindView(R.id.tv_order_detail_res_name)
    TextView tvQuickPayResName;
    @BindView(R.id.tv_order_detail_address)
    TextView tvQuickPayResAddress;
    @BindView(R.id.itv_order_detail_call)
    IconTextView itvMakeCall;
    @BindView(R.id.btn_comment)
    Button btnComment;
    @BindView(R.id.btn_del)
    Button btnDel;
    @BindView(R.id.rimg_consultant_face)
    RoundedImageView consultantFaceRImg;
    @BindView(R.id.tv_consultant_name)
    TextView consultantNameTv;
    @BindView(R.id.ll_consultant)
    AutoLinearLayout consultantLinea;


    private String[] phoneNum;
    private String orderId;
    private Dialog pDialog;
    private Activity mContext;

    private OrderRecordBean quickBean;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dquick_pay_order_detail);
        ButterKnife.bind(this);
        afterViews();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (mPresenter != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            mPresenter.getOrderDetail(orderId);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        pDialog = null;
    }


    void afterViews()
    {
        mContext = this;
        topBar.setTitle("订单详情");
        topBar.getLeftButton().setVisibility(View.VISIBLE);
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
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
        orderId = getIntent().getStringExtra("orderId");
        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理...");
        pDialog.setCancelable(false);
    }

    @OnClick({R.id.itv_order_detail_call, R.id.btn_comment, R.id.btn_del})
    void viewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.itv_order_detail_call:
                if (phoneNum != null)
                    CommonUtils.makeCall(mContext, phoneNum[0]);
                break;
            case R.id.btn_comment:
                if (btnComment.getText().equals("查看评价") && mPresenter != null)
                {
                    mPresenter.getResCommentDetail(orderId);
                } else
                {
                    if (quickBean != null)
                    {
                        Intent intent = new Intent(mContext, DOrderCommentAct.class);
                        intent.putExtra("orderId", quickBean.getOrdId());
                        intent.putExtra("resId", quickBean.getrId());
                        intent.putExtra("dishLst", (ArrayList<DishBean>) quickBean.getDishBeen());
                        intent.putExtra("orderType", "quickType");
                        mContext.startActivity(intent);
                    }
                }
                break;
            case R.id.btn_del:
                showDelDialog(orderId);
                break;
        }
    }


    private void setViewContent(OrderRecordBean recordBean)
    {
        quickBean = recordBean;
        phoneNum = recordBean.getResMobile().split(CommonUtils.SEPARATOR);
        String money = CommonUtils.keep2Decimal(Double.parseDouble(recordBean.getOrderCos2()));
        tvCustomAmount.setText(String.format("￥%s", money));
        //这个暂时只有一种状态
        tvQuickPayOrderStatus.setText(recordBean.getStatus().equals("2") ? "已付款" : "已关闭");
        tvQuickPayMobile.setText(recordBean.getMobile());
        tvQuickPayOrderId.setText(recordBean.getOrdId());
        tvQuickPayResAddress.setText(recordBean.getResAddr());
        tvQuickPayTime.setText(recordBean.getOrderTime());
        tvQuickPayResName.setText(recordBean.getrName());
        consultantLinea.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(recordBean.getConsultant()))
        {
            consultantLinea.setVisibility(View.VISIBLE);
            consultantNameTv.setText(recordBean.getConsultantName());
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(recordBean.getConsultantPhurl())
                    .placeholder(R.drawable.userhead)
                    .into(consultantFaceRImg);
        }
        if (recordBean.getStatus().equals("7"))
        {
            //btn_comment.setTag();
            btnComment.setText("查看评价");
        } else
        {
            btnComment.setText("去评价");
        }
    }

    @Override
    protected ImpDQuickPayOrderDetailView createPresenter()
    {
        return new ImpDQuickPayOrderDetailView();
    }


    private void showDelDialog(final String ordId)
    {
        new CustomAlertDialog(mContext)
                .builder()
                .setMsg("确定要删除此订单吗？")
                .setPositiveTextColor(ContextCompat.getColor(mContext, R.color.C0313))
                .setPositiveButton("确认", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mPresenter.deleteOrder(ordId);
                    }
                }).setNegativeButton("取消", new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

            }
        }).show();
    }

    private static IOnDOrderDeletedListener iOnDOrderDeletedListener;//全部订单删除回调
    private static IOnDUnpayOrderDeletedListener iOnDUnpayOrderDeletedListener;//待支付订单删除回调

    public static void setOnOrderDeletedListener(IOnDOrderDeletedListener
                                                         iOnDOrderDeletedListeners)
    {
        iOnDOrderDeletedListener = iOnDOrderDeletedListeners;
    }

    public static void setOnUnpayOrderDeletedListener(IOnDUnpayOrderDeletedListener
                                                              iOnDOrderDeletedListeners)
    {
        iOnDUnpayOrderDeletedListener = iOnDOrderDeletedListeners;
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        switch (interfaceName)
        {
            case NetInterfaceConstant.EvalC_checkEval:
                if ("EVAL_DELED".equals(code))
                {
                    ToastUtils.showShort("此评价由于违反了《看脸吃饭》相关规定，已经被删除");
                }
                break;
            case NetInterfaceConstant.OrderC_orderDetail:
                if (code.equals("ORDER_DELED"))
                {
                    ToastUtils.showShort(ErrorCodeTable.parseErrorCode(code));
                    finish();
                }
                break;
        }
    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }


    @Override
    public void getOrderDetailCallback(OrderRecordBean orderRecord)
    {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        if (orderRecord==null)
            return;
        setViewContent(orderRecord);
    }


    @Override
    public void deleteOrderCallback(String response)
    {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        //全部订单中被删除
        if (iOnDOrderDeletedListener != null)
        {
            iOnDOrderDeletedListener.onOrderIsDeleted();
        }
        //待支付订单中被删除
        if (iOnDUnpayOrderDeletedListener != null)
        {
            iOnDUnpayOrderDeletedListener.onUnpayOrderDeletedListener();
        }

        mContext.finish();
    }

    @Override
    public void getResCommentDetailCallback(MyResCommentBean resComment)
    {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        if (resComment==null)
        {
            return;
        }
        /*查看评价*/
        Intent intentV = new Intent(mContext, MyCheckOrderCommentAct.class);
        intentV.putExtra("commentInfo", (Serializable) resComment);
        intentV.putExtra("orderType", "quickType");
        mContext.startActivity(intentV);
    }
}

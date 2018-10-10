package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.listeners.IOnDOrderDeletedListener;
import com.echoesnet.eatandmeet.listeners.IOnDUnpayOrderDeletedListener;
import com.echoesnet.eatandmeet.models.bean.ClubOrderDetailBean;
import com.echoesnet.eatandmeet.models.bean.MyResCommentBean;
import com.echoesnet.eatandmeet.models.bean.PayBean;
import com.echoesnet.eatandmeet.models.bean.PayMetadataBean;
import com.echoesnet.eatandmeet.models.bean.QRCodeBean;
import com.echoesnet.eatandmeet.presenters.ImpIClubOrderRecordDetailPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IClubOrderRecordDetailView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.payUtil.IPayFinishedListener;
import com.echoesnet.eatandmeet.utils.payUtil.PayHelper;
import com.echoesnet.eatandmeet.views.adapters.ClubFoodDetailListAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.SharePopopWindow.SharePopWindow;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.google.gson.Gson;
import com.joanzapata.iconify.widget.IconTextView;
import com.linearlistview.LinearListView;
import com.orhanobut.logger.Logger;
import com.pingplusplus.android.Pingpp;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 * 轰趴订单详情页面
 *
 * @author lc
 * @version 1.0
 * @modifier
 * @createDate 2018/2/8
 * @description
 */
public class ClubOrderRecordDetailAct extends MVPBaseActivity<ClubOrderRecordDetailAct, ImpIClubOrderRecordDetailPre> implements IClubOrderRecordDetailView
{

    private final static String TAG = ClubOrderRecordDetailAct.class.getSimpleName();
    private static IOnDOrderDeletedListener iOnDOrderDeletedListener;//全部订单删除回调
    private static IOnDUnpayOrderDeletedListener iOnDUnpayOrderDeletedListener;//待支付订单删除回调
    @BindView(R.id.tbs_top_bar)
    TopBarSwitch tbsTopBar;
    @BindView(R.id.tv_order_detail_order_id)
    TextView tvOrderDetailOrderId;
    @BindView(R.id.tv_order_detail_status)
    TextView tvOrderDetailStatus;
    @BindView(R.id.lv_res_dish_lst)
    LinearListView lvResDishLst;
    @BindView(R.id.tv_order_detail_total_cost)
    TextView tvOrderDetailTotalCost;
    @BindView(R.id.tv_order_detail_final_cost)
    TextView tvOrderDetailFinalCost;
    @BindView(R.id.iv_show_code_img)
    ImageView ivShowCodeImg;
    @BindView(R.id.tv_order_detail_cost_number)
    TextView tvOrderDetailCostNumber;
    @BindView(R.id.tv_order_detail_valid_date)
    TextView tvOrderDetailValidDate;
    @BindView(R.id.tv_refund_post)
    TextView tvRefundPost;
    @BindView(R.id.all_unused)
    LinearLayout allUnused;
    @BindView(R.id.tv_check_refund_declare)
    TextView tvCheckRefundDeclare;
    @BindView(R.id.all_refund)
    RelativeLayout allRefund;
    @BindView(R.id.tv_order_detail_cost_number3)
    TextView tvOrderDetailCostNumber3;
    @BindView(R.id.tv_order_detail_valid_date3)
    TextView tvOrderDetailValidDate3;
    @BindView(R.id.all_cost_code)
    LinearLayout allCostCode;
    @BindView(R.id.tv_order_detail_time)
    TextView tvOrderDetailTime;
    @BindView(R.id.tv_order_detail_info)
    TextView tvOrderDetailInfo;//红字提示：按约定时间到达
    @BindView(R.id.tv_order_detail_name)
    TextView tvOrderDetailName;
    @BindView(R.id.tv_order_detail_address)
    TextView tvOrderDetailAddress;
    @BindView(R.id.itv_order_detail_call)
    IconTextView itvOrderDetailCall;
    @BindView(R.id.btn_order_detail_delete)
    Button btnOrderDetailDelete;
    @BindView(R.id.btn_order_detail_pay)
    Button btnOrderDetailPay;
    @BindView(R.id.all_order_detail_btn_container)
    LinearLayout allOrderDetailBtnContainer;
    @BindView(R.id.btn_comment_order)
    Button btnCommentOrder;
    @BindView(R.id.all_order_detail_btn_comment_delete)
    LinearLayout allOrderDetailBtnCommentDelete;
    @BindView(R.id.btn_check_refund_detail)
    Button btnCheckRefundDetail;
    @BindView(R.id.main)
    LinearLayout main;
    @BindView(R.id.ll_reServeOrder)
    LinearLayout llReServeOrder;
    @BindView(R.id.tv_thName)
    TextView tvThName;
    @BindView(R.id.tv_themeName)
    TextView tvThemeName;
    @BindView(R.id.tv_reMarkTitle)
    TextView tvReMarkTitle;
    @BindView(R.id.tv_reMark)
    TextView tvReMark;
    @BindView(R.id.rl_theme_remark_all)
    RelativeLayout rlThemeRemarkAll;
    @BindView(R.id.v_theme_line)
    View vThemeLine;


    private String orderId;
    private String[] phoneNum;
    private ClubOrderDetailBean orderRecordBean;
    private Dialog pDialog;
    private Activity mActivity;
    private String userName;
    private SharePopWindow sharePopWindow;
    //endregion
    private int APPLY_REFUND = 1;
    private static int PAY_SUCCESS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_club_order_detail);
        ButterKnife.bind(this);
        afterViews();
    }

    void afterViews()
    {
        mActivity = this;
        pDialog = DialogUtil.getCommonDialog(mActivity, "正在处理...");
        pDialog.setCancelable(false);
        orderId = getIntent().getStringExtra("orderId");
        userName = SharePreUtils.getNicName(mActivity);
        tbsTopBar.inflateTextCenter(new TopbarSwitchSkeleton()
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
        }).setText("订单详情");


        if (pDialog != null && !pDialog.isShowing())
        {
            pDialog.show();
        }
        if (mPresenter != null)
            mPresenter.getOrderDetail(orderId);
        Logger.t(TAG).d("orderId:" + orderId);
    }

    @Override
    protected ImpIClubOrderRecordDetailPre createPresenter()
    {
        return new ImpIClubOrderRecordDetailPre();
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
        Logger.t(TAG).d("requestCode>>>>>>>" + requestCode);
        //支付页面返回处理
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                String result = data.getExtras().getString("pay_result");
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
//                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
//                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息

                if (result.equals("success"))
                {
                    PayHelper.setIPayFinishedListener(new PayFinish(this));
                    PayHelper.thirdPartyPayStateCheck((Activity) mActivity, new PayMetadataBean("", "", "", "0"));
                } else if (result.equals("cancel"))
                {
                    ToastUtils.showShort("支付取消");
                } else
                {
                    ToastUtils.showShort("支付失败, 请重试");
                }
            }
        } else if (requestCode == APPLY_REFUND ||requestCode == PAY_SUCCESS)
        {
            //申请退款返回刷新
            if (mPresenter != null)
            {
                if (pDialog != null && !pDialog.isShowing())
                {
                    pDialog.show();
                }
                mPresenter.getOrderDetail(orderId);
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (sharePopWindow != null && sharePopWindow.isShowing())
        {
            sharePopWindow.dismiss();
            sharePopWindow = null;
        }
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
        }
        PayHelper.clearPopupWindows();
    }

    private void setViewContent(ClubOrderDetailBean recordBean)
    {
        //将可变的部分都初始化为不可见，然后按照条件来显示

        btnCommentOrder.setVisibility(View.GONE);
        allUnused.setVisibility(View.GONE);
        orderRecordBean = recordBean;
        allOrderDetailBtnCommentDelete.setVisibility(View.GONE);
        String orderStatus = recordBean.getStatus();
        String orderEvaluateStatus = recordBean.getEvaluate();
        if (orderStatus == null)
            return;
        String orderStatusDe = "未知状态";
        //"status":  "0：待付款 , 1：待使用 ,2：待评价, 3：已关闭 ,4：已过期, 5：退款中, 6：已退款 , 7已评价 , 8 过期退".
        switch (orderStatus)
        {
            case "0": //待付款
                Logger.t(TAG).d("待付款");
                orderStatusDe = "待付款";
                allOrderDetailBtnContainer.setVisibility(View.VISIBLE);
                allUnused.setVisibility(View.GONE);
                allCostCode.setVisibility(View.GONE);
                llReServeOrder.setVisibility(View.GONE);
                break;
            case "1":  //已支付未使用
                Logger.t(TAG).d("待使用");
                orderStatusDe = "待使用";
                allUnused.setVisibility(View.VISIBLE);
                allCostCode.setVisibility(View.GONE);
                tvOrderDetailInfo.setVisibility(View.VISIBLE);
                allOrderDetailBtnContainer.setVisibility(View.GONE);
                llReServeOrder.setVisibility(View.VISIBLE);
                break;
            case "2": //待评价
                Logger.t(TAG).d("待评价");
                orderStatusDe = "待评价";
                tvOrderDetailInfo.setVisibility(View.GONE);
                allCostCode.setVisibility(View.VISIBLE);
                allUnused.setVisibility(View.GONE);
                btnCommentOrder.setVisibility(View.VISIBLE);
                allOrderDetailBtnCommentDelete.setVisibility(View.VISIBLE);
                allOrderDetailBtnContainer.setVisibility(View.GONE);
                btnCommentOrder.setTag("评价");
                break;
            case "3"://已关闭
                Logger.t(TAG).d("已关闭");
                orderStatusDe = "已关闭";
                tvOrderDetailInfo.setVisibility(View.GONE);
                allOrderDetailBtnContainer.setVisibility(View.GONE);
                allUnused.setVisibility(View.GONE);
                allCostCode.setVisibility(View.VISIBLE);
                break;
            case "4"://已过期
                Logger.t(TAG).d("已过期");
                orderStatusDe = "已关闭";
                allCostCode.setVisibility(View.GONE);
                tvOrderDetailInfo.setVisibility(View.GONE);
                allUnused.setVisibility(View.GONE);
                llReServeOrder.setVisibility(View.GONE);
                allOrderDetailBtnContainer.setVisibility(View.GONE);
                break;
            case "5"://退款中
                Logger.t(TAG).d("退款中");
                orderStatusDe = "退款中";
                tvOrderDetailInfo.setVisibility(View.VISIBLE);
                allUnused.setVisibility(View.GONE);
                allCostCode.setVisibility(View.GONE);
                allRefund.setVisibility(View.VISIBLE);
                allOrderDetailBtnContainer.setVisibility(View.GONE);
                break;
            case "6"://已退款
                Logger.t(TAG).d("已退款");
                orderStatusDe = "已退款";
                tvOrderDetailInfo.setVisibility(View.VISIBLE);
                allUnused.setVisibility(View.GONE);
                allCostCode.setVisibility(View.GONE);
                allRefund.setVisibility(View.VISIBLE);
                allOrderDetailBtnContainer.setVisibility(View.GONE);
                break;
            case "7"://已评价
                Logger.t(TAG).d("已评价");
                orderStatusDe = "已关闭";//归为已关闭订单状态
                tvOrderDetailInfo.setVisibility(View.GONE);
                allUnused.setVisibility(View.GONE);
                btnCommentOrder.setTag("已评价");
                btnCommentOrder.setText("查看评价");
                allOrderDetailBtnCommentDelete.setVisibility(View.GONE);
                allCostCode.setVisibility(View.VISIBLE);
                allOrderDetailBtnContainer.setVisibility(View.GONE);
                break;
            case "8"://过期退
                Logger.t(TAG).d("过期退");
                orderStatusDe = "过期退";
                tvOrderDetailInfo.setVisibility(View.GONE);
                allUnused.setVisibility(View.GONE);
                allCostCode.setVisibility(View.VISIBLE);
                allOrderDetailBtnContainer.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        if (recordBean.getThemeName()!=null && !recordBean.getThemeName().isEmpty())
        {
            rlThemeRemarkAll.setVisibility(View.VISIBLE);
            tvThemeName.setText(recordBean.getThemeName());
            if (recordBean.getRemark()!=null && !recordBean.getRemark().isEmpty())
            {
                tvReMark.setText(recordBean.getRemark());
            }
            else
            {
                tvReMark.setVisibility(View.GONE);
                tvReMarkTitle.setVisibility(View.GONE);
            }

        }
        else
        {
            vThemeLine.setVisibility(View.GONE);
        }
        ClubFoodDetailListAdapter adapter = new ClubFoodDetailListAdapter(mActivity, recordBean.getFood());
        lvResDishLst.setAdapter(adapter);
        tvOrderDetailOrderId.setText(recordBean.getOrderId());
        tvOrderDetailStatus.setText(orderStatusDe);
        tvOrderDetailTotalCost.setText("￥" + CommonUtils.keep2Decimal(Double.parseDouble(recordBean.getPrice())));
        tvOrderDetailFinalCost.setText("￥" + CommonUtils.keep2Decimal(Double.parseDouble(recordBean.getPrice())));
        tvOrderDetailTime.setText(recordBean.getPayTime());
        tvOrderDetailName.setText(recordBean.getName());
        tvOrderDetailAddress.setText(recordBean.getAddress());
        if (!TextUtils.isEmpty(recordBean.getOCode()) && TextUtils.equals("4", orderStatus))
        {
            allCostCode.setVisibility(View.VISIBLE);
            llReServeOrder.setVisibility(View.VISIBLE);
        }
        if (TextUtils.equals("3", orderStatus))
        {
            if (!TextUtils.isEmpty(recordBean.getOCode()))
            {
                allCostCode.setVisibility(View.VISIBLE);
                llReServeOrder.setVisibility(View.VISIBLE);
            }
            else
            {
                allCostCode.setVisibility(View.GONE);
                llReServeOrder.setVisibility(View.GONE);
            }
        }



        //如果带有退款按钮的item可见，则设置其内容
        if (allUnused.getVisibility() == View.VISIBLE)
        {
            QRCodeBean qrCodeBean = new QRCodeBean();
            qrCodeBean.setType("NORMAl_ORDER_ID");
            qrCodeBean.setContent(recordBean.getOCode());
            String QRStr = new Gson().toJson(qrCodeBean);
            ivShowCodeImg.setImageBitmap(CommonUtils.createQRImage(mActivity, QRStr, 145, 145));
            tvOrderDetailCostNumber.setText(recordBean.getOCode());
            tvOrderDetailValidDate.setText(recordBean.getEndTime());
        } else if (allCostCode.getVisibility() == View.VISIBLE)
        {
            tvOrderDetailCostNumber3.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            tvOrderDetailCostNumber3.setText(recordBean.getOCode());
            String statusCopy = null;
            // "0：待付款 , 1：待使用 ,2：待评价, 3：已关闭 ,4：已过期, 5：退款中, 6：已退款 , 7已评价 , 8 过期退".
            if (orderStatus.equals("7") || orderStatus.equals("2") || orderStatus.equals("3"))
            {
                statusCopy = "已使用";
            } else if (orderStatus.equals("4") || orderStatus.equals("8"))
            {
                statusCopy = "已关闭";
            } else
            {
                statusCopy = "";
            }

            if (orderStatus.equals("4") && !TextUtils.isEmpty(orderRecordBean.getOCode()))
            {
                statusCopy = "";
            }
            String str = TextUtils.isEmpty(statusCopy) ? "" : "(" + statusCopy + ")";
            tvOrderDetailValidDate3.setText(recordBean.getEndTime() + str);
        }

        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
        }
    }


    public static void setOnOrderDeletedListener(IOnDOrderDeletedListener iOnDOrderDeletedListeners)
    {
        iOnDOrderDeletedListener = iOnDOrderDeletedListeners;
    }

    public static void removeOnOrderDeletedListener()
    {
        iOnDOrderDeletedListener = null;
    }


    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        switch (interfaceName)
        {
            case NetInterfaceConstant.HomepartyC_applyRefund:
                if (code.equals(ErrorCodeTable.HOMEPARTY_TIMEOUT) || code.equals(ErrorCodeTable.HOMEPARTY_USED))
                {
                    //刷新页面
                    if (mPresenter != null)
                        mPresenter.getOrderDetail(orderId);
                }
                Logger.t(TAG).d("HomepartyC_applyRefund  错误码为：%s", code);
                break;
            case NetInterfaceConstant.HomepartyC_partyOrderDetails:
                if (code.equals("ORDER_DELED"))
                {
                    ToastUtils.showShort(ErrorCodeTable.parseErrorCode(code));
                    finish();
                }
                Logger.t(TAG).d("HomepartyC_partyOrderDetails  错误码为：%s", code);
                break;
            case NetInterfaceConstant.EvalC_checkEval:
                if (code.equals("EVAL_DELED"))
                {
                    ToastUtils.showShort("此评价由于违反了《看脸吃饭》相关规定，已经被删除");
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void getOrderDetailSuccess(ClubOrderDetailBean orderRecord)
    {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        if (orderRecord == null)
            return;
        setViewContent(orderRecord);
    }

    @Override
    public void getDeleteOrderSuccess(String response)
    {
        Logger.t(TAG).json(response);

        ToastUtils.showShort("删除成功");
        SharePreUtils.setOrderType(mActivity, "normalType");
        Logger.t(TAG).d(iOnDOrderDeletedListener + "");

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
        //订单删除后，返回订单列表

        //如果是从支付过来的详情，包括取消支付，或者支付成功的情况，则返回餐厅详情
        String openStyle = getIntent().getStringExtra(EamConstant.EAM_ORDER_DETAIL_OPEN_SOURCE);
        if (openStyle != null && openStyle.equals("pay"))
        {
            Intent intent = new Intent(mActivity, ClubDetailAct.class);
            //  intent.putExtra("restId", orderRecordBean.getrId());
            startActivity(intent);
        }
        mActivity.finish();
    }


    @Override
    public void getResCommentDetailSuccess(MyResCommentBean resComment)
    {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();

        Intent intentV = new Intent(mActivity, MyCheckOrderCommentAct.class);
        intentV.putExtra("orderType", "normalType");
        intentV.putExtra("commentInfo", resComment);
        mActivity.startActivity(intentV);
    }

    @Override
    public void getApplyRefundSuccess(String response)
    {
        //  ToastUtils.showShort("提交申请成功！我们的工作人员将会尽快为您处理！");
        Logger.t(TAG).d(response);
        try
        {
            JSONObject body = new JSONObject(response);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("refundAmount", body.getString("refundAmount"));
            map.put("payAmount", body.getString("payAmount"));
            map.put("fee", body.getString("fee"));
            map.put("payMethod", body.getString("payMethod"));
            map.put("streamId", body.getString("streamId"));
            map.put("orderId", orderId);
            map.put("orderType", "clubOrder");
            Intent intent = new Intent(mActivity, DApplyRefundAct.class);
            intent.putExtra("applyResult", map);
            mActivity.startActivityForResult(intent, APPLY_REFUND);

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


    @OnClick({R.id.itv_order_detail_call, R.id.btn_order_detail_delete, R.id.btn_order_detail_pay,
            R.id.tv_refund_post, R.id.btn_comment_order, R.id.tv_check_refund_declare, R.id.btn_check_refund_detail})
    void viewClicked(View v)
    {
        switch (v.getId())
        {
            case R.id.itv_order_detail_call:
                if (orderRecordBean.getMobile() != null)
                {
                    CommonUtils.makeCall(mActivity, orderRecordBean.getMobile());
                }
                break;
            case R.id.btn_order_detail_delete:
                new CustomAlertDialog(mActivity)
                        .builder()
                        .setTitle("提示")
                        .setMsg("确定删除订单吗?")
                        .setPositiveButton("确定", (view2) ->
                        {
                            if (pDialog != null && !pDialog.isShowing())
                                pDialog.show();
                            mPresenter.deleteOrder(orderId);
                        })
                        .setNegativeButton("取消", (view) ->
                        {
                        }).show();
                break;
            case R.id.btn_order_detail_pay:
                PayHelper.clearPayHelperListeners();
                PayHelper.setIPayFinishedListener(new PayFinish(this));
                PayBean payBean = new PayBean();
                payBean.setOrderId(orderId);
                payBean.setAmount(tvOrderDetailFinalCost.getText().toString().replace("￥", ""));
                payBean.setSubject("订单");               // 商品的标题
                payBean.setBody("点餐订单支付");               // 商品的描述信息
                payBean.setMyPayType(EamConstant.EAM_PAY_CLUBPAY);
                SharePreUtils.setClubId(mActivity, orderRecordBean.getId());
                PayHelper.clearPopupWindows();
                PayHelper.payOrder(v.getRootView(), payBean, mActivity, new PayMetadataBean("", "", "", "7"));
                break;
            //"申请退款"
            case R.id.tv_refund_post:
                if (pDialog != null && !pDialog.isShowing())
                    pDialog.show();
                mPresenter.applyRefund(orderId);
                break;
            //评价
            case R.id.btn_comment_order:
                //去评价
                if (btnCommentOrder.getTag().equals("评价"))
                {
                    Intent intentC = new Intent(mActivity, DClubOrderCommentAct.class);
                    intentC.putExtra("orderId", orderRecordBean.getOrderId());
                    intentC.putExtra("resId", orderRecordBean.getId());
                    intentC.putExtra("resName", orderRecordBean.getName());
                    mActivity.startActivity(intentC);
                    mActivity.finish();
                } else
                {
//                    if (mPresenter!=null)
//                    mPresenter.getResCommentDetail(orderRecordBean.getOrderId());
                }
                break;
            //查看退款详情
            case R.id.tv_check_refund_declare:
                Intent intentRefund = new Intent(mActivity, DRefundDetailAct.class);
                intentRefund.putExtra("orderId", orderId);
                intentRefund.putExtra("orderType", "clubOrder");
                startActivity(intentRefund);
                break;
            //过期退款查看退款详情
            case R.id.btn_check_refund_detail:
                Intent intentDueRefund = new Intent(mActivity, DRefundDetailAct.class);
                intentDueRefund.putExtra("orderId", orderId);
                intentDueRefund.putExtra(EamConstant.EAM_REFUND_DETAIL_OPEN_SOURCE, "PassDueRefund");
                startActivity(intentDueRefund);
                break;


            default:
                break;
        }
    }


    private static class PayFinish implements IPayFinishedListener
    {
        private final WeakReference<ClubOrderRecordDetailAct> mActRef;

        private PayFinish(ClubOrderRecordDetailAct mAct)
        {
            this.mActRef = new WeakReference<ClubOrderRecordDetailAct>(mAct);
        }

        @Override
        public void PayFinished(String orderId, String streamId, String payType)
        {
            final ClubOrderRecordDetailAct cAct = mActRef.get();
            PayHelper.clearPopupWindows();
            if (cAct != null)
            {
                Intent intent = new Intent(cAct, DClubPayOrderSuccessAct.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("clubId", SharePreUtils.getClubId(cAct));
                cAct.startActivityForResult(intent, PAY_SUCCESS);
                //cAct.finish();
            }
        }

        @Override
        public void PayFailed(String orderId, String streamId, String payType)
        {
            final ClubOrderRecordDetailAct cAct = mActRef.get();
            if (cAct != null)
            {
                ToastUtils.showLong("由于未知原因没有获得支付结果，请勿重复支付，尝试刷新页面");
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        if (sharePopWindow != null && sharePopWindow.isShowing())
        {
            sharePopWindow.dismiss();
        } else
        {
            //如果是从支付过来的详情，包括取消支付，或者支付成功的情况，则返回餐厅详情
            String openStyle = getIntent().getStringExtra(EamConstant.EAM_ORDER_DETAIL_OPEN_SOURCE);
            Logger.t(TAG).d("openStyle:" + openStyle + ",SharePreUtils.getSource(mActivity)：" + SharePreUtils.getSource(mActivity));

            if (openStyle != null && openStyle.equals("pay"))
            {
                Intent intent = new Intent(mActivity, DOrderMealDetailAct.class);
                //  intent.putExtra("restId", orderRecordBean.getrId());
                intent.putExtra("streamId", EamApplication.getInstance().dateStreamId);
                startActivity(intent);
            } else if (SharePreUtils.getSource(mActivity).equals("myColloect"))
            {
                Intent intent = new Intent(mActivity, HomeAct.class);
                intent.putExtra("showPage", 3);
                mActivity.startActivity(intent);
            } else if (SharePreUtils.getSource(mActivity).equals("unPay"))
            {
                Intent intent = new Intent(mActivity, HomeAct.class);
                intent.putExtra("showPage", 3);
                mActivity.startActivity(intent);
            } else
            {
                Intent intent = new Intent();
                intent.putExtra("result", "back");
                mActivity.setResult(EamConstant.EAM_RESULT_NO, intent);
            }
            mActivity.finish();
        }
    }


}

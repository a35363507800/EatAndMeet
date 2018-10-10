package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.listeners.IOnDOrderDeletedListener;
import com.echoesnet.eatandmeet.listeners.IOnDUnpayOrderDeletedListener;
import com.echoesnet.eatandmeet.models.bean.DishBean;
import com.echoesnet.eatandmeet.models.bean.MyResCommentBean;
import com.echoesnet.eatandmeet.models.bean.OrderRecordBean;
import com.echoesnet.eatandmeet.models.bean.PayBean;
import com.echoesnet.eatandmeet.models.bean.PayMetadataBean;
import com.echoesnet.eatandmeet.models.bean.QRCodeBean;
import com.echoesnet.eatandmeet.models.bean.ShareToFaceBean;
import com.echoesnet.eatandmeet.presenters.ImpIDOrderRecordDetailView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDOrderRecordDetailView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.payUtil.IPayFinishedListener;
import com.echoesnet.eatandmeet.utils.payUtil.PayHelper;
import com.echoesnet.eatandmeet.views.adapters.DishDetailLstAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.SharePopopWindow.SharePopWindow;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.google.gson.Gson;
import com.joanzapata.iconify.widget.IconTextView;
import com.linearlistview.LinearListView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import com.pingplusplus.android.Pingpp;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

//订单详情
public class DOrderRecordDetail extends MVPBaseActivity<IDOrderRecordDetailView, ImpIDOrderRecordDetailView> implements IDOrderRecordDetailView
{
    private final static String TAG = DOrderRecordDetail.class.getSimpleName();
    @BindView(R.id.tbs_top_bar)
    TopBarSwitch tbsTopBar;
    private int APPLY_REFUND = 1;
    //region 变量
    @BindView(R.id.tv_order_detail_order_id)
    TextView tvOrderId;
    @BindView(R.id.tv_order_detail_status)
    TextView tvOrderStatus;
    @BindView(R.id.tv_order_detail_total_cost)
    TextView tvOrderTotalCost;
    @BindView(R.id.tv_order_detail_discount)
    TextView tvOrderDiscount;
    //最终付款
    @BindView(R.id.tv_order_detail_final_cost)
    TextView tvOrderFinalCost;
    @BindView(R.id.tv_order_detail_phone)
    TextView tvOrderPhoneNum;
    @BindView(R.id.tv_order_detail_user)
    TextView tvOrderUserName;
    @BindView(R.id.tv_order_detail_time)
    TextView tvOrderBookTime;
    @BindView(R.id.tv_order_detail_res_name)
    TextView tvOrderResName;
    @BindView(R.id.tv_order_detail_address)
    TextView tvOrderResAddress;
    @BindView(R.id.itv_order_detail_call)
    IconTextView tvOrderCall;
    //退款
    @BindView(R.id.tv_refund_post)
    TextView tvRefundOperate;


    @BindView(R.id.btn_order_detail_delete)
    Button btnDeleteOrder;
    @BindView(R.id.btn_order_comment_delete)
    Button btnDeleteOrderUncomment;
    @BindView(R.id.btn_order_detail_pay)
    Button btnPayOrder;
    @BindView(R.id.btn_comment_order)
    Button btnCommentOrder;
    //求代付按钮
    @BindView(R.id.itv_friend_pay)
    IconTextView ITvFriendPay;
    //查看过期退款详情
    @BindView(R.id.btn_check_refund_detail)
    Button btnCheckRefundDetail;
    @BindView(R.id.iv_show_code_img)
    ImageView ivCustomCodeImg;

    @BindView(R.id.host_userHeader)
    RoundedImageView hostUserHeader;
    @BindView(R.id.host_userName)
    TextView hostUserName;
    @BindView(R.id.host_userId)
    TextView hostUserId;
    @BindView(R.id.tui_anchorLayout)
    AutoLinearLayout tuiAnchorLayout;


    @BindView(R.id.all_order_detail_btn_container)
    AutoLinearLayout allBtnsContainer;
    //带删除的评价
    @BindView(R.id.all_order_detail_btn_comment_delete)
    AutoLinearLayout allBtnsComment;
    //带退款按钮
    @BindView(R.id.all_unused)
    AutoLinearLayout allUnusedItemContainer;
    @BindView(R.id.tv_order_detail_cost_number)
    TextView tvCostCode1;
    @BindView(R.id.tv_order_detail_valid_date)
    TextView tvValidateTime1;
    //带退款说明
    @BindView(R.id.all_refund)
    AutoLinearLayout allRefundItemContainer;
    @BindView(R.id.tv_order_detail_cost_number2)
    TextView tvCostCode2;
    @BindView(R.id.tv_order_detail_valid_date2)
    TextView tvValidateTime2;
    @BindView(R.id.tv_check_refund_declare)
    TextView tvCheckDeclare;
    //通用消费码
    @BindView(R.id.all_cost_code)
    AutoLinearLayout allCommonItemContainer;
    @BindView(R.id.tv_order_detail_cost_number3)
    TextView tvCostCode3;
    @BindView(R.id.tv_order_detail_valid_date3)
    TextView tvValidateTime3;

    @BindView(R.id.lv_res_dish_lst)
    LinearListView llvDishLst;
    // 添加备注
    @BindView(R.id.tv_remark)
    TextView tvRemark;
    @BindView(R.id.all_remark)
    AutoLinearLayout allRemark;
    @BindView(R.id.img_order_booty_call)
    ImageView bootyCallImg;
    @BindView(R.id.ll_consultant)
    LinearLayout consultantLinea;
    @BindView(R.id.rimg_consultant_face)
    RoundedImageView consultantFaceImg;
    @BindView(R.id.tv_consultant_name)
    TextView consultantNameTv;

    private static IOnDOrderDeletedListener iOnDOrderDeletedListener;//全部订单删除回调
    private static IOnDUnpayOrderDeletedListener iOnDUnpayOrderDeletedListener;//待支付订单删除回调

    private String orderId;
    private String[] phoneNum;
    private OrderRecordBean orderRecordBean;

    private Dialog pDialog;
    private Activity mActivity;
    private String userName;
    private SharePopWindow sharePopWindow;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_order_record_detail);
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
        initPopWindow();
        tbsTopBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                //如果是从支付过来的详情，包括取消支付，或者支付成功的情况，则返回餐厅详情
                String openStyle = getIntent().getStringExtra(EamConstant.EAM_ORDER_DETAIL_OPEN_SOURCE);
                Logger.t(TAG).d("openStyle:" + openStyle + ",SharePreUtils.getSource(mActivity)：" + SharePreUtils.getSource(mActivity));
                if (openStyle != null && openStyle.equals("pay"))
                {
                    Intent intent = new Intent(mActivity, DOrderMealDetailAct.class);
                    intent.putExtra("restId", orderRecordBean.getrId());
//                    intent.putExtra("resName", orderRecordBean.getrName());
//                    //intent.putExtra("location", new String[]{SharePreUtils.getLatitude(mActivity), SharePreUtils.getLongitude(mActivity)});
//                    intent.putExtra("location", EamApplication.getInstance().geoPosition);
//                    intent.putExtra("lessPrice", EamApplication.getInstance().lessPrice);
                    intent.putExtra("streamId", EamApplication.getInstance().dateStreamId);
                    startActivity(intent);
                }
                else if (SharePreUtils.getSource(mActivity).equals("myColloect"))
                {
                    Intent intent = new Intent(mActivity, HomeAct.class);
                    intent.putExtra("showPage", 3);
                    mActivity.startActivity(intent);
                }
                else if (SharePreUtils.getSource(mActivity).equals("unPay"))
                {
                    Intent intent = new Intent(mActivity, HomeAct.class);
                    intent.putExtra("showPage", 3);
                    mActivity.startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent();
                    intent.putExtra("result", "back");
                    mActivity.setResult(EamConstant.EAM_RESULT_NO, intent);
                }
                mActivity.finish();
            }

            @Override
            public void right2Click(View view)
            {

            }
        }).setText("订单详情");


        // Logger.t(TAG).d("orderId"+);
        orderRecordBean = (OrderRecordBean) getIntent().getSerializableExtra("orderRecord");
        if (orderRecordBean != null)
        {
            if (pDialog != null && !pDialog.isShowing())
            {
                pDialog.show();
            }
            setViewContent(orderRecordBean);
        }
        else
        {
            if (mPresenter != null)
            {
                if (pDialog != null && !pDialog.isShowing())
                {
                    pDialog.show();
                }
                mPresenter.getOrderDetail(orderId);
            }

        }
        Logger.t(TAG).d("orderId:" + orderId);
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
                    PayHelper.setIPayFinishedListener(new PayFinish(DOrderRecordDetail.this));
                    PayHelper.thirdPartyPayStateCheck((Activity) mActivity, new PayMetadataBean("", "", "", "0"));
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
        else if (requestCode == APPLY_REFUND)
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

    @Override
    protected ImpIDOrderRecordDetailView createPresenter()
    {
        return new ImpIDOrderRecordDetailView();
    }


    private void setViewContent(OrderRecordBean recordBean)
    {
        //将可变的部分都初始化为不可见，然后按照条件来显示
        allBtnsContainer.setVisibility(View.GONE);
        allBtnsComment.setVisibility(View.GONE);
        btnCommentOrder.setVisibility(View.GONE);
        ITvFriendPay.setVisibility(View.GONE);
        allUnusedItemContainer.setVisibility(View.GONE);
        allCommonItemContainer.setVisibility(View.GONE);
        allRefundItemContainer.setVisibility(View.GONE);
        bootyCallImg.setVisibility(View.GONE);

        if (TextUtils.isEmpty(recordBean.getAnchorId()))
        {
            tuiAnchorLayout.setVisibility(View.GONE);
        }
        else
        {
            hostUserName.setText(recordBean.getAnchorName());
            hostUserId.setText("(" + recordBean.getAnchorId() + ")");
            GlideApp.with(mActivity)
                    .asBitmap()
                    .load(recordBean.getAnchorUrl())
                    .placeholder(R.drawable.userhead)
                    .into(hostUserHeader);
        }
        consultantLinea.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(recordBean.getConsultant()))
        {
            consultantLinea.setVisibility(View.VISIBLE);
            consultantNameTv.setText(recordBean.getConsultantName());
            GlideApp.with(mActivity)
                    .asBitmap()
                    .load(recordBean.getConsultantPhurl())
                    .centerCrop()
                    .placeholder(R.drawable.userhead)
                    .into(consultantFaceImg);
        }

        if (!TextUtils.isEmpty(recordBean.getReceiveId()))
        {
            bootyCallImg.setVisibility(View.VISIBLE);
        }
        orderRecordBean = recordBean;
        String orderStatus = recordBean.getStatus();
        if (orderStatus == null)
            return;
        String orderStatusDe = "未知状态";
        Logger.t(TAG).d("orderStatusDe:" + orderStatusDe + ",orderStatus:" + orderStatus);
        switch (orderStatus)
        {
            //待付款
            case "0":
                Logger.t(TAG).d("待付款");
                orderStatusDe = "待付款";
                allBtnsContainer.setVisibility(View.VISIBLE);
                break;
            //待使用
            case "1":
                Logger.t(TAG).d("待使用");
                orderStatusDe = "待使用";
                allUnusedItemContainer.setVisibility(View.VISIBLE);
                mPresenter.getBtnOnOff();
                SharePreUtils.setSource(mActivity, "myInfo");
                break;
            //待评价
            case "2":
                Logger.t(TAG).d("待评价");
                orderStatusDe = "待评价";
                btnCommentOrder.setVisibility(View.VISIBLE);
                btnCommentOrder.setTag("评价");
                allCommonItemContainer.setVisibility(View.VISIBLE);
                allBtnsComment.setVisibility(View.VISIBLE);
                break;
            case "3":
                Logger.t(TAG).d("已关闭");
                orderStatusDe = "已关闭";
                break;
            case "4":
                Logger.t(TAG).d("已过期");
                orderStatusDe = "已过期";
                allCommonItemContainer.setVisibility(View.VISIBLE);
                break;
            case "5":
                Logger.t(TAG).d("退款中");
                orderStatusDe = "退款中";
                allRefundItemContainer.setVisibility(View.VISIBLE);
                break;
            case "6":
                Logger.t(TAG).d("已退款");
                orderStatusDe = "已退款";
                allRefundItemContainer.setVisibility(View.VISIBLE);
                break;
            case "7":
                Logger.t(TAG).d("已完成");
                //将已正常消费评价完成的item归入已关闭类
                orderStatusDe = "已关闭";
                allBtnsComment.setVisibility(View.VISIBLE);
                btnCommentOrder.setVisibility(View.VISIBLE);
                btnDeleteOrderUncomment.setVisibility(View.GONE);
                btnCommentOrder.setTag("查看评价");
                btnCommentOrder.setText("查看评价");
                break;
            case "8":
                //将系统退款的item归入已关闭类
                Logger.t(TAG).d("已关闭");
                orderStatusDe = "已关闭";
                btnCheckRefundDetail.setVisibility(View.VISIBLE);
            default:
                break;
        }

        DishDetailLstAdapter adapter = new DishDetailLstAdapter(mActivity, recordBean.getDishBeen());
        llvDishLst.setAdapter(adapter);
        tvOrderId.setText(recordBean.getOrdId());
        tvOrderTotalCost.setText(CommonUtils.keep2Decimal(Double.parseDouble(recordBean.getOrderCos1())));
        tvOrderFinalCost.setText(CommonUtils.keep2Decimal(Double.parseDouble(recordBean.getOrderCos2())));
        tvOrderPhoneNum.setText(recordBean.getMobile());
        tvOrderUserName.setText(String.format("%s  %s", recordBean.getNicName(), recordBean.getSex()));
        String sitInfo[] = recordBean.getSits().split(CommonUtils.SEPARATOR);
        if (sitInfo.length == 3)
        {
            tvOrderBookTime.setText(String.format("%s %s层%s %s人桌", recordBean.getOrderTime(),
                    sitInfo[0].substring(sitInfo[0].length() - 2), sitInfo[1], sitInfo[2]));
        }
        else if (sitInfo.length >= 6)
        {
            tvOrderBookTime.setText(String.format("%s %s层%s(%s人桌) %s层%s(%s人桌)", recordBean.getOrderTime(),
                    sitInfo[0].substring(sitInfo[0].length() - 2), sitInfo[1], sitInfo[2], sitInfo[3].substring(sitInfo[3].length() - 2), sitInfo[4], sitInfo[5]));
        }

        tvOrderResName.setText(recordBean.getrName());
        tvOrderResAddress.setText(recordBean.getResAddr());
        tvOrderStatus.setText(orderStatusDe);
        phoneNum = recordBean.getResMobile().split(CommonUtils.SEPARATOR);

        if (TextUtils.isEmpty(recordBean.getRemark()))
        {
            allRemark.setVisibility(View.GONE);
        }
        else
        {
            tvRemark.setText("备注：" + recordBean.getRemark());
        }


        //如果带有退款按钮的item可见，则设置其内容
        if (allUnusedItemContainer.getVisibility() == View.VISIBLE)
        {
            QRCodeBean qrCodeBean = new QRCodeBean();
            if (!TextUtils.isEmpty(recordBean.getReceiveId()))
            {
                qrCodeBean.setType("DATE_ORDER_ID");
            }
            else
            {
                qrCodeBean.setType("NORMAl_ORDER_ID");
            }
            qrCodeBean.setContent(recordBean.getoCode());
            String QRStr = new Gson().toJson(qrCodeBean);
            ivCustomCodeImg.setImageBitmap(CommonUtils.createQRImage(mActivity, QRStr, 145, 145));
            tvCostCode1.setText(recordBean.getoCode());
            tvValidateTime1.setText(recordBean.getOverTime());
        }
        else if (allRefundItemContainer.getVisibility() == View.VISIBLE)
        {
            tvCostCode2.setText(recordBean.getoCode());
            tvValidateTime2.setText(recordBean.getOverTime());
        }
        else if (allCommonItemContainer.getVisibility() == View.VISIBLE)
        {
            tvCostCode3.setText(recordBean.getoCode());
            tvValidateTime3.setText(recordBean.getOverTime());
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


    public static void setOnUnpayOrderDeletedListener(IOnDUnpayOrderDeletedListener iOnDOrderDeletedListeners)
    {
        iOnDUnpayOrderDeletedListener = iOnDOrderDeletedListeners;
    }

    private void initPopWindow()
    {
        ShareToFaceBean shareBean = new ShareToFaceBean();
        shareBean.setShareType(Platform.SHARE_WEBPAGE);
        shareBean.setShareTitle("帮我付款才是真友谊");
        shareBean.setShareWeChatMomentsTitle("帮我付款才是真友谊");
        shareBean.setShareListener(new PlatformActionListener()
        {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap)
            {
                mActivity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ToastUtils.showShort("分享成功");
                    }
                });
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable)
            {
                mActivity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ToastUtils.showShort("分享错误");
                    }
                });
            }

            @Override
            public void onCancel(Platform platform, int i)
            {
                mActivity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ToastUtils.showShort("分享取消");
                    }
                });
            }
        });
        shareBean.setShareUrl(NetHelper.SHARE_ORDERDS_ADDRESS + "/pay/index.html?orderId=" + orderId);
        shareBean.setShareContent("你的一小笔开支，是增进我们关系的一大步，麻溜儿的付款吧～");
        sharePopWindow = new SharePopWindow(mActivity, new int[]{SharePopWindow.SHARE_WAY_WECHAT_FRIEND,
                SharePopWindow.SHARE_WAY_WECHAT_MOMENT}, shareBean);
    }


    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        switch (interfaceName)
        {
            case NetInterfaceConstant.OrderC_applyRefund:
                if (code.equals("REFUNDING") || code.equals("ORDER_CANNOT_REFUND"))
                {
                    //刷新页面
                    if (pDialog != null && !pDialog.isShowing())
                        pDialog.show();
                    if (mPresenter != null)
                        mPresenter.getOrderDetail(orderId);
                }

                Logger.t(TAG).d("错误码为：%s", code);
                break;
            case NetInterfaceConstant.OrderC_orderDetail:
                if (code.equals("ORDER_DELED"))
                {
                    ToastUtils.showShort(ErrorCodeTable.parseErrorCode(code));
                    finish();
                }
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
    public void getOrderDetailSuccess(OrderRecordBean orderRecord)
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

        ToastUtils.showShort("删除订单成功");
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
            Intent intent = new Intent(mActivity, DOrderMealDetailAct.class);
            intent.putExtra("restId", orderRecordBean.getrId());
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
        Logger.t(TAG).json(response);
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
            map.put("orderType", "normalOrder");
            Intent intent = new Intent(mActivity, DApplyRefundAct.class);
            intent.putExtra("applyResult", map);
            mActivity.startActivityForResult(intent, APPLY_REFUND);
            //setUiContent(map);

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

    @Override
    public void getShareOrderSuccess(String response)
    {
        sharePopWindow.showPopupWindow(mActivity.findViewById(R.id.main), null);
    }

    @Override
    public void getBtnOnOffSuccess(String response)
    {
        Logger.t(TAG).d("getBtnOnOffSuccess:" + response);
        if ("0".equals(response))
        {
            ITvFriendPay.setVisibility(View.VISIBLE);
        }
        else
        {
            ITvFriendPay.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.itv_order_detail_call, R.id.btn_order_detail_delete, R.id.btn_order_comment_delete, R.id.btn_order_detail_pay,
            R.id.tv_refund_post, R.id.btn_comment_order, R.id.tv_check_refund_declare, R.id.btn_check_refund_detail, R.id.itv_friend_pay})
    void viewClicked(View v)
    {
        switch (v.getId())
        {
            case R.id.itv_order_detail_call:
               /* try
                {
                    String permission = "android.permission.CALL_PHONE";
                    mActivity.enforceCallingPermission(permission, "请打开通话权限");
                }catch (SecurityException se)
                {
                    ToastUtils.showShort(mActivity,"请打开通话权限");
                   break;
                }*/
                if (phoneNum != null)
                {
                    CommonUtils.makeCall(mActivity, phoneNum[0]);
                }
                break;
            case R.id.btn_order_detail_delete:
                new CustomAlertDialog(mActivity)
                        .builder()
                        .setTitle("提示")
                        .setMsg("确定删除订单吗?")
                        .setPositiveButton("确定", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                if (pDialog != null && !pDialog.isShowing())
                                    pDialog.show();
                                mPresenter.deleteOrder(orderId);
                            }
                        })
                        .setNegativeButton("取消", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                            }
                        }).show();
                break;
            case R.id.btn_order_comment_delete:
                new CustomAlertDialog(mActivity)
                        .builder()
                        .setTitle("提示")
                        .setMsg("确定删除订单吗?")
                        .setPositiveButton("确定", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                if (pDialog != null && !pDialog.isShowing())
                                    pDialog.show();
                                mPresenter.deleteOrder(orderId);
                            }
                        })
                        .setNegativeButton("取消", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                            }
                        }).show();
                break;
            case R.id.btn_order_detail_pay:
                PayHelper.clearPayHelperListeners();
                PayHelper.setIPayFinishedListener(new PayFinish(this));
                PayBean payBean = new PayBean();
                payBean.setOrderId(orderId);
                payBean.setAmount(tvOrderFinalCost.getText().toString());
                payBean.setSubject("订单");               // 商品的标题
                payBean.setBody("点餐订单支付");               // 商品的描述信息
                PayHelper.payOrder(v.getRootView(), payBean, mActivity, new PayMetadataBean("", "", "", "0"));
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
                    Logger.t(TAG).d("111111111111111");
                    Intent intentC = new Intent(mActivity, DOrderCommentAct.class);
                    intentC.putExtra("orderId", orderRecordBean.getOrdId());
                    intentC.putExtra("resId", orderRecordBean.getrId());
                    intentC.putExtra("dishLst", (ArrayList<DishBean>) orderRecordBean.getDishBeen());
                    intentC.putExtra("orderType", "normalType");
                    mActivity.startActivity(intentC);
                    mActivity.finish();
                }
                else
                {
                    Logger.t(TAG).d("2222222222222222");
                    if (pDialog != null && !pDialog.isShowing())
                        pDialog.show();
                    mPresenter.getResCommentDetail(orderRecordBean.getOrdId());
                }
                break;
            //查看退款详情
            case R.id.tv_check_refund_declare:
                Intent intentRefund = new Intent(mActivity, DRefundDetailAct.class);
                intentRefund.putExtra("orderId", orderId);
                //intentRefund.putExtra(EamConstant.EAM_REFUND_DETAIL_OPEN_SOURCE,"DOrderRecordDetail");
                startActivity(intentRefund);
                break;
            //过期退款查看退款详情
            case R.id.btn_check_refund_detail:
                Intent intentDueRefund = new Intent(mActivity, DRefundDetailAct.class);
                intentDueRefund.putExtra("orderId", orderId);
                intentDueRefund.putExtra(EamConstant.EAM_REFUND_DETAIL_OPEN_SOURCE, "PassDueRefund");
                startActivity(intentDueRefund);
                break;
            case R.id.itv_friend_pay:
                mPresenter.shareOrder(orderId);
                break;
            default:
                break;
        }
    }


    private static class PayFinish implements IPayFinishedListener
    {
        private final WeakReference<DOrderRecordDetail> mActRef;

        private PayFinish(DOrderRecordDetail mAct)
        {
            this.mActRef = new WeakReference<DOrderRecordDetail>(mAct);
        }

        @Override
        public void PayFinished(String orderId, String streamId, String payType)
        {
            final DOrderRecordDetail cAct = mActRef.get();
            if (cAct != null)
            {
                Intent intent = new Intent(cAct, DPayOrderSuccessAct.class);
                intent.putExtra("orderId", orderId);
                cAct.startActivity(intent);
                cAct.finish();
            }
        }

        @Override
        public void PayFailed(String orderId, String streamId, String payType)
        {
            final DOrderRecordDetail cAct = mActRef.get();
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
        }
        else
        {
//            finish();
            //如果是从支付过来的详情，包括取消支付，或者支付成功的情况，则返回餐厅详情
            String openStyle = getIntent().getStringExtra(EamConstant.EAM_ORDER_DETAIL_OPEN_SOURCE);
            Logger.t(TAG).d("openStyle:" + openStyle + ",SharePreUtils.getSource(mActivity)：" + SharePreUtils.getSource(mActivity));

            if (openStyle != null && openStyle.equals("pay"))
            {
                Intent intent = new Intent(mActivity, DOrderMealDetailAct.class);
                intent.putExtra("restId", orderRecordBean.getrId());
//                    intent.putExtra("resName", orderRecordBean.getrName());
//                    //intent.putExtra("location", new String[]{SharePreUtils.getLatitude(mActivity), SharePreUtils.getLongitude(mActivity)});
//                    intent.putExtra("location", EamApplication.getInstance().geoPosition);
//                    intent.putExtra("lessPrice", EamApplication.getInstance().lessPrice);
                intent.putExtra("streamId", EamApplication.getInstance().dateStreamId);
                startActivity(intent);
            }
            else if (SharePreUtils.getSource(mActivity).equals("myColloect"))
            {
                Intent intent = new Intent(mActivity, HomeAct.class);
                intent.putExtra("showPage", 3);
                mActivity.startActivity(intent);
            }
            else if (SharePreUtils.getSource(mActivity).equals("unPay"))
            {
                Intent intent = new Intent(mActivity, HomeAct.class);
                intent.putExtra("showPage", 3);
                mActivity.startActivity(intent);
            }
            else
            {
                Intent intent = new Intent();
                intent.putExtra("result", "back");
                mActivity.setResult(EamConstant.EAM_RESULT_NO, intent);
            }
            mActivity.finish();
        }
    }
}

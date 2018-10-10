package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.ClubOrderRecordDetailAct;
import com.echoesnet.eatandmeet.activities.DApplyRefundAct;
import com.echoesnet.eatandmeet.activities.DClubOrderCommentAct;
import com.echoesnet.eatandmeet.activities.DClubPayOrderSuccessAct;
import com.echoesnet.eatandmeet.activities.DOrderCommentAct;
import com.echoesnet.eatandmeet.activities.DOrderRecordDetail;
import com.echoesnet.eatandmeet.activities.DPayOrderSuccessAct;
import com.echoesnet.eatandmeet.activities.MyOrdersAct;
import com.echoesnet.eatandmeet.models.bean.DishBean;
import com.echoesnet.eatandmeet.models.bean.OrderRecordBean;
import com.echoesnet.eatandmeet.models.bean.PayBean;
import com.echoesnet.eatandmeet.models.bean.PayMetadataBean;
import com.echoesnet.eatandmeet.models.bean.QRCodeBean;
import com.echoesnet.eatandmeet.models.bean.ShareToFaceBean;
import com.echoesnet.eatandmeet.presenters.ImpIAllOrdersView;
import com.echoesnet.eatandmeet.presenters.ImpIOrderRecordLstAdapterView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IOrderRecordLstAdapterView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.payUtil.IPayFinishedListener;
import com.echoesnet.eatandmeet.utils.payUtil.PayHelper;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.SharePopopWindow.SharePopWindow;
import com.google.gson.Gson;
import com.linearlistview.LinearListView;
import com.mob.MobSDK;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import okhttp3.Call;

/**
 * Created by wangben on 2016/6/20.
 * Adapter应该看做是包含它的View的一部分，不应该看做是业务处理层，应该看做View层，所以此处的写法是不合适的
 * 相关人员参看 CNewFriendsAdapter 写法 --wb
 */
public class OrderRecordLstAdapter extends BaseAdapter implements IOrderRecordLstAdapterView
{
    public static final String TAG = OrderRecordLstAdapter.class.getSimpleName();
    private Activity mActivity;
    private ImpIAllOrdersView impIAllOrdersView;
    private ImpIOrderRecordLstAdapterView adapterView;
    private Dialog pDialog;
    private List<OrderRecordBean> orderRecordLst;
    private String btnOnOff;
    private SharePopWindow sharePopWindow;
    private String userName;
    private String orderIdStr;
    private String typeStr;
    private IOnItemClickListener itemClickListener;
    //private Timer timer;

    private int tempPosition;

    public void setBtnOnOff(String btnOnOff)
    {
        this.btnOnOff = btnOnOff;
    }

    public OrderRecordLstAdapter(Activity mActivity, List<OrderRecordBean> meetPersonLst)
    {
        this.mActivity = mActivity;
        this.orderRecordLst = meetPersonLst;
        //timer=new Timer();
        pDialog = DialogUtil.getCommonDialog(mActivity, "正在处理...");
        pDialog.setCancelable(true);
        userName = SharePreUtils.getNicName(mActivity);
        MobSDK.init(mActivity, EamConstant.SHARESDK_APPKEY, EamConstant.SHARESDK_APPSECRET);
        adapterView = new ImpIOrderRecordLstAdapterView(mActivity, this);
    }

    public void setImpIAllOrdersView(ImpIAllOrdersView impIAllOrdersView, String typeStr)
    {
        this.impIAllOrdersView = impIAllOrdersView;
        this.typeStr = typeStr;
    }

    @Override
    public int getCount()
    {
        return orderRecordLst.size();
    }

    @Override
    public OrderRecordBean getItem(int position)
    {
        return orderRecordLst.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final OrderRecordBean orderRecordBean = getItem(position);
           Logger.t(TAG).d("orderRecordBean>>"+orderRecordBean.getHomeparty()+",status"+orderRecordBean.getStatus()+orderRecordBean.getOrderId());
         //是轰趴餐馆
         if (orderRecordBean.getHomeparty().equals("1"))
         {
             final TextView orderId, orderTime, resName, orderValidity, orderLeftTime, orderTotalCost, tvOrderStatus, tvDishCount;
             LinearListView dishLst;
              Button tvFriendPay =null,btnCheckCustomCode =null;
             //":"0：待付款 1：待使用 2：待评价 3：已关闭 4：已过期 5：退款中 6：已退款 7已评价 8 过期退",
             String orderStatus = orderRecordBean.getStatus();
             if (orderStatus.equals("0"))
             {
                 convertView = LayoutInflater.from(mActivity).inflate(R.layout.litem_club_order_unpay, null);
                 orderId = (TextView) convertView.findViewById(R.id.tv_order_id);
                 tvOrderStatus = (TextView) convertView.findViewById(R.id.tv_order_type);
                 orderTime = (TextView) convertView.findViewById(R.id.tv_order_time);
                 resName = (TextView) convertView.findViewById(R.id.tv_order_res_name);
                 dishLst = (LinearListView) convertView.findViewById(R.id.lv_order_dish_lst);
                 orderTotalCost = (TextView) convertView.findViewById(R.id.tv_order_total_money);
                 tvDishCount = (TextView) convertView.findViewById(R.id.tv_order_dish_count);
                 tvFriendPay = (Button) convertView.findViewById(R.id.tv_friend_pay);
                 btnCheckCustomCode = (Button) convertView.findViewById(R.id.btn_order_check_code);


                 LinearLayout llUnPayView = (LinearLayout) convertView.findViewById(R.id.ll_unPayView);
                 TextView tvOrderPayInUse = (TextView) convertView.findViewById(R.id.tv_order_pay_inUse);
                 tvFriendPay.setOnClickListener(new View.OnClickListener()
                 {
                     @Override
                     public void onClick(View v)
                     {
                         Logger.t(TAG).d("删除订单");
                         showDelDialog(orderRecordBean.getOrdId(), position,true);
                     }
                 });

                 btnCheckCustomCode.setVisibility(View.VISIBLE);
                 btnCheckCustomCode.setOnClickListener(new View.OnClickListener()
                 {
                     @Override
                     public void onClick(View anchorView)
                     {
                         orderIdStr = orderRecordBean.getOrdId();
                         PayHelper.clearPayHelperListeners();
                         PayHelper.setIPayFinishedListener(new PayClubFinish(mActivity));
                         PayBean payBean = new PayBean();
                         payBean.setOrderId(orderRecordBean.getOrdId());
                         payBean.setAmount(orderRecordBean.getPrice());
                         payBean.setSubject("订单");               // 商品的标题
                         payBean.setBody("点餐订单支付");               // 商品的描述信息
                         payBean.setMyPayType(EamConstant.EAM_PAY_CLUBPAY);
                         SharePreUtils.setClubId(mActivity,orderRecordBean.getrId());
                         if(mActivity!=null && !mActivity.isFinishing())
                         ((MyOrdersAct)mActivity).setPayOrderType();

                         PayHelper.payOrder(anchorView, payBean, mActivity, new PayMetadataBean("", "", "", "7"));
                     }
                 });
                 tvOrderPayInUse.setText(orderRecordBean.getOverTime2());
             } else if (orderStatus.equals("1"))//1已支付未使用
             {
                 convertView = LayoutInflater.from(mActivity).inflate(R.layout.litem_club_order_pay, null);
                 orderId = (TextView) convertView.findViewById(R.id.tv_order_id);
                 tvOrderStatus = (TextView) convertView.findViewById(R.id.tv_order_type);
                 orderTime = (TextView) convertView.findViewById(R.id.tv_order_time);
                 resName = (TextView) convertView.findViewById(R.id.tv_order_res_name);
                 dishLst = (LinearListView) convertView.findViewById(R.id.lv_order_dish_lst);
                 orderTotalCost = (TextView) convertView.findViewById(R.id.tv_order_total_money);
                 tvDishCount = (TextView) convertView.findViewById(R.id.tv_order_dish_count);
                 tvFriendPay = (Button) convertView.findViewById(R.id.tv_friend_pay);
                  btnCheckCustomCode = (Button) convertView.findViewById(R.id.btn_order_check_code);
                 btnCheckCustomCode.setOnClickListener(new View.OnClickListener()
                 {
                     @Override
                     public void onClick(View v)
                     {
                         Logger.t(TAG).d("查看消费码");
                         checkCustomCode(orderRecordBean.getOrdId(), orderRecordBean.getoCode(),
                                 orderRecordBean.getOverTime(), orderRecordBean.getrName(), orderRecordBean.getReceiveId(), mActivity,false);
                     }
                 });

                 tvFriendPay.setVisibility(View.VISIBLE);
                 tvFriendPay.setOnClickListener(new View.OnClickListener()
                 {
                     @Override
                     public void onClick(View v)
                     {
                         orderIdStr = orderRecordBean.getOrdId();
//                         if (pDialog != null && !pDialog.isShowing())
//                             pDialog.show();
                         if (adapterView != null)
                             adapterView.applyRefundClub(orderIdStr);
                     }
                 });
             }
             else if (orderStatus.equals("2") || orderStatus.equals("5")|| orderStatus.equals("6")|| orderStatus.equals("4"))
             {
                 convertView = LayoutInflater.from(mActivity).inflate(R.layout.litem_club_order_finishi, null);
                 orderId = (TextView) convertView.findViewById(R.id.tv_order_id);
                 tvOrderStatus = (TextView) convertView.findViewById(R.id.tv_order_type);
                 orderTime = (TextView) convertView.findViewById(R.id.tv_order_time);
                 resName = (TextView) convertView.findViewById(R.id.tv_order_res_name);
                 dishLst = (LinearListView) convertView.findViewById(R.id.lv_order_dish_lst);
                 orderTotalCost = (TextView) convertView.findViewById(R.id.tv_order_total_money);
                 tvDishCount = (TextView) convertView.findViewById(R.id.tv_order_dish_count);
                 tvFriendPay = (Button) convertView.findViewById(R.id.tv_friend_pay);
                 btnCheckCustomCode = (Button) convertView.findViewById(R.id.btn_order_check_code);
                 tvFriendPay.setOnClickListener(new View.OnClickListener()
                 {
                     @Override
                     public void onClick(View view)
                     {
                         showDelDialog(orderRecordBean.getOrdId(), position,true);
                     }
                 });
                 btnCheckCustomCode.setOnClickListener(new View.OnClickListener()
                 {
                     @Override
                     public void onClick(View view)
                     {
                         switch (orderStatus)
                         {
                             case "2":
                                 Logger.t(TAG).d("待评价");
                                 Intent intent = new Intent(mActivity, DClubOrderCommentAct.class);
                                 intent.putExtra("orderId", orderRecordBean.getOrdId());
                                 intent.putExtra("resId", orderRecordBean.getrId());
                                 intent.putExtra("resName", orderRecordBean.getHpName());
                                 mActivity.startActivity(intent);
                                 break;
                         }
                     }
                 });

             }
             else
             {
                 convertView = LayoutInflater.from(mActivity).inflate(R.layout.litem_club_order_refunding, null);
                 orderId = (TextView) convertView.findViewById(R.id.tv_order_id);
                 tvOrderStatus = (TextView) convertView.findViewById(R.id.tv_order_type);
                 orderTime = (TextView) convertView.findViewById(R.id.tv_order_time);
                 resName = (TextView) convertView.findViewById(R.id.tv_order_res_name);
                 dishLst = (LinearListView) convertView.findViewById(R.id.lv_order_dish_lst);
                 orderTotalCost = (TextView) convertView.findViewById(R.id.tv_order_total_money);
                 tvDishCount = (TextView) convertView.findViewById(R.id.tv_order_dish_count);
                 tvFriendPay = (Button) convertView.findViewById(R.id.tv_friend_pay);
                 btnCheckCustomCode = (Button) convertView.findViewById(R.id.btn_order_check_code);
                 tvFriendPay.setOnClickListener(new View.OnClickListener()
             {
                 @Override
                 public void onClick(View view)
                 {
                     showDelDialog(orderRecordBean.getOrdId(), position,true);
                 }
             });


             }

             String stausText = "";
             switch (orderStatus)
             {
                 case "0":
                     stausText = "待付款";
                     break;
                 case "1":
                     stausText = "待使用";
                     break;
                 case "2":
                     stausText = "待评价";
                     break;
                 case "3":
                     stausText = "已关闭";
                     btnCheckCustomCode.setVisibility(View.GONE);
                     break;
                 case "4":
                     stausText = "已关闭";
                     btnCheckCustomCode.setVisibility(View.GONE);
                     break;
                 case "5":
                     stausText = "退款中";
                     btnCheckCustomCode.setVisibility(View.GONE);
                     tvFriendPay.setVisibility(View.GONE);
                     break;
                 case "6":
                     stausText = "已退款";
                     btnCheckCustomCode.setVisibility(View.GONE);
                     break;
                 case "7":
                     stausText = "已关闭";
                     btnCheckCustomCode.setVisibility(View.GONE);
                     break;
                 case "8":
                     stausText = "已退款";
                     btnCheckCustomCode.setVisibility(View.GONE);
                     break;
                 default:
                     //":"0：待付款 1：待使用 2：待评价 3：已关闭 4：已过期 5：退款中 6：已退款 7已评价 8 过期退",
                     break;
             }
             List<String> list = new ArrayList<>();
             list.add(orderRecordBean.getpName());
             ClubOrderlListAdapter adapter = new ClubOrderlListAdapter(mActivity,list);
             dishLst.setAdapter(adapter);
             orderTime.setText(orderRecordBean.getOrderTime());
             orderId.setText(orderRecordBean.getOrdId());
             orderTotalCost.setText("￥"+orderRecordBean.getPrice());
             tvOrderStatus.setText(stausText);
             resName.setText(orderRecordBean.getHpName());
             dishLst.setOnItemClickListener(new LinearListView.OnItemClickListener()
             {
                 @Override
                 public void onItemClick(LinearListView parent, View view, int position, long id)
                 {
                     Logger.t(TAG).d("菜单列表点击执行了>" + position);
                     if (itemClickListener != null)
                         itemClickListener.OnItemClick(orderRecordBean);
                 }
             });
         }
         else
         {
        //平台正常预定订单
        if (orderRecordBean.getSource().equals("0"))
        {
            final TextView orderId, orderTime, resName, orderValidity, orderLeftTime, orderTotalCost, tvOrderStatus, tvDishCount, tvFriendPay;
            // final AutoSplitTextView tvDishCount;
            LinearListView dishLst;
            ImageView bootyCallImg;
            //region 正常预定的订单
            final String orderStatus = orderRecordBean.getStatus();
            //待使用
            if (orderStatus.equals("1"))
            {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.litem_order_record, null);
                orderId = (TextView) convertView.findViewById(R.id.tv_order_id);
                tvOrderStatus = (TextView) convertView.findViewById(R.id.tv_order_type);
                orderTime = (TextView) convertView.findViewById(R.id.tv_order_time);
                resName = (TextView) convertView.findViewById(R.id.tv_order_res_name);
                dishLst = (LinearListView) convertView.findViewById(R.id.lv_order_dish_lst);
                orderValidity = (TextView) convertView.findViewById(R.id.tv_order_validity);
                orderLeftTime = (TextView) convertView.findViewById(R.id.tv_order_left_time);
                orderTotalCost = (TextView) convertView.findViewById(R.id.tv_order_total_money);
                tvDishCount = (TextView) convertView.findViewById(R.id.tv_order_dish_count);
                tvFriendPay = (TextView) convertView.findViewById(R.id.tv_friend_pay);
                bootyCallImg = (ImageView) convertView.findViewById(R.id.img_order_booty_call);
                Button btnCheckCustomCode = (Button) convertView.findViewById(R.id.btn_order_check_code);
                btnCheckCustomCode.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Logger.t(TAG).d("查看消费码");
                        checkCustomCode(orderRecordBean.getOrdId(), orderRecordBean.getoCode(),
                                orderRecordBean.getOverTime(), orderRecordBean.getrName(), orderRecordBean.getReceiveId(), mActivity,true);
                    }
                });
                orderValidity.setText(orderRecordBean.getOrderTime());
                tvOrderStatus.setText("待使用");
                orderLeftTime.setText(orderRecordBean.getOverTime());
                tvFriendPay.setVisibility(View.GONE);
                bootyCallImg.setVisibility(View.GONE);
                if ("0".equals(btnOnOff))
                {
                    tvFriendPay.setVisibility(View.VISIBLE);
                }
                if (!TextUtils.isEmpty(orderRecordBean.getReceiveId()))
                {
                    bootyCallImg.setVisibility(View.VISIBLE);
                }
                tvFriendPay.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        orderIdStr = orderRecordBean.getOrdId();
                        if (pDialog != null && !pDialog.isShowing())
                            pDialog.show();
                        if (adapterView != null)
                            adapterView.shareOrder(orderIdStr);
                    }
                });
            }
            else
            {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.litem_order_unpaied_record, null);
                AutoLinearLayout allContainer = (AutoLinearLayout) convertView.findViewById(R.id.all_order_container);
                AutoLinearLayout allLeftTime = (AutoLinearLayout) convertView.findViewById(R.id.all_left_time);
                orderId = (TextView) convertView.findViewById(R.id.tv_order_id);
                tvOrderStatus = (TextView) convertView.findViewById(R.id.tv_order_type);
                orderTime = (TextView) convertView.findViewById(R.id.tv_order_time);
                resName = (TextView) convertView.findViewById(R.id.tv_order_res_name);
                dishLst = (LinearListView) convertView.findViewById(R.id.lv_order_dish_lst);
                orderLeftTime = (TextView) convertView.findViewById(R.id.tv_order_left_time);
                orderTotalCost = (TextView) convertView.findViewById(R.id.tv_order_total_money);
                tvDishCount = (TextView) convertView.findViewById(R.id.tv_order_dish_count);
                final View anchorView = convertView;
                Button btnDelete = (Button) convertView.findViewById(R.id.btn_order_delete);
                bootyCallImg = (ImageView) convertView.findViewById(R.id.img_order_booty_call);
                bootyCallImg.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(orderRecordBean.getReceiveId()))
                {
                    bootyCallImg.setVisibility(View.VISIBLE);
                }
                btnDelete.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        switch (orderStatus)
                        {
                            //待付款
                            case "0":
                                Logger.t(TAG).d("待付款");
                                SharePreUtils.setSource(mActivity, "unPay");
                                PayHelper.clearPayHelperListeners();
                                PayHelper.setIPayFinishedListener(new PayFinish(mActivity));
                                PayBean payBean = new PayBean();
                                payBean.setOrderId(orderRecordBean.getOrdId());
                                payBean.setAmount(orderRecordBean.getOrderCos2());
                                payBean.setSubject("订单");               // 商品的标题
                                payBean.setBody("点餐订单支付");               // 商品的描述信息
                                PayHelper.payOrder(anchorView, payBean, mActivity, new PayMetadataBean("", "", "", "0"));
                                break;
                            //待评价
                            case "2":
                                Logger.t(TAG).d("待评价");
                                Intent intent = new Intent(mActivity, DOrderCommentAct.class);
                                intent.putExtra("orderId", orderRecordBean.getOrdId());
                                intent.putExtra("resId", orderRecordBean.getrId());
                                intent.putExtra("dishLst", (ArrayList<DishBean>) orderRecordBean.getDishBeen());
                                intent.putExtra("orderType", "normalType");
                                intent.putExtra("resName", orderRecordBean.getrName());
                                mActivity.startActivity(intent);
                                break;
                            case "3":
                                Logger.t(TAG).d("已关闭");
                                showDelDialog(orderRecordBean.getOrdId(), position,false);
                                break;
                            case "4":
                                Logger.t(TAG).d("已过期");
                                showDelDialog(orderRecordBean.getOrdId(), position,false);
                                break;
                            case "5":
                                Logger.t(TAG).d("退款中");
                                break;
                            case "6":
                                Logger.t(TAG).d("已退款");
                                showDelDialog(orderRecordBean.getOrdId(), position,false);
                                break;
                            case "7":
                                Logger.t(TAG).d("已完成");
                                showDelDialog(orderRecordBean.getOrdId(), position,false);
                                break;
                            case "8":
                                Logger.t(TAG).d("已关闭");
                                showDelDialog(orderRecordBean.getOrdId(), position,false);
                                break;
                        }
                    }
                });

                switch (orderStatus)
                {
                    //待付款
                    case "0":
                        allLeftTime.setVisibility(View.VISIBLE);
                        btnDelete.setText("支付");
                        btnDelete.setBackgroundResource(R.drawable.round_corner_12_mc1_selector);
                        btnDelete.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                        tvOrderStatus.setText("待付款");
                        allContainer.setBackgroundResource(R.color.white);
                        allContainer.setPadding(0, 0, 0, 0);
                        orderLeftTime.setText(orderRecordBean.getOverTime());
                        break;
                    //待评价
                    case "2":
                        btnDelete.setText("评价");
                        btnDelete.setBackgroundResource(R.drawable.round_corner_12_mc1_selector);
                        btnDelete.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                        tvOrderStatus.setText("待评价");
                        allContainer.setBackgroundResource(R.color.white);
                        allContainer.setPadding(0, 0, 0, 0);
                        break;
                    case "3":
                        //Logger.t(TAG).d("已关闭");
                        btnDelete.setText("删除");
                        btnDelete.setBackgroundResource(R.drawable.round_frame_6_fc3_white);
                        btnDelete.setTextColor(ContextCompat.getColor(mActivity, R.color.FC3));
                        tvOrderStatus.setText("已关闭");
                        tvOrderStatus.setTextColor(ContextCompat.getColor(mActivity, R.color.MC1));
                        //allContainer.setBackgroundResource(R.drawable.my_order_bg_gray);
                        break;
                    //后台暂时没有返回4，但是将来可能用到
                    case "4":
                        //Logger.t(TAG).d("已过期");
                        btnDelete.setText("删除");
                        btnDelete.setBackgroundResource(R.drawable.round_frame_6_fc3_white);
                        btnDelete.setTextColor(ContextCompat.getColor(mActivity, R.color.FC3));
                        tvOrderStatus.setText("已过期");
                        tvOrderStatus.setTextColor(ContextCompat.getColor(mActivity, R.color.c3));
                        break;
                    case "5":
                        //Logger.t(TAG).d("退款中");
                        tvOrderStatus.setText("退款中");
                        tvOrderStatus.setTextColor(ContextCompat.getColor(mActivity, R.color.MC1));
                        btnDelete.setVisibility(View.INVISIBLE);
                        allContainer.setBackgroundResource(R.color.white);
                        allContainer.setPadding(0, 0, 0, 0);
                        break;
                    case "6":
                        //Logger.t(TAG).d("已退款");
                        btnDelete.setText("删除");
                        btnDelete.setBackgroundResource(R.drawable.round_frame_6_fc3_white);
                        btnDelete.setTextColor(ContextCompat.getColor(mActivity, R.color.FC3));
                        tvOrderStatus.setTextColor(ContextCompat.getColor(mActivity, R.color.c3));
                        tvOrderStatus.setText("已退款");
                        //allContainer.setBackgroundResource(R.drawable.my_order_bg_gray);
                        break;
                    case "7":
                        //将已正常消费评价完成的item归入已关闭类
                        btnDelete.setText("删除");
                        btnDelete.setBackgroundResource(R.drawable.round_frame_6_fc3_white);
                        btnDelete.setTextColor(ContextCompat.getColor(mActivity, R.color.FC3));
                        tvOrderStatus.setText("已关闭");
                        tvOrderStatus.setTextColor(ContextCompat.getColor(mActivity, R.color.c3));
                        break;
                    case "8":
                        //将系统退款的item归入已关闭类,但是显示出一个可以查看退款详情的入口
                        btnDelete.setText("删除");
                        btnDelete.setBackgroundResource(R.drawable.round_frame_6_fc3_white);
                        btnDelete.setTextColor(ContextCompat.getColor(mActivity, R.color.FC3));
                        tvOrderStatus.setText("已关闭");
                        tvOrderStatus.setTextColor(ContextCompat.getColor(mActivity, R.color.c3));
                        break;
                    default:
                        tvOrderStatus.setTextColor(ContextCompat.getColor(mActivity, R.color.c3));
                        tvOrderStatus.setText("未知状态");
                        btnDelete.setVisibility(View.INVISIBLE);
                        //allContainer.setBackgroundResource(R.drawable.my_order_bg_gray);
                        break;
                }
            }
            orderId.setText(orderRecordBean.getOrdId());
            orderTime.setText(orderRecordBean.getSmtTime());
            resName.setText(orderRecordBean.getrName());
            orderTotalCost.setText(String.format("￥%s", CommonUtils.keep2Decimal(Double.parseDouble(orderRecordBean.getOrderCos2().toString()))));
            int dishCount = 0;
            for (DishBean bean : orderRecordBean.getDishBeen())
            {
                dishCount += Integer.parseInt(bean.getDishAmount());
            }
            tvDishCount.setText(String.format("(共%s道菜)", String.valueOf(dishCount)));
            List<DishBean> dishBeenLst = orderRecordBean.getDishBeen();
            if (dishBeenLst.size() > 2)
                dishBeenLst = dishBeenLst.subList(0, 2);
            dishLst.setAdapter(new DishLstAdapter(mActivity, dishBeenLst));

            //******为了使这个列表可以产生点击事件，我知道不应该写在itemClick事件中，这个只适用于当前场景，注意注意！！！****
            dishLst.setOnItemClickListener(new LinearListView.OnItemClickListener()
            {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, long id)
                {
                    Logger.t(TAG).d("菜单列表点击执行了>" + position);
                    if (itemClickListener != null)
                        itemClickListener.OnItemClick(orderRecordBean);
                }
            });
            //*********************************************************
            //endregion
        }
        else
        {
            //region 闪付
            final String orderStatus = orderRecordBean.getStatus();
            Logger.t(TAG).d("获取闪付订单状态--> " + orderStatus);

            TextView orderId, orderTime, resName, orderValidity, orderTotalCost, tvOrderStatus;
            ImageView bootyCallImg;
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.litem_order_quick_pay_order, null);
            AutoLinearLayout allContainer = (AutoLinearLayout) convertView.findViewById(R.id.all_order_container);
            AutoLinearLayout allLeftTime = (AutoLinearLayout) convertView.findViewById(R.id.all_left_time);
            orderId = (TextView) convertView.findViewById(R.id.tv_order_id);
            //暂时只有一种状态
            tvOrderStatus = (TextView) convertView.findViewById(R.id.tv_order_type);
            orderTime = (TextView) convertView.findViewById(R.id.tv_order_time);
            resName = (TextView) convertView.findViewById(R.id.tv_order_res_name);
            orderTotalCost = (TextView) convertView.findViewById(R.id.tv_order_total_money);
/*            final View anchorView=convertView;
            Button btnDelete= (Button) convertView.findViewById(R.id.btnOrderDelete);*/
            orderId.setText(orderRecordBean.getOrdId());
            orderTime.setText(orderRecordBean.getSmtTime());
            resName.setText(orderRecordBean.getrName());
            orderTotalCost.setText(String.format("￥%s", CommonUtils.keep2Decimal(Double.parseDouble(orderRecordBean.getOrderCos2().toString()))));
            // zdw添加闪付列表
            Button btnOrderDelete = (Button) convertView.findViewById(R.id.btn_order_delete);
            bootyCallImg = (ImageView) convertView.findViewById(R.id.img_order_booty_call);
            bootyCallImg.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(orderRecordBean.getReceiveId()))
            {
                bootyCallImg.setVisibility(View.VISIBLE);
            }
            btnOrderDelete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //闪付暂时只有2 和7两个状态
                    switch (orderStatus)
                    {
                        //待付款
                        case "0":
                            Logger.t(TAG).d("待付款");
                            break;
                        //待评价
                        case "2":
                            Logger.t(TAG).d("待评价");
                            Intent intent = new Intent(mActivity, DOrderCommentAct.class);
                            intent.putExtra("orderId", orderRecordBean.getOrdId());
                            intent.putExtra("resId", orderRecordBean.getrId());
                            intent.putExtra("dishLst", (ArrayList<DishBean>) orderRecordBean.getDishBeen());
                            intent.putExtra("orderType", "quickType");
                            intent.putExtra("resName", orderRecordBean.getrName());
                            mActivity.startActivity(intent);
                            break;
                        case "3":
                            Logger.t(TAG).d("已关闭");
                            showDelDialog(orderRecordBean.getOrdId(), position,false);
                            break;
                        case "4":
                            Logger.t(TAG).d("已过期");
                            showDelDialog(orderRecordBean.getOrdId(), position,false);
                            break;
                        case "5":
                            Logger.t(TAG).d("退款中");
                            break;
                        case "6":
                            Logger.t(TAG).d("已退款");
                            showDelDialog(orderRecordBean.getOrdId(), position,false);
                            break;
                        //评价完成
                        case "7":
                            Logger.t(TAG).d("已完成");
                            showDelDialog(orderRecordBean.getOrdId(), position,false);
                            break;
                        case "8":
                            Logger.t(TAG).d("已关闭");
                            showDelDialog(orderRecordBean.getOrdId(), position,false);
                            break;
                    }
                }
            });
            //闪付暂时只有2 和7两个状态
            switch (orderStatus)
            {
                //待付款
                case "0":
                    allLeftTime.setVisibility(View.VISIBLE);
                    btnOrderDelete.setText("支付");
                    tvOrderStatus.setText("待付款");
                    //     allContainer.setBackgroundResource(R.drawable.my_order_bg_zi);
                    allContainer.setPadding(0, 0, 0, 0);
                    break;
                //待评价
                case "2":
                    btnOrderDelete.setText("评价");
                    tvOrderStatus.setText("待评价");
                    //    allContainer.setBackgroundResource(R.drawable.my_order_bg_zi);
                    allContainer.setPadding(0, 0, 0, 0);
                    break;
                case "3":
                    //Logger.t(TAG).d("已关闭");
                    btnOrderDelete.setText("删除");
                    tvOrderStatus.setText("已关闭");
                    tvOrderStatus.setTextColor(ContextCompat.getColor(mActivity, R.color.c3));
                    //allContainer.setBackgroundResource(R.drawable.my_order_bg_gray);
                    break;
                //后台暂时没有返回4，但是将来可能用到
                case "4":
                    //Logger.t(TAG).d("已过期");
                    btnOrderDelete.setText("删除");
                    tvOrderStatus.setText("已过期");
                    tvOrderStatus.setTextColor(ContextCompat.getColor(mActivity, R.color.c3));
                    //allContainer.setBackgroundResource(R.drawable.my_order_bg_gray);
                    break;
                case "5":
                    //Logger.t(TAG).d("退款中");
                    tvOrderStatus.setText("退款中");
                    tvOrderStatus.setTextColor(ContextCompat.getColor(mActivity, R.color.c3));
                    btnOrderDelete.setVisibility(View.INVISIBLE);
                    //     allContainer.setBackgroundResource(R.drawable.my_order_bg_gray);
                    allContainer.setPadding(0, 0, 0, 0);
                    break;
                case "6":
                    //Logger.t(TAG).d("已退款");
                    btnOrderDelete.setText("删除");
                    tvOrderStatus.setTextColor(ContextCompat.getColor(mActivity, R.color.c3));
                    tvOrderStatus.setText("已退款");
                    //allContainer.setBackgroundResource(R.drawable.my_order_bg_gray);
                    break;
                case "7":
                    //将已正常消费评价完成的item归入已关闭类
                    btnOrderDelete.setText("删除");
                    tvOrderStatus.setText("已关闭");
                    tvOrderStatus.setTextColor(ContextCompat.getColor(mActivity, R.color.c3));
                    break;
                case "8":
                    //将系统退款的item归入已关闭类,但是显示出一个可以查看退款详情的入口
                    btnOrderDelete.setText("删除");
                    tvOrderStatus.setText("已关闭");
                    tvOrderStatus.setTextColor(ContextCompat.getColor(mActivity, R.color.c3));
                    break;
                default:
                    tvOrderStatus.setTextColor(ContextCompat.getColor(mActivity, R.color.c3));
                    tvOrderStatus.setText("未知状态");
                    btnOrderDelete.setVisibility(View.INVISIBLE);
                    //allContainer.setBackgroundResource(R.drawable.my_order_bg_gray);
                    break;
            }
            //endregion
        }

    }

        convertView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Logger.t(TAG).d("item点击执行了");
                if (itemClickListener != null)
                    itemClickListener.OnItemClick(orderRecordBean);
            }
        });

        return convertView;
    }

    public void setItemClickListener(IOnItemClickListener listener)
    {
        this.itemClickListener = listener;
    }

    private void showDelDialog(final String ordId, final int position,boolean isHomepany)
    {
        new CustomAlertDialog(mActivity)
                .builder()
                .setMsg("确定要删除此订单吗？")
                .setPositiveTextColor(ContextCompat.getColor(mActivity, R.color.C0313))
                .setPositiveButton("确认", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        tempPosition = position;
                        if (adapterView != null)
                            adapterView.deleteOrder(ordId,isHomepany);
                    }
                }).setNegativeButton("取消", new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

            }
        }).show();
    }

    //查看消费码
    private void checkCustomCode(final String orderId1, String customCode, CharSequence period, CharSequence resNameT, String receiveId, final Activity mActivity,final boolean isNormal)
    {
        final Dialog dialog = new Dialog(mActivity, R.style.AlertDialogStyle);
        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_check_cost_code, null);
        dialog.setContentView(contentView);
        TextView resName = (TextView) contentView.findViewById(R.id.txt_title);
        TextView costCode = (TextView) contentView.findViewById(R.id.tv_cost_code);
        TextView validPeriod = (TextView) contentView.findViewById(R.id.tv_valid_period);
        ImageView imageView = (ImageView) contentView.findViewById(R.id.iv_show_code_img);
        Button btnCancel = (Button) contentView.findViewById(R.id.btn_neg);
        Button btnOk = (Button) contentView.findViewById(R.id.btn_pos);
        btnCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = null;
                if (isNormal)
                {
                   intent = new Intent(mActivity, DOrderRecordDetail.class);
                }
                else
                {
                    intent = new Intent(mActivity, ClubOrderRecordDetailAct.class);
                }

                intent.putExtra("orderId", orderId1);
                intent.putExtra("btnOnOff", btnOnOff);
                mActivity.startActivity(intent);
                dialog.dismiss();
            }
        });
        resName.setText(resNameT);
        costCode.setText(customCode);
        validPeriod.setText(period);
        QRCodeBean qrCodeBean = new QRCodeBean();
        if (!TextUtils.isEmpty(receiveId))
        {
            qrCodeBean.setType("DATE_ORDER_ID");
        }
        else
        {
            qrCodeBean.setType("NORMAl_ORDER_ID");
        }
        qrCodeBean.setContent(customCode);
        String QRStr = new Gson().toJson(qrCodeBean);
        imageView.setImageBitmap(CommonUtils.createQRImage(mActivity, QRStr, 200, 200));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (CommonUtils.getScreenSize(mActivity).width * 0.85f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    private void delOrderSuccessCallback(int position)
    {
        orderRecordLst.remove(position);
        this.notifyDataSetChanged();
    }


    private void initPopWindow()
    {
        ShareToFaceBean shareBean = new ShareToFaceBean();
        shareBean.setShareType(Platform.SHARE_WEBPAGE);
        shareBean.setShareTitle("帮我付款才是真友谊");
        shareBean.setShareWeChatMomentsTitle("帮我付款才是真友谊");
        shareBean.setShareUrl(NetHelper.SHARE_ORDERDS_ADDRESS + "/pay/index.html?orderId=" + orderIdStr);
        shareBean.setShareContent("你的一小笔开支，是增进我们关系的一大步，麻溜儿的付款吧～");
        shareBean.setShareAppImageUrl(NetHelper.LIVE_SHARE_PIC);
        sharePopWindow = new SharePopWindow(mActivity,
                new int[]{SharePopWindow.SHARE_WAY_WECHAT_FRIEND, SharePopWindow.SHARE_WAY_WECHAT_MOMENT}, shareBean);
    }

    public void onDestroy()
    {
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
    }

    public void onBackPressed()
    {
        if (sharePopWindow != null && sharePopWindow.isShowing())
        {
            sharePopWindow.dismiss();
        }
        else
        {
            mActivity.finish();
        }
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.OrderC_shareOrder:
                if ("ORDER_CANNOT_SHARE".equals(code))
                {
                    if (impIAllOrdersView != null)
                        impIAllOrdersView.getAllOrders("0", "10", typeStr, "refresh");
                }
                Logger.t(TAG).d("错误码为：%s", code);
                break;
            case NetInterfaceConstant.OrderC_applyRefund:

                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
                Logger.t(TAG).d("错误码为：%s", code);
                break;
            case NetInterfaceConstant.HomepartyC_applyRefund:
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
                if (TextUtils.equals("HOMEPARTY_TIMEOUT",code)||code.equals(ErrorCodeTable.HOMEPARTY_USED))
                {
                    if (impIAllOrdersView != null)
                        impIAllOrdersView.getAllOrders("0", "20", typeStr, "refresh");
                    Logger.t(TAG).d("错误码为：%s", code);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mActivity, null, exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void deleteOrderCallBack(String response)
    {
        Logger.t(TAG).json(response);
        ToastUtils.showShort("删除成功");
        delOrderSuccessCallback(tempPosition);
//        if (impIAllOrdersView != null)
//            impIAllOrdersView.getAllOrders("0", "10", typeStr, "refresh");
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void shareOrderCallBack(String response)
    {
        Logger.t(TAG).d("分享代付前调用接口--> " + response);
        initPopWindow();
        sharePopWindow.showPopupWindow(mActivity.findViewById(R.id.main), null);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getApplyRefundClubSuccess(String response, String orderId)
    {
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
            mActivity.startActivity(intent);
        } catch (JSONException e)
        {
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
        } finally
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
        if (impIAllOrdersView != null)
            impIAllOrdersView.getAllOrders("0", "10", typeStr, "refresh");
    }

    final class ViewHolder1
    {
        public AutoLinearLayout allContainer;
        public TextView orderId;
        public TextView orderTime;
        public TextView resName;
        public ListView dishLst;
        public TextView orderValidity;
        public TextView orderLeftTime;
        public TextView orderTotalCost;
        public Button btnRefund;
        public Button btnCheckCustomCode;
    }

    private static class PayFinish implements IPayFinishedListener
    {
        private final WeakReference<Activity> mActRef;

        private PayFinish(Activity mFrg)
        {
            this.mActRef = new WeakReference<Activity>(mFrg);
        }

        @Override
        public void PayFinished(String orderId, String streamId, String payType)
        {
            final Activity cAct = mActRef.get();
            if (cAct != null)
            {
                PayHelper.clearPopupWindows();
                Intent intent = new Intent(cAct, DPayOrderSuccessAct.class);
                intent.putExtra("orderId", orderId);
                cAct.startActivity(intent);
                cAct.finish();
            }
        }

        @Override
        public void PayFailed(String orderId, String streamId, String payType)
        {
            //PayHelper.clearPopupWindows();
        }
    }


    private static class PayClubFinish implements IPayFinishedListener
    {
        private final WeakReference<Activity> mActRef;

        private PayClubFinish(Activity mFrg)
        {
            this.mActRef = new WeakReference<Activity>(mFrg);
        }

        @Override
        public void PayFinished(String orderId, String streamId, String payType)
        {
            final Activity cAct = mActRef.get();
            PayHelper.clearPopupWindows();
            if (cAct != null)
            {
                Intent intent = new Intent(cAct, DClubPayOrderSuccessAct.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("clubId", SharePreUtils.getClubId(cAct));
                cAct.startActivity(intent);
                cAct.finish();

            }
        }

        @Override
        public void PayFailed(String orderId, String streamId, String payType)
        {
            //PayHelper.clearPopupWindows();
        }
    }

    public interface IOnItemClickListener
    {
        void OnItemClick(OrderRecordBean orderBean);
    }
}

package com.echoesnet.eatandmeet.views.widgets;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.OrderAffirmBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.views.adapters.OrderAffirmAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/11/21 0021
 * @description
 */
public class OrderAffirmDialog extends DialogFragment
{
    private Activity mAct;

    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.btn_neg)
    Button btnNeg;
    @BindView(R.id.btn_pos)
    Button btnPos;
    @BindView(R.id.rv_user)
    RecyclerView rvUser;
    @BindView(R.id.ll_detail)
    LinearLayout llDetail;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_anchor)
    TextView tvAnchor;

    private OrderAffirmAdapter orderAffirmAdapter;
    private List<OrderAffirmBean> orderAffirmList;
    private String data;
    private boolean detailShowing = false; //是否正显示订单详情
    private String orderDate;
    private OrderAffirmListener orderAffirmListener;

    public void setOrderDate(String orderDate)
    {
        this.orderDate = orderDate;
    }

    public void setOrderAffirmListener(OrderAffirmListener orderAffirmListener)
    {
        this.orderAffirmListener = orderAffirmListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mAct = getActivity();
        View view = LayoutInflater.from(mAct).inflate(R.layout.dialog_order_affirm, null, false);
        ButterKnife.bind(this, view);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = getDialog().getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = CommonUtils.getScreenWidth(mAct) - CommonUtils.dp2px(mAct, 24);
        window.setAttributes(lp);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        initData(data);
    }

    public void show(FragmentManager manager, String tag, String data)
    {
        super.show(manager, tag);
        this.data = data;
    }

    public void initData(String data)
    {
        orderAffirmList = new Gson().fromJson(data, new TypeToken<List<OrderAffirmBean>>()
        {
        }.getType());
        txtTitle.setText("小美检测到即将与您吃饭的主播");
        btnNeg.setText("否");
        btnPos.setText("是");
        if (orderAffirmList.size() == 1)
        {
            llContent.removeAllViews();
            List<OrderAffirmBean.UserListBean> userListBeans = orderAffirmList.get(0).getUserList();
            if (userListBeans.size() <= 3)
            {
                llContent.setOrientation(LinearLayout.HORIZONTAL);
                for (OrderAffirmBean.UserListBean userListBean : userListBeans)
                {
                    llContent.addView(makeItemUser(userListBean));
                }
            } else if (userListBeans.size() == 4)
            {
                llContent.setOrientation(LinearLayout.VERTICAL);
                for (int i = 0; i < 2; i++)
                {
                    LinearLayout linearLayout = new LinearLayout(mAct);
                    linearLayout.setVerticalGravity(LinearLayout.HORIZONTAL);
                    linearLayout.addView(makeItemUser(userListBeans.get(i == 0 ? i : i + 1)));
                    linearLayout.addView(makeItemUser(userListBeans.get(i == 0 ? i + 1 : i + 2)));
                    llContent.addView(linearLayout);
                }
            } else if (userListBeans.size() == 5)
            {
                llContent.setOrientation(LinearLayout.VERTICAL);
                for (int i = 0; i < 2; i++)
                {
                    LinearLayout linearLayout = new LinearLayout(mAct);
                    linearLayout.setVerticalGravity(LinearLayout.HORIZONTAL);
                    linearLayout.addView(makeItemUser(userListBeans.get(i == 0 ? i : i + 1)));
                    linearLayout.addView(makeItemUser(userListBeans.get(i == 0 ? i + 1 : i + 2)));
                    if (i == 0)
                        linearLayout.addView(makeItemUser(userListBeans.get(i + 2)));
                    llContent.addView(linearLayout);
                }
            }
        } else
        {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mAct);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            rvUser.setLayoutManager(linearLayoutManager);
            orderAffirmAdapter = new OrderAffirmAdapter(mAct, orderAffirmList);
            rvUser.setAdapter(orderAffirmAdapter);
            orderAffirmAdapter.notifyDataSetChanged();
        }
    }

    private View makeItemUser(OrderAffirmBean.UserListBean userListBean)
    {
        View view = LayoutInflater.from(mAct).inflate(R.layout.item_order_affirm_user, null, false);
        TextView tv = view.findViewById(R.id.tv_nick_name);
        RoundedImageView riv = view.findViewById(R.id.riv_head);
        tv.setText(userListBean.getNicName());
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .centerCrop()
                .load(userListBean.getPhUrl())
                .placeholder(R.drawable.qs_head)
                .error(R.drawable.qs_head)
                .into(riv);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        view.setLayoutParams(layoutParams);
        return view;
    }

    @OnClick({R.id.btn_neg, R.id.btn_pos})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_neg:
                dismiss();
                if (orderAffirmListener != null)
                    orderAffirmListener.normalOrder();
                break;
            case R.id.btn_pos:
                if (!detailShowing)
                {
                    if (orderAffirmList.size() > 1)
                    {
                        if (orderAffirmAdapter.getSelectedOrder() == null)
                        {
                            //未选择 普通订单
                            dismiss();
                            if (orderAffirmListener != null)
                                orderAffirmListener.normalOrder();
                        } else
                        {
                            showOrderDetail();
                        }
                    } else if (orderAffirmList.size() == 1)
                    {
                        showOrderDetail();
                    }
                } else
                {
                    if (orderAffirmList == null)
                        break;
                    dismiss();
                    OrderAffirmBean orderAffirmBean = null;
                    if (orderAffirmList.size() == 1)
                        orderAffirmBean = orderAffirmList.get(0);
                    else if (orderAffirmList.size() > 1)
                        orderAffirmBean = orderAffirmAdapter.getSelectedOrder();
                    if (orderAffirmListener != null && orderAffirmBean != null)
                        orderAffirmListener.bootyCallOrder(orderAffirmBean.getSId());
                }

                break;
        }
    }

    private void showOrderDetail()
    {
        //显示订单明细界面
        txtTitle.setText("订单明细");
        llContent.setVisibility(View.GONE);
        llDetail.setVisibility(View.VISIBLE);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy年MM月dd日");
        String date = "";
        try
        {
            date = simpleDateFormat1.format(simpleDateFormat.parse(orderDate));
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        String orderDate = "用餐时间:" + date;
        SpannableString spannableString = new SpannableString(orderDate);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mAct, R.color.C0412)),
                orderDate.length() - date.length(), orderDate.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvDate.setText(spannableString);
        OrderAffirmBean orderAffirmBean = null;
        if (orderAffirmList.size() == 1)
            orderAffirmBean = orderAffirmList.get(0);
        else if (orderAffirmList.size() > 1)
            orderAffirmBean = orderAffirmAdapter.getSelectedOrder();
        if (orderAffirmBean == null )
            return;
        List<OrderAffirmBean.UserListBean> list = orderAffirmBean.getUserList();
        if (list != null)
        {
            String des = "";
            if (list.size() > 1)
            {
                des = String.format("约会主播:%s等%s位主播", list.get(0).getNicName(), list.size());
            } else if (list.size() == 1)
            {
                des = String.format("约会主播:%s", list.get(0).getNicName());
            }
            SpannableString spannableString1 = new SpannableString(des);
            spannableString1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mAct, R.color.C0412)),
                    5, list.get(0).getNicName().length() + 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvAnchor.setText(spannableString1);
        }
        btnNeg.setText("算了");
        btnPos.setText("确定");
        detailShowing = true;
    }

    public interface OrderAffirmListener
    {
        void normalOrder();

        void bootyCallOrder(String sId);
    }
}

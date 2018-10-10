package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.OrderAffirmBean;
import com.echoesnet.eatandmeet.views.widgets.ImageOverlayView;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.List;

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
public class OrderAffirmAdapter extends RecyclerView.Adapter<OrderAffirmAdapter.ViewHolder>
{
    private Activity mAct;
    private List<OrderAffirmBean> orderAffirmBeans;
    private OrderAffirmBean selectedOrder;

    public OrderAffirmBean getSelectedOrder()
    {
        return selectedOrder;
    }

    public OrderAffirmAdapter(Activity mAct, List<OrderAffirmBean> orderAffirmBeans)
    {
        this.mAct = mAct;
        this.orderAffirmBeans = orderAffirmBeans;
    }

    @Override
    public OrderAffirmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mAct).inflate(R.layout.item_order_affirm,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderAffirmAdapter.ViewHolder holder, int position)
    {
        OrderAffirmBean itemBean = orderAffirmBeans.get(position);
        List<String> img = new ArrayList<>();
        List<OrderAffirmBean.UserListBean> users = itemBean.getUserList();
        if (users != null)
        {
            for (OrderAffirmBean.UserListBean userListBean : users)
            {
                img.add(userListBean.getPhUrl());
            }
            holder.imageOverlayView.setHeadImages(img);
            String des = "";
            if (img.size() > 1)
            {
                des = String.format("%s等%s位主播",users.get(0).getNicName(),img.size());
            }else if (img.size() == 1){
                des = users.get(0).getNicName();
            }
            SpannableString spannableString = new SpannableString(des);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mAct,R.color.C0412)),
                    0,users.get(0).getNicName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tvDes.setText(spannableString);
        }
        holder.iconTextView.setText(itemBean.isSelect()?"{eam-s-yes-circle @color/C0412}":"{eam-s-grade1 @color/C0323}");
        holder.itemView.setOnClickListener(v -> {
            if (itemBean.isSelect())
            {
                itemBean.setSelect(false);
                selectedOrder = null;
            }else
            {
                itemBean.setSelect(true);
                selectedOrder = itemBean;
                for (int i = 0; i < orderAffirmBeans.size(); i++)
                {
                    if (i != position)
                        orderAffirmBeans.get(i).setSelect(false);
                    else
                        orderAffirmBeans.get(i).setSelect(true);
                }
                notifyDataSetChanged();
            }
            holder.iconTextView.setText(itemBean.isSelect()?"{eam-s-yes-circle @color/C0412}":"{eam-s-grade1 @color/C0323}");
        });
    }

    @Override
    public int getItemCount()
    {
        return orderAffirmBeans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        IconTextView iconTextView;

        ImageOverlayView imageOverlayView;

        TextView tvDes;
        public ViewHolder(View itemView)
        {
            super(itemView);
            iconTextView = itemView.findViewById(R.id.itv_select);
            imageOverlayView = itemView.findViewById(R.id.iov_user);
            tvDes = itemView.findViewById(R.id.tv_des);
        }
    }
}

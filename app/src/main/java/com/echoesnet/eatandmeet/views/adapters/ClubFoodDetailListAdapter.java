package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.ClubOrderDetailBean;
import com.echoesnet.eatandmeet.models.bean.DishBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;

import java.util.List;




/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2018/2/8
 * @description
 */
public class ClubFoodDetailListAdapter extends BaseAdapter
{

    private List<ClubOrderDetailBean.FoodBean> dishSource;
    private Context mContext;

    public ClubFoodDetailListAdapter(Context mContext)
    {
        this.mContext = mContext;
    }

    public ClubFoodDetailListAdapter(Context mContext, List<ClubOrderDetailBean.FoodBean> dishSource)
    {
        this.mContext = mContext;
        this.dishSource = dishSource;
    }

    @Override
    public int getCount()
    {
        return dishSource.size();
    }

    @Override
    public Object getItem(int position)
    {
        return dishSource.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        ViewHolder holder = null;
        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.litem_club_dish_detail, parent, false);
            holder.rivDishImg = (ImageView) convertView.findViewById(R.id.iv_dish_pic);
            holder.tvDishName = (TextView) convertView.findViewById(R.id.tv_dish_name);
            holder.tvDishCount = (TextView) convertView.findViewById(R.id.tv_dish_count);

            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        ClubOrderDetailBean.FoodBean dishBean = (ClubOrderDetailBean.FoodBean) dishSource.get(position);
        holder.tvDishName.setText(dishBean.getName());
        holder.tvDishCount.setText("x"+dishBean.getNum());

        GlideApp.with(EamApplication.getInstance())
                .load(dishBean.getUrl())
                .placeholder(R.drawable.qs_cai_canting)
                .centerCrop()
                .error(R.drawable.cai_da)
                .into(holder.rivDishImg);
        return convertView;
    }

    public final class ViewHolder
    {
        ImageView rivDishImg;
        TextView tvDishName;
        TextView tvDishCount;
    }


}

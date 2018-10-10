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
public class ClubOrderlListAdapter extends BaseAdapter
{
    private List<String> dishSource;
    private Context mContext;

    public ClubOrderlListAdapter(Context mContext, List<String> dishSource)
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.litem_club_order_detail, parent, false);
            holder.tvDishName = (TextView) convertView.findViewById(R.id.tv_dish_name);
            holder.tvDishCount = (TextView) convertView.findViewById(R.id.tv_dish_count);

            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        String pName = dishSource.get(position);
        holder.tvDishName.setText(pName);
        holder.tvDishCount.setText("x1");

        return convertView;
    }

    public final class ViewHolder
    {

        TextView tvDishName;
        TextView tvDishCount;
    }


}

package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.DishBean;

import java.util.List;

/**
 * Created by Administrator on 2016/6/21.
 */
public class DishLstAdapter extends BaseAdapter
{
    private List<DishBean> dishSource;
    private Context mContext;
    public DishLstAdapter(Context mContext, List<DishBean>dishSource)
    {
        this.mContext=mContext;
        this.dishSource=dishSource;
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
        convertView= LayoutInflater.from(mContext).inflate(R.layout.litem_order_record_dish,parent,false);
        TextView orderDishName= (TextView) convertView.findViewById(R.id.tv_order_dish_name);
        TextView orderDishCount= (TextView) convertView.findViewById(R.id.tv_order_dish_count);
        DishBean dishBean= (DishBean) getItem(position);
        orderDishName.setText(dishBean.getDishName());
        orderDishCount.setText(String.format("x %s",dishBean.getDishAmount()));
        return convertView;
    }
}

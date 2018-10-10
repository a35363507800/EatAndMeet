package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.DishBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/6/22.
 */
public class DishDetailLstAdapter extends BaseAdapter
{
    private List<DishBean> dishSource;
    private Context mContext;
    public DishDetailLstAdapter(Context mContext, List<DishBean>dishSource)
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
        DishBean dishBean= (DishBean) getItem(position);
        ViewHolder holder=null;
        if (convertView==null)
        {
            holder=new ViewHolder();
            convertView= LayoutInflater.from(mContext).inflate(R.layout.litem_dish_detail,parent,false);
            holder.tvDishName= (TextView) convertView.findViewById(R.id.tv_dish_name);
            holder.tvDishCount=(TextView) convertView.findViewById(R.id.tv_dish_count);
            holder.tvDishPerCost=(TextView) convertView.findViewById(R.id.tv_dish_percost);
            holder.ivDishImg= (ImageView) convertView.findViewById(R.id.riv_dish_img);
            holder.tvDishTotalCost= (TextView) convertView.findViewById(R.id.tv_dish_totalcost);
            convertView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder) convertView.getTag();
        }
        holder.tvDishName.setText(dishBean.getDishName());
        holder.tvDishPerCost.setText(String.format("￥%s", CommonUtils.keep2Decimal(Double.parseDouble(dishBean.getDishPrice()))));
        holder.tvDishCount.setText(String.format("X%s",dishBean.getDishAmount()));
        holder.tvDishTotalCost.setText(String.format("￥%s",CommonUtils.keep2Decimal(Double.parseDouble(dishBean.getDishAll()))));
        GlideApp.with(EamApplication.getInstance())
                .load(dishBean.getDishHUrl())
                .placeholder(R.drawable.qs_cai_canting)
                .centerCrop()
                .error(R.drawable.cai_da)
                .into(holder.ivDishImg);
        return convertView;
    }

    public final class ViewHolder
    {
        TextView tvDishName;
        TextView tvDishPerCost;
        TextView tvDishCount;
        TextView tvDishTotalCost;
//        RoundedImageView ivDishImg;
        public ImageView ivDishImg;
    }
}

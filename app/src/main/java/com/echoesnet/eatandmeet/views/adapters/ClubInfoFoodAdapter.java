package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.ClubInfoBean;
import com.echoesnet.eatandmeet.models.bean.FoodBean;
import com.echoesnet.eatandmeet.models.bean.PackagesBean;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;


/**
 * Created by Administrator on 2018/2/6.
 *
 * @author ling
 */

public class ClubInfoFoodAdapter extends RecyclerView.Adapter<ClubInfoFoodAdapter.ViewHolder>
{
    private Context mContext;
    private List<FoodBean> list;

    public ClubInfoFoodAdapter(Context mContext, List<FoodBean> list)
    {
        this.mContext = mContext;
        this.list = list;

    }

    public void setList(List<FoodBean> list)
    {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<FoodBean> getList()
    {
        return list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        int layoutId = R.layout.act_club_info_food_adapter;

        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.foodName.setText(list.get(position).getName());
        holder.foodCount.setText(list.get(position).getNum()+list.get(position).getUnit());
        GlideApp.with(EamApplication.getInstance())
                .load(list.get(position).getUrl())
                .centerCrop()
                .placeholder(R.drawable.bg_nochat)
                .error(R.drawable.bg_nochat)
                .into(holder.pic);
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView foodCount;
        TextView foodName;
        RoundedImageView pic;

        public ViewHolder(View view)
        {
            super(view);

            foodCount = view.findViewById(R.id.tv_food_count);
            foodName = view.findViewById(R.id.tv_food_name);
            pic = view.findViewById(R.id.rv_pic);

        }

    }


}

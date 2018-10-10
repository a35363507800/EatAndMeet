package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.RestaurantBean;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * Created by wangben on 2016/5/24.
 */
public class MeetRestaurantAdapter extends RecyclerView.Adapter<MeetRestaurantAdapter.RestaurantViewHolder>
{
    List<RestaurantBean> restaurantLst;
    Context context;
    private boolean isPullData;
    private MeetRestaurantAdapter.OnItemClickListener onItemClickListener;

    private int startIndex;

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public MeetRestaurantAdapter(Context context , List<RestaurantBean> restaurantLst)
    {
        this.context=context;
        this.restaurantLst=restaurantLst;
    }

    public boolean isPullData() {
        return isPullData;
    }

    public void setPullData(boolean pullData) {
        isPullData = pullData;
    }

    public void setOnItemClickListener(MeetRestaurantAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.gvitem_meet_res,parent,false);
        RestaurantViewHolder restaurantViewHolder = new RestaurantViewHolder(itemView);
        return restaurantViewHolder;
    }

    @Override
    public void onBindViewHolder(RestaurantViewHolder holder, final int position) {
        int index = position+startIndex*4;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null)
                {
                    onItemClickListener.itemClick(v,position+startIndex*4);
                }
            }
        });
        if (index<restaurantLst.size())
        {
            RestaurantBean res= restaurantLst.get(index);
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(res.getSnapshot())
                    .centerCrop()
                    .placeholder(R.drawable.qs_cai_canting)
                    .error(R.drawable.canting)
                    .into(holder.ivSnapshot);
            holder.tvName.setText(res.getrName());
            switch (res.getActivity())
            {
                case "0":
                    holder.tvMaker.setVisibility(View.GONE);
                    break;
                case "1":
                    holder.tvMaker.setBackgroundResource(R.drawable.faxian_qizi_lan_xhdpi);
                    break;
                case "2":
                    holder.tvMaker.setBackgroundResource(R.drawable.faxian_qizi_fen_xhdpi);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {
        public RestaurantViewHolder(View itemView) {
            super(itemView);
        }
        RoundedImageView ivSnapshot= (RoundedImageView) itemView.findViewById(R.id.iv_meet_res);
        TextView tvMaker= (TextView) itemView.findViewById(R.id.tv_meet_maker);
        TextView tvName= (TextView) itemView.findViewById(R.id.tv_meet_resname);
    }
    public interface OnItemClickListener
    {
        void itemClick(View view,int position);
    }
}

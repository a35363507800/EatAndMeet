package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.FRestaurantItemBean;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * 发现页最近入驻adapter
 * Created by an on 2017/3/29 0029.
 */

public class FRecentlyIn4FindAdapter extends RecyclerView.Adapter{
    private final String TAG = FRecentlyIn4FindAdapter.class.getSimpleName();
    private Activity mActivity;
    private List<FRestaurantItemBean> mData;
    private ItemClickListener itemClickListener;

    public FRecentlyIn4FindAdapter(Activity mActivity, List<FRestaurantItemBean> mData) {
        this.mActivity = mActivity;
        this.mData = mData;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_recently_in_4_find,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        final FRestaurantItemBean itemBean = mData.get(position);
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(itemBean.getRpUrl())
                .centerCrop()
                .placeholder(R.drawable.qs_cai_user)
                .error(R.drawable.qs_cai_user)
                .into(viewHolder.resPicImg);
        viewHolder.resNameTv.setText(itemBean.getRName());
        viewHolder.resNameTv.setSelected(true);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null)
                    itemClickListener.onItemClick(position,v,itemBean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView resPicImg;
        private TextView resNameTv;
        public ViewHolder(View itemView) {
            super(itemView);
            resPicImg = (ImageView) itemView.findViewById(R.id.img_res_pic);
            resNameTv = (TextView) itemView.findViewById(R.id.tv_res_name);
        }
    }
    public interface ItemClickListener {
        void onItemClick(int position, View view,FRestaurantItemBean itemBean);
    }
}

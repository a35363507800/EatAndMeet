package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.echoesnet.eatandmeet.models.bean.HotAnchorBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * 发现页今日推荐adapter
 * Created by an on 2017/3/29 0029.
 */

public class FTodayRec4FindAdapter extends RecyclerView.Adapter{
    private final String TAG = FTodayRec4FindAdapter.class.getSimpleName();
    private Activity mActivity;
    private List<FRestaurantItemBean> mData;
    private ItemClickListener itemClickListener;

    public FTodayRec4FindAdapter(Activity mActivity, List<FRestaurantItemBean> mData) {
        this.mActivity = mActivity;
        this.mData = mData;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_today_recommend_4_find,parent,false);
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
                .placeholder(R.drawable.qs_cai_canting)
                .error(R.drawable.qs_cai_canting)
                .into(viewHolder.resPicImg);
        if (!TextUtils.isEmpty(itemBean.getWord()))
        {
            viewHolder.noticeTv.setVisibility(View.VISIBLE);
            viewHolder.noticeTv.setText(itemBean.getWord());
        }else {
            viewHolder.noticeTv.setVisibility(View.GONE);
        }
        viewHolder.resNameTv.setText(itemBean.getRName());
        viewHolder.resAddressTv.setText(itemBean.getrAddr());
        viewHolder.resPriceTv.setText(String.format(mActivity.getResources().getString(R.string.today_recommend_price),itemBean.getPerPrice()));
        viewHolder.resNameTv.setSelected(true);
        viewHolder.resAddressTv.setSelected(true);
        viewHolder.noticeTv.setSelected(true);
        if (!TextUtils.isEmpty(itemBean.getDistance())){
            double distanceRes = Double.parseDouble(itemBean.getDistance());
//             距离
            if ((distanceRes + 0.5) > 1000)
            {
                viewHolder.distanceTv.setText(CommonUtils.keep2Decimal((distanceRes + 0.5) / 1000) + "km");
            }
            else
            {
                if((distanceRes + 0.5) > 100)
                {
                    viewHolder.distanceTv.setText(CommonUtils.keep2Decimal((distanceRes + 0.5)) + "m");
                } else
                {
                    viewHolder.distanceTv.setText("< 100m");
                }

            }
        }
        viewHolder.resAddressTv.setText(itemBean.getrAddr());
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
        private TextView noticeTv;
        private TextView resNameTv;
        private TextView resAddressTv;
        private TextView resPriceTv;
        private TextView distanceTv;
        public ViewHolder(View itemView) {
            super(itemView);
            resPicImg = (ImageView) itemView.findViewById(R.id.img_res_pic);
            noticeTv = (TextView) itemView.findViewById(R.id.tv_notice);
            resNameTv = (TextView) itemView.findViewById(R.id.tv_res_name);
            resAddressTv = (TextView) itemView.findViewById(R.id.tv_res_address);
            resPriceTv = (TextView) itemView.findViewById(R.id.tv_res_price);
            distanceTv = (TextView) itemView.findViewById(R.id.tv_distance);
        }
    }
    public interface ItemClickListener {
        void onItemClick(int position, View view, FRestaurantItemBean itemBean);
    }
}

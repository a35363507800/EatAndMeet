package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.datamodel.ImageDisposalType;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * Created by wangben on 2016/7/11.
 * 用户详细信息里面用户图片的adapter
 */
public class CUserInfoHeadImgAdapter extends RecyclerView.Adapter<CUserInfoHeadImgAdapter.ViewHolder>
{
    private LayoutInflater mInflater;
    private List<String> mData;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public CUserInfoHeadImgAdapter(Context context, List<String> data)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mData = data;
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.rvitem_userinfo_img, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.userInfoImg = (RoundedImageView) view.findViewById(R.id.riv_userinfo_img);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {


        String imgUrl = mData.get(position);
        String imageUrlByUCloud = CommonUtils.getThumbnailImageUrlByUCloud(imgUrl, ImageDisposalType.THUMBNAIL, 8, 160, 160);
        GlideApp.with(EamApplication.getInstance())
                .load(imageUrlByUCloud)
                .centerCrop()
                .placeholder(R.drawable.qs_head)
                .error(R.drawable.qs_head)
                .into(holder.userInfoImg);

        if (position == 0)
        {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            params.setMargins(0 - CommonUtils.dp2px(mContext, 6), 0, 0, 0);
            holder.itemView.setLayoutParams(params);
        }

//        if (position == (mData.size()-1))
//        {
//            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
//            params.setMargins(0,0, CommonUtils.dp2px(mContext,6),0);
//            holder.itemView.setLayoutParams(params);
//        }
        //6张照片右对齐
//        if (mData.size()==1)
//        {
//            if (position==0)
//            {
//                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
//                params.setMargins(CommonUtils.dp2px(mContext,254),0,0,0);
//                holder.itemView.setLayoutParams(params);
//            }
//
//        }else if (mData.size()==2)
//        {
//            if (position==0)
//            {
//            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
//            params.setMargins(CommonUtils.dp2px(mContext,170),0,0,0);
//            holder.itemView.setLayoutParams(params);
//            }
//        }else if(mData.size()==3)
//        {
//            if (position==0)
//            {
//            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
//            params.setMargins(CommonUtils.dp2px(mContext,84),0,0,0);
//            holder.itemView.setLayoutParams(params);
//            }
//        }


        //如果设置了回调，则设置点击事件


        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onItemClick(holder.itemView, position);
            }
        });

    }

    @Override
    public int getItemCount()
    {
        return mData.size();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View v)
        {
            super(v);
        }

        RoundedImageView userInfoImg;
    }

    /**
     * ItemClick的回调接口
     */
    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }
}

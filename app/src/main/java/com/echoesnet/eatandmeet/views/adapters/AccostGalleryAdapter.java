package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.UsersBean;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * Created by wangben on 2016/5/24.
 */
public class AccostGalleryAdapter extends RecyclerView.Adapter<AccostGalleryAdapter.ViewHolder>
{
    private LayoutInflater mInflater;
    private List<UsersBean> mData;
    private Context mContext;
    private boolean isPullData;

    public AccostGalleryAdapter(Context context, List<UsersBean> data)
    {
        mContext=context;
        mInflater = LayoutInflater.from(context);
        mData = data;
    }

    public boolean isPullData() {
        return isPullData;
    }

    public void setPullData(boolean pullData) {
        isPullData = pullData;
    }

    /**
     * ItemClick的回调接口
     *
     */
    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickLitener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public int getItemCount()
    {
        return mData.size();
    }
    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view = mInflater.inflate(R.layout.rvitem_accost,viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.userHeadImg = (RoundedImageView) view
                                         .findViewById(R.id.riv_accost_head);
        viewHolder.nickName= (TextView) view.findViewById(R.id.tv_accost_name);
        viewHolder.title= (TextView) view.findViewById(R.id.tv_accost_title);

        return viewHolder;
    }
    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i)
    {
        UsersBean user=mData.get(i);
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(user.getUphUrl())
                .centerCrop()
                .placeholder(R.drawable.qs_tx72_weichuan_xhdpi)
                .error(R.drawable.qs_tx72_weichuan_xhdpi)
                .into(viewHolder.userHeadImg);
        viewHolder.nickName.setText(user.getNicName());
        viewHolder.title.setText(user.getOccupation());

        //如果设置了回调，则设置点击事件
        if (mOnItemClickListener != null)
        {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mOnItemClickListener.onItemClick(viewHolder.itemView, i);
                }
            });
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View arg0)
        {
            super(arg0);
        }

        RoundedImageView userHeadImg;
        TextView nickName;
        TextView title;
    }
}

package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.LivePlayUserBean;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */

public class LiveAudiGalleryAdapter extends RecyclerView.Adapter<LiveAudiGalleryAdapter.AudienceViewHolder> implements View.OnClickListener
{
    private List<LivePlayUserBean> mData;
    private Context mContext;

    public LiveAudiGalleryAdapter(Context context,List<LivePlayUserBean> data)
    {
        mContext=context;
        mData = data;
    }



    /**
     * ItemClick的回调接口
     *
     */
    public interface OnItemClickListener
    {
        void onUserAvatarClick(View view, LivePlayUserBean user);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public AudienceViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.rvitem_live_audience,viewGroup, false);
        AudienceViewHolder vhAudience = new AudienceViewHolder(view);
        return vhAudience;
    }

    @Override
    public void onBindViewHolder(final AudienceViewHolder viewHolder, final int position)
    {
        LivePlayUserBean user=mData.get(position);
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(user.getFaceUrl())
                .centerCrop()
                .placeholder(R.drawable.userhead)
                .error(R.drawable.userhead)
                .into(viewHolder.userHeadImg);

        viewHolder.userHeadImg.setTag(user);
        viewHolder.userHeadImg.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.riv_live_aud_head:
                if (mOnItemClickListener != null){
                    mOnItemClickListener.onUserAvatarClick(view, (LivePlayUserBean) view.getTag());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount()
    {
        return mData.size();
    }

    public static class AudienceViewHolder extends RecyclerView.ViewHolder
    {

        RoundedImageView userHeadImg;

        public AudienceViewHolder(View v)
        {
            super(v);
            userHeadImg = (RoundedImageView) v.findViewById(R.id.riv_live_aud_head);
        }
    }
}

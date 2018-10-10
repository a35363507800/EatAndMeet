package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.echoesnet.eatandmeet.R;

import com.echoesnet.eatandmeet.activities.ShowLocationAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ss007.swiprecycleview.RefreshRecycleAdapter;

import java.util.List;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2017/9/12
 * @description
 */
public class VpArticalAdapter extends RefreshRecycleAdapter<FTrendsItemBean>
{
    private Context mAct;
    private List<FTrendsItemBean> mList;
    private VpArticalAdapter.VpArticalItemClick vpArticalItemClick;
    private boolean refreshPraise = false;

    public VpArticalAdapter(Context mAct,List<FTrendsItemBean> mList)
    {
        super(mList);
        this.mAct = mAct;
        this.mList = mList;
    }


    @Override
    public RecyclerView.ViewHolder onViewHolderCreate(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mAct).inflate(R.layout.item_vip_artical, parent, false);
        return new VpArticalAdapter.ViewHolder(view);
    }

    @Override
    public void onViewHolderBind(RecyclerView.ViewHolder holder, int position)
    {
        bindQuestionHolder((VpArticalAdapter.ViewHolder) holder, position);
    }

    private void bindQuestionHolder(ViewHolder holder, int position)
    {
        if(position>=getList().size())
            return;
        final VpArticalAdapter.ViewHolder viewHolder = (VpArticalAdapter.ViewHolder) holder;
        final FTrendsItemBean itemBean = getList().get(position);
        holder.itemView.setOnClickListener((v) ->
        {
            if (vpArticalItemClick != null)
                vpArticalItemClick.itemClick(viewHolder.tvReadNums, position, itemBean);
        });
        //点赞按钮
        viewHolder.tvPraise.setOnClickListener((v) ->
        {
            if (vpArticalItemClick != null)
                vpArticalItemClick.praiseClick(viewHolder.tvPraise, position, itemBean);
        });
        //评论按钮
        viewHolder.tvComment.setOnClickListener((v) ->
        {

            if (vpArticalItemClick != null)
                vpArticalItemClick.commentClick(itemBean);

        });
        //图片内容+标题  跳转h5链接
        viewHolder.llAllToArtical.setOnClickListener((v) ->
        {
            if (vpArticalItemClick != null)
                vpArticalItemClick.contentClick(viewHolder.tvReadNums, itemBean);
        });

        viewHolder.tvNickname.setText(!TextUtils.isEmpty(itemBean.getRemark()) ? itemBean.getRemark() : itemBean.getNicName());
        viewHolder.tvDistance.setText("·" + itemBean.getDistance());
        viewHolder.tvLevel.setLevel(itemBean.getLevel(), LevelView.USER);
        viewHolder.tvSex.setSex(itemBean.getAge(), itemBean.getSex());
        if (position == 0)
        {
            viewHolder.viewDivideBg1.setVisibility(View.VISIBLE);
            viewHolder.viewDivideBg2.setVisibility(View.GONE);
        } else
        {
            viewHolder.viewDivideBg1.setVisibility(View.GONE);
            viewHolder.viewDivideBg2.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(itemBean.getLocation()))
        {
            viewHolder.llAddress.setVisibility(View.VISIBLE);
            viewHolder.tvAddress.setText(itemBean.getLocation());
            viewHolder.llAddress.setOnClickListener((v)->
            {
                Intent intent = new Intent(mAct, ShowLocationAct.class);
                intent.putExtra("posx", itemBean.getPosx());
                intent.putExtra("posy", itemBean.getPosy());
                intent.putExtra("location", itemBean.getLocation());
                mAct.startActivity(intent);
            });
        } else
        {
            viewHolder.llAddress.setVisibility(View.GONE);
        }
        viewHolder.tvTime.setText(itemBean.getTimeToNow());
        viewHolder.ivHead.setHeadImageByUrl(itemBean.getPhurl());
        viewHolder.ivHead.showRightIcon("1");
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(itemBean.getUrl())
                .centerCrop()
                .placeholder(R.drawable.userhead)
                .error(R.drawable.userhead)
                .into(viewHolder.ivImg);

        if ("1".equals(itemBean.getIsLike()))
        {
            viewHolder.tvPraise.setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
        } else
        {
            viewHolder.tvPraise.setTextColor(ContextCompat.getColor(mAct, R.color.C0323));
        }

        viewHolder.tvPraise.setText(String.format("{eam-p-praise @dimen/f2} %s", itemBean.getLikedNum()));
        viewHolder.tvComment.setText(String.format("{eam-e60a @dimen/f2} %s", itemBean.getCommentNum()));
//        StringBuilder data = new StringBuilder();
//        data.append(String.format("<font color=%s>%s</font>", ContextCompat.getColor(mAct, R.color.C0313), itemBean.getExt().getColumnName()))
//                .append(String.format("<font color=%s>%s</font>", ContextCompat.getColor(mAct, R.color.C0321), " " + ));

        viewHolder.tvTitleArtical.setText(itemBean.getExt().getTitle());
        viewHolder.tvReadNums.setText(itemBean.getReadNum() + "人 已读");
    }


    private class ViewHolder extends RecyclerView.ViewHolder
    {
        LevelHeaderView ivHead;
        TextView tvNickname;
        TextView tvTime;
        TextView tvDistance;
        GenderView tvSex;
        LevelView tvLevel;
        LinearLayout llAllToArtical;
        ImageView ivImg;
        TextView tvTitleArtical;
        IconTextView tvPraise;
        IconTextView tvComment;
        LinearLayout llAddress;
        TextView tvAddress;
        View viewDivideBg1;
        View viewDivideBg2;
        TextView tvReadNums;

        public ViewHolder(View itemView)
        {
            super(itemView);


            ivHead = (LevelHeaderView) itemView.findViewById(R.id.iv_head);

            tvNickname = (TextView) itemView.findViewById(R.id.tv_nickname);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvDistance = (TextView) itemView.findViewById(R.id.tv_distance);
            tvSex = (GenderView) itemView.findViewById(R.id.tv_sex);
            tvLevel = (LevelView) itemView.findViewById(R.id.tv_level);
            llAllToArtical = (LinearLayout) itemView.findViewById(R.id.ll_all_to_artical);
            ivImg = (ImageView) itemView.findViewById(R.id.iv_img);
            tvTitleArtical = (TextView) itemView.findViewById(R.id.tv_title_artical);
            tvPraise = (IconTextView) itemView.findViewById(R.id.tv_praise);
            tvComment = (IconTextView) itemView.findViewById(R.id.tv_comment);
            llAddress = (LinearLayout) itemView.findViewById(R.id.ll_address);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_address);
            viewDivideBg1 = itemView.findViewById(R.id.view_divide_bg1);
            viewDivideBg2 = itemView.findViewById(R.id.view_divide_bg2);

            tvReadNums = itemView.findViewById(R.id.tv_readNums);
        }
    }

    /**
     * activity destroy 销毁 uVideoView
     */
    public void onDestroy()
    {

    }

    public void setVpArticalItemClick(VpArticalAdapter.VpArticalItemClick vpArticalItemClick)
    {
        this.vpArticalItemClick = vpArticalItemClick;
    }

    public interface VpArticalItemClick
    {
        /**
         * 点赞
         *
         * @param position
         * @param itemBean
         */
        void praiseClick(TextView tvPraise, int position, FTrendsItemBean itemBean);

        /**
         * 点击评论按钮
         */
        void commentClick(FTrendsItemBean itemBean);

        /**
         * 点击内容按钮
         */

        void contentClick(TextView view, FTrendsItemBean itemBean);

        void itemClick(TextView view, int position, FTrendsItemBean itemBean);

    }

    /**
     * 更新内容
     *
     * @param refreshPraise 是否只更新点赞 评论
     */
    public void notifyDataSetChanged(boolean refreshPraise)
    {
        this.refreshPraise = refreshPraise;
        super.notifyDataSetChanged();
    }
}

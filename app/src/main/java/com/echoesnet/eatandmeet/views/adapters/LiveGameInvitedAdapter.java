package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.GameInviteBean;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/11/13 0013
 * @description
 */
public class LiveGameInvitedAdapter extends RecyclerView.Adapter<LiveGameInvitedAdapter.ViewHolder>
{

    private Context mContext;
    private List<GameInviteBean> gameInviteList;
    private InvitedItemClickListener invitedItemClickListener;
    private String anchorUId;
    public void setInvitedItemClickListener(InvitedItemClickListener invitedItemClickListener)
    {
        this.invitedItemClickListener = invitedItemClickListener;
    }

    public LiveGameInvitedAdapter(Context mContext, List<GameInviteBean> gameInviteList,String anchorUId)
    {
        this.mContext = mContext;
        this.anchorUId = anchorUId;
        this.gameInviteList = gameInviteList;
    }

    @Override
    public LiveGameInvitedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_live_game_invited,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        GameInviteBean itemBean = gameInviteList.get(position);
        holder.tvName.setText(!TextUtils.isEmpty(itemBean.getRemark())?itemBean.getRemark():itemBean.getNicName());
        holder.genderView.setSex(itemBean.getAge(),itemBean.getSex());
        if (TextUtils.isEmpty(itemBean.getLevel())||"0".equals(itemBean.getLevel()))
            holder.levelHeaderView.setVisibility(View.GONE);
        else
        {
            holder.levelHeaderView.setVisibility(View.VISIBLE);
            holder.levelHeaderView.setLevel(itemBean.getLevel(),1);
        }
        if ("0".equals(itemBean.getStatus()))
        {
            holder.tvStatus.setVisibility(View.GONE);
            holder.imgAccept.setVisibility(View.VISIBLE);
            holder.imgDelete.setVisibility(View.VISIBLE);
        }else {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.imgAccept.setVisibility(View.GONE);
            holder.imgDelete.setVisibility(View.GONE);
            holder.tvStatus.setText("1".equals(itemBean.getStatus())?"已拒绝":"已结束");
            holder.tvStatus.setTextColor("1".equals(itemBean.getStatus())?
                    ContextCompat.getColor(mContext,R.color.C0313):ContextCompat.getColor(mContext,R.color.C0311));
        }
        holder.imgDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (invitedItemClickListener != null)
                    invitedItemClickListener.rejectInvite(position,itemBean);
            }
        });
        holder.imgAccept.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (invitedItemClickListener!= null)
                    invitedItemClickListener.acceptInvite(itemBean);
            }
        });
        holder.imgIsAnchor.setVisibility(View.GONE);
        if (TextUtils.equals(itemBean.getUId(), anchorUId))
            holder.imgIsAnchor.setVisibility(View.VISIBLE);
        holder.roundedImageView.showRightIcon(itemBean.getIsVuser());
        holder.roundedImageView.setHeadImageByUrl(itemBean.getUphUrl());
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemCount()
    {
        return gameInviteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        LevelHeaderView roundedImageView;
        TextView tvName;
        TextView tvStatus;
        GenderView genderView;
        LevelView levelHeaderView;
        ImageView imgIsAnchor;
        ImageView imgDelete;
        ImageView imgAccept;

        public ViewHolder(View itemView)
        {
            super(itemView);
            roundedImageView = itemView.findViewById(R.id.riv_head);
            tvName = itemView.findViewById(R.id.tv_name);
            tvStatus = itemView.findViewById(R.id.tv_status);
            genderView = itemView.findViewById(R.id.gender_view);
            levelHeaderView = itemView.findViewById(R.id.level_header_view);
            imgIsAnchor = itemView.findViewById(R.id.img_is_anchor);
            imgDelete = itemView.findViewById(R.id.img_delete);
            imgAccept = itemView.findViewById(R.id.img_accept);
        }
    }

    public interface InvitedItemClickListener
    {

        void rejectInvite(int position, GameInviteBean gameInviteBean);

        void acceptInvite(GameInviteBean gameInviteBean);
    }

}

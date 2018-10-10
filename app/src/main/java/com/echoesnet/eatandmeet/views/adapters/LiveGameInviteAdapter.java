package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.GameInviteBean;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
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
public class LiveGameInviteAdapter extends RecyclerView.Adapter<LiveGameInviteAdapter.ViewHolder>
{

    private Context mContext;
    private List<GameInviteBean> gameInviteList;
    private List<GameInviteBean> selectedList;
    private boolean showSelect = true;
    private String anchorUId;
    private GameInviteClickListener gameInviteClickListener;

    public LiveGameInviteAdapter(Context mContext, List<GameInviteBean> gameInviteList,String anchorUId)
    {
        this.mContext = mContext;
        this.anchorUId = anchorUId;
        this.gameInviteList = gameInviteList;
        this.selectedList = new ArrayList<>();
    }

    public void setGameInviteClickListener(GameInviteClickListener gameInviteClickListener)
    {
        this.gameInviteClickListener = gameInviteClickListener;
    }

    public void setShowSelect(boolean showSelect)
    {
        this.showSelect = showSelect;
    }

    public List<GameInviteBean> getSelectedList()
    {
        return selectedList;
    }

    @Override
    public LiveGameInviteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_live_game_invite,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        GameInviteBean itemBean = gameInviteList.get(position);
        itemBean.setPosition(position);
        holder.tvName.setText(!TextUtils.isEmpty(itemBean.getRemark())?itemBean.getRemark():itemBean.getNicName());
        holder.genderView.setSex(itemBean.getAge(),itemBean.getSex());
        if (TextUtils.isEmpty(itemBean.getLevel())||"0".equals(itemBean.getLevel()))
            holder.levelHeaderView.setVisibility(View.GONE);
        else
        {
            holder.levelHeaderView.setVisibility(View.VISIBLE);
            holder.levelHeaderView.setLevel(itemBean.getLevel(),1);
        }
        holder.tvIsSender.setVisibility("1".equals(itemBean.getStatus())?View.VISIBLE:View.GONE);
        holder.roundedImageView.setHeadImageByUrl(itemBean.getUphUrl());
        holder.roundedImageView.showRightIcon(itemBean.getIsVuser());
        holder.imgSelect.setVisibility(showSelect?View.VISIBLE:View.GONE);
        holder.imgIsAnchor.setVisibility(View.GONE);
        if (TextUtils.equals(itemBean.getUId(), anchorUId))
            holder.imgIsAnchor.setVisibility(View.VISIBLE);
        if (itemBean.isSelect())
        {
            holder.imgSelect.setImageResource(R.drawable.btn_yes);
        }else
        {
            holder.imgSelect.setImageResource(R.drawable.transparent);
        }
        holder.imgSelect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (itemBean.isSelect())
                {
                    holder.imgSelect.setImageResource(R.drawable.transparent);
                    itemBean.setSelect(false);
                    selectedList.remove(itemBean);
                }else
                {
                    holder.imgSelect.setImageResource(R.drawable.btn_yes);
                    itemBean.setSelect(true);
                    selectedList.add(itemBean);
                }
                if (gameInviteClickListener != null && selectedList != null){
                    gameInviteClickListener.refreshStatus(selectedList.size() > 0);
                }
            }
        });
    }

    public void initGameInvite(){
        if (gameInviteList != null)
            for (GameInviteBean gameInviteBean : gameInviteList)
            {
                gameInviteBean.setSelect(false);
            }
            if (selectedList != null)
                selectedList.clear();
            showSelect = false;
            notifyDataSetChanged();
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
        GenderView genderView;
        LevelView levelHeaderView;
        ImageView imgIsAnchor;
        ImageView imgSelect;
        LottieAnimationView tvIsSender;

        public ViewHolder(View itemView)
        {
            super(itemView);
            roundedImageView = itemView.findViewById(R.id.riv_head);
            tvName = itemView.findViewById(R.id.tv_name);
            genderView = itemView.findViewById(R.id.gender_view);
            levelHeaderView = itemView.findViewById(R.id.level_header_view);
            imgIsAnchor = itemView.findViewById(R.id.img_is_anchor);
            imgSelect = itemView.findViewById(R.id.img_select);
            tvIsSender = itemView.findViewById(R.id.tv_is_send);
        }
    }

    public interface GameInviteClickListener
    {
        void refreshStatus(boolean hasSelect);
    }

}

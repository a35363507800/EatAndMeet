package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.GameAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.GameItemBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/27 0027
 * @description
 */
public class TrendsGameListAdapter extends RecyclerView.Adapter<TrendsGameListAdapter.GameViewHolder>
{
    private static final String TAG=TrendsGameListAdapter.class.getSimpleName();

    private Activity mAct;
    private List<GameItemBean> gameItemBeanList;

    private static final int V_TYPE_ONE = 1;
    private static final int V_TYPE_TWO = 2;

    public TrendsGameListAdapter(Activity mAct, List<GameItemBean> gameItemBeanList)
    {
        this.mAct = mAct;
        this.gameItemBeanList = gameItemBeanList;
    }

    @Override
    public int getItemViewType(int position)
    {
        switch (getItemCount())
        {
            case 1:
                return V_TYPE_ONE;
            case 2:
                return V_TYPE_TWO;
            default:
                break;
        }
        return 0;
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view;
        switch (viewType)
        {
            case V_TYPE_ONE:
                view= LayoutInflater.from(parent.getContext()).inflate(R.layout.ritem_game_img1, parent, false);
                break;
            case V_TYPE_TWO:
                view= LayoutInflater.from(parent.getContext()).inflate(R.layout.ritem_game_img1, parent, false);
                int width = (CommonUtils.getScreenWidth(mAct) - CommonUtils.dp2px(mAct, 5)) / 2;
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, CommonUtils.dp2px(mAct, 100));
                view.findViewById(R.id.iv_game_img).setLayoutParams(layoutParams);
                break;
            default:
                view= LayoutInflater.from(parent.getContext()).inflate(R.layout.ritem_game_img_3more, parent, false);
                break;
        }
        GameViewHolder viewHolder = new GameViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GameViewHolder holder, int position)
    {
        final GameItemBean gameItem = gameItemBeanList.get(position);
        if (gameItem == null)
            return;
        GlideApp.with(EamApplication.getInstance())
                .load(gameItem.getGamePic())
                .centerCrop()
                .placeholder(R.drawable.qs_banner)
                .error(R.drawable.qs_banner)
                .into(holder.gameImg);
        Logger.t(TAG).d("game 列表刷新");
        holder.llGameImgContainer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Logger.t(TAG).d("game 点击事件触发");
                Intent intent = new Intent(mAct, GameAct.class);
                intent.putExtra("gameUrl", gameItem.getRedirectUrl());
                intent.putExtra("gameName", gameItem.getGameName());
                intent.putExtra("gameId", gameItem.getGameId());
                mAct.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return gameItemBeanList.size();
    }

    static class GameViewHolder extends RecyclerView.ViewHolder
    {
        ImageView gameImg;
        LinearLayout llGameImgContainer;
        public GameViewHolder(View view)
        {
            super(view);
            gameImg = (ImageView) view.findViewById(R.id.iv_game_img);
            llGameImgContainer= (LinearLayout) view.findViewById(R.id.ll_img_container);
        }
    }

}

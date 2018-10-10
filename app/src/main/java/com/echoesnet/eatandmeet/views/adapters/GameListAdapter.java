package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.GameAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.GameItemBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.MaskTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.echoesnet.eatandmeet.utils.GlideOptions.bitmapTransform;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @modifier
 * @createDate 2017/10/27
 * @description
 */
public class GameListAdapter extends BaseAdapter
{
   private String TAG = GameListAdapter.class.getSimpleName();
   private Activity mActivity;
   private List<GameItemBean> gameItemBeanList;
    public GameListAdapter(Activity mActivity, List<GameItemBean> gameItemBeanList)
    {
        this.mActivity = mActivity;
        this.gameItemBeanList = gameItemBeanList;
    }

    @Override
    public int getCount()
    {
        return gameItemBeanList.size();
    }

    @Override
    public GameItemBean getItem(int position)
    {
        return gameItemBeanList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        GameViewHolder gameViewHolder;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_game_image, null);
            gameViewHolder = new GameListAdapter.GameViewHolder();
            gameViewHolder.rivPic = (ImageView) convertView.findViewById(R.id.riv_pic);
            gameViewHolder.rivPicLast = (RoundedImageView) convertView.findViewById(R.id.riv_pic_last);
            gameViewHolder.vBgBlack = (View) convertView.findViewById(R.id.v_bg_black);
            gameViewHolder.ivImage = (ImageView) convertView.findViewById(R.id.iv_image);
            gameViewHolder.tvText = (TextView) convertView.findViewById(R.id.tv_text);
            convertView.setTag(gameViewHolder);
        } else
        {
            gameViewHolder = (GameListAdapter.GameViewHolder) convertView.getTag();
        }
        GameItemBean bean = gameItemBeanList.get(position);

        String status = bean.getStatus();

        switch (status)
        {
            case "1":
                GlideApp.with(EamApplication.getInstance())
                        .load(bean.getGamePic())
                        .centerCrop()
                        .apply(bitmapTransform(new RoundedCornersTransformation(12, 0,
                                RoundedCornersTransformation.CornerType.BOTTOM)))
                        .placeholder(R.drawable.qs_restaurant)
                        .error(R.drawable.qs_restaurant)
                        .into(gameViewHolder.rivPic);
                RelativeLayout.LayoutParams parm = (RelativeLayout.LayoutParams) gameViewHolder.rivPic.getLayoutParams();
               // parm.setMargins(0,(0-CommonUtils.dp2px(mActivity,2)),0,0);
                gameViewHolder.rivPic.setLayoutParams(parm);
                gameViewHolder.rivPic.setVisibility(View.VISIBLE);
                gameViewHolder.rivPic.setOnClickListener((v)->
                {
                        Logger.t(TAG).d("game 点击事件触发");
                        Intent intent = new Intent(mActivity, GameAct.class);
                        intent.putExtra("gameUrl", bean.getRedirectUrl());
                        intent.putExtra("gameName", bean.getGameName());
                        intent.putExtra("gameId", bean.getGameId());
                        mActivity.startActivity(intent);
                });

                break;
            case "2":
                GlideApp.with(EamApplication.getInstance())
                        .load(bean.getGamePic())
                        .centerCrop()
                        .placeholder(R.drawable.qs_banner)
                        .error(R.drawable.qs_banner)
                        .into(gameViewHolder.rivPicLast);
                gameViewHolder.rivPicLast.setVisibility(View.VISIBLE);

                gameViewHolder.rivPicLast.setOnClickListener((v)->
                {
                        Logger.t(TAG).d("game 不能玩 点击事件触发");
                        ToastUtils.showShortSafe("敬请期待");
                });
                gameViewHolder.vBgBlack.setVisibility(View.VISIBLE);
                gameViewHolder.ivImage.setVisibility(View.VISIBLE);
                gameViewHolder.tvText.setVisibility(View.VISIBLE);
                gameViewHolder.tvText.setText(bean.getStatusDesc());
                break;
                default:
                break;
        }
        return convertView;
    }

    private class GameViewHolder
    {
         ImageView rivPic;
         RoundedImageView rivPicLast;
         View vBgBlack;
         ImageView ivImage;
         TextView tvText;
    }
}

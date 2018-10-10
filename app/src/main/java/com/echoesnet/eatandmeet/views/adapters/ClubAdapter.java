package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.ClubDetailAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.ClubListBean;
import com.echoesnet.eatandmeet.utils.GlideApp;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @modifier
 * @createDate 2018/2/6
 * @description
 */
public class ClubAdapter extends BaseAdapter
{
    private static final String TAG = ClubAdapter.class.getSimpleName();
    private List<ClubListBean> resList;
    private Activity mAct;
    private ViewHolder holder;


    public ClubAdapter(Activity mAct, List<ClubListBean> list)
    {
        this.mAct = mAct;
        this.resList = list;
    }

    @Override
    public int getCount()
    {
        return resList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return resList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent)
    {
        if (convertView == null || convertView.getTag() == null)
        {
            convertView = LayoutInflater.from(mAct).inflate(R.layout.item_club_info, parent, false);
            holder = new ViewHolder();
            holder.llItemview = (LinearLayout)convertView.findViewById(R.id.ll_itemview);
            holder.imgSh = (ImageView) convertView.findViewById(R.id.img_sh);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
            holder.tvClubName = (TextView) convertView.findViewById(R.id.tv_club_name);
            holder.tvCount = (TextView) convertView.findViewById(R.id.tv_count);
            holder.tvPerPay = (TextView) convertView.findViewById(R.id.tv_per_pay);
            convertView.setTag(holder);
            // 对于listView 注意添加这一行 即可在item上使用高度
            //   AutoUtils.autoSize(convertView);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        ClubListBean itemBean = resList.get(i);

        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(itemBean.getUrl())
                .centerCrop()
                .skipMemoryCache(false)
                .placeholder(R.drawable.qs_cai_canting)
                .error(R.drawable.qs_cai_canting)
                .into(holder.imgSh);
        holder.imgSh.setOnClickListener((v)->
        {
            Intent intent = new Intent(mAct, ClubDetailAct.class);
            intent.putExtra("clubId",itemBean.getId());
            intent.putExtra("clubName",itemBean.getName());
            intent.putExtra("clubPic",itemBean.getUrl());
            intent.putExtra("posx",itemBean.getPosx());
            intent.putExtra("posy",itemBean.getPosy());
            mAct.startActivity(intent);
        });
        holder.llItemview.setOnClickListener((v)->
        {
            Intent intent = new Intent(mAct, ClubDetailAct.class);
            intent.putExtra("clubId",itemBean.getId());
            intent.putExtra("clubName",itemBean.getName());
            intent.putExtra("clubPic",itemBean.getUrl());
            intent.putExtra("posx",itemBean.getPosx());
            intent.putExtra("posy",itemBean.getPosy());
            mAct.startActivity(intent);
        });

        char rmb = 165;
        String yuan = String.valueOf(rmb);
        holder.tvClubName.setText(itemBean.getName());
        holder.tvPrice.setText(yuan + itemBean.getPrice()+"起");
        holder.tvPerPay.setText(itemBean.getPerPrice());


        return convertView;
    }
    static class ViewHolder
    {
        private ImageView imgSh;
        private TextView tvPrice;
        private TextView tvClubName;
        private TextView tvCount;
        private TextView tvPerPay;
        private LinearLayout llItemview;
    }
    public void destroy()
    {

    }
    public interface KtvAdapterClick
    {
        void itemClick();
    }


}

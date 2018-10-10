package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.SearchUserBean;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @version 1.0
 * @modifier
 * @createDate 2017/7/27
 * @description
 */
public class PublicSearchAdapter extends RecyclerView.Adapter<PublicSearchAdapter.MyViewHolder>
{
    private List<SearchUserBean> list;
    private Activity mActivity;

    public PublicSearchAdapter(Activity mActivity, List<SearchUserBean> list)
    {
        this.mActivity = mActivity;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_public_search, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        holder.tvName.setText(list.get(position).getNicName());
        holder.tvInfo.setText(list.get(position).getSignature());
        holder.levelHeaderView.setHeadImageByUrl(list.get(position).getUphUrl());
        holder.levelView.setLevel(list.get(position).getLevel(), 1);
        if(list.get(position).getFocus().equals("0"))
        {
            holder.tvExplain.setText("已关注");
        }else
        {
            holder.tvExplain.setText("未关注");
        }
        if("男".equals(list.get(position).getSex()))
        {
            holder.iconTextView.setBackgroundResource(R.drawable.shape_round_2corner_15_mc7);
            holder.iconTextView.setText(String.format("%s %s", "{eam-e950}",list.get(position).getAge()));
        } else
        {
            holder.iconTextView.setBackgroundResource(R.drawable.shape_round_2corner_15_mc5);
            holder.iconTextView.setText(String.format("%s %s", "{eam-e94f}",list.get(position).getAge()));
        }

    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tvName;
        public TextView tvInfo;
        public TextView tvExplain;
        public LevelView levelView;
        public IconTextView iconTextView;
        public LevelHeaderView levelHeaderView;

        public MyViewHolder(View view)
        {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvInfo = (TextView) view.findViewById(R.id.tv_info);
            levelHeaderView = (LevelHeaderView) view.findViewById(R.id.riv_head);
            iconTextView = (IconTextView) view.findViewById(R.id.itv_age);
            tvExplain = (TextView) view.findViewById(R.id.tv_explain);
            levelView = (LevelView) view.findViewById(R.id.lv_level);
        }
    }

}

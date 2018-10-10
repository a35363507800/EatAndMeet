package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.SearchUserBean;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;

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
public class PublicSearchRefreshAdapter extends BaseAdapter
{
    private List<SearchUserBean> list;
    private Activity mActivity;
    private MyViewHolder holder;

    private OnFocusClickListener clickListener;

    public PublicSearchRefreshAdapter(Activity mActivity, List<SearchUserBean> list)
    {
        this.mActivity = mActivity;
        this.list = list;
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_public_search, parent, false);
            holder = new MyViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvInfo = (TextView) convertView.findViewById(R.id.tv_info);
            holder.levelHeaderView = (LevelHeaderView) convertView.findViewById(R.id.riv_head);
            holder.iconTextView = (GenderView) convertView.findViewById(R.id.itv_age);
            holder.tvExplain = (TextView) convertView.findViewById(R.id.tv_explain);
            holder.levelView = (LevelView) convertView.findViewById(R.id.lv_level);

            convertView.setTag(holder);
        }
        else
        {
            holder = (MyViewHolder) convertView.getTag();
        }
        String userName = TextUtils.isEmpty(list.get(position).getRemark()) ? list.get(position).getNicName() : list.get(position).getRemark();
        holder.tvName.setText(userName);
        holder.tvInfo.setText(list.get(position).getSignature());
        holder.levelHeaderView.setHeadImageByUrl(list.get(position).getUphUrl());
        holder.levelHeaderView.showRightIcon(list.get(position).getIsVuser());
        holder.levelView.setLevel(list.get(position).getLevel(), 1);
        if (list.get(position).getFocus().equals("2"))
        {
            holder.tvExplain.setText("互相关注");
            holder.tvExplain.setTextColor(ContextCompat.getColor(mActivity, R.color.C0323));
            holder.tvExplain.setBackgroundResource(R.drawable.round_cornor_11_white_bg);
            holder.tvExplain.setEnabled(false);
        }
        else if (list.get(position).getFocus().equals("1"))
        {
            holder.tvExplain.setText("已关注");
            holder.tvExplain.setTextColor(ContextCompat.getColor(mActivity, R.color.C0323));
            holder.tvExplain.setBackgroundResource(R.drawable.round_cornor_11_white_bg);
            holder.tvExplain.setEnabled(false);
        }
        else
        {
            holder.tvExplain.setTextColor(ContextCompat.getColor(mActivity, R.color.C0412));
            holder.tvExplain.setBackgroundResource(R.drawable.round_cornor_11_c0412_bg);
            holder.tvExplain.setText("+关注");
            holder.tvExplain.setEnabled(true);
        }

        holder.iconTextView.setSex(list.get(position).getAge(), list.get(position).getSex());

        holder.tvExplain.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (clickListener != null)
                    clickListener.onFocusClick(position);
            }
        });
        return convertView;
    }

    public class MyViewHolder
    {
        public TextView tvName;
        public TextView tvInfo;
        public TextView tvExplain;
        public LevelView levelView;
        public GenderView iconTextView;
        public LevelHeaderView levelHeaderView;
    }

    public interface OnFocusClickListener
    {
        void onFocusClick(int position);
    }

    public void setOnFocusClickListener(OnFocusClickListener itemClickListener)
    {
        clickListener = itemClickListener;
    }

}

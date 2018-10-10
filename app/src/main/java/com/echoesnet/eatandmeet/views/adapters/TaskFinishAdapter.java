package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.FinishTaskBean;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/8/25 0025
 * @description
 */
public class TaskFinishAdapter extends BaseAdapter
{
    private Context mContext;
    private List<FinishTaskBean.RewardsBean> rewardsBeanList;

    public TaskFinishAdapter(Context mContext, List<FinishTaskBean.RewardsBean> rewardsBeanList)
    {
        this.mContext = mContext;
        this.rewardsBeanList = rewardsBeanList;
    }

    @Override
    public int getCount()
    {
        return rewardsBeanList.size();
    }

    @Override
    public FinishTaskBean.RewardsBean getItem(int position)
    {
        return rewardsBeanList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.task_finish_item,parent,false);
            viewHolder.iconImg = (ImageView) convertView.findViewById(R.id.img_icon);
            viewHolder.numTv = (TextView) convertView.findViewById(R.id.tv_num);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FinishTaskBean.RewardsBean itemBean = rewardsBeanList.get(position);
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(itemBean.getIcon())
                .placeholder(R.drawable.userhead)
                .error(R.drawable.userhead)
                .centerCrop()
                .into(viewHolder.iconImg);
        String numDes = itemBean.getName() + "+" + itemBean.getNum();
        SpannableString spannableString = new SpannableString(numDes);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,R.color.C0313)),
                itemBean.getName().length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
         viewHolder.numTv.setText(spannableString);
        return convertView;
    }

    public static class ViewHolder{
        public ImageView iconImg;
        public TextView numTv;
    }
}

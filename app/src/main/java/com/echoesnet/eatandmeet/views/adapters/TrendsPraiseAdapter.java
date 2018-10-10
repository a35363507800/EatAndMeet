package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.models.bean.TrendsPraiseBean;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/25 0025
 * @description
 */
public class TrendsPraiseAdapter extends BaseAdapter
{
    private Activity mAct;
    private List<TrendsPraiseBean> mTrendsPraiseList;

    public TrendsPraiseAdapter(Activity mAct, List<TrendsPraiseBean> trendsPraiseList)
    {
        this.mAct = mAct;
        this.mTrendsPraiseList = trendsPraiseList;
    }

    @Override
    public int getCount()
    {
        return mTrendsPraiseList.size();
    }

    @Override
    public TrendsPraiseBean getItem(int position)
    {
        return mTrendsPraiseList.get(position);
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
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mAct).inflate(R.layout.item_trends_praise, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.levelIconTv = (LevelView) convertView.findViewById(R.id.tv_level);
            viewHolder.nickNameTv = (TextView) convertView.findViewById(R.id.tv_nick_name);
            viewHolder.signTv = (TextView) convertView.findViewById(R.id.tv_sign);
            viewHolder.timeTv = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.sexIconTv = (GenderView) convertView.findViewById(R.id.tv_sex);
            viewHolder.headRiv = (LevelHeaderView) convertView.findViewById(R.id.img_head);
            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final TrendsPraiseBean trendsPraiseBean = getItem(position);
        viewHolder.headRiv.setHeadImageByUrl(trendsPraiseBean.getPhurl());
        viewHolder.headRiv.showRightIcon(trendsPraiseBean.getIsVuser());
        viewHolder.headRiv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mAct, CNewUserInfoAct.class);
                intent.putExtra("toUId", trendsPraiseBean.getUId());
                mAct.startActivity(intent);
            }
        });
        viewHolder.nickNameTv.setText(!TextUtils.isEmpty(trendsPraiseBean.getRemark()) ? trendsPraiseBean.getRemark() : trendsPraiseBean.getNicName());
        viewHolder.signTv.setText(trendsPraiseBean.getSignature());
        viewHolder.levelIconTv.setLevel(trendsPraiseBean.getLevel(), 1);
        viewHolder.timeTv.setText(trendsPraiseBean.getTimeToNow());
        viewHolder.sexIconTv.setSex(trendsPraiseBean.getAge(),trendsPraiseBean.getSex());
        return convertView;
    }

    static class ViewHolder
    {
        LevelHeaderView headRiv;
        TextView nickNameTv;
        TextView timeTv;
        TextView signTv;
        GenderView sexIconTv;
        LevelView levelIconTv;
    }

}

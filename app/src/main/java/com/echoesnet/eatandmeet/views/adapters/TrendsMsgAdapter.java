package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.TrendsMsgBean;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.IMUtils.EamSmileUtils;
import com.echoesnet.eatandmeet.views.widgets.FolderTextView;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;

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
public class TrendsMsgAdapter extends BaseAdapter
{
    private Activity mAct;
    private TrendsMsgItemClick itemClick;
    private List<TrendsMsgBean> trendsMsgBeanList;

    public TrendsMsgAdapter(Activity mAct, List<TrendsMsgBean> trendsMsgBeanList)
    {
        this.mAct = mAct;
        this.trendsMsgBeanList = trendsMsgBeanList;
    }

    @Override
    public int getCount()
    {
        return trendsMsgBeanList.size();
    }

    @Override
    public TrendsMsgBean getItem(int position)
    {
        return trendsMsgBeanList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mAct).inflate(R.layout.item_trends_msg,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.contentTv = (FolderTextView) convertView.findViewById(R.id.tv_content);
            viewHolder.timeAndDistanceTv = (TextView) convertView.findViewById(R.id.tv_time_and_distance);
            viewHolder.levelIconTv = (LevelView) convertView.findViewById(R.id.icon_tv_level);
            viewHolder.nickNameTv = (TextView) convertView.findViewById(R.id.tv_nick_name);
            viewHolder.replyTv = (TextView) convertView.findViewById(R.id.tv_reply);
            viewHolder.sexIconTv = (GenderView) convertView.findViewById(R.id.icon_tv_sex);
            viewHolder.playIconTv = (IconTextView) convertView.findViewById(R.id.icon_tv_play);
            viewHolder.headRiv = (LevelHeaderView) convertView.findViewById(R.id.riv_head);
            viewHolder.coverRiv = (RoundedImageView) convertView.findViewById(R.id.riv_cover);
            viewHolder.tvDescribe = (TextView) convertView.findViewById(R.id.tv_describe);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final TrendsMsgBean itemBean = trendsMsgBeanList.get(position);
        viewHolder.headRiv.setHeadImageByUrl(itemBean.getPhurl());
        viewHolder.headRiv.showRightIcon(itemBean.getIsVuser());
        viewHolder.nickNameTv.setText(!TextUtils.isEmpty(itemBean.getRemark())?itemBean.getRemark():itemBean.getNicName());
        if ("0".equals(itemBean.getType()))
        {
            viewHolder.contentTv.setText("赞了你的动态");
            viewHolder.replyTv.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.contentTv.setText(EamSmileUtils.getSmiledText(mAct,itemBean.getComment()));
            viewHolder.contentTv.init();
            viewHolder.replyTv.setVisibility(View.VISIBLE);
        }
        viewHolder.timeAndDistanceTv.setText(String.format("%s·%s",itemBean.getDate(),itemBean.getDistance()));
        viewHolder.sexIconTv.setSex(itemBean.getAge(),itemBean.getSex());
        viewHolder.levelIconTv.setLevel(itemBean.getLevel(),1);

        String url = "";
        if (!TextUtils.isEmpty(itemBean.getDetail().getThumbnails()))
        {
            viewHolder.tvDescribe.setVisibility(View.GONE);
            viewHolder.playIconTv.setVisibility(View.VISIBLE);
            url = itemBean.getDetail().getThumbnails();
            viewHolder.coverRiv.setVisibility(View.VISIBLE);
        }else
        {

            if(!TextUtils.isEmpty(itemBean.getDetail().getUrl()))
            {
                String[] arrUrls = itemBean.getDetail().getUrl().split("!=end=!");
                if (arrUrls!=null&&arrUrls.length>=1)
                {
                    url = arrUrls[0];
                }
                viewHolder.playIconTv.setVisibility(View.GONE);
                viewHolder.coverRiv.setVisibility(View.VISIBLE);
                viewHolder.tvDescribe.setVisibility(View.GONE);
             //   url = itemBean.getDetail().getUrl();
            } else
            {
                viewHolder.playIconTv.setVisibility(View.GONE);
                viewHolder.coverRiv.setVisibility(View.GONE);
                viewHolder.tvDescribe.setVisibility(View.VISIBLE);
            }

//            viewHolder.playIconTv.setVisibility(View.GONE);
//            url = itemBean.getDetail().getUrl();
//            viewHolder.coverRiv.setVisibility(View.GONE);
        }
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.qs_cai_user)
                .error(R.drawable.qs_cai_user)
                .into(viewHolder.coverRiv);

        if (!TextUtils.isEmpty(itemBean.getDetail().getContent()))
            viewHolder.tvDescribe.setText(EamSmileUtils.getSmiledText(mAct, itemBean.getDetail().getContent(),40,40));

        viewHolder.contentTv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (itemClick != null)
                    itemClick.contentClickCallback(itemBean, position);
            }
        });
        viewHolder.coverRiv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (itemClick != null)
                    itemClick.contentClickCallback(itemBean, position);
            }
        });
        viewHolder.tvDescribe.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (itemClick != null)
                    itemClick.contentClickCallback(itemBean, position);
            }
        });
        viewHolder.replyTv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (itemClick != null)
                    itemClick.replyClickCallback(itemBean, position);
            }
        });
        return convertView;
    }

    static class ViewHolder{
        LevelHeaderView headRiv;
        TextView nickNameTv;
        GenderView sexIconTv;
        LevelView levelIconTv;
        IconTextView playIconTv;
//        TextView contentTv;
        TextView timeAndDistanceTv;
        TextView replyTv;
        RoundedImageView coverRiv;
        FolderTextView contentTv;
        TextView tvDescribe;
    }

    public interface TrendsMsgItemClick
    {
        void contentClickCallback(TrendsMsgBean itemBean, int position);
        void replyClickCallback(TrendsMsgBean itemBean, int position);
    }

    public void setTrendsMsgItemClick(TrendsMsgAdapter.TrendsMsgItemClick itemClick)
    {
        this.itemClick = itemClick;
    }

}

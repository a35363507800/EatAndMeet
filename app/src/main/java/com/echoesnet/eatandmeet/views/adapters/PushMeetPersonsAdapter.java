package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.MeetPersonBean;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wangben on 2016/5/27.
 */
public class PushMeetPersonsAdapter extends BaseAdapter
{
    private Context mContext;
    private List<MeetPersonBean> meetPersonLst;


    public PushMeetPersonsAdapter(Context mContext, List<MeetPersonBean> meetPersonLst)
    {
        this.mContext = mContext;
        this.meetPersonLst = meetPersonLst;
    }

    @Override
    public int getCount()
    {
        return meetPersonLst.size();
    }

    @Override
    public Object getItem(int position)
    {
        return meetPersonLst.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder=null;
        if (convertView==null)
        {
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(mContext).inflate(R.layout.gvitem_meet_person,null);
            viewHolder.tvNickname= (TextView) convertView.findViewById(R.id.tv_meet_name);
            viewHolder.tvPersonInfo= (TextView) convertView.findViewById(R.id.tv_meet_age);
            viewHolder.tvCurrentOrder= (TextView) convertView.findViewById(R.id.tv_meet_order);
            viewHolder.ivHeadImage= (RoundedImageView) convertView.findViewById(R.id.riv_meet_head);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder=(ViewHolder) convertView.getTag();
        }
        MeetPersonBean personBean= (MeetPersonBean) getItem(position);
        viewHolder.tvNickname.setText(personBean.getNicName());
        viewHolder.tvPersonInfo.setText(personBean.getAge() + "岁 " + personBean.getCity());

        String time = personBean.getoTime().substring(0, 10);
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(time);
            String oTime = new SimpleDateFormat("yyyy年MM月dd日").format(date);
            Logger.t("时间测试").d(oTime + " , " + oTime.substring(5, oTime.length()));
            viewHolder.tvCurrentOrder.setText("已预订" + oTime.substring(5, oTime.length())
//                    + personBean.getrName()==null?"":personBean.getNicName());
                    + personBean.getrAddr());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try
        {
            if(!TextUtils.isEmpty(personBean.getUpUrl()))
            {
                GlideApp.with(EamApplication.getInstance())
                        .load(personBean.getUpUrl())
                        .into(viewHolder.ivHeadImage);
            } else {
                GlideApp.with(EamApplication.getInstance())
                        .load(personBean.getUpUrl())
                        .placeholder(R.drawable.userhead)
                        .into(viewHolder.ivHeadImage);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return convertView;
    }

    private final class ViewHolder
    {
        TextView tvNickname;
        TextView tvPersonInfo;
        TextView tvCurrentOrder;
        RoundedImageView ivHeadImage;
    }
}

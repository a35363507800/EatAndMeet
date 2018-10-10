package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.ChosenAdminBean;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.OnClickEvent;
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
 * @createDate 2017/8/10
 * @description
 */

public class HouseManageAdapter extends BaseAdapter
{
    private List<ChosenAdminBean> houseManageBeanList;
    private Context mContext;
    private LayoutInflater inflater;
    private MyHolder holder;

    public HouseManageAdapter(Context context, List<ChosenAdminBean> houseManageBeanList)
    {
        this.mContext = context;
        this.houseManageBeanList = houseManageBeanList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount()
    {
        return houseManageBeanList == null ? 0 : houseManageBeanList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return houseManageBeanList.get(position);
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
            holder = new MyHolder();
            convertView = inflater.inflate(R.layout.item_house_manage, null);
            holder.tvManageName = (TextView) convertView.findViewById(R.id.tv_manage_name);
            holder.ivSex = (GenderView) convertView.findViewById(R.id.iv_sex);
            holder.rivManage = (LevelHeaderView) convertView.findViewById(R.id.riv_manage);
            holder.btnOperate = (Button) convertView.findViewById(R.id.btn_operate);
            holder.levelView = (LevelView) convertView.findViewById(R.id.levelView);
            convertView.setTag(holder);
        } else
        {
            holder = (MyHolder) convertView.getTag();
        }

        final ChosenAdminBean bean = houseManageBeanList.get(position);

        holder.btnOperate.setOnClickListener(new OnClickEvent(500)
        {
            @Override
            public void singleClick(View v)
            {
                listener.onViewClick(bean, position);
            }
        });
        holder.ivSex.setSex(houseManageBeanList.get(position).getAge(), bean.getSex());
        holder.tvManageName.setText(bean.getNicName());
        holder.levelView.setLevel(bean.getLevel(), LevelView.USER);
        holder.rivManage.setHeadImageByUrl(bean.getPhUrl());
        holder.rivManage.showRightIcon(bean.getIsVuser());
//        holder.rivManage.setLevel(bean.getLevel());
        return convertView;
    }

    public class MyHolder
    {
        TextView tvManageName;
        GenderView ivSex;
        LevelHeaderView rivManage;
        Button btnOperate;
        LevelView levelView;
    }

    private HouseManageListener listener;

    public interface HouseManageListener
    {
        void onViewClick(ChosenAdminBean bean, int position);
    }

    public void setHouseManageBeanList(HouseManageListener listener)
    {
        this.listener = listener;
    }

}

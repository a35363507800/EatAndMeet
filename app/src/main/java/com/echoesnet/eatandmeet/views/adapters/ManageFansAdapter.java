package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.ChosenFansBean;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
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

public class ManageFansAdapter extends BaseAdapter
{

    private Context mContext;
    // 填充数据的list
    private List<ChosenFansBean> list;
    // 用来导入布局
    private LayoutInflater inflater = null;
    private Holder holder;
    public int mPosition = -1;


    public ManageFansAdapter(Activity mContext, List<ChosenFansBean> list)
    {
        this.mContext = mContext;
        this.list = list;
        inflater = LayoutInflater.from(mContext);
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
            holder = new Holder();
            convertView = inflater.inflate(R.layout.item_manage_fans, parent, false);
            holder.levelHeaderView = (LevelHeaderView) convertView.findViewById(R.id.ri_focus);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvFocusAge = (GenderView) convertView.findViewById(R.id.tv_fans_age);
            holder.radioBtn = (IconTextView) convertView.findViewById(R.id.radio_btn);
            holder.levelView = (LevelView) convertView.findViewById(R.id.lv_level);

            convertView.setTag(holder);
        } else
        {
            holder = (Holder) convertView.getTag();
        }

//        if (mPosition == position) {
//            holder.radioBtn.setChecked(true);
//        } else {
//            holder.radioBtn.setChecked(false);
//        }
//
//        if (holder.radioBtn.isChecked()) {
//            holder.radioBtn.setButtonDrawable(ContextCompat.getDrawable(mContext, R.drawable.choise_lanyuanda_yes_xhdpi));
//        } else {
//            holder.radioBtn.setButtonDrawable(ContextCompat.getDrawable(mContext, R.drawable.choise_lanyuanda_no_xhdpi));
//        }
        if (mPosition == position)
        {
            holder.radioBtn.setTag("true");
        } else
        {
            holder.radioBtn.setTag("false");
        }
        if (holder.radioBtn.getTag().equals("true"))
        {
            holder.radioBtn.setText(String.format("%s", "{eam-s-grade2}"));
            holder.radioBtn.setTextColor(ContextCompat.getColor(mContext, R.color.C0412));
        } else
        {
            holder.radioBtn.setText(String.format("%s", "{eam-s-grade1}"));
            holder.radioBtn.setTextColor(ContextCompat.getColor(mContext, R.color.C0331));
        }

        holder.levelView.setLevel(list.get(position).getLevel(), LevelView.USER);
        holder.levelHeaderView.setHeadImageByUrl(list.get(position).getUphUrl());
        holder.levelHeaderView.showRightIcon(list.get(position).getIsVuser());
//        holder.levelHeaderView.setLevel(list.get(position).getLevel());
        holder.tvName.setText(list.get(position).getNicName());
        holder.tvFocusAge.setSex(list.get(position).getAge(), list.get(position).getSex());
        return convertView;
    }

    public class Holder
    {
        LevelHeaderView levelHeaderView;
        TextView tvName;
        GenderView tvFocusAge;
        public IconTextView radioBtn;
        LevelView levelView;
    }
}

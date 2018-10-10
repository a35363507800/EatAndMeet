package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.SysNewsMessageListBean;

import java.util.List;

/**
 * Created by lc on 2017/7/12 13.
 */

public class SysNewsItemAdapter extends BaseAdapter
{
    private Activity mAct;
    private List<SysNewsMessageListBean> list;

    public SysNewsItemAdapter(Activity mAct, List<SysNewsMessageListBean> list)
    {
        this.mAct = mAct;
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
        SysNewsItemAdapter.ViewHolder viewHolder = null;
        SysNewsMessageListBean itemBean = list.get(position);
        if (convertView == null)
        {
            viewHolder = new SysNewsItemAdapter.ViewHolder();
            convertView = LayoutInflater.from(mAct).inflate(R.layout.item_sys_news, parent, false);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (SysNewsItemAdapter.ViewHolder) convertView.getTag();
        }
        viewHolder.tvTime.setText(itemBean.getCreateTime());
        viewHolder.tvContent.setText(itemBean.getDesc());
        return convertView;
    }

    static class ViewHolder
    {
         TextView tvContent;
         TextView tvTime;
    }
}

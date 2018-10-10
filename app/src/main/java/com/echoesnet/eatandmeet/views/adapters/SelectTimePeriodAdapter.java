package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.datamodel.TimePeriodModel;

import java.util.List;

/**
 * Created by Administrator on 2016/5/26.
 */
public class SelectTimePeriodAdapter extends BaseAdapter
{
    private Context mContext;
    private List<TimePeriodModel> periodLst;

    public SelectTimePeriodAdapter(Context mContext, List<TimePeriodModel> periodLst)
    {
        this.mContext = mContext;
        this.periodLst = periodLst;
    }

    private int clickTemp = -1;

    //标识选择的Item
    public void setSelection(int position)
    {
        clickTemp = position;
    }

    @Override
    public int getCount()
    {
        return periodLst.size();
    }

    @Override
    public Object getItem(int position)
    {
        return periodLst.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gvitem_period, null);
            holder.tvPeriod = (TextView) convertView.findViewById(R.id.tv_period);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        TimePeriodModel tpModel = (TimePeriodModel) getItem(position);
        holder.tvPeriod.setText(tpModel.getTimeStr());

        if (tpModel.getStatus().equals("0"))
        {
            holder.tvPeriod.setBackgroundResource(R.drawable.round_corner_gray_bg);
            holder.tvPeriod.setTextColor(ContextCompat.getColor(mContext, R.color.FC3));
        }else
        //if (tpModel.getStatus().equals("1"))
        {
            holder.tvPeriod.setBackgroundResource(R.drawable.round_corners_frame_mc3);
            holder.tvPeriod.setTextColor(ContextCompat.getColor(mContext,R.color.MC1));
/*            if (clickTemp == position)
            {
                //当前选中的Item改变背景颜色
                holder.tvPeriod.setBackgroundResource(R.drawable.round_corner_red_bg);
                holder.tvPeriod.setTextColor(ContextCompat.getColor(mContext,R.color.white));
            } else
            {
                holder.tvPeriod.setBackgroundResource(R.drawable.round_corners_frame_mc3);
                holder.tvPeriod.setTextColor(ContextCompat.getColor(mContext,R.color.MC1));
            }*/
        }
        return convertView;
    }

    private final class ViewHolder
    {
        TextView tvPeriod;
    }
}

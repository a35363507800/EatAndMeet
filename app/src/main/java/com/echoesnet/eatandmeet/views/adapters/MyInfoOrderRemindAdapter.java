package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.MyInfoOrderRemindBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/7/18.
 */
public class MyInfoOrderRemindAdapter extends BaseAdapter {
    private List<MyInfoOrderRemindBean> list;
    private Context context;

    public MyInfoOrderRemindAdapter(Context context, List<MyInfoOrderRemindBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.litem_order_remind, null);
            viewHolder.tv_time_date = (TextView) convertView.findViewById(R.id.tv_time_date);
            viewHolder.tv_context = (TextView) convertView.findViewById(R.id.tv_context);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String time = list.get(position).getDate();
        Date date = null;
        try
        {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
            String resDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            viewHolder.tv_time_date.setText(resDate.substring(0,resDate.length()-3));
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        viewHolder.tv_context.setText(list.get(position).getMsg());
        return convertView;
    }

    class ViewHolder {
        public TextView tv_time_date;
        public TextView tv_context;
    }
}

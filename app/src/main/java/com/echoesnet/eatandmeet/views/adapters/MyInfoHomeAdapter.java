package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/7/18.
 */
public class MyInfoHomeAdapter extends BaseAdapter
{
    private List<HashMap<String, Object>> list;
    private Context context;

    public MyInfoHomeAdapter(Context context, List<HashMap<String, Object>> list)
    {
        this.context = context;
        this.list = list;
    }

    public void setList(List<HashMap<String, Object>> list)
    {
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
        ViewHolder viewHolder;
        viewHolder = new ViewHolder();
        convertView = LayoutInflater.from(context).inflate(R.layout.item_myinfo_home, null);
        viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        viewHolder.iv_img = (ImageView) convertView.findViewById(R.id.iv_img);
        viewHolder.unread_msg_number = (TextView) convertView.findViewById(R.id.unread_msg_number);
        convertView.setTag(viewHolder);
        HashMap<String, Object> map = (HashMap<String, Object>) getItem(position);

        if (map != null)
        {
            viewHolder.iv_img.setImageResource((Integer) map.get("img"));
            viewHolder.tv_name.setText(map.get("name").toString());
            // 控制未读信息消息显示
            if (map.get("status").equals("1"))
            {
                viewHolder.unread_msg_number.setVisibility(View.VISIBLE);
            }
            else
            {
                viewHolder.unread_msg_number.setVisibility(View.INVISIBLE);
            }
        }

        return convertView;
    }

    class ViewHolder
    {
        public TextView tv_name;
        public ImageView iv_img;
        public TextView unread_msg_number;
    }
}

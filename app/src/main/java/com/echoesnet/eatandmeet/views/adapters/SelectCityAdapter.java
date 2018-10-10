package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;

/**
 * Created by Administrator on 2016/11/16.
 */

public class SelectCityAdapter extends BaseAdapter
{

    private Context context;
    private String[] data;
    private LayoutInflater inflater;

    public SelectCityAdapter(Context context, String[] data)
    {
        this.data = data;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return data.length;
    }

    @Override
    public Object getItem(int position)
    {
        return data[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        String bean = data[position];
        ViewHolder viewHolder;
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.act_select_city_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_Item = (TextView) convertView.findViewById(R.id.tv_item);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if ("西安".equals(bean))
        {
            viewHolder.tv_Item.setBackgroundResource(R.drawable.btn_liu);
            viewHolder.tv_Item.setTextColor(ContextCompat.getColor(context,R.color.FC1));
        }
        else
        {
            viewHolder.tv_Item.setBackgroundResource(R.drawable.city_no_option);
        }
        viewHolder.tv_Item.setText(data[position]);

        return convertView;
    }

    public class ViewHolder
    {
        public TextView tv_Item;
    }
}

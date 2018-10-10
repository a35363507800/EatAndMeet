package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.echoesnet.eatandmeet.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/12.
 */

public class MyChatInputIconGroupAdapter extends BaseAdapter
{

    private Context context;
    private List<Integer> images = new ArrayList<>();

    public MyChatInputIconGroupAdapter(Context context, List<Integer> imgs)
    {
        this.context = context;
        this.images = imgs;
    }

    @Override
    public int getCount()
    {
        return images.size();
    }

    @Override
    public Object getItem(int position)
    {
        return images.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_input_icon_item, parent, false);
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null)
        {
            holder = new ViewHolder();
            holder.imageItem = (ImageView) convertView.findViewById(R.id.image_item);
            convertView.setTag(holder);
        }
        holder.imageItem.setImageResource(images.get(position));
        return convertView;
    }

    static class ViewHolder
    {
        ImageView imageItem;
    }
}

package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/17.
 */

public class SharePopupAdapter extends BaseAdapter
{

    private Context mContext;
    private List<Map<String, Object>> aList;

    public SharePopupAdapter(Context mContext, List<Map<String, Object>> aList)
    {
        this.mContext = mContext;
        this.aList = aList;
    }

    @Override
    public int getCount()
    {
        return aList.size();
    }

    @Override
    public Map<String,Object> getItem(int i)
    {
        return aList.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        Holder holder;
        if (view == null)
        {
            holder = new Holder();
            view = LayoutInflater.from(mContext).inflate(R.layout.share_popup_item, null);
            holder.shareImg = (ImageView) view.findViewById(R.id.share_img);
            holder.shareText = (TextView) view.findViewById(R.id.share_text);
            view.setTag(holder);
        }
        else
        {
            holder = (Holder) view.getTag();
        }
        Map<String,Object>map=getItem(i);
        int value = (int) map.get("icon");
        String des=String.valueOf(map.get("des"));
        holder.shareImg.setImageResource(value);
        holder.shareText.setText(des);

        return view;
    }

    class Holder
    {
        ImageView shareImg;
        TextView shareText;
    }
}

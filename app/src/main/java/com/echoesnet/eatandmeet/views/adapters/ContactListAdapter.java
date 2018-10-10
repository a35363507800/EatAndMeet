package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/12/1.
 */

public class ContactListAdapter extends BaseAdapter
{
    private OnItemClickListener mOnItemClickListener;
    private Context mContext;
    private List<HashMap<String,String>> source;

    public ContactListAdapter(Context context, List<HashMap<String,String>> source)
    {
        this.mContext = context;
        this.source = source;
    }
    @Override
    public int getCount()
    {
        return source.size();
    }

    @Override
    public Object getItem(int position)
    {
        return source.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        HashMap<String,String> map= (HashMap<String, String>) getItem(position);
        convertView = LayoutInflater.from(mContext).inflate(R.layout.popup_contact_litem, null);
        TextView tvContactContent= (TextView) convertView.findViewById(R.id.my_set_contact);
        tvContactContent.setText(map.get("content"));
        if ("hotline".equals(map.get("type")))
        {
            tvContactContent.setTextColor(ContextCompat.getColor(mContext,R.color.MC7));
            tvContactContent.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mOnItemClickListener!=null)
                        mOnItemClickListener.onItemClick(v,position);
                }
            });
        }
        else
        {
            tvContactContent.setTextColor(ContextCompat.getColor(mContext,R.color.FC2));
        }

        return convertView;
    }

    /**
     * ItemClick的回调接口
     *
     */
    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.mOnItemClickListener = listener;
    }
}

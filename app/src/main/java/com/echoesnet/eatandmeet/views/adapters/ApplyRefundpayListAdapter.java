package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;



/**
 * Created by Administrator on 2016/8/24.
 */
public class ApplyRefundpayListAdapter extends BaseAdapter
{
    Context mContext;
    String[] reasons;
    private OnItemClickListener mOnItemClickListener;

    public ApplyRefundpayListAdapter(Context context, String[] reasons)
    {
        this.mContext = context;
        this.reasons = reasons;
    }

    @Override
    public int getCount()
    {
        return reasons.length;
    }

    @Override
    public Object getItem(int position)
    {
        return reasons[position];
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.litem_refund_reason, parent, false);
        }
        final TextView textView = (TextView) convertView.findViewById(R.id.tv_payList_item);
        textView.setText(reasons[position]);
        final View finalConvertView = convertView;
        convertView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnItemClickListener.onItemClick(v,position);
            }
        });

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

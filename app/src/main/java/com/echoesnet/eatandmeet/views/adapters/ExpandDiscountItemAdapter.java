package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.linearlistview.LinearListView;

/**
 * Created by wangben on 2016/6/3.
 */
public class ExpandDiscountItemAdapter extends ExpandAutoListviewAdapter<String>
{

    public ExpandDiscountItemAdapter(Context mContext, LinearListView mListView, View headView)
    {
        super(mContext, mListView, headView);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder=null;
        if (convertView==null)
        {
            viewHolder=new ViewHolder();
            convertView= inflater.inflate(R.layout.litem_res_discount,parent,false);
            viewHolder.discountItem= (TextView) convertView.findViewById(R.id.tv_discount_item);
            viewHolder.discountTime=(TextView) convertView.findViewById(R.id.tv_discount_restrict);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        String item=getItem(position);
        viewHolder.discountItem.setText(item.split(";")[0]);
        viewHolder.discountTime.setText(item.split(";")[1]);

        return convertView;
    }
    public final class ViewHolder
    {
        private TextView discountItem;
        private TextView discountTime;
    }
}

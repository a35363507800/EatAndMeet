package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.FaceBalanceDetailBean;

import java.util.List;

/**
 * Created by Administrator on 2016/5/28.
 */
public class MyFaceBalanceDetailAdapter extends BaseAdapter
{
    private List<FaceBalanceDetailBean> balanceList;
    private Context context;

    public MyFaceBalanceDetailAdapter(Context context, List<FaceBalanceDetailBean> balanceList)
    {
        this.context = context;
        this.balanceList = balanceList;
    }

    @Override
    public int getCount()
    {
        return balanceList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return balanceList.get(position);
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
        FaceBalanceDetailBean bean = balanceList.get(position);
        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.balance_detail_item, null);
            holder.tv_balance_title = (TextView) convertView.findViewById(R.id.tv_balance_title);
            holder.tv_balance = (TextView) convertView.findViewById(R.id.tv_balance);
            holder.tv_balance_content = (TextView) convertView.findViewById(R.id.tv_balance_content);
            holder.tv_balance_result = (TextView) convertView.findViewById(R.id.tv_balance_result);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        String inType = bean.getInType();

        if (inType.equals("0"))
        {
            holder.tv_balance_content.setText(String.format("+%s", bean.getFace()));
            holder.tv_balance_content.setTextColor(ContextCompat.getColor(context, R.color.C0315));
        }
        else
        {
            holder.tv_balance_content.setText(String.format("-%s", bean.getFace()));
            holder.tv_balance_content.setTextColor(ContextCompat.getColor(context, R.color.C0321));
        }

        holder.tv_balance_title.setText(bean.getTypeDetail());
        holder.tv_balance.setText(String.format("余额: %s", bean.getFaceEgg()));
        holder.tv_balance_result.setText(bean.getDealDate());
        return convertView;
    }

    class ViewHolder
    {
        public TextView tv_balance_title;
        public TextView tv_balance;
        public TextView tv_balance_content;
        public TextView tv_balance_result;
    }
}

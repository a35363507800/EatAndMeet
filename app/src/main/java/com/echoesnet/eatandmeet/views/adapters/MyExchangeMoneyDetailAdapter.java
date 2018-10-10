package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.ExchangeRecordDetailBean;

import java.util.List;

/**
 * Created by Administrator on 2016/5/28.
 */
public class MyExchangeMoneyDetailAdapter extends BaseAdapter
{
    private List<ExchangeRecordDetailBean> balanceList;
    private Context context;
    public MyExchangeMoneyDetailAdapter(Context context, List<ExchangeRecordDetailBean> balanceList)
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
        ExchangeRecordDetailBean bean = balanceList.get(position);
        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.exchange_money_detail_item, null);
            holder.tv_balance_title = (TextView) convertView.findViewById(R.id.tv_balance_title);
            holder.tv_balance = (TextView) convertView.findViewById(R.id.tv_balance);
            holder.tv_balance_content = (TextView) convertView.findViewById(R.id.tv_balance_content);
            holder.tv_balance_time = (TextView) convertView.findViewById(R.id.tv_balance_time);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_balance.setText("消耗"+bean.getMeal()+"饭票");
        holder.tv_balance_content.setText("+"+bean.getBalance());
        holder.tv_balance_time.setText(bean.getDate());

        return convertView;
    }

    class ViewHolder
    {
        public TextView tv_balance_title;
        public TextView tv_balance;
        public TextView tv_balance_content;
        public TextView tv_balance_time;
    }
}

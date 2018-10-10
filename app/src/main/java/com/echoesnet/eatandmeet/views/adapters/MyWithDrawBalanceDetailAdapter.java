package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.WithDrawBalanceDetailBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/5/28.
 */
public class MyWithDrawBalanceDetailAdapter extends BaseAdapter
{
    private List<WithDrawBalanceDetailBean> balanceList;
    private Context context;
    public MyWithDrawBalanceDetailAdapter(Context context, List<WithDrawBalanceDetailBean> balanceList)
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
        WithDrawBalanceDetailBean bean = balanceList.get(position);
        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.withdraw_detail_item, null);
            holder.tv_balance_title = (TextView) convertView.findViewById(R.id.tv_balance_title);
            holder.tv_balance = (TextView) convertView.findViewById(R.id.tv_balance);
            holder.tv_balance_content = (TextView) convertView.findViewById(R.id.tv_balance_content);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        String typeDetail = bean.getType();
        String typeContent="";


        switch (typeDetail)
        {
            case "0": // 提现到余额
                typeContent = "提现到余额";
                if(typeDetail.equals("0")) {
                    holder.tv_balance_content.setText("￥" + CommonUtils.keep2Decimal(Double.parseDouble(bean.getMoney())));
                } else {
                    holder.tv_balance_content.setText("￥" + CommonUtils.keep2Decimal(Double.parseDouble(bean.getMoney())));
                }
                holder.tv_balance_title.setText(typeContent);
                holder.tv_balance.setText(bean.getDealDate());
                break;
            case "1": // 提现到支付宝
                typeContent = "提现到支付宝";
                if(typeDetail.equals("0")) {
                    holder.tv_balance_content.setText("￥" + CommonUtils.keep2Decimal(Double.parseDouble(bean.getMoney())));
                } else {
                    holder.tv_balance_content.setText("￥" + CommonUtils.keep2Decimal(Double.parseDouble(bean.getMoney())));
                }
                holder.tv_balance_title.setText(typeContent);
                holder.tv_balance.setText(bean.getDealDate());
//                holder.tv_balance_content.setText("-" + CommonUtils.keep2Decimal(Double.parseDouble(bean.getDealMoney())));
                break;
            case "2": // 提现到微信
                typeContent = "提现到微信";
                if(typeDetail.equals("0")) {
                    holder.tv_balance_content.setText("￥" + CommonUtils.keep2Decimal(Double.parseDouble(bean.getMoney())));
                } else {
                    holder.tv_balance_content.setText("￥" + CommonUtils.keep2Decimal(Double.parseDouble(bean.getMoney())));
                }
                holder.tv_balance_title.setText(typeContent);
                holder.tv_balance.setText(bean.getDealDate());
//                holder.tv_balance_content.setText("-" + CommonUtils.keep2Decimal(Double.parseDouble(bean.getDealMoney())));
                break;
            default:
                if(typeDetail.equals("0")) {
                    holder.tv_balance_content.setText("￥" + CommonUtils.keep2Decimal(Double.parseDouble(bean.getMoney())));
                } else {
                    holder.tv_balance_content.setText("￥" + CommonUtils.keep2Decimal(Double.parseDouble(bean.getMoney())));
                }
                holder.tv_balance_title.setText(typeDetail);
                holder.tv_balance.setText(bean.getDealDate());
//                holder.tv_balance_content.setText(CommonUtils.keep2Decimal(Double.parseDouble(bean.getDealMoney())));
                break;
        }

        return convertView;
    }

    class ViewHolder
    {
        public TextView tv_balance_title;
        public TextView tv_balance;
        public TextView tv_balance_content;
    }
}

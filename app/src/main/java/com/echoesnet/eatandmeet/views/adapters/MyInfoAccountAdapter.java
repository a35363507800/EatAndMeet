package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.NewRechargeBean;

import java.util.List;


public class MyInfoAccountAdapter extends BaseAdapter {
    private static final String TAG = MyInfoAccountAdapter.class.getSimpleName();
    private List<NewRechargeBean> accountList;
    private Context context;

    public MyInfoAccountAdapter(Context context, List<NewRechargeBean> accountList) {
        this.context = context;
        this.accountList = accountList;
    }

    private int checkEgg=-1;
    @Override
    public int getCount() {
        return accountList.size();
    }

    @Override
    public NewRechargeBean getItem(int position) {
        return accountList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_myinfo_accountact, null);
            holder.tvPayAmount = (TextView) convertView.findViewById(R.id.tv_egg_count);
            holder.tvGetAmount= (TextView) convertView.findViewById(R.id.tv_egg_twocount);
            holder.allLayout= (RelativeLayout) convertView.findViewById(R.id.rl_egg);
            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        NewRechargeBean bean = getItem(position);

        holder.tvPayAmount.setText("￥"+bean.getRechargeAmount());
        holder.tvGetAmount.setText("送￥"+bean.getGetAmount());


        if(position==checkEgg)
        {
            holder.tvPayAmount.setTextColor(ContextCompat.getColor(context,R.color.C0313));
            holder.tvGetAmount.setTextColor(ContextCompat.getColor(context,R.color.C0313));
            holder.allLayout.setBackgroundResource(R.drawable.egg_count_bg0313);
            holder.tvPayAmount.getPaint().setFakeBoldText(true);
        }else
        {
            holder.tvPayAmount.setTextColor(ContextCompat.getColor(context,R.color.C0322));
            holder.tvGetAmount.setTextColor(ContextCompat.getColor(context,R.color.C0323));
            holder.allLayout.setBackgroundResource(R.drawable.egg_count_bg);
            holder.tvPayAmount.getPaint().setFakeBoldText(false);
        }


        return convertView;

    }

    class ViewHolder {
        public TextView tvPayAmount;
        public TextView tvGetAmount; // 赠送金额
        public RelativeLayout allLayout; // 赠送金额
    }

    public void checkIndex(int position)
    {
        checkEgg=position;
        notifyDataSetChanged();
    }
    public int getIndex()
    {
        return checkEgg;
    }
}

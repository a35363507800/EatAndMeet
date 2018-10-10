package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eam.icontextmodule.model.EchoesEamIcon;
import com.echoesnet.eatandmeet.R;
import com.joanzapata.iconify.IconDrawable;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/20.
 */
public class OrderRechargeAdapter extends BaseAdapter {
    public static final String TAG = OrderRechargeAdapter.class.getSimpleName();
    private Context context;
    private List<Map<String, Object>> payWays;
    private OnItemClickListener mOnItemClickListener;

    public OrderRechargeAdapter(Context context, List<Map<String, Object>> payWays) {
        this.context = context;
        this.payWays = payWays;
    }

    private int clickTemp = 0;

    //标识选择的Item
    public void setSelection(int position) {
        clickTemp = position;
    }

    @Override
    public int getCount() {
        return payWays.size();
    }

    @Override
    public Map<String, Object> getItem(int position) {
        return payWays.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.popup_recharge_mode_item, null);
            holder.iv_balance_icon = (ImageView) convertView.findViewById(R.id.iv_balance_icon);
            holder.tv_balance = (TextView) convertView.findViewById(R.id.tv_balance);
            holder.tv_recharge = (TextView) convertView.findViewById(R.id.tv_recharge);
            holder.tv_balance_price = (TextView) convertView.findViewById(R.id.tv_balance_price);
            holder.iv_select_icon = (ImageView) convertView.findViewById(R.id.iv_select_icon);
            holder.arl_right = (AutoRelativeLayout) convertView.findViewById(R.id.arl_right);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Map<String, Object> map = payWays.get(position);
        holder.tv_recharge.setVisibility(View.GONE);

        holder.iv_balance_icon.setImageDrawable((IconDrawable) map.get("icon"));
        holder.tv_balance.setText((String) map.get("payWay"));

        if (clickTemp == position) {
            holder.iv_select_icon.setImageDrawable(new IconDrawable(context, EchoesEamIcon.eam_p_radio_btn).colorRes(R.color.MC1));
        } else {
            holder.iv_select_icon.setImageDrawable(new IconDrawable(context, EchoesEamIcon.eam_n_radio_btn).colorRes(R.color.c15));
        }

        convertView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnItemClickListener!=null)
                       mOnItemClickListener.onItemClick(v,position);
            }
        });
        return convertView;
    }

    public class ViewHolder {
        public ImageView iv_balance_icon;
        public TextView tv_balance;
        public TextView tv_recharge;
        public TextView tv_balance_price;
        public ImageView iv_select_icon;
        public AutoRelativeLayout arl_right;
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

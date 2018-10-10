package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.echoesnet.eam.icontextmodule.model.EchoesEamIcon;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.MyInfoAccountAct2;
import com.joanzapata.iconify.IconDrawable;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/20.
 */
public class OrderPayAdapter extends BaseAdapter
{
    public static final String TAG = OrderPayAdapter.class.getSimpleName();
    private Context context;
    private PopupWindow popupWindow;
    private List<Map<String, Object>> payWays;
    private OnItemClickListener mOnItemClickListener;
    ViewHolder holder = null;

    public OrderPayAdapter(Context context, List<Map<String, Object>> payWays, PopupWindow popupWindow)
    {
        this.context = context;
        this.popupWindow = popupWindow;
        this.payWays = payWays;
    }

    private int clickTemp = 0;

    //标识选择的Item
    public void setSelection(int position)
    {
        clickTemp = position;
    }

    @Override
    public int getCount()
    {
        return payWays.size();
    }

    @Override
    public Map<String, Object> getItem(int position)
    {
        return payWays.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {

        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.popup_pay_mode_item, null);
            holder.iv_balance_icon = (ImageView) convertView.findViewById(R.id.iv_balance_icon);
            holder.tv_balance = (TextView) convertView.findViewById(R.id.tv_balance);
            holder.tv_recharge = (TextView) convertView.findViewById(R.id.tv_recharge);
            holder.tv_balance_price = (TextView) convertView.findViewById(R.id.tv_balance_price);
            holder.iv_select_icon = (ImageView) convertView.findViewById(R.id.iv_select_icon);
            holder.arl_right = (AutoRelativeLayout) convertView.findViewById(R.id.arl_right);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Map<String, Object> map = payWays.get(position);

        if ("余额".equals(payWays.get(position).get("payWay")))
        {
            holder.tv_recharge.setFocusable(false);
            holder.tv_recharge.setFocusableInTouchMode(false);
            holder.tv_recharge.setVisibility(View.VISIBLE);
            //去存值
            holder.tv_recharge.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                  //  MyInfoAccountAct_.intent(context).start();
                    Intent intent = new Intent(context,MyInfoAccountAct2.class);
                    context.startActivity(intent);
                    // 不让支付方式的pop消失, 从别的界面返回依然显示 --- zdw --- 8-19 9:50 ---
//                    if (popupWindow!=null)
//                        popupWindow.dismiss();
                }
            });
            holder.tv_balance_price.setVisibility(View.VISIBLE);
            holder.tv_balance_price.setText(String.format("(￥ %s)", map.get("balance")));
        }
        else
        {
            holder.tv_recharge.setVisibility(View.GONE);
        }

        holder.iv_balance_icon.setImageDrawable((IconDrawable) map.get("icon"));
        holder.tv_balance.setText((String) map.get("payWay"));

        if (clickTemp == position)
        {
            holder.iv_select_icon.setImageDrawable(new IconDrawable(context, EchoesEamIcon.eam_s_grade2).colorRes(R.color.C0412));
        }
        else
        {
            holder.iv_select_icon.setImageDrawable(new IconDrawable(context, EchoesEamIcon.eam_s_grade1).colorRes(R.color.C0323));
        }
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

    public class ViewHolder
    {
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

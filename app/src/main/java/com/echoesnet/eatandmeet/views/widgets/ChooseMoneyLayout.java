package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.LiveRedPacketBean;

import java.util.List;

/**
 * Created by Administrator on 2017/4/17.
 */

public class ChooseMoneyLayout extends GridView {
    private static final String TAG = ChooseMoneyLayout.class.getSimpleName();

    private List<LiveRedPacketBean> list;
    private LayoutInflater mInflater;
    private MyAdapter adapter;
    private int defaultChoose = 0;     //默认选中项

    public ChooseMoneyLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setData();
    }

    public void setData()
    {
        mInflater = LayoutInflater.from(getContext());
        adapter = new MyAdapter();
        setAdapter(adapter);
    }

    /**
     * 设置默认选择项目，
     * @param defaultChoose
     */
    public void setDefaultPosition(int defaultChoose)
    {
        this.defaultChoose = defaultChoose;
        adapter.notifyDataSetChanged();
    }

    /**
     * 设置数据源
     * @param list
     */

    public void setRedMoney(List<LiveRedPacketBean> list)
    {
        this.list = list;
    }

    class MyAdapter extends BaseAdapter
    {
        private RadioButton checkBox;

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final MyViewHolder holder;
            if (convertView == null)
            {
                holder = new MyViewHolder();
                convertView = mInflater.inflate(R.layout.item_money_pay, parent, false);
                holder.cbMoneyPay = (RadioButton) convertView.findViewById(R.id.money_pay_cb);
                holder.tvMoney = (TextView) convertView.findViewById(R.id.tv_money);
                convertView.setTag(holder);
            }
            else
            {
                holder = (MyViewHolder) convertView.getTag();
            }

            holder.tvMoney.setText("￥" + list.get(position).getAmount());
            holder.cbMoneyPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                    {
                        holder.cbMoneyPay.setBackground(getResources().getDrawable(R.drawable.money_round_stroke_yes));
                        //取消上一个选择
                        if (checkBox != null)
                        {
                            checkBox.setChecked(false);
                        }
                        checkBox = (RadioButton) buttonView;
                    }
                    else
                    {
                        checkBox = null;
                        holder.cbMoneyPay.setBackground(getResources().getDrawable(R.drawable.money_round_stroke_no));
                    }
                    //回调
                    listener.chooseMoney(position, isChecked, Double.parseDouble(list.get(position).getAmount()));
                }
            });


            if (position == defaultChoose) {
                defaultChoose = -1;
                holder.cbMoneyPay.setChecked(true);
                checkBox = holder.cbMoneyPay;
            }

            return convertView;
        }


        private class MyViewHolder
        {
            private RadioButton cbMoneyPay;
            private TextView tvMoney;
        }
    }


    /**
     * 解决嵌套显示不完
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    private onChoseMoneyListener listener;

    public void setOnChoseMoneyListener(onChoseMoneyListener listener) {
        this.listener = listener;
    }

    public interface onChoseMoneyListener {
        /**
         * 选择金额返回
         *
         * @param position gridView的位置
         * @param isCheck  是否选中
         * @param moneyNum 钱数
         */
        void chooseMoney(int position, boolean isCheck, double moneyNum);
    }
}

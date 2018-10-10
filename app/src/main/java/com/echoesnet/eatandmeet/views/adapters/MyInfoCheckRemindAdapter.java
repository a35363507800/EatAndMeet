package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.MyInfoCheckBean;

import java.util.List;

/**
 * Created by Administrator on 2016/7/18.
 */
public class MyInfoCheckRemindAdapter extends BaseAdapter {
    private List<MyInfoCheckBean> list;
    private Context context;

    public MyInfoCheckRemindAdapter(Context context, List<MyInfoCheckBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.litem_check_remind, null);
            viewHolder.tv_time_remind = (TextView) convertView.findViewById(R.id.tv_time_remind);
            viewHolder.tv_check_state_remind = (TextView) convertView.findViewById(R.id.tv_check_state_remind);
            viewHolder.iv_check_show = (ImageView) convertView.findViewById(R.id.iv_check_show);
            viewHolder.tv_check_content = (TextView) convertView.findViewById(R.id.tv_check_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_time_remind.setText(list.get(position).getCheckTime());
        viewHolder.tv_check_content.setText(list.get(position).getUnCheck());

        if(list.get(position).getState().equals("0")) {
            viewHolder.tv_check_state_remind.setText("您的实名认证审核已通过");
            viewHolder.iv_check_show.setVisibility(View.VISIBLE);
            viewHolder.tv_check_content.setVisibility(View.GONE);
        } else {
            viewHolder.tv_check_state_remind.setText("您的实名申请未通过");
            viewHolder.iv_check_show.setVisibility(View.GONE);
            viewHolder.tv_check_content.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    class ViewHolder {
        public TextView tv_time_remind;  // 时间
        public TextView tv_check_state_remind;  // 是否通过
        public ImageView iv_check_show;
        public TextView tv_check_content;  // 未通过原因
    }
}

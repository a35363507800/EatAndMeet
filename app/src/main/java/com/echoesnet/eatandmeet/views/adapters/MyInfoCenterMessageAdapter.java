package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/7/18.
 */
public class MyInfoCenterMessageAdapter extends BaseAdapter {
    private static final String TAG=MyInfoCenterMessageAdapter.class.getSimpleName();
    private List<HashMap<String, Object>> list;
    private Context context;

    public MyInfoCenterMessageAdapter(Context context, List<HashMap<String, Object>> list) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_center_message, null);
            viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
            viewHolder.tv_hint = (TextView) convertView.findViewById(R.id.tv_hint);
            viewHolder.itv_type_icon = (IconTextView) convertView.findViewById(R.id.itv_type_icon);
            viewHolder.riv_head = (RoundedImageView) convertView.findViewById(R.id.riv_head);
            viewHolder.ri_msg_tip = (RoundedImageView) convertView.findViewById(R.id.ri_msg_tip);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        HashMap<String, Object> map = (HashMap<String, Object>) getItem(position);
        Logger.t(TAG).d(map.toString());
        if(((boolean)map.get("isHaveMsg"))==true)
        {
            viewHolder.ri_msg_tip.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.ri_msg_tip.setVisibility(View.GONE);
        }
        viewHolder.itv_type_icon.setText(map.get("img").toString());
        viewHolder.tv_type.setText(map.get("name").toString());
        viewHolder.riv_head.setImageDrawable((ColorDrawable)map.get("color"));

        return convertView;
    }

    class ViewHolder {
        public TextView tv_type;
        public TextView tv_hint;
        public IconTextView itv_type_icon;
        private RoundedImageView riv_head;
        private RoundedImageView ri_msg_tip;
    }
}

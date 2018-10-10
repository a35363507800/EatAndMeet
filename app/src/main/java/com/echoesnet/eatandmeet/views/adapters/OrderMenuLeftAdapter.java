package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.DishRightMenuGroupBean;
import com.echoesnet.eatandmeet.models.bean.OrderMenuLeftBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/6.
 */
public class OrderMenuLeftAdapter extends BaseAdapter {

    private Context context;
    private List<DishRightMenuGroupBean> list;

    public OrderMenuLeftAdapter(Context context, List<DishRightMenuGroupBean> list) {
        this.context = context;
        this.list = list;
    }
    public void setList(ArrayList<DishRightMenuGroupBean> list) {
        this.list = list;
        notifyDataSetChanged();
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
        ViewHolder viewHolder = null;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_menu_left_item, null);
            viewHolder.tv_item_title = (TextView) convertView.findViewById(R.id.tv_item_title);
            viewHolder.iv_item_bg = (ImageView) convertView.findViewById(R.id.iv_item_bg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.updateItemView(position);
        return convertView;
    }

    class ViewHolder {
        private TextView tv_item_title;
        private ImageView iv_item_bg;

        public void updateItemView(int index) {
            tv_item_title.setText(list.get(index).getHeaderTitle().toString().trim());
            if(list.get(index).isSelect()) {
                tv_item_title.setTextColor(ContextCompat.getColor(context, R.color.MC3));
                iv_item_bg.setVisibility(View.VISIBLE);
            } else {
                tv_item_title.setTextColor(ContextCompat.getColor(context, R.color.FC2));
                iv_item_bg.setVisibility(View.GONE);
            }
        }

    }

}

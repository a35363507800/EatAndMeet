package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.DishBean;
import com.echoesnet.eatandmeet.models.bean.OrderedDishItemBean;
import com.zhy.autolayout.AutoRelativeLayout;

/**
 * Created by Administrator on 2016/5/18.
 */
public class ExpandOrderDishAdapter extends ExpandAdapter<DishBean>
{

    private ListItemView listItemView = null;
    public final class ListItemView
    {
        public AutoRelativeLayout gas_station_groupon_ll;
        private AutoRelativeLayout downLayout;
        public TextView tv_name;
        public TextView tv_number;
        public TextView tv_price;
    }

    public ExpandOrderDishAdapter(Context mContext, ListView mListView, TextView view, AutoRelativeLayout downLayout)
    {
        super(mContext, mListView, view, downLayout);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
//        final OrderConfirmBean object = (OrderConfirmBean)getItem(position);
//        final OrderedDishItemBean object = (OrderedDishItemBean)getItem(position);
        final DishBean object = (DishBean)getItem(position);

        if (convertView == null)
        {
            listItemView = new ListItemView();
            convertView = inflater.inflate(R.layout.expan_listview_item, parent, false);

            creatView(convertView, listItemView);
            convertView.setTag(listItemView);
        } else
        {
            listItemView = (ListItemView) convertView.getTag();
        }
//        listItemView.tvName.setText(object.getName());
//        listItemView.tv_number.setText(object.getNumber());
//        listItemView.tv_price.setText(object.getPrice());

        listItemView.tv_name.setText(object.getDishName());
        listItemView.tv_number.setText("x" + object.getDishAmount() + "");
        listItemView.tv_price.setText("￥" + object.getDishPrice());

        return convertView;
    }

    private void creatView(View rowView, ListItemView listItemView)
    {
        listItemView.gas_station_groupon_ll = (AutoRelativeLayout) rowView.findViewById(R.id.gas_station_groupon_ll);
        listItemView.tv_name = (TextView) rowView.findViewById(R.id.tv_name);
        listItemView.tv_number = (TextView) rowView.findViewById(R.id.tv_number);
        listItemView.tv_price = (TextView) rowView.findViewById(R.id.tv_price);
    }
}

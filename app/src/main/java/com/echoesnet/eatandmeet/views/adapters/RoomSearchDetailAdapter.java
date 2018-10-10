package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eam.icontextmodule.model.EchoesEamIcon;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.SearchRestaurantBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomRatingBar.CustomRatingBar;
import com.joanzapata.iconify.IconDrawable;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * Created by Administrator on 2016/5/6.
 */
public class RoomSearchDetailAdapter extends BaseAdapter
{
    private Context context;
    private List<SearchRestaurantBean> list;

    public RoomSearchDetailAdapter(Context context, List<SearchRestaurantBean> list)
    {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount()
    {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.room_search_detail_item, parent, false);
            viewHolder.tv_room_name = (TextView) convertView.findViewById(R.id.tv_room_name);
            viewHolder.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
            viewHolder.iv_map_distance_icon = (ImageView) convertView.findViewById(R.id.iv_map_distance_icon);
            viewHolder.iv_menu_icon = (RoundedImageView) convertView.findViewById(R.id.iv_menu_icon);
            viewHolder.ratingBar = (CustomRatingBar) convertView.findViewById(R.id.rating_bar);
            viewHolder.tv_mini_price = (TextView) convertView.findViewById(R.id.tv_mini_price);
            viewHolder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        SearchRestaurantBean srBean = list.get(position);
        viewHolder.tv_room_name.setText(srBean.getrName());
         viewHolder.iv_map_distance_icon.setImageDrawable(new IconDrawable(context, EchoesEamIcon.eam_s_location2).colorRes(R.color.c4));
      //    viewHolder.iv_map_distance_icon.setText("{eam-s-location2}");
        viewHolder.iv_map_distance_icon.setVisibility(View.GONE);
        viewHolder.tv_mini_price.setText("起订￥" + srBean.getLessPrice());
        viewHolder.tv_price.setText("人均￥" + srBean.getPerPrice());
        double distance = 0;
        try
        {
            distance = Double.parseDouble(srBean.getDistance());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        if (distance > 1000)
        {
            double km = (distance) / 1000;
            viewHolder.tv_distance.setText(String.format("%s km", CommonUtils.keep2Decimal(km)));
        }
        else if (distance < 100)
        {
            viewHolder.tv_distance.setText("<100m");
        }else
        {
            viewHolder.tv_distance.setText(String.format("%s m", CommonUtils.keep2Decimal(distance)));
        }
        String url = srBean.getRpUrls();

        if (url.contains(CommonUtils.SEPARATOR))
        {
            String[] rpUrls = url.split(CommonUtils.SEPARATOR);
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(rpUrls[0])
                    .placeholder(R.drawable.qs_cai_canting)
                    .error(R.drawable.canting)
                    .centerCrop()
                    .into(viewHolder.iv_menu_icon);
        }
        else
        {
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(url)
                    .placeholder(R.drawable.qs_cai_canting)
                    .error(R.drawable.canting)
                    .centerCrop()
                    .into(viewHolder.iv_menu_icon);
        }
        viewHolder.iv_menu_icon.setCornerRadius(8, 8, 8, 8);
        viewHolder.ratingBar.setIndicator(true);
        viewHolder.ratingBar.setRatingBar(Integer.parseInt(list.get(position).getrStar()));
        return convertView;
    }

    class ViewHolder
    {
        private TextView tv_room_name;
        private TextView tv_distance;
        private ImageView iv_map_distance_icon;
        private RoundedImageView iv_menu_icon;
        private TextView tv_mini_price;
        private TextView tv_price;
        private CustomRatingBar ratingBar;
    }

}

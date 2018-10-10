package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.CollectBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/7/25.
 */
public class MyInfoCollectAdapter extends BaseAdapter
{
    private static final String TAG = MyInfoCollectAdapter.class.getSimpleName();
    // 填充数据的list
    private List<CollectBean> foodlist;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;

    private Boolean isShow = false;

    // 构造器
    public MyInfoCollectAdapter(List<CollectBean> list, Context context, Boolean isShow)
    {
        this.context = context;
        this.foodlist = list;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<Integer, Boolean>();
        this.isShow = isShow;
        // 初始化数据
        initDate();
    }

    // 初始化isSelected的数据
    public void initDate()
    {
        if (foodlist.size() != 0)
        {
            for (int i = 0; i < foodlist.size(); i++)
            {
                getIsSelected().put(i, false);
            }
        }

    }

    @Override
    public int getCount()
    {
        return foodlist.size();
    }

    @Override
    public Object getItem(int position)
    {
        return foodlist.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        if (convertView == null)
        {
            // 获得ViewHolder对象
            holder = new ViewHolder();
            // 导入布局并赋值给convertview
            convertView = inflater.inflate(R.layout.item_myinfo_collection, null);
            holder.iv_food_imager = (RoundedImageView) convertView.findViewById(R.id.iv_food_imager);
            holder.tv_food_name = (TextView) convertView.findViewById(R.id.tv_food_name);
            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            holder.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
            holder.cb = (CheckBox) convertView.findViewById(R.id.check_box);
            // 为view设置标签
            convertView.setTag(holder);
        }
        else
        {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
        // 获取数据
        CollectBean food = foodlist.get(position);
        if (isShow)
        {
            holder.cb.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.cb.setVisibility(View.GONE);
        }
        holder.tv_food_name.setSelected(true);
        holder.tv_food_name.setText(food.getrName());
        holder.tv_price.setText("人均: ￥" + food.getPerPrice());
        // 设置list中TextView的显示
        // 根据isSelected来设置checkbox的选中状况
        holder.cb.setChecked(getIsSelected().get(position));
        // 距离
        holder.tv_distance.setText(food.getDistance()+"km");
//        double distance = Double.parseDouble(food.getDistance());
//        if (distance > 1000)
//        {
//            double km = (distance) / 1000;
//            holder.tv_distance.setText(String.format("%s km", CommonUtils.keep2Decimal(km)));
//        }
//        else if (distance < 100)
//        {
//            holder.tv_distance.setText("<100m");
//        }
//        else
//        {
//            holder.tv_distance.setText(String.format("%s m", CommonUtils.keep2Decimal(distance)));
//        }

        String url = food.getRpUrls();
        if (!TextUtils.isEmpty(url))
        {
            if (url.contains(CommonUtils.SEPARATOR))
            {
                String[] rpUrls = url.split(CommonUtils.SEPARATOR);
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(rpUrls[0])
                        .placeholder(R.drawable.cai_da)
                        .error(R.drawable.cai_da)
                        .centerCrop().into(holder.iv_food_imager);
            }
            else
            {
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(url)
                        .placeholder(R.drawable.cai_da)
                        .error(R.drawable.cai_da)
                        .centerCrop().into(holder.iv_food_imager);
            }
        }
        else
        {
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(url)
                    .placeholder(R.drawable.cai_da)
                    .error(R.drawable.cai_da)
                    .centerCrop().into(holder.iv_food_imager);
        }


        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected()
    {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected)
    {
        MyInfoCollectAdapter.isSelected = isSelected;
    }

    public class ViewHolder
    {
        public TextView tv_food_name;
        public TextView tv_distance;
        public TextView tv_price;
        public RoundedImageView iv_food_imager;
        public CheckBox cb;
    }
}

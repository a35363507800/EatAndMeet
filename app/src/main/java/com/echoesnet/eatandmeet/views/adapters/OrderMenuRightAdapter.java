package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.FoodDetailAct;
import com.echoesnet.eatandmeet.fragments.DishFrg;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomRatingBar.CustomRatingBar;
import com.echoesnet.eatandmeet.views.widgets.orderDishesView.SectionedBaseAdapter;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import static com.echoesnet.eatandmeet.fragments.DishFrg.list;

/**
 * Created by Administrator on 2016/5/6.
 */

public class OrderMenuRightAdapter extends SectionedBaseAdapter
{
    public static final String TAG = OrderMenuRightAdapter.class.getSimpleName();
    private Context context;
    //private static ArrayList<DishRightMenuGroupBean> DishFrg.list= DishFrg.list;
    private TextView tv_total, tv_price;
    private AutoFrameLayout fl_carimg;
    private Button btn_checkout;
    private String lessPrice;
    private String resName;

    public OrderMenuRightAdapter(Context context, TextView tv_total, TextView tv_price,
                                 AutoFrameLayout fl_carimg, Button btn_checkout, String lessPrice, String resName)
    {
        this.context = context;
        //this.DishFrg.list = list;
        this.tv_total = tv_total;
        this.tv_price = tv_price;
        this.fl_carimg = fl_carimg;
        this.btn_checkout = btn_checkout;
        this.lessPrice = lessPrice;
        this.resName = resName;
    }

    @Override
    public Object getItem(int section, int position)
    {
        Logger.t(TAG).d(list.get(section).getList().get(position).toString());
        return DishFrg.list.get(section).getList().get(position);
    }

    @Override
    public long getItemId(int section, int position)
    {
        return position;
    }

    @Override
    public int getSectionCount()
    {//header的数量
        return DishFrg.list.size();
    }

    @Override
    public int getCountForSection(int section)
    {
        //子item的数量
        return DishFrg.list.get(section).getList().size();
    }

    @Override
    public View getItemView(final int section,final int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;
    

        if (convertView == null)
        {

            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = (AutoLinearLayout) inflator.inflate(R.layout.lv_menu_right_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_hint = (TextView) convertView.findViewById(R.id.tv_hint);
            viewHolder.iv_add = (ImageView) convertView.findViewById(R.id.iv_add);
            viewHolder.iv_substract = (ImageView) convertView.findViewById(R.id.iv_substract);
            viewHolder.riv_editu_head = (RoundedImageView) convertView.findViewById(R.id.riv_editu_head);
            viewHolder.ratingBar = (CustomRatingBar) convertView.findViewById(R.id.rating_bar);
            viewHolder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            viewHolder.riv_editu_head.setCornerRadius(8, 8, 0, 0);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.arl_item_layout = (AutoRelativeLayout) convertView.findViewById(R.id.arl_item_layout);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.tv_title.setText(DishFrg.list.get(section).getList().get(position).getDishName().toString());
        String starNum = DishFrg.list.get(section).getList().get(position).getDishStar();
        viewHolder.tv_price.setText("￥" + DishFrg.list.get(section).getList().get(position).getDishPrice());
        viewHolder.tv_hint.setText(DishFrg.list.get(section).getList().get(position).getDishNum() + "");

        viewHolder.ratingBar.setIndicator(true);
        viewHolder.ratingBar.setRatingBar(Integer.parseInt(starNum));

        if (CommonUtils.getDishCount(DishFrg.list) != 0)
        {
            Logger.t(TAG).d("最低起订价》"+lessPrice);
            if (CommonUtils.getDishPrice(DishFrg.list) < Double.parseDouble(lessPrice))
            {
                fl_carimg.setVisibility(View.VISIBLE);
                tv_total.setVisibility(View.VISIBLE);
                tv_total.setText(CommonUtils.getDishCount(DishFrg.list) + "");
                tv_price.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(DishFrg.list)));
                btn_checkout.setText("还差" + CommonUtils.keep2Decimal((Double.parseDouble(lessPrice) - CommonUtils.getDishPrice(DishFrg.list))) + "元");
                btn_checkout.setBackgroundResource(R.color.FC7);
            }
            else
            {
                fl_carimg.setVisibility(View.VISIBLE);
                tv_total.setVisibility(View.VISIBLE);
                tv_total.setText(CommonUtils.getDishCount(DishFrg.list) + "");
                tv_price.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(DishFrg.list)));
                btn_checkout.setText("下一步");
                btn_checkout.setBackgroundResource(R.color.MC1);
            }
        }
        else
        {
            if (fl_carimg != null)
            {
                fl_carimg.setVisibility(View.VISIBLE);
                tv_total.setVisibility(View.GONE);
                tv_price.setText("￥0.00");
//                tv_price.setText("您还没有点菜哦");
                btn_checkout.setText("满" + lessPrice + "元起订");
                btn_checkout.setBackgroundResource(R.color.FC7);
            }
        }

        if (DishFrg.list.get(section).getList().get(position).getDishNum() == 0)
        {
            viewHolder.tv_hint.setVisibility(View.INVISIBLE);
            viewHolder.iv_substract.setVisibility(View.INVISIBLE);
        }
        else
        {
            viewHolder.tv_hint.setVisibility(View.VISIBLE);
            viewHolder.iv_substract.setVisibility(View.VISIBLE);
        }

        //增加菜品
        viewHolder.iv_add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DishFrg.list.get(section).getList().get(position).setDishNum((DishFrg.list.get(section).getList().get(position).getDishNum()) + 1);
                notifyDataSetChanged();
            }
        });
        //减少菜品
        viewHolder.iv_substract.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DishFrg.list.get(section).getList().get(position).setDishNum((DishFrg.list.get(section).getList().get(position).getDishNum()) - 1);
                notifyDataSetChanged();
            }
        });

        viewHolder.riv_editu_head.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, FoodDetailAct.class);
                Bundle bundle = new Bundle();
                intent.putExtra("index", "detail");
                intent.putExtra("dishId", DishFrg.list.get(section).getList().get(position).getDishId());
                intent.putExtra("rId", DishFrg.list.get(section).getList().get(position).getrId());
                intent.putExtra("lessPrice", lessPrice);
                intent.putExtra("resName", resName);
                Logger.t("Rightadapter").d("适配器传值--> " + DishFrg.list.get(section).getList().get(position).getrId() + " , "
                        + DishFrg.list.get(section).getList().get(position).getDishPrice() + " , " + resName+"dishid>>"
                        +DishFrg.list.get(section).getList().get(position).getDishId());
                intent.putExtras(bundle);
                context.startActivity(intent);

            }
        });

        GlideApp.with(context)
                .asBitmap()
                .load(DishFrg.list.get(section).getList().get(position).getDishHUrl())
                .placeholder(R.drawable.qs_cai_canting)
                .error(R.drawable.cai_da)
                .skipMemoryCache(false)
                .centerCrop()
                .into(viewHolder.riv_editu_head);
        // xml中设置 makeramen:riv_corner_radius_top_left="10dp"
        //          makeramen:riv_corner_radius_top_right="10dp"  不起作用 原因没时间详查 代码中设置左上右上圆角
        viewHolder.riv_editu_head.setCornerRadius((float)30, (float)30, 0, 0);

        return convertView;
    }

    class ViewHolder
    {
        private TextView tv_hint;
        private ImageView iv_add;
        private ImageView iv_substract;
        private RoundedImageView riv_editu_head;
        private TextView tv_price;
        private TextView tv_title;
        private CustomRatingBar ratingBar;
        private AutoRelativeLayout arl_item_layout;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent)
    {
        AutoLinearLayout layout = null;
        if (convertView == null)
        {
            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (AutoLinearLayout) inflator.inflate(R.layout.item_order_dishes_header, null);
        }
        else
        {
            layout = (AutoLinearLayout) convertView;
        }
        layout.setClickable(false);
        String headerTitle="";
        if (DishFrg.list.size()>section)
             headerTitle=DishFrg.list.get(section).getHeaderTitle();
        ((TextView) layout.findViewById(R.id.textItem)).setText(headerTitle);
        return layout;
    }

}

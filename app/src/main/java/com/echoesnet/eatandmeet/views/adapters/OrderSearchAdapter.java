package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eam.icontextmodule.model.EchoesEamIcon;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.FoodDetailAct;
import com.echoesnet.eatandmeet.fragments.DishFrg;
import com.echoesnet.eatandmeet.models.bean.OrderedDishItemBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.joanzapata.iconify.IconDrawable;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.List;

/**
 * Created by Administrator on 2016/5/6.
 */
public class OrderSearchAdapter extends BaseAdapter
{
    private static final String TAG = OrderSearchAdapter.class.getSimpleName();
    private Context context;
    private List<OrderedDishItemBean> list;
    private AutoFrameLayout flCarImg;
    private TextView tv_total;
    private TextView tv_price;
    private Button btn_checkout;
    private String lessPrice;
    private String rid, resName;

    public OrderSearchAdapter(Context context, List<OrderedDishItemBean> list, AutoFrameLayout flCarImg, TextView tv_total, TextView tv_price, Button btn_checkout
            , String lessPrice, String rid, String resName)
    {
        this.context = context;
        this.list = list;
        this.flCarImg = flCarImg;
        this.tv_total = tv_total;
        this.tv_price = tv_price;
        this.btn_checkout = btn_checkout;
        this.lessPrice = lessPrice;
        this.rid = rid;
        this.resName = resName;
    }

    @Override
    public int getCount()
    {
        return list.size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.order_search_item, parent, false);
            viewHolder.tvHint = (TextView) convertView.findViewById(R.id.tv_hint);
            viewHolder.ivAdd = (ImageView) convertView.findViewById(R.id.iv_add);
            viewHolder.ivSubtract = (ImageView) convertView.findViewById(R.id.iv_substract);
            viewHolder.ivMenuIcon = (RoundedImageView) convertView.findViewById(R.id.iv_menu_icon);
            viewHolder.allStars = (AutoLinearLayout) convertView.findViewById(R.id.all_stars);
            viewHolder.tvDishName = (TextView) convertView.findViewById(R.id.tv_dishname);
            viewHolder.tvDishPrice = (TextView) convertView.findViewById(R.id.tv_dishprice);
            viewHolder.ivMenuHint = (RoundedImageView) convertView.findViewById(R.id.iv_menu_hint);
            viewHolder.arlTop = (AutoRelativeLayout) convertView.findViewById(R.id.arl_top);

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String starNum = list.get(position).getDishStar();

        for (int i = 0; i < viewHolder.allStars.getChildCount(); i++)
        {
            if (i < Integer.parseInt(starNum))
            {
                ((ImageView) viewHolder.allStars.getChildAt(i)).setImageDrawable(new IconDrawable(context, EchoesEamIcon.eam_s_star).colorRes(R.color.MC1));
            } else
            {
                ((ImageView) viewHolder.allStars.getChildAt(i)).setImageDrawable(new IconDrawable(context, EchoesEamIcon.eam_s_star).colorRes(R.color.FC3));
            }
        }

        viewHolder.tvHint.setText(list.get(position).getDishNum() + "");
        viewHolder.ivMenuHint.setVisibility(View.INVISIBLE);

        if (CommonUtils.getDishCount(DishFrg.list) != 0)
        {
            if (CommonUtils.getDishPrice(DishFrg.list) < Double.parseDouble(lessPrice))
            {
                flCarImg.setVisibility(View.VISIBLE);
                tv_total.setText(CommonUtils.getDishCount(DishFrg.list) + "");
                tv_price.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(DishFrg.list)));
                btn_checkout.setText("还差" + CommonUtils.keep2Decimal(Double.parseDouble(lessPrice) - CommonUtils.getDishPrice(DishFrg.list)) + "元");
                btn_checkout.setBackgroundResource(R.color.FC3);
            } else
            {
                flCarImg.setVisibility(View.VISIBLE);
                tv_total.setText(CommonUtils.getDishCount(DishFrg.list) + "");
                tv_price.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(DishFrg.list)));
                btn_checkout.setText("下一步");
                btn_checkout.setBackgroundResource(R.color.MC1);
            }
        } else
        {
            flCarImg.setVisibility(View.GONE);
            tv_price.setText("您还没有点菜哦");
            btn_checkout.setText("满" + lessPrice + "元起订");
            btn_checkout.setBackgroundResource(R.color.FC3);
        }

        if (list.get(position).getDishNum() == 0)
        {
            viewHolder.tvHint.setVisibility(View.INVISIBLE);
            viewHolder.ivSubtract.setVisibility(View.INVISIBLE);
        } else
        {
            viewHolder.tvHint.setVisibility(View.VISIBLE);
            viewHolder.ivSubtract.setVisibility(View.VISIBLE);
        }

        viewHolder.ivAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                list.get(position).setDishNum(list.get(position).getDishNum() + 1);
                notifyDataSetChanged();

                for (int i = 0; i < DishFrg.list.size(); i++)
                {
                    for (int j = 0; j < DishFrg.list.get(i).getList().size(); j++)
                    {
                        if (DishFrg.list.get(i).getList().get(j).getDishId().equals(list.get(position).getDishId()))
                        {
                            DishFrg.list.get(i).getList().get(j).setDishNum(list.get(position).getDishNum());
                        }
                    }
                }

                DishFrg.dishList.clear();
                for (int i = 0; i < DishFrg.list.size(); i++)
                {
                    for (int j = 0; j < DishFrg.list.get(i).getList().size(); j++)
                    {
                        if (DishFrg.list.get(i).getList().get(j).getDishNum() != 0)
                        {
                            DishFrg.dishList.add(DishFrg.list.get(i).getList().get(j));
                        }
                    }
                }
            }
        });
        viewHolder.ivSubtract.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                list.get(position).setDishNum(list.get(position).getDishNum() - 1);
                notifyDataSetChanged();

                for (int i = 0; i < DishFrg.list.size(); i++)
                {
                    for (int j = 0; j < DishFrg.list.get(i).getList().size(); j++)
                    {
                        if (DishFrg.list.get(i).getList().get(j).getDishId().equals(list.get(position).getDishId()))
                        {
                            DishFrg.list.get(i).getList().get(j).setDishNum(list.get(position).getDishNum());
                        }
                    }
                }
            }
        });

        String dishHUrl = list.get(position).getDishHUrl();
        if (!TextUtils.isEmpty(dishHUrl))
        {
            if (dishHUrl.contains(CommonUtils.SEPARATOR))
            {
                List<String> dishHUrlList = CommonUtils.strToList(dishHUrl);
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(dishHUrlList.get(0))
                        .placeholder(R.drawable.qs_cai_canting)
                        .error(R.drawable.cai_da)
                        .centerCrop()
                        .into(viewHolder.ivMenuIcon);
            } else
            {
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(list.get(position).getDishHUrl())
                        .placeholder(R.drawable.qs_cai_canting)
                        .error(R.drawable.cai_da)
                        .centerCrop()
                        .into(viewHolder.ivMenuIcon);
            }
        }

        viewHolder.tvDishName.setText(list.get(position).getDishName());
        viewHolder.tvDishPrice.setText("￥" + CommonUtils.keep2Decimal(Double.parseDouble(list.get(position).getDishPrice())));

        viewHolder.arlTop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Logger.t(TAG).d("新布局跳转");
                Intent intent = new Intent(context, FoodDetailAct.class);
                Bundle bundle = new Bundle();
                intent.putExtra("dishId", list.get(position).getDishId());
                intent.putExtra("rId", rid);
                intent.putExtra("resName", resName);
                intent.putExtra("lessPrice", lessPrice);
                Logger.t(TAG).d("搜索列表向菜品详情传值--> " + list.get(position).getDishId() + " , " + rid + " , " + resName);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    class ViewHolder
    {
        private TextView tvHint;
        private ImageView ivAdd;
        private ImageView ivSubtract;
        private RoundedImageView ivMenuIcon;
        private TextView tvDishName;
        private TextView tvDishPrice;
        private AutoLinearLayout allStars;
        private RoundedImageView ivMenuHint;
        private AutoRelativeLayout arlTop;
    }

}

package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.echoesnet.eam.icontextmodule.model.EchoesEamIcon;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.fragments.DishFrg;
import com.echoesnet.eatandmeet.models.bean.DishBean;
import com.echoesnet.eatandmeet.models.bean.DishDetailBean;
import com.echoesnet.eatandmeet.models.bean.OrderBean;
import com.echoesnet.eatandmeet.models.bean.OrderedDishItemBean;
import com.echoesnet.eatandmeet.presenters.ImpIFoodDetail;
import com.echoesnet.eatandmeet.presenters.viewinterface.IFoodDetailView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.CustomRatingBar.CustomRatingBar;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.ImageIndicatorView.NetworkImageIndicatorView;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.joanzapata.iconify.IconDrawable;
import com.orhanobut.logger.Logger;
import com.panxw.android.imageindicator.AutoPlayManager;
import com.panxw.android.imageindicator.ImageIndicatorView;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @version 1.0
 * @modifier
 * @createDate
 * @description 菜品详情
 */

public class FoodDetailAct extends MVPBaseActivity<IFoodDetailView, ImpIFoodDetail> implements IFoodDetailView
{
    public final static String TAG = FoodDetailAct.class.getSimpleName();
    @BindView(R.id.arl_shopCar)
    AutoRelativeLayout arlShopCar;
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.iv_shoppingcart_icon)
    ImageView ivShoppingCartIcon;
    @BindView(R.id.tv_detail_title)
    TextView tvDetailTitle;
    @BindView(R.id.tv_detail_price)
    TextView tvDetailPrice;
    @BindView(R.id.rating_bar)
    CustomRatingBar ratingBar;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_context)
    TextView tvContext;
    @BindView(R.id.btn_checkout)
    Button btnCheckout;
    @BindView(R.id.fl_carimg)
    AutoFrameLayout aflCarImg;
    @BindView(R.id.icv_cycle_view)
    NetworkImageIndicatorView mImageCycleView;

    private OrderedDishItemBean itemBean;
    private String dishId, rId;
    private int screenHeight, screenWidth;
    private MyPopWindow myPopWindow;
    private String resName;
    private String lessPrice;
    private Activity mAct;
    private List<DishBean> listDishBean;
    private Dialog pDialog;
    private String index;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_fooddetail);
        ButterKnife.bind(this);
        initAfterViews();

        for (int i = 0; i < DishFrg.list.size(); i++)
        {
            for (int j = 0; j < DishFrg.list.get(i).getList().size(); j++)
            {
                if (DishFrg.list.get(i).getList().get(j).getDishId().equals(dishId))
                {
                    itemBean = DishFrg.list.get(i).getList().get(j);
                }
            }
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    protected ImpIFoodDetail createPresenter()
    {
        return new ImpIFoodDetail();
    }

    void initAfterViews()
    {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
        dishId = getIntent().getStringExtra("dishId");
        rId = getIntent().getStringExtra("rId");
        resName = getIntent().getStringExtra("resName");
        lessPrice = getIntent().getStringExtra("lessPrice");
        index = getIntent().getStringExtra("index");
        btnCheckout.setBackgroundResource(R.color.MC1);
        pDialog = DialogUtil.getCommonDialog(this, "正在获取菜品信息");
        pDialog.setCancelable(false);
        mAct = this;
        topBar.setTitle(getResources().getString(R.string.food_detail_title));
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                if (!TextUtils.isEmpty(index) && index.equals("detail"))
                {
                    Intent intent = new Intent(mAct,DOrderMealDetailAct.class);
                    intent.putExtra("index", 1);
                    intent.putExtra("fromFoodDetail", true);
                    intent.putExtra("restId",rId);
                    startActivity(intent);
                    finish();
                } else
                {
                    FoodDetailAct.this.finish();
                }
            }

            @Override
            public void left2Click(View view)
            {

            }

            @Override
            public void rightClick(View view)
            {

            }
        });
        if (mPresenter != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            mPresenter.getNewDishDetail(dishId);
        }
        ivShoppingCartIcon.setImageDrawable(new IconDrawable(this, EchoesEamIcon.eam_s_shop_cart).colorRes(R.color.white));
        tvPrice.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(DishFrg.list)));
        tvTotal.setText(CommonUtils.getDishCount(DishFrg.list) + "");
    }

    @OnClick ({R.id.btn_checkout, R.id.fl_carimg, R.id.arl_shopCar})
    void viewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_checkout:
                itemBean.setDishNum(itemBean.getDishNum() + 1);

                for (int i = 0; i < DishFrg.list.size(); i++)
                {
                    for (int j = 0; j < DishFrg.list.get(i).getList().size(); j++)
                    {
                        if (DishFrg.list.get(i).getList().get(j).getDishId().equals(itemBean.getDishId()))
                        {
                            DishFrg.list.get(i).getList().get(j).setDishNum(itemBean.getDishNum());
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
                tvPrice.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(DishFrg.list)));
                tvTotal.setText(CommonUtils.getDishCount(DishFrg.list) + "");
                switch (OrderBean.getOrderBeanInstance().getType())
                {
                    case "0":
                        OrderBean.getOrderBeanInstance().setType("2");
                        break;
                    case "1":
                        OrderBean.getOrderBeanInstance().setType("3");
                        break;
                    default:
                        break;
                }
                break;
            case R.id.fl_carimg:
                myPopWindow = new MyPopWindow(this);
                myPopWindow.setOnDismissListener(new poponDismissListener(myPopWindow));
                myPopWindow.showPopupWindow(aflCarImg);
                break;
            case R.id.arl_shopCar:
                myPopWindow = new MyPopWindow(this);
                myPopWindow.setOnDismissListener(new poponDismissListener(myPopWindow));
                myPopWindow.showPopupWindow(arlShopCar);
                break;
            default:
                break;
        }
    }

    /**
     * 购物车弹出列表
     */
    private View contentView;
    private ListView listview;
    private TextView allClear, tvPricePopup;
    private AutoFrameLayout aflCarImgPopup;
    private Button btnCheckoutPopup;
    private TextView tvTotalPopup;
    private ImageView ivCarIcon;
    private ArrayList<OrderedDishItemBean> cartList = new ArrayList<OrderedDishItemBean>();

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {

    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
    }

    @Override
    public void getNewDishDetailCallback(DishDetailBean response)
    {
        if (response == null)
        {
            ToastUtils.showShort("获取菜品详情失败");
        } else
        {
            Logger.t(TAG).d("菜品详情获取成功--> " + response);
            tvDetailTitle.setText(response.getDishName().toString());
            tvDetailPrice.setText("￥" + response.getDishPrice().toString());
            tvTotal.setText(CommonUtils.getDishCount(DishFrg.list) + "");
            tvPrice.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(DishFrg.list)));
            tvContext.setText(response.getDishMemo());
            ratingBar.setIndicator(true);
            ratingBar.setRatingBar(Integer.parseInt(response.getDishStar()));
            String url = response.getDishHUrl();
            if (url != null)
                initCycleViewData(url);
        }
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }


    private class MyPopWindow extends PopupWindow
    {
        public MyPopWindow(final Context context)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            contentView = inflater.inflate(R.layout.cart_layout, null);
            listview = (ListView) contentView.findViewById(R.id.listview);
            tvTotalPopup = (TextView) contentView.findViewById(R.id.tv_total);
            ivCarIcon = (ImageView) contentView.findViewById(R.id.iv_car_icon);
            ivCarIcon.setImageDrawable(new IconDrawable(FoodDetailAct.this, EchoesEamIcon.eam_s_shop_cart).colorRes(R.color.white));
            aflCarImgPopup = (AutoFrameLayout) contentView.findViewById(R.id.fl_carimg);
            tvPricePopup = (TextView) contentView.findViewById(R.id.tv_price);
            btnCheckoutPopup = (Button) contentView.findViewById(R.id.btn_checkout);
            allClear = (TextView) contentView.findViewById(R.id.all_clear);

            allClear.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int k = 0;
                    for (int i = 0; i < DishFrg.list.size(); i++)
                    {
                        for (int j = 0; j < DishFrg.list.get(i).getList().size(); j++)
                        {
                            k += DishFrg.list.get(i).getList().get(j).getDishNum();
                        }
                    }
                    if (k == 0)
                    {
                        ToastUtils.showShort("没有要清空的商品");
                        return;
                    }
                    new CustomAlertDialog(mAct)
                            .builder()
                            .setMsg(getString(R.string.clear_shop_car_tip))
                            .setPositiveButton("确认", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    dismiss();
                                    for (int i = 0; i < DishFrg.list.size(); i++)
                                    {
                                        for (int j = 0; j < DishFrg.list.get(i).getList().size(); j++)
                                        {
                                            DishFrg.list.get(i).getList().get(j).setDishNum(0);
                                        }
                                    }
                                    itemBean.setDishNum(0);
                                    tvTotal.setText(CommonUtils.getDishCount(DishFrg.list) + "");
                                    tvPrice.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(DishFrg.list)));
                                    String type=OrderBean.getOrderBeanInstance().getType();
                                    if("2".equals(type)||"0".equals(type))
                                        OrderBean.getOrderBeanInstance().setType("0");  // 点菜
                                    else
                                        OrderBean.getOrderBeanInstance().setType("1");  // 点菜
                                }
                            }).setNegativeButton("取消", new View.OnClickListener()
                    {

                        @Override
                        public void onClick(View v)
                        {

                        }
                    }).show();
                }
            });

            aflCarImgPopup.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dismiss();
                }
            });

            tvPricePopup.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(DishFrg.list)));
            if (CommonUtils.getDishPrice(DishFrg.list) < Double.parseDouble(lessPrice))
            {
                tvTotalPopup.setText(CommonUtils.getDishCount(DishFrg.list) + "");
                tvPricePopup.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(DishFrg.list)));
                btnCheckoutPopup.setText("还差" + (Double.parseDouble(lessPrice) - CommonUtils.getDishPrice(DishFrg.list)) + "元");
                btnCheckoutPopup.setBackgroundResource(R.color.FC3);
            } else
            {
                tvTotalPopup.setText(CommonUtils.getDishCount(DishFrg.list) + "");
                tvPricePopup.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(DishFrg.list)));
                btnCheckoutPopup.setText("下一步");
                btnCheckoutPopup.setBackgroundResource(R.color.MC1);
            }
            btnCheckoutPopup.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dismiss();
                    if (btnCheckoutPopup.getText().equals("下一步"))
                    {
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

                        generateOrder();
                        if ("0".equals(OrderBean.getOrderBeanInstance().getType()))
                        {
                            ToastUtils.showShort("还没点菜，请先点菜");
                            return;
                        }
                        if (CommonUtils.getDishPrice(DishFrg.list) < Double.valueOf(EamApplication.getInstance().lessPrice))
                        {
                            ToastUtils.showLong("不满" + lessPrice + "元");
                            return;
                        }
                        Logger.t(TAG).d("OrderBean.getOrderBeanInstance()>>>" + OrderBean.getOrderBeanInstance().toString());
                        switch (OrderBean.getOrderBeanInstance().getType())
                        {
                            case "2":
                                Intent selectIntent = new Intent(mAct,SelectTableAct.class);
                                selectIntent.putExtra("index", 0);
                                selectIntent.putExtra("restId", rId);
                                selectIntent.putExtra("resName", resName);
                                startActivity(selectIntent);
                                break;
                            case "3":
                                Intent intent = new Intent(mAct,DOrderConfirmAct.class);
                                intent.putExtra("orderData", (Serializable) DishFrg.dishList);
                                intent.putExtra("price", CommonUtils.getDishPrice(DishFrg.list));
                                startActivity(intent);
                                break;
                        }
                    } else
                    {
                        ToastUtils.showShort("不满" + lessPrice + "元");
                    }
                }
            });

            tvTotalPopup.setText(CommonUtils.getDishCount(DishFrg.list) + "");
            cartList.clear();
            for (int i = 0; i < DishFrg.list.size(); i++)
            {
                for (int j = 0; j < DishFrg.list.get(i).getList().size(); j++)
                {
                    if (DishFrg.list.get(i).getList().get(j).getDishNum() != 0)
                    {
                        cartList.add(DishFrg.list.get(i).getList().get(j));
                    }
                }
            }

            int heightList = (cartList.size() * (CommonUtils.dp2px(FoodDetailAct.this, 50)));
            int heightShow = (screenHeight / 2) - (CommonUtils.dp2px(FoodDetailAct.this, 90));
            if (heightList > heightShow)
            {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) listview.getLayoutParams();
                lp.height = heightShow;
                listview.setLayoutParams(lp);
            }
            listview.setAdapter(new CartAdapter());
            // 设置SelectPicPopupWindow的View
            this.setContentView(contentView);
            // 设置SelectPicPopupWindow弹出窗体的宽
            this.setWidth(screenWidth);
            // 设置SelectPicPopupWindow弹出窗体的高
            this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            // 设置SelectPicPopupWindow弹出窗体可点击
            this.setFocusable(true);
            this.setOutsideTouchable(true);
            this.setBackgroundDrawable(new BitmapDrawable());
            // 刷新状态
            this.update();
            this.backgroundAlpha((float) 0.5);
            // 设置SelectPicPopupWindow弹出窗体动画效果
            this.setAnimationStyle(R.style.AnimationPreview);
        }

        /**
         * 显示popupWindow
         *
         * @param parent
         */
        public void showPopupWindow(View parent)
        {
            if (!this.isShowing())
            {
                // 以下拉方式显示popupWindow
                this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            } else
            {
                this.dismiss();
            }
        }

        /**
         * 设置添加屏幕的背景透明度
         *
         * @param bgAlpha
         */
        public void backgroundAlpha(float bgAlpha)
        {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = bgAlpha; // 0.0-1.0
            getWindow().setAttributes(lp);
        }
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     */
    private class poponDismissListener implements PopupWindow.OnDismissListener
    {
        private MyPopWindow mypopuwindow;

        public poponDismissListener(MyPopWindow mypopuwindow)
        {
            this.mypopuwindow = mypopuwindow;
        }

        @Override
        public void onDismiss()
        {
            mypopuwindow.backgroundAlpha(1f);
        }
    }

    private class CartAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return cartList.size();
        }

        @Override
        public Object getItem(int arg0)
        {
            return cartList.get(arg0);
        }

        @Override
        public long getItemId(int arg0)
        {
            return arg0;
        }

        @Override
        public View getView(final int arg0, View arg1, ViewGroup arg2)
        {
            Holder holder = null;
            if (arg1 == null)
            {
                holder = new Holder();
                arg1 = LayoutInflater.from(FoodDetailAct.this).inflate(R.layout.cart_item, null);
                holder.tvName = (TextView) arg1.findViewById(R.id.tv_name);
                holder.priceTv = (TextView) arg1.findViewById(R.id.tv_price);
                holder.tvCostHint = (TextView) arg1.findViewById(R.id.tv_cost_hint);
                holder.imgSubtract = (ImageView) arg1.findViewById(R.id.img_substract);
                holder.imgAdd = (ImageView) arg1.findViewById(R.id.img_add);
                arg1.setTag(holder);
            } else
            {
                holder = (Holder) arg1.getTag();
            }
            holder.updateView(arg0);
            return arg1;
        }

        private class Holder
        {
            private TextView tvName;
            private TextView priceTv;
            private TextView tvCostHint;
            private ImageView imgSubtract;
            private ImageView imgAdd;

            public void updateView(final int position)
            {
                tvName.setText(cartList.get(position).getDishName());
                priceTv.setText("￥" + cartList.get(position).getDishPrice());
                tvCostHint.setText(cartList.get(position).getDishNum() + "");
                if (cartList.get(position).getDishNum() == 0)
                {
                    imgSubtract.setVisibility(View.INVISIBLE);
                    tvCostHint.setVisibility(View.INVISIBLE);
                } else
                {
                    imgSubtract.setVisibility(View.VISIBLE);
                    tvCostHint.setVisibility(View.VISIBLE);
                }

                imgAdd.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        cartList.get(position).setDishNum(cartList.get(position).getDishNum() + 1);
                        notifyDataSetChanged();
                        calculationPopup();
                        calculation();
                    }
                });
                imgSubtract.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        cartList.get(position).setDishNum(cartList.get(position).getDishNum() - 1);
                        if (cartList.get(position).getDishNum() == 0)
                        {
                            cartList.remove(position);
                            tvTotal.setText(CommonUtils.getDishCount(DishFrg.list) + "");
                            tvPrice.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(DishFrg.list)));
                        }
                        notifyDataSetChanged();
                        calculationPopup();
                        calculation();
                    }
                });
            }

        }
    }

    private void calculationPopup()
    {
        tvPricePopup.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(DishFrg.list)));
        tvTotalPopup.setText(CommonUtils.getDishCount(DishFrg.list) + "");
    }

    private void calculation()
    {
        if (CommonUtils.getDishCount(DishFrg.list) != 0)
        {
            if (CommonUtils.getDishPrice(DishFrg.list) < Double.parseDouble(lessPrice))
            {
                tvPrice.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(DishFrg.list)));
                tvTotal.setText(CommonUtils.getDishCount(DishFrg.list) + "");
                btnCheckoutPopup.setText("还差" + (Double.parseDouble(lessPrice) - CommonUtils.getDishPrice(DishFrg.list)) + "元");
                btnCheckoutPopup.setBackgroundResource(R.color.FC3);
            } else
            {
                tvPrice.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(DishFrg.list)));
                tvTotal.setText(CommonUtils.getDishCount(DishFrg.list) + "");
                btnCheckoutPopup.setText("下一步");
                btnCheckoutPopup.setBackgroundResource(R.color.MC1);
            }
        } else
        {
            tvTotalPopup.setText(CommonUtils.getDishCount(DishFrg.list) + "");
            tvPricePopup.setText("您还没有点菜哦");
            btnCheckoutPopup.setText("满" + lessPrice + "元起订");
            btnCheckoutPopup.setBackgroundResource(R.color.FC3);
        }
    }

    private void generateOrder()
    {
        if (DishFrg.dishList.size() == 0)
        {
            Logger.t("测试").d("dishList.size()为0");
        } else
        {
            listDishBean = new ArrayList<DishBean>();
            for (int i = 0; i < DishFrg.dishList.size(); i++)
            {
                DishBean bean = new DishBean();
                bean.setDishId(DishFrg.dishList.get(i).getDishId());
                bean.setDishName(DishFrg.dishList.get(i).getDishName());
                bean.setDishPrice(DishFrg.dishList.get(i).getDishPrice());
                bean.setDishAmount(String.valueOf(DishFrg.dishList.get(i).getDishNum()));
                listDishBean.add(bean);
            }
        }
        OrderBean.getOrderBeanInstance().setDishBeen(listDishBean);
        OrderBean.getOrderBeanInstance().setFewDishes(String.valueOf(CommonUtils.getDishCount(DishFrg.list)));
        OrderBean.getOrderBeanInstance().setOrderCos1(String.valueOf(CommonUtils.getDishPrice(DishFrg.list)));
        OrderBean.getOrderBeanInstance().setOrderCos2(String.valueOf(CommonUtils.getDishPrice(DishFrg.list)));
        Logger.t(TAG).d("FoodDetailAct:" + OrderBean.getOrderBeanInstance().getOrderCos2());
    }

    private void initCycleViewData(String url)
    {
        List<String> urlList = new ArrayList<>();

        if (url.contains(CommonUtils.SEPARATOR))
        {
            String[] urlArray = url.split(CommonUtils.SEPARATOR);
            for (int i = 0; i < urlArray.length; i++)
            {
                urlList.add(urlArray[i]);
            }
        } else
        {
            urlList.add(url);
        }

        mImageCycleView.setupLayoutByImageUrl(urlList);
        mImageCycleView.show();
        mImageCycleView.setOnItemClickListener(new ImageIndicatorView.OnItemClickListener()
        {
            @Override
            public void OnItemClick(View view, int position)
            {

            }
        });
        AutoPlayManager autoBrocastManager = new AutoPlayManager(mImageCycleView);
        autoBrocastManager.setBroadcastEnable(true);
        autoBrocastManager.setBroadCastTimes(10000);//loop times
        autoBrocastManager.setBroadcastTimeIntevel(3 * 1000, 3 * 1000);//设置第一次展示时间以及间隔，间隔不能小于2秒
        autoBrocastManager.loop();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (!TextUtils.isEmpty(index) && index.equals("detail"))
        {
            Intent intent = new Intent(mAct,DOrderMealDetailAct.class);
            intent.putExtra("index", 1);
            intent.putExtra("fromFoodDetail", true);
            intent.putExtra("restId",rId);
            startActivity(intent);
            finish();
        } else
        {
            FoodDetailAct.this.finish();
        }
    }
}

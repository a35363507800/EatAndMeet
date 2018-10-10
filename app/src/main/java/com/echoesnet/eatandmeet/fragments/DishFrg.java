package com.echoesnet.eatandmeet.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.echoesnet.eam.icontextmodule.model.EchoesEamIcon;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.DOrderConfirmAct;
import com.echoesnet.eatandmeet.activities.DOrderMealDetailAct;
import com.echoesnet.eatandmeet.activities.DOrderSearchAct;
import com.echoesnet.eatandmeet.activities.SelectTableAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.DishBean;
import com.echoesnet.eatandmeet.models.bean.DishRightMenuGroupBean;
import com.echoesnet.eatandmeet.models.bean.OrderBean;
import com.echoesnet.eatandmeet.models.bean.OrderedDishItemBean;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.presenters.ImpDishFrgView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDishFrgView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.OrderMenuLeftAdapter;
import com.echoesnet.eatandmeet.views.adapters.OrderMenuRightAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.orderDishesView.PinnedHeaderListView;
import com.joanzapata.iconify.IconDrawable;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @version 1.0
 * @modifier zdw
 * @createDate 2016/5/5
 * @description 点菜
 */
public class DishFrg extends BaseFragment implements IDishFrgView
{
    private final static String TAG = DishFrg.class.getSimpleName();

    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.fl_carimg)
    AutoFrameLayout aflCarImg;
    @BindView(R.id.arl_shopCar)
    AutoLinearLayout arlShopCar;
    @BindView(R.id.btn_checkout)
    Button btnCheckout;
    @BindView(R.id.lv_menu_left)
    ListView lvMenuLeft;
    @BindView(R.id.lv_menu_right)
    PinnedHeaderListView lvMenuRight;
    @BindView(R.id.arl_search)
    AutoRelativeLayout arlSearch;
    @BindView(R.id.rl_bottom)
    AutoRelativeLayout rlBottom;
    @BindView(R.id.iv_shoppingcart_icon)
    ImageView ivShoppingCartIcon;
    Unbinder unbinder;

    private boolean isScroll = true;
    private OrderMenuLeftAdapter adapter;
    public static OrderMenuRightAdapter adapterRight;
    //左侧菜单的集合
    public static ArrayList<DishRightMenuGroupBean> list;
    private String bootyCallDate;//约吃饭日期
    private ArrayList<DishRightMenuGroupBean> listLeft;
    public static ArrayList<OrderedDishItemBean> dishList = new ArrayList<>(); // 购物车菜品
    private int screenHeight, screenWidth;
    private MyPopWindow myPopWindow;
    private String rid;
    private ArrayList<String> dishTypes;
    private String resName;
    private String lessPrice = "0";
    private Activity mAct;
    private ImpDishFrgView impDishFrgView;
    private List<DishBean> listDishBean;

    public DishFrg()
    {
    }

    public static DishFrg newInstance(String resId, ArrayList<String> dishType, String resName, String lessPrice, String bootyCallDate)
    {
        DishFrg fragment = new DishFrg();
        Bundle args = new Bundle();
        args.putString("resId", resId);
        args.putStringArrayList("dishType", dishType);
        args.putString("resName", resName);
        args.putString("lessPrice", lessPrice);
        args.putString("bootyCallDate", bootyCallDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
        if (getArguments() != null)
        {
            rid = getArguments().getString("resId");
            resName = getArguments().getString("resName");
            dishTypes = getArguments().getStringArrayList("dishType");
            bootyCallDate = getArguments().getString("bootyCallDate");
//            lessPrice = getArguments().getString("lessPrice");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frg_dish, container, false);
        unbinder = ButterKnife.bind(this, view);
        afterView();
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (adapterRight != null && tvTotal != null && tvPrice != null)
        {
            adapterRight.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDetach()
    {
        adapterRight = null;
        super.onDetach();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }

    private void afterView()
    {
        mAct = getActivity();
        impDishFrgView = new ImpDishFrgView(mAct, this);
        list = new ArrayList<>();
        lessPrice = EamApplication.getInstance().lessPrice;
        adapter = new OrderMenuLeftAdapter(getActivity(), list);
        lvMenuLeft.setAdapter(adapter);
        adapterRight = new OrderMenuRightAdapter(getActivity(), tvTotal, tvPrice, aflCarImg, btnCheckout, lessPrice, resName);
        lvMenuRight.setAdapter(adapterRight);

        if (impDishFrgView != null)
        {
            impDishFrgView.getDishList(rid);
        }
    }

    @OnClick({R.id.arl_search, R.id.fl_carimg, R.id.btn_checkout, R.id.arl_shopCar})
    void click(View view)
    {
        switch (view.getId())
        {
            case R.id.arl_search:
                Intent intentSearch = new Intent(getActivity(),DOrderSearchAct.class);
                if (list != null && list.size() > 0)
                {
                    dishList.clear();
                    for (int i = 0; i < list.size(); i++)
                    {
                        for (int j = 0; j < list.get(i).getList().size(); j++)
                        {
                            if (list.get(i).getList().get(j).getDishNum() != 0)
                            {
                                dishList.add(list.get(i).getList().get(j));
                            }
                        }
                    }
                    intentSearch.putExtra("list", dishList);
                    intentSearch.putExtra("listTotal", list);
                }
                intentSearch.putExtra("index", "search");
                intentSearch.putExtra("dishTotal", CommonUtils.getDishCount(DishFrg.list));
                intentSearch.putExtra("dishPrice", CommonUtils.getDishPrice(DishFrg.list));
                intentSearch.putExtra("rid", rid);
                intentSearch.putExtra("resName", resName);
                intentSearch.putExtra("lessPrice", lessPrice);
                Logger.t(TAG).d("最低消费--> " + lessPrice);
                getActivity().startActivity(intentSearch);
                break;
            case R.id.fl_carimg:
                rlBottom.setVisibility(View.GONE);
                myPopWindow = new MyPopWindow(getActivity());
                myPopWindow.setOnDismissListener(new poponDismissListener(myPopWindow));
                myPopWindow.showPopupWindow(aflCarImg);
                break;
            case R.id.arl_shopCar:
                rlBottom.setVisibility(View.GONE);
                myPopWindow = new MyPopWindow(getActivity());
                myPopWindow.setOnDismissListener(new poponDismissListener(myPopWindow));
                myPopWindow.showPopupWindow(arlShopCar);
                break;
            case R.id.btn_checkout: //提交
                dishList.clear();
                for (int i = 0; i < list.size(); i++)
                {
                    for (int j = 0; j < list.get(i).getList().size(); j++)
                    {
                        if (list.get(i).getList().get(j).getDishNum() != 0)
                        {
                            dishList.add(list.get(i).getList().get(j));
                        }
                    }
                }
                if (btnCheckout.getText().equals("下一步"))
                {
                    generateOrder();
                    if (OrderBean.getOrderBeanInstance().getType().equals("0") || OrderBean.getOrderBeanInstance().getType().equals("2"))  // 未选桌 未点菜
                    {
                        OrderBean.getOrderBeanInstance().setType("2");  // 点菜
                        Intent intent = new Intent(mAct,SelectTableAct.class);
                        intent.putExtra("index", 0);
                        intent.putExtra("restId", rid);
                        intent.putExtra("resName", resName);
                        intent.putExtra("bootyCallDate", bootyCallDate);
                        getActivity().startActivityForResult(intent, EamCode4Result.reQ_SelectTableActivity);
                    }
                    else if (OrderBean.getOrderBeanInstance().getType().equals("1") || OrderBean.getOrderBeanInstance().getType().equals("3"))
                    {
                        // 选珠 未点菜
                        OrderBean.getOrderBeanInstance().setType("3");  // 全选
                        Intent intent = new Intent(getActivity(), DOrderConfirmAct.class);
                        intent.putExtra("orderData", (Serializable) DishFrg.list);
                        intent.putExtra("price", CommonUtils.getDishPrice(DishFrg.list));
                        //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        getActivity().startActivityForResult(intent, EamConstant.EAM_CONFIRM_ORDER_REQUEST_CODE);
                    }
                }
                break;
        }
    }

    private void generateOrder()
    {
        if (dishList.size() == 0)
        {
            Logger.t(TAG).d("dishList.size()为0");
        }
        else
        {
            listDishBean = new ArrayList<DishBean>();
            for (int i = 0; i < dishList.size(); i++)
            {
                DishBean bean = new DishBean();
                bean.setDishId(dishList.get(i).getDishId());
                bean.setDishName(dishList.get(i).getDishName());
                bean.setDishPrice(dishList.get(i).getDishPrice());
                bean.setDishAmount(String.valueOf(dishList.get(i).getDishNum()));
                listDishBean.add(bean);
            }
        }
        OrderBean.getOrderBeanInstance().setDiscount("0");
        OrderBean.getOrderBeanInstance().setrName(resName);
        OrderBean.getOrderBeanInstance().setDishBeen(listDishBean);
        // 修改静态变量之后
        OrderBean.getOrderBeanInstance().setFewDishes(String.valueOf(CommonUtils.getDishCount(list)));
        OrderBean.getOrderBeanInstance().setOrderCos1(String.valueOf(CommonUtils.getDishPrice(list)));
        OrderBean.getOrderBeanInstance().setOrderCos2(String.valueOf(CommonUtils.getDishPrice(list)));
        Logger.t(TAG).d("DishFrg:" + OrderBean.getOrderBeanInstance().getOrderCos2());
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
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
            ((DOrderMealDetailAct)getActivity()).backgroundAlpha(false);
            rlBottom.setVisibility(View.VISIBLE);
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
    private ArrayList<OrderedDishItemBean> cartList = new ArrayList<OrderedDishItemBean>();
    private TextView tvTotalPopup;
    private ImageView ivCarIcon;

    private class MyPopWindow extends PopupWindow
    {
        public MyPopWindow(final Context context)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            contentView = inflater.inflate(R.layout.cart_layout, null);
            listview = (ListView) contentView.findViewById(R.id.listview);
            tvTotalPopup = (TextView) contentView.findViewById(R.id.tv_total);
            ivCarIcon = (ImageView) contentView.findViewById(R.id.iv_car_icon);
            ivCarIcon.setImageDrawable(new IconDrawable(getActivity(), EchoesEamIcon.eam_s_shop_cart).colorRes(R.color.white));
            allClear = (TextView) contentView.findViewById(R.id.all_clear);
            allClear.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int k = 0;
                    for (int i = 0; i < list.size(); i++)
                    {
                        for (int j = 0; j < list.get(i).getList().size(); j++)
                        {
                            k += list.get(i).getList().get(j).getDishNum();
                        }
                    }
                    if (k == 0)
                    {
                        ToastUtils.showShort("没有要清空的商品");
                        return;
                    }
                    new CustomAlertDialog(getActivity())
                            .builder()
                            .setMsg(getString(R.string.clear_shop_car_tip))
                            .setPositiveButton("确认", new View.OnClickListener()
                            {

                                @Override
                                public void onClick(View v)
                                {
                                    dismiss();
                                    for (int i = 0; i < list.size(); i++)
                                    {
                                        for (int j = 0; j < list.get(i).getList().size(); j++)
                                        {
                                            list.get(i).getList().get(j).setDishNum(0);
                                        }
                                    }
                                    adapterRight.notifyDataSetChanged();
                                    rlBottom.setVisibility(View.VISIBLE);
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
            aflCarImgPopup = (AutoFrameLayout) contentView.findViewById(R.id.fl_carimg);
            aflCarImgPopup.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dismiss();
                    rlBottom.setVisibility(View.VISIBLE);
                }
            });
            tvPricePopup = (TextView) contentView.findViewById(R.id.tv_price);
            tvPricePopup.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(list)));

            btnCheckoutPopup = (Button) contentView.findViewById(R.id.btn_checkout);
            if (CommonUtils.getDishPrice(list) < Double.parseDouble(lessPrice))
            {
                tvTotalPopup.setText(CommonUtils.getDishCount(list) + "");
                tvPricePopup.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(list)));
                btnCheckoutPopup.setText("还差" + CommonUtils.keep2Decimal((Double.parseDouble(lessPrice) - CommonUtils.getDishPrice(list))) + "元");
                btnCheckoutPopup.setBackgroundResource(R.color.FC7);
                btnCheckout.setBackgroundResource(R.color.FC7);
            }
            else
            {
                tvTotalPopup.setText(CommonUtils.getDishCount(list) + "");
                tvPricePopup.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(list)));
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
                        dishList.clear();
                        for (int i = 0; i < list.size(); i++)
                        {
                            for (int j = 0; j < list.get(i).getList().size(); j++)
                            {
                                if (list.get(i).getList().get(j).getDishNum() != 0)
                                {
                                    dishList.add(list.get(i).getList().get(j));
                                }
                            }
                        }

                        generateOrder();

                        if (OrderBean.getOrderBeanInstance().getType().equals("0") || OrderBean.getOrderBeanInstance().getType().equals("2"))  // 未选桌 未点菜
                        {
                            OrderBean.getOrderBeanInstance().setType("2");  // 点菜
                            Intent intent = new Intent(mAct,SelectTableAct.class);
                            intent.putExtra("index", 0);
                            intent.putExtra("restId", rid);
                            intent.putExtra("resName", resName);
                            intent.putExtra("bootyCallDate", bootyCallDate);
                            getActivity().startActivityForResult(intent, EamCode4Result.reQ_SelectTableActivity);
                        }
                        else if (OrderBean.getOrderBeanInstance().getType().equals("1") || OrderBean.getOrderBeanInstance().getType().equals("3"))
                        {
                            // 选座 未点菜
                            OrderBean.getOrderBeanInstance().setType("3");  // 全选
                            Intent intent =new Intent(getActivity(),DOrderConfirmAct.class);
                            intent.putExtra("orderData", (Serializable) DishFrg.list);
                            intent.putExtra("price", CommonUtils.getDishPrice(list));
                            //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            getActivity().startActivityForResult(intent, EamConstant.EAM_CONFIRM_ORDER_REQUEST_CODE);
                        }
                    }
                }
            });


            tvTotalPopup.setText(CommonUtils.getDishCount(list) + "");
            cartList.clear();

            // 数据源格式改变 修改循环测试
            for (int i = 0; i < list.size(); i++)
            {
                for (int j = 0; j < list.get(i).getList().size(); j++)
                {
                    if (list.get(i).getList().get(j).getDishNum() != 0)
                    {
                        cartList.add(list.get(i).getList().get(j));
                    }
                }
            }

            int heightList = (cartList.size() * (CommonUtils.dp2px(getActivity(), 50)));
            int heightShow = (screenHeight / 2) - (CommonUtils.dp2px(getActivity(), 90));
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
            ((DOrderMealDetailAct)getActivity()).backgroundAlpha(true);
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
            }
            else
            {
                this.dismiss();
                rlBottom.setVisibility(View.VISIBLE);
            }
        }

//        /**
//         * 设置添加屏幕的背景透明度
//         *
//         * @param bgAlpha
//         */
//        public void backgroundAlpha(float bgAlpha)
//        {
//            WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
//            lp.alpha = bgAlpha; // 0.0-1.0
//            getActivity().getWindow().setAttributes(lp);
//        }
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
                arg1 = LayoutInflater.from(getActivity()).inflate(R.layout.cart_item, null);
                holder.tvName = (TextView) arg1.findViewById(R.id.tv_name);
                holder.tvPrice = (TextView) arg1.findViewById(R.id.tv_price);
                holder.tvCostHint = (TextView) arg1.findViewById(R.id.tv_cost_hint);
                holder.imgSubtract = (ImageView) arg1.findViewById(R.id.img_substract);
                holder.imgAdd = (ImageView) arg1.findViewById(R.id.img_add);
                arg1.setTag(holder);
            }
            else
            {
                holder = (Holder) arg1.getTag();
            }
            holder.updateView(arg0);
            return arg1;
        }

        private class Holder
        {
            private TextView tvName;
            private TextView tvPrice;
            private TextView tvCostHint;
            private ImageView imgSubtract;
            private ImageView imgAdd;

            public void updateView(final int position)
            {
                tvName.setText(cartList.get(position).getDishName());
                tvPrice.setText("￥" + cartList.get(position).getDishPrice());
                tvCostHint.setText(cartList.get(position).getDishNum() + "");
                if (cartList.get(position).getDishNum() == 0)
                {
                    imgSubtract.setVisibility(View.INVISIBLE);
                    tvCostHint.setVisibility(View.INVISIBLE);
                }
                else
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
                        adapterRight.notifyDataSetChanged();
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
                        }
                        notifyDataSetChanged();
                        adapterRight.notifyDataSetChanged();
                        calculation();
                    }
                });
            }
        }
    }

    private void calculation()
    {
        if (CommonUtils.getDishCount(list) != 0)
        {
            if (CommonUtils.getDishPrice(list) < Double.parseDouble(lessPrice))
            {
                tvTotalPopup.setText(CommonUtils.getDishCount(list) + "");
                tvPricePopup.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(list)));
                btnCheckoutPopup.setText("还差" + CommonUtils.keep2Decimal((Double.parseDouble(lessPrice) - CommonUtils.getDishPrice(list))) + "元");
                btnCheckoutPopup.setBackgroundResource(R.color.FC3);
            }
            else
            {
                tvTotalPopup.setText(CommonUtils.getDishCount(list) + "");
                tvPricePopup.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(list)));
                btnCheckoutPopup.setText("下一步");
                btnCheckoutPopup.setBackgroundResource(R.color.MC1);
//                OrderMealFrg.dishAndTableBean.setDishType("dishType");
            }
        }
        else
        {
            tvTotalPopup.setText(CommonUtils.getDishCount(list) + "");
            tvPricePopup.setText("您还没有点菜哦");
            btnCheckoutPopup.setText("满" + lessPrice + "元起订");
            btnCheckoutPopup.setBackgroundResource(R.color.FC3);
        }
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {

    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        NetHelper.handleNetError(mAct, null, interfaceName, e);
    }

    @Override
    public void getDishListCallback(ArrayList<DishRightMenuGroupBean> response)
    {
        if (getActivity() == null)
            return;
        if (response != null && response.size() > 0)
        {
            listLeft = new ArrayList<>();
            ArrayList<DishRightMenuGroupBean> group;
            ArrayList<OrderedDishItemBean> items;
            //去重复
            for (int i = 0; i < response.size(); i++)
            {
                for (int j = response.size() - 1; j > i; j--)
                {
                    if (response.get(j).getDishId().equals(response.get(i).getDishId()))
                    {
                        response.remove(j);
                    }
                }
            }

            for (int i = 0; i < response.size(); i++)
            {
                group = new ArrayList<>();
                DishRightMenuGroupBean groupBean = new DishRightMenuGroupBean();
                groupBean.setHeaderTitle(response.get(i).getDishClass());
                groupBean.setSelect(false);
                group.add(groupBean);
                listLeft.addAll(group);
            }

            for (int i = 0; i < listLeft.size(); i++)
            {
                for (int j = listLeft.size() - 1; j > i; j--)
                {
                    if (listLeft.get(j).getHeaderTitle().equals(listLeft.get(i).getHeaderTitle()))
                    {
                        listLeft.remove(j);
                    }
                }
            }

            for (int j = 0; j < listLeft.size(); j++)
            {
                group = new ArrayList<>();
                items = new ArrayList<>();
                DishRightMenuGroupBean groupBean = new DishRightMenuGroupBean();

                for (int i = 0; i < response.size(); i++)
                {
                    if (listLeft.get(j).getHeaderTitle().equals(response.get(i).getDishClass()))
                    {
                        OrderedDishItemBean itemBean = new OrderedDishItemBean();
                        itemBean.setDishNum(0);
                        itemBean.setrId(response.get(i).getrId());
                        itemBean.setDishId(response.get(i).getDishId());
                        itemBean.setDishHUrl(response.get(i).getDishHUrl());
                        itemBean.setDishPrice(response.get(i).getDishPrice());
                        itemBean.setDishClass(response.get(i).getDishClass());
                        itemBean.setDishStar(response.get(i).getDishStar());
                        itemBean.setDishName(response.get(i).getDishName());
                        items.add(itemBean);
                    }
                }
                groupBean.setHeaderTitle(listLeft.get(j).getHeaderTitle());
                groupBean.setList(items);
                groupBean.setSelect(false);
                group.add(groupBean);
                list.addAll(group);
            }

            listLeft.get(0).setSelect(true);
            adapter.notifyDataSetChanged();
            ivShoppingCartIcon.setImageDrawable(new IconDrawable(EamApplication.getInstance(),
                    EchoesEamIcon.eam_s_shop_cart).colorRes(R.color.white));
            adapterRight.notifyDataSetChanged();
            list.get(0).setSelect(true);
            lvMenuLeft.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    isScroll = false;
                    for (int i = 0; i < list.size(); i++)
                    {
                        if (i == position)
                        {
                            list.get(i).setSelect(true);
                        }
                        else
                        {
                            list.get(i).setSelect(false);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    int rightSection = 0;
                    for (int i = 0; i < position; i++)
                    {
                        rightSection += adapterRight.getCountForSection(i) + 1;
                    }
                    lvMenuRight.setSelection(rightSection + 1);
                }
            });

            lvMenuRight.setOnScrollListener(new AbsListView.OnScrollListener()
            {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState)
                {
                    switch (scrollState)
                    {
                        // 当不滚动时
                        case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                            // 判断滚动到底部
                            if (lvMenuRight.getLastVisiblePosition() == (lvMenuRight.getCount() - 1))
                            {
                                lvMenuLeft.setSelection(ListView.FOCUS_DOWN);
                            }
                            // 判断滚动到顶部
                            if (lvMenuRight.getFirstVisiblePosition() == 0)
                            {
                                lvMenuLeft.setSelection(0);
                            }
                            break;
                    }
                }

                int y = 0;
                int x = 0;
                //int z = 0;

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
                {
                    if (isScroll)
                    {
                        for (int i = 0; i < list.size(); i++)
                        {
                            if (adapterRight != null && i == adapterRight.getSectionForPosition(lvMenuRight.getFirstVisiblePosition()))
                            {
                                list.get(i).setSelect(true);
                                x = i;
                            }
                            else
                            {
                                list.get(i).setSelect(false);
                            }
                        }
                        if (x != y)
                        {
                            adapter.notifyDataSetChanged();
                            y = x;
                            if (y == lvMenuLeft.getLastVisiblePosition())
                            {
                                // z = z + 2;
                                lvMenuLeft.setSelection(x - 2);
//                                lv_menu_left.setSelection(z);
                            }
                            if (x == lvMenuLeft.getFirstVisiblePosition())
                            {
                                // z = z - 1;
                                lvMenuLeft.setSelection(x);
//                                lv_menu_left.setSelection(z);
                            }
                            if (firstVisibleItem + visibleItemCount == totalItemCount - 1)
                            {
                                lvMenuLeft.setSelection(ListView.FOCUS_DOWN);
                            }
                        }
                    }
                    else
                    {
                        isScroll = true;
                    }
                }
            });
        }
        else
        {
            ToastUtils.showShort("暂无点菜信息");
        }
    }
}

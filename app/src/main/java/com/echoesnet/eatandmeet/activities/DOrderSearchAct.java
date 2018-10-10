package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.echoesnet.eam.icontextmodule.model.EchoesEamIcon;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.fragments.DishFrg;
import com.echoesnet.eatandmeet.models.bean.DishBean;
import com.echoesnet.eatandmeet.models.bean.OrderBean;
import com.echoesnet.eatandmeet.models.bean.OrderedDishItemBean;
import com.echoesnet.eatandmeet.presenters.ImpDOrderSearchView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDOrderSearchView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.OrderSearchAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.widget.IconTextView;
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

import static com.echoesnet.eatandmeet.fragments.DishFrg.adapterRight;


public class DOrderSearchAct extends MVPBaseActivity<IDOrderSearchView, ImpDOrderSearchView> implements IDOrderSearchView
{
    private static final String TAG = DOrderSearchAct.class.getSimpleName();
    @BindView(R.id.ll_search)
    AutoLinearLayout llSearch;
    @BindView(R.id.fl_all_car_img)
    AutoFrameLayout flAllCarImg;
    @BindView(R.id.iv_search)
    IconTextView ivSearch;
    @BindView(R.id.rl_all_search)
    AutoRelativeLayout rlAllSearch;

    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.lv_search)
    ListView lvSearch;
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.iv_shoppingcart_icon)
    ImageView ivShoppingCartIcon;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.fl_carimg)
    AutoFrameLayout flCarImg;
    @BindView(R.id.rl_bottom)
    AutoRelativeLayout rlBottom;
    @BindView(R.id.btn_checkout)
    Button btnCheckout;
    @BindView(R.id.iv_delete)
    ImageView ivDelete;

    private OrderSearchAdapter adapter;
    private String rid;
    private MyPopWindow myPopWindow;
    private int screenHeight, screenWidth;
    private String resName;
    private ArrayList<OrderedDishItemBean> dishList; // 点菜页面已选菜品(传递数据)
    private int dishTotal;
    private double dishPrice;
    private String lessPrice;
    private List<OrderedDishItemBean> tempOrderList; // 通知搜索列表更新集合
    private View contentView;                         // 购物车弹出列表
    private ListView listview;
    private TextView allClear, tvPricePop;
    private AutoFrameLayout flCarImgPop;
    private Button btnCheckoutPop;
    private TextView tvTotalPop;
    private ImageView ivCarIcon;
    private Activity mActivity;

    private Dialog pDialog;
    private String index;
    private List<DishBean> listDishBean;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_order_search);
        ButterKnife.bind(this);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
        rid = getIntent().getStringExtra("rid");
//        dishList = (ArrayList<OrderedDishItemBean>) getIntent().getSerializableExtra("list");
        dishList = DishFrg.dishList;
        index = getIntent().getStringExtra("index");
        dishTotal = getIntent().getIntExtra("dishTotal", 0);
        dishPrice = getIntent().getDoubleExtra("dishPrice", 0.0);
        lessPrice = getIntent().getStringExtra("lessPrice");
        Logger.t(TAG).d("传递过来的rid--> " + rid + " , 最低消费--> " + lessPrice);
        initAfterView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (TextUtils.isEmpty(etSearch.getText().toString().trim()))
        {
            return;
        }
        if (tempOrderList != null && tempOrderList.size() > 0)
        {
            for (int i = 0; i < tempOrderList.size(); i++)
            {
                for (int j = 0; j < DishFrg.dishList.size(); j++)
                {
                    if (tempOrderList.get(i).getDishId().equals(DishFrg.dishList.get(j).getDishId()))
                    {
                        tempOrderList.get(i).setDishNum(DishFrg.dishList.get(j).getDishNum());
                    }
                }
            }
        }
        Logger.t(TAG).d("测试数据--> " + tempOrderList.get(0).getDishNum() + " , " + DishFrg.dishList.size() + " , " + tempOrderList.size());
        if (adapter != null)
        {
            adapter.notifyDataSetChanged();
        }
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

    void initAfterView()
    {
        mActivity = this;
        pDialog = DialogUtil.getCommonDialog(DOrderSearchAct.this, "正在处理...");
        pDialog.setCancelable(false);
        resName = getIntent().getStringExtra("resName");
        tvTotal.setText(dishTotal + "");
        tvPrice.setText("￥" + CommonUtils.keep2Decimal(dishPrice));
        topBar.setTitle(getResources().getString(R.string.order_search));
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                if (!TextUtils.isEmpty(index) && index.equals("search"))
                {
                    Intent intent = new Intent(mActivity, DOrderMealDetailAct.class);
                    intent.putExtra("index", 1);
                    intent.putExtra("fromFoodDetail",true);
                    startActivity(intent);
                    finish();
                } else
                {
                    DOrderSearchAct.this.finish();
                }
            }

            @Override
            public void left2Click(View view)
            {
                if (SharePreUtils.getSource(DOrderSearchAct.this).equals("find"))
                {
                    Intent intent = new Intent(DOrderSearchAct.this, HomeAct.class);
                    intent.putExtra("showPage", 0);
                    startActivity(intent);
                } else if (SharePreUtils.getSource(DOrderSearchAct.this).equals("orderMeal"))
                {
                    Intent intent = new Intent(DOrderSearchAct.this, HomeAct.class);
                    intent.putExtra("showPage", 2);
                    startActivity(intent);
                }
            }

            @Override
            public void rightClick(View view) {}
        });

        ivShoppingCartIcon.setImageDrawable(new IconDrawable(DOrderSearchAct.this, EchoesEamIcon.eam_s_shop_cart)
                .colorRes(R.color.white));
        tempOrderList = new ArrayList<>();
        adapter = new OrderSearchAdapter(DOrderSearchAct.this, tempOrderList, flCarImg, tvTotal, tvPrice, btnCheckout, lessPrice, rid, resName);
        lvSearch.setAdapter(adapter);

        etSearch.addTextChangedListener(new MyEditTextListener());
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    if (!etSearch.getText().toString().trim().equals(""))
                    {
                        // 先隐藏键盘
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(DOrderSearchAct.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        if (mPresenter != null)
                        {
                            if (pDialog != null && !pDialog.isShowing())
                                pDialog.show();
                            mPresenter.getDishList(rid, etSearch.getText().toString().trim());
                        }
                        return true;
                    } else
                    {
                        ToastUtils.showShort("请输入菜品名称");
                    }
                }
                return false;
            }
        });
    }

    @OnClick({R.id.iv_delete, R.id.iv_search, R.id.rl_all_search, R.id.btn_checkout})
    void viewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.iv_delete:
                etSearch.setText("");
                break;
            case R.id.iv_search:
                if (!etSearch.getText().toString().trim().equals(""))
                {
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(DOrderSearchAct.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    if (mPresenter != null)
                    {
                        if (pDialog != null && !pDialog.isShowing())
                            pDialog.show();
                        mPresenter.getDishList(rid, etSearch.getText().toString().trim());
                    }
                } else
                {
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(DOrderSearchAct.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    ToastUtils.showShort("请输入菜品名称");
                }
                break;
            case R.id.rl_all_search:
                rlBottom.setVisibility(View.GONE);
                myPopWindow = new MyPopWindow(DOrderSearchAct.this);
                myPopWindow.setOnDismissListener(new PopupWindowDismissListener(myPopWindow));
                myPopWindow.showPopupWindow(flCarImg);
                break;
            case R.id.btn_checkout:
                if (btnCheckout.getText().equals("下一步"))
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

                    if (OrderBean.getOrderBeanInstance().getType().equals("0") || OrderBean.getOrderBeanInstance().getType().equals("2"))  // 未选桌 未点菜
                    {
                        OrderBean.getOrderBeanInstance().setType("2");  // 点菜
                        Intent intent = new Intent(mActivity, SelectTableAct.class);
                        intent.putExtra("index", 0);
                        intent.putExtra("restId", rid);
                        intent.putExtra("resName", resName);
                        startActivity(intent);
                    } else if (OrderBean.getOrderBeanInstance().getType().equals("1") || OrderBean.getOrderBeanInstance().getType().equals("3"))
                    { // 选桌 未点菜
                        OrderBean.getOrderBeanInstance().setType("3");  // 全选
                        Intent intent = new Intent(mActivity, DOrderConfirmAct.class);
                        intent.putExtra("orderData", (Serializable) DishFrg.dishList);
                        intent.putExtra("price", CommonUtils.getDishPrice(DishFrg.list));
                        startActivityForResult(intent, EamConstant.EAM_CONFIRM_ORDER_REQUEST_CODE);
                    }
                }
                break;
            default:
                break;
        }
    }

    class MyEditTextListener implements TextWatcher
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
        }

        @Override
        public void afterTextChanged(Editable s)
        {
            if (etSearch.getText().length() != 0)
            {
                if (ivDelete.getVisibility() == View.GONE)
                {
                    ivDelete.setVisibility(View.VISIBLE);
                }
                ivDelete.setImageDrawable(new IconDrawable(DOrderSearchAct.this, EchoesEamIcon.eam_s_close2).colorRes(R.color.FC3));
            } else
            {
                if (ivDelete.getVisibility() == View.VISIBLE)
                {
                    ivDelete.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     */
    private class PopupWindowDismissListener implements PopupWindow.OnDismissListener
    {
        private MyPopWindow myPopupWindow;

        public PopupWindowDismissListener(MyPopWindow myPopupWindow)
        {
            this.myPopupWindow = myPopupWindow;
        }

        @Override
        public void onDismiss()
        {
            myPopupWindow.backgroundAlpha(1f);
            rlBottom.setVisibility(View.VISIBLE);
        }
    }

    private class MyPopWindow extends PopupWindow
    {
        public MyPopWindow(final Context context)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            contentView = inflater.inflate(R.layout.cart_layout, null);
            listview = (ListView) contentView.findViewById(R.id.listview);
            tvTotalPop = (TextView) contentView.findViewById(R.id.tv_total);
            ivCarIcon = (ImageView) contentView.findViewById(R.id.iv_car_icon);
            tvPricePop = (TextView) contentView.findViewById(R.id.tv_price);
            ivCarIcon.setImageDrawable(new IconDrawable(DOrderSearchAct.this, EchoesEamIcon.eam_s_shop_cart).colorRes(R.color.white));
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
                    new CustomAlertDialog(context)
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
                                    adapterRight.notifyDataSetChanged();
                                    rlBottom.setVisibility(View.VISIBLE);
                                    for (int i = 0; i < tempOrderList.size(); i++)
                                    {
                                        tempOrderList.get(i).setDishNum(0);
                                    }
                                    adapter.notifyDataSetChanged();
                                    flCarImg.setVisibility(View.GONE);
                                    tvTotal.setText(CommonUtils.getDishCount(DishFrg.list) + "");
                                    tvPrice.setText("您还没有点菜哦");
                                    btnCheckout.setText("满" + lessPrice + "元起订");
                                    String type=OrderBean.getOrderBeanInstance().getType();
                                    if("2".equals(type)||"0".equals(type))
                                        OrderBean.getOrderBeanInstance().setType("0");  //
                                    else
                                        OrderBean.getOrderBeanInstance().setType("1");  //
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

            flCarImgPop = (AutoFrameLayout) contentView.findViewById(R.id.fl_carimg);
            flCarImgPop.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dismiss();
                    rlBottom.setVisibility(View.VISIBLE);
                }
            });

            tvPricePop.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(DishFrg.list)));
            btnCheckoutPop = (Button) contentView.findViewById(R.id.btn_checkout);
            if (CommonUtils.getDishPrice(DishFrg.list) < Double.parseDouble(lessPrice))
            {
                tvTotalPop.setText(CommonUtils.getDishCount(DishFrg.list) + "");
                tvPricePop.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(DishFrg.list)));
                btnCheckoutPop.setText("还差" + (Double.parseDouble(lessPrice) - CommonUtils.getDishPrice(DishFrg.list)) + "元");
                btnCheckoutPop.setBackgroundResource(R.color.c4);
            } else
            {
                tvTotalPop.setText(CommonUtils.getDishCount(DishFrg.list) + "");
                tvPricePop.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(DishFrg.list)));
                btnCheckoutPop.setText("下一步");
                btnCheckoutPop.setBackgroundResource(R.color.MC1);
            }

            btnCheckoutPop.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dismiss();
                    if (btnCheckoutPop.getText().equals("下一步"))
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

                        if (OrderBean.getOrderBeanInstance().getType().equals("0") || OrderBean.getOrderBeanInstance().getType().equals("2"))  // 未选桌 未点菜
                        {
                            OrderBean.getOrderBeanInstance().setType("2");  // 点菜
                            Intent intent = new Intent(mActivity, SelectTableAct.class);
                            intent.putExtra("index", 0);
                            intent.putExtra("restId", rid);
                            intent.putExtra("resName", resName);
                            startActivity(intent);
                        } else if (OrderBean.getOrderBeanInstance().getType().equals("1") || OrderBean.getOrderBeanInstance().getType().equals("3"))
                        { // 选珠 未点菜
                            OrderBean.getOrderBeanInstance().setType("3");  // 全选
                            Intent intent = new Intent(mActivity, DOrderConfirmAct.class);
                            intent.putExtra("orderData", (Serializable) DishFrg.dishList);
                            intent.putExtra("price", CommonUtils.getDishPrice(DishFrg.list));
                            startActivityForResult(intent, EamConstant.EAM_CONFIRM_ORDER_REQUEST_CODE);
                        }
                    } else
                    {
                        ToastUtils.showLong("不满" + lessPrice + "元");
                    }
                }
            });

            tvTotalPop.setText(CommonUtils.getDishCount(DishFrg.list) + "");
            dishList.clear();
            for (int i = 0; i < DishFrg.list.size(); i++)
            {
                for (int j = 0; j < DishFrg.list.get(i).getList().size(); j++)
                {
                    if (DishFrg.list.get(i).getList().get(j).getDishNum() != 0)
                    {
                        dishList.add(DishFrg.list.get(i).getList().get(j));
                    }
                }
            }

            int height_list = (dishList.size() * (CommonUtils.dp2px(DOrderSearchAct.this, 50)));
            int height_xianshi = (screenHeight / 2) - (CommonUtils.dp2px(DOrderSearchAct.this, 90));
            if (height_list > height_xianshi)
            {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) listview.getLayoutParams();
                lp.height = height_xianshi;
                listview.setLayoutParams(lp);
            }
            listview.setAdapter(new CartAdapter());
            this.setContentView(contentView);                         // 设置SelectPicPopupWindow的View
            this.setWidth(screenWidth);                               // 设置SelectPicPopupWindow弹出窗体的宽
            this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);   // 设置SelectPicPopupWindow弹出窗体的高
            this.setFocusable(true);                                  // 设置SelectPicPopupWindow弹出窗体可点击
            this.setOutsideTouchable(true);
            this.setBackgroundDrawable(new BitmapDrawable());
            this.update();                                            // 刷新状态
            this.backgroundAlpha((float) 0.5);
            this.setAnimationStyle(R.style.AnimationPreview);         // 设置SelectPicPopupWindow弹出窗体动画效果
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
                this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);  // 以下拉方式显示popupWindow
            } else
            {
                this.dismiss();
                rlBottom.setVisibility(View.VISIBLE);
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

    private class CartAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return dishList.size();
        }

        @Override
        public Object getItem(int arg0)
        {
            return dishList.get(arg0);
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
                arg1 = LayoutInflater.from(DOrderSearchAct.this).inflate(R.layout.cart_item, null);
                holder.tvName = (TextView) arg1.findViewById(R.id.tv_name);
                holder.tvPrice = (TextView) arg1.findViewById(R.id.tv_price);
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
            private TextView tvPrice;
            private TextView tvCostHint;
            private ImageView imgSubtract;
            private ImageView imgAdd;

            public void updateView(final int position)
            {
                tvName.setText(dishList.get(position).getDishName());
                tvPrice.setText("￥" + CommonUtils.keep2Decimal(Double.parseDouble(dishList.get(position).getDishPrice())));
                tvCostHint.setText(dishList.get(position).getDishNum() + "");
                if (dishList.get(position).getDishNum() == 0)
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
                        dishList.get(position).setDishNum(dishList.get(position).getDishNum() + 1);   // 搜索空白页面 出现底部购物车 +-物品 与 搜索出菜品的数量一致

                        for (int i = 0; i < tempOrderList.size(); i++)
                        {
                            if (dishList.get(position).getDishId().contains(tempOrderList.get(i).getDishId()))
                            {
                                tempOrderList.get(i).setDishNum(dishList.get(position).getDishNum());
                                adapter.notifyDataSetChanged();
                            }
                        }

                        notifyDataSetChanged();
                        adapterRight.notifyDataSetChanged();
                        Calculation();
                    }
                });
                imgSubtract.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dishList.get(position).setDishNum(dishList.get(position).getDishNum() - 1);
                        for (int i = 0; i < tempOrderList.size(); i++)
                        {
                            if (dishList.get(position).getDishId().contains(tempOrderList.get(i).getDishId()))
                            {
                                tempOrderList.get(i).setDishNum(dishList.get(position).getDishNum());
                                adapter.notifyDataSetChanged();
                            }
                        }
                        notifyDataSetChanged();
                        adapterRight.notifyDataSetChanged();
                        Calculation();
                    }
                });
            }
        }
    }

    private void Calculation()
    {
        if (CommonUtils.getDishCount(DishFrg.list) != 0)
        {
            if (CommonUtils.getDishPrice(DishFrg.list) < Double.parseDouble(lessPrice))
            {
                tvTotalPop.setText(CommonUtils.getDishCount(DishFrg.list) + "");
                tvPricePop.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(DishFrg.list)));
                btnCheckoutPop.setText("还差" + CommonUtils.keep2Decimal((Double.parseDouble(lessPrice) - CommonUtils.getDishPrice(DishFrg.list))) + "元");
                btnCheckoutPop.setBackgroundResource(R.color.c4);
            } else
            {
                tvTotalPop.setText(CommonUtils.getDishCount(DishFrg.list) + "");
                tvPricePop.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(DishFrg.list)));
                btnCheckoutPop.setText("下一步");
                btnCheckoutPop.setBackgroundResource(R.color.MC1);
//                OrderMealFrg.dishAndTableBean.setDishType("dishType");
            }
        } else
        {
            tvTotalPop.setText(CommonUtils.getDishCount(DishFrg.list) + "");
            tvPricePop.setText("您还没有点菜哦");
            btnCheckoutPop.setText("满" + lessPrice + "元起订");
            btnCheckoutPop.setBackgroundResource(R.color.c4);
        }
    }

    private void generateOrder()
    {
        if (DishFrg.dishList != null && DishFrg.dishList.size() != 0)
        {
            listDishBean = new ArrayList<>();
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
        /*OrderBean.getOrderBeanInstance().setFewDishes(String.valueOf(DishFrg.adapterRight.totalNum));
        OrderBean.getOrderBeanInstance().setOrderCos1(String.valueOf(DishFrg.adapterRight.totalPrice));
        OrderBean.getOrderBeanInstance().setOrderCos2(String.valueOf(DishFrg.adapterRight.totalPrice));*/
        // 修改静态变量之后
        OrderBean.getOrderBeanInstance().setFewDishes(String.valueOf(CommonUtils.getDishCount(DishFrg.list)));
        OrderBean.getOrderBeanInstance().setOrderCos1(String.valueOf(CommonUtils.getDishPrice(DishFrg.list)));
        OrderBean.getOrderBeanInstance().setOrderCos2(String.valueOf(CommonUtils.getDishPrice(DishFrg.list)));
        Logger.t(TAG).d("OrderSearchAct:" + OrderBean.getOrderBeanInstance().getOrderCos2());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
//        Logger.t(TAG).d("请求码："+requestCode+"结果码："+requestCode+"data " +data.getStringExtra("result"));
        switch (requestCode)
        {
            case EamConstant.EAM_CONFIRM_ORDER_REQUEST_CODE:
                switch (resultCode)
                {
                    case EamConstant.EAM_RESULT_NO:
                        if (data != null)
                        {
                            String result = data.getStringExtra("result");
                            if (result != null && result.equals("back"))
                            {
                                topBar.getLeftButton2().setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                }
                break;
        }
    }

    @Override
    public void getDishListCallback(List<OrderedDishItemBean> response)
    {
        if (null != tempOrderList)
        {
            tempOrderList.clear();
        }
        if (response != null && response.size() > 0)
        {
            for (int i = 0; i < response.size(); i++)
            {
                for (int j = 0; j < dishList.size(); j++)
                {
                    if (response.get(i).getDishId().equals(dishList.get(j).getDishId()))
                    {
                        response.get(i).setDishNum(dishList.get(j).getDishNum());
                    }
                }
            }
            tempOrderList.addAll(response);
            rlBottom.setVisibility(View.VISIBLE);
        } else
        {
            rlBottom.setVisibility(View.GONE);
            ToastUtils.showShort("暂无搜索内容");
        }
        adapter.notifyDataSetChanged();
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {

    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        NetHelper.handleNetError(DOrderSearchAct.this, null, interfaceName, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected ImpDOrderSearchView createPresenter()
    {
        return new ImpDOrderSearchView();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (!TextUtils.isEmpty(index) && index.equals("search"))
        {
            Intent intent = new Intent(mActivity, DOrderMealDetailAct.class);
            intent.putExtra("index", 1);
            intent.putExtra("fromFoodDetail",true);
            startActivity(intent);
            finish();
        } else
        {
            DOrderSearchAct.this.finish();
        }
    }
}

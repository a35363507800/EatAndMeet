package com.echoesnet.eatandmeet.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eam.icontextmodule.model.EchoesEamIcon;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.fragments.DishFrg;
import com.echoesnet.eatandmeet.models.bean.DishBean;
import com.echoesnet.eatandmeet.models.bean.DishRightMenuGroupBean;
import com.echoesnet.eatandmeet.models.bean.OrderBean;
import com.echoesnet.eatandmeet.models.bean.OrderedDishItemBean;
import com.echoesnet.eatandmeet.models.bean.PayBean;
import com.echoesnet.eatandmeet.models.bean.PayMetadataBean;
import com.echoesnet.eatandmeet.presenters.ImpIDOrderConfirmView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDOrderConfirmView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.payUtil.IPayCancelListener;
import com.echoesnet.eatandmeet.utils.payUtil.IPayFinishedListener;
import com.echoesnet.eatandmeet.utils.payUtil.PayHelper;
import com.echoesnet.eatandmeet.views.adapters.ExpandOrderDishAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.OrderAffirmDialog;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.echoesnet.eatandmeet.views.widgets.WheelView;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import com.pingplusplus.android.Pingpp;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DOrderConfirmAct extends MVPBaseActivity<IDOrderConfirmView, ImpIDOrderConfirmView> implements IDOrderConfirmView
{
    //region 变量
    public final static String TAG = DOrderConfirmAct.class.getSimpleName();
    private static final int BIND_OR_QUERY = 101;
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.wheelview)
    WheelView wheelview;
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.lv_menu)
    ListView lvMenu;
    @BindView(R.id.iv_more_icon)
    ImageView ivMoreIcon;
    @BindView(R.id.arl_stop)
    AutoRelativeLayout arlStop;
    @BindView(R.id.tv_stop)
    TextView tvStop;
    @BindView(R.id.btn_checkout)
    Button btnCheckout;
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.tv_few_dish)
    TextView tvFewDish;
    @BindView(R.id.tv_shop_name)
    TextView tvResName;
    @BindView(R.id.tv_order_time)
    TextView tvOrderTime;
    @BindView(R.id.tv_userinfos)
    TextView tvUserInfo;
    @BindView(R.id.tv_userphone)
    TextView tvUserPhone;
    @BindView(R.id.tv_price_content)
    TextView tvTotalCost;
    @BindView(R.id.tv_hui_price)
    TextView tvHuiPrice;
    @BindView(R.id.tv_order_seatnumber)
    TextView tvOrderSeatNumber;
    @BindView(R.id.view_left)
    View viewLeft;
    @BindView(R.id.view_right)
    View viewRight;
    //菜单备注
    @BindView(R.id.et_order_note)
    EditText etOrderNote;

    @BindView(R.id.afl_no_recommed_host)
    AutoLinearLayout aflNoRecommedHost;
    @BindView(R.id.afl_has_recommed_host)
    AutoFrameLayout aflHasRecommedHost;
    @BindView(R.id.rtv_host_head)
    RoundedImageView rtvHostHead;
    @BindView(R.id.tv_host_name)
    TextView tvHostName;
    @BindView(R.id.itv_clear_host)
    TextView itvClearHost;

    @BindView(R.id.btn_bind_ok)
    Button btnBindOk;
    @BindView(R.id.et_anchor_id)
    EditText etAnchorId;
    @BindView(R.id.itv_ok)
    IconTextView itvOk;


    private Activity mContext;
    private Dialog pDialog;
    private String orderId = "";
    private String finalCost;

    ExpandOrderDishAdapter mFootviewAdapter;
    private Double price;
    private int sitTotalNum;
    private boolean isSelectTableConflict = false;
    private OrderAffirmDialog orderAffirmDialog;

    //region 主播推荐就餐相关变量
    //private String recommendHostUId;
    private String uphUrl = "";
    private String userName = "";
    private String id = "";
    private String uId = "";
    //endregion


    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            OrderBean oBean = savedInstanceState.getParcelable("orderBean");
            if (oBean != null)
                OrderBean.setOrderBean(oBean);
        }
        setContentView(R.layout.act_confirm_order);
        ButterKnife.bind(this);
        initAfterView();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //防止中途完成其他操作，覆盖原来的监听器
        PayHelper.clearPayHelperListeners();
        //支付完成回调
        PayHelper.setIPayFinishedListener(new PayFinish(DOrderConfirmAct.this));
        //支付取消回调
        PayHelper.setIPayCancelListener(new PayCanceled(DOrderConfirmAct.this));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable("orderBean", OrderBean.getOrderBeanInstance());
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
        //如果桌子被占了重新去选择桌子，则不销毁
        if (!isSelectTableConflict)
        {
            //当订单确认页关闭时，销毁订单实例
            OrderBean.destroyOrderBeanInstance();
            if (DishFrg.adapterRight != null)
                DishFrg.adapterRight.notifyDataSetChanged();
        }
        PayHelper.clearPopupWindows();
    }

    @Override
    protected ImpIDOrderConfirmView createPresenter()
    {
        return new ImpIDOrderConfirmView();
    }


    void initAfterView()
    {
        mContext = this;
        topBar.setTitle(getResources().getString(R.string.order_confirm_title));
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                mContext.finish();
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
        pDialog = DialogUtil.getCommonDialog(this, "正在处理...");
        pDialog.setCancelable(false);
        etAnchorId.addTextChangedListener(watcher);
        if (mPresenter != null)
            mPresenter.getMyConsultant();
        setUiContent();
    }

    private void setUiContent()
    {
        String testSits = OrderBean.getOrderBeanInstance().getSits();
        if (testSits != null)
        {
            String[] sitArr = testSits.split(CommonUtils.SEPARATOR);
            if (sitArr.length == 3)
            {
                int sitNum1 = Integer.parseInt(sitArr[2]);
                sitTotalNum = sitNum1;
            }
            else if (sitArr.length > 5)
            {
                int sitNum2 = Integer.parseInt(sitArr[2]) + Integer.parseInt(sitArr[5]);
                sitTotalNum = sitNum2;
            }
        }
        tvHuiPrice.setText(OrderBean.getOrderBeanInstance().getDiscount());
        tvResName.setText(OrderBean.getOrderBeanInstance().getrName());
        tvOrderTime.setText(OrderBean.getOrderBeanInstance().getOrderTime());
        tvOrderSeatNumber.setText(OrderBean.getOrderBeanInstance().getSitsName());
        tvFewDish.setText("订单 ( 共 " + OrderBean.getOrderBeanInstance().getFewDishes() + " 道菜 )");
        tvTotalPrice.setText("￥" + CommonUtils.keep2Decimal(Double.parseDouble(OrderBean.getOrderBeanInstance().getOrderCos1())));
        tvUserPhone.setText("手机号: " + OrderBean.getOrderBeanInstance().getMobile());
        tvUserInfo.setText("预订人: " + OrderBean.getOrderBeanInstance().getNicName() + " " + OrderBean.getOrderBeanInstance().getSex());
        tvTotalCost.setText("￥" + CommonUtils.keep2Decimal(Double.parseDouble(OrderBean.getOrderBeanInstance().getOrderCos2())));
        //将最终需要支付的金额赋值给finalCost;
        finalCost = OrderBean.getOrderBeanInstance().getOrderCos2();
        Logger.t(TAG).d("finalCost:" + finalCost);
        tvResult.setText("￥" + CommonUtils.keep2Decimal(Double.parseDouble(OrderBean.getOrderBeanInstance().getOrderCos2()) / sitTotalNum));

        mFootviewAdapter = new ExpandOrderDishAdapter(this, lvMenu, tvStop, arlStop);
        lvMenu.setAdapter(mFootviewAdapter);
        mFootviewAdapter.setAdapterData(OrderBean.getOrderBeanInstance().getDishBeen());
        if (tvStop.getVisibility() != View.VISIBLE)
        {
            viewLeft.setVisibility(View.GONE);
            viewRight.setVisibility(View.GONE);
        }
        ivMoreIcon.setImageDrawable(new IconDrawable(this, EchoesEamIcon.eam_e909).colorRes(R.color.c15));
        price = Double.parseDouble(OrderBean.getOrderBeanInstance().getOrderCos2());
        setPerCostCalculator();
    }


    @OnClick({R.id.btn_checkout, R.id.afl_no_recommed_host, R.id.itv_clear_host, R.id.btn_bind_ok, R.id.itv_ok})
    void clickEvent(final View view)
    {
        switch (view.getId())
        {
            //提交订单
            case R.id.btn_checkout:
                if (CommonUtils.isFastDoubleClick())
                    return;
                if (mPresenter != null)
                {
                    if (pDialog != null && !pDialog.isShowing())
                    {
                        pDialog.show();
                    }
                    mPresenter.checkPrice();
                    OrderBean.getOrderBeanInstance().setRemark(etOrderNote.getText().toString());
                }
                break;
            case R.id.itv_clear_host:
                this.id = "";
                this.uphUrl = "";
                this.userName = "";
                this.uId = "";
                aflNoRecommedHost.setVisibility(View.VISIBLE);
                aflHasRecommedHost.setVisibility(View.GONE);
                break;
            case R.id.btn_bind_ok:
                if (mPresenter != null)
                    mPresenter.queryConsultant(etAnchorId.getText().toString().trim());
                break;
            case R.id.itv_ok:
/*                if (itvOk.getTag().equals("yes"))
                {
                    break;
                }*/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(mContext,
                                new String[]{Manifest.permission.CAMERA}, 1);
                    }
                    else
                    {
                        Logger.t(TAG).d("相机权限已经被授予");
                        Intent intent = new Intent(mContext, CaptureActivity.class);
                        startActivityForResult(intent, BIND_OR_QUERY);
                        CommonUtils.jumpHelperId = "1";
                    }
                }
                else
                {
                    boolean hasCameraPermission = CommonUtils.cameraIsCanUse();
                    if (hasCameraPermission)
                    {
                        Intent intent = new Intent(mContext, CaptureActivity.class);
                        startActivityForResult(intent, BIND_OR_QUERY);
                        CommonUtils.jumpHelperId = "1";
                    }
                    else
                    {
                        ToastUtils.showShort("请开启相机功能");
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 设置人均消费计算器
     */
    private void setPerCostCalculator()
    {
        final DecimalFormat df = new DecimalFormat("#0.00");
        final List<String> items = new ArrayList<>();
        for (int i = 1; i <= 25; i++)
        {
            items.add(String.valueOf(i));
        }
        wheelview.setItems(items);
        wheelview.selectIndex(sitTotalNum - 1);
        wheelview.setAdditionCenterMark("人");
        wheelview.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener()
        {
            @Override
            public void onWheelItemSelected(WheelView wheelView, int position)
            {
                if (df != null)
                {
                    String result = df.format(price / Integer.parseInt(wheelView.getItems().get(position)));
                    tvResult.setText("￥" + result);
                }
            }

            @Override
            public void onWheelItemChanged(WheelView wheelView, int position)
            {
                if (df != null)
                {
                    String result = df.format(price / Integer.parseInt(wheelView.getItems().get(position)));
                    tvResult.setText("￥" + result);
                }
            }
        });
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //支付页面返回处理 Ping++回调
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                String result = data.getExtras().getString("pay_result");
                if (result.equals("success"))
                {
                    //PayHelper.clearPayHelperListeners();
                    PayHelper.setIPayFinishedListener(new PayFinish(this));
                    PayHelper.thirdPartyPayStateCheck2((Activity) mContext, new PayMetadataBean("", "", "", "0"));
                    Logger.t(TAG).d("销毁dateStreamId");
//                    EamApplication.getInstance().dateStreamId="noDate";
                }
                else if (result.equals("cancel"))
                {
                    //OrderBean.getOrderBeanInstance().setOrderCos2("");
                    ToastUtils.showShort("支付取消");
                }
                else
                {
                    ToastUtils.showShort("支付失败, 请重试");
                }
            }
        }
        else if (requestCode == EamConstant.EAM_ORDER_DETAIL_REQUEST_CODE)
        {
            switch (resultCode)
            {
                case EamConstant.EAM_RESULT_NO:
                    mContext.finish();
                    break;
            }
        }
        switch (requestCode)
        {
            case BIND_OR_QUERY:
                if (resultCode == RESULT_OK)
                {
                    CommonUtils.jumpHelperId = "-1";
                    this.uId = data.getStringExtra("consultant");
                    this.id = data.getStringExtra("consultantId");
                    this.uphUrl = data.getStringExtra("consultantPhUrl");
                    this.userName = data.getStringExtra("consultantName");
                    etAnchorId.setText("");
                    aflNoRecommedHost.setVisibility(View.GONE);
                    aflHasRecommedHost.setVisibility(View.VISIBLE);
                    GlideApp.with(mContext)
                            .asBitmap()
                            .load(this.uphUrl)
                            .placeholder(R.drawable.userhead)
                            .into(rtvHostHead);
                    tvHostName.setText(this.userName + "(" + this.id + ")");
/*                    itvOk.setText("{eam-e983}");
                    itvOk.setTag("yes");
                    itvOk.setTextColor(ContextCompat.getColor(mContext, R.color.C0315));*/
                    OrderBean.getOrderBeanInstance().setRecommendHostUid(uId);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody, String change)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.DishC_checkPrice:
                Logger.t(TAG).d("菜品价格有变化");
                List<DishBean> dishBeenList = new ArrayList<>();
                Logger.t(TAG).d("校验菜品有变化返回body--> " + errBody);
                try
                {
                    JSONObject jsonBody = new JSONObject(errBody);
                    String orderCos1 = jsonBody.getString("orderCos1");
                    String orderCos2 = jsonBody.getString("orderCos2");
                    Logger.t(TAG).d("菜品变化1--> " + orderCos1 + " , " + orderCos2);
                    String dish = jsonBody.getString("dish");
                    JSONArray jsonArray = new JSONArray(dish);
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        DishBean bean = new DishBean();
                        JSONObject temp = (JSONObject) jsonArray.get(i);
                        String dishAmount = temp.getString("dishAmount");
                        String dishId = temp.getString("dishId");
                        String dishName = temp.getString("dishName");
                        String dishPrice = temp.getString("dishPrice");
                        bean.setDishAmount(dishAmount);
                        bean.setDishId(dishId);
                        bean.setDishName(dishName);
                        bean.setDishPrice(dishPrice);
                        dishBeenList.add(bean);
                    }
                    // 获取新的菜品信息、更新UI
                    mFootviewAdapter = new ExpandOrderDishAdapter(DOrderConfirmAct.this, lvMenu, tvStop, arlStop);
                    lvMenu.setAdapter(mFootviewAdapter);
                    mFootviewAdapter.setAdapterData(dishBeenList);
                    tvTotalPrice.setText("￥" + CommonUtils.keep2Decimal(Double.parseDouble(orderCos1)));
                    tvTotalCost.setText("￥" + CommonUtils.keep2Decimal(Double.parseDouble(orderCos2)));
                    Logger.t(TAG).d("hehehehe" + orderCos1 + orderCos2);
                    OrderBean.getOrderBeanInstance().setOrderCos1(orderCos1);
                    OrderBean.getOrderBeanInstance().setOrderCos2(orderCos2);
                    Logger.t(TAG).d("OrderConfirmAct:" + OrderBean.getOrderBeanInstance().getOrderCos2());
                    OrderBean.getOrderBeanInstance().setDishBeen(dishBeenList);
                    // 菜品变化 首先弹出菜价变更对话框
                    new CustomAlertDialog(DOrderConfirmAct.this)
                            .builder()
                            .setTitle("提示")
                            .setMsg("您的订单金额发生变化, 金额: ￥" +
                                    CommonUtils.keep2Decimal(Double.parseDouble(OrderBean.getOrderBeanInstance().getOrderCos2()))
                                    + ",\n是否继续下单?")
                            .setPositiveButton("继续下单", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    postOrderOperation("1");
                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                }
                            }).show();
                } catch (JSONException e)
                {
                    e.printStackTrace();
                } finally
                {
                }

                break;
            case NetInterfaceConstant.OrderC_smtOrder:
                Logger.t(TAG).d("错误码为：%s", code);
                if (ErrorCodeTable.parseErrorCode(code).equals("桌子已被预订"))
                {
                    isSelectTableConflict = true;
                    new CustomAlertDialog(DOrderConfirmAct.this)
                            .builder()
                            .setTitle("提示")
                            .setMsg("您选的桌子已被预定，请重新选择？")
                            .setPositiveButton("确定", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    //已经点菜
                                    OrderBean.getOrderBeanInstance().setType("2");  // 点菜
                                    Intent intent = new Intent(mContext, SelectTableAct.class);
                                    intent.putExtra("restId", OrderBean.getOrderBeanInstance().getrId());
                                    intent.putExtra("resName", OrderBean.getOrderBeanInstance().getrName());
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    return;
                                }
                            }).show();
                    //ToastUtils.showShort(mContext, "桌子已经被预订，请重新选择");
                }
                else
                {
                    orderId = "";
                    //订单提交失败后，需要重新提交
                    new CustomAlertDialog(DOrderConfirmAct.this)
                            .builder()
                            .setTitle("提示")
                            .setMsg("订单提交失败，请重新提交")
                            .setPositiveButton("重新提交", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    if (mPresenter != null)
                                        mPresenter.postOrderToServer(v, change, OrderBean.getOrderBeanInstance(), uId);
                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                }
                            }).show();
                }
                break;
            case NetInterfaceConstant.OrderC_receiveSmtOrder:
                Logger.t(TAG).d("错误码为：%s", code);
                if (ErrorCodeTable.parseErrorCode(code).equals("桌子已被预订"))
                {
                    isSelectTableConflict = true;
                    new CustomAlertDialog(DOrderConfirmAct.this)
                            .builder()
                            .setTitle("提示")
                            .setMsg("您选的桌子已被预定，请重新选择？")
                            .setPositiveButton("确定", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    //已经点菜
                                    OrderBean.getOrderBeanInstance().setType("2");  // 点菜
                                    Intent intent = new Intent(mContext, SelectTableAct.class);
                                    intent.putExtra("restId", OrderBean.getOrderBeanInstance().getrId());
                                    intent.putExtra("resName", OrderBean.getOrderBeanInstance().getrName());
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    return;
                                }
                            }).show();
                    //ToastUtils.showShort(mContext, "桌子已经被预订，请重新选择");
                }else
                {
                    orderId = "";
                    //订单提交失败后，需要重新提交
                    new CustomAlertDialog(DOrderConfirmAct.this)
                            .builder()
                            .setTitle("提示")
                            .setMsg("订单提交失败，请重新提交")
                            .setPositiveButton("重新提交", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    mPresenter.postOrderToServer2(v, change, OrderBean.getOrderBeanInstance(), EamApplication.getInstance().dateStreamId, uId);
                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                }
                            }).show();
                }
                break;
            case NetInterfaceConstant.ConsultantC_myConsultant:
                itvOk.setText("{eam-e987}");//二维码
                itvOk.setTag("no");
                itvOk.setTextColor(ContextCompat.getColor(mContext, R.color.C0316));
                Logger.t(TAG).d("错误码为：%s", code);
                break;
            default:
                break;

        }
    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {

    }

    @Override
    public void checkPriceCallback(String response)
    {
        Logger.t(TAG).d("检查菜品是否变化--> " + response);
        if (TextUtils.isEmpty(response))
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
            return;
        }
        else
        {
            Logger.t(TAG).d("菜品价格无变化");
            if ("noDate".equals(EamApplication.getInstance().dateStreamId))
            {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMdd");
                try
                {
                    mPresenter.orderCheck(simpleDateFormat1.format(simpleDateFormat.parse(OrderBean.getOrderBeanInstance().getOrderTime())));
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                postOrderOperation("0");
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }

        }
    }

    @Override
    public void postOrderToServerCallback(String response, final View view, final String dishPriceChanged)
    {
        Logger.t(TAG).d("订单接口返回信息" + response);
        try
        {
            orderId = response;
            ToastUtils.showShort("订单提交成功");
            //订单提交成功，将菜品列表清空，回复初始状态
            for (DishRightMenuGroupBean drm : DishFrg.list)
            {
                for (OrderedDishItemBean orderedDish : drm.getList())
                {
                    orderedDish.setDishNum(0);
                }
            }
            PayHelper.clearPayHelperListeners();
            //支付完成回调
            PayHelper.setIPayFinishedListener(new PayFinish(DOrderConfirmAct.this));
            //支付取消回调
            PayHelper.setIPayCancelListener(new PayCanceled(DOrderConfirmAct.this));
            //订单提交成功后才打开付款
            PayBean payBean = new PayBean();
            payBean.setOrderId(orderId);
            payBean.setAmount(finalCost);
            payBean.setSubject("看脸吃饭App订单");               // 商品的标题
            payBean.setBody("点餐订单支付");               // 商品的描述信息
            PayHelper.payOrder(view.getRootView(), payBean, (Activity) mContext, new PayMetadataBean("", "", "", "0"));

        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    @Override
    public void postOrderToServerCallback2(String response, final View view, final String change)
    {
        Logger.t(TAG).d("订单接口返回信息--约会订单" + response);
        try
        {
            orderId = response;
            ToastUtils.showShort("订单提交成功");
            //订单提交成功，将菜品列表清空，回复初始状态
            for (DishRightMenuGroupBean drm : DishFrg.list)
            {
                for (OrderedDishItemBean orderedDish : drm.getList())
                {
                    orderedDish.setDishNum(0);
                }
            }
            PayHelper.clearPayHelperListeners();
            //支付完成回调
            PayHelper.setIPayFinishedListener(new PayFinish(DOrderConfirmAct.this));
            //支付取消回调
            PayHelper.setIPayCancelListener(new PayCanceled(DOrderConfirmAct.this));
            //订单提交成功后才打开付款
            PayBean payBean = new PayBean();
            payBean.setOrderId(orderId);
            payBean.setAmount(finalCost);
            payBean.setSubject("看脸吃饭App订单");               // 商品的标题
            payBean.setBody("点餐订单支付");               // 商品的描述信息
            PayHelper.payOrder(view.getRootView(), payBean, (Activity) mContext, new PayMetadataBean("", "", "", "0"));

        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            pDialog.dismiss();
        }
    }

    @Override
    public void queryMyConsultantCallback(String response)
    {
        try
        {
            JSONObject body = new JSONObject(response);
            String consultant = body.getString("consultant");
            String consultantId = body.getString("consultantId");
            String consultantName = body.getString("consultantName");
            String consultantPhUrl = body.getString("consultantPhurl");
            this.uId = consultant;
            this.id = consultantId;
            this.uphUrl = consultantPhUrl;
            this.userName = consultantName;
            etAnchorId.setText("");
            aflNoRecommedHost.setVisibility(View.GONE);
            aflHasRecommedHost.setVisibility(View.VISIBLE);
            GlideApp.with(mContext)
                    .asBitmap()
                    .load(consultantPhUrl)
                    .placeholder(R.drawable.userhead)
                    .into(rtvHostHead);
            tvHostName.setText(consultantName + "(" + consultantId + ")");
            //itvOk.setText("{eam-e983}");
            //itvOk.setTag("yes");
            //itvOk.setTextColor(ContextCompat.getColor(mContext, R.color.C0315));

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void getMyConsultantCallback(String response)
    {
        try
        {
            JSONObject body = new JSONObject(response);
            String consultant = body.getString("consultant");
            String consultantId = body.getString("consultantId");
            String consultantName = body.getString("consultantName");
            String consultantPhUrl = body.getString("consultantPhurl");
            this.uId = consultant;
            this.id = consultantId;
            this.uphUrl = consultantPhUrl;
            this.userName = consultantName;
            aflNoRecommedHost.setVisibility(View.GONE);
            aflHasRecommedHost.setVisibility(View.VISIBLE);
            GlideApp.with(mContext)
                    .asBitmap()
                    .load(consultantPhUrl)
                    .placeholder(R.drawable.userhead)
                    .into(rtvHostHead);
            tvHostName.setText(consultantName + "(" + consultantId + ")");
            itvOk.setText("{eam-e983}");// 对号
            itvOk.setEnabled(false);
            //itvOk.setTag("yes");
            itvOk.setTextColor(ContextCompat.getColor(mContext, R.color.C0315));
            itvClearHost.setVisibility(View.GONE);//--去掉取消按钮 wb

        } catch (JSONException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        }
    }

    @Override
    public void orderCheckCallback(String response, String date)
    {
        Logger.t(TAG).d("确认订单返回" + response);
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            if ("1".equals(jsonObject.getString("appointment")))
            {
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
                if (mContext.isFinishing())
                    return;
                if (orderAffirmDialog == null)
                    orderAffirmDialog = new OrderAffirmDialog();
                orderAffirmDialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), TAG, jsonObject.getString("appointmentList"));
                orderAffirmDialog.setOrderDate(date);
                orderAffirmDialog.setOrderAffirmListener(new OrderAffirmDialog.OrderAffirmListener()
                {
                    @Override
                    public void normalOrder()
                    {
                        EamApplication.getInstance().dateStreamId = "noDate";
                        Logger.t(TAG).d("普通订单");
                        postOrderOperation("0");
                        if (pDialog != null && pDialog.isShowing())
                            pDialog.dismiss();
                    }

                    @Override
                    public void bootyCallOrder(String sId)
                    {
                        Logger.t(TAG).d("约吃饭订单");
                        EamApplication.getInstance().dateStreamId = sId;
                        postOrderOperation("0");
                        if (pDialog != null && pDialog.isShowing())
                            pDialog.dismiss();
                    }
                });
            }
            else
            {
                postOrderOperation("0");
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void postOrderOperation(String dishPriceChanged)
    {
        if (EamApplication.getInstance().dateStreamId.equals("noDate"))
        {
            if (mPresenter != null)
            {
                SharePreUtils.setOrderType(mContext, "normalType");
                Logger.t(TAG).d("正常下单记录订单类型--> " + SharePreUtils.getOrderType(mContext));
                mPresenter.postOrderToServer(btnCheckout, dishPriceChanged, OrderBean.getOrderBeanInstance(), this.uId);
            }
        }
        else
        {
            if (mPresenter != null)
            {
                SharePreUtils.setOrderType(mContext, "dateType");
                Logger.t(TAG).d("约会下单记录订单类型--> " + SharePreUtils.getOrderType(mContext));
                mPresenter.postOrderToServer2(btnCheckout, dishPriceChanged, OrderBean.getOrderBeanInstance(), EamApplication.getInstance().dateStreamId, uId);
            }
        }
    }


    private static class PayFinish implements IPayFinishedListener
    {
        private final WeakReference<DOrderConfirmAct> mActRef;

        private PayFinish(DOrderConfirmAct mAct)
        {
            this.mActRef = new WeakReference<DOrderConfirmAct>(mAct);
        }

        @Override
        public void PayFinished(String orderId, String streamId, String payType)
        {
            final DOrderConfirmAct cAct = mActRef.get();
            if (cAct != null)
            {
                PayHelper.clearPopupWindows();
                Intent intent = new Intent(cAct, DPayOrderSuccessAct.class);
                intent.putExtra("orderId", orderId);
                cAct.startActivity(intent);
                cAct.finish();
            }
        }

        @Override
        public void PayFailed(String orderId, String streamId, String payType)
        {
            final DOrderConfirmAct cAct = mActRef.get();
            if (cAct != null)
            {
                PayHelper.clearPopupWindows();
                ToastUtils.showLong("由于网络原因没有获得支付结果，请确认是否扣款且订单是否支付成功，如若不正常请与客服联系");
                Intent intent1 = new Intent(cAct, DOrderRecordDetail.class);
                intent1.putExtra("orderId", orderId);
                cAct.startActivity(intent1);
                cAct.finish();
            }
        }
    }

    private static class PayCanceled implements IPayCancelListener
    {
        private final WeakReference<DOrderConfirmAct> mActRef;

        private PayCanceled(DOrderConfirmAct mAct)
        {
            this.mActRef = new WeakReference<DOrderConfirmAct>(mAct);
        }

        @Override
        public void payCanceled()
        {
            final DOrderConfirmAct cAct = mActRef.get();
            if (cAct != null)
            {
                ToastUtils.showShort("取消支付成功");
                //点击取消按钮，打开订单详情，订单详情返回则需要跳过确认订单界面，返回前面的页面
                Intent intent = new Intent(cAct, DOrderRecordDetail.class);
                intent.putExtra("orderId", cAct.orderId);
                intent.putExtra(EamConstant.EAM_ORDER_DETAIL_OPEN_SOURCE, "pay");
                if (SharePreUtils.getToOrderMeal(cAct).equals("toOrderMeal"))
                {
                    SharePreUtils.setOrderType(cAct, "dateType");
                }
                else
                {
                    SharePreUtils.setOrderType(cAct, "normalType");
                }

                cAct.startActivity(intent);
                cAct.finish();
            }
        }
    }

    TextWatcher watcher = new TextWatcher()
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
            if (s.length() > 0)
                btnBindOk.setVisibility(View.VISIBLE);
            else
                btnBindOk.setVisibility(View.GONE);
        }
    };
}

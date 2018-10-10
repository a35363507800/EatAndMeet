package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.OrderRecordBean;
import com.echoesnet.eatandmeet.models.bean.PayMetadataBean;
import com.echoesnet.eatandmeet.presenters.ImpIAllOrdersView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IAllOrdersView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.LoadFootView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.payUtil.IPayFinishedListener;
import com.echoesnet.eatandmeet.utils.payUtil.PayHelper;
import com.echoesnet.eatandmeet.views.adapters.OrderRecordLstAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;
import com.pingplusplus.android.Pingpp;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class MyNormalOrderShowAct extends BaseActivity implements IAllOrdersView
{
    private static final String TAG = MyNormalOrderShowAct.class.getSimpleName();
    private static final String PAGE_COUNT = "10";

    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.ptfl_lstview)
    PullToRefreshListView mPullToRefreshListview;

    private List<OrderRecordBean> dataSource;
    private OrderRecordLstAdapter orderRecordLstAdapter;
    //private boolean isFirstLoad=true;

    private Activity mActivity;
    private Dialog pDialog;
    //查询订单的类型 订单状态（0：待付款1：待使用2：待评价 quick：闪付all:全部）
    private String getOrderType;
    private String btnOnOff;
    private ImpIAllOrdersView impIAllOrdersView;
    // 没有更多内容时添加的底部提示布局
    private View footView;
    // 再没有更多数据获取时,禁止列表上拉加载动作
    private boolean pullMove = true;
    private ListView actulListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_normal_order_show);
        ButterKnife.bind(this);
        afterViews();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //如果没有则加载，有则刷新
        if (dataSource != null && dataSource.size() == 0)
        {
            impIAllOrdersView.getAllOrders("0", PAGE_COUNT, getOrderType, "add");
        } else
        {
            impIAllOrdersView.getAllOrders("0", String.valueOf(dataSource.size()), getOrderType, "refresh");
        }
        impIAllOrdersView.getBtnOnOff();
    }

    private void afterViews()
    {
        mActivity = this;
        impIAllOrdersView = new ImpIAllOrdersView(mActivity, this);
        getOrderType = getIntent().getStringExtra("orderType");
        String titleText = "";
        switch (getOrderType)
        {
            case "0":
                titleText = "待付款";
                break;
            case "1":
                titleText = "待使用";
                break;
            case "2":
                titleText = "待评价";
                break;
        }
        topBar.setTitle(titleText);
        topBar.getLeftButton().setVisibility(View.VISIBLE);
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                mActivity.finish();
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
        footView = LayoutInflater.from(mActivity).inflate(R.layout.footview_normal_list, null);
        mPullToRefreshListview.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //下拉刷新
                impIAllOrdersView.getAllOrders("0", String.valueOf(dataSource.size()), getOrderType, "refresh");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //上拉加载
                if (impIAllOrdersView != null)
                {
                    impIAllOrdersView.getAllOrders(String.valueOf(dataSource.size()), PAGE_COUNT, getOrderType, "add");
                }
                LoadFootView.showFootView(actulListView, false, footView, null);
            }
        });

        mPullToRefreshListview.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener()
        {
            @Override
            public void onLastItemVisible()
            {
                // Toast.makeText(context, "上拉刷新", Toast.LENGTH_SHORT).show();
            }
        });
        actulListView = mPullToRefreshListview.getRefreshableView();
        //View empty= LayoutInflater.from(mActivity).inflate(R.layout.empty_view,null);
        View empty = findViewById(R.id.empty_view);
        ((TextView) empty.findViewById(R.id.tv_default_des)).setText("暂时没有获得订单，上拉加载试试");
        mPullToRefreshListview.setEmptyView(empty);
        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(actulListView);
        dataSource = new ArrayList<>();
        orderRecordLstAdapter = new OrderRecordLstAdapter(mActivity, dataSource);
        orderRecordLstAdapter.setImpIAllOrdersView(impIAllOrdersView, getOrderType);
        // mPullToRefreshListview.setAdapter(orderRecordLstAdapter);
        actulListView.setAdapter(orderRecordLstAdapter);
        orderRecordLstAdapter.setItemClickListener(new OrderRecordLstAdapter.IOnItemClickListener()
        {
            @Override
            public void OnItemClick(OrderRecordBean orderBean)
            {
                //点击查看订单详情，解决跳转BUG    ---yqh
                SharePreUtils.setSource(mActivity, "myInfo");
                Logger.t(TAG).d("设置myInfo");

                Intent intent;
                if (orderBean.getSource().equals("0"))
                {
                    intent = new Intent(mActivity, DOrderRecordDetail.class);
                } else
                {
                    intent = new Intent(mActivity, DQuickPayOrderDetailAct.class);
                }
                intent.putExtra("orderId", orderBean.getOrdId());
                mActivity.startActivity(intent);
            }
        });

        pDialog = DialogUtil.getCommonDialog(mActivity, "正在处理...");
        pDialog.setCancelable(false);
    }

/*    @ItemClick(R.id.ptfl_lstview)
    void onItemClick(OrderRecordBean orderRecordBean)
    {
        //点击查看订单详情，解决跳转BUG    ---yqh
        SharePreUtils.setSource(mActivity, "myInfo");
        Logger.t(TAG).d("设置myInfo");

        Intent intent;
        if (orderRecordBean.getSource().equals("0"))
        {
            intent = DOrderRecordDetail_.intent(mActivity).get();
        }
        else
        {
            intent = DQuickPayOrderDetailAct_.intent(mActivity).get();
        }
        intent.putExtra("orderId", orderRecordBean.getOrdId());
        mActivity.startActivity(intent);
    }*/

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        orderRecordLstAdapter.onDestroy();
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //支付页面返回处理 Ping++回调
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                String result = data.getExtras().getString("pay_result");
                Logger.t(TAG).d("ping++回调状态--> " + result);
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
//                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
//                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                if (result.equals("success"))
                {
                    //PayHelper.clearPayHelperListeners();
                    PayHelper.setIPayFinishedListener(new PayFinish(this));
                    PayHelper.thirdPartyPayStateCheck((Activity) mActivity, new PayMetadataBean("", "", "", "0"));
                } else if (result.equals("cancel"))
                {
                    //OrderBean.getOrderBeanInstance().setOrderCos2("");
                    ToastUtils.showShort( "支付取消");
                } else
                {
                    ToastUtils.showShort("支付失败, 请重试");
                }
            }
        } else if (requestCode == EamConstant.EAM_ORDER_DETAIL_REQUEST_CODE)
        {
            switch (resultCode)
            {
                case EamConstant.EAM_RESULT_NO:
                    Intent intent = new Intent();
                    intent.putExtra("result", "back");
                    mActivity.setResult(EamConstant.EAM_RESULT_NO, intent);
                    mActivity.finish();
                    break;
            }
        }
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.FriendPayC_btnOnOff:
                getBtnOnOffSuccess("1");
                break;
                default:
                    break;
        }
    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        NetHelper.handleNetError(mActivity, null, interfaceName, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }


    @Override
    public void getBtnOnOffSuccess(String btnOnOff)
    {
        this.btnOnOff = btnOnOff;
        orderRecordLstAdapter.setBtnOnOff(btnOnOff);
        orderRecordLstAdapter.notifyDataSetChanged();
    }

    @Override
    public void getOrderFail(Call call, Exception e, String exceptSource)
    {

    }

    @Override
    public void getOrderSuccess(List<OrderRecordBean> response, String operateType)
    {
        //下拉刷新
        if (operateType.equals("refresh"))
        {
            dataSource.clear();
            LoadFootView.showFootView(actulListView, false, footView, null);
            pullMove = true;
        }
        if (response.size() == 0)
        {
            LoadFootView.showFootView(actulListView, true, footView, null);
            pullMove = false;
        } else
        {
            dataSource.addAll(response);
            Logger.t(TAG).d("dataSource：" + dataSource.size());
            orderRecordLstAdapter.notifyDataSetChanged();
        }
/*                            //排序
                            Collections.sort(dataSource, new Comparator<OrderRecordBean>()
                            {
                                @Override
                                public int compare(OrderRecordBean lhs, OrderRecordBean rhs)
                                {
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    long l=0,r=0;
                                    try
                                    {
                                        l=df.parse(lhs.getSmtTime()).getTime();
                                        r=df.parse(rhs.getSmtTime()).getTime();

                                    } catch (ParseException e)
                                    {
                                        e.printStackTrace();
                                    }
                                    long result=r-l;
                                    if (result>0)
                                        return 1;
                                    else if (result<0)
                                        return -1;
                                    else
                                        return 0;
                                }
                            });*/

        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
        }
        if (mPullToRefreshListview != null)
            mPullToRefreshListview.onRefreshComplete();
        if (mPullToRefreshListview != null)
        {
            mPullToRefreshListview.onRefreshComplete();
            if (pullMove)
            {
                Logger.t(TAG).d("允许上下拉动"); // 恢复上拉 上下拉动
                mPullToRefreshListview.setMode(PullToRefreshBase.Mode.BOTH);
            } else
            {
                Logger.t(TAG).d("禁止上拉"); // 禁止上拉
                mPullToRefreshListview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
        }
    }

    private static class PayFinish implements IPayFinishedListener
    {
        private final WeakReference<MyNormalOrderShowAct> mActRef;

        private PayFinish(MyNormalOrderShowAct mAct)
        {
            this.mActRef = new WeakReference<MyNormalOrderShowAct>(mAct);
        }

        @Override
        public void PayFinished(String orderId, String streamId, String payType)
        {
            final MyNormalOrderShowAct cAct = mActRef.get();
            if (cAct != null)
            {
                Intent intent = new Intent(cAct, DPayOrderSuccessAct.class);
                intent.putExtra("orderId", orderId);
                cAct.startActivity(intent);
                cAct.finish();
            }
        }

        @Override
        public void PayFailed(String orderId, String streamId, String payType)
        {
            final MyNormalOrderShowAct cAct = mActRef.get();
            if (cAct != null)
            {
                ToastUtils.showLong("由于未知原因没有获得支付结果，请勿重复支付，尝试刷新页面");
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        orderRecordLstAdapter.onBackPressed();
    }
}

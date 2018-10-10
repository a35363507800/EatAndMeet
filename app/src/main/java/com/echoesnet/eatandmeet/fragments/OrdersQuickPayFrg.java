package com.echoesnet.eatandmeet.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.DOrderRecordDetail;
import com.echoesnet.eatandmeet.activities.DQuickPayOrderDetailAct;
import com.echoesnet.eatandmeet.listeners.IOnDOrderDeletedListener;
import com.echoesnet.eatandmeet.models.bean.OrderRecordBean;
import com.echoesnet.eatandmeet.presenters.ImpIAllOrdersView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IAllOrdersView;
import com.echoesnet.eatandmeet.utils.EmptyView;
import com.echoesnet.eatandmeet.utils.LoadFootView;
import com.echoesnet.eatandmeet.utils.LoadingProgressUtil;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.views.adapters.OrderRecordLstAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

public class OrdersQuickPayFrg extends BaseFragment implements IAllOrdersView
{
    private static final String TAG = OrdersQuickPayFrg.class.getSimpleName();
    private static final String PAGE_COUNT = "10";

    @BindView(R.id.ptfl_lstview)
    PullToRefreshListView mPullToRefreshListview;
    Unbinder unbinder;

    @BindView(R.id.loading_view)
    RelativeLayout loadingView;//加载布局

    private Activity mActivity;
    private List<OrderRecordBean> dataSource;
    private OrderRecordLstAdapter orderRecordLstAdapter;
    private ImpIAllOrdersView impIAllOrdersView;
    private IOnDOrderDeletedListener iOnDOrderDeletedListener;
    // 没有更多内容时添加的底部提示布局
    private View footView;
    // 再没有更多数据获取时,禁止列表上拉加载动作
    private boolean pullMove = true;
    private ListView actulListView;
    private String mParam1;

    public OrdersQuickPayFrg()
    {
        // Required empty public constructor
    }

    public static OrdersQuickPayFrg newInstance(String param)
    {
        OrdersQuickPayFrg fragment = new OrdersQuickPayFrg();
        Bundle args = new Bundle();
        args.putString("arg_param1", param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString("arg_param1");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frg_orders_quick_pay_frg, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        afterViews();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (dataSource != null && dataSource.size() == 0)
        {
            getAllOrders("0", PAGE_COUNT, "quick", "add", false);
        }
        else
        {
            getAllOrders("0", String.valueOf(dataSource.size()), "quick", "refresh", false);
        }
    }

    private void afterViews()
    {
        mActivity = getActivity();

        LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 0, null);
        impIAllOrdersView = new ImpIAllOrdersView(mActivity, this);
        iOnDOrderDeletedListener = new IOnDOrderDeletedListener()
        {
            @Override
            public void onOrderIsDeleted()
            {
//                orderRecordLstAdapter.notifyDataSetChanged();
                Logger.t(TAG).d("运行");
                getAllOrders("0", String.valueOf(dataSource.size()), "quick", "refresh", false);
            }
        };
        DOrderRecordDetail.setOnOrderDeletedListener(iOnDOrderDeletedListener);
        footView = LayoutInflater.from(mActivity).inflate(R.layout.footview_normal_list, null);
        mPullToRefreshListview.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //下拉刷新
                getAllOrders("0", String.valueOf(dataSource.size()), "quick", "refresh", true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //上拉加载
                if (impIAllOrdersView != null)
                {
                    getAllOrders(String.valueOf(dataSource.size()), PAGE_COUNT, "quick", "add", true);
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
        EmptyView emptyView = new EmptyView(mActivity);
        emptyView.setContent("您暂时还没有订单哦~");
        emptyView.setMoreContent("快去下单吧!");
        mPullToRefreshListview.setEmptyView(emptyView);
        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(actulListView);

        dataSource = new ArrayList<>();
        orderRecordLstAdapter = new OrderRecordLstAdapter(mActivity, dataSource);
        //mPullToRefreshListview.setAdapter(orderRecordLstAdapter);
        actulListView.setAdapter(orderRecordLstAdapter);
        orderRecordLstAdapter.setItemClickListener(new OrderRecordLstAdapter.IOnItemClickListener()
        {
            @Override
            public void OnItemClick(OrderRecordBean orderBean)
            {
                //点击查看订单详情，解决跳转BUG    ---yqh
                SharePreUtils.setSource(mActivity, "myInfo");
                Logger.t(TAG).d("设置myInfo");
                Intent intent = new Intent(mActivity, DQuickPayOrderDetailAct.class);
                intent.putExtra("orderId", orderBean.getOrdId());
                mActivity.startActivity(intent);
            }
        });
    }

/*    @ItemClick(R.id.ptfl_lstview)
    void onItemClick(OrderRecordBean orderRecordBean)
    {
        Intent intent = DQuickPayOrderDetailAct_.intent(mActivity).get();
        intent.putExtra("orderId", orderRecordBean.getOrdId());
        mActivity.startActivity(intent);
    }*/

    /**
     * 获取所有订单
     *
     * @param getItemStartIndex
     * @param getItemNum
     * @param orderType
     * @param operateType
     */
    private void getAllOrders(String getItemStartIndex, String getItemNum, String orderType, final String operateType, boolean isPullTrigger)
    {
        impIAllOrdersView.getAllOrders(getItemStartIndex, getItemNum, orderType, operateType);
    }

    @Override
    public void onDestroy()
    {
        DOrderRecordDetail.removeOnOrderDeletedListener();
        super.onDestroy();
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
        NetHelper.handleNetError(mActivity, null, TAG, e);
        if (mPullToRefreshListview != null)
            mPullToRefreshListview.onRefreshComplete();
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
    }


    @Override
    public void getBtnOnOffSuccess(String btnOnOff)
    {

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
        }
        else
        {
            dataSource.addAll(response);
            orderRecordLstAdapter.notifyDataSetChanged();
        }

        if (mPullToRefreshListview != null)
            mPullToRefreshListview.onRefreshComplete();


        LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
        if (mPullToRefreshListview != null)
        {
            mPullToRefreshListview.onRefreshComplete();
            if (pullMove)
            {
                Logger.t(TAG).d("允许上下拉动"); // 恢复上拉 上下拉动
                mPullToRefreshListview.setMode(PullToRefreshBase.Mode.BOTH);
            }
            else
            {
                Logger.t(TAG).d("禁止上拉"); // 禁止上拉
                mPullToRefreshListview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }
}

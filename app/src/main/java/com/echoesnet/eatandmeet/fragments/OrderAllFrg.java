package com.echoesnet.eatandmeet.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.ClubOrderRecordDetailAct;
import com.echoesnet.eatandmeet.activities.DOrderRecordDetail;
import com.echoesnet.eatandmeet.activities.MyOrdersAct;
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

public class OrderAllFrg extends BaseFragment implements IAllOrdersView
{
    private static final String TAG = OrderAllFrg.class.getSimpleName();
    private static final String PAGE_COUNT = "10";

    @BindView(R.id.ptfl_lstview1)
    PullToRefreshListView mPullToRefreshListview;
    @BindView(R.id.loading_view)
    RelativeLayout loadingView;//加载布局
    private Activity mActivity;
    private List<OrderRecordBean> dataSource;
    private OrderRecordLstAdapter orderRecordLstAdapter;
    private IOnDOrderDeletedListener iOnDOrderDeletedListener;
    private ImpIAllOrdersView impIAllOrdersView;
    // 没有更多内容时添加的底部提示布局
    private View footView;
    // 再没有更多数据获取时,禁止列表上拉加载动作
    private boolean pullMove = true;
    private ListView actulListView;
    private Unbinder unbinder;
    private String payType;

    public OrderAllFrg()
    {
        // Required empty public constructor
    }

    public static OrderAllFrg newInstance()
    {

        return new OrderAllFrg();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (impIAllOrdersView != null)
        {
            //如果没有则加载，有则刷新
            if (dataSource != null && dataSource.size() == 0)
            {
                impIAllOrdersView.getAllOrders("0", PAGE_COUNT, "all", "add");
            } else
            {
                impIAllOrdersView.getAllOrders("0", String.valueOf(dataSource.size()), "all", "refresh");
            }
            impIAllOrdersView.getBtnOnOff();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        DOrderRecordDetail.removeOnOrderDeletedListener();
        ClubOrderRecordDetailAct.removeOnOrderDeletedListener();
        orderRecordLstAdapter.onDestroy();

        LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.order_all_frg, container, false);
        unbinder = ButterKnife.bind(this, view);
        afterViews();
        return view;
    }

    private void afterViews()
    {
        mActivity = getActivity();
//        if (!mActivity.isFinishing())
//        {
//            pDialog = DialogUtil.getCommonDialog(mActivity, "正在处理...");
//            pDialog.setCancelable(false);
//        }
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 0, null);

        impIAllOrdersView = new ImpIAllOrdersView(mActivity, this);

        iOnDOrderDeletedListener = new IOnDOrderDeletedListener()
        {
            @Override
            public void onOrderIsDeleted()
            {

                Logger.t(TAG).d("运行");
                impIAllOrdersView.getAllOrders("0", String.valueOf(dataSource.size()), "all", "refresh");
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
                if (impIAllOrdersView != null)
                    impIAllOrdersView.getAllOrders("0", String.valueOf(dataSource.size()), "all", "refresh");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //上拉加载
                if (impIAllOrdersView != null)
                {
                    impIAllOrdersView.getAllOrders(String.valueOf(dataSource.size()), PAGE_COUNT, "all", "add");
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
        ClubOrderRecordDetailAct.setOnOrderDeletedListener(iOnDOrderDeletedListener);
        dataSource = new ArrayList<>();
        orderRecordLstAdapter = new OrderRecordLstAdapter(mActivity, dataSource);
        orderRecordLstAdapter.setImpIAllOrdersView(impIAllOrdersView, "all");
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
                if (orderBean.getHomeparty().equals("1"))
                {
                    Intent intent = new Intent(mActivity, ClubOrderRecordDetailAct.class);
                    intent.putExtra("orderId", orderBean.getOrdId());
                    mActivity.startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(mActivity, DOrderRecordDetail.class);
                    intent.putExtra("orderId", orderBean.getOrdId());
                    mActivity.startActivity(intent);
                }
            }
        });

//        if (!mActivity.isFinishing() && pDialog != null && !pDialog.isShowing())
//            pDialog.show();
//        LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 0, null);
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
//        if (!mActivity.isFinishing() && pDialog != null && pDialog.isShowing())
//            pDialog.dismiss();
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
    }


    @Override
    public void getBtnOnOffSuccess(String btnOnOff)
    {
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
            dataSource.clear();
        LoadFootView.showFootView(actulListView, false, footView, null);
        pullMove = true;

        if (response.size() == 0)
        {
            LoadFootView.showFootView(actulListView, true, footView, null);
            pullMove = false;
        } else
        {
            dataSource.addAll(response);
            orderRecordLstAdapter.notifyDataSetChanged();
        }


/*                            //排序
                            Collections.sort(dataSource, new Comparator<OrderRecordBean>()
                            {
                                @Override
                                public int compare(OrderRecordBean lhs, OrderRecordBean rhs)
                                {
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    long l = 0, r = 0;
                                    try
                                    {
                                        l = df.parse(lhs.getOrderTime()).getTime();
                                        r = df.parse(rhs.getOrderTime()).getTime();

                                    } catch (ParseException e)
                                    {
                                        e.printStackTrace();
                                    }
                                    long result = r - l;
                                    if (result > 0)
                                        return 1;
                                    else if (result < 0)
                                        return -1;
                                    else
                                        return 0;
                                }
                            });*/


//        if (!mActivity.isFinishing() && pDialog != null && pDialog.isShowing())
//            pDialog.dismiss();
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

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

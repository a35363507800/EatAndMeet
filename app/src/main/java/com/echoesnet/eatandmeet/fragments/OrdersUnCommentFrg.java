package com.echoesnet.eatandmeet.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.DOrderRecordDetail;
import com.echoesnet.eatandmeet.models.bean.OrderRecordBean;
import com.echoesnet.eatandmeet.presenters.ImpIAllOrdersView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IAllOrdersView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.views.adapters.OrderRecordLstAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

public class OrdersUnCommentFrg extends BaseFragment implements IAllOrdersView
{
    private static final String TAG = OrdersUnCommentFrg.class.getSimpleName();
    private static final String ARG_DATA_SOURCE = "dataSource";
    private static final String PAGE_COUNT = "10";

    @BindView(R.id.ptfl_lstview)
    PullToRefreshListView mPullToRefreshListview;
    Unbinder unbinder;

    private ListView actualListView;
    private Activity mActivity;
    private List<OrderRecordBean> dataSource;
    private OrderRecordLstAdapter orderRecordLstAdapter;
    private Dialog pDialog;
    private ImpIAllOrdersView impIAllOrdersView;

    public OrdersUnCommentFrg()
    {
        // Required empty public constructor
    }

    public static OrdersUnCommentFrg newInstance()
    {
        return new OrdersUnCommentFrg();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.orders_un_comment_frg, container, false);
        unbinder = ButterKnife.bind(this, view);
        afterViews();
        return view;
    }

    private void afterViews()
    {
        pDialog = DialogUtil.getCommonDialog(mActivity, "正在处理...");
        pDialog.setCancelable(true);
        impIAllOrdersView = new ImpIAllOrdersView(mActivity, this);
        mPullToRefreshListview.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //下拉刷新
                if (impIAllOrdersView != null)
                    impIAllOrdersView.getAllOrders("0", String.valueOf(dataSource.size()), "2", "refresh");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //上拉加载
                if (impIAllOrdersView != null)
                    impIAllOrdersView.getAllOrders(String.valueOf(dataSource.size()), PAGE_COUNT, "2", "add");
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
        actualListView = mPullToRefreshListview.getRefreshableView();
        View empty = LayoutInflater.from(mActivity).inflate(R.layout.empty_view, null);
        ((TextView) empty.findViewById(R.id.tv_default_des)).setText("暂时没有订单");
        mPullToRefreshListview.setEmptyView(empty);
        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(actualListView);

        dataSource = new ArrayList<>();
        orderRecordLstAdapter = new OrderRecordLstAdapter(mActivity, dataSource);
        mPullToRefreshListview.setAdapter(orderRecordLstAdapter);
        orderRecordLstAdapter.setItemClickListener(new OrderRecordLstAdapter.IOnItemClickListener()
        {
            @Override
            public void OnItemClick(OrderRecordBean orderBean)
            {
                //点击查看订单详情，解决跳转BUG    ---yqh
                SharePreUtils.setSource(mActivity, "myInfo");
                Logger.t(TAG).d("设置myInfo");
                Intent intent =new Intent(mActivity, DOrderRecordDetail.class);
                intent.putExtra("orderId", orderBean.getOrdId());
                mActivity.startActivity(intent);
            }
        });

        pDialog.show();
        impIAllOrdersView.getAllOrders("0", PAGE_COUNT, "2", "add");
    }
/*    @ItemClick(R.id.ptfl_lstview)
    void onItemClick(OrderRecordBean orderRecordBean)
    {
        Intent intent= DOrderRecordDetail_.intent(mActivity).get();
        intent.putExtra("orderId",orderRecordBean.getOrdId());
        mActivity.startActivity(intent);
    }*/

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
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
        NetHelper.handleNetError(mActivity, null, TAG, e);
        if (mPullToRefreshListview != null)
            mPullToRefreshListview.onRefreshComplete();
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
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
        }
        dataSource.addAll(response);
        //排序
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
        });
        orderRecordLstAdapter.notifyDataSetChanged();
        if (mPullToRefreshListview != null)
            mPullToRefreshListview.onRefreshComplete();

        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
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

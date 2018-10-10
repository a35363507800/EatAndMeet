package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.MyInfoOrderRemindBean;
import com.echoesnet.eatandmeet.models.bean.OrderRecordBean;
import com.echoesnet.eatandmeet.presenters.ImpIInfoOrderRemindView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IInfoOrderRemindView;
import com.echoesnet.eatandmeet.utils.LoadFootView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.MyInfoOrderRemindAdapter;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MMyInfoOrderRemindAct extends MVPBaseActivity<IInfoOrderRemindView,ImpIInfoOrderRemindView> implements IInfoOrderRemindView
{
    private static final String TAG = MMyInfoOrderRemindAct.class.getSimpleName();
    private final static String PAGE_COUNT = "20";

    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.ptfl_order_remind)
    PullToRefreshListView ptflOrderRemind;


    private Activity mContext;
    private List<MyInfoOrderRemindBean> list;
    private MyInfoOrderRemindAdapter adapter;
    private int mPosition;
    // 没有更多内容时添加的底部提示布局
    private View footView;
    // 再没有更多数据获取时,禁止列表上拉加载动作
    private boolean pullMove = true;
    private ListView actualListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_myinfo_order_remind);
        ButterKnife.bind(this);
        afterViews();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected ImpIInfoOrderRemindView createPresenter()
    {
        return new ImpIInfoOrderRemindView();
    }

    private void afterViews()
    {
        mContext = this;
        topBar.setTitle("订单提醒");
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

        list = new ArrayList<>();
        footView = LayoutInflater.from(mContext).inflate(R.layout.footview_normal_list, null);
        View empty = LayoutInflater.from(mContext).inflate(R.layout.empty_view, null);
        ((TextView) empty.findViewById(R.id.tv_default_des)).setText("暂时没有新消息");
        ptflOrderRemind.setEmptyView(empty);
        actualListView=ptflOrderRemind.getRefreshableView();
        adapter = new MyInfoOrderRemindAdapter(mContext, list);
        actualListView.setAdapter(adapter);

        if (mPresenter!=null)
            mPresenter.initData("0", PAGE_COUNT, true);


        ptflOrderRemind.setMode(PullToRefreshBase.Mode.BOTH);
        ptflOrderRemind.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //下拉刷新
                if (mPresenter != null)
                {
                    mPresenter.initData("0", String.valueOf(list.size()), true);
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //上拉加载
                if (mPresenter != null)
                {
                    mPresenter.initData(String.valueOf(list.size()), PAGE_COUNT, false);
                }
                LoadFootView.showFootView(actualListView,false,footView,null);
            }
        });

        ptflOrderRemind.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                mPosition = position - 1;
                // 产品说可能会有多种消息，不属于订餐的消息就不让他跳转
                MyInfoOrderRemindBean orderRemindBean = list.get(mPosition);
                if (orderRemindBean.getoId() != null && !orderRemindBean.getoId().equals("") && orderRemindBean.getMsg() != null && orderRemindBean.getMsg().contains("查看详情"))
                {
                    if (mPresenter != null)
                        mPresenter.getOrderDetail(list.get(mPosition).getoId());
                }
            }
        });

        ptflOrderRemind.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener()
        {
            @Override
            public void onLastItemVisible()
            {
                // Toast.makeText(context, "上拉刷新", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void getOrderDetailSuccess(String response)
    {
        Logger.t(TAG).d("获得实例--> " + response.toString());
            Intent intent = new Intent(mContext, DOrderRecordDetail.class);
            intent.putExtra("orderId", list.get(mPosition).getoId());
            intent.putExtra("orderRecord", new Gson().fromJson(response, OrderRecordBean.class));
            mContext.startActivity(intent);
    }


    @Override
    public void initDataSuccess(String request,boolean isDown)
    {
        ArrayList<MyInfoOrderRemindBean> myInfoOrderRemindList = new Gson().fromJson(request, new TypeToken< List<MyInfoOrderRemindBean>>(){}.getType());
        if (ptflOrderRemind != null)
            ptflOrderRemind.onRefreshComplete();
        if (myInfoOrderRemindList==null)
        {
            return;
        }

            if (myInfoOrderRemindList.size()==0)
            {
                LoadFootView.showFootView(actualListView, true, footView, null);
                pullMove = false;
            }
            if (isDown)
            {
                LoadFootView.showFootView(actualListView, false, footView, null);
                pullMove = true;
                list.clear();
            }
            list.addAll(myInfoOrderRemindList);
            adapter.notifyDataSetChanged();

        list.addAll(myInfoOrderRemindList);
        adapter.notifyDataSetChanged();

        if (ptflOrderRemind != null)
        {
            ptflOrderRemind.onRefreshComplete();
            if (pullMove)
            {
                Logger.t(TAG).d("允许上下拉动"); // 恢复上拉 上下拉动
                ptflOrderRemind.setMode(PullToRefreshBase.Mode.BOTH);
            }
            else
            {
                Logger.t(TAG).d("禁止上拉"); // 禁止上拉
                ptflOrderRemind.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
        }
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {

    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        if (ptflOrderRemind != null)
            ptflOrderRemind.onRefreshComplete();
    }
}

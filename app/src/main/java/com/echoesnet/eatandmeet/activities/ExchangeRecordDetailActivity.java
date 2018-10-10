package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.ExchangeRecordDetailBean;
import com.echoesnet.eatandmeet.presenters.ImpIExchangeMoneyDetailView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IExchangeMoneyDetailView;
import com.echoesnet.eatandmeet.utils.LoadFootView;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.views.adapters.MyExchangeMoneyDetailAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ExchangeRecordDetailActivity extends MVPBaseActivity<IExchangeMoneyDetailView, ImpIExchangeMoneyDetailView> implements IExchangeMoneyDetailView
{
    private String TAG = ExchangeRecordDetailActivity.class.getSimpleName();
    @BindView(R.id.ptrListView)
    PullToRefreshListView ptrListView;
    @BindView(R.id.top_bar)
    TopBarSwitch topBar;

    private String PAGE_COUNT = "20";
    List<ExchangeRecordDetailBean> detailList = new ArrayList<>();
    private Activity mAct;
    private Dialog pDialog;
    private MyExchangeMoneyDetailAdapter myExchangeMoneyDetailAdapter;
    // 没有更多内容时添加的底部提示布局
    private View footView;
    // 再没有更多数据获取时,禁止列表上拉加载动作
    private boolean pullMove = true;
    private ListView listView;
    private Collection<? extends ExchangeRecordDetailBean> detailList1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exchange_record_detail_act);
        ButterKnife.bind(this);
        initAfterViews();
    }

    @Override
    protected ImpIExchangeMoneyDetailView createPresenter()
    {
        return new ImpIExchangeMoneyDetailView();
    }


    void initAfterViews()
    {
        mAct = this;
        pDialog = DialogUtil.getCommonDialog(mAct, "正在获取");
        pDialog.setCancelable(false);

        topBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                finish();
            }

            @Override
            public void right2Click(View view)
            {

            }
        }).setText("收益明细");


        footView = LayoutInflater.from(mAct).inflate(R.layout.footview_normal_list, null);
        ptrListView.setMode(PullToRefreshBase.Mode.BOTH);
        ptrListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (mPresenter != null)
                    mPresenter.getExchangeMoneyDetail("0", PAGE_COUNT, "refresh");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (mPresenter != null)
                {
                    mPresenter.getExchangeMoneyDetail(String.valueOf(detailList.size()), PAGE_COUNT, "add");
                }
                    LoadFootView.showFootView(listView, false, footView, null);
            }
        });
        listView = ptrListView.getRefreshableView();
        myExchangeMoneyDetailAdapter = new MyExchangeMoneyDetailAdapter(mAct, detailList);
        listView.setAdapter(myExchangeMoneyDetailAdapter);
        View empty = LayoutInflater.from(mAct).inflate(R.layout.empty_view, null);
        ((TextView) empty.findViewById(R.id.tv_default_des)).setText("暂时没有记录");
        listView.setEmptyView(empty);
        ptrListView.setEmptyView(empty);
        if (mPresenter != null)
        {
            mPresenter.getExchangeMoneyDetail("0", PAGE_COUNT, "refresh");
        }
        LoadFootView.showFootView(listView, false, footView, null);


    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {

    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        NetHelper.handleNetError(mAct, "", interfaceName, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void exchangeRecordDetailCallBack(ArrayMap<String, Object> response)
    {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        if (ptrListView.isRefreshing())
            ptrListView.onRefreshComplete();
        Logger.t(TAG).d("饭票兑换记录：" + response);
        if (response.get("type").equals("refresh"))
        {
            detailList.clear();
            LoadFootView.showFootView(listView, false, footView, null);
            pullMove = true;
        }

        detailList1 = (Collection<? extends ExchangeRecordDetailBean>) response.get("detailList");
        if (detailList1.size() == 0)
        {
            LoadFootView.showFootView(listView, true, footView, null);
            pullMove = false;
        }else
        {
            detailList.addAll((Collection<? extends ExchangeRecordDetailBean>) response.get("detailList"));
            myExchangeMoneyDetailAdapter.notifyDataSetChanged();
        }

        if (ptrListView != null)
        {
            ptrListView.onRefreshComplete();
            if (pullMove)
            {
                Logger.t(TAG).d("允许上下拉动"); // 恢复上拉 上下拉动
                ptrListView.setMode(PullToRefreshBase.Mode.BOTH);
            }
            else
            {
                Logger.t(TAG).d("禁止上拉"); // 禁止上拉
                ptrListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
        }
    }
}

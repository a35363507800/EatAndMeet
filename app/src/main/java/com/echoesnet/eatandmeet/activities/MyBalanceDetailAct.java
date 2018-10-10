package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.BalanceDetailBean;
import com.echoesnet.eatandmeet.presenters.ImpIMyBalanceDetailView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyBalanceDetailView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.LoadFootView;
import com.echoesnet.eatandmeet.utils.LoadingProgressUtil;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.MyBalanceDetailAdapter;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class MyBalanceDetailAct extends BaseActivity implements IMyBalanceDetailView
{
    private static final String TAG = MyBalanceDetailAct.class.getSimpleName();

    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBar;
    @BindView(R.id.prl_balance_detail)
    PullToRefreshListView prlBalanceDetail;
    @BindView(R.id.loading_view)
    RelativeLayout rlLoadingView;

    private MyBalanceDetailAdapter adapter;
    private List<BalanceDetailBean> detailBeanList;
    private static final String PAGE_COUNT = "10";
    private Activity mAct;
    private ImpIMyBalanceDetailView impIMyBalanceDetailView;
    private View footView; // 没有更多内容时添加的底部提示布局
    private boolean pullMove = true; // 再没有更多数据获取时,禁止列表上拉加载动作
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_balance_detail_layout);
        ButterKnife.bind(this);
        initAfterView();
    }

    private void initAfterView()
    {
        mAct = this;
        footView = LayoutInflater.from(mAct).inflate(R.layout.footview_normal_list, null);

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
        }).setText(getResources().getString(R.string.my_account_right));


        impIMyBalanceDetailView = new ImpIMyBalanceDetailView(mAct, this);
        prlBalanceDetail.setMode(PullToRefreshBase.Mode.BOTH);
        prlBalanceDetail.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (impIMyBalanceDetailView != null)
                {
                    impIMyBalanceDetailView.getBalanceDetailData("0", PAGE_COUNT, "refresh");
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (impIMyBalanceDetailView != null)
                {
                    impIMyBalanceDetailView.getBalanceDetailData(String.valueOf(detailBeanList.size()), PAGE_COUNT, "add");
                }
                LoadFootView.showFootView(listView, false, footView, null);
            }
        });
        listView = prlBalanceDetail.getRefreshableView();


        // 添加缺省布局
        View empty = LayoutInflater.from(MyBalanceDetailAct.this).inflate(R.layout.empty_view, null);
        ((TextView) empty.findViewById(R.id.tv_default_des)).setText("暂时没有信息");
        listView.setEmptyView(empty);
        prlBalanceDetail.setEmptyView(empty);
        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(listView);

        detailBeanList = new ArrayList<>();
        adapter = new MyBalanceDetailAdapter(MyBalanceDetailAct.this, detailBeanList);
        listView.setAdapter(adapter);
        if (impIMyBalanceDetailView != null)
        {
            LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, true, 0, null);
            impIMyBalanceDetailView.getBalanceDetailData("0", PAGE_COUNT, "add");
        }
        LoadFootView.showFootView(listView, false, footView, null);

    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }


    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        if (prlBalanceDetail != null)
            prlBalanceDetail.onRefreshComplete();
    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        if (prlBalanceDetail != null)
            prlBalanceDetail.onRefreshComplete();
        LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, true, 1, (v)->
        {
            if (impIMyBalanceDetailView != null)
                impIMyBalanceDetailView.getBalanceDetailData("0", PAGE_COUNT, "refresh");
        });
    }

    @Override
    public void getBalanceDetailDataCallback(List<BalanceDetailBean> resLst, String operateType)
    {

        if (resLst == null)
        {
            ToastUtils.showShort("获取信息失败");
        }
        else
        {
            if (operateType.equals("refresh"))
            {
                detailBeanList.clear();
                LoadFootView.showFootView(listView, false, footView, null);

                pullMove = true;
            }
            if (resLst.size() == 0)
            {
                LoadFootView.showFootView(listView, true, footView, null);
                pullMove = false;
            }
            else
            {
                detailBeanList.addAll(resLst);
                adapter.notifyDataSetChanged();
            }
        }
        if (prlBalanceDetail != null)
        {
            prlBalanceDetail.onRefreshComplete();
            if (pullMove)
            {
                Logger.t(TAG).d("允许上下拉动"); // 恢复上拉 上下拉动
                prlBalanceDetail.setMode(PullToRefreshBase.Mode.BOTH);
            }
            else
            {
                Logger.t(TAG).d("禁止上拉"); // 禁止上拉
                prlBalanceDetail.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
        }
        LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, false, 0, null);
    }


}

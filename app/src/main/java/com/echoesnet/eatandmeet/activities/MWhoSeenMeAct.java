package com.echoesnet.eatandmeet.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.WhoSeenMeBean;
import com.echoesnet.eatandmeet.presenters.ImpIMWhoSeenMeView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMWhoSeenMeView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.LoadFootView;
import com.echoesnet.eatandmeet.utils.LoadingProgressUtil;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.WhoSeenMeAdapter;
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

public class MWhoSeenMeAct extends BaseActivity implements IMWhoSeenMeView
{
    private static final String TAG = MWhoSeenMeAct.class.getSimpleName();
    private static final String PAGE_COUNT = "20";

    @BindView(R.id.ptfl_lstview1)
    PullToRefreshListView mPullToRefreshListview;
    // 添加信息加载视图
    @BindView(R.id.loading_view)
    RelativeLayout rlLoadingView;
    @BindView(R.id.top_bar_switch_my_level)
    TopBarSwitch topBarSwitch;

    private Activity mAct;
    private List<WhoSeenMeBean> dataSource;
    private WhoSeenMeAdapter adapter;
    // 没有更多内容时添加的底部提示布局
    private View footView;
    // 再没有更多数据获取时,禁止列表上拉加载动作
    private boolean pullMove = true;
    private ListView actualListView;
    private ImpIMWhoSeenMeView impIMWhoSeenMeView;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_who_seen_me);
        ButterKnife.bind(this);
        afterViews();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    private void afterViews()
    {
        mAct = this;
//        topBar.setTitle("谁看过我");
//        topBar.getRightButton().setVisibility(View.GONE);
//        topBar.getLeftButton().setVisibility(View.VISIBLE);
        topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
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
        }).setText("谁看过我");
//        topBar.setOnClickListener(new ITopbarClickListener()
//        {
//            @Override
//            public void leftClick(View view)
//            {
//                finish();
//            }
//
//            @Override
//            public void left2Click(View view)
//            {
//            }
//
//            @Override
//            public void rightClick(View view)
//            {
//            }
//        });
        impIMWhoSeenMeView = new ImpIMWhoSeenMeView(mAct, this);
        footView = LayoutInflater.from(mAct).inflate(R.layout.footview_normal_list, null);
        mPullToRefreshListview.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //下拉刷新
                if (impIMWhoSeenMeView != null)
                {
                    impIMWhoSeenMeView.getSeenMeData("0", PAGE_COUNT, "lookMe", "refresh");
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //上拉加载
                if (impIMWhoSeenMeView != null)
                {
                    impIMWhoSeenMeView.getSeenMeData(String.valueOf(dataSource.size()), PAGE_COUNT, "lookMe", "add");
                }
                LoadFootView.showFootView(actualListView, false, footView, null);

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
        View empty = LayoutInflater.from(mAct).inflate(R.layout.empty_view, null);
        ((TextView) empty.findViewById(R.id.tv_default_des)).setText(null);
        mPullToRefreshListview.setEmptyView(empty);
        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(actualListView);

        dataSource = new ArrayList<>();
        adapter = new WhoSeenMeAdapter(dataSource, mAct);
        actualListView.setAdapter(adapter);
        //mPullToRefreshListview.setAdapter(adapter);
        mPullToRefreshListview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                Intent intent = new Intent(mAct, CNewUserInfoAct.class);
                intent.putExtra("checkWay","UId");
                intent.putExtra("toUId", dataSource.get(position-1).getuId());
                mAct.startActivity(intent);

            }
        });

        if (impIMWhoSeenMeView != null)
        {
            LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, true, 0, null);
            impIMWhoSeenMeView.getSeenMeData("0", PAGE_COUNT, "lookMe", "add");
        }
        LoadFootView.showFootView(actualListView, false, footView, null);

    }



    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mAct, null, exceptSource, e);
        if (mPullToRefreshListview != null)
            mPullToRefreshListview.onRefreshComplete();
        LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, true, 1, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (impIMWhoSeenMeView != null)
                    impIMWhoSeenMeView.getSeenMeData("0", PAGE_COUNT, "lookMe", "refresh");
            }
        });
    }

    @Override
    public void getSeenMeDataCallback(List<WhoSeenMeBean> response, String operateType)
    {

        if (response == null)
        {
            ToastUtils.showShort( "获取信息失败");
        }
        else if (response.size() == 0)
        {
           LoadFootView.showFootView(actualListView, true, footView, null);
            pullMove = false;
        }
        else
        {
            if (operateType.equals("refresh"))
            {
                dataSource.clear();
                LoadFootView.showFootView(actualListView, false, footView, null);
                pullMove = true;
            }

            dataSource.addAll(response);
            adapter.notifyDataSetChanged();
            if (dataSource.size() == 0)
            {
                Logger.t(TAG).d("没有数据,显示空数据默认图");
                pullMove = true;
            }
        }

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
        LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, false, 0, null);
    }


}

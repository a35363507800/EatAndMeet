package com.echoesnet.eatandmeet.activities;


import android.app.Activity;
import android.app.Dialog;
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
import com.echoesnet.eatandmeet.presenters.ImpIMMeSeenWhoView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMMeSeenWhoView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.LoadFootView;
import com.echoesnet.eatandmeet.utils.LoadingProgressUtil;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.MeSeenWhoAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
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

public class MMeSeenWhoAct extends MVPBaseActivity<IMMeSeenWhoView, ImpIMMeSeenWhoView> implements IMMeSeenWhoView
{
    private static final String TAG = MMeSeenWhoAct.class.getSimpleName();
    private static final String PAGE_COUNT = "20";

    @BindView(R.id.ptfl_lstview1)
    PullToRefreshListView mPullToRefreshListview;
    @BindView(R.id.loading_view)
    RelativeLayout loadingView;
    @BindView(R.id.top_bar_switch_my_level)
    TopBarSwitch topBarSwitch;

    private Activity mAct;
    private List<WhoSeenMeBean> dataSource;
    private MeSeenWhoAdapter adapter;
    private Dialog pDialog;
    private String seenType = "lookToMe";

    // 没有更多内容时添加的底部提示布局
    private View footView;
    // 再没有更多数据获取时,禁止列表上拉加载动作
    private boolean pullMove = true;
    private ListView actualListView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_who_seen_me);
        ButterKnife.bind(this);
        afterViews();
    }

    private void afterViews()
    {
        mAct = this;
    //    topBar.setTitle("我看过谁");
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
        }).setText("我看过谁");
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
        pDialog = DialogUtil.getCommonDialog(mAct, "正在处理...");
        pDialog.setCancelable(false);

        footView = LayoutInflater.from(mAct).inflate(R.layout.footview_normal_list, null);
        mPullToRefreshListview.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //下拉刷新
                if (mPresenter != null)
                {
                    mPresenter.getSeenMeData("0", String.valueOf(dataSource.size()), seenType, "refresh");
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //上拉加载
                if (mPresenter != null)
                {
                    mPresenter.getSeenMeData(String.valueOf(dataSource.size()), PAGE_COUNT, seenType, "add");
                }
                LoadFootView.showFootView(actualListView, false, footView, null);
            }
        });
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
        ((TextView) empty.findViewById(R.id.tv_default_des)).setText("暂时没有信息");
        mPullToRefreshListview.setEmptyView(empty);
        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(actualListView);

        dataSource = new ArrayList<>();
        adapter = new MeSeenWhoAdapter(dataSource, mAct);
        actualListView.setAdapter(adapter);
        if (mPresenter != null)
        {
            LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 0, null);
            mPresenter.getSeenMeData("0", PAGE_COUNT, seenType, "add");
        }
        LoadFootView.showFootView(actualListView, false, footView, null);
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

    @Override
    protected ImpIMMeSeenWhoView createPresenter()
    {
        return new ImpIMMeSeenWhoView();
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mAct, null, exceptSource, e);
        if (mPullToRefreshListview != null)
            mPullToRefreshListview.onRefreshComplete();
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 1, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mPresenter != null)
                    mPresenter.getSeenMeData("0", PAGE_COUNT, seenType, "add");
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
        else if(response.size()==0)
        {
           LoadFootView.showFootView(actualListView,true,footView,null);
            //禁止上啦
            pullMove=false;
        }
        else
        {
            if (operateType.equals("refresh"))
            {
                dataSource.clear();
                LoadFootView.showFootView(actualListView,false,footView,null);
                //允许上啦
                pullMove=true;
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
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
    }


}

package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.MyFriendDynamicsBean;
import com.echoesnet.eatandmeet.presenters.ImpMMyFriendDynamicsView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMMyFriendDynamicsView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.LoadFootView;
import com.echoesnet.eatandmeet.utils.LoadingProgressUtil;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.MyFriendDynamicsAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import okhttp3.Call;

public class MMyFriendDynamicsAct extends BaseActivity implements IMMyFriendDynamicsView
{
    private static final String TAG = MMyFriendDynamicsAct.class.getSimpleName();
    private final static String PAGE_COUNT = "5";

    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.lv_dyn)
    PullToRefreshListView lv_dyn;
    @BindView(R.id.loading_view)
    RelativeLayout loadingView;

    private Dialog pDialog;
    private Activity mContext;
    private MyFriendDynamicsAdapter adapter;
    private List<MyFriendDynamicsBean> list;

    // 没有更多内容时添加的底部提示布局
    private View footView;
    // 再没有更多数据获取时,禁止列表上拉加载动作
    private boolean pullMove = true;
    private ListView actualListView;

    private ImpMMyFriendDynamicsView impMMyFriendDynamicsView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_myfriend_dynamics);
        ButterKnife.bind(this);
        afterViews();
    }

    private void afterViews()
    {
        mContext = this;
        topBar.setTitle("好友动态");
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
        impMMyFriendDynamicsView = new ImpMMyFriendDynamicsView(mContext, this);
        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理");
        pDialog.setCancelable(false);
        list = new ArrayList<>();
        footView = LayoutInflater.from(mContext).inflate(R.layout.footview_normal_list, null);

        if (impMMyFriendDynamicsView != null)
        {
            LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 0, null);
            impMMyFriendDynamicsView.getFriUpdates(String.valueOf(list.size()), PAGE_COUNT);
        }
        actualListView = lv_dyn.getRefreshableView();
        View empty = LayoutInflater.from(mContext).inflate(R.layout.empty_view, null);
        ((TextView) empty.findViewById(R.id.tv_default_des)).setText("暂时没有新动态");
        lv_dyn.setEmptyView(empty);

        adapter = new MyFriendDynamicsAdapter(mContext, list);
        actualListView.setAdapter(adapter);
        registerForContextMenu(actualListView);

        lv_dyn.setMode(PullToRefreshBase.Mode.BOTH);
        lv_dyn.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (impMMyFriendDynamicsView != null)
                    impMMyFriendDynamicsView.getFriUpdates("0", String.valueOf(list.size()));
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (impMMyFriendDynamicsView != null)
                {
                    impMMyFriendDynamicsView.getFriUpdates(String.valueOf(list.size()), PAGE_COUNT);
                }
                LoadFootView.showFootView(actualListView, false, footView, null);
            }
        });
        lv_dyn.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Logger.t(TAG).d("position--> " + position);
            }
        });

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
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {

    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 1, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (impMMyFriendDynamicsView != null)
                    impMMyFriendDynamicsView.getFriUpdates("0", String.valueOf(list.size()));
            }
        });
        if (lv_dyn != null)
            lv_dyn.onRefreshComplete();
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getFriUpdatesCallback(List<MyFriendDynamicsBean> response, String getItemStartIndex)
    {
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
        if (response==null)
        {
            ToastUtils.showShort("获取信息失败");
        }
        else if (response.size() == 0)
        {
            LoadFootView.showFootView(actualListView, true, footView, null);
            pullMove = false;
        }
        else
        {
            if ("0".equals(getItemStartIndex))
            {
                list.clear();
                LoadFootView.showFootView(actualListView, false, footView, null);
                pullMove = true;
            }
                list.addAll(response);
        }


        Logger.t(TAG).d("Act中" + list.size());
        adapter.notifyDataSetChanged();
        if (lv_dyn != null)
            lv_dyn.onRefreshComplete();
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();

        if (lv_dyn != null)
        {
            lv_dyn.onRefreshComplete();
            if (pullMove)
            {
                Logger.t(TAG).d("允许上下拉动"); // 恢复上拉 上下拉动
                lv_dyn.setMode(PullToRefreshBase.Mode.BOTH);
            }
            else
            {
                Logger.t(TAG).d("禁止上拉"); // 禁止上拉
                lv_dyn.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
        }
    }


}

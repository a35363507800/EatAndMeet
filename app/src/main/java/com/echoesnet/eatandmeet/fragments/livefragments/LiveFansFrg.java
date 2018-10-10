package com.echoesnet.eatandmeet.fragments.livefragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.fragments.MVPBaseFragment;
import com.echoesnet.eatandmeet.models.bean.MyFocusPersonBean;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.presenters.ImpIMyFansView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyFansView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.LoadFootView;
import com.echoesnet.eatandmeet.utils.LoadingProgressUtil;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.LiveFansPersonAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

public class LiveFansFrg extends MVPBaseFragment<IMyFansView, ImpIMyFansView> implements IMyFansView
{
    private static final String TAG = LiveFansFrg.class.getSimpleName();
    private static final String PAGE_COUNT = "10";
    @BindView(R.id.listview)
    PullToRefreshListView listView;
    @BindView(R.id.fans_count)
    TextView fansCount;
    @BindView(R.id.loading_view)
    RelativeLayout loadingView;

    private Dialog pDialog;
    private Activity mContext;
    private List<MyFocusPersonBean> dataSource;
    private LiveFansPersonAdapter adapter;
    private ListView actualListView;
    Unbinder unbinder;
    // 没有更多内容时添加的底部提示布局
    private View footView;
    // 再没有更多数据获取时,禁止列表上拉加载动作
    private boolean pullMove = true;
    private Collection<? extends MyFocusPersonBean> data;

    public LiveFansFrg()
    {
    }

    public static LiveFansFrg getInstance()
    {
        return new LiveFansFrg();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.live_fans_frg, container, false);
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
    protected ImpIMyFansView createPresenter()
    {
        return new ImpIMyFansView();
    }

    private void afterViews()
    {
        mContext = getActivity();
        pDialog = DialogUtil.getCommonDialog(mContext, "正在获取...");
        pDialog.setCancelable(false);
        footView = LayoutInflater.from(mContext).inflate(R.layout.footview_normal_list, null);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //下拉刷新
                if (mPresenter != null)
                    mPresenter.getAllFansPerson("0", PAGE_COUNT, "refresh");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //上拉加载
                if (mPresenter != null)
                {
                    mPresenter.getAllFansPerson(String.valueOf(dataSource.size()), PAGE_COUNT, "add");
                }
                LoadFootView.showFootView(actualListView, false, footView, null);
            }
        });
        actualListView = listView.getRefreshableView();
        View empty = LayoutInflater.from(getActivity()).inflate(R.layout.empty_view, null);
        ((TextView) empty.findViewById(R.id.tv_default_des)).setText("暂时没有粉丝");
        listView.setEmptyView(empty);
        dataSource = new ArrayList<>();
        adapter = new LiveFansPersonAdapter(mContext, dataSource);
        actualListView.setAdapter(adapter);
        adapter.setOnItemClickListener(new LiveFansPersonAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                Logger.t(TAG).d("bean.getStatus:" + dataSource.get(position).getStatus());
                if (dataSource.get(position).getStatus().equals("1"))
                {
                    EamApplication.getInstance().livePage.put(dataSource.get(position).getId(), dataSource.get(position).getUphUrl());
                    CommonUtils.startLiveProxyAct(mContext, LiveRecord.ROOM_MODE_MEMBER, "", dataSource.get(position).getUphUrl(), "",
                            dataSource.get(position).getId(), null, EamCode4Result.reqNullCode);
                } else
                {
                    Intent intent = new Intent(mContext, CNewUserInfoAct.class);
                    intent.putExtra("checkWay", "Id");
                    intent.putExtra("toId", dataSource.get(position).getId());
                    startActivity(intent);
                }
            }
        });
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 0, null);
        //下拉刷新
        mPresenter.getAllFansPerson("0", PAGE_COUNT, "refresh");
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {

    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        if (interfaceName.contains(NetInterfaceConstant.LiveC_myFans_v305))
        {
            LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 1, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mPresenter != null)
                        mPresenter.getAllFansPerson("0", PAGE_COUNT, "refresh");
                }
            });
        }
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getAllFansPersonCallBack(ArrayMap<String, Object> map)
    {
        String operateType = (String) map.get("operateType");

        fansCount.setText((String) map.get("num"));
        //下拉刷新
        if (operateType.equals("refresh"))
        {
            dataSource.clear();
            LoadFootView.showFootView(actualListView, false, footView, null);
            pullMove = true;
        }
        data = (Collection<? extends MyFocusPersonBean>) map.get("data");
        if (data.size() == 0)
        {
            LoadFootView.showFootView(actualListView, true, footView, null);
            pullMove = false;
        } else
        {
            dataSource.addAll((Collection<? extends MyFocusPersonBean>) map.get("data"));
            adapter.notifyDataSetChanged();
        }

        if (listView != null)
            listView.onRefreshComplete();
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
        if (listView != null)
        {
            listView.onRefreshComplete();
            if (pullMove)
            {
                Logger.t(TAG).d("允许上下拉动"); // 恢复上拉 上下拉动
                listView.setMode(PullToRefreshBase.Mode.BOTH);
            } else
            {
                Logger.t(TAG).d("禁止上拉"); // 禁止上拉
                listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
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

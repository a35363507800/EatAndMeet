package com.echoesnet.eatandmeet.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.GameItemBean;
import com.echoesnet.eatandmeet.presenters.ImpIGameListPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IGameListView;
import com.echoesnet.eatandmeet.utils.LoadFootView;
import com.echoesnet.eatandmeet.views.adapters.GameListAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @modifier
 * @createDate 2017/10/27
 * @description 发现页游戏专区Tab页面
 */
public class GameListFrg extends MVPBaseFragment<GameListFrg,ImpIGameListPre> implements IGameListView
{
    private String TAG= GameListFrg.class.getSimpleName();
    private Unbinder unbinder;
    private Activity mAct;
    private GameListAdapter gameListAdapter;
    private List<GameItemBean> fTrendsItemList;
    private CustomAlertDialog customAlertDialog;
    @BindView(R.id.pull_to_refresh_games)
    PullToRefreshListView refreshListView;

    // 没有更多内容时添加的底部提示布局
    private View footView;
    // 再没有更多数据获取时,禁止列表上拉加载动作
    private boolean pullMove = true;
    private ListView listView;
    private String gameNums = "";
    private boolean isShow = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frg_game_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        mAct = getActivity();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        fTrendsItemList = new ArrayList<>();
        gameListAdapter = new GameListAdapter(mAct, fTrendsItemList);
        listView = refreshListView.getRefreshableView();
        listView.setAdapter(gameListAdapter);
        refreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        footView = LayoutInflater.from(getActivity()).inflate(R.layout.footview_normal_list, null);
        refreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (mPresenter!=null)
                {
                    mPresenter.getGameList();
                }

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (mPresenter!=null)
                {
                    mPresenter.getGameList();
                    LoadFootView.showFootView(listView, false, footView, null);
                }

            }
        });

        customAlertDialog = new CustomAlertDialog(getActivity())
                .builder()
                .setTitle("提示")
                .setCancelable(false);
        mPresenter.getGameList();
    }

    @Override
    protected ImpIGameListPre createPresenter()
    {
        return new ImpIGameListPre();
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }

    @Override
    public void getGameListCallback(List<GameItemBean> gameItemBeanList)
    {

        if (gameItemBeanList != null && TextUtils.equals(gameItemBeanList.size()+"",gameNums))
        {
            if (!isShow)
            {
                LoadFootView.showFootView(listView, true, footView, null);
                isShow = true;
                pullMove = false;
            }

        }
        try
        {
            refreshListView.onRefreshComplete();
            fTrendsItemList.clear();
            if (gameItemBeanList.size() > 0)
            {
                gameNums = gameItemBeanList.size()+"";
                fTrendsItemList.addAll(gameItemBeanList);
                gameListAdapter.notifyDataSetChanged();
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        if (pullMove)
        {
            Logger.t(TAG).d("允许上下拉动"); // 恢复上拉 上下拉动
            refreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        }
        else
        {
            Logger.t(TAG).d("禁止上拉"); // 禁止上拉
            refreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }
    }
}

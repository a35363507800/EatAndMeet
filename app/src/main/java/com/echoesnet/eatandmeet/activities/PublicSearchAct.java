package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.SearchUserBean;
import com.echoesnet.eatandmeet.presenters.ImpIPublicSearchView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IPublicSearchView;
import com.echoesnet.eatandmeet.utils.LoadFootView;
import com.echoesnet.eatandmeet.utils.SearchBar;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.PublicSearchAdapter;
import com.echoesnet.eatandmeet.views.adapters.PublicSearchRefreshAdapter;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @version 1.0
 * @modifier
 * @createDate 2017/7/27
 * @description 与系统通讯录H5对接 点击关注或粉丝进入此搜素页面
 */
public class PublicSearchAct extends MVPBaseActivity<PublicSearchAct, ImpIPublicSearchView> implements IPublicSearchView
{
    private final String TAG = PublicSearchAct.class.getSimpleName();
    @BindView(R.id.tab_bar)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.recyclerView_search)
    RecyclerView recyclerView;
    @BindView(R.id.refreshList)
    PullToRefreshListView refreshList;

    private ListView listView;
    private SearchBar searchBar;
    private Activity mActivity;
    private LinearLayoutManager layoutManager;
    private String searchType;
    private String keyWord;
    private static final String DEFAULT_NUM = "20";
    private PublicSearchAdapter adapter;
    private PublicSearchRefreshAdapter refreshAdapter;
    private List<SearchUserBean> listData;
    private View footView;                           // 没有更多内容时添加的底部提示布局
    private boolean pullMove = true;                 // 没有更多数据获取时,禁止列表上拉加载动作
    private boolean isClear;
    private boolean isSearchEmpty = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_public_search);
        ButterKnife.bind(this);
        initView();
    }

    private void initView()
    {
        mActivity = this;
        searchType = getIntent().getStringExtra("searchName");
        Logger.t(TAG).d("searchType--> " + searchType);
        if (!TextUtils.isEmpty(searchType))
        {
            if (searchType.equals("followList"))
            {
                searchType = "0";
            }
            else
            {
                searchType = "1"; // 0 关注人  1 粉丝
            }
        }
        else
        {
            searchType = "0";
        }

        footView = LayoutInflater.from(mActivity).inflate(R.layout.footview_list, null);
        listData = new ArrayList<>();
//        layoutManager = new LinearLayoutManager(mActivity);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setHasFixedSize(true);
//        adapter = new PublicSearchAdapter(mActivity, listData);
//        recyclerView.setAdapter(adapter);
        View customView = topBarSwitch.inflateCustomCenter(R.layout.include_search_bar, null);
        searchBar = (SearchBar) customView.findViewById(R.id.search_bar);
        TextView tvCancel = (TextView) customView.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = getIntent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        topBarSwitch.getNavBtns2(new int[]{0, 0, 0, 0});
        searchBar.setSearchTriggerListener(new SearchBar.ISearchTriggerListener()
        {
            @Override
            public void searching(String keyword)
            {
                keyWord = keyword;
                if (TextUtils.isEmpty(keyword.trim()))
                {
                    isClear = true;
                    isSearchEmpty = true;
                }
                else
                    isSearchEmpty = false;
                refreshList.setVisibility(isSearchEmpty ? View.GONE : View.VISIBLE);
                if (mPresenter != null)
                    mPresenter.getSearchInfoPresent("0", DEFAULT_NUM, searchType, keyWord, "add");
                LoadFootView.showFootView(listView, false, footView, null);
            }
        });

        refreshList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        refreshList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (TextUtils.isEmpty(keyWord))
                {
                    ToastUtils.showShort("请输入搜索关键字");
                    if (refreshList != null)
                        refreshList.onRefreshComplete();
                }
                else
                {
                    if (mPresenter != null)
                        mPresenter.getSearchInfoPresent("0", DEFAULT_NUM, searchType, keyWord, "refresh");
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (TextUtils.isEmpty(keyWord))
                {
                    ToastUtils.showShort("请输入搜索关键字");
                    if (refreshList != null)
                        refreshList.onRefreshComplete();
                }
                else
                {
                    isClear = false;
                    if (mPresenter != null)
                        mPresenter.getSearchInfoPresent(String.valueOf(listData.size()), DEFAULT_NUM, searchType, keyWord, "add");
                    LoadFootView.showFootView(listView, false, footView, null);
                }
            }
        });

        footView = LayoutInflater.from(mActivity).inflate(R.layout.footview_list, null);
        listView = refreshList.getRefreshableView();

        View empty = LayoutInflater.from(mActivity).inflate(R.layout.empty_view, null);
        ((TextView) empty.findViewById(R.id.tv_default_des)).setText("暂无相关信息,请再次输入哦~");
        refreshList.setEmptyView(empty);

        registerForContextMenu(listView);
        refreshAdapter = new PublicSearchRefreshAdapter(mActivity, listData);
        listView.setAdapter(refreshAdapter);
        refreshAdapter.setOnFocusClickListener(new PublicSearchRefreshAdapter.OnFocusClickListener()
        {
            @Override
            public void onFocusClick(int position)
            {
                if (mPresenter != null)
                    mPresenter.focusPerson(listData, "1", position);
            }
        });
        refreshList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                SearchUserBean bean = listData.get(position - 1);
                Intent intent = new Intent(mActivity, CNewUserInfoAct.class);
                intent.putExtra("toUId", bean.getUId());
                intent.putExtra("checkWay", "UId");
                startActivity(intent);
            }
        });
    }

    @Override
    protected ImpIPublicSearchView createPresenter()
    {
        return new ImpIPublicSearchView();
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        if (refreshList != null)
            refreshList.onRefreshComplete();
    }

    @Override
    public void getSearchInfoCallback(List<SearchUserBean> list, String operateType)
    {
        if (isClear)
        {
            listData.clear();

        }
//        listData.addAll(list);
//        adapter.notifyDataSetChanged();

        if (list == null)
        {
            ToastUtils.showShort("获取信息失败");
        }
        else
        {

            if (list.size() == 0)
            {
                LoadFootView.showFootView(listView, true, footView, null);
                pullMove = false;
            }

            // 下拉刷新
            if (operateType.equals("refresh"))
            {
                listData.clear();
                LoadFootView.showFootView(listView, false, footView, null);
                pullMove = true;
            }
            listData.addAll(list);
            refreshAdapter.notifyDataSetChanged();

            if (listData.size() == 0)
            {
                Logger.t(TAG).d("没有数据,显示空数据默认图");
                pullMove = true;
            }
        }

        if (refreshList != null)
        {
            refreshList.onRefreshComplete();
            if (!operateType.equals("refresh"))
            {
                if (listData.size() == Integer.parseInt(DEFAULT_NUM))
                {
                    refreshList.getRefreshableView().smoothScrollToPosition(0);
                }
            }
            if (pullMove)
            {
                Logger.t(TAG).d("允许上下拉动"); // 恢复上拉 上下拉动
                refreshList.setMode(PullToRefreshBase.Mode.BOTH);
            }
            else
            {
                Logger.t(TAG).d("禁止上拉"); // 禁止上拉
                refreshList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
        }
    }

    @Override
    public void focusCallBack(int position)
    {
        listData.get(position).setFocus("1");
        refreshAdapter.notifyDataSetChanged();
    }
}

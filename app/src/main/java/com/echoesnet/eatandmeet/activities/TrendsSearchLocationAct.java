package com.echoesnet.eatandmeet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.presenters.ImpITrendsSearchLocationPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.ITrendsSearchLocationView;
import com.echoesnet.eatandmeet.utils.SearchBar;
import com.echoesnet.eatandmeet.views.EmptyRecyclerView;
import com.echoesnet.eatandmeet.views.adapters.TrendsSearchLocationAdapter;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/19 0019
 * @description 搜索位置
 */
public class TrendsSearchLocationAct extends MVPBaseActivity<TrendsSearchLocationAct, ImpITrendsSearchLocationPre> implements ITrendsSearchLocationView
{
    private final String TAG = TrendsSearchLocationAct.class.getSimpleName();

    @BindView(R.id.rlv_search_location)
    RecyclerView searchLocationRlv;
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.ll_searching)
    LinearLayout searchingLL;
    @BindView(R.id.ll_search_empty)
    LinearLayout searchEmptyLL;

    private TrendsSearchLocationAdapter trendsSearchLocationAdapter;
    private List<PoiInfo> mPoiInfoList;
    private String city;
    private int pageNum;
    private String mKeyword;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_trends_search_location);
        ButterKnife.bind(this);
        city = getIntent().getStringExtra("city");
        mPoiInfoList = new ArrayList<>();
        trendsSearchLocationAdapter = new TrendsSearchLocationAdapter(this, mPoiInfoList, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchLocationRlv.setLayoutManager(linearLayoutManager);
        searchLocationRlv.setAdapter(trendsSearchLocationAdapter);
        trendsSearchLocationAdapter.setItemClickListener(new TrendsSearchLocationAdapter.ItemClickListener()
        {
            @Override
            public void itemClick(PoiInfo poiInfo)
            {
                Logger.t(TAG).d(poiInfo.toString());
                Intent intent = new Intent();
                intent.putExtra("isShow", true);
                intent.putExtra("locationName", poiInfo.name);
                intent.putExtra("latitude", poiInfo.location.latitude);
                intent.putExtra("longitude", poiInfo.location.longitude);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void noShowLocationClick()
            {
                Intent intent = new Intent();
                intent.putExtra("isShow", false);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        View customView = topBarSwitch.inflateCustomCenter(R.layout.include_search_bar, null);
        SearchBar searchBar = (SearchBar) customView.findViewById(R.id.search_bar);
        searchBar.setHint("搜索附近位置");
        TextView tvCancel = (TextView) customView.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        topBarSwitch.getNavBtns2(new int[]{0, 0, 0, 0});
        searchBar.setSearchTriggerListener(new SearchBar.ISearchTriggerListener()
        {
            @Override
            public void searching(String keyword)
            {
                if (!TextUtils.isEmpty(keyword.trim()))
                {
                    mPoiInfoList.clear();
                    mKeyword = keyword;
                    pageNum = 0;
                    mPresenter.searchLocation("refresh", city, keyword, pageNum);
                    searchingLL.setVisibility(View.VISIBLE);
                    searchLocationRlv.setVisibility(View.GONE);
                    searchEmptyLL.setVisibility(View.GONE);
                }else {
                    mPoiInfoList.clear();
                    trendsSearchLocationAdapter.notifyDataSetChanged();
                }
            }
        });
        searchLocationRlv.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState)
                {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        if (isVisBottom(recyclerView))
                        {
                            mPresenter.searchLocation("add", city, mKeyword, ++pageNum);
                        }
                        break;
                }
            }
        });

    }

    private boolean isVisBottom(RecyclerView recyclerView)
    {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();
        //RecyclerView的滑动状态
        int state = recyclerView.getScrollState();
        if (visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == recyclerView.SCROLL_STATE_IDLE)
        {
            return true;
        } else
        {
            return false;
        }
    }

    @Override
    public void searchLocationCallBack(String type, List<PoiInfo> poiInfoList)
    {
        if ("refresh".equals(type))
            mPoiInfoList.clear();
        if (poiInfoList != null)
            mPoiInfoList.addAll(poiInfoList);
        trendsSearchLocationAdapter.notifyDataSetChanged();
        searchingLL.setVisibility(View.GONE);
        searchLocationRlv.setVisibility(View.VISIBLE);
        if (mPoiInfoList.size() == 0)
        {
            searchLocationRlv.setVisibility(View.GONE);
            searchEmptyLL.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected ImpITrendsSearchLocationPre createPresenter()
    {
        return new ImpITrendsSearchLocationPre();
    }
}

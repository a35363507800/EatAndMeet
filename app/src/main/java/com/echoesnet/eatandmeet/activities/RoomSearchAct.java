package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eam.icontextmodule.model.EchoesEamIcon;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.CommentsBean;
import com.echoesnet.eatandmeet.models.bean.SearchRestaurantBean;
import com.echoesnet.eatandmeet.presenters.ImpIDResSearchView;
import com.echoesnet.eatandmeet.presenters.ImpIRoomSearchPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDResSearchView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IRoomSearchView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EmptyView;
import com.echoesnet.eatandmeet.utils.SearchBar;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.RoomSearchDetailAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.CustomFlowGroup;
import com.echoesnet.eatandmeet.views.widgets.CustomFlowView;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @modifier
 * @createDate 2017/7/14
 * @description 餐厅搜索
 */
public class RoomSearchAct extends MVPBaseActivity<RoomSearchAct, ImpIRoomSearchPre> implements IRoomSearchView
{
    private static final String TAG = RoomSearchAct.class.getSimpleName();
    @BindView(R.id.cfg_history)
    CustomFlowGroup flowGroupHistory;
    @BindView(R.id.tv_history_title)
    TextView tvHistoryTitle;
    @BindView(R.id.tv_search_clear)
    TextView tvSearchClear;
    @BindView(R.id.tab_bar)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.emptyView)
    EmptyView emptyView;
    @BindView(R.id.pfl_searchView)
    PullToRefreshListView pflSearchView;
    @BindView(R.id.ll_search)
    LinearLayout llSearch;

    private EditText etKeyword;
    private SearchBar searchBar;
    private List<String> searchList;   // 存储历史搜索内容
    private List<String> searchClubList;   // 存储轰趴餐馆历史搜索内容
    private Activity mAct;
    private int geoTableId;
    private String source;
    private String keyWord;
    private ListView mListView;
    private static final String PAGE_COUNT = "10";
    private List<SearchRestaurantBean> dataList;
    private RoomSearchDetailAdapter adapter;
    private boolean isSearch = false;
    private String resType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_res_search);
        ButterKnife.bind(this);
        initAfterView();
    }

    @Override
    protected ImpIRoomSearchPre createPresenter()
    {
        return new ImpIRoomSearchPre();
    }


    private void initHistoryData(String resType)
    {
        if (TextUtils.equals("clubType",resType))
        {
            String listHistory = SharePreUtils.getSearchClubHistory(RoomSearchAct.this);
            try
            {
                searchClubList.clear();
                if (!TextUtils.isEmpty(listHistory))
                {
                    searchClubList.addAll(CommonUtils.stringToSceneList(listHistory));
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                Logger.t(TAG).d("异常信息》》" + e.getMessage());
            }
            tvSearchClear.setVisibility(View.GONE);
            llSearch.setVisibility(View.GONE);

            if (searchClubList.size() > 0)
            {
                tvSearchClear.setVisibility(View.VISIBLE);
                flowGroupHistory.removeAllViews();
                llSearch.setVisibility(View.VISIBLE);

                List<String> list = new ArrayList<>();
                for (int i = searchClubList.size() - 1; i >= 0; i--)
                {
                    list.add(searchClubList.get(i));
                }
                setSearchHistoryData(list);
            }
        }
        else
        {
            String listHistory = SharePreUtils.getSearchHistory(RoomSearchAct.this);
            try
            {
                searchList.clear();
                if (!TextUtils.isEmpty(listHistory))
                {
                    searchList.addAll(CommonUtils.stringToSceneList(listHistory));
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                Logger.t(TAG).d("异常信息》》" + e.getMessage());
            }
            tvSearchClear.setVisibility(View.GONE);
            llSearch.setVisibility(View.GONE);
            if (searchList.size() > 0)
            {
                tvSearchClear.setVisibility(View.VISIBLE);
                flowGroupHistory.removeAllViews();
                llSearch.setVisibility(View.VISIBLE);
                List<String> list = new ArrayList<>();
                for (int i = searchList.size() - 1; i >= 0; i--)
                {
                    list.add(searchList.get(i));
                }
                setSearchHistoryData(list);
            }
        }

    }

    private void setSearchHistoryData(List<String> list)
    {
        flowGroupHistory.setData(list, mAct, "", new CustomFlowGroup.ViewOnclickListener()
        {
            @Override
            public void onClickCallback(View v)
            {
                if (!((CustomFlowView) v).isSelected)
                {
                    for (int i = 0; i < flowGroupHistory.getChildCount(); i++)
                    {
                        ((CustomFlowView) flowGroupHistory.getChildAt(i)).isSelected = false;
                        ((CustomFlowView) flowGroupHistory.getChildAt(i)).setTextColor(ContextCompat.getColor(mAct, R.color.C0323));
                        flowGroupHistory.getChildAt(i).setBackgroundResource(R.drawable.round_c0323_bg_hollow);
                    }
                    ((CustomFlowView) v).setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
                    v.setBackgroundResource(R.drawable.round_c0412_bg_hollow);
                    v.setSelected(true);
                    keyWord = ((CustomFlowView) v).getText().toString().trim();
                    if (mPresenter != null)
                    {
                        mPresenter.getResList("0", PAGE_COUNT, "refresh", keyWord,resType);
                        isSearch = true;
                        Logger.t(TAG).d("搜索关键字》》 历史记录" + keyWord);
                    }
                    etKeyword.setText(keyWord);
                    etKeyword.setSelection(keyWord.length());
                    search(keyWord, true,resType);
                }
            }
        });
    }

    private void initAfterView()
    {
        mAct = this;
        geoTableId = getIntent().getIntExtra("geotable_id", 0);
        source = getIntent().getStringExtra("searchSource");
        resType = getIntent().getStringExtra("resType");
        searchList = new ArrayList<>();
        searchClubList = new ArrayList<>();
        dataList = new ArrayList<>();
        pflSearchView.setVisibility(View.GONE);
        initHistoryData(resType);
        tvSearchClear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new CustomAlertDialog(RoomSearchAct.this)
                        .builder()
                        .setMsg("确定要清空搜索历史吗？")
                        .setPositiveButton("确认", new View.OnClickListener()
                        {

                            @Override
                            public void onClick(View v)
                            {
                                if (TextUtils.equals("clubType",resType))
                                {
                                    SharePreUtils.setSearchClubHistory(mAct, "");
                                    searchClubList.clear();
                                }
                                else
                                {
                                    SharePreUtils.setSearchHistory(mAct, "");
                                    searchList.clear();
                                }

                                tvSearchClear.setVisibility(View.GONE);
                                flowGroupHistory.removeAllViews();
                                llSearch.setVisibility(View.GONE);
                            }
                        }).setNegativeButton("取消", new View.OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {

                    }
                }).show();
            }
        });

        View customView = topBarSwitch.inflateCustomCenter(R.layout.include_search_bar, null);
        searchBar = (SearchBar) customView.findViewById(R.id.search_bar);

        searchBar.setHint(TextUtils.equals("clubType",resType)?"请输入沙龙名或商圈":"请输入餐厅名或商圈");
        searchBar.setSearchCancelListener(new SearchBar.ISearchCancelListener()
        {
            @Override
            public void cancel()
            {

                if (isSearch)
                {
                    search(searchBar.getSearchKeyword(), false,resType);
                }

                if (TextUtils.equals("clubType",resType))
                {
                    String listHistory = SharePreUtils.getSearchClubHistory(RoomSearchAct.this);
                    try
                    {
                        searchClubList.clear();
                        if (!TextUtils.isEmpty(listHistory))
                        {
                            searchClubList.addAll(CommonUtils.stringToSceneList(listHistory));
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        Logger.t(TAG).d("异常信息》》" + e.getMessage());
                    }
                }
                else
                {
                    String listHistory = SharePreUtils.getSearchHistory(RoomSearchAct.this);
                    try
                    {
                        searchList.clear();
                        if (!TextUtils.isEmpty(listHistory))
                        {
                            searchList.addAll(CommonUtils.stringToSceneList(listHistory));
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        Logger.t(TAG).d("异常信息》》" + e.getMessage());
                    }
                }
                tvSearchClear.setVisibility(View.GONE);
                llSearch.setVisibility(View.GONE);

                if (TextUtils.equals("clubType",resType))
                {
                    if (searchClubList.size() > 0)
                    {
                        tvSearchClear.setVisibility(View.VISIBLE);
                        flowGroupHistory.removeAllViews();
                        llSearch.setVisibility(View.VISIBLE);

                        List<String> list = new ArrayList<>();
                        for (int i = searchClubList.size() - 1; i >= 0; i--)
                        {
                            list.add(searchClubList.get(i));
                        }

                        flowGroupHistory.reSetData(list, mAct, new CustomFlowGroup.ViewOnclickListener()
                        {
                            @Override
                            public void onClickCallback(View v)
                            {
                                for (int i = 0; i < flowGroupHistory.getChildCount(); i++)
                                {
                                    ((CustomFlowView) flowGroupHistory.getChildAt(i)).isSelected = false;
                                    ((CustomFlowView) flowGroupHistory.getChildAt(i)).setTextColor(ContextCompat.getColor(mAct, R.color.C0323));
                                    flowGroupHistory.getChildAt(i).setBackgroundResource(R.drawable.round_c0323_bg_hollow);
                                }
                                ((CustomFlowView) v).setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
                                v.setBackgroundResource(R.drawable.round_c0412_bg_hollow);
                                v.setSelected(true);
                                keyWord = ((CustomFlowView) v).getText().toString().trim();
                                if (mPresenter != null)
                                {
                                    mPresenter.getResList("0", PAGE_COUNT, "refresh", keyWord,resType);
                                    isSearch = true;
                                    Logger.t(TAG).d("搜索关键字》》 历史记录" + keyWord);
                                }
                                etKeyword.setText(keyWord);
                                etKeyword.setSelection(keyWord.length());
                                search(keyWord, true,resType);
                            }
                        });
                    }
                }
                else
                {
                    if (searchList.size() > 0)
                    {
                        tvSearchClear.setVisibility(View.VISIBLE);
                        flowGroupHistory.removeAllViews();
                        llSearch.setVisibility(View.VISIBLE);

                        List<String> list = new ArrayList<>();
                        for (int i = searchList.size() - 1; i >= 0; i--)
                        {
                            list.add(searchList.get(i));
                        }

                        flowGroupHistory.reSetData(list, mAct, new CustomFlowGroup.ViewOnclickListener()
                        {
                            @Override
                            public void onClickCallback(View v)
                            {
                                for (int i = 0; i < flowGroupHistory.getChildCount(); i++)
                                {
                                    ((CustomFlowView) flowGroupHistory.getChildAt(i)).isSelected = false;
                                    ((CustomFlowView) flowGroupHistory.getChildAt(i)).setTextColor(ContextCompat.getColor(mAct, R.color.C0323));
                                    flowGroupHistory.getChildAt(i).setBackgroundResource(R.drawable.round_c0323_bg_hollow);
                                }
                                ((CustomFlowView) v).setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
                                v.setBackgroundResource(R.drawable.round_c0412_bg_hollow);
                                v.setSelected(true);
                                keyWord = ((CustomFlowView) v).getText().toString().trim();
                                if (mPresenter != null)
                                {
                                    mPresenter.getResList("0", PAGE_COUNT, "refresh", keyWord,resType);
                                    isSearch = true;
                                    Logger.t(TAG).d("搜索关键字》》 历史记录" + keyWord);
                                }
                                etKeyword.setText(keyWord);
                                etKeyword.setSelection(keyWord.length());
                                search(keyWord, true,resType);
                            }
                        });
                    }
                }


                emptyView.setVisibility(View.GONE);
                pflSearchView.setVisibility(View.GONE);
            }
        });
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
        etKeyword = (EditText) searchBar.findViewById(R.id.query);
        etKeyword.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        etKeyword.setSingleLine(true);
        etKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    if (!TextUtils.isEmpty(etKeyword.getText().toString().trim()))
                    {
                        // 先隐藏键盘
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(RoomSearchAct.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        if (mPresenter != null)
                        {
                            mPresenter.getResList("0", PAGE_COUNT, "refresh", etKeyword.getText().toString().trim(),resType);
                            isSearch = true;
                            Logger.t(TAG).d("搜索关键字》》搜索" + etKeyword.getText().toString().trim());
                        }
                        String keyWords = etKeyword.getText().toString().trim();
                        search(keyWords, false,resType);

                        return true;
                    } else
                    {
                        ToastUtils.showShort("请输入餐厅名或商圈");
                    }
                }
                return false;
            }
        });


        pflSearchView.setMode(PullToRefreshBase.Mode.BOTH);

        pflSearchView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //下拉刷新
                if (mPresenter != null)
                {
                    mPresenter.getResList("0", String.valueOf(dataList.size()), "refresh", etKeyword.getText().toString().trim(),resType);
                    isSearch = true;
                    Logger.t(TAG).d("搜索关键字》》下拉刷新" + etKeyword.getText().toString().trim());

                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //上拉加载
                if (mPresenter != null)
                {
                    mPresenter.getResList(String.valueOf(dataList.size()), PAGE_COUNT, "add", etKeyword.getText().toString().trim(),resType);
                    isSearch = true;
                    Logger.t(TAG).d("搜索关键字》》上拉加载" + etKeyword.getText().toString().trim());
                }
            }
        });

        pflSearchView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                SearchRestaurantBean bean = dataList.get(position - 1);
                if (!TextUtils.isEmpty(source) && source.equals("sayHello"))
                {
                    // 进入餐厅布局
                    Intent intent = new Intent(mAct, CResStatusShowAct.class);
                    intent.putExtra("resId", bean.getrId());
                    intent.putExtra("resName", bean.getrName());
                    intent.putExtra("floorNum", bean.getFloor());
                    Logger.t(TAG).d("进入餐厅布局 resId--> " + bean.getrId() + " , resName--> " + bean.getrName() + " , floorNum--> " + bean.getFloor());
                    startActivity(intent);
                } else
                {

                    if (TextUtils.equals("clubType",resType))
                    {
                        // 进入轰趴餐厅详情
                        Intent intent = new Intent(mAct, ClubDetailAct.class);
                        intent.putExtra("clubId", bean.getrId());
                       // EamApplication.getInstance().lessPrice = bean.getLessPrice();
                        Logger.t(TAG).d("进入餐厅详情 restId--> " + bean.getrId() + " , resName--> " + bean.getrName() );
                        startActivity(intent);
                    }
                    else
                    {
                        // 进入餐厅详情
                        Intent intent = new Intent(mAct, DOrderMealDetailAct.class);
                        intent.putExtra("restId", bean.getrId());
                        intent.putExtra("source", "resSource");
                        EamApplication.getInstance().lessPrice = bean.getLessPrice();
                        Logger.t(TAG).d("进入餐厅详情 restId--> " + bean.getrId() + " , resName--> " + bean.getrName() + " , 起订价--> " + bean.getLessPrice());
                        startActivity(intent);
                    }

                }
            }
        });
        mListView = pflSearchView.getRefreshableView();
        // Need to use the Actual ListView when registering for Context Menu
        //  registerForContextMenu(mListView);

        adapter = new RoomSearchDetailAdapter(this, dataList);
        mListView.setAdapter(adapter);

    }

    /**
     * 存储搜索餐厅 or 轰趴餐馆 历史
     *
     * @param keyWord
     */
    private void search(String keyWord, boolean isSwapFirst,String resType)
    {
        if (TextUtils.equals("clubType",resType))
        {
            if (isSwapFirst)
            {
                String temp = keyWord;
                searchClubList.remove(keyWord);
                searchClubList.add(temp);
            }
            if (!searchClubList.contains(keyWord))
            {
                searchClubList.add(keyWord);
            }
            try
            {
                String listStr = CommonUtils.sceneListToString(searchClubList);
                SharePreUtils.setSearchClubHistory(mAct, listStr);
            } catch (IOException e)
            {
                e.printStackTrace();
                Logger.t(TAG).d(e.getMessage());
            }
        }
        else
        {
            if (isSwapFirst)
            {
                String temp = keyWord;
                searchList.remove(keyWord);
                searchList.add(temp);
            }
            if (!searchList.contains(keyWord))
            {
                searchList.add(keyWord);
            }
            try
            {
                String listStr = CommonUtils.sceneListToString(searchList);
                SharePreUtils.setSearchHistory(mAct, listStr);
            } catch (IOException e)
            {
                e.printStackTrace();
                Logger.t(TAG).d(e.getMessage());
            }
        }

    }



    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        if (pflSearchView != null && pflSearchView.getVisibility() == View.VISIBLE)
            pflSearchView.onRefreshComplete();
    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        if (pflSearchView != null && pflSearchView.getVisibility() == View.VISIBLE)
            pflSearchView.onRefreshComplete();
    }

    @Override
    public void getResListCallback(List<SearchRestaurantBean> response, String operateType)
    {
        Logger.t(TAG).d("餐厅返回数据》》" + response);
        isSearch = false;
        llSearch.setVisibility(View.GONE);
        tvSearchClear.setVisibility(View.GONE);

        if (response != null && response.size() == 0 && TextUtils.equals("refresh", operateType))
        {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setContent("暂无相关信息, 请再次输入哦~");
            pflSearchView.setVisibility(View.GONE);


        } else
        {
            pflSearchView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            if (operateType.equals("refresh"))
            {
                dataList.clear();
            }
            try
            {
                for (SearchRestaurantBean commentsItemBean : response)
                {
                    //去重复
                    if (dataList.contains(commentsItemBean))
                    {
                        int index = dataList.indexOf(commentsItemBean);
                        dataList.remove(index);
                    }
                    dataList.add(commentsItemBean);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                Logger.t(TAG).d("虑重错误" + e.getMessage());
            }
            adapter.notifyDataSetChanged();
            if (pflSearchView != null)
                pflSearchView.onRefreshComplete();
        }

    }
}

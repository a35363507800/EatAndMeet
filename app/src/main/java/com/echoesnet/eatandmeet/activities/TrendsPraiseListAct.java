package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.TrendsPraiseBean;
import com.echoesnet.eatandmeet.presenters.ImpITrendsPraiseListView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ITrendsPraiseListView;
import com.echoesnet.eatandmeet.views.adapters.TrendsPraiseAdapter;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

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
 * @createDate 2017/7/25 0025
 * @description 赞过的人
 */
public class TrendsPraiseListAct extends MVPBaseActivity<TrendsPraiseListAct,ImpITrendsPraiseListView> implements ITrendsPraiseListView
{
    private final String DEFAULT_PAGE_NUM = "200";

    @BindView(R.id.pull_to_refresh_trends_praise)
    PullToRefreshListView refreshListView;

    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;

    private List<TrendsPraiseBean> trendsPraiseList;
    private TrendsPraiseAdapter trendsMsgAdapter;
    private Activity mAct;
    private TextView topTv;
    private String tId;
    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_trends_praise_list);
        ButterKnife.bind(this);
        mAct = this;
        trendsPraiseList = new ArrayList<>();
        trendsMsgAdapter = new TrendsPraiseAdapter(this, trendsPraiseList);
        tId = getIntent().getStringExtra("tId");
        refreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        refreshListView.setAdapter(trendsMsgAdapter);
        refreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                mPresenter.getTrendsPraiseList("refresh","0",DEFAULT_PAGE_NUM,tId);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                mPresenter.getTrendsPraiseList("add", trendsPraiseList.size()+"",DEFAULT_PAGE_NUM, tId);
            }
        });
//        refreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
//        {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
//            {
//                Intent intent = new Intent(mAct, TrendsDetailAct.class);
//                intent.putExtra("tId",trendsMsgAdapter.getItem(position - 1).getTId());
//                intent.putExtra("commentId",trendsMsgAdapter.getItem(position - 1).getCommentId());
//                mAct.startActivity(intent);
//            }
//        });
        topTv = topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
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
        });
        topTv.setGravity(Gravity.CENTER);
        topTv.setLines(1);
        topTv.setText(String.format(getResources().getString(R.string.trends_praise_list),""));
        mPresenter.getTrendsPraiseList("refresh","0",DEFAULT_PAGE_NUM,tId);
    }

    @Override
    public void requestError(String err)
    {
        refreshListView.onRefreshComplete();
    }

    @Override
    public void getMyTrendsListCallBack(String type, List<TrendsPraiseBean> trendsPraiseList)
    {
        refreshListView.onRefreshComplete();
        if ("refresh".equals(type))
            this.trendsPraiseList.clear();
        this.trendsPraiseList.addAll(trendsPraiseList);
        topTv.setText(String.format(getResources().getString(R.string.trends_praise_list),"" + trendsPraiseList.size()));
        trendsMsgAdapter.notifyDataSetChanged();
    }

    @Override
    protected ImpITrendsPraiseListView createPresenter()
    {
        return new ImpITrendsPraiseListView();
    }
}

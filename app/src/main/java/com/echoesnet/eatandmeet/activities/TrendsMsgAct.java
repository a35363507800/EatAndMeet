package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.TrendsMsgBean;
import com.echoesnet.eatandmeet.presenters.ImpITrendsMsgPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.ITrendsMsgView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.views.adapters.TrendsMsgAdapter;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
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
 * @createDate 2017/7/25 0025
 * @description 互动通知
 */
public class TrendsMsgAct extends MVPBaseActivity<TrendsMsgAct,ImpITrendsMsgPre> implements ITrendsMsgView, TrendsMsgAdapter.TrendsMsgItemClick
{

    private static final String TAG = TrendsMsgAct.class.getSimpleName();
    @BindView(R.id.pull_to_refresh_trends_msg)
    PullToRefreshListView refreshListView;

    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;

    private List<TrendsMsgBean> trendsMsgBeanList;
    private TrendsMsgAdapter trendsMsgAdapter;
    private Activity mAct;
    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_trends_msg);
        ButterKnife.bind(this);
        mAct = this;
        trendsMsgBeanList = new ArrayList<>();
        trendsMsgAdapter = new TrendsMsgAdapter(this,trendsMsgBeanList);
        refreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        refreshListView.setAdapter(trendsMsgAdapter);
        trendsMsgAdapter.setTrendsMsgItemClick(this);
        refreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                mPresenter.getTrendsMsgList("refresh","0","10");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                mPresenter.getTrendsMsgList("add",trendsMsgBeanList.size()+"","10");
            }
        });
        refreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(mAct, TrendsDetailAct.class);
                intent.putExtra("tId",trendsMsgAdapter.getItem(position - 1).getTId());
                intent.putExtra("commentId",trendsMsgAdapter.getItem(position - 1).getCommentId());
                mAct.startActivity(intent);
            }
        });
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
        }).setText(getResources().getString(R.string.trends_msg));
        mPresenter.getTrendsMsgList("refresh","0","10");
    }

    @Override
    public void requestError(String business)
    {
//        if (NetInterfaceConstant.TrendC_cleanTrendMsg.equals(business) && !isFinishing())
//            finish();
//        else if (refreshListView != null)
            refreshListView.onRefreshComplete();
    }

    @Override
    public void getTrendsMsgListCallBack(String type,List<TrendsMsgBean> trendsMsgList)
    {
        refreshListView.onRefreshComplete();
        if ("refresh".equals(type))
            trendsMsgBeanList.clear();
        if (trendsMsgList!=null)
        {
            trendsMsgBeanList.addAll(trendsMsgList);
            trendsMsgAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void cleanTrendMsgListCallBack()
    {
    }

    @Override
    protected ImpITrendsMsgPre createPresenter()
    {
        return new ImpITrendsMsgPre();
    }

    @Override
    public void contentClickCallback(TrendsMsgBean itemBean, int position)
    {
        Intent intent = new Intent(mAct, TrendsDetailAct.class);
        intent.putExtra("tId",trendsMsgAdapter.getItem(position).getTId());
        intent.putExtra("commentId",trendsMsgAdapter.getItem(position).getCommentId());
        mAct.startActivity(intent);
    }

    @Override
    public void replyClickCallback(TrendsMsgBean itemBean, int position)
    {
        Intent intent = new Intent(mAct, TrendsDetailAct.class);
        intent.putExtra("tId",trendsMsgAdapter.getItem(position).getTId());
        intent.putExtra("commentId",trendsMsgAdapter.getItem(position).getCommentId());
        mAct.startActivity(intent);
    }

    @Override
    protected void onDestroy()
    {
        mPresenter.cleanTrendMsg();
        super.onDestroy();
    }
}

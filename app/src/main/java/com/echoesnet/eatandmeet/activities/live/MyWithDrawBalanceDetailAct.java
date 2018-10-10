package com.echoesnet.eatandmeet.activities.live;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.MVPBaseActivity;
import com.echoesnet.eatandmeet.models.bean.WithDrawBalanceDetailBean;
import com.echoesnet.eatandmeet.presenters.ImpIWithDrawDetailView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IWithDrawDetailView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.MyWithDrawBalanceDetailAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class MyWithDrawBalanceDetailAct extends MVPBaseActivity<IWithDrawDetailView,ImpIWithDrawDetailView> implements IWithDrawDetailView
{
    private static final String TAG = MyWithDrawBalanceDetailAct.class.getSimpleName();

    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.prl_balance_detail)
    PullToRefreshListView prlBalanceDetail;
    MyWithDrawBalanceDetailAdapter adapter;
    private List<WithDrawBalanceDetailBean> detailBeanList;
    // 添加分页
    private static final String PAGE_COUNT = "10";
    private Dialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_withdraw_balance_detail_layout);
        ButterKnife.bind(this);
        initAfterView();
    }

    private void initAfterView()
    {
        pDialog = DialogUtil.getCommonDialog(MyWithDrawBalanceDetailAct.this, "正在处理...");
        pDialog.setCancelable(false);

        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.getRightButton().setVisibility(View.INVISIBLE);
        topBar.getRightButton().setTextColor(Color.WHITE);
        topBar.setTitle("提现记录");
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                MyWithDrawBalanceDetailAct.this.finish();
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
        prlBalanceDetail.setMode(PullToRefreshBase.Mode.BOTH);
        prlBalanceDetail.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (mPresenter != null)
                    mPresenter.getBalanceDetailData("0", String.valueOf(detailBeanList.size()), "refresh");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (mPresenter != null)
                    mPresenter.getBalanceDetailData(String.valueOf(detailBeanList.size()), PAGE_COUNT, "add");
            }
        });

        // 添加缺省布局
        View empty = LayoutInflater.from(MyWithDrawBalanceDetailAct.this).inflate(R.layout.empty_view, null);
        ((TextView) empty.findViewById(R.id.tv_default_des)).setText("暂时没有信息");
        prlBalanceDetail.setEmptyView(empty);
        registerForContextMenu(prlBalanceDetail);

        detailBeanList = new ArrayList<>();
        adapter = new MyWithDrawBalanceDetailAdapter(MyWithDrawBalanceDetailAct.this, detailBeanList);
        prlBalanceDetail.setAdapter(adapter);

        if (mPresenter != null)
            mPresenter.getBalanceDetailData("0", PAGE_COUNT, "add");
    }

    /*@ItemClick(R.id.gv_account)
    void onItemClick()
    {
        MyRechargeResultAct_.intent(MyWithDrawBalanceDetailAct.this).start();
    }*/

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
    protected ImpIWithDrawDetailView createPresenter()
    {
        return new ImpIWithDrawDetailView();
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {

    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        NetHelper.handleNetError(MyWithDrawBalanceDetailAct.this, "", interfaceName, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getBalanceDetailDataCallBack(List<WithDrawBalanceDetailBean> list,String type)
    {
        Logger.t(TAG).d("获取提现明细信息成功--> ");

        //下拉刷新
        if (type.equals("refresh"))
        {
            detailBeanList.clear();
        }

            detailBeanList.addAll(list);
            adapter.notifyDataSetChanged();

        if (prlBalanceDetail != null)
            prlBalanceDetail.onRefreshComplete();

        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }
}

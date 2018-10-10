package com.echoesnet.eatandmeet.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.FaceBalanceDetailBean;
import com.echoesnet.eatandmeet.presenters.ImpMyFaceBalanceDetailView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyFaceBalanceDetailView;
import com.echoesnet.eatandmeet.utils.LoadFootView;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.MyFaceBalanceDetailAdapter;
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

public class MyFaceBalanceDetailAct extends BaseActivity implements IMyFaceBalanceDetailView
{
    private static final String TAG = MyFaceBalanceDetailAct.class.getSimpleName();

    @BindView(R.id.top_bar)
    TopBarSwitch topBar;
    @BindView(R.id.prl_balance_detail)
    PullToRefreshListView prlBalanceDetail;

    MyFaceBalanceDetailAdapter adapter;
    private List<FaceBalanceDetailBean> detailBeanList;
    // 添加分页
    private static final String PAGE_COUNT = "10";
    private Dialog pDialog;
    private ImpMyFaceBalanceDetailView impMyFaceBalanceDetailView;
    // 没有更多内容时添加的底部提示布局
    private View footView;
    // 再没有更多数据获取时,禁止列表上拉加载动作
    private boolean pullMove = true;
    private ListView balanceListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_face_balance_detail_layout);
        ButterKnife.bind(this);
        initAfterView();
    }

    private void initAfterView()
    {
        pDialog = DialogUtil.getCommonDialog(MyFaceBalanceDetailAct.this, "正在处理...");
        pDialog.setCancelable(false);

    topBar.inflateTextCenter(new TopbarSwitchSkeleton()
    {
        @Override
        public void leftClick(View view)
        {
            MyFaceBalanceDetailAct.this.finish();
        }

        @Override
        public void right2Click(View view)
        {

        }
    }).setText(getResources().getString(R.string.my_faceEgg_right));


        impMyFaceBalanceDetailView = new ImpMyFaceBalanceDetailView(MyFaceBalanceDetailAct.this, this);


        footView = LayoutInflater.from(this).inflate(R.layout.footview_normal_list, null);


        prlBalanceDetail.setMode(PullToRefreshBase.Mode.BOTH);
        prlBalanceDetail.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (impMyFaceBalanceDetailView != null)
                {
                    impMyFaceBalanceDetailView.getBalanceDetailData("0", String.valueOf(detailBeanList.size()), "refresh");
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (impMyFaceBalanceDetailView != null)
                {
                    impMyFaceBalanceDetailView.getBalanceDetailData(String.valueOf(detailBeanList.size()), PAGE_COUNT, "add");
                }
                LoadFootView.showFootView(balanceListView, false, footView, null);
            }
        });
        balanceListView = prlBalanceDetail.getRefreshableView();
        prlBalanceDetail.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
//                 脸蛋明细不可点击
//                Intent intent = new Intent(MyFaceBalanceDetailAct.this, MyRechargeResultAct.class);
//                intent.putExtra("faceEgg",detailBeanList.get(position-1).getFaceEgg());
//                startActivity(intent);
            }
        });
        // 添加缺省布局
        View empty = LayoutInflater.from(MyFaceBalanceDetailAct.this).inflate(R.layout.empty_view, null);
        ((TextView) empty.findViewById(R.id.tv_default_des)).setText("暂时没有信息");
        prlBalanceDetail.setEmptyView(empty);
        registerForContextMenu(balanceListView);


        detailBeanList = new ArrayList<>();
        adapter = new MyFaceBalanceDetailAdapter(MyFaceBalanceDetailAct.this, detailBeanList);
        // prl_balance_detail.setAdapter(adapter);
        balanceListView.setAdapter(adapter);
        if (impMyFaceBalanceDetailView != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            impMyFaceBalanceDetailView.getBalanceDetailData("0", PAGE_COUNT, "add");
        }
        LoadFootView.showFootView(balanceListView, false, footView, null);
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
    public void requestNetError(Call call, Exception e, String
            exceptSource)
    {
        NetHelper.handleNetError(MyFaceBalanceDetailAct.this, null, exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getBalanceDetailDataCallback(List<FaceBalanceDetailBean> resLst, String operateType)
    {
        if (resLst == null)
        {
            ToastUtils.showShort("获取信息失败");
        } else
        {
            if (operateType.equals("refresh"))
            {
                detailBeanList.clear();
                LoadFootView.showFootView(balanceListView, false, footView, null);
                pullMove = true;
            }
            if (resLst.size() == 0)
            {
                LoadFootView.showFootView(balanceListView, true, footView, null);
                pullMove = false;
            } else
            {
                detailBeanList.addAll(resLst);
                adapter.notifyDataSetChanged();
            }
        }

        if (prlBalanceDetail != null)
        {
            prlBalanceDetail.onRefreshComplete();
            if (pullMove)
            {
                Logger.t(TAG).d("允许上下拉动"); // 恢复上拉 上下拉动
                prlBalanceDetail.setMode(PullToRefreshBase.Mode.BOTH);
            } else
            {
                Logger.t(TAG).d("禁止上拉"); // 禁止上拉
                prlBalanceDetail.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
        }
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }


}

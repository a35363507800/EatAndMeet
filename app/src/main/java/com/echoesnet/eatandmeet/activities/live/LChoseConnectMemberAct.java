package com.echoesnet.eatandmeet.activities.live;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.MVPBaseActivity;
import com.echoesnet.eatandmeet.models.bean.LChoseConnectMemberBean;
import com.echoesnet.eatandmeet.presenters.ImpIChooseConnectMemberView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IChoseConnectMemberView;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.EmptyView;
import com.echoesnet.eatandmeet.utils.LoadingProgressUtil;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.LChoseConnectMemberAdapter;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class LChoseConnectMemberAct extends MVPBaseActivity<LChoseConnectMemberAct, ImpIChooseConnectMemberView> implements IChoseConnectMemberView,
        PullToRefreshBase.OnRefreshListener2
{
    private static final String TAG = LChoseConnectMemberAct.class.getSimpleName();
    private final String PAGE_COUNT = "20";
    @BindView(R.id.ev_empty)
    EmptyView emptyView;
    private TopBarSwitch topBarSwitch;
    private PullToRefreshListView listView;
    private ListView actualListView;
    private List<LChoseConnectMemberBean> list = new ArrayList<>();
    private Context mContext;
    private LChoseConnectMemberAdapter adapter;
    private String roomId;
    private RelativeLayout loadingView;
    private LChoseConnectMemberBean memberBean;
    private TextView right2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_lchose_connect_member);
        ButterKnife.bind(this);
        mContext = this;
        roomId = getIntent().getStringExtra("roomId");
        initView();
    }

    @Override
    protected ImpIChooseConnectMemberView createPresenter()
    {
        return new ImpIChooseConnectMemberView();
    }

    private void initView()
    {
        listView = (PullToRefreshListView) findViewById(R.id.listview);
        topBarSwitch = (TopBarSwitch) findViewById(R.id.top_bar_switch);
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
                if (memberBean != null)
                {
                    Intent intent = new Intent();
                    intent.putExtra(EamConstant.EAM_INTENT_TX_ID, memberBean);
                    setResult(RESULT_OK, intent);
                    finish();
                } else
                {
                    ToastUtils.showShort("请选择连麦人");
                }
            }
        }).setText("选择连麦人");
//       List<TextView> navBtns navBtns = topBarSwitch.getNavBtns(new int[]{1, 0, 0, 1});
//        navBtns.get(1).setText("确定");
//        navBtns.get(1).setTextSize(16);
        List<Map<String, TextView>> navBtns = topBarSwitch.getNavBtns2(new int[]{1, 0, 0, 1});
        right2 = navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON);
        right2.setText("确定");
        right2.setTextSize(16);
        loadingView = (RelativeLayout) findViewById(R.id.loading_view);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        actualListView = listView.getRefreshableView();
        View empty = LayoutInflater.from(mContext).inflate(R.layout.empty_view, null);
        ((TextView) empty.findViewById(R.id.tv_default_des)).setText("暂时没有可连麦的宝宝哟~");
        actualListView.setEmptyView(empty);
        adapter = new LChoseConnectMemberAdapter(mContext, list);
        listView.setAdapter(adapter);
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 0, null);
        mPresenter.getMyFansListInLiveRoom("0", PAGE_COUNT, roomId, "refresh");
        adapter.setChosenMemberClickListener(new LChoseConnectMemberAdapter.OnChosenMemberClickListener()
        {
            @Override
            public void onChosenMemberClick(int position, LChoseConnectMemberBean connectMemberBean)
            {
                memberBean = connectMemberBean;
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView)
    {
        mPresenter.getMyFansListInLiveRoom("0", PAGE_COUNT, roomId, "refresh");
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView)
    {
        mPresenter.getMyFansListInLiveRoom(String.valueOf(list.size()), PAGE_COUNT, roomId, "add");
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        listView.onRefreshComplete();
        NetHelper.handleNetError(mContext, null, exceptSource, e);
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 1, new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mPresenter.getMyFansListInLiveRoom("0", PAGE_COUNT, roomId, "refresh");
            }
        });
    }

    @Override
    public void getMyFansListInLiveRoomCallback(Map<String, Object> map)
    {
        listView.onRefreshComplete();

        LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);

            if ("refresh".equals(map.get("refreshType").toString()))
            {
                list.clear();
            }
            if (((List<LChoseConnectMemberBean>) map.get("response")).size()+list.size() == 0)
            {
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setTopDivideShow(true);
                right2.setOnClickListener(null);
                right2.setTextColor(ContextCompat.getColor(mContext, R.color.C0323));
                emptyView.setContent("还没有可以连麦的小伙伴哦~");
                emptyView.setImageId(R.drawable.bg_nochat);
            } else
            {
                emptyView.setVisibility(View.GONE);
                right2.setTextColor(ContextCompat.getColor(mContext, R.color.C0412));
            }
            list.addAll((List<LChoseConnectMemberBean>) map.get("response"));
            adapter.notifyDataSetChanged();

    }


}

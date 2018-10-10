package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ChosenFansBean;
import com.echoesnet.eatandmeet.presenters.ImpIManageFansView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IManageFansView;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.EmptyView;
import com.echoesnet.eatandmeet.utils.LoadFootView;
import com.echoesnet.eatandmeet.utils.LoadingProgressUtil;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.ManageFansAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import okhttp3.Call;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @version 1.0
 * @modifier zdw
 * @createDate 2017/6/21
 * @description 从粉丝中添加房管(单选-确定-选择房管)
 */
public class LiveManageFansAct extends MVPBaseActivity<LiveManageFansAct, ImpIManageFansView> implements IManageFansView
{
    private static final String TAG = LiveManageFansAct.class.getSimpleName();
    private static final String PAGE_COUNT = "20";
    private RelativeLayout loadingView;
    private PullToRefreshListView listView;
    private Dialog pDialog;
    private Activity activity;
    private TopBarSwitch topBarSwitch;
    private List<ChosenFansBean> dataSource, chosenDataSource;
    private ManageFansAdapter adapter;
    private ListView actualListView;
    private String roomId;
    private int count = 1;
    private ChosenFansBean bean;
    private View footView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_manage_fans);
        roomId = getIntent().getStringExtra("roomId");
        topBarSwitch = (TopBarSwitch) findViewById(R.id.top_bar_switch);
        listView = (PullToRefreshListView) findViewById(R.id.listview);
        loadingView = (RelativeLayout) findViewById(R.id.loading_view);
        initViews();
    }

    @Override
    protected ImpIManageFansView createPresenter()
    {
        return new ImpIManageFansView();
    }

    private void initViews()
    {
        activity = this;
        footView = LayoutInflater.from(activity).inflate(R.layout.footview_list, null);
        TextView title = topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                activity.finish();
            }

            @Override
            public void right2Click(View view)
            {
                if (bean != null && !TextUtils.isEmpty(bean.getId()))
                {
                    if (!TextUtils.isEmpty(roomId))
                    {
                        if (mPresenter != null)
                        {
                            mPresenter.setAdminByServer(roomId, bean.getuId());
                        }
                    }
                } else
                {
                    ToastUtils.showShort("请选择管理员");
                }
            }
        });
        title.setText("添加管理员");
        title.setTypeface(null, Typeface.BOLD);

        List<TextView> navBtn = topBarSwitch.getNavBtns(new int[]{1, 0, 0, 1});
        for (int i = 0; i < navBtn.size(); i++)
        {
            TextView tv = navBtn.get(i);
            if (i == 0)
                tv.setVisibility(View.VISIBLE);
            else if (i == 1)
            {
                tv.setVisibility(View.VISIBLE);
                tv.setTextSize(16);
                tv.setText("确定");
            }
        }
        pDialog = DialogUtil.getCommonDialog(activity, "正在获取...");
        pDialog.setCancelable(false);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (mPresenter != null)
                {
                    count = 1;
                    mPresenter.getAllFansPerson("0", PAGE_COUNT, "refresh");
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (mPresenter != null)
                {
                    mPresenter.getAllFansPerson(String.valueOf(Integer.parseInt(PAGE_COUNT) * count), PAGE_COUNT, "add");
                    count++;
                }
                LoadFootView.showFootView(actualListView, false, footView, null);
            }
        });
        actualListView = listView.getRefreshableView();
        EmptyView emptyView = new EmptyView(activity);
        emptyView.setContent("您还没有粉丝哦~");  // 设置提示语
        emptyView.setImageId(R.drawable.bg_nochat);
        listView.setEmptyView(emptyView);
        dataSource = new ArrayList<>();
        chosenDataSource = new ArrayList<>();
        adapter = new ManageFansAdapter(activity, dataSource);
        actualListView.setAdapter(adapter);

        actualListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                adapter.mPosition = position - 1;
                adapter.notifyDataSetChanged();
                chosenDataSource.clear();
                dataSource.get(position - 1).setManage(true);
                chosenDataSource.add(dataSource.get(position - 1));
                bean = dataSource.get(position - 1);
            }
        });

        //下拉刷新
        if (mPresenter != null)
        {
            LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 0, null);
            mPresenter.getAllFansPerson("0", String.valueOf(Integer.parseInt(PAGE_COUNT) * count), "add");
        }
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

            //下拉刷新
            if (operateType.equals("refresh"))
            {
                dataSource.clear();
                LoadFootView.showFootView(actualListView, false, footView, null);
            }
            if(((List<ChosenFansBean>) map.get("data")).size()==0)
            {
                LoadFootView.showFootView(actualListView, true, footView, null);
            }
            dataSource.addAll((Collection<? extends ChosenFansBean>) map.get("data"));
            adapter.notifyDataSetChanged();

        if (listView != null)
            listView.onRefreshComplete();
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
    }

    @Override
    public void setAdminCallback(String response)
    {
        if (response != null)
        {
            Logger.t(TAG).d("设为房管返回值--> " + response);
            try
            {
                    JSONObject jsonObject = new JSONObject(response);
                    String inRoom = jsonObject.getString("inRoom");

                    Intent intent = new Intent();
                    intent.setAction(EamConstant.EAM_BRD_ACTION_SET_ADMIN);
                    intent.putExtra("inRoom", inRoom);
                    intent.putExtra("userBean", bean);
                    sendBroadcast(intent);

                    Intent intent2 = new Intent(activity, LiveHouseManageAct.class);
                    intent2.putExtra("roomId", roomId);
                    startActivity(intent2);
                    adapter.mPosition = -1;
                    finish();

            } catch (JSONException e)
            {
                e.printStackTrace();
                Logger.t(TAG).d("设为房管异常：" + e.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}

package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ChosenAdminBean;
import com.echoesnet.eatandmeet.presenters.ImpILiveHouseView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ILiveHouseView;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.EmptyView;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.HouseManageAdapter;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

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
 * @modifier zdw
 * @createDate 2017/6/21
 * @description 房管列表(取消房管)
 */
public class LiveHouseManageAct extends MVPBaseActivity<LiveHouseManageAct, ImpILiveHouseView> implements ILiveHouseView
{
    private static final String TAG = LiveHouseManageAct.class.getSimpleName();
    private static final int adminCount = 5;

    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.emptyView)
    EmptyView emptyView;

    private TopBarSwitch topBarSwitch;
    private List<ChosenAdminBean> houseManageBeanList;
    private HouseManageAdapter adapter;
    private Activity activity;
    private String roomId;
    private View footView;
    private RelativeLayout rlAddAdmin;

    @Override
    protected ImpILiveHouseView createPresenter()
    {
        return new ImpILiveHouseView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_house_manage);
        ButterKnife.bind(this);
        activity = this;
        emptyView.setContent("暂时还没有添加房管哦~");
        emptyView.setImageId(R.drawable.bg_nochat);
        emptyView.setOnActionListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(activity, LiveManageFansAct.class);
                intent.putExtra("roomId", roomId);
                startActivity(intent);
            }
        }, "+ 添加管理员");
        houseManageBeanList = new ArrayList<>();
        roomId = getIntent().getStringExtra("roomId");
        topBarSwitch = (TopBarSwitch) findViewById(R.id.top_bar_switch);

        footView = LayoutInflater.from(activity).inflate(R.layout.live_add_admin, null);
        rlAddAdmin = (RelativeLayout) footView.findViewById(R.id.rl_add_admin);

        rlAddAdmin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (houseManageBeanList.size() >= adminCount)
                {
                    ToastUtils.showShort("房管最多设置5个");
                    return;
                }
                Intent intent = new Intent(activity, LiveManageFansAct.class);
                intent.putExtra("roomId", roomId);
                startActivity(intent);
            }
        });

        topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                activity.finish();
            }

            @Override
            public void right2Click(View view)
            {
            }
        }).setText("房间管理");

        adapter = new HouseManageAdapter(activity, houseManageBeanList);
        listView.setAdapter(adapter);
        adapter.setHouseManageBeanList(new HouseManageAdapter.HouseManageListener()
        {
            @Override
            public void onViewClick(final ChosenAdminBean bean, final int position)
            {
                if (mPresenter != null)
                {
                    mPresenter.unChosenManage(roomId, bean.getuId(), position, bean);
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (mPresenter != null)
        {
            mPresenter.getLieHouseList(roomId);
        }
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(activity, null, exceptSource, e);
    }

    @Override
    public void getHouseManageListCallback(List<ChosenAdminBean> response)
    {
        if (houseManageBeanList != null)
            houseManageBeanList.clear();
        if (response != null)
        {
            houseManageBeanList.addAll(response);
            adapter.notifyDataSetChanged();
            listView.removeFooterView(footView);
            if (houseManageBeanList.size() == 0)
            {
                listView.setVisibility(View.GONE);
                listView.removeFooterView(footView);
                emptyView.setVisibility(View.VISIBLE);
            } else
            {
                listView.addFooterView(footView, null, false);
                listView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void unAdminCallback(String response, int position, final ChosenAdminBean bean)
    {
        if (!TextUtils.isEmpty(response))
        {
            Logger.t(TAG).d("取消房管返回值--> " + response);
            try
            {
                    JSONObject jsonObject = new JSONObject(response);
                    String inRoom = jsonObject.getString("inRoom");

                    Intent intent = new Intent();
                    intent.setAction(EamConstant.EAM_BRD_ACTION_CANCEL_ADMIN);
                    intent.putExtra("inRoom", inRoom);
                    intent.putExtra("userBean", bean);
                    sendBroadcast(intent);

                    if (houseManageBeanList != null && houseManageBeanList.size() > 0)
                    {
                        houseManageBeanList.remove(position);
                        adapter.notifyDataSetChanged();
                        listView.removeFooterView(footView);
                    }

                    if (houseManageBeanList.size() == 0)
                    {
                        listView.setVisibility(View.GONE);
                        listView.removeFooterView(footView);
                        emptyView.setVisibility(View.VISIBLE);
                    } else
                    {
                        listView.addFooterView(footView, null, false);
                        listView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }

            } catch (Exception e)
            {
                e.printStackTrace();
                Logger.t(TAG).d("取消房管异常：" + e.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}

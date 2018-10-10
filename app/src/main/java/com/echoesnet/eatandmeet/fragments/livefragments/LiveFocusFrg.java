package com.echoesnet.eatandmeet.fragments.livefragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.fragments.MVPBaseFragment;
import com.echoesnet.eatandmeet.models.bean.MyFocusPersonBean;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.presenters.ImpIMyFocusView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyFocusView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.LoadFootView;
import com.echoesnet.eatandmeet.utils.LoadingProgressUtil;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.LiveFocusPersonAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LiveFocusFrg extends MVPBaseFragment<IMyFocusView, ImpIMyFocusView> implements IMyFocusView
{
    private static final String TAG = LiveFocusFrg.class.getSimpleName();
    private static final String PAGE_COUNT = "10";
    @BindView(R.id.focus_count)
    TextView focusCount;
    @BindView(R.id.listview)
    PullToRefreshListView listView;
    @BindView(R.id.loading_view)
    RelativeLayout loadingView;

    Dialog pDialog;
    Activity mContext;
    List<MyFocusPersonBean> dataSource;
    LiveFocusPersonAdapter adapter;
    ListView actualListView;
    Unbinder unbinder;
    // 再没有更多数据获取时,禁止列表上拉加载动作
    private boolean pullMove = true;
    // 没有更多内容时添加的底部提示布局
    private View footView;
    private Collection<? extends MyFocusPersonBean> data;

    public LiveFocusFrg()
    {
    }

    public static LiveFocusFrg getInstance()
    {
        return new LiveFocusFrg();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.live_focus_frg, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        afterViews();
    }

    @Override
    protected ImpIMyFocusView createPresenter()
    {
        return new ImpIMyFocusView();
    }

    private void afterViews()
    {
        mContext = getActivity();
        pDialog = DialogUtil.getCommonDialog(mContext, "正在获取...");
        pDialog.setCancelable(false);
        footView = LayoutInflater.from(mContext).inflate(R.layout.footview_normal_list, null);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //下拉刷新
                if (mPresenter != null)
                    mPresenter.getAllFollowPerson("0", PAGE_COUNT, "refresh");

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //上拉加载
                if (mPresenter != null)
                {
                    mPresenter.getAllFollowPerson(String.valueOf(dataSource.size()), PAGE_COUNT, "add");
                }
                LoadFootView.showFootView(actualListView, false, footView, null);
            }
        });
        actualListView = listView.getRefreshableView();
        registerForContextMenu(actualListView);
        View empty = LayoutInflater.from(getActivity()).inflate(R.layout.empty_view, null);
        ((TextView) empty.findViewById(R.id.tv_default_des)).setText("暂时没有关注的人");
        listView.setEmptyView(empty);
        dataSource = new ArrayList<>();
        adapter = new LiveFocusPersonAdapter(mContext, dataSource);
        //listView.setAdapter(adapter);
        actualListView.setAdapter(adapter);

        if (mPresenter != null)
        {
            //下拉刷新
            mPresenter.getAllFollowPerson("0", PAGE_COUNT, "refresh");

            // mPresenter.getAllFollowPerson("0", PAGE_COUNT, "add");
        }
        LoadFootView.showFootView(actualListView, false, footView, null);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                position = position - 1;
                Logger.t(TAG).d("bean.getStatus:" + dataSource.get(position).getStatus());
                if (dataSource.get(position).getStatus().equals("1"))
                {
                    EamApplication.getInstance().livePage.put(dataSource.get(position).getId(), dataSource.get(position).getUphUrl());
                    CommonUtils.startLiveProxyAct(mContext, LiveRecord.ROOM_MODE_MEMBER, "", dataSource.get(position).getUphUrl(), "",
                            dataSource.get(position).getId(), null, EamCode4Result.reqNullCode);
                } else
                {
                    Intent intent = new Intent(mContext, CNewUserInfoAct.class);
                    intent.putExtra("checkWay", "Id");
                    intent.putExtra("toId", dataSource.get(position).getId());
                    startActivity(intent);
                }
            }
        });

        /*adapter.setOnItemLongClickListener(new LiveFocusPersonAdapter.OnItemLongClickListener()
        {
            @Override
            public void onItemLongClick(View view, int position)
            {
            }
        });*/
        /*adapter.setOnItemClickListener(new LiveFocusPersonAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                Logger.t(TAG).d("bean.getStatus:" + dataSource.get(position).getStatus());
                if (dataSource.get(position).getStatus().equals("1"))
                {
                    EamApplication.getInstance().liveIdentity = Constants.MEMBER;
                    Intent liveIntent = LivePlayAct_.intent(mContext).get();
                    liveIntent.putExtra("roomId", dataSource.get(position).getId());
                    startActivity(liveIntent);
                }
                else
                {
                    Intent intent = CUserInfoAct_.intent(mContext).get();
                    intent.putExtra("toTxUid", "u" + dataSource.get(position).getId());
                    startActivity(intent);
                }
            }
        });*/
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 0, null);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        menu.add(0, 0, 0, "取消关注");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        // 得到当前被选中的item信息
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final MyFocusPersonBean bean = dataSource.get(menuInfo.position - 1);
        Logger.t(TAG).d("位置》" + menuInfo.id);
        Logger.t(TAG).d("position:" + menuInfo.position);
        switch (item.getItemId())
        {
            case 0:
                new CustomAlertDialog(mContext)
                        .builder()
                        .setTitle("提示")
                        .setMsg("取消关注将无法收到主播开播提醒，是否取消关注？")
                        .setPositiveButton("确定", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                mPresenter.focusAnchor(bean.getuId(), "0");
                            }
                        }).setNegativeButton("取消", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                    }
                }).show();

                break;
        }
        return super.onContextItemSelected(item);
    }


    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {

    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        if (interfaceName.contains(NetInterfaceConstant.LiveC_myFocus_v305))
        {
            LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 1, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mPresenter != null)
                        mPresenter.getAllFollowPerson("0", PAGE_COUNT, "refresh");
                }
            });
        }
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getAllFocusPersonCallBack(String response,String operateType)
    {
        ArrayList<MyFocusPersonBean> orderLst=new ArrayList<>();
        try
        {
            JSONObject obj = new JSONObject(response);
            orderLst = new Gson().fromJson(obj.getString("data"), new TypeToken<List<MyFocusPersonBean>>()
            {
            }.getType());
            focusCount.setText(obj.getString("num"));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        //下拉刷新
        if (operateType.equals("refresh"))
        {
            dataSource.clear();
            LoadFootView.showFootView(actualListView, false, footView, null);
            pullMove = true;
        }
        data = orderLst;
        if (data.size() == 0)
        {
            LoadFootView.showFootView(actualListView, true, footView, null);
            pullMove = false;
        } else
        {
            dataSource.addAll(orderLst);
            adapter.notifyDataSetChanged();
        }

        if (listView != null)
        {
            listView.onRefreshComplete();
            if (pullMove)
            {
                Logger.t(TAG).d("允许上下拉动"); // 恢复上拉 上下拉动
                listView.setMode(PullToRefreshBase.Mode.BOTH);
            } else
            {
                Logger.t(TAG).d("禁止上拉"); // 禁止上拉
                listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
        }
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
    }

    @Override
    public void changeFollowCallBack()
    {

        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        ToastUtils.showShort("取消关注成功");
        mPresenter.getAllFollowPerson("0", PAGE_COUNT, "refresh");
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }
}

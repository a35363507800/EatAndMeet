package com.echoesnet.eatandmeet.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.TopPersonAct;
import com.echoesnet.eatandmeet.activities.live.LHotAnchorVideoAct;
import com.echoesnet.eatandmeet.activities.live.LiveReadyActivity;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.activities.liveplay.View.LiveAct1;
import com.echoesnet.eatandmeet.activities.liveplay.View.LivePlayAct1;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.LAnchorsListBean;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.presenters.ImpILivePlayFrgView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ILivePlayFrgView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.Constants;
import com.echoesnet.eatandmeet.utils.LoadFootView;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.LAnchorsGridAdapter;
import com.echoesnet.eatandmeet.views.adapters.LAnchorsListAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class LivePlayFrg extends MVPBaseFragment<LivePlayFrg, ImpILivePlayFrgView> implements ILivePlayFrgView
{
    private final static String TAG = LivePlayFrg.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private Unbinder unbinder;
    private String mParam1;
    private Activity mActivity;

    @BindView(R.id.rl_live_content)
    PullToRefreshListView mPullToRefreshListView;
    @BindView(R.id.rg_live_content)
    PullToRefreshGridView mPullToRefreshGridView;
    //开始直播按钮
    @BindView(R.id.img_live_play)
    ImageView imgLivePlay;
    @BindView(R.id.img_live_top)
    ImageView imgLiveTop;

    @BindView(R.id.rl_topBar)
    TopBarSwitch topBarSwitch;
    private List<Map<String, TextView>> navBtns;

    // 进度显示
    //private Dialog pDialog;
    private LAnchorsListAdapter anchorsListAdapter;
    private LAnchorsGridAdapter anchorsGridAdapter;
    private ArrayList<LAnchorsListBean> mAnchorsList;
    private ListView lvLive;
    private GridView gvLive;
    //默认拉取数目
    private String numDefault = "20";
    private String firstLoadNum = "10";
    private String source = "3";//0为约吃饭，1为才艺主播，2为附近主播 ，3或不传为直播列表
    public boolean isFromFind = false;//是否从find跳转
    private View empty;
    // 没有更多内容时添加的底部提示布局
    private View footView;
    // 再没有更多数据获取时,禁止列表上拉加载动作
    private boolean pullMove = true;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frg_live_play, container, false);
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
    public void onResume()
    {
        super.onResume();
        LivePlayAct1.currentTime = System.currentTimeMillis();
        Logger.t(TAG).d("onResume");
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser)
        {
            if ((System.currentTimeMillis() - LiveAct1.currentTime) > 1000)
            {
                showNewbieGuide();
            }
        }
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }

    /**
     * @Description: 显示新手引导
     */
    private void showNewbieGuide()
    {
        if (SharePreUtils.getIsNewBieLiveList(getActivity()))
        {
            NetHelper.checkIsShowNewbie(getActivity(), "10", new ICommonOperateListener()
            {
                @Override
                public void onSuccess(String response)
                {
                    if ("0".equals(response))
                    {
                        //获取root节点
                        final FrameLayout fRoot = (FrameLayout) getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
                        final View vGuide = View.inflate(getActivity(), R.layout.live_guide, null);

                        final ImageView imgOrder1 = (ImageView) vGuide.findViewById(R.id.guide_1);
                        final ImageView imgOrder2 = (ImageView) vGuide.findViewById(R.id.guide_2);
                        final TextView tvClickDismiss = (TextView) vGuide.findViewById(R.id.tv_click_dismiss);


                        vGuide.setClickable(true);


                        tvClickDismiss.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                fRoot.removeView(vGuide);
                                SharePreUtils.setIsNewBieLiveList(getActivity(), false);
                                NetHelper.saveShowNewbieStatus(getActivity(), "10");
                            }
                        });
                        fRoot.addView(vGuide);

                    }
                    else
                    {

                        SharePreUtils.setIsNewBieLiveList(getActivity(), false);
                    }
                }

                @Override
                public void onError(String code, String msg)
                {

                }
            });
        }
    }

    public static LivePlayFrg newInstance(String param1)
    {
        LivePlayFrg fragment = new LivePlayFrg();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }


    private void afterViews()
    {
        navBtns = topBarSwitch.getNavBtns2(new int[]{0, 0, 0, 1});
//        navBtns.get(0).get(TopBarSwitch.NAV_BTN_ICON).setTextColor(ContextCompat.getColor(mActivity,R.color.C0412));
//        navBtns.get(0).get(TopBarSwitch.NAV_BTN_ICON).setTextSize(R.dimen.f1);
        navBtns.get(0).get(TopBarSwitch.NAV_BTN_ICON).setText("{eam-e624}");
        TextView title = topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {

            }

            @Override
            public void right2Click(View view)
            {
                //查看榜单信息
                Intent intent = new Intent(mActivity, TopPersonAct.class);
                startActivity(intent);
            }
        });
        title.setText("直 播");
        title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        topBarSwitch.setBottomLineVisibility(View.VISIBLE);
        imgLiveTop.setVisibility(View.GONE);
        footView = LayoutInflater.from(mActivity).inflate(R.layout.footview_normal_list, null);

        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        empty = LayoutInflater.from(getActivity()).inflate(R.layout.empty_view, null);
        ((TextView) empty.findViewById(R.id.tv_default_des)).setText("暂时没有主播直播");
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                getAnchorData("0", numDefault, true, true, source);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                getAnchorData(String.valueOf(mAnchorsList.size()), numDefault, false, true, source);
            }
        });
        mPullToRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView)
            {
                getAnchorData("0", numDefault, true, true, source);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView)
            {
                getAnchorData(String.valueOf(mAnchorsList.size()), numDefault, false, true, source);
            }
        });
        mAnchorsList = new ArrayList<>();
        //listNumberCatalogue = new HashMap<>();
        lvLive = mPullToRefreshListView.getRefreshableView();
        gvLive = mPullToRefreshGridView.getRefreshableView();
        anchorsListAdapter = new LAnchorsListAdapter(mActivity, mAnchorsList);
        anchorsGridAdapter = new LAnchorsGridAdapter(mActivity, mAnchorsList);
        lvLive.setAdapter(anchorsListAdapter);
        gvLive.setAdapter(anchorsGridAdapter);

        lvLive.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                LAnchorsListBean anchorsListBean = mAnchorsList.get(position - 1);
                if ("0".equals(anchorsListBean.getStatus()))
                {
                    Intent playIntent = new Intent(mActivity, LHotAnchorVideoAct.class);
                    playIntent.putExtra("video", anchorsListBean.getVedio());
                    playIntent.putExtra("coverImgUrl", anchorsListBean.getRoomUrl());
                    playIntent.putExtra("luid", anchorsListBean.getuId());
                    playIntent.putExtra("roomId", anchorsListBean.getId());
                    playIntent.putExtra("watchNum", anchorsListBean.getViewer());
                    startActivity(playIntent);
                }
                else
                {
                    Logger.t(TAG).d("System.currentTimeMillis()：" + System.currentTimeMillis() + ",LiveAct1.currentTime:" + LiveAct1.currentTime + ",currentTime:" + (System.currentTimeMillis() - LiveAct1.currentTime));
                    //退出房间后立马狂点直播间进入 ， 会出现 直播资源没有释放 问题 从而导致无法进入直播间，
                    // 这个问题 已解决 但是感觉 还是加一个 延迟进入好点， ----------yqh
                    if ((System.currentTimeMillis() - LiveAct1.currentTime) < 1300)
                        return;
                    //用户身份进入
                    EamApplication.getInstance().liveIdentity = Constants.MEMBER;
                    EamApplication.getInstance().livePage.put(anchorsListBean.getId(), anchorsListBean.getRoomUrl());
                    CommonUtils.startLiveProxyAct(mActivity,
                            LiveRecord.ROOM_MODE_MEMBER,
                            "",
                            "",
                            anchorsListBean.getRoomUrl(),
                            anchorsListBean.getId(),
                            null, EamCode4Result.reqNullCode);
                }
            }
        });
        lvLive.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {


                switch (scrollState)
                {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:

                        View item = view.getChildAt(0);
                        item.measure(0, 0);
                        if ((lvLive.getChildCount() > 0 && lvLive.getFirstVisiblePosition() == 1 && item.getMeasuredHeight() > 0) || lvLive.getFirstVisiblePosition() == 0)
                        {
                            imgLiveTop.setVisibility(View.GONE);
                        }
                        else
                        {
                            imgLiveTop.setVisibility(View.VISIBLE);
                        }
                        mPullToRefreshListView.setPullToRefreshOverScrollEnabled(true);
                        break;
                    case SCROLL_STATE_FLING:
                        mPullToRefreshListView.setPullToRefreshOverScrollEnabled(false);
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
            }
        });
        gvLive.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                LAnchorsListBean anchorsListBean = mAnchorsList.get(position);
                if ("0".equals(anchorsListBean.getStatus()))
                {
                    Intent playIntent = new Intent(mActivity, LHotAnchorVideoAct.class);
                    playIntent.putExtra("video", anchorsListBean.getVedio());
                    playIntent.putExtra("coverImgUrl", anchorsListBean.getRoomUrl());
                    playIntent.putExtra("roomId", anchorsListBean.getId());
                    playIntent.putExtra("luid", anchorsListBean.getuId());
                    playIntent.putExtra("watchNum", anchorsListBean.getViewer());
                    startActivity(playIntent);
                }
                else
                {
                    //退出房间后立马狂点直播间进入 ， 会出现 直播资源没有释放 问题 从而导致无法进入直播间，
                    // 这个问题 已解决 但是感觉 还是加一个 延迟进入好点， ----------yqh
                    if ((System.currentTimeMillis() - LiveAct1.currentTime) < 1300)
                        return;
                    //用户身份进入
                    EamApplication.getInstance().liveIdentity = Constants.MEMBER;
                    String roomId = anchorsListBean.getId();
                    EamApplication.getInstance().livePage.put(anchorsListBean.getId(), anchorsListBean.getRoomUrl());
                    CommonUtils.startLiveProxyAct(mActivity, LiveRecord.ROOM_MODE_MEMBER, "", "", anchorsListBean.getRoomUrl(),
                            anchorsListBean.getRoomId(), null, EamCode4Result.reqNullCode);
                }
            }
        });
        gvLive.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {

                switch (scrollState)
                {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:

                        if (lvLive.getLastVisiblePosition() >= 4)
                        {

                            imgLiveTop.setVisibility(View.VISIBLE);
                        }
                        else
                        {

                            imgLiveTop.setVisibility(View.GONE);
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
            }
        });
        getAnchorData("0", firstLoadNum, true, false, source);
    }

    @OnClick({R.id.img_live_play, R.id.img_live_top})
    void clickView(View view)
    {
        switch (view.getId())
        {
            case R.id.img_live_play://进入直播界面
                if (CommonUtils.isFastDoubleClick())
                    break;
                LivePlayFrgPermissionsDispatcher.onCameraAudioPermGrantedWithPermissionCheck(this);
                break;
            case R.id.img_live_top:
                lvLive.smoothScrollToPositionFromTop(0, 0);
                lvLive.setSelection(0);
                imgLiveTop.setVisibility(View.GONE);
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LivePlayFrgPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    void onCameraAudioPermGranted()
    {
        Logger.t(TAG).d("允许获取权限");
        if (CommonUtils.cameraIsCanUse())
        {
            startActivity(new Intent(mActivity, LiveReadyActivity.class));
        }
        else
        {
            ToastUtils.showShort( "请释放相机资源");
        }
    }

    @OnPermissionDenied({android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    void onCameraAudioPermDenied()
    {
        Logger.t(TAG).d("拒绝获取权限");
        ToastUtils.showLong("小饭没有获得相应的权限，无法为您进一步提供服务");
    }

    @OnNeverAskAgain({android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    void onCameraAudioPermNeverAsk()
    {
        Logger.t(TAG).d("点击了不要再次询问获取权限");
        ToastUtils.showLong(getString(R.string.per_camera_never_ask));
    }

    @OnShowRationale({android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    void onCameraAudioPermReason(final PermissionRequest request)
    {
        Logger.t(TAG).d("说明");
        new CustomAlertDialog(mActivity)
                .builder()
                .setTitle("请求权限说明")
                .setMsg("需要使用您的相机和录音权限才能完成此功能！")
                .setPositiveButton("允许", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        request.proceed();
                    }
                })
                .setNegativeButton("拒绝", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        request.cancel();
                    }
                }).show();
    }


    /**
     * 获取主播列表数据
     *
     * @param star
     * @param num
     */

    private void getAnchorData(String star, String num, final boolean isRefresh, boolean isPullTrigger, String source)
    {
        if (mPresenter != null)
            mPresenter.getAnchorData(star, num, isRefresh, source);

    }

    @Override
    protected ImpILivePlayFrgView createPresenter()
    {
        return new ImpILivePlayFrgView();
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mActivity, null, TAG, e);
        if (mPullToRefreshListView != null)
        {
            mPullToRefreshListView.onRefreshComplete();
        }
    }

    @Override
    public void getAnchorDataCallback(List<LAnchorsListBean> response, boolean isRefresh, String styleFlag)
    {
        Logger.t(TAG).d("response=====" + response);
        if (response != null && response.size() == 0)
        {
            LoadFootView.showFootView(lvLive, true, footView, null);
            pullMove = false;
        }
        if (isRefresh)
        {
            mAnchorsList.clear();
            LoadFootView.showFootView(lvLive, false, footView, null);
            pullMove = true;
        }
        if (null != response)
        {
            for (LAnchorsListBean lAnchorsListBean : response)
            {
                //去重复
                if (mAnchorsList.contains(lAnchorsListBean))
                {
                    int index = mAnchorsList.indexOf(lAnchorsListBean);
                    mAnchorsList.remove(index);
                }
                mAnchorsList.add(lAnchorsListBean);
            }

            //把主播直播间封面 存起来
            for (LAnchorsListBean lAnchorsListBean : mAnchorsList)
            {
                EamApplication.getInstance().livePage.put(lAnchorsListBean.getId(), lAnchorsListBean.getRoomUrl());
            }
        }
        else
        {
            ToastUtils.showShort("获取主播列表失败");
        }
        if ("1".equals(styleFlag))//1 九宫格
        {
            mPullToRefreshListView.setVisibility(View.GONE);
            mPullToRefreshGridView.setVisibility(View.VISIBLE);
            anchorsGridAdapter.notifyDataSetChanged();
            mPullToRefreshGridView.onRefreshComplete();
            mPullToRefreshGridView.setEmptyView(empty);
        }
        else
        {
            mPullToRefreshListView.setVisibility(View.VISIBLE);
            mPullToRefreshGridView.setVisibility(View.GONE);
            anchorsListAdapter.notifyDataSetChanged();
            mPullToRefreshListView.onRefreshComplete();
            mPullToRefreshListView.setEmptyView(empty);

            if (mAnchorsList.size() == 0)
            {
                Logger.t(TAG).d("没有数据,显示空数据默认图");
                pullMove = true;
            }

            if (pullMove)
            {
                Logger.t(TAG).d("允许上下拉动"); // 恢复上拉 上下拉动
                mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
            }
            else
            {
                Logger.t(TAG).d("禁止上拉"); // 禁止上拉
                mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
        }
    }

    public void setSource(String source)
    {
        if (!isFromFind)
            this.source = source;
    }

    /**
     * 刷新数据
     *
     * @param source 0为约吃饭，1为才艺主播，2为附近主播 ，3或不传为直播列表
     */
    public void refreshDataFromFind(String source)
    {
        isFromFind = true;
        this.source = source;
        getAnchorData("0", numDefault, true, true, source);
    }

}

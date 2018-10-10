package com.echoesnet.eatandmeet.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.ColumnArticleDetailAct;
import com.echoesnet.eatandmeet.activities.DOrderMealDetailAct;
import com.echoesnet.eatandmeet.activities.GameAct;
import com.echoesnet.eatandmeet.activities.TrendsDetailAct;
import com.echoesnet.eatandmeet.activities.live.LHotAnchorVideoAct;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.presenters.ImpIProductionPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IProductionView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.EmptyView;
import com.echoesnet.eatandmeet.utils.LoadFootView;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.views.adapters.CMomentsAdapter;
import com.echoesnet.eatandmeet.views.adapters.VMomentsAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.video.CustomManager;
import com.echoesnet.eatandmeet.views.widgets.video.ScrollCalculatorHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static com.echoesnet.eatandmeet.activities.TrendsDetailAct.RESULT_TRENDS_DELETE;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0 nMyin
 * @createDate 2017/7/14 16:31
 * @description
 */

public class VProductionFrg extends MVPBaseFragment<VProductionFrg, ImpIProductionPre> implements IProductionView,
        AbsListView.OnScrollListener, VMomentsAdapter.TrendsItemClick, View.OnTouchListener
{
    private final static String TAG = VProductionFrg.class.getSimpleName();
    private final String DEFAULT_NUM = "20";

    @BindView(R.id.pull_to_refresh_trends)
    PullToRefreshListView refreshListView;

    // 没有更多内容时添加的底部提示布局
    private View footView;
    // 再没有更多数据获取时,禁止列表上拉加载动作
    private boolean pullMove = true;
    private Unbinder unbinder;
    private ListView listView;
    private VMomentsAdapter fTrendsAdapter;
    private List<FTrendsItemBean> fTrendsItemList;
    private int visibleItemCount, firstVisibleItem;
    private int isShowingPosition;
    private String isShowingTid;
    private CustomAlertDialog customAlertDialog;
    private boolean isShowingVideo = false;
    private ScrollCalculatorHelper scrollCalculatorHelper;
    private EmptyView eview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frg_vproduction, null);
        unbinder = ButterKnife.bind(this, view);
        // registerBroadcastReceiver();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        fTrendsItemList = new ArrayList<>();
        fTrendsAdapter = new VMomentsAdapter(this.getActivity(), fTrendsItemList);
        fTrendsAdapter.setTrendsItemClick(this);
        refreshListView.getRefreshableView().setAdapter(fTrendsAdapter);
        refreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        refreshListView.setOnScrollListener(this);
        // refreshListView.getRefreshableView().setOnTouchListener(this);
        listView = refreshListView.getRefreshableView();
        listView.setOnTouchListener(this);
        int playTop = CommonUtil.getScreenHeight(getActivity()) / 2 - CommonUtil.dip2px(getActivity(), 100);
        int playBottom = CommonUtil.getScreenHeight(getActivity()) / 2 + CommonUtil.dip2px(getActivity(), 100);
        scrollCalculatorHelper = new ScrollCalculatorHelper(R.id.ftrends_uvideo_view, playTop, playBottom,getActivity());
        footView = LayoutInflater.from(getActivity()).inflate(R.layout.footview_normal_list, null);
        refreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                getTrendsData("refresh", "0", DEFAULT_NUM);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                getTrendsData("add", String.valueOf(fTrendsItemList.size()), DEFAULT_NUM);
                LoadFootView.showFootView(listView, false, footView, null);
            }
        });

        eview = new EmptyView(getActivity());
        //防止闪图      要在第一次更新数据源以后掉一次true
        eview.setIsGone(false);
        eview.setContent("您还没有发布动态哦~\n     赶紧去发一条吧!");

        eview.setImageId(R.drawable.bg_nochat);
        eview.setContent("暂时还没有好友发布动态哦~");

        customAlertDialog = new CustomAlertDialog(getActivity())
                .builder()
                .setTitle("提示")
                .setCancelable(false);

        refreshListView.setEmptyView(eview);
        getTrendsData("refresh", "0", DEFAULT_NUM);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        CustomManager.onPauseAll();
    }


    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
//        if (broadcastReceiver != null)
//        {
//            getActivity().unregisterReceiver(broadcastReceiver);
//        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unbinder.unbind();
    }

    /**
     * 刷新数据
     */
    public void refreshData()
    {
        if (mPresenter != null)
            mPresenter.getVTrends("refresh", "0", DEFAULT_NUM);
    }

    @Override
    protected ImpIProductionPre createPresenter()
    {
        return new ImpIProductionPre();
    }

//    private void registerBroadcastReceiver()
//    {
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(EamConstant.EAM_REFRESH_TREND_MSG);
//       // getActivity().registerReceiver(broadcastReceiver, intentFilter);
//    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }


    public static VProductionFrg newInstance(int type, @Nullable String luid)
    {
        Bundle bundle = new Bundle();
        bundle.putString("luid", luid);
        bundle.putInt("type", type);
        VProductionFrg momentsFrg = new VProductionFrg();
        momentsFrg.setArguments(bundle);
        return momentsFrg;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Logger.t(TAG).d("requestCode>>" + requestCode + "|" + resultCode);
        int toPosition = 0;
        String tId = "";
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case EamConstant.EAM_OPEN_TRENDS_PUBLISH:
//                if (resultCode == RESULT_OK)
//                {
//                    Bundle bundle = data.getBundleExtra("publish");
//                    FTrendsItemBean trendsItemBean = (FTrendsItemBean) bundle.getSerializable("FTrendsItemBean");
//                    if (trendsItemBean != null)
//                    {
//                        if (mPresenter != null)
//                            mPresenter.startPutTrends(trendsItemBean);
//                        if (fTrendsItemList != null)
//                            fTrendsItemList.add(0, trendsItemBean);
//                        if (fTrendsAdapter != null)
//                            fTrendsAdapter.notifyDataSetChanged(false);
//                        if (refreshListView != null)
//                            refreshListView.getRefreshableView().setSelection(0);
//                    }
//                }
                break;
            case EamConstant.EAM_OPEN_TRENDS_DETAIL:
                if (resultCode == RESULT_OK)
                {
                    tId = data.getStringExtra("tId");
                    String likedNum = data.getStringExtra("likedNum");
                    String commentNum = data.getStringExtra("commentNum");
                    String isLike = data.getStringExtra("isLike");
                    toPosition = data.getIntExtra("position", 0);
                    FTrendsItemBean fTrendsItemBean = new FTrendsItemBean();
                    fTrendsItemBean.settId(tId);
                    int index = fTrendsItemList.indexOf(fTrendsItemBean);
                    Logger.t(TAG).d("index>>" + index + "|" + tId + "|" + likedNum);
                    if (index < 0)
                        return;
                    fTrendsItemList.get(index).setLikedNum(likedNum);
                    fTrendsItemList.get(index).setCommentNum(commentNum);
                    fTrendsItemList.get(index).setIsLike(isLike);
                    fTrendsAdapter.notifyDataSetChanged(true);
                }
                else if (resultCode == RESULT_TRENDS_DELETE)
                {
                    String deleteTid = data.getStringExtra("tId");
                    FTrendsItemBean fTrendsItemBean = new FTrendsItemBean();
                    fTrendsItemBean.settId(deleteTid);
                    int index = fTrendsItemList.indexOf(fTrendsItemBean);
                    if (index >= 0)
                    {
                        fTrendsItemList.remove(index);
                        fTrendsAdapter.notifyDataSetChanged(false);
                    }
                }
                break;
            case EamConstant.EAM_OPEN_TRENDS_PLAY_VIDEO:
                if (resultCode == RESULT_OK)
                {
                    toPosition = data.getIntExtra("position", 0);
                    Logger.t(TAG).d("EAM_OPEN_TRENDS_PLAY_VIDEO position" + toPosition);
                }
                break;
            case EamConstant.EAM_VUSER:
                if (resultCode == RESULT_OK)
                {
                    String uid = data.getStringExtra("uid");
                    for (int i = 0; i < fTrendsItemList.size(); i++)
                    {
                        if (fTrendsItemList.get(i).getUp().equals(uid))
                        {
                            fTrendsItemList.get(i).setFocus("1");
                            fTrendsAdapter.notifyDataSetChanged(false);
                            return;
                        }
                    }
                }
                break;
        }
        final int finalToPosition = toPosition;
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
//                checkPlayVideo(false, finalToPosition);
            }
        }, 500);
    }

    private void getTrendsData(String type, String startIdx, String num)
    {
        if (mPresenter != null)
            mPresenter.getVTrends(type, startIdx, num);
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        Logger.t(TAG).d("错误码为：%s", code);
    }

    @Override
    public void getTrendsCallback(String type, List<FTrendsItemBean> trendsList)
    {
        refreshListView.onRefreshComplete();
        if ("refresh".equals(type))
        {
            fTrendsItemList.clear();
            refreshListView.getRefreshableView().setSelection(0);
            LoadFootView.showFootView(listView, false, footView, null);

            pullMove = true;
            //隐藏大V红点
            Intent intent = new Intent(EamConstant.EAM_REFRESH_IGNORE_BIGV_MSG);
            getActivity().sendBroadcast(intent);
        }
        if (trendsList != null && trendsList.size() == 0)
        {
            LoadFootView.showFootView(listView, true, footView, null);
            pullMove = false;
        }
        try
        {
            for (FTrendsItemBean fTrendsItemBean : trendsList)
            {
                //去重复
                if (fTrendsItemList.contains(fTrendsItemBean))
                {
                    int index = fTrendsItemList.indexOf(fTrendsItemBean);
                    fTrendsItemList.remove(index);
                }
                fTrendsItemList.add(fTrendsItemBean);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("虑重错误" + e.getMessage());
        }

        fTrendsAdapter.notifyDataSetChanged(false);
        if ("refresh".equals(type))
        {
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
//                    checkPlayVideo(true, -1);
                }
            }, 300);
        }

        eview.setIsGone(true);
        if (pullMove)
        {
            Logger.t(TAG).d("允许上下拉动"); // 恢复上拉 上下拉动
            refreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        }
        else
        {
            Logger.t(TAG).d("禁止上拉"); // 禁止上拉
            refreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }
    }

    @Override
    public void focusCallback(FTrendsItemBean itemBean)
    {
        itemBean.setFocus("1");
        fTrendsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        switch (scrollState)
        {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE: // 停止滑动判断是否可以播放暂停
                scrollCalculatorHelper.onScrollStateChanged(view,scrollState);
                break;
            case SCROLL_STATE_TOUCH_SCROLL:

                break;
        }
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        if (this.firstVisibleItem == firstVisibleItem)
        {
            return;
        }
        this.visibleItemCount = visibleItemCount;
        this.firstVisibleItem = firstVisibleItem;
        int lastVisibleItem = firstVisibleItem + visibleItemCount;
        //大于0说明有播放
        if (CustomManager.instance().size() >= 0) {
            Map<String, CustomManager> map = CustomManager.instance();
            List<String> removeKey = new ArrayList<>();
            for (Map.Entry<String, CustomManager> customManagerEntry : map.entrySet()) {
                CustomManager customManager = customManagerEntry.getValue();
                //当前播放的位置
                int position = customManager.getPlayPosition();
                //对应的播放列表TAG
                if (customManager.getPlayTag().equals(VMomentsAdapter.TAG)
                        && (position < firstVisibleItem || position > lastVisibleItem)) {
                    CustomManager.releaseAllVideos(customManagerEntry.getKey());
                    removeKey.add(customManagerEntry.getKey());
                }
            }
            if(removeKey.size() > 0) {
                for (String key : removeKey) {
                    map.remove(key);
                }
                fTrendsAdapter.notifyDataSetChanged();
            }
        }
        scrollCalculatorHelper.onScroll(view, firstVisibleItem, visibleItemCount, visibleItemCount);
    }

    @Override
    public void praiseClick(TextView tvPraise, int position, FTrendsItemBean itemBean)
    {
    }

    @Override
    public void commentClick(FTrendsItemBean itemBean)
    {
        Intent intent = new Intent(getActivity(), TrendsDetailAct.class);
        intent.putExtra("tId", itemBean.gettId());
        intent.putExtra("data", itemBean);
        startActivityForResult(intent, EamConstant.EAM_OPEN_TRENDS_DETAIL);
    }


    @Override
    public void itemClick(int position, FTrendsItemBean itemBean)
    {
        FTrendsItemBean.ExtBean extBean = itemBean.getExt();
         Intent intent = new Intent(getActivity(), ColumnArticleDetailAct.class);
        if (extBean != null)
        {
            CommonUtils.serverIncreaseRead(getActivity(), extBean.getArticleId(), new ICommonOperateListener()
            {
                @Override
                public void onSuccess(String response)
                {
                    Logger.t(TAG).d("response>>" + response);
                }

                @Override
                public void onError(String code, String msg)
                {

                }
            });
            intent.putExtra("articleId", extBean.getArticleId());
            intent.putExtra("columnName", extBean.getColumnName());
            intent.putExtra("columnTitle", extBean.getTitle());
            intent.putExtra("imgUrl", extBean.getImgUrl());

            startActivityForResult(intent, EamConstant.EAM_VUSER);
            return;
        }
    }


    @Override
    public void operateClick(FTrendsItemBean itemBean)
    {
        mPresenter.focusFriendCallServer(itemBean.getUp(), itemBean);
    }

    @Override
    public void contentClick(FTrendsItemBean itemBean)
    {
        Intent intent;
        if ("3".equals(itemBean.getType()))
        {
            intent = new Intent(getActivity(), GameAct.class);
            intent.putExtra("gameUrl", itemBean.getExt().getGameUrl());
            intent.putExtra("gameName", itemBean.getExt().getGameName());
            intent.putExtra("gameId", itemBean.getExt().getGameId());
        }
        else if ("1".equals(itemBean.getType())) //进入餐厅
        {
            SharePreUtils.setToOrderMeal(getActivity(), "noDate");
            intent = new Intent(getActivity(), DOrderMealDetailAct.class);
            intent.putExtra("restId", itemBean.getExt().getrId());
        }
        else if ("2".equals(itemBean.getType()))
        { //进入直播 或者 回放
            FTrendsItemBean.ExtBean extBean = itemBean.getExt();
            if ("1".equals(extBean.getLiveStatus()))
            {
                CommonUtils.startLiveProxyAct(getActivity(), LiveRecord.ROOM_MODE_MEMBER, "", "", "", extBean.getRoomId(), null, EamCode4Result.reqNullCode);
                return;
            }
            else
            {
                if (TextUtils.isEmpty(extBean.getVedio()))
                {
                    customAlertDialog.setMsg("直播已结束")
                            .setPositiveButton("我知道了", (v)->
                            {
                                    customAlertDialog.dismiss();
                            });
                    customAlertDialog.show();
                    return;
                }
                intent = new Intent(getActivity(), LHotAnchorVideoAct.class);
                intent.putExtra("luid", extBean.getAnchor());
                intent.putExtra("video", extBean.getVedio());
                intent.putExtra("roomId", extBean.getRoomId());
            }
        }
        else if ("4".equals(itemBean.getType()))
        {

            FTrendsItemBean.ExtBean extBean = itemBean.getExt();

            intent = new Intent(getActivity(), ColumnArticleDetailAct.class);
            if (extBean != null)
            {
                CommonUtils.serverIncreaseRead(getActivity(), extBean.getArticleId(), new ICommonOperateListener()
                {
                    @Override
                    public void onSuccess(String response)
                    {
                        Logger.t(TAG).d("response>>"+response);
                    }

                    @Override
                    public void onError(String code, String msg)
                    {

                    }
                });
                intent.putExtra("articleId", extBean.getArticleId());
                intent.putExtra("columnName", extBean.getColumnName());
                intent.putExtra("columnTitle", extBean.getTitle());
                intent.putExtra("imgUrl", extBean.getImgUrl());

                startActivityForResult(intent, EamConstant.EAM_VUSER);
                return;
            }
        }
        else
        {
            intent = new Intent(getActivity(), TrendsDetailAct.class);
            intent.putExtra("tId", itemBean.gettId());
            intent.putExtra("data", itemBean);
            startActivityForResult(intent, EamConstant.EAM_OPEN_TRENDS_DETAIL);
            return;
        }
        startActivity(intent);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                // 触摸移动时的操作
//                if (!isShowingVideo)
//                    return false;
                break;
        }
        return false;
    }

    interface INewMomentsListener
    {
        void foundNewMoments(String msg);
    }
}

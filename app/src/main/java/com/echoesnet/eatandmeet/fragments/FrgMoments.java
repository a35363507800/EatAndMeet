package com.echoesnet.eatandmeet.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import com.echoesnet.eatandmeet.activities.ClubDetailAct;
import com.echoesnet.eatandmeet.activities.ColumnArticleDetailAct;
import com.echoesnet.eatandmeet.activities.DOrderMealDetailAct;
import com.echoesnet.eatandmeet.activities.GameAct;
import com.echoesnet.eatandmeet.activities.HomeAct;
import com.echoesnet.eatandmeet.activities.PromotionActionAct;
import com.echoesnet.eatandmeet.activities.TrendsDetailAct;
import com.echoesnet.eatandmeet.activities.TrendsMsgAct;
import com.echoesnet.eatandmeet.activities.TrendsPlayVideoAct;
import com.echoesnet.eatandmeet.activities.live.LHotAnchorVideoAct;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.FPromotionBean;
import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.presenters.ImpIMomentsPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMomentsView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.EmptyView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.CMomentsAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.video.CustomManager;
import com.echoesnet.eatandmeet.views.widgets.video.ScrollCalculatorHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static android.app.Activity.RESULT_OK;
import static com.echoesnet.eatandmeet.activities.TrendsDetailAct.RESULT_TRENDS_DELETE;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0 nMyin
 * @createDate 2017/7/14 16:31
 * @description 动态片段，其他需要动态的地方都可使用，但是要根据条件来填充数据源
 */

public class FrgMoments extends MVPBaseFragment<FrgMoments, ImpIMomentsPre> implements IMomentsView,
        AbsListView.OnScrollListener, CMomentsAdapter.TrendsItemClick, View.OnTouchListener
{
    private final static String TAG = FrgMoments.class.getSimpleName();
    private final String DEFAULT_NUM = "20";

    @BindView(R.id.pull_to_refresh_trends)
    PullToRefreshListView refreshListView;
    @BindView(R.id.tv_trends_msg)
    TextView trendsMsgTv;

    private Unbinder unbinder;
    private CMomentsAdapter fTrendsAdapter;
    private List<FTrendsItemBean> fTrendsItemList;
    private int visibleItemCount, firstVisibleItem;
    private int isShowingPosition;
    private String isShowingTid;
    private CustomAlertDialog customAlertDialog;
//    private Map<String, VideoPlayView> uVideoViewMap;
    private boolean isShowingVideo = false;

    private int typeData = TYPEDATA_FOLLOWERS_MOMENTS;

    public final static int TYPEDATA_FOLLOWERS_MOMENTS = 0;   //全部动态列表
    public final static int TYPEDATA_USER_TRENDS = 1;  //指定id列表  此类型需要多传一个luid参数
    public final static int TYPEDATA_MY_TRENDS = 2;  //我的列表

    private String luid = "";
    private INewMomentsListener mListener;
    private EmptyView eview;
    private Activity mAct;
    private ScrollCalculatorHelper scrollCalculatorHelper;
    private static FrgMoments MyFrgMoments;
    private static FrgMoments AllFrgMoments;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            typeData = bundle.getInt("type", TYPEDATA_FOLLOWERS_MOMENTS);
            luid = bundle.getString("luid", "");
        }
        mPresenter.registerBroadcastReceiver();
        VideoOptionModel videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        List<VideoOptionModel> list = new ArrayList<>();
        list.add(videoOptionModel);
        GSYVideoManager.instance().setOptionModelList(list);
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
    }

    public static FrgMoments getMyInstance()
    {
        if (MyFrgMoments == null)
            MyFrgMoments = new FrgMoments();
        return MyFrgMoments;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frg_ftrends, null);
        unbinder = ButterKnife.bind(this, view);
        mAct = getActivity();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        fTrendsItemList = new ArrayList<>();
//        uVideoViewMap = new HashMap<>();
        fTrendsAdapter = new CMomentsAdapter(this.getActivity(), fTrendsItemList);
        fTrendsAdapter.setTrendsItemClick(this);
        refreshListView.getRefreshableView().setAdapter(fTrendsAdapter);
        refreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        refreshListView.setOnScrollListener(this);
        refreshListView.getRefreshableView().setOnTouchListener(this);
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
            }
        });
        int playTop = CommonUtil.getScreenHeight(mAct) / 2 - CommonUtil.dip2px(mAct, 100);
        int playBottom = CommonUtil.getScreenHeight(mAct) / 2 + CommonUtil.dip2px(mAct, 100);
        scrollCalculatorHelper = new ScrollCalculatorHelper(R.id.ftrends_uvideo_view, playTop, playBottom,getActivity());
        eview = new EmptyView(getActivity());
        //防止闪图      要在第一次更新数据源以后掉一次true
        eview.setIsGone(false);
        eview.setContent("您还没有发布动态哦~\n     赶紧去发一条吧!");
        if (TYPEDATA_FOLLOWERS_MOMENTS == typeData)
        {
            eview.setImageId(R.drawable.bg_nochat);
            eview.setContent("暂时还没有好友发布动态哦~");
        }
        customAlertDialog = new CustomAlertDialog(getActivity())
                .builder()
                .setTitle("提示")
                .setCancelable(false);
        refreshListView.setEmptyView(eview);
        //  getTrendsData("refresh", "0", DEFAULT_NUM);
        registerBroadcastReceiver();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        if (isVisibleToUser)
        {
            if (fTrendsItemList == null || fTrendsItemList.size() == 0)
                refreshData();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        CustomManager.onResumeAll();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        CustomManager.onPauseAll();
//        for (Map.Entry<String, VideoPlayView> integerUVideoViewEntry : uVideoViewMap.entrySet())
//        {
//            VideoPlayView uVideoView = integerUVideoViewEntry.getValue();
//            if (uVideoView != null)
//                uVideoView.pause();
//        }
    }


    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if (broadcastReceiver != null)
        {
            getActivity().unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    public void onDestroy()
    {
        fTrendsAdapter.onDestroy();
        mPresenter.unRegisterReceiver();
        super.onDestroy();
        CustomManager.clearAllVideo();
        unbinder.unbind();
    }

    public void refreshData()
    {
        getTrendsData("refresh", "0", DEFAULT_NUM);
    }

    public void scroll2TopAndRefresh()
    {
        if (refreshListView != null && refreshListView.getRefreshableView() != null)
        {
            refreshListView.getRefreshableView().setSelection(0);
            getTrendsData("refresh", "0", DEFAULT_NUM);
        }
    }

    private void registerBroadcastReceiver()
    {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(EamConstant.EAM_REFRESH_TREND_MSG);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }


    public static FrgMoments newInstance(int type, @Nullable String luid)
    {
        Bundle bundle = new Bundle();
        bundle.putString("luid", luid);
        bundle.putInt("type", type);
        if (AllFrgMoments == null)
            AllFrgMoments = new FrgMoments();
        AllFrgMoments.setArguments(bundle);
        return AllFrgMoments;
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
                if (resultCode == RESULT_OK)
                {
                    Bundle bundle = data.getBundleExtra("publish");
                    FTrendsItemBean trendsItemBean = (FTrendsItemBean) bundle.getSerializable("FTrendsItemBean");
                    if (trendsItemBean != null)
                    {
                        if (mPresenter != null)
                            mPresenter.startPutTrends(trendsItemBean);
                        if (fTrendsItemList != null)
                            fTrendsItemList.add(0, trendsItemBean);
                        if (fTrendsAdapter != null)
                            fTrendsAdapter.notifyDataSetChanged(false);
                        if (refreshListView != null)
                            refreshListView.getRefreshableView().setSelection(0);
                    }
                }
                break;
            case EamConstant.EAM_OPEN_TRENDS_DETAIL:
                if (resultCode == RESULT_OK)
                {
                    tId = data.getStringExtra("tId");
                    String likedNum = data.getStringExtra("likedNum");
                    String commentNum = data.getStringExtra("commentNum");
                    String isLike = data.getStringExtra("isLike");
                    String readNum = data.getStringExtra("readNum");
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
                    fTrendsItemList.get(index).setReadNum(readNum);
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
        switch (typeData)
        {
            case TYPEDATA_FOLLOWERS_MOMENTS:
                if (mPresenter != null)
                    mPresenter.getFollowersMoments(type, startIdx, num);
                break;

            case TYPEDATA_USER_TRENDS:
                if (mPresenter != null)
                    mPresenter.getUserTrends(luid, type, startIdx, num);
                break;
            case TYPEDATA_MY_TRENDS:
                if (mPresenter != null)
                    mPresenter.getMyTrends(luid, type, startIdx, num);
                break;
            default:
                break;
        }
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        Logger.t(TAG).d("错误码为：%s", code);
        switch (interfaceName)
        {
            case NetInterfaceConstant.TrendC_FocusTrendList:
                refreshListView.onRefreshComplete();
                break;
            case NetInterfaceConstant.TrendC_userTrends:
                refreshListView.onRefreshComplete();
                break;

            default:
                break;
        }
    }

    @Override
    public void getTrendsCallback(String type, String msgNum, List<FTrendsItemBean> trendsList)
    {
        refreshListView.onRefreshComplete();
        EamApplication.getInstance().interactionCount = msgNum;

        EamApplication.getInstance().dynamicCount = "0";
        //更新聊天top栏红点数字
        Intent intent = new Intent(EamConstant.EAM_HX_RECEIVE_RED_HOME);
        getActivity().sendBroadcast(intent);

        if ("refresh".equals(type))
        {
            fTrendsItemList.clear();
            refreshListView.getRefreshableView().setSelection(0);
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
                if (!(EamApplication.getInstance().getChannelResult == 1 && "3".equals(fTrendsItemBean.getType())))// 根据channelResult 去除游戏动态
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
            try
            {
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
//                        checkPlayVideo(true, -1);
                    }
                }, 300);
            } catch (Exception e)
            {
                e.printStackTrace();
                Logger.t(TAG).d(e.getMessage());
            }
        }

        try
        {
            Logger.t(TAG).d("msgNum--> " + msgNum);
            int msgNumInt = Integer.parseInt(msgNum);
            if (msgNumInt > 0)
            {
                trendsMsgTv.setText(String.format("您有%s条互动通知,可点击查看喔.", msgNum));
                trendsMsgTv.setVisibility(View.VISIBLE);
            }
            else
            {
                trendsMsgTv.setVisibility(View.GONE);
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        }
        eview.setIsGone(true);
    }

    @Override
    public void getUserTrendsCallback(String type, List<FTrendsItemBean> trendsList)
    {
        Logger.t(TAG).d("UserTrends--> " + type);
        refreshListView.onRefreshComplete();
        if ("refresh".equals(type))
            fTrendsItemList.clear();
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
                if (!(EamApplication.getInstance().getChannelResult == 1 && "3".equals(fTrendsItemBean.getType())))// 根据channelResult 去除游戏动态
                    fTrendsItemList.add(fTrendsItemBean);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("虑重错误" + e.getMessage());
        }
        eview.setIsGone(true);
        fTrendsAdapter.notifyDataSetChanged(false);
        if ("refresh".equals(type))
        {
            try
            {
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
//                        checkPlayVideo(true, -1);
                    }
                }, 300);
            } catch (Exception e)
            {
                e.printStackTrace();
                Logger.t(TAG).d(e.getMessage());
            }
        }
    }

    @Override
    public void getLikeTrendsSuccessCallback(View view, int position, String flg, int likeNum)
    {
        TextView tvPraise = (TextView) view;
        if ("1".equals(flg))
            tvPraise.setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
        else
            tvPraise.setTextColor(ContextCompat.getColor(mAct, R.color.C0323));
        tvPraise.setText(String.format("{eam-p-praise @dimen/f2} %s", likeNum));

        FTrendsItemBean itemBean = fTrendsItemList.get(position);
        itemBean.setIsLike(flg);
        itemBean.setLikedNum(String.valueOf(likeNum));
    }

    @Override
    public void deleteCommentSuc(int position)
    {
        ToastUtils.showShort("删除动态成功");
        fTrendsItemList.remove(position);
        fTrendsAdapter.notifyDataSetChanged(false);
    }

    @Override
    protected ImpIMomentsPre createPresenter()
    {
        return new ImpIMomentsPre();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        switch (scrollState)
        {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE: // 停止滑动判断是否可以播放暂停
//                checkPlayVideo(false, -1);
                scrollCalculatorHelper.onScrollStateChanged(view, scrollState);
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
                if (customManager.getPlayTag().equals(CMomentsAdapter.TAG)
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

    @OnClick({R.id.tv_trends_msg})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tv_trends_msg:
                Intent intent = new Intent(getActivity(), TrendsMsgAct.class);
                startActivity(intent);
                trendsMsgTv.setVisibility(View.GONE);
                EamApplication.getInstance().interactionCount = "0";
                Intent broadcastIntent = new Intent(EamConstant.EAM_REFRESH_TREND_MSG);
                broadcastIntent.putExtra("trend_msg", "0");
                getActivity().sendBroadcast(broadcastIntent);
                break;
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            switch (intent.getAction())
            {
                case EamConstant.EAM_REFRESH_TREND_MSG:
                    String trendsMsgNum = intent.getStringExtra("trend_msg");
                    if (!TextUtils.equals("0", trendsMsgNum))
                    {
                        trendsMsgTv.setText(String.format("您有%s条互动通知,可点击查看喔.", trendsMsgNum));
                        trendsMsgTv.setVisibility(View.VISIBLE);
                    }
                    else
                        trendsMsgTv.setVisibility(View.GONE);
                    break;
            }
        }
    };


    @Override
    public void praiseClick(TextView tvPraise, int position, FTrendsItemBean itemBean)
    {
        if (!TextUtils.isEmpty(itemBean.gettId()))
            mPresenter.likeTrends(tvPraise, position, itemBean.getTId(), itemBean.getIsLike(), itemBean.getLikedNum());
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
        if ("0".equals(itemBean.getType()) && TextUtils.isEmpty(itemBean.getUrl()))
            refreshReadNum(itemBean);
        Intent intent = new Intent(getActivity(), TrendsDetailAct.class);
        intent.putExtra("tId", itemBean.gettId());
        intent.putExtra("data", itemBean);
//        VideoPlayView uVideoView = uVideoViewMap.get(isShowingTid);
//        if (uVideoView != null)
//            intent.putExtra("position", "0".equals(((FTrendsItemBean) fTrendsItemList.get(position)).getPlayComplete()) ? uVideoView.getPalyPostion() : 0);
        startActivityForResult(intent, EamConstant.EAM_OPEN_TRENDS_DETAIL);
    }

    @Override
    public void itemLongClick(final int position, final FTrendsItemBean itemBean)
    {
        customAlertDialog.setMsg("是否删除此动态")
                .setPositiveButton("确定", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mPresenter.deleteTrends(position, itemBean.getTId());
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        customAlertDialog.dismiss();
                    }
                });
        customAlertDialog.show();
    }

    @Override
    public void videoClick(View view, int position, FTrendsItemBean itemBean)
    {
        if (!CommonUtils.isFastDoubleClick())
        {
            refreshReadNum(itemBean);
            Intent intent = new Intent(getActivity(), TrendsPlayVideoAct.class);
            intent.putExtra("url", itemBean.getUrl());
            intent.putExtra("tId", itemBean.gettId());
            intent.putExtra("phUrl", itemBean.getPhurl());
            intent.putExtra("nickName", itemBean.getNicName());
            intent.putExtra("distance", itemBean.getDistance());
            intent.putExtra("sex", itemBean.getSex());
            intent.putExtra("level", itemBean.getLevel());
            intent.putExtra("age", itemBean.getAge());
            intent.putExtra("isFocus", itemBean.getFocus());
            intent.putExtra("uid", itemBean.getUp());
            intent.putExtra("showType", itemBean.getShowType());
            intent.putExtra("isVuser", itemBean.getIsVuser());
//            VideoPlayView uVideoView = uVideoViewMap.get(isShowingTid);
//
//            if (uVideoView != null)
//                intent.putExtra("position", "0".equals(((FTrendsItemBean) fTrendsItemList.get(position)).getPlayComplete()) ? uVideoView.getPalyPostion() : 0);
            CommonUtils.playVideo(mAct, intent, itemBean.getThumbnails(), view);
        }
    }

    @Override
    public void imageClick(FTrendsItemBean itemBean)
    {
        refreshReadNum(itemBean);
    }

    @Override
    public void contentClick(FTrendsItemBean itemBean)
    {
        if (!"0".equals(itemBean.getType()) || TextUtils.isEmpty(itemBean.getUrl()))
            refreshReadNum(itemBean);
        Intent intent;
        if ("3".equals(itemBean.getType()))
        {
            if ("1".equals(itemBean.getExt().getGameType()))
            { // 直播间小游戏
                if (typeData == TYPEDATA_FOLLOWERS_MOMENTS)
                {
                    ((HomeAct) getActivity()).goToTable(2, "3");
                    ToastUtils.showShort("请进入直播间后开始游戏");
                }
                else
                {
                    Intent homeIntent = new Intent(mAct, HomeAct.class);
                    homeIntent.putExtra("showPage", 2);
                    mAct.startActivity(homeIntent);
                    ToastUtils.showShort("请进入直播间后开始游戏");
                }
                return;
            }
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
                            .setPositiveButton("我知道了", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    customAlertDialog.dismiss();
                                }
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
                intent.putExtra("articleId", extBean.getArticleId());
                intent.putExtra("columnName", extBean.getColumnName());
                intent.putExtra("columnTitle", extBean.getTitle());
                intent.putExtra("imgUrl", extBean.getImgUrl());
            }
        }
        else if ("5".equals(itemBean.getType())||"7".equals(itemBean.getType()))
        {
            FTrendsItemBean.ExtBean extBean = itemBean.getExt();
            FPromotionBean pBean = new FPromotionBean();
            pBean.setWebUrl(extBean.getShareUrl());
            pBean.setActName(extBean.getTitle());
            pBean.setActivityId(extBean.getActivityId());
            pBean.setType("2");
            intent = new Intent(getActivity(), PromotionActionAct.class);
            intent.putExtra("fpBean", pBean);
        }
        else if ("6".equals(itemBean.getType()))
        {
            intent = new Intent(getActivity(), ClubDetailAct.class);
            intent.putExtra("clubId", itemBean.getExt().getHpId());
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


    public void setNewMomentsListener(INewMomentsListener listener)
    {
        this.mListener = listener;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
//        switch (event.getAction())
//        {
//            case MotionEvent.ACTION_MOVE:
//                // 触摸移动时的操作
////                if (!isShowingVideo)
////                    return false;
//                AbsListView view = refreshListView.getRefreshableView();
//                for (int i = 0; i < visibleItemCount; i++)
//                {
//                    if (view != null && view.getChildAt(i) != null && view.getChildAt(i).findViewById(R.id.ftrends_uvideo_view) != null)
//                    {
//                        final VideoPlayView uVideoView =  view.getChildAt(i).findViewById(R.id.ftrends_uvideo_view);
//                        final FrameLayout flThumbnail = (FrameLayout) view.getChildAt(i).findViewById(R.id.fl_thumbnail);
//                        final ImageView imgThumbnail = (ImageView) view.getChildAt(i).findViewById(R.id.img_thumbnail);
//                        final IconTextView startTv = (IconTextView) view.getChildAt(i).findViewById(R.id.tv_start);
//                        Rect rect = new Rect();
//                        uVideoView.getLocalVisibleRect(rect);
//                        int videoheight = uVideoView.getHeight();
//                        int position = (int) flThumbnail.getTag();
//                        String url = (String) uVideoView.getTag();
//                        Logger.t(TAG).d("SCROLL_STATE_TOUCH_SCROLL|isShowingPosition >" + isShowingPosition + "position=" + position + "|videoheight:" + videoheight
//                                + "|(int) imgThumbnail.getTag():" + imgThumbnail.getTag()
//                                + "|rect.top:" + rect.top + "|rect.bottom:" + rect.bottom + "|isShowingVideo=" + isShowingVideo);
//                        if (rect.top <= 0 && rect.bottom <= videoheight / 2
//                                || rect.top > videoheight / 2 && rect.bottom == videoheight)
//                        {
//                            if (uVideoView != null)
//                            {
//                                if ("complete".equals(imgThumbnail.getTag()))
//                                {
//                                    imgThumbnail.setTag("");
//                                }
//                                if (isShowingPosition == (int) flThumbnail.getTag())
//                                {
//                                    uVideoView.pause();
//                                    isShowingVideo = false;
//                                }
//                            }
//                        }
//                    }
//                }
//                break;
//        }
        return false;
    }

    @Override
    public void PublishSuccess(String tid, String stamp)
    {
        FTrendsItemBean fTrendsItemBean = new FTrendsItemBean();
        fTrendsItemBean.setStamp(stamp);
        int index = fTrendsItemList.indexOf(fTrendsItemBean);
        Logger.t(TAG).d("stamp>>" + stamp + "|index>>" + index);
        if (index >= 0)
            ((FTrendsItemBean) fTrendsItemList.get(index)).settId(tid);
    }


    interface INewMomentsListener
    {
        void foundNewMoments(String msg);
    }

    private void refreshReadNum(FTrendsItemBean itemBean)
    {

        CommonUtils.serverIncreaseRead(mAct, itemBean.getTId(), new ICommonOperateListener()
        {
            @Override
            public void onSuccess(String response)
            {
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        itemBean.setReadNum(response);
                        fTrendsAdapter.notifyDataSetChanged(true);
                    }
                }, 700);
            }

            @Override
            public void onError(String code, String msg)
            {

            }
        });
    }
}

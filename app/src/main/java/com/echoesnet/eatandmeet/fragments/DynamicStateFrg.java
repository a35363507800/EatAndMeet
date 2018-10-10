package com.echoesnet.eatandmeet.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.echoesnet.eatandmeet.presenters.ImpIDynamicStatePre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDynamicStateView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.EmptyView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.TrendsRecycleViewAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.video.CustomManager;
import com.echoesnet.eatandmeet.views.widgets.video.ScrollCalculatorHelper;
import com.orhanobut.logger.Logger;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView;
import com.ss007.swiprecycleview.AdapterLoader;
import com.ss007.swiprecycleview.SwipeRefreshRecycleView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import static android.app.Activity.RESULT_OK;
import static com.echoesnet.eatandmeet.activities.TrendsDetailAct.RESULT_TRENDS_DELETE;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @createDate 2017/7/26 11.
 * @description app个人详情页的动态Tab 页面
 */

public class DynamicStateFrg extends MVPBaseFragment<DynamicStateFrg, ImpIDynamicStatePre> implements IDynamicStateView, TrendsRecycleViewAdapter.TrendsItemClick, View.OnTouchListener
{
    private final static String TAG = DynamicStateFrg.class.getSimpleName();
    @BindView(R.id.tv_trends_msg)
    TextView trendsMsgTv;
    @BindView(R.id.empty_view)
    EmptyView emptyView;
    @BindView(R.id.srrv_recyview)
    SwipeRefreshRecycleView srrvRecyview;


    private List<FTrendsItemBean> fTrendsItemList;
    private Unbinder unbinder;
    private TrendsRecycleViewAdapter testReAdapter;
    //用来标记是否正在向最后一个滑动
    private boolean isSlidingToLast = false;
    private Activity mAct;
    private CustomAlertDialog customAlertDialog;
    private String luid = "";
    private String id = "";
    private String isVuser = "";
    private final int PAGE_SIZE = 10;
    private boolean isLoadingMore = false;
    private RecyclerView recyclerView;
    private String roomId;
    private ScrollCalculatorHelper scrollCalculatorHelper;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.pull_load_more, null, false);
        unbinder = ButterKnife.bind(this, view);
        mAct = getActivity();
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            luid = bundle.getString("luid", "");
            id = bundle.getString("id", "");
            isVuser = bundle.getString("isVuser", "");
            roomId = bundle.getString("roomId");
        }
        fTrendsItemList = new ArrayList<>();

        testReAdapter = new TrendsRecycleViewAdapter(mAct, fTrendsItemList);
        testReAdapter.setHasMore(true);
        testReAdapter.setTrendsItemClick(this);
        testReAdapter.init(isVuser);

        emptyView.setContent("暂时还没有发布动态哦~");
        emptyView.setImageId(R.drawable.bg_nochat);
        srrvRecyview.init(testReAdapter, emptyView);
        srrvRecyview.setAdapter(testReAdapter);
        srrvRecyview.setPullRefreshEnable(false);
        srrvRecyview.setOnTouchListener(this);

        int playTop = CommonUtil.getScreenHeight(mAct) / 2 - CommonUtil.dip2px(mAct, 100);
        int playBottom = CommonUtil.getScreenHeight(mAct) / 2 + CommonUtil.dip2px(mAct, 100);
        scrollCalculatorHelper = new ScrollCalculatorHelper(R.id.ftrends_uvideo_view, playTop, playBottom,getActivity());

        testReAdapter.setOnRefreshLoadMoreListener(new AdapterLoader.OnRefreshLoadMoreListener()
        {
            @Override
            public void onRefresh()
            {
                srrvRecyview.setRefresh(false);
            }

            @Override
            public void onLoadMore()
            {
                if (mPresenter != null)
                    mPresenter.getUserTrends(TextUtils.isEmpty(luid) ? id : luid, String.valueOf(fTrendsItemList.size()), PAGE_SIZE + "", "add");
            }
        });
        recyclerView = srrvRecyview.getRecycle();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                Logger.t(TAG).d("newState>>" + newState);
                switch (newState)
                {
                    case RecyclerView.SCROLL_STATE_IDLE: // 停止滑动判断是否可以播放暂停
                        scrollCalculatorHelper.onScrollStateChanged(recyclerView,newState);
                        break;
                }


            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                Logger.t(TAG).d("dx>>" + dx + ",dy>>" + dy);
                //dy <0 表示 下拉刷新， dy>0 表示上滑加载等多
                if (dy > 0)
                {
                    isSlidingToLast = true;
                } else
                {
                    isSlidingToLast = false;
                }
                //   只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                LinearLayoutManager linearManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //获取最后一个可见view的位置
                int lastVisibleItem = linearManager.findLastVisibleItemPosition();
                //获取第一个可见view的位置
                int firstVisibleItem = linearManager.findFirstVisibleItemPosition();
                int visibleItemCount = firstVisibleItem + lastVisibleItem;
                //大于0说明有播放
                if (CustomManager.instance().size() >= 0) {
                    Map<String, CustomManager> map = CustomManager.instance();
                    List<String> removeKey = new ArrayList<>();
                    for (Map.Entry<String, CustomManager> customManagerEntry : map.entrySet()) {
                        CustomManager customManager = customManagerEntry.getValue();
                        //当前播放的位置
                        int position = customManager.getPlayPosition();
                        //对应的播放列表TAG
                        if (customManager.getPlayTag().equals(TrendsRecycleViewAdapter.TAG)
                                && (position < firstVisibleItem || position > lastVisibleItem)) {
                            CustomManager.releaseAllVideos(customManagerEntry.getKey());
                            removeKey.add(customManagerEntry.getKey());
                        }
                    }
                    if(removeKey.size() > 0) {
                        for (String key : removeKey) {
                            map.remove(key);
                        }
                        testReAdapter.notifyDataSetChanged();
                    }
                }
                scrollCalculatorHelper.onScroll(recyclerView, firstVisibleItem, lastVisibleItem, visibleItemCount);
            }
        });
        if (!mAct.isFinishing())
        {
            customAlertDialog = new CustomAlertDialog(mAct)
                    .builder()
                    .setCancelable(false);
        }

        if (mPresenter != null)
            mPresenter.getUserTrends(TextUtils.isEmpty(luid) ? id : luid, "0", "8", "add");

        return view;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        CustomManager.onPauseAll();
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

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
        CustomManager.releaseAllVideos(TrendsRecycleViewAdapter.TAG);
        super.onDestroy();
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
                break;
        }
    }

    @Override
    protected ImpIDynamicStatePre createPresenter()
    {
        return new ImpIDynamicStatePre();
    }


    public static DynamicStateFrg newInstance(@Nullable String luid, String id, String isVuser, String roomId)
    {
        Bundle bundle = new Bundle();
        bundle.putString("luid", luid);
        bundle.putString("id", id);
        bundle.putString("isVuser", isVuser);
        bundle.putString("roomId", roomId);
        DynamicStateFrg momentsFrg = new DynamicStateFrg();
        momentsFrg.setArguments(bundle);
        return momentsFrg;
    }


    @Override
    public void praiseClick(TextView tvPraise, int position, FTrendsItemBean itemBean)
    {
        Logger.t(TAG).d("praiseClick>>>");
        mPresenter.likeTrends(position, itemBean.getTId(), itemBean.getIsLike(), itemBean.getLikedNum());
    }

    @Override
    public void commentClick(FTrendsItemBean itemBean)
    {
        Logger.t(TAG).d("commentClick>>>");
        Intent intent = new Intent(getActivity(), TrendsDetailAct.class);
        intent.putExtra("tId", itemBean.gettId());
        intent.putExtra("data", itemBean);
        getActivity().startActivityForResult(intent, EamConstant.EAM_OPEN_TRENDS_DETAIL);
        Logger.t(TAG).d("commentClick>>>打开详情动态");
    }

    @Override
    public void itemClick(TextView textView, int position, FTrendsItemBean itemBean)
    {
        Logger.t(TAG).d("itemclick>>>");
        if (itemBean != null && itemBean.getExt() != null && "1".equals(itemBean.getExt().getGameType()))
        {
            if (CommonUtils.isInLiveRoom)
            {
                ToastUtils.showShort("请返回直播间参与游戏");
                return;
            }
        }

        Intent intent = new Intent(getActivity(), TrendsDetailAct.class);
        intent.putExtra("tId", itemBean.gettId());
        intent.putExtra("data", itemBean);
        GSYBaseVideoPlayer gsyBaseVideoPlayer = scrollCalculatorHelper.getCurrentPlayer();
        if (gsyBaseVideoPlayer != null)
            intent.putExtra("position", "0".equals(itemBean.getPlayComplete()) ? gsyBaseVideoPlayer.getPlayPosition() : 0);
        getActivity().startActivityForResult(intent, EamConstant.EAM_OPEN_TRENDS_DETAIL);
        Logger.t(TAG).d("itemClick>>>打开详情动态");
    }

    @Override
    public void videoClick(View view, TextView textView, int position, FTrendsItemBean itemBean, String isVuser)
    {
        Logger.t(TAG).d("videoClick>>>");
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
            intent.putExtra("isVuser", isVuser);
            Logger.t(TAG).d("isVuser>>>>>>" + isVuser);
            Logger.t(TAG).d("图片第一针》》" + itemBean.getThumbnails());
            GSYBaseVideoPlayer gsyBaseVideoPlayer = scrollCalculatorHelper.getCurrentPlayer();

            if (gsyBaseVideoPlayer != null)
                intent.putExtra("position", gsyBaseVideoPlayer.getCurrentState() != GSYVideoView.CURRENT_STATE_AUTO_COMPLETE ? gsyBaseVideoPlayer.getCurrentPositionWhenPlaying() : 0);
            CommonUtils.playVideo(mAct, intent, itemBean.getThumbnails(), view);
        }
    }

    @Override
    public void itemLongClick(final int position, final FTrendsItemBean itemBean)
    {
    }

    @Override
    public void contentClick(TextView textView, FTrendsItemBean itemBean)
    {
        Logger.t(TAG).d("contentClick>>>");
        refreshReadNum(itemBean);
        Intent intent;
        if (itemBean.getType()!=null && TextUtils.equals("3",itemBean.getType()))
        {
            if (itemBean.getExt()!=null && TextUtils.equals("1",itemBean.getExt().getGameType()))
            {
                // 直播间小游戏
                if (CommonUtils.isInLiveRoom)
                {
                    ToastUtils.showShort("请返回直播间参与游戏");
                    return;
                }
                Intent homeIntent = new Intent(mAct, HomeAct.class);
                homeIntent.putExtra("showPage", 2);
                mAct.startActivity(homeIntent);
                ToastUtils.showShort("请进入直播间后开始游戏");
                return;
            }
            if (CommonUtils.isInLiveRoom)
            {
                ToastUtils.showShort("请退出当前直播间");
                return;
            }
            intent = new Intent(getActivity(), GameAct.class);
            intent.putExtra("gameUrl", itemBean.getExt().getGameUrl());
            intent.putExtra("gameName", itemBean.getExt().getGameName());
            intent.putExtra("gameId", itemBean.getExt().getGameId());
        } else if ("1".equals(itemBean.getType())) //进入餐厅
        {
            SharePreUtils.setToOrderMeal(getActivity(), "noDate");
            intent = new Intent(getActivity(), DOrderMealDetailAct.class);
            intent.putExtra("restId", itemBean.getExt().getrId());
        } else if ("2".equals(itemBean.getType()))
        { //进入直播 或者 回放
            FTrendsItemBean.ExtBean extBean = itemBean.getExt();
            if ("1".equals(extBean.getLiveStatus()))
            {
                CommonUtils.startLiveProxyAct(getActivity(), LiveRecord.ROOM_MODE_MEMBER, "", "", "", extBean.getRoomId(), null, EamCode4Result.reqNullCode);
                return;
            } else
            {
                if (TextUtils.isEmpty(extBean.getVedio()))
                {
                    customAlertDialog.setMsg("直播已结束")
                            .setPositiveButton("我知道了", (v) ->
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
        }else if(TextUtils.equals("4",itemBean.getType()))
        {
            intent = new Intent(getActivity(), ColumnArticleDetailAct.class);
            FTrendsItemBean.ExtBean extBean = itemBean.getExt();
            if (extBean!=null)
            {
                intent.putExtra("articleId", extBean.getArticleId());
                intent.putExtra("columnName", extBean.getColumnName());
                intent.putExtra("columnTitle",extBean.getTitle());
                intent.putExtra("shareUrl",extBean.getShareUrl());
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
        }else
        {
            intent = new Intent(getActivity(), TrendsDetailAct.class);

            intent.putExtra("tId", itemBean.gettId());
            intent.putExtra("data", itemBean);
            getActivity().startActivityForResult(intent, EamConstant.EAM_OPEN_TRENDS_DETAIL);
            return;
        }
        startActivity(intent);
    }

    //增加阅读量方法
    private void refreshReadNum(FTrendsItemBean itemBean)
    {

        CommonUtils.serverIncreaseRead(mAct, itemBean.getTId(), new ICommonOperateListener()
        {
            @Override
            public void onSuccess(String response)
            {
                new Handler().postDelayed(() ->
                {
                    itemBean.setReadNum(response);
                    testReAdapter.notifyDataSetChanged(true);
                }, 700);
            }

            @Override
            public void onError(String code, String msg)
            {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Logger.t(TAG).d("requestCode>>" + requestCode + "|" + resultCode);
        int toPosition = 0;
        String tId = "";

        switch (requestCode)
        {
            case EamConstant.EAM_OPEN_TRENDS_PUBLISH:
                if (resultCode == RESULT_OK)
                {
                    Bundle bundle = data.getBundleExtra("publish");
                    FTrendsItemBean trendsItemBean = (FTrendsItemBean) bundle.getSerializable("FTrendsItemBean");
                    fTrendsItemList.add(1, trendsItemBean);
                    if (testReAdapter != null)
                    {
                        testReAdapter.notifyDataSetChanged(false);
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
                    ((FTrendsItemBean) fTrendsItemList.get(index)).setLikedNum(likedNum);
                    ((FTrendsItemBean) fTrendsItemList.get(index)).setCommentNum(commentNum);
                    ((FTrendsItemBean) fTrendsItemList.get(index)).setIsLike(isLike);
                    ((FTrendsItemBean) fTrendsItemList.get(index)).setReadNum(readNum);
                    testReAdapter.notifyDataSetChanged(true);
                } else if (resultCode == RESULT_TRENDS_DELETE)
                {
                    String deleteTid = data.getStringExtra("tId");
                    FTrendsItemBean fTrendsItemBean = new FTrendsItemBean();
                    fTrendsItemBean.settId(deleteTid);
                    int index = fTrendsItemList.indexOf(fTrendsItemBean);
                    if (index >= 0)
                    {
                        fTrendsItemList.remove(index);
                        testReAdapter.notifyDataSetChanged(false);
                    }
                }
                break;
            case EamConstant.EAM_OPEN_TRENDS_PLAY_VIDEO:
                if (resultCode == RESULT_OK)
                {
                    toPosition = data.getIntExtra("position", 0);
                }
                break;
            default:

                break;

        }
        final int finalToPosition = toPosition;
        new Handler().postDelayed(() ->
        {
//            checkPlayVideo(false, finalToPosition);
        },800);
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        Logger.t(TAG).d("错误码为：%s", code);
        switch (interfaceName)
        {
            case NetInterfaceConstant.TrendC_FocusTrendList:
                break;
            case NetInterfaceConstant.TrendC_userTrends:
                break;
            default:
                break;
        }
    }


    @Override
    public void getUserTrendsCallback(String type, List<FTrendsItemBean> trendsList)
    {
        Logger.t(TAG).d("trendsList>" + trendsList);
        if (trendsList != null)
        {
            if (TextUtils.equals(type, "refresh"))
            {
                fTrendsItemList.clear();
            }

            if (trendsList.isEmpty())
                testReAdapter.setHasMore(false);
            else
                testReAdapter.setHasMore(true);

            trendsList.removeAll(fTrendsItemList);
            for (FTrendsItemBean fTrendsItemBean : trendsList)
            {
                if (!(EamApplication.getInstance().getChannelResult == 1 && "3".equals(fTrendsItemBean.getType())))// 根据channelResult 去除游戏动态
                    fTrendsItemList.add(fTrendsItemBean);
            }
            testReAdapter.setList(fTrendsItemList);
        }
        srrvRecyview.setRefresh(false);
//        checkPlayVideo(true, -1);
    }

    @Override
    public void getLikeTrendsSuccessCallback(int position, String flg, int likeNum)
    {

        FTrendsItemBean itemBean = fTrendsItemList.get(position);
        itemBean.setIsLike(flg);
        itemBean.setLikedNum(String.valueOf(likeNum));
        testReAdapter.notifyDataSetChanged(true);

    }

    public void reFreshInfo(boolean reFresh, String type)
    {
        if (reFresh)
        {
            if (mPresenter != null)
                mPresenter.getUserTrends(TextUtils.isEmpty(luid) ? id : luid, "0", "8", type);
        }
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
}

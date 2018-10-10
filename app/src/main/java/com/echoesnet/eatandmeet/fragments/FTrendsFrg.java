package com.echoesnet.eatandmeet.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import com.echoesnet.eatandmeet.models.bean.UnFocusVUserBean;
import com.echoesnet.eatandmeet.models.bean.UnFocusVuserItemBean;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.presenters.ImpIFTrendsPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IFTrendsView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.LoadFootView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.FTrendsAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.video.CustomManager;
import com.echoesnet.eatandmeet.views.widgets.video.ScrollCalculatorHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

import static android.app.Activity.RESULT_OK;
import static com.echoesnet.eatandmeet.activities.TrendsDetailAct.RESULT_TRENDS_DELETE;

/**
 * 动态列表
 */
public class FTrendsFrg extends MVPBaseFragment<FTrendsFrg, ImpIFTrendsPre> implements IFTrendsView, AbsListView.OnScrollListener, FTrendsAdapter.TrendsItemClick, View.OnTouchListener
{
    private final static String TAG = FTrendsFrg.class.getSimpleName();
    private final String DEFAULT_NUM = "20";
    private final String FIRST_LOAD_NUM = "10";

    @BindView(R.id.pull_to_refresh_trends)
    PullToRefreshListView refreshListView;
    @BindView(R.id.tv_trends_msg)
    TextView trendsMsgTv;
    private Unbinder unbinder;
    private FTrendsAdapter fTrendsAdapter;
    private List<FTrendsItemBean> fTrendsItemList;
    private UnFocusVUserBean mUnFocusVUserBean;
    private int visibleItemCount, firstVisibleItem;
    private int isShowingPosition;
    private String isShowingTid;
    private CustomAlertDialog customAlertDialog;
    private boolean isShowingVideo = false;
    private boolean isShowGame = false;
    private boolean isShowUnFocus = false;
    private Activity mAct;

    private int msgNum;

    // 没有更多内容时添加的底部提示布局
    private View footView;
    // 再没有更多数据获取时,禁止列表上拉加载动作
    private boolean pullMove = true;
    private ListView listView;
    private static FTrendsFrg fTrendsFrg;
    private ScrollCalculatorHelper scrollCalculatorHelper;

    public PullToRefreshListView getRefreshListView()
    {
        return refreshListView;
    }

    public FTrendsAdapter getfTrendsAdapter()
    {
        return fTrendsAdapter;
    }

    public List<FTrendsItemBean> getfTrendsItemList()
    {
        return fTrendsItemList;
    }

    public ImpIFTrendsPre getPresenter(){
        return mPresenter;
    }

    public static FTrendsFrg getInstance(){
        if (fTrendsFrg != null)
            return fTrendsFrg;
        else
            return new FTrendsFrg();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        fTrendsFrg = this;
        mPresenter.registerBroadcastReceiver();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        fTrendsFrg = this;
        View view = inflater.inflate(R.layout.frg_ftrends, container, false);
        unbinder = ButterKnife.bind(this, view);
        mAct = getActivity();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        fTrendsItemList = new ArrayList<>();
        fTrendsAdapter = new FTrendsAdapter(this.getActivity(), fTrendsItemList);
        fTrendsAdapter.setTrendsItemClick(this);
        refreshListView.getRefreshableView().setAdapter(fTrendsAdapter);
        refreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        refreshListView.setOnScrollListener(this);
        listView = refreshListView.getRefreshableView();
        listView.setOnTouchListener(this);
        footView = LayoutInflater.from(getActivity()).inflate(R.layout.footview_normal_list, null);
        refreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //mPresenter.getGameList();
                mPresenter.getFTrends("refresh", "0", DEFAULT_NUM);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (mPresenter != null)
                {
                    mPresenter.getFTrends("add", String.valueOf(isShowGame ? fTrendsItemList.size() - 1 : fTrendsItemList.size()), DEFAULT_NUM);
                    LoadFootView.showFootView(listView, false, footView, null);
                }
            }
        });
        int playTop = CommonUtil.getScreenHeight(mAct) / 2 - CommonUtil.dip2px(mAct, 100);
        int playBottom = CommonUtil.getScreenHeight(mAct) / 2 + CommonUtil.dip2px(mAct, 100);
        scrollCalculatorHelper = new ScrollCalculatorHelper(R.id.ftrends_uvideo_view, playTop, playBottom,getActivity());
        customAlertDialog = new CustomAlertDialog(getActivity())
                .builder()
                .setTitle("提示")
                .setCancelable(false);
        //  mPresenter.getGameList();
        mPresenter.getFTrends("add", String.valueOf(isShowGame ? fTrendsItemList.size() - 1 : fTrendsItemList.size()), FIRST_LOAD_NUM);
        VideoOptionModel videoOptionModel = new VideoOptionModel(tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        List<VideoOptionModel> list = new ArrayList<>();
        list.add(videoOptionModel);
        CustomManager.getCustomManager(FTrendsAdapter.TAG).setOptionModelList(list);
        registerBroadcastReceiver();
    }

    private void registerBroadcastReceiver()
    {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(EamConstant.EAM_REFRESH_TREND_MSG);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
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
    protected String getPageName()
    {
        return TAG;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Logger.t(TAG).d("requestCode>>" + requestCode + "|" + resultCode);
        int toPosition = 0;
        String tId = "";
        boolean isFocus = false;
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
                            fTrendsItemList.add(isShowGame ? fTrendsFrg.isShowUnFocus ? 2 : 1 : fTrendsFrg.isShowUnFocus ? 1 : 0, trendsItemBean);
                        if (fTrendsAdapter != null)
                            fTrendsAdapter.notifyDataSetChanged(false);
                        if (refreshListView != null)
                        {
                            refreshListView.getRefreshableView().setSelection(0);
                        }
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
            case EamConstant.EAM_OPEN_CNEW_USER_INFO:
                if (resultCode == RESULT_OK)
                {
                    if (data != null)
                    {
                        toPosition = data.getIntExtra("position", 0);
                        isFocus = data.getBooleanExtra("isFocus", false);
                    }

                    if (mUnFocusVUserBean != null && isFocus)
                    {
                        List<UnFocusVuserItemBean> focusVuserItemBeanList = mUnFocusVUserBean.getFocusVuserList();

                        if (toPosition < focusVuserItemBeanList.size())
                            focusVuserItemBeanList.remove(toPosition);
                        if (focusVuserItemBeanList.size() == 0)
                        {
                            isShowUnFocus = false;
                            fTrendsItemList.remove(mUnFocusVUserBean);
                        }
                    }
                    fTrendsAdapter.notifyDataSetChanged(false);
                }
                break;
        }
        final int finalToPosition = toPosition;
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // TODO: 2018/2/26 0026 进入播放
//                checkPlayVideo(false, finalToPosition);
            }
        }, 800);
    }


    @Override
    public void onDestroy()
    {
        CustomManager.releaseAllVideos(FTrendsAdapter.TAG);
        mPresenter.unRegisterReceiver();
        super.onDestroy();
        unbinder.unbind();
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

    public void scroll2Top()
    {
        refreshListView.getRefreshableView().setSelection(0);
        mPresenter.getFTrends("refresh", "0", DEFAULT_NUM);
    }

    public int getMsgNum()
    {
        return msgNum;
    }

    public TextView getTrendsMsgTv()
    {
        return trendsMsgTv;
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        refreshListView.onRefreshComplete();
        fTrendsAdapter.notifyDataSetChanged(true);
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        Logger.t(TAG).d("错误码为：%s", code);
        switch (interfaceName)
        {
            case NetInterfaceConstant.TrendC_trends:
                refreshListView.onRefreshComplete();
                break;
            default:
                break;
        }
    }

    @Override
    public void getTrendsCallback(String type, String msgNum, List<FTrendsItemBean> trendsList)
    {
    
        if ("refresh".equals(type) && fTrendsItemList != null)
            fTrendsItemList.clear();
        try
        {
            refreshListView.onRefreshComplete();
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
            Intent broadcastIntent = new Intent(EamConstant.EAM_REFRESH_TREND_MSG);
            broadcastIntent.putExtra("trend_msg", msgNum);
            getActivity().sendBroadcast(broadcastIntent);
            if ("refresh".equals(type))
            {
                LoadFootView.showFootView(listView, false, footView, null);
                pullMove = true;
                refreshListView.getRefreshableView().setSelection(0);
                try
                {
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            CustomManager.onPauseAll();
                            scrollCalculatorHelper.playVideo(listView);
                        }
                    }, 300);
                } catch (Exception e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
            }
            if (trendsList != null && trendsList.size() == 0)
            {
                LoadFootView.showFootView(listView, true, footView, null);
                pullMove = false;
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
                this.msgNum = msgNumInt;
            } catch (NumberFormatException e)
            {
                e.printStackTrace();
                Logger.t(TAG).d(e.getMessage());
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

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
    public void getLikeTrendsSuccessCallback(View view, int position, String flg, int likeNum)
    {
        try
        {
            TextView tvPraise = (TextView) view;
            if ("1".equals(flg))
                tvPraise.setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
            else
                tvPraise.setTextColor(ContextCompat.getColor(mAct, R.color.C0323));
            tvPraise.setText(String.format("{eam-p-praise @dimen/f2} %s", likeNum));

            FTrendsItemBean itemBean = (FTrendsItemBean) fTrendsItemList.get(position);
            itemBean.setIsLike(flg);
            itemBean.setLikedNum(String.valueOf(likeNum));
//            fTrendsAdapter.notifyDataSetChanged(true);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {

    }

    @Override
    public void deleteCommentSuc(int position)
    {
        ToastUtils.showShort("删除动态成功");
        fTrendsItemList.remove(position);
        fTrendsAdapter.notifyDataSetChanged(false);
    }

//    @Override
//    public void getGameListCallback(List<GameItemBean> gameItemBeanList)
//    {
//        try
//        {
//            fTrendsItemList.clear();
//            if (gameItemBeanList.size() > 0)
//            {
//                fTrendsItemList.add(gameItemBeanList);
//                isShowGame = true;
//            }
//            else
//            {
//                isShowGame = false;
//            }
//            mPresenter.getFTrends("refresh", "0", DEFAULT_NUM);
//            //mPresenter.getUnFocusVuser();这个功能废弃了
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void PublishSuccess(String tid, String stamp)
    {
        FTrendsItemBean fTrendsItemBean = new FTrendsItemBean();
        fTrendsItemBean.setStamp(stamp);
        int index = fTrendsItemList.indexOf(fTrendsItemBean);
        Logger.t(TAG).d("stamp>>" + stamp + "|index>>" + index);
        if (index >= 0 && index < fTrendsItemList.size())
            ((FTrendsItemBean) fTrendsItemList.get(index)).settId(tid);
    }

    @Override
    public void getUnFocusVuser(UnFocusVUserBean unFocusVUserBean)
    {
        try
        {
            this.mUnFocusVUserBean = unFocusVUserBean;
            List<UnFocusVuserItemBean> unFocusVuserList = mUnFocusVUserBean.getFocusVuserList();
            if (unFocusVuserList != null && unFocusVuserList.size() > 0)
            {
                isShowUnFocus = true;
                // fTrendsItemList.add(mUnFocusVUserBean);
            }
            mPresenter.getFTrends("refresh", "0", DEFAULT_NUM);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void focusUserCallback(int position)
    {
        ToastUtils.showShort("关注成功");
        List<UnFocusVuserItemBean> focusVuserItemBeanList = mUnFocusVUserBean.getFocusVuserList();
        if (position < focusVuserItemBeanList.size())
            focusVuserItemBeanList.remove(position);
        if (focusVuserItemBeanList.size() == 0)
        {
            isShowUnFocus = false;
            fTrendsItemList.remove(mUnFocusVUserBean);
        }
        fTrendsAdapter.notifyDataSetChanged(false);
    }

    @Override
    protected ImpIFTrendsPre createPresenter()
    {
        return new ImpIFTrendsPre();
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


    /**
     * 刷新数据
     */
    public void refreshData()
    {
        if (mPresenter != null)
            mPresenter.getFTrends("refresh", "0", DEFAULT_NUM);
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
                    if (!TextUtils.equals("0", intent.getStringExtra("trend_msg")))
                        trendsMsgTv.setVisibility(View.VISIBLE);
                    else
                        trendsMsgTv.setVisibility(View.GONE);
                    break;
            }
        }
    };

    /**
     * 显示新手引导
     */
    public void showNewbieGuide()
    {
        ImageView imageView = new ImageView(getActivity());
        imageView.setImageResource(R.drawable.comments_cool);
        PopupWindow popupWindow = new PopupWindow(imageView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
//                SharePreUtils.setIsNewBieTrends(getActivity(),false);
                NetHelper.saveShowNewbieStatus(getActivity(), "2");
            }
        });
        ListView view = refreshListView.getRefreshableView();
        for (int i = 0; i < view.getChildCount(); i++)
        {
            TextView textView = (TextView) view.getChildAt(i).findViewById(R.id.tv_comment);
            if (textView != null)
            {
                popupWindow.showAsDropDown(textView, -CommonUtils.dp2px(getActivity(), 58), 0);
                return;
            }
        }
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        Logger.t(TAG).d("firstVisibleItem:" + firstVisibleItem + " | " + visibleItemCount + " | " + totalItemCount);

        if (firstVisibleItem == 0)
        {
            View firstVisibleItemView = refreshListView.getRefreshableView().getChildAt(0);
            if (firstVisibleItemView != null && firstVisibleItemView.getTop() == 0)
            {
                Logger.t(TAG).d("滑动到顶部");
            }
        }

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
            if (map == null)
            {
                for (Map.Entry<String, CustomManager> customManagerEntry : map.entrySet()) {
                    CustomManager customManager = customManagerEntry.getValue();
                    //当前播放的位置
                    int position = customManager.getPlayPosition();
                    //对应的播放列表TAG
                    if (customManager.getPlayTag().equals(FTrendsAdapter.TAG)
                            && (position < firstVisibleItem || position > lastVisibleItem)) {
                        CustomManager.releaseAllVideos(customManagerEntry.getKey());
                        removeKey.add(customManagerEntry.getKey());
                    }
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

    private OnFindFrgShowTitleListener titleListener;

    public interface OnFindFrgShowTitleListener
    {
        void onMoveOrientation(String moveOrientation);
    }

    public void setOnFindFrgShowTitleListener(OnFindFrgShowTitleListener findFrgShowTitleListener)
    {
        titleListener = findFrgShowTitleListener;
    }

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
            Logger.t(TAG).d("isVuser>>>" + itemBean.getIsVuser());
            GSYBaseVideoPlayer gsyBaseVideoPlayer = scrollCalculatorHelper.getCurrentPlayer();
            if (gsyBaseVideoPlayer != null)
                intent.putExtra("position", gsyBaseVideoPlayer.getCurrentState() != GSYVideoView.CURRENT_STATE_AUTO_COMPLETE ?
                        gsyBaseVideoPlayer.getCurrentPositionWhenPlaying() : 0);
            CommonUtils.playVideo(mAct, intent, itemBean.getThumbnails(), view);

        }
    }

    @Override
    public void itemClick(int position, FTrendsItemBean itemBean)
    {
        Intent intent = new Intent(getActivity(), TrendsDetailAct.class);
        intent.putExtra("tId", itemBean.gettId());
        intent.putExtra("data", itemBean);
        GSYBaseVideoPlayer gsyBaseVideoPlayer = scrollCalculatorHelper.getCurrentPlayer();
        if (gsyBaseVideoPlayer != null)
            intent.putExtra("position",gsyBaseVideoPlayer.getCurrentState() != GSYVideoView.CURRENT_STATE_AUTO_COMPLETE ?
                    gsyBaseVideoPlayer.getCurrentPositionWhenPlaying() : 0);
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
    public void contentClick(FTrendsItemBean itemBean)
    {
        if (!"0".equals(itemBean.getType()) || TextUtils.isEmpty(itemBean.getUrl()))
            refreshReadNum(itemBean);
        Intent intent;
        if ("3".equals(itemBean.getType()))
        {
            if ("1".equals(itemBean.getExt().getGameType()))
            { // 直播间小游戏
                ((HomeAct) getActivity()).goToTable(2, "3");
                ToastUtils.showShort("请进入直播间后开始游戏");
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
                intent.putExtra("shareUrl", extBean.getShareUrl());
            }
        }
        else if ("5".equals(itemBean.getType()))
        {
            FTrendsItemBean.ExtBean extBean = itemBean.getExt();
            FPromotionBean pBean = new FPromotionBean();
            pBean.setWebUrl(extBean.getShareUrl());
            pBean.setActName(extBean.getTitle());
            pBean.setType("2");
            intent = new Intent(getActivity(), PromotionActionAct.class);
            intent.putExtra("fpBean", pBean);
        }
        else if ("6".equals(itemBean.getType()))
        {
            intent = new Intent(getActivity(), ClubDetailAct.class);
            intent.putExtra("clubId", itemBean.getExt().getHpId());
        } else if ("7".equals(itemBean.getType()))
        {
            FTrendsItemBean.ExtBean extBean = itemBean.getExt();
            FPromotionBean pBean = new FPromotionBean();
            pBean.setWebUrl(extBean.getShareUrl());
            pBean.setActName(extBean.getPageTitle());
            pBean.setActivityId(extBean.getActivityId());
            pBean.setType("2");
            intent = new Intent(getActivity(), PromotionActionAct.class);
            intent.putExtra("fpBean", pBean);
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
    public void focusClick(int position)
    {
        List<UnFocusVuserItemBean> focusVuserItemBeanList = mUnFocusVUserBean.getFocusVuserList();
        if (position < focusVuserItemBeanList.size())
            mPresenter.focusUser(focusVuserItemBeanList.get(position).getUId(), position);
    }

    private volatile float mLastTouchX;
    private volatile float mLastTouchY;

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mLastTouchX = x;// Remember where we started
                mLastTouchY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                // 触摸移动时的操作
//                if (!isShowingVideo)
//                    return false;
//                AbsListView view = refreshListView.getRefreshableView();
//                for (int i = 0; i < visibleItemCount; i++)
//                {
//                    if (view != null && view.getChildAt(i) != null && view.getChildAt(i).findViewById(R.id.ftrends_uvideo_view) != null)
//                    {
//                        final UVideoView uVideoView = (UVideoView) view.getChildAt(i).findViewById(R.id.ftrends_uvideo_view);
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
                String moveOrientation = "";
                if (mLastTouchY - y > 0)
                {
                    // 向上滑动
                    moveOrientation = "up";
                }
                else
                {
                    // 向下滑动
                    moveOrientation = "down";
                }
                if (titleListener != null)
                    titleListener.onMoveOrientation(moveOrientation);
                break;
        }
        return false;
    }

    private void refreshReadNum(FTrendsItemBean itemBean)
    {
        if (TextUtils.isEmpty(itemBean.getTId()))
            return;
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

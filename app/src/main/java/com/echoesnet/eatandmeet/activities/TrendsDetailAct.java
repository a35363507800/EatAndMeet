package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.live.LHotAnchorVideoAct;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.CommentsBean;
import com.echoesnet.eatandmeet.models.bean.FPromotionBean;
import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.presenters.ImpIFTrendsDetailPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IFTrendsDetailView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.TrendsDetailAdapter;
import com.echoesnet.eatandmeet.views.widgets.CommentInputView;
import com.echoesnet.eatandmeet.views.widgets.ContextMenuDialog;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.echoesnet.eatandmeet.views.widgets.video.MultiSampleVideo;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/13 0013
 * @description 动态详情页
 */
public class TrendsDetailAct extends MVPBaseActivity<TrendsDetailAct, ImpIFTrendsDetailPre> implements IFTrendsDetailView, AbsListView.OnScrollListener, TrendsDetailAdapter.trendsCommentItemClick
{
    private final String TAG = TrendsDetailAct.class.getSimpleName();
    public static final int RESULT_TRENDS_DELETE = 0x101;

    @BindView(R.id.list_view_trends_detail)
    PullToRefreshListView listViewTrendsDetail;
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.comment_input)
    CommentInputView commentInputView;
    EditText editComment;

    private Activity mAct;
    private String tId;//动态id
    private String commentId;//跳转到的评论id
    private String mCId;//要回复的评论的id
    private List<Map<String, TextView>> navBtn;
    private TrendsDetailAdapter trendsDetailAdapter;
    private FTrendsItemBean fTrendsItemBean;
    private List<CommentsBean> mCommentsList;
    private MultiSampleVideo uVideoView;
    private CustomAlertDialog customAlertDialog;
    private boolean isFirstRefresh = true;//第一次刷新
    private boolean isComplete = false;
    private int position;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_trends_detail);
        ButterKnife.bind(this);
        mAct = this;
        initTopBar();
        mCommentsList = new ArrayList<>();
        tId = getIntent().getStringExtra("tId");
        position = getIntent().getIntExtra("position", 0);
        commentId = getIntent().getStringExtra("commentId");
        commentInputView.setCommentInputListener((String content) -> mPresenter.commentTrends(tId, content, mCId));
        editComment = commentInputView.getEditComment();
        //listViewTrendsDetail
        listViewTrendsDetail.setMode(PullToRefreshBase.Mode.BOTH);
        listViewTrendsDetail.setOnScrollListener(this);
        listViewTrendsDetail.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (uVideoView != null)
                {
                    uVideoView.onVideoPause();
                    uVideoView.release();
                    uVideoView = null;
                }
                trendsDetailAdapter.setRefreshContent(true);
                mPresenter.getTrendsDetail("refresh", tId);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                mPresenter.getTrendComments("add", tId, String.valueOf(mCommentsList.size()), "10");
            }
        });
        fTrendsItemBean = (FTrendsItemBean) getIntent().getSerializableExtra("data");
        if (fTrendsItemBean != null)
        {
            trendsDetailAdapter = new TrendsDetailAdapter(this, fTrendsItemBean, mCommentsList);
            listViewTrendsDetail.setAdapter(trendsDetailAdapter);
            trendsDetailAdapter.notifyDataSetChanged(true);
            trendsDetailAdapter.setCommentItemClick(this);
            if (!TextUtils.isEmpty(tId))
                mPresenter.getTrendsDetail("refresh", tId);
            else
            {
                mPresenter.getTrendComments("refresh", tId, "0", "200");
                refreshTopBar();
            }

        }
        else
        {
            trendsDetailAdapter = new TrendsDetailAdapter(this, fTrendsItemBean, mCommentsList);
            listViewTrendsDetail.setAdapter(trendsDetailAdapter);
            trendsDetailAdapter.setCommentItemClick(this);
            mPresenter.getTrendsDetail("refresh", tId);
        }
        customAlertDialog = new CustomAlertDialog(mAct)
                .builder()
                .setTitle("提示")
                .setCancelable(false);
        commentInputView.showOrHideSoftInput(false);
    }

    private void initTopBar()
    {
        topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {

            @Override
            public void leftClick(View view)
            {
                Intent intent = new Intent();
                intent.putExtra("tId", fTrendsItemBean.gettId());
                intent.putExtra("likedNum", fTrendsItemBean.getLikedNum());
                intent.putExtra("commentNum", fTrendsItemBean.getCommentNum());
                intent.putExtra("isLike", fTrendsItemBean.getIsLike());
                intent.putExtra("readNum", fTrendsItemBean.getReadNum());
                if (uVideoView != null)
                    intent.putExtra("position", isComplete ? 0 : uVideoView.getPlayPosition());
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void right2Click(View view)
            {
                if ("0".equals(fTrendsItemBean.getFocus()))
                    mPresenter.focusUser(fTrendsItemBean.getUp(), "1");
                else if (SharePreUtils.getUId(mAct).equals(fTrendsItemBean.getUp()))
                {
                    customAlertDialog.setMsg("是否删除此动态?")
                            .setTitle("提示!")
                            .setNegativeButton("否", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    customAlertDialog.dismiss();
                                }
                            })
                            .setPositiveButton("是", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    mPresenter.deleteTrends(fTrendsItemBean.getTId());
                                }
                            });
                    customAlertDialog.show();
                }

            }
        }).setText(mAct.getResources().getString(R.string.trends_detail));
        navBtn = topBarSwitch.getNavBtns2(new int[]{1, 0, 0, 1});

    }

    @Override
    protected ImpIFTrendsDetailPre createPresenter()
    {
        return new ImpIFTrendsDetailPre();
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        if ("TREND_DELETED".equals(code))
        {
            ToastUtils.showShort("此动态已删除");
            finish();
        }
        else if ("COMMENT_DELETED".equals(code))
        {
            commentInputView.regain();
            mPresenter.getTrendsDetail("refreshComment", tId);
        }
    }

    @Override
    public void getTrendsDetailCallBack(String type, FTrendsItemBean trendsDetailBean)
    {
        fTrendsItemBean = trendsDetailBean;
        if (trendsDetailAdapter != null)
        {
            trendsDetailAdapter.setfTrendsItemBean(trendsDetailBean);
        }
        else
        {
        }
        trendsDetailAdapter.notifyDataSetChanged(true);
        mPresenter.getTrendComments(type, tId, "0", "200");
//        playOrPauseVideo(listViewTrendsDetail.getRefreshableView());
        refreshTopBar();
    }

    private void refreshTopBar()
    {
        TextView tv = navBtn.get(1).get(TopBarSwitch.NAV_BTN_ICON);
        if (fTrendsItemBean != null)
            if ("0".equals(fTrendsItemBean.getFocus()))
            {
                tv.setVisibility(View.VISIBLE);
                tv.setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
                tv.setText("关注TA");
                tv.setTextSize(16);
                tv.setEnabled(true);
            }
            else if ("1".equals(fTrendsItemBean.getFocus()))
            {
                tv.setVisibility(View.VISIBLE);
                tv.setText("已关注");
                tv.setTextSize(16);
                tv.setTextColor(ContextCompat.getColor(mAct, R.color.C0322));
                tv.setEnabled(false);
            }
            else if (SharePreUtils.getUId(mAct).equals(fTrendsItemBean.getUp()))
            {
                tv.setVisibility(View.VISIBLE);
                tv.setText("{eam-s-delete}");
                tv.setEnabled(true);
            }
            else
            {
                tv.setVisibility(View.GONE);
            }
    }

    @Override
    public void getTrendsCommentsCallBack(String type, List<CommentsBean> commentsList)
    {
        listViewTrendsDetail.onRefreshComplete();
        if ("refresh".equals(type) || "refreshComment".equals(type))
        {
            mCommentsList.clear();
            editComment.setHint("输入评论");
            mCId = "";
        }

        try
        {
            for (CommentsBean commentsItemBean : commentsList)
            {
                //去重复
                if (mCommentsList.contains(commentsItemBean))
                {
                    int index = mCommentsList.indexOf(commentsItemBean);
                    mCommentsList.remove(index);
                }
                mCommentsList.add(commentsItemBean);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("虑重错误" + e.getMessage());
        }



        trendsDetailAdapter.notifyDataSetChanged(false);
        if ("refresh".equals(type))
        {
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    playOrPauseVideo(-1);
                }
            }, 500);
        }
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (isFirstRefresh && !TextUtils.isEmpty(commentId))
                {
                    CommentsBean commentsBean = new CommentsBean();
                    commentsBean.setCommentId(commentId);
                    int index = mCommentsList.indexOf(commentsBean);
                    Logger.t(TAG).d("index>>" + index);
                    if (index >= 0)//需要+3 定位到相应的评论
                    {
                        editComment.setHint(String.format("回复%s:", TextUtils.isEmpty(mCommentsList.get(index).getRemark()) ?
                                mCommentsList.get(index).getNicName() : mCommentsList.get(index).getRemark()));
                        mCId = mCommentsList.get(index).getCommentId();
                        listViewTrendsDetail.getRefreshableView().smoothScrollToPosition(index +
                                (SharePreUtils.getUId(mAct).equals(fTrendsItemBean.getUp()) ? 4 : 3));
                    }
                    commentInputView.showOrHideSoftInput(true);
                    isFirstRefresh = false;
                }
            }
        }, 800);
    }

    @Override
    public void commentTrendsSucCallBack()
    {
        commentInputView.regain();
        trendsDetailAdapter.setRefreshContent(false);
        mPresenter.getTrendsDetail("refreshComment", tId);
    }

    @Override
    public void likeTrendsCallBack(String flg, int likeNum)
    {
        trendsDetailAdapter.setRefreshContent(false);
        mPresenter.getTrendsDetail("refreshComment", tId);
    }

    @Override
    public void focusCallBack()
    {
        ToastUtils.showShort("关注成功");
        fTrendsItemBean.setFocus("1");
        TextView tv = navBtn.get(1).get(TopBarSwitch.NAV_BTN_ICON);
        tv.setText("已关注");
        tv.setEnabled(false);
        tv.setTextSize(16);
        tv.setTextColor(ContextCompat.getColor(mAct, R.color.C0322));
    }

    @Override
    public void deleteTrendsSuc()
    {
        Intent intent = new Intent();
        intent.putExtra("tId", fTrendsItemBean.gettId());
        setResult(RESULT_TRENDS_DELETE, intent);
        finish();
    }

    @Override
    public void deleteCommentSuc(int position)
    {
        try
        {
            int commentNum = Integer.parseInt(fTrendsItemBean.getCommentNum());
            if (commentNum > 0)
                fTrendsItemBean.setCommentNum(String.valueOf(commentNum - 1));
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        mCommentsList.remove(position);
        trendsDetailAdapter.notifyDataSetChanged(false);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        switch (scrollState)
        {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE: // 停止滑动判断是否可以播放暂停
                playOrPauseVideo(-1);
                break;
        }
    }

    private void playOrPauseVideo(int toPosition)
    {
        AbsListView view = listViewTrendsDetail.getRefreshableView();
        int count = 0;
        for (int i = 0; i < 2; i++)
        {
            View childView = view.getChildAt(i);
            if (childView != null)
            {
                MultiSampleVideo uVideoView1 = childView.findViewById(R.id.ftrends_uvideo_view);
                Logger.t(TAG).d("uVideoView >>>> " + uVideoView + "findViewById>>>>>" + uVideoView1 + "|" + (uVideoView1 == uVideoView));
            }

            if (view != null && childView != null && childView.findViewById(R.id.ftrends_uvideo_view) != null)
            {
                MultiSampleVideo uVideoView1 = childView.findViewById(R.id.ftrends_uvideo_view);
//                if (uVideoView != null && uVideoView1 != uVideoView)
//                {
//                    uVideoView.onVideoPause();
//                    uVideoView.release();
//                    uVideoView = null;
//                }
                uVideoView =  childView.findViewById(R.id.ftrends_uvideo_view);
                final FrameLayout flThumbnail = (FrameLayout) childView.findViewById(R.id.fl_thumbnail);
                final ImageView imgThumbnail = (ImageView) childView.findViewById(R.id.img_thumbnail);
                Rect rect = new Rect();
                uVideoView.getLocalVisibleRect(rect);
                int videoHeight = uVideoView.getHeight();
                Logger.t(TAG).d("videoHeight:" + videoHeight + "===" + "rect.top:" + rect.top + "===" + "rect.bottom:" + rect.bottom);
                if (rect.bottom == videoHeight && rect.top <= videoHeight / 2 || rect.top == 0
                        && rect.bottom >= videoHeight / 2)
                {
                    if ("pause".equals(imgThumbnail.getTag()))
                    {
                        imgThumbnail.setTag("playing");
                        if (toPosition >= 0)
                            uVideoView.seekTo(toPosition);
                        new Handler().postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (uVideoView!=null)
                                uVideoView.onVideoResume();
                                isComplete = false;
                                if (position > 0)
                                {
                                    uVideoView.seekTo(position);
                                    position = 0;
                                }
                                imgThumbnail.setTag("playing");
                                //缩略图延时消失
                                new Handler().postDelayed(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        flThumbnail.setVisibility(View.GONE);
                                    }
                                }, 800);
                            }
                        }, 500);
                        return;
                    }
                    else if ("playing".equals(imgThumbnail.getTag()))
                    {
                        return;
                    }
                    else if ("complete".equals(imgThumbnail.getTag()))
                    {
                        Logger.t(TAG).d("toPosition>>>" + toPosition);
                        if (toPosition >= 0)
                        {
                            uVideoView.seekTo(toPosition);
                            new Handler().postDelayed(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    uVideoView.onVideoResume();
                                    isComplete = false;
                                    imgThumbnail.setTag("playing");
                                    //缩略图延时消失
                                    new Handler().postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            flThumbnail.setVisibility(View.GONE);
                                        }
                                    }, 800);
                                }
                            }, 500);
                        }
                        return;
                    }
                    uVideoView.setVideoAllCallBack(new GSYSampleCallBack(){
                        @Override
                        public void onPrepared(String url, Object... objects)
                        {
                            super.onPrepared(url, objects);
                            GSYVideoManager.instance().setNeedMute(true);
                            flThumbnail.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAutoComplete(String url, Object... objects)
                        {
                            super.onAutoComplete(url, objects);
                            isComplete = true;
                            imgThumbnail.setTag("complete");
                            flThumbnail.setVisibility(View.VISIBLE);
                        }
                    });
                    if (position > 0)
                    {
                        uVideoView.setSeekOnStart(position);
                        position = 0;
                    }
                    uVideoView.startPlayLogic();
                    imgThumbnail.setTag("playing");
                }
                else if (rect.bottom == videoHeight && rect.top > videoHeight / 2 || rect.top == 0
                        && rect.bottom < videoHeight / 2)
                {
                    if ("complete".equals(imgThumbnail.getTag()))
                        imgThumbnail.setTag("");
                    if (uVideoView != null)
                    {
                        uVideoView.onVideoPause();
                        imgThumbnail.setTag("pause");
                        flThumbnail.setVisibility(View.VISIBLE);
                    }
                }
            }
            else
            {
                if (++count > 1)
                {
                    if (uVideoView != null)
                    {
                        uVideoView.onVideoPause();
                        uVideoView.release();
                    }
                }
            }
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
        {
            if (commentInputView.hideInputOrEmoji(mAct, ev))
                mCId = "";
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev))
        {
            return true;
        }
        return onTouchEvent(ev);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        int position = 0;
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case EamConstant.EAM_OPEN_TRENDS_PLAY_VIDEO:
                if (resultCode == RESULT_OK)
                {
                    fTrendsItemBean.setFocus(data.getStringExtra("isFocus"));
                    position = data.getIntExtra("position", 0);
                    refreshTopBar();
                }
                break;
        }
        playOrPauseVideo(position);
    }

    @Override
    public void onBackPressed()
    {
        if (fTrendsItemBean != null)
        {
            Intent intent = new Intent();
            intent.putExtra("tId", fTrendsItemBean.gettId());
            intent.putExtra("likedNum", fTrendsItemBean.getLikedNum());
            intent.putExtra("commentNum", fTrendsItemBean.getCommentNum());
            intent.putExtra("isLike", fTrendsItemBean.getIsLike());
            intent.putExtra("readNum", fTrendsItemBean.getReadNum());
            if (uVideoView != null)
                intent.putExtra("position", isComplete ? 0 : uVideoView.getPlayPosition());
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    @Override
    protected void onDestroy()
    {
        if (uVideoView != null)
        {
            uVideoView.onVideoPause();
            uVideoView.release();
        }
        super.onDestroy();
    }

    @Override
    public void commentContentClick(String cid, String nickName)
    {
        if (TextUtils.equals(cid, SharePreUtils.getUId(mAct)))
            return;
        commentInputView.showOrHideSoftInput(true);
        editComment.setHint(String.format("回复%s:", nickName));
        mCId = cid;
    }

    @Override
    public void praiseClick(String flg)
    {
        mPresenter.likeTrends(tId, fTrendsItemBean.getIsLike(), fTrendsItemBean.getLikedNum());
    }

    @Override
    public void commentClick()
    {
        commentInputView.showOrHideSoftInput(true);
    }

    @Override
    public void contentClick()
    {
        if (!"0".equals(fTrendsItemBean.getType()) || TextUtils.isEmpty(fTrendsItemBean.getUrl()))
            refreshReadNum(fTrendsItemBean);
        if ("1".equals(fTrendsItemBean.getType())) //进入餐厅
        {
            SharePreUtils.setToOrderMeal(mAct, "noDate");
            Intent intent = new Intent(mAct, DOrderMealDetailAct.class);
            intent.putExtra("restId", fTrendsItemBean.getExt().getrId());
            startActivity(intent);
        }
        else if ("2".equals(fTrendsItemBean.getType()))
        { //进入直播 或者 回放
            FTrendsItemBean.ExtBean extBean = fTrendsItemBean.getExt();
            if ("1".equals(extBean.getLiveStatus()))
            {
                CommonUtils.startLiveProxyAct(mAct, LiveRecord.ROOM_MODE_MEMBER, "", "", "", extBean.getRoomId(), null, EamCode4Result.reqNullCode);
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
                Intent intent = new Intent(mAct, LHotAnchorVideoAct.class);
                intent.putExtra("luid", extBean.getAnchor());
                intent.putExtra("video", extBean.getVedio());
                intent.putExtra("roomId", extBean.getRoomId());
                startActivity(intent);
            }
        }
        else if ("3".equals(fTrendsItemBean.getType()))
        {
            if ("1".equals(fTrendsItemBean.getExt().getGameType()))
            {
                if (CommonUtils.isInLiveRoom)//在直播间内 要关闭直播间
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
            if (CommonUtils.isInLiveRoom)//在直播间内 要关闭直播间
            {
                ToastUtils.showShort("请退出当前直播间");
                return;
            }
            Intent intent = new Intent(mAct, GameAct.class);
            intent.putExtra("gameUrl", fTrendsItemBean.getExt().getGameUrl());
            intent.putExtra("gameName", fTrendsItemBean.getExt().getGameName());
            intent.putExtra("gameId", fTrendsItemBean.getExt().getGameId());
            startActivity(intent);
        }
        else if ("4".equals(fTrendsItemBean.getType()))
        {
            FTrendsItemBean.ExtBean extBean = fTrendsItemBean.getExt();
            Intent intent = new Intent(mAct, ColumnArticleDetailAct.class);
            if (extBean != null)
            {
                intent.putExtra("articleId", extBean.getArticleId());
                intent.putExtra("columnName", extBean.getColumnName());
                intent.putExtra("columnTitle", extBean.getTitle());
                intent.putExtra("imgUrl", extBean.getImgUrl());
            }
            startActivity(intent);
        }
        else if ("5".equals(fTrendsItemBean.getType())||"7".equals(fTrendsItemBean.getType()))
        {
            FTrendsItemBean.ExtBean extBean = fTrendsItemBean.getExt();
            FPromotionBean pBean = new FPromotionBean();
            pBean.setWebUrl(extBean.getShareUrl());
            pBean.setActName(extBean.getTitle());
            pBean.setActivityId(extBean.getActivityId());
            pBean.setType("2");
            Intent intent = new Intent(mAct, PromotionActionAct.class);
            intent.putExtra("fpBean", pBean);
            startActivity(intent);
        }
        else if ("6".equals(fTrendsItemBean.getType()))
        {
            Intent intent = new Intent(mAct, ClubDetailAct.class);
            intent.putExtra("clubId", fTrendsItemBean.getExt().getHpId());
            startActivity(intent);
        }
    }

    @Override
    public void commentLongClick(final int index, final CommentsBean commentsBean)
    {
        Logger.t(TAG).d("position>>" + index);
        new ContextMenuDialog(new ContextMenuDialog.MenuDialogCallBack()
        {
            @Override
            public void menuOnClick(String menuItem, int position)
            {
                switch (menuItem)
                {
                    case "复制":
                        ClipboardManager cm = (ClipboardManager) mAct.getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setPrimaryClip(ClipData.newPlainText("content", commentsBean.getComment()));
                        break;
                    case "删除评论":
                        if (mPresenter!=null)
                        mPresenter.deleteComment(index, fTrendsItemBean.getTId(), commentsBean.getCommentId());
                        break;
                        default:
                            break;
                }
             //   mPresenter.deleteComment(index, fTrendsItemBean.getTId(), commentsBean.getCommentId());
            }
        }).showContextMenuBox(mAct, Arrays.asList(new String[]{"复制","删除评论"}));
    }

    @Override
    public void videoClick(View view, FTrendsItemBean fTrendsItemBean)
    {
        if (uVideoView != null)
        {
            refreshReadNum(fTrendsItemBean);
            Intent intent = new Intent(mAct, TrendsPlayVideoAct.class);
            intent.putExtra("url", fTrendsItemBean.getUrl());
            intent.putExtra("position", isComplete ? 0 : uVideoView.getPlayPosition());
            intent.putExtra("isFocus", fTrendsItemBean.getFocus());
            intent.putExtra("tId", fTrendsItemBean.gettId());
            intent.putExtra("phUrl", fTrendsItemBean.getPhurl());
            intent.putExtra("nickName", fTrendsItemBean.getNicName());
            intent.putExtra("distance", fTrendsItemBean.getDistance());
            intent.putExtra("sex", fTrendsItemBean.getSex());
            intent.putExtra("level", fTrendsItemBean.getLevel());
            intent.putExtra("age", fTrendsItemBean.getAge());
            intent.putExtra("isFocus", fTrendsItemBean.getFocus());
            intent.putExtra("uid", fTrendsItemBean.getUp());
            intent.putExtra("showType", fTrendsItemBean.getShowType());
            intent.putExtra("isVuser", fTrendsItemBean.getIsVuser());
            CommonUtils.playVideo(mAct, intent, fTrendsItemBean.getThumbnails(), view);
        }
    }

    @Override
    public void picClick(FTrendsItemBean fTrendsItemBean)
    {
        refreshReadNum(fTrendsItemBean);
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
                        trendsDetailAdapter.notifyDataSetChanged(true);
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

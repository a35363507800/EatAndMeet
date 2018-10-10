package com.echoesnet.eatandmeet.views.widgets.video;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.orhanobut.logger.Logger;
import com.shuyu.gsyvideoplayer.utils.NetworkUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 计算滑动，自动播放的帮助类
 */

public class ScrollCalculatorHelper
{
    public final static String TAG = ScrollCalculatorHelper.class.getSimpleName();
    private int firstVisible = 0;
    private int lastVisible = 0;
    private int visibleCount = 0;
    private int playId;
    private int rangeTop;
    private int rangeBottom;
    private PlayRunnable runnable;
    private int currentPosition;
    private int isShowingPosition;
    private GSYBaseVideoPlayer mCurrentPlayer;
    private FrameLayout mCurrentFlThumb;
    private Activity mAct ;

    private Handler playHandler = new Handler();

    public ScrollCalculatorHelper(int playId, int rangeTop, int rangeBottom,Activity mAct)
    {
        this.playId = playId;
        this.rangeTop = rangeTop;
        this.rangeBottom = rangeBottom;
        this.mAct = mAct;
    }

    public void onScrollStateChanged(ViewGroup view, int scrollState)
    {
        switch (scrollState)
        {
            case RecyclerView.SCROLL_STATE_IDLE:
                playVideo(view);
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                Logger.t(TAG).d(">>>>>>>>>>>>>>>>>>>>>>>SCROLL_STATE_DRAGGING ");
                break;
        }
    }

    public GSYBaseVideoPlayer getCurrentPlayer()
    {
        return mCurrentPlayer;
    }

    public void onScroll(ViewGroup view, int firstVisibleItem, int lastVisibleItem, int visibleItemCount)
    {
//        if (firstVisible == firstVisibleItem)
//        {
//            return;
//        }
        firstVisible = firstVisibleItem;
        lastVisible = lastVisibleItem;
        visibleCount = visibleItemCount;
    }


    public void playVideo(ViewGroup view)
    {

        if (view == null)
        {
            return;
        }
        GSYBaseVideoPlayer gsyBaseVideoPlayer = null;
        FrameLayout flThumbnail = null;
        boolean needPlay = false;
        int postion = 0;

        for (int i = 0; i < visibleCount; i++)
        {
            if (view.getChildAt(i) != null && view.getChildAt(i).findViewById(playId) != null)
            {
                MultiSampleVideo player =  view.getChildAt(i).findViewById(playId);
//                postion = (int) player.getTag();
                Rect rect = new Rect();
                player.getLocalVisibleRect(rect);
                int videoheight = player.getHeight();
                flThumbnail = view.getChildAt(i).findViewById(R.id.fl_thumbnail);
                currentPosition = (int) flThumbnail.getTag();
                Logger.t(TAG).d("isShowingPosition >" + isShowingPosition + "|position=" + currentPosition + "|videoheight:" + videoheight +
                        "|rect.top:" + rect.top + "|rect.bottom:" + rect.bottom);
                //说明第一个完全可视
                if (((rect.top < videoheight / 2 && rect.bottom == videoheight && ((isShowingPosition > currentPosition) || currentPosition == isShowingPosition))
                        || (rect.top == 0 && rect.bottom > videoheight / 2) && isShowingPosition != currentPosition))
                {
                    gsyBaseVideoPlayer = player;
                    Logger.t(TAG).d("getCurrentState() >>" + player.getCurrentPlayer().getCurrentState());
                    if ((player.getCurrentPlayer().getCurrentState() == GSYBaseVideoPlayer.CURRENT_STATE_NORMAL
                            || player.getCurrentPlayer().getCurrentState() == GSYBaseVideoPlayer.CURRENT_STATE_ERROR
                            || player.getCurrentPlayer().getCurrentState() == GSYBaseVideoPlayer.CURRENT_STATE_AUTO_COMPLETE) && !player.isInPlayingState())
                    {
                        needPlay = true;
                    } else if (player.getCurrentPlayer().getCurrentState() == GSYBaseVideoPlayer.CURRENT_STATE_PAUSE)
                    {
                        Logger.t(TAG).d("恢复播放>>>>>>>>>>>>>>>>||" + currentPosition + "||>>" + player.getCurrentPositionWhenPlaying());
                        if (mCurrentPlayer != null && mCurrentPlayer.isInPlayingState())
                        {
                            mCurrentPlayer.onVideoPause();
                            mCurrentFlThumb.setVisibility(View.VISIBLE);
                        }
                        IMediaPlayer iMediaPlayer = player.getGSYVideoManager().getMediaPlayer();
                        if (iMediaPlayer != null)
                            iMediaPlayer.start();
                        mCurrentPlayer = player;
                        mCurrentFlThumb = flThumbnail;
                        flThumbnail.setVisibility(View.GONE);
                    }
                    break;
                } else if ((rect.top <= 0 && rect.bottom <= videoheight / 2)
                        || (rect.top > videoheight / 2 && rect.bottom == videoheight))
                {
                    Logger.t(TAG).d("暂停>>>>>>>>>>>>>>>> isShowingPosition >>>" + isShowingPosition + "|currentPosition >>" + currentPosition);
                    if (isShowingPosition == currentPosition && player.isInPlayingState())
                    {
                        IMediaPlayer iMediaPlayer = player.getGSYVideoManager().getMediaPlayer();
                        if (iMediaPlayer != null)
                            iMediaPlayer.pause();
                        player.setState(GSYBaseVideoPlayer.CURRENT_STATE_PAUSE);
                        flThumbnail.setVisibility(View.VISIBLE);
                    }
                }

            }
        }

        if (gsyBaseVideoPlayer != null && needPlay)
        {
            if (runnable != null)
            {
                GSYBaseVideoPlayer tmpPlayer = runnable.gsyBaseVideoPlayer;
                playHandler.removeCallbacks(runnable);
                runnable = null;
                Logger.t(TAG).d("tmpPlayer == gsyBaseVideoPlayer>>>" + (tmpPlayer == gsyBaseVideoPlayer) + "|state>>>>" +
                        gsyBaseVideoPlayer.getCurrentPlayer().getCurrentState());
                if (tmpPlayer == gsyBaseVideoPlayer)
                {
                    if (gsyBaseVideoPlayer.getCurrentPlayer().getCurrentState() != GSYBaseVideoPlayer.CURRENT_STATE_PAUSE)
                        return;
                }
            }
            runnable = new PlayRunnable(gsyBaseVideoPlayer,flThumbnail, postion);
            //降低频率
            playHandler.postDelayed(runnable, 400);
        }


    }

    private class PlayRunnable implements Runnable
    {

        private GSYBaseVideoPlayer gsyBaseVideoPlayer;
        private int position;
        private FrameLayout flThumbnail;

        public PlayRunnable(GSYBaseVideoPlayer gsyBaseVideoPlayer,FrameLayout flThumbnail, int position)
        {
            this.gsyBaseVideoPlayer = gsyBaseVideoPlayer;
            this.flThumbnail = flThumbnail;
            this.position = position;
        }

        @Override
        public void run()
        {
            boolean inPosition = false;
            //如果未播放，需要播放
            if (gsyBaseVideoPlayer != null)
            {
                Logger.t(TAG).d("播放>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                isShowingPosition = currentPosition;
                if (mCurrentPlayer != null && mCurrentPlayer.isInPlayingState())
                {
                    mCurrentPlayer.onVideoPause();
                    mCurrentFlThumb.setVisibility(View.VISIBLE);
                }
                mCurrentPlayer = gsyBaseVideoPlayer;
                mCurrentFlThumb = flThumbnail;
//                int[] screenPosition = new int[2];
//                gsyBaseVideoPlayer.getLocationOnScreen(screenPosition);
//                int halfHeight = gsyBaseVideoPlayer.getHeight() / 2;
//                int rangePosition = screenPosition[1] + halfHeight;
//                //中心点在播放区域内
//                if (rangePosition >= rangeTop && rangePosition <= rangeBottom) {
//                    inPosition = true;
//                }
                startPlayLogic(gsyBaseVideoPlayer,flThumbnail, gsyBaseVideoPlayer.getContext(), position);
//                if (inPosition) {
//
//                    //gsyBaseVideoPlayer.startPlayLogic();
//                }
            }
        }
    }


    /***************************************自动播放的点击播放确认******************************************/
    private void startPlayLogic(GSYBaseVideoPlayer gsyBaseVideoPlayer,FrameLayout flThumbnail, Context context, int position)
    {
        if (!com.shuyu.gsyvideoplayer.utils.CommonUtil.isWifiConnected(context))
        {
            //这里判断是否wifi
            showWifiDialog(gsyBaseVideoPlayer, context);
            return;
        }
        Log.d("playPosition", "position>>>" + position);
        flThumbnail.setVisibility(View.GONE);
        gsyBaseVideoPlayer.startPlayLogic();
    }

    private void showWifiDialog(final GSYBaseVideoPlayer gsyBaseVideoPlayer, Context context)
    {
        if (!NetworkUtils.isAvailable(context))
        {
            ToastUtils.showLong(context.getResources().getString(com.shuyu.gsyvideoplayer.R.string.no_net));
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(com.shuyu.gsyvideoplayer.R.string.tips_not_wifi));
        builder.setPositiveButton(context.getResources().getString(com.shuyu.gsyvideoplayer.R.string.tips_not_wifi_confirm), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                gsyBaseVideoPlayer.startPlayLogic();
            }
        });
        builder.setNegativeButton(context.getResources().getString(com.shuyu.gsyvideoplayer.R.string.tips_not_wifi_cancel), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        if (!mAct.isFinishing())
        {
            builder.create().show();
        }

    }

}

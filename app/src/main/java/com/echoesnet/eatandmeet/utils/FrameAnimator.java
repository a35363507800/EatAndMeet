package com.echoesnet.eatandmeet.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ben on 2016/12/22.
 */

public class FrameAnimator
{
    private final static String TAG=FrameAnimator.class.getSimpleName();
    private Handler handler;
    private ImageView showView;
    private int index=0;
    private boolean isCycle = false;//是否循环播放
    private boolean isShowing = false;
    private IAnimateFinishedListener mAnimateFinishedListener;
    /**
     * handler ID
     */
    private static final int LOOP_ID = 1;
    private static final int LOOP_DRAWBLE_ID = 2;
    private static final int LOOP_DRAWBLE_LIST = 3;
/*    private List<Integer> drawableIds=new ArrayList<>(Arrays.asList(R.drawable.bumblebee_0
            ,R.drawable.bumblebee_1,R.drawable.bumblebee_2,R.drawable.bumblebee_3,R.drawable.bumblebee_4
            ,R.drawable.bumblebee_5,R.drawable.bumblebee_6)) ;*/
    private List<String> framesPathLst = new ArrayList<>();
    private int[] drawableIds;
    private List<Integer> drawableList;
    public FrameAnimator(ImageView showView)
    {
        this.showView=showView;
        this.handler=new AnimateHandler(FrameAnimator.this);
    }
    public void setShowView(ImageView showView)
    {
        this.showView=showView;
    }

    public boolean isShowing()
    {
        return isShowing;
    }

    public void startAnimation(List<String> framePathLst)
    {
        this.framesPathLst=framePathLst;
        index=0;
        showView.setVisibility(View.VISIBLE);
        handler.sendEmptyMessageDelayed(LOOP_ID,0);
    }

    public void startAnimation(int[] drawableIds , Boolean isCycle)
    {
        this.drawableIds=drawableIds;
        this.isCycle=isCycle;
        index=0;
        showView.setVisibility(View.VISIBLE);
        handler.sendEmptyMessageDelayed(LOOP_DRAWBLE_ID,0);
    }

    public void startAnimation(List<Integer> drawableList , Boolean isCycle)
    {
        if (isShowing)
            return;
        isShowing = true;
        this.drawableList =drawableList;
        this.isCycle=isCycle;
        index=0;
        showView.setVisibility(View.VISIBLE);
        handler.sendEmptyMessageDelayed(LOOP_DRAWBLE_LIST,0);
    }

    public void stopAnimation4Drawable()
    {
        isShowing = false;
        isCycle = false;
        index = 0;
        if (handler.hasMessages(LOOP_DRAWBLE_LIST))
            handler.removeMessages(LOOP_DRAWBLE_LIST);
    }

    public void setAnimateFinishedListener(IAnimateFinishedListener listener)
    {
        this.mAnimateFinishedListener=listener;
    }
    private void handleMessage(Message msg)
    {
        switch (msg.what)
        {
            case 1:
            //Logger.t(TAG).d("处理》"+System.currentTimeMillis());
            if (framesPathLst!=null&&index<framesPathLst.size())
            {
                long start=System.currentTimeMillis();
                //showView.setImageBitmap(BitmapFactory.decodeFile(framesPathLst.get(index)));
                showView.setImageDrawable(Drawable.createFromPath(framesPathLst.get(index)));
                long end=System.currentTimeMillis();

                int interval=0,loadPigTime= (int) (end-start);
                if (loadPigTime>50)
                    interval=0;
                else
                    interval=50-loadPigTime;
                //Logger.t(TAG).d("获得图片"+index+"耗时》"+ (end-start));
                index++;
                handler.sendEmptyMessageDelayed(LOOP_ID,interval);
            }else
            {
                if (handler.hasMessages(LOOP_ID))
                    handler.removeMessages(LOOP_ID);
                showView.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
                showView.setVisibility(View.GONE);
                if (mAnimateFinishedListener!=null)
                    mAnimateFinishedListener.animateFinished();
            }
            break;
            case 2:
                if (drawableIds!=null&&index<drawableIds.length)
                {
                    long start=System.currentTimeMillis();
                    //showView.setImageBitmap(BitmapFactory.decodeFile(framesPathLst.get(index)));
                    showView.setImageResource(drawableIds[index]);
                    long end=System.currentTimeMillis();

                    int interval=0,loadPigTime= (int) (end-start);
                    if (loadPigTime>50)
                        interval=0;
                    else
                        interval=50-loadPigTime;
                    //Logger.t(TAG).d("获得图片"+index+"耗时》"+ (end-start));
                    index++;
                    handler.sendEmptyMessageDelayed(LOOP_DRAWBLE_ID,interval);
                }else
                {
                    if (isCycle)
                    {
                        index = 0;
                        handler.sendEmptyMessageDelayed(LOOP_DRAWBLE_ID,0);
                    }else
                    {
                        if (mAnimateFinishedListener!=null)
                            mAnimateFinishedListener.animateFinished();
                        if (handler.hasMessages(LOOP_DRAWBLE_ID))
                            handler.removeMessages(LOOP_DRAWBLE_ID);
                    }
                }
                break;
            case LOOP_DRAWBLE_LIST:
                if (drawableList!=null&&index<drawableList.size())
                {
                    long start=System.currentTimeMillis();
                    //showView.setImageBitmap(BitmapFactory.decodeFile(framesPathLst.get(index)));
                    showView.setImageResource(drawableList.get(index));
                    long end=System.currentTimeMillis();

                    int interval=0,loadPigTime= (int) (end-start);
                    if (loadPigTime>50)
                        interval=0;
                    else
                        interval=50-loadPigTime;
                    //Logger.t(TAG).d("获得图片"+index+"耗时》"+ (end-start));
                    index++;
                    handler.sendEmptyMessageDelayed(LOOP_DRAWBLE_LIST,interval);
                }else
                {
                    if (isCycle)
                    {
                        index = 0;
                        handler.sendEmptyMessageDelayed(LOOP_DRAWBLE_LIST,0);
                    }else
                    {
                        if (mAnimateFinishedListener!=null)
                            mAnimateFinishedListener.animateFinished();
                        if (handler.hasMessages(LOOP_DRAWBLE_LIST))
                            handler.removeMessages(LOOP_DRAWBLE_LIST);
                    }
                }
                break;
        }
    }

    private static class AnimateHandler extends Handler
    {
        private WeakReference<FrameAnimator> aView;
        public AnimateHandler(FrameAnimator aView)
        {
            this.aView=new WeakReference<FrameAnimator>(aView);
        }

        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            FrameAnimator animateView=aView.get();
            if (animateView!=null)
            {
                animateView.handleMessage(msg);
            }
        }
    }
    public interface IAnimateFinishedListener
    {
        void animateFinished();
    }
}

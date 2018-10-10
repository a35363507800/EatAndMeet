package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import android.widget.Scroller;

public class SwipeMoveRelativeLayout extends RelativeLayout
{
    private static final String TAG = "SwipeMoveRelativeLayout";

    private Scroller mScroller;
    private boolean isHide = false;

    private float mLastTouchX;
    private float mLastTouchY;
    private int mTouchSlop;

    public SwipeMoveRelativeLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mScroller = new Scroller(context);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        // 获取TouchSlop值
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
    }
    public void smoothScrollToShow(boolean toShow)
    {
        if (toShow)
        {
            isHide = false;
            smoothScrollTo(0, 0);
        }
        else
        {
            isHide = true;
            smoothScrollTo(-getWidth(), 0);
        }
    }

    //调用此方法滚动到目标位置
    public void smoothScrollTo(int fx, int fy)
    {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    //调用此方法设置滚动的相对偏移
    public void smoothScrollBy(int dx, int dy)
    {
        //设置mScroller的滚动偏移量
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    @Override
    public void computeScroll()
    {

        //先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset())
        {

            //这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
        super.computeScroll();


        if (listener != null)
        {
            if (isHide)
            {
                listener.onSwipeToHidden();
            }
            else
            {
                listener.onSwipeToShow();
            }
        }
    }

//    @Override
//    public boolean onTouch(View view, MotionEvent event) {
//        Log.i("swipeView","onTouch");
//        boolean eventUsed = false;
//
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_UP:
//                Log.i("swipeView","ACTION_UP");
//                if(isHide){
//                    if ((int) (x - mLastTouchX) < -200) {
//                        isHide = false;
////                        scrollTo(0,0);
//                        smoothScrollTo(0,0);
//                    }else {
////                        scrollTo(-getWidth(),0);
//                        smoothScrollTo(-getWidth(),0);
//                    }
//                }else {
//                    if ((int) (x - mLastTouchX) > 200) {
//                        isHide = true;
////                        scrollTo(-getWidth(),0);
//                        smoothScrollTo(-getWidth(),0);
//                    }else {
////                        scrollTo(0,0);
//                        smoothScrollTo(0,0);
//                    }
//                }
//                break;
//            case MotionEvent.ACTION_DOWN: {
//                Log.i("swipeView","ACTION_DOWN");
//                mLastTouchX = x;// Remember where we started
//                mLastTouchY = y;
////                eventUsed = true;
//                break;
//            }
//            case MotionEvent.ACTION_MOVE: {
//                Log.i("swipeView","ACTION_MOVE");
//                //new - old >0 右滑动
//                //new - old <0 左滑动
//
//                if(isHide){
//                    if ((int) (x - mLastTouchX) < 0) {
////                        Log.i("swipeView","getWidth()"+getWidth());
////                        Log.i("swipeView","(x - mLastTouchX)"+(x - mLastTouchX) + "|"+x+ "|"+mLastTouchX );
//                        smoothScrollTo((int) (-getWidth()-(x - mLastTouchX)),0);
//                    }
//
//                }else {
//                    if ((int) (x - mLastTouchX) > 0) {
//                        //显示中不可向左划
//                        smoothScrollTo((int) (0-(x - mLastTouchX)),0);
//                    }
//                }
//
//            }
//        }
//
//
//        return eventUsed;
//    }


    private long downT = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (listener != null)
            listener.dispatchTouch(event);

        Log.i("swipeView", "onTouch");
        boolean eventUsed = false;

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_UP:
                Log.i("swipeView", "ACTION_UP");
                if (isHide)
                {
                    if ((int) (x - mLastTouchX) < -200)
                    {
                        isHide = false;
//                        scrollTo(0,0);
                        smoothScrollTo(0, 0);
                    }
                    else
                    {
//                        scrollTo(-getWidth(),0);
                        smoothScrollTo(-getWidth(), 0);
                    }
                }
                else
                {
                    if ((int) (x - mLastTouchX) > 200)
                    {
                        isHide = true;
//                        scrollTo(-getWidth(),0);
                        smoothScrollTo(-getWidth(), 0);
                    }
                    else
                    {
//                        scrollTo(0,0);
                        smoothScrollTo(0, 0);
                    }
                }

                if (System.currentTimeMillis() - downT < 200 && listener != null)
                {
                    listener.onClick();
                }

                break;
            case MotionEvent.ACTION_DOWN:
            {
                Log.i("swipeView", "ACTION_DOWN");
                mLastTouchX = x;// Remember where we started
                mLastTouchY = y;
                downT = System.currentTimeMillis();
                eventUsed = true;
//                return super.onTouchEvent(event);
                break;

            }
            case MotionEvent.ACTION_MOVE:
            {
                Log.i("swipeView", "ACTION_MOVE");
                //new - old >0 右滑动
                //new - old <0 左滑动
                eventUsed = false;
                if (isHide)
                {
                    if ((int) (x - mLastTouchX) < 0)
                    {
//                        Log.i("swipeView","getWidth()"+getWidth());
//                        Log.i("swipeView","(x - mLastTouchX)"+(x - mLastTouchX) + "|"+x+ "|"+mLastTouchX );
                        smoothScrollTo((int) (-getWidth() - (x - mLastTouchX)), 0);
                    }

                }
                else
                {
                    if ((int) (x - mLastTouchX) > 0)
                    {
                        //显示中不可向左划
                        smoothScrollTo((int) (0 - (x - mLastTouchX)), 0);
                    }
                }

            }
        }

        return eventUsed;
    }


    private SwipeMoveListener listener = null;

    public void setListener(SwipeMoveListener listener)
    {
        this.listener = listener;
    }

    public interface SwipeMoveListener
    {
        void onClick();

        void dispatchTouch(MotionEvent event);

        void onSwipeToHidden();

        void onSwipeToShow();
    }
}

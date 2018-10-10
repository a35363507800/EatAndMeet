package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.orhanobut.logger.Logger;


/**
 * @author yqh
 * @Date 2017/10/27
 * @Version 1.0
 */

public class FindMoveView extends ScrollView
{
    private static final String TAG = FindMoveView.class.getSimpleName();

    private Context mContext;
    private Scroller mScroller;

    private volatile int moveSize;

    private volatile int moveUpSizeCount; //移动距离总和
    private volatile int moveDownSizeCount; //移动距离总和

    private volatile float mLastTouchX;
    private volatile float mLastTouchY;

    private View titleView, vp, vScroll;

    private boolean isHorizontalMove = false;

    private volatile String moveOrientation = "down";

    private int maxMoveSize = 71;

    private volatile boolean isTitleHide = false;   //四个item是否隐藏
    private volatile boolean isMoved = false;       //是否移动， 控制up事件传递
    private volatile boolean isWithPicTitleShow = false;//是否拦截事件

    private MoveViewStateListener moveViewStateListener;

    public FindMoveView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        mScroller = new Scroller(context);
        maxMoveSize = CommonUtils.dp2px(mContext, maxMoveSize);
    }

    public boolean isWithPicTitleShow()
    {
        return isWithPicTitleShow;
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        Logger.t(TAG).d("isWithPicTitleShow:onInterceptTouchEvent");
        return true;
    }

    public void setDispatchView(View vp)
    {
        this.vp = vp;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        Logger.t(TAG).d("isWithPicTitleShow:" + isWithPicTitleShow);
//        vp.dispatchTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        Logger.t(TAG).d("event.getAction():" + event.getAction());
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mLastTouchX = x;// Remember where we started
                mLastTouchY = y;
                if (moveSize != 0)
                {
                    isMoved = true;
                    Logger.t(TAG).d("MotionEvent.ACTION_DOWN: | moveSize:" + moveSize);
                    MotionEvent ev = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), x, y + moveSize, event.getMetaState());
                    ((View) vp.getParent()).dispatchTouchEvent(ev);
                }
                else
                {
                    Logger.t(TAG).d("MotionEvent.ACTION_DOWN: | moveSize:" + moveSize);
                    isMoved = false;
                    ((View) vp.getParent()).dispatchTouchEvent(event);
                }

                break;
            case MotionEvent.ACTION_MOVE:
//                moveSize = (int) Math.ceil(mLastTouchY - y);
                Logger.t(TAG).d("moveSize:" + moveSize + " | maxMoveSize:" + maxMoveSize);

                if (Math.abs(mLastTouchX - x) > Math.abs(mLastTouchY - y))//横向动
                {
                    Logger.t(TAG).d("------->>>横向动：" + Math.abs(mLastTouchX - x) + " | " + Math.abs(mLastTouchY - y));
                    isHorizontalMove = true;
                    Logger.t(TAG).d("MotionEvent.ACTION_MOVE:横向动 | isMoved:" + isMoved);
                    if (isMoved)
                    {
                        MotionEvent ev = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), x, y + moveSize, event.getMetaState());
                        ((View) vp.getParent()).dispatchTouchEvent(ev);
                    }
                    else
                    {
                        ((View) vp.getParent()).dispatchTouchEvent(event);
                    }


                    return false;
                }
                else  //竖向动
                {
                    Logger.t(TAG).d("MotionEvent.ACTION_MOVE:竖向动 | isHorizontalMove:" + isHorizontalMove + " | isMoved:" + isMoved);
                    if (isHorizontalMove)
                    {
                        if (isMoved)
                        {
                            MotionEvent ev = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), x, y + moveSize, event.getMetaState());
                            ((View) vp.getParent()).dispatchTouchEvent(ev);
                        }
                        else
                        {
                            ((View) vp.getParent()).dispatchTouchEvent(event);
                        }
                        return false;
                    }


                    Logger.t(TAG).d("------->>>竖向动：moveOrientation：" + moveOrientation + " | " + Math.abs(mLastTouchX - x) + " | " + Math.abs(mLastTouchY - y));
                    if (isMoved)
                    {
                        MotionEvent ev = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), x, y + moveSize, event.getMetaState());
                        ((View) vp.getParent()).dispatchTouchEvent(ev);
                    }
                    else
                    {
                        ((View) vp.getParent()).dispatchTouchEvent(event);
                    }
                    //region 注释代码 以后参考
                    /*if (isTitleHide)//title隐藏 状态 vp持续接收 move事件
                    {
//                    vp.dispatchTouchEvent(event);
                        MotionEvent ev = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), x, y + maxMoveSize, event.getMetaState());
                        ((View) vp.getParent()).dispatchTouchEvent(ev);
                        Logger.t(TAG).d("------->>>isTitleHide == true 事件分发到vp");
                    }*/

                    /*if ("up".equals(moveOrientation))//上推
                    {
//                    moveUpSizeCount = moveSize + moveUpSizeCount;
//                    Logger.t(TAG).d("moveSizeCount:" + moveUpSizeCount);
                        //上划到一定程度时 事件传递给 vp
                        Logger.t(TAG).d("------->>>上推");
                        if (moveSize > maxMoveSize)
                        {
                            Logger.t(TAG).d("------->>>上推到一定程度 事件 传递 vp");
//                        moveUpSizeCount = 0;
                            isTitleHide = true;
                            moveOrientation = "down";
//                        vp.dispatchTouchEvent(event);
                            MotionEvent ev = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), x, y + maxMoveSize, event.getMetaState());
                            ((View) vp.getParent()).dispatchTouchEvent(ev);
                        }
//                        ((View) vp.getParent()).dispatchTouchEvent(event);
//                    if (isTitleHide)//title隐藏 状态 vp持续接收 move事件
//                        vp.dispatchTouchEvent(event);
                    }
                    else //if ("down".equals(moveOrientation)) //下拉
                    {
                        Logger.t(TAG).d("------->>>下拉");
                        if (isWithPicTitleShow)
                        {
                            isTitleHide = false;
                            Logger.t(TAG).d("------->>>下拉时vp触发 拉到 顶部 事件 更改 isTitleHide = false ");
                        }

                        if (isTitleHide)//此时scroll 没有在顶部
                        {
//                        vp.dispatchTouchEvent(event);  50687822407404
                            Logger.t(TAG).d("------->>>下拉时isTitleHide == true 事件分发到vp ");
                            ((View) vp.getParent()).dispatchTouchEvent(event);
                            return false;
                        }
                        else
                        {
                            Logger.t(TAG).d("------->>>下拉时 isTitleHide = false ");
                            ((View) vp.getParent()).dispatchTouchEvent(event);
                            tempValue = 0;
                        }
                    *//*if (isWithPicTitleShow)
                        isTitleHide = false;
                    if (isTitleHide)//title隐藏状态
                    {
                        vp.dispatchTouchEvent(event);
                        return false;
                        *//**//*if (!isWithPicTitleShow)//true 表示 事件 在 scrollview身上  false表示 事件在vp上
                        {
                            return false;
                        }
                        else
                        {
                            moveSizeCount = 0;//vp上拉 到 顶部 更改状态时  清除 拉动 距离总数
                            isTitleHide = false;
                            return super.onTouchEvent(event);
                        }*//**//*
                    }
                    else
                    {
                        moveUpSizeCount = 0;//vp上拉 到 顶部 更改状态时  清除 拉动 距离总数
                    }*//*
                    }*/
                    //endregion

                }

                //region 注释代码 以后参考
                /*if (Float.compare(Math.abs(mLastTouchX - x), 10.0f) == 1)
                {
                    Logger.t(TAG).d("------->>> X 轴 移动  ，事件分发到vp");
//                    vp.dispatchTouchEvent(event);
                    ((View) vp.getParent()).dispatchTouchEvent(event);
                }

                if (isTitleHide)//title隐藏 状态 vp持续接收 move事件
                {
//                    vp.dispatchTouchEvent(event);
                    ((View) vp.getParent()).dispatchTouchEvent(event);
                    Logger.t(TAG).d("------->>>isTitleHide == true 事件分发到vp");
                }

                if ("up".equals(moveOrientation))//上推
                {
//                    moveUpSizeCount = moveSize + moveUpSizeCount;
//                    Logger.t(TAG).d("moveSizeCount:" + moveUpSizeCount);
                    //上划到一定程度时 事件传递给 vp
                    Logger.t(TAG).d("------->>>上推");
                    if (moveSize > maxMoveSize)
                    {
                        Logger.t(TAG).d("------->>>上推到一定程度 时间 传递 vp");
//                        moveUpSizeCount = 0;
                        isTitleHide = true;
//                        vp.dispatchTouchEvent(event);
                        ((View) vp.getParent()).dispatchTouchEvent(event);
                    }
//                    if (isTitleHide)//title隐藏 状态 vp持续接收 move事件
//                        vp.dispatchTouchEvent(event);
                }
                else if ("down".equals(moveOrientation)) //下拉
                {
                    Logger.t(TAG).d("------->>>下拉");
                    if (isWithPicTitleShow)
                    {
                        isTitleHide = false;
                        Logger.t(TAG).d("------->>>下拉时vp触发 拉到 顶部 事件 更改 isTitleHide = false ");
                    }

                    if (isTitleHide)//此时scroll 没有在顶部
                    {
//                        vp.dispatchTouchEvent(event);
                        Logger.t(TAG).d("------->>>下拉时isTitleHide == true 事件分发到vp ");
                        ((View) vp.getParent()).dispatchTouchEvent(event);
                        return false;
                    }
                    else
                    {
                        tempValue = 0;
                    }
                    *//*if (isWithPicTitleShow)
                        isTitleHide = false;
                    if (isTitleHide)//title隐藏状态
                    {
                        vp.dispatchTouchEvent(event);
                        return false;
                        *//**//*if (!isWithPicTitleShow)//true 表示 事件 在 scrollview身上  false表示 事件在vp上
                        {
                            return false;
                        }
                        else
                        {
                            moveSizeCount = 0;//vp上拉 到 顶部 更改状态时  清除 拉动 距离总数
                            isTitleHide = false;
                            return super.onTouchEvent(event);
                        }*//**//*
                    }
                    else
                    {
                        moveUpSizeCount = 0;//vp上拉 到 顶部 更改状态时  清除 拉动 距离总数
                    }*//*
                }*/

                // Remember this touch position for the next move event
                //mLastTouchX = x;
                //mLastTouchY = y;
                //endregion

                break;
            case MotionEvent.ACTION_UP:
                isHorizontalMove = false;
                Logger.t(TAG).d("Math.abs(mLastTouchX - x):" + Math.abs(mLastTouchX - x) + " | Math.abs(mLastTouchY - y):" + Math.abs(mLastTouchY - y));
                Logger.t(TAG).d("MotionEvent.ACTION_UP:竖向动 | isMoved:" + isMoved);
                if (isMoved)
                {
                    MotionEvent ev = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), x, y + moveSize, event.getMetaState());
                    ((View) vp.getParent()).dispatchTouchEvent(ev);
                }
                else
                {
                    ((View) vp.getParent()).dispatchTouchEvent(event);
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    int tempValue = 0;

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY)
    {
        super.onScrollChanged(x, y, oldX, oldY);
        Logger.t(TAG).d("onScrollChanged:" + x + " | " + y + " | " + oldX + " | " + oldY);
        if (moveViewStateListener != null)
            moveViewStateListener.onMoveChange(x, y);
        if (y - tempValue > 0)
            moveOrientation = "up";
        else
            moveOrientation = "down";
        tempValue = y;
        Logger.t(TAG).d("moveSize:" + moveOrientation);
        moveSize = y;

        if (y > maxMoveSize)
        {
            if (moveViewStateListener != null)
                moveViewStateListener.onTitleViewIsGone();
//            isTitleHide = true;
//            isWithPicTitleShow = false;
        }
        else
        {
            if (moveViewStateListener != null)
                moveViewStateListener.onTitleViewIsShow();
//            isWithPicTitleShow = true;
        }
    }

    public void setWithPicTitleShow(boolean withPicTitleShow)
    {
        isWithPicTitleShow = withPicTitleShow;
    }

    public void setTitleHide(boolean titleHide)
    {
        isTitleHide = titleHide;
    }

    public void setMoveOrientation(String moveOrientation)
    {
        this.moveOrientation = moveOrientation;
    }
    /* @Override
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
    }*/


    public interface MoveViewStateListener
    {
        void onMoveChange(float dx, float dy);

        void onTitleViewIsGone();

        void onTitleViewIsShow();

    }

    public void setMoveViewStateListener(MoveViewStateListener listener)
    {
        moveViewStateListener = listener;
    }

}

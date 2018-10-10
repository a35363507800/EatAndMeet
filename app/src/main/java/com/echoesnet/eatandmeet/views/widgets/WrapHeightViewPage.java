package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by liuchao on 2017/7/21 16.
 */

public class WrapHeightViewPage extends NoScrollViewPager
{
    public WrapHeightViewPage(Context context)
    {
        super(context);
    }
    int h=0;
    public WrapHeightViewPage(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {





//
       super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);


    }

    public void measureHeight(float position)
    {


        LinearLayout.LayoutParams params= ( LinearLayout.LayoutParams) getLayoutParams();
        params.height= (int)position;
        setLayoutParams(params);
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev)
//    {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                //DOWN 事件的时候记录下当前的xy左标
//                mDownX=ev.getX();
//                mDownY=ev.getY();
//                getParent().requestDisallowInterceptTouchEvent(true);
//                break;
//            case MotionEvent.ACTION_MOVE:
//            /*MOVE 事件后计算x轴y轴的移动距离 ，如果x轴移动距离大于y轴，
//            那么该事件有ViewPager处理，否则交给父容器处理*/
//                if(Math.abs(ev.getX()-mDownX)>Math.abs(ev.getY()-mDownY)){
//                    getParent().requestDisallowInterceptTouchEvent(true);
//                }else{
//                    getParent().requestDisallowInterceptTouchEvent(false);
//                }
//                break;
//            case MotionEvent.ACTION_CANCEL:
//                getParent().requestDisallowInterceptTouchEvent(false);
//                break;
//            default:
//                break;
//        }
//        return super.dispatchTouchEvent(ev);
//    }
}

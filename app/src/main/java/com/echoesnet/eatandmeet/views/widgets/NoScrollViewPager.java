package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2016/7/5.
 */
public class NoScrollViewPager extends ViewPager
{
    private boolean noScroll = false;

    public NoScrollViewPager(Context context)
    {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void setNoScroll(boolean noScroll)
    {
        this.noScroll = noScroll;
    }

    @Override
    public void scrollTo(int x, int y)
    {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0)
    {
        /* return false;//super.onTouchEvent(arg0); */
        if (noScroll)
            return false;
        else
        {
            boolean flag = false;
            try
            {
                flag = super.onTouchEvent(arg0);
            } catch (IllegalArgumentException ex)
            {

            }
            return flag;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0)
    {
        if (noScroll)
            return false;
        else
        {
            boolean flag = false;
            try
            {
                flag = super.onInterceptTouchEvent(arg0);
            } catch (IllegalArgumentException ex)
            {
            }
            return flag;
        }
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll)
    {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item)
    {
        super.setCurrentItem(item);
    }

    /*@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int viewHeight = 0;
        View childView = getChildAt(getCurrentItem());
        if (childView != null)  //有可能没有子view
        {
            childView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            viewHeight = childView.getMeasuredHeight();   //得到父元素对自身设定的高
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }*/
}

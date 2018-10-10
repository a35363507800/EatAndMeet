package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.zhy.autolayout.AutoRelativeLayout;

/**
 * Created by wangben on 2016/7/30.
 * 遮罩使用
 */
public class NoTouchRelativeLayout extends AutoRelativeLayout
{
    private boolean isTouchAble=true;
    public NoTouchRelativeLayout(Context context)
    {
        super(context);
    }

    public NoTouchRelativeLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (isTouchAble)
            return false;
        else
            return true;
    }

    public void setTouchAble(boolean touchAble)
    {
        isTouchAble=touchAble;
    }
}

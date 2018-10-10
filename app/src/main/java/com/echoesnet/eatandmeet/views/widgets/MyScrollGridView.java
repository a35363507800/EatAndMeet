package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;
import android.widget.ScrollView;

/**
 * Created by liuchao on 2017/4/27 14.
 */

public class MyScrollGridView extends GridView

{
    ScrollView parentScrollView;

    public ScrollView getParentScrollView() {
        return parentScrollView;
    }

    public void setParentScrollView(ScrollView parentScrollView) {
        this.parentScrollView = parentScrollView;
    }

    private int maxHeight;

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public MyScrollGridView(Context context)
    {
        super(context);
    }

    public MyScrollGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
        public boolean onInterceptTouchEvent(MotionEvent ev)
    {
                switch (ev.getAction())
                {
                case MotionEvent.ACTION_DOWN:
                    setParentScrollAble(false);

                case MotionEvent.ACTION_MOVE:
                break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    setParentScrollAble(true);
                break;
                default:
                break;
                }
        return super.onInterceptTouchEvent(ev);
    }
    /**
     * @param flag
     */
    private void setParentScrollAble(boolean flag) {

        parentScrollView.requestDisallowInterceptTouchEvent(!flag);
    }

}

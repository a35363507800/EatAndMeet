package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by liuchao on 2017/4/24 16.
 */

public class MyCustomListView extends ListView
{
    public MyCustomListView(Context context)
    {
        super(context);
    }

    public MyCustomListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MyCustomListView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}

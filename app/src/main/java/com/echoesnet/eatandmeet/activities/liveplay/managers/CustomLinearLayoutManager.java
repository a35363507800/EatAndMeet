package com.echoesnet.eatandmeet.activities.liveplay.managers;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import com.orhanobut.logger.Logger;

/**
 * Created by liuchao on 2017/7/21 14.
 */

public class CustomLinearLayoutManager extends LinearLayoutManager
{
    private boolean isScrollEnabled = true;

    public CustomLinearLayoutManager(Context context)
    {
        super(context);
    }
    public void setScrollEnabled(boolean flag)
    {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically()
    {
        Logger.t("CustomLinearLayoutManager").d("==========");
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }
}

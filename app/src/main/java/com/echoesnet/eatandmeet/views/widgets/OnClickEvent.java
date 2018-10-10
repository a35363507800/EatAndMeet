package com.echoesnet.eatandmeet.views.widgets;

import android.view.View;

import com.orhanobut.logger.Logger;

/**
 * Created by Administrator on 2017/4/24.
 */

public abstract class OnClickEvent implements View.OnClickListener {
    private static final String TAG = OnClickEvent.class.getSimpleName();
    private static long lastTime;

    public abstract void singleClick(View v);
    private long delay;

    public OnClickEvent(long delay) {
        this.delay = delay;
    }

    @Override
    public void onClick(View v) {
        if (onMoreClick(v)) {
            return;
        }
        singleClick(v);
    }

    public boolean onMoreClick(View v) {
        boolean flag = false;
        long time = System.currentTimeMillis() - lastTime;
        if (0 < time && time < delay) {
            flag = true;
        }
        lastTime = System.currentTimeMillis();
        return flag;
    }
}

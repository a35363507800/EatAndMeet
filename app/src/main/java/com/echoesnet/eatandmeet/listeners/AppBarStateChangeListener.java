package com.echoesnet.eatandmeet.listeners;

import android.support.design.widget.AppBarLayout;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2017/8/11
 * @description
 */
public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener
{

    public enum State
        {
            EXPANDED,
            COLLAPSED,
            IDLE
        }


        private State mCurrentState = State.IDLE;

        @Override
        public final void onOffsetChanged(AppBarLayout appBarLayout, int i)
        {
            if (i == 0)
            {
                if (mCurrentState != State.EXPANDED)
                {
                    onStateChanged(appBarLayout, State.EXPANDED,i);
                }
                //完全展开
                mCurrentState = State.EXPANDED;
            }
            else if (Math.abs(i) >= appBarLayout.getTotalScrollRange())
            {
                if (mCurrentState != State.COLLAPSED)
                {
                    onStateChanged(appBarLayout, State.COLLAPSED,i);
                }
                //完全闭合
                mCurrentState = State.COLLAPSED;
            }
            else
            {
                if (mCurrentState != State.IDLE)
                {
                    onStateChanged(appBarLayout, State.IDLE,i);
                }
                mCurrentState = State.IDLE;
            }
        }
        public abstract void onStateChanged(AppBarLayout appBarLayout, State state,int offSet);
}

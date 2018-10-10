package com.echoesnet.eatandmeet.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.viewpagerindicator.PageIndicator;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2017/8/7
 * @description
 */
public class TextPageIndicator extends LinearLayout implements PageIndicator
{
    public TextPageIndicator(Context context)
    {
        super(context);
    }

    public TextPageIndicator(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TextPageIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setViewPager(ViewPager view)
    {

    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition)
    {

    }

    @Override
    public void setCurrentItem(int item)
    {

    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener)
    {

    }

    @Override
    public void notifyDataSetChanged()
    {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {

    }

    @Override
    public void onPageSelected(int position)
    {

    }

    @Override
    public void onPageScrollStateChanged(int state)
    {

    }
}

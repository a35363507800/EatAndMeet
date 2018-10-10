package com.echoesnet.eatandmeet.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by wangben on 2016/4/27.
 */
public class LaunchPagerAdapter extends FragmentPagerAdapter
{
    private List<Fragment> mFrgs;

    public LaunchPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }
    public LaunchPagerAdapter(FragmentManager fm, List<Fragment> mFrgs)
    {
        this(fm);
        this.mFrgs=mFrgs;
    }

    @Override
    public Fragment getItem(int position)
    {
        return mFrgs.get(position);
    }

    @Override
    public int getCount()
    {
        return mFrgs.size();
    }

    // Returns the page title for the top indicator,如果有的话
    @Override
    public CharSequence getPageTitle(int position)
    {
        return super.getPageTitle(position);
    }
}

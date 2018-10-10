package com.echoesnet.eatandmeet.activities.live;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.BaseActivity;
import com.echoesnet.eatandmeet.fragments.livefragments.LiveFansFrg;
import com.echoesnet.eatandmeet.fragments.livefragments.LiveFocusFrg;
import com.echoesnet.eatandmeet.views.adapters.FocusFansPagerAdapter;
import com.echoesnet.eatandmeet.views.widgets.NoScrollViewPager;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveFansActivity extends BaseActivity
{
    private static final String TAG = LiveFansActivity.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.vp_focus_or_fans)
    NoScrollViewPager mViewPager;

    //里面包含的两个fragment
    List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_fans_activity);
        ButterKnife.bind(this);
        afterViews();
    }

    private void afterViews()
    {
        topBarSwitch.inflateSwitchBtns(Arrays.asList("关注", "粉丝"), 0, new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                finish();
            }

            @Override
            public void right2Click(View view)
            {

            }

            @Override
            public void switchBtn(View view, int position)
            {
                super.switchBtn(view, position);
                mViewPager.setCurrentItem(position);
            }
        });
        initViewPager();
    }

    private void initViewPager()
    {
        LiveFocusFrg followFrg = LiveFocusFrg.getInstance();
        LiveFansFrg fansFrg = LiveFansFrg.getInstance();
        fragments.add(followFrg);
        fragments.add(fansFrg);
        PagerAdapter mPagerAdapter = new FocusFansPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setNoScroll(false);
        mViewPager.addOnPageChangeListener(mPagerChangeListener);
    }

    private ViewPager.OnPageChangeListener mPagerChangeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
        }

        @Override
        public void onPageSelected(int position)
        {
            topBarSwitch.changeSwitchBtn(position);
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {

        }
    };
}

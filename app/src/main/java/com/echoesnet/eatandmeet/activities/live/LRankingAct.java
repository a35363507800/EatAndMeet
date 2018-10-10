package com.echoesnet.eatandmeet.activities.live;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.BaseActivity;
import com.echoesnet.eatandmeet.fragments.livefragments.LiveRankingFrg;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by an on 2016/11/22 0022.
 */
public class LRankingAct extends BaseActivity
{
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.vp_viewpage)
    ViewPager vpViewpage;

    private List<Fragment> fragments;
    private FragmentActivity mFrgAc;
    private List<Map<String,TextView>> navBtns;
    private int currentPage = 0;
    private static String TAG = LRankingAct.class.getSimpleName();

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_ranking);
        ButterKnife.bind(this);
        afterViews();
    }

    private void afterViews()
    {
        mFrgAc = this;
        fragments = new ArrayList<>();
        String roomId = getIntent().getStringExtra("roomId");
        boolean isShowThisTime = getIntent().getBooleanExtra("isShowThisTime", false);
        if (isShowThisTime) {
            topBarSwitch.inflateSwitchBtns(Arrays.asList("总榜", "本场"), currentPage, new TopbarSwitchSkeleton()
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
                    currentPage = position;
                    Logger.t(TAG).d("选择到第》" + position);
                    vpViewpage.setCurrentItem(position);
                }
            });
            navBtns = topBarSwitch.getNavBtns2(new int[]{1, 0, 0, 0});
            LiveRankingFrg totalRankingFrg = new LiveRankingFrg();
            LiveRankingFrg thisTimeRankingFrg = new LiveRankingFrg();
            Bundle totalBundle = new Bundle(2);
            totalBundle.putString("roomId", roomId);
            totalBundle.putString("type", "0");
            totalRankingFrg.setArguments(totalBundle);
            Bundle thisBundle = new Bundle(2);
            thisBundle.putString("roomId", roomId);
            thisBundle.putString("type", "1");
            thisTimeRankingFrg.setArguments(thisBundle);
            fragments.add(totalRankingFrg);
            fragments.add(thisTimeRankingFrg);

        }
        else
            {
            topBarSwitch.setTopbarType(TopBarSwitch.TopbarType.TEXT);
            topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
            {
                @Override
                public void leftClick(View view)
                {

                }

                @Override
                public void right2Click(View view)
                {

                }
            }).setText("总榜");
            navBtns = topBarSwitch.getNavBtns2(new int[]{1, 0, 0, 0});
            LiveRankingFrg totalRankingFrg = new LiveRankingFrg();
            Bundle totalBundle = new Bundle(2);
            totalBundle.putString("roomId", roomId);
            totalBundle.putString("type", "0");
            totalRankingFrg.setArguments(totalBundle);
            fragments.add(totalRankingFrg);

        }
        vpViewpage.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager())
        {
            @Override
            public Fragment getItem(int position)
            {
                return fragments.get(position);
            }

            @Override
            public int getCount()
            {
                return fragments.size();
            }
        });
        vpViewpage.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
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
        });
    }

}

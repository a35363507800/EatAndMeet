package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.fragments.MyAnchorLevelFrg;
import com.echoesnet.eatandmeet.fragments.MyLevelFrg;
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
 * Created by an on 2017/4/19 0019.
 */

public class MyLevelAct extends BaseActivity
{

    private final String TAG = MyLevelAct.class.getSimpleName();

    @BindView(R.id.top_bar_switch_my_level)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.vp_viewpage)
    ViewPager vpViewpage;
    private List<Map<String, TextView>> navBtns;
    private Activity mActivity;
    private List<Fragment> fragments;
    private MyLevelFrg dateSendFrg;
    private MyAnchorLevelFrg dateAcceptFrg;
    private int currentPage = 0;
    public static  int LEVEL_RESULT=1001;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_my_level);
        ButterKnife.bind(this);
        mActivity = this;
        initView();
    }


    private void initView()
    {
        topBarSwitch.inflateSwitchBtns(Arrays.asList("我的等级", "主播等级"), 0,
                new TopbarSwitchSkeleton()
                {
                    @Override
                    public void leftClick(View view)
                    {
                        finish();
                    }

                    @Override
                    public void right2Click(View view)
                    {
                        String function = (String) view.getTag();
                        //  myDateWishListFrg.operateEditStatus(function);
                    }

                    @Override
                    public void switchBtn(View view, int position)
                    {
                        currentPage = position;
                        Logger.t(TAG).d("选择到第》" + position);
                        vpViewpage.setCurrentItem(currentPage);
                    }
                });

        navBtns = topBarSwitch.getNavBtns2(new int[]{1, 0, 0, 0});
        dateSendFrg = new MyLevelFrg();
        dateAcceptFrg = new MyAnchorLevelFrg();
        fragments = new ArrayList<>();
        fragments.add(dateSendFrg);
        fragments.add(dateAcceptFrg);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Logger.t(TAG).d("trigger>>>>js返回" + requestCode+"/"+resultCode);
        if(requestCode==MyLevelFrg.START_FOR_MY_ACCOUNT)
        {
            dateSendFrg.reLoadWebView();
        }
    }
}

package com.echoesnet.eatandmeet.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.fragments.Intro1Frg;
import com.echoesnet.eatandmeet.fragments.Intro2Frg;
import com.echoesnet.eatandmeet.fragments.Intro3Frg;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.adapters.LaunchPagerAdapter;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class IntroduceAct extends FragmentActivity implements Intro3Frg.OnFrgIntro3InteractListener
{
    public static final String TAG = IntroduceAct.class.getSimpleName();
    @BindView(R.id.vp_introduce)
    ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_introduce);
        ButterKnife.bind(this);
        initViewPager();
    }

    void initViewPager()
    {
        Intro1Frg intro1=Intro1Frg.newInstance();
        Intro2Frg intro2=Intro2Frg.newInstance();
        Intro3Frg intro3=Intro3Frg.newInstance(2,"第三页");//参数纯属演示，此处没有使用
        List<Fragment> fragments=new ArrayList<>();
        fragments.add(intro1);
        fragments.add(intro2);
        fragments.add(intro3);

        PagerAdapter mPagerAdapter=new LaunchPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(mPagerAdapter);
       /* circlePage.setViewPager(mViewPager);
        circlePage.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
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
        });*/
    }

    @Override
    public void onStartButtonClick(View v)
    {
        Intent intent = new Intent(this,LoginModeAct.class);
        startActivity(intent);
        this.finish();
    }
}

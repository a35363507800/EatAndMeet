package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2017/8/21
 * @description
 */
public class LeadAct extends BaseActivity
{
    @BindView(R.id.vp_viewpage)
    ViewPager mViewPager;
    //     @BindView(R.id.cpi_pager_indicator)
//     CirclePageIndicator circlePageIndicator;
    private LinearLayout lltPageIndicator;
    private TextView btnStart;
    private ImageView imWeLogin;

    private List<RelativeLayout> list;
    List<Integer> imgResources = new ArrayList<>(Arrays.asList(R.drawable.guide_page_p1,
            R.drawable.guide_page_p2, R.drawable.guide_page_p3, R.drawable.guide_page_p4));
    private Activity mAct;


    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_lead);
        mAct = this;
        ButterKnife.bind(this);
        initViewPager(imgResources);
        btnStart = (TextView) findViewById(R.id.btn_start);
        imWeLogin = (ImageView) findViewById(R.id.im_weixin_login);
        lltPageIndicator = (LinearLayout) findViewById(R.id.llt_page_indicator);
        lltPageIndicator.getChildAt(0).setBackgroundResource(R.drawable.shape_select_guide);
        lltPageIndicator.getChildAt(1).setBackgroundResource(R.drawable.shape_normal_guide);
        lltPageIndicator.getChildAt(2).setBackgroundResource(R.drawable.shape_normal_guide);
        lltPageIndicator.getChildAt(3).setBackgroundResource(R.drawable.shape_normal_guide);

        btnStart.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
        btnStart.setVisibility(View.VISIBLE);
        btnStart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EamApplication.getInstance().wxLoginFlag="2";
                Intent intent = new Intent(mAct, WelcomeAct.class);
                startActivity(intent);
                finish();
            }
        });

        imWeLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //让act到loginact自动进行微信登录逻辑
                EamApplication.getInstance().wxLoginFlag="1";
                Intent intent = new Intent(mAct, WelcomeAct.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initViewPager(final List<Integer> mUrls)
    {
        if (mUrls == null)
        {
            ToastUtils.showShort("没有可显示的图片");
            return;
        }
        list = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(mAct);

        for (int i = 0; i < mUrls.size(); i++)
        {
            RelativeLayout rlView = (RelativeLayout) inflater.inflate(R.layout.guid_page_view, null);
            ImageView photoView = (ImageView) rlView.getChildAt(0);
            photoView.setImageResource(mUrls.get(i));
            list.add(rlView);
        }

        PagerAdapter mPagerAdapter = new PagerAdapter()
        {
            @Override
            public int getCount()
            {
                return list.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object)
            {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position)
            {
                container.addView(list.get(position));
                return list.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object)
            {
                container.removeView(list.get(position));
            }
        };

        mViewPager.setAdapter(mPagerAdapter);
        // circlePageIndicator.setViewPager(mViewPager);
        // mViewPager.setPageMargin(30);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setCurrentItem(0);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                clearIndicatorFocusedState();
                lltPageIndicator.getChildAt(position).setBackgroundResource(R.drawable.shape_select_guide);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
    }

    private void clearIndicatorFocusedState()
    {

        int childCount = lltPageIndicator.getChildCount();
        for (int i = 0; i < childCount; i++)
        {
            lltPageIndicator.getChildAt(i).setBackgroundResource(R.drawable.shape_normal_guide);
        }
    }

}

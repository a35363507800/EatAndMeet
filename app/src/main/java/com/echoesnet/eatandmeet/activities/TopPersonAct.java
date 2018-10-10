package com.echoesnet.eatandmeet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.fragments.FaceFanFrg;
import com.echoesnet.eatandmeet.fragments.GoodSaleHostFrg;
import com.echoesnet.eatandmeet.fragments.HotHostFrg;
import com.echoesnet.eatandmeet.fragments.LocalTyrantFrg;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.orhanobut.logger.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @createDate 2017/7/14 16.
 * @description app主播排行榜的功能页面
 */

public class TopPersonAct extends BaseActivity
{
    private final String TAG = TopPersonAct.class.getSimpleName();

    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.vp_top)
    ViewPager vpTop;

    private int currentPage = 0;
    private FragmentActivity mAct;
    private List<Fragment> fragmentList;
    private LocalTyrantFrg localTyrantFrg;
    private FaceFanFrg faceFanFrg;
    private GoodSaleHostFrg goodSaleHostFrg;
    private HotHostFrg hotHostFrg;
    private final int START_FOR_MY_USERINFO = 101;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_top_person);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews()
    {
        mAct = this;
        topBarSwitch.inflateSwitchBtns(Arrays.asList("土壕乡绅", "看脸粉丝", "畅销主播", "人气主播"), 15, currentPage,
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
                    }

                    @Override
                    public void switchBtn(View view, int position)
                    {
                        Logger.t(TAG).d("选择到第》" + position);
                        vpTop.setCurrentItem(position);
                    }
                });
        topBarSwitch.getNavBtns2(new int[]{1, 0, 0, 0});
        LinearLayout centerContainer = topBarSwitch.getCenterContainer();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) centerContainer.getLayoutParams();
        params.removeRule(RelativeLayout.CENTER_IN_PARENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.setMargins(0, 0, CommonUtils.dp2px(mAct, -3), 0);//不知道为什么，不是紧贴右侧
        centerContainer.setLayoutParams(params);
        centerContainer.getParent().requestLayout();

        localTyrantFrg = new LocalTyrantFrg();
        faceFanFrg = new FaceFanFrg();
        goodSaleHostFrg = new GoodSaleHostFrg();
        hotHostFrg = new HotHostFrg();

        fragmentList = new ArrayList<>();
        fragmentList.add(localTyrantFrg);
        fragmentList.add(faceFanFrg);
        fragmentList.add(goodSaleHostFrg);
        fragmentList.add(hotHostFrg);

        vpTop.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager())
        {
            @Override
            public Fragment getItem(int position)
            {
                return fragmentList.get(position);
            }

            @Override
            public int getCount()
            {
                return fragmentList.size();
            }
        });
        //vp 设置缓存view 的个数
        vpTop.setOffscreenPageLimit(4);
        vpTop.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {

                topBarSwitch.changeSwitchBtn(position);
//                switch (position)
//                {
//                    case 0:
//                        localTyrantFrg.reLoadWebView(uid);
//                        break;
//                    case 1:
//                        faceFanFrg.reLoadWebView(uid);
//                        break;
//                    case 2:
//                        goodSaleHostFrg.reLoadWebView(uid);
//                        break;
//                    case 3:
//                        hotHostFrg.reLoadWebView(uid);
//                        break;
//                    default:
//                        break;
//                }
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }

        });

    }


    public void reloadWebView(String uid)
    {
        Logger.t(TAG).d("reloadWebView>>>>" + uid);
        for (int i = 0; i < fragmentList.size(); i++)
        {
            if (currentPage != i)
            {
                switch (i)
                {
                    case 0:
                        localTyrantFrg.reLoadWebView(uid);
                        break;
                    case 1:
                        faceFanFrg.reLoadWebView(uid);
                        break;
                    case 2:
                        goodSaleHostFrg.reLoadWebView(uid);
                        break;
                    case 3:
                        hotHostFrg.reLoadWebView(uid);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Logger.t(TAG).d("TopPersonAct执行H5方法" + requestCode);
        switch (requestCode)
        {
            case START_FOR_MY_USERINFO:

                for (int i = 0; i < fragmentList.size(); i++)
                {
                    fragmentList.get(i).onActivityResult(requestCode, resultCode, data);
                }
                break;
        }


    }

    public interface FocusCallBack{
        void focusUser(String uid);
    }

}

package com.echoesnet.eatandmeet.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.fragments.MyDateAcceptFrg;
import com.echoesnet.eatandmeet.fragments.MyDateSendFrg;
import com.echoesnet.eatandmeet.fragments.MyDateWishListFrg;
import com.echoesnet.eatandmeet.presenters.ImpIMyDateActView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyDateActView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;


/**
 * Created by an on 2016/11/22 0022.
 * Refactor by ben on 2017/2/18
 */
public class MyDateAct extends BaseActivity implements IMyDateActView
{
    private static final String TAG = MyDateAct.class.getSimpleName();
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.vp_order_type_pager)
    ViewPager mVp;
    List<Map<String, TextView>> navBtns;
    private List<Fragment> fragments;
    private FragmentActivity mAct;
    private int currentPage = 0;
    private MyDateSendFrg dateSendFrg;
    private MyDateAcceptFrg dateAcceptFrg;
    private MyDateWishListFrg myDateWishListFrg;
    private FragmentTransaction fragmentTransaction;
    private ImpIMyDateActView dateActView;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_date_action);
        ButterKnife.bind(this);
        afterViews();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Logger.t(TAG).d("currentPage>>" + currentPage);
//        FragmentTransaction frgTran = mAct.getSupportFragmentManager()
//                .beginTransaction();
//        for (int i = 0; i < fragments.size(); i++)
//        {
//            if (currentPage == i)
//                frgTran.show(fragments.get(i));
//            else
//                frgTran.hide(fragments.get(i));
//        }
//        //当Activity恢复时候fragment信息有可能不能恢复。
//        frgTran.commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterBroadcastReceiver();
    }

    private void afterViews()
    {
        mAct = this;
        currentPage = getIntent().getIntExtra("currentPage", currentPage);
        setResult(1);
        topBarSwitch.inflateSwitchBtns(Arrays.asList("发出的约会", "接受的约会"), currentPage,
                false, new TopbarSwitchSkeleton()
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
                        switchPage(position);
                        currentPage = position;
                        Logger.t(TAG).d("选择到第》" + position);
                    }

                    @Override
                    public void refreshPage(int position)
                    {
                        super.refreshPage(position);
                        if (position == 0)
                        {
                            dateSendFrg.reLoadWebView();
                        } else
                        {
                            dateAcceptFrg.reLoadWebView();
                        }
                    }
                });
        navBtns = topBarSwitch.getNavBtns2(new int[]{1, 0, 0, 1});
        Logger.t(TAG).d("currentPage--> " + currentPage);
        // myDateWishListFrg = MyDateWishListFrg.newInstance();
//        myDateWishListFrg.setInterWithParentActListener(new MyDateWishListFrg.InterWithParentActListener()
//        {
//            @Override
//            public void buttonClick(String btnId, String content, String action)
//            {
//                Logger.t(TAG).d("设置右上角编辑按钮》" + btnId);
//                if (btnId.equals("btnDateWishEdit"))
//                {
//                    TextView tv = navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON);
//                    tv.setText(content);
//                    tv.setTag(action);
//                    tv.setTextSize(16);
//                }
//            }
//        });


        if (currentPage != 0)
            navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).setVisibility(View.GONE);

        dateSendFrg = new MyDateSendFrg();
        dateAcceptFrg = new MyDateAcceptFrg();

        fragments = new ArrayList<>();
        // fragments.add(myDateWishListFrg);
        fragments.add(dateSendFrg);
        fragments.add(dateAcceptFrg);

        //初始化Adapter
        FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
        {
            @Override
            public int getCount()
            {
                return fragments.size();
            }

            @Override
            public Fragment getItem(int arg0)
            {
                return fragments.get(arg0);
            }
        };
        mVp.setAdapter(mPagerAdapter);
        switchPage(currentPage);
        mVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                topBarSwitch.changeSwitchBtn(position);

                if(position==0)
                    dateActView.queryRedStatus();
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });

        dateActView = new ImpIMyDateActView(mAct, this);

        registerBroadcastReceiver();
    }

    private void registerBroadcastReceiver()
    {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(EamConstant.EAM_HX_CMD_RECEIVE_RED_REMIND);
        //myIntentFilter.
        Logger.t(TAG).d("注册广播");
        //注册广播
        mAct.registerReceiver(updateUiReceiver, myIntentFilter);
    }

    private void unregisterBroadcastReceiver()
    {
        if (updateUiReceiver != null)
        {
            Logger.t(TAG).d("注销广播");
            mAct.unregisterReceiver(updateUiReceiver);
        }
    }

    BroadcastReceiver updateUiReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            Logger.t(TAG).d("myInfoFrg:" + action);
            if (EamConstant.EAM_HX_CMD_RECEIVE_RED_REMIND.equals(action))
            {
//                topBarSwitch.showMsgIndicator(1);
                if (dateActView != null)
                    dateActView.queryRedStatus();
            }
        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        dateActView.queryRedStatus();
    }

    private void switchPage(int showIndex)
    {
//        if (showIndex == 0)
//            navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).setVisibility(View.VISIBLE);
//        else
//            navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).setVisibility(View.GONE);
//        FragmentTransaction frgTran = mAct.getSupportFragmentManager()
//                .beginTransaction();
//        for (int i = 0; i < fragments.size(); i++)
//        {
//            if (showIndex == i)
//                frgTran.show(fragments.get(i));
//            else
//                frgTran.hide(fragments.get(i));
//        }
//        switch (showIndex)
//        {
////            case 0:
////                myDateWishListFrg.reLoadWebView();
////                break;
//            case 0:
//                dateSendFrg.reLoadWebView();
//                break;
//            case 1:
//                dateAcceptFrg.reLoadWebView();
//                break;
//        }
//        frgTran.commitAllowingStateLoss();

        mVp.setCurrentItem(showIndex);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.t(TAG).d("pay requestCode==" + requestCode + "pay resultCode==" + resultCode);
        if (requestCode == dateSendFrg.INTENT_RELOAD)
        {
            if (dateSendFrg != null)
                dateSendFrg.reLoadWebView();

        }
        //   myDateWishListFrg.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mAct, null, exceptSource, e);
    }

    @Override
    public void queryRedStatusCallBack(String response)
    {
        try
        {
            Logger.t(TAG).d("====================约吃饭红点返回结果:" + response + "======================");

            JSONObject body = new JSONObject(response);
            String inviteRemind = body.getString("inviteRemind");
            String invitedRemind = body.getString("invitedRemind");
            if (inviteRemind.equals("1"))
            {
                //    topBarSwitch.showMsgIndicator(0);
            }
            if (invitedRemind.equals("1"))
            {
                if(mVp.getCurrentItem()!=1)
                topBarSwitch.showMsgIndicator(1);
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

}
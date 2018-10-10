package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.fragments.AchievementFrg;
import com.echoesnet.eatandmeet.fragments.TaskFrg;
import com.echoesnet.eatandmeet.models.bean.FinishTaskBean;
import com.echoesnet.eatandmeet.presenters.ImpITaskActView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyTaskActView;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.views.widgets.FinishTaskDialog;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * Created by an on 2017/4/12 0012.
 */

public class TaskAct extends MVPBaseActivity<TaskAct, ImpITaskActView> implements IMyTaskActView
{
    private final static String TAG=TaskAct.class.getSimpleName();

    @BindView(R.id.top_bar_task)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.vp_task)
    ViewPager taskVp;

    private List<Fragment> fragments;
    private FinishTaskDialog finishTaskDialog;
    private Activity mAct;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_task);
        ButterKnife.bind(this);
        mAct = this;
        List<String> titleString = new ArrayList<>();
        setResult(1);
        finishTaskDialog = new FinishTaskDialog(this, R.style.Dialog02);
        fragments = new ArrayList<>();
        titleString.add(getResources().getString(R.string.task_title));
        titleString.add(getResources().getString(R.string.achievement_title));
        topBarSwitch.inflateSwitchBtns(titleString, 0, false, new TopbarSwitchSkeleton()
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
                taskVp.setCurrentItem(position);

            }
        });
        final TaskFrg taskFrg=new TaskFrg();
        final AchievementFrg achievementFrg=new AchievementFrg();
        fragments.add(taskFrg);
        fragments.add(achievementFrg);
        taskVp.setOffscreenPageLimit(2);
        taskVp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager())
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
        taskVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
                Logger.t(TAG).d("onPageScrolled>"+"position: "+position+"positionOffset: "+positionOffset+"positionOffsetPixels: "+positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position)
            {
                topBarSwitch.changeSwitchBtn(position);
                if (mPresenter != null)
                    mPresenter.updateTaskOk();

                if(position==1)
                {
                    setResult(666,getIntent().putExtra("clickAchievement",true));
                }

            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
                Logger.t(TAG).d("onPageScrollStateChanged>"+"state: "+state);
/*           软硬转化会有闪烁
                if (state==ViewPager.SCROLL_STATE_SETTLING)
                {
                    taskFrg.setWebViewLayerType(View.LAYER_TYPE_HARDWARE);
                    achievementFrg.setWebViewLayerType(View.LAYER_TYPE_HARDWARE);
                }else if (state==ViewPager.SCROLL_STATE_DRAGGING)
                {
                    taskFrg.setWebViewLayerType(View.LAYER_TYPE_SOFTWARE);
                    achievementFrg.setWebViewLayerType(View.LAYER_TYPE_SOFTWARE);
                }*/
            }
        });
        mPresenter.updateTaskOk();
        registerBroadcastReceiver();

        int index=getIntent().getIntExtra("index",0);
        topBarSwitch.changeSwitchBtn(index);
        taskVp.setCurrentItem(index);
//        finishTaskDialog.show("",new Gson().fromJson("{ \"gift\": [ { \"icon\": \"http://huisheng.ufile.ucloud.cn/14948296137193F6S40.png\", \"id\": \"0001\", \"name\": \"百合戳\", \"num\": \"10000\" }, { \"icon\": \"http://huisheng.ufile.ucloud.cn/14948296137193F6S40.png\", \"id\": \"0001\", \"name\": \"百合戳\", \"num\": \"10000\" } , { \"icon\": \"http://huisheng.ufile.ucloud.cn/14948296137193F6S40.png\", \"id\": \"0001\", \"name\": \"百合戳\", \"num\": \"10000\" } , { \"icon\": \"http://huisheng.ufile.ucloud.cn/14948296137193F6S40.png\", \"id\": \"0001\", \"name\": \"百合戳\", \"num\": \"10000\" } , { \"icon\": \"http://huisheng.ufile.ucloud.cn/14948296137193F6S40.png\", \"id\": \"0001\", \"name\": \"百合戳\", \"num\": \"10000\" } , { \"icon\": \"http://huisheng.ufile.ucloud.cn/14948296137193F6S40.png\", \"id\": \"0001\", \"name\": \"百合戳\", \"num\": \"10000\" } , { \"icon\": \"http://huisheng.ufile.ucloud.cn/14948296137193F6S40.png\", \"id\": \"0001\", \"name\": \"百合戳\", \"num\": \"10000\" } , { \"icon\": \"http://huisheng.ufile.ucloud.cn/14948296137193F6S40.png\", \"id\": \"0001\", \"name\": \"百合戳\", \"num\": \"10000\" } ] }",FinishTaskBean.class));

//        finishTaskDialog.show("",new Gson().fromJson("{\"exp\":\"200\",\"exp_icon\":\"http://huisheng.ufile.ucloud.cn/wd_renwu_exp@2x.png\"}",FinishTaskBean.class));
    }

    private void registerBroadcastReceiver()
    {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(EamConstant.EAM_HX_CMD_SUCC_RED_REMIND);
        myIntentFilter.addAction(EamConstant.EAM_HX_CMD_TASK_RED_REMIND);
        //注册广播
        mAct.registerReceiver(broadcastReceiver, myIntentFilter);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            switch (action)
            {
                case EamConstant.EAM_HX_CMD_SUCC_RED_REMIND:
                    if (taskVp.getCurrentItem() == 1)
                        topBarSwitch.showMsgIndicator(0);
                    break;
                case EamConstant.EAM_HX_CMD_TASK_RED_REMIND:
                    if (taskVp.getCurrentItem() == 0)
                        topBarSwitch.showMsgIndicator(1);
                    break;
                default:
                    break;
            }
        }
    };

    public void showFinishDialog(final String title, String jsonStr)
    {
        Intent intent = new Intent(EamConstant.ACTION_UPDATE_USER_INFO);
        intent.putExtra("needRefreshUserInfo", true);
        sendBroadcast(intent);

        finishTaskDialog.setGotoAt("",null);
        finishTaskDialog.setConfirm("确定");
        finishTaskDialog.show(title, new Gson().fromJson(jsonStr, FinishTaskBean.class));
    }

    @Override
    protected void onDestroy()
    {
        if (finishTaskDialog != null && finishTaskDialog.isShowing())
            finishTaskDialog.dismiss();
        mAct.unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected ImpITaskActView createPresenter()
    {
        return new ImpITaskActView();
    }

    @Override
    public void updateTaskOkCallBack(String task, String successes)
    {
        if ("1".equals(task) && taskVp.getCurrentItem() == 1)
        {
            topBarSwitch.showMsgIndicator(0);
        }
        else if ("1".equals(successes) && taskVp.getCurrentItem() == 0)
        {
            topBarSwitch.showMsgIndicator(1);
        }
    }
}

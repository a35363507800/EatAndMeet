package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2017/7/27
 * @description 约会管家页面
 */
public class MyDateHousekeepAct extends BaseActivity
{
    private static final String TAG = MyDateAct.class.getSimpleName();
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    List<Map<String,TextView>> navBtns;
    private Activity mAct;
    private MyDateWishListFrg myDateWishListFrg;
    private FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_date_house_keeper);
        ButterKnife.bind(this);
        afterViews();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

    }

    private void afterViews()
    {
        mAct = this;
        topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                myDateWishListFrg.triggerToH5();
                finish();
            }

            @Override
            public void right2Click(View view)
            {
                myDateWishListFrg.deleteToH5();
            }
        }).setText("约会管家");
        navBtns = topBarSwitch.getNavBtns2(new int[]{1, 0, 0, 1});

        TextView tv = navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON);
        tv.setText("移出");
        tv.setTextSize(16);
        tv.setTextColor(ContextCompat.getColor(mAct,R.color.C0412));
//        navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).setText("移除");
//        navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).setTextColor(ContextCompat.getColor(mAct, R.color.C0412));

        myDateWishListFrg = MyDateWishListFrg.newInstance();
        myDateWishListFrg.setInterWithParentActListener(new MyDateWishListFrg.InterWithParentActListener()
        {
            @Override
            public void buttonClick(String btnId, String content, String action)
            {
                Logger.t(TAG).d("设置右上角编辑按钮》" + btnId);
                if (btnId.equals("btnDateWishEdit"))
                {
                    TextView tv = navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON);
                    tv.setText("移出");
                    tv.setTag(action);
                    tv.setTextSize(16);
                    tv.setTextColor(ContextCompat.getColor(mAct,R.color.C0412));
                }
            }
        });

        fragmentTransaction =getSupportFragmentManager()
                .beginTransaction();

        fragmentTransaction.replace(R.id.fl_container,myDateWishListFrg);

        fragmentTransaction.commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.t(TAG).d("pay requestCode==" + requestCode + "pay resultCode==" + resultCode);
        myDateWishListFrg.onActivityResult(requestCode, resultCode, data);
    }

}

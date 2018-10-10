package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.fragments.CSayHelloUsersFrg;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @createDate 2017/7/24 17:59
 * @description
 */

public class CSayHelloUsersAct extends BaseActivity
{
    private static final String TAG = CSayHelloUsersAct.class.getSimpleName();

    @BindView(R.id.tbs_top_bar)
    TopBarSwitch topBar;

    private Activity mAct;
    private CSayHelloUsersFrg helloFrg;
    private TextView btnRight2;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_say_hello);
        ButterKnife.bind(this);
        this.mAct = this;
        topBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                finish();
            }

            @Override
            public void right2Click(View view)
            {
                if (helloFrg != null){
                    helloFrg.setAllMsgRead();
                    btnRight2.setTextColor(ContextCompat.getColor(mAct, R.color.C0331));
                    ((RelativeLayout) btnRight2.getParent()).setClickable(false);
                }
            }
        }).setText("打招呼");
        List<Map<String, TextView>> navBtns = topBar.getNavBtns2(new int[]{1, 0, 0, 1});
        btnRight2 = navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON);
        btnRight2.setText("忽略未读");
        btnRight2.setTextSize(16);
        initView();
    }

    private void initView()
    {
        helloFrg = new CSayHelloUsersFrg();
        helloFrg.setHelloFrgListener(new CSayHelloUsersFrg.ISayHelloFrgListener()
        {
            @Override
            public void onUnreadHelloMsgChanged(int unreadMsgCount)
            {
                if (unreadMsgCount == 0)
                {
                    btnRight2.setTextColor(ContextCompat.getColor(mAct, R.color.C0331));
                    ((RelativeLayout) btnRight2.getParent()).setClickable(false);
                }
                else
                {
                    btnRight2.setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
                    ((RelativeLayout) btnRight2.getParent()).setClickable(true);
                }
            }
        });
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_container, helloFrg)
                .commit();
    }
}

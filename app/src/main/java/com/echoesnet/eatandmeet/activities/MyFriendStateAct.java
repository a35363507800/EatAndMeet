package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.fragments.FrgMoments;
import com.echoesnet.eatandmeet.presenters.ImplMyFriendStatePre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyFriendStateView;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * Created by Administrator on 2017/7/14.
 */

public class MyFriendStateAct extends MVPBaseActivity<IMyFriendStateView,ImplMyFriendStatePre> implements IMyFriendStateView
{
    private static final String TAG = MWhoSeenMeAct.class.getSimpleName();
    private Activity mAct;
    @BindView(R.id.top_bar)
    TopBarSwitch topBar;
    private FrgMoments momentsFrg;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        mAct=this;
        setContentView(R.layout.act_friendstate);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        ButterKnife.bind(this);

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
                Intent intent = new Intent(MyFriendStateAct.this, TrendsPublishAct.class);
                startActivityForResult(intent, EamConstant.EAM_OPEN_TRENDS_PUBLISH);
            }
        }).setText("我的动态");

        List<Map<String, TextView>> navBtns = topBar.getNavBtns2(new int[]{1, 0, 0, 1});
        for (int i = 0; i < navBtns.size(); i++)
        {
            Map<String, TextView> map = navBtns.get(i);
            TextView tv = map.get(TopBarSwitch.NAV_BTN_ICON);
            switch (i)
            {
                case 1:
                    tv.setText("{eam-s-spades}");
                    tv.setTextColor(ContextCompat.getColor(mAct, R.color.C0412));

                    break;
                default:
                    break;
            }
        }

        initViewPager();
    }

    private void initViewPager()
    {
        this.momentsFrg = FrgMoments.newInstance(FrgMoments.TYPEDATA_MY_TRENDS, SharePreUtils.getUId(mAct));
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_friend_state_layout, this.momentsFrg).commit();
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                momentsFrg.refreshData();
            }
        },50);

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected ImplMyFriendStatePre createPresenter()
    {
        return new ImplMyFriendStatePre(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
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
    protected void onRestart()
    {
        super.onRestart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        momentsFrg.onActivityResult(requestCode,resultCode,data);

    }
}

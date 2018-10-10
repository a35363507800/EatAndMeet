package com.echoesnet.eatandmeet.activities.liveplay.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2017/3/21
 * @description 为了解决使用腾讯技术开直播，间前一个activity销毁太慢的不过引入的启动代理Activity
 */
public class StartLiveProxyAct extends Activity
{
    private Activity mAct;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        CommonUtils.setStatusBarDarkMode(this, true);
        //状态栏黑字体
        CommonUtils.setStatusBarDarkMode(this, true);
        mAct = this;
        Intent intent = getIntent();
        CommonUtils.startLivePlay(mAct,
                intent.getIntExtra("roomMode", 1),
                intent.getStringExtra("roomName"),
                intent.getStringExtra("vedioName"),
                intent.getStringExtra("flyPage"),
                intent.getStringExtra("roomid"),
                null);

        // CommonUtils.startLive(mAct,intent.getStringExtra("roomid"),intent.getStringExtra("roomMode"));
        new Timer().schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                finish();
            }
        }, 2000);
//        overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
    }

/*    private static void startLiveProxyAct(final Activity startingActivity, int roomMode, String roomName, String sign, String flyPage, final String strRoomid, Integer flags, int reqCode)
    {

    }*/
}

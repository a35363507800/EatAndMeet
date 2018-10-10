package com.echoesnet.eatandmeet.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by wangben on 2016/4/25.
 */
public class AppInstalledReceiver extends BroadcastReceiver
{
    final static String TAG=AppInstalledReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent)
    {
        final String action=intent.getAction();
        if (action==null)
            return;
        if (Intent.ACTION_PACKAGE_ADDED.equals(action))
        {
            //SharePreUtils.setIsInstall(context,true);
            //EventBus.getDefault().postSticky(new AppInstalledMsg("added"));
            //Logger.t(TAG).d("android.intent.action.PACKAGE_ADDED 触发了");
            //ToastUtils.showShort(context,"android.intent.action.PACKAGE_ADDED 触发了");
        }
        if (Intent.ACTION_PACKAGE_REPLACED.equals(action))
        {
            //SharePreUtils.setIsInstall(context,true);
            //EventBus.getDefault().postSticky(new AppInstalledMsg("replaced"));
            //Logger.t(TAG).d("android.intent.action.PACKAGE_REPLACED 触发了");
            //ToastUtils.showShort(context,"android.intent.action.PACKAGE_REPLACED 触发了");
        }
    }
}

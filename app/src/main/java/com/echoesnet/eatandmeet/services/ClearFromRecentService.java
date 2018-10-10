package com.echoesnet.eatandmeet.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.orhanobut.logger.Logger;

/**
 * Created by Administrator on 2016/8/19.
 */
public class ClearFromRecentService extends Service
{
    private final String TAG=ClearFromRecentService.class.getSimpleName();
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Logger.t(TAG).d("监控线程启动");
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Logger.t(TAG).d("监控线程销毁");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        super.onTaskRemoved(rootIntent);
        Logger.t(TAG).d("APP被杀死了");
    }
}

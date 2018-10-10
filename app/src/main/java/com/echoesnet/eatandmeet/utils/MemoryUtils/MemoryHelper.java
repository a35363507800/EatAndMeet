package com.echoesnet.eatandmeet.utils.MemoryUtils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;

import com.echoesnet.eatandmeet.listeners.IOnAppStateChangeListener;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/7.
 */

public class MemoryHelper implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2
{
    private final String TAG = MemoryHelper.class.getSimpleName();
    private IOnAppStateChangeListener mAppStateChangeListener;
    private static MemoryHelper instance = null;
    private static String stateOfLifeCycle = "";
    public static WeakReference<Activity> sTopActivityWeakRef;
    public static List<Activity> sActivityList = new LinkedList<>();
    private MemoryHelper()
    {
    }

    public synchronized static MemoryHelper getInstance()
    {
        if (instance == null)
        {
            instance = new MemoryHelper();
        }
        return instance;
    }

    //Application.ActivityLifecycleCallbacks
    @Override
    public void onActivityCreated(Activity activity, Bundle bundle)
    {
        stateOfLifeCycle = "Create";
        Logger.t(TAG).d("onActivityCreated");
        sActivityList.add(activity);
        setTopActivityWeakRef(activity);

    }

    @Override
    public void onActivityStarted(Activity activity)
    {
        stateOfLifeCycle = "Start";
        Logger.t(TAG).d("onActivityStarted");
        setTopActivityWeakRef(activity);
    }

    @Override
    public void onActivityResumed(Activity activity)
    {
        stateOfLifeCycle = "Resume";
        Logger.t(TAG).d("onActivityResumed");
        setTopActivityWeakRef(activity);
    }

    @Override
    public void onActivityPaused(Activity activity)
    {
        stateOfLifeCycle = "Pause";
        Logger.t(TAG).d("onActivityPaused");
    }

    @Override
    public void onActivityStopped(Activity activity)
    {
        stateOfLifeCycle = "Stop";
        Logger.t(TAG).d("onActivityStopped");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle)
    {
        Logger.t(TAG).d("onActivitySaveInstanceState");
    }

    @Override
    public void onActivityDestroyed(Activity activity)
    {
        stateOfLifeCycle = "Destroy";
        Logger.t(TAG).d("onActivityDestroyed");
        sActivityList.remove(activity);
    }


    @Override
    public void onTrimMemory(int level)
    {
        Logger.t(TAG).d("内存裁剪触发》" + level);
        // Determine which lifecycle or system event was raised.
        switch (level)
        {
            case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:
                /*
                   Release any UI objects that currently hold memory.
                   The user interface has moved to the background.
                */
                if (mAppStateChangeListener != null)
                {
                    mAppStateChangeListener.switchToBack();
                }
                break;
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:
                /*
                   Release any memory that your app doesn't need to run.

                   The device is running low on memory while the app is running.
                   The event raised indicates the severity of the memory-related event.
                   If the event is TRIM_MEMORY_RUNNING_CRITICAL, then the system will
                   begin killing background processes.
                */

                break;
            case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
            case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
                break;
            case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:
                /*
                   Release as much memory as the process can.

                   The app is on the LRU list and the system is running low on memory.
                   The event raised indicates where the app sits within the LRU list.
                   If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                   the first to be terminated.
                */

                break;
            default:
                /*
                  Release any non-critical data structures.
                  The app received an unrecognized memory level value
                  from the system. Treat this as a generic low-memory message.
                */
                break;
        }
        //onTrimMemory的TRIM_MEMORY_UI_HIDDEN 等级是在onStop方法之前调用的
/*        if (stateOfLifeCycle.equals("Stop"))
        {
            CommonUtils.isSwitched2Back = true;
            if (mAppStateChangeListener != null)
            {
                mAppStateChangeListener.switchToBack();
            }
        }*/
    }

    //以下两个为ComponentCallbacks函数
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        Logger.t(TAG).d("onConfigurationChanged");
    }

    @Override
    public void onLowMemory()
    {
        Logger.t(TAG).d("onLowMemory");
    }

    private static void setTopActivityWeakRef(Activity activity) {
        if (sTopActivityWeakRef == null || !activity.equals(sTopActivityWeakRef.get())) {
            sTopActivityWeakRef = new WeakReference<>(activity);
        }
    }

    public void setAppStateChangeListener(IOnAppStateChangeListener listener)
    {
        this.mAppStateChangeListener = listener;
    }

    public void removeAppStateChangeListener()
    {
        this.mAppStateChangeListener = null;
    }
}

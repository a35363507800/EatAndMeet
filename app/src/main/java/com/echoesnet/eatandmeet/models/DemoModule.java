package com.echoesnet.eatandmeet.models;

import android.content.Context;

import com.echoesnet.eatandmeet.models.datamodel.DemoManagerDependency;

/**
 * Created by wangben on 2016/4/12.
 */
public class DemoModule
{
/*    @Provides
    @Singleton*/
    public DemoManagerDependency provideAnalyticsManager(Context context)
    {
        return  new DemoManagerDependency(context);
    }
}

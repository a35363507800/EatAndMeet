package com.echoesnet.eatandmeet.models.datamodel;

import android.content.Context;
import android.util.Log;

public class DemoManagerDependency
{
    private static final String TAG = "DemoManagerDependency";

    public DemoManagerDependency(Context context)
    {
    }


    public void register()
    {
        Log.d(TAG, "触发注册函数 register()");
    }

    public void send(String data)
    {
        Log.d(TAG, data);
    }
}

package com.echoesnet.eatandmeet.utils;

import android.os.Build;

/**
 * Created by Administrator on 2016/4/12.
 */
public class OSHelper
{
    //必要的
    //@Inject
    public OSHelper()
    {
    }
    public String getDeviceBrand()
    {
        return Build.BRAND;
    }
}

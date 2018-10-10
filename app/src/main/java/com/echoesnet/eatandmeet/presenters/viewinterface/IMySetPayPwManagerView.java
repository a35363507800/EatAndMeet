package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.support.v4.util.ArrayMap;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/1/6.
 */

public interface IMySetPayPwManagerView
{
    void requestNetError(Call call, Exception e, String exceptSource);
    void validSecurityCodeCallback(ArrayMap<String,Object> response);
}

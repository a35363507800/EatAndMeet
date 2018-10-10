package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.support.v4.util.ArrayMap;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/1/13.
 */

public interface ISelectTableZoomView
{
    void requestNetError(Call call, Exception e, String exceptSource);
    void getTableStatusFromSeverCallback(ArrayMap<String, Object> map);
}

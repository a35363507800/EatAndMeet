package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.support.v4.util.ArrayMap;

import okhttp3.Call;

/**
 * Created by yqh on 2016/12/27.
 */

public interface IChooseGoWhereView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void requestNetErrorCallback(String interfaceName,Throwable e);
    void getResCallBack(ArrayMap<String, Object> map);
    void searchResCallBack(ArrayMap<String, Object> map);
}

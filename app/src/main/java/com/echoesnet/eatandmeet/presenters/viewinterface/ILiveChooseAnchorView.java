package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.support.v4.util.ArrayMap;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/13.
 */

public interface ILiveChooseAnchorView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetErrorCallback(String interfaceName, Throwable e);

    void getAnchorCallBack(ArrayMap<String, Object> response);

    void searchAnchorCallback(String response);
}

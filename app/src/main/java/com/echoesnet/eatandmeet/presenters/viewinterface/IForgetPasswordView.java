package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.support.v4.util.ArrayMap;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/1/6.
 */

public interface IForgetPasswordView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetErrorCallback(String interfaceName, Throwable e);

    void getPasswordCallback(String response);

    void validSecurityCodeCallback(ArrayMap<String, Object> map);
}

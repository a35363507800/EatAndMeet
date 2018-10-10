package com.echoesnet.eatandmeet.presenters.viewinterface;

/**
 * Created by Administrator on 2017/1/6.
 */

public interface IMySetVerifyOldPayPwView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void requestNetErrorCallback(String interfaceName,Throwable e);
    void verifyPayPasswordCallback(String response);
}

package com.echoesnet.eatandmeet.presenters.viewinterface;

/**
 * Created by Administrator on 2017/1/9.
 */

public interface IMyVerifyIdView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void requestNetErrorCallback(String interfaceName,Throwable e);
    void verifyIdInfoCallback(String response);
    void verifyIdInfoWithExistCallback(String response);
}

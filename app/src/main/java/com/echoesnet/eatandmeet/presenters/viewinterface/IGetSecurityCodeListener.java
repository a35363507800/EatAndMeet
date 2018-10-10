package com.echoesnet.eatandmeet.presenters.viewinterface;

/**
 * Created by Administrator on 2017/3/8.
 */

public interface IGetSecurityCodeListener
{
    void onSuccess();
    void onFailed(String errorCode);
}

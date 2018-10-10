package com.echoesnet.eatandmeet.presenters.viewinterface;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/10/27.
 */

public interface ILiveReadyView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetError(Throwable call, String interfaceName);

    void startLiveSuccess(String resultJsonStr);

    void getPermanentOrNotCallback(String response);

    void checkLiveIsAlreadyCreateCallback(Map<String, String> map);

}

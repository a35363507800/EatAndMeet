package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/27.
 */

public interface IDOrderMealDetailView
{

    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetError(Throwable call, String interfaceName);

    void collectedRestCallback(String response);

    void removeRestCallback(String response);

    void getRestInfoCallBack(String response);
}

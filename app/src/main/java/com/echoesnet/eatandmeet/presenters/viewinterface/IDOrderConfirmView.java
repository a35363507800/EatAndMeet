package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.view.View;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/1/5.
 */

public interface IDOrderConfirmView
{

    void callServerErrorCallback(String interfaceName, String code, String errBody,String change);

    void requestNetErrorCallback(String interfaceName, Throwable e);

    void checkPriceCallback(String response);

    void postOrderToServerCallback(String response, final View view, final String change);

    void postOrderToServerCallback2(String response, final View view, final String change);

    void queryMyConsultantCallback(String response);

    void getMyConsultantCallback(String response);

    void orderCheckCallback(String response,String date);

}

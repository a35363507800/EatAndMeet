package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.support.v4.util.ArrayMap;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/30.
 */

public interface IExchangeMoneyDetailView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void requestNetErrorCallback(String interfaceName,Throwable e);
    void exchangeRecordDetailCallBack(ArrayMap<String, Object> map);
}

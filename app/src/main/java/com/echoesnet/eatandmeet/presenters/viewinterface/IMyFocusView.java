package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.support.v4.util.ArrayMap;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/11/18.
 */

public interface IMyFocusView
{
     void callServerErrorCallback(String interfaceName, String code, String errBody);
     void requestNetErrorCallback(String interfaceName,Throwable e);

     void getAllFocusPersonCallBack(String response,String orderType);
     void changeFollowCallBack();
}

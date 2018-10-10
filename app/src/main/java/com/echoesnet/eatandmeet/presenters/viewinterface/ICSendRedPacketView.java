package com.echoesnet.eatandmeet.presenters.viewinterface;


import com.jungly.gridpasswordview.GridPasswordView;

import okhttp3.Call;

/**
 * Created by wangben on 2016/11/15.
 * 以后有可能引入rxAndroid
 */

public interface ICSendRedPacketView
{
    /*    //被观察者，事件产生者
        @POST(NetInterfaceConstant.UserC_payRed)
        Observable<ResponseBody> sendRedPacket(@QueryMap Map<String,String>params);*/
    void callServerErrorCallback(String interfaceName, String code, String errBody,GridPasswordView gridPasswordView);

    void requestNetError(Throwable error, String interfaceName);

    void sendReadPacket2Callback(String response, GridPasswordView gridPasswordView);

}

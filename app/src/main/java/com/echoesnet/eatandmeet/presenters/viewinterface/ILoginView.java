package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by ben on 2016/12/28.
 */

public interface ILoginView
{
    void requestNetError(Call call, Exception e, String exceptSource);
    void callServerFailCallback(String interfaceName, String code,String body);
    void getTokenIdCallback(String response,int type);
    void loginCallback(String response);
    void weChatLoginCallback(String response);
}

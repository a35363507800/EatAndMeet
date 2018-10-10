package com.echoesnet.eatandmeet.presenters.viewinterface;


import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/12.
 */

public interface IMMyInfoCenterMessageView
{

    void requestNetError(Call call, Exception e, String exceptSource);

    void getMessageCallback(String response);
}

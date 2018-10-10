package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/1/6.
 */

public interface IMySetAccountSecurityView
{
    void requestNetError(Call call, Exception e, String exceptSource);
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void checkPayPasswordStateCallback(String response);
}

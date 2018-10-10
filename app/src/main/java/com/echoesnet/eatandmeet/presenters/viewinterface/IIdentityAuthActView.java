package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by an on 2017/4/14 0014.
 */

public interface IIdentityAuthActView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetError(Call call, Exception e, String exceptSource);

    void getBuildAuthInfoSuc(String response);

    void getAlipayValidateSuc(String response);

    void getRealNameStateCallBack(String response, String type);
}

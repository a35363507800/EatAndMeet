package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.support.v4.util.ArrayMap;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/2/20.
 */

public interface IWeChatMakeInfoView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetError(Call call, Exception e, String exceptSource);

    void validSecurityCodeCallback(String response);

    void weChatDetailCallback(ArrayMap<String, Object> map);

    void addHXAccountToServerAndLoginCallback(ArrayMap<String, Object> map);
}

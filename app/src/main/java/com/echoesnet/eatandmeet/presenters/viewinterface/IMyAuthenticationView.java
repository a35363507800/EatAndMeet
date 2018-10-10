package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/12.
 */

public interface IMyAuthenticationView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetError(Call call, Exception e, String exceptSource);

    void postRealNameCallBack( String str);

    void getRealNameStateCallBack(String str);
}

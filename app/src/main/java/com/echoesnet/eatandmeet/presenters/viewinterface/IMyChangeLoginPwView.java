package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/1/6.
 */

public interface IMyChangeLoginPwView
{
    void requestNetError(Call call, Exception e, String exceptSource);

    void resetLoginPasswordCallback(String response);

    void callServerErrorCallback(String interfaceName, String errorcode, String errorbody);
}

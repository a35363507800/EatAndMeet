package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by ben on 2016/12/28.
 */

public interface IRegisterView
{
    void requestNetError(Call call, Exception e, String exceptSource);
    void registerCallback(String response);
    void validSecurityCodeCallback(String response);
}

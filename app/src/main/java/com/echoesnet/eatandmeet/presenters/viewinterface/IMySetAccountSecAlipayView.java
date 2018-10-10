package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/16.
 */

public interface IMySetAccountSecAlipayView
{
    void requestNetError(Call call, Exception e, String exceptSource);

    void checkAlipayStateCallback(String response);

    void bindAlipayCallback(String response);
}

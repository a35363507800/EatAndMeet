package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/27.
 */

public interface IDRefundDetailView
{
    void requestNetError(Call call, Exception e, String exceptSource);

    void getRefundDetailCallback(String response);
}

package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by an on 2016/12/15.
 */

public interface ICaptureActivityView
{
    void requestNetError(Call call, Exception e, String exceptSource);

    void receiveSuccessCallback(String response);

    void bindConsultantCallback(String response);

    void queryConsultantCallback(String response);

}

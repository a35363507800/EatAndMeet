package com.echoesnet.eatandmeet.presenters.viewinterface;


import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/19.
 */

public interface IMySetUserFeedbackCallback {
    void requestNetError(Call call, Exception e, String exceptSource);
    void commitFeedbackCallback(String response);
}

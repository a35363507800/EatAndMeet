package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by an on 2016/12/15.
 */

public interface IReportFoulsUserActView {
    void requestNetError(Call call, Exception e, String exceptSource);
    void reportUserCallback(String response);
}

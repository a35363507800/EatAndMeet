package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by an on 2016/12/15.
 */

public interface IReportFoulsResrActView {
    void requestNetError(Call call, Exception e, String exceptSource);
    void reportResrCallback(String response);
}

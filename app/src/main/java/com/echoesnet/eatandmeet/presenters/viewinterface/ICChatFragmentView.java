package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by an on 2016/12/12.
 */

public interface ICChatFragmentView
{
    void requestNetError(Call call, Exception e, String exceptSource);
    void getCheckRedPacketsStatesSuc(String response);
}

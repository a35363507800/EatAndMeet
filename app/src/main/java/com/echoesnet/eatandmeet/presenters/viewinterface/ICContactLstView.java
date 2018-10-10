package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/20.
 */

public interface ICContactLstView
{
    void requestNetError(Call call, Exception e, String exceptSource);

    void getApplyFriendInfoCallback(String response);
}

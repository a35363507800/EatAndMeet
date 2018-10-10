package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/1/9.
 */

public interface IPromotionActionView
{
    void requestNetError(Call call, Exception e, String exceptSource);
    void setModifyBalanceCallback(String response);
}

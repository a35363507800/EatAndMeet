package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by 充值成功接口 on 2016/11/2.
 */

public interface IRechargeResultView
{
    void requestNetError(Call call, Exception e, String exceptSource);
    void getAccountBalanceCallback(String str);
}

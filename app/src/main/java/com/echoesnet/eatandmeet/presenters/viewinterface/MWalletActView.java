package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/7/14.
 */

public interface MWalletActView
{
    /**
     * 失败
     * @param call
     * @param e
     * @param exceptSource
     */
    void requestNetError(Call call, Exception e, String exceptSource);

    /**
     * 账户信息
     * @param resultJsonStr
     */
    void getBalanceCallBack(String resultJsonStr);
}

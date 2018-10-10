package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/30.
 */

public interface IExchangeMoneyView
{
    /**
     * 网络错误
     * @param call
     * @param e
     * @param exceptSource
     */
    void requestNetError(Call call, Exception e, String exceptSource);

    /**
     * 饭票信息返回结果
     * @param response 返回结果
     */
    void getMyMealCallBack(String response);

    /**
     * 兑换饭票接口返回结果
     * @param response 返回结果
     */
    void exchangeToBalanceCallBack(String response);
}

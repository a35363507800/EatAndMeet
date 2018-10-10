package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/7/17.
 */

public interface IApproveProgressView
{
    /**
     * 失败
     * @param call
     * @param e
     * @param exceptSource
     */
    void requestNetError(Call call, Exception e, String exceptSource);


    void getRealCallBack(String response);

    void getContactCallback(String str);
}

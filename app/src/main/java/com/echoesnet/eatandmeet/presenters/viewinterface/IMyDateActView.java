package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/2/28.
 */

public interface IMyDateActView
{
    /**
     * 调用失败回调
     * @param call
     * @param e
     * @param exceptSource
     */
    void requestNetError(Call call, Exception e, String exceptSource);

    /**
     * 查询红点状态回调
     * @param response
     */
    void queryRedStatusCallBack(String response);
}

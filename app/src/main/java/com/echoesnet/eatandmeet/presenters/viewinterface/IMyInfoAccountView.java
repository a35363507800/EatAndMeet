package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/12.
 */

public interface IMyInfoAccountView
{
    /**
     * 请求网络错误
     */
    void requestNetError(Call call, Exception e, String exceptSource);

    /**
     * 我的余额信息回调
     */
    void getAccountDataCallback(String response);

    /**
     * 我的等级信息回调
     */
    void getMyLevelDataCallback(String response);

    /**
     * 新版充值列表回调
     */
    void getNewAccountDataCallback(String response);

    /**
     * 获取绑定状态
     * @param response
     */
    void getBindStatsCallBack(String response);

    /**
     * 我的余额信息回调，签约主播可调，返回结果包含提现规则
     */
    void getMyBalanceCallBack(String response);

    /**
     * 请求错误
     */
    void callServerErrorCallback(String interfaceName, String code, String errBody);
}

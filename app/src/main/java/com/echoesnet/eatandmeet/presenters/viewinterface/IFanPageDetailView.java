package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/11/29.
 */

public interface IFanPageDetailView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetError(Call call, Exception e, String exceptSource);

    void getMyBalanceCallBack(String str);

    void getBindStatsCallBack(String str);
}

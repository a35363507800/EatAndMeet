package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/1/5.
 */

public interface IOrderRecordLstAdapterView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetError(Call call, Exception e, String exceptSource);

    void deleteOrderCallBack(String response);

    void shareOrderCallBack(String response);

    void getApplyRefundClubSuccess(String response, String orderId);

}

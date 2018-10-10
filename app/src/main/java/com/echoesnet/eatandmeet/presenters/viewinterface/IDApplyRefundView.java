package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.jungly.gridpasswordview.GridPasswordView;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/26.
 */

public interface IDApplyRefundView
{

    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetError(Call call, Exception e, String exceptSource);

    void refundCallback(String response,String orderType);

}

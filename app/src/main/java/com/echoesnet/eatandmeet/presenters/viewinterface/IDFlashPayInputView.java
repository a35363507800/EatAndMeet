package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.jungly.gridpasswordview.GridPasswordView;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by ben on 2016/12/13.
 */

public interface IDFlashPayInputView
{
    void requestNetError(Call call, Exception e, String exceptSource);
    void callServerFailCallback(String interfaceName, String code, String body, Map<String,Object>tranMap);
    void getAccountBalanceCallback(String response);

    void quickPayCallback(String response, GridPasswordView gridPasswordView);

    void getMyConsultantCallback(String response);

    void queryConsultantCallback(String response);
}

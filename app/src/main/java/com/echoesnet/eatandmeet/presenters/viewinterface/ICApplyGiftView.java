package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.jungly.gridpasswordview.GridPasswordView;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/12.
 */

public interface ICApplyGiftView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody, GridPasswordView gridPasswordView);

    void requestNetErrorCallback(String interfaceName, Throwable e);

    void applyFriendByMoneyCallback(String response, GridPasswordView gridPasswordView, String applyReason);
}

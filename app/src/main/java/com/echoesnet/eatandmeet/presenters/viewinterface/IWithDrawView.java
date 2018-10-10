package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/11/29.
 */

public interface IWithDrawView
{
    void requestNetError(Call call, Exception e, String exceptSource);
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void withDrawToWeChatCallBack(String str);
    void loginWeChatSuccessCallBack(String str);
    void getAccountBalanceCallBack(String str);


}

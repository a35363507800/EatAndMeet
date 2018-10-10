package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.support.v4.util.ArrayMap;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/11/18.
 */

public interface IMakeUserInfoView
{
     void callServerErrorCallback(String interfaceName, String code, String errBody);
     void requestNetError(Call call, Exception e, String exceptSource);
     void getRegisterPresentCallback(String str);
     void inputUserInfoCallback(ArrayMap<String,Object> str);
     void addHXAccountToServerAndLoginCallback(ArrayMap<String,Object> str);
}

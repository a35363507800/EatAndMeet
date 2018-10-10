package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;

import okhttp3.Call;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2017/3/21
 * @description 设置房管相关接口定义
 */

public interface IManageFansView
{
    void requestNetErrorCallback(String interfaceName,Throwable e);

    void getAllFansPersonCallBack(ArrayMap<String, Object> map);

    void setAdminCallback(String response);
}

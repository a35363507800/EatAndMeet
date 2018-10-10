package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;

import java.sql.SQLTransactionRollbackException;
import java.util.Map;

import okhttp3.Call;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2016/11/21.
 * @description 用户详情页面接口定义
 */

public interface ICUserInfoView
{
    void requestNetError(Call call, Exception e, String exceptSource);

    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void editReMarkCallback(String input, String responseStr);

    void getUserInfoCallback(Map<String, Object> response);

    void saveContactToServerCallback(String response);

    void checkUserRoleCallback(String myUid, String myRole, String checkedUid, String checkedRole);

    void applyFriendByHelloCallback(String response);

    /**
     * 获取用户的禁言状态
     *
     * @param bodyStr
     */
    void getUserShutUpStateCallback(String bodyStr);


    /**
     * 设置用户的禁言状态
     *
     * @param bodyStr
     */
    void setUserShutUpYesCallback(String bodyStr);

    /**
     * 解除用户的禁言状态
     *
     * @param bodyStr
     */
    void setUserShutUpNoCallback(String bodyStr);
}

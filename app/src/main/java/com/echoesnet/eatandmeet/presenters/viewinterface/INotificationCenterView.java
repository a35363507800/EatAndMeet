package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.view.View;


import okhttp3.Call;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @createDate 2017/7/14 16.
 * @description app通知中心的功能接口
 */

public interface INotificationCenterView
{

    /**
     * 向后台发起的请求由于网络原因失败的回调
     *
     * @param call
     * @param exceptSource
     * @param e            异常
     */
    void requestNetError(Call call, Exception e, String exceptSource);

    /**
     * 加载消息通知列表的回调
     *
     * @param response 后台返回内容
     * @param type     类型（refresh 刷新，add 添加）
     */
    void requestAllNotifyCallback(String response, String type);

    /**
     * 忽略未读的回调
     *
     * @param response 后台返回内容
     */
    void ignoreUnreadCallBack(String response);

    /**
     * 关注、取消关注的回调
     *
     * @param response 后台返回内容
     * @param position 索引位置
     * @param operFlag 是否关注（0：否 , 1：是）
     * @param view     view
     */
    void focusCallBack(String response, int position, String operFlag, View view);

    /**
     * 删除通知消息的回调
     *
     * @param response 后台返回内容
     * @param position 索引位置
     */
    void deleteMessageCallBack(String response, int position);
}

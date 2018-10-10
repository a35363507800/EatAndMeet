package com.echoesnet.eatandmeet.presenters.viewinterface;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @createDate 2017/7/12 16.
 * @description app 系统消息功能接口
 */

public interface ISysNewsView
{
    /**
     * 向后台发起的请求由于网络原因失败的回调
     *
     * @param interfaceName 接口名称
     * @param e             异常
     */
    void requestNetErrorCallback(String interfaceName, Throwable e);

}

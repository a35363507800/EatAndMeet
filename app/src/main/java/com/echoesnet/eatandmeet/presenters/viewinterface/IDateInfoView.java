package com.echoesnet.eatandmeet.presenters.viewinterface;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @createDate 2017/7/14 16.
 * @description 个人详情页的约会 Tab页面 功能接口
 */

public interface IDateInfoView
{
    /**
     * 向后台发起的请求返回错误的回调
     *
     * @param interfaceName 接口名称
     * @param errorCode     错误码
     * @param errorBody     错误信息
     */
    void callServerErrorCallback(String interfaceName, String errorCode, String errorBody);

    /**
     * 向后台发起约会评价列表的回调
     *
     * @param body 后台返回数据
     */
    void userAppointmentCallBack(String body);

    /**
     * 向后台发起的约会支付金钱的回调
     *
     * @param body  后台返回数据
     */
    void appointPayCallBack(String body);

    /**
     * 校验约会的回调
     *
     * @param body 后台返回内容
     */
    void checkWishCallBack(String body);

    /**
     * 校验约会可约日期的回调
     *
     * @param body 后台返回内容
     */
    void checkReceiveCallBack(String body);
}

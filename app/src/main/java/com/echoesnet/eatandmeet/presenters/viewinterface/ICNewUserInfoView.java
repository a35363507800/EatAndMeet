package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.baidu.mapapi.common.SysOSUtil;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @createDate 2017/7/14 16.
 * @description app个人详情页的功能接口
 */

public interface ICNewUserInfoView
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
     * 向后台发起的请求由于网络原因失败的回调
     *
     * @param interfaceName 接口名称
     * @param e             异常
     */
    void requestNetErrorCallback(String interfaceName, Throwable e);

    /**
     * 修改备注的回调
     *
     * @param input       备注
     * @param responseStr 后台返回内容
     */
    void editReMarkCallback(String input, String responseStr);

    /**
     * 查看个人信息的回调
     *
     * @param body         后台返回内容
     * @param targetUserId 要查看的人的uid
     */
    void lookUserInfoCallBack(String body, String targetUserId);

    /**
     * 检查个人信息的回调
     *
     * @param body         后台返回内容
     * @param targetUserId 要查看的人的uid
     */
    void checkUserInfoCallBack(String body, String targetUserId);


    /**
     * 获取用户的禁言状态的回调
     *
     * @param bodyStr 后台返回内容
     */
    void getUserShutUpStateCallback(String bodyStr);


    /**
     * 设置用户的禁言状态的回调
     *
     * @param bodyStr 后台返回内容
     */
    void setUserShutUpYesCallback(String bodyStr);

    /**
     * 解除用户的禁言状态的回调
     *
     * @param bodyStr 后台返回内容
     */
    void setUserShutUpNoCallback(String bodyStr);

    /**
     * 拉黑用户的回调
     *
     * @param bodyStr 后台返回内容
     */
    void deFriendCallBack(String bodyStr);


    /**
     * 加关注用户的回调
     *
     * @param bodyStr 后台返回内容
     */
    void focusCallBack(String bodyStr);

    /**
     * 添加到约会管家的回调
     *
     * @param bodyStr 后台返回内容
     */
    void addWishCallBack(String bodyStr);

    /**
     * 查看与该用户的关系的回调
     *
     * @param bodyStr 后台返回内容
     */
    void queryUsersRelationShipCallBack(String bodyStr);

    /**
     * 查看直播间查看关系的回调
     *
     * @param myUid       我的uid
     * @param myRole      我的角色(观众，房管，主播)
     * @param checkedUid  对方的uid
     * @param checkedRole 对方的角色(观众，房管，主播)
     */
    void checkUserRoleCallback(String myUid, String myRole, String checkedUid, String checkedRole);


    /**
     * 检查红包状态的回调
     *
     * @param result 后台返回内容
     */
    void checkRedPacketStatsCallback(String result);

}

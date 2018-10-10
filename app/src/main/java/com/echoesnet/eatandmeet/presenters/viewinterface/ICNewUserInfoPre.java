package com.echoesnet.eatandmeet.presenters.viewinterface;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0.0
 * @creatData 2017/7/14 16.
 * @description app个人详情页的P层业务逻辑处理接口
 */

public interface ICNewUserInfoPre
{

    /**
     * 查看用户信息
     *
     * @param targetUserId 对方的uid
     */
    void lookUserInfo(String targetUserId);


    /**
     * 用户信息，主要检查是否在心愿单里
     *
     * @param targetUserId 对方的uid
     */
    void checkUserInfo(String targetUserId);


    /**
     * 修改用户备注
     *
     * @param reMark   备注
     * @param toAddUid 要修改的人uId
     */
    void editReMark(final String reMark, final String toAddUid);

    /**
     * 检查用户的禁言状态
     *
     * @param avRoomId 直播间房间id
     * @param userUid  对方uid
     */
    void checkUserShutUpState(String avRoomId, String userUid);

    /**
     * 设置禁言
     *
     * @param avRoomId 直播间房间id
     * @param userUid  对方uid
     */
    void setUserShutUpYes(String avRoomId, String userUid);

    /**
     * 解除禁言
     *
     * @param avRoomId 直播间房间id
     * @param userUid  对方uid
     */
    void setUserShutUpNo(String avRoomId, String userUid);

    /**
     * 查询关系
     *
     * @param luid 要查看的人uId
     */
    void queryUsersRelationShip(String luid);


    /**
     * 拉黑用户
     *
     * @param luId 要拉黑的人uid
     */
    void deFriend(String luId);

    /**
     * 添加关注
     *
     * @param luId     要加关注的人uId
     * @param operFlag 是否关注（0：否 , 1：是）
     */

    void focusPerson(String luId, String operFlag);

    /**
     * 添加到约会管家
     *
     * @param luId 要加约会的人uId
     */
    void addWish(String luId);


    /**
     * 获取某一用户在给定直播间的身份，以及自己的身份
     *
     * @param avRoomId 直播间房间id
     * @param myUid    查看用户Uid
     * @param checkUid 被查看用户的Uid
     */
    void checkUserRole(String avRoomId, String myUid, String checkUid);

    /**
     * 检查红包状态
     *
     * @param redPacketIds 红包id 集合
     */
    void checkRedPacketsStates(List<String> redPacketIds);
}

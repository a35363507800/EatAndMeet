package com.echoesnet.eatandmeet.activities.liveplay.Presenter.Interfaces;

import com.echoesnet.eatandmeet.models.bean.RefreshLiveMsgBean;

import java.util.Map;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2017/4/17
 * @description 直播间交互逻辑接口, 属于规范View的接口，相应的Activity实现此接口
 */
public interface ILiveRoomInteractView
{
    /**
     * Follow host callback.
     *
     * @param param the param
     */
    void followHostCallback(Map<String, String> param);

    /**
     * 群发红包类型消息、去更新消息列表显示
     *
     * @param hxId  the hx id
     * @param param the param
     */
    void sendRedCallback(String hxId, Map<String, String> param);

    /**
     * 刷新直播消息列表
     *
     * @param liveMsgBean the live msg bean
     */
    void refreshText(RefreshLiveMsgBean liveMsgBean);

    /**
     * 收到礼物刷新礼物
     *
     * @param param the param
     */
    void refreshGift(Map<String, String> param);

    /**
     * 显示连麦邀请窗口
     */
    void showInviteDialog();

    /**
     * 连麦用户是否同意连麦邀请
     *
     * @param hxId     the hx id
     * @param nickName the nick name
     * @param state    the state
     */
    void handleInviteRequest(String hxId, String nickName, String state);

    /**
     * Member join.
     *
     * @param id      the id
     * @param name    the name
     * @param headImg the head img
     * @param level   the level
     * @param sign    the sign
     */
    void memberJoin(String id, String name, String headImg, String level, String sign);

    /**
     * Fake member join.
     *
     * @param id        the id
     * @param name      the name
     * @param headImg   the head img
     * @param userLevel the user level
     */
    void fakeMemberJoin(String id, String name, String headImg, String userLevel);

    /**
     * Member quit.
     *
     * @param id   the id
     * @param name the name
     */
    void memberQuit(String id, String name, String hxId);

    /**
     * Fake member quit.
     *
     * @param id      the id
     * @param name    the name
     * @param headImg the head img
     */
    void fakeMemberQuit(String id, String name, String headImg);

    /**
     * 直播结束
     */
    void livePlayEnd();




}

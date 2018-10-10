package com.echoesnet.eatandmeet.activities.liveplay.Presenter.Interfaces;
import java.util.Map;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2017 /4/17
 * @description 直播流程接口回调 ，属于规范View的接口，相应的Activity实现此接口
 */
public interface ILiveFlowView
{
    /**
     * Create room success.
     *
     * @param response the response
     */
    void createRoomSuccess(Map<String,Object> response);

    /**
     * Create room fault.
     *
     * @param errCode the error code
     * @param errMsg  the error message
     */
    void createRoomFault(int errCode,String errMsg);

    /**
     * Join room success.
     *
     * @param response the response
     */
    void joinRoomSuccess(Map<String, Object> response);

    /**
     * Join room fault.
     *
     * @param errCode the err code
     * @param errMsg  the err msg
     */
    void joinRoomFault(String errCode,String errMsg);

    /**
     * Close room success.
     *
     * @param response the response
     */
    void closeRoomSuccess(Map<String,Object> response);

    /**
     * Close room fault.
     *
     * @param errCode the err code
     * @param errMsg  the err msg
     */
    void closeRoomFault(String errCode,String errMsg);

    /**
     * Quit room success.
     *
     * @param response the response
     */
    void quitRoomSuccess(Map<String,Object> response);

    /**
     * Quit room fault.
     *
     * @param errCode the err code
     * @param errMsg  the err msg
     */
    void quitRoomFault(String errCode,String errMsg);

    /**
     * 观众 断流
     */
    void memberVideoStreamPause();

    /**
     * 观众 断流恢复
     */
    void memberVideoStreamResume();

    /**
     * 主播音视频流恢复
     */
    void hostVideoStreamResume();

    /**
     * 主播音视频流暂停
     */
    void hostVideoStreamPause();

    /**
     * 主播断网
     */
    void hostNetDisconnect();

    /**
     * 主播网络恢复
     */
    void hostNetReconnect();
    /**
     * 添加用户断流计时
     */
    void addLiveDisConnectTime();
    /**
     * 移除用户断流计时
     */
    void removeLiveDisConnectTime();

}

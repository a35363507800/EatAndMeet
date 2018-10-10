package com.echoesnet.eatandmeet.activities.liveplay.Presenter.Interfaces;

import com.echoesnet.eatandmeet.models.bean.GiftBean;
import com.echoesnet.eatandmeet.models.datamodel.ExitRoomType;

import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2017/4/19
 * @description 直播间交互接口，相关presenter实现
 */
public interface ILiveRoomInteractPre
{
    /**
     * 切换摄像头
     */
    void switchCamera();

    /**
     * 关注主播
     */
    void followHost();

    /**
     * 给主播点赞
     */
    void praiseHost();

    /**
     * 向主播发送礼物
     *
     * @param chosenGiftId  礼物id
     * @param chosenGiftNum 一次发送的礼物数量
     * @param giftBean      礼物bean
     */
    void sendGift(String chosenGiftId, String chosenGiftNum, GiftBean giftBean);

    /**
     * 切换用户的弹幕开关状态
     *
     * @param flag true打开弹幕（即普通发言会触发弹幕） false 关闭弹幕
     */
    void switchBarrage(boolean flag);

    /**
     * 发送弹幕
     *
     * @param msg 弹幕的内容
     */
    void sendBarrage(String msg);

    /**
     * 拉取观众列表
     *
     * @param index     分页起始index
     * @param tranParam 接口调用过程中携带的参数
     */
    void pullAudiences(int index, Map<String, Object> tranParam);

    /**
     * 观众进入房间处理函数
     * @param uid     app 用户的uid
     * @param id      用户六位数字id
     * @param nickName
     * @param headImg
     * @param isFakeMember
     * @param level
     * @param imId   Im id  腾讯：腾讯id  环信：环信id
     * @return 是否增加一个新用户  true 增加  false 不增加
     */
    boolean audienceIntoRoom(String uid,String id, String nickName, String headImg,
                             boolean isFakeMember, String level,String imId,String isVuser);

    /**
     * 领取红包
     *
     * @param streamId 红包的唯一标识
     * @param roomId   房间Id
     * @param Id       用户的数字 id
     */
    void getGroundRed(String streamId, String roomId, String Id);

    /**
     * 创建房间时获取房管列表
     *
     * @param roomId 房间Id
     * @param imId   IMId
     */
    void enterRoomResetAdmin(String roomId, String imId);

    /**
     * 观众进入房间时弹出设置房管对话框
     *
     * @param adminUid 房管的uId
     * @param roomId   房间Id
     */
    void enteringRoomSetAdminDialog(String adminUid, String roomId);

    /**
     * 观众进入房间时弹出取消房管对话框
     *
     * @param adminUid 房管的uId
     * @param roomId   房间Id
     */
    void enteringRoomCancelAdminDialog(String adminUid, String roomId);

    /**
     * 通知后台关闭直播间
     *
     * @param videoName 录播文件名
     */
    void notifyServerCloseRoom(String videoName);

    /**
     * 设置美颜
     * @param beauty  美颜值
     * @param white  美白值
     */
    void setBeautyData(int beauty,int white);

    /**
     * 向后台发送心跳
     */
    void sendHeartBeat();


    /** 查看主播基本信息
     * look anchor BaseInfo.
     * @param luId the host id
     */
    void lookHostInfo(String luId);

    /**
     * 加心愿单
     * @param  luId the host id
     */
    void addWish(String luId);

  /*   void notifyShuntUpByAdmin(String userNick,String operMsg);
    void notifyShuntUpByHost(String userNick,String operMsg);
    void notifyShuntUpOffByHost(String userNick,String operMsg);
    void notifyShuntUpOffByAdmin(String userNick,String operMsg);*/

    void notifySetAdminByHost(Map<String,String>paramMap);
    void notifyCancelAdminByHost(Map<String,String>paramMap);
    void notifyShuntUpByAdmin(Map<String,String>paramMap);
    void notifyShuntUpByHost(Map<String,String>paramMap);
    void notifyShuntUpOffByHost(Map<String,String>paramMap);
    void notifyShuntUpOffByAdmin(Map<String,String>paramMap);

    /**
     * 退出直播间
     * @param roomId
     */
    void exitRoomToCallServer(String roomId,ExitRoomType type);

    /**
     * 直播间可邀请用户
     * @param gameId 游戏id
     * @param roomId 直播间id
     * @param start 起始
     * @param num 条数
     */
    void getCanInviteList(String gameId, String roomId, String start, String num,String type);

    /**
     * 邀请列表
     * @param gameId 游戏id
     * @param roomId 直播间id
     * @param start 起始
     * @param num 条数
     */
    void getGameInviters(String gameId, String roomId, String start, String num,String type);

    /**
     * 发送邀请
     * @param gameId  游戏id
     * @param roomId 直播间id
     * @param inviteArray 邀请对象array
     */
    void sendInvitation(String gameId, String roomId, String inviteArray,List<String> selectTxIdList,List<Integer> selectPosition);

    /**
     * 回应邀请
     * @param gameId  游戏id
     * @param roomId 直播间id
     * @param lUId 发邀请的人
     * @param flg 0：同意，1：拒绝
     */
    void answerInvitation(String gameId, String roomId, String lUId,String id,  String flg,int position);

    /**
     * 获取游戏参与详情
     * @param gameId 游戏id
     */
    void getMatchResult(String gameId);

    /**
     * 参与详情
     * @param gameId 游戏id
     */
    void joinGame(String gameId);

    /**
     * 日月星分享
     * @param gameId 游戏id
     */
    void shareGame(String gameId, String matchingId, final String type, String score);

    /**
     * 获取分享内容
     * @param gameId 游戏id
     */
    void shareH5(String gameId,String matchingId, String score, String isMyDynamics);

    /**
     * 退出游戏
     * @param gameId 游戏id
     */
    void exitGame(String gameId);

    /**
     * 检测是否弹窗
     * @param gameId 游戏id
     */
    void checkPopups(String gameId);
}

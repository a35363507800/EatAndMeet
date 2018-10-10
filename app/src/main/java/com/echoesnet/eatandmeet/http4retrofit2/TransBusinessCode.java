package com.echoesnet.eatandmeet.http4retrofit2;


import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuyang on 2016/11/11.
 */

public class TransBusinessCode
{
    private static final Map<String, String> mapBusiness = mapBusiness();

    private static Map<String, String> mapBusiness()
    {
        //添加接口注意，后边不要硬编码,分类加入。当映射表（预先加载到内存中的）的尺寸膨胀到不可接受的程度时需要重构--wb
        Map<String, String> mapErr = new HashMap<>();

/*        mapErr.put(NetInterfaceConstant.AppointmentC_userAppointment, NetInterfaceConstant.And_AppointmentC_userAppointment);
        mapErr.put(NetInterfaceConstant.AppointmentC_addWish, NetInterfaceConstant.And_AppointmentC_addWish);
        mapErr.put(NetInterfaceConstant.AppointmentC_payWish, NetInterfaceConstant.And_AppointmentC_payWish);
        mapErr.put(NetInterfaceConstant.AppointmentC_checkWish, NetInterfaceConstant.And_AppointmentC_checkWish);
        mapErr.put(NetInterfaceConstant.ActivityC_popup, NetInterfaceConstant.And_ActivityC_popup);
        mapErr.put(NetInterfaceConstant.AppointmentC_checkReceive, NetInterfaceConstant.And_AppointmentC_checkReceive);
        mapErr.put(NetInterfaceConstant.ActivityC_trend, NetInterfaceConstant.And_ActivityC_trend);


        mapErr.put(NetInterfaceConstant.CheckInC_sevenCheckIn, NetInterfaceConstant.And_CheckInC_sevenCheckIn);
        mapErr.put(NetInterfaceConstant.CheckInC_sevenWeal, NetInterfaceConstant.And_CheckInC_sevenWeal);
        mapErr.put(NetInterfaceConstant.CheckInC_todayCheck, NetInterfaceConstant.And_CheckInC_todayCheck);
        mapErr.put(NetInterfaceConstant.CheckInC_monthCheck, NetInterfaceConstant.And_CheckInC_monthCheck);
        mapErr.put(NetInterfaceConstant.CheckInC_monthCheck_v412, NetInterfaceConstant.And_CheckInC_monthCheck_v412);
        mapErr.put(NetInterfaceConstant.CheckInC_checkIn, NetInterfaceConstant.And_CheckInC_checkIn);
        mapErr.put(NetInterfaceConstant.CheckInC_checkIn_v412, NetInterfaceConstant.And_CheckInC_checkIn_v412);

        mapErr.put(NetInterfaceConstant.EvalC_evaluationService, NetInterfaceConstant.And_EvalC_evaluationService);

        mapErr.put(NetInterfaceConstant.FriendC_pullTheBlack, NetInterfaceConstant.And_FriendC_pullTheBlack);
        mapErr.put(NetInterfaceConstant.FriendC_pullTheBlack, NetInterfaceConstant.And_FriendC_pullTheBlack);
        mapErr.put(NetInterfaceConstant.FriendC_firstTalk, NetInterfaceConstant.And_FriendC_firstTalk);
        mapErr.put(NetInterfaceConstant.FriendC_searchFocusOrFans, NetInterfaceConstant.And_FriendC_searchFocusOrFans);
        mapErr.put(NetInterfaceConstant.FriendC_refreshPhoneContact, NetInterfaceConstant.And_FriendC_refreshPhoneContact);
        mapErr.put(NetInterfaceConstant.FriendC_myPhoneContact, NetInterfaceConstant.And_FriendC_myPhoneContact);
        mapErr.put(NetInterfaceConstant.FriendC_delBlack, NetInterfaceConstant.And_FriendC_delBlack);
        mapErr.put(NetInterfaceConstant.FriendC_countHello, NetInterfaceConstant.And_FriendC_countHello);
        mapErr.put(NetInterfaceConstant.FriendC_countHello, NetInterfaceConstant.And_FriendC_countHello);
        mapErr.put(NetInterfaceConstant.FriendC_editRemark, NetInterfaceConstant.And_FriendC_editRemark);
        mapErr.put(NetInterfaceConstant.FriendC_sayHello, NetInterfaceConstant.And_FriendC_sayHello);
        mapErr.put(NetInterfaceConstant.FriendC_searchUser, NetInterfaceConstant.And_FriendC_searchUser);

        mapErr.put(NetInterfaceConstant.GameC_share, NetInterfaceConstant.And_GameC_share);
        mapErr.put(NetInterfaceConstant.GameC_shareH5, NetInterfaceConstant.And_GameC_shareH5);
        mapErr.put(NetInterfaceConstant.GameC_enterGame, NetInterfaceConstant.And_GameC_enterGame);
        mapErr.put(NetInterfaceConstant.GameC_exitGame, NetInterfaceConstant.And_GameC_exitGame);
        mapErr.put(NetInterfaceConstant.GameC_gameList, NetInterfaceConstant.And_GameC_gameList);

        mapErr.put(NetInterfaceConstant.HeartC_beat, NetInterfaceConstant.And_HeartC_beat);

        mapErr.put(NetInterfaceConstant.IndexC_encounter_v402, NetInterfaceConstant.And_IndexC_encounter_v402);
        mapErr.put(NetInterfaceConstant.IndexC_carouselEnc, NetInterfaceConstant.And_IndexC_carouselEnc);

        mapErr.put(NetInterfaceConstant.LiveC_startLive, NetInterfaceConstant.And_LiveC_startLive);
        mapErr.put(NetInterfaceConstant.LiveC_enterRoom, NetInterfaceConstant.And_LiveC_enterRoom);
        mapErr.put(NetInterfaceConstant.LiveC_giftList, NetInterfaceConstant.And_LiveC_giftList);
        mapErr.put(NetInterfaceConstant.LiveC_sendGift, NetInterfaceConstant.And_LiveC_sendGift);
        mapErr.put(NetInterfaceConstant.LiveC_giftVersion, NetInterfaceConstant.And_LiveC_giftVersion);
        mapErr.put(NetInterfaceConstant.LiveC_focus, NetInterfaceConstant.And_LiveC_focus);
        mapErr.put(NetInterfaceConstant.LiveC_saveMultiVideo, NetInterfaceConstant.And_LiveC_saveMultiVideo);
        mapErr.put(NetInterfaceConstant.LiveC_saveChnlId, NetInterfaceConstant.And_LiveC_saveChnlId);
        mapErr.put(NetInterfaceConstant.LiveC_closeRoom, NetInterfaceConstant.And_LiveC_closeRoom);
        mapErr.put(NetInterfaceConstant.LiveC_swap, NetInterfaceConstant.And_LiveC_swap);
        mapErr.put(NetInterfaceConstant.LiveC_roomMember_v307, NetInterfaceConstant.And_LiveC_roomMember_v307);
        mapErr.put(NetInterfaceConstant.LiveC_sendBarrage, NetInterfaceConstant.And_LiveC_sendBarrage);
        mapErr.put(NetInterfaceConstant.LiveC_barrage, NetInterfaceConstant.And_LiveC_barrage);
        mapErr.put(NetInterfaceConstant.LiveC_roomAdminList, NetInterfaceConstant.And_LiveC_roomAdminList);
        mapErr.put(NetInterfaceConstant.LiveC_getGroupRed, NetInterfaceConstant.And_LiveC_getGroupRed);
        mapErr.put(NetInterfaceConstant.LiveC_roomAdmin, NetInterfaceConstant.And_LiveC_roomAdmin);
        mapErr.put(NetInterfaceConstant.LiveC_cancelRoomAdmin, NetInterfaceConstant.And_LiveC_cancelRoomAdmin);
        mapErr.put(NetInterfaceConstant.LiveC_creatChatRoom, NetInterfaceConstant.And_LiveC_createChatRoom);
        mapErr.put(NetInterfaceConstant.LiveC_onWheat, NetInterfaceConstant.And_LiveC_onWheat);
        mapErr.put(NetInterfaceConstant.LiveC_sendMsg, NetInterfaceConstant.And_LiveC_sendMsg);
        mapErr.put(NetInterfaceConstant.LiveC_muteStatus, NetInterfaceConstant.And_LiveC_muteStatus);
        mapErr.put(NetInterfaceConstant.LiveC_anchorBaseInfo, NetInterfaceConstant.And_LiveC_anchorBaseInfo);
        mapErr.put(NetInterfaceConstant.LiveC_focus, NetInterfaceConstant.And_LiveC_focus);
        mapErr.put(NetInterfaceConstant.LiveC_exitRoom, NetInterfaceConstant.And_LiveC_exitRoom);
        mapErr.put(NetInterfaceConstant.LiveC_delMute, NetInterfaceConstant.And_LiveC_delMute);
        mapErr.put(NetInterfaceConstant.LiveC_getReal, NetInterfaceConstant.And_LiveC_getReal);
        mapErr.put(NetInterfaceConstant.LiveC_userRole, NetInterfaceConstant.And_LiveC_userRole);
        mapErr.put(NetInterfaceConstant.LiveC_addMute, NetInterfaceConstant.And_LiveC_addMute);
        mapErr.put(NetInterfaceConstant.LiveC_newLiveList,NetInterfaceConstant.And_LiveC_newLiveList);



        mapErr.put(NetInterfaceConstant.MessageC_ignoreUnread, NetInterfaceConstant.And_MessageC_ignoreUnread);
        mapErr.put(NetInterfaceConstant.MessageC_querySystemMessage, NetInterfaceConstant.And_MessageC_querySystemMessage);
        mapErr.put(NetInterfaceConstant.MessageC_queryMessage, NetInterfaceConstant.And_MessageC_queryMessage);
        mapErr.put(NetInterfaceConstant.MessageC_deleteMessage, NetInterfaceConstant.And_MessageC_deleteMessage);
        mapErr.put(NetInterfaceConstant.MessageC_deleteSystemMessage, NetInterfaceConstant.And_MessageC_deleteSystemMessage);


        mapErr.put(NetInterfaceConstant.ChristmasC_giveCard, NetInterfaceConstant.And_NationalDayC_giveCard);
        mapErr.put(NetInterfaceConstant.ChristmasC_askCard, NetInterfaceConstant.And_NationalDayC_askCard);
        mapErr.put(NetInterfaceConstant.NeighborC_editRemark, NetInterfaceConstant.And_NeighborC_editRemark);
        mapErr.put(NetInterfaceConstant.NewbieC_guide, NetInterfaceConstant.And_NewbieC_guide);
        mapErr.put(NetInterfaceConstant.NewbieC_iknow, NetInterfaceConstant.And_NewbieC_iknow);




        mapErr.put(NetInterfaceConstant.OrderC_orderDetail, NetInterfaceConstant.And_OrderC_orderDetail);

        mapErr.put(NetInterfaceConstant.ReceiveC_sendLocation, NetInterfaceConstant.And_ReceiveC_sendLocation);
        mapErr.put(NetInterfaceConstant.ReceiveC_sendLocationOnoff, NetInterfaceConstant.And_ReceiveC_sendLocationOnoff);
        mapErr.put(NetInterfaceConstant.RestaurantC_resListBanner, NetInterfaceConstant.And_RestaurantC_resListBanner);
        mapErr.put(NetInterfaceConstant.RestaurantC_resListForAppo, NetInterfaceConstant.And_RestaurantC_resListForAppo);
        mapErr.put(NetInterfaceConstant.RestaurantC_resListByES_161011, NetInterfaceConstant.And_RestaurantC_resListByES_161011);
        mapErr.put(NetInterfaceConstant.RestaurantC_detail, NetInterfaceConstant.And_RestaurantC_detail);




        mapErr.put(NetInterfaceConstant.SinglesDayC_sendRed, NetInterfaceConstant.And_SinglesDayC_sendRed);
        mapErr.put(NetInterfaceConstant.SinglesDayC_getRed, NetInterfaceConstant.And_SinglesDayC_getRed);
        mapErr.put(NetInterfaceConstant.SinglesDayC_share, NetInterfaceConstant.And_SinglesDayC_share);
        mapErr.put(NetInterfaceConstant.SinglesDayC_myIncome, NetInterfaceConstant.And_SinglesDayC_myIncome);



        mapErr.put(NetInterfaceConstant.TrendC_likeTrend, NetInterfaceConstant.And_TrendC_likeTrend);
        mapErr.put(NetInterfaceConstant.TrendC_publishTrend, NetInterfaceConstant.And_TrendC_publishTrend);
        mapErr.put(NetInterfaceConstant.TrendC_trendMsgList, NetInterfaceConstant.And_TrendC_trendMsgList);
        mapErr.put(NetInterfaceConstant.TrendC_likeMyTrendList, NetInterfaceConstant.And_TrendC_likeMyTrendList);
        mapErr.put(NetInterfaceConstant.TrendC_cleanTrendMsg, NetInterfaceConstant.And_TrendC_cleanTrendMsg);
        mapErr.put(NetInterfaceConstant.TrendC_deleteComment, NetInterfaceConstant.And_TrendC_deleteComment);
        mapErr.put(NetInterfaceConstant.TrendC_deleteTrend, NetInterfaceConstant.And_TrendC_deleteTrend);
        mapErr.put(NetInterfaceConstant.TrendC_columns, NetInterfaceConstant.And_TrendC_columns);
        mapErr.put(NetInterfaceConstant.TrendC_FocusTrendList, NetInterfaceConstant.And_TrendC_FocusTrendList);
        mapErr.put(NetInterfaceConstant.TrendC_trends, NetInterfaceConstant.And_TrendC_trends);
        mapErr.put(NetInterfaceConstant.TrendC_myTrends, NetInterfaceConstant.And_TrendC_myTrends);
        mapErr.put(NetInterfaceConstant.TrendC_trendComments, NetInterfaceConstant.And_TrendC_trendComments);
        mapErr.put(NetInterfaceConstant.TrendC_trendDetail, NetInterfaceConstant.And_TrendC_trendDetail);
        mapErr.put(NetInterfaceConstant.TrendC_userTrends, NetInterfaceConstant.And_TrendC_userTrends);
        mapErr.put(NetInterfaceConstant.TrendC_commentTrend, NetInterfaceConstant.And_TrendC_commentTrend);
        mapErr.put(NetInterfaceConstant.TrendC_articleList, NetInterfaceConstant.And_TrendC_articleList);
        mapErr.put(NetInterfaceConstant.TrendC_likeArticle, NetInterfaceConstant.And_TrendC_likeArticle);
        mapErr.put(NetInterfaceConstant.TrendC_unFocusVuser, NetInterfaceConstant.And_TrendC_unFocusVuser);
        mapErr.put(NetInterfaceConstant.TrendC_shareArticle, NetInterfaceConstant.And_TrendC_shareArticle);
        mapErr.put(NetInterfaceConstant.TrendC_addReadNum, NetInterfaceConstant.And_TrendC_addReadNum);

        mapErr.put(NetInterfaceConstant.TaskC_getAllFinishSuccesses, NetInterfaceConstant.And_TaskC_getAllFinishSuccesses);
        mapErr.put(NetInterfaceConstant.TaskC_getAllFinishTask, NetInterfaceConstant.And_TaskC_getAllFinishTask);
        mapErr.put(NetInterfaceConstant.TaskC_finishAllSuccesses, NetInterfaceConstant.And_TaskC_finishAllSuccesses);
        mapErr.put(NetInterfaceConstant.TaskC_finishAllTask, NetInterfaceConstant.And_TaskC_finishAllTask);
        mapErr.put(NetInterfaceConstant.TaskC_taskOk, NetInterfaceConstant.And_TaskC_taskOk);

        mapErr.put(NetInterfaceConstant.UserC_userInfo, NetInterfaceConstant.And_UserC_userInfo);
        mapErr.put(NetInterfaceConstant.UserC_qOthersInfoById, NetInterfaceConstant.And_UserC_qOthersInfoById);
        mapErr.put(NetInterfaceConstant.UserC_myInfo, NetInterfaceConstant.And_UserC_myInfo);
        mapErr.put(NetInterfaceConstant.UserC_checkRedList, NetInterfaceConstant.And_UserC_checkRedList);
        mapErr.put(NetInterfaceConstant.UserC_usersRelationship, NetInterfaceConstant.And_UserC_usersRelationship);
        mapErr.put(NetInterfaceConstant.UserC_newBalance, NetInterfaceConstant.And_UserC_newBalance);
        mapErr.put(NetInterfaceConstant.UserC_baiduPush, NetInterfaceConstant.And_UserC_baiduPush);
        mapErr.put(NetInterfaceConstant.UserC_pushFlag, NetInterfaceConstant.And_UserC_pushFlag);
        mapErr.put(NetInterfaceConstant.UserC_modifyHead, NetInterfaceConstant.And_UserC_modifyHead);
        mapErr.put(NetInterfaceConstant.UserC_version_v304, NetInterfaceConstant.And_UserC_version_v304);
        mapErr.put(NetInterfaceConstant.UserC_startup, NetInterfaceConstant.And_UserC_startup);
        mapErr.put(NetInterfaceConstant.UserC_myInCode, NetInterfaceConstant.And_UserC_myInCode);
        mapErr.put(NetInterfaceConstant.UserC_myInvite, NetInterfaceConstant.And_UserC_myInvite);
        mapErr.put(NetInterfaceConstant.UserC_baiduPush, NetInterfaceConstant.And_UserC_baiduPush);
        mapErr.put(NetInterfaceConstant.UserC_imuToUser, NetInterfaceConstant.And_UserC_imuToUser);*/

        return mapErr;
    }

    /**
     * 将普通接口转化为中间件接口，如果不需要转就不要添加到map里面就好了，会返回原来的接口--wb
     * @param errorCode
     * @return
     */
    public static String businessCode(String errorCode)
    {
        String msg = mapBusiness.get(errorCode);
        Logger.t("TransBusinessCode").d(msg + " " + errorCode);
        return null == msg ? errorCode : msg;
    }


}

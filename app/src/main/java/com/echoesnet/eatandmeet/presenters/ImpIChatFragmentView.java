package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IChatFragmentView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.hyphenate.chat.EMMessage;
import com.orhanobut.logger.Logger;

import java.util.Map;

/**
 * @author Administrator
 * @Date 2017/7/27
 * @Version 1.0
 */

public class ImpIChatFragmentView extends BasePresenter<IChatFragmentView>
{
    private static final String TAG = ImpIChatFragmentView.class.getSimpleName();
    private Activity mAct;
    private IChatFragmentView iChatFragmentView;

    public ImpIChatFragmentView(Activity mAct, IChatFragmentView iChatFragmentView)
    {
        this.mAct = mAct;
        this.iChatFragmentView = iChatFragmentView;
    }

    /**
     * 关注
     *
     * @param luId
     * @param operFlag 0取关 ，1 关注
     */
    public void focusPerson(String luId, String operFlag)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.lUId, luId);
        reqParamMap.put(ConstCodeTable.operFlag, operFlag);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("聊天关注返回参数》》" + response.toString());
                if (iChatFragmentView != null)
                    iChatFragmentView.focusCallback(response.getBody());
            }
        }, NetInterfaceConstant.LiveC_focus, null, reqParamMap);
    }

    /**
     * 首次聊天接口
     *
     * @param luId
     * @param message
     */
    public void sendFirstTalk(String luId, final EMMessage message)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.lUId, luId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("首次聊天返回参数》》" + response.toString());
                if (iChatFragmentView != null)
                    iChatFragmentView.firstTalkCallback(response.getBody(), message);
            }
        }, NetInterfaceConstant.FriendC_firstTalk, null, reqParamMap);
    }

    /**
     * 发送打招呼消息
     *
     * @param luId
     */
    public void sendSayHello(String luId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.lUId, luId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("打招呼返回参数》》" + response.toString());
                if (iChatFragmentView != null)
                    iChatFragmentView.sendSayHelloCallback(response.getBody());
            }
        }, NetInterfaceConstant.FriendC_sayHello, null, reqParamMap);
    }

    /**
     * 移除黑名单
     *
     * @param luId the luid
     */
    public void deleteBlack(String luId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.lUId, luId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("聊天关注返回参数》》" + response.toString());
                if (iChatFragmentView != null)
                    iChatFragmentView.deleteBlackCallback(response.getBody());
            }
        }, NetInterfaceConstant.FriendC_delBlack, null, reqParamMap);
    }

    /**
     * 查询聊天对象关系
     *
     * @param lUid
     */
    public void queryUsersRelationShip(String lUid)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.lUId, lUid);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (iChatFragmentView != null)
                    iChatFragmentView.queryUsersRelationShipCallBack(response.getBody());
            }
        }, NetInterfaceConstant.UserC_usersRelationship, null, reqParamMap);
    }

    /**
     * 查询 转发人的 关系
     *
     * @param lUid
     */
    public void queryForwardUsersRelationShip(String lUid)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.lUId, lUid);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (iChatFragmentView != null)
                    iChatFragmentView.queryForwardUsersRelationShipCallBack(response.getBody());
            }
        }, NetInterfaceConstant.UserC_usersRelationship, null, reqParamMap);
    }

    /**
     * 发送游戏邀请
     *
     * @param lUid
     */
    public void sendGameInvite(String lUid, EMMessage message)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.lUId, lUid);
        reqParamMap.put(ConstCodeTable.messageId, message.getMsgId());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (iChatFragmentView != null)
                    iChatFragmentView.sendGameInviteCallBack(response, message);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (iChatFragmentView != null)
                    iChatFragmentView.sendGameInviteErrorCallBack(apiE.getErrorCode(), apiE.getErrBody(), message);
            }
        }, NetInterfaceConstant.Merge10C_sendInvite, reqParamMap);
    }

    /**
     * 接受游戏邀请
     *
     * @param lUid
     */
    public void acceptGameInvite(String lUid, int position, EMMessage message)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.lUId, lUid);
        reqParamMap.put(ConstCodeTable.messageId, message.getMsgId());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (iChatFragmentView != null)
                    iChatFragmentView.acceptGameInviteCallBack(response, position, message);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                if (getView() != null)
                    getView().acceptGameInviteErrorCallBack(apiE.getErrorCode(), position, message);
            }
        }, NetInterfaceConstant.Merge10C_agreeInvite, reqParamMap);
    }

    /**
     * 拒绝游戏邀请
     *
     * @param lUid
     */
    public void refuseGameInvite(String lUid, int position, EMMessage message)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.lUId, lUid);
        reqParamMap.put(ConstCodeTable.messageId, message.getMsgId());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (iChatFragmentView != null)
                    iChatFragmentView.refuseGameInviteCallBack(response, position, message);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                if (getView() != null)
                    getView().refuseGameInviteErrorCallBack(apiE.getErrorCode(), position, message);
            }
        }, NetInterfaceConstant.Merge10C_refuseInvite, reqParamMap);
    }

    /**
     * 查询未接受我邀请的其他人
     */
    public void queryAnotherInvite(String matchId,EMMessage message)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (iChatFragmentView != null)
                    iChatFragmentView.queryAnotherInviteCallBack(response.getBody(), matchId,message);
            }
        }, NetInterfaceConstant.Merge10C_refuseList, null, reqParamMap);
    }

    /**
     * 保存发送成功后的消息id
     */
    public void saveMessageId(String lUid, String messageId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.lUId,lUid);
        reqParamMap.put(ConstCodeTable.messageId,messageId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (iChatFragmentView != null)
                    iChatFragmentView.saveMessageIdCallBack(response);
            }
        }, NetInterfaceConstant.Merge10C_saveMessageId, reqParamMap);
    }

}

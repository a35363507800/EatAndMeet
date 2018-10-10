package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.hyphenate.chat.EMMessage;

/**
 * @author yqh
 * @Date 2017/7/27
 * @Version 1.0
 */

public interface IChatFragmentView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetErrorCallback(String interfaceName, Throwable e);

    void focusCallback(String response);

    void firstTalkCallback(String response, EMMessage message);

    void sendSayHelloCallback(String response);

    void deleteBlackCallback(String response);

    void queryUsersRelationShipCallBack(String response);

    void queryForwardUsersRelationShipCallBack(String response);

    void sendGameInviteCallBack(String response, EMMessage message);

    void sendGameInviteErrorCallBack(String errorCode, String errorMsg, EMMessage message);

    void acceptGameInviteCallBack(String response, int position, EMMessage message);

    void acceptGameInviteErrorCallBack(String errorCode, int position, EMMessage message);

    void refuseGameInviteCallBack(String response, int position, EMMessage message);

    void refuseGameInviteErrorCallBack(String response, int position, EMMessage message);

    void queryAnotherInviteCallBack(String response, String matchId, EMMessage message);

    void saveMessageIdCallBack(String response);


}

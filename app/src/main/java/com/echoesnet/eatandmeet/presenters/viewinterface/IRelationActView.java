package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.hyphenate.chat.EMMessage;

import okhttp3.Call;

/**
 * @author yqh
 * @Date 2017/8/1
 * @Version 1.0
 */

public interface IRelationActView
{
    void requestNetError(Call call, Exception e, String exceptSource);

    void firstTalkCallback(String response, EMMessage message,EaseUser user);

    void queryUsersRelationShipCallBack(String response, EaseUser user);

    void giveCardCallback(String response, EaseUser user);

    void askCardCallback(String response, EaseUser user);

}

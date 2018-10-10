package com.echoesnet.eatandmeet.models.datamodel;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMMessage;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by ben on 2017/5/16.
 */

public abstract class AbstractEMMessageListener implements EMMessageListener
{
    private static final String TAG = AbstractEMMessageListener.class.getSimpleName();

    @Override
    public void onMessageReceived(List<EMMessage> messages)
    {
        Logger.t(TAG).d("onMessageReceived:" + messages.toString());
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages)
    {
        Logger.t(TAG).d("onCmdMessageReceived:" + messages.toString());
    }

    @Override
    public void onMessageRead(List<EMMessage> messages)
    {
        Logger.t(TAG).d("onMessageRead:" + messages.toString());
    }

    @Override
    public void onMessageDelivered(List<EMMessage> messages)
    {
        //此方法 经调查 不会回调 ，
        Logger.t(TAG).d("onMessageDelivered:" + messages.toString());
    }

    @Override
    public void onMessageChanged(EMMessage message, Object change)
    {
        Logger.t(TAG).d("onMessageChanged:" + message.toString());
    }

    @Override
    public void onMessageRecalled(List<EMMessage> messages)
    {
        Logger.t(TAG).d("onMessageRecalled:" + messages.toString());
    }
}

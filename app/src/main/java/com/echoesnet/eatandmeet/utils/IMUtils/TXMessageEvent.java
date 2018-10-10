package com.echoesnet.eatandmeet.utils.IMUtils;

import com.orhanobut.logger.Logger;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;

import java.util.List;
import java.util.Observable;


public class TXMessageEvent extends Observable implements TIMMessageListener
{
    private volatile static TXMessageEvent instance;

    private TXMessageEvent()
    {
    }

    public static TXMessageEvent getInstance()
    {
        if (null == instance)
        {
            synchronized (TXMessageEvent.class)
            {
                if (null == instance)
                {
                    instance = new TXMessageEvent();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean onNewMessages(List<TIMMessage> list)
    {
        Logger.t("TXMessageEvent").d("onNewMessage>>>>>>>>" + list.size());
        setChanged();
        notifyObservers(list);
        return false;
    }

}

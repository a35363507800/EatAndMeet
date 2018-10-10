package com.echoesnet.eatandmeet.models.datamodel;

import com.hyphenate.EMContactListener;
import com.orhanobut.logger.Logger;

/**
 * Created by ben on 2017/5/16.
 */

public abstract class AbstractEMContactListener implements EMContactListener
{
    private static final String TAG=AbstractEMContactListener.class.getSimpleName();
    @Override
    public void onContactAdded(String username)
    {
        Logger.t(TAG).d("被添加");
    }

    @Override
    public void onContactDeleted(String username)
    {
        Logger.t(TAG).d("被删除");
    }

    @Override
    public void onContactInvited(String username, String reason)
    {
        Logger.t(TAG).d(String.format("你被%s邀请了，理由是：%s", username, reason));
    }

    @Override
    public void onFriendRequestAccepted(String username)
    {
        Logger.t(TAG).d("邀请被接受");
    }

    @Override
    public void onFriendRequestDeclined(String username)
    {
        Logger.t(TAG).d("邀请被拒绝");
    }
}

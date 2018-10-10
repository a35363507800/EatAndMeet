package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * The interface Chat activity view.
 *
 * @author yqh
 * @Date 2017 /7/27
 * @Version 1.0
 */
public interface IChatActivityView
{
    /**
     * Request net error.
     *
     * @param call         the call
     * @param e            the e
     * @param exceptSource the except source
     */
    void requestNetError(Call call, Exception e, String exceptSource);

    /**
     * Query users relation ship call back.
     *
     * @param response the response
     */
    void queryUsersRelationShipCallBack(String response);

    /**
     * Pull 2 black call back.
     *
     * @param response the response
     */
    void pull2BlackCallBack(String response);

    /**
     * Check red packet stats callback.
     *
     * @param response the response
     */
    void checkRedPacketStatsCallback(String response,boolean isPull2Black);
}

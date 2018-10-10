package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/16.
 */

public interface IMyInviteFriendsView {
    /**
     * 网络错误回调
     * @param call
     * @param e
     * @param exceptSource
     */
    void requestNetError(Call call, Exception e, String exceptSource);

    /**
     * 获取邀请码回调
     * @param response
     */
    void getInviteCodeCallback(String response);

    /**
     * 获取接受邀请的用户回调
     * @param response
     */
    void getInviteFriendsCallback(String response);
}

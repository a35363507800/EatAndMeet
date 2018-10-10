package com.echoesnet.eatandmeet.presenters.viewinterface;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2016/11/21.
 * @description 用户详情页面接口定义
 */

public interface ICUserInfoPre
{
    void editReMark(final String reMark, final String toAddUid);

    void getUserInfoDetail(String targetUserId, String type);

    void saveContactStatusToServer(final String toAddUserUid, String amount, String streamId, final String payType);

    void applyFriendByHello(String toAddUserUid);

    void checkUserShutUpState(String avRoomId, String userUid);

    void setUserShutUpYes(String avRoomId, String userUid);

    void setUserShutUpNo(String avRoomId, String userUid);

    void checkUserRole(final String avRoomId, final String myUid, final String checkUid);
}

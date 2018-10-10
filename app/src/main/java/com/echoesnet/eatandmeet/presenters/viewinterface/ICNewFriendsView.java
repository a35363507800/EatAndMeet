package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.support.v4.util.ArrayMap;
import android.view.View;

import com.echoesnet.eatandmeet.models.bean.CNewFriendBean;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/20.
 */

public interface ICNewFriendsView
{

    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetErrorCallback(String interfaceName,Throwable e);

    void getAllNewFriendsCallback(String response, String operateType);

    void deletePreFriendCallback(String response, CNewFriendBean friendBean);

    //void saveContactStatusToServerCallback(String response, final View view, final String toAddHxId, final String toAddUserUid);
    void saveContactStatusToServerCallback(String response, final View view, CNewFriendBean newFriendBean);
}

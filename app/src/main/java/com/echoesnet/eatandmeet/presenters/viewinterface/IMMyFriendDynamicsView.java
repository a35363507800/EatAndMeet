package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.MyFriendDynamicsBean;

import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/28.
 */

public interface IMMyFriendDynamicsView {
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void requestNetErrorCallback(String interfaceName,Throwable e);
    void getFriUpdatesCallback(List<MyFriendDynamicsBean> response, String getItemStartIndex);
}

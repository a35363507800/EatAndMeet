package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.LiveEnterRoomBean;

import okhttp3.Call;

/**
 * Created by an on 2016/12/29.
 */

public interface ILHotAnchorVideoActView {
    void requestNetError(Call call, Exception e, String exceptSource);
    void showRankingCallBack(boolean isShowRanking, boolean isShowThisTime, String receive);
    void getRoomInformationCallBack(LiveEnterRoomBean liveEnterRoomBean);
    void getAnchorInformationCallBack(String response);
    void focusSuccess();
    void addWishSuccess();
}

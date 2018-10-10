package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.LChoseConnectMemberBean;

import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/4/1.
 */

public interface IChoseConnectMemberView
{
    void requestNetError(Call call, Exception e, String exceptSource);

    void getMyFansListInLiveRoomCallback(Map<String, Object> map);
}

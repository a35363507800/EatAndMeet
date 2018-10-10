package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.MeetPersonBean;

import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/14.
 */

public interface IDPayOrderSuccessView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetErrorCallback(String interfaceName, Throwable e);

    void getMeetPersonListCallback(List<MeetPersonBean> response);
}

package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.MeetPersonBean;

import java.util.List;

import okhttp3.Call;

/**
 * Created by an on 2016/12/20.
 */

public interface IBeckoningActView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void requestNetErrorCallback(String interfaceName,Throwable e);

    void getAroundPersonCallback(List<MeetPersonBean> str, int currentItem, String operType);

    void loveOrHateCallback(String response, String type);

}

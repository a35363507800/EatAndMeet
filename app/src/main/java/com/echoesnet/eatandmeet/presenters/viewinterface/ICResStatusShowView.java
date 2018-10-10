package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.DinersBean;

import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/23.
 */

public interface ICResStatusShowView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void requestNetErrorCallback(String interfaceName,Throwable e);
    void getDinersInfoCallback(List<DinersBean> response);
}

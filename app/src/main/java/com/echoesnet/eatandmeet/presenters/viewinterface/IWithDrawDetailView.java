package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.models.bean.WithDrawBalanceDetailBean;

import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/11/29.
 */

public interface IWithDrawDetailView
{

    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void requestNetErrorCallback(String interfaceName,Throwable e);
    void getBalanceDetailDataCallBack(List<WithDrawBalanceDetailBean> list, String type);
}

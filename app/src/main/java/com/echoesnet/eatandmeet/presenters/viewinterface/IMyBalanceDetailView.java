package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.models.bean.BalanceDetailBean;

import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/12.
 */

public interface IMyBalanceDetailView {
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void requestNetErrorCallback(String interfaceName,Throwable e);
    void getBalanceDetailDataCallback(List<BalanceDetailBean> resLst, String operateType);
}

package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.CAccostBean;

import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by an on 2016/12/12.
 */

public interface ICSayHelloFrView {
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetErrorCallback(String interfaceName,Throwable e);

    void getAroundPersonCallback(List<CAccostBean> response, List<HashMap<String, Object>> resInfo);

    void getNewAccostToPersonCallback(List<CAccostBean> response, boolean isShowDialog);
}

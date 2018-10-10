package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.MyInfoSystemRemindBean;

import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/28.
 */

public interface IMMySystemInfoRemindView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetErrorCallback(String interfaceName,Exception e);

    void requestNetError(Call call, Exception e, String exceptSource);

    void getSystemMsgCallback(List<MyInfoSystemRemindBean> response, String getItemStartIndex, boolean isPullDown);

    void ignoreBindCallback(String response);

    void bindConsultantCallback(String response);
}

package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.MyInfoOrderRemindBean;
import com.echoesnet.eatandmeet.models.bean.OrderRecordBean;

import java.util.List;

/**
 * Created by an on 2016/12/8 0008.
 */

public interface IInfoOrderRemindView
{
    void getOrderDetailSuccess(String response);
    void initDataSuccess(String response,boolean isDown);
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetErrorCallback(String interfaceName, Throwable e);
}

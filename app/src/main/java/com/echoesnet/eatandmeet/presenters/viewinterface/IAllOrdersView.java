package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.OrderRecordBean;

import java.util.List;

import okhttp3.Call;

/**
 * Created by an on 2016/12/6 0006.
 */

public interface IAllOrdersView
{
    /**
     * 错误回调
     * @param interfaceName
     * @param code
     * @param errBody
     */
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    /**
     * 网络错误
     * @param interfaceName
     * @param e
     */
    void requestNetErrorCallback(String interfaceName,Throwable e);

    /**
     * 求代付按钮开关
     * @param btnOnOff
     */
    void getBtnOnOffSuccess(String btnOnOff);

    /**
     *
     * @param call
     * @param e
     * @param exceptSource
     */
    void getOrderFail(Call call, Exception e, String exceptSource);

    /**
     * 获取所有订单
     * @param response
     * @param operateType
     */
    void getOrderSuccess(List<OrderRecordBean> response, String operateType);
}

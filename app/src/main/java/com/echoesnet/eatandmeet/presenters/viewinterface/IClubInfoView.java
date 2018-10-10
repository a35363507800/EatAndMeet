package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.support.v4.util.ArrayMap;
import android.view.View;

import com.echoesnet.eatandmeet.models.bean.ClubInfoBean;
import com.echoesnet.eatandmeet.models.bean.OrderBean;

/**
 * Created by yqh on 2016/12/27.
 */

public interface IClubInfoView
{
    /**
     * 获取轰趴预订详情信息
     * @param bean
     */
    void getClubInfoDataCallBack(ClubInfoBean bean);

    void callServerErrorCallback(String interfaceName, String code, String errBody);

    /**
     * 实际支付前需向后台提交订单 验证后返回订单ID
     *  @orderId 订单ID
     */
    void postOrderToServer(String orderId);


}

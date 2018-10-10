package com.echoesnet.eatandmeet.presenters.viewinterface;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2018/2/8
 * @description
 */
public interface IClubOrderRecordDetailPre
{
    /**
     * 获得订单详情
     *
     * @param orderId
     */
    void getOrderDetail(String orderId);
    /**
     * 删除订单
     *
     * @param orderId
     */
    void deleteOrder(String orderId);

    /**
     * 获取评价详情
     *
     * @param orderId
     */
    void getResCommentDetail(String orderId );
    /**
     * 申请退款
     *
     * @param orderId 订单号
     */
    void applyRefund(String orderId);

}

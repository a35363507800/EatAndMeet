package com.echoesnet.eatandmeet.presenters.viewinterface;

/**
 * Created by lc on 2017/7/18 20.
 */

public interface IDateInfoPre
{
    /**
     * 发送约会邀请
     *
     * @param luid      对方uid
     * @param startIdex 起始位置
     * @param num       数量
     */
    void getUserAppointment(String luid, String startIdex, String num);


    /**
     * 发送约会邀请
     *
     * @param luId    对方uid
     * @param payType 0余额支付
     * @param amount  金额
     * @param pwd     支付密码
     * @param date    约会日期
     */
    void sendAppointment(String luId, String payType, String amount, String pwd, String date);

    /**
     * 约会校验
     *
     * @param luId 对方uid
     * @param date 约会日期
     * @param type 1多人0单人
     */
    void checkWish(String luId, String date, String type);

    /**
     * 暂未使用
     * luId 对方uid
     */

    void checkReceive(String luId);
}

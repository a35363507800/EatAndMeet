package com.echoesnet.eatandmeet.utils.payUtil;

/**
 * Created by wangben on 2016/6/27.
 * 支付完成后触发
 */
public interface IPayFinishedListener
{
    void PayFinished(String orderId,String streamId,String payType);
    void PayFailed(String orderId,String streamId,String payType);
}

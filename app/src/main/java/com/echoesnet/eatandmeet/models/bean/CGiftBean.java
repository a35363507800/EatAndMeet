package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by wangben on 2016/7/13.
 */
public class CGiftBean
{
    private String payType;
    private String amount;
    private String streamId;

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getPayType()
    {
        return payType;
    }

    public void setPayType(String payType)
    {
        this.payType = payType;
    }

    public String getStreamId()
    {
        return streamId;
    }

    public void setStreamId(String streamId)
    {
        this.streamId = streamId;
    }

    @Override
    public String toString()
    {
        return "CGiftBean{" +
                "amount='" + amount + '\'' +
                ", payType='" + payType + '\'' +
                ", streamId='" + streamId + '\'' +
                '}';
    }
}

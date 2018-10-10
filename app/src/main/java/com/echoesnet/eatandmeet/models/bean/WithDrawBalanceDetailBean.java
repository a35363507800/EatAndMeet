package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/5/28.
 */
public class WithDrawBalanceDetailBean
{

    // 模拟字段
    private String typeTitle;
    private String typeContent;
    private String balanceTitle;
    private String balanceContent;

    // 接口字段
    private String type;  // 交易类型详细 0、充值 1、消费
    private String money; // 当时余额
    private String dealDate;  // 交易日期

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getMoney()
    {
        return money;
    }

    public void setMoney(String money)
    {
        this.money = money;
    }

    public String getDealDate()
    {
        return dealDate;
    }

    public void setDealDate(String dealDate)
    {
        this.dealDate = dealDate;
    }

    @Override
    public String toString()
    {
        return "BalanceDetailBean{" +
                ", type='" + type + '\'' +
                ", money='" + money + '\'' +
                ", dealDate='" + dealDate + '\'' +
                '}';
    }
}

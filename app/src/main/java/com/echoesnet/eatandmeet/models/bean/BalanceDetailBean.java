package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/5/28.
 */
public class BalanceDetailBean {

    // 模拟字段
    private String typeTitle;
    private String typeContent;
    private String balanceTitle;
    private String balanceContent;

    // 接口字段
    private String type;  // 交易类型（0：入账，1：出账）
    private String typeDetail;  // 交易类型详细 0、充值 1、消费 2、退费 3、红包消费 4、红包收入 5、邀请好友奖励 6、注册礼7、闪付  8.手动修改,9.脸蛋充值
    private String dealBalance; // 当时余额
    private String dealMoney; // 交易金额
    private String dealDate;  // 交易日期

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    public String getTypeContent() {
        return typeContent;
    }

    public void setTypeContent(String typeContent) {
        this.typeContent = typeContent;
    }

    public String getBalanceTitle() {
        return balanceTitle;
    }

    public void setBalanceTitle(String balanceTitle) {
        this.balanceTitle = balanceTitle;
    }

    public String getBalanceContent() {
        return balanceContent;
    }

    public void setBalanceContent(String balanceContent) {
        this.balanceContent = balanceContent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeDetail() {
        return typeDetail;
    }

    public void setTypeDetail(String typeDetail) {
        this.typeDetail = typeDetail;
    }

    public String getDealBalance() {
        return dealBalance;
    }

    public void setDealBalance(String dealBalance) {
        this.dealBalance = dealBalance;
    }

    public String getDealMoney() {
        return dealMoney;
    }

    public void setDealMoney(String dealMoney) {
        this.dealMoney = dealMoney;
    }

    public String getDealDate() {
        return dealDate;
    }

    public void setDealDate(String dealDate) {
        this.dealDate = dealDate;
    }

    @Override
    public String toString()
    {
        return "BalanceDetailBean{" +
                "balanceContent='" + balanceContent + '\'' +
                ", typeTitle='" + typeTitle + '\'' +
                ", typeContent='" + typeContent + '\'' +
                ", balanceTitle='" + balanceTitle + '\'' +
                ", type='" + type + '\'' +
                ", typeDetail='" + typeDetail + '\'' +
                ", dealBalance='" + dealBalance + '\'' +
                ", dealMoney='" + dealMoney + '\'' +
                ", dealDate='" + dealDate + '\'' +
                '}';
    }
}

package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/6/23.
 */
public class RechargeBean {

    private String rechargeAmount; // 充值金额
    private String getAmount; // 赠送金额

    public String getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(String rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public String getGetAmount() {
        return getAmount;
    }

    public void setGetAmount(String getAmount) {
        this.getAmount = getAmount;
    }
}

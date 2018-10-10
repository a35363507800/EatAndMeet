package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/7/29.
 */
public class OrderRemindBean {

    private String rName;        // 餐厅名称
    private String remindTime;   // 时间
    private String orderPrice;   // 订单金额
    private String orderCode;    // 消费码
    private String validTime;    // 有效期
    private String overplusTime; // 剩余时间

    public String getrName() {
        return rName;
    }

    public void setrName(String rName) {
        this.rName = rName;
    }

    public String getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(String remindTime) {
        this.remindTime = remindTime;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getValidTime() {
        return validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    public String getOverplusTime() {
        return overplusTime;
    }

    public void setOverplusTime(String overplusTime) {
        this.overplusTime = overplusTime;
    }
}

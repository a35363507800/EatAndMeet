package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/8/17.
 */
public class CheckDishBean {

    private String dishId;
    private String dishPrice;
    private String dishAmount;

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    public String getDishPrice() {
        return dishPrice;
    }

    public void setDishPrice(String dishPrice) {
        this.dishPrice = dishPrice;
    }

    public String getDishAmount() {
        return dishAmount;
    }

    public void setDishAmount(String dishAmount) {
        this.dishAmount = dishAmount;
    }
}

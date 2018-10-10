package com.echoesnet.eatandmeet.models.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/5/9.
 */
public class OrderSearchBean implements Serializable{

    private String dishHUrl;
    private String dishName;
    private int selectNum;
    private String dishPrice;
    private String dishStar;
    private String dishId;

    public String getDishHUrl() {
        return dishHUrl;
    }

    public void setDishHUrl(String dishHUrl) {
        this.dishHUrl = dishHUrl;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public int getSelectNum() {
        return selectNum;
    }

    public void setSelectNum(int selectNum) {
        this.selectNum = selectNum;
    }

    public String getDishPrice() {
        return dishPrice;
    }

    public void setDishPrice(String dishPrice) {
        this.dishPrice = dishPrice;
    }

    public String getDishStar() {
        return dishStar;
    }

    public void setDishStar(String dishStar) {
        this.dishStar = dishStar;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }
}

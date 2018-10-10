package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/6/6.
 * 菜品详情
 */
public class DishDetail {
    // 菜品ID
    private String dishId;
    // 菜品名称
    private String dishName;
    // 单价
    private String dishPrice;
    // 菜品评星
    private String dishStar;
    // 菜品介绍
    private String dishMemo;
    // 菜品图片地址（array）
    private String dishUrls;
    // 菜品视频地址（可无）
    private String dishVUrl;

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
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

    public String getDishMemo() {
        return dishMemo;
    }

    public void setDishMemo(String dishMemo) {
        this.dishMemo = dishMemo;
    }

    public String getDishUrls() {
        return dishUrls;
    }

    public void setDishUrls(String dishUrls) {
        this.dishUrls = dishUrls;
    }

    public String getDishVUrl() {
        return dishVUrl;
    }

    public void setDishVUrl(String dishVUrl) {
        this.dishVUrl = dishVUrl;
    }
}

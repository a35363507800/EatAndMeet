package com.echoesnet.eatandmeet.models.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/3.
 */
public class DishRightMenuItemBean implements Serializable {

    private int selectNum; // 选择数量
    private String dishName;  // 名称
    private String price;  // 价钱
    private int starNum;   // 评价数量
    private String urlBitmap;
    private String dishId;

    public int getSelectNum() {
        return selectNum;
    }

    public void setSelectNum(int selectNum) {
        this.selectNum = selectNum;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getUrlBitmap() {
        return urlBitmap;
    }

    public void setUrlBitmap(String urlBitmap) {
        this.urlBitmap = urlBitmap;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getStarNum() {
        return starNum;
    }

    public void setStarNum(int starNum) {
        this.starNum = starNum;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    private String dishClass;
    private String dishHUrl;
    private String dishPrice;
    private String dishStar;
    private String rId;

    public String getDishClass() {
        return dishClass;
    }

    public void setDishClass(String dishClass) {
        this.dishClass = dishClass;
    }

    public String getDishHUrl() {
        return dishHUrl;
    }

    public void setDishHUrl(String dishHUrl) {
        this.dishHUrl = dishHUrl;
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

    public String getrId() {
        return rId;
    }

    public void setrId(String rId) {
        this.rId = rId;
    }

    @Override
    public int hashCode() {
        String in = dishId + dishClass;
        return in.hashCode();
    }
}

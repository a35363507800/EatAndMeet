package com.echoesnet.eatandmeet.models.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/3.
 */
public class OrderedDishItemBean implements Serializable {




    private int starNum;   // 评价数量
    private String urlBitmap;

    private String dishId;
    private String dishName;  // 名称
    private int dishNum; // 选择数量
    private String price;  // 价钱

    private String dishClass;
    private String dishHUrl;
    private String dishPrice;
    private String dishStar;
    private String rId;

    public int getDishNum() {
        return dishNum;
    }

    public void setDishNum(int dishNum) {
        this.dishNum = dishNum;
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

    @Override
    public String toString() {
        return "OrderedDishItemBean{" +
                "starNum=" + starNum +
                ", urlBitmap='" + urlBitmap + '\'' +
                ", dishId='" + dishId + '\'' +
                ", dishName='" + dishName + '\'' +
                ", dishNum=" + dishNum +
                ", price='" + price + '\'' +
                ", dishClass='" + dishClass + '\'' +
                ", dishHUrl='" + dishHUrl + '\'' +
                ", dishPrice='" + dishPrice + '\'' +
                ", dishStar='" + dishStar + '\'' +
                ", rId='" + rId + '\'' +
                '}';
    }
}

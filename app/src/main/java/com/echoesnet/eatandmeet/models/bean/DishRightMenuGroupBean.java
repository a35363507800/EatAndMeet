package com.echoesnet.eatandmeet.models.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/3.
 */
public class DishRightMenuGroupBean implements Serializable {

    private String headerTitle;
    private ArrayList<OrderedDishItemBean> list;
    private boolean isSelect = false;
    private int selectNum;
    private String dishClass;
    private String dishHUrl;
    private String dishId;
    private String dishName;
    private String dishPrice;
    private String dishStar;
    private String rId;


    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public ArrayList<OrderedDishItemBean> getList() {
        return list;
    }

    public void setList(ArrayList<OrderedDishItemBean> list) {
        this.list = list;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getSelectNum() {
        return selectNum;
    }

    public void setSelectNum(int selectNum) {
        this.selectNum = selectNum;
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
        return "DishRightMenuGroupBean{" +
                "list=" + list +
                '}';
    }
}

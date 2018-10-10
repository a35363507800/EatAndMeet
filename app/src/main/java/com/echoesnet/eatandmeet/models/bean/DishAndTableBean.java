package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/6/6.
 */
public class DishAndTableBean {

    // 餐厅id
    private String rId;
    // 菜品id
    private String dishId;
    // 菜品名称
    private String dishName;
    // 菜品分类
    private String dishClass;
    // 单价
    private String dishPrice;
    // 菜品图片地址
    private String dUrls;
    // 菜品星级
    private String dishStar;
    // 点菜类型
    private String dishType;
    // 订桌类型
    private String tableType;
    //


    public String getDishStar() {
        return dishStar;
    }

    public void setDishStar(String dishStar) {
        this.dishStar = dishStar;
    }

    public String getdUrls() {
        return dUrls;
    }

    public void setdUrls(String dUrls) {
        this.dUrls = dUrls;
    }

    public String getDishPrice() {
        return dishPrice;
    }

    public void setDishPrice(String dishPrice) {
        this.dishPrice = dishPrice;
    }

    public String getDishClass() {
        return dishClass;
    }

    public void setDishClass(String dishClass) {
        this.dishClass = dishClass;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    public String getrId() {
        return rId;
    }

    public void setrId(String rId) {
        this.rId = rId;
    }

    public String getDishType() {
        return dishType;
    }

    public void setDishType(String dishType) {
        this.dishType = dishType;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }
}

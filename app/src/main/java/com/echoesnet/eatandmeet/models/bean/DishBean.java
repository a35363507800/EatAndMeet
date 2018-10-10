package com.echoesnet.eatandmeet.models.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/6.
 */
public class DishBean implements Serializable
{
    //菜品id
    private String dishId;
    //菜品名
    private String dishName;
    //菜品数量
    private String dishAmount;
    //菜品单价
    private String dishPrice;
    // 菜品首图(缩略图Url)
    private String dishHUrl;
    // 菜品图片
    private String dishUrls;/*add by llj 2016-06-04*/
    // 菜品分类
    private String dishClass;
    // 菜品评星
    private String dishStar="0";
    // 菜品介绍Url
    private String dishMemo;
    // 菜品视频
    private String dishVUrl;
    // 餐厅ID
    private String rId;
    // 菜品总价
    private String dishAll;
    // 须知Url
    private String rule;
    // 菜品描述
    private String signInfo;

    public String getDishAll()
    {
        return dishAll;
    }

    public void setDishAll(String dishAll)
    {
        this.dishAll = dishAll;
    }

    public String getDishAmount()
    {
        return dishAmount;
    }

    public void setDishAmount(String dishAmount)
    {
        this.dishAmount = dishAmount;
    }

    public String getDishClass()
    {
        return dishClass;
    }

    public void setDishClass(String dishClass)
    {
        this.dishClass = dishClass;
    }

    public String getDishHUrl()
    {
        return dishHUrl;
    }

    public void setDishHUrl(String dishHUrl)
    {
        this.dishHUrl = dishHUrl;
    }

    public String getDishId()
    {
        return dishId;
    }

    public void setDishId(String dishId)
    {
        this.dishId = dishId;
    }

    public String getDishMemo()
    {
        return dishMemo;
    }

    public void setDishMemo(String dishMemo)
    {
        this.dishMemo = dishMemo;
    }

    public String getDishName()
    {
        return dishName;
    }

    public void setDishName(String dishName)
    {
        this.dishName = dishName;
    }

    public String getDishPrice()
    {
        return dishPrice;
    }

    public void setDishPrice(String dishPrice)
    {
        this.dishPrice = dishPrice;
    }

    public String getDishStar()
    {
        return dishStar;
    }

    public void setDishStar(String dishStar)
    {
        this.dishStar = dishStar;
    }

    public String getDishUrls()
    {
        return dishUrls;
    }

    public void setDishUrls(String dishUrls)
    {
        this.dishUrls = dishUrls;
    }

    public String getDishVUrl()
    {
        return dishVUrl;
    }

    public void setDishVUrl(String dishVUrl)
    {
        this.dishVUrl = dishVUrl;
    }

    public String getrId()
    {
        return rId;
    }

    public void setrId(String rId)
    {
        this.rId = rId;
    }

    public String getRule()
    {
        return rule;
    }

    public void setRule(String rule)
    {
        this.rule = rule;
    }

    public String getSignInfo()
    {
        return signInfo;
    }

    public void setSignInfo(String signInfo)
    {
        this.signInfo = signInfo;
    }

    @Override
    public String toString()
    {
        return "DishBean{" +
                "dishAll='" + dishAll + '\'' +
                ", dishId='" + dishId + '\'' +
                ", dishName='" + dishName + '\'' +
                ", dishAmount='" + dishAmount + '\'' +
                ", dishPrice='" + dishPrice + '\'' +
                ", dishHUrl='" + dishHUrl + '\'' +
                ", dishUrls='" + dishUrls + '\'' +
                ", dishClass='" + dishClass + '\'' +
                ", dishStar='" + dishStar + '\'' +
                ", dishMemo='" + dishMemo + '\'' +
                ", dishVUrl='" + dishVUrl + '\'' +
                ", rId='" + rId + '\'' +
                ", rule='" + rule + '\'' +
                ", signInfo='" + signInfo + '\'' +
                '}';
    }
}

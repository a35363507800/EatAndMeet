package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/6/14.
 */
public class DishDetailBean
{
    // 两次修改

    private String context; //
    private String hurl;
    private String urls;
    private String vurls;
    private String eval;
    private String dishId; // 菜品id
    private String dishName; // 菜品名称
    private String dishPrice; // 菜品单价
    private String dishClass; // 菜品分类
    private String dishHUrl;  // 菜品图片地址
    private String dishMemo; // 菜品简介
    private String dishStar; // 菜品星级


    public String getDishId()
    {
        return dishId;
    }

    public void setDishId(String dishId)
    {
        this.dishId = dishId;
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

    public String getContext()
    {
        return context;
    }

    public void setContext(String context)
    {
        this.context = context;
    }

    public String getHurl()
    {
        return hurl;
    }

    public void setHurl(String hurl)
    {
        this.hurl = hurl;
    }

    public String getUrls()
    {
        return urls;
    }

    public void setUrls(String urls)
    {
        this.urls = urls;
    }

    public String getVurls()
    {
        return vurls;
    }

    public void setVurls(String vurls)
    {
        this.vurls = vurls;
    }

    public String getEval()
    {
        return eval;
    }

    public void setEval(String eval)
    {
        this.eval = eval;
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

    public String getDishMemo()
    {
        return dishMemo;
    }

    public void setDishMemo(String dishMemo)
    {
        this.dishMemo = dishMemo;
    }

    public String getDishStar()
    {
        return dishStar;
    }

    public void setDishStar(String dishStar)
    {
        this.dishStar = dishStar;
    }

    @Override
    public String toString()
    {
        return "DishDetailBean{" +
                "context='" + context + '\'' +
                ", hurl='" + hurl + '\'' +
                ", urls='" + urls + '\'' +
                ", vurls='" + vurls + '\'' +
                ", eval='" + eval + '\'' +
                ", dishId='" + dishId + '\'' +
                ", dishName='" + dishName + '\'' +
                ", dishPrice='" + dishPrice + '\'' +
                ", dishClass='" + dishClass + '\'' +
                ", dishHUrl='" + dishHUrl + '\'' +
                ", dishMemo='" + dishMemo + '\'' +
                ", dishStar='" + dishStar + '\'' +
                '}';
    }
}

package com.echoesnet.eatandmeet.models.bean;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2018/2/6
 * @description
 */
public class ClubListBean
{
    /**
     * price : 价格
     * id : 轰趴id
     * url : 展示图
     * name : 轰趴名
     * perPrice : 人均
     * posx : 纬度
     * posy : 经度
     */
    private String price;
    private String id;
    private String url;
    private String name;
    private String perPrice;
    private String posx;
    private String posy;

    public String getPosx()
    {
        return posx;
    }

    public void setPosx(String posx)
    {
        this.posx = posx;
    }

    public String getPosy()
    {
        return posy;
    }

    public void setPosy(String posy)
    {
        this.posy = posy;
    }

    public String getPrice()
    {
        return price;
    }

    public void setPrice(String price)
    {
        this.price = price;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPerPrice()
    {
        return perPrice;
    }

    public void setPerPrice(String perPrice)
    {
        this.perPrice = perPrice;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClubListBean that = (ClubListBean) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return perPrice != null ? perPrice.equals(that.perPrice) : that.perPrice == null;
    }


}

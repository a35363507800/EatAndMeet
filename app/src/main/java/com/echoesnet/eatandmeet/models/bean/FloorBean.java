package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by wangben on 2016-6-9.
 */
public class FloorBean
{
    //里面包含了层数信息
    private String layoutId;
    private String width;
    private String height;
    private String imgUrl;

    public FloorBean()
    {
    }

    public FloorBean(String layoutId, String width, String height, String imgUrl)
    {
        this.layoutId = layoutId;
        this.width = width;
        this.height = height;
        this.imgUrl = imgUrl;
    }

    public String getLayoutId()
    {
        return layoutId;
    }

    public void setLayoutId(String layoutId)
    {
        this.layoutId = layoutId;
    }

    public String getWidth()
    {
        return width;
    }

    public void setWidth(String width)
    {
        this.width = width;
    }

    public String getHeight()
    {
        return height;
    }

    public void setHeight(String height)
    {
        this.height = height;
    }

    public String getImgUrl()
    {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl)
    {
        this.imgUrl = imgUrl;
    }
}

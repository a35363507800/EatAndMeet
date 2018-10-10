package com.echoesnet.eatandmeet.models.bean;

import java.io.Serializable;

/**
 * Created by wangben on 2016/8/27.
 */
public class FPromotionBean implements Serializable
{
    //轮播图图片
    private String imgUrl;
    //1:活动0：非活动
    private String isActivity;
    private String webUrl;
    //1：充值2：分享3：餐厅列表
    private String type;
    //活动名称
    private String actName;
    private String activityId;

    public String getImgUrl()
    {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl)
    {
        this.imgUrl = imgUrl;
    }

    public String getIsActivity()
    {
        return isActivity;
    }

    public void setIsActivity(String isActivity)
    {
        this.isActivity = isActivity;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getWebUrl()
    {
        return webUrl;
    }

    public void setWebUrl(String webUrl)
    {
        this.webUrl = webUrl;
    }

    public String getActivityId()
    {
        return activityId;
    }

    public void setActivityId(String activityId)
    {
        this.activityId = activityId;
    }

    public String getActName()
    {
        return actName;
    }

    public void setActName(String actName)
    {
        this.actName = actName;
    }

    @Override
    public String toString()
    {
        return "FPromotionBean{" +
                "activityId='" + activityId + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", isActivity='" + isActivity + '\'' +
                ", webUrl='" + webUrl + '\'' +
                ", type='" + type + '\'' +
                ", actName='" + actName + '\'' +
                '}';
    }
}

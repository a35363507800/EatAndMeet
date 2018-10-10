package com.echoesnet.eatandmeet.models.bean;

import java.io.Serializable;

/**
 * Created by wangben on 2016/7/5.
 */
public class CAccostBean implements Serializable
{
    /* 餐厅Id */
    private String rId;
    private String resName="";
    private String floorNum="1";
    /* 餐厅就餐人数量 */
    private String orderNum="0";
    private Double resLatitude;
    private Double resLongitud;

    //纬度
    private String posx ="39";
    //经度
    private String posy ="117";
    //选定的就餐代表
    private UsersBean userBean;

    public String getOrderNum()
    {
        return orderNum;
    }

    public void setOrderNum(String orderNum)
    {
        this.orderNum = orderNum;
    }

    public String getrId()
    {
        return rId;
    }

    public void setrId(String rId)
    {
        this.rId = rId;
    }

    public UsersBean getUserBean()
    {
        return userBean;
    }

    public void setUserBean(UsersBean userBean)
    {
        this.userBean = userBean;
    }

    public Double getResLatitude()
    {
        return resLatitude;
    }

    public void setResLatitude(Double resLatitude)
    {
        this.resLatitude = resLatitude;
    }

    public Double getResLongitud()
    {
        return resLongitud;
    }

    public void setResLongitud(Double resLongitud)
    {
        this.resLongitud = resLongitud;
    }

    public String getResName()
    {
        return resName;
    }

    public void setResName(String resName)
    {
        this.resName = resName;
    }

    public String getFloorNum()
    {
        return floorNum;
    }

    public void setFloorNum(String floorNum)
    {
        this.floorNum = floorNum;
    }

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

    @Override
    public String toString()
    {
        return "CAccostBean{" +
                "floorNum='" + floorNum + '\'' +
                ", rId='" + rId + '\'' +
                ", resName='" + resName + '\'' +
                ", orderNum='" + orderNum + '\'' +
                ", resLatitude=" + resLatitude +
                ", resLongitud=" + resLongitud +
                ", posx='" + posx + '\'' +
                ", posy='" + posy + '\'' +
                ", userBean=" + userBean +
                '}';
    }
}

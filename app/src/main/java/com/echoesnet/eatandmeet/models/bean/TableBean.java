package com.echoesnet.eatandmeet.models.bean;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2016/4/20.
 */
public class TableBean
{
    private String restaurantId;
    private String tableId;
    private String tableName;
    //图片左上角x坐标
    private String x;
    private String y;
    private String imgUrl;
    private Bitmap bitImg;
    private String w;
    private String h;
    private String angle;
    //0:可选；1：选中；2：不可选；
    private String status="0";
    //桌子的类型2人桌，或者4人桌
    private String tableType;
    //类型，是桌子还是装饰物
    private String type;
    private String picName;
    private String floorNumber;

    public String getTableType()
    {
        return tableType;
    }

    public void setTableType(String tableType)
    {
        this.tableType = tableType;
    }

    public float getWidth2()
    {
        return Float.parseFloat(getW());
    }


    public float getHeight2()
    {
        return  Float.parseFloat(getH());
    }


    public float getAngle2()
    {
        return Float.parseFloat(getAngle());
    }

    public Bitmap getBitImg()
    {
        return bitImg;
    }

    public void setBitImg(Bitmap bitImg)
    {
        this.bitImg = bitImg;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getTableId()
    {
        return tableId;
    }

    public void setTableId(String tableId)
    {
        this.tableId = tableId;
    }

    public String getImgUrl()
    {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl)
    {
        this.imgUrl = imgUrl;
    }

    public float getX2()
    {
        return Float.parseFloat(getX());
    }
    public float getY2()
    {
        return Float.parseFloat(getY());
    }
    public String getRestaurantId()
    {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId)
    {
        this.restaurantId = restaurantId;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getPicName()
    {
        return picName;
    }

    public void setPicName(String picName)
    {
        this.picName = picName;
    }

    public String getAngle()
    {
        return angle;
    }

    public void setAngle(String angle)
    {
        this.angle = angle;
    }

    public String getH()
    {
        return h;
    }

    public void setH(String h)
    {
        this.h = h;
    }

    public String getW()
    {
        return w;
    }

    public void setW(String w)
    {
        this.w = w;
    }

    public String getX()
    {
        return x;
    }

    public void setX(String x)
    {
        this.x = x;
    }

    public String getY()
    {
        return y;
    }

    public void setY(String y)
    {
        this.y = y;
    }

    public String getFloorNumber()
    {
        return floorNumber;
    }

    public void setFloorNumber(String floorNumber)
    {
        this.floorNumber = floorNumber;
    }

    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    @Override
    public String toString()
    {
        return "TableBean{" +
                "angle='" + angle + '\'' +
                ", restaurantId='" + restaurantId + '\'' +
                ", tableId='" + tableId + '\'' +
                ", tableName='" + tableName + '\'' +
                ", x='" + x + '\'' +
                ", y='" + y + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", bitImg=" + bitImg +
                ", w='" + w + '\'' +
                ", h='" + h + '\'' +
                ", status='" + status + '\'' +
                ", tableType='" + tableType + '\'' +
                ", type='" + type + '\'' +
                ", picName='" + picName + '\'' +
                ", floorNumber='" + floorNumber + '\'' +
                '}';
    }
}

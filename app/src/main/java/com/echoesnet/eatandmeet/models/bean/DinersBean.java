package com.echoesnet.eatandmeet.models.bean;

import android.graphics.Bitmap;

import com.echoesnet.eatandmeet.utils.CommonUtils;

import java.util.List;

/**
 * Created by wangben on 2016/7/6.
 */
public class DinersBean
{
    private String resId;
    private String tableId;
    private String tableName;
    private String uId;
    //图片左上角x坐标
    private String x="0";
    private String y="0";
    /*********************************
     x用餐人x坐标
     y用餐人y坐标
     x^ 餐桌x坐标
     y^ 餐桌y坐标
     w  餐桌宽
     h  餐桌高
     w^ 用餐人图片宽
     h^ 用餐人头像高（用餐人图片高-下面部分）
     x^=x+(w-w^)/2
     y^=y+(h-h^)/2
     **********************************/
    //最终要绘制到桌子上的图片
    private Bitmap bitImg;
    private String w="100";
    private String h="100";
    private String angle="0";
    private String chatting ="0";
    private String status ="0";//是否正在直播1：直播0：未直播
    //下面小头像
    private String lUphUrl;
    //头像
    private String uphUrl;
    //是否隐身，0不隐身;1:隐身
    private String privateFlag="0";

    private String roomId;

    public String getY()
    {
        return y;
    }

    public void setY(String y)
    {
        this.y = y;
    }

    public String getAngle()
    {
        return angle;
    }

    public void setAngle(String angle)
    {
        this.angle = angle;
    }

    public Bitmap getBitImg()
    {
        return bitImg;
    }

    public void setBitImg(Bitmap bitImg)
    {
        this.bitImg = bitImg;
    }

    public String getH()
    {
        return h;
    }

    public void setH(String h)
    {
        this.h = h;
    }

    public String getResId()
    {
        return resId;
    }

    public void setResId(String resId)
    {
        this.resId = resId;
    }

    public String getTableId()
    {
        return tableId;
    }

    public void setTableId(String tableId)
    {
        this.tableId = tableId;
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
    public float getWidth2()
    {
        return  Float.parseFloat(getW());
    }

    public float getHeight2()
    {
        return  Float.parseFloat(getH());
    }

    public float getAngle2()
    {
        return Float.parseFloat(getAngle());
    }
    public float getX2()
    {
        return Float.parseFloat(getX());
    }
    public float getY2()
    {
        return Float.parseFloat(getY());
    }

    public String getChatting()
    {
        return chatting;
    }

    public void setChatting(String chatting)
    {
        this.chatting = chatting;
    }

    public List<String> getImgUrls()
    {
        return CommonUtils.strWithSeparatorToList(getlUphUrl(),CommonUtils.SEPARATOR);
    }

    public String getlUphUrl()
    {
        return lUphUrl;
    }

    public void setlUphUrl(String lUphUrl)
    {
        this.lUphUrl = lUphUrl;
    }

    public String getuId()
    {
        return uId;
    }

    public void setuId(String uId)
    {
        this.uId = uId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getUphUrl()
    {
        return uphUrl;
    }

    public void setUphUrl(String uphUrl)
    {
        this.uphUrl = uphUrl;
    }

    public String getPrivateFlag()
    {
        return privateFlag;
    }

    public void setPrivateFlag(String privateFlag)
    {
        this.privateFlag = privateFlag;
    }

    public String getRoomId()
    {
        return roomId;
    }

    public void setRoomId(String roomId)
    {
        this.roomId = roomId;
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
        return "DinersBean{" +
                "resId='" + resId + '\'' +
                ", tableId='" + tableId + '\'' +
                ", uId='" + uId + '\'' +
                ", x='" + x + '\'' +
                ", y='" + y + '\'' +
                ", bitImg=" + bitImg +
                ", w='" + w + '\'' +
                ", h='" + h + '\'' +
                ", angle='" + angle + '\'' +
                ", chatting='" + chatting + '\'' +
                ", status='" + status + '\'' +
                ", lUphUrl='" + lUphUrl + '\'' +
                ", uphUrl='" + uphUrl + '\'' +
                ", privateFlag='" + privateFlag + '\'' +
                ", roomId='" + roomId + '\'' +
                '}';
    }
}

package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by wangben on 2016/10/9.
 * 支付时传入的元数据
 */

public class PayMetadataBean
{
    /* 来源0：订单，1：充值:2：红包，3：见面礼,4:脸蛋充值*/
    private String source;
    /* 充值赠送金额*/
    private String getAmount;
    /* 充值类型，普通充值：0，活动充值：活动id*/
    private String rechargeType;
    /* 对方luid，source为2,3时必传*/
    private String luId;
    /* 脸蛋数量, source为4时必传*/
    private String faceEgg;
    /* 约会日期*/
    private String date;
    /* 直播间Id */
    private String roomId;
    /* 红包个数 */
    private String num;
    public PayMetadataBean()
    {
    }

    public PayMetadataBean(String getAmount, String luId, String rechargeType, String source)
    {
        this.getAmount = getAmount;
        this.luId = luId;
        this.rechargeType = rechargeType;
        this.source = source;
    }
    public PayMetadataBean(String getAmount, String luId, String rechargeType, String source,String faceEgg)
    {
        this.getAmount = getAmount;
        this.luId = luId;
        this.rechargeType = rechargeType;
        this.source = source;
        this.faceEgg=faceEgg;
    }

    public PayMetadataBean(String getAmount, String luId, String rechargeType, String source,String faceEgg,String date)
    {
        this.getAmount = getAmount;
        this.luId = luId;
        this.rechargeType = rechargeType;
        this.source = source;
        this.faceEgg=faceEgg;
        this.date=date;
    }

    public PayMetadataBean(String getAmount, String luId, String rechargeType, String source,String num,String date, String roomId)
    {
        this.getAmount = getAmount;
        this.luId = luId;
        this.rechargeType = rechargeType;
        this.source = source;
        this.num = num;
        this.date=date;
        this.roomId = roomId;
    }

    public String getGetAmount()
    {

        return getAmount;
    }

    public void setGetAmount(String getAmount)
    {
        this.getAmount = getAmount;
    }

    public String getLuId()
    {
        return luId;
    }

    public void setLuId(String luId)
    {
        this.luId = luId;
    }

    public String getRechargeType()
    {
        return rechargeType;
    }

    public void setRechargeType(String rechargeType)
    {
        this.rechargeType = rechargeType;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "PayMetadataBean{" +
                "source='" + source + '\'' +
                ", getAmount='" + getAmount + '\'' +
                ", rechargeType='" + rechargeType + '\'' +
                ", luId='" + luId + '\'' +
                ", faceEgg='" + faceEgg + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}

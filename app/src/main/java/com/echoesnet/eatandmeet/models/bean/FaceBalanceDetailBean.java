package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/5/28.
 */
public class FaceBalanceDetailBean
{

    // 模拟字段
    private String typeTitle;
    private String typeContent;
    private String balanceTitle;
    private String balanceContent;

    // 接口字段
    private String inType;// 交易类型详细 0、充值 1、消费
    private String typeDetail; //交易详细
    private String face; // 交易金额
    private String faceEgg; // 当时余额
    private String dealDate;  // 交易日期

    public String getTypeDetail()
    {
        return typeDetail;
    }

    public void setTypeDetail(String typeDetail)
    {
        this.typeDetail = typeDetail;
    }

    public String getFace()
    {
        return face;
    }

    public void setFace(String face)
    {
        this.face = face;
    }

    public String getFaceEgg()
    {
        return faceEgg;
    }

    public void setFaceEgg(String faceEgg)
    {
        this.faceEgg = faceEgg;
    }

    public String getDealDate()
    {
        return dealDate;
    }

    public void setDealDate(String dealDate)
    {
        this.dealDate = dealDate;
    }

    public String getInType()
    {
        return inType;
    }

    public void setInType(String inType)
    {
        this.inType = inType;
    }

    @Override
    public String toString()
    {
        return "FaceBalanceDetailBean{" +
                "typeTitle='" + typeTitle + '\'' +
                ", typeContent='" + typeContent + '\'' +
                ", balanceTitle='" + balanceTitle + '\'' +
                ", balanceContent='" + balanceContent + '\'' +
                ", inType='" + inType + '\'' +
                ", typeDetail='" + typeDetail + '\'' +
                ", face='" + face + '\'' +
                ", faceEgg='" + faceEgg + '\'' +
                ", dealDate='" + dealDate + '\'' +
                '}';
    }
}

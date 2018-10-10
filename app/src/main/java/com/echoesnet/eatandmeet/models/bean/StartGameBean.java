package com.echoesnet.eatandmeet.models.bean;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/11/15 0015
 * @description
 */
public class StartGameBean
{

    /**
     * admissionFee : 入场费
     * total : 奖池累计
     * num : 参与人数
     * faceEgg : 脸蛋余额
     */

    private String admissionFee;
    private String total;
    private String num;
    private String faceEgg;
    private String left;
    private String sponsor;
    private String agree;

    public String getLeft()
    {
        return left;
    }

    public void setLeft(String left)
    {
        this.left = left;
    }

    public String getSponsor()
    {
        return sponsor;
    }

    public void setSponsor(String sponsor)
    {
        this.sponsor = sponsor;
    }

    public String getAgree()
    {
        return agree;
    }

    public void setAgree(String agree)
    {
        this.agree = agree;
    }

    public String getAdmissionFee()
    {
        return admissionFee;
    }

    public void setAdmissionFee(String admissionFee)
    {
        this.admissionFee = admissionFee;
    }

    public String getTotal()
    {
        return total;
    }

    public void setTotal(String total)
    {
        this.total = total;
    }

    public String getNum()
    {
        return num;
    }

    public void setNum(String num)
    {
        this.num = num;
    }

    public String getFaceEgg()
    {
        return faceEgg;
    }

    public void setFaceEgg(String faceEgg)
    {
        this.faceEgg = faceEgg;
    }

    @Override
    public String toString()
    {
        return "StartGameBean{" +
                "admissionFee='" + admissionFee + '\'' +
                ", total='" + total + '\'' +
                ", num='" + num + '\'' +
                ", faceEgg='" + faceEgg + '\'' +
                '}';
    }
}

package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by wangben on 2016/7/8.
 */
public class CPhoneContactUserBean
{
    private String uId;
    private String mobile;
    private String nicName;
    private String uphUrl;
    private String stat;
    private String imuId;
    private String contactName;
    private String remark;
    private String level;
    private String sex;
    private String age;
    private String focus;
    private String signature;

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getImuId()
    {
        return imuId;
    }

    public void setImuId(String imuId)
    {
        this.imuId = imuId;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getNicName()
    {
        return nicName;
    }

    public void setNicName(String nicName)
    {
        this.nicName = nicName;
    }

    public String getStat()
    {
        return stat;
    }

    public void setStat(String stat)
    {
        this.stat = stat;
    }

    public String getuId()
    {
        return uId;
    }

    public void setuId(String uId)
    {
        this.uId = uId;
    }

    public String getUphUrl()
    {
        return uphUrl;
    }

    public void setUphUrl(String uphUrl)
    {
        this.uphUrl = uphUrl;
    }

    public String getContactName()
    {
        return contactName;
    }

    public void setContactName(String contactName)
    {
        this.contactName = contactName;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getFocus()
    {
        return focus;
    }

    public void setFocus(String focus)
    {
        this.focus = focus;
    }

    public String getSignature()
    {
        return signature;
    }

    public void setSignature(String signature)
    {
        this.signature = signature;
    }

    @Override
    public String toString()
    {
        return "CPhoneContactUserBean{" +
                "uId='" + uId + '\'' +
                ", mobile='" + mobile + '\'' +
                ", nicName='" + nicName + '\'' +
                ", uphUrl='" + uphUrl + '\'' +
                ", stat='" + stat + '\'' +
                ", imuId='" + imuId + '\'' +
                ", contactName='" + contactName + '\'' +
                ", remark='" + remark + '\'' +
                ", level='" + level + '\'' +
                ", sex='" + sex + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}

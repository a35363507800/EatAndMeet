package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by wangben on 2016/7/7.
 */
public class CNewFriendBean
{
    private String uId;
    private String uphUrl;
    private String nicName;
    private String description;
    private String stat;
    private String imuId;
    private String remark;
    private String level;
    private String sex;
    private String age;
    //礼物描述
    private CGiftBean welgiftBean;

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
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

    public String getImuId()
    {
        return imuId;
    }

    public void setImuId(String imuId)
    {
        this.imuId = imuId;
    }

    public CGiftBean getWelgiftBean()
    {
        return welgiftBean;
    }

    public void setWelgiftBean(CGiftBean welgiftBean)
    {
        this.welgiftBean = welgiftBean;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    @Override
    public String toString()
    {
        return "CNewFriendBean{" +
                "uId='" + uId + '\'' +
                ", uphUrl='" + uphUrl + '\'' +
                ", nicName='" + nicName + '\'' +
                ", description='" + description + '\'' +
                ", stat='" + stat + '\'' +
                ", imuId='" + imuId + '\'' +
                ", remark='" + remark + '\'' +
                ", level='" + level + '\'' +
                ", sex='" + sex + '\'' +
                ", age='" + age + '\'' +
                ", welgiftBean=" + welgiftBean +
                '}';
    }

}

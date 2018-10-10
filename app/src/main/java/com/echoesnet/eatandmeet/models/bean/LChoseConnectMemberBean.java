package com.echoesnet.eatandmeet.models.bean;

import java.io.Serializable;

/**
 * Created by ben on 2017/3/31.
 */

public class LChoseConnectMemberBean implements Serializable
{

    private boolean isChoose = false;
    private String uId;
    private String id;
    private String imuId;
    private String phUrl;
    private String nicName;
    private String sex;
    private String level;
    private String age;
    private String isVuser;

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public boolean isChoose()
    {
        return isChoose;
    }

    public void setChoose(boolean choose)
    {
        isChoose = choose;
    }

    public String getuId()
    {
        return uId;
    }

    public void setuId(String uId)
    {
        this.uId = uId;
    }

    public String getId()
    {
        return id.replace("u", "");
    }

    public void setId(String id)
    {
        this.id = id.replace("u", "");
    }

    public String getPhUrl()
    {
        return phUrl;
    }

    public void setPhUrl(String phUrl)
    {
        this.phUrl = phUrl;
    }

    public String getNicName()
    {
        return nicName;
    }

    public void setNicName(String nicName)
    {
        this.nicName = nicName;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getImuId()
    {
        return imuId;
    }

    public void setImuId(String imuId)
    {
        this.imuId = imuId;
    }

    public String getIsVuser()
    {
        return isVuser;
    }

    public void setIsVuser(String isVuser)
    {
        this.isVuser = isVuser;
    }

    @Override
    public String toString()
    {
        return "LChoseConnectMemberBean{" +
                "isChoose=" + isChoose +
                ", uId='" + uId + '\'' +
                ", id='" + id + '\'' +
                ", imuId='" + imuId + '\'' +
                ", phUrl='" + phUrl + '\'' +
                ", nicName='" + nicName + '\'' +
                ", sex='" + sex + '\'' +
                ", level='" + level + '\'' +
                ", age='" + age + '\'' +
                ", isVuser='" + isVuser + '\'' +
                '}';
    }
}

package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/10/17.
 */

public class MyFocusPersonBean
{
    private String uId;
    private String nicName;
    private String sex;
    private String age;
    private String uphUrl;//头像
    private String status;//是否正在直播
    private String focus;//是否已关注
    private String id;//数字id
    private String level;//数字id
    private String isAdmin;

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public MyFocusPersonBean(){}

    public String getuId()
    {
        return uId;
    }

    public void setuId(String uId)
    {
        this.uId = uId;
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

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public String getUphUrl()
    {
        return uphUrl;
    }

    public void setUphUrl(String uphUrl)
    {
        this.uphUrl = uphUrl;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getFocus()
    {
        return focus;
    }

    public void setFocus(String focus)
    {
        this.focus = focus;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return "MyFocusPersonBean{" +
                "uId='" + uId + '\'' +
                ", nicName='" + nicName + '\'' +
                ", sex='" + sex + '\'' +
                ", age='" + age + '\'' +
                ", uphUrl='" + uphUrl + '\'' +
                ", status='" + status + '\'' +
                ", focus='" + focus + '\'' +
                ", id='" + id + '\'' +
                ", level='" + level + '\'' +
                '}';
    }
}

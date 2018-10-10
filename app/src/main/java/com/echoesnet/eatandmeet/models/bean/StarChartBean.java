package com.echoesnet.eatandmeet.models.bean;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2017/11/28
 * @description
 */
public class StarChartBean
{
    private String age;//年龄
    private String focus;//是否关注，0否1是
    private String id;//id
    private String isVuser;//V用户标识 0：否1：是
    private String level;//主播等级
    private String nicName;//昵称
    private String phurl;//头像
    private String sex;//性别
    private String star;//当前已拥有星光
    private String uId;//uId
    private String rank;//当前排名

    public String getRank()
    {
        return rank;
    }

    public void setRank(String rank)
    {
        this.rank = rank;
    }

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
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

    public String getIsVuser()
    {
        return isVuser;
    }

    public void setIsVuser(String isVuser)
    {
        this.isVuser = isVuser;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getNicName()
    {
        return nicName;
    }

    public void setNicName(String nicName)
    {
        this.nicName = nicName;
    }

    public String getPhurl()
    {
        return phurl;
    }

    public void setPhurl(String phurl)
    {
        this.phurl = phurl;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getStar()
    {
        return star;
    }

    public void setStar(String star)
    {
        this.star = star;
    }

    public String getuId()
    {
        return uId;
    }

    public void setuId(String uId)
    {
        this.uId = uId;
    }

    @Override
    public String toString()
    {
        return "StarChartBean{" +
                "age='" + age + '\'' +
                ", focus='" + focus + '\'' +
                ", id='" + id + '\'' +
                ", isVuser='" + isVuser + '\'' +
                ", level='" + level + '\'' +
                ", nicName='" + nicName + '\'' +
                ", phurl='" + phurl + '\'' +
                ", sex='" + sex + '\'' +
                ", star='" + star + '\'' +
                ", uId='" + uId + '\'' +
                ", rank='" + rank + '\'' +
                '}';
    }
}

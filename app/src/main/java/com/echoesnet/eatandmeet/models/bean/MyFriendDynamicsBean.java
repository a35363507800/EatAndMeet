package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/7/28.
 */
public class MyFriendDynamicsBean
{

    private String phUrl;//头像
    private String nicName;//昵称
    private int rStar;//星级
    private String date;//时间
    private String evalContent;//内容
    private String epUrls;//评论图片
    private String rAddr;//餐厅地址
    private String rId;//餐厅ID
    private String remark;//好友备注
    private String level;//等级
    private String sex;//性别
    private String age;//年龄

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

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
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

    public int getrStar()
    {
        return rStar;
    }

    public void setrStar(int rStar)
    {
        this.rStar = rStar;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getEvalContent()
    {
        return evalContent;
    }

    public void setEvalContent(String evalContent)
    {
        this.evalContent = evalContent;
    }

    public String getEpUrls()
    {
        return epUrls;
    }

    public void setEpUrls(String epUrls)
    {
        this.epUrls = epUrls;
    }

    public String getrAddr()
    {
        return rAddr;
    }

    public void setrAddr(String rAddr)
    {
        this.rAddr = rAddr;
    }

    public String getrId()
    {
        return rId;
    }

    public void setrId(String rId)
    {
        this.rId = rId;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    @Override
    public String toString()
    {
        return "MyFriendDynamicsBean{" +
                "phUrl='" + phUrl + '\'' +
                ", nicName='" + nicName + '\'' +
                ", rStar=" + rStar +
                ", date='" + date + '\'' +
                ", evalContent='" + evalContent + '\'' +
                ", epUrls='" + epUrls + '\'' +
                ", rAddr='" + rAddr + '\'' +
                ", rId='" + rId + '\'' +
                ", remark='" + remark + '\'' +
                ", level='" + level + '\'' +
                ", sex='" + sex + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}

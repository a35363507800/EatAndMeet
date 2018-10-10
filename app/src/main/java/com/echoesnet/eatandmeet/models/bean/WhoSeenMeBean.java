package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/7/27.
 */
public class WhoSeenMeBean {

    private String mobile;
    private String nicName;
    private String signature;
    private String uId;
    private String uphUrl;
    private String vTime;
    private String remark;
    private String level;
    private String age;
    private String sex;

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNicName() {
        return nicName;
    }

    public void setNicName(String nicName) {
        this.nicName = nicName;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getUphUrl() {
        return uphUrl;
    }

    public void setUphUrl(String uphUrl) {
        this.uphUrl = uphUrl;
    }

    public String getvTime() {
        return vTime;
    }

    public void setvTime(String vTime) {
        this.vTime = vTime;
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
        return "WhoSeenMeBean{" +
                "mobile='" + mobile + '\'' +
                ", nicName='" + nicName + '\'' +
                ", signature='" + signature + '\'' +
                ", uId='" + uId + '\'' +
                ", uphUrl='" + uphUrl + '\'' +
                ", vTime='" + vTime + '\'' +
                ", remark='" + remark + '\'' +
                ", level='" + level + '\'' +
                ", age='" + age + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
}

package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2017/4/7.
 */

public class LiveGetRedPacketBean{

    private String amount;
    private String sex;
    private String level;
    private String phUrl;
    private String nicName;
    private String age;
    private String isVuser;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPhUrl() {
        return phUrl;
    }

    public void setPhUrl(String phUrl) {
        this.phUrl = phUrl;
    }

    public String getNicName() {
        return nicName;
    }

    public void setNicName(String nicName) {
        this.nicName = nicName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
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
        return "LiveGetRedPacketBean{" +
                "amount='" + amount + '\'' +
                ", sex='" + sex + '\'' +
                ", level='" + level + '\'' +
                ", phUrl='" + phUrl + '\'' +
                ", nicName='" + nicName + '\'' +
                ", age='" + age + '\'' +
                ", isVuser='" + isVuser + '\'' +
                '}';
    }
}

package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by an on 2017/3/29 0029.
 */

public class EncounterBean
{
    /**
     * id : 100028
     * nicName : 小任性
     * phUrl : http://wx.qlogo.cn/mmopen/icG3rd4trDsibjUaKjAJySa8sPoa4yAhn9662wfMDwAVUwUPLh3AiauQ8bQ52Dm7dibsTFSRBXbib5o2u2mZ15HFdWgCpFX2ic2kuJ/0
     * sex : 女
     * uId : 0f7245c5-0f7c-4c7a-ba48-a185575537fd
     */

    private String id;
    private String nicName;
    private String phUrl;
    private String sex;
    private String uId;
    private String living;
    private String level;
    private String signature;
    private String age;
    private String lookTime;
    private String distance;
    private String remark;


    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getLiving() {
        return living;
    }

    public void setLiving(String living) {
        this.living = living;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNicName() {
        return nicName;
    }

    public void setNicName(String nicName) {
        this.nicName = nicName;
    }

    public String getPhUrl() {
        return phUrl;
    }

    public void setPhUrl(String phUrl) {
        this.phUrl = phUrl;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUId() {
        return uId;
    }

    public void setUId(String uId) {
        this.uId = uId;
    }

    public String getuId()
    {
        return uId;
    }

    public void setuId(String uId)
    {
        this.uId = uId;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public String getSignature()
    {
        return signature;
    }

    public void setSignature(String signature)
    {
        this.signature = signature;
    }

    public String getLookTime()
    {
        return lookTime;
    }

    public void setLookTime(String lookTime)
    {
        this.lookTime = lookTime;
    }

    public String getDistance()
    {
        return distance;
    }

    public void setDistance(String distance)
    {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EncounterBean that = (EncounterBean) o;

        return uId != null ? uId.equals(that.uId) : that.uId == null;

    }

    @Override
    public int hashCode()
    {
        return uId != null ? uId.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return "EncounterBean{" +
                "id='" + id + '\'' +
                ", nicName='" + nicName + '\'' +
                ", phUrl='" + phUrl + '\'' +
                ", sex='" + sex + '\'' +
                ", uId='" + uId + '\'' +
                ", living='" + living + '\'' +
                ", level='" + level + '\'' +
                ", signature='" + signature + '\'' +
                ", age='" + age + '\'' +
                ", lookTime='" + lookTime + '\'' +
                ", distance='" + distance + '\'' +
                '}';
    }
}

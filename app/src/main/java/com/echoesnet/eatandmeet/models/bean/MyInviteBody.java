package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/8/4.
 */
public class MyInviteBody
{
    private String nicName;
    private String regTime;
    private String reward;
    private String desc;

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public String getNicName()
    {
        return nicName;
    }

    public void setNicName(String nicName)
    {
        this.nicName = nicName;
    }

    public String getRegTime()
    {
        return regTime;
    }

    public void setRegTime(String regTime)
    {
        this.regTime = regTime;
    }

    public String getReward()
    {
        return reward;
    }

    public void setReward(String reward)
    {
        this.reward = reward;
    }

    @Override
    public String toString()
    {
        return "MyInviteBody{" +
                "nicName='" + nicName + '\'' +
                ", regTime='" + regTime + '\'' +
                ", reward='" + reward + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}

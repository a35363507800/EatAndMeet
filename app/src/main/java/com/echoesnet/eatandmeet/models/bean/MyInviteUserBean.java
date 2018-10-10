package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/8/3.
 */
public class MyInviteUserBean
{
    private String nicName;
    private String regTime;
    private String reward;

    public MyInviteUserBean()
    {
    }

    public MyInviteUserBean(String nicName, String regTime, String reward)
    {
        this.nicName = nicName;
        this.regTime = regTime;
        this.reward = reward;
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
}

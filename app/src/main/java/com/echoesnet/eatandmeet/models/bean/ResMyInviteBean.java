package com.echoesnet.eatandmeet.models.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/8/4.
 */
public class ResMyInviteBean
{
    private String totalReward;
    private List<MyInviteBody> userBeen;


    public String getTotalReward()
    {
        return totalReward;
    }

    public void setTotalReward(String totalReward)
    {
        this.totalReward = totalReward;
    }

    public List<MyInviteBody> getUserBeen()
    {
        return userBeen;
    }

    public void setUserBeen(List<MyInviteBody> userBeen)
    {
        this.userBeen = userBeen;
    }

    @Override
    public String toString()
    {
        return "ResMyInviteBean{" +
                "totalReward='" + totalReward + '\'' +
                ", userBeen=" + userBeen.toString() +
                '}';
    }
}

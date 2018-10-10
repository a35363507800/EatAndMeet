package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2017/5/13.
 */

public class NewContactLstBean
{
    private String level;
    private String phUrl;

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getPhUrl()
    {
        return phUrl;
    }

    public void setPhUrl(String phUrl)
    {
        this.phUrl = phUrl;
    }

    @Override
    public String toString()
    {
        return "NewContactLstBean{" +
                "level='" + level + '\'' +
                ", phUrl='" + phUrl + '\'' +
                '}';
    }
}

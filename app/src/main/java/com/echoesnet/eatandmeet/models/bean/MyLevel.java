package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by liuchao on 2017/4/20 13.
 */

public class MyLevel
{
    private String phUrl; // 用户头像
    private int level; // 当前等级
    private int nextLevel; // 下一等级
    private int curExp; // 当前有经验（全部）
    private int upLevelExp; // 还差多少经验升级
    private int levelExp; // 本级已有多少经验
    private int userLevel; //当前用户真实等级
    //private list<>  privilegeList; // 当前等级


    public String getPhUrl()
    {
        return phUrl;
    }

    public void setPhUrl(String phUrl)
    {
        this.phUrl = phUrl;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public int getNextLevel()
    {
        return nextLevel;
    }

    public void setNextLevel(int nextLevel)
    {
        this.nextLevel = nextLevel;
    }

    public int getCurExp()
    {
        return curExp;
    }

    public void setCurExp(int curExp)
    {
        this.curExp = curExp;
    }

    public int getUpLevelExp()
    {
        return upLevelExp;
    }

    public void setUpLevelExp(int upLevelExp)
    {
        this.upLevelExp = upLevelExp;
    }

    public int getLevelExp()
    {
        return levelExp;
    }

    public void setLevelExp(int levelExp)
    {
        this.levelExp = levelExp;
    }

    public int getUserLevel()
    {
        return userLevel;
    }

    public void setUserLevel(int userLevel)
    {
        this.userLevel = userLevel;
    }
}
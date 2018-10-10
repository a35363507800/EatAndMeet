package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by lc on 2017/7/20 18.
 */

public class StarCommentBean
{
//       "isState":false,
//       "labelStr":"颜值"
    private String isState;
    private String labelStr;

    public String getIsState()
    {
        return isState;
    }

    public void setIsState(String isState)
    {
        this.isState = isState;
    }

    public String getLabelStr()
    {
        return labelStr;
    }

    public void setLabelStr(String labelStr)
    {
        this.labelStr = labelStr;
    }

    @Override
    public String toString()
    {
        return "StarCommentBean{" +
                "isState='" + isState + '\'' +
                ", labelStr='" + labelStr + '\'' +
                '}';
    }
}

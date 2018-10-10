package com.echoesnet.eatandmeet.models.bean;

import com.echoesnet.eatandmeet.models.datamodel.LiveMsgType;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier ben
 * @createDate 2017/5/30
 * @description
 */
public class RefreshLiveMsgBean
{
    private String text;
    private String id;
    private String name;
    private String hxId;
    /**
     * 消息类型 默认TEXT_TYPE {@link LiveMsgType}
     */
    private LiveMsgType type;
    private String gName;
    private String giftNum;
    private String gUrl;
    private String gType;
    private String level;
    private String liveLevelState;
    private String uId;
    private String streamId;//红包streamId
    private String msgL;//消息L

    public String getMsgL()
    {
        return msgL;
    }

    public void setMsgL(String msgL)
    {
        this.msgL = msgL;
    }

    public String getMsgR()
    {
        return msgR;
    }

    public void setMsgR(String msgR)
    {
        this.msgR = msgR;
    }

    private String msgR;//消息L

    public String getuId()
    {
        return uId;
    }

    public void setuId(String uId)
    {
        this.uId = uId;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        if (id.contains("u"))
        {
            String[] ids = id.split("u");
            if (ids.length >= 2) ;
            id = ids[1];
        }
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public LiveMsgType getType()
    {
        return type;
    }

    public void setType(LiveMsgType type)
    {
        this.type = type;
    }

    public String getgName()
    {
        return gName;
    }

    public void setgName(String gName)
    {
        this.gName = gName;
    }

    public String getGiftNum()
    {
        return giftNum;
    }

    public void setGiftNum(String giftNum)
    {
        this.giftNum = giftNum;
    }

    public String getgUrl()
    {
        return gUrl;
    }

    public void setgUrl(String gUrl)
    {
        this.gUrl = gUrl;
    }

    public String getgType()
    {
        return gType;
    }

    public void setgType(String gType)
    {
        this.gType = gType;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getLiveLevelState()
    {
        return liveLevelState;
    }

    public void setLiveLevelState(String liveLevelState)
    {
        this.liveLevelState = liveLevelState;
    }

    public String getStreamId()
    {
        return streamId;
    }

    public void setStreamId(String streamId)
    {
        this.streamId = streamId;
    }

    public String getHxId()
    {
        return hxId;
    }

    public void setHxId(String hxId)
    {
        this.hxId = hxId;
    }

    @Override
    public String toString()
    {
        return "RefreshLiveMsgBean{" +
                "text='" + text + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", gName='" + gName + '\'' +
                ", giftNum='" + giftNum + '\'' +
                ", gUrl='" + gUrl + '\'' +
                ", gType='" + gType + '\'' +
                ", level='" + level + '\'' +
                ", liveLevelState='" + liveLevelState + '\'' +
                ", uId='" + uId + '\'' +
                ", streamId='" + streamId + '\'' +
                '}';
    }
}

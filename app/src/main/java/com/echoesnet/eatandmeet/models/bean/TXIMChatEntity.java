package com.echoesnet.eatandmeet.models.bean;

import com.echoesnet.eatandmeet.models.datamodel.LiveMsgType;

/**
 * 消息体类
 */
public class TXIMChatEntity
{
    private String id;// 用户id
    private String grpSendName;
    private String content;
    private String giftName;
    private String giftUrl;
    private String giftNum;
    private String isBigGift;
    private LiveMsgType type=LiveMsgType.NormalText; //消息类型：
    private String level="0";
    private String liveLevelState;
    private String streamId;
    private String memberName;
    private String uId;
    private String hxId;

    private String msgL="";//对应消息为“一道金光闪过 xxx 进入房间 左边文字”
    private String msgR="";//右边文字

    public String getHxId() {
        return hxId;
    }

    public void setHxId(String hxId) {
        this.hxId = hxId;
    }

    public String getuId()
    {
        return uId;
    }

    public void setuId(String uId)
    {
        this.uId = uId;
    }

    public String getStreamId()
    {
        return streamId;
    }

    public void setStreamId(String streamId)
    {
        this.streamId = streamId;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getGrpSendName()
    {
        return grpSendName;
    }

    public void setGrpSendName(String grpSendName)
    {
        this.grpSendName = grpSendName;
    }

    public String getIsBigGift()
    {
        return isBigGift;
    }

    public void setIsBigGift(String isBigGift)
    {
        this.isBigGift = isBigGift;
    }

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

    public TXIMChatEntity()
    {
        // TODO Auto-generated constructor stub
    }


    public String getGiftNum()
    {
        return giftNum;
    }

    public void setGiftNum(String giftNum)
    {
        this.giftNum = giftNum;
    }

    public String getGiftUrl()
    {
        return giftUrl;
    }

    public void setGiftUrl(String giftUrl)
    {
        this.giftUrl = giftUrl;
    }

    public String getGiftName()
    {
        return giftName;
    }

    public void setGiftName(String giftName)
    {
        this.giftName = giftName;
    }

    public String getSenderName()
    {
        return grpSendName;
    }

    public void setSenderName(String grpSendName)
    {
        this.grpSendName = grpSendName;
    }


    public String getContent()
    {
        return content;
    }

    public void setContent(String context)
    {
        this.content = context;
    }


    public LiveMsgType getType()
    {
        return type;
    }

    public void setType(LiveMsgType type)
    {
        this.type = type;
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

    public String getMemberName()
    {
        return memberName;
    }

    public void setMemberName(String memberName)
    {
        this.memberName = memberName;
    }

    @Override
    public String toString()
    {
        return "TXIMChatEntity{" +
                "id='" + id + '\'' +
                ", grpSendName='" + grpSendName + '\'' +
                ", context='" + content + '\'' +
                ", giftName='" + giftName + '\'' +
                ", giftUrl='" + giftUrl + '\'' +
                ", giftNum='" + giftNum + '\'' +
                ", isBigGift='" + isBigGift + '\'' +
                ", type=" + type +
                ", level='" + level + '\'' +
                ", liveLevelState='" + liveLevelState + '\'' +
                ", streamId='" + streamId + '\'' +
                ", memberName='" + memberName + '\'' +
                ", uId='" + uId + '\'' +
                '}';
    }
}

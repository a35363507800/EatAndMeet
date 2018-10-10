package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by liuchao on 2017/5/11 09.
 */

public class ShareToFaceBean extends ShareBean
{
    private String openSouse;
    private String roomName;
    private String titleImage;
    private String roomId;
    private String uid;
    private String gameId;
    private String columnId;
    private String activityId;
    private String messageDes;
    private String sendType;
    private String clubId;

    public String getClubId()
    {
        return clubId;
    }

    public void setClubId(String clubId)
    {
        this.clubId = clubId;
    }

    public String getSendType()
    {
        return sendType;
    }

    public void setSendType(String sendType)
    {
        this.sendType = sendType;
    }

    public String getMessageDes()
    {
        return messageDes;
    }

    public void setMessageDes(String messageDes)
    {
        this.messageDes = messageDes;
    }

    public String getColumnId()
    {
        return columnId;
    }

    public void setColumnId(String columnId)
    {
        this.columnId = columnId;
    }

    public String getActivityId()
    {
        return activityId;
    }

    public void setActivityId(String activityId)
    {
        this.activityId = activityId;
    }

    public String getGameId()
    {
        return gameId;
    }

    public void setGameId(String gameId)
    {
        this.gameId = gameId;
    }

    public String getOpenSouse()
    {
        return openSouse;
    }

    public void setOpenSouse(String openSouse)
    {
        this.openSouse = openSouse;
    }

    public String getRoomName()
    {
        return roomName;
    }

    public void setRoomName(String roomName)
    {
        this.roomName = roomName;
    }

    public String getTitleImage()
    {
        return titleImage;
    }

    public void setTitleImage(String titleImage)
    {
        this.titleImage = titleImage;
    }

    public String getRoomId()
    {
        return roomId;
    }

    public void setRoomId(String roomId)
    {
        this.roomId = roomId;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    @Override
    public String toString()
    {
        return "ShareToFaceBean{" +
                "openSouse='" + openSouse + '\'' +
                ", roomName='" + roomName + '\'' +
                ", titleImage='" + titleImage + '\'' +
                ", roomId='" + roomId + '\'' +
                ", uid='" + uid + '\'' +
                ", gameId='" + gameId + '\'' +
                ", columnId='" + columnId + '\'' +
                ", activityId='" + activityId + '\'' +
                ", messageDes='" + messageDes + '\'' +
                ", sendType='" + sendType + '\'' +
                ", clubId='" + clubId + '\'' +
                '}'+
                super.toString();
    }
}

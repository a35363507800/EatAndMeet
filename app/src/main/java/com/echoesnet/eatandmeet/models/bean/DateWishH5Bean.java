package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by an on 2017/6/9 0009.
 */

public class DateWishH5Bean
{
    /**
     * roomId : 100006
     * liveSource : 0
     * imuId : 1365202805510902335
     */

    private String roomId;
    private String liveSource;
    private String imuId;

    public String getRoomId()
    {
        return roomId;
    }

    public void setRoomId(String roomId)
    {
        this.roomId = roomId;
    }

    public String getLiveSource()
    {
        return liveSource;
    }

    public void setLiveSource(String liveSource)
    {
        this.liveSource = liveSource;
    }

    public String getImuId()
    {
        return imuId;
    }

    public void setImuId(String imuId)
    {
        this.imuId = imuId;
    }

    @Override
    public String toString()
    {
        return "DateWishH5Bean{" +
                "roomId=" + roomId +
                ", liveSource='" + liveSource + '\'' +
                ", imuId='" + imuId + '\'' +
                '}';
    }
}

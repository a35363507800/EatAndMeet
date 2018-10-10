package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/5/19.
 */
public class RoomSearchDetailBean {

    private String roomUrl;
    private String roomName;
    private int distance;

    public String getRoomUrl() {
        return roomUrl;
    }

    public void setRoomUrl(String roomUrl) {
        this.roomUrl = roomUrl;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}

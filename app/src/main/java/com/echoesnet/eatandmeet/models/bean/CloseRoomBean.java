package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/10/21 0021.
 */

public class CloseRoomBean {
    private String uid;
    private String time;
    private String viewer;
    private String meal;
    private String roomId;
    private String sec;

    public String getSec() {
        return sec;
    }

    public void setSec(String sec) {
        this.sec = sec;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getViewer() {
        return viewer;
    }

    public void setViewer(String viewer) {
        this.viewer = viewer;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "CloseRoomBean{" +
                "uid='" + uid + '\'' +
                ", time='" + time + '\'' +
                ", viewer='" + viewer + '\'' +
                ", meal='" + meal + '\'' +
                ", roomId='" + roomId + '\'' +
                ", sec='" + sec + '\'' +
                '}';
    }
}

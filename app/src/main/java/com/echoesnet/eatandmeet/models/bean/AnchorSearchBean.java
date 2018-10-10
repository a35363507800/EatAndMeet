package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by an on 2016/10/17 0017.
 */

public class AnchorSearchBean {
    private String uId;
    private String id;
    private String nicName;
    private String sex;
    private String age;
    private String uphUrl;
    private String roomId;
    private String focus;
    private String status;//0 未直播 1直播中
    private String level;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getNicName() {
        return nicName;
    }

    public void setNicName(String nicName) {
        this.nicName = nicName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getUphUrl() {
        return uphUrl;
    }

    public void setUphUrl(String uphUrl) {
        this.uphUrl = uphUrl;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    @Override
    public String toString()
    {
        return "AnchorSearchBean{" +
                "uId='" + uId + '\'' +
                ", id='" + id + '\'' +
                ", nicName='" + nicName + '\'' +
                ", sex='" + sex + '\'' +
                ", age='" + age + '\'' +
                ", uphUrl='" + uphUrl + '\'' +
                ", roomId='" + roomId + '\'' +
                ", focus='" + focus + '\'' +
                ", status='" + status + '\'' +
                ", level='" + level + '\'' +
                '}';
    }
}

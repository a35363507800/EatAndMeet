package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by an on 2016/11/1 0001.
 */

public class HotAnchorBean {
    private String roomId;
    private String roomName;
    private String roomUrl;
    private String sign;
    private String uId;
    private String focus;
    private String meal;
    private String vedio;
    private String sex;
    private String level;
    private String distance;
    private String anchorType;//主播类型 0 全民1自由2兼职3全职
    private String anchorTypeUrl;//String 主播类型图标url

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getVedio() {
        return vedio;
    }

    public void setVedio(String vedio) {
        this.vedio = vedio;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomUrl() {
        return roomUrl;
    }

    public void setRoomUrl(String roomUrl) {
        this.roomUrl = roomUrl;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getAnchorType() {
        return anchorType;
    }

    public void setAnchorType(String anchorType) {
        this.anchorType = anchorType;
    }

    public String getAnchorTypeUrl() {
        return anchorTypeUrl;
    }

    public void setAnchorTypeUrl(String anchorTypeUrl) {
        this.anchorTypeUrl = anchorTypeUrl;
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
        return "HotAnchorBean{" +
                "roomId='" + roomId + '\'' +
                ", roomName='" + roomName + '\'' +
                ", roomUrl='" + roomUrl + '\'' +
                ", sign='" + sign + '\'' +
                ", uId='" + uId + '\'' +
                ", focus='" + focus + '\'' +
                ", meal='" + meal + '\'' +
                ", vedio='" + vedio + '\'' +
                ", sex='" + sex + '\'' +
                ", level='" + level + '\'' +
                ", distance='" + distance + '\'' +
                ", anchorType='" + anchorType + '\'' +
                ", anchorTypeUrl='" + anchorTypeUrl + '\'' +
                '}';
    }
}

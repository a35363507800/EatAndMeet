package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/5/19.
 */
public class RestaurantSearchDetailBean {

    private String roomUrl;
    private String rName;
    private String perPrice;
    private String rStar;
    private String[] location;
    private String miniPrice;
    private String rId;

    public String getRoomUrl() {
        return roomUrl;
    }

    public void setRoomUrl(String roomUrl) {
        this.roomUrl = roomUrl;
    }

    public String getrName() {
        return rName;
    }

    public void setrName(String rName) {
        this.rName = rName;
    }

    public String getPerPrice() {
        return perPrice;
    }

    public void setPerPrice(String perPrice) {
        this.perPrice = perPrice;
    }

    public String getrStar() {
        return rStar;
    }

    public void setrStar(String rStar) {
        this.rStar = rStar;
    }

    public String[] getLocation() {
        return location;
    }

    public void setLocation(String[] location) {
        this.location = location;
    }

    public String getMiniPrice() {
        return miniPrice;
    }

    public void setMiniPrice(String miniPrice) {
        this.miniPrice = miniPrice;
    }

    public String getrId() {
        return rId;
    }

    public void setrId(String rId) {
        this.rId = rId;
    }
}

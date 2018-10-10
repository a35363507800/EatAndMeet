package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/8/24.
 */
public class SortResBeen {

    private String rId;
    private String distance;

    public String getrId() {
        return rId;
    }

    public void setrId(String rId) {
        this.rId = rId;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "SortResBeen{" +
                "rId='" + rId + '\'' +
                ", distance='" + distance + '\'' +
                '}';
    }
}

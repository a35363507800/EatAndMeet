package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/6/6.
 */
public class RestaurantSearchBean {
    // 餐厅id
    private String rId;
    // 餐厅名称
    private String rName;
    // 餐厅位置点
    private String mapId;
    // 餐厅人均消费
    private String rPer;
    // 餐厅图片的url
    private String rpUrl;


    public String getrId() {
        return rId;
    }

    public void setrId(String rId) {
        this.rId = rId;
    }

    public String getrName() {
        return rName;
    }

    public void setrName(String rName) {
        this.rName = rName;
    }

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public String getrPer() {
        return rPer;
    }

    public void setrPer(String rPer) {
        this.rPer = rPer;
    }

    public String getRpUrl() {
        return rpUrl;
    }

    public void setRpUrl(String rpUrl) {
        this.rpUrl = rpUrl;
    }
}

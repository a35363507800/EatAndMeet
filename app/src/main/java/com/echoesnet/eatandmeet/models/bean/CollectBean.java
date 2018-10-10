package com.echoesnet.eatandmeet.models.bean;

import java.util.Arrays;

/**
 * Created by Administrator on 2016/7/26.
 */
public class CollectBean {
    private String rId;       // 餐厅ID
    private String rName;     // 餐厅名称
    private String perPrice;  // 人均消费
    private String rpUrls;    // 餐厅图片
    private String posxy;     // 坐标
    private String[] location; // 经纬度
    private boolean isSelect;
    private String lessPrice; // 最低消费
    private String distance;  //距离
    private String delCollectId;//"删除收藏用的餐厅号，在调用删除收藏接口时需要改为此参数"
    private String homeparty;   //"1是轰趴馆订单0普通订单"


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

    public String getRpUrls() {
        return rpUrls;
    }

    public void setRpUrls(String rpUrls) {
        this.rpUrls = rpUrls;
    }

    public String getPosxy() {
        return posxy;
    }

    public void setPosxy(String posxy) {
        this.posxy = posxy;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getrId() {
        return rId;
    }

    public void setrId(String rId) {
        this.rId = rId;
    }

    public String[] getLocation() {
        return location;
    }

    public void setLocation(String[] location) {
        this.location = location;
    }

    public String getLessPrice() {
        return lessPrice;
    }

    public void setLessPrice(String lessPrice) {
        this.lessPrice = lessPrice;
    }

    public String getDistance()
    {
        return distance;
    }

    public void setDistance(String distance)
    {
        this.distance = distance;
    }

    public String getDelCollectId()
    {
        return delCollectId;
    }

    public void setDelCollectId(String delCollectId)
    {
        this.delCollectId = delCollectId;
    }

    public String getHomeparty()
    {
        return homeparty;
    }

    public void setHomeparty(String homeparty)
    {
        this.homeparty = homeparty;
    }

    @Override
    public String toString()
    {
        return "CollectBean{" +
                "rId='" + rId + '\'' +
                ", rName='" + rName + '\'' +
                ", perPrice='" + perPrice + '\'' +
                ", rpUrls='" + rpUrls + '\'' +
                ", posxy='" + posxy + '\'' +
                ", location=" + Arrays.toString(location) +
                ", isSelect=" + isSelect +
                ", lessPrice='" + lessPrice + '\'' +
                ", distance='" + distance + '\'' +
                ", delCollectId='" + delCollectId + '\'' +
                ", homeparty='" + homeparty + '\'' +
                '}';
    }
}

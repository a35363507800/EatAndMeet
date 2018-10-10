package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/8/15.
 */
public class SearchRestaurantBean {

    private String rId;         // 餐厅ID
    private String rName;       // 餐厅名称
    private String posxy;       // 餐厅位置点
    private String perPrice;    // 餐厅人均消费
    private String rpUrls;      // 餐厅图片的url
    private String lessPrice;   // 起订价
    private String rStar;       // 餐厅星级
    private String floor;       // 楼层
    private String distance;    //距离用户距离

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

    public String getPosxy() {
        return posxy;
    }

    public void setPosxy(String posxy) {
        this.posxy = posxy;
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

    public String getLessPrice() {
        return lessPrice;
    }

    public void setLessPrice(String lessPrice) {
        this.lessPrice = lessPrice;
    }

    public String getrStar() {
        return rStar;
    }

    public void setrStar(String rStar) {
        this.rStar = rStar;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getDistance()
    {
        return distance;
    }

    public void setDistance(String distance)
    {
        this.distance = distance;
    }

    @Override
    public String toString()
    {
        return "SearchRestaurantBean{" +
                "distance='" + distance + '\'' +
                ", rId='" + rId + '\'' +
                ", rName='" + rName + '\'' +
                ", posxy='" + posxy + '\'' +
                ", perPrice='" + perPrice + '\'' +
                ", rpUrls='" + rpUrls + '\'' +
                ", lessPrice='" + lessPrice + '\'' +
                ", rStar='" + rStar + '\'' +
                ", floor='" + floor + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchRestaurantBean that = (SearchRestaurantBean) o;

        if (rId != null ? !rId.equals(that.rId) : that.rId != null) return false;
        if (rName != null ? !rName.equals(that.rName) : that.rName != null) return false;

        return distance != null ? distance.equals(that.distance) : that.distance == null;
    }


}

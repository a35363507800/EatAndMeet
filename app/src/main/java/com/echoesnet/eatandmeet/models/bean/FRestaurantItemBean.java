package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by an on 2017/3/30 0030.
 */

public class FRestaurantItemBean {

    /**
     * activity : 0
     * lessPrice : 10
     * posxy : 39.137557!=end=!117.214277
     * rId : 1201030002
     * rName : 咖啡之翼
     * rpUrl : http://huisheng.ufile.ucloud.cn/1489135498699BsjG5C.jpg
     * seq : 1
     * word : 易南正在直播中
     */

    private String activity;
    private String lessPrice;
    private String posxy;
    private String rId;
    private String rName;
    private String rpUrl;
    private String seq;
    private String word;
    private String rAddr;
    private String distance;
    private String perPrice;

    public String getPerPrice() {
        return perPrice;
    }

    public void setPerPrice(String perPrice) {
        this.perPrice = perPrice;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getrAddr() {
        return rAddr;
    }

    public void setrAddr(String rAddr) {
        this.rAddr = rAddr;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getLessPrice() {
        return lessPrice;
    }

    public void setLessPrice(String lessPrice) {
        this.lessPrice = lessPrice;
    }

    public String getPosxy() {
        return posxy;
    }

    public void setPosxy(String posxy) {
        this.posxy = posxy;
    }

    public String getRId() {
        return rId;
    }

    public void setRId(String rId) {
        this.rId = rId;
    }

    public String getRName() {
        return rName;
    }

    public void setRName(String rName) {
        this.rName = rName;
    }

    public String getRpUrl() {
        return rpUrl;
    }

    public void setRpUrl(String rpUrl) {
        this.rpUrl = rpUrl;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return "FRestaurantItemBean{" +
                "activity='" + activity + '\'' +
                ", lessPrice='" + lessPrice + '\'' +
                ", posxy='" + posxy + '\'' +
                ", rId='" + rId + '\'' +
                ", rName='" + rName + '\'' +
                ", rpUrl='" + rpUrl + '\'' +
                ", seq='" + seq + '\'' +
                ", word='" + word + '\'' +
                ", rAddr='" + rAddr + '\'' +
                ", distance='" + distance + '\'' +
                ", perPrice='" + perPrice + '\'' +
                '}';
    }
}

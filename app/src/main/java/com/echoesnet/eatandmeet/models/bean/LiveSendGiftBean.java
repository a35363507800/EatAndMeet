package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by liuyang on 2017/1/16.
 */

public class LiveSendGiftBean
{
//    {"faceEgg":"21096","mealTotal":"364783"}
    /**
     * faceEgg : 21096
     * mealTotal : 364783
     */

    private String faceEgg;
    private String mealTotal;
    private String star;
    private String ranking;

    public String getGiftID() {
        return giftID;
    }

    public void setGiftID(String giftID) {
        this.giftID = giftID;
    }

    private String giftID;

    public String getGiftCount() {
        return giftCount;
    }

    public void setGiftCount(String giftCount) {
        this.giftCount = giftCount;
    }

    private String giftCount;

    public String getFaceEgg() {
        return faceEgg;
    }

    public void setFaceEgg(String faceEgg) {
        this.faceEgg = faceEgg;
    }

    public String getMealTotal() {
        return mealTotal;
    }

    public void setMealTotal(String mealTotal) {
        this.mealTotal = mealTotal;
    }


    public String getStar()
    {
        return star;
    }

    public void setStar(String star)
    {
        this.star = star;
    }

    public String getRanking()
    {
        return ranking;
    }

    public void setRanking(String ranking)
    {
        this.ranking = ranking;
    }
}

package com.echoesnet.eatandmeet.models.bean;

/**
 * 礼物mgbean
 * Created by an on 2016/10/25 0025.
 */

public class GiftMsgBean {
    private String isGift;
    private GiftBean gift;
    private String number;
    private String mealTotal;
    private int  countTotal; //礼物叠加数量
    private String ranking;
    private String  star;

    public String getRanking()
    {
        return ranking;
    }

    public void setRanking(String ranking)
    {
        this.ranking = ranking;
    }

    public String getStar()
    {
        return star;
    }

    public void setStar(String star)
    {
        this.star = star;
    }

    public int  getCountTotal()
    {
        return countTotal;
    }

    public void setCountTotal(int countTotal)
    {
        this.countTotal = countTotal;
    }

    public String getMealTotal() {
        return mealTotal;
    }

    public void setMealTotal(String mealTotal) {
        this.mealTotal = mealTotal;
    }

    public String getIsGift() {
        return isGift;
    }

    public void setIsGift(String isGift) {
        this.isGift = isGift;
    }

    public GiftBean getGift() {
        return gift;
    }

    public void setGift(GiftBean gift) {
        this.gift = gift;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString()
    {
        return "GiftMsgBean{" +
                "isGift='" + isGift + '\'' +
                ", gift=" + gift +
                ", number='" + number + '\'' +
                ", mealTotal='" + mealTotal + '\'' +
                ", countTotal='" + countTotal + '\'' +
                '}';
    }
}

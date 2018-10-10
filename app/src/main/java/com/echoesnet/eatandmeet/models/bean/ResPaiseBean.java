package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/6/15.
 */
public class ResPaiseBean {

    private String rId;
    private String rPraise;
    private String[] resPraiseList;

    public String getrId() {
        return rId;
    }

    public void setrId(String rId) {
        this.rId = rId;
    }

    public String getrPraise() {
        return rPraise;
    }

    public void setrPraise(String rPraise) {
        this.rPraise = rPraise;
    }

    public String[] getResPraiseList() {
        return resPraiseList;
    }

    public void setResPraiseList(String[] resPraiseList) {
        this.resPraiseList = resPraiseList;
    }
}

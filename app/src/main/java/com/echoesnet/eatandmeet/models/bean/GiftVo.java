package com.echoesnet.eatandmeet.models.bean;

/**
 * 小礼物vo
 * Created by Administrator on 2016/10/27 0027.
 */

public class GiftVo {
    String userName;
    String userHead;
    GiftMsgBean giftMsgBean;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }

    public GiftMsgBean getGiftMsgBean() {
        return giftMsgBean;
    }

    public void setGiftMsgBean(GiftMsgBean giftMsgBean) {
        this.giftMsgBean = giftMsgBean;
    }

    @Override
    public String toString() {
        return "GiftVo{" +
                "userName='" + userName + '\'' +
                ", userHead='" + userHead + '\'' +
                ", giftMsgBean=" + giftMsgBean +
                '}';
    }
}

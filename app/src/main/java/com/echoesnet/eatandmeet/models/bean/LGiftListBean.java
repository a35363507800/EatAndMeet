package com.echoesnet.eatandmeet.models.bean;

import java.util.ArrayList;

/**
 * Created by an on 2016/10/24 0024.
 */

public class LGiftListBean {
    private String balance;
    private ArrayList<GiftBean> gifts;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public ArrayList<GiftBean> getGifts() {
        return gifts;
    }

    public void setGifts(ArrayList<GiftBean> gifts) {
        this.gifts = gifts;
    }

    @Override
    public String toString() {
        return "LGiftListBean{" +
                "balance='" + balance + '\'' +
                ", gifts=" + gifts +
                '}';
    }
}

package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2017/4/7.
 */

public class LiveRedPacketBean extends SelectedBean{

    private String amount; // 红包金额
    private String name;   // 红包名称
    private String url;    // 红包图片

    public LiveRedPacketBean(String amount, String name, String url) {
        this.amount = amount;
        this.name = name;
        this.url = url;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

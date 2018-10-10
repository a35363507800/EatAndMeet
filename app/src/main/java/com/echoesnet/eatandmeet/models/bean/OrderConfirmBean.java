package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/5/18.
 */
public class OrderConfirmBean
{

    private String type;
    private String name;
    private String number;
    private String price;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}

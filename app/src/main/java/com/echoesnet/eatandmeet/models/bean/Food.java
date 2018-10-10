package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/7/25.
 */
public class Food {

    public int food_img;
    public String food_name;
    public String food_price;
    public String text;

    public int getFood_img() {
        return food_img;
    }

    public void setFood_img(int food_img) {
        this.food_img = food_img;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getFood_price() {
        return food_price;
    }

    public void setFood_price(String food_price) {
        this.food_price = food_price;
    }

    public Food(String food_name, String food_price) {
        super();
        this.food_name = food_name;
        this.food_price = food_price;
    }

    public Food() {
        super();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
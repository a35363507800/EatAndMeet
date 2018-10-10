package com.echoesnet.eatandmeet.models.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/6.
 */
public class OrderMenuLeftBean {

    private String title;
    private boolean isSelect;
    private int selectNum;
    private String testUrl;
    private String classLabel;
    private String price;
    private List<OrderMenuLeftBean> list;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getSelectNum() {
        return selectNum;
    }

    public void setSelectNum(int selectNum) {
        this.selectNum = selectNum;
    }

    public String getTestUrl() {
        return testUrl;
    }

    public void setTestUrl(String testUrl) {
        this.testUrl = testUrl;
    }

    public String getClassLabel() {
        return classLabel;
    }

    public void setClassLabel(String classLabel) {
        this.classLabel = classLabel;
    }

    public List<OrderMenuLeftBean> getList() {
        return list;
    }

    public void setList(List<OrderMenuLeftBean> list) {
        this.list = list;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "OrderMenuLeftBean{" +
                "title='" + title + '\'' +
                ", isSelect=" + isSelect +
                ", selectNum=" + selectNum +
                ", testUrl='" + testUrl + '\'' +
                ", classLabel='" + classLabel + '\'' +
                ", price='" + price + '\'' +
                ", list=" + list +
                '}';
    }
}

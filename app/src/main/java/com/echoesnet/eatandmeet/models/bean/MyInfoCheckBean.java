package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/7/29.
 */
public class MyInfoCheckBean {

    private String state; // 状态：显示图片还是文字(审核通过或未通过)
    private String unCheck; // 未通过原因
    private String checkTime; // 时间

    public String getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUnCheck() {
        return unCheck;
    }

    public void setUnCheck(String unCheck) {
        this.unCheck = unCheck;
    }
}

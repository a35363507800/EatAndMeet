package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/12/28.
 */

public class CommentBean {

    private boolean isState;
    public String labelStr;

    public CommentBean() {

    }

    public CommentBean(String labelStr) {
        this.labelStr = labelStr;
    }

    public CommentBean(boolean isState, String labelStr) {
        this.isState = isState;
        this.labelStr = labelStr;
    }

    public boolean isState() {
        return isState;
    }

    public void setState(boolean state) {
        isState = state;
    }

    public String getLabelStr() {
        return labelStr;
    }

    public void setLabelStr(String labelStr) {
        this.labelStr = labelStr;
    }

    @Override
    public String toString() {
        return "CommentBean{" +
                "isState=" + isState +
                ", labelStr='" + labelStr + '\'' +
                '}';
    }

    /*@Override
    public boolean equals(Object obj) {
        CommentBean s=(CommentBean)obj;
        return labelStr.equals(s.labelStr);
    }
    @Override
    public int hashCode() {
        String in = labelStr;
        return in.hashCode();
    }*/
}

package com.echoesnet.eatandmeet.models.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/12/29.
 */

public class CommentInfoBean {

    private String epUrls;
    private List<CommentBean> commentBeanList;
    private String commentStr;

    public String getEpUrls() {
        return epUrls;
    }

    public void setEpUrls(String epUrls) {
        this.epUrls = epUrls;
    }

    public List<CommentBean> getCommentBeanList() {
        return commentBeanList;
    }

    public void setCommentBeanList(List<CommentBean> commentBeanList) {
        this.commentBeanList = commentBeanList;
    }

    public String getCommentStr() {
        return commentStr;
    }

    public void setCommentStr(String commentStr) {
        this.commentStr = commentStr;
    }
}

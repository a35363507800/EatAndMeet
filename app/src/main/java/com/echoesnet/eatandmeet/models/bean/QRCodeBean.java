package com.echoesnet.eatandmeet.models.bean;

/**
 * 二维码Bean
 * Created by an on 2017/1/6 0006.
 */

public class QRCodeBean {
    private String type;
    private String content;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "QRCodeBean{" +
                "type='" + type + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

package com.echoesnet.eatandmeet.models.bean;

import java.io.Serializable;


/**
 *
 */
public class PhotoBean implements Serializable
{
    String photoName;
    String path;
    int type;//0 图片 1 视频
    long durantion;
    long time;
    public String getPhotoName() {
        return photoName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        int lastIndexOf = this.path.lastIndexOf("/");
        this.photoName = this.path.substring(lastIndexOf);
        if (path.endsWith(".mp4") || path.endsWith(".3gp") || path.endsWith(".amr")) {
            this.type = 1;
        } else {
            type = 0;
        }

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public long getDurantion() {
        return durantion;
    }

    public void setDurantion(long durantion) {
        this.durantion = durantion;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

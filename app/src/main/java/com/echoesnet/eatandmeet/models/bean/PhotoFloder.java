package com.echoesnet.eatandmeet.models.bean;

/**
 * <pre>
 * author : No.1
 * time : 2017/4/13.
 * desc :
 * </pre>
 */

public class PhotoFloder {
    String dir;//文件路径
    String fileName;//文件名
    int number;//照片数量
    String cover;//封面
    boolean isSelect;//是否选中
    int type;//封面类型

    public String getFileName() {
        return fileName;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
        int lastIndexOf = this.dir.lastIndexOf("/")+1;
        this.fileName = this.dir.substring(lastIndexOf);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "PhotoFloder{" +
                "dir='" + dir + '\'' +
                ", fileName='" + fileName + '\'' +
                ", number=" + number +
                ", cover='" + cover + '\'' +
                ", isSelect=" + isSelect +
                ", type=" + type +
                '}'+"\n";
    }
}

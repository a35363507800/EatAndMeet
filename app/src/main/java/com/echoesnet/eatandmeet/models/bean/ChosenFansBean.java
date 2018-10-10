package com.echoesnet.eatandmeet.models.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/17.
 */

public class ChosenFansBean implements Parcelable
{
    private String uId;
    private String nicName;
    private String sex;
    private String age;
    private String uphUrl;  //头像
    private String status;  //是否正在直播
    private String focus;   //是否已关注
    private String id;      //数字id
    private boolean isManage = true;
    private String level;
    private String isAdmin; //是否是管理员(1:是 0:否)
    private String imuId;
    private String isVuser;

    public String getIsVuser()
    {
        return isVuser;
    }

    public void setIsVuser(String isVuser)
    {
        this.isVuser = isVuser;
    }

    public String getImuId() {
        return imuId;
    }

    public void setImuId(String imuId) {
        this.imuId = imuId;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public ChosenFansBean(){}

    public String getuId()
    {
        return uId;
    }

    public void setuId(String uId)
    {
        this.uId = uId;
    }

    public String getNicName()
    {
        return nicName;
    }

    public void setNicName(String nicName)
    {
        this.nicName = nicName;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public String getUphUrl()
    {
        return uphUrl;
    }

    public void setUphUrl(String uphUrl)
    {
        this.uphUrl = uphUrl;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getFocus()
    {
        return focus;
    }

    public void setFocus(String focus)
    {
        this.focus = focus;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public boolean isManage() {
        return isManage;
    }

    public void setManage(boolean manage) {
        isManage = manage;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.uId);
        dest.writeString(this.nicName);
        dest.writeString(this.sex);
        dest.writeString(this.age);
        dest.writeString(this.uphUrl);
        dest.writeString(this.status);
        dest.writeString(this.focus);
        dest.writeString(this.id);
        dest.writeByte(this.isManage ? (byte) 1 : (byte) 0);
        dest.writeString(this.level);
        dest.writeString(this.isAdmin);
        dest.writeString(this.imuId);
        dest.writeString(this.isVuser);
    }

    protected ChosenFansBean(Parcel in)
    {
        this.uId = in.readString();
        this.nicName = in.readString();
        this.sex = in.readString();
        this.age = in.readString();
        this.uphUrl = in.readString();
        this.status = in.readString();
        this.focus = in.readString();
        this.id = in.readString();
        this.isManage = in.readByte() != 0;
        this.level = in.readString();
        this.isAdmin = in.readString();
        this.imuId = in.readString();
        this.isVuser = in.readString();
    }

    public static final Parcelable.Creator<ChosenFansBean> CREATOR = new Parcelable.Creator<ChosenFansBean>()
    {
        @Override
        public ChosenFansBean createFromParcel(Parcel source)
        {
            return new ChosenFansBean(source);
        }

        @Override
        public ChosenFansBean[] newArray(int size)
        {
            return new ChosenFansBean[size];
        }
    };

    @Override
    public String toString()
    {
        return "ChosenFansBean{" +
                "uId='" + uId + '\'' +
                ", nicName='" + nicName + '\'' +
                ", sex='" + sex + '\'' +
                ", age='" + age + '\'' +
                ", uphUrl='" + uphUrl + '\'' +
                ", status='" + status + '\'' +
                ", focus='" + focus + '\'' +
                ", id='" + id + '\'' +
                ", isManage=" + isManage +
                ", level='" + level + '\'' +
                ", isAdmin='" + isAdmin + '\'' +
                ", imuId='" + imuId + '\'' +
                ", isVuser='" + isVuser + '\'' +
                '}';
    }
}

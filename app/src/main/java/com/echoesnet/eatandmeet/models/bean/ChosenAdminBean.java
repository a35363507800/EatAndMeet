package com.echoesnet.eatandmeet.models.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/10/17.
 */

public class ChosenAdminBean implements Parcelable
{
    private String uId;     // 观众uId
    private String nicName; // 观众昵称
    private String sex;     // 观众性别
    private String age;     // 观众年龄
    private String phUrl;   // 观众头像
    private String status;  // 弹窗状态 0：无 1：设置 2 取消
    private String id;      // 观众id
    private String level;   // 观众等级
    private String imuId;   // 环信Id
    private String isVuser;   // 是否是大V用户

    public String getIsVuser()
    {
        return isVuser;
    }

    public void setIsVuser(String isVuser)
    {
        this.isVuser = isVuser;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public ChosenAdminBean(){}

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

    public String getPhUrl() {
        return phUrl;
    }

    public void setPhUrl(String phUrl) {
        this.phUrl = phUrl;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getImuId() {
        return imuId;
    }

    public void setImuId(String imuId) {
        this.imuId = imuId;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
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
        dest.writeString(this.phUrl);
        dest.writeString(this.status);
        dest.writeString(this.id);
        dest.writeString(this.level);
        dest.writeString(this.imuId);
        dest.writeString(this.isVuser);
    }

    protected ChosenAdminBean(Parcel in)
    {
        this.uId = in.readString();
        this.nicName = in.readString();
        this.sex = in.readString();
        this.age = in.readString();
        this.phUrl = in.readString();
        this.status = in.readString();
        this.id = in.readString();
        this.level = in.readString();
        this.imuId = in.readString();
        this.isVuser = in.readString();
    }

    public static final Parcelable.Creator<ChosenAdminBean> CREATOR = new Parcelable.Creator<ChosenAdminBean>()
    {
        @Override
        public ChosenAdminBean createFromParcel(Parcel source)
        {
            return new ChosenAdminBean(source);
        }

        @Override
        public ChosenAdminBean[] newArray(int size)
        {
            return new ChosenAdminBean[size];
        }
    };
}

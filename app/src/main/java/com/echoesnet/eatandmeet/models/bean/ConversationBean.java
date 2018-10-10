package com.echoesnet.eatandmeet.models.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.hyphenate.chat.EMConversation;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @createDate 2017/7/21 16:11
 * @description
 */

public class ConversationBean implements Parcelable
{
    private String uId;
    private String id;
    private String hxId;
    private String conversationId;
    private String nickName;
    private String gender;
    private String age;
    private String level;
    private String headImage;
    private String remark;
    private String isVUser;
    private long time;
    private String lastMsg;
    private int unreadMsgNumber;
    private boolean msgState;//消息状态1：错误 0正常
    private boolean helloMsg;
    private EMConversation.EMConversationType type;

    public String getuId()
    {
        return uId;
    }

    public String getConversationId()
    {
        return conversationId;
    }

    public String getNickName()
    {
        return nickName;
    }

    public String getGender()
    {
        return gender;
    }

    public String getAge()
    {
        return age;
    }

    public String getLevel()
    {
        return level;
    }

    public String getHeadImage()
    {
        return headImage;
    }

    public long getTime()
    {
        return time;
    }

    public String getLastMsg()
    {
        return lastMsg;
    }

    public int getUnreadMsgNumber()
    {
        return unreadMsgNumber;
    }

    public boolean getMsgState()
    {
        return msgState;
    }

    public EMConversation.EMConversationType getType()
    {
        return type;
    }

    public void setuId(String uId)
    {
        this.uId = uId;
    }

    public void setConversationId(String conversationId)
    {
        this.conversationId = conversationId;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public void setHeadImage(String headImage)
    {
        this.headImage = headImage;
    }

    public void setTime(long time)
    {
        this.time = time;
    }

    public void setLastMsg(String lastMsg)
    {
        this.lastMsg = lastMsg;
    }

    public void setUnreadMsgNumber(int unreadMsgNumber)
    {
        this.unreadMsgNumber = unreadMsgNumber;
    }

    public void setMsgState(boolean msgState)
    {
        this.msgState = msgState;
    }

    public boolean isHelloMsg()
    {
        return helloMsg;
    }

    public void setHelloMsg(boolean helloMsg)
    {
        this.helloMsg = helloMsg;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public boolean isMsgState()
    {
        return msgState;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public static Creator<ConversationBean> getCREATOR()
    {
        return CREATOR;
    }

    public void setType(EMConversation.EMConversationType type)
    {
        this.type = type;
    }

    public String getIsVUser()
    {
        return isVUser;
    }

    public void setIsVUser(String isVUser)
    {
        this.isVUser = isVUser;
    }

    public String getHxId()
    {
        return hxId;
    }

    public void setHxId(String hxId)
    {
        this.hxId = hxId;
    }

    @Override
    public String toString()
    {
        return "ConversationBean{" +
                "uId='" + uId + '\'' +
                ", id='" + id + '\'' +
                ", hxId='" + hxId + '\'' +
                ", conversationId='" + conversationId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", gender='" + gender + '\'' +
                ", age='" + age + '\'' +
                ", level='" + level + '\'' +
                ", headImage='" + headImage + '\'' +
                ", remark='" + remark + '\'' +
                ", isVUser='" + isVUser + '\'' +
                ", time=" + time +
                ", lastMsg='" + lastMsg + '\'' +
                ", unreadMsgNumber=" + unreadMsgNumber +
                ", msgState=" + msgState +
                ", helloMsg=" + helloMsg +
                ", type=" + type +
                '}';
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
        dest.writeString(this.id);
        dest.writeString(this.hxId);
        dest.writeString(this.conversationId);
        dest.writeString(this.nickName);
        dest.writeString(this.gender);
        dest.writeString(this.age);
        dest.writeString(this.level);
        dest.writeString(this.headImage);
        dest.writeString(this.remark);
        dest.writeString(this.isVUser);
        dest.writeLong(this.time);
        dest.writeString(this.lastMsg);
        dest.writeInt(this.unreadMsgNumber);
        dest.writeByte(this.msgState ? (byte) 1 : (byte) 0);
        dest.writeByte(this.helloMsg ? (byte) 1 : (byte) 0);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    }

    public ConversationBean()
    {
    }

    protected ConversationBean(Parcel in)
    {
        this.uId = in.readString();
        this.id = in.readString();
        this.hxId = in.readString();
        this.conversationId = in.readString();
        this.nickName = in.readString();
        this.gender = in.readString();
        this.age = in.readString();
        this.level = in.readString();
        this.headImage = in.readString();
        this.remark = in.readString();
        this.isVUser = in.readString();
        this.time = in.readLong();
        this.lastMsg = in.readString();
        this.unreadMsgNumber = in.readInt();
        this.msgState = in.readByte() != 0;
        this.helloMsg = in.readByte() != 0;
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : EMConversation.EMConversationType.values()[tmpType];
    }

    public static final Parcelable.Creator<ConversationBean> CREATOR = new Parcelable.Creator<ConversationBean>()
    {
        @Override
        public ConversationBean createFromParcel(Parcel source)
        {
            return new ConversationBean(source);
        }

        @Override
        public ConversationBean[] newArray(int size)
        {
            return new ConversationBean[size];
        }
    };
}

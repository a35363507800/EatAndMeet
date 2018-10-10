package com.echoesnet.eatandmeet.utils.redPacket;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/7/26.
 */
public class RedPacketInfo implements Parcelable
{
    public String fromUserId;
    public String toUserId;
    public String fromUserUid;
    public String toUserUid;
    public String fromNickName;
    public String toNickName;
    public String toRemark;
    public String fromAvatarUrl;
    public String toAvatarUrl;
    public String moneyAmount;
    public String moneyGreeting;
    public String payPwd = "";
    public String moneyID;
    public String date;
    public String totalMoney;
    public String moneyMessage;
    public int totalCount;
    public int takenCount;
    public int status;
    public String tradeNo = "";
    public String moneyMsgDirect = "";
    public int itemType;
    public String toGroupId = "";
    public int chatType;
    public String groupMoneyType = "";
    public int groupMemberCount;
    public boolean isBest = false;
    public String myAmount = "";
    public String timeLength = "";
    public String takenMoney = "";
    public int bestCount = 0;
    public static final Creator<RedPacketInfo> CREATOR =new RedPacketInfoCreator();
    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.fromUserId);
        dest.writeString(this.fromUserUid);
        dest.writeString(this.toUserId);
        dest.writeString(this.toUserUid);
        dest.writeString(this.fromNickName);
        dest.writeString(this.toNickName);
        dest.writeString(this.toRemark);
        dest.writeString(this.fromAvatarUrl);
        dest.writeString(this.toAvatarUrl);
        dest.writeString(this.moneyAmount);
        dest.writeString(this.moneyGreeting);
        dest.writeString(this.payPwd);
        dest.writeString(this.moneyID);
        dest.writeString(this.date);
        dest.writeString(this.totalMoney);
        dest.writeString(this.moneyMessage);
        dest.writeInt(this.totalCount);
        dest.writeInt(this.takenCount);
        dest.writeInt(this.status);
        dest.writeString(this.tradeNo);
        dest.writeString(this.moneyMsgDirect);
        dest.writeInt(this.itemType);
        dest.writeString(this.toGroupId);
        dest.writeInt(this.chatType);
        dest.writeString(this.groupMoneyType);
        dest.writeInt(this.groupMemberCount);
        dest.writeByte((byte)(this.isBest?1:0));
        dest.writeString(this.myAmount);
        dest.writeString(this.timeLength);
        dest.writeString(this.takenMoney);
        dest.writeInt(this.bestCount);
    }

    public RedPacketInfo() {
    }

    protected RedPacketInfo(Parcel var1) {
        this.fromUserId = var1.readString();
        this.fromUserUid=var1.readString();
        this.toUserId = var1.readString();
        this.toUserUid=var1.readString();
        this.fromNickName = var1.readString();
        this.toNickName = var1.readString();
        this.toRemark = var1.readString();
        this.fromAvatarUrl = var1.readString();
        this.toAvatarUrl = var1.readString();
        this.moneyAmount = var1.readString();
        this.moneyGreeting = var1.readString();
        this.payPwd = var1.readString();
        this.moneyID = var1.readString();
        this.date = var1.readString();
        this.totalMoney = var1.readString();
        this.moneyMessage=var1.readString();
        this.totalCount = var1.readInt();
        this.takenCount = var1.readInt();
        this.status = var1.readInt();
        this.tradeNo = var1.readString();
        this.moneyMsgDirect = var1.readString();
        this.itemType = var1.readInt();
        this.toGroupId = var1.readString();
        this.chatType = var1.readInt();
        this.groupMoneyType = var1.readString();
        this.groupMemberCount = var1.readInt();
        this.isBest = var1.readByte() != 0;
        this.myAmount = var1.readString();
        this.timeLength = var1.readString();
        this.takenMoney = var1.readString();
        this.bestCount = var1.readInt();
    }

    @Override
    public String toString()
    {
        return "RedPacketInfo{" +
                "fromUserId='" + fromUserId + '\'' +
                ", toUserId='" + toUserId + '\'' +
                ", fromUserUid='" + fromUserUid + '\'' +
                ", toUserUid='" + toUserUid + '\'' +
                ", fromNickName='" + fromNickName + '\'' +
                ", toNickName='" + toNickName + '\'' +
                ", toRemark='" + toRemark + '\'' +
                ", fromAvatarUrl='" + fromAvatarUrl + '\'' +
                ", toAvatarUrl='" + toAvatarUrl + '\'' +
                ", moneyAmount='" + moneyAmount + '\'' +
                ", moneyGreeting='" + moneyGreeting + '\'' +
                ", payPwd='" + payPwd + '\'' +
                ", moneyID='" + moneyID + '\'' +
                ", date='" + date + '\'' +
                ", totalMoney='" + totalMoney + '\'' +
                ", moneyMessage='" + moneyMessage + '\'' +
                ", totalCount=" + totalCount +
                ", takenCount=" + takenCount +
                ", status=" + status +
                ", tradeNo='" + tradeNo + '\'' +
                ", moneyMsgDirect='" + moneyMsgDirect + '\'' +
                ", itemType=" + itemType +
                ", toGroupId='" + toGroupId + '\'' +
                ", chatType=" + chatType +
                ", groupMoneyType='" + groupMoneyType + '\'' +
                ", groupMemberCount=" + groupMemberCount +
                ", isBest=" + isBest +
                ", myAmount='" + myAmount + '\'' +
                ", timeLength='" + timeLength + '\'' +
                ", takenMoney='" + takenMoney + '\'' +
                ", bestCount=" + bestCount +
                '}';
    }
}

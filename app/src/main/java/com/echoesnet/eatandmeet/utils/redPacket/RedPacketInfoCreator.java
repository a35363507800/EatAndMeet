package com.echoesnet.eatandmeet.utils.redPacket;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/7/26.
 */
public class RedPacketInfoCreator implements Parcelable.Creator<RedPacketInfo>
{
    @Override
    public RedPacketInfo createFromParcel(Parcel source)
    {
        return new RedPacketInfo(source);
    }

    @Override
    public RedPacketInfo[] newArray(int size)
    {
        return new RedPacketInfo[size];
    }
}

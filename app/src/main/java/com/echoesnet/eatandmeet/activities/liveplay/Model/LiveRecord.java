package com.echoesnet.eatandmeet.activities.liveplay.Model;

import com.echoesnet.eatandmeet.models.bean.LiveEnterRoomBean;
import com.echoesnet.eatandmeet.models.bean.TXIMChatEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author yang
 * @version 1.0
 * @modifier ben
 * @createDate 2017/4/3
 * @description 直播间 model 层，理论上直播过程中的数据都保存于此；
 * 为了保证进入房间后的显示效果；进房间前保证此类内部实例数据完整；
 */
public class LiveRecord extends BaseRecord
{
    private static final String TAG = LiveRecord.class.getSimpleName();
    public static final String KEY_ENTERROOM_EH = "LiveRecord";
    public static final int ROOM_MODE_HOST = 1;
    public static final int ROOM_MODE_MEMBER = 2;
    private LiveEnterRoomBean enterRoom4EH;  // liveC/enterroom  接口；必要数据；先拉接口，再进房间；

    private String roomId;//房间Id的,腾讯：6位数字，也是我们系统的ID；
    private int modeOfRoom;
    private String hxChatRoomId;//环信聊天室Id
    private int watchHighCount;
    private String vedioName;//ucloud 录播文件名

    public String getVedioName()
    {
        return vedioName;
    }

    public void setVedioName(String vedioName)
    {
        this.vedioName = vedioName;
    }

    public int getWatchHighCount()
    {
        return watchHighCount;
    }

    public void setWatchHighCount(int watchHighCount)
    {
        this.watchHighCount = watchHighCount;
    }

    public String getRoomId()
    {
        return roomId;
    }

    public void setRoomId(String roomId)
    {
        this.roomId = roomId;
    }

    public int getModeOfRoom()
    {
        return modeOfRoom;
    }

    public void setModeOfRoom(int modeOfRoom)
    {
        this.modeOfRoom = modeOfRoom;
    }

    public LiveEnterRoomBean getEnterRoom4EH()
    {
        if (enterRoom4EH == null)
            return enterRoom4EH = new LiveEnterRoomBean();
        return enterRoom4EH;
    }

    public void setEnterRoom4EH(LiveEnterRoomBean enterRoom4EH)
    {
        this.enterRoom4EH = enterRoom4EH;
    }


    private List<TXIMChatEntity> listMessage;

    public List<TXIMChatEntity> getListMessage()
    {
        return listMessage;
    }

    public void initRoomLayerData()
    {
        listMessage = new ArrayList<>();
    }

    public String getHxChatRoomId()
    {
        return hxChatRoomId;
    }

    public void setHxChatRoomId(String hxChatRoomId)
    {
        this.hxChatRoomId = hxChatRoomId;
    }

    public void flush()
    {
        listMessage.clear();

    }
}

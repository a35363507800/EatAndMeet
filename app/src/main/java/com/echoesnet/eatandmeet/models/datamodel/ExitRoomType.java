package com.echoesnet.eatandmeet.models.datamodel;

/**
 * 直播退出房间枚举
 * Created by an on 2017/6/10 0010.
 */

public enum ExitRoomType
{
    NORMAL,
    CONFLICT, //被踢
    PASSIVE//被动,被后台关闭
}

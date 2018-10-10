package com.echoesnet.eatandmeet.models.datamodel;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author       ben
 * @modifier
 * @createDate   2017/5/13
 * @version      1.0
 * @description  直播中消息类型枚举
 */
public enum LiveMsgType
{
    NormalText(1),//普通消息
    EnterRoom(2),//进入房间
    BigGift(3),//发送大礼物
    SmallGift(4),//发送小礼物
    FocusHost(5),//关注主播
    ParseHost(6),//给主播点赞
    ReceiveRedPacket(7),//领取其他人的红包
    SendRedPacket(8),//发红包
    ShutUp(9),//被禁言
    NotShutUp(10),//被解除禁言
    Admin(11),//被设置为方管
    NotAdmin(12),//被解除方管
    Declaration(13),//系统申明
    DeclarationGoldenLight(14),//进入特效
    ReceiveRedToSendPacket(15);//my red packet is received by others

    private int num=1;
    LiveMsgType(int num)
    {
        this.num=num;
    }

    public int getNum()
    {
        return this.num;
    }
}

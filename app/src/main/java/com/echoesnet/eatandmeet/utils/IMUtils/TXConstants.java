package com.echoesnet.eatandmeet.utils.IMUtils;


import com.tencent.av.sdk.AVRoomMulti;

/**
 * 静态函数
 */
public class TXConstants
{
    public static final int AVIMCMD_MULTI = 2048;                                                          // 多人互动消息类型
    public static final int AVIMCMD_MUlTI_HOST_INVITE = AVIMCMD_MULTI + 1;                                  // 多人主播发送邀请消息, C2C消息
    public static final int AVIMCMD_MULTI_CANCEL_INTERACT = AVIMCMD_MUlTI_HOST_INVITE + 1;                   // 主播发起下麦操作，Group消息，带断开者的imUsreid参数
    public static final int AVIMCMD_MUlTI_JOIN = AVIMCMD_MULTI_CANCEL_INTERACT + 1;                        // 多人互动方收到AVIMCMD_Multi_Host_Invite多人邀请后，同意，C2C消息
    public static final int AVIMCMD_MUlTI_REFUSE = AVIMCMD_MUlTI_JOIN + 1;                                     // 多人互动方收到AVIMCMD_Multi_Invite多人邀请后，拒绝，C2C消息
    public static final int AVIMCMD_MUlTI_HOST_CLOSE_INVITE = AVIMCMD_MUlTI_REFUSE + 1;                          // 主播取消连麦邀请，C2C消息
    public static final int AVIMCMD_MUlTI_HOST_CLOSE_INVITE_TIMEOUT = AVIMCMD_MUlTI_HOST_CLOSE_INVITE + 1;      // 主播超时取消连麦，C2C消息
    public static final int AVIMCMD_MUlTI_MEMBER_UPROLE_FAIL_NOTIFY = AVIMCMD_MUlTI_HOST_CLOSE_INVITE_TIMEOUT + 1;      // 观众上麦失败，通知主播 C2C消息
    public static final int AVIMCMD_MULTI_MEMBER_CANCEL_INTERACT = AVIMCMD_MUlTI_MEMBER_UPROLE_FAIL_NOTIFY + 1;      // 连麦观众发起下麦操作 Group消息

    public static final int AVIMCMD_Text = -1;                                               // 普通的聊天消息
    public static final int AVIMCMD_None = AVIMCMD_Text + 1;                                 // 无事件
    public static final int AVIMCMD_EnterLive = AVIMCMD_None + 1;                            // 用户加入直播, Group消息  1
    public static final int AVIMCMD_ExitLive = AVIMCMD_EnterLive + 1;                        // 用户退出直播, Group消息  2
    public static final int AVIMCMD_Focus = AVIMCMD_ExitLive + 1;                            // 关注主播,Group消息  3
    public static final int AVIMCMD_Host_Leave = AVIMCMD_Focus + 1;                          // 主播离开, Group消息 ： 4
    public static final int AVIMCMD_Host_Back = AVIMCMD_Host_Leave + 1;                      // 主播回来, Group消息 ： 5
    public static final int AVIMCMD_Host_Close = AVIMCMD_Host_Back + 1;                      // 主播关闭直播, Group消息 ： 6
    public static final int AVIMCMD_Host_ShutUp_On = AVIMCMD_Host_Close + 1;                 // 主播禁言某人, Group消息 ： 7
    public static final int AVIMCMD_Host_ShutUp_Off = AVIMCMD_Host_ShutUp_On + 1;            // 主播解除禁言某人, Group消息 ： 8
    public static final int AVIMCMD_FakeMember_Enter = AVIMCMD_Host_ShutUp_Off + 1;          // 机器人用户加入, Group消息 ： 9
    public static final int AVIMCMD_FakeMember_Leave = AVIMCMD_FakeMember_Enter + 1;         // 机器人用户离开,Group消息 ： 10
    public static final int AVIMCMD_Member_Enter = AVIMCMD_FakeMember_Leave + 1;             // 真用户加入时，Group消息 ： 11
    public static final int AVIMCMD_Praise = AVIMCMD_Member_Enter + 1;                       // 点赞飘心 Group消息 12
    public static final int AVIMCMD_Praise_Msg = AVIMCMD_Praise + 1;                         // 点赞消息 Group消息 13
    public static final int AVIMCMD_Booty_Call = AVIMCMD_Praise_Msg + 1;                     // 约炮信息 C2C消息 14
    public static final int AVIMCMD_Send_Barrage = AVIMCMD_Booty_Call + 1;                   // 弹幕消息 Group消息 15
    public static final int AVIMCMD_ROOM_ADMIN = AVIMCMD_Send_Barrage + 1;                   // 设置房管 C2C消息 16
    public static final int AVIMCMD_ROOM_ADMIN_CANCEL = AVIMCMD_ROOM_ADMIN + 1;              // 取消房管 C2C消息 17
    public static final int AVIMCMD_SEND_RED_PACKET = AVIMCMD_ROOM_ADMIN_CANCEL + 1;         // 群发红包 Group消息 18
    public static final int AVIMCMD_NOTIFY_ROOM_ADMIN = AVIMCMD_SEND_RED_PACKET + 1;         // 通知群内设置某人为房管  Group消息 19
    public static final int AVIMCMD_NOTIFY_ROOM_ADMIN_CANCEL = AVIMCMD_NOTIFY_ROOM_ADMIN + 1;// 通知群内取消某人的房管 Group消息 20
    public static final int AVIMCMD_NOTIFY_RED_HINT = AVIMCMD_NOTIFY_ROOM_ADMIN_CANCEL + 1;  // 领取红包后文字提示 C2C消息 21
    public static final int AVIMCMD_ADMIN_SHUTUP_ON = AVIMCMD_NOTIFY_RED_HINT + 1;           // 管理员禁言某人 Group消息 22
    public static final int AVIMCMD_ADMIN_SHUTUP_OFF = AVIMCMD_ADMIN_SHUTUP_ON + 1;          // 管理员解除禁言某人 Group消息 23
    public static final int AVIMCMD_SEND_GIFT = AVIMCMD_ADMIN_SHUTUP_OFF + 1;                // 发礼物 Group消息 24
    public static final int AVIMCMD_NOTIFY_RED_HINT_GROUP = AVIMCMD_SEND_GIFT + 1;                // 领取红包后文字提示 Group消息 25
    public static final int AVIMCMD_GAME_INVITATION = AVIMCMD_NOTIFY_RED_HINT_GROUP + 1;                // 直播间游戏邀请 Group消息 26
    public static final int AVIMCMD_GAME_INVITE_REJECT = AVIMCMD_GAME_INVITATION + 1;                // 直播间游戏拒绝邀请 Group消息 27
    public static final int AVIMCMD_GAME_INVITE_ACCEPT = AVIMCMD_GAME_INVITE_REJECT + 1;                // 直播间游戏接受邀请 Group消息 28
    public static final int AVIMCMD_DIFFER_STAR_COUNT = AVIMCMD_GAME_INVITE_ACCEPT + 1;                // 主播与上一名的星光值差距 Group消息 29
    public static final int AVIMCMD_STAR_FREE_GIFT = AVIMCMD_DIFFER_STAR_COUNT + 1;                // 用户领取免费礼物 Group消息 30


    public static final String CMD_KEY = "userAction";
    public static final String CMD_PARAM = "actionParam";


    public static final long HOST_AUTH = AVRoomMulti.AUTH_BITS_DEFAULT;//权限位；默认值是拥有所有权限。
    public static final long VIDEO_MEMBER_AUTH = AVRoomMulti.AUTH_BITS_DEFAULT;//权限位；默认值是拥有所有权限。
    public static final long NORMAL_MEMBER_AUTH = AVRoomMulti.AUTH_BITS_JOIN_ROOM | AVRoomMulti.AUTH_BITS_RECV_AUDIO | AVRoomMulti.AUTH_BITS_RECV_CAMERA_VIDEO | AVRoomMulti.AUTH_BITS_RECV_SCREEN_VIDEO;


    public static final String HOST_ROLE = "Host1";
    public static final String VIDEO_MEMBER_ROLE = "VideoMember";
    public static final String NORMAL_MEMBER_ROLE = "NormalMember";

    //腾讯个人资料自定义字段
    public static final String TX_CUSTOM_INFO_1 = "Tag_Profile_Custom_Str1";
    public static final String TX_CUSTOM_INFO_2 = "Tag_Profile_Custom_Str2";
    public static final String TX_CUSTOM_INFO_1_KEY_LEVEL = "level";
    public static final String TX_CUSTOM_INFO_1_KEY_SIGN = "sign";
    public static final String TX_CUSTOM_INFO_1_KEY_VUSER = "vuser";

    // 直播界面各种信息颜色值
    // 文本内容颜色
    public static final String ENTER_ROOM_MSG_CONTENT_COLOR = "#44cf89";
    // 昵称内容颜色
    public static final String ENTER_ROOM_NAME_COLOR = "#f2a437";
    // 系统内容颜色
    public static final String ENTER_ROOM_SYSTEM_COLOR = "#6c19f6";
    // 聊天内容颜色
    public static final String ENTER_ROOM_SEND_MSG_COLOR = "#ffffff";
    // 红包内容颜色
    public static final String ENTER_ROOM_SEND_RED_COLOR = "#ff3657";
}

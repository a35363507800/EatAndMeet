package com.echoesnet.eatandmeet.utils;

/**
 * Created by wangben on 2016/7/18.
 */
public class EamConstant
{
    public static final String PACKET_NAME = "com.echoesnet.eatandmeet.";
    public static final String BAIDU_PUSH_KEY = "eukKGyHtNz6RMUtaaO0xWkKPRoNnv1zz";

    public static final String SHARESDK_APPKEY = "15b9f5b376065";
    public static final String SHARESDK_APPSECRET = "23f55e7494c6e6bc762683defed8514d";

    public static final String FRAGMENTS_TAG = "android:support:fragments";//用来解决系统回收activity时，自动保存fragment造成崩溃的问题--wb

    public static final int EAM_RESULT_YES = 1;
    public static final int EAM_RESULT_NO = 0;
    public static final int EAM_CONFIRM_ORDER_REQUEST_CODE = 10;
    public static final int EAM_GIFT_REQUEST_CODE = 11;
    public static final int EAM_ORDER_DETAIL_REQUEST_CODE = 12;
    public static final int EAM_TAKE_PHOTO_FROM_GALLERY = 13;
    public static final int EAM_TAKE_PHOTO_FROM_CAMERA = 14;
    public static final int EAM_TAKE_PHOTO_FROM_CAMERA2 = 16;
    public static final int EAM_TAKE_PICTURE = 15;
    public static final int EAM_OPEN_IMAGE_PICKER = 17;
    public static final int EAM_OPEN_TASK = 18;
    public static final int EAM_OPEN_CUT_TIME = 19;

    /**
     * 表示从哪里打开的分享直播
     */
    public static final int EAM_LIVE_SHARED_OPEN_SOURCE = 19;
    public static final int REQUEST_CODE_AND_EMOJI = 20;
    public static final int EAM_OPEN_SELECT_LOCATION = 21;
    public static final int EAM_OPEN_SEARCH_LOCATION = 22;
    public static final int EAM_OPEN_RECORD_VIDEO = 23;
    public static final int EAM_OPEN_TRENDS_PUBLISH = 24;
    public static final int EAM_OPEN_TRENDS_DETAIL = 25;
    public static final int EAM_OPEN_TRENDS_PLAY_VIDEO = 26;
    public static final int EAM_OPEN_RELATION = 27;
    public static final int EAM_OPEN_ARTICAL_DETAIL = 28;
    public static final int EAM_OPEN_CNEW_USER_INFO = 29;
    public static final int EAM_OPEN_RED_PACKET_SHOW = 30;
    public static final int EAM_OPEN_SHARE_COLUMN = 31;

    public static final int EAM_NOTIFY_MSG_CENTER = 100;
    public static final int EAM_NOTIFY_RED_PACKET = 101;
    public static final int EAM_NOTIFY_WELGIFT_OVERDUE = 102;
    public static final int EAM_NOTIFY_APPLY_FRIEND = 103;
    public static final int EAM_NOTIFY_RED_OVERDUE = 104;
    public static final int EAM_NOTIFY_FRIEND_DYNAMICS = 105;
    public static final int EAM_NOTIFY_SYS_CENTER = 106;
    public static final int EAM_NOTIFY_LIVE_PLAY = 107;
    public static final int EAM_NOTIFY_BALANCE_DETAIL = 108;
    public static final int EAM_NOTIFY_REAL_NAME = 109;
    public static final int EAM_NOTIFY_DATE_INFO_USER = 110;
    public static final int EAM_NOTIFY_DEFAULT_CODE = 111;
    public static final int EAM_NOTIFY_DATE_INFO_ANCHOR = 112;
    public static final int EAM_NOTIFY_TREND_DETAIL = 113;
    public static final int EAM_NOTIFY_RES_DETAIL = 114;
    public static final int EAM_NOTIFY_COLUMN_REMIND = 115;
    public static final int EAM_NOTIFY_GAME_ACCEPT = 116;

    public static final int EAM_NOTIFY_HP1 = 117;
    public static final int EAM_NOTIFY_HP2 = 118;

    public static final int EAM_OPEN_RES_ACT = 119;
    public static final int EAM_OPEN_HP_ACT = 120;

    public static final int EAM_VUSER = 130;

    public static final String DEVICE_TYPE = "A";//A : android  I: ios


    public static final String EAM_ORDER_DETAIL_OPEN_SOURCE = "order_detail_open_source";
    public static final String EAM_SHOW_IMG_URLS = "show_img_urls";
    public static final String EAM_SHOW_IMG_CURRENT_ITEM = "currentId";
    public static final String EAM_SHOW_IMG_LOCATION_X = "locationX";
    public static final String EAM_SHOW_IMG_LOCATION_Y = "locationY";
    public static final String EAM_SHOW_IMG_WIDTH = "width";
    public static final String EAM_SHOW_IMG_HEIGHT = "height";
    public static final String EAM_SHOW_IMG_WHERE_FROM = "fromEdit";
    public static final String EAM_SHOW_IMG_HEAD_PIC = "headPic";
    public static final int EAM_OPEN_DATEHOUSE_DETAIL = 555;
    /**
     * 表示从哪里打开的退款详情页
     */
    public static final String EAM_REFUND_DETAIL_OPEN_SOURCE = "order_detail_open_source";
    /**
     * 表示从哪里打开的实名认证页
     */
    public static final String EAM_VERIFY_ID_OPEN_SOURCE = "verify_id_open_source";
    /**
     * 表示从哪里打开的设置支付密码页
     */
    public static final String EAM_SET_PAY_PW_OPEN_SOURCE = "set_pay_password_open_source";
    /**
     * 表示从哪里返回的邻座页
     */
    public static final String EAM_AROUND_OPEN_SOURCE = "set_around_open_source";
    /**
     * 表示从哪里返回的餐厅详情页
     */
    public static final String EAM_ORDER_RECORD_DETAIL_OPEN_SOURCE = "set_order_record_detail_open_source";
    /**
     * 表示从哪里打开的的存值详情页
     */
    public static final String EAM_RECHARGE_RESULT_OPEN_SOURCE = "set_recharge_result_open_source";


    //更新余额broadcast 的广播anction
    public static final String ACTION_UPDATE_BALANCE = PACKET_NAME + "action.update.balance";
    //更新余额broadcast 的广播anction
    public static final String ACTION_UPDATE_USER_BALANCE = PACKET_NAME + "action.update.userinfo.balance";
    //更新余额broadcast 的广播anction
    public static final String ACTION_UPDATE_USER_MSG = PACKET_NAME + "action.update.userinfo.msg";
    public static final String ACTION_UPDATE_USER_INFO = PACKET_NAME + "action.update.userinfo.detail";


    //更新信息中心上红点的广播
    public static final String ACTION_UPDATE_CENTER_MSG = PACKET_NAME + "action.update.infocenter.msg";

    public static final String ACTION_ANSWER_PHONE = PACKET_NAME + "action.update.answer.phone";
    public static final String ACTION_HANGUP_PHONE = PACKET_NAME + "action.update.hangup.phone";
    /**
     * 密码输入完毕，即将支付的标示
     */
    public static final String EAM_PAY_PENDING = "redPacket";

    public static final String EAM_PAY_CLUBPAY = "clubPacket";

    public static final String EAM_USERINFO_PAGE_OPEN_SOURCE = "newFriend";

    public static final String EAM_MESSAGE_TYPE_IS_SHARE_LIVE = "isLiveShare";

    public static final String EAM_HX_CMD_START_UPLOAD_LOCATION = "start_upload_location";

    /**
     * 约主播的红点
     */
    public static final String EAM_HX_CMD_RECEIVE_RED_REMIND = "receive_red_remind";

    public static final String EAM_HX_CMD_RECEIVE_RED_MY_REMIND = "receive_my_red_remind";

    public static final String EAM_HX_RECEIVE_RED_HOME = "receive_red_home_msg";

    public static final String EAM_HX_RECEIVE_BIGV_RED_HOME = "receive_red_bigv_home_msg";

    /**
     * 同意游戏邀请  关闭直播间
     */
    public static final String EAM_HX_RECEIVE_CLOSE_LIVE = "receive_game_close_invite_msg";
    /**
     *
     */
    public static final String EAM_HX_RECEIVE_HIDE_FOCUS = "receive_game_hide_focus_msg";

    /**
     * 成就的红点
     */
    public static final String EAM_HX_CMD_SUCC_RED_REMIND = "succ_red_remind";
    /**
     * 任务的红点
     */
    public static final String EAM_HX_CMD_TASK_RED_REMIND = "task_red_remind";

    /**
     * 更新动态的数量
     */
    public static final String EAM_REFRESH_MSG = "task_trend_remind";

    /**
     * 红包通知
     */
    public static final String EAM_SINGLES_DAY = "singlesDay";


    /**
     * 更新大V红点
     */
    public static final String EAM_REFRESH_BIGV_MSG = "task_bigv_remind";


    /**
     * 更新动态互动通知的数量
     */
    public static final String EAM_REFRESH_TREND_MSG = "task_trend_msg";
    /**
     * 更新通知的数量
     */
    public static final String EAM_REFRESH_SYS_MSG = "task_sys_remind";

    /**
     * 忽略系统通知的数量
     */
    public static final String EAM_REFRESH_IGNORE_SYS_MSG = "task_sys_ignore";


    /**
     * 餐厅列表topbar
     */
    public static final String EAM_REFRESH_FRG_TOP_BAR = "res_top_bar";

    public static final String EAM_REFRESH_IGNORE_BIGV_MSG = "big_v_ignore";


    public static final String ACTION_SHOW_STATE_POP = "action_show_state_pop";

    public static final String EAM_INTENT_TX_ID = "eam_intent_tx_id";
    public static final String EAM_INTENT_UID = "eam_intent_uid";
    public static final String EAM_INTENT_HX_ID = "eam_intent_hx_id";

    public static final String EAM_ACTIVITY_KILLED_BY_OS = "act_be_killed_by_os";
    public static final String EAM_ACTIVITY_STATUS = "act_be_killed";

    public static final String EAM_STATUS_NORMAL = "normal";
    public static final String EAM_STATUS_CONFLICT = "conflict";


    public static final String MESSAGE_ATTR_VOICE_TEMP_ID = "voice_temp_message";


    //****************直播中环信消息解析常量 stat**************************
    public static final String EAM_LIVE_ATTR_UID = "eam_live_hx_uid";
    public static final String EAM_LIVE_ATTR_ID = "eam_live_hx_id";
    public static final String EAM_LIVE_ATTR_NICKNAME = "eam_live_hx_nickname";
    public static final String EAM_LIVE_ATTR_HEADIMAGE = "eam_live_hx_headimage";
    public static final String EAM_LIVE_ATTR_LEVEL = "eam_live_hx_level";
    public static final String EAM_LIVE_ATTR_SIGN = "eam_live_hx_isSign";
    public static final String EAM_LIVE_ATTR_VUSER = "eam_live_hx_isVuser";
    public static final String EAM_LIVE_ATTR_PARAM = "eam_live_hx_action_param";
    //****************end**************************
    //****************聊天中环信消息解析常量 stat**************************
    public static final String EAM_CHAT_ATTR_ID = "eam_chat_id";
    public static final String EAM_CHAT_ATTR_UID = "eam_chat_uid";
    public static final String EAM_CHAT_ATTR_NICKNAME = "eam_chat_nickname";
    public static final String EAM_CHAT_ATTR_HEADIMAGE = "eam_chat_headimage";
    public static final String EAM_CHAT_ATTR_LEVEL = "eam_chat_level";
    public static final String EAM_CHAT_ATTR_GENDER = "eam_chat_gender";
    public static final String EAM_CHAT_ATTR_AGE = "eam_chat_age";
    public static final String EAM_CHAT_ATTR_REMARK = "eam_chat_remark";
    public static final String EAM_CHAT_ATTR_VUSER = "eam_chat_is_vuser";//大V 标志
    public static final String EAM_CHAT_ATTR_HELLO = "eam_chat_is_hello";
    public static final String EAM_CHAT_ATTR_VIDEO_TYPE = "eam_chat_video_type";
    public static final String EAM_CHAT_ATTR_DEVICE_TYPE = "eam_chat_device_type";
    public static final String EAM_CHAT_ATTR_APPEND_MESSAGE = "eam_chat_is_append_message";
    public static final String EAM_CHAT_ATTR_VOICE_TEMP_MESSAGE = "eam_chat_is_voice_temp_message";
    public static final String EAM_CHAT_ATTR_EXPRESSION_NAME = "em_expression_name";
    public static final String EAM_CHAT_ATTR_VIDEO_THUMBNAIL_WIDTH = "em_video_thumbnail_width";//视频缩略图 宽度
    public static final String EAM_CHAT_ATTR_VIDEO_THUMBNAIL_HEIGHT = "em_video_thumbnail_height";//视频缩略图 高度
    public static final String EAM_CHAT_ATTR_RECALL_MSG_ID = "eam_chat_recall_msg_id";//视频缩略图 高度
    //地址
    public static final String MESSAGE_ATTR_ADDRESS_NAME = "em_location_address_name";// 地理位置的名字
    public static final String MESSAGE_ATTR_DETAIL_ADDRESS = "em_location_detailed_address";//详细的地理位置
    //游戏
    public static final String EAM_CHAT_ATTR_GAME_TITLE = "em_game_invite_title";
    public static final String EAM_CHAT_ATTR_GAME_DES = "em_game_invite_des";
    public static final String EAM_CHAT_ATTR_GAME_ID = "em_game_invite_id";
    public static final String EAM_CHAT_ATTR_GAME_URL = "em_game_invite_url";
    /**
     * 消息状态 0：发送中 1：接受 2：拒绝 3：对战中 4：过期
     * *1  发送 ：0  ；   *3 拒绝  ：2  ；   4  过期  ：4   ；   *5 接受 ： 1 ；   6 进行中 ：3
     */
    public static final String EAM_CHAT_ATTR_GAME_STATE = "emgametype";
    /** 发送 */
    public static final String EAM_ATTR_GAME_STATE_SEND = "1";
    /** 接受 */
    public static final String EAM_ATTR_GAME_STATE_ACCEPT = "5";
    /** 拒绝 */
    public static final String EAM_ATTR_GAME_STATE_REFUSE = "3";
    /** 过期 */
    public static final String EAM_ATTR_GAME_STATE_OVERDUE = "4";
    /** 进行中 */
    public static final String EAM_ATTR_GAME_STATE_ONGOING = "6";


    /**
     * 互相操作的 消息id
     */
    public static final String EAM_CHAT_ATTR_GAME_MSG_ID = "em_game_invite_message_id";
    public static final String EAM_CHAT_ATTR_GAME_MATCH_ID = "em_game_invite_matchId";

    //分享消息


    //****************end**************************

    public static final String EAM_BRD_ACTION_SET_ADMIN = "EAM_BRD_ACTION_SET_ADMIN";
    public static final String EAM_BRD_ACTION_CANCEL_ADMIN = "EAM_BRD_ACTION_CANCEL_ADMIN";
    public static final String EAM_BRD_ACTION_SHUTEUP_ADMIN = "EAM_BRD_ACTION_SHUTEUP_ADMIN";
    public static final String EAM_BRD_ACTION_SHUNTUP_HOST = "EAM_BRD_ACTION_SHUNTUP_HOST";
    public static final String EAM_BRD_ACTION_SHUNTUP_OFF_HOST = "EAM_BRD_ACTION_SHUNTUP_OFF_HOST";
    public static final String EAM_BRD_ACTION_SHUNTUP_OFF_ADMIN = "EAM_BRD_ACTION_SHUNTUP_OFF_ADMIN";

    public static final String EAM_NOTIFY_LIVE_CHAT_RED = "EAM_NOTIFY_LIVE_CHAT_RED";

    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";

    public static final String MESSAGE_ATTR_IS_BIG_EXPRESSION = "em_is_big_expression";
    public static final String MESSAGE_ATTR_EXPRESSION_ID = "em_expression_id";
    //表情图片的url
    public static final String MESSAGE_ATTR_EXPRESSION_URL = "em_expression_url";
    public static final String MESSAGE_ATTR_AT_MSG = "em_at_message";


    public static final String EXTRA_CHAT_TYPE = "chatType";
    public static final String EXTRA_USER_ID = "userId";
    public static final String EVENT_ADD_EMOJI = "ease_ui_add_emoji";

    //聊天中被拉黑透传消息 CMD
    public static final String EAM_CHAT_INBLACK_NOTIFY = "eam_chat_inblack_notify";
    public static final String EAM_CHAT_OUTBLACK_NOTIFY = "eam_chat_outblack_notify";

    //聊天中被更改备注
    public static final String EAM_CHAT_CHANGE_REMARK_NOTIFY = "eam_chat_change_remark_notify";

    //聊天退出 发送 消息统计清零 CMD
    public static final String EAM_CHAT_CLEAR_MSG_COUNT_NOTIFY = "eam_chat_clear_msg_count_notify";
    //消息撤回cmd
    public static final String EAM_CHAT_RECALL_MSG_NOTIFY = "eam_chat_recall_msg_notify";
    //游戏邀请拒绝cmd
    public static final String EAM_CHAT_ACCEPT_GAME_NOTIFY = "eam_chat_accept_game_notify";
    public static final String EAM_CHAT_REFUSE_GAME_NOTIFY = "eam_chat_refuse_game_notify";

    //region 看脸吃饭的错误码码表,以8888开头依次递增，让我们一起发发发发
    public static final int EAM_FILE_UNZIP_FAILED = 88881;//解压缩失败
    public static final int EAM_READ_CATCH_FAILED = 88882;//读取缓存失败
    //endregion


}

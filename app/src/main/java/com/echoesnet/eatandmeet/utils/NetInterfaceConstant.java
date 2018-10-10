package com.echoesnet.eatandmeet.utils;

import com.echoesnet.eatandmeet.BuildConfig;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;

/**
 * Created by Administrator on 2016/11/15.
 * description：存放接口的文件
 */

public class NetInterfaceConstant
{
    static
    {
        switch (BuildConfig.evnIndex)
        {
            case 1:
                NetHelper.H5_ADDRESS = "http://shangjia.echoesnet.com";
                break;
            case 2:
                NetHelper.H5_ADDRESS = "http://106.75.50.97";//测试地址/
//                NetHelper.H5_ADDRESS = "http://192.168.10.246:8080";//wangSaoPeng/
                break;
            default:
                break;
        }
    }

    private NetInterfaceConstant()
    {
    }

    //region H5页面链接接口，请使用H5作为前缀命名,大写字母加下划线
    public static final String H5_VERSION = NetHelper.H5_ADDRESS + "/h5/version.json";
    public static final String shopping_carte = NetHelper.H5_ADDRESS + "/h5/shopping-carte.html";//约会相关
    public static final String invite_send_list = NetHelper.H5_ADDRESS + "/h5/invite-send-list.html";
    public static final String invited_receive_list = NetHelper.H5_ADDRESS + "/h5/invited-receive-list.html";
    public static final String H5_TOTAL_RANK = NetHelper.H5_ADDRESS + "/h5/live_total_cash_ranking_list.html";//总榜单
    public static final String H5_THIS_RANK = NetHelper.H5_ADDRESS + "/h5/live_this_cash_ranking_list.html";//本场榜单
    public static final String H5_ACHIEVE = NetHelper.H5_ADDRESS + "/level-and-task/achievement.html";
    public static final String H5_TASK = NetHelper.H5_ADDRESS + "/level-and-task/task.html";
    public static final String level_Introduce_Page = NetHelper.H5_ADDRESS + "/invited-to-eat/receive-detail.html";//等级说明
    public static final String anchor_appointment = NetHelper.H5_ADDRESS + "/invited-to-eat/this-receive-list.html";//主播约会界面
    public static final String member_appointment = NetHelper.H5_ADDRESS + "/invited-to-eat/anchor-receive.html";//普通用户约会界面
    public static final String data_wish = NetHelper.H5_ADDRESS + "/invited-to-eat/list-wish.html";
    public static final String data_relation = NetHelper.H5_ADDRESS + "/h5/friends-list.html"; // 系统通讯录
    public static final String data_black = NetHelper.H5_ADDRESS + "/h5/black-list.html"; // 黑名单
    public static final String H5_MY_LEVEL = NetHelper.H5_ADDRESS + "/h5/my-level.html";
    public static final String H5_HOST_LEVEL = NetHelper.H5_ADDRESS + "/h5/anchor-level.html";
    public static final String data_local_tyrant = NetHelper.H5_ADDRESS + "/h5/live_tuhao_squire_ranking_list.html";//直播榜单 frg
    public static final String data_face_fan = NetHelper.H5_ADDRESS + "/h5/live_face_fans_ranking_list.html";
    public static final String data_hot_host = NetHelper.H5_ADDRESS + "/h5/live_popular_anchor_ranking_list.html";
    public static final String data_good_sale = NetHelper.H5_ADDRESS + "/h5/live_invite_anchor_ranking_list.html";
    public static final String H5_TASK_LEVEL = NetHelper.H5_ADDRESS + "/level-and-task/level-regular.html";
    public static final String H5_COLUMN_ARTICLE_DETAIL = NetHelper.H5_ADDRESS + "/h5/special-column.html";//大V
    public static final String data_query_schedule = NetHelper.H5_ADDRESS + "/h5/query-process.html";//进度查询 act
    public static final String data_sys_msg = NetHelper.H5_ADDRESS + "/h5/message-system-list.html";//系统消息 act
    public static final String sun_moon_star_share = NetHelper.H5_ADDRESS + "/game/sun-moon-star.html";//日月星分享
    //endregion

    //region 文件链接接口，请使用FILE作为前缀，大写字母加下划线
    /**
     * 大礼物版本控制文件
     */
    public static String FILE_GIFT_VERSION = CdnHelper.CDN_ORIGINAL_SITE + "giftInfo.json";//大礼物(改版 控制 文件)

    /**
     * 下载大礼物地址
     */
    public static final String FILE_BIG_GIFT_RESOURCE = CdnHelper.CDN_ORIGINAL_SITE + "bigGift.zip";

    /**
     * 下载省市区文件
     */
    public static String FILE_PROVINCE_ADDRESS_DATA = CdnHelper.CDN_ORIGINAL_SITE + "site.json";
    /**
     * 聊天敏感词 词库
     */
    public static final String FILE_SENSITIVE_WORDS = CdnHelper.CDN_ORIGINAL_SITE + "eamSensitiveWords.json";

    //endregion


    //region 后台接口，希望以后命名改为大写字母加下划线，例如USERC_PAYRED

    public static final String UserC_payRed = "UserC/payRed";

    /**
     * 月签到
     */
    public static final String CheckInC_checkIn = "CheckInC/checkIn";

    /**
     * 月签到情况
     */
    public static final String CheckInC_monthCheck = "CheckInC/monthCheck";
    /**
     * 月签到礼物
     */
    public static final String CheckInC_checkIn_v412 = "CheckInC/checkIn_v412";
    /**
     * 月签到情况新接口
     */
    public static final String CheckInC_monthCheck_v412 = "CheckInC/monthCheck_v412";

    /**
     * 本日是否已签到
     */
    public static final String CheckInC_todayCheck = "CheckInC/todayCheck";

    /**
     * 七日福利领取情况
     */
    public static final String CheckInC_sevenWeal = "CheckInC/sevenWeal";

    /**
     * 领取七日福利
     */
    public static final String CheckInC_sevenCheckIn = "CheckInC/sevenCheckIn";


    /**
     * 完善资料 赠送详情
     */
    public static final String UserC_detailPrompt = "UserC/detailPrompt";
    /**
     * 完善资料
     */
    public static final String UserC_detail = "UserC/detail";

    /**
     * 审核头像接口 未通过时用的
     */
    public static final String UserC_modifyHead = "UserC/modifyHead";
    /**
     * 保存环信账号到后台,并登陆
     */
    public static final String UserC_addImu = "UserC/addImu";
    /**
     * 保存环信账号到后台,并登陆
     */
    public static final String UserC_imuInfo = "UserC/imuInfo";
    /**
     * 根据环信用户信息查看本地用户的相关信息
     */
    public static final String UserC_imuToUser = "UserC/imuToUser";
    /**
     * 实名认证
     */
    public static final String UserC_realName = "UserC/realName";
    /**
     * 与后台已经认证过的信息对比
     */
    public static final String UserC_realNameStatus = "UserC/realNameStatus";
    /**
     * 修改余额
     */
    public static final String UserC_recharge = "UserC/recharge";

    /**
     * 调用后台，发送短信验证码
     */
    public static final String UserC_sendCodes = "UserC/sendCodes";


    /**
     * 首页资料
     */
    public static final String IndexC_index161116 = "IndexC/index161116";
    /**
     * 修改备注
     */
    public static final String NeighborC_editRemark = "NeighborC/editRemark";


    /**
     * 动态复盘版 修改备注
     */
    public static final String FriendC_editRemark = "FriendC/editRemark";
    /**
     * 使用腾讯id查询用户详情
     */
    public static final String UserC_qOthersInfoById = "UserC/qOthersInfoById";
    /**
     * 使用uid查询用户详情
     */
    public static final String UserC_qOthersInfo = "UserC/qOthersInfo";

    public static final String NeighborC_friend = "NeighborC/friend";
    /**
     * 获取版本号
     */
    public static final String UserC_version_v304 = "UserC/version_v304";
    /**
     * 获取功能开关状态
     */
    public static final String UserC_siteStat = "UserC/siteStat";
    /**
     * 获取用户禁言状态
     */
    public static final String TX_GetShutUpStatus = "v4/group_open_http_svc/get_group_shutted_uin";
    /**
     * 删除群
     */
    public static final String TX_DestroyGroup = "v4/group_open_http_svc/destroy_group";

    /**
     * 下载背景图
     */
    public static final String UserC_startup = "UserC/startup";
    /**
     * 检查礼物是否更新
     */
    public static final String LiveC_giftVersion = "LiveC/giftVersion";
    /**
     * 获取联系方式
     */
    public static final String UserC_contact = "UserC/contact";
    /**
     * 订餐消息开关
     */
    public static final String UserC_orderFlag = "UserC/orderFlag";
    /**
     * 陌生人开关
     */
    public static final String UserC_privateFlag = "UserC/privateFlag";
    /**
     * 推送消息开关
     */
    public static final String UserC_pushFlag = "UserC/pushFlag";
    /**
     * 验证支付密码是否设置
     */
    public static final String UserC_payPwd = "UserC/payPwd";
    /**
     * 验证支付密码
     */
    public static final String UserC_validPwd = "UserC/validPwd";

    /**
     * 代付按钮开关
     */
    public static final String FriendPayC_btnOnOff = "FriendPayC/btnOnOff";
    /**
     * 第三方支付
     */
    public static final String PayC_newPay = "PayC/newPay";
    /**
     * 回声支付订单
     */
    public static final String OrderC_payOrder = "OrderC/payOrder";
    /**
     * 回声支付订单
     */
    public static final String PayC_newChkPayStat = "PayC/newChkPayStat";

    /**
     * 分享代付前调用接口
     */
    public static final String OrderC_shareOrder = "OrderC/shareOrder";
    /**
     * 搜索餐厅
     */
    public static final String RestaurantC_searchRes = "RestaurantC/searchRes";


    /**
     * 搜索轰趴餐馆
     */
    public static final String HomepartyC_search = "HomepartyC/search";

    /**
     * 搜索餐厅
     */
    public static final String RestaurantC_searchResForUInfo = "RestaurantC/searchResForUInfo";
    /**
     * 我看过谁和谁看过我
     */
    public static final String UserC_history = "UserC/history";
    /**
     * 菜品详情
     */
    public static final String DishC_dishDetail = "DishC/dishDetail";
    /**
     * 是否有未读信息
     */
    public static final String MsgC_queryStat = "MsgC/queryStat";

    public static final String MsgC_diningRemind = "MsgC/diningRemind";

    public static final String UserC_starLikeAmount = "UserC/starLikeAmount";

    public static final String UserC_starLike = "UserC/starLike";
    public static final String And_UserC_starLike = "And_UserC_starLike";

    public static final String UserC_rePwd = "UserC/rePwd";

    /**
     * 余额明细
     */
    public static final String DealDetailC_balanceDetail = "DealDetailC/balanceDetail";

    public static final String DealDetailC_balanceDetailWithDetail = "DealDetailC/balanceDetailWithDetail";

    /**
     * 充值列表
     */
    public static final String DealDetailC_recharge = "DealDetailC/recharge";
    /**
     * 账户余额
     */
    public static final String UserC_balance = "UserC/balance";
    /**
     * 我的等级
     */
    public static final String UserC_myLevel = "UserC/myLevel";

    /**
     * 新版充值列表
     */
    public static final String DealDetailC_recharge_v333 = "DealDetailC/recharge_v333";

    /**
     * 获取用户信息
     */
    public static final String UserC_queryUInfo = "UserC/queryUInfo";
    /**
     * 获取我的信息
     */
    public static final String UserC_myInfo = "UserC/myInfo";
    /**
     * 获取账户余额信息
     */
    public static final String UserC_newBalance = "UserC/newBalance";

    //检查设备是否被顶
    public static final String UserC_checkDevice = "UserC/checkDevice";
    /**
     * 就餐可能邂逅的人
     */
    public static final String EncounterC_encounterPerson = "EncounterC/encounterPerson";
    /**
     * 获取菜单
     */
    public static final String DishC_dishInfo = "DishC/dishInfo";
    /**
     * 餐厅列表检索
     */
    public static final String RestaurantC_resListByES_161011 = "RestaurantC/resListByES_161011";
    /**
     * 约吃饭餐厅列表检索
     */
    public static final String RestaurantC_resListForAppo = "RestaurantC/resListForAppo";
    /**
     * 约吃饭查询主播约会
     */
    public static final String ReceiveC_queryReceive = "ReceiveC/queryReceive";
    /**
     * 编辑资料
     */
    public static final String UserC_uInfo = "UserC/uInfo";
    /**
     * 我的收藏
     */
    public static final String UserC_myCollect = "UserC/myCollect";

    public static final String USERC_MYCOLLECT_V422 = "UserC/myCollect_v422";

    /**
     * 删除收藏
     */
    public static final String UserC_delCollect = "UserC/delCollect";
    /**
     * 餐厅点赞接口
     */
    public static final String RestaurantC_rPraise = "RestaurantC/rPraise";
    /**
     * 取消餐厅点赞接口
     */
    public static final String RestaurantC_delRPraise = "RestaurantC/delRPraise";

    /**
     * 获取餐厅详情接口
     */
    public static final String RestaurantC_resInfoByrId = "RestaurantC/resInfoByrId";

    /**
     * 菜品搜索
     */
    public static final String DishC_dishSearch = "DishC/dishSearch";
    /**
     * 我的邀请码
     */
    public static final String UserC_myInCode = "UserC/myInCode";
    /**
     * 我的邀请
     */
    public static final String UserC_myInvite = "UserC/myInvite";
    /**
     * 校验是否绑定支付宝
     */
    public static final String UserC_checkAlipay = "UserC/checkAlipay";
    /**
     * 绑定支付宝
     */
    public static final String UserC_alipay = "UserC/alipay";
    /**
     * 订单详情
     */
    public static final String OrderC_orderDetail = "OrderC/orderDetail";
    /**
     * 订单列表
     */
    public static final String OrderC_order = "OrderC/order";

    /**
     * 订单列表v422
     */
    public static final String OrderC_order_v422 = "OrderC/order_v422";
    /**
     * 删除订单
     */
    public static final String OrderC_delOrder = "OrderC/delOrder";

    /**
     * 删除轰趴馆订单
     */
    public static final String HomepartyC_delOrder = "HomepartyC/delOrder";
    /**
     * 申请退款
     */
    public static final String OrderC_applyRefund = "OrderC/applyRefund";
    /**
     * 查看评价
     */
    public static final String EvalC_checkEval = "EvalC/checkEval";
    /**
     * 意见反馈
     */
    public static final String UserC_fbInfo = "UserC/fbInfo";

    public static final String NeighborC_giftFriend = "NeighborC/giftFriend";

    public static final String LiveC_roomMember = "LiveC/roomMember";

    public static final String LiveC_setPermanent = "LiveC/setPermanent";

    public static final String LiveC_getTlsSign = "LiveC/getTlsSign";

    public static final String LiveC_roomMember_v307 = "LiveC/roomMember_v402";

    public static final String LiveC_anchorList = "LiveC/anchorList";

    public static final String LiveC_searchAnchor = "LiveC/searchAnchor";

    public static final String LiveC_enterRoom = "LiveC/enterRoom";

    public static final String LiveC_getGhostUrl = "LiveC/getGhostUrl";
    /**
     * 心跳❤
     */
    public static final String HeartC_beat = "HeartC/beat";
    /**
     * 关闭房间
     */
    public static final String LiveC_closeRoom = "LiveC/closeRoom";
    /**
     * 是否显示排行榜
     */
    public static final String LiveC_swap = "LiveC/swap";

    /**
     * 主播星光活动活动排行榜
     */
    public static final String AnchorActivityC_ranking = "AnchorActivityC/ranking";
    /**
     * 闪付
     */
    public static final String OrderC_quickPay = "OrderC/quickPay";

    /**
     * 红包状态
     */
    public static final String UserC_checkRedList = "UserC/checkRedList";
    /**
     * 红包状态
     */
    public static final String UserC_checkRed = "UserC/checkRed";
    /**
     * 向后台上传某一用户的位置信息
     */
    public static final String UserC_editPosXY = "UserC/editPosXY";
    /**
     * 将绑定好的百度channelId传给后台
     */
    public static final String UserC_baiduPush = "UserC/baiduPush";

    /** /**
     * 将绑定好的百度channelId传给后台
     */
    public static final String UserC_bindXmPush = "UserC/bindXmPush";
    /**
     * 登出后台
     */
    public static final String UserC_loginOut = "UserC/loginOut";

    /**
     * 是否是朋友
     */
    public static final String NeighborC_isFriend = "NeighborC/isFriend";

    /**
     * 联系人列表
     */
    public static final String NeighborC_friendList = "NeighborC/friendList";

    /**
     * 删除列表
     */
    public static final String NeighborC_delFriend = "NeighborC/delFriend";

    /**
     * 获得搭讪的人
     */
    public static final String EncounterC_accostToPerson = "EncounterC/accostToPerson";
    /**
     * 基于当前位置获取用户周围的人及餐厅
     */
    public static final String EncounterC_newaccostToPerson = "EncounterC/newaccostToPerson";
    /**
     * 基于当前位置获取用户周围的人及餐厅
     */
    public static final String LiveC_giftList = "LiveC/giftList";
    /**
     * 基于当前位置获取用户周围的人及餐厅
     */
    public static final String EncounterC_encounterList = "EncounterC/encounterList";

    /**
     * 心动一下 喜欢  厌恶
     */
    public static final String EncounterC_encounterlike = "EncounterC/encounterlike";

    /**
     * 轰趴预定详情
     */
    public static final String HomepartyC_partyReserve = "HomepartyC/partyReserve";
    /**
     * 轰趴订单提交
     */
    public static final String HomepartyC_partyOrder = "HomepartyC/partyOrder";
    /**
     * 轰趴支付订单
     */
    public static final String HomepartyC_payment = "HomepartyC/payment";

    /**
     * 发现页推荐主播
     */
    public static final String IndexC_indexLeLive_v307 = "IndexC/indexLeLive_v307";

    /**
     * 发现页相约艳遇
     */
    public static final String IndexC_indexRecommend_v307 = "IndexC/indexRecommend_v307";

    /**
     * 发现页闻香识美
     */
    public static final String IndexC_indexBiggie_v307 = "IndexC/indexBiggie_v307";

    /**
     * 发现页轮播图
     */
    public static final String IndexC_indexCarousel_v307 = "IndexC/indexCarousel_v307";
    /**
     * 手机联系人列表
     */
    public static final String NeighborC_phoneContactList = "NeighborC/phoneContactList";
    /**
     * 新的手机联系人列表
     */
    public static final String FriendC_myPhoneContact = "FriendC/myPhoneContact";

//    find interface
    /**
     * 邂逅
     */
    public static final String IndexC_encounter = "IndexC/encounter";

    /**
     * 邂逅页v402
     */
    public static final String IndexC_encounter_v402 = "IndexC/encounter_v402";
    /**
     * 邂逅轮播图
     */
    public static final String IndexC_carouselEnc = "IndexC/carouselEnc";

    /**
     * 动态列表
     */
    public static final String TrendC_trends = "TrendC/trends";

    /**
     * 我的动态列表
     */
    public static final String TrendC_myTrends = "TrendC/myTrends";
    /**
     * 指定动态列表
     */
    public static final String TrendC_userTrends = "TrendC/userTrends";
    /**
     * 点赞，取赞动态
     */
    public static final String TrendC_likeTrend = "TrendC/likeTrend";
    /**
     * 大V专栏文章列表
     */
    public static final String TrendC_articleList = "TrendC/articleList";

    /**
     * 大V 点赞，取赞 文章
     */
    public static final String TrendC_likeArticle = "TrendC/likeArticle";
    /**
     * 发布动态
     */
    public static final String TrendC_publishTrend = "TrendC/publishTrend";
    /**
     * 动态消息列表
     */
    public static final String TrendC_trendMsgList = "TrendC/trendMsgList";

    /**
     * 清除互动通知
     */
    public static final String TrendC_cleanTrendMsg = "TrendC/cleanTrendMsg";

    /**
     * 动态点赞列表
     */
    public static final String TrendC_likeMyTrendList = "TrendC/likeMyTrendList";

    /**
     * 动态游戏列表
     */
    public static final String GameC_gameList = "GameC/gameList";

    /**
     * 分享游戏
     */
    public static final String GameC_share = "GameC/share";

    /**
     * 进入游戏
     */
    public static final String GameC_enterGame = "GameC/enterGame";

    /**
     * 退出游戏
     */
    public static final String GameC_exitGame = "GameC/exitGame";

    /**
     * 获取游戏分享内容
     */
    public static final String GameC_shareH5 = "GameC/shareH5";

    /**
     * 关注人的动态列表
     */
    public static final String TrendC_FocusTrendList = "TrendC/focusTrendList";

    /**
     * 大V专栏
     */
    public static final String TrendC_columns = "TrendC/columns";
    /**
     * /**
     * 动态详情
     */
    public static final String TrendC_trendDetail = "TrendC/trendDetail";
    /**
     * 动态详情评论
     */
    public static final String TrendC_trendComments = "TrendC/trendComments";
    /**
     * 评论动态
     */
    public static final String TrendC_commentTrend = "TrendC/commentTrend";
    /**
     * 删除动态评论
     */
    public static final String TrendC_deleteComment = "TrendC/deleteComment";
    /**
     * 删除动态
     */
    public static final String TrendC_deleteTrend = "TrendC/deleteTrend";
    /**
     * find直播
     */
    public static final String IndexC_indexLive = "IndexC/indexLive";
    /**
     * find直播轮播图
     */
    public static final String IndexC_carouselLive = "IndexC/carouselLive";
    /**
     * find餐厅
     */
    public static final String IndexC_indexRecommend_v322 = "IndexC/indexRecommend_v322";

    /**
     * find餐厅轮播图
     */
    public static final String IndexC_carouselRes = "IndexC/carouselRes";

    //live接口
    /**
     * 粉丝列表
     */
    public static final String LiveC_myFans_v305 = "LiveC/myFans_v305";
    /**
     * 关注列表
     */
    public static final String LiveC_myFocus_v305 = "LiveC/myFocus_v305";
    /**
     * 关注或取消
     */
    public static final String LiveC_focus = "LiveC/focus";
    /**
     * 获取饭票数
     */
    public static final String LiveC_income = "LiveC/income";

    /**
     * 提现
     */
    public static final String WithdrawC_withdraw = "WithdrawC/withdraw";
    /**
     * 可兑换饭票
     */
    public static final String WithdrawC_myMeal = "WithdrawC/myMeal";
    /**
     * 余额
     */
    public static final String WithdrawC_myBalance = "WithdrawC/myBalance";

    /**
     * 饭票兑换余额
     */
    public static final String WithdrawC_exchangeToBalance = "WithdrawC/exchangeToBalance";
    /**
     * 饭票兑换记录
     */
    public static final String WithdrawC_exchangeRecord = "WithdrawC/exchangeRecord";

    /**
     * 提现记录
     */
    public static final String LiveC_withdrawDetail = "LiveC/withdrawDetail";
    /**
     * 绑定微信
     */
    public static final String LiveC_bindWeChat = "LiveC/bindWeChat";
    /**
     * 验证微信是否绑定
     */
    public static final String LiveC_validate = "LiveC/validate";
    /**
     * 充值列表
     */
    public static final String LiveC_faceList = "LiveC/faceList";
    /**
     * 脸蛋充值
     */
    public static final String LiveC_recharge = "LiveC/recharge";
    /**
     * 脸蛋明细
     */
    public static final String LiveC_faceDetail = "LiveC/faceDetail";

    public static final String LiveC_faceDetailWithDetail = "LiveC/faceDetailWithDetail";

    /**
     * 开始直播
     */
    public static final String LiveC_startLive = "LiveC/startLive_v402";
    /**
     * 创建环信聊天室
     */
    public static final String LiveC_creatChatRoom = "LiveC/creatChatRoom";
    /**
     * 检查是否设置常驻位置
     */
    public static final String LiveC_setPermanentOrNot = "LiveC/setPermanentOrNot";

    /**
     * 获取实名认证状态
     */
    public static final String LiveC_getReal = "LiveC/getReal";
    /**
     * 实名认证
     */
    public static final String LiveC_realName = "LiveC/realName";
    /**
     * 直播列表
     */
    public static final String LiveC_newLiveList = "LiveC/liveList_v333";
    /**
     * 发送礼物
     */
    public static final String LiveC_sendGift = "LiveC/sendGiftWithBag";
    /**
     * 保存旁路直播channelID
     */
    public static final String LiveC_saveChnlId = "LiveC/saveChnlId";
    /**
     * 保存旁路直播信息
     */
    public static final String LiveC_saveMultiVideo = "LiveC/saveMultiVideo";

    /**
     * @deprecated 新的朋友列表
     */
    public static final String NeighborC_newFriendList = "NeighborC/newFriendList";
    /**
     * 新的朋友列表
     */
    public static final String NeighborC_newFriendList_v333 = "NeighborC/newFriendList_v333";

    /**
     * 申请好友列表
     */
    public static final String NeighborC_preFriendList = "NeighborC/preFriendList";
    /**
     * 删除申请好友记录
     */
    public static final String NeighborC_delPreFriend = "NeighborC/delPreFriend";

    public static final String RestaurantC_ordDays = "RestaurantC/ordDays";

    /**
     * 约主播吃饭可订餐日期
     */
    public static final String RestaurantC_ordDaysForAppo = "RestaurantC/ordDaysForAppo";

    public static final String RestaurantC_ordTime = "RestaurantC/ordTime";

    public static final String RestaurantC_tabInfo = "RestaurantC/tabInfo";
    /**
     * 获取屌丝评论
     */
    public static final String EvalC_evaluationService = "EvalC/evaluationService";
    /**
     * 餐厅详情页
     */
    public static final String RestaurantC_detail = "RestaurantC/detail";
    /**
     * 邻座搭讪
     */
    public static final String NeighborC_accosted = "NeighborC/accosted";
    /**
     * 退款
     */
    public static final String OrderC_refund = "OrderC/refund";
    /**
     * 忘记密码
     */
    public static final String UserC_forgetPwd = "UserC/forgetPwd";
    /**
     * token登录
     */
    public static final String UserC_tokenSignIn = "UserC/tokenSignIn";
    /**
     * 微信登录
     */
    public static final String WeChatC_weChatLogin = "WeChatC/weChatLogin";
    /**
     * 微信登录
     */
    public static final String WeChatC_weChatDetail = "WeChatC/weChatDetail";

    /**
     * 写评价
     */
    public static final String EvalC_orderEval = "EvalC/orderEval";

    /**
     * 轰趴写评价提交
     */
    public static final String HomepartyC_comment = "HomepartyC/comment";
    /**
     * 退款详情
     */
    public static final String OrderC_refundDetail = "OrderC/refundDetail";
    /**
     * 我的收藏
     */
    public static final String UserC_collect = "UserC/collect";
    /**
     * 好友动态
     */
    public static final String MsgC_friUpdates = "MsgC/friUpdates";
    /**
     * 系统消息
     */
    public static final String MsgC_systemMsg = "MsgC/systemMsg";
    /**
     * 邀请人投诉
     */
    public static final String ReceiveC_complaintReceive = "ReceiveC/complaintReceive";
    /**
     * 邀请人评价主播
     */
    public static final String ReceiveC_evaluateReceive = "ReceiveC/evaluateReceive";
    /**
     * 邀请人评价内容
     */
    public static final String ReceiveC_evaluateContent = "ReceiveC/evaluateContent";

    /**
     * 直播搜索
     */
    public static final String LiveC_liveSearch = "LiveC/liveSearch";
    /**
     * 发送心愿单约会邀请
     */
    public static final String AppointmentC_payWish = "AppointmentC/payWish";
    /**
     * 我的约会红点状态
     */
    public static final String AppointmentC_queryRedStatus_v420 = "AppointmentC/queryRedStatus_v420";

    /**
     * 举报直播间
     */
    public static final String ReportC_reportRoom = "ReportC/reportRoom";
    /**
     * 举报用户
     */
    public static final String ReportC_reportUser = "ReportC/reportUser";
    /**
     * 举报商家
     */
    public static final String ReportC_reportRes = "ReportC/reportRes";
    /**
     * 主播确认赴约扫码
     */
    public static final String ReceiveC_receiveSuccess = "ReceiveC/receiveSuccess";

    public static final String UserC_getTokenId = "UserC/getTokenId";

    public static final String UserC_signIn = "UserC/signIn";

    public static final String UserC_voiceCode = "UserC/voiceCode";

    public static final String UserC_validCodes = "UserC/validCodes";

    public static final String UserC_register = "UserC/register";

    public static final String NeighborC_preFriend = "NeighborC/preFriend";
    /**
     * 订餐前校验有没有约会
     */
    public static final String AppointmentC_orderCheck = "AppointmentC/orderCheck";
    /**
     * 检查菜品价格是否变化
     */
    public static final String DishC_checkPrice = "DishC/checkPrice";
    /**
     * 向后台提交订单
     */
    public static final String OrderC_smtOrder = "OrderC/smtOrder";

    /**
     * 向后台提交约会订单
     */
    public static final String OrderC_receiveSmtOrder = "OrderC/receiveSmtOrder";
    /**
     * 设置支付密码
     */
    public static final String user_repayPwd = "UserC/repayPwd";
    /**
     * 邀请主播吃饭发送位置
     */
    public static final String ReceiveC_sendLocation = "ReceiveC/sendLocation";
    /**
     * 邀请主播吃饭发送位置开关
     */
    public static final String ReceiveC_sendLocationOnoff = "ReceiveC/sendLocationOnoff";

    /**
     * 绑定就餐顾问
     */
    public static final String ConsultantC_BindConsultant = "ConsultantC/bindConsultant";
    /**
     * 获取我的就餐顾问
     */
    public static final String ConsultantC_myConsultant = "ConsultantC/myConsultant";
    /**
     * 查询就餐顾问
     */
    public static final String ConsultantC_queryConsultant = "ConsultantC/queryConsultant";
    /**
     * 查询就餐顾问
     */
    public static final String ConsultantC_ignoreBind = "ConsultantC/ignoreBind";
    /**
     * 房管列表
     */
    public static final String LiveC_roomAdminList = "LiveC/roomAdminList";
    /**
     * 设为房管
     */
    public static final String LiveC_roomAdmin = "LiveC/roomAdmin";
    /**
     * 取消房管
     */
    public static final String LiveC_cancelRoomAdmin = "LiveC/cancelRoomAdmin";
    /**
     * 连麦人列表
     */
    public static final String LiveC_evenWheatList = "LiveC/evenWheatList";
    /**
     * 弹幕状态
     */
    public static final String LiveC_barrage = "LiveC/barrage";
    /**
     * 发弹幕
     */
    public static final String LiveC_sendBarrage = "LiveC/sendBarrage";
    /**
     * 直播间发言
     */
    public static final String LiveC_sendMsg = "LiveC/sendMsg";
    /**
     * 获取红包的额度
     */
    public static final String LiveC_redAmount = "LiveC/redAmount";
    /**
     * 发群红包
     */
    public static final String LiveC_groupRed = "LiveC/groupRed";
    /**
     * 收群红包
     */
    public static final String LiveC_getGroupRed = "LiveC/getGroupRed";
    /**
     * 获取支付宝绑定签名
     */
    public static final String LiveC_buildAuthInfo = "LiveC/buildAuthInfo";

    /**
     * 支付宝一键认证
     */
    public static final String LiveC_alipayValidate = "LiveC/alipayValidate";

    /**
     * 分享计次
     */
    public static final String LiveC_share = "LiveC/share";

    /**
     * 查询特权对应等级
     */
    public static final String LiveC_privilegeToLevel = "LiveC/privilegeToLevel";

    /**
     * 有无可领取奖励的任务
     */
    public static final String TaskC_taskOk = "TaskC/taskOk";

    /**
     * 连麦通知后台保存
     */
    public static final String LiveC_onWheat = "LiveC/onWheat";
    /**
     * 获取自己在直播间中的身份
     */
    public static final String LiveC_userRole = "LiveC/userRole";
    /**
     * 查询禁言状态
     */
    public static final String LiveC_muteStatus = "LiveC/muteStatus";
    /**
     * 添加禁言
     */
    public static final String LiveC_addMute = "LiveC/addMute";
    /**
     * 删除禁言
     */
    public static final String LiveC_delMute = "LiveC/delMute";

    /**
     * 通知列表
     */
    public static final String MessageC_queryMessage = "MessageC/queryMessage";
    /**
     * 系统通知详情列表
     */
    public static final String MessageC_querySystemMessage = "MessageC/querySystemMessage";
    /**
     * 忽略未读
     */
    public static final String MessageC_ignoreUnread = "MessageC/ignoreUnread";
    /**
     * 删除通知消息
     */
    public static final String MessageC_deleteMessage = "MessageC/deleteMessage";
    /**
     * 删除系统消息
     */
    public static final String MessageC_deleteSystemMessage = "MessageC/deleteSystemMessage";
    /**
     * 查看主播基本信息
     */
    public static final String LiveC_anchorBaseInfo = "LiveC/anchorBaseInfo";

    /**
     * 查看用户信息
     */
    public static final String UserC_userInfo = "UserC/userInfo";
    /**
     * 拉黑用户
     */
    public static final String FriendC_pullTheBlack = "FriendC/pullTheBlack";

    /**
     * 查看他人信息页约会
     */
    public static final String AppointmentC_userAppointment = "AppointmentC/userAppointment";

    /**
     * 添加至心愿单
     */
    public static final String AppointmentC_addWish = "AppointmentC/addWish";

    /**
     * 打招呼
     */
    public static final String FriendC_sayHello = "FriendC/sayHello";

    /**
     * 约会校验
     */
    public static final String AppointmentC_checkWish = "AppointmentC/checkWish";

    /**
     * 查询主播可约日期
     */
    public static final String AppointmentC_checkReceive = "AppointmentC/checkReceive";

    /**
     * 退出直播间
     */
    public static final String LiveC_exitRoom = "LiveC/exitRoom";

    /**
     * 获取关系
     */
    public static final String UserC_usersRelationship = "UserC/usersRelationship";
    /**
     * 领红包
     */
    public static final String UserC_getRed = "UserC/getRed";
    /**
     * 首次聊天记录
     */
    public static final String FriendC_firstTalk = "FriendC/firstTalk";

    /**
     * 关注或粉丝搜索
     */
    public static final String FriendC_searchFocusOrFans = "FriendC/searchFocusOrFans";

    /**
     * 上传通讯录
     */
    public static final String FriendC_refreshPhoneContact = "FriendC/refreshPhoneContact";
    /**
     * 从黑名单中删除
     */
    public static final String FriendC_delBlack = "FriendC/delBlack";
    /**
     * 打招呼数量，和关注动态数量
     */
    public static final String FriendC_countHello = "FriendC/countHello";

    /**
     *
     */
    public static final String FriendC_searchUser = "FriendC/searchUser";


    /**
     * 获取所有可领取的成就
     */
    public static final String TaskC_getAllFinishSuccesses = "TaskC/getAllFinishSuccesses";

    /**
     * 获取所有可领取的日常
     */
    public static final String TaskC_getAllFinishTask = "TaskC/getAllFinishTask";

    /**
     * 完成所有成就
     */
    public static final String TaskC_finishAllSuccesses = "TaskC/finishAllSuccesses";

    /**
     * 完成所有日常
     */
    public static final String TaskC_finishAllTask = "TaskC/finishAllTask";

    /**
     * 活动窗口
     */
    public static final String ActivityC_popup = "ActivityC/popup";


    /**
     * 新手引导-我知道了
     */
    public static final String NewbieC_iknow = "NewbieC/iknow";

    /**
     * 查询新手引导
     */
    public static final String NewbieC_guide = "NewbieC/guide";

    /**
     * 订餐页banner
     */
    public static final String RestaurantC_resListBanner = "RestaurantC/resListBanner";
    /**
     * 增加阅读量
     */
    public static final String TrendC_addReadNum = "TrendC/addReadNum";

    /**
     * 未关注大V用户列表
     */
    public static final String TrendC_unFocusVuser = "TrendC/unFocusVuser";

    /**
     * 分享专栏文章至动态
     */
    public static final String TrendC_shareArticle = "TrendC/shareArticle";


    /**
     * 活动分享
     */
    public static final String ActivityC_trend = "ActivityC/trend";

    /**
     * banner活动分享
     */
    public static final String ActivityC_bannerTrend = "ActivityC/bannerTrend";


    /**
     * 轰趴分享到动态
     */
    public static final String HomepartyC_trend = "HomepartyC/trend";

    /**
     * 赠送卡片接口
     */
    public static final String ChristmasC_giveCard = "ChristmasC/giveCard";

    /**
     * 索要卡片接口
     */
    public static final String ChristmasC_askCard = "ChristmasC/askCard";


    /**
     * 活动分享要给的奖励
     */
    public static final String ActivityC_share = "ActivityC/share";

    /**
     * 双十一发送红包
     */
    public static final String SinglesDayC_sendRed = "SinglesDayC/sendRed";

    /**
     * 双十一领红包
     */
    public static final String SinglesDayC_getRed = "SinglesDayC/getRed";


    /**
     * 双十一分享
     */
    public static final String SinglesDayC_share = "SinglesDayC/share";


    /**
     * 接到环信时调的 获取看过你视频的收益然后弹窗
     */
    public static final String SinglesDayC_myIncome = "SinglesDayC/myIncome";

    /**
     * 直播间可邀请用户
     */
    public static final String SunMoonStarC_canInviteList = "SunMoonStarC/canInviteList";

    /**
     * 邀请列表
     */
    public static final String SunMoonStarC_inviters = "SunMoonStarC/inviters";

    /**
     * 回应邀请
     */
    public static final String SunMoonStarC_answerInvitation = "SunMoonStarC/answerInvitation";

    /**
     * 发送邀请
     */
    public static final String SunMoonStarC_sendInvitation = "SunMoonStarC/sendInvitation";

    /**
     * 获取游戏参与详情
     */
    public static final String SunMoonStarC_matchResult = "SunMoonStarC/matchResult";


    /**
     * 参与游戏
     */
    public static final String SunMoonStarC_joinGame = "SunMoonStarC/joinGame";

    /**
     * 直播间游戏分享
     */
    public static final String SunMoonStarC_share = "SunMoonStarC/share";

    /**
     * 退出游戏
     */
    public static final String SunMoonStarC_exitGame = "SunMoonStarC/exitGame";

    /**
     * 弹窗
     */
    public static final String SunMoonStarC_checkPopups = "SunMoonStarC/checkPopups";


    public static final String TransferC_RequestTransfer = "TransferC/RequestTransfer";


    /**
     * 合到10 发送邀请
     */
    public static final String Merge10C_sendInvite = "Merge10C/sendInvite";
    /**
     * 合到10接受邀请
     */
    public static final String Merge10C_agreeInvite = "Merge10C/agreeInvite";
    /**
     * 合到10拒绝邀请
     */
    public static final String Merge10C_refuseInvite = "Merge10C/refuseInvite";
    /**
     * 合到10查询未接受我的邀请的其他人
     */
    public static final String Merge10C_refuseList = "Merge10C/refuseList";
    /**
     * 合到10保存发送成功后的消息ID
     */
    public static final String Merge10C_saveMessageId = "Merge10C/saveMessageId";
    /**
     * 关闭去对战弹窗
     */
    public static final String Merge10C_closeInvite = "Merge10C/closeInvite";
    //endregion

    /**
     * 轰趴列表
     */
    public static final String HomepartyC_partyList = "HomepartyC/partyList";

    /**
     * 轰趴详情
     */
    public static final String HomepartyC_partyDetails = "HomepartyC/partyDetails";
    /**
     * 轰趴评论列表
     */
    public static final String HomepartyC_partyComment = "HomepartyC/partyComment";

    /**
     * 轰趴馆订单详情
     */
    public static final String HomepartyC_partyOrderDetails = "HomepartyC/partyOrderDetails";
    /**
     * 轰趴馆订单退款
     */
    public static final String HomepartyC_refund = "HomepartyC/refund";

    /**
     * 轰趴馆订单申请退款
     */
    public static final String HomepartyC_applyRefund = "HomepartyC/applyRefund";

    /**
     * 轰趴馆订单退款详情
     */
    public static final String HomepartyC_refundDetail = "HomepartyC/refundDetail";

    /**
     * 收藏轰趴馆
     */
    public static final String HomepartyC_collect = "HomepartyC/collect";

    public static final String HOMEPARTYC_DEL_COLLECT = "HomepartyC/delCollect";

    /**
     * 获取需要特别处理的应用渠道信息
     *
     */
    public static final String USERC_GET_CHANNEL_INFO = "UserC/banAndroidMarket";
}

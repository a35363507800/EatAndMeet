package com.echoesnet.eatandmeet.utils.NetUtils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.echoesnet.eatandmeet.activities.LoginModeAct;
import com.echoesnet.eatandmeet.activities.NotifySMSReceivedAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author       ben
 * @modifier
 * @createDate   2016/6/7
 * @version      1.0
 * @description
 */
public class ErrorCodeTable
{
    private static final String TAG=ErrorCodeTable.class.getSimpleName();

    public static final String USR_LOGIN_TIMEOUT="USR_LOGIN_TIMEOUT";
    public static final String USR_TOK_NULL="USR_TOK_NULL";
    public static final String DEVICE_CONFLICT="DEVICE_CONFLICT";
    public static final String USR_SMOBC_ERR="USR_SMOBC_ERR";
    public static final String USR_EXITS="USR_EXITS";
    public static final String USR_SN_EXITS="USR_SN_EXITS";
    public static final String USR_PWD_ERR="USR_PWD_ERR";
    public static final String USR_SIGN_TIMES_ERR="USR_SIGN_TIMES_ERR";
    public static final String USR_NO_LOGIN="USR_NO_LOGIN";
    public static final String USR_NO_INFO="USR_NO_INFO";
    public static final String USR_FILE_TYPE_NULL="USR_FILE_TYPE_NULL";
    public static final String USR_IMG_NOTSPORT="USR_IMG_NOTSPORT";
    public static final String USR_NEWMOBILE_BAD="USR_NEWMOBILE_BAD";
    public static final String USR_NOT_EXITS="USR_NOT_EXITS";
    public static final String USR_DEVICE_NOFOUND="USR_DEVICE_NOFOUND";
    public static final String USR_ONLIY_IOS_ANDROID="USR_ONLIY_IOS_ANDROID";
    public static final String USR_NICKNAME_ERR="USR_NICKNAME_ERR";
    public static final String LONG_STR_NULL="LONG_STR_NULL";

    public static final String LONG_STR_ERR="LONG_STR_ERR";
    public static final String NUMBER_STR_NULL="NUMBER_STR_NULL";
    public static final String NUMBER_STR_ERR="NUMBER_STR_ERR";
    public static final String TYPE_ERR="TYPE_ERR";
    public static final String MAIL_ERR="MAIL_ERR";
    public static final String SORT_ERR="SORT_ERR";
    public static final String IMG_URL_ERR="IMG_URL_ERR";
    public static final String USR_INFO_INCOM="USR_INFO_INCOM";
    public static final String RES_LIST_NULL="RES_LIST_NULL";
    public static final String PRAISE_EXITS="PRAISE_EXITS";
    public static final String OVERDUE_PAYMENT="OVERDUE_PAYMENT";
    public static final String PAY_FAILD="PAY_FAILD";
    public static final String INSUFFICIENT_PAYMENT_AMOUNT="INSUFFICIENT_PAYMENT_AMOUNT";
    public static final String TABLE_HAS_BEEN_BOOKED="TABLE_HAS_BEEN_BOOKED";
    public static final String PAYPWD_NULL="PAYPWD_NULL";
    public static final String PAYPWD_ERR="PAYPWD_ERR";
    public static final String RED_HAS_BEEN_GOT="RED_HAS_BEEN_GOT";
    public static final String RED_OVERDUE="RED_OVERDUE";
    public static final String EVAL_DELED="EVAL_DELED";
    public static final String ORDER_NO_REFUND="ORDER_NO_REFUND";
    public static final String ALIPAY_EXISTS="ALIPAY_EXISTS";
    public static final String REFUNDING="REFUNDING";
    public static final String COUNTDOWN_IS_FINISH="COUNTDOWN_IS_FINISH";
    public static final String REPAY_COUNTDOWN_IS_FINISH="REPAY_COUNTDOWN_IS_FINISH";
    public static final String REALNAME_INCONSISTENT="REALNAME_INCONSISTENT";
    public static final String IDCARD_NOT_EXISTS="IDCARD_NOT_EXISTS";
    public static final String USR_IS_FRIEND="USR_IS_FRIEND";
    public static final String REALNAME_DO_NOT_PASS="REALNAME_DO_NOT_PASS";
    public static final String USR_BEEN_LOCKING="USR_BEEN_LOCKING";
    public static final String CODES_OVERDUE="CODES_OVERDUE";
    public static final String CODES_ERROR="CODES_ERROR";
    public static final String ORDERTIME_ERROR="ORDERTIME_ERROR";
    public static final String WECHAT_USER_JOINED="WECHAT_USER_JOINED";
    public static final String GET_TOKEN_ERROR="GET_TOKEN_ERROR";
    public static final String RES_OVER_TIMES="RES_OVER_TIMES";
    public static final String INVALIDCODES_OVER_TIMES="INVALIDCODES_OVER_TIMES";
    public static final String PWDVALIDCODES_OVER_TIMES="PWDVALIDCODES_OVER_TIMES";
    public static final String UPVALIDCODES_OVER_TIMES="UPVALIDCODES_OVER_TIMES";
    public static final String PWD_OVER_TIMES="PWD_OVER_TIMES";
    public static final String PAYVALIDCODES_OVER_TIMES="PAYVALIDCODES_OVER_TIMES";
    public static final String ORDER_CANNOT_REFUND="ORDER_CANNOT_REFUND";
    public static final String ALREADY_RECHARGE="ALREADY_RECHARGE";
    public static final String INCODE_NOT_EXIST="INCODE_NOT_EXIST";
    public static final String PAYPWD_OVER_TIMES="PAYPWD_OVER_TIMES";
    public static final String DUE_OVER_TIMES="DUE_OVER_TIMES";
    public static final String USR_IN_BLACKLIST="USR_IN_BLACKLIST";
    public static final String USR_WELCOMEGIFT_PAYMENT="USR_WELCOMEGIFT_PAYMENT";
    public static final String EVAL_EXITS="EVAL_EXITS";
    public static final String NOT_REACH_MINIMUM="NOT_REACH_MINIMUM";
    public static final String PAY_WAIT="PAY_WAIT";
    public static final String USR_CONCERNED_ANCHOR="USR_CONCERNED_ANCHOR";
    public static final String IN_REVIEW_24HOUR="IN_REVIEW_24HOUR";
    public static final String IDCARD_NOT_VALIDATE="IDCARD_NOT_VALIDATE";
    public static final String WECHAT_NOT_BIND="WECHAT_NOT_BIND";
    public static final String IDCARD_ERROR="IDCARD_ERROR";
    public static final String TSL_ERROR="TSL_ERROR";
    public static final String CASH_NOT_ENOUGH="CASH_NOT_ENOUGH";
    public static final String NETWORK_ERROR="NETWORK_ERROR";
    public static final String WECHAT_IS_BIND_OTHER="WECHAT_IS_BIND_OTHER";
    public static final String SIGN_ANCHOR_NOT_WITHDRAW="SIGN_ANCHOR_NOT_WITHDRAW";
    public static final String IDCARD_VAILDED="IDCARD_VAILDED";
    public static final String FACEEGG_INSUFFICIENT="FACEEGG_INSUFFICIENT";
    public static final String NOT_WITHDRAW_24HOUR="NOT_WITHDRAW_24HOUR";
    public static final String ROOM_NOT_LIVING="ROOM_NOT_LIVING";
    public static final String PAYPWD_NULL_REAL="PAYPWD_NULL_REAL";
    public static final String WECHAT_LOSE_BIND="WECHAT_LOSE_BIND";
    public static final String BINDED_WECHAT_NO_THIS="BINDED_WECHAT_NO_THIS";
    public static final String BAN_NOT_LIVE="BAN_NOT_LIVE";
    public static final String UNDER_EIGHTEEN="UNDER_EIGHTEEN";
    public static final String NO_REALNAME="NO_REALNAME";
    public static final String WCPAY_ERROR="WCPAY_ERROR";
    public static final String IDCARD_FORMAT_ERROR="IDCARD_FORMAT_ERROR";
    public static final String ORDER_REFUNDED="ORDER_REFUNDED";
    public static final String PAYFOR_SELF="PAYFOR_SELF";
    public static final String PAYED="PAYED";
    public static final String OVERDUE_ORDER="OVERDUE_ORDER";
    public static final String ORDER_CANNOT_SHARE="ORDER_CANNOT_SHARE";
    public static final String ORDER_DELED="ORDER_DELED";
    public static final String REALNAME_UNDER_REVIEW="REALNAME_UNDER_REVIEW";
    public static final String ALREADYEVALUATE="ALREADYEVALUATE";
    public static final String NO_POWER="NO_POWER";
    public static final String HAS_ORDER="HAS_ORDER";
    public static final String RECEIVE_OVER="RECEIVE_OVER";
    public static final String STATUS_ERROR="STATUS_ERROR";
    public static final String SWEPT_ERROR="SWEPT_ERROR";
    public static final String NO_RECEIVE_ORDER="NO_RECEIVE_ORDER";
    public static final String RECEIVESMTORDER_EXITS="RECEIVESMTORDER_EXITS";
    public static final String NOT_RECEIVE_MYSELF="NOT_RECEIVE_MYSELF";
    public static final String RECEIVE_DELING="RECEIVE_DELING";
    public static final String ALREADY_INVITED="ALREADY_INVITED";
    public static final String CANOT_AGREE_RECEIVE="CANOT_AGREE_RECEIVE";
    public static final String CANOT_REFUSE_RECEIVE="CANOT_REFUSE_RECEIVE";
    public static final String RECEIVESMTORDER_ERROR="RECEIVESMTORDER_ERROR";
    public static final String SWEEP_ORDER_CANNOT_REFUND="SWEEP_ORDER_CANNOT_REFUND";
    public static final String LIGHT_ORDER_CANNOT_REFUND="LIGHT_ORDER_CANNOT_REFUND";
    public static final String ISPACKAGE_ERROR="ISPACKAGE_ERROR";
    public static final String APPOEXPIRE_ERROR="APPOEXPIRE_ERROR";
    public static final String WECHAT_INFO_FAILED="WECHAT_INFO_FAILED";
    public static final String PERMANENT_NULL="PERMANENT_NULL";
    public static final String ID_ERROR="ID_ERROR";
    public static final String CONSULTANT_BIND_INVALID="CONSULTANT_BIND_INVALID";
    public static final String CONSULTANT_BINDED="CONSULTANT_BINDED";
    public static final String USR_NOT_REGISTER="USR_NOT_REGISTER";
    public static final String GROUPRED_OVER="GROUPRED_OVER";
    public static final String VOICECODE_OVER_TIME="VOICECODE_OVER_TIME";
    public static final String VOICECODE_SEND_FAILED="VOICECODE_SEND_FAILED";
    public static final String SIGN_ANCHOR_NO_BALANCE="SIGN_ANCHOR_NO_BALANCE";
    public static final String CANNOT_BIND_SELF="CANNOT_BIND_SELF";
    public static final String CANNOT_CHOOSE_SELF="CANNOT_CHOOSE_SELF";
    public static final String BALANCE_IS_INSUFFICIENT="BALANCE_IS_INSUFFICIENT";
    public static final String BARRAGE_CLOSE="BARRAGE_CLOSE";
    public static final String LEVEL_INSUFFICIENT="LEVEL_INSUFFICIENT";
    public static final String GROUPRED_NOT_EXIST="GROUPRED_NOT_EXIST";
    public static final String RED_FROM_SELF="RED_FROM_SELF";
    public static final String GROUPRED_BEEN_RECEIVED="GROUPRED_BEEN_RECEIVED";
    public static final String ALIPAY_AUTH_FAILED="ALIPAY_AUTH_FAILED";
    public static final String ALIPAY_NOT_PERSONALACCOUNT="ALIPAY_NOT_PERSONALACCOUNT";
    public static final String ALIPAY_NOT_CERTIFIED="ALIPAY_NOT_CERTIFIED";
    public static final String FORBID_ERROR="FORBID_ERROR";
    public static final String ALREADY_REWARD="ALREADY_REWARD";
    public static final String USER_CHECKED="USER_CHECKED";
    public static final String COMMENT_CANNOT_DELETE="COMMENT_CANNOT_DELETE";
    public static final String TREND_CANNOT_DELETE="TREND_CANNOT_DELETE";
    public static final String TREND_DELETED="TREND_DELETED";
    public static final String COMMENT_DELETED="COMMENT_DELETED";
    public static final String REPORT_ME_NOT="REPORT_ME_NOT";
    public static final String NATIONALDAY_TIMEOUT="NATIONALDAY_TIMEOUT";
    public static final String RECEIVED_REWARD="RECEIVED_REWARD";
    public static final String NO_THIS_CARD="NO_THIS_CARD";
    public static final String OVER_SENDCARD_LIMIT="OVER_SENDCARD_LIMIT";
    public static final String CARD_NOT_EXIST="CARD_NOT_EXIST";
    public static final String GAME_OVER="GAME_OVER";
    public static final String COUNTERPART_IN_GAME="COUNTERPART_IN_GAME";
    public static final String GAME_INVITE_INVALID="GAME_INVITE_INVALID";
    public static final String COUNTERPART_ENOUGH="COUNTERPART_ENOUGH";
    public static final String OVER_WITHDRAW_LIMIT="OVER_WITHDRAW_LIMIT";
    public static final String USER_DETAILED="USER_DETAILED";
    public static final String HOMEPARTY_TIMEOUT="HOMEPARTY_TIMEOUT";
    public static final String HOMEPARTY_SCREENINGS_RESERVED="HOMEPARTY_SCREENINGS_RESERVED";
    public static final String HOMEPARTY_OFFLINE="HOMEPARTY_OFFLINE";
    public static final String HOMEPARTY_USED="HOMEPARTY_USED";
    public static final String SENSITIVE_NAME="SENSITIVE_NAME";    public static final String USER_MUTING="USER_MUTING";
    public static final String USER_BEEN_DELMUTE = "USER_BEEN_DELMUTE";

    public static boolean handleErrorCode(String errorCode, @Nullable Context mContext)
    {
        return handleConflict(errorCode,mContext);
    }
    public static boolean handleConflict(String errorCode,Context mContext)
    {
        try
        {
            if (mContext==null)
                mContext= EamApplication.getInstance();
            if (USR_LOGIN_TIMEOUT.equals(errorCode)||USR_TOK_NULL.equals(errorCode))
            {
                HuanXinIMHelper.getInstance().quitOfflineConflict(mContext, new ICommonOperateListener()
                {
                    @Override
                    public void onSuccess(String response)
                    {
                        EamLogger.t(TAG).writeToDefaultFile("顶号退出成功》"+response);
                    }

                    @Override
                    public void onError(String code, String msg)
                    {
                        EamLogger.t(TAG).writeToDefaultFile("顶号退出失败》"+"code>"+code+" msg>"+msg);
                    }
                });
                //ToastUtils.showShort("登录失效，请重新登录");
                Intent intent = new Intent(mContext, LoginModeAct.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                return true;
            }else if (DEVICE_CONFLICT.equals(errorCode))//此错误码只负责在线被顶情况
            {
                final Context finalMContext = mContext;
                HuanXinIMHelper.getInstance().quitOnlineConflict(mContext, new ICommonOperateListener()
                {
                    @Override
                    public void onSuccess(String response)
                    {
                        Intent intent = new Intent();
                        intent.setClass(finalMContext, NotifySMSReceivedAct.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        finalMContext.startActivity(intent);
                    }
                    @Override
                    public void onError(String code, String msg)
                    {
                        EamLogger.t(TAG).writeToDefaultFile("顶号退出失败》"+"code>"+code+" msg>"+msg);
                    }
                });
                return true;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static String parseErrorCode(String errorCode)
    {
        return  errorCode.equals(USR_SMOBC_ERR)?"手机验证码错误":
                errorCode.equals(USR_EXITS)?"用户已存在":
                errorCode.equals(USR_SN_EXITS)?"用户昵称已经存在":
                errorCode.equals(USR_PWD_ERR)?"密码错误":
                errorCode.equals(USR_SIGN_TIMES_ERR)?"用户限定时间内登录次数过多":
                errorCode.equals(USR_LOGIN_TIMEOUT)?"登录失效":
                errorCode.equals(USR_NO_LOGIN)?"用户未登录":
                errorCode.equals(USR_NO_INFO)?"没有可以修改的用户信息":
                errorCode.equals(USR_FILE_TYPE_NULL)?"用户上传文件类型不能为空":
                errorCode.equals(USR_IMG_NOTSPORT)?"上传图片不支持 , 支持jpg png 和gif":
                errorCode.equals(USR_NEWMOBILE_BAD)?"新手机号已经被注册":
                errorCode.equals(USR_NOT_EXITS)?"用户不存在":
                errorCode.equals(USR_DEVICE_NOFOUND)?"没有找到推送token":
                errorCode.equals(USR_ONLIY_IOS_ANDROID)?"只支持IOS 和 Android系统":
                errorCode.equals(USR_NICKNAME_ERR)?"用户昵称格式错误,或者为空.":
                errorCode.equals(LONG_STR_NULL)?"Long类型数据为空":
                errorCode.equals(LONG_STR_ERR)?"long 类型数据错误":
                errorCode.equals(NUMBER_STR_NULL)?"数字类型字符串为空":
                errorCode.equals(NUMBER_STR_ERR)?"数字类型字符格式错误":
                errorCode.equals(TYPE_ERR)?"类型为空或者格式错误":
                errorCode.equals(MAIL_ERR)?"邮箱格式错误":
                errorCode.equals(SORT_ERR)?"排序为空,或者格式无此排序方式":
                errorCode.equals(IMG_URL_ERR)?"图片 url 为空,或者格式错误.":
                errorCode.equals(USR_INFO_INCOM)?"用户信息不完整":
                errorCode.equals(RES_LIST_NULL)?"餐厅列表为空":
                errorCode.equals(PRAISE_EXITS)?"该用户已经点过赞":
//                errorCode.equals("OVERDUE_PAYMENT")?"已支付，请勿重复支付":   // 下订单--15分钟后--待支付订单去支付--返回此错误码--提示语有问题
                errorCode.equals(OVERDUE_PAYMENT)?"支付过期":
                errorCode.equals(PAY_FAILD)?"支付失败":
                errorCode.equals(INSUFFICIENT_PAYMENT_AMOUNT)?"支付金额不足":
                errorCode.equals(TABLE_HAS_BEEN_BOOKED)?"桌子已被预订":
                errorCode.equals(RED_HAS_BEEN_GOT)?"红包已经被领取":
                errorCode.equals(RED_OVERDUE)?"红包已过期":
                errorCode.equals(EVAL_DELED)?"评价已被删除":
                errorCode.equals(ORDER_NO_REFUND)?"订单无退款信息":
                errorCode.equals(ALIPAY_EXISTS)?"支付宝账号已绑定":
                errorCode.equals(REFUNDING)?"退款中":
                errorCode.equals(COUNTDOWN_IS_FINISH)?"每日实名验证次数用尽":
                errorCode.equals(REALNAME_INCONSISTENT)?"与实名认证信息不一致":
                errorCode.equals(IDCARD_NOT_EXISTS)?"身份证号不存在":
                errorCode.equals(USR_IS_FRIEND)?"该用户已经是好友了":
                errorCode.equals(REALNAME_DO_NOT_PASS)?"没有进行实名验证":
                errorCode.equals(USR_BEEN_LOCKING)?"用户被锁":
                errorCode.equals(CODES_OVERDUE)?"验证码错误，请确认后再输入":
                errorCode.equals(CODES_ERROR)?"验证码错误，请重新输入":
                errorCode.equals(ORDERTIME_ERROR)?"订餐时间有误":
                errorCode.equals(WECHAT_USER_JOINED)?"微信用户已参加活动":
                errorCode.equals(GET_TOKEN_ERROR)?"没有获取到token":
                errorCode.equals(RES_OVER_TIMES)?"已达到今日最大获取次数！":
                errorCode.equals(INVALIDCODES_OVER_TIMES)?"您输入的验证码失效次数超过10次":
                errorCode.equals(PWDVALIDCODES_OVER_TIMES)?"忘记登录密码发送验证码超过5次":
                errorCode.equals(UPVALIDCODES_OVER_TIMES)?"修改餐厅手机号发送验证码超过20次":
                errorCode.equals(PWD_OVER_TIMES)?"已达到今日最大获取次数！请明天重试":
                errorCode.equals(PAYVALIDCODES_OVER_TIMES)?"忘记支付密码失效超过5次":
                errorCode.equals(ORDER_CANNOT_REFUND)?"此订单不可退款":
                errorCode.equals(ALREADY_RECHARGE)?"已充值":
                errorCode.equals(INCODE_NOT_EXIST)?"邀请码不存在":
                errorCode.equals(PAYPWD_OVER_TIMES)?"已达到今日最大获取次数！请明天重试":
                errorCode.equals(DUE_OVER_TIMES)?"已达到今日最大获取次数！":
                errorCode.equals(USR_IN_BLACKLIST)?"用户处于对方黑名单中":
                errorCode.equals(USR_WELCOMEGIFT_PAYMENT)?"用户申请见面礼加好友过期了":
                errorCode.equals(EVAL_EXITS)?"订单已经评价过，不能重复评价":
                errorCode.equals(NOT_REACH_MINIMUM)?"订单金额未达到起订价，请客官加菜":
                errorCode.equals(PAY_WAIT)?"由于网络原因，暂时未获得支付结果":
                errorCode.equals(USR_BEEN_LOCKING)?"此账户已被锁定，请咨询客服":
                errorCode.equals(USR_CONCERNED_ANCHOR)?"该用户已经关注过该主播":
                errorCode.equals(IN_REVIEW_24HOUR)?"审核中预计24小时内完成":
                errorCode.equals(IDCARD_ERROR)?"实名认证信息有误":
                errorCode.equals(TSL_ERROR)?"直播第三方报错":
                errorCode.equals(CASH_NOT_ENOUGH)?"饭票不足":
                errorCode.equals(NETWORK_ERROR)?"微信支付网络异常":
                errorCode.equals(WECHAT_IS_BIND_OTHER)?"微信已绑定过其他账号":
                errorCode.equals(SIGN_ANCHOR_NOT_WITHDRAW)?"签约主播不能提现":
                errorCode.equals(IDCARD_VAILDED)?"身份证已被绑定":
                errorCode.equals(FACEEGG_INSUFFICIENT)?"脸蛋余额不足":
                errorCode.equals(NOT_WITHDRAW_24HOUR)?"24小时内只能提现一次":
                errorCode.equals(ROOM_NOT_LIVING)?"该房间未在直播中":
                errorCode.equals(PAYPWD_NULL_REAL)?"没有设置支付密码":
                errorCode.equals(BINDED_WECHAT_NO_THIS)?"手机号已绑定其他微信"://绑定过微信但不是这个微信号
                errorCode.equals(BAN_NOT_LIVE)?"主播被封不能开始直播":
                errorCode.equals(UNDER_EIGHTEEN)?"您未满18周岁，根据相关法律法规您不能进行相关操作！":
                errorCode.equals(WCPAY_ERROR)?"微信提现错误":
                errorCode.equals(IDCARD_FORMAT_ERROR)?"身份证号格式错误":
                errorCode.equals(ORDER_REFUNDED)?"订单已申请退款":
                errorCode.equals(PAYFOR_SELF)?"不可为自己买单":
                errorCode.equals(PAYED)?"已经代付过":
                errorCode.equals(OVERDUE_ORDER)?"代付过期了":
                errorCode.equals(ORDER_CANNOT_SHARE)?"订单不可分享":
                errorCode.equals(ORDER_DELED)?"订单已删除":
                errorCode.equals(REALNAME_UNDER_REVIEW)?"实名认证审核中":
                errorCode.equals(ALREADYEVALUATE)?"已评价或者以投诉过":
                errorCode.equals(NO_POWER)?"你不是该约会的主播你没有权限操作":
                errorCode.equals(HAS_ORDER)?"还有未取消的订单":
                errorCode.equals(RECEIVE_OVER)?"约会结束了不能取消了":
                errorCode.equals(STATUS_ERROR)?"不是约会订单或已成功扫描":
                errorCode.equals(SWEPT_ERROR)?"约会主播已经扫码过了":
                errorCode.equals(NO_RECEIVE_ORDER)?"不是约会订单或已成功扫描":
                errorCode.equals(RECEIVESMTORDER_EXITS)?"您有一笔约会未支付的订单，请前往支付":
                errorCode.equals(NOT_RECEIVE_MYSELF)?"不能邀请自己":
                errorCode.equals(ALREADY_INVITED)?"您已经有约":
                errorCode.equals(CANOT_AGREE_RECEIVE)?"此约会不能接受":
                errorCode.equals(CANOT_REFUSE_RECEIVE)?"此约会不能拒绝":
                errorCode.equals(RECEIVESMTORDER_ERROR)?"当前约会已有订单，不能重复预定":
                errorCode.equals(SWEEP_ORDER_CANNOT_REFUND)?"主播已经扫码不能退款":
                errorCode.equals(LIGHT_ORDER_CANNOT_REFUND)?"主播已经点亮了不能退款":
                errorCode.equals(ISPACKAGE_ERROR)?"不能取消单个主播":
                errorCode.equals(APPOEXPIRE_ERROR)?"约会已过期":
                errorCode.equals(WECHAT_INFO_FAILED)?"获取微信信息失败":
                errorCode.equals(PERMANENT_NULL)?"未设置常驻位置":
                errorCode.equals(ID_ERROR)?"id错误":
                errorCode.equals(CONSULTANT_BIND_INVALID)?"顾问绑定失效":
                errorCode.equals(CONSULTANT_BINDED)?"目前处于绑定状态，无法再次绑定":
                errorCode.equals(GROUPRED_OVER)?"红包领完了或者已经过期了":
                errorCode.equals(VOICECODE_OVER_TIME)?"该设备获取语音验证码超过次数限制（5次）":
                errorCode.equals(VOICECODE_SEND_FAILED)?"发送语音验证码失败":
                errorCode.equals(SIGN_ANCHOR_NO_BALANCE)?"签约主播不允许使用余额账户":
                errorCode.equals(CANNOT_BIND_SELF)?"不能成为自己的就餐顾问":
                errorCode.equals(CANNOT_CHOOSE_SELF)?"您不能成为自己的就餐顾问":
                errorCode.equals(BALANCE_IS_INSUFFICIENT)?"余额不足":
                errorCode.equals(BARRAGE_CLOSE)?"弹幕开关已关闭":
                errorCode.equals(LEVEL_INSUFFICIENT)?"等级不足":
                errorCode.equals(GROUPRED_NOT_EXIST)?"群红包不存在或不是这个房间的红包":
                errorCode.equals(RED_FROM_SELF)?"不可以领取自己的红包":
                errorCode.equals(GROUPRED_BEEN_RECEIVED)?"您已领取该红包":
                errorCode.equals(ALIPAY_AUTH_FAILED)?"支付宝授权失败":
                errorCode.equals(ALIPAY_NOT_PERSONALACCOUNT)?"你的身份证信息错误,请更换支付宝账号重新认证或使用看脸吃饭认证":
                errorCode.equals(ALIPAY_NOT_CERTIFIED)?"你的身份证信息错误,请更换支付宝账号重新认证或使用看脸吃饭认证":
                //errorCode.equal("ALIPAY_BIND_OTHER")?"你的身份证已认证过,请更换支付宝账号重新认证":
                errorCode.equals(FORBID_ERROR)?"禁言状态中不可以发言哦~":
                errorCode.equals(ALREADY_REWARD)?"你今天已经领取过福利了,明天再领取吧":
                errorCode.equals(USER_CHECKED)?"你今天已经领取过福利了,明天再领取吧":
                errorCode.equals(COMMENT_CANNOT_DELETE)?"评论不可删除":
                errorCode.equals(TREND_CANNOT_DELETE)?"动态不可删除":
                errorCode.equals(TREND_DELETED)?"此动态已删除":
                errorCode.equals(COMMENT_DELETED)?"此评论已删除":
                errorCode.equals(REPORT_ME_NOT)?"不能举报自己":
                errorCode.equals(NATIONALDAY_TIMEOUT)?"不在活动期间":
                errorCode.equals(RECEIVED_REWARD)?"已领取奖励":
                errorCode.equals(NO_THIS_CARD)?"没有这张卡片":
                errorCode.equals(OVER_SENDCARD_LIMIT)?"超过赠送次数限制":
                errorCode.equals(CARD_NOT_EXIST)?"系统不存在这张卡片":
                errorCode.equals(GAME_OVER)?"有用户未参与，游戏已结束":
                errorCode.equals(COUNTERPART_IN_GAME)?"对方正在游戏中":
                errorCode.equals(GAME_INVITE_INVALID)?"游戏邀请已失效":
                errorCode.equals(FACEEGG_INSUFFICIENT)?"余额不足":
                errorCode.equals(COUNTERPART_ENOUGH)?"当前人数已满无法进入":
                errorCode.equals(OVER_WITHDRAW_LIMIT)?"超过提现上限":
                errorCode.equals(USER_DETAILED)?"已完善资料":
                errorCode.equals(HOMEPARTY_TIMEOUT)?"已经超过退款时间，不能退款了哦":
                errorCode.equals(HOMEPARTY_SCREENINGS_RESERVED)?"已经有人预定这一场了，看看其他场次吧~":
                errorCode.equals(HOMEPARTY_USED)?"已经超过退款时间，不能退款了哦":
             errorCode.equals(SENSITIVE_NAME)?"我们只有一个创始人哦~":
                errorCode.equals(USER_MUTING)?"该用户已被禁言":
                errorCode.equals(USER_BEEN_DELMUTE)?"该用户已被解除禁言":                errorCode.equals(PAYPWD_ERR)?"":                     //处理过的错误码解析为空串，再调用super.不会弹出toast
                errorCode.equals(REPAY_COUNTDOWN_IS_FINISH)?"":
                errorCode.equals(RECEIVE_DELING)?"":
                errorCode.equals(IDCARD_NOT_VALIDATE)?"":
                errorCode.equals(WECHAT_NOT_BIND)?"":
                errorCode.equals(WECHAT_LOSE_BIND)?"":
                errorCode.equals(NO_REALNAME)?"":
                errorCode.equals(USR_NOT_REGISTER)?"":
                errorCode.equals(PAYPWD_NULL)?"":
                errorCode.equals("1")?"小饭也不知道怎么了，您可以重试一下下":
                errorCode.equals("error")?"小饭也不知道怎么了，您可以重试一下下":    //后端太几把随意了--wb
                errorCode;
    }
}

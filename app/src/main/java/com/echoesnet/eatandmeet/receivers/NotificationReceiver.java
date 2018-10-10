package com.echoesnet.eatandmeet.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.echoesnet.eatandmeet.activities.ApproveProgressAct;
import com.echoesnet.eatandmeet.activities.CChatActivity;
import com.echoesnet.eatandmeet.activities.CNewFriendsAct;
import com.echoesnet.eatandmeet.activities.ClubOrderRecordDetailAct;
import com.echoesnet.eatandmeet.activities.DOrderMealDetailAct;
import com.echoesnet.eatandmeet.activities.HomeAct;
import com.echoesnet.eatandmeet.activities.MyBalanceDetailAct;
import com.echoesnet.eatandmeet.activities.QueryScheduleAct;
import com.echoesnet.eatandmeet.activities.SysNewsAct;
import com.echoesnet.eatandmeet.activities.TrendsDetailAct;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.activities.liveplay.View.StartLiveProxyAct;
import com.echoesnet.eatandmeet.activities.liveplay.managers.ViewShareHelper;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.IMUtils.Constant;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/8/30 0030
 * @description 通知栏点击事件响应 广播
 */
public class NotificationReceiver extends BroadcastReceiver
{
    private final String TAG = NotificationReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (CommonUtils.isInLiveRoom)
        {
            ToastUtils.showShort("直播中无法打开");
            return;
        }
        String message = intent.getStringExtra("message");
        Logger.t(TAG).d(message);
        updateUiInOnMessage(context, message);
    }


    private void updateUiInOnMessage(Context context, String message)
    {
        try
        {
            Logger.t(TAG).d("message:" + message);
            JSONObject body = new JSONObject(message);
            String code = body.getString("code");
            Logger.t(TAG).d("消息类型--> " + code);
            switch (code)
            {
                //账号被踢了
                case "ACCOUNT_CONFLICT":
/*                    Intent intent = new Intent();
                    intent.setClass(context.getApplicationContext(), NotifySMSReceivedAct.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);*/
                    break;
                case "MESSAGE_INFO":
                    if (null != context)
                    {
                        Intent intent1 = new Intent(EamConstant.ACTION_UPDATE_USER_MSG);
                        intent1.putExtra("needRefreshMsg", true);
                        context.sendBroadcast(intent1);
                    }
                    showNotify(message, EamConstant.EAM_NOTIFY_MSG_CENTER, SysNewsAct.class, context);
                    break;
                case "SYSTEM_INFO":
                    if (null != context)
                    {
                        Intent intent1 = new Intent(EamConstant.ACTION_UPDATE_USER_MSG);
                        intent1.putExtra("needRefreshMsg", true);
                        context.sendBroadcast(intent1);
                    }
                    showNotify(message, EamConstant.EAM_NOTIFY_SYS_CENTER, SysNewsAct.class, context);
                    break;
                case "FRIEND_INFO":
                    // by 刘洋
                    // bug code : 6631
                    // 这里是老版本的推送，会产生一个"好友列表"的通知；
                    // 但是新版本下，不存在"好友"这个关系，也就无法产生这个跳转。

//                    if (null != context)
//                    {
//                        Intent intent1 = new Intent(EamConstant.ACTION_UPDATE_USER_MSG);
//                        intent1.putExtra("needRefreshMsg", true);
//                        context.sendBroadcast(intent1);
//                    }
//                    showNotify(body.getString("msg"), EamConstant.EAM_NOTIFY_FRIEND_DYNAMICS, MMyFriendDynamicsAct.class);
                    break;
                case "WELGIFT_OVERDUE":
                    showNotify(message, EamConstant.EAM_NOTIFY_WELGIFT_OVERDUE, CNewFriendsAct.class, context);
                    break;
                case "APPLY_FRIEND":
                    showNotify(message, EamConstant.EAM_NOTIFY_APPLY_FRIEND, CNewFriendsAct.class, context);
                    break;
                case "RED_OVERDUE":
                    showNotify(message, EamConstant.EAM_NOTIFY_RED_OVERDUE, CChatActivity.class, context);
                    break;
                case "LIVE_NOTICE":
                    if (ViewShareHelper.liveMySelfRole == LiveRecord.ROOM_MODE_MEMBER)
                    {
                        // TODO: 2017/3/14 0014
                        showNotify(message, EamConstant.EAM_NOTIFY_LIVE_PLAY, StartLiveProxyAct.class, context);
                    }
                    break;
                case "balance":
                    showNotify(message, EamConstant.EAM_NOTIFY_BALANCE_DETAIL, MyBalanceDetailAct.class, context);
                    break;
                case "CERTIFICATE":
                    showNotify(message, EamConstant.EAM_NOTIFY_REAL_NAME, ApproveProgressAct.class, context);
                    break;
                case "RECEIVE_INFO_USER":
                    showNotify(message, EamConstant.EAM_NOTIFY_DATE_INFO_USER, QueryScheduleAct.class, context);
                    break;
                case "RECEIVE_INFO_ANCHOR":
                    showNotify(message, EamConstant.EAM_NOTIFY_DATE_INFO_ANCHOR, QueryScheduleAct.class, context);
                    break;
                case "TREND_DETAIL":
                    showNotify(message, EamConstant.EAM_NOTIFY_TREND_DETAIL, TrendsDetailAct.class, context);
                    break;
                case "TREND_MSG":
                    EamApplication.getInstance().interactionCount = String.valueOf(Integer.parseInt(EamApplication.getInstance().interactionCount) + 1);
                    showNotify(message, EamConstant.EAM_NOTIFY_TREND_DETAIL, TrendsDetailAct.class, context);
                    break;
                case "RES_DETAIL":
                    showNotify(message, EamConstant.EAM_NOTIFY_RES_DETAIL, DOrderMealDetailAct.class, context);
                    break;

                case "HPORDER1":
                    showNotify(message, EamConstant.EAM_NOTIFY_HP1, ClubOrderRecordDetailAct.class, context);
                    break;
                case "HPORDER2":
                    if (null != context)
                    {
                        Intent intent1 = new Intent(EamConstant.ACTION_UPDATE_USER_MSG);
                        intent1.putExtra("needRefreshMsg", true);
                        context.sendBroadcast(intent1);
                    }
                    showNotify(message, EamConstant.EAM_NOTIFY_HP2, SysNewsAct.class, context);
                    break;
                default:
                    showNotify(message, EamConstant.EAM_NOTIFY_DEFAULT_CODE, HomeAct.class, context);
                    break;
            }
        } catch (JSONException e)
        {
            Logger.t(TAG).d("错误信息：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showNotify(String imMessage, int notifyId, Class<?> targetAct, Context context)
    {
        Logger.t(TAG).d("imMessage" + imMessage);
        EamApplication application = EamApplication.getInstance();
        Intent descIntent = new Intent(application, targetAct);
        descIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try
        {

            JSONObject jsonObject = new JSONObject(imMessage);

        switch (notifyId)
        {
            case EamConstant.EAM_NOTIFY_LIVE_PLAY:
            {

                descIntent.putExtra("roomid", jsonObject.getString("roomId"));
                descIntent.putExtra("roomName", "");
                descIntent.putExtra("sign", "");
                descIntent.putExtra("roomMode", LiveRecord.ROOM_MODE_MEMBER);
                descIntent.putExtra("flyPage", "");
            }
            break;
            case EamConstant.EAM_NOTIFY_DATE_INFO_USER:
            {
                descIntent.putExtra("streamID", jsonObject.getString("streamID"));
                descIntent.putExtra("roomId", jsonObject.getString("roomId"));
                descIntent.putExtra("luid", jsonObject.getString("luid"));
            }
            break;
            case EamConstant.EAM_NOTIFY_DATE_INFO_ANCHOR:
                descIntent.putExtra("streamID", jsonObject.getString("streamID"));
                descIntent.putExtra("roomId", jsonObject.getString("roomId"));
                descIntent.putExtra("luid", jsonObject.getString("luid"));
                break;
        }

            if (notifyId == EamConstant.EAM_NOTIFY_TREND_DETAIL)
            {
                descIntent.putExtra("tId", jsonObject.getString("tId"));
                descIntent.putExtra("commentId", jsonObject.getString("commentId"));
            }
            else if (notifyId == EamConstant.EAM_NOTIFY_RES_DETAIL)
            {
                descIntent.putExtra("restId", jsonObject.getString("rId"));
            }
            else if (notifyId == EamConstant.EAM_NOTIFY_RED_OVERDUE)
            {
                EaseUser user = new EaseUser(jsonObject.getString("imuId"));
                user.setuId(jsonObject.getString("uId"));
                user.setNickName(jsonObject.getString("nicName"));
                descIntent.putExtra(Constant.EXTRA_TO_EASEUSER, user);
            }
            else if (notifyId == EamConstant.EAM_NOTIFY_HP1)
            {
                descIntent.putExtra("orderId", jsonObject.getString("orderId"));
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        Intent broadcastIntent = new Intent(application
                , NotificationReceiver.class);
        broadcastIntent.putExtra("message", imMessage);

        PendingIntent intent = PendingIntent.getBroadcast(application, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        if (isForeground(EamApplication.getInstance()))
//        {
//            Logger.t(TAG).d("前台");
//            intent = PendingIntent.getActivities(application, 0,
//                    new Intent[]{descIntent},
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//        }
//        else
//        {
//            Logger.t(TAG).d("后台");
//            intent = PendingIntent.getActivities(application, 0,
//                    new Intent[]{new Intent(application, HomeAct.class), descIntent},
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//        }
        Intent mainIntent = new Intent(context, HomeAct.class);
        //将MainAtivity的launchMode设置成SingleTask, 或者在下面flag中加上Intent.FLAG_CLEAR_TOP,
        //如果Task栈中有MainActivity的实例，就会把它移到栈顶，把在它之上的Activity都清理出栈，
        //如果Task栈不存在MainActivity实例，则在栈顶创建
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent[] intents = {mainIntent, descIntent};
        if (notifyId == EamConstant.EAM_NOTIFY_RED_OVERDUE)
        {
            if (EamApplication.getInstance().controlChat.size() == 2)
            {
                if (EamApplication.getInstance().controlChat.get(CChatActivity.class.getSimpleName()) != null)
                {
                    EamApplication.getInstance().controlChat.get(CChatActivity.class.getSimpleName()).finish();
                    EamApplication.getInstance().controlChat.clear();
                }
            }
        }
        context.startActivities(intents);
    }

}

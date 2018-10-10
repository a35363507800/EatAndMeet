package com.echoesnet.eatandmeet.receivers;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.activities.liveplay.managers.ViewShareHelper;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by wangben on 2016/7/21.
 */

/*
 * Push消息处理receiver。请编写您需要的回调函数， 一般来说： onBind是必须的，用来处理startWork返回值；
 *onMessage用来接收透传消息； onSetTags、onDelTags、onListTags是tag相关操作的回调；
 *onNotificationClicked在通知被点击时回调； onUnbind是stopWork接口的返回值回调

 * 返回值中的errorCode，解释如下：
 *0 - Success
 *10001 - Network Problem
 *10101  Integrate Check Error
 *30600 - Internal Server Error
 *30601 - Method Not Allowed
 *30602 - Request Params Not Valid
 *30603 - Authentication Failed
 *30604 - Quota Use Up Payment Required
 *30605 -Data Required Not Found
 *30606 - Request Time Expires Timeout
 *30607 - Channel Token Timeout
 *30608 - Bind Relation Not Found
 *30609 - Bind Number Too Many

 * 当您遇到以上返回错误时，如果解释不了您的问题，请用同一请求的返回值requestId和errorCode联系我们追查问题。
 *
 */

public class MyPushMessageReceiver extends PushMessageReceiver
{
    public static final String TAG = MyPushMessageReceiver.class.getSimpleName();

    /**
     * 调用PushManager.startWork后，sdk将对push
     * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
     * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
     *
     * @param context   BroadcastReceiver的执行Context
     * @param errorCode 绑定接口返回值，0 - 成功
     * @param appid     应用id。errorCode非0时为null
     * @param userId    应用user id。errorCode非0时为null
     * @param channelId 应用channel id。errorCode非0时为null
     * @param requestId 向服务端发起的请求id。在追查问题时有用；
     * @return none
     */
    @Override
    public void onBind(Context context, int errorCode, String appid,
                       String userId, String channelId, String requestId)
    {
        String responseString = "onBind errorCode=" + errorCode + " appid="//3838489299470062162
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
        Logger.t(TAG).d(responseString);
        if (errorCode == 0)
        {
            // 绑定成功
            bindBaiduPushToServer(context, channelId);
            Logger.t(TAG).d("绑定成功");
        }
    }

    /**
     * 接收透传消息的函数。
     *
     * @param context             上下文
     * @param message             推送的消息
     * @param customContentString 自定义内容,为空或者json字符串
     */
    @Override
    public void onMessage(Context context, String message,
                          String customContentString)
    {
        String messageString = "透传消息 message=\"" + message
                + "\" customContentString=" + customContentString;
        Logger.t(TAG).d(messageString);
        // 自定义内容获取方式，mykey和myvalue对应透传消息推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString))
        {
            JSONObject customJson = null;
            try
            {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mykey"))
                {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        updateUiInOnMessage(context, message);
    }

    /**
     * 接收通知点击的函数。
     *
     * @param context             上下文
     * @param title               推送的通知的标题
     * @param description         推送的通知的描述
     * @param customContentString 自定义内容，为空或者json字符串
     */
    @Override
    public void onNotificationClicked(Context context, String title,
                                      String description, String customContentString)
    {
        String notifyString = "通知点击 title=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
        Logger.t(TAG).d(notifyString);


//        Intent intent = new Intent();
//        intent.setClass(context.getApplicationContext(), MMyInfoOrderRemindAct.class);
//        intent.setAction("custom.myinfo.orderremind");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        Logger.t(TAG).d(intent.toURI());
//        context.getApplicationContext().startActivity(intent);

        // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
//        if (!TextUtils.isEmpty(customContentString))
//        {
//            JSONObject customJson = null;
//            try
//            {
//                customJson = new JSONObject(customContentString);
//                String myvalue = null;
//                if (!customJson.isNull("mykey"))
//                {
//                    myvalue = customJson.getString("mykey");
//                }
//            } catch (JSONException e)
//            {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * 接收通知到达的函数。
     *
     * @param context             上下文
     * @param title               推送的通知的标题
     * @param description         推送的通知的描述
     * @param customContentString 自定义内容，为空或者json字符串
     */

    @Override
    public void onNotificationArrived(Context context, String title,
                                      String description, String customContentString)
    {

        String notifyString = "onNotificationArrived  title=\"" + title
                + "\" description=\"" + description + "\" customContent="
                + customContentString;
        Logger.t(TAG).d("notifyString-------->" + notifyString);


/*        // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString))
        {
            JSONObject customJson = null;
            try
            {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mykey"))
                {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }*/
    }

    /**
     * setTags() 的回调函数。
     *
     * @param context     上下文
     * @param errorCode   错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
     * @param successTags 设置成功的tag
     * @param failTags    设置失败的tag
     * @param requestId   分配给对云推送的请求的id
     */
    @Override
    public void onSetTags(Context context, int errorCode,
                          List<String> successTags, List<String> failTags, String requestId)
    {
        String responseString = "onSetTags errorCode=" + errorCode
                + " sucessTags=" + successTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Logger.t(TAG).d(responseString);

    }

    /**
     * delTags() 的回调函数。
     *
     * @param context     上下文
     * @param errorCode   错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
     * @param successTags 成功删除的tag
     * @param failTags    删除失败的tag
     * @param requestId   分配给对云推送的请求的id
     */
    @Override
    public void onDelTags(Context context, int errorCode,
                          List<String> successTags, List<String> failTags, String requestId)
    {
        String responseString = "onDelTags errorCode=" + errorCode
                + " sucessTags=" + successTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Logger.t(TAG).d(responseString);

    }

    /**
     * listTags() 的回调函数。
     *
     * @param context   上下文
     * @param errorCode 错误码。0表示列举tag成功；非0表示失败。
     * @param tags      当前应用设置的所有tag。
     * @param requestId 分配给对云推送的请求的id
     */
    @Override
    public void onListTags(Context context, int errorCode, List<String> tags,
                           String requestId)
    {
        String responseString = "onListTags errorCode=" + errorCode + " tags="
                + tags;
        Logger.t(TAG).d(responseString);

    }

    /**
     * PushManager.stopWork() 的回调函数。
     *
     * @param context   上下文
     * @param errorCode 错误码。0表示从云推送解绑定成功；非0表示失败。
     * @param requestId 分配给对云推送的请求的id
     */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId)
    {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
        Logger.t(TAG).d(responseString);

        if (errorCode == 0)
        {
            // 解绑定成功
        }
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
                    showNotify(body.getString("msg"), EamConstant.EAM_NOTIFY_MSG_CENTER, context,message);
                    break;
                case "SYSTEM_INFO":
                    if (null != context)
                    {
                        Intent intent1 = new Intent(EamConstant.ACTION_UPDATE_USER_MSG);
                        intent1.putExtra("needRefreshMsg", true);
                        context.sendBroadcast(intent1);
                    }
                    showNotify(body.getString("msg"), EamConstant.EAM_NOTIFY_SYS_CENTER, context,message);
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
                    showNotify(body.getString("msg"), EamConstant.EAM_NOTIFY_WELGIFT_OVERDUE, context,message);
                    break;
                case "APPLY_FRIEND":
                    showNotify(body.getString("msg"), EamConstant.EAM_NOTIFY_APPLY_FRIEND, context,message);
                    break;
                case "RED_OVERDUE":
                    showNotify(message, EamConstant.EAM_NOTIFY_RED_OVERDUE, context,message);
                    break;
                case "LIVE_NOTICE":
                    if (ViewShareHelper.liveMySelfRole == LiveRecord.ROOM_MODE_MEMBER)
                    {
                        // TODO: 2017/3/14 0014
                        showNotify(message, EamConstant.EAM_NOTIFY_LIVE_PLAY, context,message);
                    }
                    break;
                case "balance":
                    showNotify(body.getString("msg"), EamConstant.EAM_NOTIFY_BALANCE_DETAIL, context,message);
                    break;
                case "CERTIFICATE":
                    showNotify(body.getString("msg"), EamConstant.EAM_NOTIFY_REAL_NAME, context,message);
                    break;
                case "RECEIVE_INFO_USER":
                    showNotify(body.getString("msg"), EamConstant.EAM_NOTIFY_DATE_INFO_USER, context,message);
                    break;
                case "RECEIVE_INFO_ANCHOR":
                    showNotify(body.getString("msg"), EamConstant.EAM_NOTIFY_DATE_INFO_ANCHOR, context,message);
                    break;
                case "TREND_DETAIL":
                    showNotify(message, EamConstant.EAM_NOTIFY_TREND_DETAIL, context,message);
                    break;
                case "TREND_MSG":
                    EamApplication.getInstance().interactionCount = String.valueOf(Integer.parseInt(EamApplication.getInstance().interactionCount) + 1);
                    showNotify(message, EamConstant.EAM_NOTIFY_TREND_DETAIL, context,message);
                    break;
                case "COLUMNREMIND":
                        //推送大V文章红点,并不需要显示通知
                    Intent columnsCountIntent = new Intent(EamConstant.EAM_HX_RECEIVE_BIGV_RED_HOME);
                    context.sendBroadcast(columnsCountIntent);
                    break;
                case "RES_DETAIL":
                    showNotify(message, EamConstant.EAM_NOTIFY_RES_DETAIL, context,message);
                    break;

//                case "HPORDER1":
//                    showNotify(message, EamConstant.EAM_NOTIFY_HP1, context,message);
//                    break;
                case "HPORDER2":
                    showNotify(message, EamConstant.EAM_NOTIFY_HP2, context,message);
                    break;
                default:
                    showNotify(body.getString("msg"), EamConstant.EAM_NOTIFY_DEFAULT_CODE, context,message);
                    break;
            }
        } catch (JSONException e)
        {
            Logger.t(TAG).d("错误信息：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showNotify(String imMessage, int notifyId, Context context,String message)
    {
        Intent broadcastIntent = new Intent(context
                                     ,NotificationReceiver.class);
        broadcastIntent.putExtra("message",message);

        PendingIntent intent = PendingIntent.getBroadcast(context,notifyId,broadcastIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        switch (notifyId)
        {
            case EamConstant.EAM_NOTIFY_LIVE_PLAY:
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(imMessage);
                    imMessage = jsonObject.getString("msg");
                    String  focusTrendsCount = jsonObject.getString("focusTrendsCount");
                    if (!TextUtils.isEmpty(focusTrendsCount))
                    {
                        EamApplication.getInstance().dynamicCount = focusTrendsCount;

                        //更新聊天top栏红点数字
                        Intent focusTrendsIntent = new Intent(EamConstant.EAM_HX_RECEIVE_RED_HOME);
                        context.sendBroadcast(focusTrendsIntent);
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            break;
            case EamConstant.EAM_NOTIFY_DATE_INFO_USER:
            {
                String[] result = imMessage.split(CommonUtils.SEPARATOR);
                imMessage = result[0];
            }
            break;
            case EamConstant.EAM_NOTIFY_DATE_INFO_ANCHOR:
                String[] result = imMessage.split(CommonUtils.SEPARATOR);
                imMessage = result[0];
                break;
        }
        try
        {
            if (notifyId == EamConstant.EAM_NOTIFY_TREND_DETAIL)
            {
                JSONObject jsonObject = new JSONObject(message);
                imMessage = jsonObject.getString("msg");
                String  focusTrendsCount = jsonObject.getString("focusTrendsCount");
                if (!TextUtils.isEmpty(focusTrendsCount))
                {
                    if(jsonObject.getString("uId").equals(SharePreUtils.getUId(context)))
                    {
                        try
                        {
                            if(Integer.parseInt(focusTrendsCount)>Integer.parseInt(EamApplication.getInstance().dynamicCount))
                            {
                                EamApplication.getInstance().dynamicCount = focusTrendsCount;
                            }
                        }catch (NumberFormatException ex)
                        {
                            EamApplication.getInstance().dynamicCount = focusTrendsCount;
                        }

                        //更新聊天top栏红点数字
                        Intent focusTrendsIntent = new Intent(EamConstant.EAM_HX_RECEIVE_RED_HOME);
                        context.sendBroadcast(focusTrendsIntent);
                    }
                }
            }
            else if (notifyId == EamConstant.EAM_NOTIFY_RES_DETAIL)
            {
                JSONObject jsonObject = new JSONObject(imMessage);
                imMessage = jsonObject.getString("msg");
            }
            else if (notifyId == EamConstant.EAM_NOTIFY_RED_OVERDUE)
            {
                JSONObject jsonObject = new JSONObject(imMessage);
                imMessage = jsonObject.getString("msg");
            }
//            else if (notifyId == EamConstant.EAM_NOTIFY_HP1)
//            {
//                JSONObject jsonObject = new JSONObject(imMessage);
//                imMessage = jsonObject.getString("msg");
//            }
            else if (notifyId == EamConstant.EAM_NOTIFY_HP2)
            {
                JSONObject jsonObject = new JSONObject(imMessage);
                imMessage = jsonObject.getString("msg");
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        //设置通知信息
        Notification notification = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setContentTitle("看脸吃饭")
                .setContentText(imMessage)
                .setContentIntent(intent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("《看脸吃饭》消息来了")
                .setWhen(System.currentTimeMillis())
                .build();

        EamApplication.getInstance().notifiBadgerCount();

        //获得通知管理器，通知是一项系统服务
        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        //小米桌面角标在notification设置 在这更新
        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi"))
        {
            try
            {
                Field field = notification.getClass().getDeclaredField("extraNotification");
                Object extraNotification = field.get(notification);
                Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
                method.invoke(extraNotification, 1);
            } catch (Exception e)
            {
                e.printStackTrace();
                Logger.t(TAG).d(e.getMessage());
            }
        }


        //通知
        manager.notify(notifyId, notification);
    }


    /**
     * 将绑定好的百度channelId传给后台
     *
     * @param mContext
     * @param channelId
     */
    private void bindBaiduPushToServer(final Context mContext, String channelId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.channelId, channelId);
        reqParamMap.put(ConstCodeTable.deviceType, "Android");

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                NetHelper.handleNetError(mContext, null, TAG, throwable);
            }

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("传递百度推送channelid成功");
            }
        }, NetInterfaceConstant.UserC_baiduPush, null, reqParamMap);
    }

    private boolean isForeground(Context context)
    {
        if (context != null)
        {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            String currentPackageName = cn.getPackageName();
            if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName()))
            {
                return true;
            }
            return false;
        }
        return false;
    }

}

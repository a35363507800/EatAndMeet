package com.echoesnet.eatandmeet.receivers;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.activities.liveplay.managers.ViewShareHelper;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.hyphenate.chat.EMMipushReceiver;
import com.orhanobut.logger.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 1、PushMessageReceiver 是个抽象类，该类继承了 BroadcastReceiver。<br/>
 * 2、需要将自定义的 DemoMessageReceiver 注册在 AndroidManifest.xml 文件中：
 * <pre>
 * {@code
 *  <receiver
 *      android:name="com.xiaomi.mipushdemo.DemoMessageReceiver"
 *      android:exported="true">
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.RECEIVE_MESSAGE" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.MESSAGE_ARRIVED" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.ERROR" />
 *      </intent-filter>
 *  </receiver>
 *  }</pre>
 * 3、DemoMessageReceiver 的 onReceivePassThroughMessage 方法用来接收服务器向客户端发送的透传消息。<br/>
 * 4、DemoMessageReceiver 的 onNotificationMessageClicked 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法会在用户手动点击通知后触发。<br/>
 * 5、DemoMessageReceiver 的 onNotificationMessageArrived 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法是在通知消息到达客户端时触发。另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数。<br/>
 * 6、DemoMessageReceiver 的 onCommandResult 方法用来接收客户端向服务器发送命令后的响应结果。<br/>
 * 7、DemoMessageReceiver 的 onReceiveRegisterResult 方法用来接收客户端向服务器发送注册命令后的响应结果。<br/>
 * 8、以上这些方法运行在非 UI 线程中。
 */
public class MiPushMessageReceiver extends EMMipushReceiver
{
    private final String TAG = MiPushMessageReceiver.class.getSimpleName();

    private String mRegId;
    private String mTopic;
    private String mAlias;
    private String mAccount;
    private String mStartTime;
    private String mEndTime;

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message)
    {
        Logger.t(TAG).d("========>>>onReceivePassThroughMessage():" + message.toString());


        updateUiInOnMessage(context,message.getContent()) ;

    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message)
    {
        Logger.t(TAG).d("========>>>onNotificationMessageClicked():" + message.toString());
        Intent broadcastIntent = new Intent(context
                ,NotificationReceiver.class);
        broadcastIntent.putExtra("message",message.getContent());
        context.sendBroadcast(broadcastIntent);

    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message)
    {
        Logger.t(TAG).d("========>>>onNotificationMessageArrived():" + message.toString());
        updateUiInOnMessage(context,message.getContent()) ;
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message)
    {
        Logger.t(TAG).d("========>>>onCommandResult():" + message.toString());
        Logger.t(TAG).d("========>>>onCommandIDIDIDIIDIDID():" + MiPushClient.getRegId(context));
        if(message.getCommand().equals("register")&&message.getResultCode()==0)
        {
            Logger.t(TAG).d("绑定小米推送成功");
            bindMiPushToServer(context, MiPushClient.getRegId(context));
        }
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message)
    {
        Logger.t(TAG).d("========>>>onReceiveRegisterResult():" + message.toString());
    }

    @SuppressLint("SimpleDateFormat")
    private static String getSimpleDate()
    {
        return new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date());
    }

    /**
     * 将绑定好的小米channelId传给后台
     *
     * @param mContext
     * @param regId
     */
    private void bindMiPushToServer(final Context mContext, String regId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.regId, regId);
        reqParamMap.put(ConstCodeTable.deviceType, "Android");
        Logger.t(TAG).d("调起小米推送接口");
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                // NetHelper.handleNetError(mContext, null, TAG, throwable);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {

            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("传递小米推送regId成功");
            }

        }, NetInterfaceConstant.UserC_bindXmPush, reqParamMap);
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
                    break;
                case "SYSTEM_INFO":
                    if (null != context)
                    {
                        Intent intent1 = new Intent(EamConstant.ACTION_UPDATE_USER_MSG);
                        intent1.putExtra("needRefreshMsg", true);
                        context.sendBroadcast(intent1);
                    }
                    break;
                case "LIVE_NOTICE":
                    if (ViewShareHelper.liveMySelfRole == LiveRecord.ROOM_MODE_MEMBER)
                    {

                        try
                        {
                            JSONObject jsonObject = new JSONObject(message);
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

                        // TODO: 2017/3/14 0014
                    }
                    break;
                case "TREND_DETAIL":
                    try
                    {
                            JSONObject jsonObject = new JSONObject(message);
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

                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case "TREND_MSG":
                    EamApplication.getInstance().interactionCount = String.valueOf(Integer.parseInt(EamApplication.getInstance().interactionCount) + 1);

                    try
                    {
                        JSONObject jsonObject = new JSONObject(message);
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

                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case "COLUMNREMIND":
                    //推送大V文章红点,并不需要显示通知
                    Intent columnsCountIntent = new Intent(EamConstant.EAM_HX_RECEIVE_BIGV_RED_HOME);
                    context.sendBroadcast(columnsCountIntent);
                    break;
                default:
                    break;
            }
        } catch (JSONException e)
        {
            Logger.t(TAG).d("错误信息：" + e.getMessage());
            e.printStackTrace();
        }
    }

}

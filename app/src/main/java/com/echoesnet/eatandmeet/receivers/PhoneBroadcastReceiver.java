package com.echoesnet.eatandmeet.receivers;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.echoesnet.eatandmeet.utils.EamConstant;
import com.orhanobut.logger.Logger;

/**
 * Created by ben on 2017/3/8.
 */

public class PhoneBroadcastReceiver extends BroadcastReceiver
{
    private static final String TAG = PhoneBroadcastReceiver.class.getSimpleName();
    private  String mIncomingNumber = null;
   // private LocalBroadcastManager broadcastManager;//部分手机不支持本地广播，已知oppo R9--wb

    @Override
    public void onReceive(Context context, Intent intent)
    {
        //broadcastManager = LocalBroadcastManager.getInstance(context);
        // 监听拨打电话,(目前为止，只需要监听接听电话，不监听拨打电话（需要危险权限）)
/*        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL))
        {
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Logger.t(TAG).d("call OUT:" + phoneNumber);
        }
        else*/// 监听来电
        {

            TelephonyManager tManager = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);

            switch (tManager.getCallState())
            {
                //响铃
                case TelephonyManager.CALL_STATE_RINGING:
                    mIncomingNumber = intent.getStringExtra("incoming_number");
                    Logger.t(TAG).d("RINGING :" + mIncomingNumber);
                    break;
                //接起电话
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    context.sendBroadcast(new Intent(EamConstant.ACTION_ANSWER_PHONE));
                    Logger.t(TAG).d("incoming ACCEPT :" + mIncomingNumber);
                    break;
                //挂电话
                case TelephonyManager.CALL_STATE_IDLE:
                    context.sendBroadcast(new Intent(EamConstant.ACTION_HANGUP_PHONE));
                    Logger.t(TAG).d("incoming IDLE");
                    break;
                default:
                    break;
            }
        }
    }
}

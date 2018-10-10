package com.echoesnet.eatandmeet.utils.IMUtils;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.widget.Toast;

import com.echoesnet.eatandmeet.BuildConfig;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.Constants;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.orhanobut.logger.Logger;
import com.tencent.TIMManager;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLog;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static com.echoesnet.eatandmeet.models.bean.ConstCodeTable.deviceId;
import static com.echoesnet.eatandmeet.models.bean.ConstCodeTable.token;
import static com.echoesnet.eatandmeet.models.bean.ConstCodeTable.uId;


/**
 * 初始化
 * 包括imsdk等
 */
public class TXInitBusinessHelper
{
    private static String TAG = "InitBusinessHelper";

    private TXInitBusinessHelper()
    {
    }

    /**
     * 初始化App
     */
    public static void initApp(final Context context)
    {
        //初始化avsdk imsdk
        TIMManager.getInstance().disableBeaconReport();
        ILiveLog.setLogLevel(ILiveLog.TILVBLogLevel.INFO);
        // 初始化ILiveSDK
        ILiveSDK.getInstance().initSdk(context, Constants.SDK_APPID, Constants.ACCOUNT_TYPE);


        ILiveLoginManager.getInstance().setUserStatusListener(new ILiveLoginManager.TILVBStatusListener()
        {
            @Override
            public void onForceOffline(int error, String message)
            {
                switch (error)
                {
                    case ILiveConstants.ERR_KICK_OUT:
                        ILiveLog.w(TAG, "onForceOffline->entered!");
                        ILiveLog.d(TAG, "ilive>设置用户状态信息" + "|" + SharePreUtils.getTlsName(context) + "|" + "on force off line");
                        //context.sendBroadcast(new Intent(TXConstants.TXACCOUNTLOGINOUT));
                        break;
                    case ILiveConstants.ERR_EXPIRE:
                        ILiveLog.w(TAG, "onUserSigExpired->entered!");
                        //context.sendBroadcast(new Intent(TXConstants.TXACCOUNTLOGINOUT));
                        refreshSig(context);
                        break;
                }
            }
        });

        //初始化CrashReport系统  腾讯bug上报功能,非开发环境下打开
        if (!BuildConfig.enableDebugLog)
        {
            TXCrashHandler crashHandler = TXCrashHandler.getInstance();
            crashHandler.init(context);
        }
    }

    public static void setTxImListener()
    {
        /**
         * ILVLiveManager init  ({@link com.tencent.livesdk.liveMgr}) -> return {@link ILVBRoom} init -> "ILiveConstants.NO_ERR;"
         */
        ILVLiveConfig liveConfig = new ILVLiveConfig();
        liveConfig.messageListener(TXMessageEvent.getInstance());
        //  liveConfig.setLiveMsgListener(TXMessageEvent.getInstance());
        ILVLiveManager.getInstance().init(liveConfig);
    }

    /**
     * 重新登陆IM
     */
    private static void reLoginIM(final String tlsName, String tlsSign)
    {
        ILiveLoginManager.getInstance().iLiveLogin(tlsName, tlsSign, new ILiveCallBack()
        {
            @Override
            public void onSuccess(Object data)
            {
                Logger.t(TAG).d("登录ilive" + "|" + tlsName + "|" + "request room id");
            }

            @Override
            public void onError(String module, int errCode, String errMsg)
            {
                Logger.t(TAG).d("登录ilive" + "|" + "tilvblogin failed:" + module + "|" + errCode + "|" + errMsg);
            }
        });
    }

    /**
     * 更新票据
     */
    private static void refreshSig(final Context context)
    {
        // TODO: 2017/3/14 0014
//        final String userId = MySelfInfo.getInstance().getId();
//        if (TextUtils.isEmpty(userId))
//        {
//            Logger.w(TAG, "refreshSig->with empty identifier");
//            return;
//        }
        Map<String, String> reqParams = new ArrayMap<>();
        reqParams.put(token, SharePreUtils.getToken(context));
        reqParams.put(uId, SharePreUtils.getUId(context));
        reqParams.put(deviceId, CommonUtils.getDeviceId(context));
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                try
                {
                    JSONObject body = new JSONObject(response);
                    String tName = body.getString("name");
                    String tSign = body.getString("sign");
                    reLoginIM(tName, tSign);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        },NetInterfaceConstant.LiveC_getTlsSign,reqParams);
    }
}

package com.echoesnet.eatandmeet.utils.IMUtils;

import android.text.TextUtils;

import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuyang on 2016/11/16.
 */

public class TencentHelper
{
    private static final String TAG = TencentHelper.class.getSimpleName();

    public interface TXLoginFinishListener
    {
        void onSuccess(Object data);

        void onDefeat(int errorCode, String msg);
    }

    /**
     * 腾讯登录，调用此函数之前确保本地已经存储了腾讯的name和sign
     *
     * @param fl
     */
    public static void txLogin(final TXLoginFinishListener fl)
    {
        final String tlsName = SharePreUtils.getTlsName(null);
        final String tlsSign = SharePreUtils.getTlsSign(null);
        final String loginUser=ILiveSDK.getInstance().getTIMManger().getLoginUser();
        Logger.t(TAG).d("腾讯name》" + tlsName + "腾讯签名》" + tlsSign+"当前登录用户》"+"loginUser》"+loginUser );
        try
        {
            if (!TextUtils.isEmpty(loginUser)&&loginUser.equals(tlsName))//已经登录过了就不登录了
                return;
            ILiveLoginManager.getInstance().iLiveLogin(tlsName, tlsSign, new ILiveCallBack()
            {
                @Override
                public void onSuccess(Object data)
                {
                    Logger.t(TAG).d("登录ilive" + "|" + tlsName + "|" + "request room id");
                    EamLogger.t("TXIM").writeToDefaultFile("登录ilive" + "|" + tlsName);
                    if (null != fl)
                    {
                        fl.onSuccess(data);
                    }
                    setUserInfoToTxServer();
                }

                @Override
                public void onError(String module, int errCode, String errMsg)
                {
                    Logger.t(TAG).d("登录ilive" + "|" + "tilvblogin failed:" + module + "|" + errCode + "|" + errMsg);
                    EamLogger.t("TXIM").writeToDefaultFile("登录ilive" + "|" + "tilvblogin failed:" + module + "|" + errCode + "|" + errMsg);
                    if (null != fl)
                    {
                        fl.onDefeat(errCode, errMsg);
                    }
                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void setUserInfoToTxServer()
    {
        TIMFriendshipManager.getInstance().setNickName(SharePreUtils.getNicName(null), new TIMCallBack()
        {
            @Override
            public void onError(int i, String s)
            {
                Logger.t(TAG).d("设置昵称失败" + "错误码 " + i + "msg " + s);
            }

            @Override
            public void onSuccess()
            {
                Logger.t(TAG).d("设置昵称成功");
            }
        });
        TIMFriendshipManager.getInstance().setFaceUrl(SharePreUtils.getHeadImg(null), new TIMCallBack()
        {
            @Override
            public void onError(int i, String s)
            {
                Logger.t(TAG).d("设置头像失败" + "错误码 " + i + "msg " + s);
            }

            @Override
            public void onSuccess()
            {
                Logger.t(TAG).d("设置头像成功>" + SharePreUtils.getHeadImg(null));
            }
        });
    }

    public static void setUserInfoToCustom1TxServer(String key, String dataStr, TIMCallBack callBack)
    {
        Map<String, String> map = new HashMap<>();
        map.put(key, dataStr);
        setUserInfoToCustom1TxServer(map, callBack);
    }

    public static void setUserInfoToCustom1TxServer(Map<String, String> map, TIMCallBack callBack)
    {
        final String tlsName = SharePreUtils.getTlsName(null);
        final String loginUser=ILiveSDK.getInstance().getTIMManger().getLoginUser();
        if (TextUtils.isEmpty(loginUser)||!loginUser.equals(tlsName))
            return;
        final String json = new Gson().toJson(map);
        if (callBack == null)
            callBack = new TIMCallBack()
            {
                @Override
                public void onError(int i, String s)
                {
                    Logger.t(TAG).d("设置自定义字段失败" + "错误码 " + i + "msg " + s);
                }

                @Override
                public void onSuccess()
                {
                    Logger.t(TAG).d("设置自定义字段success:" + json);
                }
            };
        //存入的数据大小为500K以内，开发者请注意！--wb
        TIMFriendshipManager.getInstance().setCustomInfo(TXConstants.TX_CUSTOM_INFO_1, json.getBytes(Charset.forName("utf-8")), callBack);
    }
}

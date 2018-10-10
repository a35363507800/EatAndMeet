package com.echoesnet.eatandmeet.presenters;

import android.os.Build;
import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.activities.LoginAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.ILoginPre;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EncryptSHA1;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.orhanobut.logger.Logger;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;

/**
 * Created by Administrator on 2016/12/28.
 */
public class ImpILoginView extends BasePresenter<LoginAct> implements ILoginPre
{
    private static final String TAG = ImpILoginView.class.getSimpleName();

    @Override
    public void getTokenId(final int type)
    {
        Map<String, String> reqParamMap = new HashMap<>();
        reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(getView()));
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().getTokenIdCallback(response, type);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView()!=null)
                    getView().callServerFailCallback(NetInterfaceConstant.UserC_getTokenId,apiE.getErrorCode(),apiE.getErrBody());
            }
        }, NetInterfaceConstant.UserC_getTokenId, reqParamMap);
    }

    /**
     * 登录
     */
    @Override
    public void login(String mobile, String passWord)
    {
        try
        {
            String deviceInfo = Build.MANUFACTURER + " " + Build.MODEL;//生产厂商加型号
            Map<String, String> reqParamMap = new ArrayMap<>();
            reqParamMap.put(ConstCodeTable.mobile, mobile);
            reqParamMap.put(ConstCodeTable.pwd, EncryptSHA1.SHA1(passWord));
            reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(null));
            reqParamMap.put(ConstCodeTable.channelId, "");
            reqParamMap.put(ConstCodeTable.device, deviceInfo);
            reqParamMap.put(ConstCodeTable.deviceType, "Android");
            reqParamMap.put(ConstCodeTable.token, "");//登录token传入空字符串即可
            HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
            {
                @Override
                public void onNext(String response)
                {
                    super.onNext(response);
                    if (getView() != null)
                        getView().loginCallback(response);
                }

                @Override
                public void onHandledError(ApiException apiE)
                {
                    super.onHandledError(apiE);
                    if (getView()!=null)
                        getView().callServerFailCallback(NetInterfaceConstant.UserC_signIn,apiE.getErrorCode(),apiE.getErrBody());
                }
            }, NetInterfaceConstant.UserC_signIn, reqParamMap);
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 微信登录
     */
    @Override
    public void weChatLogin(Platform platform)
    {
        try
        {
            String deviceInfo = Build.MANUFACTURER + " " + Build.MODEL;//生产厂商加型号
            Map<String, String> reqParamMap = new ArrayMap<>();
            reqParamMap.put(ConstCodeTable.access_token, platform.getDb().getToken());
            reqParamMap.put(ConstCodeTable.refresh_token, platform.getDb().get("refresh_token"));
            reqParamMap.put(ConstCodeTable.unionid, platform.getDb().get("unionid"));
            reqParamMap.put(ConstCodeTable.openId, platform.getDb().getUserId());
            reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(null));
            reqParamMap.put(ConstCodeTable.channelId, "");
            reqParamMap.put(ConstCodeTable.device, deviceInfo);
            reqParamMap.put(ConstCodeTable.deviceType, "Android");
            HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
            {
                @Override
                public void onNext(String response)
                {
                    super.onNext(response);
                    if (getView() != null)
                        getView() .weChatLoginCallback(response);
                }

                @Override
                public void onHandledError(ApiException apiE)
                {
                    super.onHandledError(apiE);
                    if (getView() !=null)
                        getView() .callServerFailCallback(NetInterfaceConstant.WeChatC_weChatLogin,apiE.getErrorCode(),apiE.getErrBody());
                }
            }, NetInterfaceConstant.WeChatC_weChatLogin, reqParamMap);

        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        }
    }

}

package com.echoesnet.eatandmeet.presenters;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IWeChatMakeInfoView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/2/20.
 */

public class ImpIWeChatMakeInfoView
{
    private static final String TAG = ImpIWeChatMakeInfoView.class.getSimpleName();
    private Context mContext;
    private IWeChatMakeInfoView weChatMakeInfoView;

    public ImpIWeChatMakeInfoView(Context mContext, IWeChatMakeInfoView weChatMakeInfoView)
    {
        this.mContext = mContext;
        this.weChatMakeInfoView = weChatMakeInfoView;
    }

    /**
     * 验证验证码
     *
     * @param mobile 电话
     * @param code   验证码
     * @param type   类型，主要用于不同地方发送验证码，例如登录，注册等
     */
    public void validSecurityCode(String mobile, String code, String type, String tokenId)
    {
        ArrayMap<String, String> reqParamMap = new ArrayMap<>();
        reqParamMap.put(ConstCodeTable.mobile, mobile);
        reqParamMap.put(ConstCodeTable.code, code);
        reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(mContext));
        reqParamMap.put(ConstCodeTable.token, tokenId);
        reqParamMap.put(ConstCodeTable.type, type);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (weChatMakeInfoView != null)
                    weChatMakeInfoView.validSecurityCodeCallback(response);
            }
        },NetInterfaceConstant.UserC_validCodes,reqParamMap);
    }

    public void weChatDetail(String unionid, String nicName, String phUrl, String genderStr, String mobile, String birthday, final String username, final String psw)
    {
        ArrayMap<String, String> reqParamMap = new ArrayMap<>();
        reqParamMap.put(ConstCodeTable.unionid, unionid);
        reqParamMap.put(ConstCodeTable.nicName, nicName);
        reqParamMap.put(ConstCodeTable.phurl, phUrl);
        reqParamMap.put(ConstCodeTable.sex, genderStr);
        reqParamMap.put(ConstCodeTable.mobile, mobile);
        reqParamMap.put(ConstCodeTable.birth, birthday);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                ArrayMap<String, Object> map = new ArrayMap<String, Object>();
                map.put("response", response);
                map.put("username", username);
                map.put("psw", psw);
                if (weChatMakeInfoView != null)
                    weChatMakeInfoView.weChatDetailCallback(map);
            }
        },NetInterfaceConstant.WeChatC_weChatDetail,reqParamMap);
    }

    /**
     * 保存环信账号到后台,并登陆
     *
     * @param userName
     * @param psw
     */
    public void addHXAccountToServerAndLogin(final String userName, final String psw)
    {
        final Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.imuId, userName);
        reqParamMap.put(ConstCodeTable.imPass, psw);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                ArrayMap<String, Object> map = new ArrayMap<String, Object>();
                map.put("response", response);
                map.put("userName", userName);
                map.put("psw", psw);
                if (weChatMakeInfoView != null)
                    weChatMakeInfoView.addHXAccountToServerAndLoginCallback(map);
            }
        }, NetInterfaceConstant.UserC_addImu, reqParamMap);

    }

}

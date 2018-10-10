package com.echoesnet.eatandmeet.presenters;

import android.text.TextUtils;

import com.echoesnet.eatandmeet.activities.RegisterAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IRegisterPre;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EncryptSHA1;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.orhanobut.logger.Logger;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author       ben
 * @modifier
 * @createDate   2016/12/28
 * @version      1.0
 * @description
 */
public class ImpIRegisterView extends BasePresenter<RegisterAct> implements IRegisterPre
{
    private static final String TAG = ImpIRegisterView.class.getSimpleName();


    @Override
    public void validSecurityCode(String mobile, String code, String type, String tokenId)
    {
        final RegisterAct act=getView();
        if (act==null)
            return;
        Map<String, String> reqParamMap = new HashMap<>();
        reqParamMap.put(ConstCodeTable.mobile, mobile);
        reqParamMap.put(ConstCodeTable.code, code);
        reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(act));
        if (TextUtils.isEmpty(tokenId))
            reqParamMap.put(ConstCodeTable.token, SharePreUtils.getToken(act));
        else
            reqParamMap.put(ConstCodeTable.token, tokenId);
        reqParamMap.put(ConstCodeTable.type, type);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (act != null)
                    act.validSecurityCodeCallback(response);
            }
        },NetInterfaceConstant.UserC_validCodes,reqParamMap);
    }

    @Override
    public void register(String mobile, String password, String tokenId)
    {
        final RegisterAct act=getView();
        if (act==null)
            return;
        Map<String, String> reqParamMap = new HashMap<>();
        reqParamMap.put(ConstCodeTable.mobile, mobile);
        try
        {
            reqParamMap.put(ConstCodeTable.pwd, EncryptSHA1.SHA1(password));
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(tokenId))
            reqParamMap.put(ConstCodeTable.token, SharePreUtils.getToken(act));
        else
            reqParamMap.put(ConstCodeTable.token, tokenId);
        reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(act));
        reqParamMap.put(ConstCodeTable.marketChannelId, CommonUtils.getMetaValue(act, "BaiduMobAd_CHANNEL"));
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (act != null)
                    act.registerCallback(response);
            }
        },NetInterfaceConstant.UserC_register,reqParamMap);
    }
}

package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.echoesnet.eatandmeet.activities.ForgetPasswordAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IForgetPasswordView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EncryptSHA1;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/1/6.
 */

public class ImpIForgetPassWordView extends BasePresenter<IForgetPasswordView>
{
    private final String TAG = ImpIForgetPassWordView.class.getSimpleName();

    public void getPassword(String mobile, String newPw)
    {
        final IForgetPasswordView forgetPasswordView = getView();
        if (forgetPasswordView == null)
        {
            return;
        }
        Activity mAct = (ForgetPasswordAct) getView();
        Map<String, String> reqParamMap = new HashMap<>();
        reqParamMap.put(ConstCodeTable.mobile, mobile);
        reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(mAct));
        try
        {
            reqParamMap.put(ConstCodeTable.np, EncryptSHA1.SHA1(newPw));
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (forgetPasswordView != null)
                    forgetPasswordView.getPasswordCallback(response);
            }
        },NetInterfaceConstant.UserC_forgetPwd,reqParamMap);
    }

    //验证验证码
    public void validSecurityCode(final String mobile, String code, String type, String tokenId)
    {
        final IForgetPasswordView forgetPasswordView = getView();
        if (forgetPasswordView == null)
        {
            return;
        }
        Activity mAct = (ForgetPasswordAct) getView();
        Map<String, String> reqParamMap = new HashMap<>();
        reqParamMap.put(ConstCodeTable.mobile, mobile);
        reqParamMap.put(ConstCodeTable.code, code);
        reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(mAct));
        if (TextUtils.isEmpty(tokenId))
            reqParamMap.put(ConstCodeTable.token, SharePreUtils.getToken(mAct));
        else
            reqParamMap.put(ConstCodeTable.token, tokenId);
        reqParamMap.put(ConstCodeTable.type, type);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                ArrayMap<String, Object> map = new ArrayMap<String, Object>();
                map.put("mobile", mobile);
                map.put("response", response);
                if (forgetPasswordView != null)
                    forgetPasswordView.validSecurityCodeCallback(map);
            }
        },NetInterfaceConstant.UserC_validCodes,reqParamMap);
    }

}

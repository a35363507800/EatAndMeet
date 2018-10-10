package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMySetVerifyOldPayPwView;
import com.echoesnet.eatandmeet.utils.EncryptSHA1;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/6.
 */

public class ImpIMySetVerifyOldPayPwView
{
    private final String TAG = ImpIMySetVerifyOldPayPwView.class.getSimpleName();
    private Context mContext;
    private IMySetVerifyOldPayPwView setVerifyOldPayPwView;

    public ImpIMySetVerifyOldPayPwView(Context mContext, IMySetVerifyOldPayPwView setVerifyOldPayPwView)
    {
        this.mContext = mContext;
        this.setVerifyOldPayPwView = setVerifyOldPayPwView;
    }

    /**
     * 验证支付密码
     */
    public void verifyPayPassword(String payPw)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        try
        {
            reqParamMap.put(ConstCodeTable.pwd, EncryptSHA1.SHA1(payPw));
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
                if (setVerifyOldPayPwView != null)
                    setVerifyOldPayPwView.verifyPayPasswordCallback(response);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (setVerifyOldPayPwView!=null)
                    setVerifyOldPayPwView.callServerErrorCallback(NetInterfaceConstant.UserC_validPwd,apiE.getErrorCode(),apiE.getErrBody());
            }
        },NetInterfaceConstant.UserC_validPwd,reqParamMap);
    }
}

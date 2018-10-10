package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyChangeLoginPwView;
import com.echoesnet.eatandmeet.utils.EncryptSHA1;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/6.
 */

public class ImpIMyChangeLoginPwView
{
    private final String TAG = ImpIMyChangeLoginPwView.class.getSimpleName();
    private Context mContext;
    private IMyChangeLoginPwView changeLoginPwView;

    public ImpIMyChangeLoginPwView(Context mContext, IMyChangeLoginPwView changeLoginPwView)
    {
        this.mContext = mContext;
        this.changeLoginPwView = changeLoginPwView;
    }

    /**
     * 重置登录密码
     *
     * @param olePw
     * @param newPw
     */
    public void resetLoginPassword(String olePw, String newPw)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        try
        {
            reqParamMap.put(ConstCodeTable.pwd, EncryptSHA1.SHA1(olePw));
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
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (changeLoginPwView != null)
                    changeLoginPwView.callServerErrorCallback(NetInterfaceConstant.UserC_rePwd,apiE.getErrorCode(), apiE.getErrBody());
            }


            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (changeLoginPwView != null)
                    changeLoginPwView.resetLoginPasswordCallback(response);
            }
        },NetInterfaceConstant.UserC_rePwd,reqParamMap);
    }
}

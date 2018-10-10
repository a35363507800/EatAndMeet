package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMySetAccountSecurityView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;

import java.util.Map;

/**
 * Created by Administrator on 2017/1/6.
 */

public class ImpIMySetAccountSecurityView
{
    private final String TAG = ImpIMySetAccountSecurityView.class.getSimpleName();
    private Context mContext;
    private IMySetAccountSecurityView accountSecurityView;

    public ImpIMySetAccountSecurityView(Context mContext, IMySetAccountSecurityView accountSecurityView)
    {
        this.mContext = mContext;
        this.accountSecurityView = accountSecurityView;
    }

    /**
     * 验证支付密码是否设置
     */
    public void checkPayPasswordState()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (accountSecurityView != null)
                    accountSecurityView.checkPayPasswordStateCallback(response);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (accountSecurityView!=null)
                    accountSecurityView.callServerErrorCallback(NetInterfaceConstant.UserC_payPwd,apiE.getErrorCode(),apiE.getErrBody());
            }
        },NetInterfaceConstant.UserC_payPwd,reqParamMap);
    }
}

package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.presenters.viewinterface.IRechargeResultView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;

import java.util.Map;

/**
 * Created by wangben on 2016/11/2.
 */

public class ImpIRechargeResultView
{
    private static final String TAG = ImpIRechargeResultView.class.getSimpleName();
    private IRechargeResultView mRechargeInter;
    private Context mContext;

    public ImpIRechargeResultView(Context context, IRechargeResultView rechargeInterface)
    {
        this.mContext=context;
        this.mRechargeInter=rechargeInterface;
    }
    public void getAccountBalance()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (mRechargeInter!=null)
                mRechargeInter.getAccountBalanceCallback(response);
            }
        },NetInterfaceConstant.UserC_newBalance,reqParamMap);
    }
}

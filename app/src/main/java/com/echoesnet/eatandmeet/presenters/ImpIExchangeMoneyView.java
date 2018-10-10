package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IExchangeMoneyView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/12.
 */

public class ImpIExchangeMoneyView
{
    private final String TAG = ImpIExchangeMoneyView.class.getSimpleName();
    private Context mContext;
    private IExchangeMoneyView exchangeMoneyView;

    public ImpIExchangeMoneyView(Context mContext, IExchangeMoneyView exchangeMoneyView)
    {
        this.mContext = mContext;
        this.exchangeMoneyView = exchangeMoneyView;
    }


    public void getMyMeal()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (exchangeMoneyView!=null)
                    exchangeMoneyView.getMyMealCallBack(response);
            }
        },NetInterfaceConstant.WithdrawC_myMeal,reqParamMap);
    }

    public void exchangeToBalance(String withdraw)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.withdraw, withdraw);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (exchangeMoneyView!=null)
                    exchangeMoneyView.exchangeToBalanceCallBack(response);
            }
        },NetInterfaceConstant.WithdrawC_exchangeToBalance,reqParamMap);
    }

}

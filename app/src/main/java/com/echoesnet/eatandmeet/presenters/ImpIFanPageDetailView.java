package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.activities.live.MyInfoFanPageDetailActivity;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.presenters.viewinterface.IFanPageDetailView;
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

public class ImpIFanPageDetailView extends BasePresenter<IFanPageDetailView>
{
    private final String TAG = ImpIFanPageDetailView.class.getSimpleName();

    public void getMyBalance()
    {
        final IFanPageDetailView fanPageDetailView = getView();
        if (fanPageDetailView == null)
            return;
        Context mContext = (MyInfoFanPageDetailActivity) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (fanPageDetailView!=null)
                    fanPageDetailView.getMyBalanceCallBack(response);
            }
        },NetInterfaceConstant.WithdrawC_myBalance,reqParamMap);
    }
    /**
     * 获取绑定状态
     */
    public void getBindStats()
    {
        final IFanPageDetailView fanPageDetailView = getView();
        if (fanPageDetailView == null)
            return;
        Context mContext = (MyInfoFanPageDetailActivity) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (fanPageDetailView!=null)
                    fanPageDetailView.callServerErrorCallback(NetInterfaceConstant.LiveC_validate,apiE.getErrorCode(),apiE.getErrBody());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (fanPageDetailView!=null)
                    fanPageDetailView.getBindStatsCallBack(response);
            }
        },NetInterfaceConstant.LiveC_validate,reqParamMap);
    }
}

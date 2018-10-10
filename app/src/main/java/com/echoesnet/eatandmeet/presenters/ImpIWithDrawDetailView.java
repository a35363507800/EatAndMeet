package com.echoesnet.eatandmeet.presenters;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.activities.live.MyWithDrawBalanceDetailAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.WithDrawBalanceDetailBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IWithDrawDetailView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/12.
 */

public class ImpIWithDrawDetailView extends BasePresenter<IWithDrawDetailView>
{
    private final String TAG = ImpIWithDrawDetailView.class.getSimpleName();

    public void getBalanceDetailData(final String startIdx, final String num, final String operateType)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.startIdx, startIdx);
        reqParamMap.put(ConstCodeTable.num, num);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if(getView()!=null)
                getView().requestNetErrorCallback(NetInterfaceConstant.LiveC_withdrawDetail,throwable);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                List<WithDrawBalanceDetailBean> resLst = new Gson().fromJson(response, new TypeToken<List<WithDrawBalanceDetailBean>>()
                {
                }.getType());
                if(getView()!=null)
                getView().getBalanceDetailDataCallBack(resLst,operateType);

            }
        },NetInterfaceConstant.LiveC_withdrawDetail,reqParamMap);


    }

}

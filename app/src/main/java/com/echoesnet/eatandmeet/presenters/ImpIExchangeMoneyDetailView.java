package com.echoesnet.eatandmeet.presenters;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.activities.ExchangeRecordDetailActivity;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.ExchangeRecordDetailBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IExchangeMoneyDetailView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/3.
 */

public class ImpIExchangeMoneyDetailView extends BasePresenter<IExchangeMoneyDetailView>
{
    private final String TAG = ImpIExchangeMoneyDetailView.class.getSimpleName();

    public void getExchangeMoneyDetail(String startIdx, String num, final String type)
    {
        final IExchangeMoneyDetailView exchangeMoneyDetailView = getView();
        if (exchangeMoneyDetailView == null)
        {
            return;
        }
        Context mContext = (ExchangeRecordDetailActivity) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.startIdx, startIdx);
        reqParamMap.put(ConstCodeTable.num, num);


        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);

                ArrayMap<String, Object> map = new ArrayMap<>();
                List<ExchangeRecordDetailBean> detailList = new Gson().fromJson(response, new TypeToken<List<ExchangeRecordDetailBean>>(){}.getType());
                if(detailList!=null)
                    map.put("detailList",detailList);
                map.put("type", type);
                if(exchangeMoneyDetailView!=null)
                exchangeMoneyDetailView.exchangeRecordDetailCallBack(map);
            }
        },NetInterfaceConstant.WithdrawC_exchangeRecord,reqParamMap);

    }

}

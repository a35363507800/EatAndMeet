package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.SearchRestaurantBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDResSearchView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/12.
 */

public class ImpIDResSearchView extends BasePresenter<IDResSearchView>
{
    private final String TAG = ImpIDResSearchView.class.getSimpleName();

    public void getResList(String startIdx, String num, final String operateType, String keyword)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.kw, keyword);
        reqParamMap.put(ConstCodeTable.num, num);
        reqParamMap.put(ConstCodeTable.startIdx, startIdx);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                List<SearchRestaurantBean> resLst = new Gson().fromJson(response, new TypeToken<List<SearchRestaurantBean>>()
                {
                }.getType());
                if ( getView()!= null)
                    getView().getResListCallback(resLst, operateType);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView()!=null)
                    getView().callServerErrorCallback(NetInterfaceConstant.RestaurantC_searchRes,apiE.getErrorCode(),apiE.getErrBody());
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (getView()!=null)
                    getView().requestNetErrorCallback(NetInterfaceConstant.RestaurantC_searchRes,throwable);
            }
        },NetInterfaceConstant.RestaurantC_searchRes,reqParamMap);
    }
}

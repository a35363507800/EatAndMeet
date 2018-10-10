package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;

import com.echoesnet.eatandmeet.activities.DOrderSearchAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.OrderedDishItemBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDOrderSearchView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/15.
 */

public class ImpDOrderSearchView extends BasePresenter<IDOrderSearchView>
{
    private final String TAG = ImpDOrderSearchView.class.getSimpleName();

    public void getDishList(String rid, String content)
    {

        final IDOrderSearchView idOrderSearchView = getView();
        if (idOrderSearchView == null)
            return;
        Activity mAct = (DOrderSearchAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.rId, rid);
        reqParamMap.put(ConstCodeTable.kw, content);
        reqParamMap.put(ConstCodeTable.num, String.valueOf(6));


        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);

                List<OrderedDishItemBean> orderLst = new ArrayList<>();
                orderLst = new Gson().fromJson(response, new TypeToken<List<OrderedDishItemBean>>(){}.getType());

                if (idOrderSearchView != null)
                    idOrderSearchView.getDishListCallback(orderLst);
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (idOrderSearchView != null)
                    idOrderSearchView.requestNetErrorCallback(NetInterfaceConstant.DishC_dishSearch,throwable);
            }
        },NetInterfaceConstant.DishC_dishSearch,reqParamMap);

    }
}

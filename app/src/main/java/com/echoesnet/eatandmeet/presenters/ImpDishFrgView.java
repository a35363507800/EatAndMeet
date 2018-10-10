package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.DishRightMenuGroupBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDishFrgView;
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

public class ImpDishFrgView
{
    private final String TAG = ImpDishFrgView.class.getSimpleName();
    private Activity mAct;
    private IDishFrgView iDishFrgView;

    public ImpDishFrgView(Activity mAct, IDishFrgView iDishFrgView)
    {
        this.mAct = mAct;
        this.iDishFrgView = iDishFrgView;
    }

    public void getDishList(String rid)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.num, String.valueOf(20));
        reqParamMap.put(ConstCodeTable.rId, rid);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);

                ArrayList<DishRightMenuGroupBean> orderLst = new ArrayList<>();

                orderLst = new Gson().fromJson(response, new TypeToken<List<DishRightMenuGroupBean>>()
                {}.getType());

                if (iDishFrgView != null)
                    iDishFrgView.getDishListCallback(orderLst);
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (iDishFrgView != null)
                    iDishFrgView.requestNetErrorCallback(NetInterfaceConstant.DishC_dishInfo,throwable);
            }
        },NetInterfaceConstant.DishC_dishInfo,reqParamMap);

    }
}

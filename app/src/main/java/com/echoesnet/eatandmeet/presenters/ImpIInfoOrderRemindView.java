package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;
import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IInfoOrderRemindView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by an on 2016/12/8 0008.
 */

public class ImpIInfoOrderRemindView extends BasePresenter<IInfoOrderRemindView>
{
    private final static String TAG = ImpIInfoOrderRemindView.class.getSimpleName();


    // zdw --- 添加中间件修改

    /**
     * 获得订单详情
     *
     * @param orderId
     */
    public void getOrderDetail(String orderId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.orderId, orderId);


        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (isViewAttached())
                    getView().requestNetErrorCallback(NetInterfaceConstant.OrderC_orderDetail,throwable);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (isViewAttached())
                    getView().getOrderDetailSuccess(response);
            }
        },NetInterfaceConstant.OrderC_orderDetail,reqParamMap);

    }

    /**
     * 订餐提醒
     *
     * @param getItemStartIndex
     * @param getItemNum
     * @param isPullDown
     */
    public void initData(final String getItemStartIndex, String getItemNum, final boolean isPullDown)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.num, getItemNum);
        reqParamMap.put(ConstCodeTable.startIdx, getItemStartIndex);



        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (isViewAttached())
                    getView().requestNetErrorCallback(NetInterfaceConstant.MsgC_diningRemind,throwable);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (isViewAttached())
                    getView().initDataSuccess(response,isPullDown);

            }
        },NetInterfaceConstant.MsgC_diningRemind,reqParamMap);

    }

}

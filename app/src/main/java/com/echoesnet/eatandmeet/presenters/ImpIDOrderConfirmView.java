package com.echoesnet.eatandmeet.presenters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.echoesnet.eatandmeet.activities.DOrderConfirmAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.OrderBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDOrderConfirmView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.Map;

/**
 * Created by Administrator on 2017/1/5.
 */

public class ImpIDOrderConfirmView extends BasePresenter<IDOrderConfirmView>
{
    private final String TAG = ImpIDOrderConfirmView.class.getSimpleName();

    /**
     * 检验菜品价格是否变化
     */
    public void checkPrice()
    {
        final IDOrderConfirmView idOrderConfirmView = getView();
        if (idOrderConfirmView == null)
        {
            return;
        }
        Context mContext = (DOrderConfirmAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put("dishBean", new Gson().toJson(OrderBean.getOrderBeanInstance().getDishBeen()));

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (idOrderConfirmView!=null)
                    idOrderConfirmView.callServerErrorCallback(NetInterfaceConstant.DishC_checkPrice,apiE.getErrorCode(),apiE.getErrBody(),"");
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (idOrderConfirmView != null)
                    idOrderConfirmView.checkPriceCallback(response);
            }
        },NetInterfaceConstant.DishC_checkPrice,reqParamMap);
    }

    /**
     * 向后台提交正常订单
     *
     * @param view             支付框依附的View
     * @param dishPriceChangeD 订单中的菜品价格是否有变化 0：无；1：有
     * @param orderBean
     */
    public void postOrderToServer(final View view, final String dishPriceChangeD, OrderBean orderBean, String consultant)
    {
        final IDOrderConfirmView idOrderConfirmView = getView();
        if (idOrderConfirmView == null)
        {
            return;
        }
        Context mContext = (DOrderConfirmAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.consultant, consultant);
        reqParamMap.put("orderBean", new Gson().toJson(orderBean, OrderBean.class));

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                    if (idOrderConfirmView!=null)
                        idOrderConfirmView.callServerErrorCallback(NetInterfaceConstant.OrderC_smtOrder,apiE.getErrorCode(),apiE.getErrBody(),dishPriceChangeD);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (idOrderConfirmView!=null)
                    idOrderConfirmView.postOrderToServerCallback(response, view, dishPriceChangeD);
            }
        },NetInterfaceConstant.OrderC_smtOrder,reqParamMap);
    }

    /**
     * 向后台提交约主播的订单
     *
     * @param view 支付框依附的View
     */
    public void postOrderToServer2(final View view, final String change, OrderBean orderBean, String streamId, String consultant)
    {
        final IDOrderConfirmView idOrderConfirmView = getView();
        if (idOrderConfirmView == null)
        {
            return;
        }
        Context mContext = (DOrderConfirmAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.streamId, streamId);
        reqParamMap.put(ConstCodeTable.consultant, consultant);

        reqParamMap.put("orderBean", new Gson().toJson(OrderBean.getOrderBeanInstance(), OrderBean.class));

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {

            @Override
            public void onHandledError(ApiException apiE)
            {
                if (idOrderConfirmView!=null)
                    idOrderConfirmView.callServerErrorCallback(NetInterfaceConstant.OrderC_receiveSmtOrder,apiE.getErrorCode(),apiE.getErrBody(),change);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (idOrderConfirmView != null)
                    idOrderConfirmView.postOrderToServerCallback2(response, view, change);
            }
        },NetInterfaceConstant.OrderC_receiveSmtOrder,reqParamMap);
    }

    /**
     * 查询就餐顾问
     */
    public void queryConsultant(String consultant)
    {
        final IDOrderConfirmView idOrderConfirmView = getView();
        if (idOrderConfirmView == null)
        {
            return;
        }
        Context mContext = (DOrderConfirmAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.id, consultant);
        reqParamMap.put(ConstCodeTable.consultant, "");

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (idOrderConfirmView != null)
                    idOrderConfirmView.queryMyConsultantCallback(response);
            }
        },NetInterfaceConstant.ConsultantC_queryConsultant,reqParamMap);
    }

    /**
     * 获取就餐顾问
     */
    public void getMyConsultant()
    {
        final IDOrderConfirmView idOrderConfirmView = getView();
        if (idOrderConfirmView == null)
        {
            return;
        }
        Context mContext = (DOrderConfirmAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                if (idOrderConfirmView!=null)
                    idOrderConfirmView.callServerErrorCallback(NetInterfaceConstant.ConsultantC_myConsultant,apiE.getErrorCode(),apiE.getErrBody(),"");
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (idOrderConfirmView != null)
                    idOrderConfirmView.getMyConsultantCallback(response);
            }
        },NetInterfaceConstant.ConsultantC_myConsultant,reqParamMap);
    }

    /**
     * 订餐前校验有没有约会
     */
    public void orderCheck(String date)
    {
        final IDOrderConfirmView idOrderConfirmView = getView();
        if (idOrderConfirmView == null)
        {
            return;
        }
        Context mContext = (DOrderConfirmAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.date,date);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (idOrderConfirmView!=null)
                    idOrderConfirmView.callServerErrorCallback(NetInterfaceConstant.AppointmentC_orderCheck,apiE.getErrorCode(),apiE.getErrBody(),"");
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (idOrderConfirmView != null)
                    idOrderConfirmView.orderCheckCallback(response,date);
            }
        },NetInterfaceConstant.AppointmentC_orderCheck,reqParamMap);
    }

}

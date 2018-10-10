package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.activities.ClubOrderRecordDetailAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ClubOrderDetailBean;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.MyResCommentBean;
import com.echoesnet.eatandmeet.models.bean.OrderRecordBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IClubOrderRecordDetailPre;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.Map;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2018/2/8
 * @description
 */
public class ImpIClubOrderRecordDetailPre extends BasePresenter<ClubOrderRecordDetailAct> implements IClubOrderRecordDetailPre
{
    private final static String TAG = ImpIClubOrderRecordDetailPre.class.getSimpleName();

    /**
     * 获得轰趴订单详情
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
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("订单详情>>"+response);
                ClubOrderDetailBean orderRecord = new Gson().fromJson(response, ClubOrderDetailBean.class);
                if (getView()!=null)
                {
                    getView().getOrderDetailSuccess(orderRecord);
                }
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView()!=null)
                    getView().callServerErrorCallback(NetInterfaceConstant.HomepartyC_partyOrderDetails,apiE.getErrorCode(),apiE.getErrBody());
            }
        },NetInterfaceConstant.HomepartyC_partyOrderDetails,reqParamMap);
    }

    //删除订单
    public void deleteOrder(String orderId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.orderId, orderId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().getDeleteOrderSuccess(response);
            }
        }, NetInterfaceConstant.HomepartyC_delOrder, reqParamMap);
    }

    /**
     * 获取评价详情
     *
     * @param orderId
     */
    public void getResCommentDetail(String orderId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.orderId, orderId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                MyResCommentBean resComment = new Gson().fromJson(response, MyResCommentBean.class);
                if (getView()!=null)
                    getView().getResCommentDetailSuccess(resComment);
            }
        },NetInterfaceConstant.EvalC_checkEval,reqParamMap);
    }

    /**
     * 申请退款
     *
     * @param orderId 订单号
     */
    public void applyRefund(final String orderId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.orderId, orderId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                    getView().callServerErrorCallback(NetInterfaceConstant.HomepartyC_applyRefund, apiE.getErrorCode(), apiE.getErrBody());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().getApplyRefundSuccess(response);
            }
        }, NetInterfaceConstant.HomepartyC_applyRefund, reqParamMap);
    }
}

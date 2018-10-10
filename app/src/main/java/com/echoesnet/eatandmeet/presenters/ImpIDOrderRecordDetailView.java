package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.MyResCommentBean;
import com.echoesnet.eatandmeet.models.bean.OrderRecordBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDOrderRecordDetailView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.Map;

/**
 * Created by an on 2016/12/8 0008.
 */

public class ImpIDOrderRecordDetailView extends BasePresenter<IDOrderRecordDetailView>
{
    private final static String TAG = ImpIDOrderRecordDetailView.class.getSimpleName();

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
            public void onNext(String response)
            {
                super.onNext(response);
                OrderRecordBean orderRecord = new Gson().fromJson(response, OrderRecordBean.class);
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
                    getView().callServerErrorCallback(NetInterfaceConstant.OrderC_orderDetail,apiE.getErrorCode(),apiE.getErrBody());
            }
        },NetInterfaceConstant.OrderC_orderDetail,reqParamMap);
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
        }, NetInterfaceConstant.OrderC_delOrder, reqParamMap);
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
                    getView().callServerErrorCallback(NetInterfaceConstant.OrderC_applyRefund, apiE.getErrorCode(), apiE.getErrBody());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().getApplyRefundSuccess(response);
            }
        }, NetInterfaceConstant.OrderC_applyRefund, reqParamMap);
    }

    /**
     * 分享代付前调用接口
     *
     * @param orderIdStr
     */
    public void shareOrder(String orderIdStr)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.orderId, orderIdStr);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("分享代付前调用接口--> " + response);
                if (getView() != null)
                    getView().getShareOrderSuccess(response);
            }
        }, NetInterfaceConstant.OrderC_shareOrder, reqParamMap);
    }

    /**
     * 求代付按钮开关
     */
    public void getBtnOnOff()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("返回》》》" + response);
                if (getView() != null)
                    getView().getBtnOnOffSuccess(response);
            }
        }, NetInterfaceConstant.FriendPayC_btnOnOff, reqParamMap);
    }
}

package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.MyResCommentBean;
import com.echoesnet.eatandmeet.models.bean.OrderRecordBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDQuickPayOrderDetailView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/16.
 */

public class ImpDQuickPayOrderDetailView extends BasePresenter<IDQuickPayOrderDetailView>
{
    private final String TAG = ImpDQuickPayOrderDetailView.class.getSimpleName();

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
                if (getView() != null)
                    getView().getOrderDetailCallback(orderRecord);
            }
        },NetInterfaceConstant.OrderC_orderDetail,reqParamMap);
    }

    /**
     * 删除订单
     *
     * @param orderId
     */
    public void deleteOrder(final String orderId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.orderId, orderId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (getView() != null)
                    getView().deleteOrderCallback(response);
            }
        },NetInterfaceConstant.OrderC_delOrder,reqParamMap);
    }

    /**
     * 查看评价
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
                if (getView() != null)
                    getView().getResCommentDetailCallback(resComment);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView()!=null)
                    getView().callServerErrorCallback(NetInterfaceConstant.EvalC_checkEval,apiE.getErrorCode(),apiE.getErrBody());
            }
        },NetInterfaceConstant.EvalC_checkEval,reqParamMap);
    }
}

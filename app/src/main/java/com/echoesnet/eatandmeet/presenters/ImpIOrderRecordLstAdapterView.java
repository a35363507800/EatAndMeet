package com.echoesnet.eatandmeet.presenters;

import android.content.Context;
import android.text.TextUtils;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IOrderRecordLstAdapterView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import java.util.Map;


/**
 * Created by Administrator on 2017/1/5.
 */

public class ImpIOrderRecordLstAdapterView
{
    private final String TAG = ImpIOrderRecordLstAdapterView.class.getSimpleName();
    private Context mContext;
    private IOrderRecordLstAdapterView adapterView;

    public ImpIOrderRecordLstAdapterView(Context mContext, IOrderRecordLstAdapterView adapterView)
    {
        this.mContext = mContext;
        this.adapterView = adapterView;
    }

    //删除订单
    public void deleteOrder(String orderId,boolean isHommpany)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.orderId, orderId);
        if (isHommpany)
        {
            HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
            {
                @Override
                public void onNext(String response)
                {
                    super.onNext(response);
                    if (adapterView != null)
                        adapterView.deleteOrderCallBack(response);
                }
            },NetInterfaceConstant.HomepartyC_delOrder,reqParamMap);
        }
        else
        {
            HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
            {
                @Override
                public void onNext(String response)
                {
                    super.onNext(response);
                    if (adapterView != null)
                        adapterView.deleteOrderCallBack(response);
                }
            },NetInterfaceConstant.OrderC_delOrder,reqParamMap);
        }

    }

    public void shareOrder(String orderIdStr)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.orderId, orderIdStr);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (adapterView!=null)
                    adapterView.callServerErrorCallback(NetInterfaceConstant.OrderC_shareOrder,apiE.getErrorCode(),apiE.getErrBody());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (adapterView != null)
                    adapterView.shareOrderCallBack(response);
            }
        },NetInterfaceConstant.OrderC_shareOrder,reqParamMap);
    }

    /**
     * 轰趴订单申请退款
     *
     * @param orderId 订单号
     */
    public void applyRefundClub(final String orderId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.orderId, orderId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (adapterView != null)
                   adapterView.callServerErrorCallback(NetInterfaceConstant.HomepartyC_applyRefund, apiE.getErrorCode(), apiE.getErrBody());

            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (adapterView != null)
                    adapterView.getApplyRefundClubSuccess(response,orderId);
            }
        }, NetInterfaceConstant.HomepartyC_applyRefund, reqParamMap);
    }
}

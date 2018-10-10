package com.echoesnet.eatandmeet.presenters;

import android.text.TextUtils;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDApplyRefundView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.orhanobut.logger.Logger;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/26.
 */

public class ImpDApplyRefundView extends BasePresenter<IDApplyRefundView>
{
    private final String TAG = ImpDApplyRefundView.class.getSimpleName();

    public void refund(String refundRea, final String orderId, final String payType, final String amount, String fee,String orderType)
    {
        Logger.t(TAG).d("提交退款paytype:" + payType);
        Logger.t(TAG).d("提交退款orderType:" + orderType);

        if (getView() == null)
            return;
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.orderId, orderId);
        reqParamMap.put(ConstCodeTable.refundRea, refundRea);
        reqParamMap.put(ConstCodeTable.payType, payType);
        reqParamMap.put(ConstCodeTable.amount, amount);
        reqParamMap.put(ConstCodeTable.fee, fee);
        if (TextUtils.equals("clubOrder",orderType))
        {
            HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
            {
                @Override
                public void onHandledError(ApiException apiE)
                {
                    super.onHandledError(apiE);
                    if (getView()!=null)
                        getView().callServerErrorCallback(NetInterfaceConstant.HomepartyC_refund,apiE.getErrorCode(),apiE.getErrBody());
                }

                @Override
                public void onNext(String response)
                {
                    super.onNext(response);
                    Logger.t(TAG).d("获得的结果：" + response);
                    if (getView() != null)
                        getView().refundCallback(response,orderType);
                }
            },NetInterfaceConstant.HomepartyC_refund,reqParamMap);
        }
        else
        {
            HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
            {
                @Override
                public void onHandledError(ApiException apiE)
                {
                    super.onHandledError(apiE);
                    if (getView()!=null)
                        getView().callServerErrorCallback(NetInterfaceConstant.OrderC_refund,apiE.getErrorCode(),apiE.getErrBody());
                }

                @Override
                public void onNext(String response)
                {
                    super.onNext(response);
                    Logger.t(TAG).d("获得的结果：" + response);
                    if (getView() != null)
                        getView().refundCallback(response,orderType);
                }
            },NetInterfaceConstant.OrderC_refund,reqParamMap);
        }

    }
}

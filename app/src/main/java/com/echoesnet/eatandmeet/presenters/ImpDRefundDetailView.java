package com.echoesnet.eatandmeet.presenters;

import android.content.Context;
import android.text.TextUtils;

import com.echoesnet.eatandmeet.activities.DRefundDetailAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDRefundDetailView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.orhanobut.logger.Logger;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/27.
 */

public class ImpDRefundDetailView extends BasePresenter<IDRefundDetailView>
{
    private final String TAG = ImpDRefundDetailView.class.getSimpleName();

    public void getRefundDetail(final String orderId,String orderType)
    {
        if (getView() == null)
        {
            return;
        }
        Context mContext = (DRefundDetailAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.orderId, orderId);
        if (TextUtils.equals("clubOrder",orderType))
        {
            HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
            {
                @Override
                public void onNext(String response)
                {
                    super.onNext(response);
                    Logger.t(TAG).d("获得的结果：" + response);
                    if (getView() != null)
                        getView().getRefundDetailCallback(response);
                }
            },NetInterfaceConstant.HomepartyC_refundDetail,reqParamMap);
        }
        else
        {
            HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
            {
                @Override
                public void onNext(String response)
                {
                    super.onNext(response);
                    Logger.t(TAG).d("获得的结果：" + response);
                    if (getView() != null)
                        getView().getRefundDetailCallback(response);
                }
            },NetInterfaceConstant.OrderC_refundDetail,reqParamMap);
        }

    }
}

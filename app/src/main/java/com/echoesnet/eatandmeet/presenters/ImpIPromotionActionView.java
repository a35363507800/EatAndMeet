package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;
import android.content.Context;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IPromotionActionView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.SharePreUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/9.
 */

public class ImpIPromotionActionView
{
    private final String TAG = ImpIPromotionActionView.class.getSimpleName();
    private Context mContext;
    private IPromotionActionView promotionActionView;

    public ImpIPromotionActionView(Context mContext, IPromotionActionView promotionActionView)
    {
        this.mContext = mContext;
        this.promotionActionView = promotionActionView;
    }

    /**
     * 修改余额
     *
     * @param context
     */
    public void setModifyBalance(final Activity context, String streamId, String payType, String payAmount, String getAmount, String source)
    {
        // 支付方式(0：汇昇币，1：支付宝alipay，2：微信wx，3：银联卡upacp)
        if (payType.equals("alipay"))
        {
            payType = "1";
        }
        else if (payType.equals("wx"))
        {
            payType = "2";
        }
        else if (payType.equals("upacp"))
        {
            payType = "3";
        }
        Map<String, String> reqParams = new HashMap<String, String>();
        reqParams.put(ConstCodeTable.token, SharePreUtils.getToken(context));
        reqParams.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(context));
        reqParams.put(ConstCodeTable.uId, SharePreUtils.getUId(context));
        reqParams.put(ConstCodeTable.payType, payType);
        reqParams.put(ConstCodeTable.streamId, streamId);
        reqParams.put(ConstCodeTable.payAmount, payAmount);
        reqParams.put(ConstCodeTable.getAmount, getAmount);
        reqParams.put(ConstCodeTable.source, source);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (promotionActionView != null)
                    promotionActionView.setModifyBalanceCallback(response);
            }
        },NetInterfaceConstant.UserC_recharge,reqParams);

    }

}

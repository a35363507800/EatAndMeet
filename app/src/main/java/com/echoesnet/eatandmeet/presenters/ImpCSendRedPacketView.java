package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.activities.CRPsendRedPacketAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICSendRedPacketView;
import com.echoesnet.eatandmeet.utils.EncryptSHA1;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.jungly.gridpasswordview.GridPasswordView;
import com.orhanobut.logger.Logger;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Created by ben on 2016/11/15.
 */

public class ImpCSendRedPacketView extends BasePresenter<ICSendRedPacketView>
{
    private static final String TAG = ImpCSendRedPacketView.class.getSimpleName();

    /*    @Override
    public Observable<ResponseBody> sendRedPacket(@QueryMap Map<String, String> params)
    {
        return null;
    }*/

    /**
     * 发送红包，修改余额(升级版)
     */
    public void sendRedPacket2(final String moneyAmount, String payType, String luId,
                               String streamId, String password, final GridPasswordView gridPasswordView)
    {
        final ICSendRedPacketView mICSendRedPacketView = getView();
        if (mICSendRedPacketView == null)
            return;
        final Context mContext = (CRPsendRedPacketAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.payAmount, moneyAmount);
        reqParamMap.put(ConstCodeTable.lUId, luId);
        reqParamMap.put(ConstCodeTable.streamId, streamId);
        reqParamMap.put(ConstCodeTable.payType, payType);
        try
        {
            reqParamMap.put(ConstCodeTable.pwd, EncryptSHA1.SHA1(password));
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (getView()!=null)
                    getView().requestNetError(throwable,NetInterfaceConstant.UserC_payRed);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView()!=null)
                    getView().callServerErrorCallback(NetInterfaceConstant.UserC_payRed,apiE.getErrorCode(),apiE.getErrBody(),gridPasswordView);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (mICSendRedPacketView != null)
                    mICSendRedPacketView.sendReadPacket2Callback(response, gridPasswordView);
            }
        },NetInterfaceConstant.UserC_payRed,reqParamMap);
    }
}

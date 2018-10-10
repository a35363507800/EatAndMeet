package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.activities.live.LiveShowBootyCallAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.ILiveShowBootyCallActView;
import com.echoesnet.eatandmeet.utils.EncryptSHA1;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import okhttp3.Call;


/**
 * Created by an on 2016/12/6 0006.
 */

public class ImpILiveShowBootyCallActView extends BasePresenter<ILiveShowBootyCallActView>
{
    private final String TAG = ImpILiveShowBootyCallActView.class.getSimpleName();

    /**
     * 发送约会邀请
     */
    public void sendReceive(String luId, String payType, String amount, String pwd, String date)
    {
        final ILiveShowBootyCallActView iLiveShowBootyCallActView = getView();
        if (iLiveShowBootyCallActView == null)
            return;
        Context mActivity = (LiveShowBootyCallAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        reqParamMap.put(ConstCodeTable.lUId, luId);
        reqParamMap.put(ConstCodeTable.payType, payType);
        reqParamMap.put(ConstCodeTable.amount, amount);
        reqParamMap.put(ConstCodeTable.date, date);
        try
        {
            reqParamMap.put(ConstCodeTable.pwd, EncryptSHA1.SHA1(pwd));
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
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (iLiveShowBootyCallActView!=null)
                    iLiveShowBootyCallActView.callServerErrorCallback(NetInterfaceConstant.AppointmentC_payWish,apiE.getErrorCode(),apiE.getErrBody());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("返回》》》" + response);
                if (iLiveShowBootyCallActView != null)
                    iLiveShowBootyCallActView.sendReceiveCallback(response);
            }
        },NetInterfaceConstant.AppointmentC_payWish,reqParamMap);
    }
}

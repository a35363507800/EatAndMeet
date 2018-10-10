package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICaptureActivityView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by an on 2016/12/12.
 */

public class ImpICaptureActivityView
{
    private final String TAG = ImpICaptureActivityView.class.getSimpleName();
    private Context mContext;
    private ICaptureActivityView iCaptureActivityView;

    public ImpICaptureActivityView(Context context, ICaptureActivityView iCaptureActivityView)
    {
        this.mContext = context;
        this.iCaptureActivityView = iCaptureActivityView;
    }

    public void receiveSuccess(String orderId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.orderId, orderId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (iCaptureActivityView != null)
                    iCaptureActivityView.receiveSuccessCallback(response);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (iCaptureActivityView != null)
                    iCaptureActivityView.requestNetError(null,null,apiE.getErrorCode());
            }
        },NetInterfaceConstant.ReceiveC_receiveSuccess,reqParamMap);
    }

    public void bindDiningConsultant(String consultantId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.consultant, consultantId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (iCaptureActivityView != null)
                    iCaptureActivityView.bindConsultantCallback(response);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (iCaptureActivityView != null)
                    iCaptureActivityView.requestNetError(null,null,apiE.getErrorCode());
            }

        },NetInterfaceConstant.ConsultantC_BindConsultant,reqParamMap);

    }

    public void queryConsultant(String consultant)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.consultant, consultant);
        reqParamMap.put(ConstCodeTable.id, "");
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (iCaptureActivityView != null)
                    iCaptureActivityView.queryConsultantCallback(response);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (iCaptureActivityView != null)
                    iCaptureActivityView.requestNetError(null,null,apiE.getErrorCode());
            }
        },NetInterfaceConstant.ConsultantC_queryConsultant,reqParamMap);
    }

}

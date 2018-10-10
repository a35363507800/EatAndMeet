package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;
import android.text.TextUtils;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.IdentityAuthAct;
import com.echoesnet.eatandmeet.controllers.okhttpCallback.BaseCallback;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IIdentityAuthActView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by an on 2016/12/27.
 */

public class ImpIIdentityAuthActView extends BasePresenter<IIdentityAuthActView>
{
    private final String TAG = ImpIIdentityAuthActView.class.getSimpleName();


    /**
     * 获取支付宝绑定签名
     */
    public void getBuildAuthInfo()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("实名签名>>" + response);
                if (getView()!=null)
                    getView().getBuildAuthInfoSuc(response);
            }
        },NetInterfaceConstant.LiveC_buildAuthInfo,reqParamMap);
    }


    /**
     * 获取支付宝绑定签名
     */
    public void getAlipayValidate(String code)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.code, code);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView()!=null)
                    getView().callServerErrorCallback(NetInterfaceConstant.LiveC_alipayValidate,apiE.getErrorCode(),apiE.getErrBody());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("支付宝一键认证>>" + response);
                if (getView()!=null)
                    getView().getAlipayValidateSuc(response);
            }
        }, NetInterfaceConstant.LiveC_alipayValidate, reqParamMap);
    }

    /**
     * 获取本人的实名状态
     *
     * @param type FROM_MY_AUTHENTICATION 从官方认证返回刷新 如果成功不finish
     */
    public void getRealNameState(final String type)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView()!=null)
                getView().getRealNameStateCallBack(response, type);
            }
        },NetInterfaceConstant.LiveC_getReal,reqParamMap);
    }

}

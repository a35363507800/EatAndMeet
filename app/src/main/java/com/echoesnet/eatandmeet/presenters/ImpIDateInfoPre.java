package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.fragments.DateInfoFrg;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDateInfoPre;
import com.echoesnet.eatandmeet.utils.EncryptSHA1;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Created by lc on 2017/7/18 20.
 */

public class ImpIDateInfoPre extends BasePresenter<DateInfoFrg> implements IDateInfoPre
{
    private String TAG = ImpIDateInfoPre.class.getSimpleName();

    @Override
    public void getUserAppointment(String luid, String startIdex, String num)
    {
        if (getView() == null)
            return;
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.lUId, luid);
        reqParamMap.put(ConstCodeTable.startIdx, startIdex);
        reqParamMap.put(ConstCodeTable.num, num);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                {
                    getView().userAppointmentCallBack(response);
                }
            }
        }, NetInterfaceConstant.AppointmentC_userAppointment, reqParamMap);
    }

    @Override
    public void sendAppointment(String luId, String payType, String amount, String pwd, String date)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.lUId, luId);
        reqParamMap.put(ConstCodeTable.date, date);
        reqParamMap.put(ConstCodeTable.payType, payType);
        reqParamMap.put(ConstCodeTable.amount, amount);
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
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView()!=null)
                    getView().callServerErrorCallback(NetInterfaceConstant.AppointmentC_payWish,apiE.getErrorCode(),apiE.getErrBody());
            }

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (getView() != null)
                {
                    getView().appointPayCallBack(response.getBody());
                }
            }
        }, NetInterfaceConstant.AppointmentC_payWish, null, reqParamMap);
    }


    @Override
    public void checkWish(String luId, String date, String type)
    {
        if (getView() == null)
            return;
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.lUId, luId);
        reqParamMap.put(ConstCodeTable.date, date);
        reqParamMap.put(ConstCodeTable.type, type);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                    getView().callServerErrorCallback(NetInterfaceConstant.AppointmentC_checkWish, apiE.getErrorCode(), apiE.getErrBody());

            }

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().checkWishCallBack(response.getBody());

            }
        }, NetInterfaceConstant.AppointmentC_checkWish, null, reqParamMap);
    }

    @Override
    public void checkReceive(String luId)
    {
        if (getView() == null)
            return;
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.lUId, luId);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                    getView().callServerErrorCallback(NetInterfaceConstant.AppointmentC_checkReceive, apiE.getErrorCode(), apiE.getErrBody());

            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().checkReceiveCallBack(response);

            }
        }, NetInterfaceConstant.AppointmentC_checkReceive,  reqParamMap);
    }
}

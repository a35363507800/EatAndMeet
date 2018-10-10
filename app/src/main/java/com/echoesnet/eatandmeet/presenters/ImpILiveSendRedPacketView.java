package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.activities.LiveSendPacketAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
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
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @version 1.0
 * @modifier zdw
 * @createDate 2017/6/15
 * @description 获取红包额度及发送红包接口请求
 */

public class ImpILiveSendRedPacketView extends BasePresenter<LiveSendPacketAct>{
    private static final String TAG = ImpILiveSendRedPacketView.class.getSimpleName();

    /**
     * 获取红包的额度
     */
    public void requestRedPacket() {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.LiveC_redAmount, new Gson().toJson(reqParamMap));
        Logger.t(TAG).d("请求获取红包的额度参数---> " + paramJson);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if(getView() != null) {
                    getView().requestLiveRedPacket(response);
                }
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if(getView() != null) {
                    getView().requestNetErrorCallback(NetInterfaceConstant.LiveC_redAmount,throwable);
                }
            }
        }, NetInterfaceConstant.LiveC_redAmount, reqParamMap);


    }

    /**
     * 发群红包
     * @param payType 支付方式(0:余额、1:支付宝、2:微信)
     * @param payAmount 支付金额
     * @param streamId 流水号(余额支付则传空串)
     * @param pwd 支付密码(第三方支付则传空串)
     * @param num 红包个数
     * @param roomId 房间号
     */
    public void sendRedPackageByServer(String payType, String payAmount, String streamId, String pwd, String num, String roomId, final GridPasswordView gridPasswordView)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.payType, payType);
        reqParamMap.put(ConstCodeTable.payAmount, payAmount);
        reqParamMap.put(ConstCodeTable.streamId, streamId);
        reqParamMap.put(ConstCodeTable.roomId, roomId);
        reqParamMap.put(ConstCodeTable.num, num);
        try {
            reqParamMap.put(ConstCodeTable.pwd, EncryptSHA1.SHA1(pwd));
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.LiveC_groupRed, new Gson().toJson(reqParamMap));
        Logger.t(TAG).d("请求发群红包 参数---> " + paramJson);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                {
                    getView().sendRedPackageByServer(response, gridPasswordView);
                }
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
               if (getView()!=null)
                   getView().callServerErrorCallback(NetInterfaceConstant.LiveC_groupRed,apiE.getErrorCode(),apiE.getErrBody());
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if(getView() != null) {
                    getView().requestNetErrorCallback(NetInterfaceConstant.LiveC_groupRed,throwable);
                }
            }
        }, NetInterfaceConstant.LiveC_groupRed, reqParamMap);

    }
}

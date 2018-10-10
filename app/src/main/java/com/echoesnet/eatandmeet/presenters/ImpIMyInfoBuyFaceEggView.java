package com.echoesnet.eatandmeet.presenters;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.activities.live.MyInfoBuyFaceEggActivity;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.FaceListBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyInfoBuyFaceEggView;
import com.echoesnet.eatandmeet.utils.EncryptSHA1;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jungly.gridpasswordview.GridPasswordView;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/12.
 */

public class ImpIMyInfoBuyFaceEggView extends BasePresenter<IMyInfoBuyFaceEggView>
{
    private final String TAG = ImpIWithDrawDetailView.class.getSimpleName();

    public void getFaceRechargeList()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if(getView()!=null)
                    getView().getFaceRechargeListCallBack(response);
            }
        },NetInterfaceConstant.LiveC_faceList,reqParamMap);

    }

    public void postFaceEggRecharge2(final String payType, String streamId, String password, final GridPasswordView gridPasswordView, String payAmount, String getAmount)
    {
        final IMyInfoBuyFaceEggView myInfoBuyFaceEggView = getView();
        if (myInfoBuyFaceEggView == null)
            return;
        Context mContext = (MyInfoBuyFaceEggActivity) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.payType, payType);
        reqParamMap.put(ConstCodeTable.streamId, streamId);
        reqParamMap.put(ConstCodeTable.amount, payAmount);
        reqParamMap.put(ConstCodeTable.faceEgg, getAmount);
        try
        {
            reqParamMap.put(ConstCodeTable.pwd, EncryptSHA1.SHA1(password));
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
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (myInfoBuyFaceEggView!=null)
                    myInfoBuyFaceEggView.callServerErrorCallback(NetInterfaceConstant.LiveC_recharge,apiE.getErrorCode(),apiE.getErrBody(),gridPasswordView);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (myInfoBuyFaceEggView!=null)
                    myInfoBuyFaceEggView.postFaceEggRecharge2CallBack(response, gridPasswordView);
            }
        },NetInterfaceConstant.LiveC_recharge,reqParamMap);
    }
}

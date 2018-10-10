package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IResLstInfoAdapterView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.SharePreUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/15.
 */

public class ImpResLstInfoAdapterView {
    private final String TAG = ImpResLstInfoAdapterView.class.getSimpleName();
    private Context context;
    private IResLstInfoAdapterView iResLstInfoAdapterView;

    public ImpResLstInfoAdapterView(Context context, IResLstInfoAdapterView iResLstInfoAdapterView) {
        this.context = context;
        this.iResLstInfoAdapterView = iResLstInfoAdapterView;
    }

    public void setPraiseInfo(final int position, String rid)
    {
        if(CommonUtils.getLock(TAG+"01"))
          return;
        CommonUtils.clickLock(TAG+"01");

        Map<String, String> reqParamMap = new HashMap<>();
        reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(context));
        reqParamMap.put(ConstCodeTable.uId, SharePreUtils.getUId(context));
        reqParamMap.put(ConstCodeTable.token, SharePreUtils.getToken(context));
        reqParamMap.put(ConstCodeTable.rId, rid);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                CommonUtils.removeClickLock(TAG+"01");
                if (iResLstInfoAdapterView!=null)
                    iResLstInfoAdapterView.setPraiseInfoCallback(response, position);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                CommonUtils.removeClickLock(TAG+"01");
                if (iResLstInfoAdapterView!=null)
                    iResLstInfoAdapterView.callServerErrorCallback(NetInterfaceConstant.RestaurantC_rPraise,apiE.getErrorCode(),apiE.getErrBody());
            }
        },NetInterfaceConstant.RestaurantC_rPraise,reqParamMap);
    }


    public void setUnPraiseInfo(final int position, String rid)
    {
        if(CommonUtils.getLock(TAG+"02"))
            return;
        CommonUtils.clickLock(TAG+"02");
        Map<String, String> reqParamMap = new HashMap<>();
        reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(context));
        reqParamMap.put(ConstCodeTable.uId, SharePreUtils.getUId(context));
        reqParamMap.put(ConstCodeTable.token, SharePreUtils.getToken(context));
        reqParamMap.put(ConstCodeTable.rId, rid);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                CommonUtils.removeClickLock(TAG+"02");
                if (iResLstInfoAdapterView!=null)
                    iResLstInfoAdapterView.callServerErrorCallback(NetInterfaceConstant.RestaurantC_delRPraise,apiE.getErrorCode(),apiE.getErrBody());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                CommonUtils.removeClickLock(TAG+"02");
                if (iResLstInfoAdapterView!=null)
                    iResLstInfoAdapterView.setUnPraiseInfoCallback(response, position);
            }
        },NetInterfaceConstant.RestaurantC_delRPraise,reqParamMap);
    }
}

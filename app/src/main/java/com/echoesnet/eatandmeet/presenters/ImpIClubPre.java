package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.fragments.ClubFrg;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ClubListBean;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.GameItemBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IClubPre;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2018/2/5
 * @description
 */
public class ImpIClubPre extends BasePresenter<ClubFrg> implements IClubPre
{
    private static final String TAG = ImpIClubPre.class.getSimpleName();
    @Override
    public void getClubList(String paraNum,String paraStartIdx,
                            String circle,
                            String region,
                            String flag,
                            Double mCurrentLongitude,Double mCurrentLantitude,String operateType)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.num, paraNum);
        reqParamMap.put(ConstCodeTable.startIdx, paraStartIdx);
        reqParamMap.put(ConstCodeTable.circle, circle);
        reqParamMap.put(ConstCodeTable.region, region);
        reqParamMap.put(ConstCodeTable.flag, flag);
        reqParamMap.put(ConstCodeTable.posx, String.valueOf(mCurrentLongitude));
        reqParamMap.put(ConstCodeTable.posy, String.valueOf(mCurrentLantitude));

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override

            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
              if (getView()!=null)
                  getView().requestNetErrorCallback( NetInterfaceConstant.HomepartyC_partyList,throwable);

            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("返回数据》》"+response.toString());
                try
                {
                    JSONObject object = new JSONObject(response);
                    String resultList = object.getString("resultList");
                    if (getView() != null)
                        getView().getClubListCallback((List<ClubListBean>) new Gson().fromJson(resultList, new TypeToken<List<ClubListBean>>()
                        {
                        }.getType()),operateType);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d("异常 》》" + e.getMessage());
                }

            }
        }, NetInterfaceConstant.HomepartyC_partyList, reqParamMap);
    }
}

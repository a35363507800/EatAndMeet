package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.fragments.CSayHelloFr;
import com.echoesnet.eatandmeet.fragments.FindFragment;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.CAccostBean;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICSayHelloFrView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by an on 2016/12/20 0020.
 */

public class ImpCSayHelloFrView extends BasePresenter<ICSayHelloFrView>
{
    private final String TAG = ImpCSayHelloFrView.class.getSimpleName();
    private Gson gson;

    public ImpCSayHelloFrView()
    {
        gson = new Gson();
    }

    /**
     * 获得搭讪的人
     *
     * @param resIds 餐厅id串
     */
    @Deprecated
    public void getAroundPerson(String resIds, final List<HashMap<String, Object>> resInfo)
    {
        final ICSayHelloFrView icSayHelloFrView=getView();
        if (icSayHelloFrView==null)
            return;
        Activity mActivity= ((FindFragment)getView()).getActivity();

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        reqParamMap.put(ConstCodeTable.rIds, resIds);


        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                List<CAccostBean> orderLst = new Gson().fromJson(response, new TypeToken<List<CAccostBean>>(){}.getType());
                if (icSayHelloFrView != null)
                    icSayHelloFrView.getAroundPersonCallback(orderLst, resInfo);
            }
        },NetInterfaceConstant.EncounterC_accostToPerson,reqParamMap);
    }

    /**
     * 基于当前位置获取用户周围的人及餐厅
     *
     * @param posX 纬度
     * @param posY 经度
     */
    public void getNewAccostToPerson(String posX, String posY, final boolean isShowDialog)
    {
        final ICSayHelloFrView icSayHelloFrView=getView();
        if (icSayHelloFrView==null)
            return;
        Activity mActivity= ((CSayHelloFr)getView()).getActivity();

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        reqParamMap.put(ConstCodeTable.posx, posX);
        reqParamMap.put(ConstCodeTable.posy, posY);


        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                List<CAccostBean> orderLst = new Gson().fromJson(response, new TypeToken<List<CAccostBean>>(){}.getType());

                if (icSayHelloFrView != null)
                    icSayHelloFrView.getNewAccostToPersonCallback(orderLst, isShowDialog);
            }
        },NetInterfaceConstant.EncounterC_newaccostToPerson,reqParamMap);

    }
}

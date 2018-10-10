package com.echoesnet.eatandmeet.presenters;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.activities.ChooseGoWhereAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.SearchRestaurantBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IChooseGoWhereView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/27.
 */

public class ImpIChooseGoWhereView extends BasePresenter<IChooseGoWhereView>
{
    private final String TAG = ImpICommunicatePre.class.getSimpleName();


    /**
     * @param getItemStartIndex
     * @param getItemNum
     * @param keyword           传空内容代表查询所有餐厅
     * @param operateType
     */
    public void getRes(String getItemStartIndex, String getItemNum, String keyword, final String operateType)
    {
        final IChooseGoWhereView iChooseGoWhereView = getView();
        if (iChooseGoWhereView == null)
            return;
        Context mActivity = (ChooseGoWhereAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        reqParamMap.put(ConstCodeTable.kw, keyword);
        reqParamMap.put(ConstCodeTable.num, getItemNum);
        reqParamMap.put(ConstCodeTable.startIdx, getItemStartIndex);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if(iChooseGoWhereView!=null)
                iChooseGoWhereView.requestNetErrorCallback(NetInterfaceConstant.RestaurantC_searchResForUInfo,throwable);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);

                if(iChooseGoWhereView==null)
                    return;

                ArrayMap<String, Object> getResMap = new ArrayMap<String, Object>();

                List<SearchRestaurantBean> resLst = new ArrayList<>();
                resLst = new Gson().fromJson(response, new TypeToken<List<SearchRestaurantBean>>()
                {
                }.getType());

                getResMap.put("operateType", operateType);
                getResMap.put("searchResult", resLst);
                iChooseGoWhereView.getResCallBack(getResMap);
            }
        },NetInterfaceConstant.RestaurantC_searchResForUInfo,reqParamMap);

    }

    public void searchResList(String startIdx, String num, String keyword, final String type)
    {
        final IChooseGoWhereView iChooseGoWhereView = getView();
        if (iChooseGoWhereView == null)
            return;
        Context mActivity = (ChooseGoWhereAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        reqParamMap.put(ConstCodeTable.kw, keyword);
        reqParamMap.put(ConstCodeTable.num, num);
        reqParamMap.put(ConstCodeTable.startIdx, startIdx);

        Gson gson = new Gson();
        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.RestaurantC_searchResForUInfo, gson.toJson(reqParamMap));
        Logger.t(TAG).d("请求参数》" + NetHelper.getRequestJsonStr(NetInterfaceConstant.RestaurantC_searchResForUInfo, gson.toJson(reqParamMap)));

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (iChooseGoWhereView != null)
                    iChooseGoWhereView.requestNetErrorCallback(NetInterfaceConstant.RestaurantC_searchResForUInfo,throwable);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                ArrayMap<String, Object> searchResMap = new ArrayMap<String, Object>();

                List<SearchRestaurantBean> resLst = new ArrayList<>();
                resLst = new Gson().fromJson(response, new TypeToken<List<SearchRestaurantBean>>()
                {
                }.getType());

                searchResMap.put("type", type);
                searchResMap.put("response", resLst);
                if (iChooseGoWhereView != null)
                    iChooseGoWhereView.searchResCallBack(searchResMap);

            }
        },NetInterfaceConstant.RestaurantC_searchResForUInfo,reqParamMap);

    }
}

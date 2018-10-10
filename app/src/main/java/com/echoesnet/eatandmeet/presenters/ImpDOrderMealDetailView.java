package com.echoesnet.eatandmeet.presenters;

import android.content.Context;
import android.text.TextUtils;

import com.echoesnet.eatandmeet.activities.DOrderMealDetailAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDOrderMealDetailView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/27.
 */

public class ImpDOrderMealDetailView extends BasePresenter<DOrderMealDetailAct>
{
    private final String TAG = ImpDOrderMealDetailView.class.getSimpleName();
    private Context mContext;
    private IDOrderMealDetailView iDorderMealDetailView;

    public ImpDOrderMealDetailView(Context mContext, IDOrderMealDetailView iDorderMealDetailView)
    {
        this.mContext = mContext;
        this.iDorderMealDetailView = iDorderMealDetailView;
    }

    /**
     * 我的收藏
     *
     * @param resId
     */
    public void collectedRest(String resId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.rId, resId);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (getView()!=null)
                    getView().requestNetError(throwable,NetInterfaceConstant.UserC_collect);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (getView() != null)
                    getView().collectedRestCallback(response);
            }
        },NetInterfaceConstant.UserC_collect,reqParamMap);
    }

    public void removeRest(String resId)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.rIds, resId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (getView()!=null)
                    getView().requestNetError(throwable,NetInterfaceConstant.UserC_delCollect);
            }
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (getView() != null)
                    getView().removeRestCallback(response);
            }
        },NetInterfaceConstant.UserC_delCollect,reqParamMap);
    }

    public void getRestInfo(String restId)
    {
        if (getView()==null)
            return;
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.rId, restId);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                if (TextUtils.equals("RSTATUS_ERROR",apiE.getErrorCode()))
                {
                    if (getView()!=null)
                        getView().callServerErrorCallback(NetInterfaceConstant.RestaurantC_resInfoByrId,apiE.getErrorCode(),apiE.getErrBody());
                }
                else
                {
                    super.onHandledError(apiE);
                }
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (getView()!=null)
                    getView().requestNetError(throwable,NetInterfaceConstant.RestaurantC_resInfoByrId);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (getView() != null)
                    getView().getRestInfoCallBack(response);
            }
        },NetInterfaceConstant.RestaurantC_resInfoByrId,reqParamMap);
    }

}

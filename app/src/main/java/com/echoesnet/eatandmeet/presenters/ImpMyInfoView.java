package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyInfoPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyInfoView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.serverdatacache.Elixir;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/14.
 */

public class ImpMyInfoView extends BasePresenter<IMyInfoView> implements IMyInfoPre
{
    private final String TAG = ImpMyInfoView.class.getSimpleName();
    private Context mContext;

    public ImpMyInfoView()
    {
        mContext = EamApplication.getInstance();
    }

    /**
     * 直播间开关接口
     */
    public void getLivePlaySwap()
    {
        final IMyInfoView iMyInfoView = getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (iMyInfoView!=null)
                {
                    iMyInfoView.callServerErrorCallback(NetInterfaceConstant.LiveC_swap,apiE.getErrorCode(),apiE.getErrBody());
                }

            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);

                Logger.t(TAG).d("返回结果》" + response);
                try
                {
                    JSONObject jsonResponse = new JSONObject(response);
                    String receive = jsonResponse.getString("receive");
                    if (isViewAttached())
                        iMyInfoView.getLivePlaySwapCallback(receive);

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

        }, NetInterfaceConstant.LiveC_swap, reqParamMap);
    }

    @Override
    public void getMyInfo()
    {
        final IMyInfoView iMyInfoView = getView();

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (getView() == null)
                    return;
                Elixir.pumpCache(EamApplication.getInstance().getApplicationContext(), NetInterfaceConstant.UserC_myInfo, new ICommonOperateListener()
                {
                    @Override
                    public void onSuccess(String cacheBody)
                    {
                        if (isViewAttached())
                            iMyInfoView.getMyinfoCallBack(cacheBody);
                    }

                    @Override
                    public void onError(String code, String msg)
                    {
                        Logger.t(TAG).d("我的信息缓存失败");
                    }
                });


            }

            @Override
            public void onNext(ResponseResult o)
            {
                super.onNext(o);
                Elixir.updateCacheOnNewThread(EamApplication.getInstance(), NetInterfaceConstant.UserC_myInfo, o.getBody());
                if (isViewAttached())
                    iMyInfoView.getMyinfoCallBack(o.getBody());

            }
        }, NetInterfaceConstant.UserC_myInfo, "1", reqParamMap);
    }

}

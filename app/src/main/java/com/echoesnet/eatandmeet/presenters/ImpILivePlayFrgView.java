package com.echoesnet.eatandmeet.presenters;

import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.fragments.LivePlayFrg;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.LAnchorsListBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.serverdatacache.Elixir;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by an on 2016/12/20 0020.
 */

public class ImpILivePlayFrgView extends BasePresenter<LivePlayFrg>
{
    private final String TAG = ImpILivePlayFrgView.class.getSimpleName();

    public ImpILivePlayFrgView()
    {

    }

    /**
     * 获取主播列表数据
     *
     * @param star
     * @param num
     */
    public void getAnchorData(String star, String num, final boolean isRefresh, String source)
    {
        Map<String, String> params = new ArrayMap<>();
        params.put(ConstCodeTable.token, SharePreUtils.getToken(EamApplication.getInstance()));
        params.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(EamApplication.getInstance()));
        params.put(ConstCodeTable.uId, SharePreUtils.getUId(EamApplication.getInstance()));
        params.put(ConstCodeTable.startIdx, star);
        params.put(ConstCodeTable.num, num);
        params.put(ConstCodeTable.source, source);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                Elixir.pumpCache(EamApplication.getInstance(), NetInterfaceConstant.LiveC_newLiveList, new ICommonOperateListener()
                {
                    @Override
                    public void onSuccess(String response)
                    {
                        handleAnchorData(response,isRefresh);
                    }

                    @Override
                    public void onError(String code, String msg)
                    {
                        if (getView() != null)
                            getView().requestNetError(null, null, msg);
                    }
                });
            }

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                handleAnchorData(response.getBody(),isRefresh);
            }
        }, NetInterfaceConstant.LiveC_newLiveList,null, params);
    }

    private void handleAnchorData(String responseStr,boolean isRefresh)
    {
        List<LAnchorsListBean> listBeanList = null;
        String styleFlag = "";
        Elixir.updateCacheOnNewThread(EamApplication.getInstance(), NetInterfaceConstant.LiveC_newLiveList, responseStr);
        try
        {
            JSONObject bodyJObj = new JSONObject(responseStr);
            String resStr = bodyJObj.getString("res");
            styleFlag = bodyJObj.optString("styleFlag", "");
            listBeanList = new Gson().fromJson(resStr, new TypeToken<List<LAnchorsListBean>>()
            {
            }.getType());

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        if (getView() != null)
            getView().getAnchorDataCallback(listBeanList, isRefresh, styleFlag);
    }
}
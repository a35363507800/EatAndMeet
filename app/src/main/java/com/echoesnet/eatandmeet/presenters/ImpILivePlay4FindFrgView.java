package com.echoesnet.eatandmeet.presenters;

import android.text.TextUtils;

import com.echoesnet.eatandmeet.controllers.okhttpCallback.BaseCallback;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.fragments.FLivePlay4FindFrg;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.FPromotionBean;
import com.echoesnet.eatandmeet.models.bean.Liveplay4FindBean;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by an on 2017/3/29 0029.
 */

public class ImpILivePlay4FindFrgView extends BasePresenter<FLivePlay4FindFrg> {
    private final String TAG = ImpILivePlay4FindFrgView.class.getSimpleName();
    private Gson gson;

    public ImpILivePlay4FindFrgView() {
        gson = new Gson();
    }

    /**
     * 获取find直播列表
     *
     * @param city     城市
     * @param startIdx 起始
     * @param type     refresh  add   刷新或者加载更多
     */
    public void getIndexLiveList(final String type, String city, String startIdx, String posx, String posy) {
        if (getView() == null)
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView().getActivity());
        params.put(ConstCodeTable.city, city);
        params.put(ConstCodeTable.startIdx, startIdx);
        params.put(ConstCodeTable.posx, posx);
        params.put(ConstCodeTable.posy, posy);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if(isViewAttached())
                    getView().getIndexLiveSuccess(type, gson.fromJson(response, Liveplay4FindBean.class));
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if(isViewAttached())
                getView().getIndexLiveSuccess(type, null);
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if(isViewAttached())
                getView().requestNetErrorCallback(NetInterfaceConstant.IndexC_indexLive,throwable);
            }
        }, NetInterfaceConstant.IndexC_indexLive, params);

    }


    /**
     * 获取find直播轮播图
     */
    public void getCarouselLive() {

        Map<String, String> params = NetHelper.getCommonPartOfParam(null);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                {
                    getView().getCarouselLiveSuccess((ArrayList<FPromotionBean>) gson.fromJson(response, new TypeToken<List<FPromotionBean>>() {
                    }.getType()));
                }
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if(isViewAttached())
                getView().requestNetErrorCallback(NetInterfaceConstant.IndexC_carouselLive,throwable);
            }
        }, NetInterfaceConstant.IndexC_indexLive, params);

    }


}

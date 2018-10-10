package com.echoesnet.eatandmeet.presenters;

import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.echoesnet.eatandmeet.controllers.okhttpCallback.BaseCallback;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.fragments.FRestaurant4FindFrg;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.FPromotionBean;
import com.echoesnet.eatandmeet.models.bean.FRestaurannt4FindBean;
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

public class ImpIFRestaurant4FindFrgView extends BasePresenter<FRestaurant4FindFrg>
{
    private final String TAG = ImpIFRestaurant4FindFrgView.class.getSimpleName();
    private Gson gson;

    public ImpIFRestaurant4FindFrgView()
    {
        gson = new Gson();
    }

    /**
     * 获取find餐厅列表
     *
     * @param num      请求数量
     * @param city     城市
     * @param startIdx 起始
     * @param type     refresh  add   刷新或者加载更多
     */
    public void getIndexRestaurantList(final String type, String num, String city, String startIdx, String posx, String posy)
    {
        if (getView() == null)
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView().getActivity());
        params.put(ConstCodeTable.num, num);
        params.put(ConstCodeTable.city, city);
        params.put(ConstCodeTable.startIdx, startIdx);
        params.put(ConstCodeTable.posx, posx);
        params.put(ConstCodeTable.posy, posy);


        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (getView()!=null)
                    getView().requestNetErrorCallback(NetInterfaceConstant.IndexC_indexRecommend_v322,throwable);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("发现页餐厅列表" + response);
                if (getView()!=null)
                    getView().getIndexRecommendSuccess(type, gson.fromJson(response, FRestaurannt4FindBean.class));
            }
        },NetInterfaceConstant.IndexC_indexRecommend_v322, params);
    }


    /**
     * 获取find餐厅轮播图
     */
    public void getCarouselRes()
    {
        Map<String, String> params = NetHelper.getCommonPartOfParam(null);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (getView()!=null)
                    getView().requestNetErrorCallback(NetInterfaceConstant.IndexC_carouselRes,throwable);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView()!=null)
                getView().getCarouselResSuccess((ArrayList<FPromotionBean>) gson.fromJson(response, new TypeToken<List<FPromotionBean>>()
                {
                }.getType()));
            }
        },NetInterfaceConstant.IndexC_carouselRes, params);
    }


}

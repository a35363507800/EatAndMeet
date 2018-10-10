package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.activities.CContactLstAct;
import com.echoesnet.eatandmeet.activities.ShareColumnArticleAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICContactLstView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IShareColumnArticleView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/20.
 */

public class ImpIShareColumnArticleView extends BasePresenter<ShareColumnArticleAct>
{
    private final String TAG = ImpIShareColumnArticleView.class.getSimpleName();

    /**
     * 分享专栏文章至动态
     * @param content 动态文字内容
     * @param vArticalId 专栏文章Id
     * @param posx 坐标纬度
     * @param posy 坐标经度
     * @param location 位置描述
     */
    public void shareArticle(String content,String vArticalId,String posx,String posy,String location)
    {
        if (getView() == null)
            return;
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView());
        reqParamMap.put(ConstCodeTable.content, content);
        reqParamMap.put(ConstCodeTable.vArticalId, vArticalId);
        reqParamMap.put(ConstCodeTable.posx, posx);
        reqParamMap.put(ConstCodeTable.posy, posy);
        reqParamMap.put(ConstCodeTable.location, location);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                getView().requestErr(apiE.getErrorCode());
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                getView().requestErr(throwable.getMessage());
            }

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                getView().shareArticleCallback(response.getBody());
            }
        },NetInterfaceConstant.TrendC_shareArticle,"",reqParamMap);
    }

    /**
     *
     * 活动分享到动态
     * @param content
     * @param type 0国庆 1中秋
     * @param posx
     * @param posy
     * @param location
     */
    public void shareAct2Trends(String content, String type, double posx, double posy, String location)
    {
        if (getView() == null)
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.content, content);
        params.put(ConstCodeTable.type, type);
        params.put(ConstCodeTable.posx, String.valueOf(posx));
        params.put(ConstCodeTable.posy, String.valueOf(posy));
        params.put(ConstCodeTable.location, location);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {

            @Override
            public void onError(Throwable e)
            {
                super.onError(e);
                getView().requestErr(e.getMessage());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("活动分享到动态" + response);
                getView().shareActivityCallback(response);
            }
        }, NetInterfaceConstant.ActivityC_trend, params);
    }

    /**
     *
     * banner活动分享到动态
     * @param content
     * @param activityId 活动id
     * @param posx
     * @param posy
     * @param location
     */
    public void shareBannerAct2Trends(String content, String activityType, String activityId, double posx, double posy, String location)
    {
        if (getView() == null)
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.content, content);
        params.put(ConstCodeTable.id, activityId);
        params.put(ConstCodeTable.type, activityType);
        params.put(ConstCodeTable.posx, String.valueOf(posx));
        params.put(ConstCodeTable.posy, String.valueOf(posy));
        params.put(ConstCodeTable.location, location);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {

            @Override
            public void onError(Throwable e)
            {
                super.onError(e);
                getView().requestErr(e.getMessage());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("活动分享到动态" + response);
                getView().shareBannerActivityCallback(response);
            }
        }, NetInterfaceConstant.ActivityC_bannerTrend, params);
    }



    /**
     *
     * 轰趴餐馆分享到动态
     * @param content
     * @param url   展示图片
     * @param roomName  展示文案
     * @param id  轰趴馆id
     * @param posx
     * @param posy
     * @param location
     */
    public void shareClubToTrends(String content,String url,String roomName,String id,double posx, double posy, String location)
    {
        if (getView() == null)
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.content, content);
        params.put(ConstCodeTable.url, url);
        params.put(ConstCodeTable.roomName, roomName);
        params.put(ConstCodeTable.id, id);
       // params.put(ConstCodeTable.type, type);
        params.put(ConstCodeTable.posx, String.valueOf(posx));
        params.put(ConstCodeTable.posy, String.valueOf(posy));
        params.put(ConstCodeTable.location, location);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {

            @Override
            public void onError(Throwable e)
            {
                super.onError(e);
                if (getView()!=null)
                getView().requestErr(e.getMessage());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("轰趴参餐馆分享到动态" + response.toString());
                    if (getView()!=null)
                    getView().shareActivityCallback(response);
            }
        }, NetInterfaceConstant.HomepartyC_trend, params);
    }
}

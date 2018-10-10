package com.echoesnet.eatandmeet.presenters;

import android.text.TextUtils;

import com.echoesnet.eatandmeet.activities.TrendsDetailAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.CommentsBean;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IFTrendsDetailPre;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/10 0010
 * @description
 */
public class ImpIFTrendsDetailPre extends BasePresenter<TrendsDetailAct> implements IFTrendsDetailPre
{
    private final String TAG = ImpIFTrendsDetailPre.class.getSimpleName();
    private Gson gson;

    public ImpIFTrendsDetailPre()
    {
        this.gson = new Gson();
    }

    @Override
    public void getTrendsDetail(final String type, String tId)
    {
        if (getView() == null || TextUtils.isEmpty(tId))
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.tId, tId);
        params.put(ConstCodeTable.num, "7");
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onNext(ResponseResult response)
            {
                if (getView() == null)
                    return;
                super.onNext(response);
                Logger.t(TAG).d("动态详情" + response.getBody().toString());
                getView().getTrendsDetailCallBack(type, gson.fromJson(response.getBody(), FTrendsItemBean.class));
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                    getView().callServerErrorCallback(NetInterfaceConstant.TrendC_trendDetail, apiE.getErrorCode(), apiE.getErrBody());
            }
        }, NetInterfaceConstant.TrendC_trendDetail, null, params);
    }

    @Override
    public void getTrendComments(final String type, String tId, String startIdx, String num)
    {
        if (getView() == null || TextUtils.isEmpty(tId))
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.tId, tId);
        params.put(ConstCodeTable.startIdx, startIdx);
        params.put(ConstCodeTable.num, num);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onNext(ResponseResult response)
            {
                if (getView() == null)
                    return;
                super.onNext(response);
                Logger.t(TAG).d("评论列表" + response.getBody().toString());
                getView().getTrendsCommentsCallBack(type, (List<CommentsBean>) gson.fromJson(response.getBody(), new TypeToken<List<CommentsBean>>()
                {
                }.getType()));
            }
        }, NetInterfaceConstant.TrendC_trendComments, null, params);
    }

    @Override
    public void commentTrends(String tId, String comment, String cId)
    {
        if (getView() == null || TextUtils.isEmpty(tId))
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.tId, tId);
        params.put(ConstCodeTable.comment, comment);
        params.put(ConstCodeTable.cId, cId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                    getView().callServerErrorCallback(NetInterfaceConstant.TrendC_commentTrend, apiE.getErrorCode(), apiE.getErrBody());
            }

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("评论" + response.getBody().toString());
                getView().commentTrendsSucCallBack();
            }
        }, NetInterfaceConstant.TrendC_commentTrend, null, params);
    }

    @Override
    public void likeTrends(String tId, final String flg, final String likeNum)
    {
        if (getView() == null || TextUtils.isEmpty(tId))
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.flg, flg);
        params.put(ConstCodeTable.tId, tId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                    getView().callServerErrorCallback(NetInterfaceConstant.TrendC_likeTrend, apiE.getErrorCode(), apiE.getErrBody());
            }

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("点赞动态" + response.getBody().toString());
                if ("0".equals(response.getStatus()))
                {
                    try
                    {
                        int likeNumInt = Integer.parseInt(likeNum);
                        if ("0".equals(flg))
                        {
                            likeNumInt++;
                        } else if (likeNumInt > 0)
                        {
                            likeNumInt--;
                        }
                        getView().likeTrendsCallBack("1".equals(flg) ? "0" : "1", likeNumInt);
                    } catch (NumberFormatException e)
                    {
                        e.printStackTrace();
                        Logger.t(TAG).d(e.getMessage());
                    }
                }
            }
        }, NetInterfaceConstant.TrendC_likeTrend, null, params);
    }

    @Override
    public void focusUser(String lUId, String operFlag)
    {
        if (getView() == null)
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.lUId, lUId);
        params.put(ConstCodeTable.operFlag, operFlag);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                    getView().callServerErrorCallback(NetInterfaceConstant.LiveC_focus, apiE.getErrorCode(), apiE.getErrBody());
            }

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("关注" + response.getBody().toString());
                if ("0".equals(response.getStatus()))
                {
                    getView().focusCallBack();
                }

            }
        }, NetInterfaceConstant.LiveC_focus, null, params);
    }


    @Override
    public void deleteComment(final int position, String tId, String cId)
    {
        if (getView() == null || TextUtils.isEmpty(tId))
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.tId, tId);
        params.put(ConstCodeTable.cId, cId);

        String paramsJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.TrendC_deleteComment, gson.toJson(params));
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                    getView().callServerErrorCallback(NetInterfaceConstant.TrendC_deleteComment, apiE.getErrorCode(), apiE.getErrBody());
            }
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("删除动态评论" + response.getBody().toString());
                if ("0".equals(response.getStatus()))
                {
                    getView().deleteCommentSuc(position);
                }
            }
        }, NetInterfaceConstant.TrendC_deleteComment, null, params);
    }

    @Override
    public void deleteTrends(String tId)
    {
        if (getView() == null || TextUtils.isEmpty(tId))
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView());
        params.put(ConstCodeTable.tId, tId);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("删除动态" + response.getBody().toString());
                getView().deleteTrendsSuc();
            }
        }, NetInterfaceConstant.TrendC_deleteTrend, null, params);
    }
}

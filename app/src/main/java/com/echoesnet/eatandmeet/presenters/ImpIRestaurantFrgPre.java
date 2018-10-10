package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.CommonUserCommentBean;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.RestaurantBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IRestaurantFrgView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;


/**
 * Created by an on 2016/12/6 0006.
 * refactor by ben on 2017/10/10
 */

public class ImpIRestaurantFrgPre extends BasePresenter<IRestaurantFrgView>
{
    private final String TAG = ImpIRestaurantFrgPre.class.getSimpleName();

    /**
     * 获取屌丝评论
     *
     * @param resId
     * @param getItemStartIndex
     * @param getItemNum
     */
    public void refreshLoserCom(String resId, String getItemStartIndex, String getItemNum)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.rId, resId);
        reqParamMap.put(ConstCodeTable.num, getItemNum);
        reqParamMap.put(ConstCodeTable.startIdx, getItemStartIndex);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView()!=null)
                    getView().callServerErrorCallback(NetInterfaceConstant.EvalC_evaluationService,apiE.getErrorCode(),apiE.getErrBody());
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (getView()!=null)
                    getView().requestNetErrorCallback(NetInterfaceConstant.EvalC_evaluationService,throwable);
            }

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (getView()!=null)
                {
                    List<CommonUserCommentBean> commentLst  = new Gson().fromJson(response.getBody(), new TypeToken<List<CommonUserCommentBean>>(){}.getType());
                    getView().refreshLoserComCallback(commentLst);
                }
            }
        },NetInterfaceConstant.EvalC_evaluationService,null,reqParamMap);
    }


    /**
     * //获得详情页
     *
     * @param resId
     * @param commentNum
     */
    public void getResDetailInfo(String resId, String commentNum)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.rId, resId);
        reqParamMap.put(ConstCodeTable.num, commentNum);
        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.RestaurantC_detail, new Gson().toJson(reqParamMap));
        Logger.t(TAG).d("提交的请求：" + paramJson);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView()!=null)
                    getView().callServerErrorCallback(NetInterfaceConstant.RestaurantC_detail,apiE.getErrorCode(),apiE.getErrBody());
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (getView()!=null)
                    getView().requestNetErrorCallback(NetInterfaceConstant.RestaurantC_detail,throwable);
            }

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (getView() != null)
                {
                    RestaurantBean restaurant = new Gson().fromJson(response.getBody(), RestaurantBean.class);
                    getView().getResDetailInfoCallback(restaurant);
                }
            }
        },NetInterfaceConstant.RestaurantC_detail,null,reqParamMap);
    }
}

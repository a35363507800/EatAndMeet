package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.ResListBannerBean;
import com.echoesnet.eatandmeet.models.bean.RestaurantBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IOrderMealView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.serverdatacache.Elixir;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/15.
 */

public class ImpOrderMealView
{
    private final String TAG = ImpOrderMealView.class.getSimpleName();
    private Activity mAct;
    private IOrderMealView iOrderMealView;

    public ImpOrderMealView(Activity act, IOrderMealView iOrderMealView)
    {
        this.mAct = act;
        this.iOrderMealView = iOrderMealView;
    }

    public void getRestaurantLstES(String paraNum, String paraStartIdx, final String operateType, String circle, String sCategory, String flag, String region,
                                   double mCurrentLantitude, double mCurrentLongitude)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.num, paraNum);
        reqParamMap.put(ConstCodeTable.startIdx, paraStartIdx);
        reqParamMap.put(ConstCodeTable.circle, circle);
        reqParamMap.put(ConstCodeTable.region, region);
        reqParamMap.put(ConstCodeTable.sCategory, sCategory);
        reqParamMap.put(ConstCodeTable.flag, flag);
        reqParamMap.put(ConstCodeTable.posx, String.valueOf(mCurrentLantitude));
        reqParamMap.put(ConstCodeTable.posy, String.valueOf(mCurrentLongitude));

       HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
       {
           @Override
           public void onHandledError(ApiException apiE)
           {
               super.onHandledError(apiE);
               if (iOrderMealView != null)
               {
                   iOrderMealView.callServerErrorCallback(NetInterfaceConstant.RestaurantC_resListByES_161011,apiE.getErrorCode(),apiE.getErrBody());
               }
           }

           @Override
           public void onHandledNetError(Throwable throwable)
           {
               super.onHandledNetError(throwable);
               if (iOrderMealView==null)
               {
                   return;
               }
               Elixir.pumpCache(EamApplication.getInstance(), NetInterfaceConstant.RestaurantC_resListByES_161011, new ICommonOperateListener()
               {
                   @Override
                   public void onSuccess(String response)
                   {
                       List<RestaurantBean> restaurantBeanList = new Gson().fromJson(response, new TypeToken<List<RestaurantBean>>() {
                       }.getType());
                       if (iOrderMealView != null)
                       {
                           iOrderMealView.getRestaurantLstESCallback(restaurantBeanList,operateType);
                       }
                   }

                   @Override
                   public void onError(String code, String msg)
                   {
                       if (iOrderMealView != null)
                       {
                           iOrderMealView.callServerErrorCallback(NetInterfaceConstant.RestaurantC_resListByES_161011,code,msg);
                       }
                   }
               });
           }

           @Override
           public void onNext(ResponseResult response)
           {
               super.onNext(response);
               Elixir.updateCacheOnNewThread(EamApplication.getInstance(), NetInterfaceConstant.RestaurantC_resListByES_161011, response.getBody());
               try
               {
                   List<RestaurantBean> restaurantBeanList = new Gson().fromJson(response.getBody(), new TypeToken<List<RestaurantBean>>() {
                   }.getType());
                   if (iOrderMealView != null)
                       iOrderMealView.getRestaurantLstESCallback(restaurantBeanList, operateType);
               } catch (JsonSyntaxException e)
               {
                   e.printStackTrace();
               }
           }
       },NetInterfaceConstant.RestaurantC_resListByES_161011,null,reqParamMap);

    }

    public void getResListForAppo(String paraNum, String paraStartIdx, final String operateType, String circle, String sCategory, String flag, String region,
                                   double mCurrentLantitude, double mCurrentLongitude)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.num, paraNum);
        reqParamMap.put(ConstCodeTable.startIdx, paraStartIdx);
        reqParamMap.put(ConstCodeTable.circle, circle);
        reqParamMap.put(ConstCodeTable.region, region);
        reqParamMap.put(ConstCodeTable.sCategory, sCategory);
        reqParamMap.put(ConstCodeTable.flag, flag);
        reqParamMap.put(ConstCodeTable.posx, String.valueOf(mCurrentLantitude));
        reqParamMap.put(ConstCodeTable.posy, String.valueOf(mCurrentLongitude));
        reqParamMap.put(ConstCodeTable.streamId, EamApplication.getInstance().dateStreamId);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (iOrderMealView != null)
                {
                    iOrderMealView.callServerErrorCallback(NetInterfaceConstant.RestaurantC_resListForAppo,apiE.getErrorCode(),apiE.getErrBody());
                }
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (iOrderMealView==null)
                {
                    return;
                }
                Elixir.pumpCache(EamApplication.getInstance(), NetInterfaceConstant.RestaurantC_resListForAppo, new ICommonOperateListener()
                {
                    @Override
                    public void onSuccess(String response)
                    {
                        List<RestaurantBean> restaurantBeanList = new Gson().fromJson(response, new TypeToken<List<RestaurantBean>>() {
                        }.getType());
                        if (iOrderMealView != null)
                        {
                            iOrderMealView.getRestaurantLstESCallback(restaurantBeanList,operateType);
                        }
                    }

                    @Override
                    public void onError(String code, String msg)
                    {
                        if (iOrderMealView != null)
                        {
                            iOrderMealView.callServerErrorCallback(NetInterfaceConstant.RestaurantC_resListForAppo,code,msg);
                        }
                    }
                });
            }

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Elixir.updateCacheOnNewThread(EamApplication.getInstance(), NetInterfaceConstant.RestaurantC_resListForAppo, response.getBody());
                try
                {
                    List<RestaurantBean> restaurantBeanList = new Gson().fromJson(response.getBody(), new TypeToken<List<RestaurantBean>>() {
                    }.getType());
                    if (iOrderMealView != null)
                        iOrderMealView.getRestaurantLstESCallback(restaurantBeanList, operateType);
                } catch (JsonSyntaxException e)
                {
                    e.printStackTrace();
                }

            }
        }, NetInterfaceConstant.RestaurantC_resListForAppo, null, reqParamMap);
    }

    /**
     * 订餐页banner
     */
    public void getResListBanner()
    {
        Map<String, String> param = NetHelper.getCommonPartOfParam(mAct);
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
                if (iOrderMealView==null)
                {
                    return;
                }
                Elixir.pumpCache(EamApplication.getInstance(), NetInterfaceConstant.RestaurantC_resListBanner, new ICommonOperateListener()
                {
                    @Override
                    public void onSuccess(String response)
                    {

                        if (iOrderMealView != null)
                            iOrderMealView.getResListBannerCallback((List<ResListBannerBean>) new Gson().fromJson(response, new TypeToken<List<ResListBannerBean>>()
                            {
                            }.getType()));
                    }

                    @Override
                    public void onError(String code, String msg)
                    {
                        if (iOrderMealView != null)
                        {
                            iOrderMealView.callServerErrorCallback(NetInterfaceConstant.RestaurantC_resListBanner,code,msg);
                        }
                    }
                });
            }

            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Elixir.updateCacheOnNewThread(EamApplication.getInstance(), NetInterfaceConstant.RestaurantC_resListBanner, response.getBody());
                try
                {
                    if (iOrderMealView != null)
                        iOrderMealView.getResListBannerCallback((List<ResListBannerBean>) new Gson().fromJson(response.getBody(), new TypeToken<List<ResListBannerBean>>()
                        {
                        }.getType()));
                } catch (JsonSyntaxException e)
                {
                    e.printStackTrace();
                }
            }
        }, NetInterfaceConstant.RestaurantC_resListBanner, "", param);
    }


    /**
     * 订餐列表检索
     *
     * @param paraNum      加载默认条数
     * @param paraStartIdx 加载的起始索引
     * @param operateType  上拉或下拉类型
     * @param circle       商圈
     * @param sCategory    分类
     * @param flag         筛选条件（1：距离筛选，2：评价，3：价格低，4：价格高）
     * @param region       区域
     */
    /*void getRestaurantLstES(String paraNum, String paraStartIdx, final String operateType, String circle, String sCategory, String flag, String region)
    {
        if (pDialog != null && !pDialog.isShowing())
        {
            pDialog.show();
        }

        ArrayMap<String, String> reqParamMap = new ArrayMap<>();
        reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(getActivity()));
        reqParamMap.put(ConstCodeTable.uId, SharePreUtils.getUId(getActivity()));
        reqParamMap.put(ConstCodeTable.token, SharePreUtils.getToken(getActivity()));
        reqParamMap.put(ConstCodeTable.num, paraNum);
        reqParamMap.put(ConstCodeTable.startIdx, paraStartIdx);
        reqParamMap.put(ConstCodeTable.circle, circle);
        reqParamMap.put(ConstCodeTable.region, region);
        reqParamMap.put(ConstCodeTable.sCategory, sCategory);
        reqParamMap.put(ConstCodeTable.flag, flag);
        reqParamMap.put(ConstCodeTable.posx, String.valueOf(mCurrentLantitude));
        reqParamMap.put(ConstCodeTable.posy, String.valueOf(mCurrentLongitude));
        Logger.t(TAG).d(NetHelper.getRequestJsonStr("RestaurantC/resListByES_161011", new Gson().toJson(reqParamMap)));

        OkHttpUtils.postString()
                .url(NetHelper.SERVER_SITE)
                .mediaType(NetHelper.JSON)
                .content(NetHelper.getRequestJsonStr("RestaurantC/resListByES_161011", new Gson().toJson(reqParamMap)))
                .build()
                .execute(new ListRestaurantCallback(getActivity())
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        NetHelper.handleNetError(getActivity(), null, TAG, e);
                        if (resListView != null)
                            resListView.onRefreshComplete();
                        if (pDialog != null && pDialog.isShowing())
                            pDialog.dismiss();
                    }

                    @Override
                    public void onResponse(List<RestaurantBean> response)
                    {
                        if (response == null)
                        {
                            ToastUtils.showShort(getActivity(), "获取餐厅信息失败");
                        }
                        else
                        {
                            Logger.t(TAG).d("新餐厅数量--> " + response.size());
                            // 下拉刷新
                            if (operateType.equals("refresh"))
                            {
                                dataList.clear();
                            }
                            dataList.addAll(response);
                            adapter.notifyDataSetChanged();
                            resListView.onRefreshComplete();
                        }
                        if (pDialog != null && pDialog.isShowing())
                            pDialog.dismiss();
                    }
                });
    }*/
}

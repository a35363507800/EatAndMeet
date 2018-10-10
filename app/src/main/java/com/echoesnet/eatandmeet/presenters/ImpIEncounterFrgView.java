package com.echoesnet.eatandmeet.presenters;

import android.support.v4.app.Fragment;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.fragments.FEncounterFrg;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.ActivityWindowBean;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.EncounterBean;
import com.echoesnet.eatandmeet.models.bean.FPromotionBean;
import com.echoesnet.eatandmeet.models.bean.FinishTaskBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IEncounterFrgView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.serverdatacache.Elixir;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by an on 2017/3/29 0029.
 */

public class ImpIEncounterFrgView extends BasePresenter<FEncounterFrg>
{
    private final String TAG = ImpIEncounterFrgView.class.getSimpleName();
    private Gson gson;

    public ImpIEncounterFrgView()
    {
        gson = new Gson();
    }

    /**
     * 获取邂逅列表
     *
     * @param num      请求数量
     * @param city     城市
     * @param startIdx 起始
     * @param posx     纬度
     * @param posy     经度
     * @param sex      性别
     * @param type     refresh  add   刷新或者加载更多
     */
    public void getEncounterList(final String type, final String num, String city, String startIdx, Double posx, Double posy, String sex)
    {
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView().getActivity());
        params.put(ConstCodeTable.num, num);
        params.put(ConstCodeTable.city, city);
        params.put(ConstCodeTable.startIdx, startIdx);
        params.put(ConstCodeTable.posx, String.valueOf(posx));
        params.put(ConstCodeTable.posy, String.valueOf(posy));
        params.put(ConstCodeTable.sex, sex);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                {
                    getView().requestNetError(NetInterfaceConstant.IndexC_encounter_v402, apiE.getErrorCode());
                }
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                Elixir.pumpCache(EamApplication.getInstance(), NetInterfaceConstant.IndexC_encounter_v402, new ICommonOperateListener()
                {
                    @Override
                    public void onSuccess(String response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            String trendsCount = jsonObject.getString("trendsCount");
                            String columnsCount = jsonObject.getString("columnsCount");
                            String messageCount = jsonObject.getString("messageCount");
                            String focusTrendsCount = jsonObject.getString("focusTrendsCount");
                            String avatarAuditStatus = jsonObject.getString("avatarAuditStatus");
                            String users = jsonObject.getString("users");
                            String phUrl = jsonObject.getString("phUrl");
                            List<EncounterBean> userLst = gson.fromJson(users, new TypeToken<List<EncounterBean>>()
                            {
                            }.getType());
                            if (getView() != null)
                            {
                                getView().getEncounterSuccess(type, focusTrendsCount, trendsCount, columnsCount, messageCount, phUrl, avatarAuditStatus, userLst);
                            }
                        } catch (JSONException e)
                        {
                            Logger.t(TAG).d("邂逅列表异常" + e.getMessage());
                        } catch (Exception e)
                        {
                            Logger.t(TAG).d("列表异常" + e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String code, String msg)
                    {

                    }
                });
            }

            @Override
            public void onNext(final ResponseResult response)
            {
                super.onNext(response);
                if (getView() == null)
                    return;
                Logger.t(TAG).d("邂逅列表" + response.getBody().toString());
                try
                {
                    JSONObject jsonObject = new JSONObject(response.getBody());
                    String trendsCount = jsonObject.getString("trendsCount");
                    String columnsCount = jsonObject.getString("columnsCount");
                    String messageCount = jsonObject.getString("messageCount");
                    String focusTrendsCount = jsonObject.getString("focusTrendsCount");
                    String avatarAuditStatus = jsonObject.getString("avatarAuditStatus");
                    String users = jsonObject.getString("users");
                    String phUrl = jsonObject.getString("phUrl");
                    List<EncounterBean> userLst = gson.fromJson(users, new TypeToken<List<EncounterBean>>()
                    {
                    }.getType());
                    if (getView() != null)
                    {
                        getView().getEncounterSuccess(type, focusTrendsCount, trendsCount, columnsCount, messageCount, phUrl, avatarAuditStatus, userLst);
                    }
                } catch (JSONException e)
                {
                    Logger.t(TAG).d("邂逅列表异常" + e.getMessage());
                }
                Elixir.updateCacheOnNewThread(EamApplication.getInstance(), NetInterfaceConstant.IndexC_encounter_v402, response.getBody());
            }
        }, NetInterfaceConstant.IndexC_encounter_v402, null, params);
    }


    /**
     * 获取邂逅轮播图
     */
    public void getCarouselEnc()
    {
        if (getView() == null)
            return;
        Map<String, String> params = NetHelper.getCommonPartOfParam(getView().getActivity());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                if (getView() == null)
                    return;
                super.onHandledError(apiE);
                getView().requestNetError(NetInterfaceConstant.IndexC_carouselEnc, apiE.getErrorCode());
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                Elixir.pumpCache(EamApplication.getInstance(), NetInterfaceConstant.IndexC_carouselEnc, new ICommonOperateListener()
                {
                    @Override
                    public void onSuccess(String response)
                    {
                        if (getView() != null)
                        {
                            getView().getCarouselEncSuccess((ArrayList<FPromotionBean>) gson.fromJson(response, new TypeToken<List<FPromotionBean>>()
                            {
                            }.getType()));
                        }
                    }

                    @Override
                    public void onError(String code, String msg)
                    {
                        if (getView() != null)
                            getView().requestNetError(NetInterfaceConstant.IndexC_carouselEnc, null);
                    }
                });
            }

            @Override
            public void onNext(final ResponseResult response)
            {
                super.onNext(response);
                if (getView() == null)
                    return;
                Logger.t(TAG).d("邂逅轮播返回" + response.toString());
                if(isViewAttached())
                getView().getCarouselEncSuccess((ArrayList<FPromotionBean>) gson.fromJson(response.getBody(), new TypeToken<List<FPromotionBean>>()
                {
                }.getType()));
                Elixir.updateCacheOnNewThread(EamApplication.getInstance(), NetInterfaceConstant.IndexC_carouselEnc, response.getBody());
            }
        }, NetInterfaceConstant.IndexC_carouselEnc, null, params);
    }


    /**
     * 审核未通过时用的头像接口
     *
     * @param picUrl 头像地址
     */
    public void upLoadingPic(String picUrl)
    {
        final IEncounterFrgView counterFrgView = getView();
        if (counterFrgView == null)
            return;
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(getView().getActivity());
        reqParamMap.put(ConstCodeTable.phurl, picUrl);
        Gson gson = new Gson();
        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.UserC_modifyHead, gson.toJson(reqParamMap));
        Logger.t(TAG).d("提交的参数为》：" + paramJson);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (getView() == null)
                    return;
                Logger.t(TAG).d("审核头像返回结果》" + response.toString());
                counterFrgView.upLoadSuccess();

            }
        }, NetInterfaceConstant.UserC_modifyHead, null, reqParamMap);
    }

    /**
     * 领取7日福利
     */
    public void getSevenCheckInEnc()
    {
        final IEncounterFrgView counterFrgView = getView();
        if (counterFrgView == null)
            return;
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(((Fragment) counterFrgView).getActivity());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onNext(ResponseResult o)
            {
                super.onNext(o);
                Logger.t(TAG).d("========领取7日福利返回结果===============" + o.getBody());

                if (isViewAttached())
                    getView().getSevenCheckInEncSuccess();
            }
        }, NetInterfaceConstant.CheckInC_sevenCheckIn, "1", reqParamMap);

    }

    /**
     * 查看7日福利
     */
    public void getSevenWeal(boolean isCheckIn)
    {
        final IEncounterFrgView counterFrgView = getView();
        if (counterFrgView == null)
            return;
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(((Fragment) counterFrgView).getActivity());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onNext(ResponseResult o)
            {
                if (getView() == null)
                    return;
                super.onNext(o);
                Logger.t(TAG).d("========查看7日福利返回结果===============" + o.getBody());
                List<Map<String, String>> param = new ArrayList<Map<String, String>>();
                try
                {
                    JSONArray ja = new JSONArray(o.getBody());

                    for (int i = 0; i < ja.length(); i++)
                    {
                        JSONObject ob = ja.getJSONObject(i);
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("name", ob.getString("name"));
                        map.put("status", ob.getString("status"));
                        map.put("time", ob.getString("time"));
                        map.put("url", ob.getString("url"));
                        map.put("count", ob.getString("count"));
                        map.put("type", ob.getString("type"));
                        param.add(map);
                    }
                    if(counterFrgView!=null)
                    counterFrgView.getSevenWealSuccess(param, isCheckIn);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d("========查看7日福利解析错误===============" + o.getBody());
                }

                //  if(isViewAttached())
                // getView().getSevenWealSuccess();
            }
        }, NetInterfaceConstant.CheckInC_sevenWeal, "1", reqParamMap);

    }


    /**
     * 今日是否已经签到
     *   "month":"0：未签到，1：已签到",
     "sevenWeal":"0：未领取，1：已领取，2：七日福利已领完"

     =======
     */
    /**
     * 今日是否已经签到
     *
     * @param
     */
    public void getTodayCheck()
    {
        getTodayCheck(null);
    }

    public void getTodayCheck(final FEncounterFrg.onResultListener resultListener)
    {
        final IEncounterFrgView counterFrgView = getView();
        if (counterFrgView == null)
            return;

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(((Fragment) counterFrgView).getActivity());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onNext(ResponseResult o)
            {
                if (getView() == null)
                    return;
                super.onNext(o);
                Logger.t(TAG).d("========是否签到返回结果===============" + o.getBody());
                try
                {
                    JSONObject jb = new JSONObject(o.getBody());
                    if (resultListener == null&&counterFrgView!=null)
                        counterFrgView.getTodayCheckSuccess(jb.getString("month"), jb.getString("sevenWeal"));
                    else
                    {
                        resultListener.onResult(jb.getString("month"));
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d("========是否签到解析错误===============" + o.getBody());
                }

            }
        }, NetInterfaceConstant.CheckInC_todayCheck, "1", reqParamMap);

    }

    /**
     * 月签到情况
     */
    public void getMonthCheck(boolean isCheckIn)
    {
        final IEncounterFrgView counterFrgView = getView();
        if (counterFrgView == null)
            return;

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(((Fragment) counterFrgView).getActivity());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onNext(ResponseResult o)
            {
                super.onNext(o);
                Logger.t(TAG).d("========月签到返回结果===============" + o.getBody());
                List<Map<String, String>> param = new ArrayList<Map<String, String>>();
                List<Map<String, String>> pirzeParam = new ArrayList<Map<String, String>>();
                try
                {
                    JSONObject body=new JSONObject(o.getBody());
                    JSONArray ja = new JSONArray(body.getString("detail"));
                    JSONArray ja2 = new JSONArray(body.getString("reward"));

                    for (int i = 0; i < ja.length(); i++)
                    {
                        JSONObject ob = ja.getJSONObject(i);
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("status", ob.getString("status"));
                        map.put("date", ob.getString("date"));
                        map.put("url", ob.getString("url"));

                        param.add(map);
                    }

                    for (int i = 0; i < ja2.length(); i++)
                    {
                        JSONObject ob = ja2.getJSONObject(i);
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("url", ob.getString("url"));
                        map.put("count", ob.getString("count"));
                        map.put("type", ob.getString("type"));
                        pirzeParam.add(map);
                    }

                    if(counterFrgView!=null)
                    counterFrgView.getMonthCheckSuccess(param, pirzeParam, isCheckIn,body.getString("skin"),body.getString("icon"));
                    //为了加载签到今日礼物
                    //getMonthPrizeImage();
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d("========月签到解析错误===============" + o.getBody());
                }
            }
        }, NetInterfaceConstant.CheckInC_monthCheck_v412, "1", reqParamMap);

    }



    /*
    月签到
     */
    public void getCheckIn()
    {
        final IEncounterFrgView counterFrgView = getView();
        if (counterFrgView == null)
            return;

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(((Fragment) counterFrgView).getActivity());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onNext(ResponseResult o)
            {
                super.onNext(o);

                if(counterFrgView!=null)
                counterFrgView.getCheckInSuccess();
            }
        }, NetInterfaceConstant.CheckInC_checkIn_v412, "1", reqParamMap);

    }

    /**
     * 获取所有可领取的成就
     */
    public void getAllFinishSuccesses()
    {
        if (getView() == null)
            return;
        final IEncounterFrgView counterFrgView = getView();
        if (counterFrgView == null)
            return;

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(((Fragment) counterFrgView).getActivity());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {

            @Override
            public void onNext(ResponseResult o)
            {
                if (getView() == null)
                    return;
                super.onNext(o);
                Logger.t(TAG).d("获取所有可领取的成就" + o.getBody());
                if(counterFrgView!=null)
                counterFrgView.getAllFinishSuccessesCallback(gson.fromJson(o.getBody(), FinishTaskBean.class));
            }
        }, NetInterfaceConstant.TaskC_getAllFinishSuccesses, "1", reqParamMap);

    }

    /**
     * 获取所有可领取的日常
     */
    public void getAllFinishTask()
    {
        if (getView() == null)
            return;

        final IEncounterFrgView counterFrgView = getView();
        if (counterFrgView == null)
            return;

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(((Fragment) counterFrgView).getActivity());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult o)
            {
                if (getView() == null)
                    return;
                super.onNext(o);
                Logger.t(TAG).d("获取所有可领取的日常" + o.getBody());
                if(counterFrgView!=null)
                counterFrgView.getAllFinishTaskCallback(gson.fromJson(o.getBody(), FinishTaskBean.class));
            }
        }, NetInterfaceConstant.TaskC_getAllFinishTask, "1", reqParamMap);

    }

    /**
     * 完成所有成就
     */
    public void finishAllSuccesses()
    {
        if (getView() == null)
            return;
        final IEncounterFrgView counterFrgView = getView();
        if (counterFrgView == null)
            return;

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(((Fragment) counterFrgView).getActivity());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult o)
            {
                if (getView() == null)
                    return;
                super.onNext(o);
                Logger.t(TAG).d("完成所有成就" + o.getBody());
                if(counterFrgView!=null)
                counterFrgView.finishAllSuccessesCallback();
            }
        }, NetInterfaceConstant.TaskC_finishAllSuccesses, "1", reqParamMap);

    }

    /**
     * 完成所有日常
     */
    public void finishAllTask()
    {
        final IEncounterFrgView counterFrgView = getView();
        if (counterFrgView == null)
            return;

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(((Fragment) counterFrgView).getActivity());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult o)
            {
                if (getView() == null)
                    return;
                super.onNext(o);
                Logger.t(TAG).d("完成所有日常" + o.getBody());
                if(counterFrgView!=null)
                counterFrgView.finishAllTaskCallback();
            }
        }, NetInterfaceConstant.TaskC_finishAllTask, "1", reqParamMap);

    }

    /**
     * 中秋活动接口
     */
    public void midAutumm()
    {
        final IEncounterFrgView counterFrgView = getView();
        if (counterFrgView == null)
            return;

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(((Fragment) counterFrgView).getActivity());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult o)
            {
                if (getView() == null)
                    return;
                super.onNext(o);
                Logger.t(TAG).d("=======活动窗口接口返回结果=======:" + o.getBody());
                try
                {
                    if(counterFrgView!=null)
                    counterFrgView.midAutummSuccess((List<ActivityWindowBean>) EamApplication.getInstance().getGsonInstance().fromJson(o.getBody(), new TypeToken<List<ActivityWindowBean>>()
                    {
                    }.getType()));
                } catch (JsonSyntaxException e)
                {
                    e.printStackTrace();
                }
            }
        }, NetInterfaceConstant.ActivityC_popup, "1", reqParamMap);

    }

    /**
     * 获取看过你视频的收益然后弹窗
     */
    public void getMyRedIncome()
    {
        Map<String, String> param = NetHelper.getCommonPartOfParam(getView().getActivity());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                if (getView() == null)
                    return;
                super.onNext(response);
                try
                {
                    Logger.t(TAG).d("查询红包返回>>" + response.getBody());
                    JSONObject jsonObject = new JSONObject(response.getBody());
                    if(isViewAttached())
                    getView().getMyRedInComeCallback(jsonObject.getString("red"), jsonObject.optString("content", ""), jsonObject.optString("income", ""));
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

        }, NetInterfaceConstant.SinglesDayC_myIncome, "", param);
    }
}

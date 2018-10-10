package com.echoesnet.eatandmeet.presenters;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.activities.live.LiveChooseAnchorAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.presenters.viewinterface.ILiveChooseAnchorView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/13.
 */

public class ImpILiveChooseAnchorView extends BasePresenter<ILiveChooseAnchorView>
{
    private final String TAG = ImpILiveChooseAnchorView.class.getSimpleName();

    public void getAnchor(String getItemStartIndex, String getItemNum, final String operateType)
    {
        final ILiveChooseAnchorView liveChooseAnchorView = getView();
        if (liveChooseAnchorView == null)
            return;
        Context mContext = (LiveChooseAnchorAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.num, getItemNum);
        reqParamMap.put(ConstCodeTable.startIdx, getItemStartIndex);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (liveChooseAnchorView != null)
                    liveChooseAnchorView.requestNetErrorCallback(NetInterfaceConstant.LiveC_anchorList,throwable);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("response:" + response.toString());
                ArrayMap<String, Object> map = new ArrayMap<>();
                map.put("operateType", operateType);

                    JSONObject object = null;
                    try
                    {
                        object = new JSONObject(response);
                        List<EaseUser> signedList = new ArrayList<>();
                        JSONArray signedArray=object.getJSONArray("signedAnchorList");
                        for (int i=0;i<signedArray.length();i++)
                        {
                            JSONObject obj=signedArray.getJSONObject(i);
                            //此处的id不是环信的id，应该是环信ID
                            EaseUser sbean=new EaseUser(obj.getString("id"));
                            sbean.setuId(obj.getString("uId"));
                            sbean.setNickName(obj.getString("nicName"));
                            sbean.setAvatar(obj.getString("uphUrl"));
                            sbean.setId(obj.getString("id"));
                            sbean.setMealTotal(Integer.parseInt(obj.getString("mealTotal")));
                            signedList.add(sbean);
                        }
                        List<EaseUser> freeAnchorList =new ArrayList<>();
                        JSONArray freeArray=object.getJSONArray("anchorList");
                        for (int i=0;i<freeArray.length();i++)
                        {
                            JSONObject obj=freeArray.getJSONObject(i);
                            //此处的id不是环信的id，应该是环信ID
                            EaseUser fbean=new EaseUser(obj.getString("id"));
                            fbean.setuId(obj.getString("uId"));
                            fbean.setNickName(obj.getString("nicName"));
                            fbean.setAvatar(obj.getString("uphUrl"));
                            fbean.setId(obj.getString("id"));
                            fbean.setMealTotal(Integer.parseInt(obj.getString("mealTotal")));
                            freeAnchorList.add(fbean);
                        }

                        map.put("signedAnchorList", signedList);
                        map.put("freeAnchorList", freeAnchorList);
                        Logger.t(TAG).d("map.size()"+map.size());

                        if (liveChooseAnchorView != null)
                            liveChooseAnchorView.getAnchorCallBack(map);
                } catch (JSONException e)
                    {
                        e.printStackTrace();
                        Logger.t(TAG).d("解析错误" + response);
                    }



            }
        },NetInterfaceConstant.LiveC_anchorList,reqParamMap);

    }

    public void searchAnchor(String kw)
    {
        final ILiveChooseAnchorView liveChooseAnchorView = getView();
        if (liveChooseAnchorView == null)
            return;
        Context mContext = (LiveChooseAnchorAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.kw, kw);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (liveChooseAnchorView != null)
                    liveChooseAnchorView.searchAnchorCallback(response);
            }
        },NetInterfaceConstant.LiveC_searchAnchor,reqParamMap);
    }

}

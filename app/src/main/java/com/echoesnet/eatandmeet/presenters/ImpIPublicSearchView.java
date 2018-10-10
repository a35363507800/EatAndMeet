package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.activities.PublicSearchAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.SearchUserBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IPublicSearchPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IPublicSearchView;
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
 * @author zdw
 * @version 1.0
 * @modifier
 * @createDate 2017/7/27
 * @description
 */

public class ImpIPublicSearchView extends BasePresenter<PublicSearchAct> implements IPublicSearchPre
{
    private final String TAG = ImpIPublicSearchView.class.getSimpleName();

    @Override
    public void getSearchInfoPresent(String startIndex, String num, String type, String keyWord, final String operateType)
    {
        final IPublicSearchView searchView = getView();
        if (searchView == null)
        {
            return;
        }
        final Context mContext = getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.startIdx, startIndex);
        reqParamMap.put(ConstCodeTable.num, num);
        reqParamMap.put(ConstCodeTable.type, type);
        reqParamMap.put(ConstCodeTable.kw, keyWord);
        Gson gson = new Gson();
        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.FriendC_searchFocusOrFans, gson.toJson(reqParamMap));
        Logger.t(TAG).d("查询关注或粉丝接口提交的参数为》：" + paramJson);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("返回值--> " + response.getBody());
                List<SearchUserBean> list = new Gson().fromJson(response.getBody(), new TypeToken<List<SearchUserBean>>()
                {
                }.getType());
                if (searchView != null)
                    searchView.getSearchInfoCallback(list, operateType);
            }
        }, NetInterfaceConstant.FriendC_searchFocusOrFans, null, reqParamMap);

    }

    @Override
    public void focusPerson(List<SearchUserBean> list, String operFlag, final int position)
    {
        final IPublicSearchView searchView = getView();
        if (searchView == null)
        {
            return;
        }
        final Context mContext = getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.lUId, list.get(position).getUId());
        reqParamMap.put(ConstCodeTable.operFlag, operFlag);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("关注返回参数--> " + response.toString());
                if(response.getStatus().equals("0"))
                {
                    if (searchView != null)
                        searchView.focusCallBack(position);
                }

            }
        }, NetInterfaceConstant.LiveC_focus, null, reqParamMap);
    }

}

package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;
import android.view.View;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.SearchUserBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICSearchConversationView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @Date 2017/10/12
 * @Version 1.0
 */

public class ImpICSearchUserView extends BasePresenter<ICSearchConversationView>
{
    private final String TAG = ImpICSearchUserView.class.getSimpleName();
    private Activity mActivity;
    private ICSearchConversationView searchView;
    private Gson gson;

    public ImpICSearchUserView(Activity mActivity, ICSearchConversationView searchConversationView)
    {
        this.mActivity = mActivity;
        this.searchView = searchConversationView;
        gson = EamApplication.getInstance().getGsonInstance();
    }

    public void searchUser(String startIndex, String num, String keyWord)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        reqParamMap.put(ConstCodeTable.startIdx, startIndex);
        reqParamMap.put(ConstCodeTable.num, num);
        reqParamMap.put(ConstCodeTable.kw, keyWord);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("返回值--> " + response);
                List<SearchUserBean> list = new Gson().fromJson(response, new TypeToken<List<SearchUserBean>>()
                {
                }.getType());
                if (searchView != null)
                    searchView.searchUserCallback(list);
            }
        }, NetInterfaceConstant.FriendC_searchUser, reqParamMap);
    }

    public void focusPerson(List<SearchUserBean> list, String operFlag, final int position, final View view)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        reqParamMap.put(ConstCodeTable.lUId, list.get(position).getUId());
        reqParamMap.put(ConstCodeTable.operFlag, operFlag);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                Logger.t(TAG).d("关注返回参数--> " + response.toString());
                if (response.getStatus().equals("0"))
                {
                    if (searchView != null)
                        searchView.focusCallBack(position,view);
                }

            }
        }, NetInterfaceConstant.LiveC_focus, null, reqParamMap);
    }

}

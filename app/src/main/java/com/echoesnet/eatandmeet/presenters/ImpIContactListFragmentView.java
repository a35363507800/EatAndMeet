package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.presenters.viewinterface.IContactListFragmentView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by an on 2016/12/20 0020.
 */

public class ImpIContactListFragmentView
{
    private final String TAG = ImpIContactListFragmentView.class.getSimpleName();
    private Activity mActivity;
    private IContactListFragmentView iContactListFragmentView;
    private Gson gson;
    private Map<String, EaseUser> list;

    public ImpIContactListFragmentView(Activity mActivity, IContactListFragmentView iContactListFragmentView)
    {
        this.mActivity = mActivity;
        this.iContactListFragmentView = iContactListFragmentView;
        gson = new Gson();
    }

    /**
     * 从服务器获得联系人数据
     */
    public void getContactListFromServer()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (iContactListFragmentView != null)
                    iContactListFragmentView.getContactListFromServerCallback(response);
            }
        },NetInterfaceConstant.NeighborC_friendList,reqParamMap);
    }
    /**
     * 删除联系人
     *
     * @param toDeleteUser
     */
    public void deleteContactFromServer(final EaseUser toDeleteUser)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        reqParamMap.put(ConstCodeTable.lUId, toDeleteUser.getuId());

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (iContactListFragmentView != null)
                    iContactListFragmentView.deleteContactFromServerCallback(response, toDeleteUser);
            }
        },NetInterfaceConstant.NeighborC_delFriend,reqParamMap);
    }

}

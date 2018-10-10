package com.echoesnet.eatandmeet.presenters;

import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.activities.MakeUserInfo;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.UsersBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMakeUserInfoPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMakeUserInfoView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/18.
 */

public class ImpIMakeUserInfoView extends BasePresenter<MakeUserInfo> implements IMakeUserInfoPre
{
    private final String TAG = ImpIMakeUserInfoView.class.getSimpleName();

    @Override
    public void getRegisterPresent()
    {
        final IMakeUserInfoView makeUserinfoView = getView();
        if (makeUserinfoView == null)
        {
            return;
        }
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView() != null)
                    getView().callServerErrorCallback(NetInterfaceConstant.UserC_detailPrompt, apiE.getErrorCode(), apiE.getErrBody());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (makeUserinfoView != null)
                    makeUserinfoView.getRegisterPresentCallback(response);
            }
        }, NetInterfaceConstant.UserC_detailPrompt, reqParamMap);
    }

    /**
     * 完善资料
     *
     * @param usersBean 用户bean
     */
    @Override
    public void inputUserInfo(final UsersBean usersBean, final String userName, final String psw)
    {
        final IMakeUserInfoView makeUserinfoView = getView();
        if (makeUserinfoView == null)
            return;
        Map<String, String> reqParamMap = new HashMap<>();
        reqParamMap.put("user", new Gson().toJson(usersBean, UsersBean.class));
        reqParamMap.put(ConstCodeTable.channelId, "");
        reqParamMap.put(ConstCodeTable.deviceType, "Android");
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("返回结果》" + response);
                ArrayMap<String, Object> map = new ArrayMap<String, Object>();
                map.put("response", response);
                map.put("usersBean", usersBean);
                map.put("userName", userName);
                map.put("psw", psw);
                if (makeUserinfoView != null)
                    makeUserinfoView.inputUserInfoCallback(map);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (makeUserinfoView != null)
                    makeUserinfoView.callServerErrorCallback(NetInterfaceConstant.UserC_detail, apiE.getErrorCode(), apiE.getErrBody());
            }
        }, NetInterfaceConstant.UserC_detail, reqParamMap);
    }

    /**
     * 保存环信账号到后台,并登陆
     *
     * @param userName
     * @param psw
     */
/*    public void addHXAccountToServerAndLogin(final String userName, final String psw, final JSONObject jsonResponse)
    {
        final IMakeUserInfoView makeUserinfoView = getView();
        if (makeUserinfoView == null)
        {
            return;
        }
        final Context mContext = (MakeUserInfo) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.imuId, userName);
        reqParamMap.put(ConstCodeTable.imPass, psw);
        Gson gson = new Gson();
        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.UserC_addImu, gson.toJson(reqParamMap));
        Logger.t(TAG).d("请求参数" + paramJson);
        NetHelper.postRequest(NetInterfaceConstant.And_UserC_addImu, null, paramJson.trim(),
                new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        makeUserinfoView.requestNetError(call, e, TAG + CommonUtils.SEPARATOR + NetInterfaceConstant.UserC_addImu);
                    }

                    @Override
                    public void onResponse(String response)
                    {
                        ArrayMap<String, Object> map = new ArrayMap<String, Object>();
                        map.put("response", response);
                        map.put("jsonResponse", jsonResponse);
                        map.put("userName", userName);
                        map.put("psw", psw);
                        makeUserinfoView.addHXAccountToServerAndLoginCallback(map);
                    }
                });
    }*/

}

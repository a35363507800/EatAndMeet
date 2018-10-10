package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.activities.ApproveProgressAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.presenters.viewinterface.IApproveProgressPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IApproveProgressView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/7/17.
 */

public class ImplApproveProgressPre extends BasePresenter<IApproveProgressView> implements IApproveProgressPre
{
    private Context mContext;
    public ImplApproveProgressPre(Context context)
    {
        this.mContext=context;
    }
    private final  String TAG="ImplApproveProgressPre";

    @Override
    public void getReal()
    {
        if (getView() == null)
            return;
        final ApproveProgressAct mActivity = (ApproveProgressAct) getView();

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                mActivity.getRealCallBack(response.getBody());
            }
        },NetInterfaceConstant.LiveC_getReal,"1",reqParamMap);
    }

    /**
     * 获取公司联系方式
     */
    public void getCompContact()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (getView()  != null)
                    getView() .getContactCallback(response);
            }
        },NetInterfaceConstant.UserC_contact,reqParamMap);
    }

}

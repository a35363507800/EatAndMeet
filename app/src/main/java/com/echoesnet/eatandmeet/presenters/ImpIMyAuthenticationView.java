package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyAuthenticationView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/12.
 */

public class ImpIMyAuthenticationView
{
    private final String TAG = ImpIMyFansView.class.getSimpleName();
    private Activity mAct;
    private IMyAuthenticationView myAuthenticationView;
/*    private ArrayList<String> idImgLst = new ArrayList<>();
    private static final int TIME_OUT = 10 * 1000;   //超时时间
    private static final String CHARSET = "utf-8"; //设置编码*/

    public ImpIMyAuthenticationView(Activity activity, IMyAuthenticationView myAuthenticationView)
    {
        this.mAct = activity;
        this.myAuthenticationView = myAuthenticationView;
    }

    /**
     * 获取本人的实名状态
     */
    public void getRealNameState()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (myAuthenticationView != null)
                    myAuthenticationView.getRealNameStateCallBack(response);
            }
        }, NetInterfaceConstant.LiveC_getReal, reqParamMap);
    }


    /**
     * 上传图片及内容
     *
     * @param userName
     * @param userIdCard
     * @param idCardPicUrl 身份证图片url
     * @param userPicUrl   手持照片url
     */
    public void postRealName(String userName, String userIdCard, String idCardPicUrl, String userPicUrl)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.realName, userName);
        reqParamMap.put(ConstCodeTable.idCard, userIdCard);
        reqParamMap.put(ConstCodeTable.idCardPic, idCardPicUrl);
        reqParamMap.put(ConstCodeTable.userPic, userPicUrl);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (myAuthenticationView != null)
                    myAuthenticationView.callServerErrorCallback(NetInterfaceConstant.LiveC_realName, apiE.getErrorCode(), apiE.getErrBody());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (myAuthenticationView != null)
                    myAuthenticationView.postRealNameCallBack(response);
            }
        }, NetInterfaceConstant.LiveC_realName, reqParamMap);
    }
}

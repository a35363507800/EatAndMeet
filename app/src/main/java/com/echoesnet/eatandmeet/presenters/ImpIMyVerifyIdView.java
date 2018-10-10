package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyVerifyIdView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;

import java.util.Map;

/**
 * Created by Administrator on 2017/1/9.
 */

public class ImpIMyVerifyIdView extends BasePresenter<IMyVerifyIdView>
{
    private final String TAG = ImpIMyVerifyIdView.class.getSimpleName();

    /**
     * 实名认证
     *
     * @param name
     * @param idNumber
     */
    public void verifyIdInfo(String name, String idNumber)
    {
        Map<String, String> reqParamMap =NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.realName, name);
        reqParamMap.put(ConstCodeTable.idCard, idNumber);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().verifyIdInfoCallback(response);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
//                super.onHandledError(apiE);
                if (getView()!=null)
                    getView().callServerErrorCallback(NetInterfaceConstant.UserC_realName,apiE.getErrorCode(),apiE.getErrBody());
            }
        },NetInterfaceConstant.UserC_realName,reqParamMap);
    }

    /**
     * 与后台已经认证过的信息对比
     *
     * @param name
     * @param idNumber
     */
    public void verifyIdInfoWithExist(String name, String idNumber)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.realName, name);
        reqParamMap.put(ConstCodeTable.idCard, idNumber);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().verifyIdInfoWithExistCallback(response);
            }
        },NetInterfaceConstant.UserC_realNameStatus,reqParamMap);
    }

}

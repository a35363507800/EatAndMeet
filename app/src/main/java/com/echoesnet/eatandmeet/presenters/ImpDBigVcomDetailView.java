package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDBigVcomDetailView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/22.
 */

public class ImpDBigVcomDetailView extends BasePresenter<IDBigVcomDetailView>
{
    private static final String TAG = ImpCSendRedPacketView.class.getSimpleName();

    /**
     * 获取赞数
     *
     * @param luId
     */
    public void getBigVSupportCount(String luId)
    {
        if (getView() == null)
        {
            return;
        }
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.lUId, luId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView()!=null)
                    getView().getBigVSupportCountCallBack(response);
            }
        },NetInterfaceConstant.UserC_starLikeAmount,reqParamMap);
    }

    /**
     * 点赞
     *
     * @param luId
     */
    public void supportBigV(String luId)
    {
        if ( getView() == null)
        {
            return;
        }
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.lUId, luId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView()!=null)
                getView().supportBigVCallBack(response);
            }
        },NetInterfaceConstant.UserC_starLike,reqParamMap);
    }


}

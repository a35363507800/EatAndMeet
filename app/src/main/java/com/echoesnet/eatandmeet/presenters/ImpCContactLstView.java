package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICContactLstView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.orhanobut.logger.Logger;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/20.
 */

public class ImpCContactLstView extends BasePresenter<ICContactLstView>
{
    private final String TAG = ImpCContactLstView.class.getSimpleName();

    public void getApplyFriendInfo(String showNum)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.num, showNum);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (getView() != null)
                    getView().getApplyFriendInfoCallback(response);
            }
        },NetInterfaceConstant.NeighborC_newFriendList_v333,reqParamMap);
    }
}

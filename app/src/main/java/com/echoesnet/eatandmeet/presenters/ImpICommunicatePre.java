package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.fragments.ConversationListFragment;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

/**
 * Created by an on 2016/12/20 0020.
 */

public class ImpICommunicatePre extends BasePresenter<ConversationListFragment>
{
    private final String TAG = ImpICommunicatePre.class.getSimpleName();

    public void checkRedPacketsStates(final String userName, final boolean deleteMessage, List<String> redPacketIds)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.streamId, new Gson().toJson(redPacketIds));
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
/*                if (getView() != null)
                    getView().getCheckRedPacketsStatesSuc(response.getBody(), userName, deleteMessage);*/
            }
        }, NetInterfaceConstant.UserC_checkRedList, null, reqParamMap);
    }
}

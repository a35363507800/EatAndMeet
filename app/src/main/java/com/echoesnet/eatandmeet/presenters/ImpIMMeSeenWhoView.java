package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.WhoSeenMeBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMMeSeenWhoView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/12.
 */

public class ImpIMMeSeenWhoView extends BasePresenter<IMMeSeenWhoView>
{
    private final String TAG = ImpIMMeSeenWhoView.class.getSimpleName();

    public void getSeenMeData(String getItemStartIndex, String getItemNum, String orderType, final String operateType)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.num, getItemNum);
        reqParamMap.put(ConstCodeTable.flg, orderType);  // 我看过谁，值：lookToMe
        reqParamMap.put(ConstCodeTable.startIdx, getItemStartIndex);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                List<WhoSeenMeBean> orderLst = new Gson().fromJson(response, new TypeToken<List<WhoSeenMeBean>>(){}.getType());
                if (getView() != null)
                    getView().getSeenMeDataCallback(orderLst, operateType);
            }
        },NetInterfaceConstant.UserC_history,reqParamMap);
    }
}

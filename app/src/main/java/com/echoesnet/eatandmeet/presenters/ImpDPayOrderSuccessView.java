package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;
import android.widget.TextView;

import com.echoesnet.eatandmeet.activities.DPayOrderSuccessAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.MeetPersonBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDPayOrderSuccessView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/14.
 */

public class ImpDPayOrderSuccessView extends BasePresenter<IDPayOrderSuccessView>
{
    private final String TAG = ImpDPayOrderSuccessView.class.getSimpleName();

    public void getMeetPersonList(String sb, String orderId)
    {
        final IDPayOrderSuccessView idPayOrderSuccessView = getView();
        if (idPayOrderSuccessView == null)
        {
            return;
        }
        Activity mAct = (DPayOrderSuccessAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.orderId, orderId);
        reqParamMap.put(ConstCodeTable.rIds, sb);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                ArrayList<MeetPersonBean> orderLst = new Gson().fromJson(response, new TypeToken<List<MeetPersonBean>>()
                {
                }.getType());

                if (idPayOrderSuccessView != null)
                    idPayOrderSuccessView.getMeetPersonListCallback(orderLst);
            }
        }, NetInterfaceConstant.EncounterC_encounterPerson, reqParamMap);

    }

}

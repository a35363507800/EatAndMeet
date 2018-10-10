package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.activities.live.LiveSetAddressAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.ISetAddressView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/23.
 */

public class ImpISetAddressView extends BasePresenter<ISetAddressView>
{
    private final String TAG = ImpISetAddressView.class.getSimpleName();

    public void setPermanent(String province, String city, String region, String street, String posx, String posy)
    {
        final ISetAddressView setAddressView = getView();
        if (setAddressView == null)
        {
            return;
        }
        Context mAct = (LiveSetAddressAct)getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.province, province);
        reqParamMap.put(ConstCodeTable.city, city);
        reqParamMap.put(ConstCodeTable.region, region);
        reqParamMap.put(ConstCodeTable.street, street);
        reqParamMap.put(ConstCodeTable.posx, posx);
        reqParamMap.put(ConstCodeTable.posy, posy);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (setAddressView != null)
                    setAddressView.setPermanentCallback(response);
            }
        },NetInterfaceConstant.LiveC_setPermanent,reqParamMap);
    }
}

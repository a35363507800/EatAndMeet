package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.FaceBalanceDetailBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyFaceBalanceDetailView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/15.
 */

public class ImpMyFaceBalanceDetailView {
    private final String TAG = ImpMyFaceBalanceDetailView.class.getSimpleName();
    private Context mContext;
    private IMyFaceBalanceDetailView iMyBalanceDetailView;

    public ImpMyFaceBalanceDetailView(Context mContext, IMyFaceBalanceDetailView iMyBalanceDetailView) {
        this.mContext = mContext;
        this.iMyBalanceDetailView = iMyBalanceDetailView;
    }

    public void getBalanceDetailData(final String startIdx, final String num, final String operateType)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.startIdx, startIdx);
        reqParamMap.put(ConstCodeTable.num, num);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                List<FaceBalanceDetailBean> resLst = new Gson().fromJson(response, new TypeToken<List<FaceBalanceDetailBean>>()
                {
                }.getType());
                if (iMyBalanceDetailView!=null)
                    iMyBalanceDetailView.getBalanceDetailDataCallback(resLst,operateType);
            }
        },NetInterfaceConstant.LiveC_faceDetailWithDetail,reqParamMap);
    }

}

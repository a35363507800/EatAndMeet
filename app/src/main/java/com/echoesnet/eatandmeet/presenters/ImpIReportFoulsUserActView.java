package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IReportFoulsUserActView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;

import java.util.Map;

/**
 * Created by an on 2016/12/15.
 */

public class ImpIReportFoulsUserActView {
    private final String TAG = ImpIReportFoulsUserActView.class.getSimpleName();
    private Activity mAct;
    private IReportFoulsUserActView iReportFoulsUserActView;

    public ImpIReportFoulsUserActView(Activity mAct, IReportFoulsUserActView iReportFoulsUserActView) {
        this.mAct = mAct;
        this.iReportFoulsUserActView = iReportFoulsUserActView;
    }

    public void reportUser(String luId,String reason,String remark) {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.lUId, luId);
        reqParamMap.put(ConstCodeTable.reason, reason);
        reqParamMap.put(ConstCodeTable.remark, remark);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (iReportFoulsUserActView!=null)
                    iReportFoulsUserActView.reportUserCallback(response);
            }
        },NetInterfaceConstant.ReportC_reportUser,reqParamMap);
    }
}

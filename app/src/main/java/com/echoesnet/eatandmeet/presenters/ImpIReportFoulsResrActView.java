package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IReportFoulsResrActView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;

import java.util.Map;

/**
 * Created by an on 2016/12/15.
 */

public class ImpIReportFoulsResrActView {
    private final String TAG = ImpIReportFoulsResrActView.class.getSimpleName();
    private Activity mAct;
    private IReportFoulsResrActView iReportFoulsResrActView;

    public ImpIReportFoulsResrActView(Activity mAct, IReportFoulsResrActView iReportFoulsResrActView) {
        this.mAct = mAct;
        this.iReportFoulsResrActView = iReportFoulsResrActView;
    }

    public void reportResr(String rId,String reason,String remark) {
        Map<String, String> reqParamMap =NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.rId, rId);
        reqParamMap.put(ConstCodeTable.reason, reason);
        reqParamMap.put(ConstCodeTable.remark, remark);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (iReportFoulsResrActView!=null)
                    iReportFoulsResrActView.reportResrCallback(response);
            }
        },NetInterfaceConstant.ReportC_reportRes,reqParamMap);
    }
}

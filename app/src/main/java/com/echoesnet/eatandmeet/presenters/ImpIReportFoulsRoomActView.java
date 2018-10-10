package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IReportFoulsRoomActView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;

import java.util.Map;

/**
 * Created by an on 2016/12/15.
 */

public class ImpIReportFoulsRoomActView {
    private final String TAG = ImpIReportFoulsRoomActView.class.getSimpleName();
    private Activity mAct;
    private IReportFoulsRoomActView reportFoulsRoomActView;

    public ImpIReportFoulsRoomActView(Activity mAct, IReportFoulsRoomActView reportFoulsRoomActView) {
        this.mAct = mAct;
        this.reportFoulsRoomActView = reportFoulsRoomActView;
    }

    public void reportRoom(String roomId,String reason,String remark) {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.roomId, roomId);
        reqParamMap.put(ConstCodeTable.reason, reason);
        reqParamMap.put(ConstCodeTable.remark, remark);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (reportFoulsRoomActView!=null)
                    reportFoulsRoomActView.reportRoomCallback(response);
            }
        },NetInterfaceConstant.ReportC_reportRoom,reqParamMap);
    }
}

package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.MyResCommentBean;
import com.echoesnet.eatandmeet.models.bean.OrderRecordBean;

/**
 * Created by an on 2016/12/8 0008.
 */

public interface IDOrderRecordDetailView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void getOrderDetailSuccess(OrderRecordBean orderRecord);

    void getDeleteOrderSuccess(String response);

    void getResCommentDetailSuccess(MyResCommentBean resComment);

    void getApplyRefundSuccess(String response);

    void getShareOrderSuccess(String response);

    void getBtnOnOffSuccess(String btnOnOff);

}

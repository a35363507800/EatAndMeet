package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.MyResCommentBean;
import com.echoesnet.eatandmeet.models.bean.OrderRecordBean;

/**
 * Created by Administrator on 2016/12/16.
 */

public interface IDQuickPayOrderDetailView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetErrorCallback(String interfaceName, Throwable e);

    void getOrderDetailCallback(OrderRecordBean orderRecord);

    void deleteOrderCallback(String response);

    void getResCommentDetailCallback(MyResCommentBean resComment);
}

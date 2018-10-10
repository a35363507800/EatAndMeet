package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.ClubOrderDetailBean;
import com.echoesnet.eatandmeet.models.bean.MyResCommentBean;
import com.echoesnet.eatandmeet.models.bean.OrderRecordBean;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2018/2/8
 * @description
 */
public interface IClubOrderRecordDetailView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void getOrderDetailSuccess(ClubOrderDetailBean orderRecord);

    void getDeleteOrderSuccess(String response);

    void getResCommentDetailSuccess(MyResCommentBean resComment);

    void getApplyRefundSuccess(String response);

}

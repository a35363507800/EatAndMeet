package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.app.Dialog;

import com.echoesnet.eatandmeet.models.datamodel.OperateType;

import okhttp3.Call;

/**
 * Created by ben on 2016/12/19.
 */

public interface ISelectTableView
{
    void requestNetError(Call call, Exception e, String exceptSource);
    void getRestoreDaysCallback(String response);
    void getOpenTimePeriodDataCallback(String response, OperateType type);
    void getTableStatusFromSeverCallback(String response);
    void validSecurityCodeCallback(String response, String newPhoneNum, Dialog dialog);
}

package com.echoesnet.eatandmeet.presenters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.view.View;

import com.echoesnet.eatandmeet.activities.ChooseGoWhereAct;
import com.echoesnet.eatandmeet.activities.ClubInfoAct;
import com.echoesnet.eatandmeet.activities.HomeAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ClubInfoBean;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.SearchRestaurantBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IChooseGoWhereView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IClubInfoView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.views.widgets.ClubListView;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/1.
 */

public class ImpIClubInfoView extends BasePresenter<IClubInfoView> {
    private final String TAG = ImpIClubInfoView.class.getSimpleName();

    public void getClubInfoData(String id) {
        final IClubInfoView iClubInfoView = getView();
        if (iClubInfoView == null)
            return;
        Context mActivity = (ClubInfoAct) getView();

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        reqParamMap.put(ConstCodeTable.id, id);

        Gson gson = new Gson();
        Logger.t(TAG).d("请求参数》" + NetHelper.getRequestJsonStr(NetInterfaceConstant.HomepartyC_partyReserve, gson.toJson(reqParamMap)));

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>() {

            @Override
            public void onNext(String response) {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);

                ClubInfoBean bean = EamApplication.getInstance().getGsonInstance().fromJson(response, ClubInfoBean.class);
                if (isViewAttached())
                    iClubInfoView.getClubInfoDataCallBack(bean);
            }

            @Override
            public void onHandledError(ApiException apiE) {

                if (ErrorCodeTable.HOMEPARTY_OFFLINE.equals(apiE.getErrorCode())) {
                    new CustomAlertDialog(mActivity)
                            .builder()
                            .setTitle("提示")
                            .setMsg("该沙龙已下线")
                            .setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mActivity, HomeAct.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    mActivity.startActivity(intent);
                                }
                            })
                            .setCancelable(false)
                            .show();
                } else {
                    super.onHandledError(apiE);
                }

            }
        }, NetInterfaceConstant.HomepartyC_partyReserve, reqParamMap);

    }

    public void postClubPartyOrderToSever(String id, String date, String mealMark, String dateMark,String themeId,String remark) {
        final IClubInfoView iClubInfoView = getView();
        if (iClubInfoView == null)
            return;
        Context mActivity = (ClubInfoAct) getView();

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        reqParamMap.put(ConstCodeTable.id, id);
        reqParamMap.put(ConstCodeTable.mealMark, mealMark);
        reqParamMap.put(ConstCodeTable.dateMark, dateMark);
        reqParamMap.put(ConstCodeTable.date, date);
        reqParamMap.put(ConstCodeTable.themeId, themeId);
        reqParamMap.put(ConstCodeTable.remark, remark);

        Gson gson = new Gson();
        Logger.t(TAG).d("请求参数》" + NetHelper.getRequestJsonStr(NetInterfaceConstant.HomepartyC_partyOrder, gson.toJson(reqParamMap)));

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>() {

            @Override
            public void onNext(String response) {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);


                if (isViewAttached())
                    iClubInfoView.postOrderToServer(response);
            }

            @Override
            public void onHandledError(ApiException apiE) {


                if (apiE.getErrorCode().equals(ErrorCodeTable.HOMEPARTY_SCREENINGS_RESERVED)) {
                    new CustomAlertDialog(mActivity)
                            .builder()
                            .setTitle("温馨提示")
                            .setMsg("已经有人预定这一场了，看看其他场次吧~")
                            .setPositiveButton("确定", null)
                            .setCancelable(false)
                            .show();
                    getClubInfoData(id);
                } else if (ErrorCodeTable.HOMEPARTY_OFFLINE.equals(apiE.getErrorCode())) {
                    new CustomAlertDialog(mActivity)
                            .builder()
                            .setTitle("提示")
                            .setMsg("该沙龙已下线")
                            .setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mActivity, HomeAct.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    mActivity.startActivity(intent);
                                }
                            })
                            .setCancelable(false)
                            .show();
                } else {
                    super.onHandledError(apiE);
                    getClubInfoData(id);
                }


            }
        }, NetInterfaceConstant.HomepartyC_partyOrder, reqParamMap);

    }
}

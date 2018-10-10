package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;

import com.echoesnet.eatandmeet.activities.FoodDetailAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.DishDetailBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IFoodDetailView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/12.
 */

public class ImpIFoodDetail extends BasePresenter<IFoodDetailView>
{
    private final String TAG = ImpIFoodDetail.class.getSimpleName();

    public void getNewDishDetail(String dishId)
    {
        final IFoodDetailView foodDetailView = getView();
        if (foodDetailView == null)
        {
            return;
        }
        Activity mAct = (FoodDetailAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.dishId, dishId);


        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (foodDetailView != null)
                    foodDetailView.getNewDishDetailCallback(new Gson().fromJson(response,DishDetailBean.class));
            }
        },NetInterfaceConstant.DishC_dishDetail,reqParamMap);

    }

    /*@Deprecated
    private void getNewDishDetail(String dishId) {
        if (!mAct.isFinishing() && pDialog != null && !pDialog.isShowing())
            pDialog.show();
        Map<String, String> reqParamMap = new HashMap<>();
        reqParamMap.put(ConstCodeTable.uId, SharePreUtils.getUId(this));
        reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(this));
        reqParamMap.put(ConstCodeTable.token, SharePreUtils.getToken(this));
        reqParamMap.put(ConstCodeTable.dishId, dishId);

        OkHttpUtils.postString()
                .url(NetHelper.SERVER_SITE)
                .mediaType(NetHelper.JSON)
                .content(NetHelper.getRequestJsonStr("DishC/dishDetail", new Gson().toJson(reqParamMap)))
                .build()
                .execute(new DishDetailCallback(mAct) {
                    @Override
                    public void onError(Call call, Exception e) {
                        NetHelper.handleNetError(mAct, null, TAG, e);
                        if (pDialog != null && pDialog.isShowing())
                            pDialog.dismiss();
                    }

                    @Override
                    public void onResponse(DishDetailBean response) {
                        Logger.t(TAG).d("菜品详情获取成功--> " + response.toString());

                        if (response == null) {
                            if (pDialog != null && pDialog.isShowing())
                                pDialog.dismiss();
                            return;
                        }
                        tv_detail_title.setText(response.getDishName().toString());
                        tv_detail_price.setText("￥" + response.getDishPrice().toString());
                        *//**//*tv_total.setText(OrderMenuRightAdapter.totalNum + "");
                        tv_price.setText("￥" + CommonUtils.keep2Decimal(OrderMenuRightAdapter.totalPrice));*//**//*
                        tv_total.setText(CommonUtils.getDishCount(DishFrg.list) + "");
                        tv_price.setText("￥" + CommonUtils.keep2Decimal(CommonUtils.getDishPrice(DishFrg.list)));
                        tv_context.setText(response.getDishMemo());
                        ratingBar.setIndicator(true);
                        ratingBar.setRatingBar(Integer.parseInt(response.getDishStar()));


                        String url = response.getDishHUrl();
//                        if (!TextUtils.isEmpty(url)) {
                        if (url != null)
                            initCycleViewData(url);
//                        }

                        if (pDialog != null && pDialog.isShowing())
                            pDialog.dismiss();
                    }
                });
    }*/
}

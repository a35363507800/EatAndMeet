package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;

import com.echoesnet.eatandmeet.activities.CResStatusShowAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.DinersBean;
import com.echoesnet.eatandmeet.models.bean.TableBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICResStatusShowView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/23.
 */

public class ImpCResStatusShowView extends BasePresenter<ICResStatusShowView>
{
    private final String TAG = ImpCResStatusShowView.class.getSimpleName();

    public void getDinersInfo(String resId, String floorNum, final List<TableBean> tableEntities)
    {
        final ICResStatusShowView icResStatusShowView = getView();
        if (icResStatusShowView == null)
            return;
        Activity mAct = (CResStatusShowAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.rId, resId);
        reqParamMap.put(ConstCodeTable.floorNum, floorNum);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);

                List<DinersBean> dinerLst = new Gson().fromJson(response, new TypeToken<List<DinersBean>>(){}.getType());
                if (icResStatusShowView != null)
                    icResStatusShowView.getDinersInfoCallback(dinerLst);

            }
        },NetInterfaceConstant.NeighborC_accosted,reqParamMap);

    }

    /**
     * 获取用餐者的信息
     * @param resId 餐厅id
     */
    /*private void getDinersInfo(String resId, String floorNum, final List<TableBean>tableEntities)
    {
        if (pDialog!=null&&!pDialog.isShowing())
            pDialog.show();

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.rId, resId);
        reqParamMap.put(ConstCodeTable.floorNum,floorNum);

        Logger.t(TAG).d("请求的参数为》"+NetHelper.getRequestJsonStr("NeighborC/accosted", new Gson().toJson(reqParamMap)));

        OkHttpUtils.postString()
                .url(NetHelper.SERVER_SITE)
                .content(NetHelper.getRequestJsonStr("NeighborC/accosted", new Gson().toJson(reqParamMap)))
                .build()
                .execute(new ListDinerCallback(mContext)
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        NetHelper.handleNetError(mContext,null,TAG,e);
                        if(pDialog!=null&&pDialog.isShowing())
                            pDialog.dismiss();
                    }

                    @Override
                    public void onResponse(List<DinersBean> response)
                    {
                        try
                        {
                            setDinersDataSource(tableEntities,response);
                            startDinersAnimate(300);
                        }
                        catch (Exception e)
                        {
                            Logger.t(TAG).d(e.getMessage());
                        }
                        finally
                        {
                            if(pDialog!=null&&pDialog.isShowing())
                                pDialog.dismiss();
                        }
                    }
                });
    }*/
}

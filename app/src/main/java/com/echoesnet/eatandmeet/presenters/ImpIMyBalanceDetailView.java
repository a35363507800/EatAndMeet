package com.echoesnet.eatandmeet.presenters;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.BalanceDetailBean;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyBalanceDetailView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/12.
 */

public class ImpIMyBalanceDetailView {
    private final String TAG = ImpIMyBalanceDetailView.class.getSimpleName();
    private Context mContext;
    private IMyBalanceDetailView iMyBalanceDetailView;

    public ImpIMyBalanceDetailView(Context context, IMyBalanceDetailView iMyBalanceDetailView) {
        this.mContext = context;
        this.iMyBalanceDetailView = iMyBalanceDetailView;
    }

    public void getBalanceDetailData(final String startIdx, final String num, final String operateType) {

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.startIdx, startIdx);
        reqParamMap.put(ConstCodeTable.num, num);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                List<BalanceDetailBean> resLst = new Gson().fromJson(response, new TypeToken<List<BalanceDetailBean>>()
                {
                }.getType());
                if (iMyBalanceDetailView != null)
                    iMyBalanceDetailView.getBalanceDetailDataCallback(resLst, operateType);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (iMyBalanceDetailView!=null)
                    iMyBalanceDetailView.callServerErrorCallback(NetInterfaceConstant.DealDetailC_balanceDetailWithDetail,apiE.getErrorCode(),apiE.getErrBody());
            }

            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (iMyBalanceDetailView!=null)
                    iMyBalanceDetailView.requestNetErrorCallback(NetInterfaceConstant.DealDetailC_balanceDetailWithDetail,throwable);
            }
        },NetInterfaceConstant.DealDetailC_balanceDetailWithDetail,reqParamMap);

      /*  try {
            OkHttpUtils
                    .post()
                    .url(NetHelper.SERVER_SITE_UPDATE)
                    .addParams("businessName", NetInterfaceConstant.And_DealDetailC_balanceDetail)
                    .addParams("syncFlag", "1")
                    .addParams("appKey", NetHelper.EAM_APP_KEY)
                    .addParams("md5", MD5Util.MD5(paramJson.trim() + NetHelper.MD5_KYE))
                    .addParams("messageJson", paramJson.trim())
                    .build()
                    .execute(new ListBalanceDetailCallback() {
                        @Override
                        public void onError(Call call, Exception e) {
                            if (iMyBalanceDetailView != null)
                                iMyBalanceDetailView.requestNetError(call, e, TAG + NetInterfaceConstant.And_DealDetailC_balanceDetail);
                        }

                        @Override
                        public void onResponse(ArrayMap<String, Object> response) {
                            if (iMyBalanceDetailView != null)
                                iMyBalanceDetailView.getBalanceDetailDataCallback(response, operateType);
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /**
     * 获取余额明细信息
     *
     * @param startIdx
     * @param num
     * @param operateType
     */
    /*private void getBalanceDetailData(final String startIdx, final String num, final String operateType) {
        if (!MyBalanceDetailAct.this.isFinishing()&&pDialog!=null&&!pDialog.isShowing())
            pDialog.show();
        Map<String, Object> reqParamMap = new HashMap<>();
        reqParamMap.put(ConstCodeTable.token, SharePreUtils.getToken(this));
        reqParamMap.put(ConstCodeTable.uId, SharePreUtils.getUId(this));
        reqParamMap.put(ConstCodeTable.deviceId, CommonUtils.getDeviceId(this));
        reqParamMap.put(ConstCodeTable.startIdx, startIdx);
        reqParamMap.put(ConstCodeTable.num, num);
        Logger.t(TAG).d("提交的请求参数》"+NetHelper.getRequestJsonStr("DealDetailC/balanceDetail", new Gson().toJson(reqParamMap)));
        OkHttpUtils.postString()
                .url(NetHelper.SERVER_SITE)
                .mediaType(NetHelper.JSON)
                .content(NetHelper.getRequestJsonStr("DealDetailC/balanceDetail", new Gson().toJson(reqParamMap)))
                .build()
                .execute(new ListBalanceDetailCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        NetHelper.handleNetError(MyBalanceDetailAct.this, null,TAG,e);
                        if (pDialog != null && pDialog.isShowing())
                            pDialog.dismiss();
                    }

                    @Override
                    public void onResponse(ArrayMap<String, Object> response)
                    {
                        Logger.t(TAG).d("获取余额明细信息成功--> " + response);
                        //下拉刷新
                        if (operateType.equals("refresh")) {
                            detailBeanList.clear();
                        }
                        if(response.containsKey("result"))
                        {
                            detailBeanList.addAll((List<BalanceDetailBean>)response.get("result"));
                            adapter.notifyDataSetChanged();
                        }else if(response.containsKey("error"))
                        {
                            String code = (String) response.get("error");
                            if (!ErrorCodeTable.handleErrorCode(code,MyBalanceDetailAct.this))
                                ToastUtils.showShort(MyBalanceDetailAct.this,ErrorCodeTable.parseErrorCode(code));
                        }

                        if (prl_balance_detail != null)
                            prl_balance_detail.onRefreshComplete();

                        if (pDialog != null && pDialog.isShowing())
                            pDialog.dismiss();
                    }
                });
    }*/
}

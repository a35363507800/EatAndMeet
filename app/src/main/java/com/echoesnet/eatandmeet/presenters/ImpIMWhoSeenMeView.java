package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.WhoSeenMeBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMWhoSeenMeView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/12.
 */

public class ImpIMWhoSeenMeView {
    private final String TAG = ImpIMWhoSeenMeView.class.getSimpleName();
    private Activity mAct;
    private IMWhoSeenMeView imWhoSeenMeView;

    public ImpIMWhoSeenMeView(Activity mAct, IMWhoSeenMeView imWhoSeenMeView) {
        this.mAct = mAct;
        this.imWhoSeenMeView = imWhoSeenMeView;
    }

    public void getSeenMeData(String getItemStartIndex, String getItemNum, String orderType, final String operateType) {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.num, getItemNum);
        reqParamMap.put(ConstCodeTable.flg, orderType);  // 谁看过我，值：lookMe
        reqParamMap.put(ConstCodeTable.startIdx, getItemStartIndex);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                List<WhoSeenMeBean> orderLst = new Gson().fromJson(response, new TypeToken<List<WhoSeenMeBean>>(){}.getType());
                if (imWhoSeenMeView != null)
                    imWhoSeenMeView.getSeenMeDataCallback(orderLst, operateType);
            }
        },NetInterfaceConstant.UserC_history,reqParamMap);
    }

    /**
     * 获取谁看过我
     *
     * @param getItemStartIndex
     * @param getItemNum
     * @param orderType
     * @param operateType
     */
    /*private void getSeenMeData(String getItemStartIndex, String getItemNum, String orderType, final String operateType) {
        if (!mContext.isFinishing()&&pDialog!=null&&!pDialog.isShowing())
            pDialog.show();
        try {
            Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
            reqParamMap.put(ConstCodeTable.num, getItemNum);
            reqParamMap.put(ConstCodeTable.flg, orderType);  // 谁看过我，值：lookMe
            reqParamMap.put(ConstCodeTable.startIdx, getItemStartIndex);

            OkHttpUtils.postString().url(NetHelper.SERVER_SITE)
                    .content(NetHelper.getRequestJsonStr("UserC/history", new Gson().toJson(reqParamMap)))
                    .build()
                    .execute(new ListWhoSeenMeCallback(mContext) {
                        @Override
                        public void onError(Call call, Exception e) {
                            NetHelper.handleNetError(mContext, null,TAG,e);
                            if (mPullToRefreshListview!=null)
                                mPullToRefreshListview.onRefreshComplete();
                            if(pDialog!=null&&pDialog.isShowing())
                                pDialog.dismiss();
                        }

                        @Override
                        public void onResponse(List<WhoSeenMeBean> response) {
                            //下拉刷新
                            if (operateType.equals("refresh"))
                            {
                                dataSource.clear();
                            }
                            dataSource.addAll(response);
                            adapter.notifyDataSetChanged();
                            if (mPullToRefreshListview!=null)
                                mPullToRefreshListview.onRefreshComplete();

                            if(pDialog!=null&&pDialog.isShowing())
                                pDialog.dismiss();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}

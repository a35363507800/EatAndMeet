package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.presenters.viewinterface.IClubFoodDetailView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IClubInfoView;

/**
 * Created by Administrator on 2016/12/27.
 */

public class ImpIClubFoodDetailView extends BasePresenter<IClubFoodDetailView>
{
    private final String TAG = ImpIClubFoodDetailView.class.getSimpleName();

//    public void searchResList(String startIdx, String num, String keyword, final String type)
//    {
//        final IChooseGoWhereView iChooseGoWhereView = getView();
//        if (iChooseGoWhereView == null)
//            return;
//        Context mActivity = (ChooseGoWhereAct) getView();
//        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
//        reqParamMap.put(ConstCodeTable.kw, keyword);
//        reqParamMap.put(ConstCodeTable.num, num);
//        reqParamMap.put(ConstCodeTable.startIdx, startIdx);
//
//        Gson gson = new Gson();
//        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.RestaurantC_searchResForUInfo, gson.toJson(reqParamMap));
//        Logger.t(TAG).d("请求参数》" + NetHelper.getRequestJsonStr(NetInterfaceConstant.RestaurantC_searchResForUInfo, gson.toJson(reqParamMap)));
//
//        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
//        {
//            @Override
//            public void onHandledNetError(Throwable throwable)
//            {
//                super.onHandledNetError(throwable);
//                if (iChooseGoWhereView != null)
//                    iChooseGoWhereView.requestNetErrorCallback(NetInterfaceConstant.RestaurantC_searchResForUInfo,throwable);
//            }
//
//            @Override
//            public void onNext(String response)
//            {
//                super.onNext(response);
//                Logger.t(TAG).d("获得的结果：" + response);
//                ArrayMap<String, Object> searchResMap = new ArrayMap<String, Object>();
//
//                List<SearchRestaurantBean> resLst = new ArrayList<>();
//                resLst = new Gson().fromJson(response, new TypeToken<List<SearchRestaurantBean>>()
//                {
//                }.getType());
//
//                searchResMap.put("type", type);
//                searchResMap.put("response", resLst);
//                if (iChooseGoWhereView != null)
//                    iChooseGoWhereView.searchResCallBack(searchResMap);
//
//            }
//        },NetInterfaceConstant.RestaurantC_searchResForUInfo,reqParamMap);
//
//    }
}

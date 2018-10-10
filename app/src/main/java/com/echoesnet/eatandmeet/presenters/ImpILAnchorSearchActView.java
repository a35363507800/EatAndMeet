package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;
import android.content.Context;

import com.echoesnet.eatandmeet.activities.live.LAnchorSearchAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.AnchorSearchBean;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.ILAnchorSearchActView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;

import okhttp3.Call;


/**
 * Created by an on 2016/12/6 0006.
 */

public class ImpILAnchorSearchActView extends BasePresenter<LAnchorSearchAct> {
    private final String TAG = ImpILAnchorSearchActView.class.getSimpleName();

    /**
     * 获取搜索结果
     * @param kwStr 搜索关键字
     * @param startStr
     * @param dataNum
     */
    public void getSearchData(String kwStr, String startStr, String dataNum)
    {
        final ILAnchorSearchActView ilAnchorSearchActView = getView();
        if (ilAnchorSearchActView==null)
            return;
        Context mActivity = (LAnchorSearchAct) getView();
        Map<String ,String > params=NetHelper.getCommonPartOfParam(mActivity);
        params.put(ConstCodeTable.kw,kwStr);
        params.put(ConstCodeTable.startIdx,startStr);
        params.put(ConstCodeTable.num,dataNum);
        String paramJson= NetHelper.getRequestJsonStr( NetInterfaceConstant.LiveC_liveSearch, new Gson().toJson(params));
        Logger.t(TAG).d("params===="+paramJson);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (ilAnchorSearchActView!=null)
                    ilAnchorSearchActView.requestNetErrorCallback(NetInterfaceConstant.LiveC_liveSearch,throwable);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);

                List<AnchorSearchBean>  anchorsList= new Gson().fromJson(response,new TypeToken<List<AnchorSearchBean>>(){}.getType());
                if (ilAnchorSearchActView!=null)
                    ilAnchorSearchActView.getLiveSearchCallback(anchorsList);
            }
        },NetInterfaceConstant.LiveC_liveSearch,params);

    }
}

package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;

import com.echoesnet.eatandmeet.activities.BeckoningAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.MeetPersonBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IBeckoningActView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by an on 2016/12/26 0026.
 */

public class ImpIBeckoningActView extends BasePresenter<BeckoningAct>
{
    private final String TAG = ImpIBeckoningActView.class.getSimpleName();
    private Activity mActivity;
    private IBeckoningActView iBeckoningActView;
    private Gson gson;

    public ImpIBeckoningActView(Activity mActivity, IBeckoningActView iBeckoningActView)
    {
        this.mActivity = mActivity;
        this.iBeckoningActView = iBeckoningActView;
        gson = new Gson();
    }


    /**
     * 获得搭讪的人
     */
    public void getAroundPerson(final String num, final String operType, final int currentItem)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        reqParamMap.put(ConstCodeTable.num, num);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                List<MeetPersonBean> accostMrRightLst = new Gson().fromJson(response, new TypeToken<List<MeetPersonBean>>(){}.getType());
                if (iBeckoningActView != null)
                    iBeckoningActView.getAroundPersonCallback(accostMrRightLst, currentItem, operType);

            }
        },NetInterfaceConstant.EncounterC_encounterList,reqParamMap);

    }


    /**
     * 对邂逅的人打分
     */
    public void markLoveOrHate(String luId, final String isLove, final int position)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mActivity);
        reqParamMap.put(ConstCodeTable.lUId, luId);
        reqParamMap.put(ConstCodeTable.like, isLove);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("请求返回--> " + response);
                if (iBeckoningActView != null)
                    iBeckoningActView.loveOrHateCallback(response, isLove);
            }
        },NetInterfaceConstant.EncounterC_encounterlike,reqParamMap);
    }
}

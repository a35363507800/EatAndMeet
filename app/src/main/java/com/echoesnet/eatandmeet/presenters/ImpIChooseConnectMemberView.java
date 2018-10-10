package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.activities.live.LChoseConnectMemberAct;
import com.echoesnet.eatandmeet.controllers.okhttpCallback.BaseCallback;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.LChoseConnectMemberBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IChoseConnectMemberView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/4/1.
 */

public class ImpIChooseConnectMemberView extends BasePresenter<LChoseConnectMemberAct>
{
    private static final String TAG = ImpIChooseConnectMemberView.class.getSimpleName();

    public void getMyFansListInLiveRoom(String getItemStartIndex, String getItemNum, String roomId, final String operateType)
    {

        final Map<String, String> map = NetHelper.getCommonPartOfParam(null);
        map.put(ConstCodeTable.startIdx, getItemStartIndex);
        map.put(ConstCodeTable.num, getItemNum);
        map.put(ConstCodeTable.roomId, roomId);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                List<LChoseConnectMemberBean> list = new Gson().fromJson(response, new TypeToken<List<LChoseConnectMemberBean>>()
                {
                }.getType());
                Map resultMap = new HashMap();
                resultMap.put("response", list);
                resultMap.put("refreshType", operateType);
                if (getView()!=null)
                    getView().getMyFansListInLiveRoomCallback(resultMap);
            }
        },NetInterfaceConstant.LiveC_evenWheatList,map);


    }
}

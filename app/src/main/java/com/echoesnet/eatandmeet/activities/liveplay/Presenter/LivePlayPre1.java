package com.echoesnet.eatandmeet.activities.liveplay.Presenter;

import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.activities.liveplay.View.LivePlayAct1;
import com.orhanobut.logger.Logger;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author yang
 * @version 1.0
 * @modifier ben
 * @createDate 2017013
 * @description
 */
public class LivePlayPre1 extends LiveRoomPre1<LivePlayAct1, LiveRecord>
{
    private static final String TAG = LivePlayPre1.class.getSimpleName();

    @Override
    public void callServerWithProgress(String interfaceName, Map<String, Object> transElement,
                                       String isSync, String desc, boolean couldCancel, Map<String, String> reqParamMap)
    {
        super.callServerWithProgress(interfaceName, transElement, isSync, desc, couldCancel, reqParamMap);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }


    @Override
    public void whenSwitchRoomToUpdate()
    {
        super.whenSwitchRoomToUpdate();
    }

    public void getGroupDetailInfo(String roomId)
    {
        List<String> groupList = new ArrayList<>();
        groupList.add(roomId);
        TIMGroupManager.getInstance().getGroupDetailInfo(groupList, new TIMValueCallBack<List<TIMGroupDetailInfo>>()
        {
            @Override
            public void onError(int i, String s)
            {
                Logger.t(TAG).d("主播信息》" + "code>" + i + "msg>" + s);
            }

            @Override
            public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos)
            {
                Logger.t(TAG).d("非主播进入房间查询主播状态:" + timGroupDetailInfos.get(0).getGroupIntroduction());
                if (mActivity != null)
                    mActivity.showHostIsLeave(timGroupDetailInfos.get(0).getGroupIntroduction());
            }
        });
    }


    @Override
    public void praiseHost()
    {

    }
}

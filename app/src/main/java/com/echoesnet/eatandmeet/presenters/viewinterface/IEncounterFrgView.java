package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.ActivityWindowBean;
import com.echoesnet.eatandmeet.models.bean.EncounterBean;
import com.echoesnet.eatandmeet.models.bean.FPromotionBean;
import com.echoesnet.eatandmeet.models.bean.FinishTaskBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by an on 2017/3/29 0029.
 */

public interface IEncounterFrgView
{
    void requestNetError(String netInterface, String code);

    void getEncounterSuccess(String type,String focusTrendsCount, String trendsCount, String columnsCount,String messageCount, String phUrl, String avatarAuditStatus, List<EncounterBean> encounterList);

    void getCarouselEncSuccess(ArrayList<FPromotionBean> pBeenLst);

    void getSevenCheckInEncSuccess();

    void getSevenWealSuccess(List<Map<String, String>> param,boolean isCheckIn);

    void getTodayCheckSuccess(String month, String sevenWeal);

    void getMonthCheckSuccess(List<Map<String, String>> param,List<Map<String, String>> prizeParam,boolean isCheckIn,String skin,String icon);

    void getAllFinishSuccessesCallback(FinishTaskBean finishTaskBean);

    void getAllFinishTaskCallback(FinishTaskBean finishTaskBean);

    void finishAllSuccessesCallback();

    void finishAllTaskCallback();

    void getCheckInSuccess();

    void midAutummSuccess(List<ActivityWindowBean> listBean);

    void upLoadSuccess();

    void getMyRedInComeCallback(String red,String content,String income);
}

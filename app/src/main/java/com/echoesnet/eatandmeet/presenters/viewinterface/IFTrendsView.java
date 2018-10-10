package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.view.View;

import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;
import com.echoesnet.eatandmeet.models.bean.GameItemBean;
import com.echoesnet.eatandmeet.models.bean.UnFocusVUserBean;

import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/12.
 */

public interface IFTrendsView
{
    void requestNetError(Call call, Exception e, String exceptSource);
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void getTrendsCallback(String type, String msgNum, List<FTrendsItemBean> trendsList);
    void getLikeTrendsSuccessCallback(View view,int position, String flg, int likeNum);
    void deleteCommentSuc(int position);

    /**
     * 动态发布成功回调
     * @param tid
     * @param stamp
     */
    void PublishSuccess(String tid,String stamp);
    void getUnFocusVuser(UnFocusVUserBean unFocusVUserBean);
    void focusUserCallback(int position);
}

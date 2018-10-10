package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.CommentsBean;
import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;
import com.echoesnet.eatandmeet.models.bean.TrendsDetailBean;

import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/12.
 */

public interface IFTrendsDetailView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void getTrendsDetailCallBack(String type, FTrendsItemBean trendsDetailBean);

    void getTrendsCommentsCallBack(String type, List<CommentsBean> commentsList);

    void commentTrendsSucCallBack();

    void likeTrendsCallBack(String flg, int likeNum);

    void focusCallBack();

    void deleteTrendsSuc();

    void deleteCommentSuc(int position);
}

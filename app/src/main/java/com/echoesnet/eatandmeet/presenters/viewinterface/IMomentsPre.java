package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.view.View;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @createDate 2017/7/14 16:33
 * @description
 */

public interface IMomentsPre
{
    /**
     * 获取关注人的动态列表
     *
     * @param type     操作类型，加载还是刷新
     * @param startIdx 分页起始id
     * @param num      每页的数目
     */
   void getFollowersMoments(final String type, String startIdx, String num);

    void getUserTrends(String luid,final String type, String startIdx, String num);

    void getMyTrends(String luid,final String type, String startIdx, String num);

    /**
     * 点赞，取赞动态
     *
     * @param tId 动态id
     * @param flg 0：点赞，1：取赞
     */
    void likeTrends(View view, int position, String tId, String flg, String likeNum);

    void deleteTrends(int position, String tId);

}

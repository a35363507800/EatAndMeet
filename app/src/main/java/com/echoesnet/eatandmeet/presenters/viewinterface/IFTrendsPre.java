package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.view.View;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/18 0018
 * @description
 */
public interface IFTrendsPre
{
    /**
     * 获取动态列表
     *
     * @param type     refresh or add
     * @param startIdx
     * @param num
     */
    void getFTrends(String type, String startIdx, String num);

    /**
     * 点赞，取赞动态
     *
     * @param tId 动态id
     * @param flg 0：点赞，1：取赞
     */
    void likeTrends(View view,final int position, String tId, final String flg, final String likeNum);

    /**
     * 删除动态
     *
     */
    void deleteTrends(final int position, String tId);

    /**
     * 获取游戏列表
     */
   // void getGameList();

    /**
     * 未关注大V用户列表
     */
    void getUnFocusVuser();
    /**
     * 关注大V
     */
    void focusUser(String lUId , final int position);
}

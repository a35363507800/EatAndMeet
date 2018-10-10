package com.echoesnet.eatandmeet.presenters.viewinterface;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/27 0027
 * @description
 */
public interface IFTrendsDetailPre
{
    /**
     * 获取动态详情
     *
     * @param tId 动态id
     */
    void getTrendsDetail(String type, String tId);
    /**
     * 获取动态详情评论
     *
     * @param type
     * @param tId 动态id
     * @param startIdx
     * @param num
     */
    void getTrendComments(String type, String tId, String startIdx, String num);
    /**
     * 评论动态
     *
     * @param tId 动态id
     * @param comment  评论内容
     * @param cId   如回复评论，需传被回复评论id
     */
    void commentTrends(String tId, String comment, String cId);
    /**
     * 点赞，取赞动态
     *
     * @param tId 动态id
     * @param flg 0：点赞，1：取赞
     */
    void likeTrends(String tId, String flg, String likeNum);
    /**
     * 关注
     *
     * @param lUId
     * @param operFlag
     */
    void focusUser(String lUId,String operFlag);

    /**
     * 删除动态评论
     *
     */
    void deleteComment(int position, String tId, String cId);
    /**
     * 删除动态
     *
     */
    void deleteTrends(String tId);
}

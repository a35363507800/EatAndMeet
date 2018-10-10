package com.echoesnet.eatandmeet.presenters.viewinterface;

/**
 * Created by lc on 2017/7/26 11.
 */

public interface IDynamicStatePre
{

    /**
     * 获取动态列表
     *
     * @param luid     用户uid
     * @param startIdx 起始位置
     * @param num      数量
     * @param type     刷新类型（refresh：刷新 add：添加）
     */
    void getUserTrends(String luid, String startIdx, String num, String type);


    /**
     * 点赞，取赞动态
     *
     * @param position 索引位置
     * @param tId      动态id
     * @param flg      0：点赞，1：取赞
     * @param likeNum  点赞数量
     */
    void likeTrends(final int position, String tId, final String flg, final String likeNum);
}

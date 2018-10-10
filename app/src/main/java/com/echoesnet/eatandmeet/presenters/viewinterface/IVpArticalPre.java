package com.echoesnet.eatandmeet.presenters.viewinterface;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2017/9/12
 * @description
 */
public interface IVpArticalPre
{
    /**
     * 点赞文章
     *
     * @param position 索引位置
     * @param tid      文章id
     * @param isLike   是否喜欢
     * @param nums     点赞数
     */
    void likeArtical(int position, String tid, String isLike, String nums);

    /**
     * 获取大V文章列表
     *
     * @param uId       用户uid
     * @param startIdex 起始位置
     * @param Nums      数目
     * @param type      类型
     */
    void getArticalList(String uId, String startIdex, String Nums, String type);
}

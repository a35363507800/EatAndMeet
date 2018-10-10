package com.echoesnet.eatandmeet.presenters.viewinterface;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @modifier
 * @createDate 2018/2/5
 * @description
 */
public interface IClubPre
{
    /**
     * @param paraNum 拉取数量
     * @param paraStartIdx  拉去取始索引
     * @param circle    商圈
     * @param region   区
     * @param flag   排序
     * @param mCurrentLantitude   x
     * @param mCurrentLongitude   y
     * @param operateType         刷新方式
     */
    void getClubList(String paraNum,String paraStartIdx,String circle,String region,String flag,Double mCurrentLantitude,Double mCurrentLongitude,String operateType);
}

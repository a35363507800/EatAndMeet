package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.GameItemBean;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2017/10/27
 * @description 发现页游戏专区接口
 */
public interface IGameListView
{
    /**
     * 获取游戏列表
     *
     * @param gameItemBeanList 后台返回数据游戏信息集合
     */
    void getGameListCallback(List<GameItemBean> gameItemBeanList);
}

package com.echoesnet.eatandmeet.presenters.viewinterface;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/19 0019
 * @description
 */
public interface ITrendsSearchLocationPre
{
    /**
     * 搜索位置
     * @param type  refresh or add
     * @param city     搜索城市
     * @param keywords 搜索关键字
     * @param pageNum 页数
     */
    void searchLocation(String type,String city,String keywords,int pageNum);
}

package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.baidu.mapapi.search.core.PoiInfo;

import java.util.List;

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
public interface ITrendsSearchLocationView
{
    void searchLocationCallBack(String type, List<PoiInfo> poiInfoList);
}

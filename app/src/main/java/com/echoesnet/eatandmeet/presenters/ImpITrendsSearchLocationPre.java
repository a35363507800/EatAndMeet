package com.echoesnet.eatandmeet.presenters;

import android.text.TextUtils;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.echoesnet.eatandmeet.activities.TrendsSearchLocationAct;
import com.echoesnet.eatandmeet.presenters.viewinterface.ITrendsSearchLocationPre;
import com.orhanobut.logger.Logger;

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
public class ImpITrendsSearchLocationPre extends BasePresenter<TrendsSearchLocationAct> implements ITrendsSearchLocationPre
{
    private final String TAG = ImpITrendsSearchLocationPre.class.getSimpleName();

    private PoiSearch mPoiSearch;

    @Override
    public void searchLocation(final String type, String city, String keywords, int pageNum)
    {
        if (mPoiSearch == null)
        {
            if (TextUtils.isEmpty(city))
                city = "天津";
            mPoiSearch = PoiSearch.newInstance();
        }
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener()
        {
            @Override
            public void onGetPoiResult(PoiResult poiResult)
            {
                Logger.t(TAG).d("搜索结果>>>>" + poiResult.toString());
                //获取POI检索结果
                List<PoiInfo> allPoi = poiResult.getAllPoi();
                if (getView() != null)
                    getView().searchLocationCallBack(type, allPoi);
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult)
            {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult)
            {

            }

        });
        try
        {
            Logger.t(TAG).d(keywords + "|" + city + "|" + pageNum);
            mPoiSearch.searchInCity(new PoiCitySearchOption().keyword(keywords).city(city).pageNum(pageNum));
        } catch (Exception e)
        {
            Logger.t(TAG).d(e.getMessage());
        }
    }

    public void onDestroy()
    {
        if (mPoiSearch != null)
            mPoiSearch.destroy();
    }
}

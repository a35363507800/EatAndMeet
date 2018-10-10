package com.echoesnet.eatandmeet.presenters;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.echoesnet.eatandmeet.activities.TrendsSelectLocationAct;
import com.echoesnet.eatandmeet.presenters.viewinterface.ITrendsSelectLocationPre;
import com.orhanobut.logger.Logger;

import java.text.DecimalFormat;
import java.util.List;

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
public class ImpITrendsSelectLocationPre extends BasePresenter<TrendsSelectLocationAct> implements ITrendsSelectLocationPre
{
    private final String TAG = ImpITrendsSelectLocationPre.class.getSimpleName();
    //定位的客户端
    private LocationClient mLocClient;
    /**
     * 最新一次的经纬度
     */
    private double mCurrentLantitude;//经度
    private double mCurrentLongitude;//纬度
    private String mCity;
    /***
     * 是否是第一次定位
     */
    private volatile boolean isFirstLocation = true;

    public String getmCity()
    {
        return mCity;
    }

    @Override
    public void searNearLocation()
    {
        if (mLocClient == null)
        {
            mLocClient = new LocationClient(getView().getApplicationContext());
            //绑定定位监听
            mLocClient.registerLocationListener(new MyLocationListener());
            //配置定位SDK参数
            LocationClientOption option = new LocationClientOption();
            //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
            option.setScanSpan(1000 * 60);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
            option.setOpenGps(true);//可选，默认false,设置是否使用gps
            option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            option.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
            option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
            mLocClient.setLocOption(option);
        }
        mLocClient.start();
    }

    public void onDestroy()
    {
        if (mLocClient != null && mLocClient.isStarted())
            mLocClient.stop();
    }

    /*****
     * 定位结果回调，重写onReceiveLocation方法
     */
    private class MyLocationListener implements BDLocationListener
    {
        @Override
        public void onReceiveLocation(BDLocation location)
        {
            if (null != location && location.getLocType() != BDLocation.TypeServerError)
            {
                DecimalFormat decimalFormat = new DecimalFormat("#.0000000");
                mCurrentLantitude = location.getLatitude();
                mCurrentLongitude = location.getLongitude();
                mCity = location.getCity();
                Logger.t(TAG).d("位置：经度：" + mCurrentLantitude + " 纬度：" + mCurrentLongitude);
                // 第一次定位时，将地图位置移动到当前位置
                if (isFirstLocation)
                {
                    if (Double.compare(mCurrentLantitude, 4.9E-324) != 0)
                    {
                        Logger.t(TAG).d("开始搜索附近");
                        isFirstLocation = false;
                        ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption();
                        LatLng currentLoc = new LatLng(mCurrentLantitude, mCurrentLongitude);
                        reverseGeoCodeOption.location(currentLoc);
                        //地理编码查询接口
                        GeoCoder geoCoder = GeoCoder.newInstance();
                        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener()
                        {
                            @Override
                            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult)
                            {
                                Logger.t(TAG).d("搜索附近 onGetGeoCodeResult");
                            }

                            @Override
                            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult)
                            {
                                Logger.t(TAG).d("搜索附近 onGetReverseGeoCodeResult");
                                //附近位置信息列表
                                List<PoiInfo> poiInfoList = reverseGeoCodeResult.getPoiList();
                                if (getView() != null)
                                    getView().searchLocationCallBack(poiInfoList);
                            }
                        });
                        geoCoder.reverseGeoCode(reverseGeoCodeOption);
                       // mLocClient.stop();
                    }
                    Logger.t(TAG).d("定位成功》" + "lan>" + decimalFormat.format(mCurrentLantitude) + "lon>" + decimalFormat.format(mCurrentLongitude));
                    //Logger.t(TAG).d("是否移动焦点："+String.valueOf(isFirstLocation));
                }

            }
        }
    }
}

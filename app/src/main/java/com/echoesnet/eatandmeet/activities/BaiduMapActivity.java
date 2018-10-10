/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.echoesnet.eatandmeet.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaiduMapActivity extends BaseActivity
{

    private final static String TAG = BaiduMapActivity.class.getSimpleName();
    FrameLayout mMapViewContainer = null;
    LocationClient mLocClient;
    public MyLocationListener myListener = new MyLocationListener();

    EditText indexText = null;
    int index = 0;
    // LocationData locData = null;
    private BDLocation lastLocation = null;
    private Context instance;
    ProgressDialog progressDialog;
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.bmapView)
    MapView bmapView;
    private BaiduMap mBaiduMap;

    private List<PoiInfo> poiInfoList;


    public class BaiduSDKReceiver extends BroadcastReceiver
    {
        public void onReceive(Context context, Intent intent)
        {
            String s = intent.getAction();
            String st1 = getResources().getString(R.string.Network_error);
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR))
            {

                String st2 = getResources().getString(R.string.please_check);
                ToastUtils.showShort(st2);
            }
            else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR))
            {
                ToastUtils.showShort(st1);
            }
        }
    }

    private BaiduSDKReceiver mBaiduReceiver;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        instance = this;
        //initialize SDK with context, should call this before setContentView
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_baidumap);
        ButterKnife.bind(this);
        TextView tvTitle = topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                finish();
            }

            @Override
            public void right2Click(View view)
            {
                sendLocation();
            }
        });
        tvTitle.setText("位置信息");
        tvTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0);

        List<Map<String, TextView>> navBtns = topBarSwitch.getNavBtns2(new int[]{1, 0, 0, 1});
        TextView tv = navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON);
        tv.setText("发送");
        tv.setTextColor(ContextCompat.getColor(instance, R.color.C0412));
        tv.setTextSize(16);
        Logger.t(TAG).d("result>" + Double.compare(latitude, 0.0));
        if (Double.compare(latitude, 0.0) == 0)
            tv.setVisibility(View.VISIBLE);
        else
            tv.setVisibility(View.GONE);

        LocationMode mCurrentMode = LocationMode.NORMAL;
        mBaiduMap = bmapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        initMapView();
        if (latitude == 0)
        {

            bmapView = new MapView(this, new BaiduMapOptions());
            mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                    mCurrentMode, true, null));
            showMapWithLocationClient();
        }
        else
        {
            double longtitude = intent.getDoubleExtra("longitude", 0);
            String address = intent.getStringExtra("address");
            LatLng p = new LatLng(latitude, longtitude);
            bmapView = new MapView(this,
                    new BaiduMapOptions().mapStatus(new MapStatus.Builder()
                            .target(p).build()));
            showMap(latitude, longtitude, address);
        }



        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mBaiduReceiver = new BaiduSDKReceiver();
        registerReceiver(mBaiduReceiver, iFilter);
    }

    private void showMap(double latitude, double longtitude, String address)
    {
        LatLng llA = new LatLng(latitude, longtitude);
        /*CoordinateConverter converter = new CoordinateConverter();
        converter.coord(llA);
        converter.from(CoordinateConverter.CoordType.COMMON);
        LatLng convertLatLng = converter.convert();*/
        OverlayOptions ooA = new MarkerOptions().position(llA).icon(BitmapDescriptorFactory
                .fromResource(R.drawable.ease_icon_marka))
                .zIndex(4).draggable(true);
        mBaiduMap.addOverlay(ooA);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(llA, 17.0f);
        mBaiduMap.animateMapStatus(u);
    }

    private void showMapWithLocationClient()
    {
        String str1 = getResources().getString(R.string.Making_sure_your_location);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(str1);

        progressDialog.setOnCancelListener(new OnCancelListener()
        {

            public void onCancel(DialogInterface arg0)
            {
                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
                Log.d("map", "cancel retrieve location");
                finish();
            }
        });
        progressDialog.show();

        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// open gps
        // Johnson change to use gcj02 coordination. chinese national standard,so need to conver to bd09 everytime when draw on baidu map
        option.setAddrType("all");
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(1000 * 60);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocClient.setLocOption(option);
    }

    @Override
    protected void onPause()
    {
//        bmapView.onPause();
        if (mLocClient != null)
        {
            mLocClient.stop();
        }
        super.onPause();
        lastLocation = null;
    }

    @Override
    protected void onResume()
    {
//        bmapView.onResume();
        if (mLocClient != null)
        {
            mLocClient.start();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        if (mLocClient != null)
            mLocClient.stop();
        bmapView.onDestroy();
        unregisterReceiver(mBaiduReceiver);
        super.onDestroy();
    }

    private void initMapView()
    {
        bmapView.setLongClickable(true);
    }

    /**
     * format new location to string and show on screen
     */
    public class MyLocationListener implements BDLocationListener
    {
        @Override
        public void onReceiveLocation(BDLocation location)
        {
            if (location == null)
            {
                return;
            }
            Log.d("map", "On location change received:" + location);
            Log.d("map", "addr:" + location.getAddrStr());
            if (progressDialog != null)
            {
                progressDialog.dismiss();
            }

            if (lastLocation != null)
            {
                if (lastLocation.getLatitude() == location.getLatitude() && lastLocation.getLongitude() == location.getLongitude())
                {
                    Log.d("map", "same location, skip refresh");
                    // mMapView.refresh(); //need this refresh?
                    return;
                }
            }
            lastLocation = location;
            mBaiduMap.clear();
            final LatLng llA = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
//            CoordinateConverter converter = new CoordinateConverter();
//            converter.coord(llA);
//            converter.from(CoordinateConverter.CoordType.COMMON);
//            LatLng convertLatLng = converter.convert();
            OverlayOptions ooA = new MarkerOptions().position(llA).icon(BitmapDescriptorFactory
                    .fromResource(R.drawable.ease_icon_marka))
                    .zIndex(4).draggable(true);
            mBaiduMap.addOverlay(ooA);
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(llA, 17.0f);
            mBaiduMap.animateMapStatus(u);

            ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption();
            reverseGeoCodeOption.location(llA);
            //地理编码查询接口
            GeoCoder geoCoder = GeoCoder.newInstance();
            geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener()
            {
                @Override
                public void onGetGeoCodeResult(GeoCodeResult geoCodeResult)
                {

                }

                @Override
                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult)
                {
                    //附近位置信息列表
                    poiInfoList = reverseGeoCodeResult.getPoiList();
                    for (PoiInfo poiInfo : poiInfoList)
                    {
                        Logger.t(TAG).d("定位地址：lla.latitude:" + llA.latitude + " | lla.longitude" + llA.longitude + " | " + poiInfo.name + " | " + poiInfo.address);
                    }
                }
            });
            geoCoder.reverseGeoCode(reverseGeoCodeOption);
        }

        public void onReceivePoi(BDLocation poiLocation)
        {
            if (poiLocation == null)
            {
                return;
            }
        }
    }

    public void back(View v)
    {
        finish();
    }

    public void sendLocation()
    {
        Intent intent = this.getIntent();
        intent.putExtra("latitude", lastLocation.getLatitude());
        intent.putExtra("longitude", lastLocation.getLongitude());
        intent.putExtra("address", lastLocation.getAddrStr());
        if (poiInfoList != null && poiInfoList.size() != 0)
            intent.putExtra("address_name", poiInfoList.get(0).name);
        this.setResult(RESULT_OK, intent);
        finish();
//        overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
    }

}

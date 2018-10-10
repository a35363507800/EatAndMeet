package com.echoesnet.eatandmeet.activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/21 0021
 * @description 动态地图显示位置
 */
public class ShowLocationAct extends BaseActivity
{
    private final String TAG = ShowLocationAct.class.getSimpleName();
    @BindView(R.id.tbs_top_bar)
    TopBarSwitch tbsTopBar;
    private MapView mBdMapView;
    //地图对象
    private BaiduMap mBaiduMap;
    private String posx, posy;
    private Activity mAct;
    private String location;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_show_location);
        ButterKnife.bind(this);
        mAct = this;
        mBdMapView = (MapView) findViewById(R.id.bd_mapView);
        posx = getIntent().getStringExtra("posx");
        posy = getIntent().getStringExtra("posy");
        location = getIntent().getStringExtra("location");
        mBdMapView.showZoomControls(false);
        initMap();
        tbsTopBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                finish();
            }

            @Override
            public void right2Click(View view)
            {

            }
        });
        tbsTopBar.setBackground(ContextCompat.getDrawable(this, R.drawable.transparent));
    }

    /**
     * 初始化地图
     */
    private void initMap()
    {
        mBaiduMap = mBdMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //设置是否允许定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //地图的最大最小缩放比例3-21
        mBaiduMap.setMaxAndMinZoomLevel(21, 11);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener()
        {
            @Override
            public boolean onMapPoiClick(MapPoi arg0)
            {
                return false;
            }

            @Override
            public void onMapClick(LatLng arg0)
            {
                //mBaiduMap.hideInfoWindow();
            }
        });
        try
        {
            //定义Maker坐标点
            LatLng point = new LatLng(Double.parseDouble(posx), Double.parseDouble(posy));
            center2myLoc(point);
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        }
    }

    /**
     * 地图移动到指定的位置
     */
    private void center2myLoc(LatLng ll)
    {
        // 设置自定义图标
        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.daohang);
        OverlayOptions overlayOptions = new MarkerOptions().position(ll)
                .icon(mCurrentMarker).zIndex(5);
        mBaiduMap.addOverlay(overlayOptions);
        //LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(u);
        showPopupWindow(ll);
    }

    private void showPopupWindow(LatLng p)
    {
        InfoWindow mInfoWindow;
        //自定义气泡形状
        View view = LayoutInflater.from(mAct).inflate(R.layout.map_loc_infowindow, null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        TextView resName = (TextView) view.findViewById(R.id.res_name);
        TextView resLocation = (TextView) view.findViewById(R.id.res_location);
        resLocation.setTextSize(15);
        resLocation.setText(location);
        Button btnNavigate = (Button) view.findViewById(R.id.btn_navigate);
        btnNavigate.setVisibility(View.GONE);
        resName.setVisibility(View.GONE);
        mInfoWindow = new InfoWindow(view, p, -120);
        // 显示InfoWindow
        mBaiduMap.showInfoWindow(mInfoWindow);
    }
}

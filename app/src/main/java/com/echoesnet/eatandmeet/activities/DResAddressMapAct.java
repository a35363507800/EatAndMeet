package com.echoesnet.eatandmeet.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.deviceSensors.MyOrientationListener;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.echoesnet.eatandmeet.views.widgets.baiduMap.overlayutil.OverlayManager;
import com.echoesnet.eatandmeet.views.widgets.baiduMap.overlayutil.WalkingRouteOverlay;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class DResAddressMapAct extends BaseActivity
{
    //region 变量
    private final static String TAG = DResAddressMapAct.class.getSimpleName();
    @BindView(R.id.bd_mapView)
    MapView mBdMapView;
    @BindView(R.id.top_bar)
    TopBar topBar;

    //地图对象
    private BaiduMap mBaiduMap;
    /**
     * 定位的客户端
     */
    private LocationClient mLocClient;
    private Activity mContext;

    // 初始化全局 bitmap 信息，不用时及时 recycle
    private BitmapDescriptor mIconMaker;
    /***
     * 是否是第一次定位
     */
    private volatile boolean isFirstLocation = true;

    /**
     * 定位的监听器
     */
    public MyLocationListener mMyLocationListener;
    /**
     * 当前定位的模式
     */
    private MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

    /**
     * 最新一次的经纬度
     */
    private double mCurrentLantitude;
    private double mCurrentLongitude;

    //导航起始点
    private double mStartLatitude;
    private double mStartLongitude;

    //导航终点
    private double mStopLatitude = 39.123736;
    private double mStopLongitude = 117.246041;

    /**
     * 当前的精度
     */
    private float mCurrentAccracy;
    /**
     * 方向传感器的监听器
     */
    private MyOrientationListener myOrientationListener;
    /**
     * 方向传感器X方向的值
     */
    private int mXDirection;

    // 搜索相关
    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
    RouteLine route = null;
    boolean useDefaultIcon = false;
    OverlayManager routeOverlay = null;
    private boolean firstClick = true;
    private Dialog pDialog;
    private String resName;
    private String resAddress;
    private String resType = "";
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_res_address_map);
        ButterKnife.bind(this);
        initAfterViews();
    }

    @Override
    protected void onStart()
    {
        Logger.t(TAG).d("地图》onStart");
        // 开启图层定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocClient.isStarted())
        {
            Logger.t(TAG).d("地图定位开始");
            mLocClient.start();
        }
        // 开启方向传感器
        myOrientationListener.start();
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mBdMapView.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mBdMapView.onPause();
    }

    @Override
    protected void onStop()
    {
        // 关闭图层定位
        mBaiduMap.setMyLocationEnabled(false);
        mLocClient.stop();
        // 关闭方向传感器
        myOrientationListener.stop();
        super.onStop();
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mBdMapView.onDestroy();
        mIconMaker.recycle();
        mSearch.destroy();
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    void initAfterViews()
    {
        mContext = this;

        resType = getIntent().getStringExtra("resType");
        topBar.setTitle(TextUtils.equals("club",resType)?"沙龙地址":"餐厅地址");
        topBar.getRightButton().setVisibility(View.VISIBLE);
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                DResAddressMapAct.this.finish();
            }

            @Override
            public void left2Click(View view)
            {

            }

            @Override
            public void rightClick(View view)
            {

            }
        });

        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理...");
        pDialog.setCancelable(false);
        mIconMaker = BitmapDescriptorFactory.fromResource(R.drawable.maker);

        String[] location = getIntent().getStringArrayExtra("location");
        mStopLatitude = Double.parseDouble(location[1]);
        mStopLongitude = Double.parseDouble(location[0]);

        resName = getIntent().getStringExtra("resName");
        resAddress = getIntent().getStringExtra("resAddress");
        Logger.t(TAG).d("经度：" + mStopLatitude + "纬度：" + mStopLongitude);
        //CommonUtils.openPermissionSettings(mContext);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            DResAddressMapActPermissionsDispatcher.onLocationPermGrantedWithPermissionCheck(DResAddressMapAct.this);

        initMap();
        initLocation();
        // 初始化传感器
        initOrientationListener();
        //设置maker
        setMarker(mStopLatitude, mStopLongitude);
        //initMarkerClickEvent();
        initRoutePlan();
    }


    //初始化地图
    private void initMap()
    {
        mBaiduMap = mBdMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

/*      BaiduMapOptions options = new BaiduMapOptions();
        options.compassEnabled(false); // 不允许指南针
        options.zoomControlsEnabled(false); // 不显示缩放按钮
        options.scaleControlEnabled(false); // 不显示比例尺
        */
        //设置是否允许定位图层
        //mBaiduMap.setMyLocationEnabled(true);
        //地图的最大最小缩放比例3-18
        mBaiduMap.setMaxAndMinZoomLevel(18, 10);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17.0f);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener()
        {
            @Override
            public boolean onMapPoiClick(MapPoi arg0)
            {
                //ToastUtils.showShort(mContext,"什么东西");
                return false;
            }

            @Override
            public void onMapClick(LatLng arg0)
            {
                mBaiduMap.hideInfoWindow();
            }
        });
    }

    //初始化定位
    private void initLocation()
    {
        //初始化定位客户端
        mLocClient = new LocationClient(getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        //绑定定位监听
        mLocClient.registerLocationListener(mMyLocationListener);
        //配置定位SDK参数
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(5000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
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

    private void initRoutePlan()
    {
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(new MyRountPlanResultListener());
    }

    /**
     * 初始化方向传感器
     */
    private void initOrientationListener()
    {
        myOrientationListener = new MyOrientationListener(getApplicationContext());
        myOrientationListener
                .setOnOrientationListener(new MyOrientationListener.OnOrientationListener()
                {
                    @Override
                    public void onOrientationChanged(float x)
                    {
                        mXDirection = (int) x;
                        // 构造定位数据
                        MyLocationData locData = new MyLocationData.Builder()
                                .accuracy(mCurrentAccracy)
                                // 此处设置开发者获取到的方向信息，顺时针0-360
                                .direction(mXDirection)
                                .latitude(mCurrentLantitude)
                                .longitude(mCurrentLongitude)
                                .build();
                        // 设置定位数据
                        mBaiduMap.setMyLocationData(locData);
                        // 设置自定义图标
                        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                                .fromResource(R.drawable.daohang);
                        //p2:是否允许显示方向信息,第三参数为自定义图标
                        MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
                        mBaiduMap.setMyLocationConfigeration(config);
                    }
                });
    }


    //设置marker
    private void setMarker(double lat, double lng)
    {
        LatLng latLng = null;
        OverlayOptions overlayOptions = null;
        Marker marker = null;

        // 位置
        latLng = new LatLng(lat, lng);
        //图标
        overlayOptions = new MarkerOptions().position(latLng)
                .icon(mIconMaker).zIndex(5);
        marker = (Marker) mBaiduMap.addOverlay(overlayOptions);
        Bundle bundle = new Bundle();
        bundle.putDoubleArray("location", new double[]{lat, lng});
        bundle.putString("target", "target");
        marker.setExtraInfo(bundle);

        // 将地图移到到最后一个经纬度位置
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
        //mBaiduMap.animateMapStatus(u);
        mBaiduMap.setMapStatus(u);
        showPopupWindow(latLng);
    }

    private void showPopupWindow(LatLng p)
    {
        InfoWindow mInfoWindow;
        //自定义气泡形状
        View view = LayoutInflater.from(mContext).inflate(R.layout.map_loc_infowindow, null);
        TextView resName = (TextView) view.findViewById(R.id.res_name);
        TextView resLocation = (TextView) view.findViewById(R.id.res_location);
        resName.setText(this.resName);
        resLocation.setText(resAddress);
        Button btnNavigate = (Button) view.findViewById(R.id.btn_navigate);


        btnNavigate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                //   localNavigation();
                if (pDialog != null && !pDialog.isShowing())
                    pDialog.show();

                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    if (pDialog != null && pDialog.isShowing())
                        pDialog.dismiss();

                    if (firstClick)
                    {
                        CommonUtils.openPermissionSettings(mContext, getResources().getString(R.string.per_permission_never_ask).replace(CommonUtils.SEPARATOR, "地理位置"));
                        firstClick=false;
                    }
                    else
                        ToastUtils.showShort(getResources().getString(R.string.per_permission_never_ask).replace(CommonUtils.SEPARATOR, "地理位置"));

                } else
                {
                    remoteNavigation(new LatLng(mStartLatitude, mStartLongitude), new LatLng(mStopLatitude, mStopLongitude), 1);
                }

                //ToastUtils.showShort(mContext,"导航");
            }
        });

        // 将marker所在的经纬度的信息转化成屏幕上的坐标
//                final LatLng ll = marker.getPosition();
//                Point p = mBaiduMap.getProjection().toScreenLocation(ll);
//                p.y -= 47;
//                LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
        // 为弹出的InfoWindow添加点击事件

        mInfoWindow = new InfoWindow(view, p, -120);
        // 显示InfoWindow
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    @NeedsPermission({Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS, Manifest.permission.ACCESS_FINE_LOCATION})
    void onLocationPermGranted()
    {
        Logger.t(TAG).d("允许定位权限");
    }

    @OnPermissionDenied({android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS, Manifest.permission.ACCESS_FINE_LOCATION})
    void onLocationPermDenied()
    {
        Logger.t(TAG).d("拒绝获取权限");
        ToastUtils.showLong("小饭没有获得相应的权限，无法为您进一步提供服务");
    }

    @OnNeverAskAgain({android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS, Manifest.permission.ACCESS_FINE_LOCATION})
    void onLocationPermNeverAsk()
    {
        Logger.t(TAG).d("点击了不要再次询问获取权限");
        CommonUtils.openPermissionSettings(mContext, getResources().getString(R.string.per_permission_never_ask).replace(CommonUtils.SEPARATOR, "地理位置"));
        ToastUtils.showLong(getString(R.string.per_phone_contact_never_gps));
    }


    //marker设置点击事件

    //region 注释代码

    /**
     * 暂时不需要，以后可能需要应用呢导航
     *
     * @param startP
     * @param stopP
     * @param navType
     */
/*    private void initMarkerClickEvent()
    {
        // 对Marker的点击
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(final Marker marker)
            {
                // 获得marker中的数据
                double[] position= (double[]) marker.getExtraInfo().get("location");
                String target= (String) marker.getExtraInfo().get("target");

                if (!(target!=null&&target.equals("target")))
                    return true;

                InfoWindow mInfoWindow;
                //自定义气泡形状
                View view = LayoutInflater.from(mContext).inflate(R.layout.map_loc_infowindow, null);
                TextView resName = (TextView) view.findViewById(R.id.res_name);
                TextView resLocation = (TextView) view.findViewById(R.id.res_location);
                Button btnNavigate= (Button) view.findViewById(R.id.btn_navigate);
                btnNavigate.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        localNavigation();
                        //remoteNavigation(new LatLng(mStartLatitude,mStartLongitude),new LatLng(mStopLatitude,mStartLongitude),1);
                        //ToastUtils.showShort(mContext,"导航");
                    }
                });

                // 将marker所在的经纬度的信息转化成屏幕上的坐标
//                final LatLng ll = marker.getPosition();
//                Point p = mBaiduMap.getProjection().toScreenLocation(ll);
//                p.y -= 47;
//                LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
                // 为弹出的InfoWindow添加点击事件

                mInfoWindow = new InfoWindow(view, new LatLng(position[0],position[1]),-120);
                // 显示InfoWindow
                mBaiduMap.showInfoWindow(mInfoWindow);

                //计算p1、p2两点之间的直线距离，单位：米
                //double distance= DistanceUtil.getDistance(new LatLng(mStartLatitude,mStartLongitude), new LatLng(mStopLatitude,mStopLongitude));
                //ToastUtils.showShort(mContext,"距离："+distance);
                return true;
            }
        });
    }*/
    //endregion

    //0:步行；1：驾车；2：公交；3：骑行
    private void remoteNavigation(LatLng startP, LatLng stopP, int navType)
    {
        switch (navType)
        {
            case 0:
                NaviParaOption para0 = new NaviParaOption()
                        .startPoint(startP)
                        .endPoint(stopP);
                try
                {
                    // 调起百度地图步行导航
                    BaiduMapNavigation.openBaiduMapWalkNavi(para0, mContext);
                } catch (BaiduMapAppNotSupportNaviException e)
                {
                    e.printStackTrace();
                }
                break;
            case 1:
                // 构建 route搜索参数以及策略，起终点也可以用name构造
                RouteParaOption para1 = new RouteParaOption()
                        .startPoint(startP)
                        .endPoint(stopP);
                try
                {

                    BaiduMapRoutePlan.openBaiduMapDrivingRoute(para1, mContext);
                } catch (Exception e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d("百度地图吊起失败》" + e.getMessage());
                }
                break;
            case 2:
                // 构建 route搜索参数以及策略，起终点也可以用name构造
                RouteParaOption para2 = new RouteParaOption()
                        .startPoint(startP)
                        .endPoint(stopP)
                        .busStrategyType(RouteParaOption.EBusStrategyType.bus_recommend_way);
                try
                {
                    BaiduMapRoutePlan.openBaiduMapTransitRoute(para2, mContext);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
            case 3:
                // 构建 导航参数
                NaviParaOption para3 = new NaviParaOption()
                        .startPoint(startP)
                        .endPoint(stopP);
                //.startName("天安门").endName("百度大厦");
                try
                {
                    // 调起百度地图骑行导航
                    BaiduMapNavigation.openBaiduMapBikeNavi(para3, mContext);
                } catch (BaiduMapAppNotSupportNaviException e)
                {
                    e.printStackTrace();
                    //showDialog();
                }
                break;
            default:
                break;
        }
        //结束调启功能时调用finish方法以释放相关资源
        BaiduMapRoutePlan.finish(mContext);
    }

    /*    private void localNavigation()
        {
            Intent intent=new Intent(ResAddressMapAct.this,RoutePlanDemo.class);
            ResAddressMapAct.this.startActivity(intent);
        }*/
    private void planWalkRoute(LatLng startP, LatLng stopP)
    {
        PlanNode stNode = PlanNode.withLocation(startP);
        PlanNode edNode = PlanNode.withLocation(stopP);
        //规划一条路线
        mSearch.walkingSearch(new WalkingRoutePlanOption().from(stNode).to(edNode));
    }

    /**
     * 地图移动到我的位置,此处可以重新发定位请求，然后定位； 直接拿最近一次经纬度，如果长时间没有定位成功，可能会显示效果不好
     */
    private void center2myLoc()
    {
        LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(u);
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
                // 构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(mXDirection)
                        .latitude(location.getLatitude())
                        .longitude(location
                                .getLongitude()).build();
                mCurrentAccracy = location.getRadius();
                mBaiduMap.setMyLocationData(locData);// 设置定位数据
                mCurrentLantitude = location.getLatitude();
                mCurrentLongitude = location.getLongitude();
                BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory// 设置自定义图标
                        .fromResource(R.drawable.daohang);
                MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
                mBaiduMap.setMyLocationConfigeration(config);
                if (isFirstLocation)// 第一次定位时，将地图位置移动到当前位置
                {
                    Logger.t(TAG).d("第一次获取经纬度》：" + location.getLatitude() + location.getLongitude());
                    isFirstLocation = false;
                    mStartLatitude = location.getLatitude();
                    mStartLongitude = location.getLongitude();
                    //LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
                    //MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                    //mBaiduMap.animateMapStatus(u);
                    //规划路线
                    //planWalkRoute(ll,new LatLng(mStopLatitude,mStopLongitude));
                }


/*                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                *//**
             * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
             * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
             *//*
                sb.append(location.getTime());
                sb.append("\nerror code : ");
                sb.append(location.getLocType());
                sb.append("\nlatitude : ");
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());
                sb.append("\nradius : ");
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");
                sb.append(location.getCityCode());
                sb.append("\ncity : ");
                sb.append(location.getCity());
                sb.append("\nDistrict : ");
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");
                sb.append(location.getStreet());
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\nDescribe: ");
                sb.append(location.getLocationDescribe());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());
                sb.append("\nPoi: ");
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 单位：米
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    sb.append("\noperationers : ");
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }*/
                //logMsg(sb.toString());
            }
        }
    }

    ;

    //路线规划回调
    private class MyRountPlanResultListener implements OnGetRoutePlanResultListener
    {

        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult result)
        {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR)
            {
                ToastUtils.showShort("抱歉，未找到结果");
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR)
            {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR)
            {
                //nodeIndex = -1;
                //mBtnPre.setVisibility(View.VISIBLE);
                //mBtnNext.setVisibility(View.VISIBLE);
                route = result.getRouteLines().get(0);
                WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                routeOverlay = overlay;
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult result)
        {

        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult)
        {

        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult result)
        {

        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult)
        {

        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult result)
        {

        }
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay
    {
        public MyWalkingRouteOverlay(BaiduMap baiduMap)
        {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker()
        {
            if (useDefaultIcon)
            {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker()
        {
            if (useDefaultIcon)
            {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }
}

package com.echoesnet.eatandmeet.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.request.transition.Transition;
import com.echoesnet.eatandmeet.utils.GlideApp;

import com.bumptech.glide.request.target.SimpleTarget;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.activities.CResStatusShowAct;
import com.echoesnet.eatandmeet.activities.RoomSearchAct;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.CAccostBean;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.presenters.ImpCSayHelloFrView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICSayHelloFrView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.views.adapters.CSayHelloUserAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.NoTouchRelativeLayout;
import com.echoesnet.eatandmeet.views.widgets.RadarViews.RadarScanView;
import com.echoesnet.eatandmeet.views.widgets.RadarViews.RandomTextView;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;


/**
 * 搭讪页面
 */
public class CSayHelloFr extends MVPBaseFragment<ICSayHelloFrView, ImpCSayHelloFrView> implements View.OnClickListener, ICSayHelloFrView
{
    public final static String TAG = CSayHelloFr.class.getSimpleName();

    private MapView mBdMapView;
    //地图对象
    private BaiduMap mBaiduMap;
    private Dialog pDialog;

    /**
     * 定位的客户端
     */
    private LocationClient mLocClient;

    /***
     * 是否是第一次定位
     */
    private volatile boolean isFirstLocation = true;

    /**
     * 最新一次的经纬度
     */
    private double mCurrentLantitude;
    private double mCurrentLongitude;
    private RandomTextView randomTextView;
    //最外层container
    private NoTouchRelativeLayout noTouchRelativeLayout;
    //private RelativeLayout rlRadarLayer;
    private RadarScanView radarScanView;
    private LevelHeaderView rivMyHead;
    private Activity mActivity;
    public boolean isHidden = false;

    /**
     * 当前定位的模式
     */
    private MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
    private List<Marker> showMarkers = new ArrayList<>();
    private Marker myLocationIndicator;

    public CSayHelloFr()
    {
        // Required empty public constructor
    }

    public static CSayHelloFr newInstance(String param1, String param2)
    {
        CSayHelloFr fragment = new CSayHelloFr();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        noTouchRelativeLayout = (NoTouchRelativeLayout) inflater.inflate(R.layout.frg_csay_hello, null);
        initAfterViews(noTouchRelativeLayout);
        Logger.t(TAG).d("onCreateView" + "执行了");
        return noTouchRelativeLayout;
    }

    private void initAfterViews(NoTouchRelativeLayout view)
    {
        mActivity = this.getActivity();
        //mBdMapView= (MapView) view.findViewById(R.id.bd_mapView);
        pDialog = DialogUtil.getCommonDialog(getActivity(), "正在处理...");
        pDialog.setCancelable(false);

        BaiduMapOptions options = new BaiduMapOptions();
        options.compassEnabled(false); // 不允许指南针
        options.zoomControlsEnabled(false); // 不显示缩放按钮
        options.scaleControlEnabled(false); // 不显示比例尺
        mBdMapView = new MapView(mActivity, options);
        view.addView(mBdMapView, 0);

        initRadarView(view);

        IconTextView itvSearchRes = (IconTextView) view.findViewById(R.id.itv_search_res);
        IconTextView itvToMyLocation = (IconTextView) view.findViewById(R.id.itv_to_mylocation);
        itvSearchRes.setOnClickListener(this);
        itvToMyLocation.setOnClickListener(this);

        rivMyHead = (LevelHeaderView) view.findViewById(R.id.riv_sayhello_myhead);
        rivMyHead.setLiveState(false);
        rivMyHead.setHeadImageByUrl(SharePreUtils.getHeadImg(mActivity));
        rivMyHead.setLevel(SharePreUtils.getLevel(mActivity)+"");
        initMap();
        initLocation();
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
        initMarkerClickEvent();
    }

    /**
     * 初始化雷达效果
     *
     * @param view
     */
    private void initRadarView(NoTouchRelativeLayout view)
    {
        randomTextView = (RandomTextView) view.findViewById(R.id.random_textview);
        //rlRadarLayer = (RelativeLayout) view.findViewById(R.id.rl_radar_layer);
        radarScanView = (RadarScanView) view.findViewById(R.id.rsv_view);
    }

    /**
     * marker设置点击事件
     */
    private void initMarkerClickEvent()
    {
        // 对Marker的点击
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(final Marker marker)
            {
                // 获得marker中的数据
                //double[] position= (double[]) marker.getExtraInfo().get("location");
                String target = (String) marker.getExtraInfo().get("type");
                if (!(target != null && target.equals("custom")))
                    return true;

                Logger.t(TAG).d("id==" + marker.getExtraInfo().getString("id") + "type" + marker.getExtraInfo().get("type") + "resId==" + marker.getExtraInfo().getString("resId"));
                //判断点击的头像，来选择进入的路径
                //未点餐的用户
                if (TextUtils.isEmpty(marker.getExtraInfo().getString("resId")))
                {
                    if ("1".equals(marker.getExtraInfo().getString("living")))
                    {
                        //进入直播间
                        EamApplication.getInstance().livePage.put(marker.getExtraInfo().getString("id"), marker.getExtraInfo().getString("headImgUrl"));
                        CommonUtils.startLiveProxyAct(mActivity, LiveRecord.ROOM_MODE_MEMBER, "", "", marker.getExtraInfo().getString("headImgUrl"),
                                marker.getExtraInfo().getString("id"), null, EamCode4Result.reqNullCode);
                    }
                    else
                    {
                        //如果是未点餐的用户则进入用户详情
                        Intent intent = new Intent(mActivity, CNewUserInfoAct.class);
                        intent.putExtra("checkWay","UId");
                        intent.putExtra("toUId", marker.getExtraInfo().getString("userId"));
                        startActivity(intent);

                    }
                }
                else//点了餐的用户
                {
                    //进入餐厅
                    Intent intent = new Intent(mActivity, CResStatusShowAct.class);
                    intent.putExtra("resId", (String) marker.getExtraInfo().get("resId"));
                    intent.putExtra("resName", (String) marker.getExtraInfo().get("resName"));
                    intent.putExtra("floorNum", (String) marker.getExtraInfo().get("floorNum"));
                    startActivity(intent);
                }
                return true;
            }
        });
    }



    /**
     * 初始化定位
     */
    private void initLocation()
    {
        //初始化定位客户端
        mLocClient = new LocationClient(mActivity.getApplicationContext());
        //绑定定位监听
        mLocClient.registerLocationListener(new MyLocationListener());
        //配置定位SDK参数
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(20 * 1000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
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





    @Override
    public void onStart()
    {
        Logger.t(TAG).d("onStart()" + "执行了");
        // 开启图层定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocClient.isStarted())
        {
            mLocClient.start();
        }
        super.onStart();
    }

    @Override
    public void onStop()
    {
        Logger.t(TAG).d("onStop()" + "执行了");
        super.onStop();
        // 关闭图层定位
        mBaiduMap.setMyLocationEnabled(false);
        mLocClient.stop();
    }

    @Override
    public void onResume()
    {
        Logger.t(TAG).d("onResume()" + "执行了");
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mBdMapView.onResume();
    }

    @Override
    public void onPause()
    {
        Logger.t(TAG).d("onPause()" + "执行了");
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mBdMapView.onPause();
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mBdMapView.onDestroy();
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    protected ImpCSayHelloFrView createPresenter()
    {
        return new ImpCSayHelloFrView();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.itv_search_res:
                Intent intent = new Intent(getActivity(), RoomSearchAct.class);
                // 跳转控制传参 zdw 修改
                intent.putExtra("searchSource", "sayHello");
                startActivity(intent);
                break;
            case R.id.itv_to_mylocation:
                center2myLoc(new LatLng(mCurrentLantitude, mCurrentLongitude));
                scanAroundPerson();
                break;
        }
    }


    /**
     * 地图上设置marker
     *
     * @param accostPs
     */
    private void setMarkers(List<CAccostBean> accostPs)
    {
        Logger.t(TAG).d("Marker count>" + accostPs.size());
        for (Marker marker : showMarkers)
        {
            marker.remove();
        }
/*        LatLngBounds.Builder llBounds=new LatLngBounds.Builder();
        LatLng northeast=new LatLng(134.066174,53.289768);
        LatLng southwest=new LatLng(76.550046,21.405012);
        llBounds.include()
        mBaiduMap.getMarkersInBounds(llBounds.build());*/
        for (final CAccostBean abean : accostPs)
        {
//            Logger.t(TAG).d(abean.toString());
            View view = LayoutInflater.from(mActivity).inflate(R.layout.c_marker_content, null);
            final RoundedImageView rv = (RoundedImageView) view.findViewById(R.id.riv_c_accost_head);
            TextView orderNumber = (TextView) view.findViewById(R.id.tv_orderNum);
            TextView tvLivePlay = (TextView) view.findViewById(R.id.tv_live_play);
            TextView tvOrderRes = (TextView) view.findViewById(R.id.tv_order_res);
            orderNumber.setText(abean.getOrderNum());
            if (abean.getUserBean() == null)
                continue;
            //此人在直播，或者是餐厅里有直播的人
            if ("1".equals(abean.getUserBean().getStatus()))
            {
               /* 优先显示直播，隐藏订单人数*/
                tvLivePlay.setVisibility(View.VISIBLE);
                orderNumber.setVisibility(View.GONE);
                //如果是餐厅中有直播的人，则显示直播图标和刀叉图标
                if (!TextUtils.isEmpty(abean.getrId()))
                {
                    tvOrderRes.setVisibility(View.VISIBLE);
                }
            }
            double posX, posY;
            //如果不是点餐的人，则从本身去拿坐标
            if (TextUtils.isEmpty(abean.getrId()))
            {
                orderNumber.setVisibility(View.GONE);
                posX = Double.parseDouble(abean.getUserBean().getPosx());
                posY = Double.parseDouble(abean.getUserBean().getPosy());
            }
            else
            {
                posX = Double.parseDouble(abean.getPosx());
                posY = Double.parseDouble(abean.getPosy());
            }
            // 位置
            //final LatLng latLng = new LatLng(abean.getResLongitud(), abean.getResLatitude());
            final LatLng latLng = new LatLng(posX, posY);
            final View tmpView = view;
            final String headImgUrl = abean.getUserBean().getUphUrl();

//            rv.setLiveState(false);
//            rv.setHeadImageByUrl(headImgUrl);
//            rv.setLevel(abean.getUserBean().getLevel());

            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(headImgUrl)
                    .centerCrop()
                    .placeholder(R.drawable.userhead)
                    .error(R.drawable.userhead)
                    .into(new SimpleTarget<Bitmap>()
                    {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation)
                        {
                            rv.setImageBitmap(resource);
                            BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(CommonUtils.getViewBitmap(tmpView));
                            //图标
                            MarkerOptions overlayOptions =
                                    new MarkerOptions().position(latLng)
                                            .icon(markerIcon).zIndex(6);
                            Marker marker = (Marker) mBaiduMap.addOverlay(overlayOptions);
                            Bundle bundle = new Bundle();
                            //bundle.putDoubleArray("location",new double[]{lat,lng});
                            bundle.putString("resId", abean.getrId());
                            bundle.putString("resName", abean.getResName());
                            bundle.putString("floorNum", abean.getFloorNum());
                            bundle.putString("userId", abean.getUserBean().getuId());
                            bundle.putString("living", abean.getUserBean().getStatus());//这个后台比较混乱，living和status都是标示是否是直播
                            bundle.putString("id", abean.getUserBean().getId());//看脸吃饭id，也是roomid
                            bundle.putString("type", "custom");
                            bundle.putString("headImgUrl", headImgUrl);
                            marker.setExtraInfo(bundle);
                            showMarkers.add(marker);
                        }
                    });
        }
        /*
        // 将地图移到到最后一个经纬度位置
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
        //mBaiduMap.animateMapStatus(u);
        mBaiduMap.setMapStatus(u);*/
    }

    /**
     * 地图移动到我的位置,此处可以重新发定位请求，然后定位； 直接拿最近一次经纬度，如果长时间没有定位成功，可能会显示效果不好
     */
    private void center2myLoc(LatLng ll)
    {
        if (myLocationIndicator != null)
            myLocationIndicator.remove();
        // 设置自定义图标
        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.daohang);
        OverlayOptions overlayOptions = new MarkerOptions().position(ll)
                .icon(mCurrentMarker).zIndex(5);
        myLocationIndicator = (Marker) mBaiduMap.addOverlay(overlayOptions);
        Bundle bundle = new Bundle();
        bundle.putString("type", "MyLocationIndicator");
        myLocationIndicator.setExtraInfo(bundle);
        //LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(u);
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {

    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
    }

    @Override
    public void getAroundPersonCallback(List<CAccostBean> response, List<HashMap<String, Object>> resInfo)
    {
        try
        {
            //将餐厅位置加到里面
            for (int i = 0; i < response.size(); i++)
            {
                for (HashMap map : resInfo)
                {
                    if (response.get(i).getrId().equals(map.get("resId")))
                    {
                        response.get(i).setResLatitude((Double) map.get("lantitude"));
                        response.get(i).setResLongitud((Double) map.get("longitude"));
                        break;
                    }
                }
            }
            setMarkers(response);
        } catch (Exception e)
        {
            Logger.t(TAG).d(e.getMessage());
        } finally
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    @Override
    public void getNewAccostToPersonCallback(List<CAccostBean> userLst, boolean isShowDialog)
    {
        if (isShowDialog)
        {
            try
            {
                for (CAccostBean ab : userLst)
                {
                    String showText = ab.getUserBean().getNicName();
                    randomTextView.addKeyWord(showText.length() > 3 ? showText.substring(0, 3) : showText);
                }
                randomTextView.setVisibility(View.VISIBLE);
                randomTextView.show();

                final List<CAccostBean> tempUserLst = userLst;
                //在开始展示搜索动画时候就设置marker，防止网络较慢的情况下，用户连续点击搜索后，积攒大量marker一起加载出来
                setMarkers(tempUserLst);
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                       // rlRadarLayer.setVisibility(View.GONE);
                        radarScanView.setVisibility(View.GONE);
                        randomTextView.setVisibility(View.GONE);
                        rivMyHead.setVisibility(View.GONE);
                        noTouchRelativeLayout.setTouchAble(true);
                        //setMarkers(tempUserLst);
                        Logger.t(TAG).d("isHidden:" + isHidden);
                        if (!isHidden)
                            showAccostPersonDialog(tempUserLst);
                    }
                }, 2 * 1000);
            } catch (Exception e)
            {
                e.printStackTrace();
                Logger.t(TAG).d(e.getMessage());
            } finally
            {
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }
        }
        else
        {
            setMarkers(userLst);
        }

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
                mCurrentLantitude = location.getLatitude();
                mCurrentLongitude = location.getLongitude();
//                Logger.t(TAG).d("位置："+mCurrentLantitude+" "+mCurrentLongitude);
                // 第一次定位时，将地图位置移动到当前位置
                if (isFirstLocation)
                {
                    //Logger.t(TAG).d("是否移动焦点："+String.valueOf(isFirstLocation));
                    isFirstLocation = false;
                    LatLng currentLoc = new LatLng(mCurrentLantitude, mCurrentLongitude);
                    center2myLoc(currentLoc);
                    if (mPresenter != null)
                        mPresenter.getNewAccostToPerson(String.valueOf(mCurrentLantitude), String.valueOf(mCurrentLongitude), false);
                }
            }
        }
    }

    /**
     * 更新地图显示
     */
    public void refreshUi()
    {
        center2myLoc(new LatLng(mCurrentLantitude, mCurrentLongitude));
        if (mPresenter != null)
            mPresenter.getNewAccostToPerson(String.valueOf(mCurrentLantitude), String.valueOf(mCurrentLongitude), false);
    }


    /**
     * 扫描周围的人
     */
    private void scanAroundPerson()
    {
       // rlRadarLayer.setVisibility(View.VISIBLE);
        radarScanView.setVisibility(View.VISIBLE);
        rivMyHead.setVisibility(View.VISIBLE);
        randomTextView.setVisibility(View.VISIBLE);
        noTouchRelativeLayout.setTouchAble(false);
        if (mPresenter != null)
            mPresenter.getNewAccostToPerson(String.valueOf(mCurrentLantitude), String.valueOf(mCurrentLongitude), true);
    }

    private void showAccostPersonDialog(final List<CAccostBean> userLst)
    {
        final Dialog dialog = new Dialog(mActivity, R.style.AlertDialogStyle);
        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.act_csayhello_user, null);
        dialog.setContentView(contentView);

        GridView gvSayUser = (GridView) contentView.findViewById(R.id.gv_say_user);
        IconTextView itvClose = (IconTextView) contentView.findViewById(R.id.iv_close_icon);
        CSayHelloUserAdapter adapter = new CSayHelloUserAdapter(mActivity, userLst);
        gvSayUser.setAdapter(adapter);
        gvSayUser.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                CAccostBean bean = userLst.get(position);
                //未点餐
                if (TextUtils.isEmpty(bean.getrId()))
                {
                    if ("1".equals(bean.getUserBean().getStatus()))
                    {
                        //进入直播间
                        EamApplication.getInstance().livePage.put(bean.getUserBean().getId(), bean.getUserBean().getUphUrl());
                        CommonUtils.startLiveProxyAct(mActivity, LiveRecord.ROOM_MODE_MEMBER, "", "", bean.getUserBean().getUphUrl(),
                                bean.getUserBean().getId(), null, EamCode4Result.reqNullCode);
                    }
                    else
                    {
                        //进入用户详情
                        Intent intent = new Intent(mActivity,CNewUserInfoAct.class);
                        intent.putExtra("checkWay","UId");
                        intent.putExtra("toUId", bean.getUserBean().getuId());
                        startActivity(intent);
                    }
                }
                else
                {
                    //进入餐厅
                    Intent intent = new Intent(mActivity,CResStatusShowAct.class);
                    intent.putExtra("resId", bean.getrId());
                    intent.putExtra("resName", bean.getResName());
                    intent.putExtra("floorNum", bean.getFloorNum());
                    startActivity(intent);
                }
            }
        });
        itvClose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        if (getActivity() != null && !getActivity().isFinishing())
            dialog.show();
    }

    interface CheckAroundFinishCallback
    {
        void success(List<CAccostBean> userLst);

        void failed();
    }
}

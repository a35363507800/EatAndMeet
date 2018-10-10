package com.echoesnet.eatandmeet.utils.LocationUtils;

import android.content.Context;
import android.support.annotation.Nullable;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.echoesnet.eatandmeet.controllers.okhttpCallback.BaseCallback;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.Map;

import okhttp3.Call;


/**
 * Created by wangben on 2016/10/19.
 * 用于处理关于地理位置的功能
 */

public class LocationUtils
{
    private final static String TAG= LocationUtils.class.getSimpleName();
    private static LocationUtils locationUtils;
    private  LocationClient mLocClient;

    private LocationUtils(){}
    public static synchronized  LocationUtils getInstance()
    {
        if (locationUtils==null)
        {
            locationUtils=new LocationUtils();
        }
        return locationUtils;
    }

    //初始化定位
    private  void initLocationClient(Context context,int scanSpan, BDLocationListener locationListener)
    {
        mLocClient = new LocationClient(context);
        //绑定定位监听
        mLocClient.registerLocationListener(locationListener);
        //配置定位SDK参数
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(scanSpan);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
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

    public  LocationClient getLocationClient(Context context,int scanSpan, BDLocationListener locationListener)
    {
        if (mLocClient==null)
        {
            initLocationClient(context,scanSpan,locationListener);
        }
        else
        {
            mLocClient.getLocOption().setScanSpan(scanSpan);
            mLocClient.registerLocationListener(locationListener);
        }
        return mLocClient;
    }
    /**
     * 向后台上传某一用户的位置信息
     * @param context
     * @param targetUid 用户id
     * @param posX 纬度
     * @param posY 经度
     */
    public void postLocationInfoToServer(final Context context, String targetUid, String posX, String posY)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(context);
        reqParamMap.put(ConstCodeTable.posx, posX);
        reqParamMap.put(ConstCodeTable.posy, posY);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {}, NetInterfaceConstant.IndexC_indexLive, reqParamMap);

    }

    /**
     * 我的约会向后台传递经纬度
     * @param context
     * @param posX 纬度
     * @param posY 经度
     */
    public void postMealMeetLocationToServer(@Nullable Context context, String streamId, String posX, String posY, SilenceSubscriber2<ResponseResult> subscriber)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(context);
        reqParamMap.put(ConstCodeTable.streamId, streamId);
        reqParamMap.put(ConstCodeTable.posx, posX);
        reqParamMap.put(ConstCodeTable.posy, posY);
        if (subscriber==null)
            subscriber=new SilenceSubscriber2<ResponseResult>()
            {
                @Override
                public void onHandledError(ApiException apiE)
                {
                    super.onHandledError(apiE);
                }

                @Override
                public void onHandledNetError(Throwable throwable)
                {
                    super.onHandledNetError(throwable);
                }

                @Override
                public void onNext(ResponseResult response)
                {
                    super.onNext(response);
                }
            };
        HttpMethods.getInstance().startServerRequest(subscriber,NetInterfaceConstant.ReceiveC_sendLocation,null,reqParamMap);
    }
}

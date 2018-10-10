package com.echoesnet.eatandmeet.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.HomeAct;
import com.echoesnet.eatandmeet.models.bean.AreaBean;
import com.echoesnet.eatandmeet.models.bean.RestaurantBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.LocationUtils.LocationUtils;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.ReservationPopupWindow;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2018/2/5
 * @description
 */
public class MealAndClubFrg extends BaseFragment
{
    private final static String TAG = MealAndClubFrg.class.getSimpleName();
    //region 变量
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.vp_meal)
    ViewPager vpMeal;


    private Unbinder unbinder;
    private String pageIndex;
    private HomeAct homeAct;
    private List<Fragment> fragments = new ArrayList<>();//里面包含的两个fragment
    private FragmentActivity mContext;
    private int currentPosition;
    private ClubFrg clubfrg;
    private int position;
    private OrderMealFrg orderMealFrg;
    private volatile boolean isFirstRefresh = true;

    private LocationClient mLocClient;
    private double mCurrentLatitude; //y
    private double mCurrentLongitude;//x
    private String openSource;
    private String openFrom;
    private List<String> filterList = new ArrayList<>();
    private List<Map<String, TextView>> navBtns;
    private List<String> areaList;                   // 区域
    private List<AreaBean> areaBeanList;             // 区域对应的商圈
    private List<String> sortList;                   // 分类
    private List<RestaurantBean> dataList;           // 餐厅列表数据源
    private String areaParamResult = "", businessParamResult = "", classParamResult = "", filterParamResult = "";    // 所选条件后的字段(做为查询餐厅列表的参数)
    private String areaParamResultClub = "", businessParamResultClub = "",
            classParamResultClub = "", filterParamResultClub = ""; // 所选条件后的字段(做为查询沙龙列表的参数)
    private ReservationPopupWindow reservationPopupWindow;


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            pageIndex = getArguments().getString("pageIndex");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frg_meal_and_club, container, false);
        mContext = getActivity();
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (!mLocClient.isStarted())
        {
            mLocClient.start();
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mLocClient.stop();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        initLocation();
        initTitle();
        initViewPager();
        getRegionDataOnCdn();
        getSortDataOnCdn();
        filterList.add("离我最近");
        filterList.add("评价最好");
        filterList.add("价格最低");
        filterList.add("价格最高");
    }
    //初始化定位
    private void initLocation()
    {
        mLocClient = new LocationClient(getActivity().getApplicationContext());
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


    /**
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
                mCurrentLatitude = location.getLatitude();
                mCurrentLongitude = location.getLongitude();

                if (isFirstRefresh)
                {
                    isFirstRefresh = false;

                    if (Double.compare(mCurrentLatitude, 4.9E-324) == 0)
                    {
                        EamLogger.t(TAG).writeToDefaultFile("定位失败》location Latitude为： " + mCurrentLatitude);
                    }
                    Logger.t(TAG).d("定位成功》" + "lan>" + decimalFormat.format(mCurrentLatitude) + "lon>" + decimalFormat.format(mCurrentLongitude));
                    //定位成功后，将位置上传
                    LocationUtils.getInstance().postLocationInfoToServer(mContext, SharePreUtils.getUId(mContext),
                            decimalFormat.format(mCurrentLatitude), decimalFormat.format(mCurrentLongitude));

                }
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (clubfrg!=null)
            clubfrg.onActivityResult(requestCode,resultCode,data);
        if (orderMealFrg!=null)
            orderMealFrg.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }


    public MealAndClubFrg()
    {
        // Required empty public constructor
    }


    public static MealAndClubFrg newInstance()
    {
        MealAndClubFrg fragment = new MealAndClubFrg();

        return fragment;
    }


    private void initTitle()
    {
        topBarSwitch.inflateSwitchBtns(Arrays.asList("美食", "玩家"), 0,
                new TopbarSwitchSkeleton()
                {
                    @Override
                    public void leftClick(View view)
                    {
                    }

                    @Override
                    public void right2Click(View view)
                    {
                        // TODO: 2018/2/6 lc
                        if (currentPosition == 0)
                        {
                            //去搜索餐厅美食
                            searchRes();
                        }
                        else
                        {
                            //去搜索ktv唱歌
                            searchClub();
                        }

                    }

                    @Override
                    public void switchBtn(View view, int position)
                    {
                        vpMeal.setCurrentItem(position);
                        currentPosition = position;
                        onChangePage(position);
                    }

                    @Override
                    public void refreshPage(int position)
                    {
                    }

                    @Override
                    public void topDoubleClick(View view)
                    {
                        super.topDoubleClick(view);
                    }
                });
        navBtns = topBarSwitch.getNavBtns2(new int[]{0, 0, 0, 1});
        Map<String, TextView> map = navBtns.get(0);
        TextView icon = map.get(TopBarSwitch.NAV_BTN_ICON);
        icon.setText("{eam-e619}");

        areaList = new ArrayList<>();
        areaBeanList = new ArrayList<>();
        sortList = new ArrayList<>();
        dataList = new ArrayList<>();
    }

    private void searchRes()
    {
        reservationPopupWindow = new ReservationPopupWindow(getActivity(),
                areaList, areaParamResult, areaBeanList, businessParamResult,
                sortList, classParamResult, filterList, filterParamResult, position);
        Logger.t(TAG).d("初始化搜选结果--> " + areaParamResult + " , " + businessParamResult + " , " + classParamResult + " , " + filterParamResult);

        int emptyHeight = CommonUtils.getScreenHeight1(mContext)-CommonUtils.dp2px(mContext,480);
        reservationPopupWindow.setEmptyHeight(emptyHeight);
        reservationPopupWindow.showPopupWindow(topBarSwitch);


        reservationPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                if (homeAct != null)
                    homeAct.setViewAlpha(false);
                if (orderMealFrg!=null)
                    orderMealFrg.setResListAlpha(1.0f);
            }
        });

        if (reservationPopupWindow.isShowing())
        {
            if (homeAct != null)
                homeAct.setViewAlpha(true);
            if (orderMealFrg!=null)
                orderMealFrg.setResListAlpha(0.3f);
        }

        reservationPopupWindow.setButtonClickListener(new ReservationPopupWindow.ButtonClickListener()
        {
            @Override
            public void queryCallback(HashMap<String, Object> map)
            {
                Logger.t(TAG).d("搜选结果--> " + map.get("areaParam") + " , " + map.get("businessParam") +
                        " , " + map.get("classParam") + " , " + map.get("filterParam") + " , " + map.get("position"));
                areaParamResult = map.get("areaParam").toString();
                businessParamResult = map.get("businessParam").toString();
                classParamResult = map.get("classParam").toString();
                filterParamResult = map.get("filterParam").toString();
                position = Integer.parseInt(map.get("position").toString());
                if (orderMealFrg!=null)
                    orderMealFrg.clearResList();

                if (orderMealFrg!=null)
                    orderMealFrg.loadData(businessParamResult, classParamResult, filterParamResult, areaParamResult, 0, 0);
            }
        });
    }
    private void searchClub()
    {
        reservationPopupWindow = new ReservationPopupWindow(getActivity(),
                areaList, areaParamResultClub, areaBeanList, businessParamResultClub,
                sortList, classParamResultClub, filterList, filterParamResultClub, position);
        Logger.t(TAG).d("初始化搜选结果--> 商圈:" + areaParamResultClub + " , " + businessParamResultClub + " , " + classParamResultClub + " , " + filterParamResultClub);

        int emptyHeight = CommonUtils.getScreenHeight1(mContext) -CommonUtils.dp2px(mContext,480) ;
        reservationPopupWindow.setGoodFoodHide();
        reservationPopupWindow.setEmptyHeight(emptyHeight);
        reservationPopupWindow.showPopupWindow(topBarSwitch);
        reservationPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                if (homeAct != null)
                    homeAct.setViewAlpha(false);
                if (clubfrg!=null)
                    clubfrg.setResListAlpha(1.0f);
            }
        });

        if (reservationPopupWindow.isShowing())
        {
            if (homeAct != null)
                homeAct.setViewAlpha(true);
            if (clubfrg!=null)
                clubfrg.setResListAlpha(0.3f);
        }

        reservationPopupWindow.setButtonClickListener(new ReservationPopupWindow.ButtonClickListener()
        {
            @Override
            public void queryCallback(HashMap<String, Object> map)
            {
                Logger.t(TAG).d("搜选结果--> " + map.get("areaParam") + " , " + map.get("businessParam") +
                        " , " + map.get("classParam") + " , " + map.get("filterParam") + " , " + map.get("position"));
                areaParamResultClub = map.get("areaParam").toString();
                businessParamResultClub = map.get("businessParam").toString();
                classParamResultClub = map.get("classParam").toString();
                filterParamResultClub = map.get("filterParam").toString();
                position = Integer.parseInt(map.get("position").toString());
                if (clubfrg!=null)
                    clubfrg.clearResList();

                if (clubfrg!=null)
                    clubfrg.loadData(businessParamResultClub,  areaParamResultClub, filterParamResultClub, mCurrentLongitude, mCurrentLatitude);
            }
        });
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        Logger.t(TAG).d("MealAndClubFrg:" + isVisibleToUser);
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }

    public void  showNewbieGuide()
    {
        if (orderMealFrg != null && vpMeal.getCurrentItem() == 0)
        {
            orderMealFrg.showNewbieGuide();
        }
    }

    private void initViewPager()
    {

        orderMealFrg = OrderMealFrg.newInstance();
        clubfrg = ClubFrg.newInstance(mCurrentLatitude,mCurrentLongitude);
        fragments.clear();
        fragments.add(orderMealFrg);
        fragments.add(clubfrg);


        vpMeal.setOffscreenPageLimit(2);
        vpMeal.setAdapter(new FragmentPagerAdapter(getChildFragmentManager())
        {
            @Override
            public Fragment getItem(int position)
            {
                return fragments.get(position);
            }

            @Override
            public int getCount()
            {
                return fragments.size();
            }
        });
        vpMeal.addOnPageChangeListener(mPagerChangeListener);

    }

    private ViewPager.OnPageChangeListener mPagerChangeListener = new ViewPager
            .OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
        }

        @Override
        public void onPageSelected(int position)
        {
            topBarSwitch.changeSwitchBtn(position);


            onChangePage(position);
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
        }
    };

    private void onChangePage(int position)
    {
        currentPosition = position;
    }


    public  OrderMealFrg getMealFrag()
    {

        if (orderMealFrg == null)
        {
            return OrderMealFrg.newInstance();
        }

        return orderMealFrg;
    }


    /**
     * 下载cdn上面的区域文件
     */

    private void getRegionDataOnCdn()
    {
        try
        {
            //没有网络的时候使用本地缓存
            if (NetHelper.getNetworkStatus(getActivity()) == -1)
            {
                File file = new File(NetHelper.getRootDirPath(getActivity()) + NetHelper.DATA_FOLDER + "area.json");
                if (file.exists() && !file.isDirectory())
                {
                    getRegionFromFile(file);
                }
            }
            else
            {
                OkHttpUtils.get()
                        .url(CdnHelper.CDN_ORIGINAL_SITE + CdnHelper.fileFolder + "area.json")
                        .build()
                        .execute(new FileCallBack(NetHelper.getRootDirPath(getActivity()) + NetHelper.DATA_FOLDER, "area.json")
                        {
                            @Override
                            public void onError(Call call, Exception e)
                            {
                                NetHelper.handleNetError(getActivity(), null, TAG, e);
                            }

                            @Override
                            public void onResponse(File response)
                            {
                                getRegionFromFile(response);
                            }

                            @Override
                            public void inProgress(float progress, long total)
                            {
                            }
                        });
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("下载cdn上面的区域文件异常--> " + e.getMessage());
        }
    }

    /**
     * 下载cdn上面的分类文件
     */
    private void getSortDataOnCdn()
    {
        try
        {
            //没有网络的时候使用本地缓存
            if (NetHelper.getNetworkStatus(getActivity()) == -1)
            {
                File file = new File(NetHelper.getRootDirPath(getActivity()) + NetHelper.DATA_FOLDER + "sort.json");
                if (file.exists() && !file.isDirectory())
                {
                    getSortFromFile(file);
                }
            }
            else
            {
                OkHttpUtils.get()
                        .url(CdnHelper.CDN_ORIGINAL_SITE + CdnHelper.fileFolder + "sort.json")
                        .build()
                        .execute(new FileCallBack(NetHelper.getRootDirPath(getActivity()) + NetHelper.DATA_FOLDER, "sort.json")
                        {
                            @Override
                            public void onError(Call call, Exception e)
                            {
                                Logger.t(TAG).d("" + e.getMessage());
                            }

                            @Override
                            public void onResponse(File response)
                            {
                                getSortFromFile(response);
                            }

                            @Override
                            public void inProgress(float progress, long total)
                            {
                            }
                        });
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("下载分类文件异常--> " + e.getMessage());
        }
    }
    /**
     * 解析区域文件
     *
     * @param jsonFile 获取cdn文件
     */
    private void getRegionFromFile(File jsonFile)
    {
        String areaResult = CommonUtils.getJsonFromFile(jsonFile);
        try
        {
            JSONObject jsonObject = new JSONObject(areaResult);
            JSONArray jsonArrayArea = jsonObject.getJSONArray("area"); // 最外层area
            for (int i = 0; i < jsonArrayArea.length(); i++)
            {
                JSONObject jsonObject1 = (JSONObject) jsonArrayArea.get(i);
                JSONArray jsonArrayCity = jsonObject1.getJSONArray("city"); // 第一层city
                for (int j = 0; j < jsonArrayCity.length(); j++)
                {
                    JSONObject jsonObject2 = (JSONObject) jsonArrayCity.get(j);
                    JSONArray jsonArrayPart = jsonObject2.getJSONArray("part"); // 第二层city
                    for (int k = 0; k < jsonArrayPart.length(); k++)
                    {
                        JSONObject jsonObject3 = (JSONObject) jsonArrayPart.get(k);
                        AreaBean bean = new AreaBean();
                        String part = jsonObject3.getString("part");
                        bean.setPart(part);
                        JSONArray jsonArrayTrade = jsonObject3.getJSONArray("trade");
                        String[] trades = new String[jsonArrayTrade.length()];
                        for (int m = 0; m < jsonArrayTrade.length(); m++)
                        {
                            trades[m] = jsonArrayTrade.get(m).toString();
                            bean.setTrades(trades);
                        }
                        areaList.add(part);
                        areaBeanList.add(bean);
                    }
                }
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("解析区域文件异常--> " + e.getMessage());
        }
    }

    /**
     * 解析分类文件
     *
     * @param jsonFile 获取cdn文件
     */
    private void getSortFromFile(File jsonFile)
    {
        String sortResult = CommonUtils.getJsonFromFile(jsonFile);
        try
        {
            JSONObject jsonObject = new JSONObject(sortResult);
            JSONArray jsonArraySort = jsonObject.getJSONArray("sort"); // 最外层sort
            for (int i = 0; i < jsonArraySort.length(); i++)
            {
                JSONObject jsonObject1 = (JSONObject) jsonArraySort.get(i);
                String sort = jsonObject1.getString("sort");
                sortList.add(sort);
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("解析分类文件异常--> " + e.getMessage());
        }
    }

}

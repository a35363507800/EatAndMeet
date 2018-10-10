package com.echoesnet.eatandmeet.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.DOrderMealDetailAct;
import com.echoesnet.eatandmeet.activities.HomeAct;
import com.echoesnet.eatandmeet.activities.RoomSearchAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.AreaBean;
import com.echoesnet.eatandmeet.models.bean.ResListBannerBean;
import com.echoesnet.eatandmeet.models.bean.RestaurantBean;
import com.echoesnet.eatandmeet.presenters.ImpOrderMealView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IOrderMealView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.EmptyView;
import com.echoesnet.eatandmeet.utils.LoadFootView;
import com.echoesnet.eatandmeet.utils.LoadingProgressUtil;
import com.echoesnet.eatandmeet.utils.LocationUtils.LocationUtils;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.ReservationPopupWindow;
import com.echoesnet.eatandmeet.views.adapters.ResLstInfoAdapter;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @modifier zdw
 * @createDate
 * @description 餐厅列表
 */


public class OrderMealFrg extends BaseFragment implements IOrderMealView, ResLstInfoAdapter.MealsItemClick
{
    private static final String TAG = OrderMealFrg.class.getSimpleName();
    // 控件定义
    @BindView(R.id.listview_order_meal_frg)
    PullToRefreshListView resListView;
    Unbinder unbinder;
    //    @BindView(R.id.top_bar_switch)
//    TopBarSwitch topBarSwitch;
    @BindView(R.id.ll_search_restaurant)
    LinearLayout llSearchRestaurant;
    @BindView(R.id.ll_bg)
    LinearLayout llBg;
    @BindView(R.id.loading_view)
    RelativeLayout loadingView;//加载布局

    // 变量定义
    private Activity mAct;
    private ImpOrderMealView impOrderMealView;
    private volatile boolean isFirstRefresh = true;
    private List<String> filterList = new ArrayList<>();
    private static final String DEFAULT_NUM = "20";  // 每页显示数量
    private static final String FIRST_LOAD_NUM = "10";  // 首次加载显示数量
    private ListView actualListView;                 // 餐厅列表
    private ResLstInfoAdapter adapter;               // 餐厅列表适配器
    private List<String> areaList;                   // 区域
    private List<AreaBean> areaBeanList;             // 区域对应的商圈
    private List<String> sortList;                   // 分类
    private List<RestaurantBean> dataList;           // 餐厅列表数据源
    private List<ResListBannerBean> resListBannerList;         // banner
    private View footView;                           // 没有更多内容时添加的底部提示布局
    private boolean pullMove = true;                 // 没有更多数据获取时,禁止列表上拉加载动作
    private String bootyCallDate;                    //约吃饭日期
    private String areaParamResult = "", businessParamResult = "", classParamResult = "", filterParamResult = "";    // 所选条件后的字段(做为查询餐厅列表的参数)
    private int position;
    private String openSource;
    private HomeAct homeAct;
    private String openFrom;
    private GetOpenSourceListener listener;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frg_ordermeal, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EamConstant.EAM_OPEN_TRENDS_PLAY_VIDEO)
        {
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    if (adapter != null)
                        adapter.startLoop();
                }
            }, 3000);
        }
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
    public  void  setResListAlpha(float alpha)
    {
        if (resListView!=null)
            resListView.setAlpha(alpha);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (adapter != null)
            adapter.stopPlayVideo(-1);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mLocClient.stop();
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (adapter != null)
            adapter.destroy();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    public OrderMealFrg()
    {
    }

    public static OrderMealFrg newInstance()
    {
        return new OrderMealFrg();
    }

    private void initViews()
    {
        mAct = getActivity();
        filterList.add("离我最近");
        filterList.add("评价最好");
        filterList.add("价格最低");
        filterList.add("价格最高");

        try
        {
            homeAct = (HomeAct) getActivity();
        } catch (ClassCastException e)
        {
            e.printStackTrace();
        }

        Bundle bundle = getArguments();
        if (bundle != null)
        {
            bootyCallDate = bundle.getString("bootyCallDate");
            openSource = bundle.getString("openSource");
            openFrom = bundle.getString("openFrom");
        }
        List<TextView> navBtn = null;
//        if (TextUtils.isEmpty(openSource))
//        {
//            navBtn = topBarSwitch.getNavBtns(new int[]{0, 0, 0, 1});
//            navBtn.get(0).setText("{eam-e619}");
//        }
//        else if ("chat".equals(openSource))
//        {
//            navBtn = topBarSwitch.getNavBtns(new int[]{1, 0, 0, 1});
//            navBtn.get(1).setText("{eam-e619}");
//        }
//
//        if (TextUtils.equals("dateEnter", openFrom))
//        {
//            navBtn = topBarSwitch.getNavBtns(new int[]{1, 0, 0, 1});
//            navBtn.get(1).setText("{eam-e619}");
//        }

        Intent intent = new Intent(EamConstant.EAM_REFRESH_FRG_TOP_BAR);
        intent.putExtra("openSource", openSource);
        intent.putExtra("openFrom", openFrom);
        getActivity().sendBroadcast(intent);



        Logger.t(TAG).d(">>>>>>" + bootyCallDate);
        footView = LayoutInflater.from(mAct).inflate(R.layout.footview_list, null);
        impOrderMealView = new ImpOrderMealView(mAct, this);
        resListView.setMode(PullToRefreshBase.Mode.BOTH);

        resListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (isFirstRefresh == true && getActivity() != null && getActivity() instanceof HomeAct)
                {
                    ((HomeAct) getActivity()).requestLocationPerm();
                }
                if (SharePreUtils.getToOrderMeal(mAct).equals("toOrderMeal"))
                {
                    impOrderMealView.getResListForAppo(DEFAULT_NUM, String.valueOf(dataList.size()), "add", businessParamResult, classParamResult, filterParamResult, areaParamResult, mCurrentLatitude, mCurrentLongitude);
                }
                else
                {
                    if (impOrderMealView != null)
                    {
                        impOrderMealView.getResListBanner();
                    }
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (impOrderMealView != null)
                {
                    if (SharePreUtils.getToOrderMeal(mAct).equals("toOrderMeal"))
                    {
                        impOrderMealView.getResListForAppo(DEFAULT_NUM, String.valueOf(dataList.size()), "add", businessParamResult, classParamResult, filterParamResult, areaParamResult, mCurrentLatitude, mCurrentLongitude);
                    }
                    else
                    {
                        impOrderMealView.getRestaurantLstES(DEFAULT_NUM, String.valueOf(dataList.size()), "add", businessParamResult, classParamResult, filterParamResult, areaParamResult, mCurrentLatitude, mCurrentLongitude);
                    }
                }
                LoadFootView.showFootView(actualListView, false, footView, null);
            }
        });
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 0, null);

        actualListView = resListView.getRefreshableView();
        EmptyView emptyView = new EmptyView(mAct);
        emptyView.setContent("暂无相关餐厅, 请再次筛选哦~");  // 设置提示语
        resListView.setEmptyView(emptyView);
        registerForContextMenu(actualListView);
        areaList = new ArrayList<>();
        areaBeanList = new ArrayList<>();
        sortList = new ArrayList<>();
        dataList = new ArrayList<>();
        resListBannerList = new ArrayList<>();
        adapter = new ResLstInfoAdapter(getActivity(), dataList, resListBannerList);
        adapter.setMealsItemClick(this);
        actualListView.setAdapter(adapter);
        initLocation();
        if (SharePreUtils.getToOrderMeal(mAct).equals("toOrderMeal"))
        {
            impOrderMealView.getResListForAppo(FIRST_LOAD_NUM, String.valueOf(dataList.size()), "add", businessParamResult, classParamResult, filterParamResult, areaParamResult, mCurrentLatitude, mCurrentLongitude);
        }
        else
        {
            if (impOrderMealView != null)
            {
                impOrderMealView.getResListBanner();
            }
        }
    }


    /**
     * @Description: 显示新手引导
     */
    public void showNewbieGuide()
    {
        Logger.t(TAG).d("显示新手引导");
        if (SharePreUtils.getIsNewBieDing(mAct))
        {
            NetHelper.checkIsShowNewbie(mAct, "8", new ICommonOperateListener()
            {
                @Override
                public void onSuccess(String response)
                {
                    if ("0".equals(response))
                    {
                        //获取root节点
                        final FrameLayout fRoot = (FrameLayout) getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
                        final View vGuide = View.inflate(mAct, R.layout.view_newbie_guide_order_meal, null);
                        final View tvClickDismiss = vGuide.findViewById(R.id.tv_click_dismiss);
                        RelativeLayout rlOrder = (RelativeLayout) vGuide.findViewById(R.id.rl_order2);

                        int marginTop;
                        if (resListBannerList != null && resListBannerList.size() > 0)
                        {
                            marginTop = CommonUtils.dp2px(mAct, 304);
                        }
                        else
                        {
                            marginTop = CommonUtils.dp2px(mAct, 200);
                        }
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rlOrder.getLayoutParams();
                        layoutParams.topMargin = marginTop;
                        rlOrder.setLayoutParams(layoutParams);
                        vGuide.setClickable(true);

                        tvClickDismiss.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                fRoot.removeView(vGuide);
                                SharePreUtils.setIsNewBieDing(mAct, false);
                                NetHelper.saveShowNewbieStatus(mAct, "8");
                            }
                        });
                        fRoot.addView(vGuide);
                    }
                    else
                    {
                        SharePreUtils.setIsNewBieDing(mAct, false);
                    }
                }

                @Override
                public void onError(String code, String msg)
                {

                }
            });


        }
    }

    @OnClick({R.id.ll_search_restaurant})
    void viewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.ll_search_restaurant:
                Intent intent = new Intent(mAct, RoomSearchAct.class);
                intent.putExtra("geotable_id", CommonUtils.BAIDU_GEOTABLE_ID);
                intent.putExtra("resType", "resType");
                getActivity().startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        Logger.t(TAG).d("frgVisible>>" + isVisibleToUser);
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }

    /**
     * 获取当前经纬度
     */
    private LocationClient mLocClient;
    private double mCurrentLatitude;
    private double mCurrentLongitude;

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

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }
    public void setOnGetOpenSourceListener(GetOpenSourceListener listener)
    {
        this.listener = listener;
    }
    public interface GetOpenSourceListener
    {
        void dataInfo(String openSource,String openFrom);
    }
    public void  clearResList()
    {
        if (dataList!=null)
            dataList.clear();
    }

    @Override
    public void contentClick(RestaurantBean itemBean)
    {
        Logger.t(TAG).d("测试约会订单类型--> " + EamApplication.getInstance().dateStreamId);
        /**经纬度现在比较乱，需要系统测试, 获取接口posx(经度)和posy(纬度)**/
        EamApplication.getInstance().geoPosition = new String[]{itemBean.getPosy(), itemBean.getPosx()};
        EamApplication.getInstance().lessPrice = itemBean.getLessPrice();
        Intent intent = new Intent(mAct, DOrderMealDetailAct.class);
        intent.putExtra("restId", itemBean.getRid());
        intent.putExtra("bootyCallDate", bootyCallDate);
        SharePreUtils.setRestId(getActivity(), itemBean.getRid());
        Logger.t(TAG).d("restId--> " + itemBean.getRid() + " , resName--> " + itemBean.getrName() + " , 起订价--> " + itemBean.getLessPrice());
        getActivity().startActivity(intent);
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
                    Logger.t(TAG).d("餐厅首次定位成功");
                    if (impOrderMealView != null)
                    {
                        if (SharePreUtils.getToOrderMeal(mAct).equals("toOrderMeal"))
                        {
                            impOrderMealView.getResListForAppo(DEFAULT_NUM, "0", "add", businessParamResult, classParamResult, filterParamResult, areaParamResult, mCurrentLatitude, mCurrentLongitude);
                        }
                        else
                        {
                            impOrderMealView.getResListBanner();
//                            impOrderMealView.getRestaurantLstES(DEFAULT_NUM, "0", "add", businessParamResult, classParamResult, filterParamResult, areaParamResult, mCurrentLatitude, mCurrentLongitude);
                        }
                    }

                    LoadFootView.showFootView(actualListView, false, footView, null);
                    if (Double.compare(mCurrentLatitude, 4.9E-324) == 0)
                    {
                        EamLogger.t(TAG).writeToDefaultFile("定位失败》location Latitude为： " + mCurrentLatitude);
                        //ToastUtils.showLong("请允许小饭使用定位权限来帮您推荐合适的餐厅");
                    }
                    Logger.t(TAG).d("定位成功》" + "lan>" + decimalFormat.format(mCurrentLatitude) + "lon>" + decimalFormat.format(mCurrentLongitude));
                    //定位成功后，将位置上传
                    LocationUtils.getInstance().postLocationInfoToServer(mAct, SharePreUtils.getUId(mAct),
                            decimalFormat.format(mCurrentLatitude), decimalFormat.format(mCurrentLongitude));
                }
            }
        }
    }


    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        if (resListView != null)
            resListView.onRefreshComplete();
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(getActivity(), null, exceptSource, e);
        if (resListView != null)
            resListView.onRefreshComplete();
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
    }

    @Override
    public void getRestaurantLstESCallback(List<RestaurantBean> response, String operateType)
    {
        Logger.t(TAG).d("餐厅信息"+response.toString());
        adapter.stopPlayVideo(-1);
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
        if (response == null)
        {
            ToastUtils.showShort("获取餐厅信息失败");
        }
        else
        {
            Logger.t(TAG).d("新餐厅数量--> " + response.size());
            if (response.size() == 0)
            {
                LoadFootView.showFootView(actualListView, true, footView, null);
                pullMove = false;
            }

            // 下拉刷新
            if (operateType.equals("refresh"))
            {
//                dataList.clear();
                LoadFootView.showFootView(actualListView, false, footView, null);
                pullMove = true;
            }
            // 添加去重复 ====
            for (RestaurantBean restaurantBean : response)
            {
                if (dataList.contains(restaurantBean))
                {
                    int index = dataList.indexOf(restaurantBean);
                    dataList.remove(index);
                }
                dataList.add(restaurantBean);
            }
            // 离我最近筛选
            if (("1").equals(filterParamResult))
            {
                Collections.sort(dataList, new Comparator<Object>()
                {
                    @Override
                    public int compare(Object lhs, Object rhs)
                    {
                        if (lhs instanceof RestaurantBean && rhs instanceof RestaurantBean)
                        {
                            double x = Double.parseDouble(((RestaurantBean) lhs).getDistance());
                            double y = Double.parseDouble(((RestaurantBean) rhs).getDistance());
                            return (int) (x - y);
                        }
                        else
                        {
                            return 0;
                        }
                    }
                });
            }
            else if ("2".equals(filterParamResult))
            {
                Collections.sort(dataList, new Comparator<Object>()
                {

                    @Override
                    public int compare(Object lhs, Object rhs)
                    {
                        if (lhs instanceof RestaurantBean && rhs instanceof RestaurantBean)
                        {
                            RestaurantBean lhsRes = (RestaurantBean) lhs;
                            RestaurantBean rhsRes = (RestaurantBean) rhs;
                            int l = 0;
                            int r = 0;
                            try
                            {
                                l = Integer.parseInt(lhsRes.getrStar());
                                r = Integer.parseInt(rhsRes.getrStar());

                                if (l == r)   // 星级相同 按距离最近排序
                                {
                                    double x = Double.parseDouble(lhsRes.getDistance());
                                    double y = Double.parseDouble(rhsRes.getDistance());
                                    return (int) (x - y);
                                }

                            } catch (NumberFormatException e)
                            {
                                e.printStackTrace();
                                Logger.t(TAG).e("评价数格式错误，找后端>" + lhsRes.getrStar() + rhsRes.getrStar());
                            }
                            return r - l;
                        }
                        else
                        {
                            return 0;
                        }
                    }
                });
            }
            else if ("3".equals(filterParamResult))
            {
                Collections.sort(dataList, new Comparator<Object>()
                {
                    @Override
                    public int compare(Object lhs, Object rhs)
                    {
                        if (lhs instanceof RestaurantBean && rhs instanceof RestaurantBean)
                        {
                            RestaurantBean lhsRes = (RestaurantBean) lhs;
                            RestaurantBean rhsRes = (RestaurantBean) rhs;
                            double x = Double.parseDouble(lhsRes.getPerPrice());
                            double y = Double.parseDouble(rhsRes.getPerPrice());
                            // 人均价格(价格最低)相同 按距离最近排序
                            if (x == y)
                            {
                                double m = Double.parseDouble(lhsRes.getDistance());
                                double n = Double.parseDouble(rhsRes.getDistance());
                                return (int) (m - n);
                            }
                            return (int) (x - y);
                        }
                        else
                        {
                            return 0;
                        }
                    }
                });
            }
            else if ("4".equals(filterParamResult))
            {
                Collections.sort(dataList, new Comparator<Object>()
                {
                    @Override
                    public int compare(Object lhs, Object rhs)
                    {
                        if (lhs instanceof RestaurantBean && rhs instanceof RestaurantBean)
                        {
                            RestaurantBean lhsRes = (RestaurantBean) lhs;
                            RestaurantBean rhsRes = (RestaurantBean) rhs;
                            double x = Double.parseDouble(lhsRes.getPerPrice());
                            double y = Double.parseDouble(rhsRes.getPerPrice());
                            // 人均价格(价格最高)相同 按距离最近排序
                            if (x == y)
                            {
                                double m = Double.parseDouble(lhsRes.getDistance());
                                double n = Double.parseDouble(rhsRes.getDistance());
                                return (int) (m - n);
                            }
                            return (int) (y - x);
                        }
                        else
                        {
                            return 0;
                        }
                    }
                });
            }
            adapter.notifyDataSetChanged();

            if (dataList.size() == 0)
            {
                Logger.t(TAG).d("没有数据,显示空数据默认图");
//                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) emptyView.getLayoutParams();
//                layoutParams.topMargin = resListBannerList.size() > 0?CommonUtils.dp2px(mAct,200):0;
//                emptyView.setLayoutParams(layoutParams);
//                emptyView.setVisibility(View.VISIBLE);
                pullMove = true;
            }
//            else
//            {
//                emptyView.setVisibility(View.GONE);
//            }
        }

        if (resListView != null)
        {
            resListView.onRefreshComplete();
            if (!operateType.equals("refresh"))
            {
                if (dataList.size() == Integer.parseInt(DEFAULT_NUM))
                {
                    resListView.getRefreshableView().smoothScrollToPosition(0);
                }
            }
            if (pullMove)
            {
                Logger.t(TAG).d("允许上下拉动"); // 恢复上拉 上下拉动
                resListView.setMode(PullToRefreshBase.Mode.BOTH);
            }
            else
            {
                Logger.t(TAG).d("禁止上拉"); // 禁止上拉
                resListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
        }
    }

    @Override
    public void getResListBannerCallback(List<ResListBannerBean> response)
    {
        dataList.clear();
        if (response != null)
        {
            resListBannerList.clear();
            resListBannerList.addAll(response);
        }
        if (impOrderMealView != null)
        {
            if (SharePreUtils.getToOrderMeal(mAct).equals("toOrderMeal"))
            {
                impOrderMealView.getResListForAppo(DEFAULT_NUM, "0", "refresh", businessParamResult, classParamResult, filterParamResult, areaParamResult, mCurrentLatitude, mCurrentLongitude);
            }
            else
            {
                impOrderMealView.getRestaurantLstES(DEFAULT_NUM, "0", "refresh", businessParamResult, classParamResult, filterParamResult, areaParamResult, mCurrentLatitude, mCurrentLongitude);
            }
        }
    }

    /**
     * 根据相关参数请求,获取相应数据
     *
     * @param businessParam     商圈
     * @param classParam        分类
     * @param filterParam       筛选
     * @param areaParam         区域
     * @param msCurrentLatitude  纬度
     * @param msCurrentLongitude 经度
     */
    public void loadData(String businessParam, String classParam, String filterParam, String areaParam, double msCurrentLatitude, double msCurrentLongitude)
    {
        businessParamResult = businessParam;
        classParamResult = classParam;
        filterParamResult = filterParam;
        areaParamResult = areaParam;
        if (impOrderMealView != null)
        {
            pullMove = true;  // 恢复上下拉动
            if (SharePreUtils.getToOrderMeal(mAct).equals("toOrderMeal"))
            {
                impOrderMealView.getResListForAppo(DEFAULT_NUM, "0", "add", businessParam, classParam, filterParam, areaParam, mCurrentLatitude, mCurrentLongitude);
            }
            else
            {
                impOrderMealView.getRestaurantLstES(DEFAULT_NUM, "0", "add", businessParam, classParam, filterParam, areaParam, mCurrentLatitude, mCurrentLongitude);
            }
        }
        LoadFootView.showFootView(actualListView, false, footView, null);
    }

    private void setWindowAlpha(float alpha)
    {
        WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
        layoutParams.alpha = alpha;
        getActivity().getWindow().setAttributes(layoutParams);
    }

    public  void  stopVideo()
    {
        if (adapter != null)
            adapter.stopPlayVideo(-1);
    }

}
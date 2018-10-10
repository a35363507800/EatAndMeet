package com.echoesnet.eatandmeet.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.HomeAct;
import com.echoesnet.eatandmeet.activities.RoomSearchAct;
import com.echoesnet.eatandmeet.models.bean.AreaBean;
import com.echoesnet.eatandmeet.models.bean.ClubListBean;
import com.echoesnet.eatandmeet.models.bean.RestaurantBean;
import com.echoesnet.eatandmeet.presenters.ImpIClubPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IClubView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EmptyView;
import com.echoesnet.eatandmeet.utils.LoadFootView;
import com.echoesnet.eatandmeet.utils.LoadingProgressUtil;
import com.echoesnet.eatandmeet.utils.LocationUtils.LocationUtils;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.ClubAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;



/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @modifier
 * @createDate 2018/2/5
 * @description
 */
public class ClubFrg extends MVPBaseFragment<ClubFrg, ImpIClubPre> implements IClubView
{
    private static final String TAG = ClubFrg.class.getSimpleName();

    @Override
    protected ImpIClubPre createPresenter()
    {
        return new ImpIClubPre();
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }

    // 控件定义
    @BindView(R.id.listview_club_frg)
    PullToRefreshListView resListView;
    Unbinder unbinder;
    @BindView(R.id.ll_search_restaurant)
    LinearLayout llSearchRestaurant;
    @BindView(R.id.ll_bg)
    LinearLayout llBg;
    @BindView(R.id.loading_view)
    RelativeLayout loadingView;//加载布局

    // 变量定义
    private Activity mAct;
    private List<TextView> navBtn = null;
    private volatile boolean isFirstRefresh = true;
    private double mCurrentLatitude;//y
    private double mCurrentLongitude;//x
    private List<String> filterList = new ArrayList<>();
    private static final String DEFAULT_NUM = "20";  // 每页显示数量
    private static final String FIRST_LOAD_NUM = "10";  // 首次加载显示数量
    private ListView actualListView;                 // 餐厅列表
    private ClubAdapter adapter;                   // 餐厅列表适配器
    private List<String> areaList;                   // 区域
    private List<AreaBean> areaBeanList;             // 区域对应的商圈
    private List<String> sortList;                   // 分类
    private List<ClubListBean> dataList;             // 餐厅Ktv列表数据源
    private View footView;                           // 没有更多内容时添加的底部提示布局
    private boolean pullMove = true;                 // 没有更多数据获取时,禁止列表上拉加载动作
    private String areaParamResult = "", businessParamResult = "", filterParamResult = "";    // 所选条件后的字段(做为查询餐厅列表的参数)
    private HomeAct homeAct;

    private LocationClient mLocClient;


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
        View view = inflater.inflate(R.layout.frg_club_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
//        if (getArguments() != null)
//        {
//            mCurrentLatitude = getArguments().getDouble("mCurrentLatitude");
//            mCurrentLongitude = getArguments().getDouble("mCurrentLongitude");
//        }

        initLocation();
        initViews();
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
    public void onDestroy()
    {
        super.onDestroy();
        if (adapter != null)
            adapter.destroy();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mLocClient.stop();
    }

    public ClubFrg()
    {
    }

    public static ClubFrg newInstance(Double mCurrentLatitude, Double mCurrentLongitude)
    {
        ClubFrg fragment = new ClubFrg();
        Bundle args = new Bundle();
        args.putDouble("mCurrentLatitude", mCurrentLatitude);
        args.putDouble("mCurrentLongitude", mCurrentLongitude);
        fragment.setArguments(args);
        return fragment;

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

        footView = LayoutInflater.from(mAct).inflate(R.layout.footview_list, null);
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
                if (mPresenter != null)
                    mPresenter.getClubList(DEFAULT_NUM, "0", businessParamResult,
                            areaParamResult,filterParamResult,  mCurrentLongitude,mCurrentLatitude, "refresh");
                LoadFootView.showFootView(actualListView, false, footView, null);

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (mPresenter != null)
                    mPresenter.getClubList(DEFAULT_NUM, String.valueOf(dataList.size()), businessParamResult,
                            areaParamResult,filterParamResult,   mCurrentLongitude, mCurrentLatitude,"add");
                LoadFootView.showFootView(actualListView, false, footView, null);
            }
        });
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 0, null);

        actualListView = resListView.getRefreshableView();
        EmptyView emptyView = new EmptyView(mAct);
        emptyView.setContent("暂无相关沙龙, 请再次筛选哦~");  // 设置提示语
        resListView.setEmptyView(emptyView);
        registerForContextMenu(actualListView);
        areaList = new ArrayList<>();
        areaBeanList = new ArrayList<>();
        sortList = new ArrayList<>();
        dataList = new ArrayList<>();
        adapter = new ClubAdapter(getActivity(), dataList);
        actualListView.setAdapter(adapter);

//        if (mPresenter != null)
//            mPresenter.getClubList(DEFAULT_NUM, String.valueOf(dataList.size()), businessParamResult,
//                    areaParamResult, filterParamResult,  mCurrentLatitude, mCurrentLongitude, "add");
//        Logger.t(TAG).d("dataList:"+dataList.size()+",businessParamResult="+businessParamResult+",areaParamResult="+areaParamResult+",filterParamResult:"
//        +filterParamResult+",mCurrentLatitude="+mCurrentLatitude+",mCurrentLongitude"+mCurrentLongitude);


    }



    /**
     * @Description: 显示新手引导
     */
    private void showNewbieGuide()
    {
    }

    @OnClick({R.id.ll_search_restaurant})
    void viewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.ll_search_restaurant:
                Intent intent = new Intent(mAct, RoomSearchAct.class);
                intent.putExtra("geotable_id", CommonUtils.BAIDU_GEOTABLE_ID);
                intent.putExtra("resType", "clubType");
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
        if (isVisibleToUser)
        {
            showNewbieGuide();
        }

    }


    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        if (resListView != null)
            resListView.onRefreshComplete();
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {

        if (resListView != null)
            resListView.onRefreshComplete();
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
    }

    @Override
    public void getClubListCallback(List<ClubListBean> response, String operateType)
    {
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
        if (response == null)
        {
            ToastUtils.showShort("获取餐厅信息失败");
        } else
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
                LoadFootView.showFootView(actualListView, false, footView, null);
                pullMove = true;
                dataList.clear();
            }
            // 添加去重复 ====
            for (ClubListBean restaurantBean : response)
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
                        } else
                        {
                            return 0;
                        }
                    }
                });
            } else if ("2".equals(filterParamResult))
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
                        } else
                        {
                            return 0;
                        }
                    }
                });
            } else if ("3".equals(filterParamResult))
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
                        } else
                        {
                            return 0;
                        }
                    }
                });
            } else if ("4".equals(filterParamResult))
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
                        } else
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
                pullMove = true;
            }
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
            } else
            {
                Logger.t(TAG).d("禁止上拉"); // 禁止上拉
                resListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
        }
    }

    public void  clearResList()
    {
        if (dataList!=null)
            dataList.clear();
    }

    /**
     * 根据相关参数请求,获取相应数据
     *
     * @param businessParam     商圈
     * @param areaParam         区域
     * @param filterParam       筛选
     * @param mCurrentLongitude 经度
     * @param mCurrentLatitude  纬度
     *
     */
    public void loadData(String businessParam,String areaParam, String filterParam, double mCurrentLongitude, double mCurrentLatitude)
    {
        businessParamResult = businessParam;
        areaParamResult = areaParam;
        filterParamResult = filterParam;
        if (mPresenter != null)
        {
            pullMove = true;  // 恢复上下拉动
            mPresenter.getClubList(DEFAULT_NUM, "0", businessParam, areaParam, filterParam,
                    mCurrentLongitude, mCurrentLatitude, "add");

        }
        LoadFootView.showFootView(actualListView, false, footView, null);
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
                mCurrentLatitude = location.getLatitude();//纬度
                mCurrentLongitude  = location.getLongitude();//经度

                if (isFirstRefresh)
                {
                    if (mPresenter != null)
                    {
                        mPresenter.getClubList(DEFAULT_NUM, String.valueOf(dataList.size()), businessParamResult,
                                areaParamResult, filterParamResult,  mCurrentLongitude, mCurrentLatitude, "add");
                        Logger.t(TAG).d(" dataList:"+dataList.size()+", businessParamResult="+businessParamResult+", areaParamResult="+areaParamResult+", filterParamResult="
                                +filterParamResult+", mCurrentLatitude="+mCurrentLatitude+", mCurrentLongitude="+mCurrentLongitude);
                    }
                    isFirstRefresh = false;

                    if (Double.compare(mCurrentLatitude, 4.9E-324) == 0)
                    {
                        EamLogger.t(TAG).writeToDefaultFile("定位失败》location Latitude为：" + mCurrentLatitude);
                    }
                    Logger.t(TAG).d("定位成功》" + "lan>" + decimalFormat.format(mCurrentLatitude) + "lon>" + decimalFormat.format(mCurrentLongitude));
                    //定位成功后，将位置上传
                    LocationUtils.getInstance().postLocationInfoToServer(mAct, SharePreUtils.getUId(mAct),
                            decimalFormat.format(mCurrentLatitude), decimalFormat.format(mCurrentLongitude));
                }
            }
        }
    }

}

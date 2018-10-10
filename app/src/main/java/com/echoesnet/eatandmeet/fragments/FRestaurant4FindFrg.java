package com.echoesnet.eatandmeet.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.DOrderMealDetailAct;
import com.echoesnet.eatandmeet.activities.PromotionActionAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.FPromotionBean;
import com.echoesnet.eatandmeet.models.bean.FRestaurannt4FindBean;
import com.echoesnet.eatandmeet.models.bean.FRestaurantItemBean;
import com.echoesnet.eatandmeet.presenters.ImpIFRestaurant4FindFrgView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IFRestaurant4FindFrgView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.views.adapters.FRecentlyIn4FindAdapter;
import com.echoesnet.eatandmeet.views.adapters.FTodayRec4FindAdapter;
import com.echoesnet.eatandmeet.views.widgets.ImageIndicatorView.NetworkImageIndicatorView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.orhanobut.logger.Logger;
import com.panxw.android.imageindicator.AutoPlayManager;
import com.panxw.android.imageindicator.ImageIndicatorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * 发现页餐厅
 * Created by an on 2017/3/29 0029.
 */
public class FRestaurant4FindFrg extends MVPBaseFragment<FRestaurant4FindFrg, ImpIFRestaurant4FindFrgView> implements IFRestaurant4FindFrgView
{
    private final String TAG = FRestaurant4FindFrg.class.getSimpleName();
    @BindView(R.id.icv_cycle_view_res)
    NetworkImageIndicatorView networkImageIndicatorView;
    @BindView(R.id.recyclerview_today_recommended)
    RecyclerView todayRecommendedRV;
    @BindView(R.id.recyclerview_recently_in)
    RecyclerView recentlyInRV;
    @BindView(R.id.pull_to_refresh_sv_res)
    PullToRefreshScrollView pullToRefreshScrollView;
    Unbinder unbinder;

    private Activity mActivity;
    private String defaultNum = "20";//默认每次请求20
    private String city = "天津";//城市

    private List<FRestaurantItemBean> todayRecommendData;
    private List<FRestaurantItemBean> recentlyInData;
    private FTodayRec4FindAdapter todayRec4FindAdapter;
    private FRecentlyIn4FindAdapter recentlyIn4FindAdapter;
    /**
     * 定位的客户端
     */
    private LocationClient mLocClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frg_restaurant4find, container, false);
        unbinder = ButterKnife.bind(this, view);
        afterViews();
        return view;
    }

    private void afterViews()
    {
        mActivity = getActivity();
        todayRecommendData = new ArrayList<>();
        recentlyInData = new ArrayList<>();
        todayRec4FindAdapter = new FTodayRec4FindAdapter(mActivity, todayRecommendData);
        recentlyIn4FindAdapter = new FRecentlyIn4FindAdapter(mActivity, recentlyInData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        todayRecommendedRV.setLayoutManager(linearLayoutManager);
        todayRecommendedRV.setAdapter(todayRec4FindAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 2)
        {
            @Override
            public boolean canScrollVertically()
            {
                return false;
            }
        };
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gridLayoutManager.setAutoMeasureEnabled(true);
        recentlyInRV.setLayoutManager(gridLayoutManager);
        recentlyInRV.setAdapter(recentlyIn4FindAdapter);
        pullToRefreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView)
            {
                //每次刷新重新定位
                if (mLocClient != null && !mLocClient.isStarted())
                    mLocClient.start();
                mPresenter.getCarouselRes();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView)
            {
            }
        });
        initItemClick();
        initLocation();
        if (mLocClient != null && !mLocClient.isStarted())
            mLocClient.start();
        mPresenter.getCarouselRes();
    }

    /**
     * 初始itemclicklistener
     */
    private void initItemClick()
    {
        todayRec4FindAdapter.setItemClickListener(new FTodayRec4FindAdapter.ItemClickListener()
        {
            @Override
            public void onItemClick(int position, View view, FRestaurantItemBean itemBean)
            {
                List<String> posxy = CommonUtils.strToList(itemBean.getPosxy());
                EamApplication.getInstance().geoPosition = new String[]{posxy.get(0), posxy.get(1)};
                EamApplication.getInstance().lessPrice = itemBean.getLessPrice();
                Intent intent = new Intent(getActivity(), DOrderMealDetailAct.class);
                intent.putExtra("restId", itemBean.getRId());
                SharePreUtils.setRestId(getActivity(), itemBean.getRId());
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
            }
        });
        recentlyIn4FindAdapter.setItemClickListener(new FRecentlyIn4FindAdapter.ItemClickListener()
        {
            @Override
            public void onItemClick(int position, View view, FRestaurantItemBean itemBean)
            {
                List<String> posxy = CommonUtils.strToList(itemBean.getPosxy());
                EamApplication.getInstance().geoPosition = new String[]{posxy.get(0), posxy.get(1)};
                EamApplication.getInstance().lessPrice = itemBean.getLessPrice();
                Intent intent = new Intent(getActivity(), DOrderMealDetailAct.class);
                intent.putExtra("restId", itemBean.getRId());
                SharePreUtils.setRestId(getActivity(), itemBean.getRId());
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
            }
        });
    }


    /**
     * 初始化轮播
     *
     * @param pBeenLst 轮播数据
     */
    private void initCarousel(final ArrayList<FPromotionBean> pBeenLst)
    {
        List<String> urlList = new ArrayList<>();
        for (int i = 0; i < pBeenLst.size(); i++)
        {
            //使用网络加载图片
            urlList.add(pBeenLst.get(i).getImgUrl());
        }
        networkImageIndicatorView.setShowIndicator(false);
        networkImageIndicatorView.setupLayoutByImageUrl(urlList);
        networkImageIndicatorView.show();
        networkImageIndicatorView.setOnItemClickListener(new ImageIndicatorView.OnItemClickListener()
        {
            @Override
            public void OnItemClick(View view, int position)
            {
                if (pBeenLst.size() == 0)
                    return;
                FPromotionBean pBean = pBeenLst.get(position);
                //有活动点击进入
                if (pBean.getIsActivity().equals("1"))
                {
                    Intent intent = new Intent(mActivity, PromotionActionAct.class);
                    intent.putExtra("fpBean", pBean);
                    startActivity(intent);
                }
            }
        });
        AutoPlayManager autoBrocastManager = new AutoPlayManager(networkImageIndicatorView);
        autoBrocastManager.setBroadcastEnable(true);
        autoBrocastManager.setBroadCastTimes(10000);//loop times
        autoBrocastManager.setBroadcastTimeIntevel(3 * 1000, 3 * 1000);//设置第一次展示时间以及间隔，间隔不能小于2秒
        autoBrocastManager.loop();

    }

    /**
     * 初始化定位
     */
    private void initLocation()
    {
        //初始化定位客户端
        mLocClient = new LocationClient(mActivity.getApplicationContext());
        //绑定定位监听
        mLocClient.registerLocationListener(new BDLocationListener()
        {
            @Override
            public void onReceiveLocation(BDLocation bdLocation)
            {
                Logger.t(TAG).d("位置》x:" + bdLocation.getLatitude() + "y:" + bdLocation.getLongitude());
                mPresenter.getIndexRestaurantList("refresh", defaultNum, city, "0", String.valueOf(bdLocation.getLatitude()), String.valueOf(bdLocation.getLongitude()));
                if (mLocClient.isStarted())
                {
                    mLocClient.stop();
                }
            }
        });
        //配置定位SDK参数
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
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
    protected ImpIFRestaurant4FindFrgView createPresenter()
    {
        return new ImpIFRestaurant4FindFrgView();
    }


    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.IndexC_indexRecommend_v322:
                if (pullToRefreshScrollView != null)
                    pullToRefreshScrollView.onRefreshComplete();
                break;
            default:
                break;
        }

    }

    @Override
    public void getIndexRecommendSuccess(String type, FRestaurannt4FindBean restaurannt4FindBean)
    {
        pullToRefreshScrollView.onRefreshComplete();
        if (restaurannt4FindBean == null)
            return;
        todayRecommendData.clear();
        recentlyInData.clear();
        List<FRestaurantItemBean> todayRecommendList = restaurannt4FindBean.getTodayRecommend();
        if (todayRecommendList != null)
            todayRecommendData.addAll(todayRecommendList);
        List<FRestaurantItemBean> recentlyInList = restaurannt4FindBean.getRecommend();
        if (recentlyInList != null)
            recentlyInData.addAll(recentlyInList);
        todayRec4FindAdapter.notifyDataSetChanged();
        recentlyIn4FindAdapter.notifyDataSetChanged();
    }

    @Override
    public void getCarouselResSuccess(ArrayList<FPromotionBean> pBeenLst)
    {
        if (pBeenLst.size() > 0)
        {
            networkImageIndicatorView.setVisibility(View.VISIBLE);
            initCarousel(pBeenLst);
        } else
        {
            networkImageIndicatorView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (mLocClient != null && mLocClient.isStarted())
            mLocClient.stop();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }
}

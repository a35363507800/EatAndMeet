package com.echoesnet.eatandmeet.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.HomeAct;
import com.echoesnet.eatandmeet.activities.PromotionActionAct;
import com.echoesnet.eatandmeet.activities.live.LHotAnchorVideoAct;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.FPromotionBean;
import com.echoesnet.eatandmeet.models.bean.HotAnchorBean;
import com.echoesnet.eatandmeet.models.bean.Liveplay4FindBean;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.presenters.ImpILivePlay4FindFrgView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ILivePlay4FindFrgView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.adapters.FArtAnchor4FindAdapter;
import com.echoesnet.eatandmeet.views.adapters.FBootyCall4FindAdapter;
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
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * 发现页直播
 * Created by an on 2017/3/29 0029.
 */
public class FLivePlay4FindFrg extends MVPBaseFragment<FLivePlay4FindFrg, ImpILivePlay4FindFrgView> implements ILivePlay4FindFrgView
{
    private final String TAG = FLivePlay4FindFrg.class.getSimpleName();
    @BindView(R.id.icv_cycle_view_live)
    NetworkImageIndicatorView networkImageIndicatorView;
    @BindView(R.id.pull_to_refresh_sv_live)
    PullToRefreshScrollView pullToRefreshScrollView;
    @BindView(R.id.tv_booty_call_title)
    TextView bootyCallTitle;
    @BindView(R.id.recyclerview_booty_call)
    RecyclerView bootyCallRecyclerView;
    @BindView(R.id.recyclerview_art_anchor)
    RecyclerView artAnchorRecyclerView;
    @BindView(R.id.recyclerview_nearby_anchor)
    RecyclerView nearbyAnchorRecyclerView;
    Unbinder unbinder;

    private Activity mActivity;
    private String city = "天津";//城市
    private List<HotAnchorBean> mBootyCallData;//约主播数据
    private List<HotAnchorBean> mArtAnchorData;//才艺主播数据
    private List<HotAnchorBean> mNearByAnchorData;//附近主播数据
    private FBootyCall4FindAdapter mBootyCallAdapter;
    private FArtAnchor4FindAdapter mArtAnchorAdapter;
    private FArtAnchor4FindAdapter mNearbyAnchorAdapter;
    /**
     * 定位的客户端
     */
    private LocationClient mLocClient;
    private String currentLatitude, currentLongitude;//当前经纬度

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frg_liveplay4find, container, false);
        unbinder = ButterKnife.bind(this, view);
        afterViews();
        return view;
    }

    @Override
    protected ImpILivePlay4FindFrgView createPresenter()
    {
        return new ImpILivePlay4FindFrgView();
    }

    private void afterViews()
    {
        mActivity = getActivity();
        mBootyCallData = new ArrayList<>();
        mArtAnchorData = new ArrayList<>();
        mNearByAnchorData = new ArrayList<>();
        initLocation();
        mBootyCallAdapter = new FBootyCall4FindAdapter(mActivity, mBootyCallData);
        mArtAnchorAdapter = new FArtAnchor4FindAdapter(mActivity, mArtAnchorData, true);
        mNearbyAnchorAdapter = new FArtAnchor4FindAdapter(mActivity, mNearByAnchorData, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        bootyCallRecyclerView.setLayoutManager(linearLayoutManager);
        bootyCallRecyclerView.setAdapter(mBootyCallAdapter);
        LinearLayoutManager artAnchorLM = new LinearLayoutManager(mActivity);
        artAnchorLM.setOrientation(LinearLayoutManager.HORIZONTAL);
        artAnchorRecyclerView.setLayoutManager(artAnchorLM);
        artAnchorRecyclerView.setAdapter(mArtAnchorAdapter);
        LinearLayoutManager nearbyAnchorLM = new LinearLayoutManager(mActivity);
        nearbyAnchorLM.setOrientation(LinearLayoutManager.HORIZONTAL);
        nearbyAnchorRecyclerView.setLayoutManager(nearbyAnchorLM);
        nearbyAnchorRecyclerView.setAdapter(mNearbyAnchorAdapter);
        pullToRefreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView)
            {
                if (!mLocClient.isStarted())
                {
                    mLocClient.start();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView)
            {
            }
        });
        initItemClick();
        mPresenter.getCarouselLive();
        if (!mLocClient.isStarted())
        {
            mLocClient.start();
        }
    }

    @OnClick({R.id.tv_art_anchor_more, R.id.tv_nearby_anchor_more})
    void clickView(View view)
    {
        switch (view.getId())
        {
            case R.id.tv_art_anchor_more:
                ((HomeAct) mActivity).goToTable(2, "1");
                break;
            case R.id.tv_nearby_anchor_more:
                ((HomeAct) mActivity).goToTable(2, "2");
                break;
        }
    }

    /**
     * 设置adapter itemclick
     */
    private void initItemClick()
    {
        mBootyCallAdapter.setItemClickListener(new FBootyCall4FindAdapter.ItemClickListener()
        {
            @Override
            public void onItemClick(int position, View view, int itemViewType)
            {
                if (itemViewType == FBootyCall4FindAdapter.ITEM_DEFAULT)
                {
                    gotoLivePlay(mBootyCallData.get(position));
                }
                else if (itemViewType == FBootyCall4FindAdapter.ITEM_MORE)
                {
                    ((HomeAct) mActivity).goToTable(2, "0");
                }
            }
        });
        mArtAnchorAdapter.setItemClickListener(new FArtAnchor4FindAdapter.ItemClickListener()
        {
            @Override
            public void onItemClick(int position, View view, int itemViewType)
            {
                if (itemViewType == FArtAnchor4FindAdapter.ITEM_DEFAULT)
                {
                    gotoLivePlay(mArtAnchorData.get(position));
                }
            }
        });

        mNearbyAnchorAdapter.setItemClickListener(new FArtAnchor4FindAdapter.ItemClickListener()
        {
            @Override
            public void onItemClick(int position, View view, int itemViewType)
            {
                if (itemViewType == FArtAnchor4FindAdapter.ITEM_DEFAULT)
                {
                    gotoLivePlay(mNearByAnchorData.get(position));
                }
            }
        });
    }

    private void gotoLivePlay(HotAnchorBean anchorsListBean)
    {
        if (TextUtils.isEmpty(anchorsListBean.getVedio()))
        {
            EamApplication.getInstance().livePage.put(anchorsListBean.getRoomId(), anchorsListBean.getRoomUrl());
            CommonUtils.startLiveProxyAct(mActivity, LiveRecord.ROOM_MODE_MEMBER, "", "", anchorsListBean.getRoomUrl(),
                    anchorsListBean.getRoomId(), null, EamCode4Result.reqNullCode);
        }
        else
        {
            Intent playIntent = new Intent(mActivity, LHotAnchorVideoAct.class);
            playIntent.putExtra("vedio", anchorsListBean.getVedio());
            playIntent.putExtra("coverImgUrl", anchorsListBean.getRoomUrl());
            playIntent.putExtra("luid", anchorsListBean.getuId());
            playIntent.putExtra("roomId", anchorsListBean.getRoomId());
            startActivity(playIntent);
            getActivity().overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
        }
    }


    /**
     * 初始化轮播
     *
     * @param pBeenLst 轮播数据
     */
    private void initCarousel(final ArrayList<FPromotionBean> pBeenLst)
    {
        if (networkImageIndicatorView == null)
            return;
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
                currentLatitude = String.valueOf(bdLocation.getLatitude());
                currentLongitude = String.valueOf(bdLocation.getLongitude());
                mPresenter.getCarouselLive();
                mPresenter.getIndexLiveList("refresh", city, "0", currentLatitude, currentLongitude);
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
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        pullToRefreshScrollView.onRefreshComplete();
    }

    @Override
    public void getIndexLiveSuccess(String type, Liveplay4FindBean liveplay4FindBean)
    {
        pullToRefreshScrollView.onRefreshComplete();
        if (liveplay4FindBean == null)
            return;
        if ("refresh".equals(type))
        {
            mBootyCallData.clear();
            mArtAnchorData.clear();
            mNearByAnchorData.clear();
        }
        mBootyCallData.addAll(liveplay4FindBean.getDateAnchor());
        mArtAnchorData.addAll(liveplay4FindBean.getArtAnchor());
        mNearByAnchorData.addAll(liveplay4FindBean.getNearbyAnchor());
        mBootyCallAdapter.notifyDataSetChanged();
        mArtAnchorAdapter.notifyDataSetChanged();
        mNearbyAnchorAdapter.notifyDataSetChanged();
    }

    @Override
    public void getCarouselLiveSuccess(ArrayList<FPromotionBean> pBeenLst)
    {
        if (pBeenLst.size() > 0)
        {
            if (networkImageIndicatorView != null)
                networkImageIndicatorView.setVisibility(View.VISIBLE);
            initCarousel(pBeenLst);
        }
        else
        {
            if (networkImageIndicatorView != null)
                networkImageIndicatorView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
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

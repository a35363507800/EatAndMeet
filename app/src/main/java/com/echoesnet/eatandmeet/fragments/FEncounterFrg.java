package com.echoesnet.eatandmeet.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.activities.HomeAct;
import com.echoesnet.eatandmeet.activities.PromotionActionAct;
import com.echoesnet.eatandmeet.activities.RedPacketShowAct;
import com.echoesnet.eatandmeet.activities.TaskAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.ActivityWindowBean;
import com.echoesnet.eatandmeet.models.bean.EncounterBean;
import com.echoesnet.eatandmeet.models.bean.FPromotionBean;
import com.echoesnet.eatandmeet.models.bean.FinishTaskBean;
import com.echoesnet.eatandmeet.models.datamodel.ImageDisposalType;
import com.echoesnet.eatandmeet.presenters.ImpIEncounterFrgView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IEncounterFrgView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.IOnCdnFeedbackListener;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.FEncounterFrgAdapter;
import com.echoesnet.eatandmeet.views.widgets.DragLayout;
import com.echoesnet.eatandmeet.views.widgets.FinishTaskDialog;
import com.echoesnet.eatandmeet.views.widgets.ImageIndicatorView.NetworkImageIndicatorView;
import com.echoesnet.eatandmeet.views.widgets.KillDayCardPop;
import com.handmark.pulltorefresh.library.HeaderGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshHeaderGridView;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import com.panxw.android.imageindicator.AutoPlayManager;
import com.panxw.android.imageindicator.ImageIndicatorView;
import com.soundcloud.android.crop.Crop;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.iwf.photopicker.PhotoPicker;

import static android.app.Activity.RESULT_OK;

/**
 * 发现页邂逅
 * Created by an on 2017/3/29 0029.
 */

public class FEncounterFrg extends MVPBaseFragment<FEncounterFrg, ImpIEncounterFrgView> implements IEncounterFrgView
{
    private final String TAG = FEncounterFrg.class.getSimpleName();
    @BindView(R.id.pull_to_refresh_header_gv)
    PullToRefreshHeaderGridView pullToRefreshHeaderGridView;
    NetworkImageIndicatorView networkImageIndicatorView;
    @BindView(R.id.iv_day_seven)
    ImageView ivDaySeven;
    @BindView(R.id.iv_gq)
    ImageView ivGq;
    @BindView(R.id.iv_day_seven_layout)
    DragLayout ivSevenL;
    @BindView(R.id.iv_day_gq_layout)
    DragLayout glGq;

    @BindView(R.id.rl_all_content)
    RelativeLayout rlContainer;


    //定位的客户端
    private LocationClient mLocClient;
    private volatile boolean isFirstLocation = true;//是否是第一次定位
    private FinishTaskDialog finishTaskDialog;
    private FinishTaskDialog achievementDialog;
    /**
     * 最新一次的经纬度
     */
    private double mCurrentLantitude;//经度
    private double mCurrentLongitude;//纬度
    // 当前定位的模式
    // private MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;


    private Activity mActivity;
    private static final int PAGE_SIZE = 21;//默认每次请求21
    private static final String FIRST_LOAD_PAGE_SIZE = "18";//第一次请求21
    private String city = "天津";//城市
    private List<EncounterBean> mEncounterList;
    private FEncounterFrgAdapter mAdapter;
    private String headImgUrl;
    private RoundedImageView rivHeadImg;
    private Uri imgUriCopy;
    private KillDayCardPop dayPop;
    private Unbinder unbinder;
    private int countFresh = 0;
    private List<FPromotionBean> promotionList;
    private Map<String, HomeAct.Listener> listWindowMap;  //刷新窗口接口
    private FindFragment findFragment;
    private OnScrollChangeListener listener;

    private WindowDissmissListener clickListener;
    private boolean isFirstLoad = true;

    AutoLinearLayout ll_popup;
    HeaderGridView gridView;

    public void setFindFragment(FindFragment findFragment)
    {
        this.findFragment = findFragment;
    }


    @Override
    protected ImpIEncounterFrgView createPresenter()
    {
        return new ImpIEncounterFrgView();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frg_encounter, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        afterViews();
        initLocation();
    }

    /**
     * 初始化定位
     */
    private void initLocation()
    {
        try
        {
            if (getActivity()!=null && !getActivity().isFinishing())
            {
                mLocClient = new LocationClient(getActivity().getApplicationContext());
            }
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
            mLocClient.start();
        } catch (Exception e)
        {
            e.printStackTrace();
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
                DecimalFormat decimalFormat = new DecimalFormat("#.0000000");
                mCurrentLantitude = location.getLatitude();
                mCurrentLongitude = location.getLongitude();

                Logger.t(TAG).d("位置：经度：" + mCurrentLantitude + " 纬度：" + mCurrentLongitude);
                // 第一次定位时，将地图位置移动到当前位置
                if (isFirstLocation)
                {
                    if (Double.compare(mCurrentLantitude, 4.9E-324) != 0)
                    {
                        isFirstLocation = false;
                        mPresenter.getEncounterList("refresh", PAGE_SIZE + "", city, "0", mCurrentLantitude, mCurrentLongitude, "");
                        mLocClient.stop();
                    }
                }
            }
        }
    }

    private void afterViews()
    {
        mActivity = getActivity();
        mEncounterList = new ArrayList<>();
        gridView = pullToRefreshHeaderGridView.getRefreshableView();
        pullToRefreshHeaderGridView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshHeaderGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HeaderGridView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HeaderGridView> refreshView)
            {
                mPresenter.getCarouselEnc();
                mPresenter.getEncounterList("refresh", PAGE_SIZE + "", city, "0", mCurrentLantitude, mCurrentLongitude, "");
                mPresenter.getTodayCheck();

                listWindowMap.clear();
                mPresenter.getAllFinishSuccesses();
                mPresenter.getAllFinishTask();
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        firstRun = false;
                    }
                }, 2000);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HeaderGridView> refreshView)
            {
                mPresenter.getEncounterList("add", PAGE_SIZE + "", city, mEncounterList.size() + "", mCurrentLantitude, mCurrentLongitude, "");
                mPresenter.getTodayCheck();
            }
        });
        mAdapter = new FEncounterFrgAdapter(mEncounterList, mActivity);
        if (networkImageIndicatorView == null)
        {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_encounter_indicator, null, false);
            networkImageIndicatorView = view.findViewById(R.id.icv_cycle_view_enc);
            gridView.addHeaderView(view);
        }
        gridView.setNumColumns(3);
        gridView.setHorizontalSpacing(CommonUtils.dp2px(mActivity, 5));
        gridView.setVerticalSpacing(CommonUtils.dp2px(mActivity, 5));
        gridView.setBackgroundResource(R.color.white);
        gridView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new FEncounterFrgAdapter.OnItemClickListener()
        {
            @Override
            public void itemClick(int position, EncounterBean itemBean)
            {
                if (CommonUtils.isFastDoubleClick())
                    return;
                Logger.t(TAG).d("item ->>>>" + position);
                Intent intent = new Intent(getActivity(), CNewUserInfoAct.class);
                intent.putExtra("checkWay", "UId");
                intent.putExtra("toUId", itemBean.getUId());
                startActivity(intent);
            }
        });
        mPresenter.getCarouselEnc();
        mPresenter.getEncounterList("refresh", FIRST_LOAD_PAGE_SIZE, city, "0", mCurrentLantitude, mCurrentLongitude, "");
        long time = System.currentTimeMillis();
        initWindow();
/*  测试用
      GlideApp.with(mActivity)
                .load(R.drawable.loading)
                .asGif()
                .into(ivTest);*/
        // setGetCard();
    }

    int tempValue = 0;
    private volatile String moveOrientation = "down";
    private OnShowFindTitleWithoutPicListener findTitleWithoutPicListener;

    public interface OnShowFindTitleWithoutPicListener
    {
        void onShowFindTitle();

        void onMoveOrientation(String moveOrientation);
    }

    public void setOnShowFindTitleWithoutPicListener(OnShowFindTitleWithoutPicListener listener)
    {
        findTitleWithoutPicListener = listener;
    }

    private void initWindow()
    {
        listWindowMap = new HashMap<String, HomeAct.Listener>();
        ivSevenL.setOnClickListenern(new DragLayout.OnClickListenern()
        {
            @Override
            public void onClick()
            {
                mPresenter.getMonthCheck(false);
                mPresenter.getSevenWeal(false);
            }
        });

        dayPop = new KillDayCardPop(getActivity());
        dayPop.setOnClickListener(new KillDayCardPop.OnClickListenern()
        {
            @Override
            public void onYueClick()
            {
                mPresenter.getCheckIn();
            }

            @Override
            public void onQiClick()
            {
                mPresenter.getSevenCheckInEnc();
            }
        });
        dayPop.setOnShowListener(new KillDayCardPop.OnShowListenern()
        {
            @Override
            public void onShowState(boolean isShow)
            {
                mPresenter.getTodayCheck();
            }
        });
        dayPop.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                if (onDissmiss != null)
                {
                    onDissmiss.onDissmiss("每日签到");
                }
                // mPresenter.getAllFinishSuccesses();
            }
        });

    }

    private void dissMissType(String type)
    {
        if (onDissmiss != null)
        {
            onDissmiss.onDissmiss(type);
        }
    }

    //预加载窗口
    public void checkWindow()
    {
        showTodayPop();
        showAllFinishSuccessesPop();
        showmgetAllFinishTaskPop();
        showFestivalDialog();
    }

    //检测弹出成就
    private void showAllFinishSuccessesPop()
    {
        if (mPresenter != null)
            mPresenter.getAllFinishSuccesses();
    }

    //检测任务成就
    private void showmgetAllFinishTaskPop()
    {
        if (mPresenter != null)
            mPresenter.getAllFinishTask();
    }


    //检测弹出月签到
    private void showTodayPop()
    {
        //产品需每天第一次进入APP弹出每日签到 七日不需要弹出
        if ("1".equals(SharePreUtils.getFirst(mActivity)))
        {
            showTodayDialog();
        }
        else
        {
            putListener("每日签到", null);
        }
    }

    //弹出活动弹窗
    private void showFestivalDialog()
    {
        if (mPresenter != null)
        {
            mPresenter.getMyRedIncome();
            mPresenter.midAutumm();
        }
    }

    public void showTodayDialog()
    {
        if (mPresenter != null)
        {
            mPresenter.getMonthCheck(false);
            mPresenter.getSevenWeal(false);
        }
    }


    //活动弹窗
    private void showMidAutumndialog(final String url, final String pic, final String name, final String title)
    {
        final Dialog dialog = new Dialog(mActivity, R.style.dialog2);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                dissMissType(title);
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_midautumn, null);
        RoundedImageView imageView = (RoundedImageView) view.findViewById(R.id.ri_image);
        RelativeLayout rel = view.findViewById(R.id.rl_layout);
        IconTextView cancel = (IconTextView) view.findViewById(R.id.killcard_cancel);
        ImageView join = (ImageView) view.findViewById(R.id.btn_join);
        join.setImageResource("双十一弹窗".equals(title) ? R.drawable.act_look : R.drawable.btn_join);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rel.getLayoutParams();
        layoutParams.height = CommonUtils.dp2px(mActivity, "双十一弹窗".equals(title) ? 350 : 455);
        rel.setLayoutParams(layoutParams);
        join.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FPromotionBean pBean = new FPromotionBean();
                pBean.setActivityId("1");
                pBean.setActName(name);
                pBean.setActivityId(name);
                pBean.setWebUrl(url);
                pBean.setType("2");
                Intent intent = new Intent(mActivity, PromotionActionAct.class);
                intent.putExtra("fpBean", pBean);
                startActivity(intent);
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });

        //把头像准备好，
        GlideApp.with(mActivity.getApplicationContext())
                .asBitmap()
                .placeholder(R.drawable.qs_3_4)
                .load(pic)
                .centerCrop()
                .into(imageView);


        dialog.setContentView(view);


        putListener(title, new HomeAct.Listener()
        {
            @Override
            public void listener()
            {
                if (!TextUtils.isEmpty(url))
                {
                    if (!dialog.isShowing())
                        dialog.show();
                }
                else
                {
                    dissMissType(title);
                }
            }
        });
    }

    /**
     * @Description: 显示新手引导
     */
    public void showNewbieGuide()
    {
        if (SharePreUtils.getIsNewBieFind(mActivity))
        {
            NetHelper.checkIsShowNewbie(mActivity, "1", new ICommonOperateListener()
            {
                @Override
                public void onSuccess(String response)
                {
                    if ("0".equals(response) && findFragment != null && findFragment.getCurrentPosition() == 0)
                    {
                        //获取root节点
                        if(mActivity==null)
                            return;
                        final FrameLayout fRoot = (FrameLayout) mActivity.getWindow().getDecorView().findViewById(android.R.id.content);
                        final View vGuide = View.inflate(mActivity, R.layout.view_newbie_guide_find, null);
                        final ImageView imgFound = (ImageView) vGuide.findViewById(R.id.img_found);
                        final FrameLayout nextFl = (FrameLayout) vGuide.findViewById(R.id.fl_next);
                        final TextView nextTv = (TextView) vGuide.findViewById(R.id.tv_next);
                        final FrameLayout knownFl = (FrameLayout) vGuide.findViewById(R.id.fl_known);
                        TextView next1Tv = (TextView) vGuide.findViewById(R.id.tv_next_1);
                        vGuide.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {

                            }
                        });
                        vGuide.setClickable(true);
                        nextTv.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                nextFl.setVisibility(View.GONE);
                                knownFl.setVisibility(View.VISIBLE);
                            }
                        });
                        next1Tv.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                knownFl.setVisibility(View.GONE);
                                imgFound.setVisibility(View.VISIBLE);
                            }
                        });
                        imgFound.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                imgFound.setVisibility(View.GONE);
                                fRoot.removeView(vGuide);
                                //通知主页可以显示下一个弹窗
                                SharePreUtils.setIsNewBieFind(mActivity, false);
                                NetHelper.saveShowNewbieStatus(mActivity, "1");
                                dissMissType("新手引导");
                            }
                        });
                        fRoot.addView(vGuide);
                    }
                    else
                    {
                        dissMissType("新手引导");
                        SharePreUtils.setIsNewBieFind(mActivity, false);
                    }
                }

                @Override
                public void onError(String code, String msg)
                {

                }
            });

        }
        else
        {
            dissMissType("新手引导");
        }
    }


    //排序头像检测
    public void showHeadCheck()
    {
        if (SharePreUtils.getShowPop(mActivity))
        {
            Logger.t(TAG).d("读取》》审核弹窗显示状态>>" + SharePreUtils.getShowPop(mActivity));
            String phUrl = SharePreUtils.getHeadImg(mActivity);
            showPopupReview(1, R.layout.popup_review_head_portrait_yes, phUrl);
            popupWindow.showAtLocation(rlContainer, Gravity.CENTER, 0, 0);
            SharePreUtils.setShowPop(mActivity, false);
        }
        else
        {
            if (popupWindow != null && !popupWindow.isShowing())
                popupWindow.showAtLocation(rlContainer, Gravity.CENTER, 0, 0);
        }
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
            String urlByUCloud = CommonUtils.getThumbnailImageUrlByUCloud(pBeenLst.get(i).getImgUrl(), ImageDisposalType.THUMBNAIL, 7, 90, 90);
            urlList.add(urlByUCloud);
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
                if (pBeenLst.size() <= position)
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
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        mPresenter.getTodayCheck();
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }

    @Override
    public void requestNetError(String netInterface, String code)
    {
        pullToRefreshHeaderGridView.onRefreshComplete();
    }

    @Override
    public synchronized void getEncounterSuccess(String type, String focusTrendsCount, String trendsCount, String columnsCount, String messageCount, String phUrl, String avatarAuditStatus, List<EncounterBean> encounterList)
    {

        if ("refresh".equals(type) )
        {

            switch (avatarAuditStatus)
            {
                case "0":
                    break;
                case "1":
                 //    showPopupReview(1, R.layout.popup_review_head_portrait_yes, phUrl).showAsDropDown(gridView);
                    break;
                case "2":

                    if(countFresh>=3)
                    {
                        showPopupReview(0, R.layout.popup_review_head_portrait, phUrl);
                        if (popupWindow != null && !popupWindow.isShowing() && !SharePreUtils.getShowPop(mActivity) )
                            popupWindow.showAtLocation(rlContainer, Gravity.CENTER, 0, 0);
                    }else
                        {
                            putListener("头像审核", new HomeAct.Listener()
                            {
                                @Override
                                public void listener()
                                {
                                    showPopupReview(0, R.layout.popup_review_head_portrait, phUrl);
                                    if (popupWindow != null && !popupWindow.isShowing() && !SharePreUtils.getShowPop(mActivity) )
                                        popupWindow.showAtLocation(rlContainer, Gravity.CENTER, 0, 0);
                                }
                            });
                        }

                    break;
                default:
                    break;
            }
            putListener("头像审核",null);
        }
        if (encounterList != null && !encounterList.isEmpty())
        {
            if ("refresh".equals(type))
            {
                mEncounterList.clear();
            }
            for (EncounterBean encounterBean : encounterList)
            {
                if (mEncounterList.contains(encounterBean))
                {
                    int index = mEncounterList.indexOf(encounterBean);
                    mEncounterList.remove(index);
                }
                mEncounterList.add(encounterBean);
            }
            int offset = mEncounterList.size() % 3;//为了使列表下面对齐--wb
            for (int i = 0; i < offset; i++)
            {
                mEncounterList.remove(mEncounterList.size() - 1);
            }
            mAdapter.notifyDataSetChanged();
            if ("add".equals(type) && encounterList.size() > 0)
            {
                new Handler().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                        gridView.scrollListBy(100);
                    }
                });
            }
        }
        pullToRefreshHeaderGridView.onRefreshComplete();

        if (!TextUtils.isEmpty(trendsCount))
        {
            try
            {
                Logger.t("动态数量设置").d("未读" + SharePreUtils.getFocusTrendsCount(mActivity));
                int trendsCountInt = SharePreUtils.getFocusTrendsCount(mActivity) + Integer.parseInt(trendsCount);
                trendsCount = String.valueOf(trendsCountInt);
                if (isFirstLoad)
                {
                    isFirstLoad = false;
                    SharePreUtils.setFocusTrendsCount(mActivity, trendsCountInt);
                    Logger.t("动态数量设置").d("设置" + trendsCountInt);
                }
            } catch (NumberFormatException e)
            {
                e.printStackTrace();
                Logger.t(TAG).d("动态未读数量错误");
            }
            //更新动态数量
            Intent intent = new Intent(EamConstant.EAM_REFRESH_MSG);
            intent.putExtra("trend_counts", trendsCount);
            mActivity.sendBroadcast(intent);
        }
        if (!TextUtils.isEmpty(columnsCount))
        {
            //更新大V红点
            Intent intent = new Intent(EamConstant.EAM_REFRESH_BIGV_MSG);
            intent.putExtra("bigv_counts", columnsCount);
            mActivity.sendBroadcast(intent);
        }
        if (!TextUtils.isEmpty(messageCount))
        {
            //更新系统通知数量
            Logger.t(TAG).d("未读》》》messageCount》》" + messageCount);
            Intent intent = new Intent(EamConstant.EAM_REFRESH_SYS_MSG);
            intent.putExtra("sys_counts", messageCount);
            mActivity.sendBroadcast(intent);
        }
        if (!TextUtils.isEmpty(focusTrendsCount))
        {
            EamApplication.getInstance().dynamicCount = focusTrendsCount;
            //更新聊天top栏红点数字
            Intent intent = new Intent(EamConstant.EAM_HX_RECEIVE_RED_HOME);
            getActivity().sendBroadcast(intent);
        }
        countFresh ++;
    }

    @Override
    public void getCarouselEncSuccess(ArrayList<FPromotionBean> pBeenLst)
    {
        promotionList = pBeenLst;
        //  测试数据
       //   http://192.168.10.243:8080/activity/host-contest/app.html
       // pBeenLst.get(0).setWebUrl("http://192.168.10.243:8080/activity/host-contest/app.html");
        if (pBeenLst != null && pBeenLst.size() > 0)
        {
            networkImageIndicatorView.setVisibility(View.VISIBLE);
            initCarousel(pBeenLst);
        }
        else
        {
            networkImageIndicatorView.setVisibility(View.GONE);
        }
    }

    @Override
    public void getSevenCheckInEncSuccess()
    {
        mPresenter.getTodayCheck();
        mPresenter.getSevenWeal(true);
    }

    @Override
    public void getSevenWealSuccess(List<Map<String, String>> param, boolean isCheckIn)
    {
        dayPop.setQiDate(param);
    }

    @Override
    public void getTodayCheckSuccess(String month, String sevenWeal)
    {

        boolean qiAnim = true;
        boolean yueAnim = true;
        switch (month)
        {
            case "0":
                dayPop.setCartState(false);
                break;
            case "1":
                dayPop.setCartState(true);
                yueAnim = false;
                break;
        }
        dayPop.setQiCartState(sevenWeal);
        switch (sevenWeal)
        {
            case "1":
                qiAnim = false;
                break;
            case "2":
                //国庆图标下移
                RelativeLayout.LayoutParams pa = (RelativeLayout.LayoutParams) glGq.getLayoutParams();
                if (pa.bottomMargin - CommonUtils.dp2px(mActivity, 80) > 0)
                    pa.bottomMargin = pa.bottomMargin - CommonUtils.dp2px(mActivity, 80);
                glGq.setLayoutParams(pa);
                qiAnim = false;
                break;
        }
        if (clickListener != null)
        {
            if (qiAnim || yueAnim)
                clickListener.onDissmiss("1");
            else
                clickListener.onDissmiss("0");
        }

    }

    @Override
    public void getMonthCheckSuccess(final List<Map<String, String>> param, final List<Map<String, String>> prizeParam, boolean isCheckIn,String skin,String icon)
    {
        if (firstRun)
        {
            putListener("每日签到", new HomeAct.Listener()
            {
                @Override
                public void listener()
                {
                    if(!mActivity.isFinishing())
                    dayPop.showDialog(param, prizeParam, isCheckIn,skin,icon);
                }
            });
        }
        else
        {
            if(!mActivity.isFinishing())
            dayPop.showDialog(param, prizeParam, isCheckIn,skin,icon);
        }
    }


    @Override
    public void getAllFinishTaskCallback(final FinishTaskBean finishTaskBean)
    {
        listWindowMap.put("任务", null);
        if (!mActivity.isFinishing() &&
                "1".equals(finishTaskBean.getReward()) && (firstRun ? CommonUtils.getDayFirst(mActivity, "task") : true))
        {
            if (finishTaskDialog == null)
            {
                finishTaskDialog = new FinishTaskDialog(mActivity, R.style.Dialog02);
                finishTaskDialog.setConfirm("领取");
                finishTaskDialog.setGotoAt("领取明细查看我的-任务>", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(getActivity(), TaskAct.class);
                        intent.putExtra("index", 0);
                        getActivity().startActivityForResult(intent, HomeAct.WINDOWSSISE);
                        new Handler().postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (mActivity != null && !mActivity.isFinishing() && finishTaskDialog.isShowing())
                                    finishTaskDialog.dismiss();
                            }
                        }, 1000);

                    }
                });
                finishTaskDialog.setDialogClickListener(new FinishTaskDialog.DialogClickListener()
                {
                    @Override
                    public void confirmClick()
                    {
                        mPresenter.finishAllTask();
                    }
                });

                finishTaskDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        dissMissType("任务弹窗");
                        executeWindow();
                    }
                });
            }
            if (firstRun)
            {
                putListener("任务弹窗", new HomeAct.Listener()
                {
                    @Override
                    public void listener()
                    {
                        if (finishTaskDialog != null && !finishTaskDialog.isShowing())
                            finishTaskDialog.show("完成任务奖励", finishTaskBean);
                    }
                });
            }
            else
            {
                listWindowMap.put("任务", new HomeAct.Listener()
                {
                    @Override
                    public void listener()
                    {
                        finishTaskDialog.show("完成任务奖励", finishTaskBean);
                    }
                });
                if (listWindowMap.size() >= 2)
                {
                    executeWindow();
                }
            }
        }
        else
        {
            putListener("任务弹窗", null);
        }
    }

    @Override
    public void getAllFinishSuccessesCallback(final FinishTaskBean finishTaskBean)
    {
        listWindowMap.put("成就", null);
        if (!mActivity.isFinishing() &&
                "1".equals(finishTaskBean.getReward()) && (firstRun ? CommonUtils.getDayFirst(mActivity, "achieve") : true))
        {
            if (achievementDialog == null)
            {
                achievementDialog = new FinishTaskDialog(mActivity, R.style.Dialog02);
                achievementDialog.setConfirm("领取");

                achievementDialog.setGotoAt("领取明细查看我的-成就>", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(getActivity(), TaskAct.class);
                        intent.putExtra("index", 1);
                        startActivity(intent);
                        new Handler().postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (mActivity != null && !mActivity.isFinishing() && achievementDialog.isShowing())
                                    achievementDialog.dismiss();
                            }
                        }, 1000);
                    }
                });

                achievementDialog.setDialogClickListener(new FinishTaskDialog.DialogClickListener()
                {
                    @Override
                    public void confirmClick()
                    {
                        mPresenter.finishAllSuccesses();
                    }
                });


                achievementDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        dissMissType("成就弹窗");
                        executeWindow();
                    }
                });
            }

            if (firstRun)
            {
                putListener("成就弹窗", new HomeAct.Listener()
                {
                    @Override
                    public void listener()
                    {
                        if (achievementDialog != null && !achievementDialog.isShowing())
                            achievementDialog.show("获得成就奖励", finishTaskBean);
                    }
                });
            }
            else
            {
                listWindowMap.put("成就", new HomeAct.Listener()
                {
                    @Override
                    public void listener()
                    {
                        achievementDialog.show("获得成就奖励", finishTaskBean);
                    }
                });

                if (listWindowMap.size() >= 2)
                {
                    executeWindow();
                }
            }

        }
        else
        {
            putListener("成就弹窗", null);
        }
    }

    @Override
    public void midAutummSuccess(final List<ActivityWindowBean> listBean)
    {

        if (!mActivity.isFinishing())
        {

            SharePreUtils.setIsHasAct(false);
            for (int i = 0; i < listBean.size(); i++)
            {
                final String url = listBean.get(i).getUrl();
                final String pic = listBean.get(i).getPic();

                String type = listBean.get(i).getType();
                if ("0".equals(type))
                {
                    String name = null, title = null;
                    String activity = listBean.get(i).getActivity();
                    switch (activity)
                    {
                        case "0":
                            name = "迎中秋 赢大奖";
                            title = "中秋弹窗";
                            break;
                        case "1":
                            name = "开仓放梁！度国庆！送大奖！";
                            title = "国庆弹窗";
                            break;
                        case "2":
                            name = "红包雨,领现金!";
                            title = "双十一弹窗";
                            SharePreUtils.setIsHasAct(true);
                            break;
                        default:
                            name = "开仓放梁！度国庆！送大奖！";
                            title = "国庆弹窗";
                            break;


                    }
                    showMidAutumndialog(url, pic, name, title);
                }
                else if ("1".equals(type))
                {
                    glGq.setVisibility(View.VISIBLE);
                    AnimationDrawable animationDrawable = (AnimationDrawable) ivGq.getDrawable();
                    animationDrawable.start();
                    glGq.setOnClickListenern(new DragLayout.OnClickListenern()
                    {
                        @Override
                        public void onClick()
                        {
                            FPromotionBean pBean = new FPromotionBean();
                            pBean.setActivityId("1");
                            pBean.setActName("开仓放梁！度国庆！送大奖！");
                            pBean.setActivityId("开仓放梁！度国庆！送大奖！");
                            pBean.setWebUrl(url);
                            pBean.setType("2");
                            Intent intent = new Intent(mActivity, PromotionActionAct.class);
                            intent.putExtra("fpBean", pBean);
                            startActivity(intent);
                        }

                    });
                }
            }

        }
        putListener("中秋弹窗", null);
        putListener("国庆弹窗", null);
        putListener("双十一弹窗", null);

    }


    @Override
    public void finishAllSuccessesCallback()
    {
        if (!mActivity.isFinishing())
        {
            ToastUtils.setGravity(Gravity.CENTER, 0, 0);
            ToastUtils.showCustomShortSafe(LayoutInflater.from(mActivity).inflate(R.layout.toast_finish, null));
            ToastUtils.cancel();
        }
        Intent intent = new Intent(EamConstant.EAM_HX_CMD_RECEIVE_RED_MY_REMIND);
        getActivity().sendBroadcast(intent);
    }

    @Override
    public void finishAllTaskCallback()
    {
        if (!mActivity.isFinishing())
        {
            ToastUtils.setGravity(Gravity.CENTER, 0, 0);
            ToastUtils.showCustomShortSafe(LayoutInflater.from(mActivity).inflate(R.layout.toast_finish, null));
            ToastUtils.cancel();
        }
        Intent intent = new Intent(EamConstant.EAM_HX_CMD_RECEIVE_RED_MY_REMIND);
        getActivity().sendBroadcast(intent);
    }

    @Override
    public void getCheckInSuccess()
    {
        mPresenter.getTodayCheck();
        mPresenter.getMonthCheck(true);
    }


    @Override
    public void upLoadSuccess()
    {
        Logger.t(TAG).d("上传头像成功");
        showPopupReview(1, R.layout.popup_review_head_portrait_yes, null).showAtLocation(rlContainer, Gravity.CENTER, 0, 0);;

        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(imgUriCopy)
                .placeholder(R.drawable.userhead)
                .error(R.drawable.userhead)
                .into(rivHeadImg);
    }

    @Override
    public void getMyRedInComeCallback(String red, String content, String income)
    {
        EamApplication.getInstance().isCheckRed = true;
        if ("1".equals(red))
        {
            putListener("双十一红包弹窗", new HomeAct.Listener()
            {
                @Override
                public void listener()
                {
                    if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(income))
                    {
                        Intent intent = new Intent(EamApplication.getInstance(), RedPacketShowAct.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        intent.putExtra("content", content);
                        intent.putExtra("income", income);
                        mActivity.startActivityForResult(intent, EamConstant.EAM_OPEN_RED_PACKET_SHOW);
                    }
                }
            });
        }
        else
        {
            putListener("双十一红包弹窗", null);
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    private PopupWindow popupWindow;

    private PopupWindow showPopupReview(int type, int layoutId, String imgUrl)
    {
        if (mActivity.isFinishing())
            return null;

        View contentView = LayoutInflater.from(mActivity).inflate(layoutId, null);
        contentView.requestFocus();
        rivHeadImg = (RoundedImageView) contentView.findViewById(R.id.riv_edit_head);
        if (popupWindow == null)
        {
            popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        }
      //  popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        if (imgUrl != null)
        {
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(imgUrl)
                    .centerCrop()
                    .placeholder(R.drawable.userhead)
                    .into(rivHeadImg);
        }
        contentView.setFocusable(true);
        contentView.setFocusableInTouchMode(true);
        Button leftBtn = (Button) contentView.findViewById(R.id.review_left_btn);
        Button rightBtn = (Button) contentView.findViewById(R.id.review_right_btn);
        Button singleBtn = (Button) contentView.findViewById(R.id.review_single_btn);
        if (type == 0)
        {
            leftBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (popupWindow != null)
                    {
                        popupWindow.dismiss();
                        popupWindow = null;
                    }

                }
            });
            rightBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (popupWindow != null)
                    {
                        popupWindow.dismiss();
                        popupWindow = null;
                        chooseMakeUserPhoto();
                    }

                }
            });
        }
        else
        {
            singleBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (popupWindow != null)
                    {
                        popupWindow.dismiss();
                        popupWindow = null;
                    }
                }
            });
        }

        contentView.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    if (popupWindow != null)
                    {
                        popupWindow.dismiss();
                        popupWindow = null;
                    }
                }
                return false;
            }
        });
        return popupWindow;
    }

    private void executeWindow()
    {
        if (listWindowMap.get("任务") != null)
        {
            listWindowMap.get("任务").listener();
            listWindowMap.remove("任务");
        }
        else if (listWindowMap.get("成就") != null)
        {
            listWindowMap.get("成就").listener();
            listWindowMap.remove("成就");
        }
    }


    private void chooseMakeUserPhoto()
    {
        toPhotoFromGallery();
    }

    /**
     * 从相机获取图片
     */
    private void toTakePhotoFromCamera()
    {
        boolean isCameraPermissions = CommonUtils.cameraIsCanUse();
        if (isCameraPermissions)
        {
            File storageDir = mActivity.getExternalCacheDir();
            File image = new File(storageDir, "TempImage.jpg");
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (openCameraIntent.resolveActivity(mActivity.getPackageManager()) != null)
            {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
//                {
//                    //对于Android N 的处理  详情参考 http://blog.csdn.net/honjane/article/details/52057132  ---yqh
//                    ContentValues contentValues = new ContentValues(1);
//                    contentValues.put(MediaStore.Images.Media.DATA, image.getAbsolutePath());
//                    Uri hostUri = mAct.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
//                    openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, hostUri);
//                }else
//                {
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(mActivity, "com.echoesnet.eatandmeet.provider", image));
//                }
                startActivityForResult(openCameraIntent, EamConstant.EAM_TAKE_PHOTO_FROM_CAMERA);
            }
        }
        else
        {
            ToastUtils.showShort("请打开相机功能");
        }
    }

    /**
     * 从相册获取图片
     */
    private void toPhotoFromGallery()
    {
        Intent getImageIntent = new Intent(Intent.ACTION_PICK, null);
        getImageIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        if (getImageIntent.resolveActivity(mActivity.getPackageManager()) != null)
        {
            //return the call activity after get the picture.
            PhotoPicker.builder()
                    .setPhotoCount(1)
                    .setPreviewEnabled(true)
                    .setShowCamera(true)
                    .setShowGif(false)
                    .start(mActivity, EamConstant.EAM_OPEN_IMAGE_PICKER);
        }
    }

    private class CdnFeedbackListener implements IOnCdnFeedbackListener
    {
        private Uri imgUri;


        private CdnFeedbackListener(Uri imgUri)
        {

            this.imgUri = imgUri;
        }

        @Override
        public void onSuccess(JSONObject response, File file, String fileKeyName, int uploadOrder)
        {

            if (mActivity != null)
            {
                //  mActivity.tvUploadProgress.setVisibility(View.GONE);
                Logger.t(TAG).d("成功：" + response.toString());
                imgUriCopy = imgUri;
                headImgUrl = CdnHelper.CDN_ORIGINAL_SITE + fileKeyName;
                //向后台更新头像
                if (mPresenter != null)
                {
                    mPresenter.upLoadingPic(headImgUrl);
                }

            }
        }

        @Override
        public void onProcess(long len)
        {

            // mActivity.tvUploadProgress.setText((int) len + "%");

        }

        @Override
        public void onFail(JSONObject response, File file)
        {
            if (mActivity != null)
            {

                Logger.t(TAG).d("错误：" + response.toString());
            }
        }
    }

    public void setCode(int requestCode, int resultCode, Intent data)
    {
        handleCrop(resultCode, data);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.t(TAG).d("requestCode--> " + requestCode + " , resultCode--> " + resultCode);
        switch (requestCode)
        {

            case EamConstant.EAM_OPEN_IMAGE_PICKER:
                if (resultCode == RESULT_OK)
                {
                    ArrayList<String> mResults = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    Logger.t(TAG).d("图片地址：" + mResults.get(0));
                    File imgPath = new File(mResults.get(0));

                    String tempFileName = "a_" + SharePreUtils.getUserMobile(mActivity) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
                    String outPutImagePath = NetHelper.getRootDirPath(mActivity) + CommonUtils.toMD5(tempFileName);
                    File outFile = new File(outPutImagePath);

                    // int degree = ImageUtils.readPictureDegree(result.getData().getPath().replace("/raw", ""));
                    int degree = ImageUtils.readPictureDegree(mResults.get(0));
                    //         Logger.t(TAG).d("旋转角度为》" + degree + " , " + result.getData().getPath());

                    Bitmap bitmap = ImageUtils.getBitmapFromUri(Uri.fromFile(imgPath), mActivity);
                    if (bitmap != null)
                    {
                        ImageUtils.compressBitmap(bitmap, outFile.getPath(), 150, degree);
                        Logger.t(TAG).d("压缩成功" + outFile.length() / 1024);
                        beginCrop(Uri.fromFile(outFile));
                    } else
                    {
                        ToastUtils.showShort("选择图片失败，请重新选择");
                    }

                }
                break;
            case Crop.REQUEST_CROP:
                handleCrop(resultCode, data);
                break;
            case EamConstant.EAM_TAKE_PHOTO_FROM_CAMERA:
                Logger.t(TAG).d("拍照进入照片路径");
                if (resultCode == RESULT_OK)
                {
                    try
                    {
                        File imgPath = new File(mActivity.getExternalCacheDir() + "/" + "TempImage.jpg");
                        String tempFileName = "a_" + SharePreUtils.getUserMobile(mActivity) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
                        String outPutImagePath = NetHelper.getRootDirPath(mActivity) + CommonUtils.toMD5(tempFileName);
                        // Uri uri1 = Uri.parse("file://" + "/" + imgPath.getAbsolutePath());
                        Logger.t(TAG).d("照片路径：" + imgPath.getAbsolutePath());
                        Log.d(TAG, "照片路径：" + imgPath.getAbsolutePath());
                        final File outFile = new File(outPutImagePath);

                        // zdw --- 添加针对三星拍照后旋转90显示问题
                        int degree = ImageUtils.readPictureDegree(imgPath.getAbsolutePath());
                        Bitmap bitmap = ImageUtils.getBitmapFromFile(mActivity, imgPath.getAbsolutePath());
                        if (bitmap != null)
                        {
                            ImageUtils.compressBitmap(bitmap, outFile.getPath(), 130, degree);
                            Logger.t(TAG).d("压缩成功" + outFile.length() / 1024);

                            beginCrop(FileProvider.getUriForFile(mActivity, "com.echoesnet.eatandmeet.provider", outFile));
                            imgPath.delete();
                        }
                        else
                        {
                            ToastUtils.showShort("选择图片失败，请重新选择");
                        }

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                break;

            case EamConstant.EAM_OPEN_RED_PACKET_SHOW:
                dissMissType("双十一红包弹窗");
                break;

        }
    }

    private void beginCrop(Uri source)
    {
        String fileName = "a_" + SharePreUtils.getUserMobile(mActivity) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
        Logger.t(TAG).d(mActivity.getCacheDir() + "/" + fileName);
        Uri destination = Uri.fromFile(new File(mActivity.getCacheDir(), CommonUtils.toMD5(fileName)));
        Crop.of(source, destination).asSquare().withMaxSize(500, 500).start(mActivity);
    }

    private void handleCrop(int resultCode, Intent result)
    {
        if (resultCode == RESULT_OK)
        {
            Uri uri = Crop.getOutput(result);
            updateHeadImg(uri);
        }
        else if (resultCode == Crop.RESULT_ERROR)
        {
            Logger.t(TAG).d("拍照返回错误信息--> " + Crop.getError(result).getMessage());
            if (Crop.getError(result).getMessage().toLowerCase().contains("gif"))
            {
                ToastUtils.showShort("不能使用GIF图片");
            }
            else
            {
                ToastUtils.showShort(Crop.getError(result).getMessage());
            }
        }
    }

    private void updateHeadImg(final Uri inputUri)
    {
        String fileKeyName = CdnHelper.userImage + SharePreUtils.getUserMobile(mActivity) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
        CdnHelper.getInstance().setOnCdnFeedbackListener(new CdnFeedbackListener(inputUri));
//        rivHeadImg.setBackground(getResources().getDrawable(R.drawable.shape_circle_c0412_bg));
        CdnHelper.getInstance().putFile(new File(inputUri.getPath()), "img", fileKeyName, 0);
    }

    //各种窗口关闭监听
    private WindowDissmissListener onDissmiss;

    public interface WindowDissmissListener
    {
        void onDissmiss(String flag);
    }

    public interface onResultListener
    {
        void onResult(String result);
    }

    public void setWindowDissmissListener(WindowDissmissListener onDissmiss)
    {
        this.onDissmiss = onDissmiss;
    }

    public void setClickListener(WindowDissmissListener clickListener)
    {
        this.clickListener = clickListener;
    }

    private Map<String, HomeAct.Listener> listenerMap;  //首次开启APP接口


    public void setListenerMap(Map<String, HomeAct.Listener> map)
    {
        this.listenerMap = map;
    }

    //
    private boolean firstRun = true;

    private void putListener(String name, HomeAct.Listener listener)
    {
        if (listenerMap != null)
        {
            if (listenerMap.containsKey(name))
                return;

            listenerMap.put(name, listener);

            if (listenerMap.size() >= HomeAct.WINDOWSSISE)
            {
                firstRun = false;
            }
            dissMissType("notification");
        }
    }


    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener)
    {
        listener = onScrollChangeListener;
    }

    public interface OnScrollChangeListener
    {
        void onScrollChange(int x, int y, int oldX, int oldY);
    }

}

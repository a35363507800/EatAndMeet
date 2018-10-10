package com.echoesnet.eatandmeet.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.echoesnet.eam.icontextmodule.model.EchoesEamIcon;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.DFlashPayInputAct;
import com.echoesnet.eatandmeet.activities.DOrderMealDetailAct;
import com.echoesnet.eatandmeet.activities.DResAddressMapAct;
import com.echoesnet.eatandmeet.activities.HtmlPureShowAct;
import com.echoesnet.eatandmeet.activities.ReportFoulsResrAct;
import com.echoesnet.eatandmeet.activities.SelectTableAct;
import com.echoesnet.eatandmeet.activities.TrendsPlayVideoAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.BigVcommentBean;
import com.echoesnet.eatandmeet.models.bean.CarouselBean;
import com.echoesnet.eatandmeet.models.bean.CommonUserCommentBean;
import com.echoesnet.eatandmeet.models.bean.RestaurantBean;
import com.echoesnet.eatandmeet.models.bean.RestaurantNoteBean;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.presenters.ImpIRestaurantFrgPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IRestaurantFrgView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.LoadingProgressUtil;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.BigVCommentAdapter;
import com.echoesnet.eatandmeet.views.adapters.LoserCommentAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.CustomRatingBar.CustomRatingBar;
import com.echoesnet.eatandmeet.views.widgets.ExpandCollapseView;
import com.echoesnet.eatandmeet.views.widgets.ImageIndicatorView.NetworkImageIndicatorView;
import com.echoesnet.eatandmeet.views.widgets.ResCallPhonePop;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.video.EmptyControlVideo;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.widget.IconTextView;
import com.linearlistview.LinearListView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import com.panxw.android.imageindicator.AutoPlayManager;
import com.panxw.android.imageindicator.ImageIndicatorView;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;


public class RestaurantFrg extends MVPBaseFragment<IRestaurantFrgView,ImpIRestaurantFrgPre> implements IRestaurantFrgView
{
    //region 变量
    public final static String TAG = RestaurantFrg.class.getSimpleName();
    private final static String PAGE_COUNT = "5";
    private final static String INIT_COUNT = "3";
    private Unbinder unbinder;
    private Activity mActivity;
    @BindView(R.id.icv_cycle_view)
    NetworkImageIndicatorView mImageCycleView;
    @BindView(R.id.iv_res_down_arrow)
    ImageView ivMore;
    @BindView(R.id.lv_res_discount)
    LinearListView lvDiscount;
    @BindView(R.id.lv_res_bigv_comment)
    LinearListView lvBigVComment;
    @BindView(R.id.lv_res_loser_comment)
    LinearListView lvLoserComment;
    @BindView(R.id.prs_res_scrollview)
    PullToRefreshScrollView pullToRefreshScrollView;
    @BindView(R.id.all_more_discount)
    LinearLayout allMoreDiscount;
    @BindView(R.id.ll_Big_comment)
    LinearLayout llBigComment;
    @BindView(R.id.ll_vip_comment)
    LinearLayout llVipComment;
    @BindView(R.id.all_res_address)
    IconTextView allResLocation;
    @BindView(R.id.all_call_phone)
    IconTextView allCallPhone;
    @BindView(R.id.itv_report_restaurant)
    IconTextView itvReportRes;//举报
    @BindView(R.id.tv_res_name)
    TextView tvResName;
    @BindView(R.id.tv_res_limit)
    TextView tvResLimit;
    @BindView(R.id.tv_res_average)
    TextView tvResAverage;
    @BindView(R.id.tv_res_address)
    TextView tvResAddress;
    @BindView(R.id.tv_res_opentime)
    TextView tvResOpenTime;
    @BindView(R.id.tv_res_ordered)
    TextView tvResOrderedTable;
    @BindView(R.id.tv_res_phone)
    TextView tvResPhoneNum;
    //闪付
    @BindView(R.id.btn_flash_pay)
    Button btnFlashPay;
    //品牌故事
    @BindView(R.id.btn_brand_story)
    Button btnBrandStory;
    @BindView(R.id.view_bigTop_line)
    View viewBigTopLine;
    @BindView(R.id.view_vip_top_line)
    View viewVipTopLine;
    @BindView(R.id.btn_res_book_desk)
    Button btnResBookDesk;
    @BindView(R.id.rating_bar)
    CustomRatingBar ratingBar;
    @BindView(R.id.loading_view)
    RelativeLayout rlLoadingView;
    //找到暂无更多
    @BindView(R.id.tv_zanwu)
    TextView tvZanWu;
    @BindView(R.id.rl_user_hint)
    RelativeLayout rlUserHint;

    private AutoPlayManager autoBrocastManager;
    private ResCallPhonePop resCallPhonePop;
    // 再没有更多数据获取时,禁止列表上拉加载动作
    private boolean pullMove = true;

    private String resId;
    private String resName;
    private String phone[];
    private ArrayList<BigVcommentBean> bigVcommentLst = new ArrayList<>();
    private List<CommonUserCommentBean> loserCommentLst = new ArrayList<>();
    private String[] location;
    private boolean phoneFlag = false;
    private boolean isAutoPlay = true;
    //0:未点菜未订桌 2：点菜单未订桌1:订桌但未点菜3：两者都具备
    //private String orderStatus="0";
    //endregion
    @BindView(R.id.expandable_text)
    ExpandCollapseView expandCollapseView;

    private BigVCommentAdapter bigVCommentAdapter;
    private LoserCommentAdapter loserCommentAdapter;

    private String bootyCallDate;//约会日期
    private RestaurantNoteBean noteBean;
    private CustomAlertDialog customAlertDialog;
    private Map<Integer,View> videoViewMap;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        videoViewMap = new HashMap<>();
        if (getArguments() != null)
        {
            resId = getArguments().getString("resId");
            bootyCallDate = getArguments().getString("bootyCallDate");
            ArrayList<HashMap<String, String>> bigVLst = (ArrayList<HashMap<String, String>>) getArguments().getSerializable("bigVLst");
            Logger.t(TAG).d(bigVLst.toString());
            for (Map<String, String> map : bigVLst)
            {
                BigVcommentBean bigvBean = new BigVcommentBean();
                bigvBean.setNickName(map.get("nicName"));
                bigvBean.setTitle(map.get("evaOccupation"));
                try
                {
                    bigvBean.setRating(Integer.parseInt(map.get("rStar")));
                } catch (NumberFormatException ex)
                {
                    bigvBean.setRating(0);
                }
                bigvBean.setComment(map.get("detail"));
                bigvBean.setUserHeadImg(map.get("uphUrl"));
                bigvBean.setuId(map.get("uId"));
                bigvBean.setLevel(map.get("level"));
                bigvBean.setCommentImgUrls(map.get("epUrls"));
                bigvBean.setSex(map.get("sex"));
                bigVcommentLst.add(bigvBean);

            }
            location = getArguments().getStringArray("location");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frg_restaurant, container, false);
        unbinder = ButterKnife.bind(this, view);
        initAfterViews();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if (mPresenter != null)
        {
            LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, true, 0, null);
            mPresenter.getResDetailInfo(resId, INIT_COUNT);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (autoBrocastManager != null)
        {
            isAutoPlay = true;
            autoBrocastManager.setBroadcastEnable(true);
            autoBrocastManager.loop();
        }
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
        stopPlayVideo( -1 );
    }

    @Override
    protected String getPageName()
    {
        return TAG;

    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (autoBrocastManager != null)
        {
            autoBrocastManager.stop();
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * popupwindow在显示中点击返回杀死当前页会造成窗口泄露
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (resCallPhonePop != null && resCallPhonePop.isShowing())
        {
            resCallPhonePop.dismiss();
            resCallPhonePop = null;
        }
        GSYVideoManager.releaseAllVideos();
    }

    @Override
    protected ImpIRestaurantFrgPre createPresenter()
    {
        return new ImpIRestaurantFrgPre();
    }

    public RestaurantFrg()
    {
    }

    public static RestaurantFrg newInstance(String resId, ArrayList<HashMap<String, String>> bigVLst, String[] location, String bootyCallDate)
    {
        RestaurantFrg fragment = new RestaurantFrg();
        Bundle args = new Bundle();
        args.putSerializable("bigVLst", bigVLst);
        args.putString("resId", resId);
        args.putStringArray("location", location);
        args.putString("bootyCallDate", bootyCallDate);
        fragment.setArguments(args);
        return fragment;
    }

    /*    @AfterInject
    void afterInject()
    {
        IRestaurantComponent restaurantComponent = DaggerIRestaurantComponent.builder()
                .restaurantModule(new RestaurantModule()).build();
        restaurantComponent.inject(this);

        resInstance.FadeData();
    }*/

    private void initAfterViews()
    {
        mActivity = getActivity();
        getResDataOnCdn();
        btnResBookDesk.setVisibility(View.VISIBLE);
        ivMore.setImageDrawable(new IconDrawable(mActivity, EchoesEamIcon.eam_n_down)
                .colorRes(R.color.c15));
        tvResName.setSelected(true);

        bigVCommentAdapter = new BigVCommentAdapter(mActivity, bigVcommentLst);
        //设置大咖评论
        lvBigVComment.setAdapter(bigVCommentAdapter);
        lvBigVComment.setOnItemClickListener(new LinearListView.OnItemClickListener()
        {
            @Override
            public void onItemClick(LinearListView parent, View view, int position, long id)
            {
            }
        });

        //屌丝评论
        pullToRefreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        pullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>()
        {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView)
            {
                if(mPresenter != null)
                {
                    mPresenter.refreshLoserCom(resId, String.valueOf(loserCommentLst.size()), PAGE_COUNT);
                    changeUIShow();
                }

            }
        });
        //改变UI显示,显示暂无更多评论
        changeUIShow();

        //loserCommentAdapter=new LoserCommentAdapter(mActivity,resInstance.getRestaurantInstance().getEvaList());
        loserCommentAdapter = new LoserCommentAdapter(mActivity, loserCommentLst);
        lvLoserComment.setAdapter(loserCommentAdapter);

    }

    public void changeUIShow()
    {
        if (loserCommentLst.size() == 0)
        {
            //增加新代码
            Logger.t(TAG).d("没有数据,显示空数据默认图");
            //tvZanWu.setVisibility(View.VISIBLE);
            viewVipTopLine.setVisibility(View.GONE);
            llVipComment.setVisibility(View.GONE);
            pullMove = false;
        }
        else
        {
            viewVipTopLine.setVisibility(View.VISIBLE);
            llVipComment.setVisibility(View.GONE);
            tvZanWu.setVisibility(View.GONE);
            pullMove = true;
        }
    }


    //根据数据设置UI
    private void setResDetailInfo(RestaurantBean resBean)
    {
        if (!TextUtils.isEmpty(resBean.getResStory()))
        {
            btnBrandStory.setVisibility(View.VISIBLE);
            btnBrandStory.setTag(resBean.getResStory());
        }

        loserCommentLst.addAll(resBean.getEvaList());
        if (loserCommentLst.size() == 0)
        {
            viewVipTopLine.setVisibility(View.GONE);
            llVipComment.setVisibility(View.GONE);
        }
        else
        {
            viewVipTopLine.setVisibility(View.VISIBLE);
            llVipComment.setVisibility(View.GONE);
        }
        loserCommentAdapter.notifyDataSetChanged();
        ratingBar.setIndicator(true);
        ratingBar.setRatingBar(Integer.parseInt(resBean.getrStar()));
//        TextView ivCollection = ((DOrderMealDetailAct) getActivity()).navBtns.get(1);
        Map<String, TextView> mapTextView = ((DOrderMealDetailAct) getActivity()).navBtns.get(1);
        TextView ivCollection = mapTextView.get(TopBarSwitch.NAV_BTN_ICON);
        //设置收藏按钮
        if (resBean.getCollected().equals("0"))
        {
            ivCollection.setText("{eam-s-star3}");
            ivCollection.setTag("0");
        }
        else
        {
            ivCollection.setText("{eam-s-star}");
            ivCollection.setTag("1");
        }

        resName = resBean.getrName() == null ? "无名餐厅" : resBean.getrName();
        //Logger.t(TAG).d(resName);
        tvResName.setText(resBean.getrName());
        tvResLimit.setText("￥" + resBean.getLessPrice());

        try
        {
            tvResAverage.setText("￥" + (int)(Float.parseFloat(resBean.getPerPrice())));
        } catch (NumberFormatException e)
        {
            tvResAverage.setText("￥" + resBean.getPerPrice());
        }
        tvResAddress.setText(resBean.getrAddr());
        tvResOpenTime.setText(resBean.getrTime());
//        tvResOrderedTable.setText(Html.fromHtml(String.format("<p>%s <font color=%s>桌</font></p>", resBean.getBookedNum(), "#333333")));
        Logger.t(TAG).d("订桌--> " + resBean.getBookedNum());
        tvResOrderedTable.setText(resBean.getBookedNum());
        phone = resBean.getrMobile().split(CommonUtils.SEPARATOR);
        tvResPhoneNum.setText("商家电话 " + phone[0]);
        Logger.t(TAG).d("phone[].length" + phone.length);
        if (phone != null && phone.length > 1)
        {
            tvResPhoneNum.setText("商家电话 " + resBean.getrMobile().replace(CommonUtils.SEPARATOR, ","));
        }
        if (phone.length > 1)
        {
            phoneFlag = true;
        }
        EamApplication.getInstance().lessPrice = resBean.getLessPrice();
        Logger.t(TAG).d(resBean.getRpUrls());
        initCycleViewData(resBean);
        if(bigVcommentLst.size() == 0 && loserCommentLst.size() == 0)
        {
            rlUserHint.setVisibility(View.GONE);
        }
        LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, false, 0, null);
        Logger.t(TAG).d("222222222222");
    }


    @OnClick({R.id.btn_res_book_desk, R.id.all_more_discount,
            R.id.all_res_address, R.id.all_call_phone, R.id.btn_flash_pay, R.id.btn_brand_story, R.id.itv_report_restaurant})
    void clickView(View view)
    {
        switch (view.getId())
        {
            //订桌
            case R.id.btn_res_book_desk:
                Logger.t(TAG).d("下单类型--> " + SharePreUtils.getOrderType(mActivity));
                Intent intent = new Intent(mActivity, SelectTableAct.class);
                intent.putExtra("restId", resId);
                intent.putExtra("resName", resName);
                intent.putExtra("bootyCallDate", bootyCallDate);
                mActivity.startActivityForResult(intent, EamCode4Result.reQ_SelectTableActivity);
                break;
            case R.id.all_more_discount:

                break;
            case R.id.all_res_address://地址
                Intent intentM = new Intent(mActivity, DResAddressMapAct.class);
                intentM.putExtra("location", location);
                intentM.putExtra("resName", resName);
                intentM.putExtra("resAddress", tvResAddress.getText());
                getActivity().startActivity(intentM);
                break;
            //点击电话
            case R.id.all_call_phone:

                if (phoneFlag)
                {
                    resCall();
                }
                else
                {
                    CommonUtils.makeCall(getActivity(), tvResPhoneNum.getText().toString());
                }
                break;

            //退款须知
//            case R.id.all_refund_policy:
//                startActivity(new Intent(mActivity, DRefundPolicyAct.class));
//                mActivity.overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
//                break;
            //闪付
            case R.id.btn_flash_pay:
                Intent intentQuickPay = new Intent(mActivity,DFlashPayInputAct.class);
                intentQuickPay.putExtra("resName", resName);
                intentQuickPay.putExtra("resId", resId);
                startActivity(intentQuickPay);
                break;
            //品牌故事
            case R.id.btn_brand_story:
                Intent intentBrandStory =new Intent(mActivity, HtmlPureShowAct.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", resName);
                bundle.putString("pageUrl", String.valueOf(btnBrandStory.getTag()));
                intentBrandStory.putExtra("pageInfo", bundle);
                startActivity(intentBrandStory);
                break;
            case R.id.itv_report_restaurant:
                Intent reportIntent = new Intent(mActivity, ReportFoulsResrAct.class);
                reportIntent.putExtra("rId", resId);
                startActivity(reportIntent);
                break;
            default:
                break;
        }

    }

    private View.OnClickListener ietmOnclick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.res_call_one:
                    TextView tv = (TextView) v.findViewById(R.id.res_call_one);
                    CommonUtils.makeCall(getActivity(), tv.getText().toString());
            }
        }
    };

    private void resCall()
    {
        resCallPhonePop = new ResCallPhonePop(getActivity(), ietmOnclick, phone);
        resCallPhonePop.showPopupWindow(getActivity().findViewById(R.id.main),null);
    }
    private long startTime ;
    private void initCycleViewData(final RestaurantBean resBean)
    {
        String[] imgUrls =  resBean.getRpUrls().split(CommonUtils.SEPARATOR);
        final List<String> urlList = new ArrayList<>();
        List<CarouselBean> carouselList = new ArrayList<>();
        if (imgUrls == null)
            urlList.add("http://huisheng.ufile.ucloud.com.cn/test/120101000201.jpg");
        else
        {
            for (int i = 0; i < imgUrls.length; i++)
            {
                //使用网络加载图片
                urlList.add(imgUrls[i]);
            }
        }
        if (!TextUtils.isEmpty(resBean.getVideoUrl()))
        {
            CarouselBean carouselBean = new CarouselBean();
            carouselBean.setType("1");
            carouselBean.setVideoUrl(resBean.getVideoUrl());
            carouselBean.setPicUrl(resBean.getVideoPic());
            carouselList.add(carouselBean);
        }
        for (String s : urlList)
        {
            CarouselBean carouselBean = new CarouselBean();
            carouselBean.setType("0");
            carouselBean.setPicUrl(s);
            carouselList.add(carouselBean);
        }

        if (carouselList.size() > 1)
        {
            carouselList.add(0,carouselList.get(carouselList.size() - 1));
            carouselList.add(carouselList.get(1));
        }
        for (int i = 0; i < carouselList.size(); i++)
        {
            CarouselBean carouselBean = carouselList.get(i);
            if ("1".equals(carouselBean.getType()))
            {
                addVideoView(i,carouselBean);
            }else {
                ImageView imageView = new ImageView(getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                GlideApp.with(getContext())
                        .asBitmap()
                        .load(carouselBean.getPicUrl())
                        .centerCrop()
                        .placeholder(R.drawable.qs_cai_canting)
                        .error(R.drawable.cai_da)
                        .into(imageView);
                mImageCycleView.addViewItem(imageView);
            }
        }
        mImageCycleView.show();
        mImageCycleView.setOnItemClickListener(new ImageIndicatorView.OnItemClickListener()
        {
            @Override
            public void OnItemClick(View view, int position)
            {
                if (!TextUtils.isEmpty(resBean.getVideoUrl()))
                    position = position - 1;
                if (position > -1)
                    CommonUtils.showImageBrowser(mActivity, urlList, position, view);
            }
        });
        mImageCycleView.setOnItemChangeListener(new ImageIndicatorView.OnItemChangeListener()
        {
            @Override
            public void onPosition(int position, int totalCount)
            {
                stopPlayVideo( -1);
            }
        });
        autoBrocastManager = new AutoPlayManager(mImageCycleView);
        autoBrocastManager.setBroadcastEnable(true);
        autoBrocastManager.setBroadCastTimes(10000);//loop times
        autoBrocastManager.setBroadcastTimeIntevel(2 * 1000, 2 * 1000);//设置第一次展示时间以及间隔，间隔不能小于1秒
        autoBrocastManager.loop();
        mImageCycleView.showPageCountView();
        Logger.t(TAG).d("countTime>>>>>>>>>>>" + (System.currentTimeMillis() - startTime));
    }

    private void addVideoView(int position, final CarouselBean resBean)
    {
        if (!TextUtils.isEmpty(resBean.getVideoUrl()))
        {
            startTime = System.currentTimeMillis();
            final String videoUrl = resBean.getVideoUrl();
            final View view = LayoutInflater.from(mActivity).inflate(R.layout.item_indicator_play_video, null,false);
            Logger.t(TAG).d("countTime>>>>>>>>>>>" + (System.currentTimeMillis() - startTime));
            final EmptyControlVideo uVideoView = view.findViewById(R.id.uvideo_view);

            RoundedImageView thumbnailImg = (RoundedImageView) view.findViewById(R.id.img_thumbnail);
            TextView noticeTv = (TextView) view.findViewById(R.id.tv_notice);
            final FrameLayout thumbnailFl = (FrameLayout) view.findViewById(R.id.fl_thumbnail);
            final ImageView startOrStopImg = (ImageView) view.findViewById(R.id.img_start_or_stop);
            ImageView fullScreenImg = (ImageView) view.findViewById(R.id.img_full_screen);
            final ProgressBar playPro = (ProgressBar) view.findViewById(R.id.play_pro);

            thumbnailImg.setCornerRadius(0);
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .placeholder(R.drawable.qs_cai_canting)
                    .error(R.drawable.qs_cai_canting)
                    .centerCrop()
                    .load(resBean.getPicUrl())
                    .into(thumbnailImg);
            noticeTv.setVisibility(View.GONE);
            thumbnailFl.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    playVideo(uVideoView,videoUrl,startOrStopImg);
                }
            });
            uVideoView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    playVideo(uVideoView,videoUrl,startOrStopImg);
                }
            });
            fullScreenImg.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    uVideoView.onVideoPause();
                    uVideoView.seekTo(0);
                    isAutoPlay = false;
                    autoBrocastManager.stop();
                    thumbnailFl.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(mActivity, TrendsPlayVideoAct.class);
                    intent.putExtra("type","resBanner");
                    intent.putExtra("showType","0");
                    intent.putExtra("url",videoUrl);
                    getActivity().startActivityForResult(intent, EamConstant.EAM_OPEN_TRENDS_PLAY_VIDEO);
                }
            });
            startOrStopImg.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    playVideo(uVideoView,videoUrl,startOrStopImg);
                }
            });
            Logger.t(TAG).d("countTime>>>>>>>>>>>" + (System.currentTimeMillis() - startTime));
            uVideoView.setVideoAllCallBack(new GSYSampleCallBack(){
                @Override
                public void onPlayError(String url, Object... objects)
                {
                    super.onPlayError(url, objects);
                    GSYVideoManager.instance().setNeedMute(false);
                    Logger.t(TAG).d("onPlayError>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                }

                @Override
                public void onAutoComplete(String url, Object... objects)
                {
                    Logger.t(TAG).d("onAutoComplete>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    super.onAutoComplete(url, objects);
                    thumbnailFl.setVisibility(View.VISIBLE);
                    autoBrocastManager.setBroadcastEnable(true);
                    autoBrocastManager.loop();
                    playPro.setProgress(0);
                    startOrStopImg.setImageResource(R.drawable.play_video);
                    startOrStopImg.setTag("completed");
                }

                @Override
                public void onPrepared(String url, Object... objects)
                {
                    super.onPrepared(url, objects);
                    Logger.t(TAG).d("prepared>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    thumbnailFl.setVisibility(View.GONE);
                    uVideoView.setVisibility(View.VISIBLE);
                    mProgressBar = playPro;
                    playPro.setMax(uVideoView.getDuration());
                    handler.sendEmptyMessage(REFRESH_PROGRESS);
                    autoBrocastManager.stop();
                    stopPlayVideo(mImageCycleView.getCurrentIndex());
                    startOrStopImg.setImageResource(R.drawable.stop_video);
                    startOrStopImg.setTag("playing");
                }
            });
            videoViewMap.put(position,view);
            mImageCycleView.addViewItem(view);
        }
    }

    private void playVideo(EmptyControlVideo uVideoView,String videoUrl,ImageView startOrStopImg)
    {
        if (customAlertDialog == null)
        {
            customAlertDialog = new CustomAlertDialog(mActivity)
                    .builder()
                    .setCancelable(false);
        }
        this.uVideoView = uVideoView;
        if (uVideoView.getCurrentState() == GSYVideoView.CURRENT_STATE_PLAYING)
        {
            startOrStopImg.setImageResource(R.drawable.play_video);
            uVideoView.onVideoPause();
        }else {
            final int netStatus = NetHelper.getNetworkStatus(mActivity);
            String msg = null;
            if (netStatus == -1)//没网
            {
                msg = "当前网络不可用,请先检查网络是否可用";

            }else if (netStatus == 1)//非wifi
            {
                if (uVideoView.getCurrentState() == GSYVideoView.CURRENT_STATE_PAUSE)
                {
                    startOrStopImg.setImageResource(R.drawable.stop_video);
                    uVideoView.onVideoResume();
                }
                else if (uVideoView.getCurrentState() == GSYVideoView.CURRENT_STATE_PLAYING)
                {
                    startOrStopImg.setImageResource(R.drawable.play_video);
                    uVideoView.onVideoPause();
                }
                if (!TextUtils.isEmpty(videoUrl) && (uVideoView.getCurrentState() == GSYVideoView.CURRENT_STATE_AUTO_COMPLETE || startOrStopImg.getTag() == null))
                {
                    startOrStopImg.setImageResource(R.drawable.stop_video);
                    uVideoView.setUp(videoUrl,true,"");
                    uVideoView.startPlayLogic();
                }
            }else {
                msg = "当前为运营商流量使用环境，是否开始播放？";
            }
            if (!TextUtils.isEmpty(msg))
            {
                customAlertDialog.setMsg(msg);
                customAlertDialog.setPositiveButton("确定", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (netStatus == -1)
                        {
                            autoBrocastManager.setBroadcastEnable(true);
                            autoBrocastManager.loop();
                            customAlertDialog.dismiss();
                        }
                        else{
                            if (uVideoView.getCurrentState() == GSYVideoView.CURRENT_STATE_PAUSE)
                            {
                                startOrStopImg.setImageResource(R.drawable.stop_video);
                                uVideoView.onVideoResume();
                            }
                            else if (uVideoView.getCurrentState() == GSYVideoView.CURRENT_STATE_PLAYING)
                            {
                                startOrStopImg.setImageResource(R.drawable.play_video);
                                uVideoView.onVideoPause();
                            }
                            if (!TextUtils.isEmpty(videoUrl) && (uVideoView.getCurrentState() == GSYVideoView.CURRENT_STATE_AUTO_COMPLETE || startOrStopImg.getTag() == null))
                            {
                                startOrStopImg.setImageResource(R.drawable.stop_video);
                                uVideoView.setUp(videoUrl,true,"");
                                uVideoView.startPlayLogic();
                            }
                        }
                    }
                });
                customAlertDialog.setNegativeButton("取消", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        customAlertDialog.dismiss();
                        autoBrocastManager.setBroadcastEnable(true);
                        autoBrocastManager.loop();
                    }
                });
                isAutoPlay = true;
                autoBrocastManager.stop();
                customAlertDialog.show();
            }
        }
    }

    /**
     * 停止视频播放
     * @param position 当前position 传入则不停止当前视频  负值停止全部
     */
    public void stopPlayVideo(int position)
    {
        if (position <0 && autoBrocastManager != null && isAutoPlay)
        {
            autoBrocastManager.setBroadcastEnable(true);
            autoBrocastManager.loop();
        }
        if (videoViewMap != null)
        {
            for (Map.Entry<Integer, View> integerUVideoViewEntry : videoViewMap.entrySet())
            {
                if (position >= 0 && position == integerUVideoViewEntry.getKey())
                    continue;
                View view = integerUVideoViewEntry.getValue();
                if (view != null)
                {
                    FrameLayout thumbnailFl = (FrameLayout) view.findViewById(R.id.fl_thumbnail);
                    EmptyControlVideo uVideoView =  view.findViewById(R.id.uvideo_view);
                    ImageView startOrStopImg = (ImageView) view.findViewById(R.id.img_start_or_stop);
                    if (thumbnailFl != null && uVideoView != null && startOrStopImg != null)
                    {
                        thumbnailFl.setVisibility(View.VISIBLE);
                        uVideoView.setVisibility(View.INVISIBLE);
                        startOrStopImg.setTag(null);
                        if (uVideoView.getCurrentState() == GSYVideoView.CURRENT_STATE_PLAYING)
                            uVideoView.onVideoPause();
                        uVideoView.seekTo(0);
                        startOrStopImg.setImageResource(R.drawable.play_video);
                    }
                }
            }
        }
    }


    private final int REFRESH_PROGRESS = 101;
    private ProgressBar mProgressBar;
    private EmptyControlVideo uVideoView;
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case REFRESH_PROGRESS:
                    if (mProgressBar != null && uVideoView != null)
                    {
                        mProgressBar.setProgress(uVideoView.getPlayPosition());
                        sendEmptyMessageDelayed(REFRESH_PROGRESS, 100);
                    }
                    break;
            }
        }
    };

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.RestaurantC_detail:
                LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, true, 1, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (mPresenter != null)
                            mPresenter.getResDetailInfo(resId, INIT_COUNT);
                    }
                });
                break;
            default:
                LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, false, 0, null);
                break;
        }
    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, false, 0, null);
    }


    @Override
    public void refreshLoserComCallback(List<CommonUserCommentBean> response)
    {
        Logger.t(TAG).d(TAG + "response" + response);

        if (response.size() == 0)
        {
            viewVipTopLine.setVisibility(View.GONE);
            llVipComment.setVisibility(View.GONE);
            tvZanWu.setVisibility(View.VISIBLE);
            pullMove = false;
        }
        else
        {
            viewVipTopLine.setVisibility(View.VISIBLE);
            llVipComment.setVisibility(View.VISIBLE);
            tvZanWu.setVisibility(View.GONE);
            pullMove = true;
        }

        if (loserCommentLst.size() == 0)
        {
            Logger.t(TAG).d("没有数据,显示空数据默认图");
            viewVipTopLine.setVisibility(View.GONE);
            llVipComment.setVisibility(View.GONE);
        }
        else
        {
            viewVipTopLine.setVisibility(View.VISIBLE);
            llVipComment.setVisibility(View.GONE);
        }

        loserCommentLst.addAll(response);
        loserCommentAdapter.notifyDataSetChanged();
        if ( response.size() > 0)
        {
            new Handler().post(new Runnable()
            {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run()
                {
                    pullToRefreshScrollView.getRefreshableView().scrollBy(0,350);
                }
            });
        }

        if (pullToRefreshScrollView != null)
        {
            pullToRefreshScrollView.onRefreshComplete();
            if (pullMove)
            {
                Logger.t(TAG).d("允许上下拉动"); // 恢复上拉 上下拉动
                pullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
            }
            else
            {
                Logger.t(TAG).d("禁止上拉"); // 禁止上拉
                pullToRefreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
        }
    }

    @Override
    public void getResDetailInfoCallback(RestaurantBean rBean)
    {
        try
        {
            setResDetailInfo(rBean);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void getResDataOnCdn()
    {
        OkHttpUtils.get()
                .url(CdnHelper.CDN_ORIGINAL_SITE +CdnHelper.fileFolder+"public.json")
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        ToastUtils.showShort("读取失败");
                    }

                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject object = new JSONObject(response);
                            noteBean = new RestaurantNoteBean();
                            noteBean.setNoteContext(object.getString("refund_policy"));
                            if (expandCollapseView==null)
                                return;
                            expandCollapseView.setText(noteBean.getNoteContext(), noteBean.isCollapsed());
                            expandCollapseView.setListener(new ExpandCollapseView.OnExpandStateChangeListener()
                            {
                                @Override
                                public void onExpandStateChanged(boolean isExpanded)
                                {
                                    noteBean.setCollapsed(isExpanded);
                                }
                            });
                        } catch (JSONException e)
                        {
                            Logger.t(TAG).d(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
    }
}

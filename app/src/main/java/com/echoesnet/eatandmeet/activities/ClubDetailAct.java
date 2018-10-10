package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.ClubDetailBean;
import com.echoesnet.eatandmeet.models.bean.ShareToFaceBean;
import com.echoesnet.eatandmeet.models.datamodel.ImageDisposalType;
import com.echoesnet.eatandmeet.presenters.ImpIClubDetailPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IClubDetailView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.LoadingProgressUtil;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.ClubCommentAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.ImageIndicatorView.NetworkImageIndicatorView;
import com.echoesnet.eatandmeet.views.widgets.ResCallPhonePop;
import com.echoesnet.eatandmeet.views.widgets.SharePopopWindow.SharePopWindow;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.joanzapata.iconify.widget.IconTextView;
import com.linearlistview.LinearListView;
import com.orhanobut.logger.Logger;
import com.panxw.android.imageindicator.AutoPlayManager;
import com.panxw.android.imageindicator.ImageIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.grabner.circleprogress.CircleProgressView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.tencent.qq.QQClientNotExistException;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 * 轰趴ktv详细信息
 *
 * @author lc
 * @version 1.0
 * @modifier
 * @createDate 2018/2/5
 * @description
 */
public class ClubDetailAct extends MVPBaseActivity<ClubDetailAct, ImpIClubDetailPre> implements IClubDetailView
{
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;

    @BindView(R.id.rl_res_phone)
    RelativeLayout rlResPhone;
    @BindView(R.id.icv_cycle_view)
    NetworkImageIndicatorView icvCycleView;
    @BindView(R.id.loading_view)
    RelativeLayout rlLoadingView;
    @BindView(R.id.blackt50_bg)
    View vBgT50;
    @BindView(R.id.tv_introduce)
    TextView tvIntroduce;
    @BindView(R.id.tv_count_price)
    TextView tvCountPrice;
    @BindView(R.id.tv_introduce_text)
    TextView tvIntroduceText;
    @BindView(R.id.tv_fun_tool)
    TextView tvFunTool;
    @BindView(R.id.tv_fun_tool_text)
    TextView tvFunToolText;
    @BindView(R.id.tv_set_meal)
    TextView tvSetMeal;
    @BindView(R.id.tv_club_info)
    TextView tvClubInfo;
    @BindView(R.id.tv_res_phone)
    TextView tvResPhone;
    @BindView(R.id.all_call_phone)
    IconTextView allCallPhone;
    @BindView(R.id.tv_res_address)
    TextView tvResAddress;
    @BindView(R.id.all_res_address)
    IconTextView allResAddress;
    @BindView(R.id.tv_stop)
    TextView tvStop;
    @BindView(R.id.view_left)
    View viewLeft;
    @BindView(R.id.view_right)
    View viewRight;
    @BindView(R.id.rl_user_hint)
    RelativeLayout rlUserHint;
    @BindView(R.id.rv_comment)
    LinearListView rvComment;
    @BindView(R.id.tv_zanwu)
    TextView tvZanwu;
    @BindView(R.id.prs_res_scrollview)
    PullToRefreshScrollView prsResScrollview;
    @BindView(R.id.pb_circle)
    CircleProgressView pbCircle;
    @BindView(R.id.rlPopCover)
    RelativeLayout rlPopCover;
    @BindView(R.id.ll_package)
    LinearLayout llPackage;
    @BindView(R.id.v_bg)
    View vBg;
    @BindView(R.id.define_error)
    ImageView defineError;
    @BindView(R.id.tv_load_des)
    TextView tvLoadDes;
    @BindView(R.id.main)
    RelativeLayout main;
    @BindView(R.id.tv_go_to_order)
    TextView tvGoToOrder;
    private static final String TAG = ClubDetailAct.class.getSimpleName();
    private static final String DEAFULT_NUM = "10";
    private Activity mAct;
    private int position;
    public List<Map<String, TextView>> navBtns;
    private ClubDetailBean cBean;
    private String clubName;
    private String clubPic;
    private String clubId;
    private String clubAdress;
    private String phoneArray[];
    private boolean pullMove = true;       // 没有更多数据获取时,禁止列表上拉加载动作
    private ClubCommentAdapter adapter;
    private TextView tvTitle;
    private AutoPlayManager autoBrocastManager;
    private boolean phoneFlag = false;
    private PopupWindow popupWindow;
    private ResCallPhonePop resCallPhonePop;
    private SharePopWindow sharePopWindow;
    private List<ClubDetailBean.CommentsBean> commentList = new ArrayList<>();
    private List<String> packageText = new ArrayList<>();
    private String[] location;
    private static final int START_COLUMN_SHARE = 100;
    private boolean isPullDataed = false;
    private boolean isCollect = true;


    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.private_detail_act);
        ButterKnife.bind(this);
        initView();
    }

    private void initView()
    {
        mAct = this;
        clubId = getIntent().getStringExtra("clubId");
        initTopBar();
        initClubDetail();
    }

    private void initClubDetail()
    {
        LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, true, 0, null);
        if (mPresenter != null)
            mPresenter.getClubDetail(clubId,false);
        prsResScrollview.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        prsResScrollview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>()
        {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView)
            {
                if (mPresenter != null)
                    mPresenter.getPartyComment(DEAFULT_NUM, commentList.size() + "", clubId, "add");
            }
        });

        changeUIShow();

        adapter = new ClubCommentAdapter(mAct, commentList);

        rvComment.setAdapter(adapter);
    }

    public void changeUIShow()
    {
        if (commentList.size() == 0)
        {
            //增加新代码
            Logger.t(TAG).d("没有数据,显示空数据默认图");
            pullMove = false;
        } else
        {
            tvZanwu.setVisibility(View.GONE);
            pullMove = true;
        }
    }

    private void initTopBar()
    {
        tvTitle = topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                if (dismissShareWin() == false)
                    return;
                Intent data = new Intent();
                data.putExtra("isCollect", isCollect);
                setResult(RESULT_OK, data);
                finish();
            }

            @Override
            public void rightClick(View view)
            {
                //收藏
                if (navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).getTag().equals("0"))
                {
                    if (mPresenter != null)
                        mPresenter.collectedClub(clubId);
                } else//取消收藏
                {
                    if (mPresenter != null)
                        mPresenter.removeClub(clubId);
                }
            }

            @Override
            public void right2Click(View view)
            {
                if (isPullDataed)
                {
                    initSharePopWindow(view);
                }
                else
                {
                    ToastUtils.showShort("正在努力加载分享信息，请等一下下~");
                }

            }
        });
        tvTitle.setText(clubName);
        tvTitle.setMaxLines(1);
        tvTitle.setMaxWidth(CommonUtils.dp2px(mAct,200));
        tvTitle.setTypeface(null, Typeface.BOLD);

        navBtns = topBarSwitch.getNavBtns2(new int[]{1, 0, 1, 1});
        for (int i = 0; i < navBtns.size(); i++)
        {
            Map<String, TextView> map = navBtns.get(i);
            TextView tv = map.get(TopBarSwitch.NAV_BTN_ICON);
            switch (i)
            {
                case 0:
                    break;
                case 1:
                    tv.setText("{eam-s-star3}");
                    tv.setTag("0");
                    tv.setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
                    break;
                case 2:
                    //  tv.setText("{eam-e98a}");
                    tv.setText("{eam-e993}");
                    tv.setMaxWidth(20);
                    tv.setMinWidth(17);
                    tv.setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed()
    {

        if (dismissShareWin() == false)
            return;
      //  super.onBackPressed();
        Intent data = new Intent();
        data.putExtra("isCollect", isCollect);
        setResult(RESULT_OK, data);
        finish();

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (sharePopWindow != null)
        {
            sharePopWindow = null;
        }
    }

    private void initSharePopWindow(View view)
    {
        ShareToFaceBean bean = new ShareToFaceBean();
        bean.setShareTitleUrl(NetHelper.SHARE_HP_ADDRESS +clubId);
        bean.setShareUrl(NetHelper.SHARE_HP_ADDRESS +clubId);
        bean.setShareSiteUrl(NetHelper.SHARE_HP_ADDRESS +clubId);
        bean.setShareTitle("");
        bean.setShareAppImageUrl(clubPic);
        bean.setShareType(Platform.SHARE_WEBPAGE);
        bean.setShareSite("看脸吃饭");
        bean.setOpenSouse("party");// 传递房间参数到看脸好友界面
        bean.setClubId(clubId);
        bean.setShareImgUrl(clubPic);
        bean.setShareContent("有吃有喝有快乐，就选【看脸吃饭】" +clubName );
        bean.setMessageDes("有吃有喝有快乐，就选【看脸吃饭】" + clubName);
        bean.setShareWeChatMomentsTitle("有吃有喝有快乐，就选看脸吃饭" +clubName);
        bean.setShareSinaContent("有吃有喝有快乐，就选看脸吃饭" +clubName+NetHelper.SHARE_HP_ADDRESS +clubId);

        bean.setShareListener(new PlatformActionListener()
        {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap)
            {
                mAct.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ToastUtils.showShort("分享成功");

                    }
                });
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable)
            {
                Logger.t(TAG).d(">>>>>>>>>分享失败" + i + ">>" + throwable.getMessage());
                throwable.printStackTrace();
                if (throwable instanceof QQClientNotExistException)
                {
                    mAct.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ToastUtils.showLong("请安装QQ客户端");
                        }
                    });
                } else
                {
                    mAct.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ToastUtils.showShort("分享失败");
                        }
                    });
                }
            }

            @Override
            public void onCancel(Platform platform, int i)
            {

                mAct.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ToastUtils.showShort("分享取消");
                    }
                });
            }
        });


        sharePopWindow = new SharePopWindow(mAct, new int[]
                {
                        SharePopWindow.SHARE_WAY_DYNAMIC,
                        SharePopWindow.SHARE_WAY_APPFRIEND,
                        SharePopWindow.SHARE_WAY_WECHAT_FRIEND,
                        SharePopWindow.SHARE_WAY_QQ_FRIEND,
                        SharePopWindow.SHARE_WAY_QZONE,
                        SharePopWindow.SHARE_WAY_WECHAT_MOMENT,
                        SharePopWindow.SHARE_WAY_SINA}, bean);
        sharePopWindow.setPopupTitle("分享你喜欢的沙龙~");
        sharePopWindow.setShareItemClickListener(new SharePopWindow.ShareItemClickListener()
        {
            @Override
            public void onItemCLick(int position, String shareKey)
            {
                switch (shareKey)
                {
                    case "我的动态":
                        Intent intent = new Intent(mAct, ShareColumnArticleAct.class);
                        intent.putExtra("imgUrl", clubPic);
                        intent.putExtra("content", "有吃有喝有快乐，就选看脸吃饭" + clubName);
                        intent.putExtra("shareType", "club");
                        intent.putExtra("clubContent", "有吃有喝有快乐，就选看脸吃饭" + clubName);
                        intent.putExtra("clubId", clubId);
                        startActivityForResult(intent,START_COLUMN_SHARE);
                        break;
                }
            }
        });
        sharePopWindow.showPopupWindow(view, rlPopCover);
    }

    private void initShowReportPopupWindow(View anchorView)
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        Rect mRect = new Rect();
        int[] mLocation = new int[2];
        anchorView.getLocationOnScreen(mLocation);
        View mView = inflater.inflate(R.layout.popup_repost, null);
        popupWindow = new PopupWindow(mView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        backgroundAlpha(true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        mRect.set(mLocation[0], mLocation[1], mLocation[0] + mView.getWidth(), mLocation[1] + mView.getHeight());
        popupWindow.showAtLocation(anchorView, Gravity.TOP | Gravity.RIGHT, 6, mRect.bottom + 95);
        RelativeLayout relativeLayout = (RelativeLayout) mView.findViewById(R.id.ll_report);
        IconTextView itvReport = (IconTextView) mView.findViewById(R.id.itv_report_restaurant);
        itvReport.setOnClickListener((v) ->
        {
            popupWindow.dismiss();
            Intent reportIntent = new Intent(mAct, ReportFoulsResrAct.class);
            reportIntent.putExtra("rId", clubId);
            startActivity(reportIntent);
        });
        relativeLayout.setOnClickListener((v) ->
        {
            if (popupWindow.isShowing())
            {
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupDismissListener());
    }


    /**
     * 分享窗口消失
     */
    public boolean dismissShareWin()
    {
        boolean isShowing = true;
        if (sharePopWindow != null && sharePopWindow.isShowing())
        {
            sharePopWindow.dismiss();
            isShowing = false;
        }
        return isShowing;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
       // super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case START_COLUMN_SHARE:
            case EamConstant.EAM_OPEN_RELATION:
                if (data!=null)
                {
                    Boolean isShare = data.getBooleanExtra("isShare",false);
                    if (!isShare)
                    {
                        ToastUtils.showShort("分享失败");
                    }
                }
                break;
                default:
                    break;

        }
    }

    @Override
    protected ImpIClubDetailPre createPresenter()
    {
        return new ImpIClubDetailPre();
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String errorCode, String errorBody)
    {
        switch (interfaceName)
        {

            case NetInterfaceConstant.HomepartyC_partyDetails:
                if (ErrorCodeTable.HOMEPARTY_OFFLINE.equals(errorCode))
                {
                    new CustomAlertDialog(mAct)
                            .builder()
                            .setTitle("提示")
                            .setMsg("该沙龙已下线")
                            .setPositiveButton("确定", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .show();
                }
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
        switch (interfaceName)
        {
            case NetInterfaceConstant.HomepartyC_partyDetails:
                LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, true, 1, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (mPresenter != null)
                            mPresenter.getClubDetail(clubId,false);
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void lookClubDetailCallBack(String response)
    {
        Logger.t(TAG).d("会所详情返回参数》》--> " + response);
        LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, false, 0, null);

        cBean = EamApplication.getInstance().getGsonInstance().fromJson(response, ClubDetailBean.class);

        if (cBean == null)
            return;
        //-------------------------------更新ui-----------------------------
        tvTitle.setText(cBean.getName());
        clubName = cBean.getName();
        tvIntroduce.setText("沙龙介绍");
        initPackagPicView(cBean.getPackageUrl());//更新套餐图
        initHeadPicView(cBean.getUrls());//更新轮播图
        tvIntroduceText.setText(cBean.getIntroduce());
        if (cBean.getUrls().size()>0)
        {
            clubPic = cBean.getUrls().get(0);
        }
        clubAdress = cBean.getAddress();
        if (cBean.getMobile().length() > 1)
        {
            phoneFlag = true;
        }
        phoneArray = new String[1];
        phoneArray[0] = cBean.getMobile();
        location = new String[]{cBean.getPosx(),cBean.getPosy()};
        tvFunToolText.setText(cBean.getItem());
        tvCountPrice.setText(cBean.getPerPrice());
        tvResAddress.setText(cBean.getAddress());
        tvResPhone.setText("联系电话：" + cBean.getMobile());
        collectState("1".equals(cBean.getCollect()) ? true : false);
        isPullDataed = true;
        if (cBean.getComments()!=null && cBean.getComments().size() > 0)
        {
            rlUserHint.setVisibility(View.VISIBLE);
            commentList.addAll(cBean.getComments());
            adapter.notifyDataSetChanged();
        }
    }

    private void initPackagPicView(List<ClubDetailBean.PackagesPicBean> packageUrl)
    {
        for (int i = 0; i < packageUrl.size(); i++)
        {
            LinearLayout llrow = new LinearLayout(mAct);
            llrow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            llrow.setOrientation(LinearLayout.VERTICAL);
            TextView tvPackageText = new TextView(mAct);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1.setMargins(0, CommonUtils.dp2px(mAct, 13), 0, 0);
            tvPackageText.setLayoutParams(layoutParams1);
            tvPackageText.setText(packageUrl.get(i).getName());
            llrow.addView(tvPackageText);

            for (int j = 0;j<packageUrl.get(i).getUrl().size();j++)
            {
                ImageView ivPackagePic = new ImageView(mAct);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CommonUtils.dp2px(mAct, 120));
                layoutParams.setMargins(0, CommonUtils.dp2px(mAct, 13), 0, 0);
                ivPackagePic.setLayoutParams(layoutParams);
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(packageUrl.get(i).getUrl().get(j))
                        .centerCrop()
                        .placeholder(R.drawable.qs_banner)
                        .error(R.drawable.qs_banner)
                        .into(ivPackagePic);
                llrow.addView(ivPackagePic);
            }

            llPackage.addView(llrow);
        }
    }

    @Override
    public void collectedClubCallback(String response)
    {
        collectState(true);
        isCollect = true;
        ToastUtils.showShort("收藏成功");
    }

    @Override
    public void removeClubCallback(String response)
    {
        collectState(false);
        isCollect = false;
        ToastUtils.showShort("取消收藏成功");
    }

    @Override
    public void getClubCommentCallback(List<ClubDetailBean.CommentsBean> response, String operateType)
    {
        try
        {
            Logger.t(TAG).d("获得评论详情成功--> " + response);

            if (response == null)
            {
                ToastUtils.showShort("获取评论详情失败");
            } else
            {
                Logger.t(TAG).d("评论详情数量--> " + response.size());
                if (response.size() == 0)
                {
                    pullMove = false;
                    tvZanwu.setVisibility(View.VISIBLE);
                    if (commentList.size() > 0)
                    {
                        rlUserHint.setVisibility(View.VISIBLE);
                    }
                } else
                {
                    pullMove = true;
                    tvZanwu.setVisibility(View.GONE);
                    rlUserHint.setVisibility(View.VISIBLE);
                }
                // 下拉刷新
                if (operateType.equals("refresh"))
                {
                    pullMove = true;
                }
                // 添加去重复 ====
    //            for (ClubDetailBean.CommentsBean restaurantBean : response)
    //            {
    //                if (commentList.contains(restaurantBean))
    //                {
    //                    int index = commentList.indexOf(restaurantBean);
    //                    commentList.remove(index);
    //                }
    //                commentList.add(restaurantBean);
    //            }
                commentList.addAll(response);
                adapter.notifyDataSetChanged();

                if (commentList.size() == 0)
                {
                    pullMove = true;
                }
                if ( response.size() > 0)
                {
                    new Handler().post(new Runnable()
                    {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void run()
                        {
                            prsResScrollview.getRefreshableView().scrollBy(0,350);
                        }
                    });
                }
            }

            if (prsResScrollview != null)
            {
                prsResScrollview.onRefreshComplete();
                if (pullMove)
                {
                    Logger.t(TAG).d("允许上下拉动"); // 恢复上拉 上下拉动
                    prsResScrollview.setMode(PullToRefreshBase.Mode.BOTH);
                } else
                {
                    Logger.t(TAG).d("禁止上拉"); // 禁止上拉
                    prsResScrollview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("异常信息>> " + e.getMessage());
        }

    }

    public void backgroundAlpha(boolean bgAlpha)
    {
        if (bgAlpha)
            vBgT50.setVisibility(View.VISIBLE);
        else
            vBgT50.setVisibility(View.GONE);
    }

    class PopupDismissListener implements PopupWindow.OnDismissListener
    {

        @Override
        public void onDismiss()
        {
            backgroundAlpha(false);
        }
    }

    @OnClick({R.id.tv_go_to_order, R.id.all_res_address, R.id.all_call_phone})
    void viewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tv_go_to_order://立即预订
                if (mPresenter!=null)
                {
                    mPresenter.getClubDetail(clubId,true);
                }
                Intent intent = new Intent(mAct, ClubInfoAct.class);
                intent.putExtra("clubId", clubId);
                startActivity(intent);
                break;
            case R.id.all_res_address://地址
                Intent intentM = new Intent(mAct, DResAddressMapAct.class);
                intentM.putExtra("location", location);
                intentM.putExtra("resName", cBean.getName());
                intentM.putExtra("resAddress", clubAdress);
                intentM.putExtra("resType", "club");
                mAct.startActivity(intentM);
                break;
            case R.id.all_call_phone://打电话
                if (phoneFlag)
                {
                    resCall();
                } else
                {
                    CommonUtils.makeCall(mAct, cBean.getMobile().toString());
                }
                break;
            default:
                break;
        }
    }

    private void resCall()
    {
        resCallPhonePop = new ResCallPhonePop(mAct, ietmOnclick, phoneArray);

        resCallPhonePop.showPopupWindow(rlResPhone, rlPopCover);
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
                    CommonUtils.makeCall(mAct, tv.getText().toString());
            }
        }
    };

    private void initHeadPicView(List<String> response)
    {
        List<String> urlList = new ArrayList<>();
        List<String> copyUrlList = new ArrayList<>();
        for (int i = 0; i < response.size(); i++)
        {
            // 使用网络加载图片
            String urlByUCloud = CommonUtils.getThumbnailImageUrlByUCloud(response.get(i), ImageDisposalType.THUMBNAIL, 7, 750, 430);
            urlList.add(urlByUCloud);
            copyUrlList.add(urlByUCloud);
        }
        icvCycleView.setShowIndicator(true);
        icvCycleView.setupLayoutByImageUrl(urlList);
        icvCycleView.show();
        icvCycleView.setOnItemClickListener(new ImageIndicatorView.OnItemClickListener()
        {
            @Override
            public void OnItemClick(View view, int position)
            {

                CommonUtils.showImageBrowser(mAct, copyUrlList, position, view);
            }
        });

        autoBrocastManager = new AutoPlayManager(icvCycleView);
        autoBrocastManager.setBroadcastEnable(true);
        autoBrocastManager.setBroadCastTimes(10000);//loop times
        autoBrocastManager.setBroadcastTimeIntevel(2 * 1000, 2 * 1000);//设置第一次展示时间以及间隔，间隔不能小于1秒
        autoBrocastManager.loop();
        icvCycleView.showPageCountView();
    }

    private void collectState(boolean isCollect)
    {
        if (isCollect)
        {
            navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).setTag("1");
            navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).setText("{eam-s-star}");
            navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
        } else
        {
            navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).setTag("0");
            navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).setText("{eam-s-star3}");
            navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
        }
    }
}

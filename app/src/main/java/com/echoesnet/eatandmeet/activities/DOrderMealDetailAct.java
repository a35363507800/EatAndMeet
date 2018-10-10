package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.fragments.DishFrg;
import com.echoesnet.eatandmeet.fragments.RestaurantFrg;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.OrderBean;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.presenters.ImpDOrderMealDetailView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDOrderMealDetailView;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.LoadingProgressUtil;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.OrderDinnerPagerAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.NoScrollViewPager;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class DOrderMealDetailAct extends MVPBaseActivity<DOrderMealDetailAct, ImpDOrderMealDetailView> implements IDOrderMealDetailView
{
    final static String TAG = DOrderMealDetailAct.class.getSimpleName();
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.vp_order_detail)
    NoScrollViewPager mViewPager;
    @BindView(R.id.loading_view)
    RelativeLayout rlLoadingView;
    @BindView(R.id.blackt50_bg)
    View vBgT50;

    private String bootyCallDate;//约吃饭日期
    private String resId, resName;
    private Map<String, Object> resDataMap = new HashMap<>();
    private String[] location;
    private String lessPrice;
    private String source;  // 跳转方向来源(从PayOrderSuccessAct过来)
    private Activity mContext;
    //    public List<TextView> navBtns;
    public List<Map<String, TextView>> navBtns;
    private int index;
    private int pagePosition;
    private RestaurantFrg resFrg;
    private boolean isOpenSingleTask = false;
    private List<Fragment> fragments = new ArrayList<>();
    private PagerAdapter mPagerAdapter;
    private boolean isCollect = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_order_meal_detail);
        ButterKnife.bind(this);
        initAfterViews();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        Logger.t(TAG).d("onResume>>" + getIntent().getIntExtra("index", 0));
        int index = getIntent().getIntExtra("index", 0);
        mViewPager.setCurrentItem(index);
    }

    //一定要调用
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    void initAfterViews()
    {
        mContext = this;
        bootyCallDate = getIntent().getStringExtra("bootyCallDate");
        topBarSwitch.inflateSwitchBtns(new ArrayList<String>(Arrays.asList("餐厅", "点菜")), 0,
                new TopbarSwitchSkeleton()
                {
                    @Override
                    public void leftClick(View view)
                    {
                        Intent data = new Intent();
                        data.putExtra("isCollect", isCollect);
                        setResult(RESULT_OK, data);
                        if (!TextUtils.isEmpty(source) && source.equals("find"))
                        {
                            Logger.t(TAG).d("==========从发现页下单========" + source);
                            Intent intent = new Intent(mContext, HomeAct.class);
                            intent.putExtra("showPage", 0);
                            mContext.startActivity(intent);
                        }
//                        else if (SharePreUtils.getToOrderMeal(mContext).equals("toOrderMeal"))
//                        {
//                            Logger.t(TAG).d("==========跳转约会详情改为==========> " + SharePreUtils.getToOrderMeal(mContext) + " , ==> " + EamApplication.getInstance().dateStreamId);
//                            Intent intent = CopyOrderMealAct_.intent(mContext).get();
//                            startActivity(intent);
//                            finish();
//                        }
                        else if (!TextUtils.isEmpty(source) && source.equals("orderMeal"))
                        {
                            Logger.t(TAG).d("==========从餐厅列表下单========" + source);
                            Intent intent = new Intent(mContext, HomeAct.class);
                            intent.putExtra("showPage", 2);
                            // 普通下单设置为noDate， 约会下单为streamId
                            EamApplication.getInstance().dateStreamId = "noDate";
                            mContext.startActivity(intent);
                        } else if (!TextUtils.isEmpty(source) && source.equals("unPay"))
                        {
                            Logger.t(TAG).d("==========从我的待支付付款========" + source);
                            Intent intent = new Intent(mContext, HomeAct.class);
                            intent.putExtra("showPage", 3);
                            mContext.startActivity(intent);
                        } else if (!TextUtils.isEmpty(source) && source.equals("myColloect"))
                        {
                            Logger.t(TAG).d("==========从我的收藏付款========" + source);
                            finish();
                        } else if (!TextUtils.isEmpty(source) && source.equals("resSource"))
                        {
                            Logger.t(TAG).d("==========从搜索餐厅列表========" + source);
                            finish();
                        } else if (!TextUtils.isEmpty(source) && source.equals("promotion"))
                        {
                            //活动也过来
                            finish();
                        } else
                        {
                            // 普通下单设置为noDate， 约会下单为streamId
//                            EamApplication.getInstance().dateStreamId = "noDate";
                            Logger.t(TAG).d("==========其他2========" + source);
                            finish();
                            overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
                        }
                    }

                    @Override
                    public void right2Click(View view)
                    {
                        if (pagePosition == 0)
                            initShowReportPopupWindow(view);
                    }

                    @Override
                    public void rightClick(View view)
                    {
                        if (pagePosition == 0)
                        {
                            //收藏
                            if (navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).getTag().equals("0"))
                            {
                                if (mPresenter != null)
                                    mPresenter.collectedRest(resId);
                            }
                            //取消收藏
                            else
                            {
                                if (mPresenter != null)
                                    mPresenter.removeRest(resId);
                            }
                        }
                    }

                    @Override
                    public void switchBtn(View view, int position)
                    {
                        Logger.t("==========").d(position + "");
                        //不知为什么 页面进来 这个方法就直接调用 然后 navBtns 在switchPage 里就 为空 暂时 这么解决  -----------yqh
                        navBtns = topBarSwitch.getNavBtns2(new int[]{1, 0, 1, 1});
                        switchPage(position);
                        pagePosition = position;
                    }
                });
        navBtns = topBarSwitch.getNavBtns2(new int[]{1, 0, 1, 1});
        Logger.t(TAG).d("navBtns:" + Arrays.toString(navBtns.toArray()));
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
                    tv.setTextColor(ContextCompat.getColor(mContext, R.color.C0412));
                    break;
                case 2:
                    tv.setText("{eam-e609}");
                    tv.setMaxWidth(20);
                    tv.setMinWidth(20);
                    tv.setTextColor(ContextCompat.getColor(mContext, R.color.C0412));
                    break;
                default:
                    break;
            }
        }
        // 修改从支付成功返回 (返回时拿不到resId)
        resId = getIntent().getStringExtra("restId");
        source = getIntent().getStringExtra("source");
        if (SharePreUtils.getToOrderMeal(mContext).equals("toOrderMeal"))
        {
            Logger.t(TAG).d("约会下单--> " + EamApplication.getInstance().dateStreamId);
        } else
        {
            EamApplication.getInstance().dateStreamId = "noDate";
            Logger.t(TAG).d("普通下单--> " + EamApplication.getInstance().dateStreamId);
        }

        showNewbieGuide();
        if (mPresenter != null)
        {
            LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, true, 0, null);
            mPresenter.getRestInfo(resId);
        }




    }

    /**
     * @Description: 显示新手引导
     */
    private void showNewbieGuide()
    {
        if (SharePreUtils.getIsNewDinnerDetail(mContext))
        {
            NetHelper.checkIsShowNewbie(mContext, "9", new ICommonOperateListener()
            {
                @Override
                public void onSuccess(String response)
                {
                    if ("0".equals(response))
                    {
                        //获取root节点
                        final FrameLayout fRoot = (FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);
                        final View vGuide = View.inflate(mContext, R.layout.collect_guide, null);

                        final ImageView imgOrder1 = (ImageView) vGuide.findViewById(R.id.guide_1);
                        final ImageView imgOrder2 = (ImageView) vGuide.findViewById(R.id.guide_2);
                        final TextView tvClickDismiss = (TextView) vGuide.findViewById(R.id.tv_click_dismiss);


                        vGuide.setClickable(true);


                        tvClickDismiss.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                fRoot.removeView(vGuide);
                                SharePreUtils.setIsNewDinnerDetail(mContext, false);
                                NetHelper.saveShowNewbieStatus(mContext, "9");
                            }
                        });
                        fRoot.addView(vGuide);
                    } else
                    {
                        SharePreUtils.setIsNewDinnerDetail(mContext, false);
                    }
                }

                @Override
                public void onError(String code, String msg)
                {

                }
            });

        }
    }

    private void switchPage(int position)
    {
        if (position == 0)
            selectRes(mContext);
        else
            selectDish(mContext);
    }

    private void initViewPager()
    {
        if (isOpenSingleTask)
        {
            //解决viewpage缓存页面问题
            fragments.clear();
            mViewPager.removeAllViews();
            isOpenSingleTask = false;
            //解决viewpage缓存页面问题
            mPagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager())
            {
                @Override
                public int getCount()
                {
                    return fragments.size();
                }

                @Override
                public Fragment getItem(int arg0)
                {
                    return fragments.get(arg0);
                }

                @Override
                public int getItemPosition(Object object)
                {
                    return PagerAdapter.POSITION_NONE;
                }
            };
        }
        else
        {
            mPagerAdapter = new OrderDinnerPagerAdapter(getSupportFragmentManager(), fragments);
        }

        resFrg = RestaurantFrg.newInstance(resId, (ArrayList<HashMap<String, String>>) resDataMap.get("starEva")
                , location, bootyCallDate);
        DishFrg dishFrg = DishFrg.newInstance(resId, (ArrayList<String>) resDataMap.get("dishType"), resName, lessPrice, bootyCallDate);
        fragments.add(resFrg);
        fragments.add(dishFrg);

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setNoScroll(false);
        mViewPager.addOnPageChangeListener(mPagerChangeListener);
        index = getIntent().getIntExtra("index", 0);
        mViewPager.setCurrentItem(index);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        if (intent != null)
        {
            setIntent(intent);
        }

        //获取最新的intent
        resId = getIntent().getStringExtra("restId");
        boolean fromFood ;
        fromFood = getIntent().getBooleanExtra("fromFoodDetail",false);
        Logger.t(TAG).d("onNewIntent>>"+resId);
        isOpenSingleTask = true;
        if (!fromFood)
        {
            if (mPresenter != null)
            {
                LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, true, 0, null);
                mPresenter.getRestInfo(resId);
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Logger.t(TAG).d("请求码：" + requestCode + "结果码：" + resultCode);
        switch (resultCode)
        {
            case EamConstant.EAM_RESULT_NO:
                switch (requestCode)
                {
                    case EamConstant.EAM_CONFIRM_ORDER_REQUEST_CODE:
                        if (data != null)
                        {
                            String result = data.getStringExtra("result");
                            if (result != null && result.equals("back"))
                            {
                            }
                        }
                        break;
                    case EamCode4Result.reQ_SelectTableActivity:
                        if (!mContext.isFinishing())
                            //   mContext.finish();
                            mViewPager.setCurrentItem(index);
                        break;
                }
            default:
                break;
        }

    }

    /**
     * 获取菜品分类和大咖评价
     *
     * @param resId
     */
    private void getResDataOnCdn(final String resId)
    {
        //region 注释代码
        /***************暂时保留，防止以后做缓存*************************
         try
         {
         *//*            //暂时加快速度，以后需要根据后台数据判断是否有更新
            File file = new File(NetHelper.getRootDirPath(mContext) + resId + ".json");
            //Logger.t(TAG).d("文件路径："+NetHelper.getRootDirPath(mContext)+resId + ".json");
            //如果需要更新则删除文件重新下载
            if (EamApplication.getInstance().resJsonDataMap != null &&
                    CommonUtils.strWithSeparatorToList(EamApplication.getInstance().resJsonDataMap.get(resId), CommonUtils.SEPARATOR)
                            .contains(resId + ".json"))
            {
                if (file.exists())
                {
                    Logger.t(TAG).d("文件更新了");
                    file.delete();
                }
            }

            if (file.exists())
            {
                Logger.t(TAG).d(file.getAbsolutePath());
                parseResInfoFromJson(file);
            }
            else*//*
            {
                if (pDialog!=null&&!pDialog.isShowing())
                    pDialog.show();
                Logger.t(TAG).d("请求参数为》" + CdnHelper.CDN_ORIGINAL_SITE + CdnHelper.fileFolder + resId + ".json");
                OkHttpUtils.get()
                        .url(CdnHelper.CDN_ORIGINAL_SITE + CdnHelper.fileFolder + resId + ".json")
                        .build()
                        .execute(new FileCallBack(NetHelper.getRootDirPath(mContext), resId + ".json")
                        {
                            @Override
                            public void onError(Call call, Exception e)
                            {
                                NetHelper.handleNetError(mContext,null,TAG,e);
                            }

                            @Override
                            public void onResponse(File response)
                            {
                                Logger.t(TAG).d("json文件："+response);
                                parseResInfoFromJson(response);
                            }

                            @Override
                            public void inProgress(float progress, long total)
                            {

                            }
                        });
            }
        } catch (Exception e)
        {
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }*/
        //endregion

        LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, true, 0, null);
        Logger.t(TAG).d("请求参数为》" + CdnHelper.CDN_ORIGINAL_SITE + CdnHelper.fileFolder + resId + ".json");
        OkHttpUtils.get()
                .url(CdnHelper.CDN_ORIGINAL_SITE + CdnHelper.fileFolder + resId + ".json")
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, false, 1, new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                getResDataOnCdn(SharePreUtils.getRestId(mContext));
                            }
                        });
                        NetHelper.handleNetError(mContext, null, TAG, e);
                    }

                    @Override
                    public void onResponse(String response)
                    {
                        Logger.t(TAG).d("json文件：" + response);
                        parseResInfoFromJson(response);
                    }
                });
    }

    private void parseResInfoFromJson(String jsonStr)
    {
        try
        {
            Logger.t(TAG).d("result:" + jsonStr);
            JSONObject jObj = new JSONObject(jsonStr);
            //JSONArray dishTypes = jObj.getJSONArray("dishType");
            List<String> dishLst = new ArrayList<String>();
//            for (int i = 0; i < dishTypes.length(); i++)
//            {
//                dishLst.add(dishTypes.getString(i));
//            }
            resDataMap.put("dishType", dishLst);
            JSONArray bigV = jObj.getJSONArray("starEva");
            ArrayList<HashMap<String, String>> bigVLst = new ArrayList<>();
            for (int i = 0; i < bigV.length(); i++)
            {
                HashMap<String, String> map = new HashMap<String, String>();
                JSONObject b = bigV.getJSONObject(i);
                map.put("uId", b.getString("uId"));
                map.put("nicName", b.getString("nicName"));
                map.put("uphUrl", b.getString("uphUrl"));
                map.put("detail", b.getString("detail"));
                map.put("rStar", b.getString("rStar"));
                map.put("evaOccupation", b.getString("evaOccupation"));
                map.put("epUrls", b.getString("epUrls"));
                map.put("pAmount", b.getString("pAmount"));
                map.put("level", b.optString("level", "0"));
                map.put("sex", b.optString("sex", "男"));
                bigVLst.add(map);
            }
            resDataMap.put("starEva", bigVLst);
            initViewPager();
            Logger.t(TAG).d(resDataMap.toString());
        } catch (JSONException e)
        {
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
        } finally
        {
            LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, false, 0, null);
        }
    }

    private void selectRes(Context context)
    {
//        if (navBtns!=null)
//        {
        ((View) navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).getParent()).setVisibility(View.VISIBLE);
        ((View) navBtns.get(2).get(TopBarSwitch.NAV_BTN_ICON).getParent()).setVisibility(View.VISIBLE);
        mViewPager.setCurrentItem(0);
        //     }

    }

    private void selectDish(Context context)
    {
//        if (navBtns!=null)
//        {
        ((View) navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).getParent()).setVisibility(View.GONE);
        ((View) navBtns.get(2).get(TopBarSwitch.NAV_BTN_ICON).getParent()).setVisibility(View.GONE);
        mViewPager.setCurrentItem(1);
        //    }
    }

    private ViewPager.OnPageChangeListener mPagerChangeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
        }

        @Override
        public void onPageSelected(int position)
        {
            if (position == 0)
                selectRes(DOrderMealDetailAct.this);
            else
            {
                selectDish(DOrderMealDetailAct.this);
                if (resFrg != null)
                    resFrg.stopPlayVideo(-1);
            }

            topBarSwitch.changeSwitchBtn(position);
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {

        }
    };

    @Override
    protected ImpDOrderMealDetailView createPresenter()
    {
        return new ImpDOrderMealDetailView(this, this);
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.RestaurantC_resInfoByrId:
                if ("RSTATUS_ERROR".equals(code))
                {
                    new CustomAlertDialog(mContext)
                            .builder()
                            .setTitle("提示")
                            .setMsg("该餐厅已下架")
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
                break;
        }
    }

    @Override
    public void requestNetError(Throwable api, String interfaceName)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.RestaurantC_resInfoByrId:
                LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, true, 1, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (mPresenter != null)
                            mPresenter.getRestInfo(resId);
                    }
                });
                break;
            default:
                LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, false, 0, null);
                break;
        }

    }

    @Override
    public void collectedRestCallback(String response)
    {
        Logger.t(TAG).d("收藏成功--> " + response);
        isCollect = true;
        navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).setTag("1");
        navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).setText("{eam-s-star}");
        navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).setTextColor(ContextCompat.getColor(mContext, R.color.C0412));
        ToastUtils.showShort("收藏成功");
        LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, false, 0, null);
    }

    @Override
    public void removeRestCallback(String response)
    {
        Logger.t(TAG).d("取消收藏返回--> " + response);
        isCollect = false;
        navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).setTag("0");
        navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).setText("{eam-s-star3}");
        navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON).setTextColor(ContextCompat.getColor(mContext, R.color.C0412));
        ToastUtils.showShort("取消收藏成功");
        LoadingProgressUtil.showWithErrorLoadingView(rlLoadingView, false, 0, null);

    }

    @Override
    public void getRestInfoCallBack(String response)
    {
        try
        {
            JSONObject body = new JSONObject(response);
            resName = body.getString("rName");
            String posxy = body.getString("posxy");
            location = posxy.split(",");
            lessPrice = body.getString("lessPrice");
            SharePreUtils.setRestId(mContext, resId);
            SharePreUtils.setResName(mContext, resName);
            getResDataOnCdn(resId);

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
//            if (SharePreUtils.getToOrderMeal(mContext).equals("toOrderMeal"))
//            {
//                Logger.t(TAG).d("==========返回键执行跳转约会详情改为==========> " + SharePreUtils.getToOrderMeal(mContext) + " , ==> " + EamApplication.getInstance().dateStreamId);
//                Intent intent = CopyOrderMealAct_.intent(mContext).get();
//                startActivity(intent);
            Intent data = new Intent();
            data.putExtra("isCollect", isCollect);
            setResult(RESULT_OK, data);
            finish();
            overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
//            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private PopupWindow popupWindow;

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
//        popupWindow.showAsDropDown(anchorView, 0, 8);
        popupWindow.update();
        mRect.set(mLocation[0], mLocation[1], mLocation[0] + mView.getWidth(), mLocation[1] + mView.getHeight());
        popupWindow.showAtLocation(anchorView, Gravity.TOP | Gravity.RIGHT, 6, mRect.bottom + 95);
        RelativeLayout relativeLayout = (RelativeLayout) mView.findViewById(R.id.ll_report);
        IconTextView itvReport = (IconTextView) mView.findViewById(R.id.itv_report_restaurant);
        itvReport.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                popupWindow.dismiss();
                Intent reportIntent = new Intent(mContext, ReportFoulsResrAct.class);
                reportIntent.putExtra("rId", resId);
                startActivity(reportIntent);
            }
        });
        relativeLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (popupWindow.isShowing())
                {
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow.setOnDismissListener(new PopupDismissListener());
    }

    public void backgroundAlpha(boolean bgAlpha)
    {
        if(bgAlpha)
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
}

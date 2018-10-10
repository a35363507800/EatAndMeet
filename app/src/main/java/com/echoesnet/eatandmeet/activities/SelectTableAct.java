package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.request.transition.Transition;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.utils.GlideApp;

import com.bumptech.glide.request.target.SimpleTarget;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.fragments.DishFrg;
import com.echoesnet.eatandmeet.models.bean.OrderBean;
import com.echoesnet.eatandmeet.models.bean.ResLayoutBean;
import com.echoesnet.eatandmeet.models.bean.TableBean;
import com.echoesnet.eatandmeet.models.datamodel.OperateType;
import com.echoesnet.eatandmeet.models.datamodel.TimePeriodModel;
import com.echoesnet.eatandmeet.models.eventmsgs.OrderTableMsg;
import com.echoesnet.eatandmeet.presenters.ImpISelectTableView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IGetSecurityCodeListener;
import com.echoesnet.eatandmeet.presenters.viewinterface.IGetVoiceCodeListener;
import com.echoesnet.eatandmeet.presenters.viewinterface.ISelectTableView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.DateUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.MyProgressDialog;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.SelectTimePeriodAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.CustomAlterDialogs.DialogWith2BtnAtBottom;
import com.echoesnet.eatandmeet.views.widgets.NoTouchRelativeLayout;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.echoesnet.eatandmeet.views.widgets.selectTableView.IOnDoubleClickListener;
import com.echoesnet.eatandmeet.views.widgets.selectTableView.ITableClickListener;
import com.echoesnet.eatandmeet.views.widgets.selectTableView.SelectTableView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;
import com.squareup.timessquare.CalendarPickerView;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier ben 于2017/1/4
 * @createDate
 * @description 选择餐桌 1.重构为中间件 2.选桌标签代码重构  修改
 */
public class SelectTableAct extends MVPBaseActivity<SelectTableAct, ImpISelectTableView> implements ISelectTableView
{
    //region 变量
    private final static String TAG = SelectTableAct.class.getSimpleName();
    private final static String TIME_DES = "未营业";

    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.rg_st_gender)
    RadioGroup rgGender;
    @BindView(R.id.rb_st_female)
    RadioButton rbFemale;
    @BindView(R.id.rb_st_male)
    RadioButton rbMale;
    @BindView(R.id.st_st_layout)
    SelectTableView stvSelectTable;
    @BindView(R.id.itv_st_calender)
    IconTextView itvCalender;
    @BindView(R.id.tv_st_type1)
    TextView tvTableType1;
    @BindView(R.id.tv_st_type2)
    TextView tvTableType2;
    @BindView(R.id.all_parent)
    AutoLinearLayout allParent;
    @BindView(R.id.tv_time_period)//时间按钮
    TextView tvTimePeriod;
    @BindView(R.id.btn_st_modifyPhone)
    Button btnModifyPhone;
    @BindView(R.id.btn_next)
    Button btn_next;
    @BindView(R.id.btn_floors)//选择楼层按钮
    Button btnMoreFloor;
    @BindView(R.id.tv_date1)
    TextView todayDate;
    @BindView(R.id.tv_date2)
    TextView tomorrowDate;
    @BindView(R.id.tv_date3)
    TextView afterTomorrowDate;
    @BindView(R.id.v_bottom_1)
    View vToday;
    @BindView(R.id.v_bottom_2)
    View vTomorrow;
    @BindView(R.id.v_bottom_3)
    View vAfterTomorrow;
    @BindView(R.id.et_st_name)
    EditText etOrderName;
    @BindView(R.id.tv_st_phone_num)
    TextView tvOrderPhone;
    @BindView(R.id.arl_select_table_container)
    NoTouchRelativeLayout arlTableContainer;
    @BindView(R.id.lv_floors)//楼层
    ListView lvFloors;
    @BindView(R.id.all_floor_container)
    AutoLinearLayout allFloorPanel;

    private Activity mAct;
    private List<TableBean> tableEntities;//每一层的tables
    private List<TableBean> selectedTables = new ArrayList<>();//整个餐厅选中的table
    private ArrayList<TextView> tableLabels = new ArrayList<>();
    private List<ResLayoutBean> resLayouts = new ArrayList<>();//所有楼层的数据

    private String resId = "";
    private String resName = "";
    private String orderedTableInfo = "";
    private String floorNum = "";
    private Button btnGetVcode;
    private MyProgressDialog pDialog;

    private String endDate;//最晚可预订日期
    private String weekFlg;//周末是否可预订，0可，1不可
    private String restTime;//休息时间，使用分隔符分割的多个日期

    private String bootyCallDate;//约会日期
    private TextView tvSoundTip;
    private TextView tvSoundCode;

    private int MsgCountdown = 60;
    private int AudioCountdown = 60;
    private Timer MsgTimer;
    private Timer AudioTimer;
    private TimerTask ttMsgCountDown;
    private TimerTask ttAudioMsgCountDown;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_select_table);
        ButterKnife.bind(this);
        initAfterView();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //为了从大界面返回刷新选择的显示
        refreshSelectStatus();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        EventBus.getDefault().unregister(this);

        //停止时发送一个消息
        EventBus.getDefault().post(new OrderTableMsg(floorNum, resLayouts,
                String.format("%s %s:00", getSelectedDate(), tvTimePeriod.getText().toString()), resId, selectedTables));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case EamConstant.EAM_CONFIRM_ORDER_REQUEST_CODE:
                switch (resultCode)
                {
                    case EamConstant.EAM_RESULT_NO:
                        if (data != null)
                        {
                            String result = data.getStringExtra("result");
                            if (result != null && result.equals("back"))
                            {
                                topBar.getLeftButton2().setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    protected ImpISelectTableView createPresenter()
    {
        return new ImpISelectTableView();
    }

    private void initAfterView()
    {
        mAct = this;
        initData();
        showNewbieGuide();
    }

    private void initData()
    {
        //获得餐厅id
        resId = getIntent().getStringExtra("restId");
        resName = getIntent().getStringExtra("resName");
        bootyCallDate = getIntent().getStringExtra("bootyCallDate");
        topBar.setTitle(getResources().getString(R.string.select_table));
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                SelectTableAct.this.finish();
            }

            @Override
            public void left2Click(View view)
            {
                if (!TextUtils.isEmpty(bootyCallDate))
                {
                    setResult(RESULT_OK);
                    finish();
                } else
                {
                    Intent intent = new Intent(mAct, HomeAct.class);
                    intent.putExtra("showPage", 3);
                    mAct.startActivity(intent);
                }
            }

            @Override
            public void rightClick(View view)
            {

            }
        });
        MsgTimer = new Timer();
        AudioTimer = new Timer();
        tableLabels.add(tvTableType1);
        tableLabels.add(tvTableType2);
        rbFemale.setButtonDrawable(R.drawable.radio_btn_p);
        rbMale.setButtonDrawable(R.drawable.radio_btn_n);
        pDialog = new MyProgressDialog()
                .buildDialog(this)
                .setDescription("正在处理...");
        pDialog.setCancelable(false);
        if (mPresenter != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            if (SharePreUtils.getToOrderMeal(mAct).equals("toOrderMeal"))
            {
                mPresenter.getRestoreDaysForAppo(resId);
            } else
            {
                mPresenter.getRestoreDays(resId);
            }
        }
        stvSelectTable.setOnTableClickListener(new ITableClickListener()
        {
            @Override
            public void onTableClick(final TableBean te)
            {
                //如果是装饰物不处理
                if (te.getType().equals("00"))
                    return;
                if (TIME_DES.equals(tvTimePeriod.getText().toString()))
                {
                    ToastUtils.showShort("请先选择预定时间");
                    return;
                }
                Logger.t(TAG).d("桌号：" + te.getTableId());
                switch (te.getStatus())
                {
                    //未选中
                    case "0":
                        if (selectedTables.size() == 2)
                        {
                            ToastUtils.showShort("一次最多可以预定两张餐桌");
                            return;
                        }
                        te.setStatus("2");
                        selectedTables.add(te);
                        break;
                    //锁定
                    case "1":
                        return;
                    //已经选中
                    case "2":
                        te.setStatus("0");
                        selectedTables.remove(te);
                        break;
                }

                Logger.t(TAG).d("重新设置图片的函数触发了2》》" + CdnHelper.getInstance().getMaterialUrl(te.getPicName() + te.getStatus(), mAct));
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(CdnHelper.getInstance().getMaterialUrl(te.getPicName() + te.getStatus(), mAct))
                        .skipMemoryCache(true)
                        .into(new SimpleTarget<Bitmap>((int) te.getWidth2(), (int) te.getHeight2())
                        {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation)
                            {
                                //Logger.t(TAG).d("点击的桌子1》》" + te.toString());
                                Bitmap bitmap = ImageUtils.rotateImageView(te.getAngle2(), resource);
                                te.setBitImg(bitmap);
                                stvSelectTable.dataSourceChanged(tableEntities);
                                refreshSelectStatus();
                            }
                        });
            }
        });

        stvSelectTable.setOnDoubleClickListener(new IOnDoubleClickListener()
        {
            @Override
            public void onDoubleClick(View v)
            {
                if (TIME_DES.equals(tvTimePeriod.getText().toString()))
                    return;
                Intent intent = new Intent(SelectTableAct.this, SelectTableZoomAct.class);
                mAct.startActivity(intent);
            }
        });
        Logger.t(TAG).d("密度》" + getResources().getDisplayMetrics().density);
        stvSelectTable.setInitScale((getResources().getDisplayMetrics().density / 4) * 1.5f);
        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                if (checkedId == rbFemale.getId())
                {
                    rbFemale.setButtonDrawable(R.drawable.radio_btn_p);
                    rbMale.setButtonDrawable(R.drawable.radio_btn_n);
                } else if (checkedId == rbMale.getId())
                {
                    rbMale.setButtonDrawable(R.drawable.radio_btn_p);
                    rbFemale.setButtonDrawable(R.drawable.radio_btn_n);
                }
            }
        });
    }

    /**
     * @Description: 显示新手引导
     */
    private void showNewbieGuide()
    {
        if (SharePreUtils.getIsNewBieOrder(mAct))
        {
            NetHelper.checkIsShowNewbie(mAct, "3", new ICommonOperateListener()
            {
                @Override
                public void onSuccess(String response)
                {
                    if ("0".equals(response))
                    {
                        //获取root节点
                        final FrameLayout fRoot = (FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);
                        final View vGuide = View.inflate(mAct, R.layout.view_newbie_guide_order_new, null);
                        final ImageView imgOrder1 = (ImageView) vGuide.findViewById(R.id.img_order1);
                        final ImageView imgOrder2 = (ImageView) vGuide.findViewById(R.id.img_order2);
                        final ImageView imgOrder3 = (ImageView) vGuide.findViewById(R.id.img_order3);
                        final TextView tvClickDismiss1 = (TextView) vGuide.findViewById(R.id.tv_click_dismiss1);
                        final TextView tvClickDismiss2 = (TextView) vGuide.findViewById(R.id.tv_click_dismiss2);
                        final TextView tvClickDismiss3 = (TextView) vGuide.findViewById(R.id.tv_click_dismiss3);

                        vGuide.setClickable(true);
                        tvClickDismiss1.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                imgOrder1.setVisibility(View.GONE);
                                tvClickDismiss1.setVisibility(View.GONE);
                                imgOrder2.setVisibility(View.VISIBLE);
                            }
                        });
                        tvClickDismiss2.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {

                                imgOrder2.setVisibility(View.GONE);
                                tvClickDismiss2.setVisibility(View.GONE);
                                imgOrder3.setVisibility(View.VISIBLE);
                            }
                        });
                        tvClickDismiss3.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                fRoot.removeView(vGuide);
                                SharePreUtils.setIsNewBieOrder(mAct, false);
                                NetHelper.saveShowNewbieStatus(mAct, "3");
                            }
                        });
                        fRoot.addView(vGuide);
                    } else
                    {
                        SharePreUtils.setIsNewBieOrder(mAct, false);
                    }
                }

                @Override
                public void onError(String code, String msg)
                {

                }
            });

        }
    }

    /**
     * 初始化时间
     */
    private void initDateBar()
    {
        //日期
        DateFormat sdf = new SimpleDateFormat("MM.dd");
        Calendar today = Calendar.getInstance();
        Calendar tToday = Calendar.getInstance();
        tToday.add(Calendar.DATE, 1);
        Calendar atToday = Calendar.getInstance();
        atToday.add(Calendar.DATE, 2);

        DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        todayDate.setText(String.format("今天(%s)", sdf.format(today.getTime())));
        HashMap<String, String> tInfo = new HashMap<>();
        tInfo.put("time", sdf2.format(today.getTime()));
        tInfo.put("status", "1");
        todayDate.setTag(tInfo);

        tomorrowDate.setText(String.format("明天(%s)", sdf.format(tToday.getTime())));
        HashMap<String, String> tInfo2 = new HashMap<>();
        tInfo2.put("time", sdf2.format(tToday.getTime()));
        tInfo2.put("status", "0");
        tomorrowDate.setTag(tInfo2);

        afterTomorrowDate.setText(String.format("后天(%s)", sdf.format(atToday.getTime())));
        HashMap<String, String> tInfo3 = new HashMap<>();
        tInfo3.put("time", sdf2.format(atToday.getTime()));
        tInfo3.put("status", "0");
        afterTomorrowDate.setTag(tInfo3);

        if (!TextUtils.isEmpty(bootyCallDate))
        {
            setBootyCallDate();
        } else
        {
            selectToday();
        }

    }

    /**
     * 从日历选择日期后赋值到timebar上
     *
     * @param centerDay 日历对象
     */
    private void setTimeBar(Calendar centerDay)
    {
        //final Calendar tempCalender=centerDay;
        DateFormat sdf = new SimpleDateFormat("MM月dd日");
        DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        //先查看当前选择的是那个日期
        String currentDateStr = "";
        if (((HashMap<String, String>) todayDate.getTag()).get("status").equals("1"))
            currentDateStr = ((HashMap<String, String>) todayDate.getTag()).get("time");
        else if (((HashMap<String, String>) tomorrowDate.getTag()).get("status").equals("1"))
            currentDateStr = ((HashMap<String, String>) tomorrowDate.getTag()).get("time");
        else if (((HashMap<String, String>) afterTomorrowDate.getTag()).get("status").equals("1"))
            currentDateStr = ((HashMap<String, String>) afterTomorrowDate.getTag()).get("time");

        Logger.t(TAG).d("当前日期" + String.valueOf(currentDateStr) + "选择日期 " + sdf2.format(centerDay.getTime()));
        Logger.t(TAG).d("   " + sdf2.format(centerDay.getTime()).equals(String.valueOf(currentDateStr)));
        if (!(sdf2.format(centerDay.getTime()).equals(String.valueOf(currentDateStr))))
        {
            if (selectedTables.size() > 0)
            {
                ToastUtils.showShort( "修改订餐时间将需要重新选桌");
                return;
            }
        }

        //日期
        DateFormat sdf1 = new SimpleDateFormat("MM.dd");
        if (DateUtils.isToday(centerDay))
        {
            Logger.t(TAG).d("今天");
            //centerDay.add(Calendar.DATE,-1);
            todayDate.setText(String.format("今天(%s)", sdf1.format(centerDay.getTime())));
            ((HashMap<String, String>) todayDate.getTag()).put("time", sdf2.format(centerDay.getTime()));
            ((HashMap<String, String>) todayDate.getTag()).put("status", "1");

            centerDay.add(Calendar.DATE, 1);
            tomorrowDate.setText(String.format("明天(%s)", sdf1.format(centerDay.getTime())));
            ((HashMap<String, String>) tomorrowDate.getTag()).put("time", sdf2.format(centerDay.getTime()));
            ((HashMap<String, String>) tomorrowDate.getTag()).put("status", "0");

            centerDay.add(Calendar.DATE, 1);
            afterTomorrowDate.setText(String.format("后天(%s)", sdf1.format(centerDay.getTime())));
            ((HashMap<String, String>) afterTomorrowDate.getTag()).put("time", sdf2.format(centerDay.getTime()));
            ((HashMap<String, String>) afterTomorrowDate.getTag()).put("status", "0");
            selectToday();
        } else if (DateUtils.isWithinDaysFuture(centerDay, 1))
        {
            Logger.t(TAG).d("明天");
            centerDay.add(Calendar.DATE, -1);
            todayDate.setText(String.format("今天(%s)", sdf1.format(centerDay.getTime())));
            ((HashMap<String, String>) todayDate.getTag()).put("time", sdf2.format(centerDay.getTime()));
            ((HashMap<String, String>) todayDate.getTag()).put("status", "0");


            centerDay.add(Calendar.DATE, 1);
            tomorrowDate.setText(String.format("明天(%s)", sdf1.format(centerDay.getTime())));
            ((HashMap<String, String>) tomorrowDate.getTag()).put("time", sdf2.format(centerDay.getTime()));
            ((HashMap<String, String>) tomorrowDate.getTag()).put("status", "1");

            centerDay.add(Calendar.DATE, 1);
            afterTomorrowDate.setText(String.format("后天(%s)", sdf1.format(centerDay.getTime())));
            ((HashMap<String, String>) afterTomorrowDate.getTag()).put("time", sdf2.format(centerDay.getTime()));
            ((HashMap<String, String>) afterTomorrowDate.getTag()).put("status", "0");
            selectTomorrow();
        } else if (DateUtils.isWithinDaysFuture(centerDay, 2))
        {
            Logger.t(TAG).d("后天");
            centerDay.add(Calendar.DATE, -2);
            todayDate.setText(String.format("今天(%s)", sdf1.format(centerDay.getTime())));
            ((HashMap<String, String>) todayDate.getTag()).put("time", sdf2.format(centerDay.getTime()));
            ((HashMap<String, String>) todayDate.getTag()).put("status", "0");


            centerDay.add(Calendar.DATE, 1);
            tomorrowDate.setText(String.format("明天(%s)", sdf1.format(centerDay.getTime())));
            ((HashMap<String, String>) tomorrowDate.getTag()).put("time", sdf2.format(centerDay.getTime()));
            ((HashMap<String, String>) tomorrowDate.getTag()).put("status", "0");

            centerDay.add(Calendar.DATE, 1);
            afterTomorrowDate.setText(String.format("后天(%s)", sdf1.format(centerDay.getTime())));
            ((HashMap<String, String>) afterTomorrowDate.getTag()).put("time", sdf2.format(centerDay.getTime()));
            ((HashMap<String, String>) afterTomorrowDate.getTag()).put("status", "1");
            selectAfterTom();
        } else
        {
            centerDay.add(Calendar.DATE, -1);
            todayDate.setText(sdf.format(centerDay.getTime()));
            ((HashMap<String, String>) todayDate.getTag()).put("time", sdf2.format(centerDay.getTime()));
            ((HashMap<String, String>) todayDate.getTag()).put("status", "0");


            centerDay.add(Calendar.DATE, 1);
            tomorrowDate.setText(sdf.format(centerDay.getTime()));
            ((HashMap<String, String>) tomorrowDate.getTag()).put("time", sdf2.format(centerDay.getTime()));
            ((HashMap<String, String>) tomorrowDate.getTag()).put("status", "1");

            centerDay.add(Calendar.DATE, 1);
            afterTomorrowDate.setText(sdf.format(centerDay.getTime()));
            ((HashMap<String, String>) afterTomorrowDate.getTag()).put("time", sdf2.format(centerDay.getTime()));
            ((HashMap<String, String>) afterTomorrowDate.getTag()).put("status", "0");
            selectTomorrow();
        }
    }

    /**
     * 根据楼层来改变餐厅显示信息,入口函数，其他函数在此函数链当中
     *
     * @param resId
     * @param floorNum
     * @param orderTime
     */
    private void setLayoutInfo(String resId, String floorNum, String orderTime)
    {
        if (mAct == null || mAct.isFinishing())
            return;

        Logger.t(TAG).d("已经选择的桌子集合》" + selectedTables.toString());
        Logger.t(TAG).d("设置楼层数据》" + "餐厅id" + resId + "楼层" + floorNum + "订单时间" + orderTime);
        if (pDialog != null && !pDialog.isShowing())
            pDialog.show();
        ResLayoutBean rlb = null;
        Logger.t(TAG).d("resLayouts.size():" + resLayouts.size());
        for (int i = 0; i < resLayouts.size(); i++)
        {
            Logger.t(TAG).d("resLayouts.get(i).getLayoutId():" + resLayouts.get(i).getLayoutId());
            if (resLayouts.get(i).getLayoutId().equals(resId + floorNum))
            {
                rlb = resLayouts.get(i);
                break;
            }
        }
        if (rlb == null)
        {
            Logger.t(TAG).d("楼层信息》 空");
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
            return;
        }

        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(rlb.getFloor().getImgUrl())
                .skipMemoryCache(true)
                .into(new SimpleTarget<Bitmap>(Integer.parseInt(rlb.getFloor().getWidth()), Integer.parseInt(rlb.getFloor().getHeight()))
                {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation)
                    {
                        stvSelectTable.setFloorImg(resource);
                    }
                });
        tableEntities = rlb.getTables();

/*        //保存餐桌的选择状态
        List<HashMap<String, String>> sMapLst = new ArrayList<>();
        //Logger.t(TAG).d("tableEntities.size():"+tableEntities.size());
        for (int i = 0; i < tableEntities.size(); i++)
        {
            //Logger.t(TAG).d("tableEntities.get(i).getEmojiFilePath():"+tableEntities.get(i).getStatus());
            if (tableEntities.get(i) != null && tableEntities.get(i).getStatus().equals("2"))
            {
                HashMap<String, String> map = new HashMap<>();
                map.put("tNum", tableEntities.get(i).getTableId());
                map.put("tStatus", tableEntities.get(i).getStatus());
                sMapLst.add(map);
            }
        }*/
        //Logger.t(TAG).d("保存的桌子状态" + sMapLst.toString());
        if (mPresenter != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            mPresenter.getTableStatusFromSever(resId, floorNum, orderTime);
        }
        stvSelectTable.setFloorSize(Float.parseFloat(rlb.getFloor().getWidth()), Float.parseFloat(rlb.getFloor().getHeight()));
    }

    //刷新餐厅桌子选择的状态，每次点击时间或者楼层时刷新
    private void setTablesStatus(List<HashMap<String, String>> statueLst)
    {
        Logger.t(TAG).d("桌子状态：" + statueLst.toString());
        Logger.t(TAG).d("桌子集合：" + tableEntities.toString());
        for (int i = 0; i < tableEntities.size(); i++)
        {
            final int num = i;
            String status = "0";
            String tableId = tableEntities.get(i).getTableId();
            //如果不是桌子则不处理 type 01 为普通桌子，02为包间
            if (tableEntities.get(i).getType().equals("00"))
            {
                status = "";
            } else
            {
                for (HashMap<String, String> map : statueLst)
                {
                    if (map.get("tNum").equals(tableId))
                    {
                        status = map.get("tStatus");
                        tableEntities.get(i).setStatus(status);
                        break;
                    }
                }
            }
            //Logger.t(TAG).d("图片"+CdnHelper.getInstance().getMaterialUrl(tableEntities.get(i).getPicName()+status,mAct));
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(CdnHelper.getInstance().getMaterialUrl(tableEntities.get(i).getPicName() + status, mAct))
                    .into(new SimpleTarget<Bitmap>((int) tableEntities.get(num).getWidth2(), (int) tableEntities.get(num).getHeight2())
                    {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation)
                        {
                            tableEntities.get(num).setBitImg(ImageUtils.rotateImageView(tableEntities.get(num).getAngle2(), resource));
                            if (num == (tableEntities.size() - 1))
                            {
                                stvSelectTable.setTableLst(tableEntities);
                            }
                        }
                    });
        }
        //设置桌子前前reset一下
        reFreshSelectTableStatus(tableEntities);
    }

    //获得当前选中的日期
    private String getSelectedDate()
    {
        String selectDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        if (todayDate.getTag() != null && ((HashMap<String, String>) todayDate.getTag()).get("status").equals("1"))
            selectDate = ((HashMap<String, String>) todayDate.getTag()).get("time");
        else if (todayDate.getTag() != null && ((HashMap<String, String>) tomorrowDate.getTag()).get("status").equals("1"))
            selectDate = ((HashMap<String, String>) tomorrowDate.getTag()).get("time");
        else if (afterTomorrowDate.getTag() != null && ((HashMap<String, String>) afterTomorrowDate.getTag()).get("status").equals("1"))
            selectDate = ((HashMap<String, String>) afterTomorrowDate.getTag()).get("time");
        return selectDate;
    }

    //设置楼层数
    private void setFloorsData(List<ResLayoutBean> resLays)
    {
        String firstLayoutId = resLays.get(0).getLayoutId();
        //初始化一下floorNum
        floorNum = firstLayoutId.substring(firstLayoutId.length() - 2);
        //如果只有一层，不显示楼层按钮
        if (resLays.size() == 1)
        {
            btnMoreFloor.setVisibility(View.GONE);
            //return;
        } else
        {
            btnMoreFloor.setVisibility(View.VISIBLE);
            btnMoreFloor.setText(floorNum);
        }
        final List<Map<String, Object>> mapLst = new ArrayList<>();
        for (ResLayoutBean rlb : resLays)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("floorNum", rlb.getLayoutId().substring(rlb.getLayoutId().length() - 2));
            map.put("img", R.drawable.white);
            mapLst.add(map);
        }
        SimpleAdapter floorCountAdapter = new SimpleAdapter(mAct, mapLst, R.layout.litem_small_floors,
                new String[]{"floorNum", "img"}, new int[]{R.id.tv_small_floor, R.id.iv_small_floor_split});
        lvFloors.setAdapter(floorCountAdapter);
        lvFloors.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                btnMoreFloor.setText(String.valueOf(mapLst.get(position).get("floorNum")));
                allFloorPanel.setVisibility(View.GONE);
                floorNum = String.valueOf(String.valueOf(mapLst.get(position).get("floorNum")));
                setLayoutInfo(resId, floorNum, String.format("%s %s:00", getSelectedDate(), tvTimePeriod.getText().toString()));
            }
        });
    }

    /**
     * 获得所有楼层数据源并初始化指定楼层
     *
     * @param resId     餐厅id
     * @param orderTime 查看时间
     */
    private void getTableData(final String resId, final String orderTime)
    {
        Logger.t(TAG).d("参数为：》" + CdnHelper.CDN_ORIGINAL_SITE + CdnHelper.fileFolder + resId + "_table.json");
        OkHttpUtils.get()
                .url(CdnHelper.CDN_ORIGINAL_SITE + CdnHelper.fileFolder + resId + "_table.json")
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        NetHelper.handleNetError(mAct, null, TAG, e);
                        if (pDialog != null && pDialog.isShowing())
                            pDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response)
                    {
                        parseResInfoFromJson(response);
                        setLayoutInfo(resId, floorNum, orderTime);
                    }
                });
    }

    /**
     * 从字符串中解析json数据
     *
     * @param jsonStr
     */
    private void parseResInfoFromJson(String jsonStr)
    {
        Logger.t(TAG).d("餐厅原始数据：" + jsonStr);
        resLayouts = new Gson().fromJson(jsonStr, new TypeToken<ArrayList<ResLayoutBean>>()
        {
        }.getType());
        for (ResLayoutBean rlb : resLayouts)
        {
            String tempFloorNum = rlb.getLayoutId().substring(rlb.getLayoutId().length() - 2);
            for (TableBean tb : rlb.getTables())
            {
                tb.setFloorNumber(tempFloorNum);
            }
        }
        setFloorsData(resLayouts);
        Logger.t(TAG).d("餐厅解析后数据：" + resLayouts.toString());
    }


    private void setUiContent(Map<String, String> resultMap)
    {
        tvOrderPhone.setText(resultMap.get("mobile"));
        //etOrderName.setText("");
        //etOrderName.setText(resultMap.get("name"));
        rbFemale.setChecked(resultMap.get("gender").equals("女"));
        rbMale.setChecked(resultMap.get("gender").equals("男"));
    }

    private void getInitTimePeriod(List<TimePeriodModel> periodLst)
    {
        boolean isValidTime = false;
        for (TimePeriodModel tModel : periodLst)
        {
            if (tModel.getStatus().equals("1"))
            {
                tvTimePeriod.setText(tModel.getTimeStr());
                isValidTime = true;
                break;
            }
        }
        if (isValidTime == false)
        {
            ToastUtils.showShort( "今天已过营业时间，请选择下一天");
            tvTimePeriod.setText("未营业");
        }
        getTableData(resId, String.format("%s %s:00", getSelectedDate(), tvTimePeriod.getText().toString()));
    }

    private void showTimeDialog(List<TimePeriodModel> periodLst)
    {
        TimePopWindow myPopWindow = new TimePopWindow(SelectTableAct.this, periodLst);
        myPopWindow.setOnDismissListener(new popupDismissListener(myPopWindow));
        myPopWindow.showPopupWindow(allParent);
    }


    /**
     * On order table msg.
     *
     * @param event the event
     */
    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    public void onOrderTableMsg(OrderTableMsg event)
    {
        Logger.t(TAG).d("大选座页面传回：》" + event.toString());
        resLayouts = event.getLayoutEntities();
        selectedTables = event.getSelectedTables();
        resId = event.getRestId();
        floorNum = event.getFloorNum();
        btnMoreFloor.setText(floorNum);
        refreshSelectStatus();
        setLayoutInfo(resId, floorNum, event.getOrderTime());
    }

    private void refreshSelectStatus()
    {
        for (TextView tableLabel : tableLabels)
        {
            tableLabel.setVisibility(View.GONE);
        }
        for (int i = 0; i < selectedTables.size(); i++)
        {
            TextView tv = tableLabels.get(i);
            TableBean te1 = selectedTables.get(i);
            tv.setText(String.format("%s层%s(%s人)", te1.getFloorNumber(), te1.getTableName(), te1.getTableType().replaceFirst("^0+(?!$)", "")));
            tv.setTag(resId + te1.getFloorNumber() + CommonUtils.SEPARATOR + te1.getTableId() + CommonUtils.SEPARATOR + te1.getTableType().replaceFirst("^0+(?!$)", ""));
            tv.setVisibility(View.VISIBLE);
            MyTableLabelClickListener tableLabelClick = new MyTableLabelClickListener(te1);
            tv.setOnClickListener(tableLabelClick);
        }
    }

    //点击timebar
    private void selectToday()
    {
        if (handleResCloseStatus("today") && mPresenter != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            mPresenter.getOpenTimePeriodData(getSelectedDate(), resId, OperateType.InitPeriod);
        }
    }

    /**
     * 选择明天
     */
    private void selectTomorrow()
    {
        //默认选择第一可选时间
        if (handleResCloseStatus("tomorrow") && mPresenter != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            mPresenter.getOpenTimePeriodData(getSelectedDate(), resId, OperateType.InitPeriod);
        }
    }

    /**
     * 选择后天
     */
    private void selectAfterTom()
    {
        if (handleResCloseStatus("afterTomorrow") && mPresenter != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            mPresenter.getOpenTimePeriodData(getSelectedDate(), resId, OperateType.InitPeriod);
        }
    }

    /**
     * 处理未营业状态
     */
    private boolean handleResCloseStatus(String handleView)
    {
        HashMap<String, TextView> dateViewMap = new HashMap<>();
        dateViewMap.put("today", todayDate);
        dateViewMap.put("tomorrow", tomorrowDate);
        dateViewMap.put("afterTomorrow", afterTomorrowDate);
        HashMap<String, View> dateBottomBarMap = new HashMap<>();
        dateBottomBarMap.put("today", vToday);
        dateBottomBarMap.put("tomorrow", vTomorrow);
        dateBottomBarMap.put("afterTomorrow", vAfterTomorrow);
        TextView tvHandlingView = null;
        for (Map.Entry<String, TextView> entry : dateViewMap.entrySet())
        {
            if (entry.getKey().equals(handleView))
            {
                tvHandlingView = entry.getValue();
                break;
            }
        }
        if (tvHandlingView == null)
        {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String selectedDateStr = ((HashMap<String, String>) tvHandlingView.getTag()).get("time");
        try
        {
            String result = handleCantOrderTime(sdf.parse(selectedDateStr));
            switch (result)
            {
                case "0":
                    break;
                case "1":
                    ToastUtils.showShort( String.format("此商户暂时只支持%s之前的预定", endDate));
                    break;
/*                case "2":
                    ToastUtils.showShort(mContext, "当日商家休息");
                    break;*/
                case "3":
                    ToastUtils.showShort( "商家不支持周末预定");
                    break;
                default:
                    break;
            }
            if (!result.equals("0"))
            {
                tvTimePeriod.setText("未营业");
            }
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        if (hasSelectedTable() && ((HashMap<String, String>) tvHandlingView.getTag()).get("status").equals("0"))
        {
            ToastUtils.showShort( "目前一个订单只支持预定同一天的餐桌");
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
            return false;
        }
        for (Map.Entry<String, TextView> entry : dateViewMap.entrySet())
        {

            String keyStr = entry.getKey();
            if (keyStr.equals(handleView))
            {
                tvHandlingView.setTextColor(ContextCompat.getColor(mAct, R.color.MC1));
                dateBottomBarMap.get(keyStr).setBackgroundResource(R.color.MC1);
                ((HashMap<String, String>) tvHandlingView.getTag()).put("status", "1");
            } else
            {
                entry.getValue().setTextColor(ContextCompat.getColor(mAct, R.color.FC1));
                dateBottomBarMap.get(keyStr).setBackgroundResource(R.color.transparent);
                ((HashMap<String, String>) entry.getValue().getTag()).put("status", "0");
            }
        }
        return true;
    }

    /**
     * 刷新后重置桌子选择状态，防止用户在桌子状态没有就绪后就选择
     */
    private void reFreshSelectTableStatus(List<TableBean> sTableBeans)
    {
        selectedTables.clear();
        for (ResLayoutBean rlb : resLayouts)
        {
            for (TableBean tb : rlb.getTables())
            {
                //Logger.t(TAG).d("刷新桌子》" + tb.toString());
                if (tb.getStatus().equals("2"))
                {
                    selectedTables.add(tb);
                }
            }
        }
        refreshSelectStatus();
    }

    /**
     * 产生订单
     */
    private void generateOrder()
    {
        OrderBean.getOrderBeanInstance().setrId(resId);
        Logger.t(TAG).d(resName);
        OrderBean.getOrderBeanInstance().setrName(resName);
        OrderBean.getOrderBeanInstance().setOrderTime(String.format("%s %s:00", getSelectedDate(), tvTimePeriod.getText().toString()));
        OrderBean.getOrderBeanInstance().setMobile(tvOrderPhone.getText().toString());
        OrderBean.getOrderBeanInstance().setNicName(etOrderName.getText().toString());
        OrderBean.getOrderBeanInstance().setSex(rbFemale.isChecked() ? "女士" : "先生");
        List<String> labelTags = new ArrayList<>();
        List<String> labelTexts = new ArrayList<>();
        for (TextView tableLabel : tableLabels)
        {
            if (tableLabel.getVisibility() == View.VISIBLE)
            {
                labelTags.add((String) tableLabel.getTag());
                labelTexts.add(tableLabel.getText().toString());
            }
        }
        orderedTableInfo = CommonUtils.listToStrWishSeparator(labelTags, CommonUtils.SEPARATOR);
        String selectedTableTextStr = CommonUtils.listToStrWishSeparator(labelTexts, " ");
        OrderBean.getOrderBeanInstance().setSits(orderedTableInfo);
        OrderBean.getOrderBeanInstance().setSitsName(selectedTableTextStr);
    }

    @OnClick({R.id.itv_st_calender, R.id.tv_time_period, R.id.btn_st_modifyPhone, R.id.btn_next, R.id.tv_date1
            , R.id.tv_date2, R.id.tv_date3, R.id.btn_floors})
    void clickView(View view)
    {
        switch (view.getId())
        {
            case R.id.itv_st_calender:
                showCalendarInDialog("日历", R.layout.calendar);
                break;
            //           R.id.btn_res_book_desk,
//            case R.id.btn_res_book_desk:
//
//                break;
            //选择时间
            case R.id.tv_time_period:
                if (SharePreUtils.getIsFirstUse(mAct))
                {
                    //设置为用户已经使用
                    SharePreUtils.setIsFirstUse(mAct, false);
                    arlTableContainer.setTouchAble(true);
                }

                if (!TIME_DES.equals(tvTimePeriod.getText().toString()))
                {
                    if (mPresenter != null)
                    {
                        if (pDialog != null && !pDialog.isShowing())
                            pDialog.show();
                        mPresenter.getOpenTimePeriodData(getSelectedDate(), resId, OperateType.CurrentPeriod);
                    }
                }
                break;
            case R.id.btn_st_modifyPhone:
                modifyMobile(view);
                break;
            //下一步
            case R.id.btn_next:
                if (hasSelectedTable() == false)
                {
                    ToastUtils.showShort("请选择桌子");
                    break;
                }
                if (TextUtils.isEmpty(etOrderName.getText().toString().trim()))
                {
                    ToastUtils.showShort("请输入订餐人姓名");
                    break;
                }
                if (TIME_DES.equals(tvTimePeriod.getText().toString()))
                {
                    ToastUtils.showShort("请选择订餐时间");
                    break;
                }
                //提示有可能被重新分配
                new CustomAlertDialog(mAct)
                        .builder()
                        .setTitle("提示")
                        .setMsg("由于餐厅客流量较大，您在前往用餐时可能会出现调配桌位的情况。")
                        .setPositiveButton("同意", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                generateOrder();
                                if (OrderBean.getOrderBeanInstance().getType().equals("0") || OrderBean.getOrderBeanInstance().getType().equals("1"))
                                {
                                    OrderBean.getOrderBeanInstance().setType("1");
                                    Intent intent = new Intent(mAct, DOrderMealDetailAct.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    intent.putExtra("restId", resId);
                                    intent.putExtra("index", 1);
                                    mAct.startActivity(intent);
                                    Logger.t(TAG).d("走到这里了吗？？》》》");
                                    finish();

                                } else if (OrderBean.getOrderBeanInstance().getType().equals("2") || OrderBean.getOrderBeanInstance().getType().equals("3"))
                                {
                                    OrderBean.getOrderBeanInstance().setType("3");
                                    Intent intent = new Intent(mAct, DOrderConfirmAct.class);
                                    intent.putExtra("orderData", (Serializable) DishFrg.dishList);
                                    intent.putExtra("price", 23.00);
                                    //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    mAct.startActivityForResult(intent, EamConstant.EAM_CONFIRM_ORDER_REQUEST_CODE);
                                    //mAct.startActivity(intent);
                                    finish();
                                }
                            }
                        })
                        .setNegativeButton("拒绝", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                            }
                        }).show();

                break;
            case R.id.btn_floors:
                if (lvFloors.getCount() > 1)
                {
                    if (allFloorPanel.getVisibility() == View.VISIBLE)
                        allFloorPanel.setVisibility(View.GONE);
                    else
                        allFloorPanel.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_date1:
                String todayTime = ((HashMap<String, String>) todayDate.getTag()).get("time");
                try
                {
                    if (((HashMap<String, String>) todayDate.getTag()).get("status").equals("0") && checkDateCanReserve(new SimpleDateFormat("yyyy-MM-dd").parse(todayTime)))
                        selectToday();
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_date2:
                String tomorrowTime = ((HashMap<String, String>) tomorrowDate.getTag()).get("time");
                try
                {
                    if (((HashMap<String, String>) tomorrowDate.getTag()).get("status").equals("0") && checkDateCanReserve(new SimpleDateFormat("yyyy-MM-dd").parse(tomorrowTime)))
                        selectTomorrow();
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_date3:
                String afterTomorrowTime = ((HashMap<String, String>) afterTomorrowDate.getTag()).get("time");
                try
                {
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(afterTomorrowTime);
                    Date end = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
                    if (DateUtils.isAfterDay(date, end))
                    {
                        ToastUtils.showShort( String.format("此商户暂时只支持%s之前的预定", endDate));
                        break;
                    }
                    if (((HashMap<String, String>) afterTomorrowDate.getTag()).get("status").equals("0") && checkDateCanReserve(new SimpleDateFormat("yyyy-MM-dd").parse(afterTomorrowTime)))
                        selectAfterTom();
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private boolean hasSelectedTable()
    {
        boolean hasSelectedTable = false;
        for (TextView tableLabel : tableLabels)
        {
            if (tableLabel.getVisibility() == View.VISIBLE)
            {
                hasSelectedTable = true;
                break;
            }
        }
        return hasSelectedTable;
    }

    /**
     * 设定约会日期
     */
    private void setBootyCallDate()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        try
        {
            Date date = simpleDateFormat.parse(bootyCallDate);
            if (checkDateCanReserve(date))
            {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                setTimeBar(calendar);
            }
        } catch (ParseException e)
        {
            e.printStackTrace();
            Logger.t("bootycallDate").d(e.getMessage());
        }
    }


    /**
     * 检查日期是否可以预定
     *
     * @param date
     * @return
     */
    private boolean checkDateCanReserve(Date date)
    {
        if (!TextUtils.isEmpty(bootyCallDate))
        {
            Calendar selectC = Calendar.getInstance();
            Calendar endC = Calendar.getInstance();
            Calendar bootCallC = Calendar.getInstance();
            selectC.setTime(date);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMdd");
            Date end = null;
            Date bootyCall = null;
            try
            {
                end = simpleDateFormat.parse(endDate);
                endC.setTime(end);
                bootyCall = simpleDateFormat1.parse(bootyCallDate);
                bootCallC.setTime(bootyCall);
            } catch (ParseException e)
            {
                e.printStackTrace();
            }
            if (bootCallC.compareTo(endC) > 0)
            {
                ToastUtils.showShort( "约会当天该餐厅未营业，请选择其他餐厅");
                return false;
            }
            if ("1".equals(weekFlg) && (selectC.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                    selectC.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY))
            {
                ToastUtils.showShort( "该餐厅周末不能预订");
                return false;
            }

            if (selectC.compareTo(bootCallC) != 0)
            {
                ToastUtils.showShort( "只能预定约会当天");
                return false;
            }
        }
        return true;
    }

    /**
     * 修改手机号
     *
     * @param view
     */
    private void modifyMobile(View view)
    {
        View contentView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_modify_phone, null);
        final EditText etPhoneNum = (EditText) contentView.findViewById(R.id.ev_modify_phone_num);
        final EditText etSecurityCode = (EditText) contentView.findViewById(R.id.ew_modify_verify_code);
        tvSoundTip = (TextView) contentView.findViewById(R.id.tv_tip);
        tvSoundCode = (TextView) contentView.findViewById(R.id.tv_sound_code);
        btnGetVcode = (Button) contentView.findViewById(R.id.btn_register_get_vcode);
        btnGetVcode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String phone = etPhoneNum.getText().toString();
                if (btnGetVcode.isEnabled())
                {
                    if (!CommonUtils.verifyInput(3, phone))
                    {
                        ToastUtils.showShort( "请输入正确的手机号");
                    } else
                    {
                        Logger.t(TAG).d("执行了");
                        btnGetVcode.setEnabled(false);
                        btnGetVcode.setTextColor(ContextCompat.getColor(mAct, R.color.FC7));
                        ttMsgCountDown = new SecurityCountDown();
                        MsgTimer.scheduleAtFixedRate(ttMsgCountDown, 0, 1000);
                        //发送验证码
                        NetHelper.getSecurityCodeMsg(mAct, phone, "2", null, new IGetSecurityCodeListener()
                        {
                            @Override
                            public void onSuccess()
                            {
                                ToastUtils.showShort("发送成功，验证码有效时间15分钟！");
                            }

                            @Override
                            public void onFailed(String errorCode)
                            {
                                resetGetSecurityButton();
                            }
                        });
                    }
                }
            }
        });

        tvSoundCode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String phone = etPhoneNum.getText().toString();
                if (!CommonUtils.verifyInput(3, phone))
                {
                    ToastUtils.showShort("请输入正确的手机号");
                } else
                {
                    if (tvSoundCode.isEnabled())
                    {
                        ttAudioMsgCountDown = new SoundCodeCountDown();
                        AudioTimer.scheduleAtFixedRate(ttAudioMsgCountDown, 0, 1000);
                        tvSoundTip.setText(getString(R.string.voiceCode));
                        tvSoundCode.setEnabled(false);
                        tvSoundCode.setTextColor(ContextCompat.getColor(mAct, R.color.C0323));
                        NetHelper.getVoiceCodeMsg(mAct, phone, "2", null, new IGetVoiceCodeListener()
                        {
                            @Override
                            public void onSuccess()
                            {

                            }

                            @Override
                            public void onFailed(String errorCode)
                            {
                                resetGetVoiceCodeButton();
                            }
                        });
                    }

                }
            }
        });

        new DialogWith2BtnAtBottom(mAct)
                .buildDialog(mAct)
                .setDialogTitle("修改预定手机号", false)
                .setContent(contentView)
                .setCancelBtnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

/*                        if (tt != null)
                            tt.cancel();*/
                    }
                }).setCommitBtnClickListener(new DialogWith2BtnAtBottom.OnDialogWithPositiveBtnListener()
        {
            @Override
            public void onPositiveBtnClick(View view, Dialog dialog)
            {
                if (mPresenter != null)
                {
                    String phone = etPhoneNum.getText().toString();
                    String SecurityCode = etSecurityCode.getText().toString();
                    if (TextUtils.isEmpty(phone))
                    {
                        ToastUtils.showShort( "请输入手机号");
                        return;
                    }
                    if (TextUtils.isEmpty(SecurityCode))
                    {
                        ToastUtils.showShort( "请输入验证码");
                        return;
                    }
                    mPresenter.validSecurityCode(phone, SecurityCode, "2", dialog);
                }
            }
        }).show();
    }

    /**
     * 处理哪些日期不可以预定,(只有周六日可以按天设置，平时只能按时间)
     *
     * @param selectedDate
     * @return
     */
    private String handleCantOrderTime(Date selectedDate)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //判断选择日期是否超出可预定时间
        try
        {
            if (DateUtils.isAfterDay(selectedDate, sdf.parse(endDate)))
            {
                return "1";
                //return String.format("此商暂时只支持%s是之前的预定",endDate);
            }
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        //判断周末,1不可预定
        if (weekFlg.equals("1"))
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedDate);
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                    cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            {
                return "3";
            }
        }
        return "0";
    }

    /**
     * 显示日期选择器
     *
     * @param title
     * @param layoutResId
     */
    private void showCalendarInDialog(String title, int layoutResId)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final Dialog dialog = new Dialog(mAct, R.style.AlertDialogStyle);
        Date endDate = null;
        try
        {
            endDate = sdf.parse(this.endDate);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        Calendar endTime = Calendar.getInstance();
        if (endDate == null)
            return;
        endTime.setTime(endDate);
        endTime.add(Calendar.DATE, 1);
        Calendar startTime = Calendar.getInstance();
        Calendar firstSelectedTime = Calendar.getInstance();
        if (!TextUtils.isEmpty(bootyCallDate))
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            try
            {
                Date date = simpleDateFormat.parse(bootyCallDate);
                firstSelectedTime.setTime(date);
                firstSelectedTime.add(Calendar.DATE, 0);
            } catch (ParseException e)
            {
                e.printStackTrace();
            }

        } else
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try
            {
                Logger.t(TAG).d("getSelectedDate()-----------------> " + getSelectedDate());
                Date date = simpleDateFormat.parse(getSelectedDate());
                firstSelectedTime.setTime(date);
                firstSelectedTime.add(Calendar.DATE, 0);
            } catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
        View contentView = LayoutInflater.from(mAct).inflate(layoutResId, null);
        final CalendarPickerView dialogView = (CalendarPickerView) contentView.findViewById(R.id.calendar_view);

        dialogView.init(startTime.getTime(), endTime.getTime())
                .inMode(CalendarPickerView.SelectionMode.SINGLE)
                .withSelectedDate(firstSelectedTime.getTime());

        dialogView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener()
        {
            @Override
            public void onDateSelected(Date date)
            {
                //如果选择的时间是休息时间
                if (handleCantOrderTime(date).equals("2"))
                {
                    ToastUtils.showShort( "当日商家休息");
                    return;
                } else if (handleCantOrderTime(date).equals("3"))
                {
                    ToastUtils.showShort( "商家不支持周末预定");
                    return;
                } else
                {
                    Logger.t(TAG).d("选择是今天");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    if (checkDateCanReserve(date))
                        setTimeBar(calendar);
                    dialog.dismiss();
                }
            }

            @Override
            public void onDateUnselected(Date date)
            {

            }
        });
        dialogView.setOnInvalidDateSelectedListener(new CalendarPickerView.OnInvalidDateSelectedListener()
        {
            @Override
            public void onInvalidDateSelected(Date date)
            {
                //ToastUtils.showShort(mAct,new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        });

        dialog.setContentView(contentView);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (CommonUtils.getScreenSize(SelectTableAct.this).width * 0.85f);
        //lp.height = CommonUtils.dp2px(mAct,300);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }


    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mAct, null, exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getRestoreDaysCallback(String response)
    {
        try
        {
            JSONObject results = new JSONObject(response);
            endDate = results.getString("endDate");
            weekFlg = results.getString("weekFlg");
            restTime = results.getString("restDate");
            //advDay = results.getString("advDay");
            initDateBar();

        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).e(e.getMessage());
        } finally
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }


    @Override
    public void getOpenTimePeriodDataCallback(String response, OperateType type)
    {
        try
        {
            JSONArray body = new JSONArray(response);
            List<TimePeriodModel> periodLst = new ArrayList<TimePeriodModel>();
            for (int i = 0; i < body.length(); i++)
            {
                TimePeriodModel tpm = new TimePeriodModel();
                tpm.setStatus(body.getJSONObject(i).getString("stat"));
                tpm.setTimeStr(body.getJSONObject(i).getString("time"));
                periodLst.add(tpm);
            }
            if (OperateType.CurrentPeriod == type)
                showTimeDialog(periodLst);
            else if (OperateType.InitPeriod == type)
                getInitTimePeriod(periodLst);
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).e(e.getMessage());
        } finally
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    @Override
    public void getTableStatusFromSeverCallback(String response)
    {
        try
        {
            JSONObject body = new JSONObject(response);
            Map<String, String> tmpMap = new HashMap<String, String>();
            tmpMap.put("mobile", body.getString("uMobile"));
            tmpMap.put("name", body.getString("uName"));
            tmpMap.put("gender", body.getString("uSex"));
            setUiContent(tmpMap);
            JSONArray resLayouts = body.getJSONArray("restrLayouts");
            //一层楼的数据
            JSONObject resLayout = resLayouts.getJSONObject(0);
            JSONArray tableStatus = resLayout.getJSONArray("tableStatusBeen");
            //保存要刷新的桌子状态
            List<HashMap<String, String>> lst = new ArrayList<>();
            for (int i = 0; i < tableStatus.length(); i++)
            {
                JSONObject tableStatusBean = tableStatus.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("tNum", tableStatusBean.getString("tNum"));
                map.put("tStatus", tableStatusBean.getString("tStatus"));

                //把状态是0的桌子，根据是否选中设置为2
                //如果桌子已经被占了，则从选中的集合中去掉
                for (TableBean tb : selectedTables)
                {
                    if (tb.getTableId().equals(tableStatusBean.getString("tNum")) &&
                            tableStatusBean.getString("tStatus").equals("0"))
                    {
                        map.put("tNum", tb.getTableId());
                        map.put("tStatus", tb.getStatus());
                        break;
                        //selectedTables.remove(tb);
                    }
                }
                lst.add(map);
            }
            //根据后台返回的状态来设置桌子
            setTablesStatus(lst);

        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).e(e.getMessage());
        } finally
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    @Override
    public void validSecurityCodeCallback(String response, String newPhoneNum, Dialog dialog)
    {
        //*******发布时放开*********
        tvOrderPhone.setText(newPhoneNum);
        Logger.t(TAG).d("手机号修改成功");
        dialog.dismiss();
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    private class TimePopWindow extends PopupWindow
    {
        private Activity mContext;
        private List<TimePeriodModel> periodLst;
        private String selectText;
        private TimePopWindow timePopWindow;

        /**
         * Instantiates a new Time pop window.
         *
         * @param context   the context
         * @param periodLst the period lst
         */
        public TimePopWindow(Activity context, List<TimePeriodModel> periodLst)
        {
            this.mContext = context;
            this.periodLst = periodLst;
            timePopWindow = this;
            initWindow();
        }

        private void initWindow()
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            AutoLinearLayout contentView = (AutoLinearLayout) inflater.inflate(R.layout.res_period_popup, null);
            GridView gridview = (GridView) contentView.findViewById(R.id.gv_select_time_period);
            TextView tvCancel = (TextView) contentView.findViewById(R.id.tv_cancel);
            Button btnCommit = (Button) contentView.findViewById(R.id.btn_commit);
            btnCommit.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (!TextUtils.isEmpty(selectText))
                        tvTimePeriod.setText(selectText);
                   /* Logger.t(TAG).d("选择的时间为2》"+ tvTimePeriod.getText().toString());*/
                    setLayoutInfo(resId, floorNum, String.format("%s %s:00", getSelectedDate(), tvTimePeriod.getText().toString()));
                    dismiss();
                }
            });
            tvCancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dismiss();
                }
            });
            final SelectTimePeriodAdapter stpAdapter = new SelectTimePeriodAdapter(mContext, periodLst);
            gridview.setAdapter(stpAdapter);

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    if (!periodLst.get(position).getStatus().equals("0"))
                    {
                        if (selectedTables.size() != 0)
                        {
                            CustomAlertDialog customAlertDialog = new CustomAlertDialog(mContext)
                                    .builder()
                                    .setMsg("修改订餐时间将需要重新选桌")
                                    .setTitle("提示")
                                    .setCancelable(false)
                                    .setPositiveBtnClickListener("确定", new CustomAlertDialog.OnDialogWithPositiveBtnListener()
                                    {
                                        @Override
                                        public void onPositiveBtnClick(View view, Dialog dialog)
                                        {
                                            selectText = periodLst.get(position).getTimeStr();
                                            if (!TextUtils.isEmpty(selectText))
                                                tvTimePeriod.setText(selectText);
                                            selectedTables.clear();
                                            refreshSelectStatus();
                                            setLayoutInfo(resId, floorNum, String.format("%s %s:00", getSelectedDate(), tvTimePeriod.getText().toString()));
                                            timePopWindow.dismiss();
                                            dialog.dismiss();
                                        }
                                    })
                                    .setCancelBtnClickListener("取消", new CustomAlertDialog.OnDialogWithNavigateBtnListener()
                                    {
                                        @Override
                                        public void onNavigateBtnClick(View view, Dialog dialog)
                                        {
                                            dialog.dismiss();
                                        }
                                    });
                            customAlertDialog.show();
                        }else {
                            selectText = periodLst.get(position).getTimeStr();
                            if (!TextUtils.isEmpty(selectText))
                                tvTimePeriod.setText(selectText);
                            selectedTables.clear();
                            refreshSelectStatus();
                            setLayoutInfo(resId, floorNum, String.format("%s %s:00", getSelectedDate(), tvTimePeriod.getText().toString()));
                            timePopWindow.dismiss();
                        }
                    }
                }
            });
            // 设置SelectPicPopupWindow的View
            this.setContentView(contentView);
            // 设置SelectPicPopupWindow弹出窗体的宽
            this.setWidth(CommonUtils.getScreenSize(mContext).width);
            // 设置SelectPicPopupWindow弹出窗体的高
            this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            // 设置SelectPicPopupWindow弹出窗体可点击
            this.setFocusable(true);
            this.setOutsideTouchable(true);
            // 刷新状态
            this.update();
            this.backgroundAlpha(0.5f);
            // 设置SelectPicPopupWindow弹出窗体动画效果
            this.setAnimationStyle(R.style.AnimationPreview);
        }

        /**
         * 显示popupWindow
         *
         * @param parent the parent
         */
        public void showPopupWindow(View parent)
        {
            if (!this.isShowing())
            {
                this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            } else
            {

                this.dismiss();
            }
        }

        /**
         * 设置添加屏幕的背景透明度
         *
         * @param bgAlpha the bg alpha
         */
        public void backgroundAlpha(float bgAlpha)
        {
            WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
            lp.alpha = bgAlpha; // 0.0-1.0
            mContext.getWindow().setAttributes(lp);
        }
    }

    /**
     * 关闭弹窗，回复界面透明度
     */
    private static class popupDismissListener implements PopupWindow.OnDismissListener
    {
        private TimePopWindow popupWindow;

        /**
         * Instantiates a new Popup dismiss listener.
         *
         * @param popupWindow the popup window
         */
        public popupDismissListener(TimePopWindow popupWindow)
        {
            this.popupWindow = popupWindow;
        }

        @Override
        public void onDismiss()
        {
            popupWindow.backgroundAlpha(1f);
        }
    }


    /**
     * The type My table label click listener.
     */
    class MyTableLabelClickListener implements View.OnClickListener
    {
        private TableBean te;
        private int tableNum = 1;

        /**
         * Instantiates a new My table label click listener.
         *
         * @param tableBean the table bean
         */
        public MyTableLabelClickListener(TableBean tableBean)
        {
            this.te = tableBean;
        }

        /**
         * Instantiates a new My table label click listener.
         *
         * @param tableNum the table num
         */
        public MyTableLabelClickListener(int tableNum)
        {
            this.tableNum = tableNum;
        }

        /**
         * Instantiates a new My table label click listener.
         */
        public MyTableLabelClickListener()
        {
        }

        /**
         * Sets table bean.
         *
         * @param tableBean the table bean
         */
        public void setTableBean(TableBean tableBean)
        {
            this.te = tableBean;
        }

        @Override
        public void onClick(View v)
        {
            Logger.t(TAG).d("点击事件" + te.toString());
            if (te.getStatus().equals("2"))
            {
                te.setStatus("0");
                //te.setFloorNumber("");
                selectedTables.remove(te);

                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(CdnHelper.getInstance().getMaterialUrl(te.getPicName() + te.getStatus(), mAct))
                        .skipMemoryCache(true)
                        .into(new SimpleTarget<Bitmap>((int) te.getWidth2(), (int) te.getHeight2())
                        {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation)
                            {
                                Logger.t(TAG).d("点击的桌子1》》" + te.toString());
                                Bitmap bitmap = ImageUtils.rotateImageView(te.getAngle2(), resource);
                                te.setBitImg(bitmap);
                                stvSelectTable.dataSourceChanged(tableEntities);
                                for (TextView tableLabel : tableLabels)
                                {
                                    tableLabel.setVisibility(View.GONE);
                                }
                                for (int i = 0; i < selectedTables.size(); i++)
                                {
                                    TextView tv = tableLabels.get(i);
                                    TableBean te1 = selectedTables.get(i);
                                    tv.setText(String.format("%s层%s(%s人)", te1.getFloorNumber(), te1.getTableName(), te1.getTableType().replaceFirst("^0+(?!$)", "")));
                                    tv.setTag(resId + te1.getFloorNumber() + CommonUtils.SEPARATOR + te1.getTableId() + CommonUtils.SEPARATOR + te1.getTableType().replaceFirst("^0+(?!$)", ""));
                                    tv.setVisibility(View.VISIBLE);
                                    MyTableLabelClickListener tableLabelClick = new MyTableLabelClickListener(te1);
                                    tv.setOnClickListener(tableLabelClick);
                                }
                            }
                        });
            }
        }

    }

    private class SecurityCountDown extends TimerTask
    {
        @Override
        public void run()
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    btnGetVcode.setText(String.format("(%s)秒", String.valueOf(MsgCountdown--)));
                    if (MsgCountdown < 1)
                    {
                        resetGetSecurityButton();
                    }
                }
            });
        }
    }

    private void resetGetSecurityButton()
    {
        btnGetVcode.setText("获取验证码");
        btnGetVcode.setEnabled(true);
        btnGetVcode.setTextColor(ContextCompat.getColor(mAct, R.color.MC7));
        MsgCountdown = 60;
        if (ttMsgCountDown != null)
            ttMsgCountDown.cancel();
        //MsgTimer.cancel();
        //MsgTimer.purge();
    }

    /**
     * 语音验证码倒计时
     */
    private class SoundCodeCountDown extends TimerTask
    {
        @Override
        public void run()
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    tvSoundCode.setText(String.format("剩余%s秒", String.valueOf(AudioCountdown--)));
                    if (AudioCountdown < 1)
                    {
                        resetGetVoiceCodeButton();
                    }
                }
            });
        }
    }

    /**
     * 重置语音验证码获取按钮
     */
    private void resetGetVoiceCodeButton()
    {
        tvSoundTip.setText(getString(R.string.tryOtherStyle));
        tvSoundCode.setText(getString(R.string.voiceCode));
        tvSoundCode.setEnabled(true);
        tvSoundCode.setTextColor(ContextCompat.getColor(mAct, R.color.C0311));
        AudioCountdown = 60;
        if (ttAudioMsgCountDown != null)
            ttAudioMsgCountDown.cancel();
        //MsgTimer.cancel();
        //MsgTimer.purge();
    }

}

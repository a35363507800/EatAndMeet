package com.echoesnet.eatandmeet.views.widgets.DateWheelPicker;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.SelectWheelPicker.ISelectItemFinishListener;
import com.echoesnet.eatandmeet.views.widgets.cascadeWheelView.ArrayWheelAdapter;
import com.echoesnet.eatandmeet.views.widgets.cascadeWheelView.OnWheelChangedListener;
import com.echoesnet.eatandmeet.views.widgets.cascadeWheelView.WheelView;
import com.orhanobut.logger.Logger;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by wangben on 2016/6/6.
 * 请使用DatePicker 替代
 */
@Deprecated
public class DateWheelPicker extends PopupWindow
{
    private Activity mContext;
    private ISelectItemFinishListener mSelectItemFinishListener;
    int selectIndex = -1;
    WheelView wv_month, wv_day ,wv_year;
   // TextView titleYear, titleMonth, titleDay, titleHour;
    List<String> list_big;
    List<String> list_little;
    int year, month, day;
    private String DateStr;

    //是否是最近想去选择时间
    private boolean flag = false;
    private WheelView wv_time;

    public DateWheelPicker(Activity context)
    {
        this.mContext = context;
        initWindow();
    }

    public DateWheelPicker(Activity context, Boolean flag)
    {
        this.mContext = context;
        this.flag = flag;
        initWindow();
    }

    private void initWindow()
    {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DATE);

        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
        String[] months_little = {"4", "6", "9", "11"};

        list_big = Arrays.asList(months_big);
        list_little = Arrays.asList(months_little);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View contentView = inflater.inflate(R.layout.date_wheel_picker, null);
        wv_year = (WheelView) contentView.findViewById(R.id.wv_year);
        wv_month = (WheelView) contentView.findViewById(R.id.wv_month);
        wv_day = (WheelView) contentView.findViewById(R.id.wv_day);
        wv_time = (WheelView) contentView.findViewById(R.id.wv_hour);
//        titleYear = (TextView) contentView.findViewById(R.id.title_year);
//        titleMonth = (TextView) contentView.findViewById(R.id.title_month);
//        titleDay = (TextView) contentView.findViewById(R.id.title_day);
//        titleHour = (TextView) contentView.findViewById(R.id.title_hour);
        if (flag)
        {
//            titleHour.setVisibility(View.VISIBLE);
            wv_time.setVisibility(View.VISIBLE);
        }
        TextView btnOk = (TextView) contentView.findViewById(R.id.tv_ok);
        TextView tvCancel = (TextView) contentView.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    DateStr = getSelectedDate(wv_year, wv_month, wv_day, wv_time);
                    if (flag)
                    {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh");
                        Date today = new Date();
                        if (sdf.parse(DateStr).getTime() > today.getTime())
                        {
                            mSelectItemFinishListener.finishSelect(selectIndex, DateStr);
                            dismiss();
                        }
                        else
                        {
                            ToastUtils.showShort("您所选择的日期不合法");
                        }
                    }
                    else
                    {
                        dismiss();
                        if (!TextUtils.isEmpty(DateStr))
                            mSelectItemFinishListener.finishSelect(selectIndex, DateStr);
                    }
                } catch (ParseException e)
                {
                    e.printStackTrace();
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
        this.setAnimationStyle(R.style.PopupAnimation);

        contentView.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                int height = contentView.findViewById(R.id.arl_top).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    if (y < height)
                    {
                        dismiss();
                    }
                }
                return true;
            }
        });
        setUpData();
        setUpListener();
    }

    private String getSelectedDate(WheelView wv_year, WheelView wv_month, WheelView wv_day, WheelView wv_time)
    {
        int[] dateTime = new int[]{2010, 1, 1, 1};
        // 如果是个数,则显示为"02"的样式
        String parten = "00";
        DecimalFormat decimal = new DecimalFormat(parten);
        dateTime[0] = wv_year.getCurrentItem() + START_YEAR;
        Logger.t("生日").d("shengti--> " + dateTime[0] + ", " + wv_year.getCurrentItem() + " , " + START_YEAR);
        dateTime[1] = Integer.parseInt(decimal.format((wv_month.getCurrentItem() + 1)));
        dateTime[2] = Integer.parseInt(decimal.format((wv_day.getCurrentItem() + 1)));
        if (flag)
        {
            dateTime[3] = Integer.parseInt(decimal.format((wv_time.getCurrentItem())));
            return dateTime[0] + "年" + (dateTime[1] < 10 ? ("0" + dateTime[1]) : dateTime[1]) + "月" + (dateTime[2] < 10 ? ("0" + dateTime[2]) : dateTime[2]) + "日" + dateTime[3];
        }
        Logger.t("生日选择器").d(Integer.toString(dateTime[0]) + "-" + Integer.toString(dateTime[1]) + "-" + Integer.toString(dateTime[2]) + "-" + Integer.toString(dateTime[3]));
        return dateTime[0] + "年" + (dateTime[1] < 10 ? ("0" + dateTime[1]) : dateTime[1]) + "月" + (dateTime[2] < 10 ? ("0" + dateTime[2]) : dateTime[2]) + "日";
    }

    public void setOnSelectFinishListener(ISelectItemFinishListener mSelectItemFinishListener)
    {
        this.mSelectItemFinishListener = mSelectItemFinishListener;
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent)
    {
        if (!this.isShowing())
        {
            this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        }
        else
        {
            this.dismiss();
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        mContext.getWindow().setAttributes(lp);
    }

    public void setUpListener()
    {
        // 添加change事件
        wv_year.addChangingListener(new MyOnWheelChangeListener());
        // 添加change事件
        wv_month.addChangingListener(new MyOnWheelChangeListener());
    }

    public void setUpData()
    {
        initDateData();
        wv_year.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mYears));
        wv_month.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mMonth));
        wv_time.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mHours));
        // 设置可见条目数量
        wv_year.setVisibleItems(3);
        wv_month.setVisibleItems(3);
        wv_day.setVisibleItems(3);
        wv_time.setVisibleItems(3);
        wv_year.setCyclic(true);
        wv_month.setCyclic(true);
        wv_day.setCyclic(true);
        wv_time.setCyclic(true);
        initDay();
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void initDay()
    {
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (list_big.contains(String.valueOf(month + 1)))
        {
            wv_day.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mMonth31));
        }
        else if (list_little.contains(String.valueOf(month + 1)))
        {
            wv_day.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mMonth30));
        }
        else
        {
            // 闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
                wv_day.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mMonth29));
            else
                wv_day.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mMonth28));
        }
        wv_day.setCurrentItem(day - 1);
        wv_month.setCurrentItem(month);
//        wv_year.setCurrentItem(1990);
//        wv_year.setCurrentItem(0);
        wv_year.setCurrentItem(yearLst.size() / 2 + 20);
    }

    public void setSelectIndex(int year, int month, int day)
    {
        wv_year.setCurrentItem(year);
        wv_month.setCurrentItem(month);
        wv_day.setCurrentItem(day);
    }

    public static void setStartAndEndYear(int startYear, int endYear)
    {
        START_YEAR = startYear;
        END_YEAR = endYear;
    }

    public static void setStartYear(int startYear)
    {
        START_YEAR = startYear;
    }

    public static void setEndYear(int endYear)
    {
        END_YEAR = endYear;
    }

    private static int START_YEAR = 1930, END_YEAR = 2010;

    private String[] mYears;
    private String[] mMonth;
    private String[] mMonth28;
    private String[] mMonth29;
    private String[] mMonth30;
    private String[] mMonth31;
    private String[] mHours;

    ArrayList<String> yearLst;
    ArrayList<String> hourLst;

    private void initDateData()
    {
        yearLst = new ArrayList<>();
        for (int i = START_YEAR; i < END_YEAR; i++)
        {
            yearLst.add(String.valueOf(i)+"年");
        }
        mYears = new String[yearLst.size()];
        yearLst.toArray(mYears);

        ArrayList<String> monthLst = new ArrayList<>();
        for (int i = 1; i <= 12; i++)
        {
            monthLst.add(String.valueOf(i)+"月");
        }
        mMonth = new String[monthLst.size()];
        monthLst.toArray(mMonth);

        ArrayList<String> month28Lst = new ArrayList<>();
        for (int i = 1; i <= 28; i++)
        {
            month28Lst.add(String.valueOf(i)+"日");
        }
        mMonth28 = new String[month28Lst.size()];
        month28Lst.toArray(mMonth28);

        ArrayList<String> month29Lst = new ArrayList<>();
        for (int i = 1; i <= 29; i++)
        {
            month29Lst.add(String.valueOf(i)+"日");
        }
        mMonth29 = new String[month29Lst.size()];
        month29Lst.toArray(mMonth29);

        ArrayList<String> month30Lst = new ArrayList<>();
        for (int i = 1; i <= 30; i++)
        {
            month30Lst.add(String.valueOf(i)+"日");
        }
        mMonth30 = new String[month30Lst.size()];
        month30Lst.toArray(mMonth30);

        ArrayList<String> month31Lst = new ArrayList<>();
        for (int i = 1; i <= 31; i++)
        {
            month31Lst.add(String.valueOf(i)+"日");
        }
        mMonth31 = new String[month31Lst.size()];
        month31Lst.toArray(mMonth31);

        hourLst = new ArrayList<>();
        for (int i = 0; i < 24; i++)
        {
            // 0 代表前面补充0
            // 4 代表长度为4
            // d 代表参数为正数型
            hourLst.add(String.format("%02d", i));
/*            if(i<10)
                hourLst.add("0"+String.valueOf(i));
            else
                hourLst.add(String.valueOf(i));*/
        }
        mHours = new String[hourLst.size()];
        hourLst.toArray(mHours);
    }


    private class MyOnWheelChangeListener implements OnWheelChangedListener
    {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue)
        {
            if (wheel == wv_year)
            {
                int year_num = newValue + START_YEAR;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big.contains(String.valueOf(wv_month.getCurrentItem() + 1)))
                {
                    wv_day.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mMonth31));

                }
                else if (list_little.contains(String.valueOf(wv_month
                        .getCurrentItem() + 1)))
                {
                    wv_day.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mMonth30));
                }
                else
                {
                    if ((year_num % 4 == 0 && year_num % 100 != 0)
                            || year_num % 400 == 0)
                        wv_day.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mMonth29));
                    else
                        wv_day.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mMonth28));
                }
                wv_day.setCurrentItem(0);
            }
            else if (wheel == wv_month)
            {
                int month_num = newValue + 1;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big.contains(String.valueOf(month_num)))
                {
                    wv_day.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mMonth31));
                }
                else if (list_little.contains(String.valueOf(month_num)))
                {
                    wv_day.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mMonth30));
                }
                else
                {
                    if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
                            .getCurrentItem() + START_YEAR) % 100 != 0)
                            || (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
                        wv_day.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mMonth29));
                    else
                        wv_day.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mMonth28));
                }
            }
        }
    }
}

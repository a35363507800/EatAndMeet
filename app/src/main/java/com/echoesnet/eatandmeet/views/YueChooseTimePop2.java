package com.echoesnet.eatandmeet.views;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.DateBaseInfoBean;
import com.echoesnet.eatandmeet.models.bean.DateBaseInfoCopyBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.SpacesItemDecoration;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.DateInfoRvTimeAdapter;

import java.security.Key;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lc on 2017/7/17 11.
 */

public class YueChooseTimePop2 extends PopupWindow implements View.OnClickListener
{
    private Activity mAct;
    private IFinishedSelectPeriodsListener mSelectItemFinishListener;
    private String selectedPeriods = "";

    private int mYear;
    private int mMonth;
    private int mDay;

    private RecyclerView rvYueView;
    private List<DateBaseInfoBean> List;
    private List<DateBaseInfoCopyBean> copyList;

    public YueChooseTimePop2(Activity act)
    {
        this.mAct = act;
        initWindow();
    }

    public YueChooseTimePop2(Activity act, List<DateBaseInfoBean> mList, List<DateBaseInfoCopyBean> copyList)
    {
        this.mAct = act;
        this.List = mList;
       this.copyList = copyList;
        initWindow();
    }

    private String timesStr[] = {"今天", "明天", "后天", "大后天"};

    private void initWindow()
    {
        LayoutInflater inflater = (LayoutInflater) mAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.pop_show_time_yue_2, null);
        TextView btnOk = (TextView) popupView.findViewById(R.id.btn_ok);
        rvYueView = (RecyclerView) popupView.findViewById(R.id.rv_yue_view);

        for (int i = 0; i < List.size(); i++)
        {
            DateBaseInfoBean itemBean = List.get(i);
            itemBean.setDate(timesStr[i] + "\n—\n" +(itemBean.getDate().substring(4, 6) + "/" + itemBean.getDate().substring(itemBean.getDate().length()-2,itemBean.getDate().length())));
        }
 //=================================================================================================================================================

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR); //获取当前年份
        mMonth = c.get(Calendar.MONTH);//获取当前月份
        mDay = c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mAct);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvYueView.setLayoutManager(linearLayoutManager);
        DateInfoRvTimeAdapter adapter = new DateInfoRvTimeAdapter(mAct, List);
        adapter.setOnItemClickListener(new DateInfoRvTimeAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                selectedPeriods = copyList.get(position).getDate();
            }
        });
        //RecycleView 增加边距
//            int spacingInPixels = CommonUtils.dp2px(mAct,24);
//            rvYueView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        rvYueView.setAdapter(adapter);


        //默认选择为19:30  ---  00:00
        for (int i = 0;i<copyList.size();i++)
        {
            if (copyList.get(i).getStatus().equals("0"))
            {
                selectedPeriods = copyList.get(i).getDate();
                break;
            }
        }

        btnOk.setOnClickListener(this);
        // 设置SelectPicPopupWindow的View
        this.setContentView(popupView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(CommonUtils.getScreenSize(mAct).width);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.PopupAnimation);
        //让pop可以点击外面消失掉
        this.setBackgroundDrawable(new ColorDrawable(0));
        this.setTouchInterceptor(new View.OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
                {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 显示弹窗列表界面
     */
    public void show()
    {
        this.backgroundAlpha(0.5f);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = mAct.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        mAct.getWindow().setAttributes(lp);
    }


    /**
     * 获取前n天日期、后n天日期
     *
     * @param distanceDay 前几天 如获取前7天日期则传-7即可；如果后7天则传7
     * @return
     */
    public String getOldDate(int distanceDay)
    {
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMMdd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        Date endDate = null;
        try
        {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return dft.format(endDate);
    }

    public void setOnSelectFinishListener(IFinishedSelectPeriodsListener mSelectItemFinishListener)
    {
        this.mSelectItemFinishListener = mSelectItemFinishListener;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_ok:
                if (TextUtils.isEmpty(selectedPeriods))
                {
                    ToastUtils.showShort("请选择可约会时间");
                    break;
                }
                mSelectItemFinishListener.selectPeriodsFinish(selectedPeriods);
                dismiss();
                break;

        }
    }

//        private void selectTime(int position)
//        {
//            TextView tv = ()rvYueView.getChildAt(position);
//
//
//                for (int i = 0; i < tvViews.length; i++)
//                {
//                    if (i == position)
//                    {
//                        if (tv.isSelected())
//                        {
//                            tv.setSelected(false);
//                        }
//                        else
//                        {
//                            tv.setSelected(true);
//                        }
//                    }
//                    else
//                    {
//                        tvViews[i].setSelected(false);
//                    }
//                }
//        }

    public interface IFinishedSelectPeriodsListener
    {
        void selectPeriodsFinish(String periods);
    }

    /**
     * 消失弹窗，设置添加屏幕的背景透明度
     */
    public void dismissPop()
    {
        this.backgroundAlpha(1f);
    }
}

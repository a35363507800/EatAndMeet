package com.echoesnet.eatandmeet.views.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yqh on 2017/2/22.
 */

public class LiveChooseAppointmentTimePop extends PopupWindow implements View.OnClickListener
{

    private Activity mAct;
    private IFinishedSelectPeriodsListener mSelectItemFinishListener;
    private TextView tvTimeOne;
    private TextView tvTimeTwo;
    private TextView tvTimeThree;
    private TextView tvTimeFour;
    private List<String> selectedPeriods = new ArrayList<>();

    private String[] time = new String[]{"11:30 - 13:30 ", "17:30 - 19:30 ", "19:30 - 00:00", "不接受"};
    private TextView[] tvViews;

    public LiveChooseAppointmentTimePop(Activity act)
    {
        this.mAct = act;
        initWindow();
    }

    private void initWindow()
    {
        LayoutInflater inflater = (LayoutInflater) mAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.live_choose_appoint_time_popup, null);
        Button btnOk = (Button) popupView.findViewById(R.id.btn_ok);
        tvTimeOne = (TextView) popupView.findViewById(R.id.tv_time_one);
        tvTimeTwo = (TextView) popupView.findViewById(R.id.tv_time_two);
        tvTimeThree = (TextView) popupView.findViewById(R.id.tv_time_three);
        tvTimeFour = (TextView) popupView.findViewById(R.id.tv_time_four);
        tvViews = new TextView[]{tvTimeOne, tvTimeTwo, tvTimeThree, tvTimeFour};
        for (TextView llView : tvViews)
        {
            llView.setOnClickListener(this);
        }
        //默认选择为19:30  ---  00:00
        tvTimeThree.setSelected(true);
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
                selectedPeriods.clear();
                for (int i = 0; i < tvViews.length; i++)
                {
                    TextView tv = tvViews[i];
                    if (tv.isSelected())
                        selectedPeriods.add(time[i]);
                }
                if (selectedPeriods.size() == 0)
                {
                    ToastUtils.showShort( "请选择可约会时间");
                    break;
                }
                mSelectItemFinishListener.selectPeriodsFinish(selectedPeriods);
                dismiss();
                break;
            case R.id.tv_time_one:
                selectTime(0);
                break;
            case R.id.tv_time_two:
                selectTime(1);
                break;
            case R.id.tv_time_three:
                selectTime(2);
                break;
            case R.id.tv_time_four:
                selectTime(3);
                break;
        }
    }

    private void selectTime(int position)
    {
        TextView tv = tvViews[position];
        if (tv.isSelected())
        {
            tv.setSelected(false);
        }
        else
        {
            if (position != 3 && tvViews[3].isSelected())
                tvViews[3].setSelected(false);
            tv.setSelected(true);
        }
        if (position == tvViews.length - 1)
        {
            for (int i = 0; i < tvViews.length - 1; i++)
            {
                tvViews[i].setSelected(false);
            }
        }
        //以下代码控制不选时间就选择不接受约会
        int k = 0;
        for (int i = 0; i < tvViews.length - 1; i++)
        {
            if (tvViews[i].isSelected())
            {
                k++;
            }
        }
        if (k == 0)
        {
            for (int i = 0; i < tvViews.length; i++)
            {
                if (i == 3)
                    tvViews[i].setSelected(true);
                else
                    tvViews[i].setSelected(false);
            }
        }
    }

    public interface IFinishedSelectPeriodsListener
    {
        void selectPeriodsFinish(List<String> periods);
    }
}

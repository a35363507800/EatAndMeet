package com.echoesnet.eatandmeet.views;


import android.widget.PopupWindow;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lc on 2017/7/17 11.
 */

public class YueChooseTimePop extends PopupWindow implements View.OnClickListener
{
        private Activity mAct;
        private IFinishedSelectPeriodsListener mSelectItemFinishListener;
        private TextView tvTimeOne;
        private TextView tvTimeTwo;
        private TextView tvTimeThree;
        private TextView tvTimeFour;
        private List<String> selectedPeriods = new ArrayList<>();

        private int mYear;
        private int mMonth;
        private int mDay;

        private TextView[] tvViews;
        private List<Map<String,String>> mlist;

        public YueChooseTimePop(Activity act)
        {
            this.mAct = act;
            initWindow();
        }

        private void initWindow()
        {
            LayoutInflater inflater = (LayoutInflater) mAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View popupView = inflater.inflate(R.layout.pop_show_time_yue,null);
            TextView btnOk = (TextView) popupView.findViewById(R.id.btn_ok);
            tvTimeOne = (TextView) popupView.findViewById(R.id.tv_time_one);
            tvTimeTwo = (TextView) popupView.findViewById(R.id.tv_time_two);
            tvTimeThree = (TextView) popupView.findViewById(R.id.tv_time_three);
            tvTimeFour = (TextView) popupView.findViewById(R.id.tv_time_four);
            tvViews = new TextView[]{tvTimeOne, tvTimeTwo, tvTimeThree, tvTimeFour};

             final Calendar c = Calendar.getInstance();
             mYear = c.get(Calendar.YEAR); //获取当前年份
             mMonth = c.get(Calendar.MONTH);//获取当前月份
             mDay = c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期

            mlist = new ArrayList<Map<String, String>>();
            Map map1= new HashMap<String,String>();
            map1.put("showtime","今天\n—\n"+(getOldDate(0).substring(4,6)+"/"+getOldDate(0).substring(6)));
            map1.put("datetime",getOldDate(0));
            mlist.add(map1);

            Map map2= new HashMap<String,String>();
            map2.put("showtime","明天\n—\n"+(getOldDate(1).substring(4,6)+"/"+getOldDate(1).substring(6)));
            map2.put("datetime",getOldDate(1));
            mlist.add(map2);

            Map map3= new HashMap<String,String>();
            map3.put("showtime","后天\n—\n"+(getOldDate(2).substring(4,6)+"/"+getOldDate(2).substring(6)));
            map3.put("datetime",getOldDate(2));
            mlist.add(map3);

            Map map4= new HashMap<String,String>();
            map4.put("showtime","大后天\n—\n"+(getOldDate(3).substring(4,6)+"/"+getOldDate(3).substring(6)));
            map4.put("datetime",getOldDate(3));
            mlist.add(map4);

            tvTimeOne.setText(mlist.get(0).get("showtime"));
            tvTimeTwo.setText(mlist.get(1).get("showtime"));
            tvTimeThree.setText(mlist.get(2).get("showtime"));
            tvTimeFour.setText(mlist.get(3).get("showtime"));



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
     * 消失弹窗，设置添加屏幕的背景透明度
     */
    public void dismissPop()
    {
        this.backgroundAlpha(1f);
    }
    /**
     * 获取前n天日期、后n天日期
     *
     * @param distanceDay 前几天 如获取前7天日期则传-7即可；如果后7天则传7
     * @return
     */
    public  String getOldDate(int distanceDay)
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
        }
        catch (ParseException e)
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
                    selectedPeriods.clear();
                    for (int i = 0; i < tvViews.length; i++)
                    {
                        TextView tv = tvViews[i];
                        if (tv.isSelected())
                           // selectedPeriods.add(time[i]);
                            selectedPeriods.add(mlist.get(i).get("datetime"));
                    }
                    if (selectedPeriods.size() == 0)
                    {
                        ToastUtils.showShort("请选择可约会时间");
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


                for (int i = 0; i < tvViews.length; i++)
                {
                    if (i == position)
                    {
                        if (tv.isSelected())
                        {
                            tv.setSelected(false);
                        }
                        else
                        {
                            tv.setSelected(true);
                        }
                    }
                    else
                    {
                        tvViews[i].setSelected(false);
                    }



//
//                    if (tvCanotChoose.get)
//                    {
//
//                    }
//                     tv.setSelected(false);
//                    tv.setBackgroundColor(ContextCompat.getColor(mAct,R.color.C0331));
                }

            //以下代码控制不选时间就选择不接受约会
//            int k = 0;
//            for (int i = 0; i < tvViews.length - 1; i++)
//            {
//                if (tvViews[i].isSelected())
//                {
//                    k++;
//                }
//            }
//            if (k == 0)
//            {
//                for (int i = 0; i < tvViews.length; i++)
//                {
//                    if (i == 3)
//                        tvViews[i].setSelected(true);
//                    else
//                        tvViews[i].setSelected(false);
//                }
//            }
        }

        public interface IFinishedSelectPeriodsListener
        {
            void selectPeriodsFinish(List<String> periods);
        }

}

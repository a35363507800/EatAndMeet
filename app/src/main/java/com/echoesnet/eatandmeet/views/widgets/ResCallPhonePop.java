package com.echoesnet.eatandmeet.views.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.zhy.autolayout.AutoLinearLayout;

/**
 * Created by Administrator on 2016/8/13.
 */
public class ResCallPhonePop extends PopupWindow
{
    private Activity mContext;
    View popupView;
    String phoneNum[];
    private View hideView;

    public ResCallPhonePop(Activity context, View.OnClickListener onclickListener, String phoneNum[])
    {
        this.mContext = context;
        this.phoneNum = phoneNum;
        initWindow(onclickListener);
    }

    private void initWindow(View.OnClickListener onclickListener)
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_res_call_phone_pop, null);
        AutoLinearLayout myLlCalls = (AutoLinearLayout) popupView.findViewById(R.id.ll_calls);
        for (int i = 0; i < phoneNum.length; i++)
        {
            TextView textView = new TextView(mContext);
            ViewGroup.LayoutParams params = new  ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CommonUtils.dp2px(mContext,46));
            textView.setLayoutParams(params);
            textView.setTextColor(Color.parseColor("#666666"));
            textView.setText(phoneNum[i]);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(14);
            textView.setId(R.id.res_call_one);
            myLlCalls.addView(textView);

            TextView tvLine = new TextView(mContext);
            AutoLinearLayout.LayoutParams param = new AutoLinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CommonUtils.dp2px(mContext,1));
            tvLine.setLayoutParams(param);
            tvLine.setBackgroundColor(Color.parseColor("#e3e3e3"));
            myLlCalls.addView(tvLine);
            textView.setOnClickListener(onclickListener);
        }


        Button mySetContactUsCancle = (Button) popupView.findViewById(R.id.my_set_contact_us_cancle);


        mySetContactUsCancle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });
// 设置SelectPicPopupWindow的View
        this.setContentView(popupView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(CommonUtils.getScreenSize(mContext).width);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
//        this.backgroundAlpha(0.5f);
        this.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), (Bitmap) null));
        this.setAnimationStyle(R.style.PopupAnimation);
        this.getContentView().setFocusableInTouchMode(true);
        this.getContentView().setFocusable(true);
        popupView.setOnTouchListener(new View.OnTouchListener()
        {

            public boolean onTouch(View v, MotionEvent event)
            {

                int height = popupView.findViewById(R.id.contact_us_pop_model).getTop();
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
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
//    public void showPopupWindow(View parent)
//    {
//        if (!this.isShowing())
//        {
//            this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
//        }
//        else
//        {
//            this.dismiss();
//        }
//    }



    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent, View hideView)
    {
        if (!this.isShowing())
        {
            if (hideView == null)
            {
                backgroundAlpha(0.5f);
            }
            else
            {
                this.hideView = hideView;
                hideView.setVisibility(View.VISIBLE);
            }

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
    private void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        mContext.getWindow().setAttributes(lp);
    }
    @Override
    public void dismiss()
    {
        if (hideView == null)
        {
            backgroundAlpha(1.0f);
        }
        else
        {
            hideView.setVisibility(View.GONE);
        }
        super.dismiss();
    }
}

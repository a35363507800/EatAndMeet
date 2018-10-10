package com.echoesnet.eatandmeet.views.widgets.CommonPopupWindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.echoesnet.eatandmeet.utils.CommonUtils;

/**
 * Created by ben on 2017/3/2.
 * 这是一个通用的弹出框，用户可以自由填充里面的内容
 */

public class CommonPopupWindow extends PopupWindow
{
    private Activity mAct;

    public CommonPopupWindow(Activity mAct) {
        this.mAct = mAct;
        initWindow();
    }


    private void initWindow()
    {
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(CommonUtils.getScreenSize(mAct).width);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setOutsideTouchable(true);
        this.setFocusable(true);
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

    public CommonPopupWindow setView(int viewLayoutId)
    {
        LayoutInflater inflater = (LayoutInflater) mAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(viewLayoutId, null);
        // 设置SelectPicPopupWindow的View
        this.setContentView(popupView);

        return this;
    }

    public void dismiss()
    {
        if (this.isShowing())
            dismiss();
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
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent, int gravity, int x, int y)
    {
        if (!this.isShowing())
        {
            this.showAtLocation(parent, gravity, x, y);
        }
    }
}

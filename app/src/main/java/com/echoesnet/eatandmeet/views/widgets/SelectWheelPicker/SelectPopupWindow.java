package com.echoesnet.eatandmeet.views.widgets.SelectWheelPicker;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.widgets.VerticalWheelView;

import java.util.List;

/**
 * Created by wangben on 2016/5/27.
 */
public class SelectPopupWindow extends PopupWindow
{
    int selectIndex=-1;
    String sItem;
    private Activity mContext;
    private List<String> periodLst;
    private ISelectItemFinishListener mSelectItemFinishListener;

    public SelectPopupWindow(Activity context,List<String> periodLst)
    {
        this.mContext=context;
        this.periodLst=periodLst;
        initWindow();
    }

    private void initWindow()
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View contentView =  inflater.inflate(R.layout.single_vertical_wheel_layout, null);
        VerticalWheelView vwv_content = (VerticalWheelView) contentView.findViewById(R.id.vwv_content);
        vwv_content.setOffset(1);
        vwv_content.setItems(periodLst);
        vwv_content.setOnWheelViewListener(new MyOnWheelViewListener());

        Button btnOk = (Button) contentView.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
                if (selectIndex==-1)
                {
                    selectIndex=0;
                    sItem=periodLst.get(0);
                }
                mSelectItemFinishListener.finishSelect(selectIndex,sItem);
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
                int height = contentView.findViewById(R.id.all_bottom).getTop();
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

    public void setOnSelectFinishListener(ISelectItemFinishListener mSelectItemFinishListener)
    {
        this.mSelectItemFinishListener=mSelectItemFinishListener;
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
        } else
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

    private class MyOnWheelViewListener extends VerticalWheelView.OnWheelViewListener
    {
        @Override
        public void onSelected(int selectedIndex, String item)
        {
            super.onSelected(selectedIndex, item);
            selectIndex=selectedIndex;
            sItem=item;
        }
    }
}

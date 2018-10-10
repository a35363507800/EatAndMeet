package com.echoesnet.eatandmeet.views.widgets.SpinnerPopup;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.views.adapters.ApplyRefundpayListAdapter;



/**
 * Created by Administrator on 2016/6/23.
 */
public class SpinnerPopup extends PopupWindow
{
    private Activity mContext;
    private String[] reasons;
    private int width;
    private IOnSpinnerItemClickedListener onSpinnerItemClicked;

    public SpinnerPopup(Activity context, String[] reasons, int width)
    {
        this.mContext = context;
        this.reasons = reasons;
        this.width = width;
        initWindow();
    }

    private void initWindow()
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.pop_drop_down, null);
        ListView payLst = (ListView) popupView.findViewById(R.id.lv_refund_reason);

//        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.litem_refund_reason, reasons);
        //自定义适配器 添加点击回调，解决vivo手机选不上退款原因问题
        ApplyRefundpayListAdapter payListAdapter = new ApplyRefundpayListAdapter(mContext,reasons);
        payLst.setAdapter(payListAdapter);

        payListAdapter.setOnItemClickListener(new ApplyRefundpayListAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                SpinnerPopup.this.dismiss();
                if (onSpinnerItemClicked != null) {
                    onSpinnerItemClicked.itemClicked(position, reasons[position]);
                }
            }
        });

        /*payLst.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                SpinnerPopup.this.dismiss();
                if (onSpinnerItemClicked != null)
                    onSpinnerItemClicked.itemClicked(position, reasons[position]);
            }
        });*/

        // 设置SelectPicPopupWindow的View
        this.setContentView(popupView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(width);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setTouchable(true);
        this.setOutsideTouchable(false);
        // 刷新状态
        this.update();
        //this.backgroundAlpha(0.5f);
        this.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), (Bitmap) null));
        //this.setAnimationStyle(R.style.PopupAnimFromTop2Bottom);
        this.getContentView().setFocusableInTouchMode(true);
        this.getContentView().setFocusable(true);
        this.getContentView().setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0
                        && event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent)
    {
//        popupView.setAnimation(animation);
//        popupView.startAnimation(animation);
        if (!this.isShowing())
        {
            this.showAsDropDown(parent);
        }
        else
        {
            this.dismiss();
        }
    }

    public void hidePopupWindow()
    {
        if (this.isShowing())
        {
            this.dismiss();
        }
    }

    public void setIOnSpinnerItemClicked(IOnSpinnerItemClickedListener listener)
    {
        onSpinnerItemClicked = listener;
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
}

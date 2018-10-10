package com.echoesnet.eatandmeet.views.widgets.PasswordKeyPad;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.joanzapata.iconify.widget.IconTextView;
import com.jungly.gridpasswordview.GridPasswordView;

/**
 * Created by wangben on 2016/8/20.
 */
public class PasswordKeyPad extends PopupWindow
{
    private Activity mContext;
    private GridPasswordView  pwView;
    private String pwStr="";

    public PasswordKeyPad(Activity context,GridPasswordView  pwView)
    {
        this.mContext = context;
        this.pwView=pwView;
        initWindow();
    }

    private void initWindow()
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout contentView = (LinearLayout) inflater.inflate(R.layout.password_key_pad_layout, null);
        Button btn1= (Button) contentView.findViewById(R.id.btn_1);
        Button btn2= (Button) contentView.findViewById(R.id.btn_2);
        Button btn3= (Button) contentView.findViewById(R.id.btn_3);
        Button btn4= (Button) contentView.findViewById(R.id.btn_4);
        Button btn5= (Button) contentView.findViewById(R.id.btn_5);
        Button btn6= (Button) contentView.findViewById(R.id.btn_6);
        Button btn7= (Button) contentView.findViewById(R.id.btn_7);
        Button btn8= (Button) contentView.findViewById(R.id.btn_8);
        Button btn9= (Button) contentView.findViewById(R.id.btn_9);
        Button btn0= (Button) contentView.findViewById(R.id.btn_0);
        Button btnDelete= (Button) contentView.findViewById(R.id.btn_delete);
        IconTextView itvSwitchBtn= (IconTextView) contentView.findViewById(R.id.open_switch);
        btnClickListener mBtnClickListener=new btnClickListener("digit");
        btn1.setOnClickListener(mBtnClickListener);
        btn2.setOnClickListener(mBtnClickListener);
        btn3.setOnClickListener(mBtnClickListener);

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
     * @param parent
     */
    public void showPopupWindow(View parent)
    {
        if (!this.isShowing())
        {
            this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
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

    private class btnClickListener implements View.OnClickListener
    {
        String operType="";
        private btnClickListener(String operType)
        {
            this.operType=operType;
        }
        @Override
        public void onClick(View v)
        {
             if (operType.equals("digit"))
             {
                 switch (v.getId())
                 {
                     case R.id.btn_1:
                         pwStr+=1;
                         break;
                 }
                 //pwView.setPassword(pwStr);
             }
            else if (operType.equals("padSwitch"))
             {

             }
            else
             {

             }
        }
    }
}

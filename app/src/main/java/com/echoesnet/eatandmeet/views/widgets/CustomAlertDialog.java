package com.echoesnet.eatandmeet.views.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;

import static com.echoesnet.eatandmeet.R.id.txt_msg2;


public class CustomAlertDialog
{
    private Context context;
    private Dialog dialog;
    private LinearLayout lLayoutBg;
    private TextView txtTitle;
    private TextView txtMsg;
    private TextView txtMsg2;
    private Button btnNeg;
    private Button btnPos;
    private ImageView imgLine;
    private RelativeLayout titleLayout;
    private Display display;
    private boolean showTitle = false;
    private boolean showMsg = false;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;
    private boolean isMsgBold = false;
    private boolean isMsg2Bold = false;

    public CustomAlertDialog(Context context)
    {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public CustomAlertDialog builder()
    {
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_alertdialog, null);

        lLayoutBg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        titleLayout = (RelativeLayout) view.findViewById(R.id.title_layout);
        txtTitle = (TextView) view.findViewById(R.id.txt_title);
        txtTitle.setVisibility(View.GONE);
        txtMsg = (TextView) view.findViewById(R.id.txt_msg);
        txtMsg.setVisibility(View.GONE);
        txtMsg2 = (TextView) view.findViewById(txt_msg2);
        txtMsg2.setVisibility(View.GONE);
        btnNeg = (Button) view.findViewById(R.id.btn_neg);
        btnNeg.setVisibility(View.GONE);
        btnPos = (Button) view.findViewById(R.id.btn_pos);
        btnPos.setVisibility(View.GONE);
        imgLine = (ImageView) view.findViewById(R.id.img_line);
        imgLine.setVisibility(View.GONE);

        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

//        lLayoutBg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
//                .getWidth() * 0.70), LayoutParams.WRAP_CONTENT));
        return this;
    }

    public CustomAlertDialog setTitle(String title)
    {
        showTitle = true;
        if ("".equals(title))
        {
            txtTitle.setText("标题");
        }
        else
        {
            txtTitle.setText(title);
        }
        return this;
    }

    public CustomAlertDialog setMsg(String msg)
    {
        showMsg = true;
        if ("".equals(msg))
        {
            txtMsg.setText("内容");
        }
        else
        {
            txtMsg.setText(msg);
        }
        return this;
    }

    public CustomAlertDialog configContentView(int gravity)
    {
        txtMsg.setGravity(gravity);
        return this;
    }

    public CustomAlertDialog setMsgBold(boolean isBold)
    {
        isMsgBold = isBold;
        return this;
    }

    public CustomAlertDialog setMsgColor(int color)
    {
        txtMsg.setTextColor(color);
        return this;
    }

    public CustomAlertDialog setBoldMsg(String msg)
    {
        showMsg = true;
        if ("".equals(msg))
        {
            txtMsg2.setText("");
        }
        else
        {
            txtMsg2.setText(" " + msg);
        }
        return this;
    }

    public CustomAlertDialog setMsg2Bold(boolean isBold)
    {
        isMsg2Bold = isBold;
        return this;
    }

    public CustomAlertDialog setCancelable(boolean cancel)
    {
        if (dialog == null)
            throw new NullPointerException("请在builder方法后调用");
        else
            dialog.setCancelable(cancel);
        return this;
    }

    public CustomAlertDialog setPositiveTextColor(int color)
    {
        btnPos.setTextColor(color);
        return this;
    }

    public CustomAlertDialog setNegativeTextColor(int color)
    {
        btnNeg.setTextColor(color);
        return this;
    }

    public CustomAlertDialog setPositiveButtonBackgroundColor(int color)
    {
        btnPos.setBackgroundColor(color);
        return this;
    }

    public CustomAlertDialog setPositiveButton(String text, final OnClickListener listener)
    {
        showPosBtn = true;
        if (TextUtils.isEmpty(text))
        {
            btnPos.setText("确定");
        }
        else
        {
            btnPos.setText(text);
        }
        btnPos.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener != null)
                    listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public CustomAlertDialog setPositiveBtnClickListener(String text, final OnDialogWithPositiveBtnListener listener)
    {
        showPosBtn = true;
        if (TextUtils.isEmpty(text))
        {
            btnPos.setText("确定");
        }
        else
        {
            btnPos.setText(text);
        }
        btnPos.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener != null)
                    listener.onPositiveBtnClick(v, dialog);

/*                if (listener!=null)
                     listener.onClick(v);
                if (v.getTag().equals("Y"))
                    dialog.dismiss();*/
            }
        });
        return this;
    }

    public CustomAlertDialog setCancelBtnClickListener(String text, final OnDialogWithNavigateBtnListener listener)
    {
        showNegBtn = true;
        if (TextUtils.isEmpty(text))
        {
            btnNeg.setText("取消");
        }
        else
        {
            btnNeg.setText(text);
        }
        btnNeg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener != null)
                    listener.onNavigateBtnClick(v, dialog);
            }
        });
        return this;
    }

    public CustomAlertDialog setNegativeButton(String text,
                                               final OnClickListener listener)
    {
        showNegBtn = true;
        if (TextUtils.isEmpty(text))
        {
            btnNeg.setText("取消");
        }
        else
        {
            btnNeg.setText(text);
        }
        btnNeg.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener != null)
                    listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    private void setLayout()
    {
        if (!showTitle && !showMsg)
        {
            txtTitle.setText("提示");
            txtTitle.setVisibility(View.VISIBLE);
        }

        if (showTitle)
        {
            txtTitle.setVisibility(View.VISIBLE);
            titleLayout.setVisibility(View.VISIBLE);
        }

        if (showMsg)
        {
            txtMsg.setVisibility(View.VISIBLE);
            txtMsg2.setVisibility(View.VISIBLE);
        }
        if (isMsgBold)
        {
            Paint paint = txtMsg.getPaint();
            paint.setFakeBoldText(true);
            txtMsg.setLayerPaint(paint);
        }
        if (isMsg2Bold)
        {
            Paint paint = txtMsg2.getPaint();
            paint.setFakeBoldText(true);
            txtMsg2.setLayerPaint(paint);
        }
        if (!showPosBtn && !showNegBtn)
        {
            btnPos.setText("确定");
            btnPos.setVisibility(View.VISIBLE);
            imgLine.setVisibility(View.GONE);
            btnPos.setBackgroundResource(R.drawable.alertdialog_single_selector);
            btnPos.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dialog.dismiss();
                }
            });
        }

        if (showPosBtn && showNegBtn)
        {
            btnPos.setVisibility(View.VISIBLE);
            btnPos.setBackgroundResource(R.drawable.alertdialog_right_selector);
            btnNeg.setVisibility(View.VISIBLE);
            btnNeg.setBackgroundResource(R.drawable.alertdialog_left_selector);
            imgLine.setVisibility(View.VISIBLE);
        }

        if (showPosBtn && !showNegBtn)
        {
            btnPos.setVisibility(View.VISIBLE);
            btnPos.setBackgroundResource(R.drawable.alertdialog_single_selector);
            imgLine.setVisibility(View.GONE);
            //btnPos.setBackgroundResource(R.drawable.alertdialog_right_selector);
        }

        if (!showPosBtn && showNegBtn)
        {
            btnNeg.setVisibility(View.VISIBLE);
            btnNeg.setBackgroundResource(R.drawable.alertdialog_single_selector);
            imgLine.setVisibility(View.GONE);
            //btnNeg.setBackgroundResource(R.drawable.alertdialog_left_selector);
        }
    }


    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener)
    {
        if (dialog != null && onDismissListener != null)
            dialog.setOnDismissListener(onDismissListener);
    }

    public void show()
    {
        setLayout();
        dialog.show();
    }

    public void dismiss()
    {
        if (dialog != null)
            dialog.dismiss();
    }

    public boolean isShowing()
    {
        if (dialog == null)
            throw new NullPointerException("弹出窗对象为空");
        return dialog.isShowing();
    }

    public interface OnDialogWithPositiveBtnListener
    {
        void onPositiveBtnClick(View view, Dialog dialog);
    }

    public interface OnDialogWithNavigateBtnListener
    {
        void onNavigateBtnClick(View view, Dialog dialog);
    }

}

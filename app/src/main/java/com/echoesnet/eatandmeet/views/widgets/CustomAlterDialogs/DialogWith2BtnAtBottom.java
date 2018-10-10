package com.echoesnet.eatandmeet.views.widgets.CustomAlterDialogs;

import android.app.Dialog;
import android.content.Context;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.zhy.autolayout.AutoLinearLayout;

/**
 * Created by wangben on 2016/7/21.
 */
public class DialogWith2BtnAtBottom
{
    private Context context;
    private Dialog dialog;
    private View contentView;
    private Display display;
    private TextView tvTitle;
    private Button btnCancel, btnCommit;
    private LinearLayout allContent;
    private LinearLayout lLayoutBg ;
    private OnDialogWithPositiveBtnListener onDialogWithPositiveBtnListener;
    private OnDialogWithNavigateBtnListener onDialogWithNavigateBtnListener;


    public DialogWith2BtnAtBottom(Context context)
    {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public DialogWith2BtnAtBottom buildDialog(Context mContext)
    {

        dialog = new Dialog(mContext, R.style.AlertDialogStyle);
        contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_with2btn_at_bottom, null);
        dialog.setContentView(contentView);
        tvTitle = (TextView) contentView.findViewById(R.id.txt_title);
        btnCancel = (Button) contentView.findViewById(R.id.btn_neg);
        btnCommit = (Button) contentView.findViewById(R.id.btn_pos);
        allContent = (LinearLayout) contentView.findViewById(R.id.all_content_container);
        lLayoutBg  = (LinearLayout) contentView.findViewById(R.id.lLayout_bg);


        return this;
    }

    public DialogWith2BtnAtBottom setDialogTitle(String title,Boolean isBold)
    {
        tvTitle.setText(title);
        if (isBold)
        {
            TextPaint tp = tvTitle.getPaint();
            tp.setFakeBoldText(true);
        }
        // TODO: 2017/8/10 lc 暂时这么写，以后兼容或者另写
        if (TextUtils.equals(title,"修改备注"))
        {
            lLayoutBg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                    .getWidth() * 0.70), LinearLayout.LayoutParams.WRAP_CONTENT));
        }
        return this;
    }



    public DialogWith2BtnAtBottom setCancelBtnClickListener(final View.OnClickListener listener)
    {
        btnCancel.setOnClickListener(new View.OnClickListener()
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

    public DialogWith2BtnAtBottom setCancelBtnClickListener(final OnDialogWithNavigateBtnListener listener)
    {
        btnCancel.setOnClickListener(new View.OnClickListener()
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

    public DialogWith2BtnAtBottom setCommitBtnClickListener(final View.OnClickListener listener)
    {
        btnCommit.setOnClickListener(new View.OnClickListener()
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

    public DialogWith2BtnAtBottom setCommitBtnClickListener(final OnDialogWithPositiveBtnListener listener)
    {
        btnCommit.setOnClickListener(new View.OnClickListener()
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

    public DialogWith2BtnAtBottom setCancelable(boolean cancelAble)
    {
        dialog.setCancelable(cancelAble);
        return this;
    }


    public DialogWith2BtnAtBottom setPositiveButton(String text, final View.OnClickListener listener)
    {

        if (TextUtils.isEmpty(text))
        {
            btnCommit.setText("确定");
        }
        else
        {
            btnCommit.setText(text);
        }
        btnCommit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener!=null)
                    listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }



    public DialogWith2BtnAtBottom setNegativeButton(String text, final View.OnClickListener listener)
    {
        if (TextUtils.isEmpty(text))
        {
            btnCancel.setText("取消");
        }
        else
        {
            btnCancel.setText(text);
        }
        btnCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener!=null)
                    listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public DialogWith2BtnAtBottom setPositiveTextColor(int color)
    {
        btnCommit.setTextColor(color);
        return this;
    }

    public DialogWith2BtnAtBottom setNegativeTextColor(int color)
    {
        btnCancel.setTextColor(color);
        return this;
    }

    public void setOnDialogWithPositiveBtnListener(OnDialogWithPositiveBtnListener listener)
    {
        this.onDialogWithPositiveBtnListener = listener;
    }

    public void setOnDialogWithNavigateBtnListener(OnDialogWithNavigateBtnListener listener)
    {
        this.onDialogWithNavigateBtnListener = listener;
    }


    public DialogWith2BtnAtBottom setContent(View contentView)
    {
        allContent.addView(contentView);
        return this;
    }

    public void show()
    {
        dialog.show();
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

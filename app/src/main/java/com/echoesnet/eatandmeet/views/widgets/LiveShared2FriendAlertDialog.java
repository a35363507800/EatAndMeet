package com.echoesnet.eatandmeet.views.widgets;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eam.icontextmodule.model.EchoesEamIcon;
import com.echoesnet.eatandmeet.R;
import com.joanzapata.iconify.IconDrawable;


public class LiveShared2FriendAlertDialog
{
    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private TextView txt_title;
    private TextView txt_msg;
    private Button btn_neg;
    private Button btn_pos;
    private ImageView img_line, iv_hint_icon;
    private ImageView titleImage;
    private Display display;
    private boolean showTitle = false;
    private boolean showMsg = false;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;

    public LiveShared2FriendAlertDialog(Context context)
    {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public LiveShared2FriendAlertDialog builder()
    {
        View view = LayoutInflater.from(context).inflate(
                R.layout.live_shared_view_alertdialog, null);

        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_title.setVisibility(View.GONE);
        txt_msg = (TextView) view.findViewById(R.id.txt_msg);
        txt_msg.setVisibility(View.GONE);
        btn_neg = (Button) view.findViewById(R.id.btn_neg);
        btn_neg.setVisibility(View.GONE);
        btn_pos = (Button) view.findViewById(R.id.btn_pos);
        btn_pos.setVisibility(View.GONE);
        img_line = (ImageView) view.findViewById(R.id.img_line);
        img_line.setVisibility(View.GONE);
        iv_hint_icon = (ImageView) view.findViewById(R.id.iv_hint_icon);
        titleImage = (ImageView) view.findViewById(R.id.title_img);
        iv_hint_icon.setImageDrawable(new IconDrawable(context, EchoesEamIcon.eam_s_warning)
                .colorRes(R.color.c4));

        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * 0.85), LayoutParams.WRAP_CONTENT));

        return this;
    }

    public LiveShared2FriendAlertDialog setTitle(String title)
    {
        showTitle = true;
        if ("".equals(title))
        {
            txt_title.setText("标题");
        }
        else
        {
            txt_title.setText(title);
        }
        return this;
    }

    public LiveShared2FriendAlertDialog setMsg(String msg)
    {
        showMsg = true;
        if ("".equals(msg))
        {
            txt_msg.setText("内容");
        }
        else
        {
            txt_msg.setText(msg);
        }
        return this;
    }

    public LiveShared2FriendAlertDialog setTitleImage(String imagePath)
    {
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(imagePath)
                .placeholder(R.drawable.userhead)
                .into(titleImage);
        return this;
    }

    public LiveShared2FriendAlertDialog setCancelable(boolean cancel)
    {
        dialog.setCancelable(cancel);
        return this;
    }

    public LiveShared2FriendAlertDialog setPositiveTextColor(int color)
    {
        btn_pos.setTextColor(color);
        return this;
    }

    public LiveShared2FriendAlertDialog setPositiveButtonBackgroundColor(int color)
    {
        btn_pos.setBackgroundColor(color);
        return this;
    }

    public LiveShared2FriendAlertDialog setPositiveButton(String text,
                                                          final OnClickListener listener)
    {
        showPosBtn = true;
        if (TextUtils.isEmpty(text))
        {
            btn_pos.setText("确定");
        }
        else
        {
            btn_pos.setText(text);
        }
        btn_pos.setOnClickListener(new OnClickListener()
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

    public LiveShared2FriendAlertDialog setNegativeButton(String text,
                                                          final OnClickListener listener)
    {
        showNegBtn = true;
        if (TextUtils.isEmpty(text))
        {
            btn_neg.setText("取消");
        }
        else
        {
            btn_neg.setText(text);
        }
        btn_neg.setOnClickListener(new OnClickListener()
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
            txt_title.setText("提示");
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showTitle)
        {
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showMsg)
        {
            txt_msg.setVisibility(View.VISIBLE);
        }

        if (!showPosBtn && !showNegBtn)
        {
            btn_pos.setText("确定");
            btn_pos.setVisibility(View.VISIBLE);
            img_line.setVisibility(View.GONE);
            btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
            btn_pos.setOnClickListener(new OnClickListener()
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
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alertdialog_right_selector);
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.alertdialog_left_selector);
            img_line.setVisibility(View.VISIBLE);
        }

        if (showPosBtn && !showNegBtn)
        {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
            img_line.setVisibility(View.GONE);
            //btn_pos.setBackgroundResource(R.drawable.alertdialog_right_selector);
        }

        if (!showPosBtn && showNegBtn)
        {
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.alertdialog_single_selector);
            img_line.setVisibility(View.GONE);
            //btn_neg.setBackgroundResource(R.drawable.alertdialog_left_selector);
        }
    }

    public void show()
    {
        setLayout();
        dialog.show();
    }
}

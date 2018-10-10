package com.echoesnet.eatandmeet.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @modifier
 * @createDate 2017/11/28
 * @description
 */
public class ObtainStarDialog extends Dialog
{
    private final String TAG = ObtainStarDialog.class.getSimpleName();
    private TextView tvTitle;
    private ImageView ivStar;
    private TextView tvGiftDetail;
    private TextView tvConfirm;
    private Context context;
    private Dialog dialog;
    private boolean isClose;
    private Display display;
    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    closeDialog();
                    break;
            }
        }

        ;
    };

    public ObtainStarDialog builder()
    {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_obtain_star, null);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        ivStar = (ImageView) view.findViewById(R.id.iv_star);
        tvGiftDetail = (TextView) view.findViewById(R.id.tv_gift_detail);
        tvConfirm = (TextView) view.findViewById(R.id.tv_confirm);
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        isClose = true;
        return this;
    }

    public ObtainStarDialog(Context context)
    {
        super(context);
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public ObtainStarDialog setTitle(String title)
    {
        tvTitle.setText(title);
        return this;
    }

    public ObtainStarDialog setTvGiftInfo(String messageInfo)
    {
        tvGiftDetail.setText(messageInfo);
        return this;
    }

    public ObtainStarDialog setShowGiftImage(String url)
    {
        GlideApp.with(EamApplication.getInstance())
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.qs_photo)
                .error(R.drawable.qs_photo)
                .into(ivStar);
        return this;
    }

    private void closeDialog()
    {
        if (isClose)
        {
            dismiss();
            isClose = false;
        }
    }

    class MyTask extends TimerTask
    {
        @Override
        public void run()
        {
            Message message = new Message();
            message.what = 1;
            mHandler.sendMessage(message);
        }
    }

    public void show()
    {
        new Timer().schedule(new MyTask(), 5000);
        tvConfirm.setOnClickListener((v) ->
        {
            dismiss();
        });
        dialog.show();
    }

    public void dismiss()
    {
        if (dialog != null)
            dialog.dismiss();
    }
}

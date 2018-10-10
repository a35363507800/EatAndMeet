package com.echoesnet.eatandmeet.views.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;

/**
 * Created by yqh on 2017/6/9 0009.
 */

public class ChatTipDialog
{
    private Activity mAct;

    private Dialog dialog;
    private View contentView;
    private Button btnCancel, btnCommit;
    private RelativeLayout allContent;

    public ChatTipDialog buildDialog(Context mContext)
    {
        dialog = new Dialog(mContext, R.style.AlertDialogStyle);
        contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_chat_tip, null);
        dialog.setContentView(contentView);
        btnCancel = (Button) contentView.findViewById(R.id.btn_neg);
        btnCommit = (Button) contentView.findViewById(R.id.btn_pos);
        allContent = (RelativeLayout) contentView.findViewById(R.id.all_content_container);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = CommonUtils.dp2px(mContext, 340);
        //lp.width= (int) (CommonUtils.getScreenSize(mContext).width*0.85f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        return this;
    }

    public ChatTipDialog setCommitBtnText(String text)
    {
        btnCommit.setText(text);
        return this;
    }

    public ChatTipDialog setCancelBtnText(String text)
    {
        btnCancel.setText(text);
        return this;
    }

    public ChatTipDialog setCancelBtnClickListener(final View.OnClickListener listener)
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


    public ChatTipDialog setCommitBtnClickListener(final View.OnClickListener listener)
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

    public ChatTipDialog setCancelable(boolean cancelAble)
    {
        dialog.setCancelable(cancelAble);
        return this;
    }

    public ChatTipDialog setContent(View contentView)
    {
        allContent.addView(contentView);
        return this;
    }

    public void show()
    {
        dialog.show();
    }
}

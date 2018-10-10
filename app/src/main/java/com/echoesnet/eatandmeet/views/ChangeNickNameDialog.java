package com.echoesnet.eatandmeet.views;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.views.widgets.Change2thNicNameDialog;
import com.echoesnet.eatandmeet.views.widgets.MaxByteLengthEditText;

/**
 * Created by lc on 2017/7/17 14.
 */

public class ChangeNickNameDialog
{
    private Context mContext;
    private Dialog dialog;
    private MaxByteLengthEditText editText;
    private Button btn_neg, btn_pos;
    private ChangeNickNameDialog.OnDialogWith2thNicNameListener onDialogWith2thNicNameListener;


    public ChangeNickNameDialog(Context mContext)
    {
        this.mContext = mContext;
    }

    public ChangeNickNameDialog build()
    {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.edit_nicname_dialog, null);
        editText = (MaxByteLengthEditText) view.findViewById(R.id.input_2thNicName);
        btn_neg = (Button) view.findViewById(R.id.btn_neg);
        btn_pos = (Button) view.findViewById(R.id.btn_pos);
        dialog = new Dialog(mContext, R.style.AlertDialogStyle);
        dialog.setContentView(view);
        editText.setMaxByteLength(14);
        return this;
    }

    public ChangeNickNameDialog setHintReMark(String reMarkHint)
    {
        if(reMarkHint.equals("请输入备注名"))
        {
            editText.setHint(reMarkHint);
        }else
        {
            editText.setText(reMarkHint);
        }

        return this;
    }

    public ChangeNickNameDialog setPositiveButton(String text,
                                                    final Change2thNicNameDialog.OnDialogWith2thNicNameListener listener)
    {
        if (TextUtils.isEmpty(text))
        {
            btn_pos.setText("确定");
        }
        else
        {
            btn_pos.setText(text);
        }
        btn_pos.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener != null)
                    listener.onPositiveBtnClick(v, editText);
                dialog.dismiss();
            }
        });
        return this;
    }

    public ChangeNickNameDialog setNegativeButton(String text,
                                                    final View.OnClickListener listener)
    {
        if (TextUtils.isEmpty(text))
        {
            btn_neg.setText("取消");
        }
        else
        {
            btn_neg.setText(text);
        }
        btn_neg.setOnClickListener(new View.OnClickListener()
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

    public void show()
    {
        dialog.show();
    }

    public void setOnDialogWith2btnAtBottomListener(ChangeNickNameDialog.OnDialogWith2thNicNameListener listener)
    {
        this.onDialogWith2thNicNameListener = listener;
    }

    public interface OnDialogWith2thNicNameListener
    {
        void onPositiveBtnClick(View view, EditText editText);

        void onNavigateBtnClick(View view, EditText editText);
    }

}

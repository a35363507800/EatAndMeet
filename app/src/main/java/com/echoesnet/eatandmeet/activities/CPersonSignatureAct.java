package com.echoesnet.eatandmeet.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

//个性签名
public class CPersonSignatureAct extends BaseActivity
{
    public final static String TAG = RegisterAct.class.getSimpleName();
    private static final int MAX_LENGTH = 40;
    private int restLength = MAX_LENGTH;

    @BindView(R.id.top_bar)
    TopBarSwitch topBar;
    @BindView(R.id.et_input)
    EditText et_input;
    @BindView(R.id.tv_count)
    TextView tv_count;
    private String etResult;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_preson_signature);
        ButterKnife.bind(this);
        initAfterView();
    }

    private void initAfterView()
    {
//        et_input.setHint("请输入签名内容");
        et_input.setTextColor(ContextCompat.getColor(this, R.color.FC3));

        if (TextUtils.isEmpty(SharePreUtils.getUserSign(CPersonSignatureAct.this)) || SharePreUtils.getUserSign(CPersonSignatureAct.this).equals("这家伙很懒，什么都没有留下哦~"))
        {
            et_input.setHint("请输入个性签名");
        } else
        {
            et_input.setText(SharePreUtils.getUserSign(CPersonSignatureAct.this));
            et_input.setTextColor(ContextCompat.getColor(this, R.color.C0322));
            et_input.setSelection(SharePreUtils.getUserSign(CPersonSignatureAct.this).length());
            int number = MAX_LENGTH - SharePreUtils.getUserSign(CPersonSignatureAct.this).length();
            tv_count.setText(number + "");
        }


        et_input.addTextChangedListener(new TextWatcher()
        {
            private CharSequence charSequence;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                tv_count.setText(restLength + "");
                et_input.setTextColor(getResources().getColor(R.color.c3));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                charSequence = s;
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                etResult = et_input.getText().toString().trim();
                int number = MAX_LENGTH - s.length();
                tv_count.setText(number + "");
                selectionStart = et_input.getSelectionStart();
                selectionEnd = et_input.getSelectionEnd();
                if (charSequence.length() > MAX_LENGTH)
                {
                    s.delete(selectionStart - 1, selectionEnd);
                    int selection = selectionStart;
                    et_input.setText(s);
                    et_input.setSelection(selection);
                }
            }
        });


        topBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
  /*Intent intent = new Intent();
                intent.putExtra("etresult", "");
                PresonSignatureAct.this.setResult(RESULT_OK, intent);*/
                CPersonSignatureAct.this.finish();
            }

            @Override
            public void right2Click(View view)
            {
    /*Intent intent = new Intent();
                intent.putExtra("etresult", etResult);
                PresonSignatureAct.this.setResult(RESULT_OK, intent);
                PresonSignatureAct.this.finish();*/

                SharePreUtils.setUserSign(CPersonSignatureAct.this, et_input.getText().toString());
                String sign = et_input.getText().toString();
                Intent i = new Intent();
                i.putExtra("sign", sign);
                CPersonSignatureAct.this.setResult(RESULT_OK, i);
                CPersonSignatureAct.this.finish();
            }
        }).setText(getResources().getString(R.string.preson_signa_title));

        List<TextView> navBtns = topBar.getNavBtns(new int[]{1, 0, 0, 1});
        for (int i = 0; i < navBtns.size(); i++)
        {
            TextView tv = navBtns.get(i);
            if (i == 1)
            {
                tv.setText("提交");
                tv.setTextColor(ContextCompat.getColor(this, R.color.MC1));
                tv.setTextSize(16);
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            /*Intent intent = new Intent();
            intent.putExtra("etresult", "");
            PresonSignatureAct.this.setResult(RESULT_OK, intent);*/
            CPersonSignatureAct.this.finish();
        }
        return true;
    }
}

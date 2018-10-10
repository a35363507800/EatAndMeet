package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.presenters.ImpIMyChangeLoginPwView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyChangeLoginPwView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.joanzapata.iconify.widget.IconTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class MyChangeLoginPwAct extends BaseActivity implements IMyChangeLoginPwView
{
    private static final String TAG = MyChangeLoginPwAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.et_input_pw)
    EditText fetOldPw;
    @BindView(R.id.et_input_pw_confirm)
    EditText fetNewPw;
    @BindView(R.id.iv_input_pw_show)
    IconTextView itvIsShowOldPw;
    @BindView(R.id.iv_input_pw_show_confirm)
    IconTextView itvIsShowNewPw;
    @BindView(R.id.btn_commit_ok)
    Button btnCommit;

    private Dialog pDialog;
    private Activity mContext;

    private ImpIMyChangeLoginPwView changeLoginPwView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_change_login_pw);
        ButterKnife.bind(this);
        afterViews();
    }

    private void afterViews()
    {
        mContext = this;
        topBar.setTitle("修改登录密码");
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                mContext.finish();
            }

            @Override
            public void left2Click(View view)
            {
            }

            @Override
            public void rightClick(View view)
            {
            }
        });
        itvIsShowOldPw.setTag("hide");
        itvIsShowNewPw.setTag("hide");
        fetOldPw.setFilters(new InputFilter[]{filter});
        fetNewPw.setFilters(new InputFilter[]{filter});

        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理");
        pDialog.setCancelable(false);

        changeLoginPwView = new ImpIMyChangeLoginPwView(mContext, this);

    }

    @OnClick({R.id.iv_input_pw_show, R.id.iv_input_pw_show_confirm, R.id.btn_commit_ok})
    void viewClick(View v)
    {
        switch (v.getId())
        {
            case R.id.iv_input_pw_show:
                if (String.valueOf(itvIsShowOldPw.getTag()).equals("hide"))
                {
                    itvIsShowOldPw.setTag("show");
                    itvIsShowOldPw.setText("{eam-e95c @color/C0412}");
//                    itvIsShowOldPw.setTextColor(ContextCompat.getColor(mContext, R.color.c10));
                    fetOldPw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    fetOldPw.setSelection(fetOldPw.getText().length());
                }
                else
                {
                    itvIsShowOldPw.setTag("hide");
                    itvIsShowOldPw.setText("{eam-e620 @color/FC3}");
//                    itvIsShowOldPw.setTextColor(ContextCompat.getColor(mContext, R.color.FC3));
                    fetOldPw.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    fetOldPw.setSelection(fetOldPw.getText().length());
                }
                break;
            case R.id.iv_input_pw_show_confirm:
                if (String.valueOf(itvIsShowNewPw.getTag()).equals("hide"))
                {
                    itvIsShowNewPw.setTag("show");
                    itvIsShowNewPw.setText("{eam-e95c @color/C0412}");
                    itvIsShowNewPw.setTextColor(ContextCompat.getColor(mContext, R.color.c10));
                    fetNewPw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    fetNewPw.setSelection(fetNewPw.getText().length());
                }
                else
                {
                    itvIsShowNewPw.setTag("hide");
                    itvIsShowNewPw.setText("{eam-e620 @color/FC3}");
                    itvIsShowNewPw.setTextColor(ContextCompat.getColor(mContext, R.color.FC3));
                    fetNewPw.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    fetNewPw.setSelection(fetNewPw.getText().length());
                }
                break;
            case R.id.btn_commit_ok:
                switch (verifyInput())
                {
                    case 1:
                        ToastUtils.showShort("请输入原密码");
                        break;
                    case 2:
                        ToastUtils.showShort("请输入新密码");
                        break;
                    case 3:
                        ToastUtils.showShort("请输入8到16位字母和数字的组合");
                        break;
                    case 4:
                        if (pDialog != null && !pDialog.isShowing())
                            pDialog.show();
                        if (changeLoginPwView != null)
                            changeLoginPwView.resetLoginPassword(fetOldPw.getText().toString(), fetNewPw.getText().toString());
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    private int verifyInput()
    {
        String oldPassWord = fetOldPw.getText().toString().trim();
        String newPassWord = fetNewPw.getText().toString().trim();
        boolean NPWD = CommonUtils.verifyInput(1, newPassWord);
        if (TextUtils.isEmpty(oldPassWord))
            return 1;//旧密码为空
        if (TextUtils.isEmpty(newPassWord))
            return 2;//新密码为空
        //密码校验
        if (NPWD == false)
            return 3;
        //成功
        return 4;
    }

    InputFilter filter = new InputFilter()
    {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
        {
            for (int i = start; i < end; i++)
            {
                int type = Character.getType(source.charAt(i));
                //System.out.println("Type : " + type);
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL)
                {
                    ToastUtils.showShort("不支持输入Emoji表情符号");
                    return "";
                }
            }
            return null;
        }
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private void setPwVisibility(IconTextView ivPwShow, EditText etPassword)
    {
        if (String.valueOf(ivPwShow.getTag()).equals("hide"))
        {
            ivPwShow.setTag("show");
            ivPwShow.setTextColor(ContextCompat.getColor(mContext, R.color.MC7));
            etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            etPassword.setSelection(etPassword.getText().length());
        }
        else
        {
            ivPwShow.setTag("hide");
            ivPwShow.setTextColor(ContextCompat.getColor(mContext, R.color.FC3));
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etPassword.setSelection(etPassword.getText().length());
        }
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mContext, null, exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void resetLoginPasswordCallback(String response)
    {
        ToastUtils.showShort("修改完成");
        mContext.finish();
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String errorcode, String errorbody)
    {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }
}

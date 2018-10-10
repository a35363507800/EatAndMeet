package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.presenters.ImpIMyVerifyIdView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyVerifyIdView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 实名认证页
 */
public class MyVerifyIdAct extends MVPBaseActivity<IMyVerifyIdView, ImpIMyVerifyIdView> implements IMyVerifyIdView
{
    private final static String TAG = MyVerifyIdAct.class.getSimpleName();
    //region 变量
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.ev_id_name)
    EditText etUserIdName;
    @BindView(R.id.ev_id_number)
    EditText fetUserIdNum;
    @BindView(R.id.btn_id_info_commit)
    Button btnIdInfoCommit;
    @BindView(R.id.tv_instruction_3)
    TextView tvInstruction;


    private String openSource = "";
    private Activity mContext;

    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_verify_id);
        ButterKnife.bind(this);
        initAfterView();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected ImpIMyVerifyIdView createPresenter()
    {
        return new ImpIMyVerifyIdView();
    }


    private void initAfterView()
    {
        mContext = this;
        topBar.setTitle("实名认证");
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
        openSource = getIntent().getStringExtra(EamConstant.EAM_VERIFY_ID_OPEN_SOURCE);
        if (!TextUtils.isEmpty(openSource))
        {
            Logger.t(TAG).d(openSource);
            if (openSource.equals("MySetPayPwManagerAct"))
            {
                topBar.setTitle("身份验证");
                tvInstruction.setVisibility(View.INVISIBLE);
            }
        }
/*        fetUserIdNum.addValidator(new OrValidator(
                "请输入正确的身份证号码",
                new RegexpValidator("15位身份证号不正确", "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$"),
                new RegexpValidator("18位身份证号不正确", "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$"))
        );*/
        EamApplication.getInstance().actStake.add(this);
    }

    @OnClick({R.id.btn_id_info_commit})
    void viewClick(View v)
    {
        switch (v.getId())
        {

            case R.id.btn_id_info_commit:
                String userIdName = etUserIdName.getText().toString().trim();
                String userIdNum = fetUserIdNum.getText().toString();
                if (TextUtils.isEmpty(userIdName) || TextUtils.isEmpty(userIdNum))
                {
                    ToastUtils.showShort("姓名或身份证不能为空");
                    break;
                }
                if (!CommonUtils.verifyInput(5, userIdName))
                {
                    ToastUtils.showShort("请输入正确的姓名");
                    break;
                }
                if (!(CommonUtils.verifyInput(6, userIdNum) ||
                        CommonUtils.verifyInput(7, userIdNum)))
                {
                    ToastUtils.showShort("请输入正确的身份证号");
                    break;
                }
                //根据进入页的不同，选择不同的行为
                if (openSource.equals("PayHelper") || openSource.equals("MyAccountSecurityAct"))
                {
                    if (mPresenter != null)
                        mPresenter.verifyIdInfo(etUserIdName.getText().toString().trim(), fetUserIdNum.getText().toString());
                    //测试用，发布时打开上面的函数
                    //afterVerifyIdSuccess();
                }
                else if (openSource.equals("MySetPayPwManagerAct"))
                {
                    if (mPresenter != null)
                        mPresenter.verifyIdInfoWithExist(etUserIdName.getText().toString().trim(), fetUserIdNum.getText().toString());
                }
//                else if (openSource.equals("PayHelpers"))
//                {
//                    verifyIdInfoView(etUserIdName.getText().toString(), fetUserIdNum.getText().toString());
//                }

                break;
            default:
                break;
        }
    }

    private void afterVerifyIdSuccess()
    {
        //验证成功去设置支付密码
        Intent intent = new Intent(mContext, MySetNewPayPwAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra("type", "setPayPw");
        intent.putExtra(EamConstant.EAM_SET_PAY_PW_OPEN_SOURCE,
                mContext.getIntent().getStringExtra(EamConstant.EAM_SET_PAY_PW_OPEN_SOURCE));
        mContext.startActivity(intent);
        //关闭
        mContext.finish();
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.UserC_realName:
                try
                {
                    JSONObject body = new JSONObject(errBody);
                    JSONObject info = new JSONObject(body.getString("number"));
                    if (code.equals("COUNTDOWN_IS_FINISH"))
                    {
                        ToastUtils.showShort(ErrorCodeTable.parseErrorCode(code));
                    }
                    else
                        ToastUtils.showShort(String.format("姓名和身份证不一致，今日剩余次数为%s", info.getString("surplus")));
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
    }

    @Override
    public void verifyIdInfoCallback(String response)
    {
        afterVerifyIdSuccess();
    }

    @Override
    public void verifyIdInfoWithExistCallback(String response)
    {
        //验证成功去设置支付密码
        Intent intent = new Intent(mContext, MySetNewPayPwAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra("type", "setPayPw");
        mContext.startActivity(intent);
    }
}

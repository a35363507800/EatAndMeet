package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.presenters.ImpMySetUserFeedbackCallback;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMySetUserFeedbackCallback;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.EditViewWithCharIndicate;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class MySetUserFeedbackAct extends BaseActivity implements IMySetUserFeedbackCallback
{
    private static final String TAG = MySetUserFeedbackAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.evw_input_feedback)
    EditViewWithCharIndicate ewciFeedBack;

    //private Dialog pDialog;
    private Activity mContext;
    private ImpMySetUserFeedbackCallback impMySetUserFeedbackCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_user_feedback);
        ButterKnife.bind(this);
        afterViews();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
//        if (pDialog != null && pDialog.isShowing())
//        {
//            pDialog.dismiss();
//            pDialog = null;
//        }
    }

    private void afterViews()
    {
        mContext = this;
        impMySetUserFeedbackCallback = new ImpMySetUserFeedbackCallback(mContext, this);
        topBar.setTitle("意见反馈");
        topBar.getRightButton().setVisibility(View.VISIBLE);
        topBar.getRightButton().setText("提交");
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
                String fStr = ewciFeedBack.getInputText();
                if (TextUtils.isEmpty(fStr))
                {
                    ToastUtils.showShort( "请输入意见");
                }
                else
                {
                    if (impMySetUserFeedbackCallback != null)
                    {
//                        if (pDialog != null && !pDialog.isShowing())
//                            pDialog.show();
                        impMySetUserFeedbackCallback.commitFeedback(mContext, fStr);
                    }
                }
            }
        });

//        pDialog = DialogUtil.getCommonDialog(mContext, "正在退出");
//        pDialog.setCancelable(false);
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mContext, null, exceptSource, e);
//        if (pDialog != null && pDialog.isShowing())
//            pDialog.dismiss();
    }

    @Override
    public void commitFeedbackCallback(String response)
    {
        Logger.t(TAG).d("获得的结果：" + response);
        ToastUtils.showShort("衷心感谢您的反馈，您的青睐就是我们奋发的动力！");
        mContext.finish();
    }
}

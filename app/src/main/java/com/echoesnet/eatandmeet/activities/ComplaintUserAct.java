package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.presenters.ImpComplaintUserView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IComplaintUserView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.EditViewWithCharIndicate;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 邀请人投诉
 */
public class ComplaintUserAct extends MVPBaseActivity<IComplaintUserView, ImpComplaintUserView> implements IComplaintUserView
{

    private static final String TAG = ComplaintUserAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.evciRemarkContent)
    EditViewWithCharIndicate evciRemarkContent;

    @BindView(R.id.btnComment)
    Button btnComment;
    @BindView(R.id.allRadiosHolder)
    AutoLinearLayout allRadiosHolder;

    private Dialog mDialog;
    private Activity mAct;

    private String luId = "";//举报 id
    private String strSendType = "";
    private String streamId = "";

    private HashMap<String, IconTextView> mapRadios = new HashMap<>();

    private ArrayList<String> arrTypes = new ArrayList<>();

    View.OnClickListener listenerOnRadioClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            strSendType = (String) view.getTag();
            refreshRadios(strSendType);
        }
    };

    private void refreshRadios(String str)
    {
        for (Map.Entry<String, IconTextView> entry : mapRadios.entrySet())
        {
            if (str.equals(entry.getKey()))
            {
                entry.getValue().setText("{eam-s-grade2 @color/MC1 @dimen/f3}");
            }else
            {
                entry.getValue().setText("{eam-s-grade1 @color/FC7 @dimen/f3}");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_complaint);
        ButterKnife.bind(this);
        afterViews();
    }

    private void afterViews()
    {
        arrTypes.add(getResources().getString(R.string.actComplaint_Time));
        arrTypes.add(getResources().getString(R.string.actComplaint_Miss));
        arrTypes.add(getResources().getString(R.string.actComplaint_Fake));
        arrTypes.add(getResources().getString(R.string.actComplaint_Else));

        mAct = this;
        streamId = getIntent().getStringExtra("streamId");
        luId = getIntent().getStringExtra("luId");
        topBar.setTitle(getResources().getString(R.string.actComplaint_TopBarTitle_Text));
        topBar.getRightButton().setVisibility(View.INVISIBLE);
        topBar.getRightButton().setText("提交");
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                mAct.finish();
            }

            @Override
            public void left2Click(View view)
            {
                return;
            }

            @Override
            public void rightClick(View view)
            {
                if (!mAct.isFinishing() && mDialog != null && !mDialog.isShowing())
                    mDialog.show();
                if (mPresenter != null)
                    mPresenter.commitMessage(luId, strSendType, evciRemarkContent.getInputText(), streamId);
            }
        });
        mDialog = DialogUtil.getCommonDialog(mAct, getResources().getString(R.string.loadingComplaint));
        mDialog.setCancelable(false);

        btnComment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String fStr = evciRemarkContent.getInputText();
                Logger.t(TAG).d("选择和填写信息--> " + fStr + " , " + strSendType);
                if (!TextUtils.isEmpty(fStr) || !TextUtils.isEmpty(strSendType))
                {
                    if (!mAct.isFinishing() && mDialog != null && !mDialog.isShowing())
                        mDialog.show();
                    if (mPresenter != null)
                        mPresenter.commitMessage(luId, strSendType, fStr, streamId);
                }
                else
                {
                    ToastUtils.showShort("请选择投诉内容和填写吐槽内容");
                }
            }
        });

        for (int i = 0; i < arrTypes.size(); i++)
        {
            View v = LayoutInflater.from(mAct).inflate(R.layout.item_report_fouls_type, null, false);
            TextView tvDesc = (TextView) v.findViewById(R.id.tvDesc);
            IconTextView itvRadio = (IconTextView) v.findViewById(R.id.itvRadio);
            String desc = arrTypes.get(i);
            tvDesc.setText(desc);
            mapRadios.put(desc, itvRadio);
            allRadiosHolder.addView(v);

            v.setTag(desc);
            v.setOnClickListener(listenerOnRadioClick);
        }
        refreshRadios(strSendType);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mDialog != null && mDialog.isShowing())
        {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    protected ImpComplaintUserView createPresenter()
    {
        return new ImpComplaintUserView();
    }


    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.ReceiveC_complaintReceive:
                if ("NO_POWER".equals(code))
                {
                    ToastUtils.showShort( "你不是该约会的用户你没有权限操作");
                }
                break;
                default:
                    break;
        }
    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Exception e)
    {

    }

    @Override
    public void commitMessageCallback(String response)
    {
        Logger.t(TAG).d("获得的结果：" + response);
        ToastUtils.showShort("衷心感谢您的反馈，您的青睐就是我们奋发的动力！");
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
        mAct.setResult(MyCommentAct.TO_COMPLAINT_USER_OK);
        mAct.finish();


    }
}

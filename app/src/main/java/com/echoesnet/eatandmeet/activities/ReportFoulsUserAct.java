package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.presenters.ImpIReportFoulsUserActView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IReportFoulsUserActView;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SpacesItemDecoration;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.ReportFoulsAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.EditViewWithCharIndicate;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 举报用户
 */
public class ReportFoulsUserAct extends BaseActivity implements IReportFoulsUserActView
{
    private static final String TAG = ReportFoulsUserAct.class.getSimpleName();

    @BindView(R.id.evciRemarkContent)
    EditViewWithCharIndicate evciRemarkContent;
    @BindView(R.id.btnComment)
    Button btnComment;
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.tvReportContentDesc)
    TextView tvReportContentDesc;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private Dialog mDialog;
    private Activity mAct;
    private String luId = "";//举报 id
    private String strSendType = "";
    private HashMap<String, IconTextView> mapRadios = new HashMap<>();
    private ArrayList<String> arrTypes = new ArrayList<>();
    private ImpIReportFoulsUserActView impIReportFoulsUserActView;
    private List<Map<String,TextView>> navBtns;
    private final int DEAFULT_POSITION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_report_fouls_user);
        ButterKnife.bind(this);
        afterViews();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    private void afterViews()
    {
        arrTypes.add("政治反动");
        arrTypes.add("不实信息");
        arrTypes.add("淫秽色情");
        arrTypes.add("广告骚扰");
        arrTypes.add("违法信息");
        arrTypes.add("人身攻击");

        mAct = this;
        impIReportFoulsUserActView = new ImpIReportFoulsUserActView(mAct, this);
        luId = getIntent().getStringExtra("luId");

        topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                mAct.finish();
            }

            @Override
            public void right2Click(View view)
            {
                return;
            }

            @Override
            public void rightClick(View view)
            {
                super.rightClick(view);
                impIReportFoulsUserActView.reportUser(luId, strSendType, evciRemarkContent.getInputText());
            }
        }).setText(getResources().getString(R.string.actReportFouls_TopBarTitle_Text));
        navBtns = topBarSwitch.getNavBtns2(new int[]{1, 0, 0, 1});
        for (int i = 0; i < navBtns.size(); i++)
        {
            TextView tv=navBtns.get(i).get(TopBarSwitch.NAV_BTN_ICON);
            if (i==1)
            {
                tv.setVisibility(View.INVISIBLE);
                tv.setText("提交");
            }
        }
        ReportFoulsAdapter adapter = new ReportFoulsAdapter(mAct, arrTypes);
        recyclerview.setAdapter(adapter);
      //  strSendType = arrTypes.get(DEAFULT_POSITION);

        mDialog = DialogUtil.getCommonDialog(mAct, getResources().getString(R.string.loadingReportFouls));
        mDialog.setCancelable(false);

        btnComment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String fStr = evciRemarkContent.getInputText();

                if (!TextUtils.isEmpty(fStr) || !TextUtils.isEmpty(strSendType))
                {
                    impIReportFoulsUserActView.reportUser(luId, strSendType, fStr);
                } else
                    {
                    ToastUtils.showShort("请选择举报理由");
                }
            }
        });

        int spacingInPixels = 10;
        recyclerview.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        recyclerview.setLayoutManager(new GridLayoutManager(this, 3));
        adapter.setListener(new ReportFoulsAdapter.OnRadioClickListener()
        {
            @Override
            public void OnRadioClick(View view , String data,int position)
            {
                strSendType = data;
            }
        });
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mAct, null, TAG, e);
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    @Override
    public void reportUserCallback(String response)
    {
        Logger.t(TAG).d("获得的结果：" + response);
        ToastUtils.showShort("衷心感谢您的反馈，您的青睐就是我们奋发的动力！");
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
        mAct.finish();
    }
}

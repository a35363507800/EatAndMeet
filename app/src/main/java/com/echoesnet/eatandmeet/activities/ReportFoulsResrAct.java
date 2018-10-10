package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.presenters.ImpIReportFoulsResrActView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IReportFoulsResrActView;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
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
 * 商户举报
 */
public class ReportFoulsResrAct extends BaseActivity implements IReportFoulsResrActView
{
    private static final String TAG = ReportFoulsResrAct.class.getSimpleName();
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.evciRemarkContent)
    EditViewWithCharIndicate evciRemarkContent;
    @BindView(R.id.btnComment)
    Button btnComment;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.allRadiosHolder)
    LinearLayout allRadiosHolder;

    private Dialog mDialog;
    private Activity mAct;
    private String rId = "";//举报 id
    private String strSendType = "";
    private ArrayList<String> arrTypes = new ArrayList<>();
    private ImpIReportFoulsResrActView impIReportFoulsResrActView;
    private HashMap<String, IconTextView> mapRadios = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_report_fouls);
        ButterKnife.bind(this);
        afterViews();
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

    private void afterViews()
    {
//        arrTypes.add("政治反动");
//        arrTypes.add("不实信息");
//        arrTypes.add("淫秽色情");
//        arrTypes.add("广告骚扰");
//        arrTypes.add("违法信息");
//        arrTypes.add("人身攻击");

        arrTypes.add("商家已关闭");
        arrTypes.add("商家说不支持使用看脸吃饭预定");
        arrTypes.add("商家说不支持使用看脸吃饭闪付");
        arrTypes.add("商家装修，不接待");

        mAct = this;
        impIReportFoulsResrActView = new ImpIReportFoulsResrActView(mAct, this);

        List<Map<String,TextView>> navBtns  = topBarSwitch.getNavBtns2(new int[]{1, 0, 0, 0});
        TextView title = topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                finish();
            }

            @Override
            public void right2Click(View view)
            {

            }
        });
        title.setText(getResources().getString(R.string.actReportRes_TopBarTitle_Text));
        title.setTypeface(null, Typeface.BOLD);
        rId = getIntent().getStringExtra("rId");
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
                    impIReportFoulsResrActView.reportResr(rId, fStr, strSendType);
                }
                else
                {
                    ToastUtils.showShort("请选择举报理由");
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

//        ReportFoulsAdapter adapter = new ReportFoulsAdapter(mAct, arrTypes);
//        recyclerview.setAdapter(adapter);
//        int spacingInPixels = 20;
//        recyclerview.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
//        recyclerview.setLayoutManager(new GridLayoutManager(this, 3));
//        adapter.setListener(new ReportFoulsAdapter.OnRadioClickListener()
//        {
//            @Override
//            public void OnRadioClick(View view , String data,int position)
//            {
//                strSendType = data;
//            }
//        });
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mAct, null, TAG, e);
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    @Override
    public void reportResrCallback(String response)
    {
        Logger.t(TAG).d("获得的结果：" + response);
        ToastUtils.showShort("衷心感谢您的反馈，您的青睐就是我们奋发的动力！");
        mAct.finish();
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

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
            }
            else
            {
                entry.getValue().setText("{eam-s-grade1 @color/FC7 @dimen/f3}");
            }
        }
    }
}

package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.CAddEmojBean;
import com.echoesnet.eatandmeet.presenters.ImpICAddEmojsView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICAddEmojsView;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.views.adapters.CAddEmojAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;


public class CAddEmojsAct extends MVPBaseActivity<CAddEmojsAct, ImpICAddEmojsView> implements ICAddEmojsView
{
    //region 变量
    private static final String TAG = CAddEmojsAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.lv_emoj_lst)
    PullToRefreshListView contactListView;

    private CAddEmojAdapter mCAddEmojAdapter;
    private List<CAddEmojBean> mCEmojLst;

    private Activity mContext;
    private Dialog pDialog;
    private ArrayList<String> emojiNames = new ArrayList<>();

    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cadd_emojs);
        ButterKnife.bind(this);
        initAfterView();
    }

    private void initAfterView()
    {

        topBar.setTitle("表情列表");
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.getLeftButton().setVisibility(View.VISIBLE);
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                Intent intent = new Intent();
                intent.putStringArrayListExtra("emojiNames", emojiNames);
                mContext.setResult(RESULT_OK, intent);
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

        pDialog = DialogUtil.getCommonDialog(this, "正在加载...");
        pDialog.setCancelable(false);

        contactListView.setMode(PullToRefreshBase.Mode.DISABLED);
        View empty = LayoutInflater.from(mContext).inflate(R.layout.empty_view, null);
        ((TextView) empty.findViewById(R.id.tv_default_des)).setText("暂时没有新表情");
        contactListView.setEmptyView(empty);

        mCEmojLst = new ArrayList<>();
        mCAddEmojAdapter = new CAddEmojAdapter(mContext, mCEmojLst);
        contactListView.setAdapter(mCAddEmojAdapter);
        mCAddEmojAdapter.setOnDownLoadBtnClickedListener(new CAddEmojAdapter.OnDownLoadBtnClickedListener()
        {
            @Override
            public void onDownLoadBtnClicked(int position, CAddEmojBean emojBean)
            {
                emojiNames.add(emojBean.getEmojiFilePath());
            }
        });

        if (mPresenter != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            mPresenter.getEmojDataLst();
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("emojiNames", emojiNames);
        mContext.setResult(RESULT_OK, intent);
        mContext.finish();
    }

    @Override
    protected void onDestroy()
    {
        if (pDialog != null)
            pDialog = null;
        super.onDestroy();
    }

    @Override
    protected ImpICAddEmojsView createPresenter()
    {
        return new ImpICAddEmojsView(this, this);
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mContext, null, exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getEmojDataLstCallback(List<CAddEmojBean> emojLst)
    {
        mCEmojLst.clear();
        mCEmojLst.addAll(emojLst);
        mCAddEmojAdapter.notifyDataSetChanged();
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }
}

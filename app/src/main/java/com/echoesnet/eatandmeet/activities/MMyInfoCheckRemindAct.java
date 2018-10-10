package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.MyInfoCheckBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.adapters.MyInfoCheckRemindAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MMyInfoCheckRemindAct extends BaseActivity {
    private static final String TAG = MMyInfoCheckRemindAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.ptfl_check_remind)
    PullToRefreshListView ptflCheckRemind;

    private Dialog pDialog;
    private Activity mContext;
    private List<MyInfoCheckBean> list;
    private MyInfoCheckRemindAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_myinfo_check_remind);
        ButterKnife.bind(this);
        afterViews();
    }

    private void afterViews() {
        mContext = this;
        topBar.setTitle("审核提醒");
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.setOnClickListener(new ITopbarClickListener() {
            @Override
            public void leftClick(View view) {
                mContext.finish();
            }

            @Override
            public void left2Click(View view) {

            }

            @Override
            public void rightClick(View view) {
            }
        });

        ptflCheckRemind.setMode(PullToRefreshBase.Mode.BOTH);
        ptflCheckRemind.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //下拉刷新
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //上拉加载
            }
        });

        ptflCheckRemind.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener()
        {
            @Override
            public void onLastItemVisible() {
                // Toast.makeText(context, "上拉刷新", Toast.LENGTH_SHORT).show();
            }
        });
        ptflCheckRemind.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                 Logger.t(TAG).d("position:"+position);
            }
        });
        list = new ArrayList<>();

        MyInfoCheckBean bean1 = new MyInfoCheckBean();
        bean1.setCheckTime("2016-03-23");
        bean1.setState("1");
        bean1.setUnCheck("未通过原因: 身份证照片不清晰");
        MyInfoCheckBean bean2 = new MyInfoCheckBean();
        bean2.setCheckTime("2016-03-24");
        bean2.setState("0");
        bean2.setUnCheck("通过原因: 成功");
        MyInfoCheckBean bean3 = new MyInfoCheckBean();
        bean3.setCheckTime("2016-03-25");
        bean3.setState("1");
        bean3.setUnCheck("未通过原因: 不符合基本条件");

        list.add(bean1);
        list.add(bean2);
        list.add(bean3);

        adapter = new MyInfoCheckRemindAdapter(mContext,list);
        ptflCheckRemind.setAdapter(adapter);

        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理");
        pDialog.setCancelable(false);
    }



    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(pDialog!=null&&pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog=null;
        }
    }
}

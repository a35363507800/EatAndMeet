package com.echoesnet.eatandmeet.activities.live;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eam.icontextmodule.model.EchoesEamIcon;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.activities.CUserInfoAct;
import com.echoesnet.eatandmeet.activities.MVPBaseActivity;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.AnchorSearchBean;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.presenters.ImpILAnchorSearchActView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ILAnchorSearchActView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.Constants;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.views.adapters.LAnchorsSearchAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.joanzapata.iconify.IconDrawable;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2016/10/17
 * @description
 */
public class LAnchorSearchAct extends MVPBaseActivity<LAnchorSearchAct, ImpILAnchorSearchActView> implements ILAnchorSearchActView
{
    private static final String TAG = LAnchorSearchAct.class.getSimpleName();
    private Activity mActivity;

    EditText etSearch;
    ImageView ivDelete;
    //直播list
    @BindView(R.id.rv_search)
    RecyclerView rvSearch;
    @BindView(R.id.no_data)
    LinearLayout llNoData;
    @BindView(R.id.ll_search_top)
    TopBarSwitch topBarSwitch;

    //startStr 起始id ，dataNum 返回条数
    private String startStr = "0";
    private String dataNum = "10";

    // 进度显示
    private Dialog pDialog;

    private List<AnchorSearchBean> mAnchorSearchBeanList;
    private LAnchorsSearchAdapter mAnchorsSearchAdapter;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);

        setContentView(R.layout.act_lanchor_search);
        ButterKnife.bind(this);
        mActivity = this;
        afterViews();
    }

    @Override
    protected ImpILAnchorSearchActView createPresenter()
    {
        return new ImpILAnchorSearchActView();
    }

    private void afterViews()
    {
        initTopbar();
        pDialog = DialogUtil.getCommonDialog(mActivity, "正在处理...");
        pDialog.setCancelable(false);
        mAnchorSearchBeanList = new ArrayList<>();
        ((TextView) llNoData.findViewById(R.id.tv_default_des)).setText("没有找到相应的直播间，请重试");

        //设置布局管
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSearch.setLayoutManager(linearLayoutManager);
        mAnchorsSearchAdapter = new LAnchorsSearchAdapter(mActivity, mAnchorSearchBeanList);
        rvSearch.setAdapter(mAnchorsSearchAdapter);
        mAnchorsSearchAdapter.setItemClickListener(new LAnchorsSearchAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                AnchorSearchBean anchorSearchBean = mAnchorSearchBeanList.get(position);
                if ("1".equals(anchorSearchBean.getStatus()))
                {
                    //直播中 用户身份进入
                    EamApplication.getInstance().liveIdentity = Constants.MEMBER;
                    CommonUtils.startLiveProxyAct(mActivity, LiveRecord.ROOM_MODE_MEMBER, "", "", "", anchorSearchBean.getRoomId(), null, EamCode4Result.reqNullCode);
                } else
                {
                    Intent userIntent = new Intent(mActivity, CNewUserInfoAct.class);
                    userIntent.putExtra("checkWay", "UId");
                    userIntent.putExtra("toUId", anchorSearchBean.getuId());
                    startActivity(userIntent);
                }
            }
        });
    }

    private void initTopbar()
    {
        View view = topBarSwitch.inflateCustomCenter(R.layout.part_top_bar_search, new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {

            }

            @Override
            public void right2Click(View view)
            {
                finish();
            }
        });
        List<TextView> navBtns = topBarSwitch.getNavBtns(new int[]{1, 0, 0, 1});
        for (int i = 0; i < navBtns.size(); i++)
        {
            TextView tv = navBtns.get(i);
            if (i == 0)
                tv.setVisibility(View.GONE);
            else if (i == 1)
            {
                tv.setVisibility(View.VISIBLE);
                tv.setTextSize(16);
                tv.setText("取消");
            }
        }
        etSearch = (EditText) view.findViewById(R.id.et_search);
        ivDelete = (ImageView) view.findViewById(R.id.iv_delete);
        ivDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                etSearch.setText("");
            }
        });
        etSearch.addTextChangedListener(watcher);
    }

    //点击其他区域收回软键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
        {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev))
            {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev))
        {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event)
    {
        if (v != null && (v instanceof EditText))
        {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom)
            {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else
            {
                return true;
            }
        }
        return false;
    }

    TextWatcher watcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {

        }

        @Override
        public void afterTextChanged(Editable s)
        {

            if (etSearch.getText().length() != 0)
            {
                if (ivDelete.getVisibility() == View.GONE)
                {
                    ivDelete.setVisibility(View.VISIBLE);
                }
                ivDelete.setImageDrawable(new IconDrawable(LAnchorSearchAct.this, EchoesEamIcon.eam_s_close2).colorRes(R.color.FC3));

                //刷新数据
                if (mPresenter != null)
                    mPresenter.getSearchData(etSearch.getText().toString(), startStr, dataNum);
            } else
            {
                if (ivDelete.getVisibility() == View.VISIBLE)
                {
                    ivDelete.setVisibility(View.GONE);
                }
                mAnchorSearchBeanList.clear();
                mAnchorsSearchAdapter.notifyDataSetChanged();
                llNoData.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {

    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        llNoData.setVisibility(View.VISIBLE);
    }

    @Override
    public void getLiveSearchCallback(List<AnchorSearchBean> response)
    {
        if (response != null)
        {
            if (response.size() > 0)
            {
                llNoData.setVisibility(View.GONE);
            } else
            {
                llNoData.setVisibility(View.VISIBLE);
            }
            mAnchorSearchBeanList.clear();
            mAnchorSearchBeanList.addAll(response);
            mAnchorsSearchAdapter.notifyDataSetChanged();
        } else
        {
            llNoData.setVisibility(View.VISIBLE);
        }

        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

}

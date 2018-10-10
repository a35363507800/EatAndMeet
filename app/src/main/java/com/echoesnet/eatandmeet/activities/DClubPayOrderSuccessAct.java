package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.MeetPersonBean;
import com.echoesnet.eatandmeet.presenters.ImpDPayOrderSuccessView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDPayOrderSuccessView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.IMUtils.Constant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.PushMeetPersonsAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2018/2/26
 * @description
 */
public class DClubPayOrderSuccessAct extends BaseActivity
{
    private final static String TAG = DClubPayOrderSuccessAct.class.getSimpleName();

    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.btn_back_res)
    Button btnReturnHome;
    @BindView(R.id.btn_check_order)
    Button btnCheckOrders;
    private Activity mActivity;
    private int geotable_id;
    private String orderId;
    private Dialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_club_pay_success);
        ButterKnife.bind(this);
        initAfterViews();
    }


    void initAfterViews()
    {
        mActivity = this;
        pDialog = DialogUtil.getCommonDialog(this, "正在加载...");
        pDialog.setCancelable(false);
        geotable_id = CommonUtils.BAIDU_GEOTABLE_ID;
        orderId = getIntent().getStringExtra("orderId");
        topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
            }

            @Override
            public void right2Click(View view)
            {
            }
        }).setText("支付成功");
        topBarSwitch.getNavBtns2(new int[]{0, 0, 0, 0});

    }

    @OnClick({R.id.btn_back_res, R.id.btn_check_order})
    void clickView(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_back_res:
                Intent intent2 = new Intent(mActivity, ClubDetailAct.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent2.putExtra("clubId", SharePreUtils.getClubId(mActivity));
                mActivity.startActivity(intent2);
                this.finish();
                break;
            case R.id.btn_check_order:
                Intent intent1 = new Intent(mActivity, ClubOrderRecordDetailAct.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent1.putExtra("orderId", getIntent().getStringExtra("orderId"));
                mActivity.startActivity(intent1);
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}

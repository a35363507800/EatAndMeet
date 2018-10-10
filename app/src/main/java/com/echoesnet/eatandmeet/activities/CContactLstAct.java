package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.NewContactLstBean;
import com.echoesnet.eatandmeet.presenters.ImpCContactLstView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICContactLstView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.google.gson.reflect.TypeToken;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.hyphenate.util.NetUtils;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 联系人列表
 */
public class CContactLstAct extends MVPBaseActivity<ICContactLstView, ImpCContactLstView> implements ICContactLstView
{
    //region 变量
    private static final String TAG = CContactLstAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.all_new_friend)
    AutoLinearLayout allNewFriends;
    @BindView(R.id.all_friend_container)
    AutoLinearLayout getAllNewFriendContainer;
    @BindView(R.id.tv_new_friend_count)
    TextView tvNewFriendCount;
    @BindView(R.id.fragment_container)
    RelativeLayout rlContainer;
//    ContactListFragment contactListFragment;

    private boolean isFirstLoaded = false;
    private Activity mContext;
    private Dialog pDialog;
    //endregion
//    private LiveRoomAct1 mRoomActivity  = new LiveRoomAct1();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ccontact_lst);
        ButterKnife.bind(this);
        initAfterView();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (mPresenter != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            mPresenter.getApplyFriendInfo("3");
        }
//        //布局
//        if (!isFirstLoaded)
//        {
//            android.support.v4.app.FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
//            fragTransaction.detach(contactListFragment);
//            fragTransaction.attach(contactListFragment);
//            fragTransaction.commit();
//        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        isFirstLoaded = false;
    }

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


   private void initAfterView()
    {
        mContext = this;
        topBar.setTitle("联系人");
        topBar.getRightButton().setVisibility(View.VISIBLE);
        topBar.getRightButton().setText("添加好友");
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.getRightButton().setTextColor(ContextCompat.getColor(mContext, R.color.white));
        topBar.getLeftButton().setVisibility(View.VISIBLE);

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
                //启动添加朋友界面

                Intent intent=new Intent(mContext, CAddNewFriendAct.class);
                startActivity(intent);
                NetUtils.hasDataConnection(mContext);
            }
        });
//        contactListFragment = ContactListFragment.newInstance(1);
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.fragment_container, contactListFragment)
//                .show(contactListFragment)
//                .commit();

        isFirstLoaded = true;
        Logger.t(TAG).d(isFirstLoaded + "");

        pDialog = DialogUtil.getCommonDialog(this, "正在处理...");
        pDialog.setCancelable(false);
    }

    @Override
    protected ImpCContactLstView createPresenter()
    {
        return new ImpCContactLstView();
    }

    @OnClick({R.id.all_friend_container})
    void viewClick(View v)
    {
        switch (v.getId())
        {
            case R.id.all_friend_container:
                Intent intent=new Intent(mContext, CNewFriendsAct.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 设置待同意的好友数
     */
    private void setApplyFriendInfo(String imgCount, List<NewContactLstBean> lstBean)
    {
        //Logger.t(TAG).d("设置："+imgCount);
        if (imgCount.equals("0"))
        {
            getAllNewFriendContainer.setVisibility(View.VISIBLE);
            tvNewFriendCount.setVisibility(View.GONE);
            allNewFriends.removeAllViews();
        }
        else
        {
            getAllNewFriendContainer.setVisibility(View.VISIBLE);
            tvNewFriendCount.setVisibility(View.VISIBLE);
            tvNewFriendCount.setText(imgCount);
            allNewFriends.removeAllViews();
            for (NewContactLstBean newContactLstBean : lstBean)
            {
                LevelHeaderView riv = new LevelHeaderView(mContext, 0, 0);
                riv.setLiveState(false);
                riv.setLevel(newContactLstBean.getLevel());
                riv.setHeadImageByUrl(newContactLstBean.getPhUrl());
                LinearLayout.LayoutParams lParam = new LinearLayout.LayoutParams(CommonUtils.dp2px(mContext, 36), CommonUtils.dp2px(mContext, 36));
                lParam.setMargins(CommonUtils.dp2px(mContext, 8), 0, 0, 0);
                riv.setLayoutParams(lParam);
                allNewFriends.addView(riv, 0);
            }

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
    public void getApplyFriendInfoCallback(String response)
    {
        Logger.t(TAG).json(response);
        try
        {
            JSONObject body = new JSONObject(response);
            String arr = body.getString("detail");
            Logger.t(TAG).d("数量" + body.getString("amount"));
            List<NewContactLstBean> lstBean = EamApplication.getInstance().getGsonInstance().fromJson(arr, new TypeToken<List<NewContactLstBean>>()
            {
            }.getType());
            setApplyFriendInfo(body.getString("amount"), lstBean);

            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }catch (JSONException e)
        {
            Logger.t(TAG).d(e.getMessage());
        }
    }
}

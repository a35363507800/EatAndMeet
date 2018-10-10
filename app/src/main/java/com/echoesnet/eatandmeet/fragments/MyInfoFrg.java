package com.echoesnet.eatandmeet.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.IdentityAuthAct;

import com.echoesnet.eatandmeet.activities.MWalletAct;
import com.echoesnet.eatandmeet.activities.MyDateAct;
import com.echoesnet.eatandmeet.activities.MyFriendStateAct;
import com.echoesnet.eatandmeet.activities.MyInfoAccountAct2;
import com.echoesnet.eatandmeet.activities.MyInfoCollectAct;
import com.echoesnet.eatandmeet.activities.MyInfoEditAct;
import com.echoesnet.eatandmeet.activities.MyInviteFriendsAct;
import com.echoesnet.eatandmeet.activities.MyLevelAct;
import com.echoesnet.eatandmeet.activities.MyNormalOrderShowAct;
import com.echoesnet.eatandmeet.activities.MyOrdersAct;
import com.echoesnet.eatandmeet.activities.MySettingAct;
import com.echoesnet.eatandmeet.activities.RelationAct;
import com.echoesnet.eatandmeet.activities.TaskAct;
import com.echoesnet.eatandmeet.activities.live.LiveAuthenticationPassActivity;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.QRCodeBean;
import com.echoesnet.eatandmeet.presenters.ImpMyInfoView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyInfoView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.IMUtils.TXConstants;
import com.echoesnet.eatandmeet.utils.IMUtils.TencentHelper;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;
import okhttp3.Call;

public class MyInfoFrg extends MVPBaseFragment<IMyInfoView, ImpMyInfoView> implements IMyInfoView
{
    private static final String TAG = MyInfoFrg.class.getSimpleName();
    //region 变量
    @BindView(R.id.rlUserInfoGroup)
    RelativeLayout rlUserInfoGroup;
    @BindView(R.id.riv_head)
    LevelHeaderView levelHeaderView;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.tv_attention_number)
    TextView tvAttentionNumber;
    @BindView(R.id.tv_newfans_number)
    TextView tvNewfansNumber;
    @BindView(R.id.tv_fans_number)
    TextView tvFansNumber;
    @BindView(R.id.tv_friending_number)
    TextView tvFriendingNumber;
    @BindView(R.id.tv_orderform_number)
    TextView tvOrderformNumber;
    @BindView(R.id.tv_dateing)
    TextView tvDateing;
    @BindView(R.id.tv_dateing_number)
    TextView tvDateingNumber;
    @BindView(R.id.level_u_view)
    LevelView lvView;
    @BindView(R.id.img_u_sex)
    GenderView ivSex;
    @BindView(R.id.tv_id)
    TextView idTv;  // ID
    @BindView(R.id.tv_fan_num_count)
    TextView tvFanNumCount;  // ID
    @BindView(R.id.tv_name)
    TextView nameTv; // 昵称
    @BindView(R.id.tv_task)
    TextView tvTask;
    @BindView(R.id.tv_task_number)
    TextView tvTaskNumber;
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.one_view)
    View vTop;
    private Unbinder unbinder;

    private Activity mContext;
    private List<Map<String, TextView>> navBtns;
    //endregion

    private boolean needRefreshMsgInfo = true, needRefreshUserInfo = true, needRefreshAccountInfo = true;


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.mfrg_myinfo2, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        initAfterView();
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Logger.t(TAG).d("needRefreshUserInfo--> " + needRefreshUserInfo);
        if (mPresenter != null)
        {
            mPresenter.getMyInfo();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterBroadcastReceiver();
    }

    @Override
    protected ImpMyInfoView createPresenter()
    {
        return new ImpMyInfoView();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    public MyInfoFrg()
    {
        // Required empty public constructor
    }

    public static MyInfoFrg newInstance()
    {
        return new MyInfoFrg();
    }

    private void initAfterView()
    {
        mContext = getActivity();

//        topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
//        {
//            @Override
//            public void leftClick(View view)
//            {
//                startActivity(new Intent(mContext, MySettingAct.class));
//            }
//
//            @Override
//            public void rightClick(View view)
//            {
//                QRCodeBean qrCodeBean = new QRCodeBean();
//                qrCodeBean.setType("UID");
//                qrCodeBean.setContent(SharePreUtils.getUId(getActivity()));
//                String QRStr = new Gson().toJson(qrCodeBean);
//                Logger.t(TAG).d("二维码内容--> " + QRStr);
//                // getQRCode(mContext, CommonUtils.createQRImage(mContext, QRStr, 200, 200));
//
//                CommonUtils.getQRCode(mContext, CommonUtils.createQRImage(mContext, QRStr, 200, 200), true);
//            }
//
//            @Override
//            public void right2Click(View view)
//            {
//                Logger.t(TAG).d("消息中心按钮事件");
//                startActivity(new Intent(mContext, MMyInfoCenterMessageAct.class));
//            }
//        });
       // topBarSwitch.setBackground(ContextCompat.getDrawable(mContext, R.drawable.transparent));
      //  navBtns = topBarSwitch.getNavBtns2(new int[]{1, 0, 1, 1});
//        for (int i = 0; i < navBtns.size(); i++)
//        {
//            Map<String, TextView> map = navBtns.get(i);
//            TextView tv = map.get(TopBarSwitch.NAV_BTN_ICON);
//            tv.setTextSize(22);
//            tv.setTextColor(ContextCompat.getColor(mContext, R.color.C0412));
//            switch (i)
//            {
//                case 0:
//                    tv.setText("{eam-e978}");
//                    break;
//                case 1:
//                    tv.setText("{eam-s-code}");
//                    break;
//                case 2:
//                    tv.setText("{eam-s-msg}");
//                    break;
//            }
//        }


        rlUserInfoGroup.setFocusable(true);
        rlUserInfoGroup.setFocusableInTouchMode(true);
        rlUserInfoGroup.requestFocus();


        if (mPresenter != null)
        {
            mPresenter.getLivePlaySwap();
            mPresenter.getMyInfo();
        }
        registerBroadcastReceiver();

        //适配低版本状态栏颜色
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && !Build.MANUFACTURER.equalsIgnoreCase("Xiaomi"))
        {
            vTop.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.C0412T50));
        }

        //适配美图状态栏颜色
        if (Build.MANUFACTURER.equalsIgnoreCase("Meitu"))
        {
            vTop.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.C0412T50));
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            // CommonUtils.setTranslucent(getActivity());
            if (mPresenter != null)
            {
                mPresenter.getMyInfo();
            }
        }
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }


    @Optional
    @OnClick({//以下是重构监听id
            R.id.rl_money_layout, R.id.rl_date_layout, R.id.rl_collect_layout, R.id.rl_orderform_layout,
            R.id.rl_level_layout, R.id.rl_task_layout, R.id.rl_set_layout, R.id.rlUserInfoGroup,
            R.id.rl_attention_layout, R.id.rl_fans_layout, R.id.rl_friend_state, R.id.riv_head,
            R.id.rl_code_layout, R.id.rl_friend_layout
    })
    void clickEvent(View view)
    {
        switch (view.getId())
        {
            //编辑用户资料
            case R.id.rlUserInfoGroup:
            case R.id.riv_head:
                Intent intent = new Intent(getActivity(), MyInfoEditAct.class);
                startActivity(intent);
                break;
            case R.id.rl_orderform_layout:    //订单
                startActivity(new Intent(mContext, MyOrdersAct.class));
                break;
            case R.id.rl_set_layout:         //设置
                startActivity(new Intent(mContext, MySettingAct.class));
                break;
            case R.id.rl_friend_layout:         //邀请好友
                startActivity(new Intent(mContext, MyInviteFriendsAct.class));
                break;
            case R.id.rl_collect_layout:         //收藏
                startActivity(new Intent(mContext, MyInfoCollectAct.class));
                break;
            case R.id.rl_date_layout:         //约会
                if ("1".equals(EamApplication.getInstance().isShowReceive))
                {
                    ToastUtils.showShort("此功能暂未开放");
                    return;
                }
                ((Activity) mContext).startActivityForResult(new Intent(mContext, MyDateAct.class), EamConstant.EAM_OPEN_TASK);
                break;
            case R.id.rl_money_layout:   //钱包
            {
                Intent intent10 = new Intent(getActivity(), MWalletAct.class);
                startActivity(intent10);
                //MyInfoAccountAct_.intent(getActivity()).start();
                // 设置setSource 原因是: 从邻座聊天发信息后, 点击我的, 点击我的余额, 进入我的账户, 点击余额明细后, 一路返回, 返回到邻座
                // 修改后要求回到我的界面
                SharePreUtils.setSource(mContext, "myInfo");
                break;
            }
            case R.id.rl_level_layout:   //等级
                Intent myLevelIntent = new Intent(mContext, MyLevelAct.class);
                startActivity(myLevelIntent);
                break;
            case R.id.rl_task_layout:   //任务
                Intent taskIntent = new Intent(mContext, TaskAct.class);
                ((Activity) mContext).startActivityForResult(taskIntent, EamConstant.EAM_OPEN_TASK);
                break;
            case R.id.rl_friend_state:   //动态
                Intent friendStateIntent = new Intent(mContext, MyFriendStateAct.class);
                startActivity(friendStateIntent);
                break;
            case R.id.rl_fans_layout:   //粉丝
                Intent fansIntent = new Intent(mContext, RelationAct.class);
                fansIntent.putExtra("openFrom", "fans-list");
                startActivity(fansIntent);
                break;
            case R.id.rl_attention_layout:   //关注
                Intent attentionIntent = new Intent(mContext, RelationAct.class);
                attentionIntent.putExtra("openFrom", "follow-list");
                startActivity(attentionIntent);
                break;
            case R.id.rl_code_layout:   //二维码
                QRCodeBean qrCodeBean = new QRCodeBean();
                qrCodeBean.setType("UID");
                qrCodeBean.setContent(SharePreUtils.getUId(getActivity()));
                String QRStr = new Gson().toJson(qrCodeBean);
                Logger.t(TAG).d("二维码内容--> " + QRStr);
                CommonUtils.getQRCode(mContext, CommonUtils.createQRImage(mContext, QRStr, 200, 200), true);
                break;
            default:
                break;
        }
    }


    BroadcastReceiver updateUiReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            Logger.t(TAG).d("myInfoFrg:" + action);
            if (EamConstant.ACTION_UPDATE_USER_BALANCE.equals(action))
            {
                needRefreshAccountInfo = intent.getExtras().getBoolean("needRefreshAccountInfo");
            }
            if (EamConstant.ACTION_UPDATE_USER_INFO.equals(action))
            {
                needRefreshUserInfo = intent.getExtras().getBoolean("needRefreshUserInfo");
            }
            if (EamConstant.ACTION_UPDATE_USER_MSG.equals(action))
            {
                needRefreshMsgInfo = intent.getExtras().getBoolean("needRefreshMsg");
            }
            if (EamConstant.EAM_USERINFO_PAGE_OPEN_SOURCE.equals(action))
            {
                if (mPresenter != null)
                {

                    mPresenter.getMyInfo();
                }
            }
            if (EamConstant.EAM_HX_CMD_RECEIVE_RED_REMIND.equals(action))
            {
                if (mPresenter != null)
                {
                    mPresenter.getMyInfo();
                }
            }
        }
    };

    private void registerBroadcastReceiver()
    {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(EamConstant.ACTION_UPDATE_BALANCE);
        myIntentFilter.addAction(EamConstant.ACTION_UPDATE_USER_BALANCE);
        myIntentFilter.addAction(EamConstant.ACTION_UPDATE_USER_INFO);
        myIntentFilter.addAction(EamConstant.ACTION_UPDATE_USER_MSG);
        myIntentFilter.addAction(EamConstant.EAM_HX_CMD_RECEIVE_RED_REMIND);
        myIntentFilter.addAction(EamConstant.EAM_USERINFO_PAGE_OPEN_SOURCE);
        //myIntentFilter.
        Logger.t(TAG).d("注册广播");
        //注册广播
        mContext.registerReceiver(updateUiReceiver, myIntentFilter);
    }

    private void unregisterBroadcastReceiver()
    {
        if (updateUiReceiver != null)
        {
            Logger.t(TAG).d("注销广播");
            mContext.unregisterReceiver(updateUiReceiver);
        }
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case  NetInterfaceConstant.LiveC_swap:
                 getLivePlaySwapCallback("0");
                break;
        }
    }

    @Override
    public void getLivePlaySwapCallback(String receive)
    {
        if (!TextUtils.isEmpty(receive))
            EamApplication.getInstance().isShowReceive = receive;

    }

    @Override
    public void getMyinfoCallBack(String response)
    {
        Logger.t(TAG).d("========我的页面信息返回结果===============" + response);
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            String receiveNum = jsonObject.getString("receiveNum");
            String fansNum = jsonObject.getString("fansNum");
            String newFansNum = jsonObject.getString("newFansNum");
            String focusNum = jsonObject.getString("focusNum");
            String orderNum = jsonObject.getString("orderNum");
            String trendNum = jsonObject.getString("trendNum");
            String taskNum = jsonObject.getString("taskNum");
            String sex = jsonObject.getString("sex");
            String age = jsonObject.getString("age");
            String level = jsonObject.getString("level");
            String uphUrl = jsonObject.getString("uphUrl");
            String id = jsonObject.getString("id");
            String privilege = jsonObject.getString("privilege");
            String nicName = jsonObject.getString("nicName");
            String balance = jsonObject.getString("balance");
            String isSign = jsonObject.getString("isSign");
            String isVUser = jsonObject.getString("isVuser");

            tvAttentionNumber.setText(focusNum);

            //设置粉丝数========逻辑为显示为:粉丝数=粉丝数-新粉丝数============
            if (newFansNum.equals("0"))
            {
                tvNewfansNumber.setText("");
                tvFansNumber.setText(fansNum);
            } else
            {
                tvNewfansNumber.setText("+" + newFansNum);
                try
                {
                    tvFansNumber.setText(String.valueOf(Integer.parseInt(fansNum) - Integer.parseInt(newFansNum)));
                } catch (NumberFormatException e)
                {
                    tvFansNumber.setText(fansNum);
                }
            }


            tvFriendingNumber.setText(trendNum);

            tvOrderformNumber.setText(orderNum);
            if (orderNum.equals("0"))
                tvOrderformNumber.setVisibility(View.GONE);
            else
            {
                tvOrderformNumber.setVisibility(View.VISIBLE);
            }

            tvBalance.setText(balance);

            try
            {
                Integer.parseInt(receiveNum);
                tvDateing.setVisibility(View.GONE);
                tvDateingNumber.setVisibility(View.VISIBLE);
                tvDateingNumber.setText(receiveNum);

            } catch (NumberFormatException e)
            {
                tvDateing.setVisibility(View.VISIBLE);
                tvDateingNumber.setVisibility(View.GONE);
                tvDateing.setText(receiveNum);
            }

            try
            {
                Integer.parseInt(taskNum);
                tvTaskNumber.setVisibility(View.VISIBLE);
                tvTask.setVisibility(View.GONE);
                tvTaskNumber.setText(taskNum);

            } catch (NumberFormatException e)
            {
                tvTask.setVisibility(View.VISIBLE);
                tvTaskNumber.setVisibility(View.GONE);
                tvTask.setText(taskNum);
            }

            ivSex.setSex(age, sex);

            try
            {
                int lv = Integer.parseInt(level);
                SharePreUtils.setLevel(mContext, Integer.parseInt(level));

                lvView.setLevel(level, LevelView.USER);

            } catch (NumberFormatException e)
            {
                e.printStackTrace();
                Logger.t(TAG).d("等级格式不正确：" + level);
            }

            levelHeaderView.setHeadImageByUrl(uphUrl);
            levelHeaderView.showRightIcon(isVUser);

            Type ype = new TypeToken<List<String>>()
            {
            }.getType();
            SharePreUtils.setPrivilege(mContext, (List<String>) EamApplication.getInstance().getGsonInstance().fromJson(privilege, ype));
            SharePreUtils.setSex(mContext, sex);
            SharePreUtils.setAge(mContext, age);
            SharePreUtils.setId(mContext, id);
            SharePreUtils.setIsVUser(mContext, isVUser);
            Map<String, String> map = new HashMap<String, String>();
            map.put(TXConstants.TX_CUSTOM_INFO_1_KEY_LEVEL, SharePreUtils.getLevel(mContext) + "");
            map.put(TXConstants.TX_CUSTOM_INFO_1_KEY_SIGN, SharePreUtils.getIsSignAnchor(mContext) + "");
            map.put(TXConstants.TX_CUSTOM_INFO_1_KEY_SIGN, SharePreUtils.getIsSignAnchor(mContext) + "");
            map.put(TXConstants.TX_CUSTOM_INFO_1_KEY_VUSER, SharePreUtils.getIsVUser(mContext) + "");
            TencentHelper.setUserInfoToCustom1TxServer(map, null);

            //保存本地 此人是否为签约主播   在充值界面控制 是否可以提现  ---yqh
            SharePreUtils.setIsSignAnchor(mContext, isSign);

            if (!TextUtils.isEmpty(id))
            {
                idTv.setText("ID : " + id);
            } else
            {
                idTv.setText("ID : ");
            }

            if (!TextUtils.isEmpty(nicName))
            {
                nameTv.setText(nicName);
            } else
            {
                nameTv.setText("");
            }


        } catch (JSONException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("========我的页面信息解析错误===============" + response);
        }
    }


    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }
}

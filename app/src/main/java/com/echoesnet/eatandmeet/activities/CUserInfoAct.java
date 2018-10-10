package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.live.LiveShowBootyCallAct;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.activities.liveplay.managers.ViewShareHelper;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.CGiftBean;
import com.echoesnet.eatandmeet.models.bean.UsersBean;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.presenters.ImpICUserInfoView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICUserInfoView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.echoesnet.eatandmeet.utils.LoadingProgressUtil;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.CUserInfoHeadImgAdapter;
import com.echoesnet.eatandmeet.views.adapters.UserLabelAdapter;
import com.echoesnet.eatandmeet.views.widgets.Change2thNicNameDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.echoesnet.eatandmeet.views.widgets.MyGridView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2017/6/14
 * @description 用户详细信息界面
 */
public class CUserInfoAct extends MVPBaseActivity<CUserInfoAct, ImpICUserInfoView> implements ICUserInfoView
{
    private static final String TAG = CUserInfoAct.class.getSimpleName();
    //region 变量
    private Activity mContext;
    @BindView(R.id.level_view_act_per)
    LevelView levelView;
    @BindView(R.id.btn_left)
    IconTextView btnLeft;//左上角返回
    @BindView(R.id.btn_right)
    IconTextView tvRight;//右上角状态
    @BindView(R.id.tv_title_name)
    TextView tvTitleName;//上方昵称
    @BindView(R.id.iv_head)
    ImageView ivHead;//大图头像

    @BindView(R.id.tv_age)
    IconTextView tvAge;//简介年龄
    @BindView(R.id.all_order_res)
    AutoLinearLayout llAllMRes;//控制显示订餐信息
    @BindView(R.id.ll_want_to_go)
    AutoLinearLayout llWantToGo;//最近想去点击
    @BindView(R.id.ll_want_to_go2)
    AutoLinearLayout llWantToGo2;//最近想去点击
    @BindView(R.id.ll_at_where_now)
    AutoLinearLayout llAtWhereNow;//当前所在显示
    @BindView(R.id.tv_m_res)
    TextView tvMRes;//订餐餐厅
    @BindView(R.id.user_id)
    TextView userId;//简介ID
    @BindView(R.id.tv_fan_num_count)
    TextView tvFanNumCount;//简介饭票数
    @BindView(R.id.tv_report)
    TextView tvReport;//简介举报
    @BindView(R.id.rv_user_imgs)
    RecyclerView mRecyclerView;//上传的照片
    @BindView(R.id.at_where_now)
    IconTextView atWhereNow;//当前所在
    @BindView(R.id.tv_want_to_go)
    TextView tvWantToGo;//好友时最近想去
    @BindView(R.id.tv_want_to_go_time)
    TextView tvWantToGoTime;//好友时最近想去的时间
    @BindView(R.id.last_chuxian)
    TextView lastChuXian;//上次出现
    @BindView(R.id.tv_nickname)
    TextView tvNickName;//基本信息中昵称
    @BindView(R.id.btn_addNote)
    Button btnAddNote;//添加备注
    @BindView(R.id.tv_gender_two)
    TextView tvGenderTwo;//基本信息中性别
    @BindView(R.id.tv_height)
    TextView tvHeight;//基本信息中身高
    @BindView(R.id.tv_xingzuo)
    TextView tvXingZuo;//基本信息中星座
    @BindView(R.id.tv_ganqing)
    TextView tvGanQing;//基本信息中感情
    @BindView(R.id.tv_birthday)
    TextView tvBirthday;//基本信息中生日
    @BindView(R.id.tv_xueli)
    TextView tvXueLi;//基本信息中学历
    @BindView(R.id.tv_zhiye)
    TextView tvZhiYe;//基本信息中职业
    @BindView(R.id.tv_shouru)
    TextView tvShouRu;//基本信息中收入
    @BindView(R.id.ll_user_sign)
    AutoLinearLayout llUserSign;
    @BindView(R.id.tv_sign)
    TextView tvSign;//基本信息中签名
    @BindView(R.id.ll_user_tag)
    AutoLinearLayout llUserTag;//标签父类
    @BindView(R.id.user_tag_label)
    MyGridView userTagLabel;//标签
    @BindView(R.id.tv_my_booty_call)
    TextView myBootyCallTv;//我的约会
    @BindView(R.id.u_info_line)
    TextView uInfoLine;//竖线
    //以下为陌生人时的控件
    @BindView(R.id.tv_want_to_go2)
    TextView tvWantToGo2;//最近想去
    @BindView(R.id.tv_want_to_go_time2)
    TextView tvWantToGoTime2;//最近想去时间
    @BindView(R.id.last_chuxian2)
    TextView lastChuXian2;//上次出现
    @BindView(R.id.tv_ganqing2)
    TextView tvGanQing2;//感情
    @BindView(R.id.tv_height2)
    TextView tvHeight2;//身高
    @BindView(R.id.tv_xingzuo2)
    TextView tvXingZuo2;//星座
    @BindView(R.id.tv_city)
    TextView tvCity;//城市
    @BindView(R.id.tv_zhiye2)
    TextView tvZhiYe2;//职业
    @BindView(R.id.tv_sign2)
    TextView tvSign2;//签名

    @BindView(R.id.all_friend_show)
    AutoLinearLayout allFriendShow;//好友时显示
    @BindView(R.id.all_stranger_show)
    AutoLinearLayout allStrangerShow;//陌生人时显示
    @BindView(R.id.all_apply_friend)
    AutoLinearLayout allApplyFriend;//申请好友部分
    @BindView(R.id.btn_begin_chat)
    Button allChatButton;//聊天按钮部分
    @BindView(R.id.btn_accept)
    Button allAcceptButton;
    @BindView(R.id.btn_apply_by_money)
    Button btnApplyByMoney;
    @BindView(R.id.btn_apply_by_hello)
    Button btnApplyByHello;
    @BindView(R.id.loading_view)
    RelativeLayout loadingView;//加载布局
    @BindView(R.id.all_back)
    AutoLinearLayout allBack;

    private String toCheckUserUid;//查看用户的uId;
    private String toCheckUserId; //查看用户的Id;
    private String toCheckUserNicName;
    private String toCheckUserIdH;//查看用户的环信Id;
    private String toCheckUserIdT;//查看用户腾讯Id
    private String toCheckUserAvRoomId;//查看的用户直播房间号(要查看人的房间号，不是当前直播间的)
    private String reMark = "";
    private String currentUserAvRoomId;//当前用户加入的直播间Id

    private boolean isGhostUser = false;
    private UserLabelAdapter userLabelAdapter;
    private CUserInfoHeadImgAdapter mCUserInfoHeadImgAdapter;
    private List<String> userImgs = new ArrayList<>();
    private Map<String, Object> gotInfoMap;

    private Dialog pDialog;
    //private HuanXinIMHelper.DataSyncListener contactSyncListener;
    private UsersBean usersBean;
    private String chatRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.act_personalinfo);
        ButterKnife.bind(this);
        initAfterViews();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //HuanXinIMHelper.getInstance().removeSyncContactListener(contactSyncListener);
    }

    @Override
    protected ImpICUserInfoView createPresenter()
    {
        return new ImpICUserInfoView();
    }

    private void initAfterViews()
    {
        tvRight.setText("");
        tvRight.setTag("");
        uInfoLine.setVisibility(View.GONE);
        pDialog = DialogUtil.getCommonDialog(mContext, "正在获取...");
        pDialog.setCancelable(false);

        toCheckUserUid = getIntent().getStringExtra("toUId");
        toCheckUserId = getIntent().getStringExtra("toId");
        chatRoomId = getIntent().getStringExtra("chatRoomId");
        if (!TextUtils.isEmpty(toCheckUserId))
            toCheckUserIdT = toCheckUserId;
        currentUserAvRoomId = getIntent().getStringExtra("currentRoomId");
        tvSign.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvSign2.setMovementMethod(ScrollingMovementMethod.getInstance());
        //region 设置用户图片的列表
        //设置布局管
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //设置适配器
        mCUserInfoHeadImgAdapter = new CUserInfoHeadImgAdapter(mContext, userImgs);
        mCUserInfoHeadImgAdapter.setOnItemClickListener(new CUserInfoHeadImgAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                CommonUtils.showImageBrowser(mContext, userImgs, position, view);
            }
        });
        mRecyclerView.setAdapter(mCUserInfoHeadImgAdapter);
        //endregion
/*        contactSyncListener = new HuanXinIMHelper.DataSyncListener()
        {
            @Override
            public void onSyncComplete(boolean success)
            {
                //如果通信录同步成功了再变为聊天状态
                if (success)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            uiForFriendRelation();
                        }
                    });
                }
            }
        };*/
        //HuanXinIMHelper.getInstance().addSyncContactListener(contactSyncListener);
        Logger.t(TAG).d("checkWay--> " + getIntent().getStringExtra("checkWay"));
        switch (getIntent().getStringExtra("checkWay"))
        {
            case "Id":
                if (mPresenter != null)
                {
                    LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 0, null);
                    mPresenter.getUserInfoDetail(toCheckUserId, "Id");
                }
                break;
            case "UId":
                if (mPresenter != null)
                {
                    LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 0, null);
                    mPresenter.getUserInfoDetail(toCheckUserUid, "UId");
                }
                break;
            default:
                break;
        }
    }

    @OnClick({R.id.btn_apply_by_money, R.id.btn_apply_by_hello, R.id.btn_begin_chat, R.id.all_order_res
            , R.id.btn_accept, R.id.btn_addNote, R.id.btn_left, R.id.tv_report, R.id.btn_right, R.id.ll_want_to_go2,
            R.id.ll_want_to_go, R.id.last_chuxian, R.id.last_chuxian2, R.id.tv_my_booty_call, R.id.all_back})
    void viewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_apply_by_money://红包申请
                Intent intent = new Intent(mContext, CApplyGiftAct.class);
                intent.putExtra("toCheckUserUid", toCheckUserUid);
                intent.putExtra("toCheckUserIdH", toCheckUserIdH);
                intent.putExtra("toCheckUserNicName", toCheckUserNicName);
                mContext.startActivityForResult(intent, EamConstant.EAM_GIFT_REQUEST_CODE);
                break;
            case R.id.btn_apply_by_hello://普通申请
                applyAsFriend(toCheckUserUid, toCheckUserIdH, "-1");
                break;
            case R.id.btn_begin_chat: //开始聊天
                Intent intentChat = new Intent(mContext, CChatActivity.class);
                if (!TextUtils.isEmpty(currentUserAvRoomId))//说明这个用户详情页是从直播间打开的--wb
                {
                    intentChat.putExtra("isLiveOpen", "true");
                }
                intentChat.putExtra("userId", toCheckUserIdH);
                startActivity(intentChat);
                break;
            case R.id.all_order_res://点击餐厅链接，两个按钮同一个地方？
            case R.id.at_where_now:
                UsersBean usersBean = (UsersBean) gotInfoMap.get("userBean");
                ArrayMap<String, String> mapInfo = (ArrayMap<String, String>) gotInfoMap.get("orderBean");
                Intent intentShowDiner = new Intent(mContext, CDinnarShowOnResAct.class);
                intentShowDiner.putExtra("tableId", mapInfo.get("sits"));
                intentShowDiner.putExtra("headImg", usersBean.getUphUrl());
                intentShowDiner.putExtra("resId", mapInfo.get("rId"));
                intentShowDiner.putExtra("floorNum", mapInfo.get("floor"));
                intentShowDiner.putExtra("orderTime", mapInfo.get("orderTime"));
                intentShowDiner.putExtra("resName", mapInfo.get("rName"));
                startActivity(intentShowDiner);
                break;
            case R.id.btn_accept://接受别人的申请（分普通申请，和见面礼）
                String applyType = (String) allAcceptButton.getTag();
                if (applyType.equals("common"))
                {
                    acceptAsFriend(toCheckUserUid, toCheckUserIdH, "", "", "");
                } else
                {
                    try
                    {
                        CGiftBean giftBean = (CGiftBean) gotInfoMap.get("welgiftBean");
                        acceptAsFriend(toCheckUserUid, toCheckUserIdH, giftBean.getAmount(), giftBean.getStreamId(), giftBean.getPayType());
                    } catch (Exception e)
                    {
                        Logger.t(TAG).d(e.getMessage());
                    }
                }
                break;
            case R.id.btn_addNote://添加备注按钮
                new Change2thNicNameDialog(mContext)
                        .build()
                        .setHintReMark(TextUtils.isEmpty(reMark) ? "请输入备注名" : reMark)
                        .setPositiveButton("", new Change2thNicNameDialog.OnDialogWith2thNicNameListener()
                        {
                            @Override
                            public void onPositiveBtnClick(View view, EditText editText)
                            {
                                String reMark = editText.getText().toString();
                                if (reMark != null && mPresenter != null)
                                {
                                    if (!TextUtils.isEmpty(reMark))
                                    {
                                        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                                        Matcher m = p.matcher(reMark);
                                        mPresenter.editReMark(m.replaceAll(""), toCheckUserUid);
                                    } else
                                    {
                                        ToastUtils.showShort("请输入备注内容");
                                    }
                                }
                            }

                            @Override
                            public void onNavigateBtnClick(View view, EditText editText)
                            {

                            }
                        })
                        .setNegativeButton("", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                            }
                        }).show();
                break;
            case R.id.btn_left:
                finish();
                break;
            case R.id.all_back:
                finish();
                break;
            case R.id.btn_right:
                switch (String.valueOf(tvRight.getTag()))
                {
                    case "living"://直播中
                        if (ViewShareHelper.liveMySelfRole == LiveRecord.ROOM_MODE_HOST)
                        {
                            ToastUtils.showShort("您当前正在直播，无法查看他人直播");
                            break;
                        }
                        if (CommonUtils.isInLiveRoom)
                        {
                            ToastUtils.showShort("您当前正在直播间中，请先退出！");
                            return;
                        }
                        CommonUtils.startLiveProxyAct(mContext, LiveRecord.ROOM_MODE_MEMBER, "", "", "", toCheckUserAvRoomId, null, EamCode4Result.reqNullCode);
                        break;
                    case "shutUp"://禁言中
                        if (isGhostUser)//如果是假用户
                        {
                            handleGhostUserShutUp();
                            break;
                        }
                        if (mPresenter != null)
                        {
                            mPresenter.setUserShutUpNo(currentUserAvRoomId, toCheckUserUid);
                        }
                        break;
                    case "speak"://未禁言中
                        if (isGhostUser)//如果是假用户
                        {
                            handleGhostUserShutUp();
                            break;
                        }
                        if (mPresenter != null)
                        {
                            mPresenter.setUserShutUpYes(currentUserAvRoomId, toCheckUserUid);
                        }
                        break;
                    default:
                        break;
                }
                break;
            case R.id.tv_report://举报
                Intent reportIntent = new Intent(mContext, ReportFoulsUserAct.class);
                reportIntent.putExtra("luId", toCheckUserUid);
                startActivity(reportIntent);
                break;
            case R.id.ll_want_to_go://最近想去点击
            case R.id.ll_want_to_go2:
                UsersBean uBean = (UsersBean) gotInfoMap.get("userBean");
                Logger.t(TAG).d(uBean.getWhitherId() + "," + uBean.getWhitherName());
                if (!TextUtils.isEmpty(uBean.getWhitherName()))
                {
                    SharePreUtils.setToOrderMeal(mContext, "noDate");
                    Intent intent1 = new Intent(mContext, DOrderMealDetailAct.class);
                    intent1.putExtra("restId", uBean.getWhitherId());
                    startActivity(intent1);
                }
                break;
            case R.id.last_chuxian://上次出现过点击
            case R.id.last_chuxian2:
                UsersBean bean = (UsersBean) gotInfoMap.get("userBean");
                if (!TextUtils.isEmpty(bean.getrPreName()))
                {
                    SharePreUtils.setToOrderMeal(mContext, "noDate");
                    Intent intent2 = new Intent(mContext, DOrderMealDetailAct.class);
                    intent2.putExtra("restId", bean.getrPreId());
                    startActivity(intent2);
                }
                break;
            case R.id.tv_my_booty_call://约会
                UsersBean usersInfor = (UsersBean) gotInfoMap.get("userBean");
                Intent bootyCallIntent = new Intent(mContext, LiveShowBootyCallAct.class);
                bootyCallIntent.putExtra("roomId", usersInfor.getId());
                bootyCallIntent.putExtra("hostUId", usersInfor.getuId());
                startActivity(bootyCallIntent);
                break;
            default:
                break;
        }
    }

    /**
     * 获取用户详情后设置UI
     *
     * @param infoMap
     */
    private void setUiContent(Map<String, Object> infoMap)
    {
        this.gotInfoMap = infoMap;
        usersBean = (UsersBean) infoMap.get("userBean");
        String stat = (String) infoMap.get("stat");
        Map<String, String> mapInfo = (ArrayMap<String, String>) infoMap.get("orderBean");
        //CGiftBean giftBean= (CGiftBean) infoMap.get("welgiftBean");
        levelView.setLevel(usersBean.getLevel(), LevelView.USER);
        if (TextUtils.equals(usersBean.getSex(), "女"))
        {
            tvAge.setBackgroundResource(R.drawable.shape_round_2corner_15_mc5);
            tvAge.setText(String.format("%s %s", "{eam-e94f}", usersBean.getAge()));
        } else
        {
            tvAge.setBackgroundResource(R.drawable.shape_round_2corner_15_mc7);
            tvAge.setText(String.format("%s %s", "{eam-e950}", usersBean.getAge()));
        }
        toCheckUserNicName = usersBean.getNicName();
        toCheckUserIdH = usersBean.getImuId();
        //当使用腾讯id查询的时候，应该设置一下Uid
        toCheckUserUid = usersBean.getuId();
        toCheckUserAvRoomId = usersBean.getId();
        isGhostUser = "0".equals(usersBean.getGhost()) ? true : false;
        //如果是从直播页面进入的
        if (!TextUtils.isEmpty(currentUserAvRoomId) && !TextUtils.isEmpty(toCheckUserUid))
        {
            if (mPresenter != null)
                mPresenter.checkUserRole(currentUserAvRoomId, SharePreUtils.getUId(mContext), toCheckUserUid);
        } else
        {
            if (usersBean.getLiving().equals("1"))
            {
                tvRight.setText("{eam-s-play-video} 直播中");
                tvRight.setTag("living");
                uInfoLine.setVisibility(View.VISIBLE);
                tvReport.setVisibility(View.VISIBLE);
            } else
            {
                uInfoLine.setVisibility(View.GONE);
                tvRight.setText("");
                tvRight.setTag("");
            }
        }
        Logger.t(TAG).d("好友关系》 " + stat);
        allStrangerShow.setVisibility(View.GONE);
        allApplyFriend.setVisibility(View.GONE);
        allAcceptButton.setVisibility(View.GONE);
        allChatButton.setVisibility(View.GONE);
        allFriendShow.setVisibility(View.GONE);
        switch (stat)
        {
            case "0"://互相未发送任何请求:
                allStrangerShow.setVisibility(View.VISIBLE);
                allApplyFriend.setVisibility(View.VISIBLE);
                break;
            case "1"://好友关系
                btnAddNote.setVisibility(View.VISIBLE);
                allFriendShow.setVisibility(View.VISIBLE);
                allChatButton.setVisibility(View.VISIBLE);
                break;
            case "2"://我向对方发送了普通申请
                allStrangerShow.setVisibility(View.VISIBLE);
                allApplyFriend.setVisibility(View.VISIBLE);
                btnApplyByHello.setText("等待验证");
                btnApplyByHello.setEnabled(false);
                break;
            case "3"://我向对方发送了见面礼
                allStrangerShow.setVisibility(View.VISIBLE);
                allApplyFriend.setVisibility(View.VISIBLE);
                btnApplyByMoney.setText("等待收礼");
                btnApplyByMoney.setEnabled(false);
                break;
            case "4"://我向对方发送了普通申请和见面礼
                allStrangerShow.setVisibility(View.VISIBLE);
                allApplyFriend.setVisibility(View.VISIBLE);
                btnApplyByMoney.setText("等待收礼");
                btnApplyByMoney.setEnabled(false);
                break;
            case "5"://对方向我发送了普通申请
                uiForWaitAcceptRelation("common");
                break;
            case "6"://对方向我发送了见面礼申请
                uiForWaitAcceptRelation("gift");
                break;
            case "7"://对方向我发送了见面礼申请和普通申请
                uiForWaitAcceptRelation("commonAndGift");
                break;
            default:
                break;
        }

        //如果是自己则下面的聊天送礼按钮隐藏
        if (SharePreUtils.getUId(mContext).equals(usersBean.getuId()))
        {
            allFriendShow.setVisibility(View.VISIBLE);
            allStrangerShow.setVisibility(View.GONE);
            btnAddNote.setVisibility(View.GONE);
            allApplyFriend.setVisibility(View.GONE);
            allChatButton.setVisibility(View.GONE);
            tvReport.setVisibility(View.GONE);
        }
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(usersBean.getUphUrl())
                .placeholder(R.drawable.userhead)
                .error(R.drawable.userhead)
                .into(ivHead);
        if (TextUtils.isEmpty(usersBean.getRemark()))
        {
            tvTitleName.setText(usersBean.getNicName());
        } else
        {
            reMark = usersBean.getRemark();
            tvTitleName.setText(usersBean.getRemark());
        }
        tvTitleName.setSelected(true);

        tvGenderTwo.setText(usersBean.getSex());
        llAtWhereNow.setVisibility(View.GONE);

        if (mapInfo.get("rName") == null)//是否展示订餐信息
        {
            llAllMRes.setVisibility(View.GONE);
            atWhereNow.setText("");
        } else
        {
            tvMRes.setText(mapInfo.get("rName"));
            String text = "{eam-e94e} " + mapInfo.get("rName");
            atWhereNow.setText(text);
        }
        Logger.t(TAG).d("usersBean.getrPreName()" + usersBean.getrPreName());
        if (TextUtils.isEmpty(usersBean.getrPreName()))
        {
            lastChuXian.setText("没有定过餐哦~");
            lastChuXian2.setText("没有定过餐哦~");
            lastChuXian.setTextColor(ContextCompat.getColor(mContext, R.color.FC3));
            lastChuXian2.setTextColor(ContextCompat.getColor(mContext, R.color.FC3));
        } else
        {
            lastChuXian.setText(usersBean.getrPreName());
            lastChuXian2.setText(usersBean.getrPreName());
            lastChuXian.setTextColor(ContextCompat.getColor(mContext, R.color.MC7));
            lastChuXian2.setTextColor(ContextCompat.getColor(mContext, R.color.MC7));
        }

        if (TextUtils.isEmpty(usersBean.getWhitherName()))
        {
            tvWantToGo.setText("太忙了，还没有选餐厅");
            tvWantToGo2.setText("太忙了，还没有选餐厅");
            tvWantToGoTime.setVisibility(View.GONE);
            tvWantToGoTime2.setVisibility(View.GONE);
            tvWantToGo.setTextColor(ContextCompat.getColor(mContext, R.color.FC3));
            tvWantToGo2.setTextColor(ContextCompat.getColor(mContext, R.color.FC3));
        } else
        {
            tvWantToGo.setText(usersBean.getWhitherName());
            tvWantToGo2.setText(usersBean.getWhitherName());
            tvWantToGoTime.setText(usersBean.getWhitherTime());
            tvWantToGoTime2.setText(usersBean.getWhitherTime());
            tvWantToGo.setTextColor(ContextCompat.getColor(mContext, R.color.MC7));
            tvWantToGo2.setTextColor(ContextCompat.getColor(mContext, R.color.MC7));
            tvWantToGoTime.setTextColor(ContextCompat.getColor(mContext, R.color.MC7));
            tvWantToGoTime2.setTextColor(ContextCompat.getColor(mContext, R.color.MC7));
        }

        tvGanQing.setText(usersBean.getEmState());
        tvGanQing2.setText(usersBean.getEmState());
        tvHeight.setText(usersBean.getHeight());
        tvHeight2.setText(usersBean.getHeight());
        tvXingZuo.setText(usersBean.getConstellation());
        tvXingZuo2.setText(usersBean.getConstellation());
        tvCity.setText(usersBean.getCity());
        if (TextUtils.isEmpty(usersBean.getRemark()))
        {
            tvNickName.setText(usersBean.getNicName());
        } else
        {
            tvNickName.setText(usersBean.getRemark() + "(" + usersBean.getNicName() + ")");
        }
        tvBirthday.setText(usersBean.getBirth());
        tvXueLi.setText(usersBean.getEducation());
        tvZhiYe.setText(usersBean.getOccupation());
        tvZhiYe.setText(usersBean.getOccupation());
        tvSign.setText(usersBean.getSignature());
        tvSign2.setText(usersBean.getSignature());
        tvShouRu.setText(usersBean.getIncome());
        userId.setText(usersBean.getId());
        tvFanNumCount.setText(usersBean.getMealTotal());
        if ("0".equals(EamApplication.getInstance().isShowReceive))
        {
            myBootyCallTv.setVisibility(View.VISIBLE);
            myBootyCallTv.setText(SharePreUtils.getUId(mContext).equals(usersBean.getuId()) ? "我的约会" : "Ta的约会");
        } else
        {
            myBootyCallTv.setVisibility(View.GONE);
        }
        //修改查看资料签名显示
        List<String> list = CommonUtils.strWithSeparatorToList(usersBean.getuLab(), CommonUtils.SEPARATOR);
        if (!list.isEmpty())
        {
            userLabelAdapter = new UserLabelAdapter(this, list);
            userTagLabel.setAdapter(userLabelAdapter);
            userTagLabel.post(new Runnable()
            {
                @Override
                public void run()
                {
                    AutoLinearLayout.LayoutParams params = new AutoLinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    llUserTag.setLayoutParams(params);
                }
            });
        } else
        {
            userTagLabel.post(new Runnable()
            {
                @Override
                public void run()
                {
                    AutoLinearLayout.LayoutParams params = new AutoLinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 146);
                    llUserTag.setLayoutParams(params);
                }
            });
        }
        //设置相册
        userImgs.clear();
        userImgs.addAll(usersBean.getImgUrls());
        mCUserInfoHeadImgAdapter.notifyDataSetChanged();
//        if (pDialog != null && pDialog.isShowing())
//            pDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case EamConstant.EAM_GIFT_REQUEST_CODE:
                switch (resultCode)
                {
                    case 0://返回
                        break;
                    case 1://成功
                        appleSendSuccess("1");
                        break;
                    case 2://发送失败
                        ToastUtils.showShort("见面礼发送失败");
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    /**
     * 对方向自己发出了好友申请时的界面
     *
     * @param applyType 申请类型
     */
    private void uiForWaitAcceptRelation(String applyType)
    {
        allStrangerShow.setVisibility(View.VISIBLE);
        allAcceptButton.setVisibility(View.VISIBLE);
        allApplyFriend.setVisibility(View.GONE);
        allFriendShow.setVisibility(View.GONE);
        allChatButton.setVisibility(View.GONE);
        if (applyType.equals("common"))
        {
            allAcceptButton.setText("通过验证");
            allAcceptButton.setTag("common");
        } else if (applyType.equals("gift"))
        {
            allAcceptButton.setText("领取见面礼");
            allAcceptButton.setTag("gift");
        } else
        {
            allAcceptButton.setText("领取见面礼");
            allAcceptButton.setTag("commonAndGift");
        }
    }

    private void handleGhostUserShutUp()
    {
        if (tvRight.getTag().equals("speak"))
        {
            tvRight.setText("{eam-e9b0} 解除禁言");
            tvRight.setTag("shutUp");
            uInfoLine.setVisibility(View.VISIBLE);
            tvReport.setVisibility(View.VISIBLE);
        } else if (tvRight.getTag().equals("shutUp"))
        {
            tvRight.setText("{eam-e9b1} 禁言");
            tvRight.setTag("speak");
            uInfoLine.setVisibility(View.VISIBLE);
            tvReport.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 接受好友申请
     *
     * @param hToAddUsername
     */
    private void acceptAsFriend(final String toAddUserUid, final String hToAddUsername,
                                final String amount, final String streamId, final String payType)
    {
        if (mPresenter != null)
        {
            //先调用后台接口，再调用环信接口
            mPresenter.saveContactStatusToServer(toAddUserUid, amount, streamId, payType);
        }
    }

    /**
     * 申请好友
     *
     * @param hToAddUsername
     * @param reason
     */
    private void applyAsFriend(final String toAddUserUid, final String hToAddUsername, final String reason)
    {
        new Thread()
        {
            @Override
            public void run()
            {
                super.run();
                try
                {
                    EMClient.getInstance().contactManager().addContact(hToAddUsername, reason);
                    if (mPresenter != null)
                    {
                        mPresenter.applyFriendByHello(toAddUserUid);
                    }
                    //region 待用
/*            EMClient.getInstance().contactManager().setContactListener(new EMContactListener()
            {

                @Override
                public void onContactAgreed(String username)
                {
                    //好友请求被同意
                    if (username.equals(hToAddUsername))
                    {

                    }
                }

                @Override
                public void onContactRefused(String username)
                {
                    //好友请求被拒绝
                }

                @Override
                public void onContactInvited(String username, String reason)
                {
                    //收到好友邀请
                }

                @Override
                public void onContactDeleted(String username)
                {
                    //被删除时回调此方法
                }


                @Override
                public void onContactAdded(String username)
                {
                    //增加了联系人时回调此方法
                }
            });*/
                    //endregion

                } catch (HyphenateException e)
                {
                    //ToastUtils.showShort(mContext,"环信调用失败+"+e.getMessage());
                    Logger.t(TAG).d(e.getMessage());
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 申请发送成功
     *
     * @param applyType 0 普通申请；1 红包申请
     */
    private void appleSendSuccess(String applyType)
    {
//        StatService.onEvent(mContext, "apply_friend", getString(R.string.baidu_society), 1);
        //普通申请
        if (applyType.equals("0"))
        {
            ToastUtils.showShort("申请发送成功，等待验证");
            btnApplyByHello.setText("等待验证");
            btnApplyByHello.setEnabled(false);
        } else
        {
            ToastUtils.showShort("申请发送成功，等待接收");
            btnApplyByMoney.setText("等待接收");
            btnApplyByMoney.setEnabled(false);
        }
    }

    /**
     * 好友关系时显示的界面
     */
    private void uiForFriendRelation()
    {
        allStrangerShow.setVisibility(View.GONE);
        allApplyFriend.setVisibility(View.GONE);
        allAcceptButton.setVisibility(View.GONE);
        allFriendShow.setVisibility(View.VISIBLE);
        allChatButton.setVisibility(View.VISIBLE);
        //ToastUtils.showShort(mContext, "您两已经是好友了，可以直接聊天");
    }

    /**
     * 陌生人时的显示界面
     */
    private void uiForStrangerRelation()
    {
        allStrangerShow.setVisibility(View.VISIBLE);
        allApplyFriend.setVisibility(View.VISIBLE);
        allFriendShow.setVisibility(View.GONE);
        allChatButton.setVisibility(View.GONE);
        allAcceptButton.setVisibility(View.GONE);
        btnApplyByHello.setText("好友申请");
        btnApplyByHello.setEnabled(true);
        btnApplyByMoney.setText("见面礼");
        btnApplyByMoney.setEnabled(true);
    }


    @Override
    public void requestNetError(Call call, Exception e, final String exceptSource)
    {
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 1, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (exceptSource.contains(NetInterfaceConstant.UserC_qOthersInfoById))
                {
                    mPresenter.getUserInfoDetail(toCheckUserIdT, "Id");
                } else if (exceptSource.contains(NetInterfaceConstant.UserC_qOthersInfo))
                {
                    mPresenter.getUserInfoDetail(toCheckUserUid, "UId");
                }
            }
        });
        NetHelper.handleNetError(mContext, null, exceptSource, e);
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        Logger.t(TAG).d("错误码为：%s", code);
        switch (interfaceName)
        {
            case NetInterfaceConstant.LiveC_userRole:
                break;
            case NetInterfaceConstant.LiveC_muteStatus:
                break;
            case NetInterfaceConstant.NeighborC_friend:
                if ("USR_IS_FRIEND".equals(code))
                {
                    uiForFriendRelation();
                }
                break;
            default:
                break;
        }
        ToastUtils.showShort(ErrorCodeTable.parseErrorCode(code));//如果有非retrofit的请求需要这个，没有这不需要
    }

    @Override
    public void editReMarkCallback(String input, String responseStr)
    {
        try
        {
            Logger.t(TAG).d("返回结果" + responseStr);
            UsersBean usersBean = (UsersBean) gotInfoMap.get("userBean");
            HuanXinIMHelper.getInstance().updateContact(usersBean, input);
            reMark = input;
            if (mPresenter != null)
                mPresenter.getUserInfoDetail(toCheckUserUid, "UId");
            ToastUtils.showShort("修改成功");

            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        } finally
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    @Override
    public void getUserInfoCallback(Map<String, Object> response)
    {
        setUiContent(response);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
    }

    @Override
    public void saveContactToServerCallback(String status)
    {
        new Thread(()->
        {
                try//参数为要添加的好友的username，要是环信添加好友失败。最好在聊天时候判断一下
                {
                    EMClient.getInstance().contactManager().acceptInvitation(toCheckUserIdH);
                    //添加成功，发送一条添加成功消息
                    EMMessage message = EMMessage.createTxtSendMessage(SharePreUtils.getNicName(mContext)
                            + "通过了你的好友请求，现在可以发起聊天了", toCheckUserIdH);
                    message.setAttribute("avatar", SharePreUtils.getHeadImg(mContext));
                    message.setAttribute("uid", SharePreUtils.getUId(mContext));
                    message.setAttribute("nickname", SharePreUtils.getNicName(mContext));
                    message.setAttribute("level", SharePreUtils.getLevel(mContext));
                    message.setAttribute("sex", SharePreUtils.getSex(mContext));
                    message.setAttribute("age", SharePreUtils.getAge(mContext));
                    EMClient.getInstance().chatManager().sendMessage(message);
                    // ZDW  申请通过时 把单个用户信息更新保存本地数据库
                    Logger.t(TAG).d("userBean--> " + usersBean.toString() + " , " + usersBean.getRemark());
                    HuanXinIMHelper.getInstance().updateContact(usersBean, usersBean.getRemark());
                } catch (HyphenateException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
        }).start();
    }

    @Override
    public void applyFriendByHelloCallback(String response)
    {
        appleSendSuccess("0");
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    @Override
    public void getUserShutUpStateCallback(String bodyStr)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(bodyStr);
            String isMute = jsonObject.getString("isMute");
            Logger.t(TAG).d("isMute--> " + isMute);
            if (isMute.equals("1"))
            {
                tvRight.setText("{eam-e9b0} 解除禁言");
                tvRight.setTag("shutUp");
                uInfoLine.setVisibility(View.VISIBLE);
                tvReport.setVisibility(View.VISIBLE);
            } else
            {
                tvRight.setText("{eam-e9b1} 禁言");
                tvRight.setTag("speak");
                uInfoLine.setVisibility(View.VISIBLE);
                tvReport.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("获取用户禁言状态解析异常--> " + e.getMessage());
        }
    }

    @Override
    public void setUserShutUpYesCallback(String bodyStr)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(bodyStr);
            String userRole = jsonObject.getString("userRole");
            tvRight.setText("{eam-e9b0} 解除禁言");
            tvRight.setTag("shutUp");
            uInfoLine.setVisibility(View.VISIBLE);
            tvReport.setVisibility(View.VISIBLE);
            String action = "";
            switch (userRole)
            {
                case "1"://主播禁言
                    action = EamConstant.EAM_BRD_ACTION_SHUNTUP_HOST;
                    break;
                case "2":
                    action = EamConstant.EAM_BRD_ACTION_SHUTEUP_ADMIN;
                    break;
                default:
                    break;
            }
            sendBroadcast(action);
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("设置用户禁言异常：" + e.getMessage());
        }
    }

    @Override
    public void setUserShutUpNoCallback(String bodyStr)
    {
        try
        {
            tvRight.setText("{eam-e9b1} 禁言");
            tvRight.setTag("speak");
            uInfoLine.setVisibility(View.VISIBLE);
            tvReport.setVisibility(View.VISIBLE);
            JSONObject jsonObject = new JSONObject(bodyStr);
            String userRole = jsonObject.getString("userRole");
            String action = "";
            switch (userRole)
            {
                case "1"://主播解除禁言
                    action = EamConstant.EAM_BRD_ACTION_SHUNTUP_OFF_HOST;
                    break;
                case "2":
                    action = EamConstant.EAM_BRD_ACTION_SHUNTUP_OFF_ADMIN;
                    break;
                default:
                    break;
            }
            sendBroadcast(action);
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("解除用户禁言异常：" + e.getMessage());
        }
    }

    private void sendBroadcast(String action)
    {
        HashMap<String, String> paraMap = new HashMap<>();
        paraMap.put("toCheckUserUid", toCheckUserUid);
        paraMap.put("toCheckUserId", toCheckUserId);
        paraMap.put("toCheckUserNicName", toCheckUserNicName);
        paraMap.put("toCheckUserIdT", toCheckUserIdT);
        paraMap.put("currentUserAvRoomId", currentUserAvRoomId);
        paraMap.put("chatRoomId", chatRoomId);
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("param", paraMap);
        sendBroadcast(intent);
    }

    @Override
    public void checkUserRoleCallback(String myUid, String myRole, String checkedUid, String checkedRole)
    {
        Logger.t(TAG).d("myUid--> " + myUid + " , myRole-->" + myRole + " , checkedUid--> " + checkedUid + " , checkedRole--> " + checkedRole);
        if (checkedUid.equals(myUid))//自己查看自己
        {
            switch (checkedRole)
            {
                case "1":
                    tvRight.setText("{eam-s-play-video} 直播中");
                    tvRight.setTag("");
                    uInfoLine.setVisibility(View.GONE);
                    tvReport.setVisibility(View.GONE);
                    break;
                case "2":
                case "0":
                    tvRight.setText("");
                    tvRight.setTag("");
                    uInfoLine.setVisibility(View.GONE);
                    tvReport.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        } else//查看的是其他用户
        {
            switch (checkedRole)
            {
                case "1"://查看的是主播
                    switch (myRole)
                    {
                        case "2":
                        case "0":
                            tvRight.setText("{eam-s-play-video} 直播中");
                            tvRight.setTag("living");
                            uInfoLine.setVisibility(View.VISIBLE);
                            tvReport.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                    break;
                case "2"://查看的是管理员
                    switch (myRole)
                    {
                        case "1":
                            if (mPresenter != null)
                            {
                                mPresenter.checkUserShutUpState(currentUserAvRoomId, toCheckUserUid);
                            }
                            break;
                        case "2":
                        case "0":
                            tvRight.setText("");
                            tvRight.setTag("");
                            uInfoLine.setVisibility(View.GONE);
                            tvReport.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                    break;
                case "0": // 查看的是普通用户
                    switch (myRole)
                    {
                        case "1":
                        case "2":
                            if (mPresenter != null)
                            {
                                mPresenter.checkUserShutUpState(currentUserAvRoomId, toCheckUserUid);
                            }
                            break;
                        case "0":
                            tvRight.setText("");
                            tvRight.setTag("");
                            uInfoLine.setVisibility(View.GONE);
                            tvReport.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }
}

package com.echoesnet.eatandmeet.activities.liveplay.managers;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.activities.live.LChoseConnectMemberAct;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.activities.liveplay.Presenter.LivePresenter;
import com.echoesnet.eatandmeet.activities.liveplay.View.LiveBaseAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.AudienceBean;
import com.echoesnet.eatandmeet.models.bean.GameInviteBean;
import com.echoesnet.eatandmeet.models.bean.LiveEnterRoomBean;
import com.echoesnet.eatandmeet.models.bean.RefreshLiveMsgBean;
import com.echoesnet.eatandmeet.models.bean.ShareToFaceBean;
import com.echoesnet.eatandmeet.models.bean.StartGameBean;
import com.echoesnet.eatandmeet.models.bean.TXIMChatEntity;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.models.datamodel.LiveMsgType;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.FrameAnimator;
import com.echoesnet.eatandmeet.utils.IMUtils.TXConstants;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.LiveNewMsgDialog;
import com.echoesnet.eatandmeet.views.adapters.AudienceListAdapter;
import com.echoesnet.eatandmeet.views.adapters.ChatMsgListAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.LiveEndDetailDialog;
import com.echoesnet.eatandmeet.views.widgets.LiveGameInviteDialog;
import com.echoesnet.eatandmeet.views.widgets.LiveMsgDialog.LiveMsgDialog;
import com.echoesnet.eatandmeet.views.widgets.SharePopopWindow.SharePopWindow;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.tencent.qq.QQClientNotExistException;
import io.reactivex.annotations.Nullable;

import static com.tencent.qalsdk.service.QalService.context;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2017/6/6
 * @description 此类作为直播间公共视图的帮助类
 */
public class ViewShareHelper
{
    private static final String TAG = ViewShareHelper.class.getSimpleName();
    private static final int SYNC_AUDI_THRESHOLD = 10;

    private static ViewShareHelper instance;
    private LivePresenter livePresenter;
    public static int liveMySelfRole = LiveRecord.ROOM_MODE_MEMBER;

    private ChatMsgListAdapter chatMsgListAdapter;
    private LinearLayoutManager mChatRoomLinearLayoutManager;
    private SharePopWindow sharePopWindow;
    private AudienceListAdapter audienceAdapter;
    private LinearLayoutManager mAudienceLinearLayoutManager;
    private BarrageManager barrageManager; //弹幕管理器
    private OutAnimManager outAnimManager;//进场特效
    //  private GiftUI giftUI;//礼物
    private int audienceCount;
    private int audienceChangeCounter;
    private LiveEndDetailDialog mLiveEndDetailDialog;
    private TextView tvAudiencesCount;
    private CustomAlertDialog backDialog;
    private LiveBaseAct mAct;
    //约吃饭图标红点
    private View bootyRed;
    private ImageView imgGame;
    private FrameAnimator frameAnimator;
    private BridgeWebView mGameWeb;
    private boolean gameStart = false;
    private GameExitListener gameExitListener;
    private LiveGameInviteDialog liveGameInviteDialog;//直播间小游戏邀请dialog

    private ViewShareHelper(LiveBaseAct act, final LivePresenter livePresenter)
    {
        this.livePresenter = livePresenter;
        this.mAct = act;
        outAnimManager = new OutAnimManager(mAct, mAct.getWindow(), R.id.room_welcome_welcome);
        barrageManager = new BarrageManager(mAct, mAct.getWindow(), R.id.room_barrage_container);
        bootyRed = mAct.getWindow().findViewById(R.id.img_booty_red);
//        giftUI = (GiftUI) mAct.getWindow().findViewById(R.id.room_amin_giftui);
//        giftUI.setOnClick(new GiftUI.OnViewClick()
//        {
//            @Override
//            public void onClick(SlidGift.GiftRecord record)
//            {
//                if (EamApplication.getInstance().controlUInfo.size() == 2)
//                {
//                    if (EamApplication.getInstance().controlUInfo.get(CNewUserInfoAct.class.getSimpleName()) != null)
//                    {
//                        EamApplication.getInstance().controlUInfo.get(CNewUserInfoAct.class.getSimpleName()).finish();
//                        EamApplication.getInstance().controlUInfo.clear();
//                    }
//                }
//                Intent intent = new Intent(mAct, CNewUserInfoAct.class);
//                intent.putExtra("toId", record.uid);
//                intent.putExtra("toUId", record.uid);
//                intent.putExtra("currentRoomId", livePresenter.getmRecord().getRoomId());
//                mAct.startActivity(intent);
//            }
//        });
    }

    public static ViewShareHelper getInstance(LiveBaseAct act, LivePresenter livePresenter)
    {
        if (instance == null)
            instance = new ViewShareHelper(act, livePresenter);

        return instance;
    }

    public static void destroyInstance()
    {
        instance = null;
    }


    //开始约吃饭图标动画
    public void startBootyCallMsgAni()
    {
        bootyRed.setVisibility(View.VISIBLE);
    }

    //停止约吃饭图标动画
    public void stopBootyCallMsgAni()
    {
        bootyRed.setVisibility(View.GONE);
    }

    /**
     * 展示弹幕
     *
     * @param map
     */
    public void shootBarrage(Map<String, String> map)
    {
        String msg = "";
        try
        {
            msg = new JSONObject(map.get(EamConstant.EAM_LIVE_ATTR_PARAM)).getString("msg");
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        // msg = map.get(EamConstant.EAM_LIVE_ATTR_PARAM);
        barrageManager.addBarrage(map.get(EamConstant.EAM_LIVE_ATTR_HEADIMAGE)
                , map.get(EamConstant.EAM_LIVE_ATTR_LEVEL)
                , map.get(EamConstant.EAM_LIVE_ATTR_NICKNAME)
                , msg, map.get(EamConstant.EAM_LIVE_ATTR_VUSER));
    }

//    public void putGift(SlidGift.GiftRecord record)
//    {
//        giftUI.put(record);
//    }
//
//    public int getGiftCount(String gid)
//    {
//        return giftUI.getAnimIngGiftCount(gid);
//    }

    /**
     * 初始化直播间聊天列表并配置
     *
     * @param act
     * @param chatListView
     */
    public void setupChatListView(final Activity act, final ListView chatListView)
    {
        if (chatListView == null)
            return;

        mChatRoomLinearLayoutManager = new LinearLayoutManager(context);
        mChatRoomLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mChatRoomLinearLayoutManager.setStackFromEnd(true);
        chatMsgListAdapter = new ChatMsgListAdapter(act, chatListView);
        chatListView.setAdapter(chatMsgListAdapter);
        chatMsgListAdapter.setOnItemClickListener(new ChatMsgListAdapter.OnItemClickListener()
        {
            @Override
            public void onUserNickClick(View view, String source, TXIMChatEntity entity)
            {
                //点击消息发送人
                if (entity != null)
                {
                    Logger.t(TAG).d("onSenderClick--> " + entity.getId() + " , " + entity.getuId() + "," + livePresenter.getmRecord().getHxChatRoomId() + "," + livePresenter.getmRecord().getRoomId());
                    if (!TextUtils.isEmpty(entity.getId()))
                    {
                        if (EamApplication.getInstance().controlUInfo.size() == 2)
                        {
                            if (EamApplication.getInstance().controlUInfo.get(CNewUserInfoAct.class.getSimpleName()) != null)
                            {
                                EamApplication.getInstance().controlUInfo.get(CNewUserInfoAct.class.getSimpleName()).finish();
                                EamApplication.getInstance().controlUInfo.clear();
                            }
                        }
                        Intent intent = new Intent(act, CNewUserInfoAct.class);
                        intent.putExtra("toUId", entity.getuId());
                        intent.putExtra("toId", entity.getId());
                        intent.putExtra("chatRoomId", livePresenter.getmRecord().getHxChatRoomId());
                        intent.putExtra("currentRoomId", livePresenter.getmRecord().getRoomId());
                        act.startActivity(intent);
                    }
                }
            }

            @Override
            public void onImageClick(View view, String source, TXIMChatEntity entity)
            {
                if (entity != null)
                {
                    switch (source)
                    {
                        case "redPacket":
                            try
                            {
                                JSONObject jsonObject = new JSONObject(entity.getStreamId());
                                String streamId = jsonObject.getString("streamId");
                                livePresenter.getGroundRed(streamId, livePresenter.getmRecord().getRoomId(), entity.getId());
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                                Logger.t(TAG).d(e.getMessage());
                            }
                            break;
                    }
                }
            }
        });
//        mChatRoomListView.setLayoutManager(mChatRoomLinearLayoutManager);
        // 解决自增长，跟最大高度问题
        //布局为自适应高度，监听高度，到达最大高度-》取消；
        // item view ; 最小高度 22dp；写死的值;android:minHeight="22dp"22 * 8 = 176dp
//        chatListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
//        {
//            @Override
//            public void onGlobalLayout()
//            {
//                if (chatListView.getHeight() > CommonUtils.dp2px(act, 176))
//                {
//                    chatListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                    ViewGroup.LayoutParams lp = chatListView.getLayoutParams();
//                    lp.height = CommonUtils.dp2px(act, 150);
//                    chatListView.setLayoutParams(lp);
//                }
//                else
//                {
//                    //Logger.t(TAG).d("mChatRoomListView Changed ");
//                }
//            }
//        });
    }


    /**
     * 将聊天列表滚动到底部
     */
    public void scrollMsgLstToBottom()
    {
        if (mChatRoomLinearLayoutManager != null)
            mChatRoomLinearLayoutManager.scrollToPosition(chatMsgListAdapter.getCount() - 1);
    }

    public void notifyChatListDataSetChanged(TXIMChatEntity chatEntity)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("isAdd", false);
        map.put("chatEntity", chatEntity);
        chatMsgListAdapter.notifyDataChanged(map);
    }

    /**
     * 更新 message list 操作
     *
     * @param liveMsgBean 刷新直播消息列表参数bean {@link RefreshLiveMsgBean}
     */
    public void refreshText(final RefreshLiveMsgBean liveMsgBean)
    {
        if (ViewShareHelper.getInstance(mAct, livePresenter).isGameStart())
            return;
        chatMsgListAdapter.updateMsgList(liveMsgBean);
    }

    public void setupAudienceListView(final Activity act, RecyclerView rvAudienceAvatar, final String liveType)
    {
        if (rvAudienceAvatar == null)
            return;
        rvAudienceAvatar.setHasFixedSize(true);
        audienceAdapter = new AudienceListAdapter(act, livePresenter.arrAudiencesObj);
        rvAudienceAvatar.setAdapter(audienceAdapter);
        mAudienceLinearLayoutManager = new LinearLayoutManager(act, LinearLayout.HORIZONTAL, false);
        rvAudienceAvatar.setLayoutManager(mAudienceLinearLayoutManager);
        audienceAdapter.setOnItemClickListener(new AudienceListAdapter.OnItemClickListener()
        {
            @Override
            public void onAvatarClick(View view, AudienceBean entity)
            {
                Logger.t(TAG).d("onAvatarClick--> " + entity.getIdentifier() + " , " + entity.getUid() + "," + livePresenter.getmRecord().getHxChatRoomId() + "," + livePresenter.getmRecord().getRoomId());
                if (EamApplication.getInstance().controlUInfo.size() == 2)
                {
                    if (EamApplication.getInstance().controlUInfo.get(CNewUserInfoAct.class.getSimpleName()) != null)
                    {
                        EamApplication.getInstance().controlUInfo.get(CNewUserInfoAct.class.getSimpleName()).finish();
                        EamApplication.getInstance().controlUInfo.clear();
                    }
                }
                Intent intent = new Intent(act, CNewUserInfoAct.class);
                intent.putExtra("chatRoomId", livePresenter.getmRecord().getHxChatRoomId());
                intent.putExtra("toId", entity.getIdentifier());
                intent.putExtra("toUId", entity.getUid());
                intent.putExtra("currentRoomId", livePresenter.getmRecord().getRoomId());
                act.startActivity(intent);
            }
        });

        rvAudienceAvatar.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            //用来标记是否正在向最后一个滑动
            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    //获取最后一个完全显示的ItemPosition
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();

                    // 判断是否滚动到底部，并且是向右滚动
                    if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast)
                    {
                        //加载更多功能的代码
                        livePresenter.pullAudiences(livePresenter.arrAudiencesObj.size(), new HashMap<String, Object>());
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
                if (dx > 0)
                {
                    //大于0表示正在向右滚动
                    isSlidingToLast = true;
                }
                else
                {
                    //小于等于0表示停止或向左滚动
                    isSlidingToLast = false;
                }
            }
        });
    }

    public void notifyAudienceLstDataChanged()
    {
        if (audienceAdapter != null)
            audienceAdapter.notifyDataSetChanged();

    }

    public void fakeMemberQuit(String id, String name, String headImg)
    {
        Logger.t(TAG).d("有机器人用户退出直播间 " + id + "|" + name);
        AudienceBean userBean = new AudienceBean();
        userBean.setIdentifier(id);
        userBean.setIsGhost("1");
        userBean.setFaceUrl(headImg);
        setAudiencesCount(1, AudienceType.REMOVE);
        onAudienceLeaveRoom(userBean);
        notifyAudienceLstDataChanged();
    }

    /**
     * 有成员退出直播间
     *
     * @param id
     * @param name
     */
    public void memberQuit(String id, String name, String hxId)
    {
        Logger.t(TAG).d("有成员退出直播间> " + id + "|" + name);
        AudienceBean userBean = new AudienceBean();
        userBean.setIdentifier(id);
        userBean.setIsGhost("0");
        userBean.setImId(hxId);
        onAudienceLeaveRoom(userBean);
        notifyAudienceLstDataChanged();
        setAudiencesCount(1, AudienceType.REMOVE);
        // 取消离开发送消息
        // refreshText(TextUtils.isEmpty(name) ? id : name + " 离开了直播间", "", "消息");
    }

    /**
     * 有机器人成员加入
     *
     * @param id      腾讯id
     * @param name    昵称
     * @param headImg 头像url
     */
    public void fakeMemberJoin(String id, String name, String headImg, String userLevel)
    {
        Logger.t(TAG).d("假用户 " + "用户名》" + id + "昵称》" + name + "头像》" + headImg);
        notifyAudienceLstDataChanged();
        setAudiencesCount(1, AudienceType.ADD);
        RefreshLiveMsgBean refreshLiveMsgBean = new RefreshLiveMsgBean();
        refreshLiveMsgBean.setText("进场了， 欢迎~");
        refreshLiveMsgBean.setId(id);
        refreshLiveMsgBean.setName(name);
        refreshLiveMsgBean.setLevel(userLevel);
        refreshLiveMsgBean.setType(LiveMsgType.EnterRoom);
        refreshLiveMsgBean.setLiveLevelState("1");
        refreshText(refreshLiveMsgBean);
    }

    /**
     * 成员状态变更 有真实成员加入
     *
     * @param id      用户id 六位数字，别瞎写啊！--wb
     * @param name    昵称
     * @param headImg 头像url
     */
    public void memberJoin(final String id, final String name, String headImg, final String level, final String sign)
    {
        Logger.t(TAG).d("真用户用户 id》" + id + "昵称》" + name + "头像》" + headImg +
                " , " + livePresenter.getmRecord().getEnterRoom4EH().getNotification()
                + " , " + livePresenter.getmRecord().getEnterRoom4EH().getUser());
        //如果是主播不刷新观众列表及人数
        if (id.equals(livePresenter.getmRecord().getRoomId()))
        {
            return;
        }
        setAudiencesCount(1, AudienceType.ADD);
        notifyAudienceLstDataChanged();
        RefreshLiveMsgBean refreshLiveMsgBean = new RefreshLiveMsgBean();
        String htmlStr = "";
        if (outAnimManager.checkLvAnim(level, sign))
        {
            Logger.t(TAG).d("等级》" + level + "是否签约》" + sign);
            mAct.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    outAnimManager.addOutAnim(level, name);
                }
            });

            htmlStr = String.format("<font color=%s>%s</font>",
                    TXConstants.ENTER_ROOM_MSG_CONTENT_COLOR, "进入直播间~");

            refreshLiveMsgBean.setMsgL("一道金光闪过, ");
            refreshLiveMsgBean.setMsgR("进入直播间~");

            refreshLiveMsgBean.setType(LiveMsgType.DeclarationGoldenLight);
            refreshLiveMsgBean.setLiveLevelState("0");
        }
        else
        {
            htmlStr = "进场了， 欢迎~";
            refreshLiveMsgBean.setType(LiveMsgType.EnterRoom);
            refreshLiveMsgBean.setLevel(level);
            refreshLiveMsgBean.setLiveLevelState("1");
        }
        refreshLiveMsgBean.setName(name);
        refreshLiveMsgBean.setText(htmlStr);
        refreshLiveMsgBean.setId(id);
        refreshText(refreshLiveMsgBean);
    }

    private void setAudiencesCount(int number, AudienceType type)
    {
        Logger.t(TAG).d("人数》" + number + "类型>" + type);
        if (type == AudienceType.REFRESH)
        {
            audienceCount = number;
        }
        else if (type == AudienceType.ADD)
        {
            audienceCount = audienceCount + number;
            setRoomWatchedNumber(number);
        }
        else if (type == AudienceType.REMOVE)
        {
            audienceCount = audienceCount - number;
        }


        if (tvAudiencesCount != null)
            tvAudiencesCount.setText(String.format("%s人气", handleAudienceNum(audienceCount)));

        audienceChangeCounter++;

        if (audienceChangeCounter > SYNC_AUDI_THRESHOLD)
        {
            Map<String, Object> tran = new HashMap<>();
            tran.put("type", "syncAudienceNum");
            livePresenter.pullAudiences(0, tran);
        }
    }

    /**
     * 用户离开 -> remove arrlist -> remove map mark
     *
     * @param audi
     * @return 1|0 是否删除一个数据源
     */
    private void onAudienceLeaveRoom(AudienceBean audi)
    {
        if (livePresenter.arrAudiencesObj.contains(audi))
        {
            livePresenter.arrAudiencesObj.remove(audi);
        }
    }

    /**
     * 向后台拉取观众列表（分页拉取）和观众数目
     */
    public void insetAudiences(int num)
    {
        int temp = num;
        temp--;
        Logger.t(TAG).d("房间观众数》" + temp + "观众列表》" + livePresenter.arrAudiencesObj.toString());
        EamLogger.t(TAG).writeToDefaultFile("后台拉去观众数>>" + (temp == -1 ? 0 : temp));
        notifyAudienceLstDataChanged();
        setAudiencesCount(temp == -1 ? 0 : temp, AudienceType.REFRESH);
    }

    public TextView getTvAudiencesCount(TextView tvAudiencesCount)
    {
        return this.tvAudiencesCount = tvAudiencesCount;
    }

    public void setAudienceNum(int aCount)
    {
        //  Logger.t(TAG).d("后台返回观众数量:"+ aCount+"   本地累计观众数量:"+audienceCount);
        audienceChangeCounter = 0;
        audienceCount = aCount;
        tvAudiencesCount.setText(String.format("%s人气", handleAudienceNum(aCount)));
    }

    /**
     * 由于某种未知原因，列表数据和观众数可能对不上
     *
     * @return
     */
    private String handleAudienceNum(int inputAudienceCount)
    {
        if (inputAudienceCount < livePresenter.arrAudiencesObj.size() &&
                livePresenter.arrAudiencesObj.size() < livePresenter.AUDIENCE_MAX_COUNT)
        {
            Logger.t(TAG).d("由于某种未知原因，列表数据和观众数可能对不上");
            inputAudienceCount = livePresenter.arrAudiencesObj.size();
        }
        return inputAudienceCount + "";
    }

    /**
     * 统计房间中最高观看人数
     *
     * @param number
     */
    private void setRoomWatchedNumber(int number)
    {
        if (livePresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST)
            livePresenter.getmRecord().setWatchHighCount(livePresenter.getmRecord().getWatchHighCount() + number);
    }

    /**
     * 打开分享窗口
     *
     * @param act
     * @param enterRoomBean
     * @param mRecord
     * @param anchorView    窗口展示位置依赖的view
     * @param backView      背景view
     */
    public void popShareWin(final Activity act, LiveEnterRoomBean enterRoomBean, LiveRecord mRecord, View anchorView, View backView)
    {
        ShareToFaceBean bean = new ShareToFaceBean();
        bean.setShareTitleUrl(NetHelper.LIVE_SHARE_ADDRESS + mRecord.getRoomId());
        bean.setShareUrl(NetHelper.LIVE_SHARE_ADDRESS + mRecord.getRoomId());
        bean.setShareSiteUrl(NetHelper.LIVE_SHARE_ADDRESS + mRecord.getRoomId());
        bean.setRoomId(mRecord.getRoomId());
        bean.setShareTitle(enterRoomBean.getHn());
        bean.setShareAppImageUrl(NetHelper.LIVE_SHARE_PIC);
        bean.setShareType(Platform.SHARE_WEBPAGE);
        bean.setShareSite("看脸吃饭");
        bean.setOpenSouse("liveShare");// 传递房间参数到看脸好友界面
        bean.setRoomName(enterRoomBean.getNicName());
        bean.setShareImgUrl(enterRoomBean.getAnph());
        bean.setUid(enterRoomBean.getuId());
        bean.setShareListener(new PlatformActionListener()
        {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap)
            {
                act.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ToastUtils.showShort("分享成功");
                        NetHelper.addLiveShareCount(act);
                        NetHelper.activityShare(mAct, "0");
                    }
                });
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable)
            {
                Logger.t(TAG).d(">>>>>>>>>分享失败" + i + ">>" + throwable.getMessage());
                throwable.printStackTrace();
                if (throwable instanceof cn.sharesdk.tencent.qzone.QQClientNotExistException ||
                        throwable instanceof QQClientNotExistException)
                {
                    act.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ToastUtils.showLong("请安装QQ客户端");
                        }
                    });
                }
                else
                {
                    act.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ToastUtils.showShort("分享失败");
                        }
                    });
                }
            }

            @Override
            public void onCancel(Platform platform, int i)
            {

                act.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ToastUtils.showShort("分享取消");
                    }
                });
            }
        });
        switch (mRecord.getModeOfRoom())
        {
            case LiveRecord.ROOM_MODE_HOST:
                bean.setShareWeChatMomentsTitle("我的直播间是“" + enterRoomBean.getHn() + "”,颜值高不高，直播才知道，赶快来给我送礼物吧！");
                bean.setShareContent("我的直播间是:" + enterRoomBean.getHn() + ",颜值高不高，直播才知道，赶快来给我送礼物吧！");
                bean.setShareSinaContent("我的直播间是:" + enterRoomBean.getHn() + ",颜值高不高，直播才知道，赶快来给我送礼物吧！"
                        + NetHelper.LIVE_SHARE_ADDRESS + livePresenter.getmRecord().getRoomId());
                break;
            case LiveRecord.ROOM_MODE_MEMBER:
                bean.setShareContent("越帅越优惠 越靓越实惠,看脸吃饭 【" + enterRoomBean.getHn() + "】");
                bean.setShareWeChatMomentsTitle("越帅越优惠 越靓越实惠,看脸吃饭 【" + enterRoomBean.getHn() + "】");
                bean.setShareSinaContent("越帅越优惠 越靓越实惠,看脸吃饭 【" + enterRoomBean.getHn() + "】"
                        + NetHelper.LIVE_SHARE_ADDRESS + livePresenter.getmRecord().getRoomId());
                break;
            default:
                break;
        }
        sharePopWindow = new SharePopWindow(act, new int[]{
                SharePopWindow.SHARE_WAY_APPFRIEND,
                SharePopWindow.SHARE_WAY_WECHAT_FRIEND,
                SharePopWindow.SHARE_WAY_QQ_FRIEND,
                SharePopWindow.SHARE_WAY_QZONE,
                SharePopWindow.SHARE_WAY_WECHAT_MOMENT,
                SharePopWindow.SHARE_WAY_SINA}, bean);
        sharePopWindow.setPopupTitle("分享你喜欢的直播间，可以提升等级哟~");
        sharePopWindow.setShareItemClickListener(new SharePopWindow.ShareItemClickListener()
        {
            @Override
            public void onItemCLick(int position, String shareKey)
            {
                switch (shareKey)
                {
                    case "看脸好友":
                        NetHelper.activityShare(mAct, "0");
                        break;
                }
            }
        });
        sharePopWindow.showPopupWindow(anchorView, backView);
    }

    /**
     * 分享窗口消失
     */
    public boolean dismissShareWin()
    {
        boolean isShowing = true;
        if (sharePopWindow != null && sharePopWindow.isShowing())
        {
            sharePopWindow.dismiss();
            isShowing = false;
        }
        return isShowing;
    }

    /**
     * 分享窗口销毁，避免窗口泄露
     */
    public void killShareWin()
    {
        if (sharePopWindow != null)
        {
            sharePopWindow.dismiss();
            sharePopWindow = null;
        }
    }

    /**
     * 跳转选择选择连麦人
     */
    public void chooseConnectMember(Activity activity, String roomId)
    {
        Intent intent = new Intent(activity, LChoseConnectMemberAct.class);
        intent.putExtra("roomId", roomId);
        activity.startActivityForResult(intent, EamCode4Result.reQ_LChooseContactMemberAct);
    }

    /**
     * 确定是否直播结束dialog
     *
     * @param clickListener 确定按钮监听
     * @return
     */
    public CustomAlertDialog getBackDialog(View.OnClickListener clickListener)
    {
        if (backDialog != null)
            return backDialog;
//        backDialog = new Dialog(mAct, R.style.dialog);
//        backDialog.setContentView(R.layout.dialog_end_live);
//        TextView tvSure = (TextView) backDialog.findViewById(R.id.btn_sure);
//        tvSure.setOnClickListener(clickListener);
//        TextView tvCancel = (TextView) backDialog.findViewById(R.id.btn_cancel);
//        tvCancel.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                backDialog.cancel();
//            }
//        });

        backDialog = new CustomAlertDialog(mAct).builder();
        backDialog.setTitle("提示");
        backDialog.setMsg(mAct.getString(R.string.live_host_quite_tip));
        backDialog.setPositiveButton("确认", clickListener);
        backDialog.setNegativeButton("取消", new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                backDialog.dismiss();
            }
        });

        return backDialog;
    }


    /**
     * 显示主播结束直播详情dialog
     *
     * @param sec  直播时长 单位秒
     * @param meal 直播获得饭票数
     */
    public void showLiveEndDetailDialog(String totalMeal, long startTime, @Nullable String sec, @Nullable String meal)
    {
        if (mAct.isFinishing())
            return;
        if (mLiveEndDetailDialog == null)
            mLiveEndDetailDialog = new LiveEndDetailDialog();
        long liveTime;
        if (!TextUtils.isEmpty(sec))
        {
            liveTime = Long.parseLong(sec);
        }
        else
        {
            liveTime = (System.currentTimeMillis() - startTime) / 1000;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
        String hms = simpleDateFormat.format(liveTime * 1000);
        int newFanPiao = 0;
        if (TextUtils.isEmpty(meal))
        {
            newFanPiao = Integer.parseInt(totalMeal) - Integer.parseInt(livePresenter.getmRecord().getEnterRoom4EH().getMeal());
            Logger.t("FanPiao").d(Integer.parseInt(totalMeal) + ">>>" + Integer.parseInt(livePresenter.getmRecord().getEnterRoom4EH().getMeal()));
        }
        else
        {
            newFanPiao = Integer.parseInt(meal);
        }
        meal = String.valueOf((newFanPiao < 0 ? 0 : newFanPiao));
        mLiveEndDetailDialog.showDialog(mAct.getSupportFragmentManager(), livePresenter, hms, meal);
    }

    /**
     * 初始化美颜工具
     *
     * @param targetView 显示view
     * @param maxBeauty 美颜最大值
     * @param maxWhite  美白最大值
     * @param defaultBeauty 美颜默认值
     * @param defaultWhite  美白默认值
     */
    private int beauty = 3;
    private int white = 3;

    public void initBeauty(LinearLayout targetView, final int maxBeauty, final int maxWhite, int defaultBeauty, int defaultWhite)
    {
        beauty = defaultBeauty;
        white = defaultWhite;
        View view = LayoutInflater.from(mAct).inflate(R.layout.live_beauty_layout, targetView);
        //  targetView.addView(view);
        final TextView tvBeautyNum = (TextView) targetView.findViewById(R.id.tv_beauty_num);
        final TextView tvWhiteNum = (TextView) targetView.findViewById(R.id.tv_white_num);
        SeekBar seekBarBeauty = (SeekBar) targetView.findViewById(R.id.seekBar_beauty);
        SeekBar seekBarWhite = (SeekBar) targetView.findViewById(R.id.seekBar_white);
        seekBarBeauty.setEnabled(true);
        seekBarWhite.setEnabled(true);
        seekBarBeauty.setMax(maxBeauty);
        seekBarWhite.setMax(maxWhite);
        tvBeautyNum.setText(String.format("%d%%", defaultBeauty * 100 / maxBeauty));
        tvWhiteNum.setText(String.format("%d%%", defaultWhite * 100 / maxBeauty));
        seekBarBeauty.setProgress(defaultBeauty);
        seekBarWhite.setProgress(defaultWhite);
        seekBarBeauty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                tvBeautyNum.setText(String.format("%d%%", progress * 100 / maxBeauty));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                Logger.t(TAG).d("progress >>> " + seekBar.getProgress());
                beauty = seekBar.getProgress();
                livePresenter.setBeautyData(beauty, white);
            }
        });
        seekBarWhite.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                tvWhiteNum.setText(String.format("%d%%", progress * 100 / maxWhite));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                Logger.t(TAG).d("progress >>> " + seekBar.getProgress());
                white = seekBar.getProgress();
                livePresenter.setBeautyData(beauty, white);
            }
        });

    }


    //------------------------------------------------------------直播间消息 start-----------------------------------------------------------------------------------------------
    private LiveMsgDialog liveMsgDialog;

    /**
     * 直播间消息实例
     *
     * @return
     */
    public LiveMsgDialog liveMsgDialogInstance()
    {
        if (liveMsgDialog == null)
            liveMsgDialog = LiveMsgDialog.newInstance();
        return liveMsgDialog;
    }


    private LiveNewMsgDialog liveNewMsgDialog;

    /**
     * 直播间消息实例
     *
     * @return
     */
    public LiveNewMsgDialog liveNewMsgDialogInstance()
    {
        if (liveNewMsgDialog == null)
            liveNewMsgDialog = LiveNewMsgDialog.newInstance();
        return liveNewMsgDialog;
    }

    //------------------------------------------------------------直播间消息 end-----------------------------------------------------------------------------------------------

    /**
     * @Description: 显示新手引导
     */
    public void showNewbieGuide()
    {
        if (SharePreUtils.getIsNewLiveRoom(mAct))
        {
            NetHelper.checkIsShowNewbie(mAct, "11", new ICommonOperateListener()
            {
                @Override
                public void onSuccess(String response)
                {
                    if ("0".equals(response))
                    {
                        //获取root节点
                        final FrameLayout fRoot = (FrameLayout) mAct.getWindow().getDecorView().findViewById(android.R.id.content);
                        final View vGuide = View.inflate(mAct, R.layout.live_room_guide, null);

                        final ImageView imgOrder1 = (ImageView) vGuide.findViewById(R.id.guide_1);
                        final ImageView imgOrder2 = (ImageView) vGuide.findViewById(R.id.guide_2);
                        final TextView tvClickDismiss = (TextView) vGuide.findViewById(R.id.tv_click_dismiss);


                        vGuide.setClickable(true);

                        tvClickDismiss.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                fRoot.removeView(vGuide);
                                SharePreUtils.setIsNewLiveRoom(mAct, false);
                                NetHelper.saveShowNewbieStatus(mAct, "11");

                            }
                        });
                        fRoot.addView(vGuide);
                    }
                    else
                    {
                        SharePreUtils.setIsNewLiveRoom(mAct, false);
                    }
                }

                @Override
                public void onError(String code, String msg)
                {

                }
            });

        }
    }

    /**
     * 开始游戏
     *
     * @param url
     * @param battleId
     * @param scheduled
     */
    public void goToGame(String url, String battleId, String scheduled, GameExitListener exitListener)
    {
        if (mGameWeb == null)
            mGameWeb = new BridgeWebView(mAct);
        if (liveGameInviteDialog != null)
            liveGameInviteDialog.dismiss();
        try
        {
            ((FrameLayout) mAct.findViewById(android.R.id.content)).addView(mGameWeb);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mGameWeb.setBackgroundColor(ContextCompat.getColor(mAct, R.color.transparent));
            mGameWeb.setLayoutParams(layoutParams);
            mGameWeb.getSettings().setLoadWithOverviewMode(true);
            mGameWeb.getSettings().setUseWideViewPort(true);
            mGameWeb.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            mGameWeb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            //数据交互处理器
            mGameWeb.registerHandler("shareDataBetweenJavaAndJs", new BridgeHandler()
            {
                @Override
                public void handler(String data, CallBackFunction function)
                {
                    //从js获得数据
                    Logger.t(TAG).d("JStoJava" + data);
                    //传给js的数据
                    Map<String, Object> reqParamMap = NetHelper.getH5CommonMap(mAct);
                    reqParamMap.put("battleId", battleId);
                    reqParamMap.put("scheduled", scheduled);
                    function.onCallBack(new Gson().toJson(reqParamMap));
                }
            });
            //退出游戏
            mGameWeb.registerHandler("gameExit", (String data, CallBackFunction function) ->
            {
                Logger.t(TAG).d("gameExit" + data);
                gameStart = false;
                mGameWeb.loadUrl("about:blank");
                if (mGameWeb != null)
                    ((FrameLayout) mAct.findViewById(android.R.id.content)).removeView(mGameWeb);
                if (gameExitListener != null)
                    gameExitListener.exit();
                if (exitListener != null)
                    exitListener.exit();
            });
            mGameWeb.registerHandler("shareGame", (String data, CallBackFunction function) ->
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data);
                if (TextUtils.isEmpty(data))
                    return;
                JSONObject jsonObject = null;
                try
                {
                    jsonObject = new JSONObject(data);
                    livePresenter.shareH5(jsonObject.getString("gameId"), jsonObject.getString("matchingId"), jsonObject.getString("score"), jsonObject.getString("isMyDynamics"));
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            });
            mGameWeb.loadUrl(url);
            gameStart = true;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void gameOnResume()
    {
        if (mGameWeb != null)
            mGameWeb.callHandler("qiantai", "", null);
    }

    public void gameOnPause()
    {
        if (mGameWeb != null)
            mGameWeb.callHandler("houtai", "", null);
    }

    /**
     * 设置退出游戏监听
     *
     * @param gameExitListener
     */
    public void setGameExitListener(GameExitListener gameExitListener)
    {
        this.gameExitListener = gameExitListener;
    }

    public boolean isGameStart()
    {
        return gameStart;
    }

    /**
     * 显示游戏邀请列表弹窗
     */
    public void showGameInviteDialog()
    {
        if (mAct.isFinishing())
            return;
        if (liveGameInviteDialog == null)
            liveGameInviteDialog = new LiveGameInviteDialog();
        liveGameInviteDialog.setAnchorUId(livePresenter.getmRecord().getEnterRoom4EH().getuId());
        liveGameInviteDialog.setAnchor(livePresenter.getmRecord().getModeOfRoom() == LiveRecord.ROOM_MODE_HOST);
        if (!liveGameInviteDialog.isAdded() && !liveGameInviteDialog.isRemoving())
            liveGameInviteDialog.show(mAct.getSupportFragmentManager(), TAG, frameAnimator != null && frameAnimator.isShowing() ? 1 : 0);
        livePresenter.getCanInviteList("0002", livePresenter.getmRecord().getRoomId(), "0", "20", "refresh");
        liveGameInviteDialog.setInviteDialogListener(new LiveGameInviteDialog.InviteDialogListener()
        {
            @Override
            public void inviteClicked()
            {
                livePresenter.getCanInviteList("0002", livePresenter.getmRecord().getRoomId(), "0", "20", "refresh");
            }

            @Override
            public void invitedClicked()
            {
                livePresenter.getGameInviters("0002", livePresenter.getmRecord().getRoomId(), "0", "20", "refresh");
                stopGameAnim();
            }

            @Override
            public void sendInvite(String list, List<String> selectTxIdList, List<Integer> selectPosition)
            {
                if (!CommonUtils.isFastDoubleClick())
                    livePresenter.sendInvitation("0002", livePresenter.getmRecord().getRoomId(), list, selectTxIdList, selectPosition);
            }

            @Override
            public void rejectInvite(int position, GameInviteBean gameInviteBean)
            {
                livePresenter.answerInvitation("0002", livePresenter.getmRecord().getRoomId(), gameInviteBean.getUId(), gameInviteBean.getId(), "1", position);
            }

            @Override
            public void acceptInvite(GameInviteBean gameInviteBean)
            {
                if (!CommonUtils.isFastDoubleClick())
                    livePresenter.answerInvitation("0002", livePresenter.getmRecord().getRoomId(), gameInviteBean.getUId(), gameInviteBean.getId(), "0", -1);
            }

            @Override
            public void refreshStartGame()
            {
                livePresenter.getMatchResult("0002");
            }

            @Override
            public void startGameClick()
            {
                if (!CommonUtils.isFastDoubleClick())
                    livePresenter.joinGame("0002");
            }

            @Override
            public void exitGame()
            {
                if (!CommonUtils.isFastDoubleClick())
                    livePresenter.exitGame("0002");
            }

            @Override
            public void loadMoreInviteOrInvited(boolean isInvite, String start)
            {
                if (isInvite)
                    livePresenter.getCanInviteList("0002", livePresenter.getmRecord().getRoomId(), start, "20", "add");
                else
                    livePresenter.getGameInviters("0002", livePresenter.getmRecord().getRoomId(), start, "20", "add");
            }
        });
    }

    /**
     * 刷新游戏邀请别表数据
     *
     * @param isInviteList 是否是邀请别表 true 刷新邀请列表 false 刷新被邀请列表
     * @param list
     * @param position     如果position 大于0 则是拒绝邀请 position 为list 索引
     */
    public void refreshGameInviteDialog(boolean isInviteList, List<GameInviteBean> list, int position, String type, List<Integer> invitePosition, String noShowId)
    {
        if (liveGameInviteDialog != null)
        {
            if (isInviteList)
                liveGameInviteDialog.notifyInviteList(list, type, invitePosition, noShowId);
            else
                liveGameInviteDialog.notifyInvitedList(list, type);
        }
        if (position >= 0 && invitePosition == null)
        {
            if (liveGameInviteDialog != null)
                liveGameInviteDialog.deleteInvitedItem(position);
        }
    }

    /**
     * 显示游戏开始界面
     *
     * @param startGameBean
     */
    public void showGameStart(StartGameBean startGameBean, boolean isResume, boolean isStartGame)
    {
        if (liveGameInviteDialog != null)
        {
            if (isResume && !liveGameInviteDialog.isShowStart())
                return;
            liveGameInviteDialog.showStart(startGameBean, mAct, TAG, isStartGame);
        }

    }

    /**
     * 开始游戏心跳
     */
    public void startGameHeart()
    {
        Logger.t("liveGame").d("heartStart>>>>" + liveGameInviteDialog);
        if (liveGameInviteDialog != null)
            liveGameInviteDialog.startGameHeart();
    }

    /**
     * 停止游戏心跳
     */
    public void stopGameHeart()
    {
        if (liveGameInviteDialog != null)
            liveGameInviteDialog.stopGameHeart();
    }

    /**
     * 游戏邀请弹窗dismiss
     */
    public void liveGameInviteDialogDismiss()
    {
        if (liveGameInviteDialog != null)
            liveGameInviteDialog.dismissAllowingStateLoss();
    }

    public void refreshGameStartBtn(boolean joinSuccess)
    {
        if (liveGameInviteDialog != null)
            liveGameInviteDialog.refreshStartBtn(joinSuccess);
    }

    private List<Integer> imgs;

    /**
     * 小游戏icon动画开始
     */
    public void startGameAnim()
    {
        if (liveGameInviteDialog != null && liveGameInviteDialog.getDialog() != null && liveGameInviteDialog.getDialog().isShowing() &&
                liveGameInviteDialog.getCurrentPosition() == 1)
        {
            livePresenter.getGameInviters("0002", livePresenter.getmRecord().getRoomId(), "0", "20", "refresh");
        }
        else
        {
            if (imgGame == null)
                imgGame = mAct.getWindow().findViewById(R.id.img_game);
            if (frameAnimator == null)
                frameAnimator = new FrameAnimator(imgGame);
            if (imgs == null)
            {
                imgs = new ArrayList<>();
                for (int i = 1; i < 49; i++)
                {
                    int id = mAct.getResources().getIdentifier("gameiconanim_" + i, "drawable", mAct.getPackageName());
                    if (id > 0)
                    {
                        imgs.add(id);
                    }
                }
            }
            frameAnimator.startAnimation(imgs, true);
        }

    }

    /**
     * 小游戏icon动画停止
     */
    public void stopGameAnim()
    {
        if (frameAnimator != null)
            frameAnimator.stopAnimation4Drawable();
        if (imgGame != null)
            imgGame.setImageResource(R.drawable.btn_game);

    }


    /**
     * 小游戏分享
     *
     * @param url
     * @param shareIcon
     * @param ShareContent
     * @param shareTitle
     * @param gameId
     * @param matchingId
     * @param score
     */
    public void shareGame(String url, String shareIcon, String ShareContent, String shareTitle
            , String gameId, String matchingId, String score, String isMyDynamics, View view)
    {
        ShareToFaceBean bean = new ShareToFaceBean();
        String shareUrl = NetInterfaceConstant.sun_moon_star_share;
        bean.setShareWeChatMomentsTitle(shareTitle);
        bean.setShareWeChatMomentsContent(ShareContent);
        bean.setShareTitleUrl(shareUrl);
        bean.setShareUrl(shareUrl);
        bean.setShareSiteUrl(shareUrl);
        bean.setShareTitle(shareTitle);
        bean.setShareContent(ShareContent);
        bean.setShareSinaContent(ShareContent + shareUrl);
        bean.setShareAppImageUrl(shareIcon);
        bean.setShareImgUrl(shareIcon);
        bean.setGameId(gameId);
        bean.setShareType(Platform.SHARE_WEBPAGE);
        bean.setShareSite("看脸吃饭");
        bean.setOpenSouse("game");// 传递房间参数到看脸好友界面
        bean.setTitleImage(shareIcon);
        bean.setShareListener(new PlatformActionListener()
        {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap)
            {
                ToastUtils.showShort("分享成功");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable)
            {

            }

            @Override
            public void onCancel(Platform platform, int i)
            {
                ToastUtils.showShort("分享取消");
            }
        });
        int[] showIndex;
        if ("0".equals(isMyDynamics))
        {
            showIndex = new int[]{SharePopWindow.SHARE_WAY_WECHAT_FRIEND, SharePopWindow.SHARE_WAY_QQ_FRIEND,
                    SharePopWindow.SHARE_WAY_QZONE, SharePopWindow.SHARE_WAY_WECHAT_MOMENT, SharePopWindow.SHARE_WAY_SINA};
        }
        else
        {
            showIndex = new int[]{SharePopWindow.SHARE_WAY_DYNAMIC, SharePopWindow.SHARE_WAY_WECHAT_FRIEND,
                    SharePopWindow.SHARE_WAY_QQ_FRIEND, SharePopWindow.SHARE_WAY_QZONE,
                    SharePopWindow.SHARE_WAY_WECHAT_MOMENT, SharePopWindow.SHARE_WAY_SINA};
        }
        sharePopWindow = new SharePopWindow(mAct, showIndex, bean)
        ;
        sharePopWindow.setShareItemClickListener(new SharePopWindow.ShareItemClickListener()
        {
            @Override
            public void onItemCLick(int position, String shareKey)
            {
                if ("我的动态".equals(shareKey))
                {
                    livePresenter.shareGame(gameId, matchingId, "6", score);
                }
                else if ("看脸好友".equals(shareKey))
                {
                    sharePopWindow.getShareInfo().setShareUrl(url);
                    Logger.t(TAG).d("分享数据》》" + sharePopWindow.getShareInfo().getShareUrl());
                    livePresenter.shareGame(gameId, matchingId, "7", score);
                }
            }
        });
        sharePopWindow.showPopupWindow(((ViewGroup) mAct.findViewById(android.R.id.content)).getChildAt(0), view);
    }

    //观众列表操作类型
    public enum AudienceType
    {
        REFRESH,
        ADD,
        REMOVE
    }

    public interface GameExitListener
    {
        void exit();
    }

}

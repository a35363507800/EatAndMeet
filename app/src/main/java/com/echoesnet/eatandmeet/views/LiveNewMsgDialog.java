package com.echoesnet.eatandmeet.views;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.RelationAct;
import com.echoesnet.eatandmeet.activities.liveplay.View.LiveRoomAct1;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.fragments.CSayHelloUsersFrg;
import com.echoesnet.eatandmeet.fragments.ConversationListFragment;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.models.datamodel.AbstractEMMessageListener;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.IMUtils.Constant;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.echoesnet.eatandmeet.views.widgets.LiveMsgDialog.LiveChatDialog;
import com.echoesnet.eatandmeet.views.widgets.NoScrollViewPager;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lc on 2017/7/25 09.
 */

public class LiveNewMsgDialog extends DialogFragment implements View.OnClickListener
{

    private static final String TAG = LiveNewMsgDialog.class.getSimpleName();
    private NoScrollViewPager viewPager;
    private TextView root;
    private PagerAdapter mPagerAdapter;
    private TextView tvChooseContact;
    private TabLayout tabLayout;

    private ImageView chatRedImg;//右边红点
    private ImageView sayHelloRedImg;//左边红点
    private boolean hasNewChat = false;
    private boolean hasNewSayHello = false;
    private int mCurrentIndex;

    private CSayHelloUsersFrg cSayHelloFrg;
    private ConversationListFragment conversationFragment;
    //里面包含的两个fragment
    List<Fragment> fragments = new ArrayList<>();
    private static LiveNewMsgDialog yourDialogFragment;

    private LiveChatDialog liveChatDialog;
    public static View liveMsgView;

    public RelativeLayout relativeLayout;


    public LiveNewMsgDialog()
    {
    }

    public static LiveNewMsgDialog newInstance()
    {
        if (yourDialogFragment == null)
            yourDialogFragment = new LiveNewMsgDialog();
        return yourDialogFragment;
    }

    public boolean isHasNewChat()
    {
        return hasNewChat;
    }

    public boolean isHasNewSayHello()
    {
        return hasNewSayHello;
    }

    public void setHasNewChat(boolean hasNewChat)
    {
        this.hasNewChat = hasNewChat;
    }

    public void setHasNewSayHello(boolean hasNewSayHello)
    {
        this.hasNewSayHello = hasNewSayHello;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(R.style.AnimationBottomInOut, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        try
        {
            EMClient.getInstance().chatManager().addMessageListener(msgListener);
        } catch (Exception e)
        {
            e.printStackTrace();
            if (e instanceof NullPointerException)
            {
                HuanXinIMHelper.getInstance().init(EamApplication.getInstance());
                EamLogger.t(TAG).writeToDefaultFile("由于有崩溃发生，环信状态为空：" + e == null ? "null" : e.getMessage());
            }
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Window window = getDialog().getWindow();
        if (window != null)
        {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = CommonUtils.getScreenSize(getActivity()).width;
            window.setAttributes(lp);
            window.getAttributes().windowAnimations = R.style.AnimationBottomInOut;
        }
        root = liveMsgView.findViewById(R.id.root);
        viewPager = (NoScrollViewPager) liveMsgView.findViewById(R.id.vp_msg_or_friend);
        tabLayout = (TabLayout) liveMsgView.findViewById(R.id.tabLayout_live_msg);
        chatRedImg = (ImageView) liveMsgView.findViewById(R.id.img_new_Friend);
        sayHelloRedImg = (ImageView) liveMsgView.findViewById(R.id.img_new_msg);
        tvChooseContact = (TextView) liveMsgView.findViewById(R.id.tv_choose_contact);
        root.setOnClickListener(this);
        tvChooseContact.setOnClickListener(this);

        relativeLayout = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(params);
        relativeLayout.addView(liveMsgView);
        initView();

        return relativeLayout;
        //    return liveMsgView;
    }

    private void registerBrdReceiver()
    {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(EamConstant.EAM_NOTIFY_LIVE_CHAT_RED);
        if (getActivity() != null)
            getActivity().registerReceiver(liveReceiver, myIntentFilter);
    }

    private void unRegisterBrdReceiver()
    {
        if (getActivity() != null)
            getActivity().unregisterReceiver(liveReceiver);
    }

    BroadcastReceiver liveReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action))
                return;
            switch (action)
            {
                case EamConstant.EAM_NOTIFY_LIVE_CHAT_RED:
                    if (cSayHelloFrg != null)
                    {
                        if (cSayHelloFrg.getUnreadMsgCount() > 0)
                            showSayHelloRed();
                        else
                            sayHelloRedImg.setVisibility(View.GONE);
                    }
                    if (conversationFragment != null)
                    {
                        if (conversationFragment.getNormalChatMsgCount() > 0)
                            showChatRed();
                        else
                            chatRedImg.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };


    private void initView()
    {
//        resFrg = new LiveCommunicateFrg();

        cSayHelloFrg = new CSayHelloUsersFrg();
        conversationFragment = new ConversationListFragment();
        Bundle bud = new Bundle();
        bud.putString(ConversationListFragment.MSG_TYPE, ConversationListFragment.MSG_HELLO_CHAT);
        bud.putString(ConversationListFragment.OPEN_SOURCE, ConversationListFragment.LIVE_OPEN);
        conversationFragment.setArguments(bud);

        fragments.clear();
        fragments.add(cSayHelloFrg);
        fragments.add(conversationFragment);

        registerBrdReceiver();

        //初始化Adapter
        mPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager())
        {
            @Override
            public int getCount()
            {
                return fragments.size();
            }

            @Override
            public Fragment getItem(int arg0)
            {
                return fragments.get(arg0);
            }

            @Override
            public void finishUpdate(ViewGroup container)
            {
                try
                {
                    super.finishUpdate(container);
                } catch (NullPointerException nullPointerException)
                {
                    Logger.t(TAG).d("Catch the NullPointerException in FragmentPagerAdapter.finishUpdate");
                }
            }
        };
        viewPager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        initTabLayout();

        conversationFragment.setIMessageReceiveListener(new ConversationListFragment.IMessageReceiveListener()
        {
            @Override
            public void onChatMessageReceived(EMMessage message)
            {
                showChatRed();
            }

            @Override
            public void onHelloMessageReceived(EMMessage message)
            {
                showSayHelloRed();
            }
        });

        Logger.t(TAG).d("显示：hasNewChat：" + hasNewChat + " | hasNewSayHello:" + hasNewSayHello);
        if (hasNewChat)
        {
            showChatRed();
        }
        else
            chatRedImg.setVisibility(View.GONE);

        if (hasNewSayHello)
            showSayHelloRed();
        else
            sayHelloRedImg.setVisibility(View.GONE);
    }

    private void initTabLayout()
    {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                mCurrentIndex = tab.getPosition();
                if (tab.getPosition() == 1)
                {
                    chatRedImg.setVisibility(View.GONE);
                    HuanXinIMHelper.getInstance().getUnreadChatMsgNum(HuanXinIMHelper.HELLO_CHAT_TYPE, new ICommonOperateListener()
                    {
                        @Override
                        public void onSuccess(String response)
                        {
                            Map<String, Integer> map = EamApplication.getInstance().getGsonInstance().fromJson(response, new TypeToken<Map<String, Integer>>()
                            {
                            }.getType());
                            int allUnreadNum = map.get("hello");
                            if (allUnreadNum > 0)
                                sayHelloRedImg.setVisibility(View.VISIBLE);
                            else
                                sayHelloRedImg.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(String code, String msg)
                        {

                        }
                    });

                    HuanXinIMHelper.getInstance().getUnreadChatMsgNum(HuanXinIMHelper.NORMAL_CHAT_TYPE, new ICommonOperateListener()
                    {
                        @Override
                        public void onSuccess(String response)
                        {
                            Map<String, Integer> map = EamApplication.getInstance().getGsonInstance().fromJson(response, new TypeToken<Map<String, Integer>>()
                            {
                            }.getType());
                            int allUnreadNum = map.get("chat");
                            if (allUnreadNum == 0)
                                hasNewChat = false;
                        }

                        @Override
                        public void onError(String code, String msg)
                        {

                        }
                    });

                }
                else if (tab.getPosition() == 0)
                {
                    sayHelloRedImg.setVisibility(View.GONE);
                    HuanXinIMHelper.getInstance().getUnreadChatMsgNum(HuanXinIMHelper.NORMAL_CHAT_TYPE, new ICommonOperateListener()
                    {
                        @Override
                        public void onSuccess(String response)
                        {
                            Map<String, Integer> map = EamApplication.getInstance().getGsonInstance().fromJson(response, new TypeToken<Map<String, Integer>>()
                            {
                            }.getType());
                            int allUnreadNum = map.get("chat");
                            if (allUnreadNum > 0)
                                chatRedImg.setVisibility(View.VISIBLE);
                            else
                                chatRedImg.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(String code, String msg)
                        {

                        }
                    });
                    HuanXinIMHelper.getInstance().getUnreadChatMsgNum(HuanXinIMHelper.HELLO_CHAT_TYPE, new ICommonOperateListener()
                    {
                        @Override
                        public void onSuccess(String response)
                        {
                            Map<String, Integer> map = EamApplication.getInstance().getGsonInstance().fromJson(response, new TypeToken<Map<String, Integer>>()
                            {
                            }.getType());
                            int allUnreadNum = map.get("hello");
                            if (allUnreadNum == 0)
                                hasNewSayHello = false;
                        }

                        @Override
                        public void onError(String code, String msg)
                        {

                        }
                    });

                }
                changeTabTextStyle2(mCurrentIndex);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });

        for (int i = 0; i < tabLayout.getTabCount(); i++)
        {

            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View tabView = LayoutInflater.from(getActivity()).inflate(R.layout.find_tab, null);
            TextView tabTitle = (TextView) tabView.findViewById(R.id.tv_tab_title);
            ImageView tabImg = (ImageView) tabView.findViewById(R.id.img_tab);
            tabImg.setBackgroundResource(R.drawable.live_msg_tab_img_selector);
            tabTitle.setText(i == 0 ? "打招呼" :
                    i == 1 ? "聊天" : "");
            tab.setCustomView(tabView);
            if (i == 0)
            {
                tabTitle.setTypeface(null, Typeface.BOLD);
                tabTitle.setSelected(true);
            }
        }
    }

    private void changeTabTextStyle2(int position)

    {
        for (int i = 0; i < tabLayout.getTabCount(); i++)
        {
            View view = tabLayout.getTabAt(i).getCustomView();
            if (view != null)
            {
                TextView text1 = (TextView) view.findViewById(R.id.tv_tab_title);
                if (position == i)
                {
                    text1.setTypeface(null, Typeface.BOLD);
                    text1.setSelected(true);
                }
                else
                {
                    text1.setTypeface(null, Typeface.NORMAL);
                    text1.setSelected(false);
                }
            }
        }
    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.root:
                dismiss();
                break;
            case R.id.tv_choose_contact:
                Intent intent = new Intent(getActivity(), RelationAct.class);
                intent.putExtra("openFrom", "follow-list");
                intent.putExtra("openSource", "live");
                startActivityForResult(intent, EamCode4Result.reQ_LiveChooseContactAct);
//                startActivity(intent);
            default:
                break;
        }
    }


    public void showChatRed()
    {
        Logger.t(TAG).d("mCurrentIndex" + mCurrentIndex);
        if (tabLayout != null)
        {
            if (getActivity() != null)
            {
                getActivity().runOnUiThread(() ->
                {
                    if (!conversationFragment.getConFragmentVisibility())
                        chatRedImg.setVisibility(View.VISIBLE);
                });
            }
        }
    }


    public void showSayHelloRed()
    {
        if (tabLayout != null && mCurrentIndex == 1)
        {
            if (getActivity() != null)
            {
                getActivity().runOnUiThread(() ->
                {
                    if (!cSayHelloFrg.getVisibility())
                        sayHelloRedImg.setVisibility(View.VISIBLE);
                });
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                switch (requestCode)
                {
                    case EamCode4Result.reQ_LiveChooseContactAct:
                        Logger.t(TAG).d("onActivityResult:" + data);
                        if (data != null)
                        {
                            final EaseUser toEaseUser = data.getParcelableExtra(Constant.EXTRA_TO_EASEUSER);
                            if (toEaseUser == null)
                                return;
//                            this.show(getChildFragmentManager(), TAG);
                            new Thread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    HuanXinIMHelper.getInstance().saveContact(toEaseUser);
                                }
                            }).start();
                            liveChatDialog = LiveChatDialog.newInstance(toEaseUser);
                            liveChatDialog.setOnDismissListener(new LiveChatDialog.OnDismissListener()
                            {
                                @Override
                                public void onDisMiss()
                                {
                                    if (conversationFragment != null)
                                    {
                                        conversationFragment.refresh(ConversationListFragment
                                                .MSG_HELLO_CHAT);
                                    }
                                }
                            });
                            liveChatDialog.show(getChildFragmentManager(), TAG);
                        }
                        break;
                }
                break;
            case Activity.RESULT_CANCELED:
                break;
            case Activity.RESULT_FIRST_USER:
                break;
        }
    }

    private EMMessageListener msgListener = new AbstractEMMessageListener()
    {
        @Override
        public void onMessageReceived(List<EMMessage> messages)
        {
            //防止当处理出现异常时阻挡消息继续向下传递--wb
            try
            {
                Logger.t(TAG).d("onMessageReceived==" + messages.toString());
//                showSayHelloRed();//收到消息
            } catch (Exception e)
            {
                e.printStackTrace();
                Logger.t(TAG).d("hx 消息解析错误" + e.getMessage());
            }
        }
    };

//    private EMContactListener contactListener = new AbstractEMContactListener()
//    {
//        @Override
//        public void onContactDeleted(String username)
//        {
//            super.onContactDeleted(username);
//            if (liveChatDialog != null && liveChatDialog.getDialog() != null && liveChatDialog.getDialog().isShowing())
//                liveChatDialog.dismiss();
//        }
//
//        @Override
//        public void onContactInvited(String username, String reason)
//        {
//            super.onContactInvited(username, reason);
//            Logger.t(TAG).d("被好友邀请了，" + username + "," + reason);
//            showChatRed();
//        }
//    };

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        Logger.t("addMsgListener").d("onDismiss>>>>>");
        try
        {
            EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        } catch (Exception e)
        {
            e.printStackTrace();
            if (e instanceof NullPointerException)
            {
                HuanXinIMHelper.getInstance().init(EamApplication.getInstance());
                EamLogger.t(TAG).writeToDefaultFile("由于有崩溃发生，环信状态为空：" + e == null ? "null" : e.getMessage());
            }
        }
        unRegisterBrdReceiver();
        if (getActivity() != null)
        {
            if (getActivity() instanceof LiveRoomAct1)
                ((LiveRoomAct1) getActivity()).addMsgListener();
        }
        relativeLayout.removeAllViews();
        if (dismissListener != null)
            dismissListener.onDismiss();
        super.onDismiss(dialog);
    }

    public void upHeight()
    {
        if (liveChatDialog != null)
        {
            liveChatDialog.upHeight();
        }
    }

    private LiveNewMsgDialog.DismissListener dismissListener;

    public interface DismissListener
    {
        void onDismiss();
    }

    public void setOnDismissListener(LiveNewMsgDialog.DismissListener dismissListener)
    {
        this.dismissListener = dismissListener;
    }
}

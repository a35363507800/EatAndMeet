package com.echoesnet.eatandmeet.views.widgets.LiveMsgDialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.live.LiveChooseContactAct;
import com.echoesnet.eatandmeet.activities.liveplay.View.LiveRoomAct1;
import com.echoesnet.eatandmeet.fragments.LiveNewFriendsFragment;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.models.datamodel.AbstractEMContactListener;
import com.echoesnet.eatandmeet.models.datamodel.AbstractEMMessageListener;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.widgets.NoScrollViewPager;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yqh on 2017/3/28.
 */

public class LiveMsgDialog extends DialogFragment implements View.OnClickListener
{
    private static final String TAG = LiveMsgDialog.class.getSimpleName();
    private NoScrollViewPager viewPager;
    private RelativeLayout root;
    private PagerAdapter mPagerAdapter;
    private TextView tvChooseContact;
    private TabLayout tabLayout;
    private ImageView newFriendRedImg;
    private ImageView newMsgRedImg;
    private boolean hasNewFriend = false;
    private boolean hasNewMsg = false;
    private int mCurrentIndex;
//    private LiveCommunicateFrg resFrg;
    private LiveNewFriendsFragment contactListFragment;
    //里面包含的两个fragment
    List<Fragment> fragments = new ArrayList<>();
    private static LiveMsgDialog yourDialogFragment;
    private LiveChatDialog liveChatDialog;
    public static View liveMsgView;
    private Handler handler;



    public RelativeLayout relativeLayout;


    public LiveMsgDialog()
    {
    }

    public static LiveMsgDialog newInstance()
    {
        if (yourDialogFragment == null)
            yourDialogFragment = new LiveMsgDialog();
        return yourDialogFragment;
    }

    public void setHasNewFriend(boolean hasNewFriend)
    {
        this.hasNewFriend = hasNewFriend;
    }

    public void setHasNewMsg(boolean hasNewMsg)
    {
        this.hasNewMsg = hasNewMsg;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(R.style.AnimationBottomInOut, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
         handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case 0:
                        viewPager.setAdapter(mPagerAdapter);
                        tabLayout.setupWithViewPager(viewPager);
                        initTabLayout();
                        break;
                }
            }
        };

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
        root = (RelativeLayout) liveMsgView.findViewById(R.id.root);
        viewPager = (NoScrollViewPager) liveMsgView.findViewById(R.id.vp_msg_or_friend);
        tabLayout = (TabLayout) liveMsgView.findViewById(R.id.tabLayout_live_msg);
        newFriendRedImg = (ImageView) liveMsgView.findViewById(R.id.img_new_Friend);
        newMsgRedImg = (ImageView) liveMsgView.findViewById(R.id.img_new_msg);
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

    private void initView()
    {
//        resFrg = new LiveCommunicateFrg();
        contactListFragment = new LiveNewFriendsFragment();

        Runnable runnable = new Runnable()
        {
            public void run()
            {
//                try
                {
//                    Thread.sleep(500);
                    fragments.clear();
//                    fragments.add(resFrg);
                    fragments.add(contactListFragment);
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
                    handler.sendEmptyMessage(0);


                }
//                catch (InterruptedException e)
//                {
//                    e.printStackTrace();
//                }
            }
        };
        new Thread(runnable).start();
//        viewPager.setCurrentItem(1);
//        viewPager.setCurrentItem(0);

        if (hasNewFriend)
        {
            showNewFriendRed();
        }
        else
            newFriendRedImg.setVisibility(View.GONE);

        if (hasNewMsg)
            showNewMsgRed();
        else
            newMsgRedImg.setVisibility(View.GONE);
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
                    newFriendRedImg.setVisibility(View.GONE);
                    hasNewFriend = false;
                }
                else if (tab.getPosition() == 0)
                {
                    newMsgRedImg.setVisibility(View.GONE);
                    hasNewMsg = false;
                }
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
            tabImg.setImageResource(R.drawable.live_msg_tab_img_selector);
            tabTitle.setText(i == 0 ? "私信" :
                    i == 1 ? "新朋友" : "");
            tab.setCustomView(tabView);
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
                Intent intent = new Intent(getActivity(), LiveChooseContactAct.class);
                startActivityForResult(intent, EamCode4Result.reQ_LiveChooseContactAct);
            default:
                break;
        }
    }

    /**
     * 新朋友红点显示
     */
    public void showNewFriendRed()
    {
        Logger.t(TAG).d("mCurrentIndex"+mCurrentIndex);
        //&& mCurrentIndex == 0
        if (tabLayout != null)
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    newFriendRedImg.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    /**
     * 新消息红点显示
     */
    public void showNewMsgRed()
    {
        if (tabLayout != null && mCurrentIndex == 1)
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    newMsgRedImg.setVisibility(View.VISIBLE);
                }
            });
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
                            String username = data.getStringExtra("username");
//                            this.show(getChildFragmentManager(), TAG);
                            liveChatDialog = LiveChatDialog.newInstance(new EaseUser(username));
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
                showNewMsgRed();//收到消息
            } catch (Exception e)
            {
                e.printStackTrace();
                Logger.t(TAG).d("hx 消息解析错误" + e.getMessage());
            }
        }
    };

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        Logger.t("addMsgListener").d("onDismiss>>>>>");
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
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
//        if (null != resFrg)
//            resFrg.upHeight();
        if (liveChatDialog != null)
        {
            liveChatDialog.upHeight();
        }
    }

    private DismissListener dismissListener;

    public interface DismissListener
    {
        void onDismiss();
    }

    public void setOnDismissListener(DismissListener dismissListener)
    {
        this.dismissListener = dismissListener;
    }
}

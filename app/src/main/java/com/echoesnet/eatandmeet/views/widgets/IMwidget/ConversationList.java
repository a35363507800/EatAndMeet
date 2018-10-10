package com.echoesnet.eatandmeet.views.widgets.IMwidget;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.echoesnet.eatandmeet.activities.CChatActivity;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.ConversationBean;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.IMUtils.Constant;
import com.echoesnet.eatandmeet.utils.IMUtils.EaseCommonUtils;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.redPacket.RedPacketConstant;
import com.echoesnet.eatandmeet.views.adapters.ConversationAdapter;
import com.echoesnet.eatandmeet.views.widgets.ContextMenuDialog;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @createDate 2017/7/25 11:03
 * @description
 */

public class ConversationList extends RecyclerView
{
    private static final String TAG = ConversationList.class.getSimpleName();

    private ConversationAdapter conversationAdapter;
    private List<ConversationBean> mDataSource = new ArrayList<>();
    private Context mContext;
    private View emptyView;

    public ConversationList(Context context)
    {
        this(context, null);
    }

    public ConversationList(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs)
    {
        this.mContext = context;
    }

    public void init(List<ConversationBean> dataSource, View emptyView)
    {
        this.mDataSource = dataSource;
        conversationAdapter = new ConversationAdapter(mContext, mDataSource);
        LinearLayoutManager cManager = new LinearLayoutManager(mContext, LinearLayout.VERTICAL, false);
        setAdapter(conversationAdapter);
        setLayoutManager(cManager);
        conversationAdapter.setOnItemClickListener(new ConversationAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, final ConversationBean entity)
            {
                Observable.create(new ObservableOnSubscribe<Object>()
                {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Object> e) throws Exception
                    {
                        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(entity.getConversationId(), EMConversation.EMConversationType.Chat);
                        if (conversation != null)
                        {
                            List<EMMessage> messageList = conversation.getAllMessages();
                            for (EMMessage message : messageList)
                            {
                                //语音和视频要 听 或看 之后才算已读
                                if (message.getType() != EMMessage.Type.VOICE && message.getType() != EMMessage.Type.VIDEO)
                                    EaseCommonUtils.makeMessageAsRead(TAG + ".onItemClick", conversation, message, true);
                                else
                                    EaseCommonUtils.makeMessageAsRead(TAG + ".onItemClick", conversation, message, false);
                            }
                            entity.setUnreadMsgNumber(0);
                        }
                        e.onComplete();
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Object>()
                        {
                            @Override
                            public void onSubscribe(@NonNull Disposable d)
                            {
                                Logger.t(TAG).d("onSubscribe。。。。");
                            }

                            @Override
                            public void onNext(@NonNull Object o)
                            {
                                Logger.t(TAG).d("onNext。。。。");
                            }

                            @Override
                            public void onError(@NonNull Throwable e)
                            {
                                Logger.t(TAG).d("onError。。。。");
                            }

                            @Override
                            public void onComplete()
                            {
                                Logger.t(TAG).d("刷新数据。。。。");
                                conversationAdapter.notifyDataSetChanged();
                            }
                        });

                if (itemClickListener != null && CommonUtils.isInLiveRoom)
                {
                    itemClickListener.onItemClicked(view, entity);
                }
                else
                {
                    Intent intent = new Intent(mContext, CChatActivity.class);
                    EaseUser eUser = new EaseUser(entity.getHxId());
                    eUser.setNickName(entity.getNickName());
                    eUser.setuId(entity.getuId());
                    eUser.setAvatar(entity.getHeadImage());
                    eUser.setRemark(entity.getRemark());
                    eUser.setIsVuser(entity.getIsVUser());
                    eUser.setId(entity.getId());
                    eUser.setLevel(entity.getLevel());
                    eUser.setSex(entity.getGender());
                    eUser.setAge(entity.getAge());
                    intent.putExtra(Constant.EXTRA_TO_EASEUSER, eUser);
                    mContext.startActivity(intent);
                }
            }
        });
        conversationAdapter.setOnItemLongClickListener(new ConversationAdapter.OnItemLongClickListener()
        {
            @Override
            public void onItemLongClick(View view, final ConversationBean entity)
            {
                new ContextMenuDialog(new ContextMenuDialog.MenuDialogCallBack()
                {
                    @Override
                    public void menuOnClick(String menuItem, int position)
                    {
                        boolean isDeleteMsg = true;
/*                        if (position == 1)
                            isDeleteMsg = true;*/
                        deleteConversation(entity.getConversationId(), isDeleteMsg);
                    }
                }).showContextMenuBox(mContext, Arrays.asList(new String[]{"删除会话和消息"}));
            }
        });
        setEmptyView(emptyView);
    }

    public void init(List<ConversationBean> dataSource)
    {
        init(dataSource, null);
    }

    public void refreshUI()
    {
        conversationAdapter.notifyDataSetChanged();
    }

    /**
     * 删除会话
     *
     * @param userName      环信id
     * @param deleteMessage 是否连相关的消息也删除
     */

    private void deleteConversation(final String userName, final boolean deleteMessage)
    {
        Observable.create(new ObservableOnSubscribe<List<String>>()
        {
            @Override
            public void subscribe(ObservableEmitter<List<String>> e) throws Exception
            {
                if (deleteMessage == false)
                {
                    deleteConBaseType(userName, deleteMessage);
                    e.onComplete();
                }
                else
                {
                    List<String> redPacketIds = new ArrayList<>();
                    EMConversation c = EMClient.getInstance().chatManager().getConversation(userName, EMConversation.EMConversationType.Chat);
                    List<EMMessage> msgLst = new ArrayList<>();
                    if (c != null)
                        msgLst = c.getAllMessages();
                    for (EMMessage msg : msgLst)
                    {
                        //如果检测到接收的消息中有没有收取的红包，则提示
                        if (msg.getType() == EMMessage.Type.TXT
                                && msg.direct() == EMMessage.Direct.RECEIVE
                                && msg.getBooleanAttribute("is_money_msg", false) == true)
                        {
                            redPacketIds.add(msg.getStringAttribute(RedPacketConstant.EXTRA_RED_PACKET_ID, ""));
                        }
                    }
                    e.onNext(redPacketIds);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>()
                {
                    @Override
                    public void accept(List<String> redPacketIds) throws Exception
                    {
                        Logger.t(TAG).d("执行》" + redPacketIds);
                        checkRedPacketsStates(userName, redPacketIds);
                    }
                });
    }

    private void deleteConBaseType(final String userName, final boolean deleteMsg)
    {
        Observable.create(new ObservableOnSubscribe<Boolean>()
        {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception
            {
                boolean isDeleteSuc = EMClient.getInstance().chatManager().deleteConversation(userName, deleteMsg);
                if (isDeleteSuc && deleteMsg)//目前只有会话列表使用到了的本地存放的contact，所以可以在删除会话时候删除本地存储，如果以后在其他地方也用到了本地Contact就不能在此处删除了--wb
                    HuanXinIMHelper.getInstance().deleteContact(userName);
                e.onNext(isDeleteSuc);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>()
                {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception
                    {
                        if (aBoolean && mListener != null)// 更新主页上未读消息数目
                            mListener.onItemDeleted(userName);
                    }
                });
    }

    private void checkRedPacketsStates(final String userName, List<String> redPacketIds)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.streamId, new Gson().toJson(redPacketIds));
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                try
                {
                    JSONObject body = new JSONObject(response.getBody());
                    if ("true".equals(body.getString("flag")))//说明有没有领取的红包
                    {
                        new CustomAlertDialog(mContext)
                                .builder()
                                .setTitle("提示")
                                .setMsg("您有未领取的红包，是否确认清空所有聊天记录？")
                                .setPositiveButton("确定", new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        deleteConBaseType(userName, true);
                                    }
                                })
                                .setNegativeButton("取消", new OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {

                                    }
                                }).show();
                    }
                    else
                    {
                        deleteConBaseType(userName, true);
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                } catch (Exception e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        }, NetInterfaceConstant.UserC_checkRedList, null, reqParamMap);
    }

    private void checkIfEmpty()
    {
        if (emptyView != null && getAdapter() != null)
        {
            final boolean emptyViewVisible =
                    getAdapter().getItemCount() == 0;
            emptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
            setVisibility(emptyViewVisible ? GONE : VISIBLE);
        }
    }

    @Override
    public void setAdapter(Adapter adapter)
    {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null)
        {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null)
        {
            adapter.registerAdapterDataObserver(observer);
        }
        checkIfEmpty();
    }

    public void setEmptyView(View emptyView)
    {
        this.emptyView = emptyView;
        checkIfEmpty();
    }

    private final AdapterDataObserver observer = new AdapterDataObserver()
    {
        @Override
        public void onChanged()
        {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount)
        {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount)
        {
            checkIfEmpty();
        }
    };

    private IConversationListListener mListener;
    private ILiveConversationItemClickListener itemClickListener;

    public void setConversationListListener(IConversationListListener listener)
    {
        this.mListener = listener;
    }

    public interface IConversationListListener
    {
        void onItemDeleted(String hxId);
    }

    public void setOnLiveConversationItemClicked(ILiveConversationItemClickListener listener)
    {
        this.itemClickListener = listener;
    }

    public interface ILiveConversationItemClickListener
    {
        void onItemClicked(View view, ConversationBean entity);
    }

}

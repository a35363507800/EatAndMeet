package com.echoesnet.eatandmeet.views.widgets.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.GameAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.ChatCommonUtils;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.IMUtils.Constant;
import com.echoesnet.eatandmeet.views.adapters.ChatMessageAdapter2;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.ChatRow;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.jakewharton.rxbinding2.view.RxView;
import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * @author yqh
 * @version 1.0
 * @createDate 2017/11/15
 * @Description 游戏邀请 row
 */

public class ChatRowGame extends ChatRow
{

    private LinearLayout gameBottomView, gameAcceptView;
    private Button gameAccept, gameRefuse;
    private TextView tvGameState, gameTitle, gameDesc;

    public ChatRowGame(Context context, EMMessage message, int position, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter)
    {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView()
    {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_game : R.layout.ease_row_sent_game, this);
    }

    @Override
    protected void onFindViewById()
    {
        gameBottomView = findViewById(R.id.game_bottom_view);
        gameAcceptView = findViewById(R.id.game_accept_view);
        gameAccept = findViewById(R.id.game_button_accept);
        gameRefuse = findViewById(R.id.game_button_refuse);
        tvGameState = findViewById(R.id.game_state);
        gameTitle = findViewById(R.id.game_title);
        gameDesc = findViewById(R.id.game_desc);
    }

    @Override
    protected void onUpdateView()
    {
        adapter.notifyItemChanged(position);
    }

    @Override
    protected void onSetUpView()
    {
        String gameTitle = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_TITLE, "");
        String gameDesc = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_DES, "");
        if (!TextUtils.isEmpty(gameTitle) || !TextUtils.isEmpty(gameDesc))
        {
            this.gameTitle.setText(gameTitle);
            this.gameDesc.setText(gameDesc);
        }
        String gameState = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, "0");
        if (EamConstant.EAM_ATTR_GAME_STATE_SEND.equals(gameState))//发送中的状态去刷新是否过期
        {
            if (System.currentTimeMillis() - message.getMsgTime() > 1000 * 60 * 2)//大于两分钟的时候 消息做过期处理
            {
                gameState = EamConstant.EAM_ATTR_GAME_STATE_OVERDUE;
                message.setAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, gameState);
                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getFrom(), ChatCommonUtils.getConversationType(Constant.CHATTYPE_SINGLE), true);
                if (conversation != null)
                    conversation.updateMessage(message);
            }
        }
        Logger.t(TAG).d("chat------>chatRow onSetUpView gameState:" + gameState + " | position:" + position);
        boolean isSender = message.direct() == EMMessage.Direct.SEND;
        //消息状态 0：发送中 1：接受 2：拒绝 3：对战中 4：过期
        // *1  发送 ：0  ；   *3 拒绝  ：2  ；   4  过期  ：4   ；   *5 接受 ： 1 ；   6 进行中 ：3
        switch (gameState)
        {
            case EamConstant.EAM_ATTR_GAME_STATE_SEND:
                gameBottomView.setVisibility(VISIBLE);
                if (isSender)
                {
                    gameAcceptView.setVisibility(GONE);
                    tvGameState.setVisibility(VISIBLE);
                    tvGameState.setText("2分钟后失效，需重新邀请");
                }
                else
                {
                    gameAcceptView.setVisibility(VISIBLE);
                    tvGameState.setVisibility(GONE);
                }
                break;
            case EamConstant.EAM_ATTR_GAME_STATE_ACCEPT:
                gameBottomView.setVisibility(GONE);
//                if (!isSender)
            {
                int pdL = bubbleLayout.getPaddingLeft();
                int pdT = bubbleLayout.getPaddingTop();
                int pdR = bubbleLayout.getPaddingRight();
                int pdB = bubbleLayout.getPaddingBottom();
                bubbleLayout.setPadding(pdL, pdT, pdR, pdB + 10);
            }
            break;
            case EamConstant.EAM_ATTR_GAME_STATE_REFUSE://拒绝
                gameBottomView.setVisibility(VISIBLE);
                gameAcceptView.setVisibility(GONE);
                tvGameState.setVisibility(VISIBLE);
                if (isSender)
                    tvGameState.setText("对方拒绝了你的游戏邀请");
                else
                    tvGameState.setText("你拒绝了对方的游戏邀请");
                break;
            case EamConstant.EAM_ATTR_GAME_STATE_ONGOING://对战中
                gameBottomView.setVisibility(VISIBLE);
                gameAcceptView.setVisibility(GONE);
                tvGameState.setVisibility(VISIBLE);
                tvGameState.setText("您的好友已经开始和Ta对战了哦~");
                break;
            case EamConstant.EAM_ATTR_GAME_STATE_OVERDUE://过期
                gameBottomView.setVisibility(VISIBLE);
                gameAcceptView.setVisibility(GONE);
                tvGameState.setVisibility(VISIBLE);
//                if (isSender)
//                    tvGameState.setText("游戏邀请已过期");
//                else
                tvGameState.setText("游戏邀请已过期");
                break;
        }
        RxView.clicks(gameAccept)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>()
                {
                    @Override
                    public void accept(Object o) throws Exception
                    {
                        Logger.t(TAG).d("gameAccept.setOnClickListener :" + itemClickListener);
                        if (itemClickListener != null)
                            itemClickListener.onGameAcceptOrRefuseClick(true, position, message);
                    }
                });
        RxView.clicks(gameRefuse)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>()
                {
                    @Override
                    public void accept(Object o) throws Exception
                    {
                        Logger.t(TAG).d("gameRefuse.setOnClickListener :" + itemClickListener);
                        if (itemClickListener != null)
                            itemClickListener.onGameAcceptOrRefuseClick(false, position, message);
                    }
                });
        if (adapter != null)
            ((ChatMessageAdapter2) adapter).selectLast();
    }

    @Override
    protected void onBubbleClick()
    {
        String gameState = message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_STATE, "");
        Logger.t(TAG).d("chat------>点击游戏邀请时 消息状态：" + gameState);
        if (!EamConstant.EAM_ATTR_GAME_STATE_SEND.equals(gameState))//"3".equals(gameState) ||
        {
            if (!CommonUtils.isInLiveRoom)
            {
                Intent gameIntent = new Intent(activity, GameAct.class);
                gameIntent.putExtra("gameUrl", message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_URL, ""));
                gameIntent.putExtra("gameId", message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_ID, ""));
                activity.startActivity(gameIntent);
            }
            else
            {
                new CustomAlertDialog(context)
                        .builder()
                        .setTitle("提示 !")
                        .setMsg(context.getString(R.string.chat_game_live_tips))
                        .setCancelable(false)
                        .setPositiveButton("确认", (view) ->
                        {
                            Intent intent = new Intent(EamConstant.EAM_HX_RECEIVE_CLOSE_LIVE);
                            EamApplication.getInstance().sendBroadcast(intent);
                            Intent gameIntent = new Intent(activity, GameAct.class);
                            gameIntent.putExtra("gameUrl", message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_URL, ""));
                            gameIntent.putExtra("gameId", message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GAME_ID, ""));
                            activity.startActivity(gameIntent);
                        })
                        .setNegativeButton("取消", (view) ->
                        {
                        })
                        .show();
            }
        }
        Logger.t(TAG).d("chat------>消息点击时messageId：" + message.getMsgId());
    }

    @Override
    protected void onBubbleLongClick()
    {

    }
}

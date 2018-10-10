package com.echoesnet.eatandmeet.utils.LiveSharedUtil;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.ClubDetailAct;
import com.echoesnet.eatandmeet.activities.ColumnArticleDetailAct;
import com.echoesnet.eatandmeet.activities.GameAct;
import com.echoesnet.eatandmeet.activities.PromotionActionAct;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.FPromotionBean;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.chat.chatrow.ChatRow;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;

/**
 * Created by yqh on 2016/10/26.
 */

public class ChatRowLiveSharedAck extends ChatRow
{
    private TextView liveRoomName1, liveRoomName2, columnTitleTv;
    private RoundedImageView shareImageView;
    private ImageView columnRiv;
    private LinearLayout llNormalShare, llColumnShare;

    public ChatRowLiveSharedAck(Context context, EMMessage message, int position, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter)
    {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView()
    {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_live_shared : R.layout.ease_row_sent_live_shared, this);
    }

    @Override
    protected void onFindViewById()
    {
        llNormalShare = (LinearLayout) findViewById(R.id.ll_normal_share);
        llColumnShare = (LinearLayout) findViewById(R.id.ll_column_share);
        liveRoomName1 = (TextView) findViewById(R.id.tv_live_room_name);
        liveRoomName2 = (TextView) findViewById(R.id.share_live_room_name);
        columnTitleTv = (TextView) findViewById(R.id.tv_column_title);
        shareImageView = (RoundedImageView) findViewById(R.id.share_img);
        columnRiv = (ImageView) findViewById(R.id.riv_column);
    }

    @Override
    protected void onUpdateView()
    {

    }

    @Override
    protected void onSetUpView()
    {
        try
        {
            String shareTitle, shareContent, shareIcon, shareType;
            Logger.t("chatShare").d(message.getStringAttribute("shareType"));
            shareTitle = message.getStringAttribute("shareTitle");
            shareType = message.getStringAttribute("shareType");
            shareContent = message.getStringAttribute("shareContent");
            shareIcon = message.getStringAttribute("shareImageUrl");
            if ("column".equals(shareType))
            {
                llNormalShare.setVisibility(GONE);
                llColumnShare.setVisibility(VISIBLE);
//                String columnTitle = shareContent + " " + shareTitle;
//                SpannableString spannableString = new SpannableString(shareTitle);
//                spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context,R.color.C0313)),0,shareContent.length()
//                        , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(shareIcon)
                        .placeholder(R.drawable.qs_550_260)
                        .into(columnRiv);
                columnTitleTv.setText(shareTitle);
            }
            else
            {
                llNormalShare.setVisibility(VISIBLE);
                llColumnShare.setVisibility(GONE);
                if ("activity".equals(shareType) || "party".equals(shareType))
                    liveRoomName1.setVisibility(GONE);
                liveRoomName1.setText(Html.fromHtml(shareTitle));
                liveRoomName2.setText(Html.fromHtml(shareContent));
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(shareIcon)
                        .placeholder(R.drawable.userhead)
                        .centerCrop()
                        .into(shareImageView);
            }
        } catch (HyphenateException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onBubbleClick()
    {
        try
        {
            String shareType = message.getStringAttribute("shareType");
            if ("game".equals(shareType))
            {
                if (!CommonUtils.isInLiveRoom)
                {
                    Intent gameIntent = new Intent(activity, GameAct.class);
                    gameIntent.putExtra("gameUrl", message.getStringAttribute("shareUrl"));
                    gameIntent.putExtra("gameId", message.getStringAttribute("gameId"));
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
                                gameIntent.putExtra("gameUrl", message.getStringAttribute("shareUrl", ""));
                                gameIntent.putExtra("gameId", message.getStringAttribute("gameId", ""));
                                activity.startActivity(gameIntent);
                            })
                            .setNegativeButton("取消", (view) ->
                            {
                            })
                            .show();
                }
            }
            else if ("column".equals(shareType))
            {
                String columnId = message.getStringAttribute("columnId");
                CommonUtils.serverIncreaseRead(activity, columnId, new ICommonOperateListener()
                {
                    @Override
                    public void onSuccess(String response)
                    {
                        Logger.t(TAG).d("增加阅读量成功");
                    }

                    @Override
                    public void onError(String code, String msg)
                    {
                        Logger.t(TAG).d("增加阅读量失败 code:" + code + " | msg:" + msg);
                    }
                });
                Intent intent = new Intent(activity, ColumnArticleDetailAct.class);
                intent.putExtra("articleId", columnId);
                activity.startActivity(intent);
            }
            else if ("party".equals(shareType))
            {
                Logger.t(TAG).d("=====================12345========" + message.getBody());
                Intent intent = new Intent(activity, ClubDetailAct.class);
                intent.putExtra("clubId", message.getStringAttribute("phId"));
                activity.startActivity(intent);
            }
            else if ("activity".equals(shareType))
            {
                FPromotionBean pBean = new FPromotionBean();
                pBean.setWebUrl(message.getStringAttribute("shareUrl"));
                pBean.setType("2");
                pBean.setActivityId(message.getStringAttribute("activityId"));
                Intent intent = new Intent(activity, PromotionActionAct.class);
                intent.putExtra("fpBean", pBean);
                activity.startActivity(intent);
            }
            else
            {
                Logger.t(TAG).d(message.getStringAttribute("roomId") + "|" + message.getStringAttribute("shareImageUrl"));
                if (message.getStringAttribute("roomId").equals(SharePreUtils.getTlsName(context).substring(1)))
                {
                    ToastUtils.showShort("此房间为自己的直播间！");
                    return;
                }
                else if (CommonUtils.isInLiveRoom)
                {
                    ToastUtils.showShort("您当前正在直播间中，请先退出！");
                    return;
                }
                EamApplication.getInstance().livePage.put(message.getStringAttribute("roomId"), message.getStringAttribute("shareImageUrl"));
                CommonUtils.startLiveProxyAct(activity,
                        LiveRecord.ROOM_MODE_MEMBER,
                        "",
                        "",
                        "",
                        message.getStringAttribute("roomId"),
                        null, EamCode4Result.reqNullCode);
            }

        } catch (HyphenateException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("解析异常》》" + e.getMessage());
        }
//                livePlayIntent.putExtra("focus",anchorsListBean.getFocus());
    }

    @Override
    protected void onBubbleLongClick()
    {

    }
}

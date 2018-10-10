package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.ShareBean;
import com.echoesnet.eatandmeet.models.datamodel.AbstractEMMessageListener;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.presenters.ImpITrendsPlayVideoView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ITrendsPlayVideoView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.echoesnet.eatandmeet.views.widgets.VideoRedPacketPop;
import com.echoesnet.eatandmeet.views.widgets.video.EmptyControlVideo;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/14 0014
 * @description 视频播放页面
 */
public class TrendsPlayVideoAct extends MVPBaseActivity<TrendsPlayVideoAct, ImpITrendsPlayVideoView> implements ITrendsPlayVideoView, PlatformActionListener
{
    private static final String TAG = TrendsPlayVideoAct.class.getSimpleName();
    @BindView(R.id.lhv_head)
    LevelHeaderView headRiv;
    @BindView(R.id.trends_play_video_view)
    EmptyControlVideo uVideoView;
    @BindView(R.id.tv_nick_name)
    TextView nickNameTv;
    @BindView(R.id.icon_tv_sex)
    GenderView sexIconTv;
    @BindView(R.id.icon_tv_level)
    LevelView levelIconTv;
    @BindView(R.id.tv_distance)
    TextView distanceTv;
    @BindView(R.id.tv_focus)
    TextView focusTv;
    @BindView(R.id.seekbar_play)
    SeekBar seekBar;
    @BindView(R.id.rl_information)
    RelativeLayout informationRl;
    @BindView(R.id.icon_tv_play)
    IconTextView playIconTv;
    @BindView(R.id.img_start_or_stop)
    ImageView startOrStopImg;
    @BindView(R.id.img_full_screen)
    ImageView fullScreenImg;
    @BindView(R.id.img_close)
    ImageView closeImg;
    @BindView(R.id.img_thumbnail)
    ImageView imgThumbnail;
//    @BindView(R.id.smoothImageView)
//    SmoothImageView smoothImageView;

    private String playUrl, uid, isFocus, tId, showType;
    private int position;
    private final int UP_SEEKBAR = 101;
    private final int COMPLETE = 103;
    private PopupWindow popupWindow;
    private Activity mAct;
    private boolean isRestart = false;
    private boolean isComplete = false;
    private int currentPosition;
    private String isVuser;
    private String type;//播放类型 resBanner 餐厅banner页面进入
    private boolean showRedPacket = false; //视频播放完是否显示红包
    private VideoRedPacketPop videoRedPacketPop;
    private List<String> redList;
    private boolean isShare = false;
    private boolean hasGetRed = false;
    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_trends_play_video);
        ButterKnife.bind(this);
        mAct = this;
        initView();
    }

    private void initView()
    {
        type = getIntent().getStringExtra("type");
        playUrl = getIntent().getStringExtra("url");
        if ("resBanner".equals(type))
        {
            startOrStopImg.setVisibility(View.VISIBLE);
            fullScreenImg.setVisibility(View.VISIBLE);
            closeImg.setVisibility(View.VISIBLE);
        }

        //信息——————————————————————
        final String phUrl = getIntent().getStringExtra("phUrl");
        final String nickName = getIntent().getStringExtra("nickName");
        String distance = getIntent().getStringExtra("distance");
        String sex = getIntent().getStringExtra("sex");
        String level = getIntent().getStringExtra("level");
        String age = getIntent().getStringExtra("age");
        showType = getIntent().getStringExtra("showType");
        isFocus = getIntent().getStringExtra("isFocus");
        uid = getIntent().getStringExtra("uid");
        tId = getIntent().getStringExtra("tId");
        isVuser = getIntent().getStringExtra("isVuser");
        position = getIntent().getIntExtra("position", 0);

        if (SharePreUtils.getUId(mAct).equals(uid))
        {
            EamApplication.getInstance().isCheckRed = false;
            redList = new ArrayList<>();
            EMClient.getInstance().chatManager().addMessageListener(emMessageListener);
        }

        String thumbnailUrl = getIntent().getStringExtra(EamConstant.EAM_SHOW_IMG_URLS);
        int locationX = getIntent().getIntExtra(EamConstant.EAM_SHOW_IMG_LOCATION_X, 0);
        int locationY = getIntent().getIntExtra(EamConstant.EAM_SHOW_IMG_LOCATION_Y, 0);
        int width = getIntent().getIntExtra(EamConstant.EAM_SHOW_IMG_WIDTH, 0);
        int height = getIntent().getIntExtra(EamConstant.EAM_SHOW_IMG_HEIGHT, 0);
        informationRl.setVisibility(View.INVISIBLE);

        Logger.t(TAG).d("缩略图》》》" + thumbnailUrl);
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .centerCrop()
                .load(thumbnailUrl)
                .into(imgThumbnail);
       Logger.t(TAG).d("playUrl:" + playUrl + "| position =" + position + " |" + showType + " |thumbnailUrl>" + thumbnailUrl);
        if (TextUtils.isEmpty(nickName))
        {
            informationRl.setVisibility(View.INVISIBLE);
        }
        else
        {
            nickNameTv.setText(nickName);
            distanceTv.setText(distance);
            sexIconTv.setSex(age, sex);

            levelIconTv.setLevel(level, 1);
        }
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
        GSYVideoManager.instance().setNeedMute(false);
        uVideoView.setShowPauseCover(true);
        uVideoView.setVideoAllCallBack(new GSYSampleCallBack(){
            @Override
            public void onPrepared(String url, Object... objects)
            {
                super.onPrepared(url, objects);
                GSYVideoManager.instance().setNeedMute(false);
                Logger.t(TAG).d("onPrepared>>>>>>>>>>> position>>" + position);
                        isComplete = false;
                        if (!TextUtils.isEmpty(nickName))
                            informationRl.setVisibility(View.VISIBLE);
                            headRiv.setHeadImageByUrl(phUrl);
                            headRiv.showRightIcon(isVuser);
                        playIconTv.setVisibility(View.GONE);
                if (!isRestart && !"resBanner".equals(type))
                    uVideoView.setSeekOnStart(position);
                seekBar.setMax((int) uVideoView.getDuration());
                        handler.sendEmptyMessage(UP_SEEKBAR);
                        startOrStopImg.setImageResource(R.drawable.stop_video);
                imgThumbnail.setVisibility(View.GONE);
            }

            @Override
            public void onAutoComplete(String url, Object... objects)
            {
                super.onAutoComplete(url, objects);
                        isComplete = true;
                        seekBar.setProgress(seekBar.getMax());
                        if (!"resBanner".equals(type))
                            playIconTv.setVisibility(View.VISIBLE);
                        startOrStopImg.setImageResource(R.drawable.play_video);
                        if (showRedPacket)
                            handler.sendEmptyMessage(COMPLETE);
                        imgThumbnail.setVisibility(View.VISIBLE);
                }
        });
        if (!TextUtils.isEmpty(playUrl))
        {
            uVideoView.setUp(playUrl,true,"");
            uVideoView.setShowPauseCover(true);
            uVideoView.startPlayLogic();
        }
        uVideoView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if ("resBanner".equals(type))
                    return;
                uVideoView.onVideoPause();
                Logger.t(TAG).d("position" + (isComplete ? 0 : uVideoView.getPlayPosition()));
                Intent intent = new Intent();
                intent.putExtra("isFocus", isFocus);
                intent.putExtra("tId", tId);
                intent.putExtra("position", isComplete ? 0 : uVideoView.getPlayPosition());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        if (!SharePreUtils.getUId(mAct).equals(uid) && !TextUtils.isEmpty(uid))
        {
            if (popupWindow == null || !popupWindow.isShowing())
                mPresenter.getUsersRelationship(uid);
        }
        else
            focusTv.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(tId))
            mPresenter.sendRed(tId);
//        uVideoView.setVisibility(View.INVISIBLE);
    }

    private Handler handler = new Handler(new Handler.Callback()
    {
        @Override
        public boolean handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case UP_SEEKBAR:
                    if (uVideoView != null)
                    {
                        seekBar.setProgress((int) uVideoView.getCurrentPositionWhenPlaying());
                        handler.sendEmptyMessageDelayed(UP_SEEKBAR, 100);
                    }
                    break;
                case 102:
                    if (popupWindow != null && !isFinishing() && !popupWindow.isShowing())
                    {
                        popupWindow.showAsDropDown(focusTv, -CommonUtils.dp2px(mAct, 43), 0);
                    }
                    break;
                case COMPLETE:
                    if (videoRedPacketPop == null)
                        videoRedPacketPop = new VideoRedPacketPop(mAct);
                    videoRedPacketPop.setVideoRedPacketClick(new VideoRedPacketPop.VideoRedPacketClick()
                    {
                        @Override
                        public void openRedClick()
                        {
                            if (!CommonUtils.isFastDoubleClick() && !hasGetRed)
                            {
                                mPresenter.getRed(tId);
                                hasGetRed = true;
                            }
                        }

                        @Override
                        public void shareClick()
                        {
                            isShare = true;
                        }
                    });
                    if (!videoRedPacketPop.isShowing())
                        videoRedPacketPop.showPopupWindow(((ViewGroup) mAct.findViewById(android.R.id.content)).getChildAt(0));
                    videoRedPacketPop.setOnDismissListener(new PopupWindow.OnDismissListener()
                    {
                        @Override
                        public void onDismiss()
                        {
                            videoRedPacketPop.backgroundAlpha(1);
                            if (!EamApplication.getInstance().isCheckRed && redList != null)
                            {
                                for (String s : redList)
                                {
                                    try
                                    {
                                        JSONObject jsonObject = new JSONObject(s);
                                        if ("1".equals(jsonObject.getString("red")))
                                        {
                                            Intent intent = new Intent(EamApplication.getInstance(), RedPacketShowAct.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                            intent.putExtra("content", jsonObject.getString("content"));
                                            intent.putExtra("income", jsonObject.getString("income"));
                                            EamApplication.getInstance().startActivity(intent);
                                        }
                                    } catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                                redList.clear();
                            }
                            EamApplication.getInstance().isCheckRed = true;
                        }
                    });
                    showRedPacket = false;
                    break;
            }
            return false;
        }
    });

    @OnClick({R.id.tv_focus, R.id.icon_tv_play, R.id.img_close, R.id.img_start_or_stop, R.id.img_full_screen})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tv_focus:
                mPresenter.focusUser(uid, "1");
                break;
            case R.id.icon_tv_play:
                if (uVideoView != null)
                {
                    isRestart = true;
                    uVideoView.setSeekOnStart(0);
                    uVideoView.startPlayLogic();
                }
                break;
            case R.id.img_close:
                finish();
                break;
            case R.id.img_full_screen:
                finish();
                break;
            case R.id.img_start_or_stop:
                if (uVideoView.getCurrentState() == GSYVideoView.CURRENT_STATE_PLAYING)
                {
                    uVideoView.onVideoPause();
                    startOrStopImg.setImageResource(R.drawable.play_video);
                }
                else if (uVideoView.getCurrentState() == GSYVideoView.CURRENT_STATE_PAUSE)
                {
                    uVideoView.onVideoResume();
                    startOrStopImg.setImageResource(R.drawable.stop_video);
                }
                else
                {
                    startOrStopImg.setImageResource(R.drawable.stop_video);
                    uVideoView.startPlayLogic();
                }
                break;
        }
    }

    private EMMessageListener emMessageListener = new AbstractEMMessageListener()
    {
        @Override
        public void onCmdMessageReceived(List<EMMessage> messages)
        {
            super.onCmdMessageReceived(messages);
            for (EMMessage message : messages)
            {
                String action = "";
                EMMessageBody cmdMsgBody = message.getBody();
                Logger.t(TAG).d("接到了后台推送的红点信息" + messages.size() + "///" + messages.toString() + "///" + cmdMsgBody);
                if (cmdMsgBody instanceof EMCmdMessageBody)
                {
                    action = ((EMCmdMessageBody) cmdMsgBody).action();
                }
                if (EamConstant.EAM_SINGLES_DAY.equals(action) && !EamApplication.getInstance().isCheckRed)
                {
                    mPresenter.getMyRedIncome();
                }
                Logger.t(TAG).d(String.format("Command：action:%s,message:%s", action, message.toString()));
            }
        }
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
        if (uVideoView != null)
        {
            uVideoView.release();
            uVideoView = null;
        }
        EamApplication.getInstance().isCheckRed = true;
        if (SharePreUtils.getUId(mAct).equals(uid))
            EMClient.getInstance().chatManager().removeMessageListener(emMessageListener);
    }

    @Override
    protected ImpITrendsPlayVideoView createPresenter()
    {
        return new ImpITrendsPlayVideoView();
    }

    @Override
    public void focusCallBack()
    {
        ToastUtils.showShort("关注成功");
        isFocus = "1";
        focusTv.setBackgroundResource(R.drawable.round_stroke_0323_bg);
        focusTv.setTextColor(ContextCompat.getColor(mAct, R.color.C0323));
        focusTv.setText("已关注");
        focusTv.setEnabled(false);
    }

    @Override
    public void getIsFocusCallback(String focus)
    {
        isFocus = focus;
        if ("0".equals(focus))
        {
            focusTv.setVisibility(View.VISIBLE);
            focusTv.setBackgroundResource(R.drawable.round_c0313_bg_live_connect);
            focusTv.setTextColor(ContextCompat.getColor(mAct, R.color.C0313));
            focusTv.setText("+关注");
            focusTv.setEnabled(true);
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setText("喜欢TA的视频,就关注\n一下TA吧~");
            textView.setBackgroundResource(R.drawable.video_browsing);
            textView.setTextColor(ContextCompat.getColor(this, R.color.C0324));
            textView.setGravity(Gravity.CENTER);
            linearLayout.addView(textView);
            if (popupWindow == null)
                popupWindow = new PopupWindow(linearLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setBackgroundDrawable(new ColorDrawable());
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setContentView(linearLayout);
            if ("0".equals(isFocus))
                handler.sendEmptyMessageDelayed(102, 700);
        }
        else if ("1".equals(focus))
        {
            focusTv.setBackgroundResource(R.drawable.round_stroke_0323_bg);
            focusTv.setTextColor(ContextCompat.getColor(mAct, R.color.C0323));
            focusTv.setText("已关注");
            focusTv.setEnabled(false);
        }
    }

    @Override
    public void sendRedCallback(String red)
    {
        showRedPacket = "1".equals(red);
    }

    @Override
    public void getRedCallback(String response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            String red = jsonObject.getString("red");
            String amount = "";
            if ("1".equals(red))
            {
                amount = jsonObject.getString("amount");
                ShareBean shareBean = new ShareBean();
                shareBean.setShareType(Platform.SHARE_WEBPAGE);
                shareBean.setShareTitle(jsonObject.getString("title"));
                shareBean.setShareContent(jsonObject.getString("desc"));
                shareBean.setShareImgUrl(jsonObject.getString("icon"));
                shareBean.setShareUrl(jsonObject.getString("url"));
                shareBean.setShareWeChatMomentsTitle(jsonObject.getString("title"));
                shareBean.setShareWeChatMomentsContent(jsonObject.getString("desc"));
                shareBean.setShareListener(this);
                if (videoRedPacketPop != null)
                    videoRedPacketPop.setShareInfo(shareBean);
            }
            videoRedPacketPop.showRedPacket(red,jsonObject.optString("content",""),amount,
                    jsonObject.optString("income",""));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void shareRedCallback(String amount, String income)
    {
        if (videoRedPacketPop != null)
            videoRedPacketPop.showAfterShare(amount,income);
    }

    @Override
    public void getMyRedInComeCallback(String response)
    {
        if (redList != null)
            redList.add(response);
    }

    @Override
    public void onError(String sign)
    {
        if (NetInterfaceConstant.SinglesDayC_getRed.equals(sign))
            hasGetRed = false;
    }


    @Override
    protected void onPause()
    {
        super.onPause();
//        uVideoView.onPause();
        if (uVideoView != null )
        {
            uVideoView.onVideoPause();
            currentPosition = (int) uVideoView.getPlayPosition();
        }
    }

    @Override
    protected void onResume()
    {
        if (isShare)
        {
            isShare = false;
            mPresenter.shareRed(tId);
        }
        super.onResume();
        if (uVideoView != null )
            uVideoView.onVideoResume();
        if (currentPosition > 0 && uVideoView != null && videoRedPacketPop == null)
            uVideoView.seekTo(currentPosition);
        if (videoRedPacketPop != null)
            videoRedPacketPop.setFocusable(true);
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(0, R.anim.browser_exit_anim);
    }

    @Override
    public void onBackPressed()
    {
//        uVideoView.pause();
        Intent intent = new Intent();
        intent.putExtra("isFocus", isFocus);
        intent.putExtra("tId", tId);
        intent.putExtra("position", isComplete ? 0 : uVideoView.getPlayPosition());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap)
    {
        Logger.t(TAG).d("分享成功");
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable)
    {
        Logger.t(TAG).d("分享失败" + throwable.getMessage() + i);
    }

    @Override
    public void onCancel(Platform platform, int i)
    {
    }
}

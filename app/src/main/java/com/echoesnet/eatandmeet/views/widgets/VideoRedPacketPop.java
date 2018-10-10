package com.echoesnet.eatandmeet.views.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.ShareBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.mob.MobSDK;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import uk.co.senab.photoview.log.Logger;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/10/27 0027
 * @description
 */
public class VideoRedPacketPop extends PopupWindow
{
    @BindView(R.id.fl_get_red)
    FrameLayout flGetRed;
    @BindView(R.id.tv_get_red)
    TextView tvGetRed;
    @BindView(R.id.ll_red_packet_open)
    LinearLayout llRedPacketOpen;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_total_income)
    TextView tvTotalIncome;
    @BindView(R.id.img_share)
    ImageView imgShare;
    @BindView(R.id.ll_share)
    LinearLayout llShare;
    @BindView(R.id.img_share_we_chat)
    ImageView imgShareWeChat;
    @BindView(R.id.img_share_friends)
    ImageView imgShareFriends;
    @BindView(R.id.rl_red_packet_no)
    RelativeLayout rlRedPacketNo;
    @BindView(R.id.tv_no_red_packet_des)
    TextView tvRedPacketNoDes;
    @BindView(R.id.tv_no_red_total_income)
    TextView tvRedPacketTotalIncome;
    @BindView(R.id.tv_red_packet_open_des)
    TextView tvRedPacketOpenDes;

    private Activity mAct;
    private VideoRedPacketClick videoRedPacketClick;
    private ShareBean shareInfo;
    public VideoRedPacketPop(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    public VideoRedPacketPop(Activity activity)
    {
        mAct = activity;
        initPop();
    }

    public void setVideoRedPacketClick(VideoRedPacketClick videoRedPacketClick)
    {
        this.videoRedPacketClick = videoRedPacketClick;
    }

    public void setShareInfo(ShareBean shareInfo)
    {
        this.shareInfo = shareInfo;
    }

    private void initPop()
    {
        View popupView = LayoutInflater.from(mAct).inflate(R.layout.pop_video_red_packet,null);
        // 设置SelectPicPopupWindow的View
        this.setContentView(popupView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(CommonUtils.getScreenSize(mAct).width);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setOutsideTouchable(false);
        this.setFocusable(true);
//        this.setAnimationStyle(R.style.PopupAnimation);
        this.backgroundAlpha(0.5f);
        //让pop可以点击外面消失掉
        this.setBackgroundDrawable(new ColorDrawable(0));
        this.setTouchInterceptor(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
                {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        setOnDismissListener(() -> {
            backgroundAlpha(1);
        });
        ButterKnife.bind(this,popupView);
    }



    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = mAct.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        mAct.getWindow().setAttributes(lp);
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent)
    {
        if (!this.isShowing())
        {
            llRedPacketOpen.setVisibility(View.GONE);
            llShare.setVisibility(View.GONE);
            rlRedPacketNo.setVisibility(View.GONE);
            flGetRed.setVisibility(View.VISIBLE);
            this.showAtLocation(parent, Gravity.CENTER, 0, 0);
        }
    }

    @OnClick({R.id.tv_get_red,R.id.img_share,R.id.img_share_we_chat,R.id.img_share_friends,R.id.tv_close})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tv_get_red:
                if (videoRedPacketClick != null)
                    videoRedPacketClick.openRedClick();
                break;
            case R.id.img_share:
                flGetRed.setVisibility(View.GONE);
                llRedPacketOpen.setVisibility(View.GONE);
                llShare.setVisibility(View.VISIBLE);
                break;
            case R.id.img_share_we_chat:
                share2WeChat();
                if (videoRedPacketClick != null)
                    videoRedPacketClick.shareClick();
                break;
            case R.id.img_share_friends:
                share2Friends();
                if (videoRedPacketClick != null)
                    videoRedPacketClick.shareClick();
                break;
            case R.id.tv_close:
                dismiss();
                break;
        }
    }

    /**
     * 领取红包后调用显示红包内容
     * @param red 0肯定没钱 1有钱
     * @param content 没钱的时候展示的一句话
     * @param amount 有钱的时候获得的金额
     * @param income 收益
     */
    public void  showRedPacket(String red,String content,String amount,String income)
    {
        flGetRed.setVisibility(View.GONE);
        if ("0".equals(red))
        {
            llRedPacketOpen.setVisibility(View.GONE);
            rlRedPacketNo.setVisibility(View.VISIBLE);
            tvRedPacketNoDes.setText(content);
            tvRedPacketTotalIncome.setText(String.format("当前累计收益: %s元", income));
        }else {
            rlRedPacketNo.setVisibility(View.GONE);
            llRedPacketOpen.setVisibility(View.VISIBLE);
            String money = amount + "元";
            Spannable spannable = new SpannableString(money);
            spannable.setSpan(new AbsoluteSizeSpan(CommonUtils.dp2px(mAct,50)),0,amount.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),0,amount.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvMoney.setText(spannable);
            tvTotalIncome.setText(String.format("当前累计收益: %s元", income));
        }
    }

    /**
     * 分享后红包展示
     * @param amount 金额
     * @param income 累计收益
     */
    public void showAfterShare(String amount,String income){
        llShare.setVisibility(View.GONE);
        flGetRed.setVisibility(View.GONE);
        llRedPacketOpen.setVisibility(View.VISIBLE);
        imgShare.setVisibility(View.GONE);
        String money = amount + "元";
        Spannable spannable = new SpannableString(money);
        spannable.setSpan(new AbsoluteSizeSpan(CommonUtils.dp2px(mAct,50)),0,amount.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),0,amount.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvMoney.setText(spannable);
        tvTotalIncome.setText(String.format("当前累计收益: %s元", income));
        tvRedPacketOpenDes.setText("该红包为分享红包");
    }


    private void share2Friends()
    {
        if (shareInfo==null)
            return;
        MobSDK.init(mAct);
        Platform weChatMoments = ShareSDK.getPlatform(WechatMoments.NAME);
        if (weChatMoments.isClientValid())
        {
            WechatMoments.ShareParams shareParams = new WechatMoments.ShareParams();
            shareParams.setShareType(shareInfo.getShareType());
            shareParams.setTitle(shareInfo.getShareWeChatMomentsTitle());
            shareParams.setUrl(shareInfo.getShareUrl());
            shareParams.setText(shareInfo.getShareContent());
            if (!TextUtils.isEmpty(shareInfo.getShareImgUrl()))
                shareParams.setImageUrl(shareInfo.getShareImgUrl());
            else
                shareParams.setImageData(BitmapFactory.decodeResource(mAct.getResources(), R.mipmap.ic_launcher));
            weChatMoments.setPlatformActionListener(shareInfo.getShareListener());
            weChatMoments.share(shareParams);
        }
        else
        {
            ToastUtils.showShort("请先安装微信客户端");
        }
    }

    private void share2WeChat()
    {
        if (shareInfo==null)
            return;
        MobSDK.init(mAct);
        Platform weChat = ShareSDK.getPlatform(Wechat.NAME);
        if (weChat.isClientValid())
        {
            Wechat.ShareParams sp = new Wechat.ShareParams();
            sp.setShareType(shareInfo.getShareType());
            sp.setTitle(shareInfo.getShareTitle());
            sp.setUrl(shareInfo.getShareUrl());
            sp.setText(shareInfo.getShareContent());
            if (!TextUtils.isEmpty(shareInfo.getShareImgUrl()))
                sp.setImageUrl(shareInfo.getShareImgUrl());
            else
                sp.setImageData(BitmapFactory.decodeResource(mAct.getResources(), R.mipmap.ic_launcher));
            weChat.setPlatformActionListener(shareInfo.getShareListener());
            weChat.share(sp);
        }
        else
        {
            ToastUtils.showShort("请先安装微信客户端");
        }
    }

    public interface VideoRedPacketClick{
        void openRedClick();
        void shareClick();
    }
}

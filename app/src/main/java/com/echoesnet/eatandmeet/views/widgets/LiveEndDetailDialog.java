package com.echoesnet.eatandmeet.views.widgets;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.activities.liveplay.Presenter.LivePresenter;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.LiveEnterRoomBean;
import com.echoesnet.eatandmeet.models.bean.ShareToFaceBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
 * Created by an on 2017/6/10 0010.
 */

public class LiveEndDetailDialog extends DialogFragment
{
    private Activity mAct;
    private TextView mDetailAdmires, mDetailTime, mDetailWatchCount;
    private ImageView liveRoomCoverImg;
    private RadioButton shared2Friend, shared2Sina, shared2weChatFriend, shared2weChatMoment, shared2QQ, shared2QZone;
    private Button tvCancel;
    private LivePresenter livePresenter;
    private String sec, meal;
    private boolean isShowing = false;

    public LiveEndDetailDialog()
    {
        setCancelable(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAct = getActivity();
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.dialog_live_detail, container);
        mDetailTime = (TextView) view.findViewById(R.id.tv_time);//直播时间
        mDetailAdmires = (TextView) view.findViewById(R.id.tv_admires);//饭票
        mDetailWatchCount = (TextView) view.findViewById(R.id.tv_members);//观看人数
        shared2Friend = (RadioButton) view.findViewById(R.id.iv_shared_to_friend);
        shared2Sina = (RadioButton) view.findViewById(R.id.iv_shared_to_sina);
        shared2weChatFriend = (RadioButton) view.findViewById(R.id.iv_shared_to_weChatFriend);
        shared2weChatMoment = (RadioButton) view.findViewById(R.id.iv_shared_to_weChatMoments);
        shared2QQ = (RadioButton) view.findViewById(R.id.iv_shared_to_QQ);
        shared2QZone = (RadioButton) view.findViewById(R.id.iv_shared_to_QZone);
        liveRoomCoverImg = (ImageView) view.findViewById(R.id.img_live_room_cover);

        tvCancel = (Button) view.findViewById(R.id.btn_cancel);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                dismissAllowingStateLoss();
                getActivity().finish();
                return false;
            }
        });
        initView();
        return view;
    }

    private void initView()
    {
        LiveEnterRoomBean enterRoomBean = livePresenter.getmRecord().getEnterRoom4EH();
        final ShareToFaceBean bean = new ShareToFaceBean();
        bean.setShareTitle(enterRoomBean.getHn());
        bean.setShareTitleUrl(NetHelper.LIVE_SHARE_ADDRESS + livePresenter.getmRecord().getRoomId());
        bean.setShareUrl(NetHelper.LIVE_SHARE_ADDRESS + livePresenter.getmRecord().getRoomId());
        bean.setShareAppImageUrl(NetHelper.LIVE_SHARE_PIC);
        bean.setShareType(Platform.SHARE_WEBPAGE);
        bean.setShareSite("看脸吃饭");
        bean.setShareSiteUrl(NetHelper.LIVE_SHARE_ADDRESS + livePresenter.getmRecord().getRoomId());
        bean.setOpenSouse("liveShare");// 传递房间参数到看脸好友界面
        bean.setRoomName(enterRoomBean.getNicName());
        bean.setShareImgUrl(enterRoomBean.getAnph());
        bean.setRoomId(livePresenter.getmRecord().getRoomId());
        bean.setUid(enterRoomBean.getuId());
        switch (livePresenter.getmRecord().getModeOfRoom())
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
        bean.setShareListener(listner);

        View.OnClickListener listener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                switch (view.getId())
                {
                    case R.id.iv_shared_to_weChatFriend:
                        if (CommonUtils.isFastDoubleClick())
                            return;
                        CommonUtils.shareWithApp(mAct, "微信好友", bean);
                        break;
                    case R.id.iv_shared_to_weChatMoments:
                        if (CommonUtils.isFastDoubleClick())
                            return;
                        CommonUtils.shareWithApp(mAct, "微信朋友圈", bean);
                        break;
                    case R.id.iv_shared_to_friend:
                        if (CommonUtils.isFastDoubleClick())
                            return;
                        CommonUtils.shareWithApp(mAct, "看脸好友", bean);
                        break;
                    case R.id.iv_shared_to_sina:
                        if (CommonUtils.isFastDoubleClick())
                            return;
                        CommonUtils.shareWithApp(mAct, "新浪微博", bean);
                        break;
                    case R.id.iv_shared_to_QQ:
                        if (CommonUtils.isFastDoubleClick())
                            return;
                        CommonUtils.shareWithApp(getActivity(), "QQ好友", bean);
                        break;
                    case R.id.iv_shared_to_QZone:
                        if (CommonUtils.isFastDoubleClick())
                            return;
                        CommonUtils.shareWithApp(getActivity(), "QQ空间", bean);
                        break;
                    case R.id.btn_cancel:
                        getActivity().finish();
                        break;
                    default:
                        break;
                }
            }
        };
        shared2weChatFriend.setOnClickListener(listener);
        shared2weChatMoment.setOnClickListener(listener);
        shared2Friend.setOnClickListener(listener);
        shared2Sina.setOnClickListener(listener);
        shared2QQ.setOnClickListener(listener);
        shared2QZone.setOnClickListener(listener);
        tvCancel.setOnClickListener(listener);
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(enterRoomBean.getAnph())
                .centerCrop()
                .skipMemoryCache(false)
                .into(liveRoomCoverImg);
        mDetailTime.setText(sec);
        mDetailAdmires.setText(meal);
        mDetailWatchCount.setText(String.valueOf(livePresenter.getmRecord().getWatchHighCount()) + " 人");
    }

    //分享回调监听
    PlatformActionListener listner = new PlatformActionListener()
    {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap)
        {
            mAct.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    ToastUtils.showShort("分享成功");
                }
            });
            NetHelper.addLiveShareCount(mAct);
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable)
        {
            if (throwable instanceof cn.sharesdk.tencent.qzone.QQClientNotExistException)
            {
                mAct.runOnUiThread(new Runnable()
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
                mAct.runOnUiThread(new Runnable()
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
            mAct.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    ToastUtils.showShort("分享取消");
                }
            });
        }
    };

    public void showDialog(FragmentManager manager, LivePresenter livePresenter, String sec, String meal)
    {
        try
        {
            if (isShowing)
                return;
            this.livePresenter = livePresenter;
            this.sec = sec;
            this.meal = meal;
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, "liveEndDetailDialog");
            ft.commitAllowingStateLoss();
            isShowing = true;
        }catch (Exception e){

        }

    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        isShowing = false;
        super.onDismiss(dialog);
    }
}

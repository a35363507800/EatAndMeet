package com.echoesnet.eatandmeet.views.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.activities.liveplay.Presenter.LivePresenter;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.LiveEnterRoomBean;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by an on 2017/6/9 0009.
 */

public class LiveEndDialog extends Dialog
{
    private IconTextView itvBack,itvFocus;
    private GenderView itvConnectAge;
    private TextView liveFinishNicName;
    private LevelView levelView;
    private LevelHeaderView rivHead;
    private ImageView imgLiveFinishCover;
    private LivePresenter livePresenter;
    private Activity mAct;

    public LiveEndDialog( Activity activity , LivePresenter livePresenter)
    {
        super(activity, R.style.dialog);
        this.livePresenter = livePresenter;
        this.mAct = activity;
        initDialog();
    }

    private void initDialog()
    {
        setContentView(R.layout.dialog_live_finish);
        itvBack = (IconTextView) findViewById(R.id.btn_back);
        itvFocus = (IconTextView) findViewById(R.id.btn_addFocus);
        liveFinishNicName = (TextView) findViewById(R.id.tv_nick_name);
        itvConnectAge = (GenderView) findViewById(R.id.tv_connect_age);
        levelView = (LevelView) findViewById(R.id.level_view);
        rivHead = (LevelHeaderView) findViewById(R.id.level_header_view);
        imgLiveFinishCover = (ImageView) findViewById(R.id.img_live_finish_cover);
        setCancelable(false);
        View.OnClickListener listener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                switch (view.getId())
                {
                    case R.id.btn_back:
                        if (isShowing())
                        {
                            mAct.finish();
//                            mAct.overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
                        }
                        break;
                    case R.id.btn_addFocus:
                        livePresenter.followHost();
                        break;
                }
            }
        };
        itvBack.setOnClickListener(listener);
        itvFocus.setOnClickListener(listener);
        setOnKeyListener(new OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                switch (keyCode)
                {
                    case KeyEvent.KEYCODE_BACK:
                        if (isShowing())
                            dismiss();
                        mAct.finish();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void show()
    {
        if (!isShowing()){
            refreshData(livePresenter.getmRecord().getEnterRoom4EH());
            super.show();
        }
    }

    private void refreshData(LiveEnterRoomBean enterRoomBean)
    {
        // rivHead.setLiveState(false);
        // rivHead.setHeadImageByUrl(enterRoomBean.getPhUrl());
        // rivHead.setLevel(enterRoomBean.getAnchorLevel());
//        GlideApp.with(EamApplication.getInstance())
//                .asBitmap()
//                .load(enterRoomBean.getPhUrl())
//                .centerCrop()
//                .skipMemoryCache(false)
//                .into(rivHead);
        rivHead.setHeadImageByUrl(enterRoomBean.getPhUrl());
        rivHead.showRightIcon(enterRoomBean.getIsVuser());

        rivHead.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mAct, CNewUserInfoAct.class);
                intent.putExtra("toUId",livePresenter.getmRecord().getEnterRoom4EH().getuId());
                mAct.startActivity(intent);
            }
        });
        if (imgLiveFinishCover != null)
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(enterRoomBean.getAnph())
                    .centerCrop()
                    .skipMemoryCache(false)
                    .into(imgLiveFinishCover);
        liveFinishNicName.setText(enterRoomBean.getNicName());
        itvConnectAge.setSex(enterRoomBean.getAnchorAge(),enterRoomBean.getAnchorSex());
        levelView.setLevel(enterRoomBean.getAnchorLevel(),LevelView.USER);
        if (enterRoomBean.getFlag().equals("0"))
        {
            itvFocus.setText("{eam-s-spades} 加关注");
            itvFocus.setTextColor(ContextCompat.getColor(mAct, R.color.C0313));
            itvFocus.setBackgroundResource(R.drawable.round_c0313_bg_live_connect);
            itvFocus.setEnabled(true);
        }
        else
        {
            itvFocus.setText("{eam-e983} 已关注");
            itvFocus.setTextColor(ContextCompat.getColor(mAct, R.color.C0315));
            itvFocus.setBackgroundResource(R.drawable.round_c0315_bg_live_connect);
            itvFocus.setEnabled(false);
        }
    }

    /**
     * 刷新关注状态
     */
    public void refreshFocusStatus(){
        if (!isShowing())
            return;
        itvFocus.setText("{eam-e983} 已关注");
        itvFocus.setTextColor(ContextCompat.getColor(mAct, R.color.C0315));
        itvFocus.setBackgroundResource(R.drawable.round_c0315_bg_live_connect);
        itvFocus.setEnabled(false);
    }

}

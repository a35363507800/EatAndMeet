package com.echoesnet.eatandmeet.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.LiveGetRedPacketBean;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.joanzapata.iconify.widget.IconTextView;

/**
 * Created by Administrator on 2017/5/12.
 */

public class LiveRedPacketDialog
{

    /**
     * 领取红包弹出层(抢到红包)
     *
     * @param bean
     */
    public static void showGetRedPacket(Activity mActivity, final LiveGetRedPacketBean bean)
    {
        final Dialog dialog = new Dialog(mActivity, R.style.AlertDialogStyle);
        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_live_red_packet_yes, null);
        dialog.setContentView(contentView);

        LevelHeaderView lhvHead = (LevelHeaderView) contentView.findViewById(R.id.lhv_head);
        LevelView level = (LevelView) contentView.findViewById(R.id.lv_level);
        GenderView ivSex = (GenderView) contentView.findViewById(R.id.iv_sex);
        TextView nickName = (TextView) contentView.findViewById(R.id.tv_nick_name);
        TextView amount = (TextView) contentView.findViewById(R.id.tv_red_packet_money);
        IconTextView itvClose = (IconTextView) contentView.findViewById(R.id.itv_close);
        lhvHead.setHeadImageByUrl(bean.getPhUrl());
        lhvHead.showRightIcon(bean.getIsVuser());
        level.setLevel(bean.getLevel(), LevelView.USER);
        nickName.setText(bean.getNicName());
        amount.setText(bean.getAmount());
        ivSex.setSex(bean.getAge(), bean.getSex());

        itvClose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setCancelable(false);
    }

    /**
     * 领取红包弹出层(没抢到红包)
     */
    public static void showRedPacket(Activity mActivity, final LiveGetRedPacketBean bean)
    {
        final Dialog dialog = new Dialog(mActivity, R.style.AlertDialogStyle);
        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_live_red_packet_no, null);
        dialog.setContentView(contentView);

        LevelHeaderView lhvHead = (LevelHeaderView) contentView.findViewById(R.id.lhv_head);
        LevelView level = (LevelView) contentView.findViewById(R.id.lv_level);
        GenderView ivSex = (GenderView) contentView.findViewById(R.id.iv_sex);
        TextView nickName = (TextView) contentView.findViewById(R.id.tv_nick_name);
        IconTextView itvClose = (IconTextView) contentView.findViewById(R.id.itv_close);
        //TextView red_title = (TextView) contentView.findViewById(R.id.tv_red_title);
        //  lhvHead.setLevel(bean.getLevel());
        lhvHead.setHeadImageByUrl(bean.getPhUrl());
        lhvHead.showRightIcon(bean.getIsVuser());
        level.setLevel(bean.getLevel(), LevelView.USER);
        nickName.setText(bean.getNicName());
        //red_title.setText(showInfo);
        ivSex.setSex(bean.getAge(), bean.getSex());

        itvClose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setCancelable(false);
    }
}

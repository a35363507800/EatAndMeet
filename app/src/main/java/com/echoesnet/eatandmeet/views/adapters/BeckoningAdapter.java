package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.MeetPersonBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.widgets.cardSlidePanel.BaseCardAdapter;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.List;

/**
 * Created by an on 2017/1/11 0011.
 */

public class BeckoningAdapter extends BaseCardAdapter {
    private List<MeetPersonBean> mData;
    private Activity mActivity;

    public BeckoningAdapter(List<MeetPersonBean> mData, Activity mActivity) {
        this.mData = mData;
        this.mActivity = mActivity;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public int getCardLayoutId() {
        return R.layout.vpitem_find_meet;
    }

    @Override
    public void onBindData(int position, View cardview) {
        if (mData == null || mData.size() == 0) {
            return;
        }
        RoundedImageView imageView = (RoundedImageView) cardview.findViewById(R.id.iv_find_meet_img);
        View maskView = cardview.findViewById(R.id.maskView);
        TextView userNameTv = (TextView) cardview.findViewById(R.id.tv_find_meet_name);
        TextView cityTv = (TextView) cardview.findViewById(R.id.tv_find_meet_city);
        TextView tvGender = (TextView) cardview.findViewById(R.id.tv_find_meet_gender);
        TextView tvOldOrders = (TextView) cardview.findViewById(R.id.tv_find_meet_old_orders);
        TextView lastChuXian = (TextView) cardview.findViewById(R.id.last_chuxian);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) imageView.getLayoutParams();
        params.height = CommonUtils.getScreenWidth(mActivity)-55;
        imageView.setLayoutParams(params);
        MeetPersonBean itemData = mData.get(position);
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .placeholder(R.drawable.userhead)
                .error(R.drawable.userhead)
                .centerCrop()
                .load(itemData.getUpUrl())
                .into(imageView);
        userNameTv.setText(itemData.getNicName());
        if (itemData.getSex().equals("女"))
        {
            tvGender.setBackgroundResource(R.drawable.shape_round_2corner_15_mc5);
            tvGender.setText(String.format("%s %s", "{eam-e94f}", itemData.getAge()));
        }
        else
        {
            tvGender.setBackgroundResource(R.drawable.shape_round_2corner_15_mc7);
            tvGender.setText(String.format("%s %s", "{eam-e950}", itemData.getAge()));
        }

        cityTv.setText(itemData.getCity());
        if (!TextUtils.isEmpty(itemData.getFlag())&&itemData.getFlag().equals("1"))
        {
            if (!"".equals(itemData.getrPreName()) && itemData.getrPreName() != null)
            {
                lastChuXian.setVisibility(View.VISIBLE);
                tvOldOrders.setVisibility(View.VISIBLE);
                tvOldOrders.setText(itemData.getrPreName());
            }
            else
            {
                tvOldOrders.setVisibility(View.GONE);
                lastChuXian.setVisibility(View.GONE);
            }
        }
        else
        {
            tvOldOrders.setVisibility(View.GONE);
            lastChuXian.setVisibility(View.GONE);
        }

    }

    @Override
    public int getVisibleCardCount() {
        return 3;
    }
}

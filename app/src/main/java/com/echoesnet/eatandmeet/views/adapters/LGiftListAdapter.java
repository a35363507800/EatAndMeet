package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.GiftBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.List;

import static com.echoesnet.eatandmeet.R.id.tv_gift_number;

/**
 * Created by an on 2016/10/24 0024.
 */

public class LGiftListAdapter extends BaseAdapter {
    private Context mContext;
    private List<GiftBean> giftList;
    private int clickPosition = -1;
    private int startPositon;

    public LGiftListAdapter(Context mContext, List<GiftBean> giftList, int startPositon) {
        this.mContext = mContext;
        this.giftList = giftList;
        this.startPositon = startPositon;
    }

    //标记选中
    public void setClickPosition(int clickPosition) {
        this.clickPosition = clickPosition;
    }

    @Override
    public int getCount() {
        return 8;
    }

    @Override
    public Object getItem(int position) {
        return giftList.get(position + startPositon);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final GiftHolder giftHolder;
        int index = position + startPositon;
        if (convertView == null) {
            giftHolder = new GiftHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lgift_list, null, false);
            giftHolder.imgGiftIcon = (ImageView) convertView.findViewById(R.id.img_gift_icon);
            giftHolder.tvGiftName = (TextView) convertView.findViewById(R.id.tv_gift_name);
            giftHolder.tvGiftNumber = (TextView) convertView.findViewById(tv_gift_number);
            giftHolder.tvGiftPrice = (TextView) convertView.findViewById(R.id.tv_gift_price);
            giftHolder.rlGift = (RelativeLayout) convertView.findViewById(R.id.rl_gift_number);
            convertView.setTag(giftHolder);
        } else {
            giftHolder = (GiftHolder) convertView.getTag();
        }
        if (index < giftList.size()) {
            GiftBean giftbean = giftList.get(index);
            giftHolder.tvGiftPrice.setText(giftbean.getgPrice());
            giftHolder.tvGiftName.setText(giftbean.getgName());
            giftHolder.tvGiftNumber.setVisibility(View.GONE);

            String giftName = CommonUtils.toMD5(giftbean.getgUrl());
            File file = new File(NetHelper.getRootDirPath(mContext) + NetHelper.GIFT_FOLDER, giftName);
            if (file.exists() && !file.isDirectory()) {
                Logger.t("giftListAdapter").d("从file加载图片" + giftbean.getgName());
                GlideApp.with(EamApplication.getInstance())
                        .load(file)
                        .centerCrop()
                        .placeholder(R.drawable.userhead)
                        .error(R.drawable.userhead)
                        .into(giftHolder.imgGiftIcon);
            } else {
                Logger.t("giftListAdapter").d("从net加载图片" + giftbean.getgName());
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(giftbean.getgUrl())
                        .centerCrop()
                        .placeholder(R.drawable.userhead)
                        .error(R.drawable.userhead)
                        .into(giftHolder.imgGiftIcon);
            }

            if (clickPosition == position + startPositon) {
                giftHolder.rlGift.setBackground(mContext.getResources().getDrawable(R.drawable.rec_yellow_bg));
                giftHolder.tvGiftNumber.setVisibility(View.VISIBLE);
            } else {
                giftHolder.rlGift.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                giftHolder.tvGiftNumber.setVisibility(View.GONE);
            }
        }else
        {
            convertView.setVisibility(View.GONE);
        }
        return convertView;
    }


    class GiftHolder {
        TextView tvGiftNumber;
        TextView tvGiftName;
        TextView tvGiftPrice;
        ImageView imgGiftIcon;
        RelativeLayout rlGift;
    }
}

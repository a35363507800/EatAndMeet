package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.LAnchorsListBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

/**
 * @author an
 * @Description: 直播列表adapger
 * @time 2016/10/13 0013 14:07
 */

public class LAnchorsListAdapter extends BaseAdapter
{
    private Context mContext;
    private ArrayList<LAnchorsListBean> anchorsList;
    private String TAG = LAnchorsListAdapter.class.getSimpleName();

    public LAnchorsListAdapter(Context mContext, ArrayList<LAnchorsListBean> anchorsList)
    {
        this.mContext = mContext;
        this.anchorsList = anchorsList;
    }

    @Override
    public int getCount()
    {
        return anchorsList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return anchorsList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LAnchorsListBean anchorsListBean = anchorsList.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.litem_live_anchors_list, parent, false);
            viewHolder.rivHeader = (LevelHeaderView) convertView.findViewById(R.id.riv_header);
            viewHolder.imgLiveCover = (ImageView) convertView.findViewById(R.id.img_live_cover);
            viewHolder.rlLiveCover = convertView.findViewById(R.id.rl_live_cover);
            viewHolder.imgLiveSign = (ImageView) convertView.findViewById(R.id.img_live_sign);
            viewHolder.tvAnchorCity = (TextView) convertView.findViewById(R.id.tv_anchor_city);
            viewHolder.tvAnchorName = (TextView) convertView.findViewById(R.id.tv_anchor_name);
            viewHolder.tvRoomName = (TextView) convertView.findViewById(R.id.tv_room_name);
            viewHolder.tvWatchNum = (TextView) convertView.findViewById(R.id.live_watch_Num);
            viewHolder.tvAnchorSex = (GenderView) convertView.findViewById(R.id.iv_anchor_sex);
            viewHolder.tvLiveStatus = (TextView) convertView.findViewById(R.id.tv_live_status);
            viewHolder.levelView = (LevelView) convertView.findViewById(R.id.level_view);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.imgLiveCover.getLayoutParams();
            params.height = CommonUtils.getScreenSize((Activity) mContext).width;
            viewHolder.imgLiveCover.setLayoutParams(params);
            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) viewHolder.rlLiveCover.getLayoutParams();
            params1.height = CommonUtils.getScreenSize((Activity) mContext).width + CommonUtils.dp2px(mContext,65);
            viewHolder.rlLiveCover.setLayoutParams(params1);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        Logger.t(TAG).d(anchorsListBean.toString());

        viewHolder.rivHeader.setHeadImageByUrl(anchorsListBean.getUphUrl());
        viewHolder.rivHeader.showRightIcon(anchorsListBean.getIsVuser());
        viewHolder.imgLiveSign.setVisibility(View.GONE);
        viewHolder.tvLiveStatus.setVisibility(View.GONE);
        if (TextUtils.equals(anchorsListBean.getSign(), "1"))
        {
            viewHolder.imgLiveSign.setVisibility(View.VISIBLE);
        }
        if (TextUtils.equals(anchorsListBean.getStatus(), "1"))
        {
            viewHolder.tvLiveStatus.setText(Html.fromHtml("<font color='red'>• </font>直播中"));
            viewHolder.tvLiveStatus.setVisibility(View.VISIBLE);
            viewHolder.tvLiveStatus.setBackgroundResource(R.drawable.round_red_border_black_bg);
        }
        else
        {
            viewHolder.tvLiveStatus.setText(Html.fromHtml("<font color='green'>• </font>回 放"));
            viewHolder.tvLiveStatus.setVisibility(View.VISIBLE);
            viewHolder.tvLiveStatus.setBackgroundResource(R.drawable.round_green_border_black_bg);
        }
        if (!TextUtils.isEmpty(anchorsListBean.getAnchorTypeUrl()))
        {
            viewHolder.imgLiveSign.setVisibility(View.VISIBLE);
            CommonUtils.setImageFromFile(viewHolder.imgLiveSign, anchorsListBean.getAnchorTypeUrl(), NetHelper.getRootDirPath(mContext) + NetHelper.ANCHOR_TYPE_FOLDER);
        }
        else
        {
            switch (anchorsListBean.getAnchorType())
            {
                case "0":
                    viewHolder.imgLiveSign.setVisibility(View.VISIBLE);
                    viewHolder.imgLiveSign.setImageResource(R.drawable.ico_touxian_qmbz_xhdpi);
                    break;
                case "1":
                    viewHolder.imgLiveSign.setVisibility(View.VISIBLE);
                    viewHolder.imgLiveSign.setImageResource(R.drawable.ico_touxian_zybb_xhdpi);
                    break;
                case "2":
                    viewHolder.imgLiveSign.setVisibility(View.VISIBLE);
                    viewHolder.imgLiveSign.setImageResource(R.drawable.ico_touxian_jybb_xhdpi);
                    break;
                case "3":
                    viewHolder.imgLiveSign.setVisibility(View.VISIBLE);
                    viewHolder.imgLiveSign.setImageResource(R.drawable.ico_touxian_msdr_xhdpi);
                    break;
            }
        }

//        String headerImageUrl = CommonUtils.getThumbnailImageUrlByUCloud(anchorsListBean.getRoomUrl(), ImageDisposalType.THUMBNAIL,1,)
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(anchorsListBean.getRoomUrl())
                .centerCrop()
                .error(R.drawable.qs_zb)
                .placeholder(R.drawable.qs_zb)
                .into(viewHolder.imgLiveCover);
        viewHolder.levelView.setLevel(anchorsListBean.getLevel(), LevelView.USER);
        viewHolder.tvRoomName.setText(anchorsListBean.getRoomName());
        viewHolder.tvWatchNum.setText(String.valueOf((int) Double.parseDouble(TextUtils.isEmpty(anchorsListBean.getViewer()) ? "0" : anchorsListBean.getViewer())));
        viewHolder.tvAnchorName.setText(!TextUtils.isEmpty(anchorsListBean.getNicName()) ? anchorsListBean.getNicName() : "");
        viewHolder.tvAnchorCity.setText(anchorsListBean.getCity());
        if (!TextUtils.isEmpty(anchorsListBean.getSex()))
        {
            viewHolder.tvAnchorSex.setSex(anchorsListBean.getAge(), anchorsListBean.getSex());
        }
        return convertView;
    }

    public class ViewHolder
    {
        LevelHeaderView rivHeader;
        ImageView imgLiveCover;
        RelativeLayout rlLiveCover;
        ImageView imgLiveSign;
        TextView tvLiveStatus;
        TextView tvAnchorName;
        TextView tvRoomName;
        TextView tvWatchNum;
        GenderView tvAnchorSex;
        TextView tvAnchorCity;
        LevelView levelView;
    }
}

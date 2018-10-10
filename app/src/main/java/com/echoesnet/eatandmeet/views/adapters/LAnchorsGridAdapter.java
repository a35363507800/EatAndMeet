package com.echoesnet.eatandmeet.views.adapters;

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

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.LAnchorsListBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

/**
 * @author an
 * @Description: 直播列表adapger
 * @time 2016/10/13 0013 14:07
 */

public class LAnchorsGridAdapter extends BaseAdapter
{
    private Context mContext;
    private ArrayList<LAnchorsListBean> anchorsList;
    private String TAG = LAnchorsGridAdapter.class.getSimpleName();

    public LAnchorsGridAdapter(Context mContext, ArrayList<LAnchorsListBean> anchorsList)
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.litem_live_anchors_gd_list, parent, false);
            viewHolder.imgLiveCover = (RoundedImageView) convertView.findViewById(R.id.img_live_cover);
            viewHolder.imgLiveSign = (ImageView) convertView.findViewById(R.id.img_live_sign);
            viewHolder.tvAnchorCity = (TextView) convertView.findViewById(R.id.tv_anchor_city);
            viewHolder.tvRoomName = (TextView) convertView.findViewById(R.id.tv_room_name);
            viewHolder.tvWatchNum = (TextView) convertView.findViewById(R.id.live_watch_Num);
            viewHolder.tvLiveStatus = (TextView) convertView.findViewById(R.id.tv_live_status);
            viewHolder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nick_name);
            viewHolder.genderView =  convertView.findViewById(R.id.gender_view);
            viewHolder.levelView =  convertView.findViewById(R.id.level_view);
            viewHolder.rlLiveCover =  convertView.findViewById(R.id.rl_live_cover);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.rlLiveCover.getLayoutParams();
            params.height = CommonUtils.getScreenWidth(mContext)/2-30;
            viewHolder.rlLiveCover.setLayoutParams(params);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Logger.t(TAG).d(anchorsListBean.toString());
        viewHolder.imgLiveSign.setVisibility(View.GONE);
        viewHolder.tvLiveStatus.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(anchorsListBean.getAnchorTypeUrl())){
            viewHolder.imgLiveSign.setVisibility(View.VISIBLE);
            CommonUtils.setImageFromFile(viewHolder.imgLiveSign,anchorsListBean.getAnchorTypeUrl(), NetHelper.getRootDirPath(mContext)+NetHelper.ANCHOR_TYPE_FOLDER);
        }else {
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
        if (TextUtils.equals(anchorsListBean.getSign(), "1"))
        {
            viewHolder.imgLiveSign.setVisibility(View.VISIBLE);
        }
        if (TextUtils.equals(anchorsListBean.getStatus(), "1"))
        {
            viewHolder.tvLiveStatus.setText(Html.fromHtml("<font color='red'>• </font>直播中"));
            viewHolder.tvLiveStatus.setVisibility(View.VISIBLE);
            viewHolder.tvLiveStatus.setBackgroundResource(R.drawable.round_red_border_black_bg);
        }else
        {
            viewHolder.tvLiveStatus.setText(Html.fromHtml("<font color='green'>• </font>回 放"));
            viewHolder.tvLiveStatus.setVisibility(View.VISIBLE);
            viewHolder.tvLiveStatus.setBackgroundResource(R.drawable.round_green_border_black_bg);
        }
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(anchorsListBean.getRoomUrl())
                .centerCrop()
                .placeholder(R.drawable.qs_zb)
                .into(viewHolder.imgLiveCover);
        viewHolder.tvRoomName.setText(anchorsListBean.getRoomName());
        viewHolder.tvWatchNum.setText("" + parseNumber(Double.parseDouble(anchorsListBean.getViewer())));
        viewHolder.tvAnchorCity.setText(anchorsListBean.getCity());
        viewHolder.genderView.setSex(anchorsListBean.getAge(),anchorsListBean.getSex());
        viewHolder.levelView.setLevel(anchorsListBean.getLevel(),1);
        viewHolder.tvNickName.setText(anchorsListBean.getNicName());
//
//        if (!TextUtils.isEmpty(anchorsListBean.getSex()))
//        {
//
//            if ("女".equals(anchorsListBean.getSex()))
//            {
//                viewHolder.tvAnchorSex.setBackgroundResource(R.drawable.shape_round_2corner_15_mc5);
//                viewHolder.tvAnchorSex.setText(String.format("%s %s", "{eam-e94f}", anchorsListBean.getAge()));
//            }
//            else
//            {
//                viewHolder.tvAnchorSex.setBackgroundResource(R.drawable.shape_round_2corner_15_mc7);
//                viewHolder.tvAnchorSex.setText(String.format("%s %s", "{eam-e950}", anchorsListBean.getAge()));
//            }
//        }
        return convertView;
    }

    private String parseNumber(double i) {
        if (i>=10000)
        {
            java.text.DecimalFormat   df=new   java.text.DecimalFormat("#.#");
            double   d=i/10000;
            System.out.println(df.format(d));
            return df.format(d)+"万";
        }
        return (int)i+"";
    }

    public class ViewHolder
    {
        RoundedImageView imgLiveCover;
        ImageView imgLiveSign;
        TextView tvLiveStatus;
        TextView tvRoomName;
        TextView tvWatchNum;
        TextView tvAnchorCity;
        TextView tvNickName;
        GenderView genderView;
        LevelView levelView;
        RelativeLayout rlLiveCover;
    }
}

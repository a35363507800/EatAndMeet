package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/20.
 */

public class KillCardAdapter extends BaseAdapter
{
    private List<Map<String, String>> list;
    private Context mCon;

    public KillCardAdapter(Context context, List<Map<String, String>> list)
    {
        this.mCon = context;
        this.list = list;
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mCon).inflate(R.layout.adp_killcarda, null);
            viewHolder.prize = (RoundedImageView) convertView.findViewById(R.id.rv_prize);
            viewHolder.prizeBg = (ImageView) convertView.findViewById(R.id.prize_bg);
            viewHolder.prizeName = (TextView) convertView.findViewById(R.id.prize_name);
            viewHolder.prizeCount = (TextView) convertView.findViewById(R.id.prize_count);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        String time = list.get(position).get("time");
        String prizecount = list.get(position).get("count");
        String url = list.get(position).get("url");
        String status = list.get(position).get("status");
        String type = list.get(position).get("type");

        if(TextUtils.isEmpty(type))
            type="";
        switch (type)
        {
            case "1":
                type="脸蛋";
                break;
            case "2":
                type="礼物";
                break;
            case "3":
                type="补签卡";
                break;
            case "4":
                type="余额";
                break;
            case "5":
                type="饭票";
                break;
            case "6":
                type="经验";
                break;
            default:
                type="不明物体";
                break;
        }

        viewHolder.prizeCount.setText("+"+prizecount);
        viewHolder.prizeName.setText(type);
        //奖品
        GlideApp.with(mCon.getApplicationContext())
                .asBitmap()
                .load(url)
                .placeholder(R.drawable.userhead)
                .centerCrop()
                .error(R.drawable.userhead)
                .into(viewHolder.prize);

        switch (status)
        {
            case "0":  //未领取

                viewHolder.prizeBg.setBackgroundResource(R.drawable.round_btn_mc0332);
                viewHolder.prize.bringToFront();
                break;
            case "1":  //已领取
                viewHolder.prizeBg.setImageResource(R.drawable.welfare_bg_xuanwan);
                viewHolder.prizeBg.bringToFront();
                break;
            case "2":  //可领取
                viewHolder.prizeBg.setImageResource(R.drawable.sprize_pup3);
                AnimationDrawable animationDrawable = (AnimationDrawable) viewHolder.prizeBg
                        .getDrawable();
                animationDrawable.start();
                viewHolder.prize.bringToFront();
                break;
        }

        return convertView;
    }

    private class ViewHolder
    {
        private RoundedImageView prize;
        private ImageView prizeBg;
        private TextView prizeCount;
        private TextView prizeName;
    }
}

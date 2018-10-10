package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/20.
 */

public class KillDayCardAdapter extends BaseAdapter
{
    private List<Map<String, String>> list;
    private Context mCon;
    private int giftPosition = -1;
    //皮肤url
    private String skin;
    private String icon;
    public KillDayCardAdapter(Context context, List<Map<String, String>> list,String skin,String icon)
    {
        this.mCon = context;
        this.list = list;
        this.skin=skin;
        this.icon=icon;
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
            convertView = LayoutInflater.from(mCon).inflate(R.layout.adp_daykillcarda, null);
            viewHolder.count = (TextView) convertView.findViewById(R.id.day_count);
            viewHolder.checkPrize = (IconTextView) convertView.findViewById(R.id.check_prize);
            viewHolder.prizeBg = (ImageView) convertView.findViewById(R.id.day_prize_im);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        String type = list.get(position).get("name");
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

        String prizecount = list.get(position).get("count");
        String url = list.get(position).get("url");
        String status = list.get(position).get("status");
        String date = list.get(position).get("date");
        date = date.substring(date.length() - 2, date.length());

        if (TextUtils.isEmpty(url))
        {
            viewHolder.count.setText(date);
            viewHolder.count.setBackgroundResource(R.drawable.killcart_adp_item_round_bg);
            viewHolder.prizeBg.setImageBitmap(null);
            viewHolder.count.setVisibility(View.VISIBLE);
        } else
        {
            viewHolder.count.setVisibility(View.GONE);

            if(icon.equals(list.get(position).get("date")))
            {
                viewHolder.prizeBg.setImageDrawable(ContextCompat.getDrawable(mCon,R.drawable.ico_14_monthly_sign_in));
                viewHolder.prizeBg.setScaleX(1.5f);
                viewHolder.prizeBg.setScaleY(1.5f);
            }
            else
            {
                //奖品
                GlideApp.with(mCon.getApplicationContext())
                        .asBitmap()
                        .load(url)
                        .placeholder(R.drawable.userhead)
                        .centerCrop()
                        .error(R.drawable.userhead)
                        .into(viewHolder.prizeBg);
                if(viewHolder.prizeBg.getScaleX()>1)
                {
                    viewHolder.prizeBg.setScaleX(1);
                    viewHolder.prizeBg.setScaleY(1);
                }
            }

        }
        switch (status)
        {
            case "0":  //未领取
                viewHolder.count.setBackgroundResource(R.drawable.killcart_adp_item_round_bg);
                viewHolder.count.setTextColor(ContextCompat.getColor(mCon, R.color.C0322));
                viewHolder.checkPrize.setVisibility(View.GONE);
                break;
            case "1":  //已领取
                viewHolder.count.setBackgroundResource(R.drawable.killcart_adp_item_round_bg_0412);
                viewHolder.count.setTextColor(ContextCompat.getColor(mCon, R.color.white));

                if(viewHolder.count.getVisibility()==View.GONE)
                    viewHolder.checkPrize.setVisibility(View.VISIBLE);
                else
                    viewHolder.checkPrize.setVisibility(View.GONE);
                giftPosition = position;
                break;
            case "2":  //可领取
                viewHolder.checkPrize.setVisibility(View.GONE);
                viewHolder.count.setBackgroundResource(R.drawable.killcart_adp_item_round_bg);
                viewHolder.count.setTextColor(ContextCompat.getColor(mCon, R.color.C0322));
                break;
        }


        return convertView;
    }

    private class ViewHolder
    {
        private TextView count;
        private ImageView prizeBg;
        private IconTextView checkPrize;
    }

    public int getPosition()
    {
        return giftPosition;
    }

}

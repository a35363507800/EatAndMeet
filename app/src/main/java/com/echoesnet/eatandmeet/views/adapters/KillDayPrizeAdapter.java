package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/20.
 */

public class KillDayPrizeAdapter extends BaseAdapter
{
    private List<Map<String, String>> list;
    private Context mCon;
    private boolean tomorrow=false;
    public KillDayPrizeAdapter(Context context, List<Map<String, String>> list)
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
            convertView = LayoutInflater.from(mCon).inflate(R.layout.adp_dayprize, null);
            viewHolder.count = (TextView) convertView.findViewById(R.id.prize_count);
            viewHolder.name = (TextView) convertView.findViewById(R.id.prize_name);
            viewHolder.checkPrize = (TextView) convertView.findViewById(R.id.tv_nextprize);
            viewHolder.prizeBg = (ImageView) convertView.findViewById(R.id.prize_content);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
                String type=list.get(position).get("type");

                if(!"7".equals(type))
                {
                    //奖品
                    GlideApp.with(EamApplication.getInstance())
                            .asBitmap()
                            .load(list.get(position).get("url"))
                            .placeholder(R.drawable.userhead)
                            .centerCrop()
                            .error(R.drawable.userhead)
                            .into(viewHolder.prizeBg);
                }

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
                    case "7":
                        type="皮肤";
                        viewHolder.prizeBg.setImageDrawable(ContextCompat.getDrawable(mCon,R.drawable.img_14_monthly_sign_in));
                        break;
                    default:
                        type="不明物体";
                        break;
                }

                   if(tomorrow)
                    viewHolder.checkPrize.setVisibility(View.VISIBLE);
                    else
                    viewHolder.checkPrize.setVisibility(View.GONE);


        viewHolder.name.setText(type);
        viewHolder.count.setText("+"+list.get(position).get("count"));

        return convertView;
    }

    private class ViewHolder
    {
        private TextView count;
        private TextView name;
        private ImageView prizeBg;
        private TextView checkPrize;
    }

    public void upData(List<Map<String, String>> list,boolean tomorrow){
        if(list!=null)
            this.list=list;
        this.tomorrow=tomorrow;
        notifyDataSetChanged();
    }
}

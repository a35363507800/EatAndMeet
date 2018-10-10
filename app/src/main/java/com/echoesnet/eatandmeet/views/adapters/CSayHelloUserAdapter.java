package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.CAccostBean;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.List;


public class CSayHelloUserAdapter extends BaseAdapter
{
    private List<CAccostBean> tempUserLst;
    private Context context;
    private ViewHolder holder = null;

    public CSayHelloUserAdapter(Context context, List<CAccostBean> tempUserLst)
    {
        this.context = context;
        this.tempUserLst = tempUserLst;
    }

    @Override
    public int getCount()
    {
        return tempUserLst.size();
    }

    @Override
    public Object getItem(int position)
    {
        return tempUserLst.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final CAccostBean bean = (CAccostBean) getItem(position);
        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_sayhello_user, null);
            holder.rivUserHeader = (LevelHeaderView) convertView.findViewById(R.id.riv_user_header);
            holder.itvSexIcon = (ImageView) convertView.findViewById(R.id.iv_sex_icon);
            holder.tvUserName = (TextView) convertView.findViewById(R.id.tv_user_name);
            holder.vSpliter = convertView.findViewById(R.id.view_div);

            holder.ivLiveIcon = (IconTextView) convertView.findViewById(R.id.iv_live_icon);
            holder.ivMealIcon = (IconTextView) convertView.findViewById(R.id.iv_meal_icon);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        // 设置用户头像
        holder.rivUserHeader.setLiveState(false);
        holder.rivUserHeader.setHeadImageByUrl(bean.getUserBean().getUphUrl());
        holder.rivUserHeader.setLevel(bean.getUserBean().getLevel());
        String name = bean.getUserBean().getNicName();

        if(name.length()>5)
            name=(name.substring(0,5)+"...");
        holder.tvUserName.setText(name);

        // 设置性别
        if (!TextUtils.isEmpty(bean.getUserBean().getSex()))
        {
            if (bean.getUserBean().getSex().equals("男"))
            {
//                holder.itvSexIcon.setBackgroundResource(R.drawable.shape_round_2corner_15_mc7);
//                holder.itvSexIcon.setText(String.format("%s %s", "{eam-s-people4}", bean.getUserBean().getAge()));
                holder.itvSexIcon.setImageResource(R.drawable.man_1_xxhdpi);
            }
            else
            {
//                holder.itvSexIcon.setBackgroundResource(R.drawable.shape_round_2corner_15_mc5);
//                holder.itvSexIcon.setText(String.format("%s %s", "{eam-e94f}", bean.getUserBean().getAge()));
                holder.itvSexIcon.setImageResource(R.drawable.women_1_xxhdpi);
            }
        }
        holder.ivLiveIcon.setVisibility(View.GONE);
        holder.vSpliter.setVisibility(View.GONE);
        holder.ivMealIcon.setVisibility(View.GONE);
        //如果在直播
        if (bean.getUserBean().getStatus().equals("1"))
        {
            //如果在餐厅就餐
            if (!TextUtils.isEmpty(bean.getrId()))
            {
                holder.ivLiveIcon.setVisibility(View.VISIBLE);
                holder.vSpliter.setVisibility(View.VISIBLE);
                holder.ivMealIcon.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.ivLiveIcon.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            if (!TextUtils.isEmpty(bean.getrId()))
            {
                holder.ivMealIcon.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    public class ViewHolder
    {
        public LevelHeaderView rivUserHeader;
        public ImageView itvSexIcon;
        public TextView tvUserName;
        public IconTextView ivLiveIcon;
        public IconTextView ivMealIcon;
        public View vSpliter;
    }
}

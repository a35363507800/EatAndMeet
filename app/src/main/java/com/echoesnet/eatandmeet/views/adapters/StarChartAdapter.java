package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.StarChartBean;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.orhanobut.logger.Logger;

import java.util.List;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @modifier
 * @createDate 2017/11/28
 * @description
 */
public class StarChartAdapter extends BaseAdapter
{
    private Activity mAct;
    private List<StarChartBean> mList;
    private StarChartViewHolder viewHolder;

    public StarChartAdapter(Activity activity, List<StarChartBean> list)
    {
        this.mAct = activity;
        this.mList = list;
    }

    @Override
    public int getCount()
    {
        return mList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        final StarChartBean itemBean = mList.get(position);
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mAct).inflate(R.layout.item_act_star_chart, null);
            viewHolder = new StarChartViewHolder();
            viewHolder.ivTopNum = (ImageView) convertView.findViewById(R.id.iv_top_num);
            viewHolder.tvTopNum = (TextView) convertView.findViewById(R.id.tv_top_num);
            viewHolder.userHeadImg = (LevelHeaderView) convertView.findViewById(R.id.riv_head);
            viewHolder.rlAll = (RelativeLayout) convertView.findViewById(R.id.rl_all);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.itvAge = (GenderView) convertView.findViewById(R.id.itv_age);
            viewHolder.llHostLevel = (LevelView) convertView.findViewById(R.id.ll_host_level);
            viewHolder.tvStarNum = (TextView) convertView.findViewById(R.id.tv_star_num);

            //   viewHolder.tvAddFocus = (TextView) convertView.findViewById(R.id.tv_add_focus);


            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (StarChartViewHolder) convertView.getTag();
        }
        viewHolder.tvName.setText(itemBean.getNicName());
        viewHolder.itvAge.setSex(itemBean.getAge(), itemBean.getSex());
        viewHolder.llHostLevel.setLevel(itemBean.getLevel(), 0);

        viewHolder.userHeadImg.setHeadImageByUrl(itemBean.getPhurl());
        viewHolder.userHeadImg.showRightIcon(itemBean.getIsVuser());

        viewHolder.tvStarNum.setText(itemBean.getStar()+"星光值");

        int rank = Integer.parseInt(itemBean.getRank());
        if (rank < 4)
        {
            viewHolder.ivTopNum.setVisibility(View.VISIBLE);
            viewHolder.tvTopNum.setVisibility(View.GONE);
            switch (rank)
            {
                case 1:
                    viewHolder.ivTopNum.setImageResource(R.drawable.live_no1);
                    break;
                case 2:
                    viewHolder.ivTopNum.setImageResource(R.drawable.live_no2);
                    break;
                case 3:
                    viewHolder.ivTopNum.setImageResource(R.drawable.live_no3);
                    break;
                default:
                    break;

            }
        } else
        {
            viewHolder.ivTopNum.setVisibility(View.GONE);
            viewHolder.tvTopNum.setVisibility(View.VISIBLE);
            viewHolder.tvTopNum.setText(itemBean.getRank());

        }
        //是否关注
//        if (TextUtils.equals(itemBean.getFocus(), "0"))
//        {
//            changeFocusUi(false);
//        } else
//        {
//            changeFocusUi(true);
//        }
        return convertView;
    }

    public class StarChartViewHolder
    {
        private LevelHeaderView userHeadImg;
        private RelativeLayout rlAll;
        private TextView tvName;
        private GenderView itvAge;
        private LevelView llHostLevel;
        private TextView tvStarNum;
        private ImageView ivTopNum;
        private TextView tvTopNum;
        //  private TextView tvAddFocus;


    }

//    private void changeFocusUi(boolean isFocus)
//    {
//        if (isFocus)
//        {
//            viewHolder.tvAddFocus.setBackgroundResource(R.drawable.shape_round_bg_focus);
//            viewHolder.tvAddFocus.setText(String.format("%s", "已关注"));
//            viewHolder.tvAddFocus.setTextColor(ContextCompat.getColor(mAct, R.color.C0323));
//            viewHolder.tvAddFocus.setTag("已关注");
//        } else
//        {
//            viewHolder.tvAddFocus.setBackgroundResource(R.drawable.shape_round_bg_unfocus);
//            viewHolder.tvAddFocus.setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
//            viewHolder.tvAddFocus.setText(String.format("%s %s", "＋", "关注"));
//            viewHolder.tvAddFocus.setTag("未关注");
//        }
//    }
}

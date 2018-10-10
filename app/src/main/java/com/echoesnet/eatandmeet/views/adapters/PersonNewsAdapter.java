package com.echoesnet.eatandmeet.views.adapters;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.activities.SysNewsAct;
import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;
import com.echoesnet.eatandmeet.models.bean.SysNewsTopBean;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.SysNewsMessageListBean;
import com.echoesnet.eatandmeet.views.CircleTextView;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;

import butterknife.OnClick;

/**
 * Created by lc on 2017/7/11 13.t
 */

public class PersonNewsAdapter extends BaseAdapter
{
    private List<Object> mData;
    private Activity mActivity;
    private static final String TAG = PersonNewsAdapter.class.getSimpleName();
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnTopItemClickListener onTopItemClickListener;
    ViewHolder viewHolder;
    TopViewHolder topViewHolder;
    private boolean TopUnRead  = true;
    private SysNewsTopBean itemTopBean;


    private static final int TYPE_TOP=0;
    private static final int TYPE_PERSON=1;

    public PersonNewsAdapter(List<Object> mData, Activity mActivity)
    {
        this.mData = mData;
        this.mActivity = mActivity;


    }

    @Override
    public int getItemViewType(int position)
    {
        Object object = mData.get(position);
        if (object instanceof SysNewsTopBean)
        {
            return TYPE_TOP;
        }
        else if (object instanceof SysNewsMessageListBean)
        {
            return TYPE_PERSON;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public int getCount()
    {
        return mData.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        SysNewsMessageListBean itemBean = null;
        int Viewtype = getItemViewType(position);
        Object object = mData.get(position);
        if (Viewtype != TYPE_TOP)
            itemBean = (SysNewsMessageListBean) object;
        switch (Viewtype)
        {
            case TYPE_TOP:
                topViewHolder = new TopViewHolder();
                 itemTopBean = (SysNewsTopBean) object;
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.act_item_notification_top, parent, false);
                topViewHolder.ctvSysUnRead = (CircleTextView) convertView.findViewById(R.id.ctv_sys_unRead);
                topViewHolder.tvSysTitle = (TextView) convertView.findViewById(R.id.tv_sys_title);
                topViewHolder.tvSysContent = (TextView) convertView.findViewById(R.id.tv_sys_content);
                topViewHolder.tvSysTime = (TextView) convertView.findViewById(R.id.tv_sys_time);
                topViewHolder.rlAllSysInfo = (RelativeLayout) convertView.findViewById(R.id.rl_all_sysInfo);
                topViewHolder.ctvSysUnRead.setText(itemTopBean.getSystemUnreadNum());
                topViewHolder.tvSysContent.setText(itemTopBean.getTvSysContent());
                topViewHolder.tvSysTime.setText(itemTopBean.getTvSysTime());
                topViewHolder.rlAllSysInfo.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (onTopItemClickListener!=null)
                            onTopItemClickListener.onTopItemClick(v,position,itemTopBean);
                    }
                });

                if (itemTopBean.getSystemUnreadNum() != null && TopUnRead)
                {
                    if (TextUtils.equals("0", itemTopBean.getSystemUnreadNum()))
                    {
                        topViewHolder.ctvSysUnRead.setVisibility(View.GONE);

                    } else
                    {
                        topViewHolder.ctvSysUnRead.setVisibility(View.VISIBLE);

                    }
                    topViewHolder.ctvSysUnRead.setNotifiText(itemTopBean.getSystemUnreadNum() );
                }


                break;
            case TYPE_PERSON:
             //   final SysNewsMessageListBean itemBean = getItem(position);
                if (convertView == null)
                {
                    viewHolder = new ViewHolder();
                    convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_person_news, parent, false);
                    viewHolder.userHeadImg = (LevelHeaderView) convertView.findViewById(R.id.riv_head);
                    viewHolder.rlAll = (RelativeLayout) convertView.findViewById(R.id.rl_all);
                    viewHolder.userNameTv = (TextView) convertView.findViewById(R.id.tv_name);
                    viewHolder.sexImg = (GenderView) convertView.findViewById(R.id.itv_age);
                    viewHolder.levelView = (LevelView) convertView.findViewById(R.id.ll_level);
                    viewHolder.userLookYou = (TextView) convertView.findViewById(R.id.tv_look_u_info);
                    viewHolder.userDistance = (TextView) convertView.findViewById(R.id.tv_distance);
                    viewHolder.userAddFocus = (TextView) convertView.findViewById(R.id.tv_add_focus);
                    convertView.setTag(viewHolder);
                } else
                {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.userHeadImg.setHeadImageByUrl(itemBean.getUser().getPhUrl());
                viewHolder.userHeadImg.showRightIcon(itemBean.getUser().getIsVuser());
                viewHolder.levelView.setLevel(itemBean.getUser().getLevel(), 1);
                viewHolder.userHeadImg.setTag(itemBean.getUser().getPhUrl());
                viewHolder.userNameTv.setText(TextUtils.isEmpty(itemBean.getUser().getRemark()) ? itemBean.getUser().getNickName() : itemBean.getUser().getRemark());
                viewHolder.userDistance.setText(TextUtils.isEmpty(itemBean.getUser().getDistance()) ? "火星" : itemBean.getUser().getDistance());
                viewHolder.sexImg.setSex(itemBean.getUser().getAge(), itemBean.getUser().getSex());
                viewHolder.userLookYou.setText(itemBean.getDesc());
                //是否关注
                if (TextUtils.equals(itemBean.getUser().getFocus(), "0"))
                {
                    changeFocusUi(false);
                } else
                {
                    changeFocusUi(true);
                }
                final SysNewsMessageListBean finalItemBean = itemBean;
                viewHolder.userAddFocus.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (mOnItemClickListener != null)
                            mOnItemClickListener.onItemClick(viewHolder.userAddFocus, finalItemBean, position);
                    }
                });
                break;
        }

        return convertView;
    }


    private void changeFocusUi(boolean isFocus)
    {
        if (isFocus)
        {
            viewHolder.userAddFocus.setBackgroundResource(R.drawable.shape_round_bg_focus);
            viewHolder.userAddFocus.setText(String.format("%s", "已关注"));
            viewHolder.userAddFocus.setTextColor(ContextCompat.getColor(mActivity, R.color.C0323));
            viewHolder.userAddFocus.setTag("已关注");
        } else
        {
            viewHolder.userAddFocus.setBackgroundResource(R.drawable.shape_round_bg_unfocus);
            viewHolder.userAddFocus.setTextColor(ContextCompat.getColor(mActivity, R.color.C0412));
            viewHolder.userAddFocus.setText(String.format("%s %s", "＋", "关注"));
            viewHolder.userAddFocus.setTag("未关注");
        }
    }


    static class ViewHolder
    {
        private LevelHeaderView userHeadImg;
        private LevelView levelView;
        private GenderView sexImg;
        private TextView userNameTv;
        private TextView userDistance;
        private TextView userLookYou;
        private TextView userAddFocus;
        private RelativeLayout rlAll;
    }

    static class TopViewHolder
    {
        private CircleTextView ctvSysUnRead;
        private TextView tvSysTitle;
        private TextView tvSysContent;
        private TextView tvSysTime;
        private RelativeLayout rlAllSysInfo;

    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener)
    {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }
    public void notifyTopUnReadChanged()
    {
        if (topViewHolder!=null)
        {
            topViewHolder.ctvSysUnRead.setVisibility(View.GONE);
            if (itemTopBean!=null)
                itemTopBean.setSystemUnreadNum("0");
        }
    }


    /**
     * 更新内容
     *
     * @param TopUnRead 是否只更新点赞 评论
     */
    public void notifyDataSetChanged(boolean TopUnRead)
    {
        this.TopUnRead = TopUnRead;
        super.notifyDataSetChanged();
    }


    /**
     * ItemClick的回调接口
     */
    public interface OnItemClickListener
    {
        void onItemClick(View view, SysNewsMessageListBean bean, int position);
    }

    /**
     * ItemLongClick的回调接口
     */
    public interface OnItemLongClickListener
    {
        void onItemLongClick(View view, int position);
    }

    /**
     * ItemLongClick的回调接口
     */
    public interface OnTopItemClickListener
    {
        void onTopItemClick(View view, int position,SysNewsTopBean bean);
    }
    public void setOnTopItemClickListener(OnTopItemClickListener OnTopItemClickListener)
    {
        this.onTopItemClickListener = OnTopItemClickListener;
    }
}

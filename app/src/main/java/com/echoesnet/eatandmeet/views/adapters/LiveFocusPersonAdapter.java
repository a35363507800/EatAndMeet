package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.MyFocusPersonBean;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.joanzapata.iconify.widget.IconTextView;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/17.
 */

public class LiveFocusPersonAdapter extends BaseAdapter
{

    private static final String TAG = MyInviteFriendsAdapter.class.getSimpleName();
    private Activity mContext;
    List<MyFocusPersonBean> list = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public LiveFocusPersonAdapter(Activity mContext, List<MyFocusPersonBean> list)
    {
        this.mContext = mContext;
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
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final Holder holder;
        if (convertView == null)
        {
            holder = new Holder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.live_focus_person_adapter, parent, false);
            holder.ri_focus = (LevelHeaderView) convertView.findViewById(R.id.ri_focus);
            holder.levelView = (LevelView) convertView.findViewById(R.id.level_view);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_focus_age = (IconTextView) convertView.findViewById(R.id.tv_focus_age);
            holder.ll_inLive = (AutoLinearLayout) convertView.findViewById(R.id.ll_inLive);
            holder.ll_all = (AutoLinearLayout) convertView.findViewById(R.id.ll_all);
            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }
        holder.ri_focus.setHeadImageByUrl(list.get(position).getUphUrl());
        holder.ri_focus.setLevel(list.get(position).getLevel());
//        holder.levelView.setLevel("1");
        holder.levelView.setLevel(list.get(position).getLevel(),LevelView.USER);
        holder.tv_name.setText(list.get(position).getNicName());
        if (list.get(position).getSex().equals("女"))
        {
            holder.tv_focus_age.setBackgroundResource(R.drawable.shape_round_2corner_15_mc5);
            holder.tv_focus_age.setText(String.format("%s %s", "{eam-e94f}",list.get(position).getAge()));
        }
        else
        {
            holder.tv_focus_age.setBackgroundResource(R.drawable.shape_round_2corner_15_mc7);
            holder.tv_focus_age.setText(String.format("%s %s", "{eam-e950}",list.get(position).getAge()));
        }
        holder.ll_inLive.setVisibility(View.GONE);
        if ("1".equals(list.get(position).getStatus()))
        {
            holder.ll_inLive.setVisibility(View.VISIBLE);
        }
        //如果设置了回调，则设置点击事件
        if (mOnItemClickListener != null)
        {
            holder.ll_all.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mOnItemClickListener.onItemClick(holder.ll_all, position);
                }
            });
        }
        //如果设置了回调，则设置点击事件
        if (mOnItemLongClickListener != null)
        {
            holder.ll_all.setOnLongClickListener(new View.OnLongClickListener()
            {

                @Override
                public boolean onLongClick(View v)
                {
                    mOnItemLongClickListener.onItemLongClick(holder.ll_all, position);
                    return false;
                }
            });
        }

        return convertView;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener)
    {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    /**
     * ItemClick的回调接口
     */
    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }

    /**
     * ItemLongClick的回调接口
     */
    public interface OnItemLongClickListener
    {
        void onItemLongClick(View view, int position);
    }

    class Holder
    {
        LevelHeaderView ri_focus;
        LevelView levelView;
        TextView tv_name;
        IconTextView tv_focus_age;
        AutoLinearLayout ll_inLive;
        AutoLinearLayout ll_all;
    }

}

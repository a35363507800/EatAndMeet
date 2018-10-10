package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.LChoseConnectMemberBean;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ben on 2017/3/31.
 */

public class LChoseConnectMemberAdapter extends BaseAdapter
{
    private static final String TAG = LChoseConnectMemberAdapter.class.getSimpleName();
    private Context mContext;
    private List<LChoseConnectMemberBean> mData;
    private OnChosenMemberClickListener chosenMemberClickListener;
    // 用于记录每个RadioButton的状态，并保证只可选一个
    HashMap<String, Boolean> states = new HashMap<String, Boolean>();

    public LChoseConnectMemberAdapter(Context context, List<LChoseConnectMemberBean> data)
    {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount()
    {
        return mData.size();
    }

    @Override
    public LChoseConnectMemberBean getItem(int position)
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
        final LChoseConnectMemberBean lcn = mData.get(position);
        final ViewHolder viewHolder;
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.litem_chose_connect_member, parent, false);
            viewHolder.rootView = (RelativeLayout) convertView.findViewById(R.id.root_view);
            viewHolder.tvChoseIcon = (CheckBox) convertView.findViewById(R.id.tv_chose_btn);
            viewHolder.levelView = (LevelView) convertView.findViewById(R.id.level_view);
            viewHolder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nick_name);
            viewHolder.rivHeadImg = (LevelHeaderView) convertView.findViewById(R.id.ri_head_img);
            viewHolder.tvConnectAge = (GenderView) convertView.findViewById(R.id.tv_connect_age);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (lcn.isChoose())
            viewHolder.tvChoseIcon.setChecked(true);
        else
            viewHolder.tvChoseIcon.setChecked(false);
        setSex(viewHolder, lcn);
        viewHolder.tvNickName.setText(lcn.getNicName());
        viewHolder.rivHeadImg.setHeadImageByUrl(lcn.getPhUrl());
        viewHolder.rivHeadImg.showRightIcon(lcn.getIsVuser());
        viewHolder.levelView.setLevel(lcn.getLevel(),LevelView.USER);
        viewHolder.rootView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            // 重置，确保最多只有一项被选中
                for (String key : states.keySet())
                {
                    states.put(key, false);
                }
                viewHolder.tvChoseIcon.setChecked(true);
                states.put(String.valueOf(position), viewHolder.tvChoseIcon.isChecked());
                if (chosenMemberClickListener != null)
                    chosenMemberClickListener.onChosenMemberClick(position, lcn);
            }
        });
        boolean res = false;
        if (states.get(String.valueOf(position)) == null
                || !states.get(String.valueOf(position)))
        {
            res = false;
            states.put(String.valueOf(position), false);
        }
        else
            res = true;

        viewHolder.tvChoseIcon.setChecked(res);
        return convertView;
    }

    private void setSex(ViewHolder viewHolder, LChoseConnectMemberBean lcn)
    {
        viewHolder.tvConnectAge.setSex(lcn.getAge(),lcn.getSex());
        /*if (lcn.getSex().equals("女"))
        {
            viewHolder.tvConnectAge.setBackgroundResource(R.drawable.shape_round_2corner_15_mc5);
            viewHolder.tvConnectAge.setText(String.format("%s %s", "{eam-e94f}", lcn.getAge()));
        }
        else
        {
            viewHolder.tvConnectAge.setBackgroundResource(R.drawable.shape_round_2corner_15_mc7);
            viewHolder.tvConnectAge.setText(String.format("%s %s", "{eam-e950}", lcn.getAge()));
        }*/
    }

    static class ViewHolder
    {
        RelativeLayout rootView;
        CheckBox tvChoseIcon;
        LevelView levelView;
        TextView tvNickName;
        LevelHeaderView rivHeadImg;
        GenderView tvConnectAge;
    }

    public interface OnChosenMemberClickListener
    {
        void onChosenMemberClick(int position, LChoseConnectMemberBean lcn);
    }

    public void setChosenMemberClickListener(OnChosenMemberClickListener chosenMemberClickListener)
    {
        this.chosenMemberClickListener = chosenMemberClickListener;
    }
}

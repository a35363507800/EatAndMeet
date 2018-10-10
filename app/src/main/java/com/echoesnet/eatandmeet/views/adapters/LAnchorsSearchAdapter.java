package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.AnchorSearchBean;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.List;

/**
 * 直播搜索页adapter
 * Created by an on 2016/10/17 0017.
 */

public class LAnchorsSearchAdapter extends RecyclerView.Adapter<LAnchorsSearchAdapter.AnchorsSearchViewholder>
{

    private Context mContext;
    private List<AnchorSearchBean> mAnchorsList;
    private OnItemClickListener itemClickListener;

    public LAnchorsSearchAdapter(Context mContext, List<AnchorSearchBean> mAnchorsList)
    {
        this.mContext = mContext;
        this.mAnchorsList = mAnchorsList;
    }

    @Override
    public AnchorsSearchViewholder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_anchors_search, parent, false);
        AnchorsSearchViewholder viewholder = new AnchorsSearchViewholder(view);
        viewholder.imgItemSearchHead = (LevelHeaderView) view.findViewById(R.id.img_item_search_head);
        viewholder.levelView = (LevelView) view.findViewById(R.id.level_view);
        viewholder.ivItemSearchLiving = (IconTextView) view.findViewById(R.id.iv_item_search_living);
        viewholder.ivItemSearchSex = (IconTextView) view.findViewById(R.id.iv_item_search_sex);
        viewholder.tvItemSearchId = (TextView) view.findViewById(R.id.tv_item_search_id);
        viewholder.tvItemSearchName = (TextView) view.findViewById(R.id.tv_item_search_name);

        return viewholder;
    }

    @Override
    public void onBindViewHolder(AnchorsSearchViewholder holder, final int position)
    {
        AnchorSearchBean anchorSearchBean = mAnchorsList.get(position);
        if (!TextUtils.isEmpty(anchorSearchBean.getNicName()))
        {
            holder.tvItemSearchName.setText(anchorSearchBean.getNicName());
        }
        if (!TextUtils.isEmpty(anchorSearchBean.getuId()))
        {
            holder.tvItemSearchId.setText("ID:" + anchorSearchBean.getId());
        }
        if (TextUtils.equals(anchorSearchBean.getSex(), "女"))
        {

            holder.ivItemSearchSex.setBackgroundResource(R.drawable.shape_round_2corner_15_mc5);
            holder.ivItemSearchSex.setText(String.format("%s %s", "{eam-e94f}",anchorSearchBean.getAge()));
        }
        else
        {
            holder.ivItemSearchSex.setBackgroundResource(R.drawable.shape_round_2corner_15_mc7);
            holder.ivItemSearchSex.setText(String.format("%s %s", "{eam-e950}",anchorSearchBean.getAge()));
        }

        //是否直播中
        if (TextUtils.equals(anchorSearchBean.getStatus(), "1"))
        {
            holder.ivItemSearchLiving.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.ivItemSearchLiving.setVisibility(View.GONE);
        }

        holder.imgItemSearchHead.setLiveState(false);
        holder.imgItemSearchHead.setHeadImageByUrl(anchorSearchBean.getUphUrl());
        holder.imgItemSearchHead.setLevel(anchorSearchBean.getLevel());
        holder.levelView.setLevel(anchorSearchBean.getLevel(),LevelView.USER);
        //item 点击
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                itemClickListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mAnchorsList.size();
    }

    public void setItemClickListener(OnItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    public static class AnchorsSearchViewholder extends RecyclerView.ViewHolder
    {
        LevelHeaderView imgItemSearchHead;
        TextView tvItemSearchName;
        TextView tvItemSearchId;
        IconTextView ivItemSearchSex;
        IconTextView ivItemSearchLiving;
        LevelView levelView;

        public AnchorsSearchViewholder(View itemView)
        {
            super(itemView);
        }
    }

    /**
     * ItemClick的回调接口
     */
    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }

}

package com.echoesnet.eatandmeet.views.adapters;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.AudienceBean;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderAdpView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;

import java.util.List;

/**
 * 观众列表
 */
public class AudienceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = AudienceListAdapter.class.getSimpleName();

    //private static final int VIEW_TYPE_AVATAR = 1000;
    private List<AudienceBean> lpmListData = null;
    private Activity mAct;

    public AudienceListAdapter(Activity act, List<AudienceBean> objects)
    {
        mAct = act;
        lpmListData = objects;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //AudienceViewHolder vhAudience=null;
        //if (VIEW_TYPE_AVATAR == viewType)
        {
            View view = LayoutInflater.from(mAct).inflate(R.layout.item_aduience_avatar, parent, false);
            return new AudienceViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        return super.getItemViewType(position);
        //return VIEW_TYPE_AVATAR;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder)
    {
        super.onViewRecycled(holder);
        ((AudienceViewHolder) holder).crvAvatar.showRightIcon(false);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        //if (getItemViewType(position) == VIEW_TYPE_AVATAR)
        {
            bindMessageItem(position, (AudienceViewHolder) holder);
        }
    }

    private void bindMessageItem(int position, AudienceViewHolder holder)
    {
        final AudienceBean itemBean = lpmListData.get(position);
        holder.crvAvatar.setHeadImageByUrl(itemBean.getFaceUrl());

        if ("1".equals(itemBean.getIsGhost()))//假用户不显示大V
            holder.crvAvatar.showRightIcon(false);
        else
            holder.crvAvatar.showRightIcon(itemBean.getIsVuser());

        holder.crvAvatar.setLevel(itemBean.getLevel());
        holder.crvAvatar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnItemClickListener != null)
                {
                    mOnItemClickListener.onAvatarClick(v, itemBean);
                }
            }
        });
    }


    @Override
    public long getItemId(int position)// 这个实现是不对的，不要随意重写这个方法，这个要结合setHasStableIds 与hasStableIds 来实现--wb
    {
        return super.getItemId(position);
        //return position;
    }

    @Override
    public int getItemCount()
    {
        return lpmListData.size();
    }


    /**
     * ItemClick的回调接口
     */
    public interface OnItemClickListener
    {
        void onAvatarClick(View view, AudienceBean entity);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }


    public static class AudienceViewHolder extends RecyclerView.ViewHolder
    {
        LevelHeaderAdpView crvAvatar;

        public AudienceViewHolder(View v)
        {
            super(v);
            crvAvatar = (LevelHeaderAdpView) v;
        }
    }
}
package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.HotAnchorBean;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * 发现页约主播adapter
 * Created by an on 2017/3/29 0029.
 */

public class FBootyCall4FindAdapter extends RecyclerView.Adapter
{
    private final String TAG = FBootyCall4FindAdapter.class.getSimpleName();
    public static final int ITEM_DEFAULT = 1001;
    public static final int ITEM_MORE = 1002;
    private Activity mActivity;
    private List<HotAnchorBean> mData;
    private ItemClickListener itemClickListener;

    public FBootyCall4FindAdapter(Activity mActivity, List<HotAnchorBean> mData)
    {
        this.mActivity = mActivity;
        this.mData = mData;
    }

    @Override
    public int getItemViewType(int position)
    {
        if (position == mData.size())
            return ITEM_MORE;
        return ITEM_DEFAULT;
    }

    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_booty_call_4_find, parent, false);
        LivePlayViewHolder viewHolder = new LivePlayViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        LivePlayViewHolder livePlayViewHolder = (LivePlayViewHolder) holder;
        if (getItemViewType(position) == ITEM_DEFAULT)
        {
            HotAnchorBean hotAnchorBean = mData.get(position);
            livePlayViewHolder.userNameTv.setText(hotAnchorBean.getRoomName());
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(hotAnchorBean.getRoomUrl())
                    .centerCrop()
                    .placeholder(R.drawable.qs_photo)
                    .error(R.drawable.qs_photo)
                    .into(livePlayViewHolder.userHeadImg);
            livePlayViewHolder.levelView.setLevel(hotAnchorBean.getLevel(),LevelView.USER);
            if ("男".equals(hotAnchorBean.getSex()))
            {
                livePlayViewHolder.sexImg.setImageResource(R.drawable.man_1_xxhdpi);
            }
            else
            {
                livePlayViewHolder.sexImg.setImageResource(R.drawable.women_1_xxhdpi);
            }
        }
        else
        {
            livePlayViewHolder.inforRl.setVisibility(View.GONE);
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(R.drawable.img_more)
                    .centerCrop()
                    .into(livePlayViewHolder.userHeadImg);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (itemClickListener != null)
                    itemClickListener.onItemClick(position, v, getItemViewType(position));
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mData.size() + 1;
    }

    public class LivePlayViewHolder extends RecyclerView.ViewHolder
    {
        private RoundedImageView userHeadImg;
        private LevelView levelView;
        private ImageView sexImg;
        private TextView userNameTv;
        private RelativeLayout inforRl;

        public LivePlayViewHolder(View itemView)
        {
            super(itemView);
            userHeadImg = (RoundedImageView) itemView.findViewById(R.id.img_uer_head);
            levelView = (LevelView) itemView.findViewById(R.id.level_view);
            sexImg = (ImageView) itemView.findViewById(R.id.img_sex);
            userNameTv = (TextView) itemView.findViewById(R.id.tv_user_name);
            inforRl = (RelativeLayout) itemView.findViewById(R.id.rl_information);
        }
    }

    public interface ItemClickListener
    {
        void onItemClick(int position, View view, int itemViewType);
    }
}

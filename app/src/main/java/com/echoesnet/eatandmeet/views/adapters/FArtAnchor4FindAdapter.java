package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * 发现页才艺or附近主播adapter
 * Created by an on 2017/3/29 0029.
 */

public class FArtAnchor4FindAdapter extends RecyclerView.Adapter
{
    private final String TAG = FArtAnchor4FindAdapter.class.getSimpleName();
    public static final int ITEM_DEFAULT = 1001;
    public static final int ITEM_MORE = 1002;
    private Activity mActivity;
    private List<HotAnchorBean> mData;
    private ItemClickListener itemClickListener;
    private boolean isArtAnchor;

    public FArtAnchor4FindAdapter(Activity mActivity, List<HotAnchorBean> mData, boolean isArtAnchor)
    {
        this.mActivity = mActivity;
        this.mData = mData;
        this.isArtAnchor = isArtAnchor;
    }

    @Override
    public int getItemViewType(int position)
    {
        return ITEM_DEFAULT;
    }

    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_art_anchor_4_find, parent, false);
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
            if (!TextUtils.isEmpty(hotAnchorBean.getDistance()) && !isArtAnchor)
            {
                livePlayViewHolder.distanceTv.setVisibility(View.VISIBLE);
                double distanceRes = Double.parseDouble(hotAnchorBean.getDistance());
//             距离
                if ((distanceRes + 0.5) > 1000)
                {
                    livePlayViewHolder.distanceTv.setText(CommonUtils.keep2Decimal((distanceRes + 0.5) / 1000) + "km");
                }
                else
                {
                    if ((distanceRes + 0.5) > 100)
                    {
                        livePlayViewHolder.distanceTv.setText(CommonUtils.keep2Decimal((distanceRes + 0.5)) + "m");
                    }
                    else
                    {
                        livePlayViewHolder.distanceTv.setText("< 100m");
                    }

                }
            }
        }
        else
        {
            livePlayViewHolder.distanceTv.setVisibility(View.GONE);
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
        return mData.size();
    }

    public class LivePlayViewHolder extends RecyclerView.ViewHolder
    {
        private RoundedImageView userHeadImg;
        private LevelView levelView;
        private ImageView sexImg;
        private TextView userNameTv;
        private RelativeLayout inforRl;
        private TextView distanceTv;

        public LivePlayViewHolder(View itemView)
        {
            super(itemView);
            userHeadImg = (RoundedImageView) itemView.findViewById(R.id.img_uer_head);
            levelView = (LevelView) itemView.findViewById(R.id.level_view);
            sexImg = (ImageView) itemView.findViewById(R.id.img_sex);
            userNameTv = (TextView) itemView.findViewById(R.id.tv_user_name);
            inforRl = (RelativeLayout) itemView.findViewById(R.id.rl_information);
            distanceTv = (TextView) itemView.findViewById(R.id.tv_distance);
        }
    }

    public interface ItemClickListener
    {
        void onItemClick(int position, View view, int itemViewType);
    }
}

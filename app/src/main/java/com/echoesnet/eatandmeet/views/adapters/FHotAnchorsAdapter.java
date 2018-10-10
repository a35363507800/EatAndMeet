package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.HotAnchorBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * Created by an on 2016/10/17 0017.
 */

public class FHotAnchorsAdapter extends RecyclerView.Adapter<FHotAnchorsAdapter.HotAnchorsViewHolder>{

    private Context mContext;
    private List<HotAnchorBean> anchorsLists;
    private FHotAnchorsAdapter.OnItemClickListener onItemClickListener;
    private boolean isPullData;
    private int startIndex;

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public FHotAnchorsAdapter(Context mContext, List<HotAnchorBean> anchorsLists) {
        this.mContext = mContext;
        this.anchorsLists = anchorsLists;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public boolean isPullData() {
        return isPullData;
    }

    public void setPullData(boolean pullData) {
        isPullData = pullData;
    }

    @Override
    public HotAnchorsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_hot_anchors,parent,false);
        HotAnchorsViewHolder hotAnchorsViewHolder = new HotAnchorsViewHolder(itemView);
        return hotAnchorsViewHolder;
    }

    @Override
    public void onBindViewHolder(HotAnchorsViewHolder holder, final int position) {
        final int index = position+startIndex*6;
        if (index<anchorsLists.size())
        {
            HotAnchorBean itemBean= anchorsLists.get(index);
            GlideApp.with(mContext)
                    .asBitmap()
                    .load(itemBean.getRoomUrl())
                    .centerCrop()
                    .placeholder(R.drawable.userhead)
                    .error(R.drawable.userhead)
                    .into(holder.imgHead);
            holder.tvName.setText(itemBean.getRoomName());
            if (!TextUtils.isEmpty(itemBean.getAnchorTypeUrl())){
                holder.imgSign.setVisibility(View.VISIBLE);
                CommonUtils.setImageFromFile(holder.imgSign,itemBean.getAnchorTypeUrl(), NetHelper.getRootDirPath(mContext)+NetHelper.ANCHOR_TYPE_FOLDER);
            }
            if(TextUtils.equals(itemBean.getSign(),"1"))
            {
                holder.imgSign.setVisibility(View.VISIBLE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener!=null)
                        onItemClickListener.itemClick(v,index);
                }
            });

        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public class HotAnchorsViewHolder extends RecyclerView.ViewHolder {
        public HotAnchorsViewHolder(View itemView) {
            super(itemView);
        }
        TextView tvName= (TextView) itemView.findViewById(R.id.tv_anchor_name);
        RoundedImageView imgHead= (RoundedImageView) itemView.findViewById(R.id.img_anchor_head);
        ImageView imgSign = (ImageView) itemView.findViewById(R.id.img_anchor_sign);
    }
    public interface OnItemClickListener
    {
        void itemClick(View view,int position);
    }
}

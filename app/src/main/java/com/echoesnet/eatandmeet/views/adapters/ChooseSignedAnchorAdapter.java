package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * Created by Administrator on 2016/12/13.
 */

public class ChooseSignedAnchorAdapter extends BaseAdapter
{
    private Context mContext;
    private List<EaseUser> signedAnchorList;

    public ChooseSignedAnchorAdapter(Context mContext, List<EaseUser> signedAnchorList)
    {
        this.mContext = mContext;
        this.signedAnchorList = signedAnchorList;
    }

    @Override
    public int getCount()
    {
        return signedAnchorList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return signedAnchorList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Holder holder = null;
        if (convertView == null)
        {
            holder = new Holder();
            convertView = View.inflate(mContext, R.layout.item_choose_anchor,null);
            holder.imgAnchorHead = (RoundedImageView) convertView.findViewById(R.id.img_anchor_head);
            holder.tvAnchorId = (TextView) convertView.findViewById(R.id.tv_anchor_id);
            holder.tvAnchorName = (TextView) convertView.findViewById(R.id.tv_anchor_name);
            convertView.setTag(holder);
        }else
        {
            holder = (Holder) convertView.getTag();
        }
        holder.tvAnchorName.setText(signedAnchorList.get(position).getNickName());
        holder.tvAnchorId.setText("（"+signedAnchorList.get(position).getId()+"）");
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(signedAnchorList.get(position).getAvatar())
                .centerCrop()
                .placeholder(R.drawable.userhead)
                .into(holder.imgAnchorHead);

        return convertView;
    }
    class Holder
    {
        RoundedImageView imgAnchorHead;
        TextView tvAnchorName;
        TextView tvAnchorId;
    }
}

package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by Administrator on 2016/12/22.
 */

public class LiveChooseAnchorAdapter extends BaseAdapter
{
    private String TAG = LiveChooseAnchorAdapter.class.getSimpleName();
    private Context mContext;
    private List<EaseUser> anchorList;

    private String tempInitialLetter = "-1";

    public LiveChooseAnchorAdapter(Context mContext, List<EaseUser> anchorList)
    {
        this.mContext = mContext;
        this.anchorList = anchorList;
    }

    @Override
    public int getCount()
    {
        return anchorList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return anchorList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return Integer.parseInt(anchorList.get(position).getId());
    }

    class Holder
    {
        TextView tvHeader;
        TextView userName;
        TextView userId;
        RoundedImageView rivUserHeader;
        ImageView ivHot;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Holder holder;
        if (convertView == null)
        {
            holder = new Holder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.live_choose_anchor_item, null);
            holder.tvHeader = (TextView) convertView.findViewById(R.id.tv_header);
            holder.userName = (TextView) convertView.findViewById(R.id.name);
            holder.userId = (TextView) convertView.findViewById(R.id.id);
            holder.rivUserHeader = (RoundedImageView) convertView.findViewById(R.id.riv_user_header);
            holder.ivHot = (ImageView) convertView.findViewById(R.id.iv_hot);
            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }
        EaseUser user = anchorList.get(position);
        Logger.t(TAG).d("position:" + position + ",tempInitialLetter:" + tempInitialLetter + ",User:" + user.toString());

        /*if (TextUtils.isEmpty(user.getInitialLetter()) && position == 0)
        {
            this.tempInitialLetter = user.getInitialLetter();
            holder.tvHeader.setVisibility(View.VISIBLE);
        }
        else
        {
            if (this.tempInitialLetter.equals(user.getInitialLetter()))
            {
                holder.tvHeader.setVisibility(View.GONE);
            }
            this.tempInitialLetter = user.getInitialLetter();
        }*/
        if (user.getTag().equals("1"))
            holder.tvHeader.setVisibility(View.VISIBLE);
        else
            holder.tvHeader.setVisibility(View.GONE);
        /*if (position == 0 || user.getInitialLetter() != null && !user.getInitialLetter().equals(anchorList.get(position-1).getInitialLetter()))
        {
            if (TextUtils.isEmpty(user.getInitialLetter()))
            {
                holder.tvHeader.setVisibility(View.GONE);
            }
            else
            {
                holder.tvHeader.setVisibility(View.VISIBLE);
                holder.tvHeader.setText(user.getInitialLetter());
            }
        }
        else
        {
            holder.tvHeader.setVisibility(View.GONE);
        }*/


        if (TextUtils.isEmpty(user.getInitialLetter()))
        {
            holder.tvHeader.setText("热门主播");
        }
        else
        {
            holder.tvHeader.setText(user.getInitialLetter());
        }
        holder.userName.setText(user.getNickName());
        if ("1".equals(user.getUserType()))
        {
            holder.ivHot.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.ivHot.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(user.getId()))
        {
            holder.userId.setText("(" + user.getId() + ")");
        }
        else
        {
            holder.userId.setText("");
        }
        GlideApp.with(mContext)
                .asBitmap()
                .load(user.getAvatar())
                .centerCrop()
                .placeholder(R.drawable.userhead)
                .into(holder.rivUserHeader);

        return convertView;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(String section)
    {
        if(section.equals("热"))
            return 0;
        for (int i = 0; i < getCount(); i++)
        {
            String sortStr = anchorList.get(i).getInitialLetter();
            if (section.equals(sortStr))
                return i;
        }
        return -1;
    }


}

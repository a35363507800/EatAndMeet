package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.datamodel.EmojiIcon;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;

import java.util.List;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/20 0020
 * @description
 */
public class EmojiGridAdapter extends BaseAdapter
{
    private List<EmojiIcon> list;
    private Context mContext;
    private int itemCount;
    private EmojiItemClick emojiItemClick;

    public EmojiGridAdapter(List<EmojiIcon> list, Context mContext, int itemCount)
    {
        this.list = list;
        this.mContext = mContext;
        this.itemCount = itemCount;
    }

    public void setEmojiItemClick(EmojiItemClick emojiItemClick)
    {
        this.emojiItemClick = emojiItemClick;
    }

    @Override
    public int getCount()
    {
        return itemCount;
    }

    @Override
    public EmojiIcon getItem(int position)
    {
        if (position < list.size())
            return list.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final EmojiIcon emojiIcon = getItem(position);
        ImageView imageView = new ImageView(mContext);
        if (emojiIcon != null)
        {
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(CommonUtils.dp2px(mContext, 30), CommonUtils.dp2px(mContext, 30));
//            layoutParams.topMargin = CommonUtils.dp2px(mContext, 30);
//            layoutParams.bottomMargin = CommonUtils.dp2px(mContext, 20);
            imageView.setPadding(CommonUtils.dp2px(mContext, 3), CommonUtils.dp2px(mContext, 3), CommonUtils.dp2px(mContext, 3), CommonUtils.dp2px(mContext, 3));
            imageView.setLayoutParams(layoutParams);
            imageView.setImageResource(emojiIcon.getIcon());
            imageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (emojiItemClick != null)
                        emojiItemClick.itemClick(emojiIcon);
                }
            });
        }

        if (position == getCount() - 1)
        {
            imageView.setImageResource(R.drawable.delete_expression);
            imageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (emojiItemClick != null)
                        emojiItemClick.deleteClick();
                }
            });
        }


        return imageView;
    }

    public interface EmojiItemClick{
        void itemClick(EmojiIcon emojiIcon);
        void deleteClick();
    }
}

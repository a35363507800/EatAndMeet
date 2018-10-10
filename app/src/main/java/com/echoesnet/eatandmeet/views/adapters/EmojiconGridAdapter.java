package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.datamodel.EmojiIcon;
import com.echoesnet.eatandmeet.utils.IMUtils.EamSmileUtils;

import java.util.List;

public class EmojiconGridAdapter extends ArrayAdapter<EmojiIcon>
{

    private EmojiIcon.Type emojiconType;


    public EmojiconGridAdapter(Context context, int textViewResourceId, List<EmojiIcon> objects, EmojiIcon.Type emojiconType)
    {
        super(context, textViewResourceId, objects);
        this.emojiconType = emojiconType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            if (emojiconType == EmojiIcon.Type.BIG_EXPRESSION)
            {
                convertView = View.inflate(getContext(), R.layout.ease_row_big_expression, null);
            }
            else
            {
                convertView = View.inflate(getContext(), R.layout.ease_row_expression, null);
            }
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_expression);
        EmojiIcon emojicon = getItem(position);

        //if you want show a name for the icons, you can set text to R.id.tv_name
        if (emojiconType == EmojiIcon.Type.BIG_EXPRESSION)
        {
            TextView emojName = (TextView) convertView.findViewById(R.id.tv_name);
            emojName.setText(emojicon.getName());
        }
        if (EamSmileUtils.DELETE_KEY.equals(emojicon.getEmojiText()))
        {
            imageView.setImageResource(R.drawable.delete_expression);
        }
        else
        {
            if (emojicon.getIcon() != 0)
            {
                imageView.setImageResource(emojicon.getIcon());
            }
            else if (emojicon.getIconPath() != null)
            {
                GlideApp.with(getContext())
                        .load(emojicon.getIconPath())
                        .placeholder(R.drawable.ease_default_expression)
                        .into(imageView);
            }
        }

        return convertView;
    }

}

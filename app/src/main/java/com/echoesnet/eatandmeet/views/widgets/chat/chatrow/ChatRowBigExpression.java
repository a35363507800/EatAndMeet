package com.echoesnet.eatandmeet.views.widgets.chat.chatrow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.utils.GlideApp;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EaseUI;
import com.echoesnet.eatandmeet.models.datamodel.EmojiIcon;
import com.echoesnet.eatandmeet.utils.IMUtils.Constant;
import com.hyphenate.chat.EMMessage;
import com.orhanobut.logger.Logger;

/**
 * big emoji icons
 */
public class ChatRowBigExpression extends ChatRowText
{

    private ImageView imageView;


    public ChatRowBigExpression(Context context, EMMessage message, int position, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter)
    {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView()
    {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_bigexpression : R.layout.ease_row_sent_bigexpression, this);
    }

    @Override
    protected void onFindViewById()
    {
        percentageView = (TextView) findViewById(R.id.percentage);
        imageView = (ImageView) findViewById(R.id.image);
    }


    @Override
    public void onSetUpView()
    {
        String emojiconId = message.getStringAttribute(Constant.MESSAGE_ATTR_EXPRESSION_ID, null);
        EmojiIcon emojicon = null;
        if (EaseUI.getInstance().getEmojiconInfoProvider() != null)
        {
            emojicon = EaseUI.getInstance().getEmojiconInfoProvider().getEmojiconInfo(emojiconId);
        }
        Logger.t(TAG).d("---->emojicon :" + emojicon);
        if (emojicon != null)
        {
            if (emojicon.getBigIcon() != 0)
            {
                GlideApp.with(activity)
                        .asGif()
                        .load(emojicon.getBigIcon())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.ease_default_expression)
                        .into(imageView);
            }
            else if (emojicon.getBigIconPath() != null)
            {
                GlideApp.with(activity)
                        .asGif()
                        .load(emojicon.getBigIconPath())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.ease_default_expression)
                        .into(imageView);
            }
            else
            {
                imageView.setImageResource(R.drawable.ease_default_expression);
            }
        }
        else
        {
            String emojiconUrl = message.getStringAttribute(Constant.MESSAGE_ATTR_EXPRESSION_URL, null);
            Logger.t(TAG).d("emojiconUrl:" + emojiconUrl);
            GlideApp.with(activity)
                    .asGif()
                    .load(emojiconUrl)
                    .placeholder(R.drawable.ease_default_expression)
                    .into(imageView);
        }
//        handleTextMessage();
    }
}

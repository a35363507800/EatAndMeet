package com.echoesnet.eatandmeet.views.widgets.chat;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.datamodel.EmojiGroupEntity;
import com.echoesnet.eatandmeet.models.datamodel.EmojiIcon;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yqh
 * @version 1.0
 * @createDate 2017/7/17
 * @Description 聊天底部emoji模块
 */
public class ChatEmojiMenu extends LinearLayout
{

    private int emojiconColumns;
    private int bigEmojiColumns;

    private EmojiScrollTabBar tabBar;
    private EmojiIndicatorView indicatorView;
    private EmojiPageView pagerView;

    private EmojiconMenuListener listener;


    private List<EmojiGroupEntity> emojiconGroupList = new ArrayList<>();

    public ChatEmojiMenu(Context context)
    {
        super(context);
        init(context, null);
    }

    public ChatEmojiMenu(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public ChatEmojiMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        LayoutInflater.from(context).inflate(R.layout.my_widget_emojicon, this);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseEmojiconMenu);
        int defaultColumns = 6;
        emojiconColumns = ta.getInt(R.styleable.EaseEmojiconMenu_emojiconColumns, defaultColumns);
        int defaultBigColumns = 4;
        bigEmojiColumns = ta.getInt(R.styleable.EaseEmojiconMenu_bigEmojiconRows, defaultBigColumns);
        ta.recycle();
        tabBar = (EmojiScrollTabBar) findViewById(R.id.tab_bar);
        indicatorView = (EmojiIndicatorView) findViewById(R.id.cpi_pager_indicator);
        pagerView = (EmojiPageView) findViewById(R.id.pager_view);
    }

    public void init(List<EmojiGroupEntity> groupEntities)
    {
        if (groupEntities == null || groupEntities.size() == 0)
        {
            return;
        }
        for (EmojiGroupEntity groupEntity : groupEntities)
        {
            emojiconGroupList.add(groupEntity);
            tabBar.addTab(groupEntity.getIcon());
        }

        pagerView.setPagerViewListener(new EmojiconPagerViewListener());
        pagerView.init(emojiconGroupList, emojiconColumns, bigEmojiColumns);

        tabBar.setTabBarItemClickListener(new EmojiScrollTabBar.MyScrollTabBarItemClickListener()
        {
            @Override
            public void onItemClick(int position)
            {
                pagerView.setGroupPostion(position);
            }
        });

        tabBar.setOnBtnSendClickListener(new EmojiScrollTabBar.OnBtnSendClickListener()
        {
            @Override
            public void onSendClick()
            {
                if (listener != null)
                    listener.onScrollTabBarSendClicked();
            }

            @Override
            public void onAddEmojiClick()
            {
                if (listener != null)
                    listener.onAddEmojiBtnClicked();
            }
        });

    }

    private class EmojiconPagerViewListener implements EmojiPageView.EmojiconPagerViewListener
    {

        @Override
        public void onPagerViewInited(int groupMaxPageSize, int firstGroupPageSize)
        {
            indicatorView.init(groupMaxPageSize);
            indicatorView.updateIndicator(firstGroupPageSize);
            tabBar.selectedTo(0);
        }

        @Override
        public void onGroupPositionChanged(int groupPosition, int pagerSizeOfGroup)
        {
            indicatorView.updateIndicator(pagerSizeOfGroup);
            tabBar.selectedTo(groupPosition);
        }

        @Override
        public void onGroupInnerPagePostionChanged(int oldPosition, int newPosition)
        {
            indicatorView.selectTo(oldPosition, newPosition);
        }

        @Override
        public void onGroupPagePostionChangedTo(int position)
        {
            indicatorView.selectTo(position);
        }

        @Override
        public void onGroupMaxPageSizeChanged(int maxCount)
        {
            indicatorView.updateIndicator(maxCount);
        }

        @Override
        public void onDeleteImageClicked()
        {
            if (listener != null)
            {
                listener.onDeleteImageClicked();
            }
        }

        @Override
        public void onExpressionClicked(EmojiIcon emojicon)
        {
            if (listener != null)
            {
                listener.onExpressionClicked(emojicon);
            }
        }
    }

    public void addEmojiconGroup(EmojiGroupEntity groupEntity)
    {
        emojiconGroupList.add(groupEntity);
        pagerView.addEmojiconGroup(groupEntity, true);
        tabBar.addTab(groupEntity.getIconPath());
    }

    public boolean isContainEmojiconGroup(EmojiGroupEntity groupEntity)
    {
        boolean isContaine = false;
        for (EmojiGroupEntity eg : emojiconGroupList)
        {
            if (!TextUtils.isEmpty(eg.getName()))
            {
                if (groupEntity.getName().equals(eg.getName()))
                {
                    isContaine = true;
                    break;
                }
            }
        }
        return isContaine;
    }

    /**
     * set emojicon menu listener
     *
     * @param listener
     */
    public void setEmojiconMenuListener(EmojiconMenuListener listener)
    {
        this.listener = listener;
    }

    public interface EmojiconMenuListener
    {
        /**
         * on emojicon clicked
         *
         * @param emojicon
         */
        void onExpressionClicked(EmojiIcon emojicon);

        /**
         * on delete image clicked
         */
        void onDeleteImageClicked();

        void onScrollTabBarSendClicked();

        void onAddEmojiBtnClicked();

    }


}

package com.echoesnet.eatandmeet.views.widgets.chat;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.echoesnet.eatandmeet.R;
import com.hyphenate.util.DensityUtil;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/17.
 */

public class EmojiScrollTabBar extends LinearLayout
{
    private Context context;

    private IconTextView itvAddEmoji;
    private HorizontalScrollView scrollView;
    private LinearLayout tabContainer;
    private Button btnEmojiSend;

    private List<LinearLayout> tabList = new ArrayList<>();
    private MyScrollTabBarItemClickListener itemClickListener;
    private OnBtnSendClickListener onBtnSendClickListener;

    public EmojiScrollTabBar(Context context)
    {
        this(context, null);
    }

    public EmojiScrollTabBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public EmojiScrollTabBar(Context context, AttributeSet attrs, int defStyleAttr)
    {
        this(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.my_widget_emojicon_tab_bar, this);
        itvAddEmoji = (IconTextView) findViewById(R.id.itv_add_emoji);
        scrollView = (HorizontalScrollView) findViewById(R.id.scroll_view);
        tabContainer = (LinearLayout) findViewById(R.id.tab_container);
        btnEmojiSend = (Button) findViewById(R.id.btn_emoji_send);
        btnEmojiSend.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onBtnSendClickListener != null)
                    onBtnSendClickListener.onSendClick();
            }
        });
        itvAddEmoji.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onBtnSendClickListener != null)
                    onBtnSendClickListener.onAddEmojiClick();
            }
        });
    }

    /**
     * add tab
     *
     * @param icon
     */
    public void addTab(int icon)
    {
        View tabView = View.inflate(context, R.layout.my_scroll_tab_item, null);
        LinearLayout linearLayout = (LinearLayout) tabView.findViewById(R.id.linearLayout);
        ImageView imageView = (ImageView) tabView.findViewById(R.id.iv_icon);
        imageView.setImageResource(icon);
        int tabWidth = 44;
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(DensityUtil.dip2px(context, tabWidth), LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(imgParams);
        tabContainer.addView(tabView);
        tabList.add(linearLayout);
        final int position = tabList.size() - 1;
        imageView.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if (itemClickListener != null)
                {
                    itemClickListener.onItemClick(position);
                }
            }
        });
    }

    /**
     * add tab
     *
     * @param iconPath
     */
    public void addTab(String iconPath)
    {
        View tabView = View.inflate(context, R.layout.my_scroll_tab_item, null);
        LinearLayout linearLayout = (LinearLayout) tabView.findViewById(R.id.linearLayout);
        ImageView imageView = (ImageView) tabView.findViewById(R.id.iv_icon);
        imageView.setImageBitmap(BitmapFactory.decodeFile(iconPath));
        int tabWidth = 44;
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(DensityUtil.dip2px(context, tabWidth), LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(imgParams);
        tabContainer.addView(tabView);
        tabList.add(linearLayout);
        final int position = tabList.size() - 1;
        imageView.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if (itemClickListener != null)
                {
                    itemClickListener.onItemClick(position);
                }
            }
        });
    }

    /**
     * remove tab
     *
     * @param position
     */
    public void removeTab(int position)
    {
        tabContainer.removeViewAt(position);
        tabList.remove(position);
    }

    public void selectedTo(int position)
    {
        scrollTo(position);
        for (int i = 0; i < tabList.size(); i++)
        {
            if (position == i)
            {
                tabList.get(i).setBackgroundColor(ContextCompat.getColor(context, R.color.C0333));
            }
            else
            {
                tabList.get(i).setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }
        }
    }

    private void scrollTo(final int position)
    {
        int childCount = tabContainer.getChildCount();
        if (position < childCount)
        {
            scrollView.post(new Runnable()
            {
                @Override
                public void run()
                {
                    int mScrollX = tabContainer.getScrollX();
                    int childX = (int) ViewCompat.getX(tabContainer.getChildAt(position));

                    if (childX < mScrollX)
                    {
                        scrollView.scrollTo(childX, 0);
                        return;
                    }

                    int childWidth = (int) tabContainer.getChildAt(position).getWidth();
                    int hsvWidth = scrollView.getWidth();
                    int childRight = childX + childWidth;
                    int scrollRight = mScrollX + hsvWidth;

                    if (childRight > scrollRight)
                    {
                        scrollView.scrollTo(childRight - scrollRight, 0);
                        return;
                    }
                }
            });
        }
    }

    public void setTabBarItemClickListener(MyScrollTabBarItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }


    public interface MyScrollTabBarItemClickListener
    {
        void onItemClick(int position);
    }

    public void setOnBtnSendClickListener(OnBtnSendClickListener onBtnSendClickListener)
    {
        this.onBtnSendClickListener = onBtnSendClickListener;
    }

    public interface OnBtnSendClickListener
    {
        void onSendClick();

        void onAddEmojiClick();

    }
}

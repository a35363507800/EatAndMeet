package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @modifier
 * @createDate 2017/7/12
 * @description
 */

public class CustomFlowGroup extends ViewGroup
{
    private static final String TAG = CustomFlowGroup.class.getSimpleName();

    public CustomFlowGroup(Context context)
    {
        this(context, null);
    }

    public CustomFlowGroup(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public CustomFlowGroup(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // wrap_content
        int width = 0;
        int height = 0;

        // 记录每一行的宽度与高度
        int lineWidth = 0;
        int lineHeight = 0;

        // 得到内部元素的个数
        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++)
        {
            View child = getChildAt(i);
            // 测量子View的宽和高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // 得到LayoutParams
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();

            // 子View占据的宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin
                    + lp.rightMargin;
            // 子View占据的高度
            int childHeight = child.getMeasuredHeight() + lp.topMargin
                    + lp.bottomMargin;

            // 换行
            if (lineWidth + childWidth > sizeWidth - getPaddingLeft()
                    - getPaddingRight())
            {
                // 对比得到最大的宽度
                width = Math.max(width, lineWidth);
                // 重置lineWidth
                lineWidth = childWidth;
                // 记录行高
                height += lineHeight;
                lineHeight = childHeight;
            } else
            // 未换行
            {
                // 叠加行宽
                lineWidth += childWidth;
                // 得到当前行最大的高度
                lineHeight = Math.max(lineHeight, childHeight);
            }
            // 最后一个控件
            if (i == cCount - 1)
            {
                width = Math.max(lineWidth, width);
                height += lineHeight;
            }
        }

        setMeasuredDimension(
                //
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width
                        + getPaddingLeft() + getPaddingRight(),
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height
                        + getPaddingTop() + getPaddingBottom()//
        );
    }

    /**
     * 存储所有的View
     */
    private List<List<View>> mAllViews = new ArrayList<List<View>>();
    /**
     * 每一行的高度
     */
    private List<Integer> mLineHeight = new ArrayList<Integer>();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        mAllViews.clear();
        mLineHeight.clear();

        // 当前ViewGroup的宽度
        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;

        List<View> lineViews = new ArrayList<View>();

        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++)
        {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            // 如果需要换行
            if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin > width
                    - getPaddingLeft() - getPaddingRight())
            {
                // 记录LineHeight
                mLineHeight.add(lineHeight);
                // 记录当前行的Views
                mAllViews.add(lineViews);

                // 重置我们的行宽和行高
                lineWidth = 0;
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
                // 重置我们的View集合
                lineViews = new ArrayList<View>();
            }
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin
                    + lp.bottomMargin);
            lineViews.add(child);

        }// for end
        // 处理最后一行
        mLineHeight.add(lineHeight);
        mAllViews.add(lineViews);

        // 设置子View的位置

        int left = getPaddingLeft();
        int top = getPaddingTop();

        // 行数
        int lineNum = mAllViews.size();

        for (int i = 0; i < lineNum; i++)
        {
            // 当前行的所有的View
            lineViews = mAllViews.get(i);
            lineHeight = mLineHeight.get(i);

            for (int j = 0; j < lineViews.size(); j++)
            {
                View child = lineViews.get(j);
                // 判断child的状态
                if (child.getVisibility() == View.GONE)
                {
                    continue;
                }

                MarginLayoutParams lp = (MarginLayoutParams) child
                        .getLayoutParams();

                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                // 为子View进行布局
                child.layout(lc, tc, rc, bc);

                left += child.getMeasuredWidth() + lp.leftMargin
                        + lp.rightMargin;
            }
            left = getPaddingLeft();
            top += lineHeight;
        }

    }

    /**
     * 与当前ViewGroup对应的LayoutParams
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new MarginLayoutParams(getContext(), attrs);
    }

    public void setData(final List<String> list, Context context, String selectedContent, final ViewOnclickListener listener)
    {
        for (int i = 0; i < list.size(); i++)
        {
            CustomFlowView flowView = new CustomFlowView(context);
            if(list.get(i).equals(selectedContent))
            {
                flowView.setText(list.get(i));
                flowView.setTextColor(ContextCompat.getColor(context, R.color.C0412));
                flowView.setBackgroundResource(R.drawable.round_c0412_bg_hollow);
                flowView.setSelected(true);
            } else
            {
                flowView.setText(list.get(i));
                flowView.setTextColor(ContextCompat.getColor(context, R.color.C0323));
                flowView.setBackgroundResource(R.drawable.round_c0323_bg_hollow);
                flowView.setSelected(false);
            }
            addView(flowView, getViewLayoutParams(context));

            flowView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (listener != null)
                    {
                        listener.onClickCallback(v);
                    }
                }
            });
        }
    }

    public void setData1(final List<String> list, Context context, String selectedContent, final ViewOnclickListener listener)
    {

        switch (selectedContent)
        {
            case "1":
                selectedContent = "离我最近";
                break;
            case "2":
                selectedContent = "评价最好";
                break;
            case "3":
                selectedContent = "价格最低";
                break;
            case "4":
                selectedContent = "价格最高";
                break;
            default:
                selectedContent = "";
                break;


        }

        for (int i = 0; i < list.size(); i++)
        {
            CustomFlowView flowView = new CustomFlowView(context);
            if(list.get(i).equals(selectedContent))
            {
                flowView.setText(list.get(i));
                flowView.setTextColor(ContextCompat.getColor(context, R.color.C0412));
                flowView.setBackgroundResource(R.drawable.round_c0412_bg_hollow);
                flowView.setSelected(true);
            } else
            {
                flowView.setText(list.get(i));
                flowView.setTextColor(ContextCompat.getColor(context, R.color.C0323));
                flowView.setBackgroundResource(R.drawable.round_c0323_bg_hollow);
                flowView.setSelected(false);
            }
            addView(flowView, getViewLayoutParams(context));

            flowView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (listener != null)
                    {
                        listener.onClickCallback(v);
                    }
                }
            });
        }
    }

    public void setData(final String[] arr, Context context, String selectedContent, final ViewOnclickListener listener)
    {
        for (int i = 0; i < arr.length; i++)
        {
            CustomFlowView flowView = new CustomFlowView(context);
            if(arr[i].equals(selectedContent))
            {
                flowView.setText(arr[i]);
                flowView.setTextColor(ContextCompat.getColor(context, R.color.C0412));
                flowView.setBackgroundResource(R.drawable.round_c0412_bg_hollow);
            } else
            {
                flowView.setText(arr[i]);
                flowView.setTextColor(ContextCompat.getColor(context, R.color.C0323));
                flowView.setBackgroundResource(R.drawable.round_c0323_bg_hollow);
            }

            addView(flowView, getViewLayoutParams(context));
            flowView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (listener != null)
                    {
                        listener.onClickCallback(v);
                    }
                }
            });
        }
    }

    public void reSetData(final List<String> list, Context context, final ViewOnclickListener listener)
    {
        for (int i = 0; i < list.size(); i++)
        {
            CustomFlowView flowView = new CustomFlowView(context);
            flowView.setText(list.get(i));
            flowView.setTextColor(ContextCompat.getColor(context, R.color.C0323));
            flowView.setBackgroundResource(R.drawable.round_c0323_bg_hollow);
            addView(flowView, getViewLayoutParams(context));
            flowView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (listener != null)
                    {
                        listener.onClickCallback(v);
                    }
                }
            });
        }
    }

    private ViewOnclickListener listener;

    public interface ViewOnclickListener
    {
        void onClickCallback(View v);
    }

    public void setViewOnclickListener(ViewOnclickListener listener)
    {
        this.listener = listener;
    }

    public ViewGroup.MarginLayoutParams getViewLayoutParams(Context mAct)
    {
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = CommonUtils.dp2px(mAct, 6);
        lp.rightMargin = CommonUtils.dp2px(mAct, 6);
        lp.topMargin = CommonUtils.dp2px(mAct, 9);
        lp.bottomMargin = CommonUtils.dp2px(mAct, 9);
        return lp;
    }
}

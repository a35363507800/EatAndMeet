package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.datamodel.DefaultEmojiconDatas;
import com.echoesnet.eatandmeet.models.datamodel.EmojiIcon;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.EmojiGridAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/20 0020
 * @description 动态emojiview
 */
public class TrendsEmojiView extends LinearLayout
{
    private ViewPager viewPager;
    private LinearLayout navigationLinea;
    private Context mContext;
    private List<View> viewList;
    private List<ImageView> dots;
    private int emojiconColumns = 6;
    private int emojiconRows = 3;


    private PagerAdapter viewPagerAdapter;
    private EmojiItemClick emojiItemClick;

    public TrendsEmojiView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        this.setOrientation(VERTICAL);
        initView();
    }

    public void setEmojiItemClick(EmojiItemClick emojiItemClick)
    {
        this.emojiItemClick = emojiItemClick;
    }

    private void initView()
    {
        viewList = new ArrayList<>();
        dots = new ArrayList<>();
        //navigationLinea
        navigationLinea = new LinearLayout(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        navigationLinea.setOrientation(HORIZONTAL);
        navigationLinea.setGravity(Gravity.CENTER);
        navigationLinea.setLayoutParams(layoutParams);
        //viewPager
        viewPager = new ViewPager(mContext);
        LinearLayout.LayoutParams vpLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        viewPager.setLayoutParams(vpLayoutParams);
        viewPagerAdapter = new PagerAdapter()
        {

            @Override
            public int getCount()
            {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object)
            {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object)
            {
                container.removeView(viewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position)
            {
                container.addView(viewList.get(position));
                return viewList.get(position);
            }
        };
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                for (int i = 0; i < navigationLinea.getChildCount(); i++)
                {
                    ImageView imageView = (ImageView) navigationLinea.getChildAt(i);
                    if (i == position)
                        imageView.setImageResource(R.drawable.round_cornor_36_mc1_bg);
                    else
                        imageView.setImageResource(R.drawable.round_cornor_36_fc4_bg);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
        viewPager.setAdapter(viewPagerAdapter);
        viewList.addAll(getGroupGridViews(Arrays.asList(DefaultEmojiconDatas.getData())));
        for (View view : viewList)
        {
            setListViewHeightBasedOnChildren((GridView) view);
        }
        viewPagerAdapter.notifyDataSetChanged();
        this.addView(viewPager);
        this.addView(navigationLinea);
    }

    public List<View> getGroupGridViews(List<EmojiIcon> emojiconList)
    {
        System.out.print("emojiconList: " + emojiconList);
        int itemSize = emojiconColumns * emojiconRows;
        int totalSize = emojiconList.size();
        int pageSize = totalSize % itemSize == 0 ? totalSize / itemSize : totalSize / itemSize + 1;
        List<View> views = new ArrayList<View>();
        for (int i = 0; i < pageSize; i++)
        {
            GridView gv = new GridView(mContext);
            LinearLayout.LayoutParams gvLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            gv.setLayoutParams(gvLayoutParams);
            gv.setGravity(Gravity.CENTER);
            gv.setVerticalSpacing(CommonUtils.dp2px(mContext,10));
            gv.setNumColumns(emojiconColumns);
            gv.setSelector(R.color.transparent);
            List<EmojiIcon> list = new ArrayList<EmojiIcon>();
            if (i != pageSize - 1)
            {
                list.addAll(emojiconList.subList((i * itemSize - 1) < 0?0:i * itemSize - 1, (i + 1) * itemSize - 1));
            } else
            {
                list.addAll(emojiconList.subList((i * itemSize - 1) < 0?0:i * itemSize - 1, totalSize));
            }
            final EmojiGridAdapter gridAdapter = new EmojiGridAdapter(list, mContext,itemSize);
            gv.setAdapter(gridAdapter);
            gridAdapter.setEmojiItemClick(new EmojiGridAdapter.EmojiItemClick()
            {
                @Override
                public void itemClick(EmojiIcon emojiIcon)
                {
                    if (emojiItemClick != null)
                        emojiItemClick.itemClick(emojiIcon);
                }

                @Override
                public void deleteClick()
                {
                    if (emojiItemClick != null)
                        emojiItemClick.deleteClick();
                }
            });
            ImageView imageView = new ImageView(mContext);
            LinearLayout.LayoutParams layoutParams = new LayoutParams(20, 20);
            layoutParams.rightMargin = CommonUtils.dp2px(mContext,8);
            layoutParams.topMargin = CommonUtils.dp2px(mContext,2);
            layoutParams.bottomMargin = CommonUtils.dp2px(mContext,3);
            imageView.setLayoutParams(layoutParams);
            imageView.setImageDrawable(ContextCompat.getDrawable(mContext, i == 0 ? R.drawable.round_cornor_36_mc1_bg : R.drawable.round_cornor_36_fc4_bg));
            navigationLinea.addView(imageView);
            views.add(gv);
        }
        return views;
    }
    private   void setListViewHeightBasedOnChildren(GridView listView) {
        // 获取listview的adapter
        EmojiGridAdapter listAdapter = (EmojiGridAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int col = 4;// listView.getNumColumns();
        int totalHeight = 0;
        // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加
        for (int i = 0; i < listAdapter.getCount(); i += col) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight();
        }
        Logger.t("emojiView").d("total>>" + totalHeight);
        // 获取listview的布局参数
        LinearLayout.LayoutParams params = (LayoutParams) listView.getLayoutParams();
        // 设置高度
        params.height = totalHeight;
        listView.setPadding(0,CommonUtils.dp2px(mContext,10),0,CommonUtils.dp2px(mContext,10));
        // 设置margin
        // 设置参数
        listView.setLayoutParams(params);
        listAdapter.notifyDataSetChanged();
        LinearLayout.LayoutParams layoutParams = (LayoutParams) viewPager.getLayoutParams();
        layoutParams.height = layoutParams.height > totalHeight + CommonUtils.dp2px(mContext,5)?layoutParams.height:totalHeight + CommonUtils.dp2px(mContext,5);
        layoutParams.gravity = Gravity.CENTER;
        viewPager.setLayoutParams(layoutParams);
    }

    public interface EmojiItemClick{
        void itemClick(EmojiIcon emojiBean);
        void deleteClick();
    }
}

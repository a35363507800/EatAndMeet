package com.panxw.android.imageindicator;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * user guide, image indicator
 *
 * @author savant-pan
 */
public class ImageIndicatorView extends RelativeLayout
{
    private LinearLayout llPageCount;
    private TextView tvCurrentPage;
    private TextView tvFinalPage;
    private MyPagerAdapter myPagerAdapter;

    /**
     * ViewPager
     */
    private ViewPager viewPager;
    /**
     * anchor container
     */
    private LinearLayout indicateLayout;

    /**
     * left button
     */
    private Button leftButton;

    /**
     * right button
     */
    private Button rightButton;

    /**
     * page vies list
     */
    public List<View> viewList = new ArrayList<View>();

    private Handler refreshHandler;

    /**
     * item changed listener
     */
    private OnItemChangeListener onItemChangeListener;

    /**
     * item clicked listener
     */
    private OnItemClickListener onItemClickListener;
    /**
     * page total count
     */
    private int totelCount = 0;
    /**
     * current page
     */
    private int currentIndex = 0;

    /**
     * cycle list arrow anchor
     */
    public static final int INDICATE_ARROW_ROUND_STYLE = 0;

    /**
     * user guide anchor
     */
    public static final int INDICATE_USERGUIDE_STYLE = 1;

    /**
     * 没有button的style
     */
    public static final int INDICATE_NO_BUTTONS_STYLE = 2;
    /**
     * INDICATOR style
     */
    private int indicatorStyle = INDICATE_NO_BUTTONS_STYLE;

    /**
     * latest scroll time
     */
    private long refreshTime = 0l;

    private int downX, downY;

    /**
     * is show  indicator
     */
    private boolean isShowIndicator = true;
    /**
     *  默认缩小的padding值
     */
    private  int sWidthPadding;

    private   int sHeightPadding;

    /**
     *  show as gallery
     */
    private boolean isGallery = false;

    /**
     * set is show  indicator
     */
    public void setShowIndicator(boolean showIndicator) {
        isShowIndicator = showIndicator;
    }

    /**
     * item changed callback
     */
    public interface OnItemChangeListener
    {
        void onPosition(int position, int totalCount);
    }

    /**
     * item clicked callback
     */
    public interface OnItemClickListener
    {
        void OnItemClick(View view, int position);
    }

    public ImageIndicatorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.init(context);
    }

    public ImageIndicatorView(Context context)
    {
        super(context);
        this.init(context);
    }

    /**
     * @param context
     */
    private void init(Context context)
    {
        LayoutInflater.from(context).inflate(R.layout.image_indicator_layout, this);
        this.viewPager = (ViewPager) findViewById(R.id.view_pager);
        this.indicateLayout = (LinearLayout) findViewById(R.id.image_indicater_layout);
        this.leftButton = (Button) findViewById(R.id.left_button);
        this.rightButton = (Button) findViewById(R.id.right_button);

        this.llPageCount = (LinearLayout) findViewById(R.id.ll_page_count);
        this.tvCurrentPage = (TextView) findViewById(R.id.tv_current_page);
        this.tvFinalPage = (TextView) findViewById(R.id.tv_final_page);

        this.leftButton.setVisibility(View.GONE);
        this.rightButton.setVisibility(View.GONE);

        this.viewPager.addOnPageChangeListener(new PageChangeListener());
        this.viewPager.setOffscreenPageLimit(3);

        final ArrowClickListener arrowClickListener = new ArrowClickListener();
        this.leftButton.setOnClickListener(arrowClickListener);
        this.rightButton.setOnClickListener(arrowClickListener);

        this.refreshHandler = new ScrollIndicateHandler(ImageIndicatorView.this);
        sWidthPadding = 48;
        sHeightPadding = 64;
    }

    /**
     * 是否显示为画廊模式
     * @param gallery
     * @param width item宽度
     */
    public void setGallery(boolean gallery,int width,int height)
    {
        isGallery = gallery;
        if (gallery){
            RelativeLayout.LayoutParams layoutParams = (LayoutParams) viewPager.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;
            viewPager.setLayoutParams(layoutParams);
            RelativeLayout.LayoutParams layoutParams1 = (LayoutParams) indicateLayout.getLayoutParams();
            layoutParams1.addRule(BELOW,R.id.view_pager);
            layoutParams1.bottomMargin = 0;
            indicateLayout.setLayoutParams(layoutParams1);
            indicateLayout.setBackgroundColor(Color.parseColor("#f2f2f2"));
        }
    }



    /**
     * get ViewPager object
     */
    public ViewPager getViewPager()
    {
        return viewPager;
    }

    /**
     * get current index
     */
    public int getCurrentIndex()
    {
        return this.currentIndex;
    }

    /**
     * git view count
     */
    public int getTotalCount()
    {
        return viewList.size();
    }

    /**
     * get latest scroll time
     */
    public long getRefreshTime()
    {
        return this.refreshTime;
    }

    /**
     * add single View
     *
     * @param view
     */
    public void addViewItem(View view)
    {
        final int position = viewList.size();
        view.setOnClickListener(new ItemClickListener(position > 0 ? position - 1:0));
        this.viewList.add(view);
    }

    /**
     * set ItemClickListener
     */
    private class ItemClickListener implements OnClickListener
    {
        private int position = 0;

        public ItemClickListener(int position)
        {
            this.position = position;
        }

        @Override
        public void onClick(View view)
        {
            if (onItemClickListener != null)
            {
                onItemClickListener.OnItemClick(view, (currentIndex - 1) < 0 ? 0:currentIndex - 1);
            }
        }
    }

    /**
     * set Drawable array
     *
     * @param resArray Drawable array
     */
    public void setupLayoutByDrawable(final Integer resArray[])
    {
        if (resArray == null)
            throw new NullPointerException();

        this.setupLayoutByDrawable(Arrays.asList(resArray));
    }

    /**
     * set Drawable list
     *
     * @param resList Drawable list
     */
    public void setupLayoutByDrawable(final List<Integer> resList)
    {
        if (resList == null)
            throw new NullPointerException();

        final int len = resList.size();
        if (len > 0)
        {
            for (int index = 0; index < len; index++)
            {
                final View pageItem = new ImageView(getContext());
                pageItem.setBackgroundResource(resList.get(index));
                addViewItem(pageItem);
            }
        }
    }

    /**
     * set image url list
     */
    public void setupLayoutByImageUrl(List<String> urlList)
    {
    }

    /**
     * set show item current
     *
     * @param index postion
     */
    public void setCurrentItem(int index)
    {
        this.currentIndex = index;
    }

    /**
     * set anchor style, default INDICATOR_ARROW_ROUND_STYLE
     *
     * @param style INDICATOR_USERGUIDE_STYLE or INDICATOR_ARROW_ROUND_STYLE
     */
    public void setIndicateStyle(int style)
    {
        this.indicatorStyle = style;
    }

    /**
     * add OnItemChangeListener
     *
     * @param onItemChangeListener callback
     */
    public void setOnItemChangeListener(OnItemChangeListener onItemChangeListener)
    {
        if (onItemChangeListener == null)
        {
            throw new NullPointerException();
        }
        this.onItemChangeListener = onItemChangeListener;
    }

    /**
     * add setOnItemClickListener
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }

    public void hideNavigateButton()
    {
        this.leftButton.setVisibility(View.GONE);
        this.rightButton.setVisibility(View.GONE);
    }
    /**
     * show
     */
    public void show()
    {
        this.totelCount = viewList.size() > 2 ? viewList.size() - 2 : viewList.size();
        Log.d("showIndicator","totelCount >" + totelCount + "|viewList>" + viewList.size());
        if (isShowIndicator){
            indicateLayout.removeAllViews();
            final LayoutParams params = (LayoutParams) indicateLayout.getLayoutParams();
            if (INDICATE_USERGUIDE_STYLE == this.indicatorStyle)
            {// user guide
                params.bottomMargin = 45;
            }
            this.indicateLayout.setLayoutParams(params);
            //init anchor
            if (totelCount > 1){
                for (int index = 0; index < this.totelCount; index++)
                {
                    final View indicater = new ImageView(getContext());
                    this.indicateLayout.addView(indicater, index);
                }
                this.refreshHandler.sendEmptyMessage(currentIndex);
            }
        }
        // set data for viewpager
        if (myPagerAdapter == null)
        {
            myPagerAdapter = new MyPagerAdapter(this.viewList);
            this.viewPager.setAdapter(myPagerAdapter);
        }else
        {
            myPagerAdapter.notifyDataSetChanged();
        }
        this.viewPager.setCurrentItem(viewList.size() > 1 ? 1: currentIndex, false);
    }

    public void showPageCountView()
    {
        this.totelCount = viewList.size() > 2 ? viewList.size() - 2 : viewList.size();
        if(totelCount > 1)
        {
            llPageCount.setVisibility(VISIBLE);
            indicateLayout.setVisibility(GONE);
        }
        this.tvFinalPage.setText(totelCount + "");
    }

    /**
     * deal clicked event
     */
    private class ArrowClickListener implements OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            if (view == leftButton)
            {
                if (currentIndex >= (totelCount - 1))
                {
                    return;
                }
                else
                {
                    viewPager.setCurrentItem(currentIndex + 1, true);
                }
            }
            else
            {
                if (totelCount <= 0)
                {
                    return;
                }
                else
                {
                    viewPager.setCurrentItem(currentIndex - 1, true);
                }
            }
        }
    }

    /**
     * deal page change
     */
    private class PageChangeListener implements OnPageChangeListener
    {
        @Override
        public void onPageSelected(int index)
        {
            currentIndex = index;
            refreshTime = System.currentTimeMillis();
            if (index == viewList.size() - 1)
            {
                //在position4左滑且左滑positionOffset百分比接近1时，偷偷替换为position1（原本会滑到position5）
                viewPager.setCurrentItem(1, false);
            }
            if (isShowIndicator)
            refreshHandler.sendEmptyMessage(index);
            //Log.i("info", "选中 index > " + index);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
            //Log.i("onPageScrolledinfo", "position> " + position +"positionOffset>>" + positionOffset +"positionOffsetPixels>>"  + positionOffsetPixels );
            if (viewList.size() > 1)
            {
                if (position == viewList.size() - 1 && positionOffsetPixels == 0) {
                    //在position4左滑且左滑positionOffset百分比接近1时，偷偷替换为position1（原本会滑到position5）
                    viewPager.setCurrentItem(1, false);
                } else if (position == 0 && positionOffsetPixels == 0) {
                    //在position1右滑且右滑百分比接近0时，偷偷替换为position4（原本会滑到position0）
                    viewPager.setCurrentItem(viewList.size() - 2, false);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0)
        {
        }
    }

    /**
     * refresh indicate anchor 设置标志点--wb
     */
    protected void refreshIndicateView()
    {
        this.refreshTime = System.currentTimeMillis();
        for (int index = 0; index < totelCount; index++)
        {
            final ImageView imageView = (ImageView) this.indicateLayout.getChildAt(index);
            if (imageView == null)
                return;
            if (currentIndex > 0 && this.currentIndex - 1 == index)
            {
                imageView.setBackgroundResource(R.drawable.image_indicator_dot_focus);
            }
            else
            {
                imageView.setBackgroundResource(R.drawable.image_indicator_dot);
            }
            tvCurrentPage.setText(currentIndex+ "");
        }

        if (INDICATE_USERGUIDE_STYLE == this.indicatorStyle)
        {
            // no arrow when user guide style
            this.leftButton.setVisibility(View.GONE);
            this.rightButton.setVisibility(View.GONE);
        }
        else if (INDICATE_NO_BUTTONS_STYLE==this.indicatorStyle)
        {
            this.leftButton.setVisibility(View.GONE);
            this.rightButton.setVisibility(View.GONE);
        }
        else
        {
            // set arrow style
            if (totelCount <= 1)
            {
                leftButton.setVisibility(View.GONE);
                rightButton.setVisibility(View.GONE);
            }
            else if (totelCount == 2)
            {
                if (currentIndex == 0)
                {
                    leftButton.setVisibility(View.VISIBLE);
                    rightButton.setVisibility(View.GONE);
                }
                else
                {
                    leftButton.setVisibility(View.GONE);
                    rightButton.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                if (currentIndex == 0)
                {
                    leftButton.setVisibility(View.VISIBLE);
                    rightButton.setVisibility(View.GONE);
                }
                else if (currentIndex == (totelCount - 1))
                {
                    leftButton.setVisibility(View.GONE);
                    rightButton.setVisibility(View.VISIBLE);
                }
                else
                {
                    leftButton.setVisibility(View.VISIBLE);
                    rightButton.setVisibility(View.VISIBLE);
                }
            }
        }
        if (this.onItemChangeListener != null)
        {// notify item state changed
            try
            {
                this.onItemChangeListener.onPosition(this.currentIndex, this.totelCount);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * ScrollIndicateHandler
     */
    private static class ScrollIndicateHandler extends Handler
    {
        private final WeakReference<ImageIndicatorView> scrollIndicateViewRef;

        public ScrollIndicateHandler(ImageIndicatorView scrollIndicateView)
        {
            this.scrollIndicateViewRef = new WeakReference<ImageIndicatorView>(
                    scrollIndicateView);

        }

        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            ImageIndicatorView scrollIndicateView = scrollIndicateViewRef.get();
            if (scrollIndicateView != null)
            {
                scrollIndicateView.refreshIndicateView();
            }
        }
    }

    private class MyPagerAdapter extends PagerAdapter
    {
        private List<View> pageViews = new ArrayList<View>();

        public MyPagerAdapter(List<View> pageViews)
        {
            this.pageViews = pageViews;
        }

        @Override
        public int getCount()
        {
            return pageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1)
        {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object)
        {
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public Object instantiateItem(View arg0, int arg1)
        {
            ((ViewPager) arg0).addView(pageViews.get(arg1));
            return pageViews.get(arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1)
        {

        }

        @Override
        public Parcelable saveState()
        {
            return null;
        }

        @Override
        public void startUpdate(View arg0)
        {

        }

        @Override
        public void finishUpdate(View arg0)
        {

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) { // 分发TouchEvent
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 让当前viewpager的父控件不去拦截touch事件
                getParent().requestDisallowInterceptTouchEvent(true);
                downX = (int) ev.getX();
                downY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getX();
                int moveY = (int) ev.getY();
                if (Math.abs(moveX - downX) >= Math.abs(moveY - downY)) {
                    // 滑动轮播图
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    // 刷新listview
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) { // 拦截TouchEvent
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) { // 处理TouchEvent

        return super.onTouchEvent(event);
    }

}

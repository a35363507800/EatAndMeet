package com.ss007.swiprecycleview;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.List;

public class SwipeRefreshRecycleView extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener
{
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    @Nullable
    private RecyclerView.LayoutManager manager;
    private Context context;
    private View emptyView;
    public SwipeRefreshRecycleView(Context context)
    {
        this(context, null);
    }

    public SwipeRefreshRecycleView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public SwipeRefreshRecycleView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context);
    }

    private void init(Context context)
    {
        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_recycler, this, false);
        mRecyclerView = (RecyclerView) inflate.findViewById(R.id.recycle_view);
        mRefreshLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.container);
        mRecyclerView.setVerticalScrollBarEnabled(true);
        mRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setItemAnimator(new DefaultAnimator());
        mRecyclerView.addOnScrollListener(new FinishScrollListener());
        addView(inflate, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * Set the colors used in the progress animation. The first
     * color will also be the color of the bar that grows in response to a user
     * swipe gesture.
     */

    public void setColorSchemeColors(@ColorInt int... colors)
    {
        mRefreshLayout.setColorSchemeColors(colors);
    }

    public void setLayoutManager(final RecyclerView.LayoutManager manager)
    {
        this.manager = manager;
        if (manager instanceof GridLayoutManager)
        {
            ((GridLayoutManager) manager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
            {
                @Override
                public int getSpanSize(int position)
                {
                    switch (adapter.getItemViewType(position))
                    {
                        case AdapterLoader.TYPE_BOTTOM:
                            return ((GridLayoutManager) manager).getSpanCount();
                        default:
                            return (spanSizeCallBack != null ? spanSizeCallBack.getSpanSize(position) : 0) == 0 ? 1 : spanSizeCallBack.getSpanSize(position);
                    }

                }
            });
        }

        mRecyclerView.setLayoutManager(manager);
    }

    @SuppressWarnings("unused")
    public void setItemAnimator(RecyclerView.ItemAnimator animator)
    {
        mRecyclerView.setItemAnimator(animator);

    }

    /**
     * Enable to pullRefresh
     *
     * @param enable whether can pull refresh or not...
     */
    @SuppressWarnings("unused")
    public void setPullRefreshEnable(boolean enable)
    {
        mRefreshLayout.setEnabled(enable);
    }


    private RefreshRecycleAdapter adapter;

    public void setAdapter(RefreshRecycleAdapter adapter)
    {
        this.adapter = adapter;
        mRecyclerView.setAdapter(adapter);
    }

    public void setRefresh(final boolean refresh)
    {
        mRecyclerView.post(new Runnable()
        {
            @Override
            public void run()
            {
                mRefreshLayout.setRefreshing(refresh);
            }
        });
    }



    public void init(RefreshRecycleAdapter testReAdapter, final View emptyView)
    {

        this.adapter = testReAdapter;

        LinearLayoutManager cManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
        setAdapter(testReAdapter);
        setLayoutManager(cManager);

        new android.os.Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                setEmptyView(emptyView);
            }
        }, 900);

    }

    @Override
    public void onRefresh()
    {
        if (adapter!=null&&adapter.getLoadMoreListener()!=null)
        {
            adapter.getLoadMoreListener().onRefresh();
        }
    }

    public RecyclerView getRecycle()
    {
        return mRecyclerView;
    }

    private class FinishScrollListener extends RecyclerView.OnScrollListener
    {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
        {
            if (null != scrollListener)
            {
                scrollListener.onScrolled(SwipeRefreshRecycleView.this, dx, dy);
            }
            if (null == manager)
            {
                throw new RuntimeException("you should call setLayoutManager() first!!");
            }
            if (null == adapter)
            {
                throw new RuntimeException("you should call setAdapter() first!!");
            }
            if (manager instanceof LinearLayoutManager)
            {
                int lastCompletelyVisibleItemPosition = ((LinearLayoutManager) manager).findLastCompletelyVisibleItemPosition();

                if (adapter.getItemCount() > 1 && lastCompletelyVisibleItemPosition >= adapter.getItemCount() - 1 && adapter.isHasMore())
                {
                    adapter.isLoadingMore();
//                    if (null != listener) {
//                        listener.onLoadMore();
//                    }
                }
//                int position = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
//                if (lastTitlePos == position) {
//                    return;
//                }
//                lastTitlePos = position;
            }
            if (manager instanceof StaggeredGridLayoutManager)
            {
                int[] itemPositions = new int[2];
                ((StaggeredGridLayoutManager) manager).findLastVisibleItemPositions(itemPositions);

                int lastVisibleItemPosition = (itemPositions[1] != 0) ? ++itemPositions[1] : ++itemPositions[0];

                if (lastVisibleItemPosition >= adapter.getItemCount() && adapter.isHasMore())
                {
                    adapter.isLoadingMore();
//                    if (null != listener) {
//                        listener.onLoadMore();
//                    }
                }
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState)
        {
            super.onScrollStateChanged(recyclerView, newState);
        }
    }

    private OnScrollListener scrollListener;

    public void setOnScrollListener(OnScrollListener listener)
    {
        this.scrollListener = listener;
    }

    public interface OnScrollListener
    {
        void onScrolled(SwipeRefreshRecycleView recyclerView, int dx, int dy);
    }

    /**
     * you should call this method when you want to specified SpanSize.
     *
     * @param spanSizeCallBack
     */
    public void setSpanSizeCallBack(@NonNull SpanSizeCallBack spanSizeCallBack)
    {
        this.spanSizeCallBack = spanSizeCallBack;
    }

    @Nullable
    private SpanSizeCallBack spanSizeCallBack;

    public interface SpanSizeCallBack
    {
        int getSpanSize(int position);
    }


    private void checkIfEmpty()
    {
        if (emptyView != null && mRecyclerView.getAdapter() != null)
        {
            final boolean emptyViewVisible =
                    mRecyclerView.getAdapter().getItemCount() == 0;
            emptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);

        }
    }

    public void setEmptyView(View emptyView)
    {
        this.emptyView = emptyView;
        checkIfEmpty();
    }

    private final RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver()
    {
        @Override
        public void onChanged()
        {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount)
        {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount)
        {
            checkIfEmpty();
        }
    };
}

package com.ss007.swiprecycleview;

import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;


public interface AdapterLoader<T>
{
    //可以替代枚举
    @IntDef(flag = true, value = {STATE_LOADING,STATE_LASTED,STATE_ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public  @interface LoadState
    {
    }

    /**
     * state about load more..
     */
    static final int STATE_LOADING = 1;
    static final int STATE_LASTED = 2;
    static final int STATE_ERROR = 3;

    int TYPE_BOTTOM = 400;



    /**
     * If you want to use custom loading view call this method to add your specified layout !
     *
     * @param view the specified bottom layout
     */
    void setLoadMoreView(View view);

    /**
     * This method should be called  when you load more !
     *
     * @param holder    the current holder.
     * @param loadState the current state.
     */
    void onBottomViewHolderBind(RecyclerView.ViewHolder holder, int loadState);

    /**
     * If you want to create the specified bottom layout ,you should implements this method to create your own bottomViewHolder
     *
     * @param loadMore whether is loadingMore or not..
     */
    RecyclerView.ViewHolder onBottomViewHolderCreate(View loadMore);

    void onRefresh();

    boolean isHasMore();

    void isLoadingMore();

    void loadMoreError();

    /**
     * You can call this method to add data to RecycleView,if you want to append data,you should call {@link #appendList(List)}
     *
     * @param data the data you want to add
     */
    void setList(List<T> data);
    /**
     * @param data the data you want to add
     */
    void appendList(List<T> data);

    /**
     * @param position the current pos .
     * @return the current Type.
     */
    int getItemViewTypes(int position);

    /**
     * @param holder   current holder.
     * @param position current pos.
     */
    void onViewHolderBind(RecyclerView.ViewHolder holder, int position);

    RecyclerView.ViewHolder onViewHolderCreate(ViewGroup parent, int viewType);

    /**
     * Return the current size about {@link RefreshRecycleAdapter#list}.
     *
     * @return current list size!
     */
    int getItemRealCount();

    interface OnItemClickListener
    {
        void onItemClick(View itemView, int position);
    }

    interface OnItemLongClickListener
    {
        boolean onItemLongClick(View itemView, int position);
    }

    interface OnRefreshLoadMoreListener
    {
        void onRefresh();

        void onLoadMore();
    }

}

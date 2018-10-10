package com.ss007.swiprecycleview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ss007.swiprecycleview.hoders.BottomViewHolder;

import java.util.ArrayList;
import java.util.List;


public abstract class RefreshRecycleAdapter<T> extends RecyclerView.Adapter implements AdapterLoader<T>
{
    private View loadMore;
    private int loadState;
    private boolean isHasMore=true;
    private List<T> list;

    public OnRefreshLoadMoreListener getLoadMoreListener()
    {
        return refreshLoadMoreListener;
    }

    public RefreshRecycleAdapter(List<T> dataSource)
    {
        list=new ArrayList<>();
        setList(dataSource);
    }


    public List<T> getList()
    {
        return list;
    }

    @Override
    public final void setList(List<T> data)
    {
        if (data == null)
        {
            return;
        }
        if (list != null)
        {
            list.clear();
        }
        appendList(data);
    }

    @Override
    public final void appendList(List<T> data)
    {
        int positionStart = list.size();
        list.addAll(data);
        int itemCount = list.size() - positionStart;

        if (positionStart == 0)
        {
            notifyDataSetChanged();
        }
        else
        {
            notifyItemRangeInserted(positionStart + 1, itemCount);
        }
    }


    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case TYPE_BOTTOM:
                if (loadMore != null)
                {
                    RecyclerView.ViewHolder holder = onBottomViewHolderCreate(loadMore);
                    if (holder == null)
                    {
                        throw new RuntimeException("You must impl onBottomViewHolderCreate() and return your own holder ");
                    }
                    return holder;
                }
                else
                {
                    return new BottomViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_footer_new, parent, false), refreshLoadMoreListener);
                }
            default:
                return onViewHolderCreate(parent, viewType);
        }
    }

    @Override
    public RecyclerView.ViewHolder onBottomViewHolderCreate(View loadMore)
    {
        return new BottomViewHolder(loadMore, refreshLoadMoreListener);
    }

    @Override
    public void onBottomViewHolderBind(RecyclerView.ViewHolder holder, int loadState)
    {

    }

    @Override
    public void onRefresh()
    {

    }

    @Override
    public abstract RecyclerView.ViewHolder onViewHolderCreate(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position)
    {
        if (getItemViewType(position) == TYPE_BOTTOM)
        {
            loadState = loadState == STATE_ERROR ? STATE_ERROR : isHasMore() ? STATE_LOADING : STATE_LASTED;
            if (loadMore != null)
            {
                try
                {
                    onBottomViewHolderBind(holder, loadState);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                try
                {
                    ((BottomViewHolder) holder).bindDateView(loadState);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (listener != null)
                        listener.onItemClick(v, holder.getAdapterPosition());
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    if (longListener != null)
                        longListener.onItemLongClick(v, holder.getAdapterPosition());
                    return true;
                }
            });
            onViewHolderBind(holder, position);
        }
    }


    @Override
    public abstract void onViewHolderBind(RecyclerView.ViewHolder holder, int position);

    @Override
    public final void isLoadingMore()
    {
        if (loadState == STATE_LOADING)
        {
            Log.e("TAG", "isLoadingMore: 正在加载中 !!");
            return;
        }
        loadState = STATE_LOADING;
        notifyItemRangeChanged(getItemRealCount(), 1);
    }

    @Override
    public final int getItemCount()
    {
        return list.size() == 0 ? 0 : list.size() + 1;
    }

    @Override
    public int getItemRealCount()
    {
        return list.size();
    }

    @Override
    public final void setLoadMoreView(@NonNull View view)
    {
        loadMore = view;
    }

    @Override
    public final int getItemViewType(int position)
    {
        if (list.size() > 0 && position < list.size())
        {
            return getItemViewTypes(position);
        }
        else
        {
            return TYPE_BOTTOM;
        }
    }

    @Override
    public long getItemId(int position)
    {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewTypes(int position)
    {
        return 0;
    }


    @Override
    public boolean isHasMore()
    {
        return isHasMore;
    }

    public void setHasMore(boolean more)
    {
        isHasMore=more;
    }

    public final void loadMoreError()
    {
        loadState = STATE_ERROR;
        notifyItemRangeChanged(getItemRealCount(), 1);
    }


    @Nullable
    private OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.listener = listener;
    }

    @Nullable
    private OnItemLongClickListener longListener;
    public void setOnItemLongClickListener(OnItemLongClickListener listener)
    {
        this.longListener = listener;
    }


    private OnRefreshLoadMoreListener refreshLoadMoreListener;
    public void setOnRefreshLoadMoreListener(OnRefreshLoadMoreListener listener)
    {
        this.refreshLoadMoreListener = listener;
    }
}

package com.ss007.swiprecycleview;

import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class DefaultAnimator extends DefaultItemAnimator
{
    public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull List<Object> payloads)
    {
        return true;
    }
}

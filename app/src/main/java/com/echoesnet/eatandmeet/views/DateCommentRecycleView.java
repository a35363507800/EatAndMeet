package com.echoesnet.eatandmeet.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.echoesnet.eatandmeet.models.bean.DateCommentBean;
import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;
import com.echoesnet.eatandmeet.views.adapters.DateInfoRecycleViewAdapter;
import com.echoesnet.eatandmeet.views.adapters.TrendsRecycleViewAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2017/8/3
 * @description
 */
public class DateCommentRecycleView extends RecyclerView
{
    private Context mAct;
    private View emptyView;
    private DateInfoRecycleViewAdapter testReAdapter;
    private List<DateCommentBean> mList = new ArrayList<>();
    private List<String> stringList = new ArrayList<>();
    private static final String TAG = DateCommentRecycleView.class.getSimpleName();
    public DateCommentRecycleView(Context context)
    {
        this(context,null);
    }

    public DateCommentRecycleView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        initView(context, attrs);
    }


    private void initView(Context context, AttributeSet attrs)
    {
        this.mAct = context;
    }

    public void init(DateInfoRecycleViewAdapter testReAdapter,View emptyView)
    {
        this.testReAdapter = testReAdapter;
        LinearLayoutManager cManager = new LinearLayoutManager(mAct, LinearLayout.VERTICAL, false);
        setAdapter(testReAdapter);
        setLayoutManager(cManager);
        setEmptyView(emptyView);
    }

    public void refreshUI()
    {
        testReAdapter.notifyDataSetChanged();
    }




    private void checkIfEmpty()
    {
        if (emptyView != null && getAdapter() != null)
        {
            final boolean emptyViewVisible =
                    getAdapter().getItemCount() == 2;
            emptyView.setVisibility(emptyViewVisible ? GONE : GONE);

        }
    }

    @Override
    public void setAdapter(Adapter adapter)
    {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null)
        {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null)
        {
            adapter.registerAdapterDataObserver(observer);
        }
        checkIfEmpty();
    }

    public void setEmptyView(View emptyView)
    {
        this.emptyView = emptyView;
        checkIfEmpty();
    }

    private final AdapterDataObserver observer = new AdapterDataObserver()
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

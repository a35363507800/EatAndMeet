package com.echoesnet.eatandmeet.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;

import com.linearlistview.LinearListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangben on 2016/6/3.
 */
public abstract class ExpandAutoListviewAdapter <E> extends BaseAdapter
{
    //显示的条数
    private int showCount=2;
    protected Context mContext;
    protected LinearListView mListView;
    protected LayoutInflater inflater;
    //点击此view可以展开和折叠listview
    protected View headView;
    //需要显示的item
    protected List<E> mShowObjects = new ArrayList<E>();
    //全部item
    protected List<E> mAllObjects = null;
    protected boolean shrink = true;

    @SuppressLint("InflateParams")
    public ExpandAutoListviewAdapter(Context mContext, LinearListView mListView, View headView)
    {
        this.mContext = mContext;
        this.mListView = mListView;
        inflater = LayoutInflater.from(mContext);
        this.headView = headView;
        this.headView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                changeShow();
            }
        });
    }

    public void setAdapterData( List<E> mAllObjects )
    {
        this.mAllObjects = mAllObjects;
        mShowObjects.clear();
        if( mAllObjects != null )
        {
            if( mAllObjects.size() <= showCount )
            {
                headView.setVisibility(View.GONE);
                mShowObjects.addAll(mAllObjects);
            } else
            {
                headView.setVisibility(View.VISIBLE);
                for (int i = 0; i < showCount; i++)
                {
                    mShowObjects.add(mAllObjects.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setShowCount(int showCount)
    {
        this.showCount=showCount;
    }
    @Override
    public int getCount()
    {
        int showCount = 0;
        if( mShowObjects != null ) {
            showCount = mShowObjects.size();
        }
        return showCount;
    }

    @Override
    public E getItem(int position)
    {
        E object = null;
        if( mShowObjects != null )
        {
            object = mShowObjects.get(position);
        }
        return object;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //展开折叠按钮
    private void changeShow()
    {
        if( headView.getVisibility() == View.GONE )
        {
            headView.setVisibility(View.VISIBLE);
        }
        mShowObjects.clear();
        if(shrink)
        {
            shrink = false;
            mShowObjects.addAll(mAllObjects);
            //headView.setText("收起");
        }
        else
        {
            shrink = true;
            for (int i = 0; i < showCount; i++)
            {
                mShowObjects.add(mAllObjects.get(i));
            }
            //headView.setText("展开");
        }
        notifyDataSetChanged();
    }
}

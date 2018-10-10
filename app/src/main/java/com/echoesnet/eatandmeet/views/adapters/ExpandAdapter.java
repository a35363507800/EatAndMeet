package com.echoesnet.eatandmeet.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zhy.autolayout.AutoRelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/18.
 */
public abstract class ExpandAdapter<E> extends BaseAdapter
{
    //显示的条数
    private int showCount=2;
    protected Context mContext;
    protected ListView mListView;
    protected LayoutInflater inflater;
    //点击此view可以展开和折叠listview
    protected TextView headView;
    //需要显示的item
    protected List<E> mShowObjects = new ArrayList<E>();
    //全部item
    protected List<E> mAllObjects = null;

    public boolean isShrink()
    {
        return shrink;
    }

    protected boolean shrink = true;

    private AutoRelativeLayout downLayout;


    @SuppressLint("InflateParams")
    public ExpandAdapter(Context mContext, ListView mListView, TextView headView, AutoRelativeLayout downLayout)
    {
        this.mContext = mContext;
        this.mListView = mListView;
        inflater = LayoutInflater.from(mContext);
        this.headView = headView;
        this.downLayout  = downLayout;
        this.downLayout.setOnClickListener(new View.OnClickListener()
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
        setListViewHeightBasedOnChildren(mListView);
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
            headView.setText("收起");
        }
        else
        {
            shrink = true;
            for (int i = 0; i < showCount; i++)
            {
                mShowObjects.add(mAllObjects.get(i));
            }
            headView.setText("展开全部");
        }
        notifyDataSetChanged();
        setListViewHeightBasedOnChildren(mListView);
    }

    /**
     * 当ListView外层有ScrollView时，需要动态设置ListView高度
     * @param listView
     */
    protected void setListViewHeightBasedOnChildren(ListView listView)
    {
        if(listView == null)
            return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
        {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}

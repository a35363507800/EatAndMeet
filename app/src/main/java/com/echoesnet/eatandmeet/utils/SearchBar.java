package com.echoesnet.eatandmeet.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.echoesnet.eatandmeet.R;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @createDate 2017/7/21 13:59
 * @description
 */

public class SearchBar extends LinearLayout
{
    private static final String TAG=SearchBar.class.getSimpleName();
    private EditText etKeyword;
    private IconTextView searchClear;
    private Context mContext;
    private String searchHint = "输入ID搜索";
    private ISearchTriggerListener mListener;
    private ISearchCancelListener mCancelListener;

    public SearchBar(Context context)
    {
        this(context, null);
    }

    public SearchBar(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs)
    {
        this.mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SearchBar, 0, 0);
        searchHint = ta.getString(R.styleable.SearchBar_searchHint);
        ta.recycle();
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        View view = LayoutInflater.from(mContext).inflate(R.layout.ease_search_bar, this);
        etKeyword = (EditText) view.findViewById(R.id.query);
        searchClear = (IconTextView) view.findViewById(R.id.search_clear);
        etKeyword.setHint(searchHint);
        searchClear.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                etKeyword.setText("");
                if (mCancelListener!=null)
                    mCancelListener.cancel();
            }
        });
        RxTextView.textChanges(etKeyword)
                .debounce(500, TimeUnit.MILLISECONDS,AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<CharSequence>()
                {
                    @Override
                    public boolean test(CharSequence filter) throws Exception
                    {
                        final String key = filter.toString().trim();
                        if (!TextUtils.isEmpty(key))
                            searchClear.setVisibility(VISIBLE);
                        else
                            searchClear.setVisibility(GONE);
                        return true;
                    }
                })
                .switchMap(new Function<CharSequence, ObservableSource<CharSequence>>()
                {
                    @Override
                    public ObservableSource<CharSequence> apply(CharSequence s) throws Exception
                    {
                        return Observable.just(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CharSequence>()
                {
                    @Override
                    public void accept(CharSequence s) throws Exception
                    {
                        Logger.t(TAG).d("search keyword is "+ s);
                        if (mListener!=null)
                            mListener.searching(s.toString());
                    }
                });
    }

    public String getSearchKeyword()
    {
        return etKeyword.getText().toString().trim();
    }

    public void setHint(String hint)
    {
        etKeyword.setHint(hint);
    }

    public void setSearchTriggerListener(ISearchTriggerListener listener)
    {
        this.mListener = listener;
    }

    public interface ISearchTriggerListener
    {
        void searching(String keyword);
    }


    public interface ISearchCancelListener
    {
        void cancel();
    }
    public void setSearchCancelListener(ISearchCancelListener listener)
    {
        this.mCancelListener = listener;
    }
}

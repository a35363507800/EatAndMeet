package com.echoesnet.eatandmeet.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.echoesnet.eatandmeet.presenters.BasePresenter;
import com.orhanobut.logger.Logger;

/**
 * Created by ben on 2017/3/1.
 */

public abstract class MVPBaseFragment<V,T extends BasePresenter<V>> extends BaseFragment
{
    private final static  String TAG = MVPBaseFragment.class.getSimpleName();
    protected T mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mPresenter=createPresenter();
        mPresenter.attachView((V) this);
        Logger.t(TAG).d("MVPBaseFragment 的oncreat 方法执行");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mPresenter.detachView();
    }

    protected abstract T createPresenter();
}

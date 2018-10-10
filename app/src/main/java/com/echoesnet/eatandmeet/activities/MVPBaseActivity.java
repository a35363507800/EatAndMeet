package com.echoesnet.eatandmeet.activities;

import android.os.Bundle;

import com.echoesnet.eatandmeet.presenters.BasePresenter;

/**
 * Created by ben on 2017/2/22.
 *
 * V:view 接口类型
 * T: Presenter 的具体类型
 */

public abstract class MVPBaseActivity<V,T extends BasePresenter<V>> extends BaseActivity
{
    protected T mPresenter;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        mPresenter=createPresenter();
        mPresenter.attachView((V) this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mPresenter.detachView();
    }

    protected abstract T createPresenter();
}

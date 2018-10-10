package com.echoesnet.eatandmeet.presenters;

import com.echoesnet.eatandmeet.utils.Log.EamLogger;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by ben on 2017/2/22.
 * presenter 的基类
 * T:view 的引用
 */

public abstract class BasePresenter<T>
{
    private Reference<T> mViewRef;

    public void attachView(T view)
    {
        mViewRef = new WeakReference<T>(view);
    }

    public T getView()
    {
        if (mViewRef == null)
        {
            EamLogger.t("BasePresenter").writeToDefaultFile("BasePresenter的mViewRef对象空了" + mViewRef);
            return null;
        }
        return mViewRef.get();
    }

    public boolean isViewAttached()
    {
        return mViewRef != null && mViewRef.get() != null;
    }

    public void detachView()
    {
        if (mViewRef != null)
        {
            mViewRef.clear();
            mViewRef = null;
        }
    }
}

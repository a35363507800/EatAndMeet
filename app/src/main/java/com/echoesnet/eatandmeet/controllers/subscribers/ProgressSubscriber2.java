package com.echoesnet.eatandmeet.controllers.subscribers;

import android.content.Context;

import com.echoesnet.eatandmeet.http4retrofit2.progress.ProgressCancelListener;
import com.echoesnet.eatandmeet.http4retrofit2.progress.ProgressDialogHandler;

import io.reactivex.disposables.Disposable;

/**
 * Created by ben on 2017/3/30.
 */

public abstract class ProgressSubscriber2<T> extends SilenceSubscriber2<T> implements ProgressCancelListener
{
    public final static String TAG = ProgressSubscriber2.class.getSimpleName();

    private ProgressDialogHandler mProgressDialogHandler;
    private Disposable disposable;

    public ProgressSubscriber2(Context context, boolean cancel, String desc)
    {
        mProgressDialogHandler = new ProgressDialogHandler(context, this, cancel);
        mProgressDialogHandler.setDesc(desc);
    }

    private void showProgressDialog()
    {
        if (mProgressDialogHandler != null)
        {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    private void dismissProgressDialog()
    {
        if (mProgressDialogHandler != null)
        {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
        }
    }

    @Override
    public void onNext(T o)
    {
        super.onNext(o);
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onComplete()
    {
        dismissProgressDialog();
        super.onComplete();
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     *
     * @param e
     */
    @Override
    public void onError(Throwable e)
    {
        dismissProgressDialog();
        super.onError(e);
    }

    @Override
    public void onSubscribe(Disposable d)
    {
        super.onSubscribe(d);
        this.disposable=d;
        showProgressDialog();
    }

    /**
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    @Override
    public void onCancelProgress()
    {
        if (disposable!=null)
            disposable.dispose();
    }

/*    public void onStart()
    {
        showProgressDialog();
    }*/
}

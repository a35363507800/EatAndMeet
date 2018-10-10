package com.echoesnet.eatandmeet.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.baidu.mobstat.StatService;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/2/18.
 * Fragment 注意使用support库中的--wb
 */

public abstract class BaseFragment extends RxFragment
{
    private static final String TAG = BaseFragment.class.getSimpleName();

    protected InputMethodManager inputMethodManager;
    //对于聊天页面里面的聊天Tab不准确（第一个，其onResume与onPause会让其他的操作触发，而setUserVisibleHint 只要初始化出来就会true），会增加它的无效统计--wb
    private List<String> notStatPages =
                        Arrays.asList(new String[]{"ConversationListFragment"});
    private volatile int hasStat=0;//当前是否存在一个统计状态
    private volatile boolean isPageVisible=false;

    @Override
    public void onResume()
    {
        super.onResume();
        Logger.t(TAG).d("onResume>"+getPageName());
        if (notStatPages.contains(getPageName()))
            return;
        if (hasStat==0&&isPageVisible)
        {
            Logger.t(TAG).d("onResume>onPageStart>"+getPageName());
            StatService.onPageStart(getActivity(), getPageName());
            hasStat++;
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Logger.t(TAG).d("onPause>"+getPageName());
        if (notStatPages.contains(getPageName()))
            return;
        if (hasStat>=1&&isPageVisible)
        {
            Logger.t(TAG).d("onPause>onPageEnd>"+getPageName());
            StatService.onPageEnd(getActivity(), getPageName());
            hasStat--;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    protected void hideSoftKeyboard()
    {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        //此重写不对此fragment做保存，防止恢复时候出现的崩溃--wb
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)//不能确定它的执行顺序--wb
    {
        super.setUserVisibleHint(isVisibleToUser);
        isPageVisible=isVisibleToUser;
        Logger.t(TAG).d("设置可见性》"+isPageVisible +" hasStat> "+hasStat);
        if (isVisibleToUser)
        {
            if (hasStat==0)
            {
                Logger.t(TAG).d("setUserVisibleHint>onPageStart>"+getPageName());
                StatService.onPageStart(getActivity(), getPageName());
                hasStat++;
            }
        }
        else
        {
            if (hasStat>=1)
            {
                Logger.t(TAG).d("setUserVisibleHint>onPageEnd>"+getPageName());
                StatService.onPageEnd(getActivity(), getPageName());
                hasStat--;
            }
        }
    }

    protected abstract String getPageName();
}

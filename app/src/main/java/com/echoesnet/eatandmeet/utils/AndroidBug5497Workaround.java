package com.echoesnet.eatandmeet.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.orhanobut.logger.Logger;

/**
 * Created by ben on 2017/2/26.
 * 此类是为了解决在全屏模式，带有webview 或者沉浸式状态栏时候解决键盘遮住输入框的
 */

public class AndroidBug5497Workaround
{
    private static final String TAG = AndroidBug5497Workaround.class.getSimpleName();

    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
// To use this class, simply invoke assistActivity() on an Activity that already has its content view set.
    public static void assistActivity(Activity activity)
    {
        new AndroidBug5497Workaround(activity);
    }

    private Activity activity;
    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    private AndroidBug5497Workaround(Activity activity)
    {
        this.activity = activity;
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            public void onGlobalLayout()
            {
                possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent()
    {
        int usableHeightNow = computeUsableHeight();
        int navBarHeight = 0;
        if (usableHeightNow != usableHeightPrevious)
        {
            if (CommonUtils.checkDeviceHasNavigationBar(activity))
            {
                navBarHeight = CommonUtils.getBottomStatusHeight(activity);
            }
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            Logger.t(TAG).d("键盘 navBarHeight：" + navBarHeight + " | heightDifference：" + heightDifference + " | usableHeightSansKeyboard：" + usableHeightSansKeyboard + " | usableHeightSansKeyboard/8：" + usableHeightSansKeyboard / 8);
            if (heightDifference > (usableHeightSansKeyboard / 8))
            {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                Logger.t(TAG).d("键盘弹出》" + frameLayoutParams.height);
            }
            else
            {
                // keyboard probably just became hidden
                frameLayoutParams.height = usableHeightSansKeyboard - navBarHeight;
                Logger.t(TAG).d("键盘收起》" + frameLayoutParams.height);
            }
            if (heightDifference == 0)//虚拟键隐藏
            {
                frameLayoutParams.height = usableHeightSansKeyboard;
                Logger.t(TAG).d("键盘 虚拟键隐藏");
            }
            else if (heightDifference == CommonUtils.getBottomStatusHeight(activity))
            {
                //虚拟键 出现
                frameLayoutParams.height = usableHeightSansKeyboard - navBarHeight;
                Logger.t(TAG).d("键盘 虚拟键出现");
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight()
    {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);

        // Logger.t(TAG).d("rec bottom>"+r.bottom+" rec top>"+ r.top);
        return (r.bottom);
        //return (r.bottom - r.top);// 全屏模式下： return r.bottom
    }

}

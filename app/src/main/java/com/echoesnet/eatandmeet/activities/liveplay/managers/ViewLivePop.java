package com.echoesnet.eatandmeet.activities.liveplay.managers;

import android.app.Activity;
import android.view.View;

import com.echoesnet.eatandmeet.activities.liveplay.Presenter.LiveBasePresenter;
import com.echoesnet.eatandmeet.models.bean.LookAnchorBean;
import com.echoesnet.eatandmeet.views.widgets.LiveHostInfoPop;

/**
 * Created by lc on 2017/7/14 12.
 */

public class ViewLivePop
{
    private static final String TAG = ViewLivePop.class.getSimpleName();
    private LiveHostInfoPop mLiveHostInfoPop;
    private Activity mActivity;
    private LookAnchorBean bean;
    private LiveBasePresenter livePresenter;
    private View slBody;

    public ViewLivePop(Activity mActivity, LookAnchorBean bean, LiveBasePresenter livePresenter, View slBody)
    {
        this.mActivity = mActivity;
        this.bean = bean;
        this.livePresenter = livePresenter;
        this.slBody =slBody;
    }

//    public void showHostInfoWin()
//    {
//        mLiveHostInfoPop = new LiveHostInfoPop(mActivity,bean,livePresenter);
//        mLiveHostInfoPop.showPopupWindow(slBody);
//    }

    /**
     * 分享窗口消失
     */
    public boolean dismissShareWin()
    {
        boolean isShowing = true;
        if (mLiveHostInfoPop != null && mLiveHostInfoPop.isShowing()) {
            mLiveHostInfoPop.dismiss();
            isShowing = false;
        }
        return isShowing;
    }

    /**
     * 分享窗口销毁，避免窗口泄露
     */
    public void killShareWin()
    {
        if (mLiveHostInfoPop != null) {
            mLiveHostInfoPop.dismiss();
            mLiveHostInfoPop = null;
        }
    }
}

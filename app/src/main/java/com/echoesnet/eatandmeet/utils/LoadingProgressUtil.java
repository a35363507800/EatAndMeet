package com.echoesnet.eatandmeet.utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

/**
 * Created by ben on 2017/2/16.
 * 加载过程中显示的视图，将来里面的进度匡可能会替换为gif动态图
 */

public class LoadingProgressUtil
{
    private static void showAndHindLoadingView(View loadingView, boolean toShow)
    {
        final CircleProgressView mCircleView = (CircleProgressView) loadingView.findViewById(R.id.pb_circle);
        mCircleView.setShowTextWhileSpinning(true); // Show/hide text in spinning mode
        mCircleView.setText("加载中...");
        mCircleView.setTextMode(TextMode.TEXT); // show text while spinning
        mCircleView.setUnitVisible(false);
        if (toShow)
        {
            if (loadingView.getVisibility() != View.VISIBLE)
            {
                loadingView.setVisibility(View.VISIBLE);
                mCircleView.spin();
            }
        }
        else
        {
            mCircleView.stopSpinning();
            loadingView.setVisibility(View.GONE);
        }
    }

    /**
     * @param loadingView
     * @param toShow
     * @param state
     * @param errorListener 网络错误时 传入  state == 1 时需传入listener  其他 可传入空
     */
    public static void showWithErrorLoadingView(View loadingView, boolean toShow, int state, View.OnClickListener errorListener)
    {
        if (loadingView == null)
            return;
        final CircleProgressView mCircleView = (CircleProgressView) loadingView.findViewById(R.id.pb_circle);
        TextView tvLoadDes = (TextView) loadingView.findViewById(R.id.tv_load_des);
        ImageView defineError = (ImageView) loadingView.findViewById(R.id.define_error);
        View vBg = (View) loadingView.findViewById(R.id.v_bg);
        loadingView.setOnClickListener(errorListener);
        switch (state)
        {
            case 0://正常加载
                mCircleView.setShowTextWhileSpinning(true); // Show/hide text in spinning mode
                mCircleView.setText("加载中...");
                mCircleView.setTextMode(TextMode.TEXT); // show text while spinning
                mCircleView.setUnitVisible(false);
                break;
            case 1://接口调用出现错误
                tvLoadDes.setVisibility(View.VISIBLE);
                defineError.setVisibility(View.VISIBLE);
                vBg.setVisibility(View.VISIBLE);
                tvLoadDes.setText("点击页面重新加载");
                mCircleView.stopSpinning();
                mCircleView.setVisibility(View.GONE);
                break;
        }
        if (toShow)
        {
            if (loadingView.getVisibility() != View.VISIBLE)
            {
                loadingView.setVisibility(View.VISIBLE);
                mCircleView.spin();
            }
        }
        else
        {
            mCircleView.stopSpinning();
            loadingView.setVisibility(View.GONE);
        }
    }

}

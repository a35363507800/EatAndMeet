package com.echoesnet.eatandmeet.views.widgets.trendsCameraView.lisenter;

import android.graphics.Bitmap;


public interface CameraLisenter
{

    void captureSuccess(String showType,Bitmap bitmap,String url);

    void recordSuccess(String showType,String url, Bitmap firstFrame,long duration);

    void quit();

}

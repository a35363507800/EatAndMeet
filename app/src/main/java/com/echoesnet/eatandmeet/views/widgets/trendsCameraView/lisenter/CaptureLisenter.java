package com.echoesnet.eatandmeet.views.widgets.trendsCameraView.lisenter;



public interface CaptureLisenter {
    void takePictures();

    void recordShort(long time);

    void recordStart();

    void recordEnd(long time);

    void recordZoom(float zoom);

    void recordError();
}

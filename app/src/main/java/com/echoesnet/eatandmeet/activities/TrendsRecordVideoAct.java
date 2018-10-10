package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.views.widgets.trendsCameraView.CameraView;
import com.echoesnet.eatandmeet.views.widgets.trendsCameraView.lisenter.CameraLisenter;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/19 0019
 * @description
 */
public class TrendsRecordVideoAct extends BaseActivity
{
    private final String TAG = TrendsRecordVideoAct.class.getSimpleName();

    @BindView(R.id.camera_view_trends)
    CameraView cameraView;

    private Activity mAct;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_trends_record_video);
        ButterKnife.bind(this);
        mAct = this;
        cameraView.setSaveVideoPath(NetHelper.getRootDirPath(this) + "camera");
        cameraView.setCameraLisenter(new CameraLisenter()
        {
            @Override
            public void captureSuccess(String showType,Bitmap bitmap, String url)
            {
                Logger.t(TAG).d(" bitmap.getWidth >>>" + bitmap.getWidth() + "|" + url);
                Intent intent = new Intent();
                intent.putExtra("picUrl", url);
                intent.putExtra("type", "pic");
                intent.putExtra("showType", showType);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void recordSuccess(String showType,String url, Bitmap firstFrame, long videoTime)
            {
                String fileKeyName = "a_" + SharePreUtils.getUserMobile(mAct) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
                String outPutImagePath = NetHelper.getRootDirPath(mAct) + "camera" + File.separator + CommonUtils.toMD5(fileKeyName);
                try
                {
                    final File outFile = new File(outPutImagePath);
                    ImageUtils.compressBitmap(firstFrame, outFile.getPath(), 130, 0);
                    Logger.t(TAG).d("压缩成功" + outFile.length() / 1024);
                } catch (Exception e)
                {
                    Logger.t(TAG).d("压缩失败" + e.getMessage());
                    outPutImagePath = "";
                }
                Logger.t(TAG).d("success ->" + url);
                Intent intent = new Intent();
                intent.putExtra("videoUrl", url);
                intent.putExtra("thumbnail", outPutImagePath); //第一帧缩略图
                intent.putExtra("type", "video");
                intent.putExtra("videoTime",videoTime);
                intent.putExtra("showType",showType);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void quit()
            {
                finish();
            }
        });
    }
}

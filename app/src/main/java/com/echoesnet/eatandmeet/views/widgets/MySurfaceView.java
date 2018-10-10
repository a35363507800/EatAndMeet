package com.echoesnet.eatandmeet.views.widgets;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.echoesnet.eatandmeet.utils.CommonUtils;

import java.io.IOException;

/**
 * Created by Administrator on 2016/10/24.
 */

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
    private static final String TAG = "Kintai";

    private SurfaceHolder holder;
    private Camera mCamera;
    private Context context;

    public MySurfaceView(Context context)
    {
        super(context);
        this.context = context;
        holder = getHolder();//后面会用到！
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0)
    {
        try
        {
            if (mCamera == null)
            {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);//开启相机，可以放参数 0 或 1，分别代表后置、前置摄像头，默认为 0

                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size preSize = CommonUtils.getCloselyPreSize(CommonUtils.getScreenSize((Activity) context).width,
                        CommonUtils.getScreenSize((Activity) context).height, parameters.getSupportedPreviewSizes());
                parameters.setPreviewSize(preSize.width, preSize.height);
                mCamera.setParameters(parameters);

                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewDisplay(holder);//整个程序的核心，相机预览的内容放在 holder
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
    {
        try
        {
            mCamera.startPreview();//该方法只有相机开启后才能调用
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0)
    {
        if (mCamera != null)
        {
            mCamera.release();//释放相机资源
            mCamera = null;
        }
    }
}

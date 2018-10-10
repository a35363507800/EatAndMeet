package com.echoesnet.eatandmeet.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

import static com.echoesnet.eatandmeet.views.widgets.DialogUtil.getScreenWidth;

/**
 * Created by Administrator on 2016/12/13.
 */

public class MyProgressDialog
{
    private  Dialog dialog;
    private  TextView tvDescribe;
    private boolean isOutTime = true;

    public MyProgressDialog buildDialog(Activity context)
    {
        dialog = new Dialog(context, R.style.Dialog02);
        dialog.setContentView(R.layout.firset_dialog_view);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();

        int screenW = getScreenWidth(context);
        lp.width = (int) (0.8 * screenW);
        lp.height = CommonUtils.dp2px(context,80);
        tvDescribe = (TextView) dialog.findViewById(R.id.tvLoad);

        final ProcessOutTime processOutTime = new ProcessOutTime(context,dialog);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (processOutTime!=null && isOutTime)
                    processOutTime.stop();
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (processOutTime!=null && isOutTime)
                    processOutTime.start();
            }
        });

        return this;
    }
    public MyProgressDialog setDescription(String description)
    {
        tvDescribe.setText(description);
        return this;
    }
    public void show()
    {
        if(dialog != null)
        dialog.show();
    }
    public void dismiss()
    {
        if (dialog != null)
            dialog.dismiss();
    }

    public boolean isShowing()
    {
        return dialog.isShowing();
    }
    public void setCancelable(boolean cancelable)
    {
        dialog.setCancelable(cancelable);
    }

    public MyProgressDialog setOutTime(boolean outTime)
    {
        isOutTime = outTime;
        return this;
    }

    /*超时线程*/
    private static class ProcessOutTime implements Runnable
    {
        private boolean running = false;
        private long startTime = 0L;
        private Thread thread = null;
        private final WeakReference<Activity> mActRef;
        private final WeakReference<Dialog> pDialogRef;

        public ProcessOutTime(Activity mAct,Dialog dialog)
        {
            mActRef = new WeakReference<Activity>(mAct);
            pDialogRef = new WeakReference<Dialog>(dialog);
        }

        public void run()
        {
            while (true)
            {
                if (!this.running)
                    return;
                if (System.currentTimeMillis() - this.startTime > 15 * 1000L)
                {
                    final Activity cAct = mActRef.get();
                    final Dialog pDialog = pDialogRef.get();
                    if (cAct != null)
                    {
                        cAct.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (pDialog != null && pDialog.isShowing())
                                {
                                    Logger.t("dialog>").d("dialog.dis");
                                    pDialog.dismiss();
//									ToastUtils.showShort(cAct.mAct, "程序出现了不可抗拒的错误，关闭页面重试下...");
                                }
                            }
                        });
                    }
                    this.running = false;
                    this.thread = null;
                    this.startTime = 0L;
                }
                try
                {
                    Thread.sleep(200L);
                } catch (Exception localException)
                {
                    Logger.t("thread_sleep").d(localException.getMessage());
                }
            }
        }

        public void start()
        {
            try
            {
                this.thread = new Thread(this);
                this.running = true;
                this.startTime = System.currentTimeMillis();
                this.thread.start();
            } catch (Exception e)
            {
                Logger.t("Dialog").d(e.getMessage());
            } finally
            {
            }
        }

        public void stop()
        {
            this.running = false;
            this.thread = null;
            this.startTime = 0L;
        }
    }

}

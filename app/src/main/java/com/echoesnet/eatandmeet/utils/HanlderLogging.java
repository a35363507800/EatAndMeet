
package com.echoesnet.eatandmeet.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.util.Printer;

/**
 * qiguolong
 * 主线程跟踪 监听卡顿阻塞
 */
public class HanlderLogging {
    private static final String dispatch = ">>>>> Dispatching";
    private static final String finished = "<<<<< Finished";
    public static final long lagDuration = 150L;
    public static long startTime;
    public static String mes = "";
    private static LogStraceInfo sLogStraceInfo;

    public HanlderLogging() {
    }

    public static void startTraceHandler() {
        sLogStraceInfo = new LogStraceInfo("logtrace");
        Looper.getMainLooper().setMessageLogging(new Printer() {
            public void println(String x) {
                if (HanlderLogging.sLogStraceInfo!=null) {
                    if (x.startsWith(">>>>> Dispatching")) {
                        HanlderLogging.sLogStraceInfo.dispatch();
                    } else if (x.startsWith("<<<<< Finished")) {
                        HanlderLogging.sLogStraceInfo.finish();
                    }
                }
            }
        });
    }
    public static void stopTrace() {
        Looper.getMainLooper().setMessageLogging(null);
        if (sLogStraceInfo!=null){
            sLogStraceInfo.quit();
            sLogStraceInfo=null;
        }
    }
    private static String getStackInfo() {
        RuntimeException re = new RuntimeException();
        return Log.getStackTraceString(re.fillInStackTrace());
    }

    public static class LogStraceInfo extends HandlerThread {
        private Handler mHandler;
        private Runnable mRunnable = new Runnable() {
            public void run() {
                StringBuilder sb = new StringBuilder();
                StackTraceElement[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();
                StackTraceElement[] var3 = stackTrace;
                int var4 = stackTrace.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    StackTraceElement s = var3[var5];
                    sb.append(s.toString() + "\n");
                }

                Log.w("lagger", sb.toString());
            }
        };

        public LogStraceInfo(String name) {
            super(name);
            this.start();
            this.mHandler = new Handler(this.getLooper());
        }

        public Handler getHandler() {
            return this.mHandler;
        }

        public void dispatch() {
            HanlderLogging.sLogStraceInfo.getHandler().postDelayed(this.mRunnable, HanlderLogging.lagDuration);
        }

        public void finish() {
            HanlderLogging.sLogStraceInfo.getHandler().removeCallbacks(this.mRunnable);
        }
    }
}

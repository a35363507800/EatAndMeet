package com.echoesnet.eatandmeet.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.view.WindowManager;

import com.echoesnet.eatandmeet.activities.HomeAct;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 *
 * @author wangben
 */
public class CrashHandler implements UncaughtExceptionHandler
{
    // 系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;
    private static CrashHandler crashInstance;
    private Context mContext;// 程序的Context对象

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler()
    {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance()
    {
        if (crashInstance == null)
            crashInstance = new CrashHandler();
        return crashInstance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context)
    {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取系统默认的UncaughtException处理器
        Thread.setDefaultUncaughtExceptionHandler(this);// 设置该CrashHandler为程序的默认处理器
        mContext = context.getApplicationContext();
    }

    /**
     * 当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
     * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        Logger.t("异常捕捉1》").d(thread.getId() + "   " + ex.getMessage());
        handleException(ex);

        //如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己
        if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex 异常信息
     * @return true 如果处理了该异常信息;否则返回false.
     */
    public boolean handleException(Throwable ex)
    {
        if (ex == null)
        {
            return false;
        }
        final String crashReport = getCrashReport(mContext, ex);
        new Thread()
        {
            public void run()
            {
                Looper.prepare();
                File file = save2File(crashReport);
                //showAppCrashReport(mContext);
                //sendAppCrashReport(mContext, crashReport, file);
                Looper.loop();
            }
        }.start();

        return true;
    }
    private void reStartApp()
    {
        Intent intent=new Intent(mContext, HomeAct.class);
        mContext.startActivity(intent);
        //for restarting the Activity
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
    private File save2File(String crashReport)
    {
        String fileName = "crash-" + System.currentTimeMillis() + ".txt";
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
        {
            try
            {
                File dir = new File(NetHelper.getRootDirPath(mContext) + NetHelper.CRASH_FOLDER);
                if (!dir.exists())
                    dir.mkdir();
                File file = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(crashReport.toString().getBytes());
                fos.close();
                return file;
            } catch (Exception e)
            {
                Logger.t("CrashHandle").i("save2File error:" + e.getMessage());
            }
        }
        // 退出
/*        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);*/
        return null;
    }
    /**
     * 退出应用程序
     */
/*    public void exitApp(Context context) {
        try {
            ActivityManager activityMgr = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {

        }
    }*/
/*    private void showAppCrashReport(final Context context)
    {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setCancelable(false)
                .setMessage("貌似发生了一点点小意外,即将退出...")
                .setNeutralButton("再忍你你一次", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // 退出
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                })
                .create()
                .show();
    }*/

    private void sendAppCrashReport(final Context context, final String crashReport, final File file)
    {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("看脸吃饭错误报告");
        builder.setMessage("貌似发生了一点点小意外，您可以通知我们");
        builder.setPositiveButton("提交报告",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try
                        {

                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            String[] tos = {"568894336@qq.com"};
                            intent.putExtra(Intent.EXTRA_EMAIL, tos);

                            intent.putExtra(Intent.EXTRA_SUBJECT,
                                    "看脸吃饭Android客户端 - 错误报告");
                            if (file != null)
                            {
                                intent.putExtra(Intent.EXTRA_STREAM,
                                        Uri.fromFile(file));
                                intent.putExtra(Intent.EXTRA_TEXT,
                                        "请将此错误报告发送给王犇，以便我尽快修复此问题，谢谢合作！\n");
                            }
                            else
                            {
                                intent.putExtra(Intent.EXTRA_TEXT,
                                        "请将此错误报告发送给我，以便我尽快修复此问题，谢谢合作！\n"
                                                + crashReport);
                            }
                            intent.setType("text/plain");
                            intent.setType("message/rfc882");
                            Intent.createChooser(intent, "Choose Email Client");
                            context.startActivity(intent);
                        } catch (Exception e)
                        {
                            ToastUtils.showLong("There are no email clients installed.");
                        } finally
                        {
                            dialog.dismiss();
                            // 退出
                            android.os.Process.killProcess(android.os.Process
                                    .myPid());
                            System.exit(1);
                        }
                    }
                });
        builder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        // 退出
                        android.os.Process.killProcess(android.os.Process
                                .myPid());
                        System.exit(1);
                    }
                });
        dialog = builder.create();
        dialog.getWindow()
                .setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    /**
     * 获取APP崩溃异常报告
     *
     * @param ex
     * @return
     */
    private String getCrashReport(Context context, Throwable ex)
    {
        PackageInfo pinfo = getPackageInfo(context);
        StringBuffer exceptionStr = new StringBuffer();
        exceptionStr.append("Version: " + pinfo.versionName + "("
                + pinfo.versionCode + ")\n");
        exceptionStr.append("Android: " + android.os.Build.VERSION.RELEASE
                + "(" + android.os.Build.MODEL + ")\n");
        exceptionStr.append("Exception: " + ex.getMessage() + "\n");
        StackTraceElement[] elements = ex.getStackTrace();
        for (int i = 0; i < elements.length; i++)
        {
            exceptionStr.append(elements[i].toString() + "\n");
        }
        return exceptionStr.toString();
    }

    /**
     * 获取App安装包信息
     *
     * @return
     */
    private PackageInfo getPackageInfo(Context context)
    {
        PackageInfo info = null;
        try
        {
            info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
        } catch (NameNotFoundException e)
        {
            // e.printStackTrace(System.err);
            Logger.t("CrashHandle").i("getPackageInfo err = " + e.getMessage());
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }
}
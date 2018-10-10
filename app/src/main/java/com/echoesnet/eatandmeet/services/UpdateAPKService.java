package com.echoesnet.eatandmeet.services;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.FileUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;

/**
 * @author yqh
 * @version 1.0
 * @createDate 2018/3/5
 * @Description 更新APP 下载service
 */
public class UpdateAPKService extends Service
{
    private final String TAG = UpdateAPKService.class.getSimpleName();
    /**
     * 安卓系统下载类
     **/
    DownloadManager manager;

    /**
     * 接收下载完的广播
     **/
    DownloadCompleteReceiver receiver;

    String apkUrl;
    String apkName;

    /**
     * 初始化下载器
     **/
    private void initDownManager()
    {

        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        receiver = new DownloadCompleteReceiver();

        //设置下载地址
        DownloadManager.Request down = new DownloadManager.Request(
                Uri.parse(apkUrl));

        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                | DownloadManager.Request.NETWORK_WIFI);

        // 下载时，通知栏显示途中
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

        // 显示下载界面
        down.setVisibleInDownloadsUi(true);

        // 设置下载后文件存放的位置
        down.setDestinationInExternalFilesDir(this,
                NetHelper.getRootDirPath(EamApplication.getInstance()), apkName + ".apk");

        // 将下载请求放入队列
        manager.enqueue(down);

        //注册下载广播
        registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        apkUrl = intent.getStringExtra("apkUrl");
        apkName = intent.getStringExtra("apkName");
        // 调用下载
        initDownManager();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent)
    {

        return null;
    }

    @Override
    public void onDestroy()
    {

        // 注销下载广播
        if (receiver != null)
            unregisterReceiver(receiver);

        super.onDestroy();
    }

    // 接受下载完成后的intent
    class DownloadCompleteReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {

            //判断是否下载完成的广播
            if (intent.getAction().equals(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            {
                ToastUtils.showShort("下载完成");
                //获取下载的文件id
                long downId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                //自动安装apk
                File file = new File(manager.getUriForDownloadedFile(downId).getPath());
                if (file.exists())
                {
                    installApk(context, file);
                }
                //停止服务并关闭广播
                UpdateAPKService.this.stopSelf();

            }
        }

        /**
         * 安装apk文件
         */
        private void installAPK(Uri apk)
        {

            // 通过Intent安装APK文件
            Intent intents = new Intent();

            intents.setAction("android.intent.action.VIEW");
            intents.addCategory("android.intent.category.DEFAULT");
            intents.setType("application/vnd.android.package-archive");
            intents.setData(apk);
            intents.setDataAndType(apk, "application/vnd.android.package-archive");
            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            android.os.Process.killProcess(android.os.Process.myPid());
            // 如果不加上这句的话在apk安装完成之后点击单开会崩溃

            startActivity(intents);

        }

        private void installApk(Context mContext, File file)
        {
            //在安装时候将token存入文件夹中，防止丢失后重新登录
            HashMap<String, String> paraMap = new HashMap<>();
            paraMap.put(ConstCodeTable.token, SharePreUtils.getToken(mContext));
            paraMap.put(ConstCodeTable.uId, SharePreUtils.getUId(mContext));
            FileUtils.writeFile(NetHelper.getRootDirPath(mContext) + NetHelper.DATA_FOLDER + "appInnerFile.json", new Gson().toJson(paraMap), false);
            String authority = "com.echoesnet.eatandmeet.provider";
            mContext.startActivity(CommonUtils.getInstallAppIntent(file, authority, true));
        }

    }

}

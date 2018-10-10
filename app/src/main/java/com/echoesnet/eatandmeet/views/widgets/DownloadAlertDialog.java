package com.echoesnet.eatandmeet.views.widgets;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.FileUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.HashMap;

import okhttp3.Call;

/**
 * @author yqh
 * @version 1.0
 * @createDate 2016/8/12
 * @Description 版本更新下载弹窗
 */
public class DownloadAlertDialog
{
    private static final String TAG = DownloadAlertDialog.class.getSimpleName();
    private Context mContext;
    private Dialog dialog;
    private TextView name;
    private ImageView image;
    NumberProgressBar pBar;
    private String url = "";

    public DownloadAlertDialog(Context context)
    {
        this.mContext = context;
    }

    public DownloadAlertDialog build()
    {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.download_notification_layout, null);
        image = (ImageView) view.findViewById(R.id.image);
        name = (TextView) view.findViewById(R.id.name);
        pBar = (NumberProgressBar) view.findViewById(R.id.progressbar);
        dialog = new Dialog(mContext, R.style.AlertDialogStyle);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        return this;
    }

    /**
     * 设置下载链接
     *
     * @param fileUrl 文件url
     * @return
     */
    public DownloadAlertDialog setDownLoadFileUrl(String fileUrl)
    {
        url = fileUrl;
        return this;
    }

    public DownloadAlertDialog setTitle(String title)
    {
        if (TextUtils.isEmpty(title))
        {
            title = "看脸吃饭.apk";
        }
        name.setText(title);
        return this;
    }

    /**
     * 设置下载时图片
     * @param imagePath
     * @return
     */
    public DownloadAlertDialog setImage(int imagePath)
    {
        image.setImageResource(imagePath);
        return this;
    }


    private void DownLoadAPK()
    {
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new FileCallBack(NetHelper.getRootDirPath(mContext), "EatAndMeet.apk")
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        ToastUtils.showShort("下载失败，请重试！");
                        if (dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onResponse(final File response)
                    {
                        Logger.t(TAG).d("路径" + NetHelper.getRootDirPath(mContext));
                        if (dialog != null && dialog.isShowing())
                            dialog.dismiss();

                        installAPK(response);
                    }

                    @Override
                    public void inProgress(float mProgress, long total)
                    {
                        int progress = (int) (mProgress * 100);
                        Logger.t(TAG).d("下载进度:" + progress);
                        pBar.setProgress(progress);
                    }
                });
    }

    private void installAPK(File file)
    {
        //在安装时候将token存入文件夹中，防止丢失后重新登录
        HashMap<String, String> paraMap = new HashMap<>();
        paraMap.put(ConstCodeTable.token, SharePreUtils.getToken(mContext));
        paraMap.put(ConstCodeTable.uId, SharePreUtils.getUId(mContext));
        FileUtils.writeFile(NetHelper.getRootDirPath(mContext) + NetHelper.DATA_FOLDER + "appInnerFile.json", new Gson().toJson(paraMap), false);


        //会重新卸载安装
/*        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);*/
        String authority = "com.echoesnet.eatandmeet.provider";
        mContext.startActivity(CommonUtils.getInstallAppIntent(file, authority, true));


//        Intent promptInstall = new Intent(Intent.ACTION_VIEW)
//                .setDataAndType(Uri.fromFile(file),
//                        "application/vnd.android.package-archive");
//        promptInstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        mContext.startActivity(promptInstall);


        /*// 替换安装包
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
//        intent.setData(Uri.fromFile(file));
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
        intent.putExtra(Intent.EXTRA_ALLOW_REPLACE, true);
        intent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME,
                mContext.getApplicationInfo().packageName);
        if (intent.resolveActivity(mContext.getPackageManager()) != null)
            mContext.startActivity(intent);*/

    }

    public void show()
    {
        dialog.show();
        DownLoadAPK();
    }

}

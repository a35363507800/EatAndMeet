package com.echoesnet.eatandmeet.utils.BigGiftUtil;

import android.app.Activity;
import android.content.Context;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.BigGiftItemBean;
import com.echoesnet.eatandmeet.utils.FileUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ZipUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author yqh
 * @version 1.0
 * @createDate 2017/9/16
 * @Description
 */
public class BigGiftUtil
{
    private static BigGiftUtil instance;
    private static final String TAG = BigGiftUtil.class.getSimpleName();
    /**
     * 用于保存所有礼物总数
     */
    private List<BigGiftItemBean> giftList = new ArrayList<>();
    /**
     * 大礼物是否在下载中，  true is downloading
     */
    public static volatile boolean isBigGiftDownloading = false;
    /**
     * 大礼物已下载数量
     */
    public static volatile int bigGiftDownloadNum = 0;
    /**
     * 控制网络监听重复进入，重复下载问题
     */
    public int lastNetStatus;

    public static BigGiftUtil getInstance()
    {
        if (instance == null)
            instance = new BigGiftUtil();
        return instance;
    }

    /**
     *
     * @param url
     *
     */
    /**
     * 读取UCloud线上 大礼物 版本文件
     *
     * @param context    上下文
     * @param url        文件url
     * @param isCheckNet 是否启用自身的检查网络，提示弹窗 ， true时context需传入 Activity 上下文
     */
    public static void startCheckBigGif(Context context, String url, boolean isCheckNet)
    {
//        final String json_version = "{            \n" +
//                "\"gifts\":\n" +
//                "[\n" +
//                "{\"giftUrl\":\"htttpssss\",\"giftName\":\"0001\",\"giftCode\":\"1\"},\n" +
//                "{\"giftUrl\":\"htttpssss\",\"giftName\":\"0001\",\"giftCode\":\"1\"},\n" +
//                "{\"giftUrl\":\"htttpssss\",\"giftName\":\"0001\",\"giftCode\":\"1\"}\n" +
//                "]\n" +
//                "}";
        if (context == null)
            context = EamApplication.getInstance();
        Context innerContext = context;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if (instance == null)
                    instance = new BigGiftUtil();
                try
                {
                    URL jsonVersionUrl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) jsonVersionUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream stream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                    String strRead = null;
                    StringBuffer sbf = new StringBuffer();
                    while ((strRead = reader.readLine()) != null)
                    {
                        sbf.append(strRead);
                        sbf.append("\r\n");
                    }
                    reader.close();
                    String result = sbf.toString();
                    Logger.t(TAG).d("BigGift--------------->giftInfo" + result.replace(" ", ""));
//                    result = json_version;
                    JSONObject obj = new JSONObject(result);
                    JSONArray array = obj.getJSONArray("gifts");
                    List<BigGiftItemBean> list = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++)
                    {
                        JSONObject object = array.getJSONObject(i);
                        String giftName = object.getString("giftName");
                        String giftUrl = object.getString("giftUrl");
                        String giftCode = object.getString("giftCode");
                        BigGiftItemBean bigGiftItemBean = new BigGiftItemBean();
                        bigGiftItemBean.setGiftName(giftName);
                        bigGiftItemBean.setGiftUrl(giftUrl);
                        bigGiftItemBean.setGiftCode(giftCode);
                        Logger.t(TAG).d("BigGift--------------->bigGiftItemBean -> " + bigGiftItemBean.toString());
                        list.add(bigGiftItemBean);
                    }
                    instance.compareLocalGiftCode(innerContext, list, isCheckNet);
                } catch (Exception e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d("BigGift--------------->startCheckBigGif:crash:" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 比较本地大礼物
     *
     * @param list
     */
    private void compareLocalGiftCode(Context context, List<BigGiftItemBean> list, boolean needCheckNet)
    {
        List<Map<String, Object>> needDownloadGift = getDownloadGiftFileInfo(list);
        if (needDownloadGift.size() == 0)//没有需要下载的礼物直接返回
            return;
        if (needCheckNet)
        {
            int netState = NetHelper.getNetworkStatus(context);
            if (netState == 1)
            {
                for (Map<String, Object> map : needDownloadGift)
                {
                    getInstance().downloadGift((BigGiftItemBean) map.get("bigGiftBean"), (String) map.get("bigGiftPath"));
                }
            }
            else if (netState == 2 || netState == 3)
            {
                ((Activity) context).runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        new CustomAlertDialog(context)
                                .builder()
                                .setCancelable(false)
                                .setTitle("提示")
                                .setMsg("当前为移动网络环境，下载直播间大礼物将消耗流量，是否下载？")
                                .setPositiveButton("是", (view) ->
                                {
                                    new Thread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            for (Map<String, Object> map : needDownloadGift)
                                            {
                                                getInstance().downloadGift((BigGiftItemBean) map.get("bigGiftBean"), (String) map.get("bigGiftPath"));
                                            }
                                        }
                                    }).start();
                                })
                                .setNegativeButton("否", null)
                                .show();
                    }
                });
            }
        }
        else
        {
            for (Map<String, Object> map : needDownloadGift)
            {
                getInstance().downloadGift((BigGiftItemBean) map.get("bigGiftBean"), (String) map.get("bigGiftPath"));
            }
        }
    }

    /**
     * 获取并检查需要下载的礼物信息
     *
     * @param list 线上所有礼物信息
     * @return 需要下载的礼物信息
     */
    private List<Map<String, Object>> getDownloadGiftFileInfo(List<BigGiftItemBean> list)
    {
        List<Map<String, Object>> needDownloadFileInfo = new ArrayList<>();
        giftList.clear();
        giftList.addAll(list);
        String giftPath = NetHelper.getRootDirPath(null) + NetHelper.GIFT_FOLDER + "bigGift/";

        //删除多余的GIF  start
        try
        {
            File fileFolder = new File(giftPath);
            if (fileFolder.exists())
            {
                File giftFiles[] = fileFolder.listFiles();
                if (giftFiles != null)
                {
                    List<String> onlineFileName = new ArrayList<>();//线上所有大礼物名称
                    for (BigGiftItemBean bigGiftItemBean : list)
                    {
                        onlineFileName.add(bigGiftItemBean.getGiftName());
                    }
                    Logger.t(TAG).d("onlineFileName:" + onlineFileName.toString());
                    for (int i = 0; i < giftFiles.length; i++)
                    {
                        File file = giftFiles[i];
                        Logger.t(TAG).d("giftFile:" + file.getName());
                        if (!onlineFileName.contains(file.getName()))
                        {
                            FileUtils.deleteFile(file.getPath());
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        //删除多余的GIF  end

        bigGiftDownloadNum = Integer.parseInt(getLocalGiftCount());

        for (BigGiftItemBean bigGiftItemBean : list)
        {
            boolean isDownLoad = false;
            File file = new File(giftPath + bigGiftItemBean.getGiftName());
            File rulesFile = new File(file.getPath() + "/rules");//追加rules 文件 里面 包含 大礼物规则 及 版本号
            if (file.exists() || rulesFile.exists())
            {
                //rules 文件 在的情况下 去读取 code 否则 重新下载
                String result = "";
                try
                {
                    FileInputStream fin = new FileInputStream(rulesFile);
                    byte[] b = new byte[fin.available()];
                    fin.read(b);
                    result = new String(b, Charset.forName("UTF-8"));
                    fin.close();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                Logger.t(TAG).d("BigGift--------------->rules:" + result);
                String code = "";
                try
                {
                    JSONObject object = new JSONObject(result);
                    code = object.getString("code");
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                if (!bigGiftItemBean.getGiftCode().equals(code))
                {
                    isDownLoad = true;
                }
            }
            else
            {
                isDownLoad = true;
            }
            if (isDownLoad == false)
                continue;
            Map<String, Object> map = new HashMap<>();
            map.put("bigGiftBean", bigGiftItemBean);
            map.put("bigGiftPath", giftPath);
            needDownloadFileInfo.add(map);
        }
        Logger.t(TAG).d("gift------------>getDownloadGiftFileInfo():" + needDownloadFileInfo.size() + " | " + needDownloadFileInfo.toString());
        return needDownloadFileInfo;
    }

    /**
     * 逐个下载大礼物
     *
     * @param bigGiftItemBean 要下载的信息
     * @param giftPath        保存路径
     */
    private void downloadGift(BigGiftItemBean bigGiftItemBean, final String giftPath)
    {
        try
        {
            isBigGiftDownloading = true;
            URL jsonVersionUrl = new URL(bigGiftItemBean.getGiftUrl());
            HttpURLConnection conn = (HttpURLConnection) jsonVersionUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream stream = conn.getInputStream();
            final File fileName = new File(giftPath + bigGiftItemBean.getGiftName() + ".zip");
            if (!fileName.getParentFile().exists())
            {
                fileName.getParentFile().mkdirs();
            }
            /*// 建立文件
            File file = new File(fileName.toString() + ".tmp");
            if (file.exists())
            {
                file.delete();
            }
            if (!file.getParentFile().exists())
            {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
*/
            FileOutputStream fs = new FileOutputStream(fileName);
            byte[] buffer = new byte[2048];
            int size = 0;
            while ((size = stream.read(buffer)) != -1)
            {
                fs.write(buffer, 0, size);
            }
            fs.close();
//            file.renameTo(fileName);
            stream.close();
            conn.disconnect();
            try
            {
                File file = new File(giftPath + bigGiftItemBean.getGiftName() + "/script");
                if (file.exists())
                {
                    file.delete();
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            ZipUtils.unZipWithoutSuffix(fileName, new File(giftPath), new ICommonOperateListener()
            {
                @Override
                public void onSuccess(String response)
                {
                    Logger.t(TAG).d("gift------------>下载大礼物解压完成：" + giftPath);
                    FileUtils.deleteFile(fileName.getPath());
                    bigGiftDownloadNum++;
                    if (downloadListener != null)
                        downloadListener.onBigGiftDownload(String.valueOf(bigGiftDownloadNum), String.valueOf(giftList.size()));
                    if (bigGiftDownloadNum == giftList.size())
                    {
                        isBigGiftDownloading = false;
                        bigGiftDownloadNum = 0;
                        lastNetStatus = 0;
                    }
                }

                @Override
                public void onError(String code, String msg)
                {
                    try
                    {
                        ZipUtils.unZipWithoutSuffix(fileName, new File(giftPath), new ICommonOperateListener()
                        {
                            @Override
                            public void onSuccess(String response)
                            {
                                FileUtils.deleteFile(fileName.getPath());
                            }

                            @Override
                            public void onError(String code, String msg)
                            {

                            }
                        });
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
//            instance.unZip(giftPath, fileName);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 检查大礼物是否存在
     *
     * @param giftName 礼物名称 001
     * @return true is exists
     */
    public boolean checkBigGiftIsExists(String giftName)
    {
        boolean isExists = false;
        String giftPath = NetHelper.getRootDirPath(null) + NetHelper.GIFT_FOLDER + "bigGift/";
        Logger.t(TAG).d("检查某个大礼物是否存在路径：" + giftPath + "g_" + giftName);
        File file = new File(giftPath + "g_" + giftName);
        if (file.exists())
            isExists = true;
        return isExists;
    }

    /**
     * 获取大礼物总数
     *
     * @return 从giftInfo.json 中获取的gift总数
     */
    public String getGiftCount()
    {
        String giftCuont = "0";
        if (giftList != null)
            giftCuont = String.valueOf(giftList.size());
        Logger.t(TAG).d("gift------------>getGiftCount():" + giftCuont);
        return giftCuont;
    }

    /**
     * 检查本地礼物资源个数
     *
     * @return 本地大礼物资源个数
     */
    public String getLocalGiftCount()
    {
        int giftCount = 0;
        if (instance == null)
            return String.valueOf(giftCount);
        for (BigGiftItemBean bigGiftItemBean : instance.giftList)
        {
            String giftPath = NetHelper.getRootDirPath(null) + NetHelper.GIFT_FOLDER + "bigGift/";
            File file = new File(giftPath + bigGiftItemBean.getGiftName());
            if (file.exists())
            {
                giftCount++;
            }
        }
        Logger.t(TAG).d("gift------------>本地资源个数总和为：" + giftCount);
        return String.valueOf(giftCount);
    }

    /**
     * 检查所有大礼物是否存在
     *
     * @return true is exists
     */
    public boolean checkAllBigGiftExists()
    {
        boolean isAllExists = true;
        if (instance == null)
            return false;
//        boolean isExists = true;
        File file = new File(NetHelper.getRootDirPath(null) + NetHelper.GIFT_FOLDER + "bigGift");
        String[] fileList = file.list(new FilenameFilter()
        {
            @Override
            public boolean accept(File file, String s)
            {
                return !s.contains(".");
            }
        });
        if (fileList == null)
            return false;
        if (fileList.length != instance.giftList.size())
        {
            isAllExists = false;
        }
        Logger.t(TAG).d("fileList:" + Arrays.toString(fileList));

//        for (BigGiftItemBean bigGiftItemBean : instance.giftList)
//        {
//            String giftPath = NetHelper.getRootDirPath(null) + NetHelper.GIFT_FOLDER + "bigGift/";
//            Logger.t(TAG).d("检查全部大礼物是否存在路径：" + giftPath + bigGiftItemBean.getGiftName());
//            File files = new File(giftPath + bigGiftItemBean.getGiftName());
//            if (!files.exists())
//            {
//                isAllExists = false;
//                break;
//            }
//        }
        Logger.t(TAG).d("gift------------>checkAllBigGiftExists():" + isAllExists);
        return isAllExists;
    }

    private IOnBigGiftDownloadListener downloadListener;

    public interface IOnBigGiftDownloadListener
    {
        void onBigGiftDownload(String sucNum, String count);
    }

    public void setIOnBigGiftDownloadListener(IOnBigGiftDownloadListener listener)
    {
        downloadListener = listener;
    }

    private String charStream2String(Reader reader)
    {
        BufferedReader r = new BufferedReader(reader);
        StringBuilder b = new StringBuilder();
        String line;
        try
        {
            while ((line = r.readLine()) != null)
            {
                b.append(line);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return b.toString();
    }

    /**
     * 会递归删除 目录下文件
     *
     * @param targetDir
     * @param zipFile
     * @throws Exception
     */
    private void unZip(String targetDir, File zipFile) throws Exception
    {
        File targetDirectory = new File(targetDir);
        if (!targetDirectory.exists())
        {
            targetDirectory.mkdirs();
        }
        else
        {
            instance.deleteDir(targetDirectory);
        }

        try
        {
            ZipInputStream zis = new ZipInputStream(
                    new BufferedInputStream(
                            new FileInputStream(zipFile)));
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null)
            {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                {
                    throw new FileNotFoundException("Failed to ensure directory: " + dir.getAbsolutePath());
                }

                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try
                {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally
                {
                    fout.close();
                }
            }
            zis.close();
        } catch (Exception exc)
        {
            throw exc;
        }
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    private boolean deleteDir(File dir)
    {
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++)
            {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success)
                {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

}

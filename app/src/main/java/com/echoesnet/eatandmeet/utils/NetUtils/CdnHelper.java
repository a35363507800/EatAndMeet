package com.echoesnet.eatandmeet.utils.NetUtils;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.ucloud.ufilesdk.Callback;
import cn.ucloud.ufilesdk.UFilePart;
import cn.ucloud.ufilesdk.UFileRequest;
import cn.ucloud.ufilesdk.UFileSDK;
import cn.ucloud.ufilesdk.UFileUtils;
import cn.ucloud.ufilesdk.task.HttpAsyncTask;
import okhttp3.Call;

import static com.orhanobut.logger.Logger.t;

/**
 * Created by wangben on 2016/6/6.
 */
public class CdnHelper
{
    private static final String TAG = CdnHelper.class.getSimpleName();

    //cdn加速网址
    public static final String CDN_ACC_SITE="http://huisheng.ufile.ucloud.com.cn/";

    //cdn原始网址
    public static final String CDN_ORIGINAL_SITE = "http://huisheng.ufile.ucloud.cn/";

    /**
     * ucloud 推流url
     */
    public static final String UCLOUD_STREAM_URL = "rtmp://publish.echoesnet.com/cola/%s";

    /**
     * ucloud 推流url(开启录像)
     */
    public static final String UCLOUD_STREAM_URL_WITH_RECORD = "rtmp://publish.echoesnet.com/cola/%s?record=true&filename=%s";
    /**
     * ucloud 推流accessKey
     */
    public static final String UCLOUD_STREAM_ACCESS_KEY = "ucloud.publish.echoesnet.comf3760411";

    /**
     * ucloud 播放url
     */
    public static final String UCLOUD_PLAY_URL = "http://hls.echoesnet.com/cola/%s.flv";
    /**
     * 测试环境
     */
    public static String fileFolder = "test/";

    /**
     * UCloudPublicKey 请改成用户的公钥
     * UCloudPrivateKey 请改成用户的私钥
     */
    public static final String publicKey = "vUXKLgsVjlbdo8ckBoU6vIfe6vtTVz9HBzaEgWwIM0Ow6xVxYbKlGg==";
    public static final String privatekey = "6fa258e66b31bf0dbe017a10da91356f7746b2d2";
    private static final String proxySuffix = ".ufile.ucloud.com.cn";

    public static final String userImage = "user_";
    public static final String resImage = "rest_";
    public static final String liveImage = "live_";
    public static final String trendsImage = "trends_";

    /**
     * BUCKET : BUCKET name
     * proxySuffix : 域名后缀
     */
    private static final String BUCKET = "huisheng";
    private UFileSDK uFileSDK;

    private IOnCdnFeedbackListener mOncdnFeedbackListener;
    private IOnCdnDeletebackListener mDeleteListener;

    //test file
    private File testFile = getTestFile();

    private static volatile CdnHelper cdnHelper;

    private static final int TIME_OUT = 10 * 1000;   //超时时间
    private static final String CHARSET = "utf-8"; //设置编码

    private CdnHelper()
    {
        //init ufile sdk
        uFileSDK = new UFileSDK(BUCKET, proxySuffix);
    }

    public static CdnHelper getInstance()
    {
        if (cdnHelper == null)
        {
            synchronized (CdnHelper.class)
            {
                if (cdnHelper == null)
                {
                    cdnHelper = new CdnHelper();
                }
            }
        }
        return cdnHelper;
    }

    public void setOnCdnFeedbackListener(IOnCdnFeedbackListener listener)
    {
        this.mOncdnFeedbackListener = listener;
    }

    public void setOnCdnDeletebackListener(IOnCdnDeletebackListener listener)
    {
        this.mDeleteListener = listener;
    }

    //上传文本文件
    public void putFile(final File file, String fileType, final int uploadOrder)
    {
        String http_method = "PUT";
        String content_md5 = UFileUtils.getFileMD5(file);
        String content_type = null;
        if (fileType.equals("txt"))
            content_type = "text/plain";
        else if (fileType.equals("img"))
            content_type = "application/octet-stream";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd mm:ss");
        String date = "";// sdf.format(new Date());

        //存取图片的名字
        final String key_name = file.getName();

        String authorization = getAuthorization(http_method, content_md5, content_type, date, BUCKET, key_name);
        final UFileRequest request = new UFileRequest();
        request.setHttpMethod(http_method);
        request.setAuthorization(authorization);
        request.setContentMD5(content_md5);
        request.setContentType(content_type);

        final HttpAsyncTask httpAsyncTask = uFileSDK.putFile(request, file, key_name, new Callback()
        {
            @Override
            public void onSuccess(JSONObject response)
            {
                t(TAG).d("成功：" + response.toString());
                mOncdnFeedbackListener.onSuccess(response, file, key_name, uploadOrder);
            }

            @Override
            public void onProcess(long len)
            {
                int value = (int) (len * 100 / file.length());
                //Logger.t(TAG).d("progress value is " + value);
                mOncdnFeedbackListener.onProcess(len);
            }

            @Override
            public void onFail(JSONObject response)
            {
                Logger.t(TAG).d("错误：" + response.toString());
                mOncdnFeedbackListener.onFail(response, file);
            }
        });
    }

    //上传文本文件fileK
    public void putFile(final File file, String fileType, String fileKeyName, final int uploadOrder)
    {
        String http_method = "PUT";
        String content_md5 = UFileUtils.getFileMD5(file);
        String content_type = null;
        if (fileType.equals("txt"))
            content_type = "text/plain";
        else if (fileType.equals("img"))
            content_type = "application/octet-stream";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd mm:ss");
        String date = "";// sdf.format(new Date());

        //存取图片的名字
        final String key_name = fileKeyName;

        String authorization = getAuthorization(http_method, content_md5, content_type, date, BUCKET, key_name);
        final UFileRequest request = new UFileRequest();
        request.setHttpMethod(http_method);
        request.setAuthorization(authorization);
        request.setContentMD5(content_md5);
        request.setContentType(content_type);

        final HttpAsyncTask httpAsyncTask = uFileSDK.putFile(request, file, key_name, new Callback()
        {
            @Override
            public void onSuccess(JSONObject response)
            {
                t(TAG).d("成功：" + response.toString());
                mOncdnFeedbackListener.onSuccess(response, file, key_name, uploadOrder);
            }

            @Override
            public void onProcess(long len)
            {
                int value = (int) (len * 100 / file.length());
                //Logger.t(TAG).d("progress value is " + value);
                mOncdnFeedbackListener.onProcess(value);
            }

            @Override
            public void onFail(JSONObject response)
            {
                t(TAG).d("错误：" + response.toString());
                mOncdnFeedbackListener.onFail(response, file);
            }
        });
    }

    public void uploadHit(final File file)
    {
        String http_method = "POST";
        String key_name = file.getName();

        String authorization = getAuthorization(http_method, "", "", "", BUCKET, key_name);

        UFileRequest request = new UFileRequest();
        request.setHttpMethod(http_method);
        request.setAuthorization(authorization);

        final HttpAsyncTask httpAsyncTask = uFileSDK.uploadHit(request, file, getDefaultCallback());
        showProcessDialog(httpAsyncTask);
    }

    //下载文件
    public void getFile(String fileName)
    {
        final String http_method = "GET";
        String authorization = getAuthorization(http_method, "", "", "", BUCKET, fileName);

        UFileRequest request = new UFileRequest();
        request.setHttpMethod(http_method);
        request.setAuthorization(authorization);

        final HttpAsyncTask httpAsyncTask = uFileSDK.getFile(request, fileName, getSaveFile(), new Callback()
        {
            @Override
            public void onSuccess(JSONObject response)
            {
                Log.i(TAG, "onSuccess " + response);
            }

            @Override
            public void onProcess(long len)
            {
                int value = (int) (len * 100 / testFile.length());
                Log.i(TAG, "progress value is " + value);
            }

            @Override
            public void onFail(JSONObject response)
            {
                Log.i(TAG, "onFail " + response);
            }
        });
    }

    public void headFile(View view)
    {
        String http_method = "HEAD";
        String key_name = "test.txt";
        String authorization = getAuthorization(http_method, "", "", "", BUCKET, key_name);

        UFileRequest request = new UFileRequest();
        request.setHttpMethod(http_method);
        request.setAuthorization(authorization);

        HttpAsyncTask httpAsyncTask = uFileSDK.headFile(request, key_name, getDefaultCallback());
        showProcessDialog(httpAsyncTask);
    }

    /**
     * 删除cdn上的文件
     *
     * @param keyFileName 要删除的文件访问key
     */
    public void deleteFile(String keyFileName)
    {

        /*String http_method = "DELETE";
        String key_name = fileName;


        String authorization = getAuthorization(http_method, "", "", "", BUCKET, key_name);

        UFileRequest request = new UFileRequest();
        request.setHttpMethod(http_method);
        request.setAuthorization(authorization);

        HttpAsyncTask httpAsyncTask = uFileSDK.deleteFile(request, key_name, getDefaultCallback());
        showProcessDialog(httpAsyncTask);*/
        String http_method = "DELETE";
        final String key_name = keyFileName;

        String authorization = getAuthorization(http_method, "", "", "", BUCKET, key_name);

        UFileRequest request = new UFileRequest();
        request.setHttpMethod(http_method);
        request.setAuthorization(authorization);

        HttpAsyncTask httpAsyncTask = uFileSDK.deleteFile(request, key_name, new Callback()
        {
            @Override
            public void onSuccess(JSONObject response)
            {
                t(TAG).d("成功：" + response.toString());
                mDeleteListener.onSuccess(response, key_name);
            }

            @Override
            public void onProcess(long len)
            {
                mDeleteListener.onProcess(len);
            }

            @Override
            public void onFail(JSONObject response)
            {
                mDeleteListener.onFail(response);
            }
        });
        showProcessDialog(httpAsyncTask);
    }

    //分片相关
    private UFilePart uFilePart = null;
    //private File partFile = getTestFile("app.apk");

    public void initiateMultipartUpload(File partFile)
    {
        String http_method = "POST";
        String key_name = partFile.getName();
        String authorization = getAuthorization(http_method, "", "", "", BUCKET, key_name);

        UFileRequest request = new UFileRequest();
        request.setHttpMethod(http_method);
        request.setAuthorization(authorization);

        HttpAsyncTask httpAsyncTask = uFileSDK.initiateMultipartUpload(request, key_name, new Callback()
        {
            @Override
            public void onSuccess(JSONObject response)
            {
                Log.i(TAG, "onSuccess " + response);
                uFilePart = new UFilePart();
                try
                {
                    JSONObject message = response.getJSONObject("message");
                    uFilePart.setUploadId(message.getString("UploadId"));
                    uFilePart.setBlkSize(Long.parseLong(message.getString("BlkSize")));
                    uFilePart.setBucket(message.getString("Bucket"));
                    uFilePart.setKey(message.getString("Key"));
                    uFilePart.setEtags();
                    Log.e(TAG, uFilePart.toString());
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onProcess(long len)
            {

            }

            @Override
            public void onFail(JSONObject response)
            {
                Log.i(TAG, "onFail " + response);
/*                progressDialog.dismiss();
                showDialog(response.toString());*/
            }
        });
        showProcessDialog(httpAsyncTask);
    }

    private int count = 0;

/*    public void uploadPart(final File partFile)
    {
        if (uFilePart == null)
        {
            return;
        }
        //uploadPartRetry.setEnabled(false);
        final String http_method = "PUT";
        final String key_name = partFile.getColumnName();
        String content_type = "application/octet-stream";
        String authorization = getAuthorization(http_method, "", content_type, "", BUCKET, key_name);

        long blk_size = uFilePart.getBlkSize();
        long file_len = partFile.length();

        final int part = (int) Math.ceil(file_len / blk_size);

        final List<HttpAsyncTask> list = new ArrayList<>();

        for (int i = 0; i <= part; i++)
        {
            UFileRequest request = new UFileRequest();
            request.setHttpMethod(http_method);
            request.setAuthorization(authorization);
            request.setContentType(content_type);

            HttpAsyncTask httpAsyncTask = uFileSDK.uploadPart(request, key_name, uFilePart.getUploadId(), partFile, i, uFilePart.getBlkSize(), new Callback()
            {
                @Override
                public void onSuccess(JSONObject response)
                {
                    Log.i(TAG, "onSuccess " + response);
                    try
                    {
                        String etag = response.getString("ETag");
                        int partNumber = response.getJSONObject("message").getInt("PartNumber");
                        uFilePart.addEtag(partNumber, etag);
                        if (count < part)
                        {
                            count++;
                        }
                        else
                        {
                            count = 0;
                            mOncdnFeedbackListener.onSuccess(response, partFile, key_name, 1);
                        }
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onProcess(long len)
                {
                    mOncdnFeedbackListener.onProcess(len);
                }

                @Override
                public void onFail(JSONObject response)
                {
                    mOncdnFeedbackListener.onFail(response);
                }
            });
            list.add(httpAsyncTask);
        }
    }*/

    public void uploadPartRetry(File partFile)
    {
        if (uFilePart == null)
        {
            return;
        }
        //uploadPart.setEnabled(false);
        final String http_method = "PUT";
        String key_name = partFile.getName();
        String content_type = "application/octet-stream";
        String authorization = getAuthorization(http_method, "", content_type, "", BUCKET, key_name);

        long blk_size = uFilePart.getBlkSize();
        long file_len = partFile.length();

        final int part = (int) Math.ceil(file_len / blk_size);

        final List<UFileSDK.UploadPartManager> list = new ArrayList<>();
/*        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMax(part + 1);*/

        for (int i = 0; i <= part; i++)
        {
            UFileRequest request = new UFileRequest();
            request.setHttpMethod(http_method);
            request.setAuthorization(authorization);
            request.setContentType(content_type);

            UFileSDK.UploadPartManager uploadPartManager = uFileSDK.uploadPart(request, key_name, uFilePart.getUploadId(), partFile, i, uFilePart.getBlkSize(), new Callback()
            {
                @Override
                public void onSuccess(JSONObject response)
                {
                    Log.i(TAG, "onSuccess " + response);
                    try
                    {
                        String etag = response.getString("ETag");
                        int partNumber = response.getJSONObject("message").getInt("PartNumber");
                        uFilePart.addEtag(partNumber, etag);
                        if (count < part)
                        {
                            count++;
                            //dialog.setProgress(count);
                        }
                        else
                        {
                            count = 0;
                            //dialog.dismiss();
                            //finishMultipartUpload.setEnabled(true);
                        }
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onProcess(long len)
                {

                }

                @Override
                public void onFail(JSONObject response)
                {
                    Log.i(TAG, "onFail " + response);
                    //dialog.dismiss();
                    //showDialog(response.toString());
                }
            }, 3, 1000, new Handler());
            list.add(uploadPartManager);
        }
/*        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (UFileSDK.UploadPartManager uploadPartManager : list) {
                    uploadPartManager.stop();
                }
                dialog.dismiss();
            }
        });
        dialog.show();*/
    }

    public void finishMultipartUpload(File partFile)
    {
        if (uFilePart == null)
        {
            return;
        }
        String http_method = "POST";
        String key_name = partFile.getName();
        String content_type = "text/plain";
        String authorization = getAuthorization(http_method, "", content_type, "", BUCKET, key_name);
        String etags = uFilePart.getEtags();

        UFileRequest request = new UFileRequest();
        request.setHttpMethod(http_method);

        request.setAuthorization(authorization);
        request.setContentType(content_type);

        HttpAsyncTask httpAsyncTask = uFileSDK.finishMultipartUpload(request, key_name, uFilePart.getUploadId(), etags, "new_" + key_name, getDefaultCallback());
        showProcessDialog(httpAsyncTask);

/*        uploadPart.setEnabled(false);
        uploadPartRetry.setEnabled(false);
        finishMultipartUpload.setEnabled(false);
        abortMultipartUpload.setEnabled(false);*/

        uFilePart = null;
    }

    public void abortMultipartUpload(File partFile)
    {
        if (uFilePart == null)
        {
            return;
        }
        String http_method = "DELETE";
        String key_name = partFile.getName();
        String authorization = getAuthorization(http_method, "", "", "", BUCKET, key_name);

        UFileRequest request = new UFileRequest();
        request.setHttpMethod(http_method);
        request.setAuthorization(authorization);

        HttpAsyncTask httpAsyncTask = uFileSDK.abortMultipartUpload(request, key_name, uFilePart.getUploadId(), getDefaultCallback());
        showProcessDialog(httpAsyncTask);

/*        uploadPart.setEnabled(false);
        uploadPartRetry.setEnabled(false);
        finishMultipartUpload.setEnabled(false);
        abortMultipartUpload.setEnabled(false);*/

        uFilePart = null;
    }

    private File getTestFile()
    {
        File sdcard = Environment.getExternalStorageDirectory();
        return new File(sdcard, "test.txt");
    }

    private File getSaveFile()
    {
        File sdcard = Environment.getExternalStorageDirectory();
        File download = new File(sdcard, "ufiledown");
        if (!download.exists())
        {
            download.mkdir();
        }
        return new File(download, "test.txt");
    }

    private String getAuthorization(String http_method, String content_md5, String content_type, String date, String bucket, String key)
    {
        String signature = "";
        try
        {
            String strToSign = http_method + "\n" + content_md5 + "\n" + content_type + "\n" + date + "\n" + "/" + bucket + "/" + key;
            byte[] hmac = UFileUtils.hmacSha1(privatekey, strToSign);
            signature = Base64.encodeToString(hmac, Base64.DEFAULT);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        String auth = "UCloud" + " " + publicKey + ":" + signature;
        //Log.e(TAG, "getAuthorization " + auth);
        return auth;
    }

    private Callback getDefaultCallback()
    {
        Callback callback = new Callback()
        {
            @Override
            public void onSuccess(JSONObject response)
            {
                Log.i(TAG, "onSuccess " + response);

            }

            @Override
            public void onProcess(long len)
            {

            }

            @Override
            public void onFail(JSONObject response)
            {
                Log.i(TAG, "onFail " + response);

            }
        };

        return callback;
    }

    private void showProcessDialog(final HttpAsyncTask httpAsyncTask)
    {
/*        progressDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (httpAsyncTask != null)
                    httpAsyncTask.cancel();
                dialog.dismiss();
            }
        });
        progressDialog.show();*/
    }

    /**
     * android上传文件到服务器
     *
     * @param file       需要上传的文件
     * @param RequestURL 请求的rul
     * @return 返回响应的内容
     */
    public void uploadFileToBackServer(File file, String RequestURL, String fileKey, int uploadOrder, IOnCdnFeedbackListener callback)
    {
        String result = "";
        String BOUNDARY = UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型
        InputStream is = null;
        DataOutputStream dos = null;
        try
        {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(30 * 1000);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
            conn.setRequestProperty("Charset", CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if (file != null)
            {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的   比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"img\"; filename=\"" + fileKey + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1)
                {
                    dos.write(bytes, 0, len);
                }

                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);

                /**
                 * 获取响应码  200=成功
                 * 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                t(TAG).d("response code:" + res);
                if (res == 200)
                {
                    Logger.t(TAG).d("request success");
                    InputStream input = conn.getInputStream();
                    StringBuffer sb1 = new StringBuffer();
                    int ss;
                    while ((ss = input.read()) != -1)
                    {
                        sb1.append((char) ss);
                    }
                    result = sb1.toString();
                    JSONObject object = new JSONObject(result);
                    callback.onSuccess(object, file, fileKey, uploadOrder);
                }
                else
                {
                    callback.onFail(new JSONObject(), file);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (is != null)
                {
                    is.close();
                }
                if (dos != null)
                {
                    dos.flush();
                    dos.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }

    public String getMaterialUrl(final String key, final Context mContext)
    {
        try
        {
            final File file = new File(NetHelper.getRootDirPath(mContext) + NetHelper.DATA_FOLDER + "material.json");
            if (file.exists() && !file.isDirectory())
            {
                return getValueFromFileByKey(key, file);
            }
            else
            {
                OkHttpUtils.get()
                        .url(CdnHelper.CDN_ORIGINAL_SITE + CdnHelper.fileFolder + "material.json")
                        .build()
                        .execute(new FileCallBack(NetHelper.getRootDirPath(mContext) + NetHelper.DATA_FOLDER, "material.json")
                        {
                            @Override
                            public void onError(Call call, Exception e)
                            {
                                t(TAG).d(e.getMessage());
                            }

                            @Override
                            public void onResponse(File response)
                            {
                                getMaterialUrl(key, mContext);
                            }

                            @Override
                            public void inProgress(float progress, long total)
                            {

                            }
                        });
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return "error";
    }

    private static String getValueFromFileByKey(String key, File jsonContentFile)
    {
        try
        {
            Logger.t(TAG).d("键值" + key);
            if (EamApplication.getInstance().materialObj == null)
            {
                EamApplication.getInstance().materialObj = new JSONObject(CommonUtils.getJsonFromFile(jsonContentFile));
            }
            return EamApplication.getInstance().materialObj.getString(key);
        } catch (JSONException e)
        {
            e.printStackTrace();
            t(TAG).e(e.getMessage());
        } catch (Exception e)
        {
            e.printStackTrace();
            t(TAG).e(e.getMessage());
        }
        return "解析材料文件失败";
    }
}
